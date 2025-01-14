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
package org.eclipse.kapua.service.security.test;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.commons.configuration.AccountRelativeFinder;
import org.eclipse.kapua.commons.configuration.ResourceBasedServiceConfigurationMetadataProvider;
import org.eclipse.kapua.commons.configuration.RootUserTester;
import org.eclipse.kapua.commons.configuration.ServiceConfigImplJpaRepository;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.crypto.CryptoUtil;
import org.eclipse.kapua.commons.crypto.CryptoUtilImpl;
import org.eclipse.kapua.commons.crypto.setting.CryptoSettings;
import org.eclipse.kapua.commons.jpa.EventStorerImpl;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaJpaTxManagerFactory;
import org.eclipse.kapua.commons.metric.CommonsMetric;
import org.eclipse.kapua.commons.metric.MetricsService;
import org.eclipse.kapua.commons.metric.MetricsServiceImpl;
import org.eclipse.kapua.commons.service.event.store.internal.EventStoreRecordImplJpaRepository;
import org.eclipse.kapua.commons.service.internal.cache.CacheManagerProvider;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.qa.common.MockedLocator;
import org.eclipse.kapua.qa.common.TestJAXBContextProvider;
import org.eclipse.kapua.service.authentication.credential.CredentialFactory;
import org.eclipse.kapua.service.authentication.credential.CredentialService;
import org.eclipse.kapua.service.authentication.credential.handler.CredentialTypeHandler;
import org.eclipse.kapua.service.authentication.credential.handler.shiro.ApiKeyCredentialTypeHandler;
import org.eclipse.kapua.service.authentication.credential.handler.shiro.JwtCredentialTypeHandler;
import org.eclipse.kapua.service.authentication.credential.handler.shiro.PasswordCredentialTypeHandler;
import org.eclipse.kapua.service.authentication.credential.shiro.AccountPasswordLengthProviderImpl;
import org.eclipse.kapua.service.authentication.credential.shiro.CredentialFactoryImpl;
import org.eclipse.kapua.service.authentication.credential.shiro.CredentialImplJpaRepository;
import org.eclipse.kapua.service.authentication.credential.shiro.CredentialServiceImpl;
import org.eclipse.kapua.service.authentication.credential.shiro.PasswordResetterImpl;
import org.eclipse.kapua.service.authentication.credential.shiro.PasswordValidator;
import org.eclipse.kapua.service.authentication.credential.shiro.PasswordValidatorImpl;
import org.eclipse.kapua.service.authentication.exception.KapuaAuthenticationErrorCodes;
import org.eclipse.kapua.service.authentication.mfa.MfaAuthenticator;
import org.eclipse.kapua.service.authentication.shiro.CredentialServiceConfigurationManagerImpl;
import org.eclipse.kapua.service.authentication.shiro.SystemPasswordLengthProviderImpl;
import org.eclipse.kapua.service.authentication.shiro.mfa.MfaAuthenticatorImpl;
import org.eclipse.kapua.service.authentication.shiro.setting.KapuaAuthenticationSetting;
import org.eclipse.kapua.service.authentication.shiro.setting.KapuaCryptoSetting;
import org.eclipse.kapua.service.authentication.shiro.utils.AuthenticationUtils;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.access.AccessInfoService;
import org.eclipse.kapua.service.authorization.access.AccessRoleService;
import org.eclipse.kapua.service.authorization.access.shiro.AccessInfoFactoryImpl;
import org.eclipse.kapua.service.authorization.access.shiro.AccessRoleFactoryImpl;
import org.eclipse.kapua.service.authorization.domain.DomainRegistryService;
import org.eclipse.kapua.service.authorization.group.GroupFactory;
import org.eclipse.kapua.service.authorization.group.GroupService;
import org.eclipse.kapua.service.authorization.group.shiro.GroupFactoryImpl;
import org.eclipse.kapua.service.authorization.group.shiro.GroupImplJpaRepository;
import org.eclipse.kapua.service.authorization.group.shiro.GroupServiceImpl;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.authorization.permission.shiro.PermissionValidator;
import org.eclipse.kapua.service.authorization.role.RoleFactory;
import org.eclipse.kapua.service.authorization.role.RolePermissionFactory;
import org.eclipse.kapua.service.authorization.role.RoleService;
import org.eclipse.kapua.service.authorization.role.shiro.RoleFactoryImpl;
import org.eclipse.kapua.service.authorization.role.shiro.RoleImplJpaRepository;
import org.eclipse.kapua.service.authorization.role.shiro.RolePermissionFactoryImpl;
import org.eclipse.kapua.service.authorization.role.shiro.RolePermissionImplJpaRepository;
import org.eclipse.kapua.service.authorization.role.shiro.RoleServiceImpl;
import org.eclipse.kapua.service.user.UserFactory;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.service.user.internal.UserFactoryImpl;
import org.eclipse.kapua.service.user.internal.UserImplJpaRepository;
import org.eclipse.kapua.service.user.internal.UserServiceImpl;
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
public class SecurityLocatorConfiguration {

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
                AccessInfoService mockedAccessInfo = Mockito.mock(AccessInfoService.class);
                AccessRoleService mockedAccessRole = Mockito.mock(AccessRoleService.class);

                try {
                    Mockito.doNothing().when(mockedAuthorization).checkPermission(Matchers.any(Permission.class));
                } catch (KapuaException e) {
                    // skip
                }

                bind(KapuaJpaRepositoryConfiguration.class).toInstance(new KapuaJpaRepositoryConfiguration());

