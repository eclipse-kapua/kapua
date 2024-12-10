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
package org.eclipse.kapua.service.device.registry;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.kapua.commons.event.ServiceEventClientConfiguration;
import org.eclipse.kapua.commons.event.ServiceEventHouseKeeperFactory;
import org.eclipse.kapua.commons.event.ServiceEventTransactionalModule;
import org.eclipse.kapua.commons.event.ServiceInspector;
import org.eclipse.kapua.event.ServiceEventBus;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionService;

public class DeviceServiceModule extends ServiceEventTransactionalModule {

    public DeviceServiceModule(DeviceConnectionService deviceConnectionService,
            DeviceRegistryService deviceRegistryService,
            KapuaDeviceRegistrySettings deviceRegistrySettings,
            ServiceEventHouseKeeperFactory serviceEventTransactionalHousekeeperFactory,
            ServiceEventBus serviceEventBus,
            String subscriptionGroupId) {
        super(Arrays.asList(ServiceInspector.getEventBusClients(deviceRegistryService, DeviceRegistryService.class),
                                ServiceInspector.getEventBusClients(deviceConnectionService, DeviceConnectionService.class)
                        )
                        .stream()
                        .flatMap(l -> l.stream())
                        .collect(Collectors.toList())
                        .toArray(new ServiceEventClientConfiguration[0]),
                deviceRegistrySettings.getString(KapuaDeviceRegistrySettingKeys.DEVICE_EVENT_ADDRESS),
                subscriptionGroupId,
                serviceEventTransactionalHousekeeperFactory,
                serviceEventBus);
    }
}
