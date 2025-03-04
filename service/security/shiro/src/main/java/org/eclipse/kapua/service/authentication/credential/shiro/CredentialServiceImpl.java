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
package org.eclipse.kapua.service.authentication.credential.shiro;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.commons.configuration.KapuaConfigurableServiceBase;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.event.ServiceEvent;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.credential.CredentialAttributes;
import org.eclipse.kapua.service.authentication.credential.CredentialCreator;
import org.eclipse.kapua.service.authentication.credential.CredentialFactory;
import org.eclipse.kapua.service.authentication.credential.CredentialListResult;
import org.eclipse.kapua.service.authentication.credential.CredentialQuery;
import org.eclipse.kapua.service.authentication.credential.CredentialRepository;
import org.eclipse.kapua.service.authentication.credential.CredentialService;
import org.eclipse.kapua.service.authentication.credential.handler.CredentialTypeHandler;
import org.eclipse.kapua.service.authentication.shiro.setting.KapuaAuthenticationSetting;
import org.eclipse.kapua.service.authentication.shiro.setting.KapuaAuthenticationSettingKeys;
import org.eclipse.kapua.service.authentication.user.PasswordResetRequest;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link CredentialService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class CredentialServiceImpl extends KapuaConfigurableServiceBase implements CredentialService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialServiceImpl.class);

    private SecureRandom random;
    private final CredentialRepository credentialRepository;
    private final CredentialFactory credentialFactory;
    private final KapuaAuthenticationSetting kapuaAuthenticationSetting;
    private final AccountPasswordLengthProvider accountPasswordLengthProvider;
    private final PasswordValidator passwordValidator;
    private final PasswordResetter passwordResetter;

    private final Map<String, CredentialTypeHandler> availableCredentialAuthenticationType;

    public CredentialServiceImpl(
            ServiceConfigurationManager serviceConfigurationManager,
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            TxManager txManager,
            CredentialRepository credentialRepository,
            CredentialFactory credentialFactory,
            PasswordValidator passwordValidator,
            KapuaAuthenticationSetting kapuaAuthenticationSetting,
            Set<CredentialTypeHandler> availableCredentialAuthenticationType,
            AccountPasswordLengthProvider accountPasswordLengthProvider,
            PasswordResetter passwordResetter) {
        super(txManager, serviceConfigurationManager, Domains.CREDENTIAL, authorizationService, permissionFactory);

        this.credentialRepository = credentialRepository;
        this.credentialFactory = credentialFactory;
        this.kapuaAuthenticationSetting = kapuaAuthenticationSetting;
        this.accountPasswordLengthProvider = accountPasswordLengthProvider;
        this.passwordResetter = passwordResetter;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw KapuaRuntimeException.internalError(e, "Cannot instantiate SecureRandom (SHA1PRNG)");
        }
        this.passwordValidator = passwordValidator;
        this.availableCredentialAuthenticationType = availableCredentialAuthenticationType
                .stream()
                .collect(Collectors.toMap(CredentialTypeHandler::getName, Function.identity()));
    }

    @Override
    public Credential create(CredentialCreator credentialCreator)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(credentialCreator, "credentialCreator");
        ArgumentValidator.notNull(credentialCreator.getScopeId(), "credentialCreator.scopeId");
        ArgumentValidator.notNull(credentialCreator.getUserId(), "credentialCreator.userId");
        ArgumentValidator.notNull(credentialCreator.getCredentialType(), "credentialCreator.credentialType");
        ArgumentValidator.notNull(credentialCreator.getCredentialStatus(), "credentialCreator.credentialStatus");

        CredentialTypeHandler credentialTypeHandler = availableCredentialAuthenticationType.get(credentialCreator.getCredentialType());
        if (credentialTypeHandler == null) {
            throw new KapuaIllegalArgumentException("credentialCreator.credentialType", credentialCreator.getCredentialType());
        }
        credentialTypeHandler.validateCreator(credentialCreator);

        // Check access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.CREDENTIAL, Actions.write, credentialCreator.getScopeId()));

        final AtomicReference<String> plainKey = new AtomicReference<>(null);

        Credential credential = txManager.execute(tx -> {

            // Generated plain key if necessary
            if (credentialTypeHandler.isServiceGenerated()) {
                plainKey.set(credentialTypeHandler.generate());

                credentialCreator.setCredentialPlainKey(plainKey.get());
            }

            // Crypt plain key
            String encryptedKey = credentialTypeHandler.cryptCredentialKey(credentialCreator.getCredentialPlainKey());

            // Convert creator to entity
            Credential newCredential = new CredentialImpl(
                    credentialCreator.getScopeId(),
                    credentialCreator.getUserId(),
                    credentialCreator.getCredentialType(),
                    encryptedKey,
                    credentialCreator.getCredentialStatus(),
                    credentialCreator.getExpirationDate()
            );

            // Do create
            return credentialRepository.create(tx, newCredential);
        });

        // Set back the plain key to return service generated secret once
        credential.setCredentialKey(plainKey.get());

        // Return resul
        return credential;
    }

    @Override
    public Credential update(Credential credential)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(credential, "credential");
        ArgumentValidator.notNull(credential.getId(), "credential.id");
        ArgumentValidator.notNull(credential.getScopeId(), "credential.scopeId");
        ArgumentValidator.notNull(credential.getUserId(), "credential.userId");
        ArgumentValidator.notNull(credential.getCredentialType(), "credential.credentialType");

        // Check access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.CREDENTIAL, Actions.write, credential.getScopeId()));

        final Credential updatedCredential = txManager.execute(tx -> {
            Credential currentCredential = credentialRepository.find(tx, credential.getScopeId(), credential.getId())
                    .orElseThrow(() -> new KapuaEntityNotFoundException(Credential.TYPE, credential.getId()));

            if (!currentCredential.getCredentialType().equals(credential.getCredentialType())) {
                throw new KapuaIllegalArgumentException("credentialType", credential.getCredentialType());
            }

            // Some fields must be updated only by admin users
            if (tryEditAdminFields(credential, currentCredential)) {
                authorizationService.checkPermission(permissionFactory.newPermission(Domains.CREDENTIAL, Actions.write, null));
            }

            // Passing attributes??
            return credentialRepository.update(tx, credential);
        });
        updatedCredential.setCredentialKey(null);
        return updatedCredential;
    }

    @Override
    public Credential find(KapuaId scopeId, KapuaId credentialId)
            throws KapuaException {
        // Validation of the fields
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(credentialId, "credentialId");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.CREDENTIAL, Actions.read, scopeId));

        return txManager.execute(tx -> credentialRepository.find(tx, scopeId, credentialId))
                .map(cred -> {
                    cred.setCredentialKey(null);
                    return cred;
                })
                .orElse(null);
    }

    @Override
    public CredentialListResult query(KapuaQuery query)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.CREDENTIAL, Actions.read, query.getScopeId()));

        final CredentialListResult credentials = txManager.execute(tx -> credentialRepository.query(tx, query));
        credentials.getItems().forEach(credential -> credential.setCredentialKey(null));
        return credentials;
    }

    @Override
    public long count(KapuaQuery query)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.CREDENTIAL, Actions.read, query.getScopeId()));
        return txManager.execute(tx -> credentialRepository.count(tx, query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId credentialId)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(credentialId, "credential.id");
        ArgumentValidator.notNull(scopeId, "credential.scopeId");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.CREDENTIAL, Actions.delete, scopeId));
        txManager.execute(tx -> credentialRepository.delete(tx, scopeId, credentialId));
    }

    @Override
    public CredentialListResult findByUserId(KapuaId scopeId, KapuaId userId)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(userId, "userId");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.CREDENTIAL, Actions.read, scopeId));

        // Do find
        final CredentialListResult credentials = txManager.execute(tx -> credentialRepository.findByUserId(tx, scopeId, userId));
        credentials.getItems().forEach(credential -> credential.setCredentialKey(null));
        return credentials;
    }

    @Override
    public CredentialListResult findByUserId(KapuaId scopeId, KapuaId userId, String credentialType)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(userId, "userId");
        ArgumentValidator.notEmptyOrNull(credentialType, credentialType);
        ArgumentValidator.notNull(availableCredentialAuthenticationType.get(credentialType), "credentialType");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.CREDENTIAL, Actions.read, scopeId));

        // Do find
        CredentialQuery credentialQuery = new CredentialQueryImpl(scopeId);

        credentialQuery.setPredicate(
                credentialQuery.andPredicate(
                        credentialQuery.attributePredicate(CredentialAttributes.USER_ID, userId),
                        credentialQuery.attributePredicate(CredentialAttributes.CREDENTIAL_TYPE, credentialType)
                )
        );

        CredentialListResult credentials = txManager.execute(tx -> credentialRepository.query(tx, credentialQuery));
        credentials.getItems().forEach(credential -> credential.setCredentialKey(null));
        return credentials;
    }

    @Override
    public Credential findByApiKey(String apiKey) throws KapuaException {
        int preLength = kapuaAuthenticationSetting.getInt(KapuaAuthenticationSettingKeys.AUTHENTICATION_CREDENTIAL_APIKEY_PRE_LENGTH);
        // Argument Validation
        ArgumentValidator.notEmptyOrNull(apiKey, "apiKey");
        ArgumentValidator.lengthRange(apiKey, preLength, null, "apiKey");
        // Do the find
        Credential credential = txManager.execute(tx -> {
            // Build search query
            String preSeparator = kapuaAuthenticationSetting.getString(KapuaAuthenticationSettingKeys.AUTHENTICATION_CREDENTIAL_APIKEY_PRE_SEPARATOR);
            String apiKeyPreValue = apiKey.substring(0, preLength).concat(preSeparator);

            // Build query
            KapuaQuery query = new CredentialQueryImpl();

            query.setPredicate(
                    query.andPredicate(
                            query.attributePredicate(CredentialAttributes.CREDENTIAL_TYPE, "API_KEY"),
                            query.attributePredicate(CredentialAttributes.CREDENTIAL_KEY, apiKeyPreValue, AttributePredicate.Operator.STARTS_WITH)
                    )
            );

            // Query
            CredentialListResult credentialListResult = credentialRepository.query(tx, query);
            // Parse the result
            return credentialListResult.getFirstItem();
        });

        ///FIXME: why the permission check here? it does not rollback!
        // Check Access
        if (credential != null) {
            authorizationService.checkPermission(permissionFactory.newPermission(Domains.CREDENTIAL, Actions.read, credential.getId()));
            credential.setCredentialKey(null);
        }

        return credential;
    }

    @Override
    public void unlock(KapuaId scopeId, KapuaId credentialId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(credentialId, "credentialId");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.CREDENTIAL, Actions.write, scopeId));

        txManager.execute(tx -> {
            Credential credential = credentialRepository.find(tx, scopeId, credentialId)
                    .orElseThrow(() -> new KapuaEntityNotFoundException(Credential.TYPE, credentialId));
            credential.setLoginFailures(0);
            credential.setFirstLoginFailure(null);
            credential.setLoginFailuresReset(null);
            credential.setLockoutReset(null);
            return credentialRepository.update(tx, credential);
        });
    }

    @Override
    public int getMinimumPasswordLength(KapuaId scopeId) throws KapuaException {
        return txManager.execute(tx -> accountPasswordLengthProvider.getMinimumPasswordLength(tx, scopeId));
    }

    //@ListenServiceEvent(fromAddress="account")
    //@ListenServiceEvent(fromAddress="user")
    public void onKapuaEvent(ServiceEvent kapuaEvent) throws KapuaException {
        if (kapuaEvent == null) {
            //service bus error. Throw some exception?
        }
        LOGGER.info("CredentialService: received kapua event from {}, operation {}", kapuaEvent.getService(), kapuaEvent.getOperation());
        if ("user".equals(kapuaEvent.getService()) && "delete".equals(kapuaEvent.getOperation())) {
            deleteCredentialByUserId(kapuaEvent.getScopeId(), kapuaEvent.getEntityId());
        } else if ("account".equals(kapuaEvent.getService()) && "delete".equals(kapuaEvent.getOperation())) {
            deleteCredentialByAccountId(kapuaEvent.getScopeId(), kapuaEvent.getEntityId());
        }
    }

    private void deleteCredentialByUserId(KapuaId scopeId, KapuaId userId) throws KapuaException {
        KapuaLocator locator = KapuaLocator.getInstance();
        CredentialFactory credentialFactory = locator.getFactory(CredentialFactory.class);

        CredentialQuery query = credentialFactory.newQuery(scopeId);
        query.setPredicate(query.attributePredicate(CredentialAttributes.USER_ID, userId));

        CredentialListResult credentialsToDelete = query(query);

        for (Credential c : credentialsToDelete.getItems()) {
            delete(c.getScopeId(), c.getId());
        }
    }

    private void deleteCredentialByAccountId(KapuaId scopeId, KapuaId accountId) throws KapuaException {
        KapuaLocator locator = KapuaLocator.getInstance();
        CredentialFactory credentialFactory = locator.getFactory(CredentialFactory.class);

        CredentialQuery query = credentialFactory.newQuery(accountId);

        CredentialListResult credentialsToDelete = query(query);

        for (Credential c : credentialsToDelete.getItems()) {
            delete(c.getScopeId(), c.getId());
        }
    }

    @Override
    public void validatePassword(KapuaId scopeId, String plainPassword) throws KapuaException {
        txManager.execute(tx -> {
            passwordValidator.validatePassword(tx, scopeId, plainPassword);
            return null;
        });
    }

    @Override
    public Credential findWithKey(KapuaId scopeId, KapuaId credentialId) throws KapuaException {
        // Validation of the fields
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(credentialId, "credentialId");

        //
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.CREDENTIAL, Actions.read, null));

        return txManager.execute(tx -> credentialRepository.find(tx, scopeId, credentialId))
                .orElse(null);
    }

    @Override
    public Credential adminResetUserPassword(KapuaId scopeId, KapuaId userId, PasswordResetRequest passwordResetRequest) throws KapuaException {
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(userId, "userId");
        ArgumentValidator.notNull(passwordResetRequest, "passwordResetRequest");
        ArgumentValidator.notNull(passwordResetRequest.getNewPassword(), "passwordResetRequest.netPassword");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.CREDENTIAL, Actions.read, scopeId));
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.CREDENTIAL, Actions.write, scopeId));

        return txManager.execute(tx -> passwordResetter.resetPassword(tx, scopeId, userId, false, passwordResetRequest));
    }

    @Override
    public Set<String> getAvailableCredentialTypes() {
        return availableCredentialAuthenticationType.keySet();
    }

    private boolean tryEditAdminFields(Credential updated, Credential current) {
        return updated.getLoginFailures() != current.getLoginFailures() ||
                updated.getFirstLoginFailure() != current.getFirstLoginFailure() ||
                updated.getLoginFailuresReset() != current.getLoginFailuresReset() ||
                updated.getLockoutReset() != current.getLockoutReset();
    }
}
