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
package org.eclipse.kapua.service.tag.test;

import java.util.Collections;
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
import org.eclipse.kapua.commons.model.query.QueryFactoryImpl;
import org.eclipse.kapua.commons.service.event.store.internal.EventStoreRecordImplJpaRepository;
import org.eclipse.kapua.commons.service.internal.cache.CacheManagerProvider;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.KapuaMessageFactory;
import org.eclipse.kapua.message.internal.KapuaMessageFactoryImpl;
import org.eclipse.kapua.model.query.QueryFactory;
import org.eclipse.kapua.qa.common.MockedLocator;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.account.internal.AccountFactoryImpl;
import org.eclipse.kapua.service.authentication.mfa.MfaAuthenticator;
import org.eclipse.kapua.service.authentication.shiro.mfa.MfaAuthenticatorImpl;
import org.eclipse.kapua.service.authentication.shiro.setting.KapuaAuthenticationSetting;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.access.GroupQueryHelper;
import org.eclipse.kapua.service.authorization.domain.DomainRegistryService;
import org.eclipse.kapua.service.authorization.group.GroupService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
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
import org.eclipse.kapua.service.device.registry.internal.DeviceRegistryServiceImpl;
import org.eclipse.kapua.service.tag.TagFactory;
import org.eclipse.kapua.service.tag.TagRepository;
import org.eclipse.kapua.service.tag.TagService;
import org.eclipse.kapua.service.tag.internal.TagFactoryImpl;
import org.eclipse.kapua.service.tag.internal.TagImplJpaRepository;
import org.eclipse.kapua.service.tag.internal.TagServiceImpl;
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
public class TagLocatorConfiguration {

