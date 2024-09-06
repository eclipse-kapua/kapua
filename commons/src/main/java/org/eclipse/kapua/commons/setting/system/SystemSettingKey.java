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
package org.eclipse.kapua.commons.setting.system;

import org.eclipse.kapua.commons.setting.SettingKey;

import javax.persistence.EntityExistsException;

/**
 * Setting system key implementation.
 *
 * @since 1.0
 */
public enum SystemSettingKey implements SettingKey {
    /**
     * Cluster name
     */
    CLUSTER_NAME("cluster.name"),
    /**
     * Provisioning account name
     */
    SYS_PROVISION_ACCOUNT_NAME("commons.sys.provision.account.name"),
    /**
     * System administration account name
     */
    SYS_ADMIN_ACCOUNT("commons.sys.admin.account"),
    /**
     * System administration username
     */
    SYS_ADMIN_USERNAME("commons.sys.admin.userName"),

    /**
     * Software version
     */
    VERSION("commons.version"),
    /**
     * Build version
     */
    BUILD_REVISION("commons.build.revision"),
    /**
     * Build timestamp
     */
    BUILD_TIMESTAMP("commons.build.timestamp"),
    /**
     * Build branch
     */
    BUILD_BRANCH("commons.build.branch"),
    /**
     * Build number
     */
    BUILD_NUMBER("commons.build.number"),

    /**
     * Character encoding
     */
    CHAR_ENCODING("character.encoding"),

    /**
     * Jdbc url connection resolver implementation
     */
    DB_JDBC_CONNECTION_URL_RESOLVER("commons.db.jdbcConnectionUrlResolver"),

    /**
     * Database name
     */
    DB_NAME("commons.db.name"),
    /**
     * Database username
     */
    DB_USERNAME("commons.db.username"),
    /**
     * Database password
     */
    DB_PASSWORD("commons.db.password"),

    /**
     * Database jdbc driver class
     */
    DB_JDBC_DRIVER("commons.db.jdbc.driver"),
    /**
     * Database jdbc driver class
     */
    DB_JDBC_DATABASE_TARGET("commons.db.jdbc.database.target"),
    /**
     * Database connection schema
     */
    DB_CONNECTION_SCHEME("commons.db.connection.scheme"),
    /**
     * Database host
     */
    DB_CONNECTION_HOST("commons.db.connection.host"),
    /**
     * Database port
     */
    DB_CONNECTION_PORT("commons.db.connection.port"),
    /**
     * Database use ssl connection
     */
    DB_CONNECTION_USE_SSL("commons.db.connection.useSsl"),
    /**
     * Database TLS/SSL Protocol Version Selection (for MariaDB only)
     */
    DB_CONNECTION_ENABLED_SSL_PROTOCOL_SUITES("commons.db.connection.enabledSslProtocolSuites"),
    /**
     * Database truststore url
     */
    DB_CONNECTION_TRUSTSTORE_URL("commons.db.connection.trust.store.url"),
    /**
     * Database truststore password
     */
    DB_CONNECTION_TRUSTSTORE_PWD("commons.db.connection.trust.store.pwd"),
    /**
     * Any additional option that can be passed to the JDBC connection string
     */
    DB_CONNECTION_ADDITIONAL_OPTIONS("commons.db.connection.additionalOptions"),
    /**
     * Database schema name
     */
    DB_SCHEMA("commons.db.schema"),

    /**
     * Database schema name
     */
    DB_SCHEMA_ENV("COMMONS_DB_SCHEMA"),

    /**
     * Update database schema on startup
     */
    DB_SCHEMA_UPDATE("commons.db.schema.update"),

    /**
     * Database timezone to use
     */
    DB_USE_TIMEZONE("commons.db.useTimezone"),
    /**
     * Database use legacy date time flag
     */
    DB_USE_LEGACY_DATETIME_CODE("commons.db.useLegacyDatetimeCode"),
    /**
     * Database server timezone
     */
    DB_SERVER_TIMEZONE("commons.db.serverTimezone"),
    /**
     * Database character encoding
     */
    DB_CHAR_ENCODING("commons.db.characterEncoding"),
    /**
     * Character used in DB query to escape other special characters
     */
    DB_CHARACTER_ESCAPE("commons.db.character.escape"),
    /**
     * Character used in DB query to use as wildcard
     */
    DB_CHARACTER_WILDCARD_ANY("commons.db.character.wildcard.any"),
    /**
     * Character used in DB query to use as single character wildcard
     */
    DB_CHARACTER_WILDCARD_SINGLE("commons.db.character.wildcard.single"),

