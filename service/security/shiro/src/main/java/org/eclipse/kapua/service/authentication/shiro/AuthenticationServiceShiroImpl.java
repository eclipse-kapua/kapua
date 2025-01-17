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
package org.eclipse.kapua.service.authentication.shiro;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.subject.Subject;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.KapuaUnauthenticatedException;
import org.eclipse.kapua.commons.logging.LoggingMdcKeys;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.commons.util.KapuaDelayUtil;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.service.authentication.AuthenticationCredentials;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.LoginCredentials;
import org.eclipse.kapua.service.authentication.SessionCredentials;
import org.eclipse.kapua.service.authentication.UsernamePasswordCredentials;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.credential.CredentialService;
import org.eclipse.kapua.service.authentication.credential.handler.shiro.PasswordCredentialTypeHandler;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOptionService;
import org.eclipse.kapua.service.authentication.exception.KapuaAuthenticationErrorCodes;
import org.eclipse.kapua.service.authentication.exception.KapuaAuthenticationException;
import org.eclipse.kapua.service.authentication.shiro.exceptions.MfaRequiredException;
import org.eclipse.kapua.service.authentication.shiro.realm.CredentialsConverter;
import org.eclipse.kapua.service.authentication.shiro.realm.KapuaAuthenticationToken;
import org.eclipse.kapua.service.authentication.shiro.session.ShiroSessionKeys;
import org.eclipse.kapua.service.authentication.shiro.setting.KapuaAuthenticationSetting;
import org.eclipse.kapua.service.authentication.shiro.setting.KapuaAuthenticationSettingKeys;
import org.eclipse.kapua.service.authentication.token.AccessToken;
import org.eclipse.kapua.service.authentication.token.AccessTokenAttributes;
import org.eclipse.kapua.service.authentication.token.AccessTokenCreator;
import org.eclipse.kapua.service.authentication.token.AccessTokenFactory;
import org.eclipse.kapua.service.authentication.token.AccessTokenService;
import org.eclipse.kapua.service.authentication.token.LoginInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfoService;
import org.eclipse.kapua.service.authorization.access.AccessPermissionAttributes;
import org.eclipse.kapua.service.authorization.access.AccessPermissionFactory;
import org.eclipse.kapua.service.authorization.access.AccessPermissionListResult;
import org.eclipse.kapua.service.authorization.access.AccessPermissionService;
import org.eclipse.kapua.service.authorization.access.AccessRole;
import org.eclipse.kapua.service.authorization.access.AccessRoleAttributes;
import org.eclipse.kapua.service.authorization.access.AccessRoleFactory;
import org.eclipse.kapua.service.authorization.access.AccessRoleListResult;
import org.eclipse.kapua.service.authorization.access.AccessRoleService;
import org.eclipse.kapua.service.authorization.role.RolePermissionAttributes;
import org.eclipse.kapua.service.authorization.role.RolePermissionFactory;
import org.eclipse.kapua.service.authorization.role.RolePermissionListResult;
import org.eclipse.kapua.service.authorization.role.RolePermissionService;
import org.eclipse.kapua.service.certificate.Certificate;
import org.eclipse.kapua.service.certificate.CertificateAttributes;
import org.eclipse.kapua.service.certificate.CertificateFactory;
import org.eclipse.kapua.service.certificate.CertificateQuery;
import org.eclipse.kapua.service.certificate.CertificateService;
import org.eclipse.kapua.service.certificate.CertificateStatus;
import org.eclipse.kapua.service.certificate.util.CertificateUtils;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.common.collect.Sets;

