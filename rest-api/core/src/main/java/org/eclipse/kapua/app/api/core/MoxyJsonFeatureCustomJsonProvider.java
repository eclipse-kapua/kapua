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
package org.eclipse.kapua.app.api.core;

import org.glassfish.jersey.internal.InternalProperties;
import org.glassfish.jersey.internal.util.PropertiesHelper;

import javax.inject.Inject;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * A custom feature used to set a custom moxyJsonProvider
 */
public class MoxyJsonFeatureCustomJsonProvider implements Feature {
    private static final String JSON_FEATURE = MoxyJsonFeatureCustomJsonProvider.class.getSimpleName();

    @Override
    public boolean configure(final FeatureContext context) {
        Configuration config = context.getConfiguration();

        // Disable other JSON providers.
        // In this way the org.glassfish.jersey.moxy.json.MoxyJsonFeature (registered as default by MOXy) will skip the registration of the default provider
        context.property(
                PropertiesHelper.getPropertyNameForRuntime(InternalProperties.JSON_FEATURE, config.getRuntimeType()),
                JSON_FEATURE
        );
        context.register(CustomMoxyJsonProvider.class);

        return true;
    }

    @Provider
    public static class CustomMoxyJsonProvider extends org.glassfish.jersey.moxy.json.internal.ConfigurableMoxyJsonProvider {

        @Inject
        public CustomMoxyJsonProvider(@Context Providers providers, @Context Configuration config) {
            super(providers, config);
        }

        /**
         * A custom moxyJsonProvider that sets the unmarshaller validationEventHandler to the default one. This one allows to propagate exceptions to the stack when an error is found (for example, when an exception has been thrown from one of our custom "xmlAdapters")
         *
         * @param type - The Class to be unmarshalled (i.e. <i>Customer</i> or <i>List</i>)
         * @param genericType - The type of object to be unmarshalled (i.e <i>Customer</i> or <i>List&lt;Customer&gt;</i>).
         * @param annotations - The annotations corresponding to domain object.
         * @param mediaType - The media type for the HTTP entity.
         * @param httpHeaders - HTTP headers associated with HTTP entity.
         * @param unmarshaller - The instance of <i>Unmarshaller</i> that will be used to unmarshal the JSON message.
         * @throws JAXBException
         */
        @Override
        protected void preReadFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, Unmarshaller unmarshaller) throws JAXBException {
            super.preReadFrom(type, genericType, annotations, mediaType, httpHeaders, unmarshaller);

            unmarshaller.setEventHandler(new DefaultValidationEventHandler());
        }
    }
}
