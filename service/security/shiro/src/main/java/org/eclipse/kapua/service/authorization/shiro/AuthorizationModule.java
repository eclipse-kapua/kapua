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
package org.eclipse.kapua.service.authorization.shiro;

import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.core.ServiceModule;
import org.eclipse.kapua.commons.event.ServiceEventHouseKeeperFactoryImpl;
import org.eclipse.kapua.commons.jpa.EntityCacheFactory;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaJpaTxManagerFactory;
import org.eclipse.kapua.commons.jpa.NamedCacheFactory;
import org.eclipse.kapua.commons.metric.CommonsMetric;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreFactory;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecordRepository;
import org.eclipse.kapua.commons.service.event.store.internal.EventStoreServiceImpl;
import org.eclipse.kapua.commons.service.internal.cache.KapuaCacheManager;
import org.eclipse.kapua.event.ServiceEventBus;
import org.eclipse.kapua.event.ServiceEventBusException;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.model.domain.DomainEntry;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.access.AccessInfoFactory;
import org.eclipse.kapua.service.authorization.access.AccessInfoRepository;
import org.eclipse.kapua.service.authorization.access.AccessInfoService;
import org.eclipse.kapua.service.authorization.access.AccessPermissionFactory;
import org.eclipse.kapua.service.authorization.access.AccessPermissionRepository;
import org.eclipse.kapua.service.authorization.access.AccessPermissionService;
import org.eclipse.kapua.service.authorization.access.AccessRoleFactory;
import org.eclipse.kapua.service.authorization.access.AccessRoleRepository;
import org.eclipse.kapua.service.authorization.access.AccessRoleService;
import org.eclipse.kapua.service.authorization.access.GroupQueryHelper;
import org.eclipse.kapua.service.authorization.access.shiro.AccessInfoCacheFactory;
import org.eclipse.kapua.service.authorization.access.shiro.AccessInfoCachingRepository;
import org.eclipse.kapua.service.authorization.access.shiro.AccessInfoFactoryImpl;
import org.eclipse.kapua.service.authorization.access.shiro.AccessInfoImplJpaRepository;
import org.eclipse.kapua.service.authorization.access.shiro.AccessInfoServiceImpl;
import org.eclipse.kapua.service.authorization.access.shiro.AccessPermissionFactoryImpl;
import org.eclipse.kapua.service.authorization.access.shiro.AccessPermissionImplJpaRepository;
import org.eclipse.kapua.service.authorization.access.shiro.AccessPermissionServiceImpl;
import org.eclipse.kapua.service.authorization.access.shiro.AccessRoleFactoryImpl;
import org.eclipse.kapua.service.authorization.access.shiro.AccessRoleImplJpaRepository;
import org.eclipse.kapua.service.authorization.access.shiro.AccessRoleServiceImpl;
import org.eclipse.kapua.service.authorization.access.shiro.CachingAccessPermissionRepository;
import org.eclipse.kapua.service.authorization.access.shiro.CachingAccessRoleRepository;
import org.eclipse.kapua.service.authorization.access.shiro.GroupQueryHelperImpl;
import org.eclipse.kapua.service.authorization.domain.DomainFactory;
import org.eclipse.kapua.service.authorization.domain.DomainRegistryService;
import org.eclipse.kapua.service.authorization.domain.DomainRepository;
import org.eclipse.kapua.service.authorization.domain.shiro.DomainFactoryImpl;
import org.eclipse.kapua.service.authorization.domain.shiro.DomainImplJpaRepository;
import org.eclipse.kapua.service.authorization.domain.shiro.DomainRegistryServiceImpl;
import org.eclipse.kapua.service.authorization.domain.shiro.DomainsAligner;
import org.eclipse.kapua.service.authorization.group.GroupFactory;
import org.eclipse.kapua.service.authorization.group.GroupRepository;
import org.eclipse.kapua.service.authorization.group.GroupService;
import org.eclipse.kapua.service.authorization.group.shiro.GroupFactoryImpl;
import org.eclipse.kapua.service.authorization.group.shiro.GroupImplJpaRepository;
import org.eclipse.kapua.service.authorization.group.shiro.GroupServiceImpl;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.authorization.permission.shiro.PermissionFactoryImpl;
import org.eclipse.kapua.service.authorization.permission.shiro.PermissionValidator;
import org.eclipse.kapua.service.authorization.role.RoleFactory;
import org.eclipse.kapua.service.authorization.role.RolePermissionFactory;
import org.eclipse.kapua.service.authorization.role.RolePermissionRepository;
import org.eclipse.kapua.service.authorization.role.RolePermissionService;
import org.eclipse.kapua.service.authorization.role.RoleRepository;
import org.eclipse.kapua.service.authorization.role.RoleService;
import org.eclipse.kapua.service.authorization.role.shiro.RoleCachingRepository;
import org.eclipse.kapua.service.authorization.role.shiro.RoleFactoryImpl;
import org.eclipse.kapua.service.authorization.role.shiro.RoleImplJpaRepository;
import org.eclipse.kapua.service.authorization.role.shiro.RolePermissionCachingRepository;
import org.eclipse.kapua.service.authorization.role.shiro.RolePermissionFactoryImpl;
import org.eclipse.kapua.service.authorization.role.shiro.RolePermissionImplJpaRepository;
import org.eclipse.kapua.service.authorization.role.shiro.RolePermissionServiceImpl;
import org.eclipse.kapua.service.authorization.role.shiro.RoleServiceImpl;
import org.eclipse.kapua.service.authorization.shiro.setting.KapuaAuthorizationSetting;
import org.eclipse.kapua.storage.TxManager;

