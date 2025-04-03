/*******************************************************************************
 * Copyright (c) 2023, 2024 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.connection.listener.internal;

import org.eclipse.kapua.commons.core.ServiceModule;
import org.eclipse.kapua.commons.event.ServiceEventClientConfiguration;
import org.eclipse.kapua.commons.event.ServiceEventHouseKeeperFactory;
import org.eclipse.kapua.commons.event.ServiceEventTransactionalModule;
import org.eclipse.kapua.commons.event.ServiceInspector;
import org.eclipse.kapua.event.ServiceEventBus;
import org.eclipse.kapua.service.device.connection.listener.DeviceConnectionEventListenerService;

public class DeviceConnectionEventListenerServiceModule extends ServiceEventTransactionalModule implements ServiceModule {

    public DeviceConnectionEventListenerServiceModule(DeviceConnectionEventListenerService deviceConnectionEventListenerService, String eventAddress,
            ServiceEventHouseKeeperFactory serviceEventTransactionalHousekeeperFactory,
            ServiceEventBus serviceEventBus,
            String subscriptionGroupId) {
        super(ServiceInspector.getEventBusClients(deviceConnectionEventListenerService, DeviceConnectionEventListenerService.class).toArray(new ServiceEventClientConfiguration[0]),
                eventAddress,
                subscriptionGroupId,
                serviceEventTransactionalHousekeeperFactory, serviceEventBus);
    }

}
