/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.broker.artemis.plugin.security.metric;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;
import org.eclipse.kapua.commons.metric.MetricsLabel;
import org.eclipse.kapua.commons.metric.MetricsService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class LoginMetric {

    public static final String COMPONENT_LOGIN = "login";
    //action
    private static final String DISCONNECT = "disconnect";

    private static final String INTERNAL_CONNECTOR = "internal_connector";
    private static final String EXTERNAL_CONNECTOR = "external_connector";
    private static final String AUTHENTICATE_FROM_CACHE = "authenticate_from_cache";
    private static final String PASSWORD = "password";
    private static final String CLEANUP_GENERIC = "cleanup_generic";
    private static final String CLEANUP_NULL_SESSION = "cleanup_null_session";
    private static final String CLOSED_CONNECTION = "closed_connection";
    private static final String DUPLICATE_SESSION_METADATA = "duplicate_session_metadata";
    private static final String DISCONNECT_CALLBACK_CALL = "disconnect_callback_call";
    private static final String SESSION_CONTEXT_BY_CLIENT_ID_NOT_FOUND = "session_context_by_client_id_not_found";
    private static final String ACL_CACHE_HIT = "acl_cache_hit";
    private static final String ACL_CREATION = "acl_creation";
    private static final String DISCONNECT_BY_EVENT = DISCONNECT + "_by_event";
    private static final String ADD_CONNECTION = "add_connection";
    private static final String REMOVE_CONNECTION = "remove_connection";

    private final ActionMetric externalConnector;
    private final ActionMetric internalConnector;
    private final Counter authenticateFromCache;
    private final Counter cleanupGenericFailure;
    private final Counter cleanupNullSessionFailure;
    private final Counter loginClosedConnectionFailure;
    private final Counter duplicateSessionMetadataFailure;
    private final Counter disconnectCallbackCallFailure;//disconnect callback called before the connect callback (usually when a stealing link happens)
    private final Counter sessionContextByClientIdNotFound;//no session context is found by client id on disconnect on cleanupConnectionData (disconnect)
    private final Counter aclCacheHit;//acl found from cache (it happens when a client id disconnected but some address related to this client id deleted after)
    private final Counter aclCreationFailure;//error while creating acl

    private final Counter invalidUserPassword;
    private final Counter disconnectByEvent;

    private final Timer externalAddConnection;
    private final Timer removeConnection;

    @Inject
    public LoginMetric(MetricsService metricsService,
                       @Named("metricModuleName")
                       String metricModuleName) {
        // login by connectors
        externalConnector = new ActionMetric(metricsService, metricModuleName, COMPONENT_LOGIN, EXTERNAL_CONNECTOR);
        authenticateFromCache = metricsService.getCounter(metricModuleName, COMPONENT_LOGIN, AUTHENTICATE_FROM_CACHE);
        internalConnector = new ActionMetric(metricsService, metricModuleName, COMPONENT_LOGIN, INTERNAL_CONNECTOR);
        cleanupGenericFailure = metricsService.getCounter(metricModuleName, COMPONENT_LOGIN, CLEANUP_GENERIC, MetricsLabel.FAILURE);
        cleanupNullSessionFailure = metricsService.getCounter(metricModuleName, COMPONENT_LOGIN, CLEANUP_NULL_SESSION, MetricsLabel.FAILURE);
        loginClosedConnectionFailure = metricsService.getCounter(metricModuleName, COMPONENT_LOGIN, CLOSED_CONNECTION, MetricsLabel.FAILURE);
        duplicateSessionMetadataFailure = metricsService.getCounter(metricModuleName, COMPONENT_LOGIN, DUPLICATE_SESSION_METADATA, MetricsLabel.FAILURE);
        disconnectCallbackCallFailure = metricsService.getCounter(metricModuleName, COMPONENT_LOGIN, DISCONNECT_CALLBACK_CALL, MetricsLabel.FAILURE);
        sessionContextByClientIdNotFound = metricsService.getCounter(metricModuleName, COMPONENT_LOGIN, SESSION_CONTEXT_BY_CLIENT_ID_NOT_FOUND);
        aclCacheHit = metricsService.getCounter(metricModuleName, COMPONENT_LOGIN, ACL_CACHE_HIT);
        aclCreationFailure = metricsService.getCounter(metricModuleName, COMPONENT_LOGIN, ACL_CREATION, MetricsLabel.FAILURE);

        invalidUserPassword = metricsService.getCounter(metricModuleName, COMPONENT_LOGIN, PASSWORD, MetricsLabel.FAILURE);
        disconnectByEvent = metricsService.getCounter(metricModuleName, COMPONENT_LOGIN, DISCONNECT_BY_EVENT, DISCONNECT);

        // login time
        externalAddConnection = metricsService.getTimer(metricModuleName, COMPONENT_LOGIN, ADD_CONNECTION, MetricsLabel.TIME, MetricsLabel.SECONDS);
        removeConnection = metricsService.getTimer(metricModuleName, COMPONENT_LOGIN, REMOVE_CONNECTION, MetricsLabel.TIME, MetricsLabel.SECONDS);
    }

    public Counter getAuthenticateFromCache() {
        return authenticateFromCache;
    }

    public ActionMetric getExternalConnector() {
        return externalConnector;
    }

    public ActionMetric getInternalConnector() {
        return internalConnector;
    }

    public Counter getCleanupGenericFailure() {
        return cleanupGenericFailure;
    }

    public Counter getCleanupNullSessionFailure() {
        return cleanupNullSessionFailure;
    }

    public Counter getLoginClosedConnectionFailure() {
        return loginClosedConnectionFailure;
    }

    public Counter getDuplicateSessionMetadataFailure() {
        return duplicateSessionMetadataFailure;
    }

    public Counter getInvalidUserPassword() {
        return invalidUserPassword;
    }

    public Counter getDisconnectByEvent() {
        return disconnectByEvent;
    }

    /**
     * Disconnect callback called before the connect callback (usually when a stealing link happens)
     *
     * @return
     */
    public Counter getDisconnectCallbackCallFailure() {
        return disconnectCallbackCallFailure;
    }

    /**
     * No session context is found by client id on disconnect on cleanupConnectionData (disconnect)
     * It's not necessary an error or failure but the metric is classified as failure
     *
     * @return
     */
    public Counter getSessionContextByClientIdNotFound() {
        return sessionContextByClientIdNotFound;
    }

    /**
     * ACL found from cache (it happens when a client id disconnected but some address related to this client id deleted after)
     *
     * @return
     */
    public Counter getAclCacheHit() {
        return aclCacheHit;
    }

    /**
     * Failure while creating ACL count (a failure doesn't mean all the ACL for a user aren't created but just one of the available ACLs)
     *
     * @return
     */
    public Counter getAclCreationFailure() {
        return aclCreationFailure;
    }

    /**
     * External connector - Add connection total time
     *
     * @return
     */
    public Timer getExternalAddConnection() {
        return externalAddConnection;
    }

    /**
     * Remove connection total time
     *
     * @return
     */
    public Timer getRemoveConnection() {
        return removeConnection;
    }

}
