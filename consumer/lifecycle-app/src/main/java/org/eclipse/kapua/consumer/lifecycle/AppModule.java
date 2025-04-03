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
package org.eclipse.kapua.consumer.lifecycle;

import com.google.inject.Provides;
import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.liquibase.DatabaseCheckUpdate;
import org.eclipse.kapua.commons.util.xml.JAXBContextProvider;
import org.eclipse.kapua.commons.util.xml.XmlUtil;

import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.ContainerIdResolver;
import org.eclipse.kapua.commons.DefaultContainerIdResolver;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;

public class AppModule extends AbstractKapuaModule {
    @Override
    protected void configureModule() {
        bind(DatabaseCheckUpdate.class).asEagerSingleton();
        bind(MetricsLifecycle.class).in(Singleton.class);
    }

    @Provides
    @Named("metricModuleName")
    String metricModuleName() {
        return MetricsLifecycle.CONSUMER_LIFECYCLE;
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
    @Named("clusterName")
    String clusterName(SystemSetting systemSetting) {
        return systemSetting.getString(SystemSettingKey.CLUSTER_NAME);
    }

    private String getSubscriptionId(ContainerIdResolver containerIdResolver) {
        return "con-lif-" + containerIdResolver.getContainerId();
    }

    @Provides
    @Singleton
    JAXBContextProvider jaxbContextProvider() {
        final JAXBContextProvider jaxbContextProvider = new LifecycleJAXBContextProvider();
        XmlUtil.setContextProvider(jaxbContextProvider);
        return jaxbContextProvider;
    }
}
