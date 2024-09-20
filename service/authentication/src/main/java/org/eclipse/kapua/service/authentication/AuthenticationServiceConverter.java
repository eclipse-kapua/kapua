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

import java.io.IOException;

import javax.inject.Inject;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.camel.component.jms.JmsMessage;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.client.security.bean.AuthRequest;
import org.eclipse.kapua.client.security.bean.EntityRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

/**
 * Kapua message converter used to convert authentication messages.
 */
public class AuthenticationServiceConverter {

    public static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceConverter.class);

    private static ObjectMapper mapper = new ObjectMapper();
    private static ObjectReader reader = mapper.reader();//check if it's thread safe

    private final MetricsAuthentication metrics;

    @Inject
    public AuthenticationServiceConverter(MetricsAuthentication metricsAuthentication) {
        this.metrics = metricsAuthentication;
    }

    /**
     * Convert incoming message to a authentication request message
     *
     * @param exchange
     * @param value
     * @return Authorization request bean
     * @throws KapuaException
     *         if incoming message does not contain a javax.jms.BytesMessage or an error during conversion occurred
     */
    @Converter
    public AuthRequest convertToAuthRequest(Exchange exchange, Object value) throws KapuaException {
        try {
            String body = ((JmsMessage) exchange.getIn()).getBody(String.class);
            AuthRequest authRequest = reader.readValue(body, AuthRequest.class);
            metrics.getConverter().inc();
            return authRequest;
        } catch (IOException e) {
            metrics.getConverterError().inc();
            throw KapuaException.internalError(e, "Error while converting authentication message");
        }
    }

    @Converter
    public EntityRequest convertToGetEntity(Exchange exchange, Object value) throws KapuaException {
        try {
            String body = ((JmsMessage) exchange.getIn()).getBody(String.class);
            EntityRequest entityRequest = reader.readValue(body, EntityRequest.class);
            metrics.getConverter().inc();
            return entityRequest;
        } catch (IOException e) {
            metrics.getConverterError().inc();
            throw KapuaException.internalError(e, "Error while converting getEntity message");
        }
    }
}
