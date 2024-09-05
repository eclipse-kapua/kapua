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
package org.eclipse.kapua.client.security;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.jms.JMSException;

import org.eclipse.kapua.client.security.bean.EntityRequest;
import org.eclipse.kapua.client.security.bean.MessageConstants;
import org.eclipse.kapua.client.security.amqpclient.Message;
import org.eclipse.kapua.client.security.bean.AuthRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class MessageHelper {

    private static ObjectMapper mapper = new ObjectMapper();
    private static ObjectWriter writer = mapper.writer();//check if it's thread safe

    private MessageHelper() {
    }

    static Message getBrokerConnectMessage(AuthRequest authRequest) throws Exception {
        return new Message(
            authRequest!=null ? writer.writeValueAsString(authRequest) : "",
            buildBaseMessage(authRequest));
    }

    static Message getBrokerDisconnectMessage(AuthRequest authRequest) throws Exception {
        return new Message(
            authRequest!=null ? writer.writeValueAsString(authRequest) : "",
            buildBaseMessage(authRequest));
    }

    static Message getEntityMessage(EntityRequest entityRequest) throws Exception {
        return new Message(
            entityRequest!=null ? writer.writeValueAsString(entityRequest) : "",
            buildBaseMessage(entityRequest));
    }

    static Map<String, Object> buildBaseMessage(AuthRequest authRequest) throws JMSException {
        Map<String, Object> properties = new HashMap<>();
        properties.put(MessageConstants.HEADER_REQUEST_ID, authRequest.getRequestId());
        properties.put(MessageConstants.HEADER_ACTION, authRequest.getAction());
        properties.put(MessageConstants.HEADER_USERNAME, authRequest.getUsername());
        properties.put(MessageConstants.HEADER_CLIENT_ID, authRequest.getClientId());
        properties.put(MessageConstants.HEADER_CLIENT_IP, authRequest.getClientIp());
        properties.put(MessageConstants.HEADER_CONNECTION_ID, authRequest.getConnectionId());
        return properties;
    }

    static Map<String, Object> buildBaseMessage(EntityRequest entityRequest) throws JMSException {
        Map<String, Object> properties = new HashMap<>();
        properties.put(MessageConstants.HEADER_REQUEST_ID, entityRequest.getRequestId());
        properties.put(MessageConstants.HEADER_ACTION, entityRequest.getAction());
        properties.put(MessageConstants.HEADER_NAME, entityRequest.getName());
        return properties;
    }

    static String getNewRequestId() {
        return UUID.randomUUID().toString();
    }
}
