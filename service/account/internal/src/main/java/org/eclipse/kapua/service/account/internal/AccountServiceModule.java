/*******************************************************************************
 * Copyright (c) 2017, 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.account.internal;

import org.eclipse.kapua.commons.core.ServiceModule;
import org.eclipse.kapua.commons.event.ServiceEventClientConfiguration;
import org.eclipse.kapua.commons.event.ServiceEventHouseKeeperFactory;
import org.eclipse.kapua.commons.event.ServiceEventTransactionalModule;
import org.eclipse.kapua.commons.event.ServiceInspector;
import org.eclipse.kapua.event.ServiceEventBus;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.account.internal.setting.KapuaAccountSetting;
import org.eclipse.kapua.service.account.internal.setting.KapuaAccountSettingKeys;

/**
 * {@link AccountService} {@link ServiceModule} implementation.
 *
 * @since 1.0.0
 */
public class AccountServiceModule extends ServiceEventTransactionalModule {

    public AccountServiceModule(
            AccountService accountService,
            KapuaAccountSetting kapuaAccountSetting,
            ServiceEventHouseKeeperFactory serviceEventHouseKeeperFactory,
            ServiceEventBus serviceEventBus,
            String eventModuleName) {
        super(
                ServiceInspector.getEventBusClients(accountService, AccountService.class).toArray(new ServiceEventClientConfiguration[0]),
                kapuaAccountSetting.getString(KapuaAccountSettingKeys.ACCOUNT_EVENT_ADDRESS),
                eventModuleName,
                serviceEventHouseKeeperFactory,
                serviceEventBus);
    }
}
