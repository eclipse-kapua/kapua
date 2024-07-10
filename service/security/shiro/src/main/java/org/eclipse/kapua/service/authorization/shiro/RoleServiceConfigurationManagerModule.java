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

import javax.inject.Singleton;

import org.eclipse.kapua.commons.configuration.AccountRelativeFinder;
import org.eclipse.kapua.commons.configuration.CachingServiceConfigRepository;
import org.eclipse.kapua.commons.configuration.ResourceLimitedServiceConfigurationManagerImpl;
import org.eclipse.kapua.commons.configuration.RootUserTester;
import org.eclipse.kapua.commons.configuration.ServiceConfigImplJpaRepository;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManagerCachingWrapper;
import org.eclipse.kapua.commons.configuration.UsedEntitiesCounterImpl;
import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.jpa.EntityCacheFactory;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.service.authorization.role.RoleFactory;
import org.eclipse.kapua.service.authorization.role.RoleRepository;
import org.eclipse.kapua.service.authorization.role.RoleService;

import com.google.inject.Module;
import com.google.inject.multibindings.ClassMapKey;
import com.google.inject.multibindings.ProvidesIntoMap;

/**
 * This module provides the ServiceConfigurationManager for the RoleService.
 * <br><br>
 * Unfortunately Guice does not support overriding for Map binder entries, therefore if you need to change the behaviour of this ServiceConfigurationManager, just skip this module and define the new
 * instance in a separate one
 */
public class RoleServiceConfigurationManagerModule extends AbstractKapuaModule implements Module {

    @Override
    protected void configureModule() {
    }

    @ProvidesIntoMap
    @ClassMapKey(RoleService.class)
    @Singleton
    public ServiceConfigurationManager roleServiceConfigurationManager(
            RoleFactory roleFactory,
            RootUserTester rootUserTester,
            AccountRelativeFinder accountRelativeFinder,
            RoleRepository roleRepository,
            KapuaJpaRepositoryConfiguration jpaRepoConfig,
            EntityCacheFactory entityCacheFactory,
            XmlUtil xmlUtil
    ) {
        return new ServiceConfigurationManagerCachingWrapper(
                new ResourceLimitedServiceConfigurationManagerImpl(
                        RoleService.class.getName(),
                        new CachingServiceConfigRepository(
                                new ServiceConfigImplJpaRepository(jpaRepoConfig),
                                entityCacheFactory.createCache("AbstractKapuaConfigurableServiceCacheId")
                        ),
                        rootUserTester,
                        accountRelativeFinder,
                        new UsedEntitiesCounterImpl(
                                roleFactory,
                                roleRepository
                        ),
                        xmlUtil));
    }
}
