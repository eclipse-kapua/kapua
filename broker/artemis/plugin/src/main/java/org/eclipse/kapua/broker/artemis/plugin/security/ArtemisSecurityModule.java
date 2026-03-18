/*******************************************************************************
 * Copyright (c) 2019, 2026 Eurotech and/or its affiliates and others
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
import org.eclipse.kapua.broker.artemis.plugin.security.context.SecurityContext.LockType;
import org.eclipse.kapua.broker.artemis.plugin.security.metric.LoginMetric;
import org.eclipse.kapua.broker.artemis.plugin.security.setting.BrokerSetting;
import org.eclipse.kapua.broker.artemis.plugin.security.setting.BrokerSettingKey;
import org.eclipse.kapua.commons.cache.LocalCache;
import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.util.lock.RunWithLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provides;

public class ArtemisSecurityModule extends AbstractKapuaModule {

    private static final Logger logger = LoggerFactory.getLogger(ArtemisSecurityModule.class);

    @Override
    protected void configureModule() {
        bind(ServerContext.class).in(Singleton.class);
        bind(MetricsSecurityPlugin.class).in(Singleton.class);
        bind(PluginUtility.class).in(Singleton.class);
        bind(AddressAccessTracker.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    @Named("securityRunWithLock")
    RunWithLock<LockType> securityRunWithLock() {
        return new RunWithLock<LockType>(LockType.class, 256);
    }

    @Provides
    @Singleton
    SecurityContext securityContext(LoginMetric loginMetric,
            BrokerSetting brokerSettings,
            MetricsSecurityPlugin metricsSecurityPlugin,
            PluginUtility pluginUtility,
            @Named("securityRunWithLock") RunWithLock<LockType> runWithLock) {
        int connectionTokenSize = brokerSettings.getInt(BrokerSettingKey.CACHE_CONNECTION_TOKEN_SIZE);
        int connectionTokenTtl = brokerSettings.getInt(BrokerSettingKey.CACHE_CONNECTION_TOKEN_TTL);
        int sessionContextSize = brokerSettings.getInt(BrokerSettingKey.CACHE_SESSION_CONTEXT_SIZE);
        int sessionContextTtl = brokerSettings.getInt(BrokerSettingKey.CACHE_SESSION_CONTEXT_TTL);
        logger.info("Connection token: size {} / ttl {} - session context: size {} / ttl {}",
                connectionTokenSize, connectionTokenTtl, sessionContextSize, sessionContextTtl);
        return new SecurityContext(loginMetric,
                brokerSettings.getBoolean(BrokerSettingKey.PRINT_SECURITY_CONTEXT_REPORT, false),
                new LocalCache<>(
                        connectionTokenSize,
                        connectionTokenTtl,
                        null),
                new LocalCache<>(
                        sessionContextSize,
                        sessionContextTtl,
                        null),
                new LocalCache<>(
                        sessionContextSize,
                        sessionContextTtl,
                        null),
                metricsSecurityPlugin,
                pluginUtility,
                runWithLock
        );
    }

}
