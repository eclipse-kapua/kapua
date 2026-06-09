/*******************************************************************************
 * Copyright (c) 2016, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console;

import com.google.inject.Provides;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.ContainerIdResolver;
import org.eclipse.kapua.commons.DefaultContainerIdResolver;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationsFacade;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationsFacadeImpl;
import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.util.xml.JAXBContextProvider;
import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;


public class AppModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {

    }

    @Provides
    @Named("metricModuleName")
    String metricModuleName() {
        return "web-console";
    }

    @Singleton
    @Provides
    ContainerIdResolver containerIdResolver() throws KapuaException {
        return new DefaultContainerIdResolver();
    }

    @Provides
    @Named("accountEvtSubscriptionGroupId")
    String accountEvtSubscriptionGroupId(ContainerIdResolver containerIdResolver) {
        return getSubscriptionId(containerIdResolver);
    }

    @Provides
    @Named("authenticationEvtSubscriptionGroupId")
    String authenticationEvtSubscriptionGroupId(ContainerIdResolver containerIdResolver) {
        return getSubscriptionId(containerIdResolver);
    }

    @Provides
    @Named("authorizationEvtSubscriptionGroupId")
    String authorizationEvtSubscriptionGroupId(ContainerIdResolver containerIdResolver) {
        return getSubscriptionId(containerIdResolver);
    }

    @Provides
    @Named("deviceConnectionEvtSubscriptionGroupId")
    String deviceConnectionEvtSubscriptionGroupId(ContainerIdResolver containerIdResolver) {
        return getSubscriptionId(containerIdResolver);
    }

    @Provides
    @Named("deviceRegistryEvtSubscriptionGroupId")
    String deviceRegistryEvtSubscriptionGroupId(ContainerIdResolver containerIdResolver) {
        return getSubscriptionId(containerIdResolver);
    }

    @Provides
    @Named("userEvtSubscriptionGroupId")
    String userEvtSubscriptionGroupId(ContainerIdResolver containerIdResolver) {
        return getSubscriptionId(containerIdResolver);
    }

    @Provides
    @Singleton
    ServiceConfigurationsFacade serviceConfigurationsFacade(
            Map<Class<?>, ServiceConfigurationManager> serviceConfigurationManagersByServiceClass, AuthorizationService authorizationService,
            PermissionFactory permissionFactory, AccountService accountService) {
        return new ServiceConfigurationsFacadeImpl(serviceConfigurationManagersByServiceClass, authorizationService, permissionFactory, accountService);
    }

    @Provides
    @Singleton
    JAXBContextProvider jaxbContextProvider() {
        final JAXBContextProvider jaxbContextProvider = new ConsoleJAXBContextProvider();
        XmlUtil.setContextProvider(jaxbContextProvider);
        return jaxbContextProvider;
    }

    private String getSubscriptionId(ContainerIdResolver containerIdResolver) {
        return "console-" + containerIdResolver.getContainerId();
    }
}
