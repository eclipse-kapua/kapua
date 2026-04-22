/*******************************************************************************
 * Copyright (c) 2017, 2022 Red Hat Inc and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Red Hat Inc - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.broker.artemis.plugin.security.setting;

import org.eclipse.kapua.commons.setting.SettingKey;

/**
 * Broker settings
 */
public enum BrokerSettingKey implements SettingKey {
    /**
     * Broker Host
     */
    BROKER_HOST("broker.host"),
    /**
     * Print the security context report (broker core metrics and other infos related to the connected clients)
     */
    PRINT_SECURITY_CONTEXT_REPORT("broker.print_security_context_report"),
    /**
     * AMQP acceptor port for "internal use acceptor" (used by the external consumers, console and rest-api)
     * default value should be 5672 (AMQP)
     */
    INTERNAL_AMQP_ACCEPTOR_PORT("broker.amqp_internal_acceptor.port"),
    /**
     * MQTT acceptor port for "internal use acceptor" (used by the external consumers, console and rest-api)
     * default value should be 1893 (MQTT)
     */
    INTERNAL_MQTT_ACCEPTOR_PORT("broker.mqtt_internal_acceptor.port"),
    /**
     * Broker acceptors prefix (used to read the acceptor map
     */
    ACCEPTORS("broker.acceptor"),
    /**
     * Receive buffer size used by broker connectors (all, not distinguish between MQTT/AMQP internal/external)
     */
    //leave parameter_ as prefix for acceptor otherwise this parameter become one of the acceptors (acceptors are read by prefix from the config file)
    RECEIVE_BUFFER_SIZE("broker.parameter_acceptor.buffer_size.receive"),
    /**
     * Send buffer size used by broker connectors (all, not distinguish between MQTT/AMQP internal/external)
     */
    //leave parameter_ as prefix for acceptor otherwise this parameter become one of the acceptors (acceptors are read by prefix from the config file)
    SEND_BUFFER_SIZE("broker.parameter_acceptor.buffer_size.send"),
    /**
     * Publish message info if the message size is over the specified threshold
     */
    PUBLISHED_MESSAGE_SIZE_LOG_THRESHOLD("broker.security.published.message_size.log_threshold"),
    /**
     * Connection token cache size
     */
    CACHE_CONNECTION_TOKEN_SIZE("broker.cache.connection_token.size"),
    /**
     * Connection token cache ttl (in seconds)
     */
    CACHE_CONNECTION_TOKEN_TTL("broker.cache.connection_token.ttl"),
    /**
     * Session context cache size
     */
    CACHE_SESSION_CONTEXT_SIZE("broker.cache.session_context.size"),
    /**
     * Session context cache ttl (in seconds)
     */
    CACHE_SESSION_CONTEXT_TTL("broker.cache.session_context.ttl"),
    /**
     * Scope id cache size
     */
    CACHE_SCOPE_ID_SIZE("broker.cache.scope_id.size"),
    /**
     * Scope id cache ttl (in seconds)
     */
    CACHE_SCOPE_ID_TTL("broker.cache.scope_id.ttl");

    private String key;

    private BrokerSettingKey(String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }
}
