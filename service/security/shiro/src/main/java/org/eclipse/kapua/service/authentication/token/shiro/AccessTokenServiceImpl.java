/*******************************************************************************
 * Copyright (c) 2016, 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authentication.token.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.event.ServiceEvent;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authentication.exception.KapuaAuthenticationErrorCodes;
import org.eclipse.kapua.service.authentication.exception.KapuaAuthenticationException;
import org.eclipse.kapua.service.authentication.shiro.setting.KapuaAuthenticationSetting;
import org.eclipse.kapua.service.authentication.shiro.setting.KapuaAuthenticationSettingKeys;
import org.eclipse.kapua.service.authentication.token.AccessToken;
import org.eclipse.kapua.service.authentication.token.AccessTokenAttributes;
import org.eclipse.kapua.service.authentication.token.AccessTokenCreator;
import org.eclipse.kapua.service.authentication.token.AccessTokenFactory;
import org.eclipse.kapua.service.authentication.token.AccessTokenListResult;
import org.eclipse.kapua.service.authentication.token.AccessTokenQuery;
import org.eclipse.kapua.service.authentication.token.AccessTokenRepository;
import org.eclipse.kapua.service.authentication.token.AccessTokenService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.certificate.Certificate;
import org.eclipse.kapua.service.certificate.CertificateAttributes;
import org.eclipse.kapua.service.certificate.CertificateFactory;
import org.eclipse.kapua.service.certificate.CertificateQuery;
import org.eclipse.kapua.service.certificate.CertificateService;
import org.eclipse.kapua.service.certificate.CertificateStatus;
import org.eclipse.kapua.service.certificate.util.CertificateUtils;
import org.eclipse.kapua.storage.TxManager;
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

import javax.inject.Singleton;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * {@link AccessTokenService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class AccessTokenServiceImpl implements AccessTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessTokenServiceImpl.class);
    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final TxManager txManager;
    private final AccessTokenRepository accessTokenRepository;
    private final AccessTokenFactory accessTokenFactory;
    private final CertificateService certificateService;
    private final CertificateFactory certificateFactory;


    private final KapuaAuthenticationSetting kapuaAuthenticationSetting;
    private final long accessTokenTtl;
    private final long accessTokenRefreshTtl;
    private final JwtConsumer jwtConsumer;

    public AccessTokenServiceImpl(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            AccessTokenRepository accessTokenRepository,
            AccessTokenFactory accessTokenFactory,
            CertificateService certificateService,
            CertificateFactory certificateFactory,
            TxManager txManager,
            KapuaAuthenticationSetting kapuaAuthenticationSetting
    ) {
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.accessTokenRepository = accessTokenRepository;
        this.accessTokenFactory = accessTokenFactory;
        this.certificateService = certificateService;
        this.certificateFactory = certificateFactory;

        this.txManager = txManager;

        // Retrieve TTL access token
        this.kapuaAuthenticationSetting = kapuaAuthenticationSetting;

        this.accessTokenTtl = kapuaAuthenticationSetting.getLong(KapuaAuthenticationSettingKeys.AUTHENTICATION_TOKEN_EXPIRE_AFTER);
        this.accessTokenRefreshTtl = kapuaAuthenticationSetting.getLong(KapuaAuthenticationSettingKeys.AUTHENTICATION_REFRESH_TOKEN_EXPIRE_AFTER);

        // Configure JWT Consumer
        this.jwtConsumer = new JwtConsumerBuilder()
                .setSkipAllValidators()
                .setDisableRequireSignature()
                .setSkipSignatureVerification()
                .build();
    }

    @Override
    public AccessToken create(AccessTokenCreator accessTokenCreator) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(accessTokenCreator, "accessTokenCreator");
        ArgumentValidator.notNull(accessTokenCreator.getScopeId(), "accessTokenCreator.scopeId");
        ArgumentValidator.notNull(accessTokenCreator.getTokenId(), "accessTokenCreator.tokenId");
        ArgumentValidator.notNull(accessTokenCreator.getUserId(), "accessTokenCreator.userId");
        ArgumentValidator.notNull(accessTokenCreator.getExpiresOn(), "accessTokenCreator.expiresOn");
        ArgumentValidator.notNull(accessTokenCreator.getTokenIdentifier(), "accessTokenCreator.tokenIdentifier");

        // Check access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_TOKEN, Actions.write, accessTokenCreator.getScopeId()));

        // Do create
        AccessToken at = accessTokenFactory.newEntity(accessTokenCreator.getScopeId());
        at.setUserId(accessTokenCreator.getUserId());
        at.setTokenId(accessTokenCreator.getTokenId());
        at.setExpiresOn(accessTokenCreator.getExpiresOn());
        at.setRefreshToken(accessTokenCreator.getRefreshToken());
        at.setRefreshExpiresOn(accessTokenCreator.getRefreshExpiresOn());
        at.setTokenIdentifier(accessTokenCreator.getTokenIdentifier());

        return txManager.execute(tx -> accessTokenRepository.create(tx, at));
    }

    @Override
    public AccessToken update(AccessToken accessToken) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(accessToken, "accessToken");
        ArgumentValidator.notNull(accessToken.getId(), "accessToken.id");
        ArgumentValidator.notNull(accessToken.getScopeId(), "accessToken.scopeId");
        ArgumentValidator.notNull(accessToken.getUserId(), "accessToken.userId");
        ArgumentValidator.notNull(accessToken.getExpiresOn(), "accessToken.expiresOn");
        // Check access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_TOKEN, Actions.write, accessToken.getScopeId()));
        return txManager.execute(tx -> {
            // Check existence
            if (!accessTokenRepository.find(tx, accessToken.getScopeId(), accessToken.getId()).isPresent()) {
                throw new KapuaEntityNotFoundException(AccessToken.TYPE, accessToken.getId());
            }
            // Do update
            return accessTokenRepository.update(tx, accessToken);
        });
    }

    @Override
    public AccessToken find(KapuaId scopeId, KapuaId accessTokenId) throws KapuaException {
        // Validation of the fields
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(accessTokenId, KapuaEntityAttributes.ENTITY_ID);
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_TOKEN, Actions.read, scopeId));
        // Do find
        return txManager.execute(tx -> accessTokenRepository.find(tx, scopeId, accessTokenId))
                .orElse(null);
    }

    @Override
    public AccessTokenListResult query(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_TOKEN, Actions.read, query.getScopeId()));
        // Do query
        return txManager.execute(tx -> accessTokenRepository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_TOKEN, Actions.read, query.getScopeId()));
        // Do count
        return txManager.execute(tx -> accessTokenRepository.count(tx, query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId accessTokenId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(accessTokenId, KapuaEntityAttributes.ENTITY_ID);
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_TOKEN, Actions.delete, scopeId));
        // Check existence
        txManager.execute(tx -> {
            if (!accessTokenRepository.find(tx, scopeId, accessTokenId).isPresent()) {
                throw new KapuaEntityNotFoundException(AccessToken.TYPE, accessTokenId);
            }
            // Do delete
            return accessTokenRepository.delete(tx, scopeId, accessTokenId);
        });
    }

    @Override
    public AccessTokenListResult findByUserId(KapuaId scopeId, KapuaId userId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(userId, "userId");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_TOKEN, Actions.read, scopeId));
        // Build query
        AccessTokenQuery query = new AccessTokenQueryImpl(scopeId);
        query.setPredicate(query.attributePredicate(AccessTokenAttributes.USER_ID, userId));
        // Do query
        return query(query);
    }

    @Override
    public AccessToken findByTokenIdentifier(String tokenIdentifier) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(tokenIdentifier, "tokenIdentifier");

        // Do find
        Optional<AccessToken> accessToken = txManager.execute(tx -> accessTokenRepository.findByTokenId(tx, tokenIdentifier));

        // Check Access
        if (accessToken.isPresent()) {
            authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_TOKEN, Actions.read, accessToken.get().getScopeId()));
        }

        return accessToken.orElse(null);
    }

    @Override
    public AccessToken findByTokenId(String tokenIdentifier) throws KapuaException {
        return findByTokenIdentifier(tokenIdentifier);
    }

    @Override
    public AccessToken findByJwt(String jwt) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(jwt, "jwt");

        // Parse JWT
        String tokenIdentifier;
        try {
            JwtContext jwtContext = jwtConsumer.process(jwt);

            tokenIdentifier = jwtContext
                .getJwtClaims()
                .getClaimValue(AccessTokenAttributes.TOKEN_IDENTIFIER, String.class);

            if (tokenIdentifier == null) {
                throw new KapuaIllegalArgumentException("jwt", jwt);
            }
        } catch (InvalidJwtException | MalformedClaimException e) {
            throw new AuthenticationException();
        }

        // Do find
        return KapuaSecurityUtils.doPrivileged(() -> findByTokenIdentifier(tokenIdentifier));
    }

    @Override
    public AccessToken generateAccessToken(KapuaId scopeId, KapuaId userId) throws KapuaException {
        // Generate AccessToken info
        Date now = new Date();
        String refreshToken = UUID.randomUUID().toString();
        String tokenIdentifier = UUID.randomUUID().toString();
        String jwt = generateJwt(scopeId, userId, now, accessTokenTtl, tokenIdentifier);

        // Persist AccessToken
        try {
            AccessTokenCreator accessTokenCreator = accessTokenFactory.newCreator(scopeId);
            accessTokenCreator.setUserId(userId);
            accessTokenCreator.setTokenId(jwt);
            accessTokenCreator.setExpiresOn(new Date(now.getTime() + accessTokenTtl));
            accessTokenCreator.setRefreshToken(refreshToken);
            accessTokenCreator.setRefreshExpiresOn(new Date(now.getTime() + accessTokenRefreshTtl));
            accessTokenCreator.setTokenIdentifier(tokenIdentifier);

            return KapuaSecurityUtils.doPrivileged(() -> create(accessTokenCreator));
        } catch (Exception e) {
            throw KapuaException.internalError(e);
        }
    }

    @Override
    public AccessToken refreshAccessToken(String tokenId, String refreshToken) throws KapuaException {
        // Find existing
        AccessToken expiredAccessToken;
        try {
            expiredAccessToken = findByJwt(tokenId);
        } catch (AuthenticationException e) {
            throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.REFRESH_ERROR, "");
        }

        // Check AccessToken
        Date now = new Date();
        if (expiredAccessToken == null) {
            throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.REFRESH_ERROR, "AccessToken not found");
        } else if (expiredAccessToken.getInvalidatedOn() != null && now.after(expiredAccessToken.getInvalidatedOn())) {
            throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.REFRESH_ERROR, "The provided AccessToken has been invalidated");
        } else if (!expiredAccessToken.getRefreshToken().equals(refreshToken)) {
            throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.REFRESH_ERROR, "The provided refresh token doesn't match the one for this AccessToken");
        } else if (expiredAccessToken.getRefreshExpiresOn() != null && now.after(expiredAccessToken.getRefreshExpiresOn())) {
            throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.REFRESH_ERROR, "The provided refresh token is expired");
        }

        // Invalidate existing
        KapuaSecurityUtils.doPrivileged(() -> {
            try {
                invalidate(expiredAccessToken.getScopeId(), expiredAccessToken.getId());
            } catch (KapuaEntityNotFoundException kenfe) {
                // Exception should not be propagated it is sometimes expected behaviour
            }
            return null;
        });

        // Create a new one
        return generateAccessToken(expiredAccessToken.getScopeId(), expiredAccessToken.getUserId());
    }

    @Override
    public void invalidate(KapuaId scopeId, KapuaId accessTokenId) throws KapuaException {
        // Validation of the fields
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(accessTokenId, KapuaEntityAttributes.ENTITY_ID);

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_TOKEN, Actions.write, scopeId));

        // Do find
        txManager.execute(tx ->
                accessTokenRepository.find(tx, scopeId, accessTokenId)
                        .map(at -> {
                            at.setInvalidatedOn(new Date());
                            return accessTokenRepository.update(tx, at, at);
                        })
                        .orElseThrow(() -> new KapuaEntityNotFoundException(AccessToken.TYPE, scopeId)));
    }

    //
    // Private Methods

    /**
     * Generates a JWT with the given information and signs it with the available JWT {@link Certificate}. This to be used as a {@link AccessToken#getTokenId()}
     *
     * @param scopeId The {@link AccessToken#getScopeId()}
     * @param userId The {@link AccessToken#getUserId()}
     * @param now The issue {@link Date}
     * @param ttl The TTL in ms that this {@link AccessToken} will be valid
     * @param tokenId The {@link AccessToken#getTokenIdentifier()}
     * @return The JWT in plain String form
     * @since 1.0.0
     */
    private String generateJwt(KapuaId scopeId, KapuaId userId, Date now, long ttl, String tokenId) {
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

        try {
            CertificateQuery certificateQuery = certificateFactory.newQuery(scopeId);
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

            return jws.getCompactSerialization();
        } catch (JoseException | KapuaException e) {
            throw KapuaRuntimeException.internalError(e);
        }
    }

    //@ListenServiceEvent(fromAddress="account")
    //@ListenServiceEvent(fromAddress="user")
    public void onKapuaEvent(ServiceEvent kapuaEvent) throws KapuaException {
        if (kapuaEvent == null) {
            //service bus error. Throw some exception?
        }

        LOGGER.info("AccessTokenService: received kapua event from {}, operation {}", kapuaEvent.getService(), kapuaEvent.getOperation());
        if ("user".equals(kapuaEvent.getService()) && "delete".equals(kapuaEvent.getOperation())) {
            deleteAccessTokenByUserId(kapuaEvent.getScopeId(), kapuaEvent.getEntityId());
        } else if ("account".equals(kapuaEvent.getService()) && "delete".equals(kapuaEvent.getOperation())) {
            deleteAccessTokenByAccountId(kapuaEvent.getScopeId(), kapuaEvent.getEntityId());
        }
    }

    private void deleteAccessTokenByUserId(KapuaId scopeId, KapuaId userId) throws KapuaException {

        AccessTokenQuery query = new AccessTokenQueryImpl(scopeId);
        query.setPredicate(query.attributePredicate(AccessTokenAttributes.USER_ID, userId));

        AccessTokenListResult accessTokensToDelete = query(query);

        for (AccessToken at : accessTokensToDelete.getItems()) {
            delete(at.getScopeId(), at.getId());
        }
    }

    private void deleteAccessTokenByAccountId(KapuaId scopeId, KapuaId accountId) throws KapuaException {
        AccessTokenQuery query = new AccessTokenQueryImpl(accountId);

        AccessTokenListResult accessTokensToDelete = query(query);

        for (AccessToken at : accessTokensToDelete.getItems()) {
            delete(at.getScopeId(), at.getId());
        }
    }
}
