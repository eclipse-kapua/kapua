/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.broker.artemis.plugin.security;

import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.kapua.broker.artemis.plugin.security.context.SecurityContext;
import org.eclipse.kapua.broker.artemis.plugin.security.metric.LoginMetric;
import org.eclipse.kapua.broker.artemis.plugin.security.setting.BrokerSetting;
import org.eclipse.kapua.broker.artemis.plugin.security.setting.BrokerSettingKey;
import org.eclipse.kapua.client.security.KapuaMessageListener;
import org.eclipse.kapua.client.security.MessageHelper;
import org.eclipse.kapua.client.security.ServiceClient;
import org.eclipse.kapua.client.security.ServiceClientMessagingImpl;
import org.eclipse.kapua.client.security.client.Client;
import org.eclipse.kapua.commons.cache.LocalCache;
import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.setting.system.SystemSetting;

import com.google.inject.Provides;

public class ArtemisSecurityModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(ServerContext.class).in(Singleton.class);
        bind(MetricsSecurityPlugin.class).in(Singleton.class);
        bind(PluginUtility.class).in(Singleton.class);
        bind(RunWithLock.class).in(Singleton.class);
        bind(AddressAccessTracker.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    SecurityContext securityContext(LoginMetric loginMetric,
            BrokerSetting brokerSettings,
            MetricsSecurityPlugin metricsSecurityPlugin,
            PluginUtility pluginUtility,
            RunWithLock runWithLock) {
        return new SecurityContext(loginMetric,
                brokerSettings.getBoolean(BrokerSettingKey.PRINT_SECURITY_CONTEXT_REPORT, false),
                new LocalCache<>(
                        brokerSettings.getInt(BrokerSettingKey.CACHE_CONNECTION_TOKEN_SIZE),
                        brokerSettings.getInt(BrokerSettingKey.CACHE_CONNECTION_TOKEN_TTL),
                        null),
                new LocalCache<>(
                        brokerSettings.getInt(BrokerSettingKey.CACHE_SESSION_CONTEXT_SIZE),
                        brokerSettings.getInt(BrokerSettingKey.CACHE_SESSION_CONTEXT_TTL),
                        null),
                new LocalCache<>(
                        brokerSettings.getInt(BrokerSettingKey.CACHE_SESSION_CONTEXT_SIZE),
                        brokerSettings.getInt(BrokerSettingKey.CACHE_SESSION_CONTEXT_TTL),
                        null),
                metricsSecurityPlugin,
                pluginUtility,
                runWithLock
        );
    }

    @Singleton
    @Provides
    ServiceClient authServiceClient(
            KapuaMessageListener messageListener,
            @Named("clusterName") String clusterName,
            @Named("brokerHost") String brokerHost,
            @Named("serviceBusClient") Client client,
            SystemSetting systemSetting,
            MessageHelper messageHelper) {
        return new ServiceClientMessagingImpl(messageListener, client, messageHelper);
    }

}
