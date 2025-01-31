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
package org.eclipse.kapua.service.scheduler.test;

import org.eclipse.kapua.KapuaException;
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
import org.eclipse.kapua.job.engine.client.JobEngineServiceClient;
import org.eclipse.kapua.job.engine.client.settings.JobEngineClientSetting;
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
import org.eclipse.kapua.service.job.internal.JobFactoryImpl;
import org.eclipse.kapua.service.job.internal.JobImplJpaRepository;
import org.eclipse.kapua.service.job.internal.JobServiceImpl;
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

import com.codahale.metrics.MetricRegistry;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import io.cucumber.java.Before;

@Singleton
public class SchedulerLocatorConfiguration {

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

                // Inject mocked Authorization Service method checkPermission
                AuthorizationService mockedAuthorization = Mockito.mock(AuthorizationService.class);
                try {
                    Mockito.doNothing().when(mockedAuthorization).checkPermission(Matchers.any(Permission.class));
                } catch (KapuaException e) {
                    // skip
                }
                bind(AuthorizationService.class).toInstance(mockedAuthorization);
                bind(KapuaJpaRepositoryConfiguration.class).toInstance(new KapuaJpaRepositoryConfiguration());

                // binding Account related services
                bind(AccountRelativeFinder.class).toInstance(Mockito.mock(AccountRelativeFinder.class));
                bind(AccountService.class).toInstance(Mockito.mock(AccountService.class));
                bind(AccountFactory.class).toInstance(Mockito.spy(new AccountFactoryImpl()));
                bind(RootUserTester.class).toInstance(Mockito.mock(RootUserTester.class));

                // Inject actual Tag service related services
                bind(JobFactory.class).toInstance(new JobFactoryImpl());
                final JobFactory jobFactory = new JobFactoryImpl();
                final KapuaJpaRepositoryConfiguration jpaRepoConfig = new KapuaJpaRepositoryConfiguration();
                final JobImplJpaRepository jobRepository = new JobImplJpaRepository(jpaRepoConfig);
                final TriggerImplJpaRepository triggerRepository = new TriggerImplJpaRepository(jpaRepoConfig);

                final TxManager schedulerTxManager = new KapuaJpaTxManagerFactory(maxInsertAttempts).create("kapua-scheduler");
                final TriggerDefinitionFactoryImpl triggerDefinitionFactory = new TriggerDefinitionFactoryImpl();
                final TriggerDefinitionImplJpaRepository triggerDefinitionRepository = new TriggerDefinitionImplJpaRepository(jpaRepoConfig);
                final TriggerFactoryImpl triggerFactory = new TriggerFactoryImpl();
                final TriggerServiceImpl triggerService = new TriggerServiceImpl(
                        mockedAuthorization,
                        schedulerTxManager,
                        triggerRepository,
                        triggerFactory,
                        triggerDefinitionRepository,
                        triggerDefinitionFactory
                );
                bind(JobService.class).toInstance(new JobServiceImpl(
                        Mockito.mock(ServiceConfigurationManager.class),
                        new JobEngineServiceClient(new JobEngineClientSetting(), new XmlUtil(new TestJAXBContextProvider())),
                        mockedAuthorization,
                        new KapuaJpaTxManagerFactory(maxInsertAttempts).create("kapua-job"),
                        jobRepository,
                        triggerService
                ));
                bind(TriggerService.class).toInstance(triggerService);
                bind(TriggerFactory.class).toInstance(triggerFactory);
                bind(TriggerDefinitionService.class).toInstance(new TriggerDefinitionServiceImpl(
                        mockedAuthorization,
                        schedulerTxManager,
                        triggerDefinitionRepository,
                        triggerDefinitionFactory));
                bind(TriggerDefinitionFactory.class).toInstance(triggerDefinitionFactory);
            }
        };

        Injector injector = Guice.createInjector(module);
        mockedLocator.setInjector(injector);
    }
}
