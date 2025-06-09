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
package org.eclipse.kapua.service.device.registry.test;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.configuration.AccountRelativeFinder;
import org.eclipse.kapua.commons.configuration.RootUserTester;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.crypto.CryptoUtil;
import org.eclipse.kapua.commons.crypto.CryptoUtilImpl;
import org.eclipse.kapua.commons.crypto.setting.CryptoSettings;
import org.eclipse.kapua.commons.jpa.EventStorer;
import org.eclipse.kapua.commons.jpa.EventStorerImpl;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaJpaTxManagerFactory;
import org.eclipse.kapua.commons.metric.CommonsMetric;
import org.eclipse.kapua.commons.metric.MetricsService;
import org.eclipse.kapua.commons.metric.MetricsServiceImpl;
import org.eclipse.kapua.commons.model.mappers.KapuaBaseMapperImpl;
import org.eclipse.kapua.commons.service.event.store.internal.EventStoreRecordImplJpaRepository;
import org.eclipse.kapua.commons.service.internal.cache.CacheManagerProvider;
import org.eclipse.kapua.commons.service.internal.cache.KapuaCacheManager;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.qa.common.MockedLocator;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authentication.mfa.MfaAuthenticator;
import org.eclipse.kapua.service.authentication.shiro.mfa.MfaAuthenticatorImpl;
import org.eclipse.kapua.service.authentication.shiro.setting.KapuaAuthenticationSetting;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.access.GroupQueryHelper;
import org.eclipse.kapua.service.authorization.domain.DomainRegistryService;
import org.eclipse.kapua.service.authorization.group.GroupService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.device.authentication.UserPassDeviceConnectionCredentialAdapter;
import org.eclipse.kapua.service.device.authentication.api.DeviceConnectionCredentialAdapter;
import org.eclipse.kapua.service.device.registry.DeviceFactory;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.DeviceRepository;
import org.eclipse.kapua.service.device.registry.KapuaDeviceRegistrySettingKeys;
import org.eclipse.kapua.service.device.registry.KapuaDeviceRegistrySettings;
import org.eclipse.kapua.service.device.registry.common.DeviceValidation;
import org.eclipse.kapua.service.device.registry.common.DeviceValidationImpl;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionFactory;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionRepository;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionService;
import org.eclipse.kapua.service.device.registry.connection.internal.DeviceConnectionFactoryImpl;
import org.eclipse.kapua.service.device.registry.connection.internal.DeviceConnectionImplJpaRepository;
import org.eclipse.kapua.service.device.registry.connection.internal.DeviceConnectionServiceImpl;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventRepository;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;
import org.eclipse.kapua.service.device.registry.event.internal.DeviceEventFactoryImpl;
import org.eclipse.kapua.service.device.registry.event.internal.DeviceEventImplJpaRepository;
import org.eclipse.kapua.service.device.registry.event.internal.DeviceEventServiceImpl;
import org.eclipse.kapua.service.device.registry.internal.DeviceFactoryImpl;
import org.eclipse.kapua.service.device.registry.internal.DeviceImplJpaRepository;
import org.eclipse.kapua.service.device.registry.internal.DeviceRegistryCacheFactory;
import org.eclipse.kapua.service.device.registry.internal.DeviceRegistryServiceImpl;
import org.eclipse.kapua.service.tag.internal.TagImplJpaRepository;
import org.eclipse.kapua.service.tag.internal.TagMapperImpl;
import org.eclipse.kapua.service.tag.internal.TagServiceImpl;
import org.eclipse.kapua.storage.TxManager;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;

import io.cucumber.java.Before;

@Singleton
public class DeviceRegistryLocatorConfiguration {

