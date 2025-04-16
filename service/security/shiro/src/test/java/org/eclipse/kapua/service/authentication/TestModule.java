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
package org.eclipse.kapua.service.authentication;

import java.util.Collections;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.activemq.artemis.spi.core.security.jaas.UserPrincipal;
import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.util.xml.JAXBContextProvider;
import org.eclipse.kapua.commons.util.xml.JAXBContextProviderImpl;
import com.google.inject.Provides;

public class TestModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(JAXBContextProvider.class).toInstance(new JAXBContextProviderImpl(Collections.emptySet()));
        bind(UserPrincipal.class).toInstance(new UserPrincipal(""));
    }

    @Provides
    @Named(value = "metricModuleName")
    String metricModuleName() {
        return "test";
    }

    @Provides
    @Named(value = "eventsModuleName")
    String eventsModuleName() {
        return "test";
    }

    @Provides
    @Singleton
    @Named("accountEvtSubscriptionGroupId")
    String accountEvtSubscriptionGroupId() {
        return "shiro-test";
    }

    @Provides
    @Singleton
    @Named("authenticationEvtSubscriptionGroupId")
    String authenticationEvtSubscriptionGroupId() {
        return "shiro-test";
    }

    @Provides
    @Singleton
    @Named("authorizationEvtSubscriptionGroupId")
    String authorizationEvtSubscriptionGroupId() {
        return "shiro-test";
    }

    @Provides
    @Singleton
    @Named("deviceConnectionEvtSubscriptionGroupId")
    String deviceConnectionEvtSubscriptionGroupId() {
        return "shiro-test";
    }

    @Provides
    @Singleton
    @Named("deviceRegistryEvtSubscriptionGroupId")
    String deviceRegistryEvtSubscriptionGroupId() {
        return "shiro-test";
    }

    @Provides
    @Singleton
    @Named("userEvtSubscriptionGroupId")
    String userEvtSubscriptionGroupId() {
        return "shiro-test";
    }

}
