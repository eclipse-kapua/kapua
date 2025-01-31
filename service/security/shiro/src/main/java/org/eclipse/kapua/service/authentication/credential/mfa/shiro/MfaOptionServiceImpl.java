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
package org.eclipse.kapua.service.authentication.credential.mfa.shiro;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang.time.DateUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.qr.QRCodeBuilder;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authentication.credential.mfa.KapuaExistingMfaOptionException;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOption;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOptionAttributes;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOptionCreator;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOptionListResult;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOptionRepository;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOptionService;
import org.eclipse.kapua.service.authentication.credential.mfa.ScratchCode;
import org.eclipse.kapua.service.authentication.credential.mfa.ScratchCodeListResult;
import org.eclipse.kapua.service.authentication.credential.mfa.ScratchCodeRepository;
import org.eclipse.kapua.service.authentication.exception.KapuaAuthenticationErrorCodes;
import org.eclipse.kapua.service.authentication.exception.KapuaAuthenticationException;
import org.eclipse.kapua.service.authentication.mfa.MfaAuthenticator;
import org.eclipse.kapua.service.authentication.shiro.exceptions.MfaRequiredException;
import org.eclipse.kapua.service.authentication.shiro.utils.AuthenticationUtils;
import org.eclipse.kapua.service.authentication.shiro.utils.CryptAlgorithm;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.exception.InternalUserOnlyException;
import org.eclipse.kapua.service.authorization.exception.SelfManagedOnlyException;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.service.user.UserType;
import org.eclipse.kapua.storage.TxContext;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * {@link MfaOptionService} implementation.
 *
 * @since 1.3.0
 */