    @Before(value = "@setup", order = 1)
    public void setupDI() {
        System.setProperty("locator.class.impl", "org.eclipse.kapua.qa.common.MockedLocator");
        MockedLocator mockedLocator = (MockedLocator) KapuaLocator.getInstance();
        final int maxInsertAttempts = 3;
        AbstractModule module = new AbstractModule() {

            @Override
            protected void configure() {
                bind(CommonsMetric.class).toInstance(Mockito.mock(CommonsMetric.class));
                bind(SystemSetting.class).toInstance(SystemSetting.getInstance());
                bind(DomainRegistryService.class).toInstance(Mockito.mock(DomainRegistryService.class));
                final CacheManagerProvider cacheManagerProvider;
                cacheManagerProvider = new CacheManagerProvider(Mockito.mock(CommonsMetric.class), SystemSetting.getInstance());
                bind(javax.cache.CacheManager.class).toInstance(cacheManagerProvider.get());
                bind(MfaAuthenticator.class).toInstance(new MfaAuthenticatorImpl(new KapuaAuthenticationSetting()));
                bind(CryptoUtil.class).toInstance(new CryptoUtilImpl(new CryptoSettings()));
                bind(String.class).annotatedWith(Names.named("metricModuleName")).toInstance("tests");
                bind(MetricRegistry.class).toInstance(new MetricRegistry());
                bind(MetricsService.class).to(MetricsServiceImpl.class).in(Singleton.class);

                // Inject mocked Authorization Service method checkPermission
                AuthorizationService mockedAuthorization = Mockito.mock(AuthorizationService.class);
                try {
                    Mockito.doNothing().when(mockedAuthorization).checkPermission(Matchers.any(Permission.class));
                } catch (KapuaException e) {
                    // skip
                }
                bind(AuthorizationService.class).toInstance(mockedAuthorization);
                bind(KapuaJpaRepositoryConfiguration.class).toInstance(new KapuaJpaRepositoryConfiguration());
                bind(AccountRelativeFinder.class).toInstance(Mockito.mock(AccountRelativeFinder.class));
                bind(AccountFactory.class).toInstance(Mockito.mock(AccountFactory.class));
                bind(AccountService.class).toInstance(Mockito.mock(AccountService.class));

                bind(RootUserTester.class).toInstance(Mockito.mock(RootUserTester.class));

                // Inject actual Device registry service related services
                //TODO: FIXME: PRIORITY: build test instance
                final CommonsMetric commonsMetric = null;
                final KapuaCacheManager cacheManager = null;

                final DeviceRegistryCacheFactory deviceRegistryCacheFactory = new DeviceRegistryCacheFactory(cacheManager, commonsMetric);
                bind(DeviceRegistryCacheFactory.class).toInstance(deviceRegistryCacheFactory);

                final Map<String, DeviceConnectionCredentialAdapter> availableDeviceConnectionAdapters = new HashMap<>();
                availableDeviceConnectionAdapters.put("USER_PASS", new UserPassDeviceConnectionCredentialAdapter());

                final MapBinder<Class, ServiceConfigurationManager> serviceConfigurationManagerMapBinder = MapBinder.newMapBinder(binder(), Class.class, ServiceConfigurationManager.class);

                serviceConfigurationManagerMapBinder.addBinding(DeviceConnectionService.class)
                        .toInstance(Mockito.mock(ServiceConfigurationManager.class));

                bind(DeviceFactory.class).toInstance(new DeviceFactoryImpl());
                final KapuaJpaRepositoryConfiguration jpaRepoConfig = new KapuaJpaRepositoryConfiguration();
                final TxManager txManager = new KapuaJpaTxManagerFactory(maxInsertAttempts).create("kapua-device");
                final EventStorer eventStorer = new EventStorerImpl(new EventStoreRecordImplJpaRepository(jpaRepoConfig));
                final Map<Class<?>, ServiceConfigurationManager> classServiceConfigurationManagerMap = new HashMap<>();
                classServiceConfigurationManagerMap.put(DeviceConnectionService.class, Mockito.mock(ServiceConfigurationManager.class));

                final DeviceConnectionService deviceConnectionService = new DeviceConnectionServiceImpl(
                        classServiceConfigurationManagerMap,
                        mockedAuthorization,
                        new DeviceConnectionFactoryImpl(),
                        txManager,
                        new DeviceConnectionImplJpaRepository(jpaRepoConfig),
                        availableDeviceConnectionAdapters,
                        eventStorer);
                bind(DeviceConnectionService.class).toInstance(deviceConnectionService);
                bind(DeviceConnectionFactory.class).toInstance(new DeviceConnectionFactoryImpl());

                bind(DeviceRepository.class).toInstance(new DeviceImplJpaRepository(jpaRepoConfig));
                bind(DeviceConnectionRepository.class).toInstance(new DeviceConnectionImplJpaRepository(jpaRepoConfig));
                bind(DeviceEventRepository.class).toInstance(new DeviceEventImplJpaRepository(jpaRepoConfig));
                final DeviceEventService deviceEventService = new DeviceEventServiceImpl(
                        mockedAuthorization,
                        txManager,
                        new DeviceImplJpaRepository(jpaRepoConfig),
                        new DeviceEventFactoryImpl(),
                        new DeviceEventImplJpaRepository(jpaRepoConfig)
                );
                bind(DeviceEventService.class).toInstance(deviceEventService);
                bind(DeviceEventFactory.class).toInstance(new DeviceEventFactoryImpl());

                final DeviceValidation deviceValidation = new DeviceValidationImpl(
                        new KapuaDeviceRegistrySettings().getInt(KapuaDeviceRegistrySettingKeys.DEVICE_LIFECYCLE_BIRTH_VAR_FIELDS_LENGTH_MAX),
                        new KapuaDeviceRegistrySettings().getInt(KapuaDeviceRegistrySettingKeys.DEVICE_LIFECYCLE_BIRTH_EXTENDED_PROPERTIES_LENGTH_MAX),
                        mockedAuthorization,
                        Mockito.mock(GroupService.class),
                        deviceConnectionService,
                        deviceEventService,
                        new DeviceImplJpaRepository(jpaRepoConfig),
                        new TagServiceImpl(
                                mockedAuthorization,
                                Mockito.mock(ServiceConfigurationManager.class),
                                new KapuaJpaTxManagerFactory(maxInsertAttempts).create("kapua-tag"),
                                new TagImplJpaRepository(jpaRepoConfig),
                                new TagMapperImpl(new KapuaBaseMapperImpl())
                        )
                );

                bind(DeviceValidation.class).toInstance(deviceValidation);
                bind(DeviceRegistryService.class).toInstance(new DeviceRegistryServiceImpl(
                        Mockito.mock(ServiceConfigurationManager.class),
                        mockedAuthorization,
                        txManager,
                        new DeviceImplJpaRepository(jpaRepoConfig),
                        new DeviceFactoryImpl(),
                        Mockito.mock(GroupQueryHelper.class),
                        eventStorer,
                        deviceValidation)
                );
            }
        };

        Injector injector = Guice.createInjector(module);
        mockedLocator.setInjector(injector);
    }
}
