/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.job.test;

import org.eclipse.kapua.commons.configuration.AccountRelativeFinder;
import org.eclipse.kapua.commons.configuration.RootUserTester;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.crypto.CryptoUtil;
import org.eclipse.kapua.commons.crypto.CryptoUtilImpl;
import org.eclipse.kapua.commons.crypto.setting.CryptoSettings;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaJpaTxManagerFactory;
import org.eclipse.kapua.commons.metric.CommonsMetric;
import org.eclipse.kapua.commons.metric.MetricsService;
import org.eclipse.kapua.commons.metric.MetricsServiceImpl;
import org.eclipse.kapua.commons.service.internal.cache.CacheManagerProvider;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.job.engine.JobEngineService;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.qa.common.MockedLocator;
import org.eclipse.kapua.qa.common.TestJAXBContextProvider;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.account.internal.AccountFactoryImpl;
import org.eclipse.kapua.service.authentication.mfa.MfaAuthenticator;
import org.eclipse.kapua.service.authentication.shiro.mfa.MfaAuthenticatorImpl;
import org.eclipse.kapua.service.authentication.shiro.setting.KapuaAuthenticationSetting;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.domain.DomainRegistryService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.job.JobFactory;
import org.eclipse.kapua.service.job.JobService;
import org.eclipse.kapua.service.job.execution.JobExecutionFactory;
import org.eclipse.kapua.service.job.execution.JobExecutionService;
import org.eclipse.kapua.service.job.execution.internal.JobExecutionFactoryImpl;
import org.eclipse.kapua.service.job.execution.internal.JobExecutionImplJpaRepository;
import org.eclipse.kapua.service.job.execution.internal.JobExecutionServiceImpl;
import org.eclipse.kapua.service.job.internal.JobFactoryImpl;
import org.eclipse.kapua.service.job.internal.JobImplJpaRepository;
import org.eclipse.kapua.service.job.internal.JobServiceImpl;
import org.eclipse.kapua.service.job.step.JobStepFactory;
import org.eclipse.kapua.service.job.step.JobStepService;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionFactory;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionService;
import org.eclipse.kapua.service.job.step.definition.internal.JobStepDefinitionFactoryImpl;
import org.eclipse.kapua.service.job.step.definition.internal.JobStepDefinitionImplJpaRepository;
import org.eclipse.kapua.service.job.step.definition.internal.JobStepDefinitionServiceImpl;
import org.eclipse.kapua.service.job.step.internal.JobStepFactoryImpl;
import org.eclipse.kapua.service.job.step.internal.JobStepImplJpaRepository;
import org.eclipse.kapua.service.job.step.internal.JobStepServiceImpl;
import org.eclipse.kapua.service.job.targets.JobTargetFactory;
import org.eclipse.kapua.service.job.targets.JobTargetService;
import org.eclipse.kapua.service.job.targets.internal.JobTargetFactoryImpl;
import org.eclipse.kapua.service.job.targets.internal.JobTargetImplJpaRepository;
import org.eclipse.kapua.service.job.targets.internal.JobTargetServiceImpl;
import org.eclipse.kapua.service.scheduler.trigger.TriggerFactory;
import org.eclipse.kapua.service.scheduler.trigger.TriggerService;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionFactory;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionService;
import org.eclipse.kapua.service.scheduler.trigger.definition.quartz.TriggerDefinitionFactoryImpl;
import org.eclipse.kapua.service.scheduler.trigger.definition.quartz.TriggerDefinitionImplJpaRepository;
import org.eclipse.kapua.service.scheduler.trigger.definition.quartz.TriggerDefinitionServiceImpl;
import org.eclipse.kapua.service.scheduler.trigger.quartz.TriggerFactoryImpl;
import org.eclipse.kapua.service.scheduler.trigger.quartz.TriggerImplJpaRepository;
import org.eclipse.kapua.service.scheduler.trigger.quartz.TriggerServiceImpl;
import org.eclipse.kapua.storage.TxManager;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import io.cucumber.java.Before;

