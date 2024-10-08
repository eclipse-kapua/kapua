/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.client.security.bean.AuthRequest;
import org.eclipse.kapua.client.security.bean.AuthResponse;
import org.eclipse.kapua.client.security.bean.EntityRequest;
import org.eclipse.kapua.client.security.bean.EntityResponse;
import org.eclipse.kapua.client.security.bean.Request;
import org.eclipse.kapua.client.security.bean.ResponseContainer;
import org.eclipse.kapua.client.security.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security service. Implementation through AMQP messaging layer.
 */
public class ServiceClientMessagingImpl implements ServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(ServiceClientMessagingImpl.class);

    private static final int TIMEOUT = 5000;
    private final KapuaMessageListener messageListener;
    private MessageHelper messageHelper;

    private Client client;

    public ServiceClientMessagingImpl(KapuaMessageListener messageListener, Client client, MessageHelper messageHelper) {
        this.messageListener = messageListener;
        this.client = client;
        this.messageHelper = messageHelper;
    }

    @Override
    public AuthResponse brokerConnect(AuthRequest authRequest) throws Exception {
        String requestId = messageHelper.getNewRequestId();
        authRequest.setRequestId(requestId);
        authRequest.setAction(SecurityAction.brokerConnect.name());
        ResponseContainer<AuthResponse> responseContainer = ResponseContainer.createAnRegisterNewMessageContainer(messageListener, authRequest);
        logRequest(authRequest);
        client.sendMessage(messageHelper.getBrokerConnectMessage(authRequest));
        synchronized (responseContainer) {
            responseContainer.wait(TIMEOUT);
        }
        messageListener.removeCallback(requestId);
        return responseContainer.getResponse();
    }

    @Override
    public AuthResponse brokerDisconnect(AuthRequest authRequest) throws Exception {
        String requestId = messageHelper.getNewRequestId();
        authRequest.setRequestId(requestId);
        authRequest.setAction(SecurityAction.brokerDisconnect.name());
        ResponseContainer<AuthResponse> responseContainer = ResponseContainer.createAnRegisterNewMessageContainer(messageListener, authRequest);
        logRequest(authRequest);
        client.sendMessage(messageHelper.getBrokerDisconnectMessage(authRequest));
        synchronized (responseContainer) {
            responseContainer.wait(TIMEOUT);
        }
        messageListener.removeCallback(requestId);
        return responseContainer.getResponse();
    }

    @Override
    public EntityResponse getEntity(EntityRequest entityRequest) throws Exception {
        String requestId = messageHelper.getNewRequestId();
        entityRequest.setRequestId(requestId);
        ResponseContainer<EntityResponse> responseContainer = ResponseContainer.createAnRegisterNewMessageContainer(messageListener, entityRequest);
        logRequest(entityRequest);
        client.sendMessage(messageHelper.getEntityMessage(entityRequest));
        synchronized (responseContainer) {
            responseContainer.wait(TIMEOUT);
        }
        messageListener.removeCallback(requestId);
        return responseContainer.getResponse();
    }

    private void logRequest(Request request) {
        logger.info("Request id: {} - action: {} - requester: {}",
                request.getRequestId(), request.getAction(), request.getRequester());
    }
}