public class MfaOptionServiceImpl implements MfaOptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MfaOptionServiceImpl.class);

    private final int trustKeyDuration;
    private final MfaAuthenticator mfaAuthenticator;
    private final TxManager txManager;
    private final MfaOptionRepository mfaOptionRepository;
    private final AccountService accountService;
    private final ScratchCodeRepository scratchCodeRepository;
    private final AuthorizationService authorizationService;
    private final UserService userService;
    private final AuthenticationUtils authenticationUtils;
    private final QRCodeBuilder qrCodeBuilder;

    public MfaOptionServiceImpl(
            int trustKeyDuration,
            MfaAuthenticator mfaAuthenticator, TxManager txManager,
            MfaOptionRepository mfaOptionRepository,
            AccountService accountService,
            ScratchCodeRepository scratchCodeRepository,
            AuthorizationService authorizationService,
            UserService userService,
            AuthenticationUtils authenticationUtils, QRCodeBuilder qrCodeBuilder) {
        this.trustKeyDuration = trustKeyDuration;
        this.mfaAuthenticator = mfaAuthenticator;
        this.txManager = txManager;
        this.mfaOptionRepository = mfaOptionRepository;
        this.accountService = accountService;
        this.scratchCodeRepository = scratchCodeRepository;
        this.authorizationService = authorizationService;
        this.userService = userService;
        this.authenticationUtils = authenticationUtils;
        this.qrCodeBuilder = qrCodeBuilder;
    }

    @Override
    public MfaOption create(final MfaOptionCreator mfaOptionCreator) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(mfaOptionCreator, "mfaOptionCreator");
        ArgumentValidator.notNull(mfaOptionCreator.getScopeId(), "mfaOptionCreator.scopeId");
        ArgumentValidator.notNull(mfaOptionCreator.getUserId(), "mfaOptionCreator.userId");
        // Check access
        authorizationService.checkPermission(new Permission(Domains.CREDENTIAL, Actions.write, mfaOptionCreator.getScopeId()));
        // Check that the operation is carried by the user itself
        final KapuaSession session = KapuaSecurityUtils.getSession();
        final KapuaId expectedUser = session.getUserId();
        if (!expectedUser.equals(mfaOptionCreator.getUserId())) {
            throw new SelfManagedOnlyException();
        }

        //Generate the key and then create the entity in the db
        final String fullKey = mfaAuthenticator.generateKey();
        final MfaOption option = txManager.execute(tx -> {
            // Check that the user is an internal user (external users cannot have the MFA enabled)
            final User user = fetchUser(mfaOptionCreator);
            final Account account = fetchAccount(mfaOptionCreator);

            // Check existing MfaOption
            Optional<MfaOption> existingMfaOption = mfaOptionRepository.findByUserId(tx, mfaOptionCreator.getScopeId(), mfaOptionCreator.getUserId());
            if (existingMfaOption.isPresent()) {
                throw new KapuaExistingMfaOptionException();
            }

            // Do create
            MfaOption toCreate = new MfaOptionImpl(mfaOptionCreator.getScopeId());
            toCreate.setUserId(mfaOptionCreator.getUserId());
            toCreate.setMfaSecretKey(fullKey);
            final MfaOption mfaOption = mfaOptionRepository.create(tx, toCreate);

            // generating base64 QR code image

            mfaOption.setQRCodeImage(generateQRCode(account.getOrganization().getName(), account.getName(), user.getName(), fullKey));
            mfaOption.setScratchCodes(createAllScratchCodes(tx, mfaOption.getScopeId(), mfaOption.getId()));
            return mfaOption;
        });

        // Do post persist magic on key value (note that this is the only place in which the key is returned in plain-text)
        option.setMfaSecretKey(fullKey);
        return option;
    }

    private Account fetchAccount(MfaOptionCreator mfaOptionCreator) throws KapuaException {
        final Account account = Optional.ofNullable(
                KapuaSecurityUtils.doPrivileged(() ->
                        accountService.find(KapuaId.ANY, mfaOptionCreator.getScopeId())
                )
        ).orElseThrow(() -> new KapuaEntityNotFoundException(Account.TYPE, mfaOptionCreator.getScopeId()));
        return account;
    }

    private User fetchUser(MfaOptionCreator mfaOptionCreator) throws KapuaException {
        final User user = Optional.ofNullable(
                KapuaSecurityUtils.doPrivileged(() ->
                        userService.find(mfaOptionCreator.getScopeId(), mfaOptionCreator.getUserId())
                )
        ).orElseThrow(() -> new KapuaEntityNotFoundException(User.TYPE, mfaOptionCreator.getUserId()));
        if (!user.getUserType().equals(UserType.INTERNAL) || user.getExternalId() != null) {
            throw new InternalUserOnlyException();
        }
        return user;
    }

    /**
     * Generates all the scratch codes. The number of generated scratch codes is decided through the {@link org.eclipse.kapua.service.authentication.mfa.MfaAuthenticator} service. The scratch code
     * provided within the scratchCodeCreator parameter is ignored.
     *
     * @return
     * @throws KapuaException
     */
    private List<ScratchCode> createAllScratchCodes(TxContext tx, KapuaId scopeId, KapuaId mfaOptionImpl) throws KapuaException {
        final List<String> codes = mfaAuthenticator.generateCodes();
        final List<ScratchCode> res = new ArrayList<>();
        for (String code : codes) {

            // Crypto code (it's ok to do than if BCrypt is used when checking a provided scratch code against the stored one)
            String encryptedCode = authenticationUtils.cryptCredential(CryptAlgorithm.BCRYPT, code);
            // Create code
            ScratchCode scratchCode = scratchCodeRepository.create(tx, new ScratchCodeImpl(scopeId, mfaOptionImpl, encryptedCode));
            //return a copy of the created entity, with the code in clear to be seen by the user
            final ScratchCodeImpl clearCodeClone = new ScratchCodeImpl(scratchCode);
            clearCodeClone.setCode(code);
            res.add(clearCodeClone);
        }
        return res;
    }

    @Override
    public MfaOption update(MfaOption mfaOption) throws KapuaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MfaOption find(KapuaId scopeId, KapuaId mfaOptionId) throws KapuaException {
        // Validation of the fields
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(mfaOptionId, "mfaOptionId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.CREDENTIAL, Actions.read, scopeId));

        return txManager.execute(tx -> mfaOptionRepository.find(tx, scopeId, mfaOptionId))
                .map(this::clearSecuritySensibleFields)
                .orElse(null);
    }

    private MfaOption clearSecuritySensibleFields(MfaOption mfaOption) {
        // Set the mfa secret key to null before returning the mfaOption, because they should never be seen again
        mfaOption.setMfaSecretKey(null);
        mfaOption.setTrustKey(null);
        mfaOption.setScratchCodes(null);
        mfaOption.setQRCodeImage(null);
        return mfaOption;
    }

    @Override
    public MfaOptionListResult query(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.CREDENTIAL, Actions.read, query.getScopeId()));

        final MfaOptionListResult res = txManager.execute(tx -> mfaOptionRepository.query(tx, query));
        if (res.isEmpty() == false) {
            final MfaOptionListResult cleanedRes = new MfaOptionListResult();
            cleanedRes.setLimitExceeded(res.isLimitExceeded());
            cleanedRes.setTotalCount(res.getTotalCount());
            cleanedRes.addItems(res.getItems()
                    .stream()
                    .map(this::clearSecuritySensibleFields)
                    .collect(Collectors.toList()));
            return cleanedRes;
        }
        return res;
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.CREDENTIAL, Actions.read, query.getScopeId()));

        return txManager.execute(tx -> mfaOptionRepository.count(tx, query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId mfaOptionId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(mfaOptionId, "mfaOptionId");
        ArgumentValidator.notNull(scopeId, "scopeId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.CREDENTIAL, Actions.delete, scopeId));

        txManager.execute(tx -> mfaOptionRepository.delete(tx, scopeId, mfaOptionId));
    }

    @Override
    public void deleteByUserId(KapuaId scopeId, KapuaId userId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(userId, "userId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.CREDENTIAL, Actions.delete, scopeId));

        txManager.execute(tx -> mfaOptionRepository
                .findByUserId(tx, scopeId, userId)
                .map(d -> mfaOptionRepository.delete(tx, d))
                .orElseThrow(() -> new KapuaEntityNotFoundException(MfaOptionImpl.TYPE, userId)));
    }

    @Override
    public boolean validateMfaCredentials(KapuaId scopeId, KapuaId userId, String tokenAuthenticationCode, String tokenTrustKey) throws KapuaException {
        if (!mfaAuthenticator.isEnabled()) {
            return true;
        }
        final Boolean res = txManager.execute(tx -> {
            // Check if MFA is enabled for the current user
            final MfaOption mfaOption;
            try {
                final Optional<MfaOption> maybeOption = mfaOptionRepository.findByUserId(tx, scopeId, userId);
                if (!maybeOption.isPresent()) {
                    return true;  // MFA service is enabled, but the user has no MFA enabled
                }
                mfaOption = maybeOption.get();
            } catch (Exception e) {
                throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.AUTHENTICATION_ERROR, e, "Error while finding Mfa Option!");
            }

            if (tokenAuthenticationCode != null) {
                return validateFromTokenAuthenticationCode(tx, scopeId, mfaOption, tokenAuthenticationCode);
            }

            // If authentication code is null, then check the trust_key
            if (tokenTrustKey != null) {
                return validateFromTrustKey(tx, mfaOption, tokenTrustKey);
            }
            return false;
        });
        if (!res) {
            if (tokenAuthenticationCode != null && !tokenAuthenticationCode.isEmpty()) {
                throw new IncorrectCredentialsException();
            }
            // In case both the authenticationCode and the trustKey are null, the MFA login via Rest API must be triggered.
            // Since this method only returns true or false, the MFA request via Rest API is handled through exceptions.
            // It could also be the case that tokens are not null but empty, in this case we throw same exception...
            throw new MfaRequiredException();
        }
        return res;
    }

    private Boolean validateFromTrustKey(TxContext tx, MfaOption mfaOption, String tokenTrustKey) throws KapuaAuthenticationException {
        // Check trust machine authentication on the server side
        if (mfaOption.getTrustKey() == null || tokenTrustKey.isEmpty()) {
            return false;
        }
        Date now = new Date(System.currentTimeMillis());
        if (mfaOption.getTrustExpirationDate().before(now)) {
            // The trust key is expired and must be disabled
            try {
                doDisableTrust(tx, mfaOption);
                return false;
            } catch (Exception e) {
                throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.MFA_ERROR, e, "Error while disabling trust!");
            }
        }
        return BCrypt.checkpw(tokenTrustKey, mfaOption.getTrustKey());
    }

    private Boolean validateFromTokenAuthenticationCode(TxContext tx, KapuaId scopeId, MfaOption mfaOption, String tokenAuthenticationCode) throws KapuaAuthenticationException {
        // Do MFA match
        try {
            if (tokenAuthenticationCode.isEmpty()) { //Token is not a numeric value and it's empty, so for sure validation is false even considering scratch codes
                return false;
            }
            final int numberToken = Integer.parseInt(tokenAuthenticationCode);
            boolean isCodeValid = mfaAuthenticator.authorize(mfaOption.getMfaSecretKey(), numberToken);
            if (isCodeValid) {
                return true;
            }
        } catch (NumberFormatException e) {
            //Token is not a valid number, continue assuming it is a scratch code
        } catch (Exception e) {
            throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.MFA_ERROR, e, "Error while authenticating Mfa Option!");
        }

        //  Code is not valid, try scratch codes login
        final ScratchCodeListResult scratchCodeListResult;
        try {
            scratchCodeListResult = scratchCodeRepository.findByMfaOptionId(tx, scopeId, mfaOption.getId());
        } catch (Exception e) {
            throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.MFA_ERROR, e, "Error while finding scratch codes!");
        }

        for (ScratchCode code : scratchCodeListResult.getItems()) {
            final boolean codeIsValid;
            try {
                codeIsValid = mfaAuthenticator.authorize(code.getCode(), tokenAuthenticationCode);
            } catch (KapuaException e) {
                throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.MFA_ERROR, e, "Error while validating scratch codes!");
            }
            if (codeIsValid) {
                try {
                    // Delete the used scratch code
                    scratchCodeRepository.delete(tx, code);
                    return true;
                } catch (Exception e) {
                    throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.MFA_ERROR, e, "Error while removing used scratch code!");
                }
            }
        }
        //None of the scratch codes matched, authentication failed
        return false;
    }

    @Override
    public MfaOption findByUserId(KapuaId scopeId, KapuaId userId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(userId, MfaOptionAttributes.USER_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.CREDENTIAL, Actions.read, scopeId));

        return txManager.execute(tx -> mfaOptionRepository.findByUserId(tx, scopeId, userId))
                .map(this::clearSecuritySensibleFields)
                .orElse(null);
    }

    @Override
    public String enableTrust(KapuaId scopeId, KapuaId userId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(userId, "userId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.CREDENTIAL, Actions.read, scopeId));
        authorizationService.checkPermission(new Permission(Domains.CREDENTIAL, Actions.write, scopeId));

        return txManager.execute(tx -> {
            // Checking existence
            MfaOption mfaOption = mfaOptionRepository.findByUserId(tx, scopeId, userId)
                    .orElseThrow(() -> new KapuaEntityNotFoundException(MfaOption.TYPE, userId));

            // Trust key generation always performed
            // This allows the use only of a single trusted machine,
            // until a solution with different trust keys is implemented!
            final String trustKey = generateTrustKey();
            mfaOption.setTrustKey(authenticationUtils.cryptCredential(CryptAlgorithm.BCRYPT, trustKey));

            Date expirationDate = new Date(System.currentTimeMillis());
            expirationDate = DateUtils.addDays(expirationDate, trustKeyDuration);
            mfaOption.setTrustExpirationDate(expirationDate);

            // Update
            mfaOptionRepository.update(tx, mfaOption);

            return trustKey;
        });
    }

    @Override
    public void disableTrust(KapuaId scopeId, KapuaId mfaOptionId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(mfaOptionId, "mfaOptionId");
        ArgumentValidator.notNull(scopeId, "scopeId");
        authorizationService.checkPermission(new Permission(Domains.CREDENTIAL, Actions.write, scopeId));
        txManager.execute(tx -> {
            // extracting the MfaOption
            MfaOption mfaOption = mfaOptionRepository.find(tx, scopeId, mfaOptionId)
                    .orElseThrow(() -> new KapuaEntityNotFoundException(MfaOption.TYPE, mfaOptionId));

            return doDisableTrust(tx, mfaOption);
        });
    }

    @Override
    public void disableTrustByUserId(KapuaId scopeId, KapuaId userId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(userId, "userId");
        ArgumentValidator.notNull(scopeId, "scopeId");
        txManager.execute(tx -> {
            // extracting the MfaOption
            MfaOption mfaOption = mfaOptionRepository.findByUserId(tx, scopeId, userId)
                    .orElseThrow(() -> new KapuaEntityNotFoundException(MfaOption.TYPE, userId));

            // Reset the trust machine fields
            return doDisableTrust(tx, mfaOption);
        });
    }

    private MfaOption doDisableTrust(TxContext tx, MfaOption mfaOption) throws KapuaException {
        // Reset the trust machine fields
        mfaOption.setTrustKey(null);
        mfaOption.setTrustExpirationDate(null);

        return mfaOptionRepository.update(tx, mfaOption);
    }

    /**
     * Generate the trust key string.
     *
     * @return String
     * @since 1.3.0
     */
    private String generateTrustKey() {
        return UUID.randomUUID().toString();
    }

    /**
     * Produce a QR code in base64 format for the authenticator app. This QR code generator follows the spec detailed here for the URI format:
     * https://github.com/google/google-authenticator/wiki/Key-Uri-Format
     *
     * @param organizationName
     *         the organization name to be used as issuer in the QR code
     * @param accountName
     *         the account name of the account to which the user belongs
     * @param username
     *         the username
     * @param key
     *         the Mfa secret key in plain text
     * @return the QR code image in base64 format
     * @since 1.3.0
     */
    private String generateQRCode(String organizationName, String accountName, String username, String key) {
        URI uri = null;
        try {
            uri = new URIBuilder()
                    .setScheme("otpauth")
                    .setHost("totp")
                    .setPath(organizationName + ":" + username + "@" + accountName)
                    .setParameter("secret", key)
                    .setParameter("issuer", organizationName)
                    .build();
            return qrCodeBuilder.generateQRCode(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
