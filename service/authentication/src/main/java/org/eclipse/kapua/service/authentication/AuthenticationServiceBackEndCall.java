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

import java.util.Map;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.apache.shiro.util.ThreadContext;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalAccessException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.client.security.AuthErrorCodes;
import org.eclipse.kapua.client.security.KapuaIllegalDeviceStateException;
import org.eclipse.kapua.client.security.ServiceClient.EntityType;
import org.eclipse.kapua.client.security.ServiceClient.ResultCode;
import org.eclipse.kapua.client.security.bean.AuthContext;
import org.eclipse.kapua.client.security.bean.AuthRequest;
import org.eclipse.kapua.client.security.bean.AuthResponse;
import org.eclipse.kapua.client.security.bean.EntityRequest;
import org.eclipse.kapua.client.security.bean.EntityResponse;
import org.eclipse.kapua.client.security.metric.AuthMetric;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authentication.authentication.Authenticator;
import org.eclipse.kapua.service.authentication.exception.KapuaAuthenticationErrorCodes;
import org.eclipse.kapua.service.authentication.exception.KapuaAuthenticationException;
import org.eclipse.kapua.service.authentication.token.AccessToken;
import org.eclipse.kapua.service.device.authentication.api.DeviceConnectionCredentialAdapter;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionService;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Timer.Context;
import com.google.common.base.Strings;

public class AuthenticationServiceBackEndCall {

    protected static Logger logger = LoggerFactory.getLogger(AuthenticationServiceBackEndCall.class);

    private AuthMetric authenticationMetric;

    @Inject
    private Authenticator authenticator;

    private KapuaLocator locator;
    private AuthenticationService authenticationService;
    private AccountService accountService;
    private DeviceConnectionService deviceConnectionService;
    private UserService userService;

    @Inject
    private Map<String, DeviceConnectionCredentialAdapter> deviceConnectionAuthHandlers;

    public AuthenticationServiceBackEndCall() {
        locator = KapuaLocator.getInstance();
        authenticationService = locator.getService(AuthenticationService.class);
        accountService = locator.getService(AccountService.class);
        deviceConnectionService = locator.getService(DeviceConnectionService.class);
        userService = locator.getService(UserService.class);
        authenticationMetric = locator.getComponent(AuthMetric.class);
    }

    public AuthResponse brokerConnect(AuthRequest authRequest) {
        try {
            logger.info("Login for clientId {} - user: {} - password: {} - client certificates: {}", authRequest.getClientId(), authRequest.getUsername(), Strings.isNullOrEmpty(authRequest.getPassword()) ? "no" : "yes", authRequest.getCertificates() != null ? "yes" : "no");
            ThreadContext.unbindSubject();
            String deviceConnectionAuthType = extractAuthTypeFromAuthRequest(authRequest);
            LoginCredentials authenticationCredentials = buildLoginCredentialsFromAuthType(authRequest, deviceConnectionAuthType);
            AccessToken accessToken = authenticationService.login(authenticationCredentials);
            Account account = KapuaSecurityUtils.doPrivileged(() -> accountService.find(accessToken.getScopeId()));
            AuthResponse authResponse = buildLoginResponseAuthorized(authRequest, accessToken, account);
            AuthContext authContext = new AuthContext(authRequest, authResponse);
            authContext.setAuthenticationType(deviceConnectionAuthType);
            authResponse.setAcls(authenticator.connect(authContext));
            authResponse.update(authContext);
            return authResponse;
        } catch (Exception e) {
            //this is not a proper error since the login throws exception if the user is not allowed to connect
            //so the error is logged but no metric is incremented
            logger.error("Login error: {}", e.getMessage(), e);
            return buildLoginResponseNotAuthorized(authRequest, e);
        } finally {
            Context timeShiroLogout = authenticationMetric.getExtConnectorTime().getLogoutOnLogin().time();
            try {
                authenticationService.logout();
            } catch (Exception e) {
                //error while cleaning up the logged user
                authenticationMetric.getFailure().getLogoutFailureOnLogin().inc();
                logger.error("Logout error: {}", e.getMessage(), e);
            }
            timeShiroLogout.stop();
            ThreadContext.unbindSubject();
        }
    }