                bind(AuthorizationService.class).toInstance(mockedAuthorization);
                // Inject mocked Permission Factory
                PermissionFactory mockPermissionFactory = Mockito.mock(PermissionFactory.class);
                bind(PermissionFactory.class).toInstance(mockPermissionFactory);

                // Inject actual Role service related services
                final KapuaJpaRepositoryConfiguration jpaRepoConfig = new KapuaJpaRepositoryConfiguration();
                final TxManager txManager = new KapuaJpaTxManagerFactory(maxInsertAttempts).create("kapua-authorization");
                bind(RoleService.class).toInstance(new RoleServiceImpl(
                        mockPermissionFactory,
                        mockedAuthorization,
                        new RolePermissionFactoryImpl(),
                        new AccessRoleFactoryImpl(),
                        new AccessInfoFactoryImpl(),
                        mockedAccessRole,
                        mockedAccessInfo,
                        Mockito.mock(ServiceConfigurationManager.class),
                        txManager,
                        new RoleImplJpaRepository(jpaRepoConfig),
                        new RolePermissionImplJpaRepository(jpaRepoConfig),
                        Mockito.mock(PermissionValidator.class)
                ));
                bind(RoleFactory.class).toInstance(new RoleFactoryImpl());
                bind(RolePermissionFactory.class).toInstance(new RolePermissionFactoryImpl());

                bind(GroupService.class).toInstance(new GroupServiceImpl(
                        mockPermissionFactory,
                        mockedAuthorization,
                        Mockito.mock(ServiceConfigurationManager.class),
                        txManager,
                        new GroupImplJpaRepository(jpaRepoConfig)
                ));
                bind(GroupFactory.class).toInstance(new GroupFactoryImpl());
                final CredentialFactoryImpl credentialFactory = new CredentialFactoryImpl();
                bind(CredentialFactory.class).toInstance(credentialFactory);
                final SystemPasswordLengthProviderImpl systemMinimumPasswordLengthProvider = new SystemPasswordLengthProviderImpl(new KapuaAuthenticationSetting());
                final CredentialServiceConfigurationManagerImpl credentialServiceConfigurationManager = new CredentialServiceConfigurationManagerImpl(
                        txManager,
                        new ServiceConfigImplJpaRepository(jpaRepoConfig),
                        systemMinimumPasswordLengthProvider,
                        Mockito.mock(RootUserTester.class),
                        new KapuaAuthenticationSetting(),
                        new ResourceBasedServiceConfigurationMetadataProvider(new XmlUtil(new TestJAXBContextProvider())));
                final CredentialImplJpaRepository credentialRepository = new CredentialImplJpaRepository(jpaRepoConfig);
                final AuthenticationUtils authenticationUtils = authenticationUtils(new KapuaCryptoSetting());
                final AccountPasswordLengthProviderImpl accountPasswordLengthProvider = new AccountPasswordLengthProviderImpl(systemMinimumPasswordLengthProvider,
                        credentialServiceConfigurationManager);
                final PasswordValidator passwordValidator = new PasswordValidatorImpl(accountPasswordLengthProvider);

                bind(CredentialService.class).toInstance(new CredentialServiceImpl(
                        credentialServiceConfigurationManager,
                        mockedAuthorization,
                        mockPermissionFactory,
                        txManager,
                        credentialRepository,
                        credentialFactory,
                        passwordValidator, new KapuaAuthenticationSetting(),
                        new HashSet<CredentialTypeHandler>() {{
                            add(
                                    new PasswordCredentialTypeHandler(
                                            txManager,
                                            credentialRepository,
                                            authenticationUtils,
                                            passwordValidator)
                            );
                            add(
                                    new ApiKeyCredentialTypeHandler(
                                            new KapuaAuthenticationSetting(),
                                            authenticationUtils)
                            );
                            add(new JwtCredentialTypeHandler(authenticationUtils));
                        }},
                        accountPasswordLengthProvider,
                        new PasswordResetterImpl(
                                credentialRepository,
                                new PasswordCredentialTypeHandler(
                                        txManager,
                                        credentialRepository,
                                        authenticationUtils,
                                        passwordValidator
                                ), passwordValidator)
                ));

                final UserFactoryImpl userFactory = new UserFactoryImpl();
                bind(UserFactory.class).toInstance(userFactory);
                final RootUserTester rootUserTester = Mockito.mock(RootUserTester.class);
                bind(RootUserTester.class).toInstance(rootUserTester);
                final AccountRelativeFinder accountRelativeFinder = Mockito.mock(AccountRelativeFinder.class);
                bind(AccountRelativeFinder.class).toInstance(accountRelativeFinder);
                bind(UserService.class).toInstance(new UserServiceImpl(
                        Mockito.mock(ServiceConfigurationManager.class),
                        mockedAuthorization,
                        mockPermissionFactory,
                        new KapuaJpaTxManagerFactory(maxInsertAttempts).create("kapua-user"),
                        new UserImplJpaRepository(jpaRepoConfig),
                        userFactory,
                        new EventStorerImpl(new EventStoreRecordImplJpaRepository(jpaRepoConfig))));
            }
        };

        Injector injector = Guice.createInjector(module);
        mockedLocator.setInjector(injector);
    }

    AuthenticationUtils authenticationUtils(KapuaCryptoSetting kapuaCryptoSetting) {
        final SecureRandom random;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new KapuaRuntimeException(KapuaAuthenticationErrorCodes.CREDENTIAL_CRYPT_ERROR, e);
        }
        return new AuthenticationUtils(random, kapuaCryptoSetting);
    }

}
