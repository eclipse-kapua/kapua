/*******************************************************************************
 * Copyright (c) 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.consumer.telemetry.util;

import java.util.Map;

import org.apache.camel.Exchange;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.datastore.internal.mediator.MessageStoreConfiguration;
import org.eclipse.kapua.service.device.management.asset.store.DeviceAssetStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceStatusCheckerNoCache implements ServiceStatusChecker {

    private static final Logger logger = LoggerFactory.getLogger(ServiceStatusCheckerNoCache.class);

    protected AccountService accountService;
    protected DeviceAssetStoreService deviceAssetStoreService;
    protected ServiceConfigurationManager scmMessageStore;

    public ServiceStatusCheckerNoCache(AccountService accountService,
            DeviceAssetStoreService deviceAssetStoreService,
            ServiceConfigurationManager scmMessageStore) {
        this.accountService = accountService;
        this.deviceAssetStoreService = deviceAssetStoreService;
        this.scmMessageStore = scmMessageStore;
    }

    public Integer getServiceEnabledLevel(Exchange exchange, String accountName) throws Exception {
        Account account = findAccount(accountName);
        //some kind of synchronization would be good (with high concurrency will result in a lot of queries before having an updated value set)
        //but synchronization should be by account. A solution like RunWithLock could be used.
        Map<String, Object> configMessageStore = scmMessageStore.getConfigValues(account.getId(), false);
        Integer ttl = (Integer)configMessageStore.get("dataTTL");
        int option = Boolean.TRUE.equals((Boolean)configMessageStore.get("enabled")) && (ttl!=null && ttl.intValue()!=MessageStoreConfiguration.DISABLED) ? 1 : 0;
        Map<String, Object> configAssetStore = deviceAssetStoreService.getConfigValues(account.getId());
        option += configAssetStore != null && Boolean.TRUE.equals((Boolean)configAssetStore.get("enabled")) ? 2 : 0;
        return option;
    }

    private Account findAccount(String accountName) throws KapuaException {
        Account account = accountService.findByName(accountName);
        if (account == null) {
            logger.warn("Cannot find account for account name {}", accountName);
            throw new KapuaIllegalArgumentException("account", accountName);
        }
        else {
            return account;
        }
    }

}