    public AuthResponse brokerDisconnect(AuthRequest authRequest) {
        try {
            logger.info("Logout for user: {} - clientId: {}", authRequest.getUsername(), authRequest.getClientId());
            authenticator.disconnect(new AuthContext(authRequest));
            return buildLogoutResponseAuthorized(authRequest);
        } catch (Exception e) {
            //this is an error since the disconnect shouldn't throws exception
            authenticationMetric.getFailure().getDisconnectFailure().inc();
            logger.error("Login error: {}", e.getMessage(), e);
            return buildLogoutResponseNotAuthorized(authRequest, e);
        }
    }

    public EntityResponse getEntity(EntityRequest entityRequest) {
        logger.info("Get entity {} for name: {}", entityRequest.getEntity(), entityRequest.getName());
        if (EntityType.account.name().equals(entityRequest.getEntity())) {
            try {
                Account account = KapuaSecurityUtils.doPrivileged(() -> accountService.findByName(entityRequest.getName()));
                return buildGetEntityResponse(entityRequest, account);
            } catch (Exception e) {
                return buildGetEntityResponseError(entityRequest, e);
            }
        } else if (EntityType.user.name().equals(entityRequest.getEntity())) {
            try {
                User user = KapuaSecurityUtils.doPrivileged(() -> userService.findByName(entityRequest.getName()));
                return buildGetEntityResponse(entityRequest, user);
            } catch (Exception e) {
                return buildGetEntityResponseError(entityRequest, e);
            }
        } else {
            return buildGetEntityResponseError(entityRequest, new KapuaIllegalArgumentException("action", entityRequest.getAction()));
        }
    }

    private AuthResponse buildLoginResponseAuthorized(AuthRequest authRequest, AccessToken accessToken, Account account) {
        AuthResponse authResponse = buildAuthResponse(authRequest, ResultCode.authorized);
        //if authorized should be not null, anyway do a check
        if (accessToken != null) {
            authResponse.setScopeId(accessToken.getScopeId().toCompactId());
            authResponse.setUserId(accessToken.getUserId().toCompactId());
            authResponse.setAccessTokenId(accessToken.getTokenId());
        }
        //if authorized should be not null, anyway do a check
        if (account != null) {
            authResponse.setAccountName(account.getName());
        }
        return authResponse;
    }

    private AuthResponse buildLoginResponseNotAuthorized(AuthRequest authRequest, Exception exception) {
        return buildAuthResponse(authRequest, ResultCode.notAuthorized, exception);
    }

    private AuthResponse buildLogoutResponseAuthorized(AuthRequest authRequest) {
        return buildAuthResponse(authRequest, ResultCode.authorized);
    }

    private AuthResponse buildLogoutResponseNotAuthorized(AuthRequest authRequest, Exception exception) {
        return buildAuthResponse(authRequest, ResultCode.notAuthorized, exception);
    }

    private AuthResponse buildAuthResponse(AuthRequest authRequest, ResultCode resultCode) {
        return buildAuthResponse(authRequest, resultCode, null);
    }

    private AuthResponse buildAuthResponse(AuthRequest authRequest, ResultCode resultCode, Exception exception) {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setClusterName(authRequest.getClusterName());
        authResponse.setRequester(authRequest.getRequester());
        authResponse.setAction(authRequest.getAction());
        authResponse.setRequestId(authRequest.getRequestId());
        authResponse.setResultCode(resultCode.name());
        authResponse.setUsername(authRequest.getUsername());
        authResponse.setClientId(authRequest.getClientId());
        authResponse.setClientIp(authRequest.getClientIp());
        authResponse.setConnectionId(authRequest.getConnectionId());
        if (exception != null) {
            updateError(authResponse, exception);
        }
        return authResponse;
    }

    private EntityResponse buildGetEntityResponse(EntityRequest entityRequest, KapuaEntity entity) {
        EntityResponse entityResponse = buildGetEntityResponseCommon(entityRequest, ResultCode.authorized);
        entityResponse.setClusterName(entityRequest.getClusterName());
        entityResponse.setId(entity.getId() != null ? entity.getId().toCompactId() : null);
        entityResponse.setScopeId(entity.getScopeId() != null ? entity.getScopeId().toCompactId() : null);
        return entityResponse;
    }

