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
package org.eclipse.kapua.client.security.bean;

import org.eclipse.kapua.client.security.context.SessionContext;
import org.eclipse.kapua.model.id.KapuaId;

import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Map;

public class AuthContext {

    private String clusterName;
    private String requester;
    private String username;
    private String userId;
    private String accountName;
    private String scopeId;
    private String clientId;
    private String clientIp;
    private String connectionId;
    private String brokerId;
    private String brokerHost;
    private String transportProtocol;
    private Certificate[] clientCertificates;
    private boolean sslEnabled;
    private String authenticationType;
    private String exceptionClass;
    private String errorCode;

    //stealing link flag
    private boolean stealingLink;
    //device connection illegal state flag
    private boolean illegalState;
    private String kapuaConnectionId;

    private Map<String, Object> properties;

    public AuthContext(AuthRequest authRequest) {
        initCommonFields(authRequest);
        accountName = authRequest.getAccountName();
        scopeId = authRequest.getScopeId();
        userId = authRequest.getUserId();
    }

    public AuthContext(AuthRequest authRequest, AuthResponse authResponse) {
        initCommonFields(authRequest);
        accountName = authResponse.getAccountName();
        scopeId = authResponse.getScopeId();
        userId = authResponse.getUserId();
        if (authResponse.getProperties() != null) {
            properties.putAll(authResponse.getProperties());
        }
    }

    private void initCommonFields(AuthRequest authRequest) {
        properties = new HashMap<>();
        clusterName = authRequest.getClusterName();
        requester = authRequest.getRequester();
        username = authRequest.getUsername();
        clientId = authRequest.getClientId();
        clientIp = authRequest.getClientIp();
        connectionId = authRequest.getConnectionId();
        brokerId = authRequest.getBrokerId();
        brokerHost = authRequest.getBrokerHost();
        transportProtocol = authRequest.getTransportProtocol();
        clientCertificates = authRequest.getCertificates();
        sslEnabled = authRequest.getSslEnabled() != null && authRequest.getSslEnabled();
        exceptionClass = authRequest.getExceptionClass();
        errorCode = authRequest.getErrorCode();
        stealingLink = authRequest.isStealingLink();
        illegalState = authRequest.isIllegalState();
        setProperty(SessionContext.PARAM_KEY_STATUS_MISSING, authRequest.isMissing());
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getRequester() {
        return requester;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getScopeId() {
        return scopeId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientIp() {
        return clientIp;
    }

    public Certificate[] getClientCertificates() {
        return clientCertificates;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public String getBrokerId() {
        return brokerId;
    }

    public String getBrokerHost() {
        return brokerHost;
    }

    public boolean getSslEnabled() {
        return sslEnabled;
    }

    /**
     * Gets the device connection's authentication type used when connecting.
     *
     * @return The device connection's authentication type used when connecting.
     * @since 2.0.0
     */
    public String getAuthenticationType() {
        return authenticationType;
    }

    /**
     * Sets the device connection's authentication type used to connection.
     *
     * @param authenticationType
     *         The device connection's authentication type used to connection.
     * @since 2.0.0
     */
    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getTransportProtocol() {
        return transportProtocol;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setAuthErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getKapuaConnectionId() {
        return kapuaConnectionId;
    }

    public void setKapuaConnectionId(KapuaId kapuaConnectionId) {
        this.kapuaConnectionId = kapuaConnectionId.toCompactId();
    }

    public boolean isStealingLink() {
        return stealingLink;
    }

    public void setStealingLink(boolean stealingLink) {
        this.stealingLink = stealingLink;
    }

    public boolean isIllegalState() {
        return illegalState;
    }

    public void setIllegalState(boolean illegalState) {
        this.illegalState = illegalState;
    }

    public <T> void setProperty(String key, T value) {
        properties.put(key, value);
    }

    public <T> T getProperty(String key, T defaultValue) {
        try {
            T value = (T) properties.get(key);
            if (value == null) {
                return defaultValue;
            } else {
                return value;
            }
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
