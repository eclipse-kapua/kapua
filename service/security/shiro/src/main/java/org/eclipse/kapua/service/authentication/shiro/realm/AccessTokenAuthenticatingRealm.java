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
package org.eclipse.kapua.service.authentication.shiro.realm;

import java.util.Date;
import java.util.Optional;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.subject.Subject;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.authentication.AccessTokenCredentials;
import org.eclipse.kapua.service.authentication.shiro.AccessTokenCredentialsImpl;
import org.eclipse.kapua.service.authentication.shiro.exceptions.ExpiredAccessTokenException;
import org.eclipse.kapua.service.authentication.shiro.exceptions.InvalidatedAccessTokenException;
import org.eclipse.kapua.service.authentication.shiro.exceptions.JwtCertificateNotFoundException;
import org.eclipse.kapua.service.authentication.shiro.exceptions.MalformedAccessTokenException;
import org.eclipse.kapua.service.authentication.shiro.setting.KapuaAuthenticationSetting;
import org.eclipse.kapua.service.authentication.shiro.setting.KapuaAuthenticationSettingKeys;
import org.eclipse.kapua.service.authentication.token.AccessToken;
import org.eclipse.kapua.service.authentication.token.AccessTokenAttributes;
import org.eclipse.kapua.service.authentication.token.AccessTokenService;
import org.eclipse.kapua.service.certificate.CertificateAttributes;
import org.eclipse.kapua.service.certificate.CertificateStatus;
import org.eclipse.kapua.service.certificate.info.CertificateInfo;
import org.eclipse.kapua.service.certificate.info.CertificateInfoFactory;
import org.eclipse.kapua.service.certificate.info.CertificateInfoListResult;
import org.eclipse.kapua.service.certificate.info.CertificateInfoQuery;
import org.eclipse.kapua.service.certificate.info.CertificateInfoService;
import org.eclipse.kapua.service.certificate.util.CertificateUtils;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.ErrorCodes;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link AccessTokenCredentials} based {@link AuthenticatingRealm} implementation.
 *
 * @since 1.0.0
 */
public class AccessTokenAuthenticatingRealm extends KapuaAuthenticatingRealm {

    /**
     * Realm name.
     */
    public static final String REALM_NAME = "accessTokenAuthenticatingRealm";
    private final CertificateInfoFactory certificateInfoFactory = KapuaLocator.getInstance().getFactory(CertificateInfoFactory.class);
    private final CertificateInfoService certificateInfoService = KapuaLocator.getInstance().getService(CertificateInfoService.class);
    private final AccessTokenService accessTokenService = KapuaLocator.getInstance().getService(AccessTokenService.class);
    private final UserService userService = KapuaLocator.getInstance().getService(UserService.class);
    private final KapuaAuthenticationSetting authenticationSetting = KapuaLocator.getInstance().getComponent(KapuaAuthenticationSetting.class);
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Constructor
     *
     * @since 1.0.0
     */
    public AccessTokenAuthenticatingRealm() {
        setName(REALM_NAME);
        setCredentialsMatcher(new AllowAllCredentialsMatcher());
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        logger.trace("processing authenticationToken: {}", authenticationToken);
        // Extract credentials
        final AccessTokenCredentialsImpl token = (AccessTokenCredentialsImpl) authenticationToken;
        // Token data
        final String jwt = token.getTokenId();
        logger.trace("processing jwt: {}", jwt);
        //verify validity of this token
        final JwtClaims jwtClaims;
        try {
            final String issuer = authenticationSetting.getString(KapuaAuthenticationSettingKeys.AUTHENTICATION_SESSION_JWT_ISSUER);
            final CertificateInfo certificateInfo = getNearestCertificate(authenticationToken);

            if (certificateInfo == null) {
                throw new JwtCertificateNotFoundException();
            }
            // Set validator
            final JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                    .setVerificationKey(CertificateUtils.stringToCertificate(certificateInfo.getCertificate()).getPublicKey()) // Set public key
                    .setExpectedIssuer(issuer) // Set expected issuer
                    .setRequireIssuedAt() // Set require reserved claim: iatp
                    .setRequireExpirationTime() // Set require reserved claim: exp
                    .setRequireSubject() // // Set require reserved claim: sub
                    .build();
            // This validates JWT
            final JwtContext jwtContext = jwtConsumer.process(jwt);
            jwtClaims = jwtContext.getJwtClaims();
            // FIXME: JWT cert. could be cached to speed-up validation process
        } catch (KapuaException | MalformedClaimException ke) {
            //As we are swallowing the original exception, let's at least log it
            logger.error("Error processing Auth Token(KapuaException)", ke);
            throw new AuthenticationException();
        } catch (InvalidJwtException e) {
            //As we are swallowing the original exception, let's at least log it
            logger.error("Error processing Auth Token (InvalidJwtException)", e);
            if (e.hasErrorCode(ErrorCodes.EXPIRED)) {
                throw new ExpiredAccessTokenException();
            } else {
                throw new MalformedAccessTokenException();
            }
        }

        // Find accessToken
        final AccessToken accessToken;
        try {
            final String tokenIdentifier = Optional.ofNullable(jwtClaims.getClaimValue(AccessTokenAttributes.TOKEN_IDENTIFIER))
                    .map(s -> (String) s)
                    .orElseThrow(() -> new ShiroException("Missing tokenIdentifier in jwt token"));
            accessToken = KapuaSecurityUtils.doPrivileged(() -> accessTokenService.findByTokenId(tokenIdentifier));
        } catch (KapuaException ke) {
            throw new AuthenticationException();
        }

        // Check existence
        if (accessToken == null) {
            throw new UnknownAccountException();
        }

        // Check validity
        Date now = new Date();
        if (accessToken.getInvalidatedOn() != null && accessToken.getInvalidatedOn().before(now)) {
            throw new InvalidatedAccessTokenException();
        }

        // Get the associated user by name
        final User user;
        try {
            user = KapuaSecurityUtils.doPrivileged(() -> userService.find(accessToken.getScopeId(), accessToken.getUserId()));
        } catch (AuthenticationException ae) {
            throw ae;
        } catch (Exception e) {
            throw new ShiroException("Unexpected error while looking for the user!", e);
        }
        // Check user
        checkUser(user);
        // Check account
        Account account = checkAccount(user.getScopeId());
        // BuildAuthenticationInfo
        return new SessionAuthenticationInfo(
                jwtClaims,
                getName(),
                account,
                user,
                accessToken);
    }