import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;

public class AuthorizationModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(AuthorizationService.class).to(AuthorizationServiceImpl.class).in(Singleton.class);
        bind(RoleFactory.class).to(RoleFactoryImpl.class).in(Singleton.class);

        bind(DomainFactory.class).to(DomainFactoryImpl.class).in(Singleton.class);

        bind(PermissionFactory.class).to(PermissionFactoryImpl.class).in(Singleton.class);

        bind(AccessInfoFactory.class).to(AccessInfoFactoryImpl.class).in(Singleton.class);
        bind(AccessPermissionFactory.class).to(AccessPermissionFactoryImpl.class).in(Singleton.class);
        bind(AccessRoleFactory.class).to(AccessRoleFactoryImpl.class).in(Singleton.class);

        bind(RolePermissionFactory.class).to(RolePermissionFactoryImpl.class).in(Singleton.class);

        bind(GroupFactory.class).to(GroupFactoryImpl.class).in(Singleton.class);
        bind(KapuaAuthorizationSetting.class).in(Singleton.class);
        bind(PermissionValidator.class).in(Singleton.class);
        bind(PermissionMapper.class).to(PermissionMapperImpl.class).in(Singleton.class);
        bind(DomainsAligner.class).in(Singleton.class);
    }

    @ProvidesIntoSet
    public Domain accessInfoDomain() {
        return new DomainEntry(Domains.ACCESS_INFO, AccessInfoService.class.getName(), false, Actions.read, Actions.delete, Actions.write);
    }

    @ProvidesIntoSet
    public Domain domainDomain() {
        return new DomainEntry(Domains.DOMAIN, DomainRegistryService.class.getName(), false, Actions.read, Actions.delete, Actions.write);
    }

    @ProvidesIntoSet
    public Domain groupDomain() {
        return new DomainEntry(Domains.GROUP, GroupService.class.getName(), false, Actions.read, Actions.delete, Actions.write);
    }

    @ProvidesIntoSet
    public Domain roleDomain() {
        return new DomainEntry(Domains.ROLE, RoleService.class.getName(), false, Actions.read, Actions.delete, Actions.write);
    }

    @ProvidesIntoSet
    ServiceModule authorizationServiceModule(AccessInfoService accessInfoService,
            RoleService roleService,
            DomainRegistryService domainRegistryService,
            GroupService groupService,
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            KapuaJpaTxManagerFactory txManagerFactory,
            EventStoreFactory eventStoreFactory,
            EventStoreRecordRepository eventStoreRecordRepository,
            ServiceEventBus serviceEventBus,
            KapuaAuthorizationSetting kapuaAuthorizationSetting,
            @Named("authorizationEvtSubscriptionGroupId") String subscriptionGroupId
    ) throws ServiceEventBusException {
        return new AuthorizationServiceModule(
                accessInfoService,
                roleService,
                domainRegistryService,
                groupService,
                kapuaAuthorizationSetting,
                new ServiceEventHouseKeeperFactoryImpl(
                        new EventStoreServiceImpl(
                                authorizationService,
                                permissionFactory,
                                txManagerFactory.create("kapua-authorization"),
                                eventStoreFactory,
                                eventStoreRecordRepository
                        ),
                        txManagerFactory.create("kapua-authorization"),
                        serviceEventBus
                ), serviceEventBus,
                subscriptionGroupId);
    }

    @Provides
    @Singleton
    @Named("authorizationTxManager")
    TxManager authorizationTxManager(
            KapuaJpaTxManagerFactory jpaTxManagerFactory
    ) {
        return jpaTxManagerFactory.create("kapua-authorization");
    }

    @Provides
    @Singleton
    DomainRegistryService domainRegistryService(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            DomainRepository domainRepository,
            DomainFactory domainFactory,
            KapuaJpaTxManagerFactory jpaTxManagerFactory) {
        return new DomainRegistryServiceImpl(
                authorizationService,
                permissionFactory,
                jpaTxManagerFactory.create("kapua-authorization"),
                domainRepository,
                domainFactory);
    }

    @Provides
    @Singleton
    DomainRepository domainRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        return new DomainImplJpaRepository(jpaRepoConfig);
    }

    @Provides
    @Singleton
    RolePermissionService rolePermissionService(PermissionFactory permissionFactory,
            AuthorizationService authorizationService,
            RoleRepository roleRepository,
            RolePermissionRepository rolePermissionRepository,
            KapuaJpaTxManagerFactory jpaTxManagerFactory,
            PermissionValidator permissionValidator) {
        return new RolePermissionServiceImpl(
                authorizationService,
                permissionFactory,
                jpaTxManagerFactory.create("kapua-authorization"),
                roleRepository,
                rolePermissionRepository,
                permissionValidator
        );
    }

    @Provides
    @Singleton
    RoleService roleService(PermissionFactory permissionFactory,
            AuthorizationService authorizationService,
            RolePermissionFactory rolePermissionFactory,
            AccessRoleFactory accessRoleFactory,
            AccessInfoFactory accessInfoFactory,
            AccessRoleService accessRoleService,
            AccessInfoService accessInfoService,
            Map<Class<?>, ServiceConfigurationManager> serviceConfigurationManagersByServiceClass,
            RoleRepository roleRepository,
            RolePermissionRepository rolePermissionRepository,
            KapuaJpaTxManagerFactory jpaTxManagerFactory,
            PermissionValidator permissionValidator
    ) {
        return new RoleServiceImpl(
                permissionFactory,
                authorizationService,
                rolePermissionFactory,
                accessRoleFactory,
                accessInfoFactory,
                accessRoleService,
                accessInfoService,
                serviceConfigurationManagersByServiceClass.get(RoleService.class),
                jpaTxManagerFactory.create("kapua-authorization"),
                roleRepository,
                rolePermissionRepository,
                permissionValidator
        );
    }

    @Provides
    @Singleton
    RoleRepository roleRepository(NamedCacheFactory namedCacheFactory, KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        return new RoleCachingRepository(new RoleImplJpaRepository(jpaRepoConfig),
                namedCacheFactory.createCache("RoleId", "RoleName"));
    }

    @Provides
    @Singleton
    RolePermissionRepository rolePermissionRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig,
            EntityCacheFactory entityCacheFactory) {
        return new RolePermissionCachingRepository(new RolePermissionImplJpaRepository(jpaRepoConfig),
                entityCacheFactory.createCache("RolePermissionId"));
    }

    @Provides
    @Singleton
    GroupService groupService(PermissionFactory permissionFactory,
            AuthorizationService authorizationService,
            Map<Class<?>, ServiceConfigurationManager> serviceConfigurationManagersByServiceClass,
            GroupRepository groupRepository,
            KapuaJpaTxManagerFactory jpaTxManagerFactory) {
        return new GroupServiceImpl(permissionFactory, authorizationService, serviceConfigurationManagersByServiceClass.get(GroupService.class),
                jpaTxManagerFactory.create("kapua-authorization"),
                groupRepository);
    }

    @Provides
    @Singleton
    GroupRepository groupRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        return new GroupImplJpaRepository(jpaRepoConfig);
    }

    @Provides
    @Singleton
    AccessInfoService accessInfoService(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            RoleRepository roleRepository,
            AccessRoleFactory accessRoleFactory,
            AccessRoleRepository accessRoleRepository,
            AccessInfoRepository accessInfoRepository,
            AccessInfoFactory accessInfoFactory,
            AccessPermissionRepository accessPermissionRepository,
            AccessPermissionFactory accessPermissionFactory,
            KapuaJpaTxManagerFactory jpaTxManagerFactory,
            PermissionValidator permissionValidator) {
        return new AccessInfoServiceImpl(authorizationService,
                permissionFactory,
                jpaTxManagerFactory.create("kapua-authorization"),
                roleRepository,
                accessRoleFactory,
                accessRoleRepository,
                accessInfoRepository,
                accessInfoFactory,
                accessPermissionRepository,
                accessPermissionFactory,
                permissionValidator);
    }

    @Provides
    @Singleton
    AccessInfoRepository accessInfoRepository(KapuaCacheManager kapuaCacheManager, CommonsMetric commonsMetric, KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        return new AccessInfoCachingRepository(
                new AccessInfoImplJpaRepository(jpaRepoConfig),
                new AccessInfoCacheFactory(kapuaCacheManager, commonsMetric).createCache()
        );
    }

    @Provides
    @Singleton
    AccessPermissionService accessPermissionService(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            AccessPermissionRepository accessPermissionRepository,
            AccessInfoRepository accessInfoRepository,
            KapuaJpaTxManagerFactory jpaTxManagerFactory,
            PermissionValidator permissionValidator) {
        return new AccessPermissionServiceImpl(authorizationService,
                permissionFactory,
                jpaTxManagerFactory.create("kapua-authorization"),
                accessPermissionRepository,
                accessInfoRepository,
                permissionValidator);
    }

    @Provides
    @Singleton
    AccessPermissionRepository accessPermissionRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig,
            EntityCacheFactory entityCacheFactory) {
        return new CachingAccessPermissionRepository(
                new AccessPermissionImplJpaRepository(jpaRepoConfig),
                entityCacheFactory.createCache("AccessPermissionId")
        );
    }

    @Provides
    @Singleton
    AccessRoleService accessRoleService(RoleRepository roleRepository,
            AccessInfoRepository accessInfoRepository,
            AccessRoleRepository accessRoleRepository,
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            KapuaJpaTxManagerFactory jpaTxManagerFactory) {
        return new AccessRoleServiceImpl(
                jpaTxManagerFactory.create("kapua-authorization"),
                roleRepository,
                accessInfoRepository,
                accessRoleRepository,
                authorizationService,
                permissionFactory
        );
    }

    @Provides
    @Singleton
    AccessRoleRepository accessRoleRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig,
            EntityCacheFactory entityCacheFactory) {
        return new CachingAccessRoleRepository(
                new AccessRoleImplJpaRepository(jpaRepoConfig), entityCacheFactory.createCache("AccessRoleId")
        );
    }

    @Provides
    @Singleton
    GroupQueryHelper groupQueryHelper(
            KapuaJpaTxManagerFactory jpaTxManagerFactory,
            AccessInfoFactory accessInfoFactory,
            AccessInfoRepository accessInfoRepository,
            AccessPermissionRepository accessPermissionRepository,
            AccessRoleRepository accessRoleRepository,
            RoleRepository roleRepository,
            RolePermissionRepository rolePermissionRepository) {
        return new GroupQueryHelperImpl(
                jpaTxManagerFactory.create("kapua-authorization"),
                accessInfoFactory,
                accessInfoRepository,
                accessPermissionRepository,
                accessRoleRepository,
                roleRepository,
                rolePermissionRepository);
    }
}
