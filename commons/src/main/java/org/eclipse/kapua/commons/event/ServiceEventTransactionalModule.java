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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.core.ServiceModule;
import org.eclipse.kapua.event.ServiceEventBus;
import org.eclipse.kapua.event.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServiceEventTransactionalModule implements ServiceModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceEventTransactionalModule.class);

    private static final int MAX_WAIT_LOOP_ON_SHUTDOWN = 30;
    private static final int SCHEDULED_EXECUTION_TIME_WINDOW = 30;
    private static final long WAIT_TIME = 1000;

    private Set<String> subscriberNames = new HashSet<>();

    private ScheduledExecutorService houseKeeperScheduler;
    private ScheduledFuture<?> houseKeeperHandler;
    private ServiceEventTransactionalHousekeeper houseKeeperJob;

    private String getSubscriptionName(String address, String subscriber) {
        return String.format("%s-%s", address, subscriber);
    }

    private final ServiceEventClientConfiguration[] serviceEventClientConfigurations;
    private final String internalAddress;
    private final ServiceEventHouseKeeperFactory houseKeeperFactory;
    private final ServiceEventBus serviceEventBus;

    public ServiceEventTransactionalModule(
            ServiceEventClientConfiguration[] serviceEventClientConfigurations,
            String internalAddress,
            String uniqueClientId,
            ServiceEventHouseKeeperFactory serviceEventTransactionalHousekeeperFactory,
            ServiceEventBus serviceEventBus) {
        this.serviceEventBus = serviceEventBus;
        this.serviceEventClientConfigurations = appendClientId(uniqueClientId, serviceEventClientConfigurations);
        this.internalAddress = internalAddress;
        this.houseKeeperFactory = serviceEventTransactionalHousekeeperFactory;
    }

    @Override
    public void start() throws KapuaException {
        LOGGER.info("Starting service event module... {}", this.getClass().getName());
        LOGGER.info("Starting service event module... initialize configurations");
        LOGGER.info("Starting service event module... initialize event bus");
        LOGGER.info("Starting service event module... initialize event subscriptions");
        List<ServiceEntry> servicesEntryList = new ArrayList<>();
        if (serviceEventClientConfigurations != null) {
            for (ServiceEventClientConfiguration selc : serviceEventClientConfigurations) {
                //get the specific service address... if empty switch to use the default configuration address
                String address = selc.getAddress();
                if (StringUtils.isEmpty(selc.getAddress())) {
                    address = internalAddress;
                }
                // Listen to upstream service events
                if (selc.getEventListener() != null) {
                    serviceEventBus.subscribe(new Subscription(address, getSubscriptionName(address, selc.getClientName()), selc.getEventListener()));
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
        houseKeeperJob = houseKeeperFactory.apply(servicesEntryList);// new ServiceEventTransactionalHousekeeper(
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
        ServiceMap.unregisterServices(new ArrayList<>(subscriberNames));
        subscriberNames.clear();
        LOGGER.info("Stopping service event module... DONE");
    }

    protected ServiceEventClientConfiguration[] appendClientId(String clientId, ServiceEventClientConfiguration[] configs) {
        return Arrays.stream(configs).map(config -> {
            if (config.getEventListener() == null) {
                // config for @RaiseServiceEvent
                LOGGER.debug("Adding config for @RaiseServiceEvent - address: {}, name: {}, listener: {}", config.getAddress(), config.getClientName(), config.getEventListener());
                return config;
            } else {
                // config for @ListenServiceEvent
                String subscriberName = config.getClientName() + (clientId == null ? "" : "-" + clientId);
                LOGGER.debug("Adding config for @ListenServiceEvent - address: {}, name: {}, listener: {}", config.getAddress(), subscriberName, config.getEventListener());
                return new ServiceEventClientConfiguration(config.getAddress(), subscriberName, config.getEventListener());
            }
        }).toArray(ServiceEventClientConfiguration[]::new);
    }
}