    @Override
    protected void assertCredentialsMatch(AuthenticationToken authcToken, AuthenticationInfo info)
            throws AuthenticationException {
        SessionAuthenticationInfo kapuaInfo = (SessionAuthenticationInfo) info;
        // Credential match
        super.assertCredentialsMatch(authcToken, info);
        // Set kapua session
        AccessToken accessToken = kapuaInfo.getAccessToken();
        KapuaSession kapuaSession = new KapuaSession(accessToken, accessToken.getScopeId(), accessToken.getUserId());
        KapuaSecurityUtils.setSession(kapuaSession);
        // Set shiro session
        Subject currentSubject = SecurityUtils.getSubject();
        currentSubject.getSession().setAttribute(KapuaSession.KAPUA_SESSION_KEY, kapuaSession);
    }

    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return authenticationToken instanceof AccessTokenCredentialsImpl;
    }

    private CertificateInfo getNearestCertificate(AuthenticationToken authenticationToken)
            throws KapuaException, InvalidJwtException, MalformedClaimException {
        final JwtConsumer jwtConsumerNoValidation = new JwtConsumerBuilder()
                .setSkipAllValidators()
                .setDisableRequireSignature()
                .setSkipSignatureVerification()
                .build();
        final String principal = (String) authenticationToken.getPrincipal();
        final String username = jwtConsumerNoValidation.process(principal).getJwtClaims().getSubject();
        final String account = jwtConsumerNoValidation.process(principal).getJwtClaims().getClaimValueAsString("sId");
        final KapuaId userId = KapuaEid.parseCompactId(username);
        final KapuaId accountId = KapuaEid.parseCompactId(account);
        final User user = KapuaSecurityUtils.doPrivileged(() -> userService.find(accountId, userId));

        final CertificateInfoQuery certificateInfoQuery = new CertificateInfoQuery(user.getScopeId());
        certificateInfoQuery.setPredicate(
                certificateInfoQuery.andPredicate(
                        certificateInfoQuery.attributePredicate(CertificateAttributes.USAGE_NAME, "JWT"),
                        certificateInfoQuery.attributePredicate(CertificateAttributes.STATUS, CertificateStatus.VALID)
                )
        );
        certificateInfoQuery.setIncludeInherited(true);

        final CertificateInfoListResult certificateInfoList = KapuaSecurityUtils.doPrivileged(() -> certificateInfoService.query(certificateInfoQuery));
        return getNearestCertificateFromList(certificateInfoList);
    }

    private CertificateInfo getNearestCertificateFromList(CertificateInfoListResult certificateInfoList) throws KapuaException {
        CertificateInfo result = certificateInfoList.getFirstItem();
        for (CertificateInfo certificateInfo : certificateInfoList.getItems()) {
            if (isNearestThat(certificateInfo, result)) {
                result = certificateInfo;
            }
        }
        return result;
    }

    private boolean isNearestThat(CertificateInfo certificateInfo, CertificateInfo result) throws KapuaException {
        final KapuaId certificateInfoScopeId = certificateInfo.getScopeId();
        final KapuaId resultScopeId = result.getScopeId();
        return isChildOf(certificateInfoScopeId, resultScopeId);
    }

    private boolean isChildOf(KapuaId certificateInfoScopeId, KapuaId resultScopeId) throws KapuaException {
        final Account certificateInfoAccount = KapuaSecurityUtils.doPrivileged(() -> accountService.find(certificateInfoScopeId));
        final Account resultAccount = KapuaSecurityUtils.doPrivileged(() -> accountService.find(resultScopeId));

        KapuaId scopeId = certificateInfoAccount.getScopeId();
        while (scopeId != null) {
            if (scopeId.equals(resultAccount.getScopeId())) {
                return true;
            }
            final KapuaId finalScopeId = scopeId;
            scopeId = KapuaSecurityUtils.doPrivileged(() -> accountService.find(finalScopeId)).getId();
        }
        return false;
    }
}
