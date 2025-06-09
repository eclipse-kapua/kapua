/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.commons.event;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.EntityManager;
import org.eclipse.kapua.commons.jpa.EntityManagerFactory;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecord;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecordAttributes;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecordListResult;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecordQuery;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreService;
import org.eclipse.kapua.commons.service.event.store.api.ServiceEventUtil;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;
import org.eclipse.kapua.commons.util.KapuaDateUtils;
import org.eclipse.kapua.event.ServiceEvent.EventStatus;
import org.eclipse.kapua.event.ServiceEventBus;
import org.eclipse.kapua.event.ServiceEventBusException;
import org.eclipse.kapua.model.KapuaUpdatableEntityAttributes;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Event bus housekeeper. It is responsible to send unsent messages or send again messages gone in error.
 *
 * @since 1.0
 * @deprecated since 2.0.0 - use {@link ServiceEventTransactionalHousekeeper} instead
 */
@Deprecated
public class ServiceEventHousekeeper implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceEventHousekeeper.class);

    private enum EventsProcessType {
        OLD,
        SEND_ERROR
    }

    private static final long WAIT_TIME = SystemSetting.getInstance().getLong(SystemSettingKey.HOUSEKEEPER_EXECUTION_WAIT_TIME);
    private static final long OLD_MESSAGES_TIME_WINDOW = SystemSetting.getInstance().getLong(SystemSettingKey.HOUSEKEEPER_OLD_MESSAGES_TIME_WINDOW);
    private static final int EVENT_SCAN_WINDOW = SystemSetting.getInstance().getInt(SystemSettingKey.HOUSEKEEPER_EVENT_SCAN_WINDOW);

    private final Object monitor = new Object();

    private EventStoreService kapuaEventService;

    private EntityManager manager;

    private ServiceEventBus eventbus;
    private List<ServiceEntry> servicesEntryList;
    private boolean running;

    /**
     * Default constructor
     *
     * @param entityManagerFactory
     * @param eventbus
     * @param servicesEntryList
     * @throws KapuaException
     */
    public ServiceEventHousekeeper(EventStoreService eventStoreService, EntityManagerFactory entityManagerFactory, ServiceEventBus eventbus, List<ServiceEntry> servicesEntryList)
            throws KapuaException {
        this.eventbus = eventbus;
        this.servicesEntryList = servicesEntryList;
        manager = entityManagerFactory.createEntityManager();
        kapuaEventService = eventStoreService;
    }

    @Override
    public void run() {
        //TODO handling events table cleanup
        running = true;
        while (running) {
            waitStep();
            for (ServiceEntry serviceEntry : servicesEntryList) {
                try {
                    if (running) {
                        KapuaSecurityUtils.doPrivileged(() -> processServiceEvents(serviceEntry.getServiceName()));
                    }
                } catch (KapuaException e) {
                    LOGGER.warn("Generic error {}", e.getMessage(), e);
                } finally {
                    //remove the lock if present
                    if (manager.isTransactionActive()) {
                        manager.rollback();
                    }
                }
            }
        }
        running = false;
    }

    private void processServiceEvents(String serviceName) throws KapuaException {
        try {
            LOGGER.trace("Scan not processed events for service '{}'", serviceName);
            Date startRun = Date.from(KapuaDateUtils.getKapuaSysDate());
            //try to acquire lock
            HousekeeperRun kapuaEventHousekeeper = getLock(serviceName);
            //scan unsent events (marked as SENT_ERROR)
            findAndSendUnsentEvents(serviceName, EventsProcessType.SEND_ERROR);
            //scan unsent OLD events (marked as FIRED but raised before a specific (configurable) time window)
            findAndSendUnsentEvents(serviceName, EventsProcessType.OLD);
            //release lock
            updateLock(kapuaEventHousekeeper, serviceName, startRun);
        } catch (LockException | NoExecutionNeededException e) {
            LOGGER.trace("The lock is handled by someone else or the last execution was to close");
        } finally {
            //remove the lock if present
            if (manager.isTransactionActive()) {
                manager.rollback();
            }
        }
    }

    private void findAndSendUnsentEvents(String serviceName, EventsProcessType eventsProcessType) throws KapuaException {
        EventStoreRecordListResult unsentMessagesList = getUnsentEvents(serviceName, eventsProcessType);
        //send unprocessed events
        if (!unsentMessagesList.isEmpty()) {
            for (EventStoreRecord kapuaEvent : unsentMessagesList.getItems()) {
                try {
                    String address = ServiceMap.getAddress(serviceName);
                    LOGGER.info("publish event: service '{}' - address '{}' - operation '{}' - id '{}'",
                            kapuaEvent.getService(),
                            address,
                            kapuaEvent.getOperation(),
                            kapuaEvent.getContextId());

                    eventbus.publish(address, ServiceEventUtil.toServiceEventBus(kapuaEvent));
                    //if message was sent successfully then confirm the event in the event table
                    //if something goes wrong during this update the event message may be raised twice (but this condition should happens rarely and it is compliant to the contract of the service events)
                    //this is done in a different transaction
                    kapuaEvent.setStatus(EventStatus.SENT);
                    kapuaEventService.update(kapuaEvent);
                } catch (ServiceEventBusException e) {
                    LOGGER.warn("Exception publishing event: {}", e.getMessage(), e);
                } catch (KapuaException e) {
                    //this may be a valid condition if the HouseKeeper is doing the update concurrently with this task
                    LOGGER.warn("Exception acknowledging event: {}", e.getMessage(), e);
                }
            }
        }
    }

    private EventStoreRecordListResult getUnsentEvents(String serviceName, EventsProcessType eventsProcessType) throws KapuaException {
        EventStoreRecordQuery query = new EventStoreRecordQuery(null);

        AndPredicate andPredicate = query.andPredicate();
        andPredicate.and(query.attributePredicate(EventStoreRecordAttributes.SERVICE_NAME, serviceName));

        if (EventsProcessType.SEND_ERROR.equals(eventsProcessType)) {
            LOGGER.trace("Looking for SENT_ERROR events. Add EventStatus=SENT_ERROR query predicate.");
            andPredicate.and(query.attributePredicate(EventStoreRecordAttributes.EVENT_STATUS, EventStatus.SEND_ERROR));
        } else {
            LOGGER.trace("Looking for OLD events. Add EventStatus=RAISED query predicate.");
            andPredicate.and(query.attributePredicate(EventStoreRecordAttributes.EVENT_STATUS, EventStatus.TRIGGERED));
            //add timestamp predicate
            Date eventDateBound = Date.from(KapuaDateUtils.getKapuaSysDate().minusMillis(OLD_MESSAGES_TIME_WINDOW));
            LOGGER.trace("Looking for OLD events. Add timestamp condition query predicate. Date before {}", eventDateBound);
            andPredicate.and(query.attributePredicate(KapuaUpdatableEntityAttributes.MODIFIED_ON, eventDateBound, Operator.LESS_THAN_OR_EQUAL));
        }

        query.setPredicate(andPredicate);
        query.setLimit(EVENT_SCAN_WINDOW);

        return kapuaEventService.query(query);
    }

    private void waitStep() {
        try {
            synchronized (monitor) {
                monitor.wait(WAIT_TIME);
            }
        } catch (InterruptedException e) {
            LOGGER.warn("Exception waiting for next scheduled execution: {}", e.getMessage(), e);
        }
    }

    public void stop() {
        running = false;
        synchronized (monitor) {
            monitor.notify();
        }
    }

    private HousekeeperRun getLock(String serviceName) throws LockException, NoExecutionNeededException {
        HousekeeperRun kapuaEventHousekeeper = null;
        try {
            manager.beginTransaction();
            kapuaEventHousekeeper = manager.findWithLock(HousekeeperRun.class, serviceName);
        } catch (Exception e) {
            throw new LockException(String.format("Cannot acquire lock: %s", e.getMessage()), e);
        }
        // Check last housekeeper run
        if (KapuaDateUtils.getKapuaSysDate().isBefore(kapuaEventHousekeeper.getLastRunOn().toInstant().plus(Duration.of(WAIT_TIME, ChronoUnit.MILLIS)))) {
            throw new NoExecutionNeededException("Not enough time since the last execution");
        }
        return kapuaEventHousekeeper;
    }

    private void updateLock(HousekeeperRun kapuaEventHousekeeper, String serviceName, Date startRun) throws KapuaException {
        kapuaEventHousekeeper.setLastRunBy(serviceName);
        kapuaEventHousekeeper.setLastRunOn(startRun);
        manager.persist(kapuaEventHousekeeper);
        manager.commit();
    }

    private class LockException extends Exception {

        private static final long serialVersionUID = 3099804470559976126L;

        public LockException(String msg, Throwable t) {
            super(msg, t);
        }
    }

    private class NoExecutionNeededException extends Exception {

        private static final long serialVersionUID = 7292333466656851052L;

        public NoExecutionNeededException(String msg) {
            super(msg);
        }

    }

}
