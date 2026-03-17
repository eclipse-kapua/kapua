/*******************************************************************************
 * Copyright (c) 2020, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.consumer.telemetry;

import java.util.Map;

import javax.inject.Named;

import org.eclipse.kapua.commons.cache.LocalCache;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.liquibase.DatabaseCheckUpdate;
import org.eclipse.kapua.commons.util.lock.RunWithLock;
import org.eclipse.kapua.consumer.telemetry.config.ConsumerTelemetrySetting;
import org.eclipse.kapua.consumer.telemetry.config.ConsumerTelemetrySettingKey;
import org.eclipse.kapua.consumer.telemetry.util.ServiceStatusChecker;
import org.eclipse.kapua.consumer.telemetry.util.ServiceStatusCheckerCache;
import org.eclipse.kapua.consumer.telemetry.util.ServiceStatusCheckerCache.ServiceEnabled;
import org.eclipse.kapua.consumer.telemetry.util.ServiceStatusCheckerNoCache;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.camel.application.MetricsCamel;
import org.eclipse.kapua.service.client.protocol.ProtocolDescriptorProvider;
import org.eclipse.kapua.service.datastore.MessageStoreService;
import org.eclipse.kapua.service.datastore.internal.ConfigurationProvider;
import org.eclipse.kapua.service.datastore.internal.MetricsDatastore;
import org.eclipse.kapua.service.device.management.asset.store.DeviceAssetStoreService;
import org.eclipse.kapua.translator.TranslatorHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.inject.TypeLiteral;

@Configuration
public class SpringBridge {

    private static final Logger logger = LoggerFactory.getLogger(SpringBridge.class);

    @Bean
    DatabaseCheckUpdate databaseCheckUpdate() {
        return KapuaLocator.getInstance().getComponent(DatabaseCheckUpdate.class);
    }

    @Bean
    MetricsCamel metricsCamel() {
        return KapuaLocator.getInstance().getComponent(MetricsCamel.class);
    }

    @Bean
    MetricsTelemetry metricsTelemetry() {
        return KapuaLocator.getInstance().getComponent(MetricsTelemetry.class);
    }

    @Bean
    MetricsDatastore metricsDatastore() {
        return KapuaLocator.getInstance().getComponent(MetricsDatastore.class);
    }

    @Bean
    TranslatorHub translatorHub() {
        return KapuaLocator.getInstance().getComponent(TranslatorHub.class);
    }

    @Bean
    ProtocolDescriptorProvider protocolDescriptorProvider() {
        return KapuaLocator.getInstance().getComponent(ProtocolDescriptorProvider.class);
    }

    @Bean
    ConfigurationProvider configurationProvider() {
        return KapuaLocator.getInstance().getComponent(ConfigurationProvider.class);
    }

    @Bean
    AccountService accountService() {
        return KapuaLocator.getInstance().getComponent(AccountService.class);
    }

    @Bean
    @Named("serviceEnabledRunWithLock")
    public RunWithLock<ServiceEnabled> serviceEnabledRunWithLock() {
        return new RunWithLock<ServiceEnabled>(ServiceEnabled.class, 32);//not expected too much concurrency
    }

    @Bean
    ServiceConfigurationManager serviceConfigurationManager() {
        final TypeLiteral<Map<Class<?>, ServiceConfigurationManager>> typeLiteral = new TypeLiteral<Map<Class<?>, ServiceConfigurationManager>>() {
        };
        final Map<Class<?>, ServiceConfigurationManager> component = KapuaLocator.getInstance().getComponent(typeLiteral.getType());
        return component.get(MessageStoreService.class);
    }

    @Bean
    DeviceAssetStoreService getDeviceAssetStoreService() {
        return KapuaLocator.getInstance().getService(DeviceAssetStoreService.class);
    }

    @Bean
    ServiceStatusChecker getServiceStatusChecker(AccountService accountService,
            ServiceConfigurationManager scmMessageStore,
            DeviceAssetStoreService deviceAssetStoreService,
            @Named("serviceEnabledRunWithLock") RunWithLock<ServiceEnabled> serviceEnabledRunWithLock) {
        int defaultCacheTTL = 30;
        int defaultCacheSize = 1000;
        int cacheTtl = ConsumerTelemetrySetting.getInstance().getInt(ConsumerTelemetrySettingKey.TELEMETRY_SERVICE_SETTINGS_CACHE_TTL, defaultCacheTTL);
        int cacheSize = ConsumerTelemetrySetting.getInstance().getInt(ConsumerTelemetrySettingKey.TELEMETRY_SERVICE_SETTINGS_CACHE_SIZE, defaultCacheSize);
        if (cacheTtl>0 && cacheSize>0) {
            logger.info("Initializing telemetry service settings cache: size {} - ttl {}[s]", cacheSize, cacheTtl);
            return new ServiceStatusCheckerCache(accountService, deviceAssetStoreService, scmMessageStore,
                    new LocalCache<String, Integer>(cacheSize, cacheTtl, null), serviceEnabledRunWithLock);
        }
        else {
            logger.info("Cannot initialize telemetry service settings cache since one parameter is not positive: size {} - ttl {}[s]", cacheSize, cacheTtl);
            return new ServiceStatusCheckerNoCache(accountService, deviceAssetStoreService, scmMessageStore);
        }
    }
}