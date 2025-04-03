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

import com.google.inject.Provides;
import org.eclipse.kapua.commons.core.AbstractKapuaModule;

import javax.inject.Named;
import javax.inject.Singleton;

public class TestModule extends AbstractKapuaModule {
    @Override
    protected void configureModule() {

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
