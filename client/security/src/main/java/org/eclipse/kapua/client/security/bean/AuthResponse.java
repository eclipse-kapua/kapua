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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponse implements Response {

    protected static Logger logger = LoggerFactory.getLogger(AuthResponse.class);

    @JsonProperty("requester")
    private String requester;

    @JsonProperty("clusterName")
    private String clusterName;

    @JsonProperty("action")
    private String action;

    @JsonProperty("requestId")
    private String requestId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("clientId")
    private String clientId;

    @JsonProperty("clientIp")
    private String clientIp;

    @JsonProperty("connectionId")
    private String connectionId;

    @JsonProperty("accountName")
    private String accountName;

    @JsonProperty("scopeId")
    private String scopeId;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("kapuaConnectionId")
    private String kapuaConnectionId;

    @JsonProperty("stealingLink")
    private boolean stealingLink;

    @JsonProperty("accessTokenId")
    private String accessTokenId;

    @JsonProperty("acls")
    private List<AuthAcl> acls;

    @JsonProperty("resultCode")
    private String resultCode;

    @JsonProperty("exceptionClass")
    private String exceptionClass;

    @JsonProperty("errorCode")
    private String errorCode;

    @JsonProperty("properties")
    private Map<String, Object> properties;

    public AuthResponse() {
        acls = new ArrayList<>();
    }

    public void update(AuthContext authContext) {
        stealingLink = authContext.isStealingLink();
        kapuaConnectionId = authContext.getKapuaConnectionId();
        properties = new HashMap<>();
        if (authContext.getProperties() != null) {
            properties.putAll(authContext.getProperties());
        }
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public String getAccessTokenId() {
        return accessTokenId;
    }

    public void setAccessTokenId(String accessTokenId) {
        this.accessTokenId = accessTokenId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKapuaConnectionId() {
        return kapuaConnectionId;
    }

    public void setKapuaConnectionId(String kapuaConnectionId) {
        this.kapuaConnectionId = kapuaConnectionId;
    }

    public boolean isStealingLink() {
        return stealingLink;
    }

    public void setStealingLink(boolean stealingLink) {
        this.stealingLink = stealingLink;
    }

    public List<AuthAcl> getAcls() {
        return acls;
    }

    public void setAcls(List<AuthAcl> acls) {
        this.acls = acls;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}