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
package org.eclipse.kapua.service.user.internal;

import org.eclipse.kapua.commons.event.ServiceEventClientConfiguration;
import org.eclipse.kapua.commons.event.ServiceEventHouseKeeperFactory;
import org.eclipse.kapua.commons.event.ServiceEventTransactionalModule;
import org.eclipse.kapua.commons.event.ServiceInspector;
import org.eclipse.kapua.event.ServiceEventBus;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.service.user.internal.setting.KapuaUserSetting;
import org.eclipse.kapua.service.user.internal.setting.KapuaUserSettingKeys;

public class UserServiceModule extends ServiceEventTransactionalModule {

    public UserServiceModule(UserService userService, KapuaUserSetting kapuaUserSetting, ServiceEventHouseKeeperFactory serviceEventTransactionalHousekeeperFactory,
            ServiceEventBus serviceEventBus,
            String subscriptionGroupId) {
        super(ServiceInspector.getEventBusClients(userService, UserService.class).toArray(new ServiceEventClientConfiguration[0]),
                kapuaUserSetting.getString(KapuaUserSettingKeys.USER_EVENT_ADDRESS),
                subscriptionGroupId,
                serviceEventTransactionalHousekeeperFactory, serviceEventBus);
    }
}