    /**
     * Database pool size strategy.
     * <p>
     * Created to maintain backward compatibility with 2.0.x release.
     * 2.0.x is defaulting to {@code range} since the previous strategy was using a variable DB connection pool sizing.
     * {@code range} strategy is deprecated and users should migrate to use {@code fixed} strategy
     * <p>
     * See official documentation <a href="https://github.com/brettwooldridge/HikariCP">Hikari CP Docs</a> about performances.
     *
     * @since 2.1.0
     */
    DB_POOL_SIZE_STRATEGY("commons.db.pool.size.strategy"),

    /**
     * Database fixed pool size.
     * <p>
     * Taken into account when {@link #DB_POOL_SIZE_STRATEGY} is {@code fixed}
     *
     * @since 2.1.0
     */
    DB_POOL_SIZE_FIXED("commons.db.pool.size.fixed"),

    /**
     * Database pool minimum pool size.
     * <p>
     * Taken into account when {@link #DB_POOL_SIZE_STRATEGY} is {@code range}
     *
     * @since 1.0.0
     * @deprecated Since 2.1.0. Please make use of {@link #DB_POOL_SIZE_FIXED}
     */
    @Deprecated
    DB_POOL_SIZE_MIN("commons.db.pool.size.min"),

    /**
     * Database pool maximum pool size.
     * <p>
     * Taken into account when {@link #DB_POOL_SIZE_STRATEGY} is {@code range}
     *
     * @since 1.0.0
     * @deprecated Since 2.1.0. Please make use of {@link #DB_POOL_SIZE_FIXED}
     */
    @Deprecated
    DB_POOL_SIZE_MAX("commons.db.pool.size.max"),

    /**
     * Database pool maximum time before evicting an idle connection
     * <p>
     * Taken into account when {@link #DB_POOL_SIZE_STRATEGY} is {@code range}
     *
     * @since 1.0.0
     * @deprecated Since 2.1.0.
     */
    @Deprecated
    DB_POOL_IDLE_TIMEOUT("commons.db.pool.idle.timeout"),

    /**
     * Database pool keepalive query interval for idle connections
     */
    DB_POOL_KEEPALIVE_TIME("commons.db.pool.keepalive.timeout"),
    /**
     * Database pool maximum lifetime of a connection
     */
    DB_POOL_MAX_LIFETIME("commons.db.pool.max.lifetime"),
    /**
     * Test query used for connection liveness tests and keepalive
     */
    DB_POOL_TEST_QUERY("commons.db.pool.test.query"),
    /**
     * Amount of time that a connection can be out of the pool before a message is logged
     */
    DB_POOL_LEAKDETECTION_THRESHOLD("commons.db.pool.leakdetection.threshold"),

    /**
     * Broker schema (e.g. mqtt, mqtts, ..)
     */
    BROKER_SCHEME("broker.scheme"),
    /**
     * Broker host
     */
    BROKER_HOST("broker.host"),
    /**
     * Broker internal port
     */
    BROKER_INTERNAL_CONNECTOR_PORT("broker.connector.internal.port"),
    /**
     * Broker internal connector name
     */
    BROKER_INTERNAL_CONNECTOR_MQTT_NAME("broker.connector.internal.mqtt.name"),
    /**
     * Broker AMQP internal connector name
     */
    BROKER_INTERNAL_CONNECTOR_AMQP_NAME("broker.connector.internal.amqp.name"),
    /**
     * Internal connector username
     */
    BROKER_INTERNAL_CONNECTOR_USERNAME("broker.connector.internal.username"),
    /**
     * Internal connector password
     */
    BROKER_INTERNAL_CONNECTOR_PASSWORD("broker.connector.internal.password"),
    //TODO should be moved to service-api
    /**
     * Service bus url
     */
    SERVICE_BUS_URL("commons.servicebus.url"),
    /**
     * Service bus username
     */
    SERVICE_BUS_USERNAME("commons.servicebus.username"),
    /**
     * Service bus password
     */
    SERVICE_BUS_PASSWORD("commons.servicebus.password"),

