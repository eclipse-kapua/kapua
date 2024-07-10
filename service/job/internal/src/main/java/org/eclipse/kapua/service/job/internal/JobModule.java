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
package org.eclipse.kapua.service.job.internal;

import java.util.Map;

import javax.inject.Named;
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
import org.eclipse.kapua.commons.jpa.KapuaJpaTxManagerFactory;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.job.engine.JobEngineService;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.model.domain.DomainEntry;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.job.JobFactory;
import org.eclipse.kapua.service.job.JobRepository;
import org.eclipse.kapua.service.job.JobService;
import org.eclipse.kapua.service.scheduler.trigger.TriggerService;
import org.eclipse.kapua.storage.TxManager;

import com.google.inject.Provides;
import com.google.inject.multibindings.ClassMapKey;
import com.google.inject.multibindings.ProvidesIntoMap;
import com.google.inject.multibindings.ProvidesIntoSet;

public class JobModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(JobFactory.class).to(JobFactoryImpl.class);
    }

    @ProvidesIntoSet
    public Domain jobdomain() {
        return new DomainEntry(Domains.JOB, JobService.class.getName(), false, Actions.read, Actions.delete, Actions.write, Actions.execute);
    }

    @Provides
    @Singleton
    @Named("jobTxManager")
    TxManager jobTxManager(
            KapuaJpaTxManagerFactory jpaTxManagerFactory
    ) {
        return jpaTxManagerFactory.create("kapua-job");
    }

    @Provides
    @Singleton
    JobService jobService(
            Map<Class<?>, ServiceConfigurationManager> serviceConfigurationManagersByServiceClass,
            JobEngineService jobEngineService,
            PermissionFactory permissionFactory,
            AuthorizationService authorizationService,
            @Named("jobTxManager") TxManager txManager,
            JobRepository jobRepository,
            TriggerService triggerService) {

        return new JobServiceImpl(
                serviceConfigurationManagersByServiceClass.get(JobService.class),
                jobEngineService,
                permissionFactory,
                authorizationService,
                txManager,
                jobRepository,
                triggerService
        );
    }

    @ProvidesIntoMap
    @ClassMapKey(JobService.class)
    @Singleton
    public ServiceConfigurationManager jobServiceConfigurationManager(
            JobFactory factory,
            RootUserTester rootUserTester,
            AccountRelativeFinder accountRelativeFinder,
            JobRepository jobRepository,
            KapuaJpaRepositoryConfiguration jpaRepoConfig,
            EntityCacheFactory entityCacheFactory
    ) {
        return new ServiceConfigurationManagerCachingWrapper(
                new ResourceLimitedServiceConfigurationManagerImpl(
                        JobService.class.getName(),
                        new CachingServiceConfigRepository(
                                new ServiceConfigImplJpaRepository(jpaRepoConfig),
                                entityCacheFactory.createCache("AbstractKapuaConfigurableServiceCacheId")
                        ),
                        rootUserTester,
                        accountRelativeFinder,
                        new UsedEntitiesCounterImpl(
                                factory,
                                jobRepository
                        )));

    }

    @Provides
    @Singleton
    JobRepository jobRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        return new JobImplJpaRepository(jpaRepoConfig);
    }
}