/**
 * {@link AuthenticationService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class AuthenticationServiceShiroImpl implements AuthenticationService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationServiceShiroImpl.class);

    private final CredentialService credentialService;
    private final MfaOptionService mfaOptionService;

    private final AccessTokenService accessTokenService;
    private final AccessTokenFactory accessTokenFactory;
    private final CertificateService certificateService;
    private final CertificateFactory certificateFactory;

    private final AccessInfoService accessInfoService;
    private final AccessRoleService accessRoleService;
    private final AccessRoleFactory accessRoleFactory;
    private final RolePermissionService rolePermissionService;
    private final RolePermissionFactory rolePermissionFactory;
    private final AccessPermissionService accessPermissionService;
    private final AccessPermissionFactory accessPermissionFactory;

    private final UserService userService;

    private final Set<CredentialsConverter> credentialsConverters;
    private final KapuaAuthenticationSetting kapuaAuthenticationSetting;

    private final JwtConsumer jwtConsumer;

    @Inject
    public AuthenticationServiceShiroImpl(
            CredentialService credentialService,
            MfaOptionService mfaOptionService,
            AccessTokenService accessTokenService,
            AccessTokenFactory accessTokenFactory,
            CertificateService certificateService,
            CertificateFactory certificateFactory,
            AccessInfoService accessInfoService,
            AccessRoleService accessRoleService,
            AccessRoleFactory accessRoleFactory,
            RolePermissionService rolePermissionService,
            RolePermissionFactory rolePermissionFactory,
            AccessPermissionService accessPermissionService,
            AccessPermissionFactory accessPermissionFactory,
            UserService userService,
            Set<CredentialsConverter> credentialsConverters,
            KapuaAuthenticationSetting kapuaAuthenticationSetting) {
        this.credentialService = credentialService;
        this.mfaOptionService = mfaOptionService;
        this.accessTokenService = accessTokenService;
        this.accessTokenFactory = accessTokenFactory;
        this.certificateService = certificateService;
        this.certificateFactory = certificateFactory;
        this.accessInfoService = accessInfoService;
        this.accessRoleService = accessRoleService;
        this.accessRoleFactory = accessRoleFactory;
        this.rolePermissionService = rolePermissionService;
        this.rolePermissionFactory = rolePermissionFactory;
        this.accessPermissionService = accessPermissionService;
        this.accessPermissionFactory = accessPermissionFactory;
        this.userService = userService;
        this.credentialsConverters = credentialsConverters;
        this.kapuaAuthenticationSetting = kapuaAuthenticationSetting;
        this.jwtConsumer = new JwtConsumerBuilder()
                .setSkipAllValidators()
                .setDisableRequireSignature()
                .setSkipSignatureVerification()
                .build();
    }

    @Override
    public AccessToken login(LoginCredentials loginCredentials, boolean enableTrust) throws KapuaException {

        if (loginCredentials instanceof UsernamePasswordCredentials) {
            UsernamePasswordCredentialsImpl usernamePasswordCredentials = UsernamePasswordCredentialsImpl.parse((UsernamePasswordCredentials) loginCredentials);
            usernamePasswordCredentials.setTrustMe(enableTrust);
        }

        return login(loginCredentials);
    }

    @Override
    public AccessToken login(LoginCredentials loginCredentials) throws KapuaException {
        // Check LoginCredentials
        if (loginCredentials == null) {
            throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.INVALID_LOGIN_CREDENTIALS);
        }

        // Check subject
        checkCurrentSubjectNotAuthenticated();

        // Parse login credentials
        KapuaAuthenticationToken shiroAuthenticationToken = doMapToShiro(loginCredentials);

        // Login the user
        AccessToken accessToken = null;
        Subject currentUser = null;
        try {
            // Shiro login
            currentUser = SecurityUtils.getSubject();
            currentUser.login(shiroAuthenticationToken);

            // Create the access token
            // FIXME: It is likely that it is possible to use the currentUser instead of getting it again
            Subject shiroSubject = SecurityUtils.getSubject();
            Session shiroSession = shiroSubject.getSession();
            accessToken = createAccessToken(shiroSession);

            // Establish session
            String openIDidToken = shiroAuthenticationToken.getOpenIdToken().orElse(null);
            establishSession(shiroSubject, accessToken, openIDidToken);

            // Create trust key if required
            createTrustKey(shiroAuthenticationToken, accessToken);

            LOG.info("Login for thread '{}' - '{}' - '{}'", Thread.currentThread().getId(), Thread.currentThread().getName(), shiroSubject);
        } catch (ShiroException se) {
            handleTokenLoginException(se, currentUser, shiroAuthenticationToken);
        }

        return accessToken;
    }

    @Override
    public void authenticate(SessionCredentials sessionCredentials) throws KapuaException {
        // Check LoginCredentials
        if (sessionCredentials == null) {
            throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.INVALID_SESSION_CREDENTIALS);
        }

        // Check subject
        checkCurrentSubjectNotAuthenticated();

        // Parse login credentials
        AuthenticationToken shiroAuthenticationToken = doMapToShiro(sessionCredentials);

        // Login the user
        Subject currentUser = null;
        try {
            // Shiro login
            currentUser = SecurityUtils.getSubject();
            currentUser.login(shiroAuthenticationToken);

            // Retrieve token
            AccessToken accessToken = findAccessTokenSession((String) shiroAuthenticationToken.getCredentials());

            // Enstablish session
            establishSession(currentUser, accessToken, null);

            LOG.info("Login for thread '{}' - '{}' - '{}'", Thread.currentThread().getId(), Thread.currentThread().getName(), SecurityUtils.getSubject());
        } catch (ShiroException se) {

            KapuaAuthenticationException kae;
            if (se instanceof UnknownAccountException) {
                kae = new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.UNKNOWN_SESSION_CREDENTIAL, se, shiroAuthenticationToken.getPrincipal());
            } else if (se instanceof DisabledAccountException) {
                kae = new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.DISABLED_SESSION_CREDENTIAL, se, shiroAuthenticationToken.getPrincipal());
            } else if (se instanceof LockedAccountException) {
                kae = new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.LOCKED_SESSION_CREDENTIAL, se, shiroAuthenticationToken.getPrincipal());
            } else if (se instanceof IncorrectCredentialsException) {
                kae = new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.INVALID_SESSION_CREDENTIALS, se, shiroAuthenticationToken.getPrincipal());
            } else if (se instanceof ExpiredCredentialsException) {
                kae = new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.EXPIRED_SESSION_CREDENTIALS, se, shiroAuthenticationToken.getPrincipal());
            } else {
                throw KapuaException.internalError(se);
            }

            if (currentUser != null) {
                currentUser.logout();
            }
            throw kae;
        }

    }

    @Override
    public void verifyCredentials(LoginCredentials loginCredentials) throws KapuaException {
        AuthenticationToken shiroAuthenticationToken = doMapToShiro(loginCredentials);

        // Login the user
        Subject verifySubject = null;
        try {
            // Create custom subject to verify
            verifySubject =
                    new Subject
                            .Builder()
                            .session(new SimpleSession())
                            .sessionCreationEnabled(false)
                            .authenticated(false)
                            .buildSubject();

            // Login its token
            verifySubject.login(shiroAuthenticationToken);

            // Logout after verification has occurred
            verifySubject.logout();

        } catch (ShiroException se) {
            handleTokenLoginException(se, verifySubject, shiroAuthenticationToken);
        }
    }

    @Override
    public void logout()
            throws KapuaException {
        Subject currentUser = SecurityUtils.getSubject();
        try {
            KapuaSession kapuaSession = KapuaSecurityUtils.getSession();
            if (kapuaSession != null) {
                AccessToken accessToken = kapuaSession.getAccessToken();

                if (accessToken != null) {
                    KapuaSecurityUtils.doPrivileged(() ->
                            accessTokenService.invalidate(accessToken.getScopeId(), accessToken.getId())
                    );
                }
            }
            currentUser.logout();
        } catch (KapuaEntityNotFoundException kenfe) {
            // Exception should not be propagated it is sometimes expected behaviour
        } catch (Exception e) {
            throw KapuaException.internalError(e);
        } finally {
            KapuaSecurityUtils.clearSession();
        }
    }

    public AccessToken findAccessTokenSession(String jwt) throws KapuaException {
        AccessToken accessToken = null;
        try {
            KapuaSession kapuaSession = KapuaSecurityUtils.getSession();
            if (kapuaSession != null) {
                accessToken = kapuaSession.getAccessToken();

                if (accessToken == null) {
                    accessToken = findAccessToken(jwt);
                }
            }
        } finally {
            KapuaSecurityUtils.clearSession();
        }

        return accessToken;
    }

    public AccessToken findAccessToken(String jwt) throws KapuaException {
        try {
            final JwtContext jwtContext = jwtConsumer.process(jwt);
            final String tokenIdentifier = Optional.ofNullable(jwtContext.getJwtClaims().getClaimValue(AccessTokenAttributes.TOKEN_IDENTIFIER, String.class))
                    .orElseThrow(() -> new AuthenticationException());
            return KapuaSecurityUtils.doPrivileged(() -> accessTokenService.findByTokenId(tokenIdentifier));
        } catch (InvalidJwtException | MalformedClaimException e) {
            throw new AuthenticationException();
        }
    }

    @Override
    public AccessToken findRefreshableAccessToken(String tokenId) throws KapuaException {
        KapuaQuery accessTokenQuery = new KapuaQuery((KapuaId) null);
        AndPredicate andPredicate = accessTokenQuery.andPredicate(
                accessTokenQuery.attributePredicate(AccessTokenAttributes.REFRESH_EXPIRES_ON, new java.sql.Timestamp(new Date().getTime()), AttributePredicate.Operator.GREATER_THAN_OR_EQUAL),
                accessTokenQuery.attributePredicate(AccessTokenAttributes.INVALIDATED_ON, null, AttributePredicate.Operator.IS_NULL),
                accessTokenQuery.attributePredicate(AccessTokenAttributes.TOKEN_ID, tokenId)
        );
        accessTokenQuery.setPredicate(andPredicate);
        accessTokenQuery.setLimit(1);
        return accessTokenService.query(accessTokenQuery).getFirstItem();
    }

    @Override
    public AccessToken refreshAccessToken(String tokenId, String refreshToken) throws KapuaException {
        Date now = new Date();
        AccessToken expiredAccessToken;
        try {
            expiredAccessToken = findAccessToken(tokenId);
        } catch (AuthenticationException e) {
            throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.REFRESH_ERROR, "");
        }
        if (expiredAccessToken == null) {
            throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.REFRESH_ERROR, "");
        } else if (expiredAccessToken.getInvalidatedOn() != null && now.after(expiredAccessToken.getInvalidatedOn())) {
            throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.REFRESH_ERROR, "The provided access token has been invalidated");
        } else if (!expiredAccessToken.getRefreshToken().equals(refreshToken)) {
            throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.REFRESH_ERROR, "The provided refresh token doesn't match the one for this jwt");
        } else if (expiredAccessToken.getRefreshExpiresOn() != null && now.after(expiredAccessToken.getRefreshExpiresOn())) {
            throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.REFRESH_ERROR, "The provided refresh token is expired");
        }
        KapuaSecurityUtils.doPrivileged(() -> {
            try {
                accessTokenService.invalidate(expiredAccessToken.getScopeId(), expiredAccessToken.getId());
            } catch (KapuaEntityNotFoundException kenfe) {
                // Exception should not be propagated it is sometimes expected behaviour
            }
            return null;
        });
        return createAccessToken((KapuaEid) expiredAccessToken.getScopeId(), (KapuaEid) expiredAccessToken.getUserId());
    }

    @Override
    public LoginInfo getLoginInfo() throws KapuaException {
        LoginInfo loginInfo = accessTokenFactory.newLoginInfo();

        // AccessToken
        AccessToken accessToken = KapuaSecurityUtils.getSession().getAccessToken();
        loginInfo.setAccessToken(accessToken);

        // AccessInfo
        AccessInfo accessInfo = KapuaSecurityUtils.doPrivileged(() -> accessInfoService.findByUserId(accessToken.getScopeId(), accessToken.getUserId()));

        // AccessRole
        final KapuaQuery accessRoleQuery = new KapuaQuery(accessToken.getScopeId());
        accessRoleQuery.setPredicate(accessRoleQuery.attributePredicate(AccessRoleAttributes.ACCESS_INFO_ID, accessInfo.getId()));
        AccessRoleListResult accessRoleListResult = KapuaSecurityUtils.doPrivileged(() -> accessRoleService.query(accessRoleQuery));

        // RolePermission
        final KapuaQuery rolePermissionQuery = new KapuaQuery(accessToken.getScopeId());
        rolePermissionQuery.setPredicate(
                rolePermissionQuery.attributePredicate(RolePermissionAttributes.ROLE_ID, accessRoleListResult.getItems().stream().map(AccessRole::getRoleId).collect(Collectors.toList())));
        RolePermissionListResult rolePermissions = KapuaSecurityUtils.doPrivileged(() -> rolePermissionService.query(rolePermissionQuery));
        loginInfo.setRolePermission(Sets.newHashSet(rolePermissions.getItems()));

        // AccessPermission
        final KapuaQuery accessPermissionQuery = new KapuaQuery(accessToken.getScopeId());
        accessPermissionQuery.setPredicate(accessPermissionQuery.attributePredicate(AccessPermissionAttributes.ACCESS_INFO_ID, accessInfo.getId()));
        AccessPermissionListResult accessPermissions = KapuaSecurityUtils.doPrivileged(() -> accessPermissionService.query(accessPermissionQuery));
        loginInfo.setAccessPermission(Sets.newHashSet(accessPermissions.getItems()));

        return loginInfo;
    }

    @Override
    public boolean isAuthenticated()
            throws KapuaException {
        KapuaSession session = KapuaSecurityUtils.getSession();

        if (session == null) {
            throw new KapuaUnauthenticatedException();
        }

        return session.isTrustedMode() || SecurityUtils.getSubject().isAuthenticated();
    }

    //
    // Private Methods
    //

    /**
     * Checks if the Shiro {@link Subject} is authenticated or not. If {@link Subject#isAuthenticated()} {@code equals true}, {@link KapuaAuthenticationException} is raised.
     *
     * @throws KapuaAuthenticationException
     *         If {@link Subject#isAuthenticated()} {@code equals true}
     * @since 1.0
     */
    private void checkCurrentSubjectNotAuthenticated()
            throws KapuaAuthenticationException {
        Subject currentUser = SecurityUtils.getSubject();

        if (currentUser != null && currentUser.isAuthenticated()) {
            LOG.info("Thread already authenticated for thread '{}' - '{}' - '{}'", Thread.currentThread().getId(), Thread.currentThread().getName(), currentUser);
            throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.SUBJECT_ALREADY_LOGGED);
        }
    }

    /**
     * Converts am instance of {@link AuthenticationCredentials} to the compatible {@link KapuaAuthenticationToken} to be used in Apache Shiro.
     *
     * @param authenticationCredentials
     *         The {@link AuthenticationCredentials} to convert
     * @return The converted {@link KapuaAuthenticationToken}.
     * @throws KapuaAuthenticationException
     *         if the instance of {@link AuthenticationCredentials} cannot be handled or is are invalid.
     * @since 2.0.0
     */
    private KapuaAuthenticationToken doMapToShiro(AuthenticationCredentials authenticationCredentials) throws KapuaAuthenticationException {
        // Parse login credentials
        CredentialsConverter credentialsConverter = credentialsConverters
                .stream()
                .filter(ch -> ch.canProcess(authenticationCredentials))
                .findFirst()
                .orElseThrow(() -> new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.INVALID_CREDENTIALS_TYPE_PROVIDED));

        return credentialsConverter.convertToShiro(authenticationCredentials);
    }

    private void handleTokenLoginException(ShiroException se, Subject currentSubject, AuthenticationToken authenticationToken) throws KapuaException {

        if (currentSubject != null) {
            currentSubject.logout();
        }

        KapuaAuthenticationException kae;

        if (se instanceof UnknownAccountException) {
            kae = new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.UNKNOWN_LOGIN_CREDENTIAL, se, authenticationToken.getPrincipal());
        } else if (se instanceof LockedAccountException) {
            kae = new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.LOCKED_LOGIN_CREDENTIAL, se, authenticationToken.getPrincipal());
        } else if (se instanceof DisabledAccountException) {
            kae = new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.DISABLED_LOGIN_CREDENTIAL, se, authenticationToken.getPrincipal());
        } else if (se instanceof MfaRequiredException) {
            kae = new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.REQUIRE_MFA_CREDENTIALS, se, authenticationToken.getPrincipal());
        } else if (se instanceof IncorrectCredentialsException) {
            if (checkIfCredentialHasJustBeenLocked(authenticationToken)) {
                kae = new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.LOCKED_LOGIN_CREDENTIAL, se, authenticationToken.getPrincipal());
            } else {
                kae = new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.INVALID_LOGIN_CREDENTIALS, se, authenticationToken.getPrincipal());
            }
        } else if (se instanceof ExpiredCredentialsException) {
            kae = new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.EXPIRED_LOGIN_CREDENTIALS, se, authenticationToken.getPrincipal());
        } else {
            throw KapuaException.internalError(se);
        }

        KapuaDelayUtil.executeDelay();
        throw kae;
    }

    /**
     * Create and persist a {@link AccessToken} from the data contained in the Shiro {@link Session}
     *
     * @param session
     *         The Shiro {@link Session} from which extract data
     * @return The persisted {@link AccessToken}
     * @throws KapuaException
     * @since 1.0
     */
    private AccessToken createAccessToken(Session session) throws KapuaException {
        // Extract userId and scope id from the shiro session
        KapuaEid scopeId = (KapuaEid) session.getAttribute("scopeId");
        KapuaEid userId = (KapuaEid) session.getAttribute("userId");

        return createAccessToken(scopeId, userId);
    }

    /**
     * Create and persist a {@link AccessToken} from a scopeId and a userId
     *
     * @param scopeId
     *         The scopeID
     * @param userId
     *         The userID
     * @return The persisted {@link AccessToken}
     * @throws KapuaException
     * @since 1.0.0
     */
    private AccessToken createAccessToken(KapuaEid scopeId, KapuaEid userId) throws KapuaException {
        // Retrieve TTL access token
        long tokenTtl = kapuaAuthenticationSetting.getLong(KapuaAuthenticationSettingKeys.AUTHENTICATION_TOKEN_EXPIRE_AFTER);
        long refreshTokenTtl = kapuaAuthenticationSetting.getLong(KapuaAuthenticationSettingKeys.AUTHENTICATION_REFRESH_TOKEN_EXPIRE_AFTER);

        // Generate token
        Date now = new Date();
        String tokenId = UUID.randomUUID().toString();
        String jwt = generateJwt(scopeId, userId, now, tokenTtl, tokenId);

        // Persist token
        AccessTokenCreator accessTokenCreator = new AccessTokenCreator(scopeId,
                userId,
                jwt,
                new Date(now.getTime() + tokenTtl),
                UUID.randomUUID().toString(),
                new Date(now.getTime() + refreshTokenTtl),
                tokenId);

        AccessToken accessToken;
        try {
            accessToken = KapuaSecurityUtils.doPrivileged(() -> accessTokenService.create(accessTokenCreator));
        } catch (Exception e) {
            throw KapuaException.internalError(e);
        }

        return accessToken;
    }

    /**
     * Creates a trust key if given {@link AuthenticationToken} is a {@link UsernamePasswordCredentials} and if {@link UsernamePasswordCredentials#getTrustMe()} is {@code true}
     *
     * @param shiroAuthenticationToken
     *         The {@link AuthenticationToken} extracted {@link LoginCredentials}
     * @param accessToken
     *         The {@link AccessToken} of this login.
     * @throws KapuaException
     * @since 2.0.0
     */
    private void createTrustKey(AuthenticationToken shiroAuthenticationToken, AccessToken accessToken) throws KapuaException {
        if (shiroAuthenticationToken instanceof UsernamePasswordCredentials) {
            UsernamePasswordCredentialsImpl usernamePasswordCredentials = UsernamePasswordCredentialsImpl.parse((UsernamePasswordCredentials) shiroAuthenticationToken);

            if (usernamePasswordCredentials.getTrustMe()) {
                String trustKey = KapuaSecurityUtils.doPrivileged(() -> {
                    return mfaOptionService.enableTrust(accessToken.getScopeId(), accessToken.getUserId());
                });

                accessToken.setTrustKey(trustKey);
            }
        }
    }

    private void establishSession(Subject subject, AccessToken accessToken, String openIDidToken) {
        KapuaSession kapuaSession = new KapuaSession(accessToken, accessToken.getScopeId(), accessToken.getUserId(), openIDidToken);
        KapuaSecurityUtils.setSession(kapuaSession);

        Session subjectSession = subject.getSession();
        subjectSession.setAttribute(KapuaSession.KAPUA_SESSION_KEY, kapuaSession);

        // Set some logging stuff
        MDC.put(LoggingMdcKeys.SCOPE_ID, accessToken.getScopeId().toCompactId());
        MDC.put(LoggingMdcKeys.ACCOUNT_NAME, (String) subjectSession.getAttribute(ShiroSessionKeys.ACCOUNT_NAME));
        MDC.put(LoggingMdcKeys.USER_ID, accessToken.getUserId().toCompactId());
        MDC.put(LoggingMdcKeys.USER_NAME, (String) subjectSession.getAttribute(ShiroSessionKeys.USER_NAME));
    }

    private String generateJwt(KapuaEid scopeId, KapuaEid userId, Date now, long ttl, String tokenId) {
        // Build claims
        JwtClaims claims = new JwtClaims();

        // Reserved claims
        String issuer = kapuaAuthenticationSetting.getString(KapuaAuthenticationSettingKeys.AUTHENTICATION_SESSION_JWT_ISSUER);
        Date issuedAtDate = now; // Issued at claim
        Date expiresOnDate = new Date(now.getTime() + ttl); // Expires claim.

        claims.setIssuer(issuer);
        claims.setIssuedAt(NumericDate.fromMilliseconds(issuedAtDate.getTime()));
        claims.setExpirationTime(NumericDate.fromMilliseconds(expiresOnDate.getTime()));

        // Jwts.builder().setIssuer(issuer)
        // .setIssuedAt(issuedAtDate)
        // .setExpiration(new Date(expiresOnDate))
        // .setSubject(userId.getShortId()).claims.setClaim("sId", scopeId.getShortId());
        claims.setSubject(userId.toCompactId());
        claims.setClaim("sId", scopeId.toCompactId());
        claims.setStringClaim(AccessTokenAttributes.TOKEN_IDENTIFIER, tokenId);

        String jwt = null;
        try {
            CertificateQuery certificateQuery = new CertificateQuery(scopeId);
            certificateQuery.setPredicate(
                    certificateQuery.andPredicate(
                            certificateQuery.attributePredicate(CertificateAttributes.USAGE_NAME, "JWT"),
                            certificateQuery.attributePredicate(CertificateAttributes.STATUS, CertificateStatus.VALID)
                    )
            );

            certificateQuery.setIncludeInherited(true);
            certificateQuery.setLimit(1);

            Certificate certificate = KapuaSecurityUtils.doPrivileged(() -> certificateService.query(certificateQuery)).getFirstItem();
            if (certificate == null) {
                throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.JWT_CERTIFICATE_NOT_FOUND);
            }
            JsonWebSignature jws = new JsonWebSignature();
            jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
            jws.setPayload(claims.toJson());
            jws.setKey(CertificateUtils.stringToPrivateKey(certificate.getPrivateKey(), certificate.getPassword()));
            jwt = jws.getCompactSerialization();
        } catch (JoseException | KapuaException e) {
            throw KapuaRuntimeException.internalError(e);
        }
        return jwt;
    }

    /**
     * Checks if a {@link Credential} is locked.
     * <p>
     * As of 2.1.0, only PASSWORD credentials can be locked.
     *
     * @param authenticationToken
     *         The {@link AuthenticationToken} for the login attempt.
     * @since 1.1.0
     */
    private Boolean checkIfCredentialHasJustBeenLocked(AuthenticationToken authenticationToken) throws KapuaException {
        String principal = (String) authenticationToken.getPrincipal();
        User user = KapuaSecurityUtils.doPrivileged(() -> userService.findByName(principal));

        // Retrieve Credential
        Credential passwordCredential = null;
        if (user != null) {
            passwordCredential = KapuaSecurityUtils.doPrivileged(() ->
                    credentialService.findByUserId(user.getScopeId(), user.getId(), PasswordCredentialTypeHandler.TYPE).getFirstItem()
            );
        }

        // Do check on current timestamp
        Date now = new Date();
        return passwordCredential != null &&
                passwordCredential.getLockoutReset() != null &&
                now.before(passwordCredential.getLockoutReset());
    }

}
