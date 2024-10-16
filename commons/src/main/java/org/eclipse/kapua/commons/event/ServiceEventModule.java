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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.core.ServiceModule;
import org.eclipse.kapua.commons.jpa.JpaTxContext;
import org.eclipse.kapua.commons.service.event.store.internal.EventStoreFactoryImpl;
import org.eclipse.kapua.commons.service.event.store.internal.EventStoreRecordImplJpaRepository;
import org.eclipse.kapua.commons.service.event.store.internal.EventStoreServiceImpl;
import org.eclipse.kapua.event.ServiceEventBus;
import org.eclipse.kapua.event.Subscription;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.storage.TxManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Base {@link ServiceModule} implementation to be used by the modules that listen for events.
 *
 * @since 1.0
 * @deprecated since 2.0.0 - use {@link ServiceEventTransactionalModule} instead
 */
@Deprecated
public abstract class ServiceEventModule implements ServiceModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceEventModule.class);

    private static final int MAX_WAIT_LOOP_ON_SHUTDOWN = 30;
    private static final int SCHEDULED_EXECUTION_TIME_WINDOW = 30;
    private static final long WAIT_TIME = 1000;

    private ServiceEventModuleConfiguration serviceEventModuleConfiguration;
    private Set<String> subscriberNames = new HashSet<>();

    private ScheduledExecutorService houseKeeperScheduler;
    private ScheduledFuture<?> houseKeeperHandler;
    private ServiceEventHousekeeper houseKeeperJob;

    protected abstract ServiceEventModuleConfiguration initializeConfiguration();

    private String getSubscriptionName(String address, String subscriber) {
        return String.format("%s-%s", address, subscriber);
    }

    @Override
    public void start() throws KapuaException {
        LOGGER.info("Starting service event module... {}", this.getClass().getName());
        LOGGER.info("Starting service event module... initialize configurations");
        serviceEventModuleConfiguration = initializeConfiguration();
        LOGGER.info("Starting service event module... initialize event bus");
        ServiceEventBus eventbus = KapuaLocator.getInstance().getComponent(ServiceEventBus.class);
        LOGGER.info("Starting service event module... initialize event subscriptions");
        List<ServiceEntry> servicesEntryList = new ArrayList<>();
        if (serviceEventModuleConfiguration.getServiceEventClientConfigurations() != null) {
            for (ServiceEventClientConfiguration selc : serviceEventModuleConfiguration.getServiceEventClientConfigurations()) {
                //get the specific service address... if empty switch to use the default configuration address
                String address = selc.getAddress();
                if (StringUtils.isEmpty(selc.getAddress())) {
                    address = serviceEventModuleConfiguration.getInternalAddress();
                }
                // Listen to upstream service events
                if (selc.getEventListener() != null) {
                    eventbus.subscribe(new Subscription(address, getSubscriptionName(address, selc.getClientName()), selc.getEventListener()));
                }
                servicesEntryList.add(new ServiceEntry(selc.getClientName(), address));
                subscriberNames.add(selc.getClientName()); // Set because names must be unique
            }
        } else {
            LOGGER.info("Configuration subscriptions are missing. No subscriptions added!");
        }

        // register events to the service map
        LOGGER.info("Starting service event module... register services names");
        ServiceMap.registerServices(servicesEntryList);

        // Start the House keeper
        LOGGER.info("Starting service event module... start housekeeper");
        houseKeeperScheduler = Executors.newScheduledThreadPool(1);
        final KapuaLocator locator = KapuaLocator.getInstance();
        houseKeeperJob = new ServiceEventHousekeeper(
                new EventStoreServiceImpl(locator.getService(AuthorizationService.class),
                        locator.getFactory(PermissionFactory.class),
                        new TxManagerImpl(() -> new JpaTxContext(serviceEventModuleConfiguration.getEntityManagerFactory().getJpaEntityManagerFactory()), serviceEventModuleConfiguration.maxInsertAttempts),
                        new EventStoreFactoryImpl(),
                        new EventStoreRecordImplJpaRepository(serviceEventModuleConfiguration.getKapuaJpaRepositoryConfiguration())
                ),
                serviceEventModuleConfiguration.getEntityManagerFactory(),
                eventbus,
                servicesEntryList);
        // Start time can be made random from 0 to 30 seconds
        houseKeeperHandler = houseKeeperScheduler.scheduleAtFixedRate(houseKeeperJob, SCHEDULED_EXECUTION_TIME_WINDOW, SCHEDULED_EXECUTION_TIME_WINDOW, TimeUnit.SECONDS);
        LOGGER.info("Starting service event module... DONE");
    }

    @Override
    public void stop() throws KapuaException {
        LOGGER.info("Stopping service event module... {}", this.getClass().getName());
        LOGGER.info("Stopping service event module... house keeper scheduler [step 1/3]");
        if (houseKeeperJob != null) {
            houseKeeperJob.stop();
        } else {
            LOGGER.warn("Cannot shutdown the housekeeper scheduler [step 1/3] since it is null (initialization may not be successful)");
        }
        LOGGER.info("Stopping service event module... house keeper scheduler [step 2/3]");
        if (houseKeeperHandler != null) {
            int waitLoop = 0;
            while (houseKeeperHandler.isDone()) {
                try {
                    Thread.sleep(WAIT_TIME);
                } catch (InterruptedException e) {
                    // do nothing
                }
                if (waitLoop++ > MAX_WAIT_LOOP_ON_SHUTDOWN) {
                    LOGGER.warn("Cannot cancel the house keeper task afeter a while!");
                    break;
                }
            }
        } else {
            LOGGER.warn("Cannot shutdown the housekeeper scheduler [step 2/3] since it is null (initialization may not be successful)");
        }
        LOGGER.info("Stopping service event module... house keeper scheduler [step 3/3]");
        if (houseKeeperScheduler != null) {
            houseKeeperScheduler.shutdown();
        } else {
            LOGGER.warn("Cannot shutdown the housekeeper scheduler [step 3/3] since it is null (initialization may not be successful)");
        }
        LOGGER.info("Stopping service event module... unregister services names");
        if (serviceEventModuleConfiguration != null) {
            ServiceMap.unregisterServices(new ArrayList<>(subscriberNames));
            subscriberNames.clear();
        } else {
            LOGGER.warn("Cannot unregister services since configuration is null (initialization may not be successful)");
        }
        LOGGER.info("Stopping service event module... DONE");
    }

}
