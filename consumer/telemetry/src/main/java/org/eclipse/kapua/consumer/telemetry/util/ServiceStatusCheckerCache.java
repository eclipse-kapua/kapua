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

import org.apache.camel.Exchange;
import org.eclipse.kapua.commons.cache.LocalCache;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.util.lock.RunWithLock;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.management.asset.store.DeviceAssetStoreService;

public class ServiceStatusCheckerCache extends ServiceStatusCheckerNoCache {

    public enum ServiceEnabled {
        TELEMETRY_AND_ASSET
    }

    private final RunWithLock<ServiceEnabled> serviceEnabledRunWithLock;
    private final LocalCache<String, Integer> serviceEnabledLevel;

    public ServiceStatusCheckerCache(AccountService accountService, DeviceAssetStoreService deviceAssetStoreService, ServiceConfigurationManager scmMessageStore,
            LocalCache<String, Integer> serviceEnabledLevel, RunWithLock<ServiceEnabled> serviceEnabledRunWithLock) {
        super(accountService, deviceAssetStoreService, scmMessageStore);
        this.serviceEnabledLevel = serviceEnabledLevel;
        this.serviceEnabledRunWithLock = serviceEnabledRunWithLock;
    }

    public Integer getServiceEnabledLevel(Exchange exchange, String accountName) throws Exception {
        Integer enabledLevel = serviceEnabledLevel.get(accountName);
        //some kind of synchronization would be good (with high concurrency will result in a lot of queries before having an updated value set)
        //but synchronization should be by account. A solution like RunWithLock could be used.
        if (enabledLevel == null) {
            Integer enabledLevelFromNoCache = serviceEnabledRunWithLock.run(ServiceEnabled.TELEMETRY_AND_ASSET, accountName, () -> {
                Integer tmp = serviceEnabledLevel.get(accountName);
                if (tmp == null) {
                    tmp = super.getServiceEnabledLevel(exchange, accountName);
                    serviceEnabledLevel.put(accountName, tmp);
                }
                return tmp;
            });
            exchange.getMessage().setHeader("serviceMessageAndAssetEnabled", enabledLevelFromNoCache);
            return enabledLevelFromNoCache;
        }
        else {
            exchange.getMessage().setHeader("serviceMessageAndAssetEnabled", enabledLevel);
            return enabledLevel;
        }
    }

}