    /**
     * Metrics JMX disabled
     */
    METRICS_ENABLE_JMX("metrics.enable.jmx"),

    /**
     * Set the Kapua key size (the size is expressed in bits)
     */
    KAPUA_KEY_SIZE("commons.entity.key.size"),

    /**
     * Maximum allowed retry (due to a {@link EntityExistsException}, so already exists key) on insert operation
     */
    KAPUA_INSERT_MAX_RETRY("commons.entity.insert.max.retry"),

    /**
     * Url of the event bus
     */
    EVENT_BUS_URL("commons.eventbus.url"),

    /**
     * Username to connect to the event bus
     */
    EVENT_BUS_USERNAME("commons.eventbus.username"),

    /**
     * Password to connect to the event bus
     */
    EVENT_BUS_PASSWORD("commons.eventbus.password"),

    /**
     * Producers pool min size (per microservice)
     */
    EVENT_BUS_PRODUCER_POOL_MIN_SIZE("commons.eventbus.producerPool.minSize"),

    /**
     * Producers pool max size (per microservice)
     */
    EVENT_BUS_PRODUCER_POOL_MAX_SIZE("commons.eventbus.producerPool.maxSize"),

    /**
     * Producers pool max wait on borrow
     */
    EVENT_BUS_PRODUCER_POOL_BORROW_WAIT_MAX("commons.eventbus.producerPool.maxWaitOnBorrow"),

    /**
     * Producers pool eviction interval
     */
    EVENT_BUS_PRODUCER_EVICTION_INTERVAL("commons.eventbus.producerPool.evictionInterval"),

    /**
     * Consumers pool size (per microservice)
     */
    EVENT_BUS_CONSUMER_POOL_SIZE("commons.eventbus.consumerPool.size"),

    /**
     * Event message serializer class
     */
    EVENT_BUS_MESSAGE_SERIALIZER("commons.eventbus.messageSerializer"),

    /**
     * If true the transport will use the native Epoll layer when available instead of the NIO layer.
     */
    EVENT_BUS_TRANSPORT_USE_EPOLL("commons.eventbus.transport.useEpoll"),

    /**
     * Wait time between housekeeper executions (in milliseconds)
     */
    HOUSEKEEPER_EXECUTION_WAIT_TIME("commons.eventbus.houskeeper.waitTime"),
    /**
     * Housekeeper event scan window
     */
    HOUSEKEEPER_EVENT_SCAN_WINDOW("commons.eventbus.houskeeper.eventScanWindow"),
    /**
     * Time window to consider FIRED messages as "old" messages so ready to be processed by the housekeeper (in milliseconds)
     */
    HOUSEKEEPER_OLD_MESSAGES_TIME_WINDOW("commons.eventbus.houskeeper.oldMessagesTimeWindow"),
    /**
     * Allow System Settings to be updatable at runtime via System.setProperty()
     */
    SETTINGS_HOTSWAP("commons.settings.hotswap"),

    /**
     * Provide the classname for the Cache Provider
     */
    CACHING_PROVIDER("commons.cache.provider.classname"),
    /**
     * Size of the local cache for the KapuaTmetadata
     */
    TMETADATA_LOCAL_CACHE_SIZE_MAXIMUM("commons.cache.local.tmetadata.maxsize"),
    /**
     * Provide the CacheManager config file URL
     */
    CACHE_CONFIG_URL("commons.cache.config.url"),
    /**
     * Provide the Cache TTL
     */
    CACHE_TTL("commons.cache.config.ttl"),
    /**
     * Provide the JCache Expiry Policy. Allowed values: MODIFIED, TOUCHED
     */
    JCACHE_EXPIRY_POLICY("commons.cache.config.expiryPolicy");

    private String key;

    /**
     * Constructor
     *
     * @param key
     */
    private SystemSettingKey(String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }
}
