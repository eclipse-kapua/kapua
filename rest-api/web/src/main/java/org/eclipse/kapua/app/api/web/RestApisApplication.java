/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.api.web;

import java.util.HashMap;

import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.eclipse.kapua.app.api.core.MoxyJsonFeatureCustomJsonProvider;
import org.eclipse.kapua.commons.jersey.rest.ExceptionConfigurationProvider;
import org.eclipse.kapua.commons.jersey.rest.ExceptionConfigurationProviderImpl;
import org.eclipse.kapua.commons.jersey.rest.JaxbContextResolver;
import org.eclipse.kapua.commons.jersey.rest.KapuaSerializableBodyWriter;
import org.eclipse.kapua.commons.jersey.rest.ListBodyWriter;
import org.eclipse.kapua.commons.jersey.rest.MoxyJsonConfigContextResolver;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.locator.guice.GuiceLocatorImpl;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.UriConnegFilter;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

public class RestApisApplication extends ResourceConfig {

    public RestApisApplication() throws JAXBException {
        register(new AbstractBinder() {

            @Override
            protected void configure() {
                this.bind(ExceptionConfigurationProviderImpl.class)
                        .to(ExceptionConfigurationProvider.class)
                        .in(Singleton.class);
            }
        });

        packages("org.eclipse.kapua.commons.jersey",
                "org.eclipse.kapua.app.api",
                "org.eclipse.kapua.service");

        // Bind media type to resource extension
        HashMap<String, MediaType> mappedMediaTypes = new HashMap<>();
        mappedMediaTypes.put("xml", MediaType.APPLICATION_XML_TYPE);
        mappedMediaTypes.put("json", MediaType.APPLICATION_JSON_TYPE);

        property(ServerProperties.MEDIA_TYPE_MAPPINGS, mappedMediaTypes);
        property(ServerProperties.WADL_FEATURE_DISABLE, true);

        //Manually adding MOXyJSONFeature
        register(MoxyJsonConfigContextResolver.class);
        register(UriConnegFilter.class);
        register(JaxbContextResolver.class);
        register(KapuaSerializableBodyWriter.class);
        register(ListBodyWriter.class);
        register(MoxyJsonFeatureCustomJsonProvider.class);

        register(new ContainerLifecycleListener() {

            @Override
            public void onStartup(Container container) {
                ServiceLocator serviceLocator = container.getApplicationHandler().getInjectionManager().getInstance(ServiceLocator.class);
                final KapuaLocator kapuaLocator = KapuaLocator.getInstance();
                if (kapuaLocator instanceof GuiceLocatorImpl) {
                    GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
                    GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
                    guiceBridge.bridgeGuiceInjector(((GuiceLocatorImpl) kapuaLocator).getInjector());
                }
            }

            @Override
            /**
             * Nothing to do
             */
            public void onReload(Container container) {
            }

            @Override
            /**
             * Nothing to do
             */
            public void onShutdown(Container container) {
            }
        });
    }

}
