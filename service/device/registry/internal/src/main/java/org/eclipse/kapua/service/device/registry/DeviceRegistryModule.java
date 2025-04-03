/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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

import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.core.ServiceModule;
import org.eclipse.kapua.commons.event.ServiceEventHouseKeeperFactoryImpl;
import org.eclipse.kapua.commons.jpa.EventStorer;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaJpaTxManagerFactory;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreFactory;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecordRepository;
import org.eclipse.kapua.commons.service.event.store.internal.EventStoreServiceImpl;
import org.eclipse.kapua.event.ServiceEventBus;
import org.eclipse.kapua.event.ServiceEventBusException;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.model.domain.DomainEntry;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.access.GroupQueryHelper;
import org.eclipse.kapua.service.authorization.group.GroupService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.device.authentication.api.DeviceConnectionCredentialAdapter;
import org.eclipse.kapua.service.device.registry.common.DeviceValidation;
import org.eclipse.kapua.service.device.registry.common.DeviceValidationImpl;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionFactory;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionRepository;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionService;
import org.eclipse.kapua.service.device.registry.connection.internal.CachingDeviceConnectionRepository;
import org.eclipse.kapua.service.device.registry.connection.internal.DeviceConnectionFactoryImpl;
import org.eclipse.kapua.service.device.registry.connection.internal.DeviceConnectionImplJpaRepository;
import org.eclipse.kapua.service.device.registry.connection.internal.DeviceConnectionServiceImpl;
import org.eclipse.kapua.service.device.registry.connection.option.DeviceConnectionOptionFactory;
import org.eclipse.kapua.service.device.registry.connection.option.DeviceConnectionOptionRepository;
import org.eclipse.kapua.service.device.registry.connection.option.DeviceConnectionOptionService;
import org.eclipse.kapua.service.device.registry.connection.option.internal.DeviceConnectionOptionFactoryImpl;
import org.eclipse.kapua.service.device.registry.connection.option.internal.DeviceConnectionOptionImplJpaRepository;
import org.eclipse.kapua.service.device.registry.connection.option.internal.DeviceConnectionOptionServiceImpl;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventRepository;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;
import org.eclipse.kapua.service.device.registry.event.internal.DeviceEventFactoryImpl;
import org.eclipse.kapua.service.device.registry.event.internal.DeviceEventImplJpaRepository;
import org.eclipse.kapua.service.device.registry.event.internal.DeviceEventServiceImpl;
import org.eclipse.kapua.service.device.registry.internal.CachingDeviceRepository;
import org.eclipse.kapua.service.device.registry.internal.DeviceFactoryImpl;
import org.eclipse.kapua.service.device.registry.internal.DeviceImplJpaRepository;
import org.eclipse.kapua.service.device.registry.internal.DeviceRegistryCache;
import org.eclipse.kapua.service.device.registry.internal.DeviceRegistryCacheFactory;
import org.eclipse.kapua.service.device.registry.internal.DeviceRegistryServiceImpl;
import org.eclipse.kapua.service.device.registry.lifecycle.DeviceLifeCycleService;
import org.eclipse.kapua.service.device.registry.lifecycle.internal.DeviceLifeCycleServiceImpl;
import org.eclipse.kapua.service.tag.TagService;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;

/**
 * kapua-device-registry-internal's {@link AbstractKapuaModule}
 *
 * @since 2.0.0
 */
