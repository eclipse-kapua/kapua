/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.storeengine.client.configuration;

/**
 * Options to customize the ssl behavior of the ES rest client.
 * Available configurations are:
 * <ol>
 * <li> enable ssl without any validation:
 * ssl.enabled=true
 * ssl.trust_server_certificate=false
 * others: unused
 * </li>
 * <li>
 * enable ssl forcing server certificate validation
 * ssl.enabled=true
 * ssl.trust_server_certificate=true
 * keystore_path/ssl.keystore_password empty
 * if ssl.truststore_path/ssl.truststore_password are empty the default jvm truststore will be used (otherwise the provided truststore).
 * </li>
 * <li>enable ssl allowing client certificate validation and forcing server certificate validation
 * ssl.enabled=true
 * ssl.trust_server_certificate=true
 * keystore_path/ssl.keystore_password valued with the proper key store
 * if ssl.truststore_path/ssl.truststore_password are empty the default jvm truststore will be used. Otherwise the provided truststore.
 * </li>
 * </ol>
 *
 * @since 1.3.0
 */
public class ElasticsearchClientSslConfiguration {

    private boolean enabled;
    private String keyStorePath;
    private String keyStorePassword;
    private String keyStoreType;
    private String trustStorePath;
    private String trustStorePassword;

    /**
     * Gets whether or not the SSL encryption is enabled.
     * <p>
     * Default value: false
     *
     * @return {@code true} if SSL encryption is enabled, {@code false} otherwise.
     * @since 1.3.0
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sest whether or not the SSL encryption is enabled.
     *
     * @param enabled {@code true} if SSL encryption is enabled, {@code false} otherwise.
     * @return This {@link ElasticsearchClientSslConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public ElasticsearchClientSslConfiguration setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Gets the {@link java.security.KeyStore} path.
     *
     * @return he {@link java.security.KeyStore} path.
     * @since 1.3.0
     */
    public String getKeyStorePath() {
        return keyStorePath;
    }

    /**
     * Sets the {@link java.security.KeyStore} path.
     *
     * @param keyStorePath The {@link java.security.KeyStore} path.
     * @return This {@link ElasticsearchClientSslConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public ElasticsearchClientSslConfiguration setKeyStorePath(String keyStorePath) {
        this.keyStorePath = keyStorePath;
        return this;
    }

    /**
     * Gets the {@link java.security.KeyStore} password.
     *
     * @return The {@link java.security.KeyStore} password.
     * @since 1.3.0
     */
    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    /**
     * Sets the {@link java.security.KeyStore} password.
     *
     * @param keyStorePassword The {@link java.security.KeyStore} password.
     * @return This {@link ElasticsearchClientSslConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public ElasticsearchClientSslConfiguration setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
        return this;
    }

    /**
     * Gets the {@link java.security.KeyStore} type.
     *
     * @return The {@link java.security.KeyStore} type.
     * @since 1.3.0
     */
    public String getKeyStoreType() {
        return keyStoreType;
    }

    /**
     * Sets the {@link java.security.KeyStore} type.
     *
     * @param keyStoreType The {@link java.security.KeyStore} type.
     * @return This {@link ElasticsearchClientSslConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public ElasticsearchClientSslConfiguration setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
        return this;
    }

    /**
     * Gets the trust {@link java.security.KeyStore} path.
     *
     * @return The trust {@link java.security.KeyStore} path.
     * @since 1.3.0
     */
    public String getTrustStorePath() {
        return trustStorePath;
    }

    /**
     * Sets the trust {@link java.security.KeyStore} path.
     *
     * @param trustStorePath The trust {@link java.security.KeyStore} path.
     * @return This {@link ElasticsearchClientSslConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public ElasticsearchClientSslConfiguration setTrustStorePath(String trustStorePath) {
        this.trustStorePath = trustStorePath;
        return this;
    }

    /**
     * Gets the trust {@link java.security.KeyStore} password.
     *
     * @return The trust {@link java.security.KeyStore} password.
     * @since 1.3.0
     */
    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    /**
     * Sets the trust {@link java.security.KeyStore} password.
     *
     * @param trustStorePassword The trust {@link java.security.KeyStore} password.
     * @return This {@link ElasticsearchClientSslConfiguration} to chain method invocation.
     * @since 1.3.0
     */
    public ElasticsearchClientSslConfiguration setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
        return this;
    }
}
