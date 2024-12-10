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
package org.eclipse.kapua.service.user.internal;

import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.kapua.commons.configuration.RootUserTester;
import org.eclipse.kapua.commons.configuration.RootUserTesterImpl;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.core.ServiceModule;
import org.eclipse.kapua.commons.event.ServiceEventHouseKeeperFactoryImpl;
import org.eclipse.kapua.commons.jpa.EventStorer;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaJpaTxManagerFactory;
import org.eclipse.kapua.commons.jpa.NamedCacheFactory;
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
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.user.UserFactory;
import org.eclipse.kapua.service.user.UserRepository;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.service.user.internal.setting.KapuaUserSetting;

import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;

public class UserModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(UserFactory.class).to(UserFactoryImpl.class).in(Singleton.class);
        bind(KapuaUserSetting.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    RootUserTester rootUserTester(UserService userService) {
        return new RootUserTesterImpl(
                userService
        );
    }

    @ProvidesIntoSet
    public Domain userDomain() {
        return new DomainEntry(Domains.USER, UserService.class.getName(), false, Actions.read, Actions.delete, Actions.write);
    }

    @Provides
    @Singleton
    public UserService userService(
            Map<Class<?>, ServiceConfigurationManager> serviceConfigurationManagersByServiceClass,
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            UserRepository userRepository,
            UserFactory userFactory,
            EventStorer eventStorer,
            KapuaJpaTxManagerFactory jpaTxManagerFactory) {
        return new UserServiceImpl(
                serviceConfigurationManagersByServiceClass.get(UserService.class),
                authorizationService,
                permissionFactory,
                jpaTxManagerFactory.create("kapua-user"),
                userRepository,
                userFactory,
                eventStorer);
    }

    @ProvidesIntoSet
    public ServiceModule userServiceModule(UserService userService,
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            KapuaJpaTxManagerFactory txManagerFactory,
            EventStoreFactory eventStoreFactory,
            EventStoreRecordRepository eventStoreRecordRepository,
            ServiceEventBus serviceEventBus,
            KapuaUserSetting kapuaUserSetting,
            @Named("userEvtSubscriptionGroupId") String subscriptionGroupId
    ) throws ServiceEventBusException {
        return new UserServiceModule(
                userService,
                kapuaUserSetting,
                new ServiceEventHouseKeeperFactoryImpl(
                        new EventStoreServiceImpl(
                                authorizationService,
                                permissionFactory,
                                txManagerFactory.create("kapua-user"),
                                eventStoreFactory,
                                eventStoreRecordRepository
                        ),
                        txManagerFactory.create("kapua-user"),
                        serviceEventBus
                ), serviceEventBus,
                subscriptionGroupId);
    }

    @Provides
    @Singleton
    UserRepository userRepository(NamedCacheFactory namedCacheFactory, KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        return new UserCachedRepository(
                new UserImplJpaRepository(jpaRepoConfig),
                namedCacheFactory.createCache("UserId", "UserName")
        );
    }
}
