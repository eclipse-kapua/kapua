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
package org.eclipse.kapua.message.internal;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.xml.JAXBContextProvider;
import org.eclipse.kapua.message.KapuaChannel;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.message.xml.XmlAdaptedMetric;
import org.eclipse.kapua.message.xml.XmlAdaptedMetrics;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageJAXBContextProvider implements JAXBContextProvider {

    private static final Logger logger = LoggerFactory.getLogger(MessageJAXBContextProvider.class);

    private JAXBContext context;

    @Override
    public JAXBContext getJAXBContext() throws KapuaException {
        if (context == null) {
            Class<?>[] classes = new Class<?>[] {
                    KapuaMessage.class,
                    KapuaChannel.class,
                    KapuaPayload.class,
                    KapuaPosition.class,
                    KapuaIdAdapter.class,

                    XmlAdaptedMetric.class,
                    XmlAdaptedMetrics.class,
            };
            try {
                Map<String, Object> properties = new HashMap<>(1);
                properties.put(MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);

                context = JAXBContextFactory.createContext(classes, properties);
            } catch (JAXBException jaxbException) {
                logger.warn("Error creating JAXBContext, tests will fail!", jaxbException);
            }
        }
        return context;
    }
}