@Singleton
public class JobLocatorConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(JobLocatorConfiguration.class);

    @Before(value = "@setup", order = 1)
    public void setupDI() {
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

                // Account
                bind(AccountRelativeFinder.class).toInstance(Mockito.mock(AccountRelativeFinder.class));
                bind(AccountService.class).toInstance(Mockito.mock(AccountService.class));
                bind(AccountFactory.class).toInstance(Mockito.spy(new AccountFactoryImpl()));
                bind(KapuaJpaRepositoryConfiguration.class).toInstance(new KapuaJpaRepositoryConfiguration());

                bind(RootUserTester.class).toInstance(Mockito.mock(RootUserTester.class));

                final KapuaJpaRepositoryConfiguration jpaRepoConfig = new KapuaJpaRepositoryConfiguration();
                // Auth
                // Inject mocked Authorization Service method checkPermission
                AuthorizationService mockedAuthorization = Mockito.mock(AuthorizationService.class);
                try {
                    Mockito.doNothing().when(mockedAuthorization).checkPermission(Matchers.any(Permission.class));
                    bind(AuthorizationService.class).toInstance(mockedAuthorization);
                } catch (Exception e) {
                    LOG.warn("Error while setting mock AuthorizationService. This may lead to failures...", e);
                }

                // Job
                bind(JobFactory.class).toInstance(new JobFactoryImpl());
                final TriggerImplJpaRepository triggerImplJpaRepository = new TriggerImplJpaRepository(jpaRepoConfig);
                final TxManager txManager = new KapuaJpaTxManagerFactory(maxInsertAttempts).create("kapua-job");
                bind(JobService.class).toInstance(new JobServiceImpl(
                        Mockito.mock(ServiceConfigurationManager.class),
                        Mockito.mock(JobEngineService.class),
                        mockedAuthorization,
                        txManager,
                        new JobImplJpaRepository(jpaRepoConfig),
                        new TriggerServiceImpl(mockedAuthorization,
                                txManager, triggerImplJpaRepository, new TriggerFactoryImpl(),
                                new TriggerDefinitionImplJpaRepository(jpaRepoConfig),
                                new TriggerDefinitionFactoryImpl()
                        )));
                bind(JobStepDefinitionService.class).toInstance(new JobStepDefinitionServiceImpl(
                        mockedAuthorization,
                        txManager,
                        new JobStepDefinitionImplJpaRepository(jpaRepoConfig)
                ));
                bind(JobStepDefinitionFactory.class).toInstance(new JobStepDefinitionFactoryImpl());
                final JobExecutionImplJpaRepository jobExecutionRepository = new JobExecutionImplJpaRepository(jpaRepoConfig);
                final JobExecutionService jobExecutionService = new JobExecutionServiceImpl(mockedAuthorization, txManager, jobExecutionRepository);
                bind(JobStepService.class).toInstance(new JobStepServiceImpl(
                        mockedAuthorization,
                        txManager,
                        new JobStepImplJpaRepository(jpaRepoConfig),
                        new JobStepFactoryImpl(),
                        jobExecutionService,
                        new JobStepDefinitionImplJpaRepository(jpaRepoConfig),
                        new XmlUtil(new TestJAXBContextProvider())
                ));
                bind(JobStepFactory.class).toInstance(new JobStepFactoryImpl());
                bind(JobTargetService.class).toInstance(new JobTargetServiceImpl(
                        mockedAuthorization,
                        txManager,
                        new JobTargetImplJpaRepository(jpaRepoConfig),
                        new JobTargetFactoryImpl(),
                        new JobImplJpaRepository(jpaRepoConfig)
                ));
                bind(JobTargetFactory.class).toInstance(new JobTargetFactoryImpl());
                bind(JobExecutionService.class).toInstance(new JobExecutionServiceImpl(
                        mockedAuthorization,
                        txManager,
                        jobExecutionRepository
                ));
                bind(JobExecutionFactory.class).toInstance(new JobExecutionFactoryImpl());

                // Trigger
                final TriggerDefinitionFactoryImpl triggerDefinitionFactory = new TriggerDefinitionFactoryImpl();
                final TriggerDefinitionImplJpaRepository triggerDefinitionRepository = new TriggerDefinitionImplJpaRepository(jpaRepoConfig);
                final TriggerFactoryImpl triggerFactory = new TriggerFactoryImpl();
                bind(TriggerService.class).toInstance(new TriggerServiceImpl(
                        mockedAuthorization,
                        txManager,
                        triggerImplJpaRepository,
                        triggerFactory,
                        triggerDefinitionRepository,
                        triggerDefinitionFactory
                ));
                bind(TriggerFactory.class).toInstance(triggerFactory);
                bind(TriggerDefinitionService.class).toInstance(new TriggerDefinitionServiceImpl(
                        mockedAuthorization,
                        txManager,
                        triggerDefinitionRepository,
                        triggerDefinitionFactory
                ));
                bind(TriggerDefinitionFactory.class).toInstance(triggerDefinitionFactory);
            }
        };

        Injector injector = Guice.createInjector(module);
        mockedLocator.setInjector(injector);
    }
}
