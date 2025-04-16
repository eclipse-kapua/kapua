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

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.client.security.ServiceClient.SecurityAction;
import org.eclipse.kapua.client.security.bean.AuthResponse;
import org.eclipse.kapua.client.security.bean.EntityResponse;
import org.eclipse.kapua.client.security.bean.MessageConstants;
import org.eclipse.kapua.client.security.bean.Response;
import org.eclipse.kapua.client.security.bean.ResponseContainer;
import org.eclipse.kapua.client.security.client.Message;
import org.eclipse.kapua.client.security.client.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

/**
 * This class is responsible to correlate request/response messages. Only one instance of this must be present at any given time!
 */
@Singleton
public class KapuaMessageListener implements MessageListener, Closeable {

    protected static Logger logger = LoggerFactory.getLogger(KapuaMessageListener.class);
    //Should only be one
    private static final AtomicInteger INSTANCES = new AtomicInteger();
    private final int currentInstanceNumber;
    //Hate to use a static here, but at least in case of multiple listeners they will be able to correlate messages
    private static final Map<String, ResponseContainer<?>> CALLBACKS = new ConcurrentHashMap<>();
    //is not needed the synchronization
    private static ObjectMapper mapper = new ObjectMapper();
    private static ObjectReader reader = mapper.reader();//check if it's thread safe

    private MetricsClientSecurity metrics;

    public KapuaMessageListener(MetricsClientSecurity metricsClientSecurity) {
        currentInstanceNumber = INSTANCES.incrementAndGet();
        if (currentInstanceNumber != 1) {
            logger.warn("Starting KapuaMessageListener, instance number {}! Is this right?!?!?", currentInstanceNumber);
        } else {
            logger.debug("Starting KapuaMessageListener, instance {}", currentInstanceNumber);
        }
        this.metrics = metricsClientSecurity;
    }

    @Override
    public void onMessage(Message message) {
        logger.debug("KapuaMessageListener processing message, instance {} responding", currentInstanceNumber);
        try {
            SecurityAction securityAction = SecurityAction.valueOf((String)message.getProperties().get(MessageConstants.HEADER_ACTION));
            switch (securityAction) {
            case brokerConnect:
                updateResponseContainer(buildAuthResponseFromMessage(message));
                break;
            case brokerDisconnect:
                updateResponseContainer(buildAuthResponseFromMessage(message));
                break;
            case getEntity:
                updateResponseContainer(buildAccountResponseFromMessage(message));
                break;
            default:
                throw new KapuaRuntimeException(KapuaErrorCodes.ILLEGAL_ARGUMENT, "action");
            }
        } catch (IOException e) {
            metrics.getLoginCallbackError().inc();
            logger.error("Error while processing Authentication/Authorization message: {}", e.getMessage(), e);
        }
    }

    private <R extends Response> void updateResponseContainer(R response) throws IOException {
        logger.debug("update callback {} on instance {}, map size: {}", response.getRequestId(), this, CALLBACKS.size());
        ResponseContainer<R> responseContainer = (ResponseContainer<R>) CALLBACKS.get(response.getRequestId());
        if (responseContainer == null) {
            //internal error
            logger.error("Cannot find request container for requestId {}", response.getRequestId());
            metrics.getLoginCallbackTimeout().inc();
        } else {
            synchronized (responseContainer) {
                responseContainer.setResponse(response);
                responseContainer.notify();
            }
        }
    }

    private AuthResponse buildAuthResponseFromMessage(Message message) throws IOException {
        return reader.readValue(message.getBody(), AuthResponse.class);
    }

    private EntityResponse buildAccountResponseFromMessage(Message message) throws IOException {
        return reader.readValue(message.getBody(), EntityResponse.class);
    }

    public void registerCallback(String requestId, ResponseContainer<?> responseContainer) {
        CALLBACKS.put(requestId, responseContainer);
        logger.debug("registered callback {} on instance {}, map size: {}", requestId, this, CALLBACKS.size());
    }

    public void removeCallback(String requestId) {
        CALLBACKS.remove(requestId);
        logger.debug("removed callback {} from instance {}, map size: {}", requestId, this, CALLBACKS.size());
    }

    @Override
    public void close() throws IOException {
        INSTANCES.decrementAndGet();
    }
}