    private EntityResponse buildGetEntityResponseError(EntityRequest entityRequest, Exception exception) {
        EntityResponse entityResponse = buildGetEntityResponseCommon(entityRequest, ResultCode.notAuthorized);
        entityResponse.setClusterName(entityRequest.getClusterName());
        entityResponse.setErrorCode(exception.getMessage());
        return entityResponse;
    }

    private EntityResponse buildGetEntityResponseCommon(EntityRequest entityRequest, ResultCode resultCode) {
        EntityResponse entityResponse = new EntityResponse();
        entityResponse.setClusterName(entityRequest.getClusterName());
        entityResponse.setRequester(entityRequest.getRequester());
        entityResponse.setAction(entityRequest.getAction());
        entityResponse.setRequestId(entityRequest.getRequestId());
        entityResponse.setName(entityRequest.getName());
        entityResponse.setEntity(entityRequest.getEntity());
        entityResponse.setResultCode(resultCode.name());
        return entityResponse;
    }

    private void updateError(AuthResponse authResponse, @NotNull Exception exception) {
        authResponse.setExceptionClass(exception.getClass().getName());
        String errorCode = KapuaAuthenticationErrorCodes.AUTHENTICATION_ERROR.name();
        if (exception instanceof KapuaAuthenticationException) {
            KapuaAuthenticationErrorCodes authErrorCode = (KapuaAuthenticationErrorCodes) ((KapuaAuthenticationException) exception).getCode();
            if (authErrorCode != null) {
                errorCode = authErrorCode.name();
            }
        } else if (exception instanceof KapuaIllegalAccessException) {
            errorCode = KapuaErrorCodes.ILLEGAL_ACCESS.name();
        } else if (exception instanceof KapuaIllegalDeviceStateException) {
            AuthErrorCodes authErrorCode = (AuthErrorCodes) ((KapuaIllegalDeviceStateException) exception).getCode();
            if (authErrorCode != null) {
                errorCode = authErrorCode.name();
            }
        }
        authResponse.setErrorCode(errorCode);
    }

    /**
     * Extracts the {@link DeviceConnection#getAuthenticationType()} to be used for this connect attempt from the {@link AuthRequest}.
     *
     * @param authRequest The {@link AuthRequest} from which to extract data.
     * @return The resolved  {@link DeviceConnection#getAuthenticationType()}.
     * @throws KapuaException
     * @since 2.0.0
     */
    private String extractAuthTypeFromAuthRequest(AuthRequest authRequest) throws KapuaException {
        User user = KapuaSecurityUtils.doPrivileged(() -> userService.findByName(authRequest.getUsername()));
        if (user == null) {
            throw new KapuaEntityNotFoundException(User.TYPE, authRequest.getUsername());
        }
        DeviceConnection deviceConnection = KapuaSecurityUtils.doPrivileged(() -> deviceConnectionService.findByClientId(user.getScopeId(), authRequest.getClientId()));
        if (deviceConnection != null) {
            return deviceConnection.getAuthenticationType();
        } else {
            Map<String, Object> deviceConnectionServiceConfigValues = KapuaSecurityUtils.doPrivileged(() -> deviceConnectionService.getConfigValues(user.getScopeId()));
            return (String) deviceConnectionServiceConfigValues.get("deviceConnectionAuthenticationType");
        }
    }

    /**
     * Builds the {@link LoginCredentials} to be used from the resolved {@link DeviceConnection#getAuthenticationType()}.
     *
     * @param authRequest              The {@link AuthRequest} with the {@link LoginCredentials} values.
     * @param deviceConnectionAuthType The resolved {@link DeviceConnection#getAuthenticationType()}.
     * @return The {@link LoginCredentials} for the given {@link DeviceConnection#getAuthenticationType()}.
     * @throws KapuaException
     * @since 2.0.0
     */
    private LoginCredentials buildLoginCredentialsFromAuthType(AuthRequest authRequest, String deviceConnectionAuthType) throws KapuaException {
        DeviceConnectionCredentialAdapter deviceConnectionCredentialAdapter = deviceConnectionAuthHandlers.get(deviceConnectionAuthType);
        if (deviceConnectionCredentialAdapter == null) {
            throw new UnsupportedOperationException("No DeviceConnectionCredentialAdapter has been found for the given DeviceConnection.authenticationType: " + deviceConnectionAuthType);
        }
        return deviceConnectionCredentialAdapter.mapToCredential(authRequest);
    }
}
