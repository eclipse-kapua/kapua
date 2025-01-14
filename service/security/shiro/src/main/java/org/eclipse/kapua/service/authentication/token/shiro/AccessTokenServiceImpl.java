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
package org.eclipse.kapua.service.authentication.token.shiro;

import java.util.Date;
import java.util.Optional;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.event.ServiceEvent;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authentication.token.AccessToken;
import org.eclipse.kapua.service.authentication.token.AccessTokenAttributes;
import org.eclipse.kapua.service.authentication.token.AccessTokenCreator;
import org.eclipse.kapua.service.authentication.token.AccessTokenFactory;
import org.eclipse.kapua.service.authentication.token.AccessTokenListResult;
import org.eclipse.kapua.service.authentication.token.AccessTokenRepository;
import org.eclipse.kapua.service.authentication.token.AccessTokenService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public AccessTokenServiceImpl(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            TxManager txManager,
            AccessTokenRepository accessTokenRepository,
            AccessTokenFactory accessTokenFactory) {
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.txManager = txManager;
        this.accessTokenRepository = accessTokenRepository;
        this.accessTokenFactory = accessTokenFactory;
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
        final KapuaQuery query = new KapuaQuery(scopeId);
        query.setPredicate(query.attributePredicate(AccessTokenAttributes.USER_ID, userId));
        // Do query
        return query(query);
    }

    @Override
    public AccessToken findByTokenId(String tokenId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(tokenId, "tokenId");
        // Do find
        Optional<AccessToken> accessToken = txManager.execute(tx -> accessTokenRepository.findByTokenId(tx, tokenId));
        // Check Access
        if (accessToken.isPresent()) {
            authorizationService.checkPermission(permissionFactory.newPermission(Domains.ACCESS_TOKEN, Actions.read, accessToken.get().getScopeId()));
        }

        return accessToken
                .orElse(null);
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

        final KapuaQuery query = new KapuaQuery(scopeId);
        query.setPredicate(query.attributePredicate(AccessTokenAttributes.USER_ID, userId));

        AccessTokenListResult accessTokensToDelete = query(query);

        for (AccessToken at : accessTokensToDelete.getItems()) {
            delete(at.getScopeId(), at.getId());
        }
    }

    private void deleteAccessTokenByAccountId(KapuaId scopeId, KapuaId accountId) throws KapuaException {

        final KapuaQuery query = new KapuaQuery(scopeId);

        AccessTokenListResult accessTokensToDelete = query(query);

        for (AccessToken at : accessTokensToDelete.getItems()) {
            delete(at.getScopeId(), at.getId());
        }
    }
}