public class DeviceRegistryModule extends AbstractKapuaModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRegistryModule.class);

    @Override
    protected void configureModule() {
        bind(DeviceRegistryCacheFactory.class).in(Singleton.class);
        bind(DeviceFactory.class).to(DeviceFactoryImpl.class).in(Singleton.class);
        bind(DeviceConnectionFactory.class).to(DeviceConnectionFactoryImpl.class).in(Singleton.class);
        bind(DeviceConnectionOptionFactory.class).to(DeviceConnectionOptionFactoryImpl.class).in(Singleton.class);
        bind(DeviceEventFactory.class).to(DeviceEventFactoryImpl.class).in(Singleton.class);
        bind(DeviceLifeCycleService.class).to(DeviceLifeCycleServiceImpl.class).in(Singleton.class);
        bind(KapuaDeviceRegistrySettings.class).in(Singleton.class);
        //This needs to be auto-created by guice, otherwise the RaiseServiceEventInterceptor will not work (sic!)
        bind(DeviceConnectionService.class).to(DeviceConnectionServiceImpl.class).in(Singleton.class);
    }

    @ProvidesIntoSet
    public Domain deviceDomain() {
        return new DomainEntry(Domains.DEVICE, DeviceRegistryService.class.getName(), true, Actions.read, Actions.delete, Actions.write);
    }

    @ProvidesIntoSet
    public Domain deviceLifecycleDomain() {
        return new DomainEntry(Domains.DEVICE_LIFECYCLE, DeviceLifeCycleService.class.getName(), false, Actions.read, Actions.delete, Actions.write);
    }

    @ProvidesIntoSet
    public Domain deviceConnectionDomain() {
        return new DomainEntry(Domains.DEVICE_CONNECTION, DeviceConnectionService.class.getName(), false, Actions.read, Actions.delete, Actions.write);
    }

    @ProvidesIntoSet
    public Domain deviceEventDomain() {
        return new DomainEntry(Domains.DEVICE_EVENT, DeviceEventService.class.getName(), false, Actions.read, Actions.delete, Actions.write);
    }

    @ProvidesIntoSet
    ServiceModule deviceRegistryModule(DeviceConnectionService deviceConnectionService,
            DeviceRegistryService deviceRegistryService,
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            KapuaJpaTxManagerFactory txManagerFactory,
            EventStoreFactory eventStoreFactory,
            EventStoreRecordRepository eventStoreRecordRepository,
            ServiceEventBus serviceEventBus,
            KapuaDeviceRegistrySettings kapuaDeviceRegistrySettings,
            KapuaJpaTxManagerFactory jpaTxManagerFactory,
            @Named("deviceRegistryEvtSubscriptionGroupId") String subscriptionGroupId
    ) throws ServiceEventBusException {
        return new DeviceServiceModule(
                deviceConnectionService,
                deviceRegistryService,
                kapuaDeviceRegistrySettings,
                new ServiceEventHouseKeeperFactoryImpl(
                        new EventStoreServiceImpl(
                                authorizationService,
                                permissionFactory,
                                jpaTxManagerFactory.create("kapua-device"),
                                eventStoreFactory,
                                eventStoreRecordRepository
                        ),
                        jpaTxManagerFactory.create("kapua-device"),
                        serviceEventBus
                ),
                serviceEventBus,
                subscriptionGroupId);
    }

    @Provides
    @Singleton
    DeviceValidation deviceValidation(KapuaDeviceRegistrySettings deviceRegistrySettings,
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            GroupService groupService,
            DeviceConnectionService deviceConnectionService,
            DeviceEventService deviceEventService,
            DeviceRepository deviceRepository,
            DeviceFactory deviceFactory,
            TagService tagService) {
        return new DeviceValidationImpl(deviceRegistrySettings.getInt(KapuaDeviceRegistrySettingKeys.DEVICE_LIFECYCLE_BIRTH_VAR_FIELDS_LENGTH_MAX),
                deviceRegistrySettings.getInt(KapuaDeviceRegistrySettingKeys.DEVICE_LIFECYCLE_BIRTH_EXTENDED_PROPERTIES_LENGTH_MAX),
                authorizationService,
                permissionFactory,
                groupService,
                deviceConnectionService,
                deviceEventService,
                deviceRepository,
                deviceFactory,
                tagService);
    }

    @Provides
    @Singleton
    DeviceRegistryService deviceRegistryService(
            Map<Class<?>, ServiceConfigurationManager> serviceConfigurationManagersByServiceClass,
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            DeviceRepository deviceRepository,
            DeviceFactory deviceFactory,
            GroupQueryHelper groupQueryHelper,
            EventStorer eventStorer,
            @Named("DeviceRegistryTransactionManager") TxManager deviceRegistryTxManager,
            DeviceValidation deviceValidation) {
        return new DeviceRegistryServiceImpl(
                serviceConfigurationManagersByServiceClass.get(DeviceRegistryService.class),
                authorizationService,
                permissionFactory,
                deviceRegistryTxManager,
                deviceRepository,
                deviceFactory,
                groupQueryHelper,
                eventStorer,
                deviceValidation);
    }

    @Provides
    @Singleton
    DeviceRepository deviceRepository(DeviceFactory deviceFactory,
            DeviceRegistryCache deviceRegistryCache,
            KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        return new CachingDeviceRepository(new DeviceImplJpaRepository(jpaRepoConfig),
                deviceRegistryCache
        );
    }

    @Provides
    @Named("DeviceRegistryTransactionManager")
    @Singleton
    protected TxManager deviceRegistryTxManager(KapuaJpaTxManagerFactory jpaTxManagerFactory) {
        return jpaTxManagerFactory.create("kapua-device");
    }

    @Provides
    @Singleton
    DeviceRegistryCache deviceRegistryCache(DeviceRegistryCacheFactory deviceRegistryCacheFactory) {
        return (DeviceRegistryCache) deviceRegistryCacheFactory.createCache();
    }

    @Provides
    @Singleton
    protected DeviceConnectionRepository deviceConnectionRepository(DeviceRegistryCache deviceRegistryCache,
            KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        return new CachingDeviceConnectionRepository(
                new DeviceConnectionImplJpaRepository(jpaRepoConfig),
                deviceRegistryCache
        );
    }

    @Provides
    @Singleton
    protected DeviceConnectionOptionService deviceConnectionOptionService(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            DeviceConnectionRepository deviceConnectionRepository,
            DeviceConnectionFactory entityFactory,
            DeviceConnectionOptionRepository repository,
            @Named("DeviceRegistryTransactionManager") TxManager txManager,
            Map<String, DeviceConnectionCredentialAdapter> availableDeviceConnectionAdapters) {
        return new DeviceConnectionOptionServiceImpl(
                authorizationService,
                permissionFactory,
                txManager,
                deviceConnectionRepository,
                entityFactory,
                repository,
                availableDeviceConnectionAdapters);
    }

    @Provides
    @Singleton
    protected DeviceConnectionOptionRepository deviceConnectionOptionRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        return new DeviceConnectionOptionImplJpaRepository(jpaRepoConfig);
    }

    @Provides
    @Singleton
    protected DeviceEventService deviceEventService(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            DeviceRepository deviceRepository,
            DeviceEventFactory entityFactory,
            DeviceEventRepository deviceEventRepository,
            @Named("DeviceRegistryTransactionManager") TxManager txManager) {
        return new DeviceEventServiceImpl(
                authorizationService,
                permissionFactory,
                txManager,
                deviceRepository,
                entityFactory,
                deviceEventRepository);
    }

    @Provides
    @Singleton
    protected DeviceEventRepository deviceEventRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        return new DeviceEventImplJpaRepository(jpaRepoConfig);
    }
}