    /**
     * Setup DI with Google Guice DI. Create mocked and non mocked service under test and bind them with Guice. It is based on custom MockedLocator locator that is meant for service unit tests.
     */
    @Before(value = "@setup", order = 1)
    public void setupDI() {
        MockedLocator mockedLocator = (MockedLocator) KapuaLocator.getInstance();
        final int maxInsertAttempts = 3;

        AbstractModule module = new AbstractModule() {

            @Override
            protected void configure() {
                bind(MfaAuthenticator.class).toInstance(new MfaAuthenticatorImpl(new KapuaAuthenticationSetting()));
                bind(CryptoUtil.class).toInstance(new CryptoUtilImpl(new CryptoSettings()));
                bind(String.class).annotatedWith(Names.named("metricModuleName")).toInstance("tests");
                bind(MetricRegistry.class).toInstance(new MetricRegistry());
                bind(MetricsService.class).to(MetricsServiceImpl.class).in(Singleton.class);

                bind(CommonsMetric.class).toInstance(Mockito.mock(CommonsMetric.class));
                bind(SystemSetting.class).toInstance(SystemSetting.getInstance());
                bind(DomainRegistryService.class).toInstance(Mockito.mock(DomainRegistryService.class));
                final CacheManagerProvider cacheManagerProvider;
                cacheManagerProvider = new CacheManagerProvider(Mockito.mock(CommonsMetric.class), SystemSetting.getInstance());
                bind(javax.cache.CacheManager.class).toInstance(cacheManagerProvider.get());
                // Inject mocked Authorization Service method checkPermission
                AuthorizationService mockedAuthorization = Mockito.mock(AuthorizationService.class);
                bind(KapuaJpaRepositoryConfiguration.class).toInstance(new KapuaJpaRepositoryConfiguration());

                bind(AuthorizationService.class).toInstance(mockedAuthorization);
                // Inject mocked Permission Factory
                final PermissionFactory permissionFactory = Mockito.mock(PermissionFactory.class);
                bind(PermissionFactory.class).toInstance(permissionFactory);
                try {
                    Mockito.doNothing().when(mockedAuthorization).checkPermission(Matchers.any(Permission.class));
                } catch (KapuaException e) {
                    // skip
                }
                bind(QueryFactory.class).toInstance(new QueryFactoryImpl());

                // binding Account related services
                bind(AccountRelativeFinder.class).toInstance(Mockito.mock(AccountRelativeFinder.class));
                bind(AccountService.class).toInstance(Mockito.mock(AccountService.class));
                bind(AccountFactory.class).toInstance(Mockito.spy(new AccountFactoryImpl()));
                bind(RootUserTester.class).toInstance(Mockito.mock(RootUserTester.class));

                final MapBinder<Class, ServiceConfigurationManager> serviceConfigurationManagerMapBinder = MapBinder.newMapBinder(binder(), Class.class, ServiceConfigurationManager.class);

                serviceConfigurationManagerMapBinder.addBinding(DeviceConnectionService.class)
                        .toInstance(Mockito.mock(ServiceConfigurationManager.class));

                // Inject actual Device service related services
                final KapuaJpaRepositoryConfiguration jpaRepoConfig = new KapuaJpaRepositoryConfiguration();
                final EventStorer eventStorer = new EventStorerImpl(new EventStoreRecordImplJpaRepository(jpaRepoConfig));
                bind(TagRepository.class).toInstance(new TagImplJpaRepository(jpaRepoConfig));
                final Map<Class<?>, ServiceConfigurationManager> classServiceConfigurationManagerMap = new HashMap<>();
                classServiceConfigurationManagerMap.put(DeviceConnectionService.class, Mockito.mock(ServiceConfigurationManager.class));

                final DeviceConnectionServiceImpl deviceConnectionService = new DeviceConnectionServiceImpl(
                        classServiceConfigurationManagerMap,
                        mockedAuthorization,
                        permissionFactory,
                        new DeviceConnectionFactoryImpl(),
                        new KapuaJpaTxManagerFactory(maxInsertAttempts).create("kapua-device"),
                        new DeviceConnectionImplJpaRepository(jpaRepoConfig),
                        Collections.emptyMap(),
                        eventStorer);
                bind(DeviceEventRepository.class).toInstance(new DeviceEventImplJpaRepository(jpaRepoConfig));
                final DeviceEventServiceImpl deviceEventService = new DeviceEventServiceImpl(
                        mockedAuthorization,
                        permissionFactory,
                        new KapuaJpaTxManagerFactory(maxInsertAttempts).create("kapua-device"),
                        new DeviceImplJpaRepository(jpaRepoConfig),
                        new DeviceEventFactoryImpl(),
                        new DeviceEventImplJpaRepository(jpaRepoConfig)
                );

                final DeviceValidation deviceValidation = new DeviceValidationImpl(
                        new KapuaDeviceRegistrySettings().getInt(KapuaDeviceRegistrySettingKeys.DEVICE_LIFECYCLE_BIRTH_VAR_FIELDS_LENGTH_MAX),
                        new KapuaDeviceRegistrySettings().getInt(KapuaDeviceRegistrySettingKeys.DEVICE_LIFECYCLE_BIRTH_EXTENDED_PROPERTIES_LENGTH_MAX),
                        mockedAuthorization,
                        permissionFactory,
                        Mockito.mock(GroupService.class),
                        deviceConnectionService,
                        deviceEventService,
                        new DeviceImplJpaRepository(jpaRepoConfig),
                        new DeviceFactoryImpl(),
                        new TagServiceImpl(
                                permissionFactory,
                                mockedAuthorization,
                                Mockito.mock(ServiceConfigurationManager.class),
                                new KapuaJpaTxManagerFactory(maxInsertAttempts).create("kapua-tag"),
                                new TagImplJpaRepository(jpaRepoConfig),
                                new TagFactoryImpl())
                );
                bind(DeviceRegistryService.class).toInstance(
                        new DeviceRegistryServiceImpl(
                                Mockito.mock(ServiceConfigurationManager.class),
                                mockedAuthorization,
                                permissionFactory,
                                new KapuaJpaTxManagerFactory(maxInsertAttempts).create("kapua-device"),
                                new DeviceImplJpaRepository(jpaRepoConfig),
                                new DeviceFactoryImpl(),
                                Mockito.mock(GroupQueryHelper.class),
                                eventStorer,
                                deviceValidation)
                );
                bind(DeviceFactory.class).toInstance(new DeviceFactoryImpl());
                bind(DeviceConnectionService.class).toInstance(deviceConnectionService);
                bind(DeviceConnectionFactory.class).to(DeviceConnectionFactoryImpl.class);

                bind(DeviceRepository.class).toInstance(new DeviceImplJpaRepository(jpaRepoConfig));
                bind(DeviceConnectionRepository.class).toInstance(new DeviceConnectionImplJpaRepository(jpaRepoConfig));
                bind(DeviceEventService.class).toInstance(deviceEventService);
                bind(DeviceEventFactory.class).toInstance(new DeviceEventFactoryImpl());
                bind(KapuaMessageFactory.class).toInstance(new KapuaMessageFactoryImpl());
                bind(TagFactory.class).to(TagFactoryImpl.class);
                bind(TagService.class).toInstance(new TagServiceImpl(
                        permissionFactory,
                        mockedAuthorization,
                        Mockito.mock(ServiceConfigurationManager.class),
                        new KapuaJpaTxManagerFactory(maxInsertAttempts).create("kapua-tag"),
                        new TagImplJpaRepository(new KapuaJpaRepositoryConfiguration()),
                        new TagFactoryImpl()
                ));
            }
        };

        Injector injector = Guice.createInjector(module);
        mockedLocator.setInjector(injector);
    }
}