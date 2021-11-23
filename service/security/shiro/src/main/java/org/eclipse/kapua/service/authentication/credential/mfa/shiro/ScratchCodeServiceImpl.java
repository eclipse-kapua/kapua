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

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.EntityManager;
import org.eclipse.kapua.commons.service.internal.AbstractKapuaService;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.KapuaExceptionUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.locator.KapuaProvider;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.predicate.QueryPredicate;
import org.eclipse.kapua.service.authentication.AuthenticationDomains;
import org.eclipse.kapua.service.authentication.credential.mfa.KapuaExistingScratchCodesException;
import org.eclipse.kapua.service.authentication.credential.mfa.ScratchCode;
import org.eclipse.kapua.service.authentication.credential.mfa.ScratchCodeAttributes;
import org.eclipse.kapua.service.authentication.credential.mfa.ScratchCodeCreator;
import org.eclipse.kapua.service.authentication.credential.mfa.ScratchCodeFactory;
import org.eclipse.kapua.service.authentication.credential.mfa.ScratchCodeListResult;
import org.eclipse.kapua.service.authentication.credential.mfa.ScratchCodeQuery;
import org.eclipse.kapua.service.authentication.credential.mfa.ScratchCodeService;
import org.eclipse.kapua.service.authentication.mfa.MfaAuthenticator;
import org.eclipse.kapua.service.authentication.shiro.AuthenticationEntityManagerFactory;
import org.eclipse.kapua.service.authentication.shiro.mfa.MfaAuthenticatorServiceLocator;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * {@link ScratchCodeService} implementation.
 */
@KapuaProvider
public class ScratchCodeServiceImpl extends AbstractKapuaService implements ScratchCodeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScratchCodeServiceImpl.class);

    private static final MfaAuthenticatorServiceLocator MFA_AUTH_SERVICE_LOCATOR = MfaAuthenticatorServiceLocator.getInstance();
    private static final MfaAuthenticator MFA_AUTHENTICATOR = MFA_AUTH_SERVICE_LOCATOR.getMfaAuthenticator();

    public ScratchCodeServiceImpl() {
        super(AuthenticationEntityManagerFactory.getInstance());
    }

    @Override
    public ScratchCode create(ScratchCodeCreator scratchCodeCreator) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(scratchCodeCreator, "scratchCodeCreator");
        ArgumentValidator.notNull(scratchCodeCreator.getScopeId(), "scratchCodeCreator.scopeId");
        ArgumentValidator.notNull(scratchCodeCreator.getMfaOptionId(), "scratchCodeCreator.mfaOptionId");
        ArgumentValidator.notEmptyOrNull(scratchCodeCreator.getCode(), "scratchCodeCreator.code");

        //
        // Check access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(AuthenticationDomains.CREDENTIAL_DOMAIN, Actions.write,
                scratchCodeCreator.getScopeId()));

        //
        // Do create
        ScratchCode scratchCode = null;
        EntityManager em = AuthenticationEntityManagerFactory.getEntityManager();
        try {
            em.beginTransaction();

            //
            // Do pre persist magic on key values
            String fullKey = scratchCodeCreator.getCode();
            scratchCode = ScratchCodeDAO.create(em, scratchCodeCreator);
            scratchCode = ScratchCodeDAO.find(em, scratchCode.getScopeId(), scratchCode.getId());
            em.commit();

            // Do post persist magic on key values
            scratchCode.setCode(fullKey);
        } catch (Exception pe) {
            em.rollback();
            throw KapuaExceptionUtils.convertPersistenceException(pe);
        } finally {
            em.close();
        }

        return scratchCode;
    }

    @Override
    public ScratchCode update(ScratchCode scratchCode) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(scratchCode, "scratchCode");
        ArgumentValidator.notNull(scratchCode.getId(), "scratchCode.id");
        ArgumentValidator.notNull(scratchCode.getScopeId(), "scratchCode.scopeId");
        ArgumentValidator.notNull(scratchCode.getMfaOptionId(), "scratchCode.mfaOptionId");
        ArgumentValidator.notEmptyOrNull(scratchCode.getCode(), "scratchCode.code");

        //
        // Check access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(AuthenticationDomains.CREDENTIAL_DOMAIN, Actions.write, scratchCode.getScopeId()));

        return entityManagerSession.doTransactedAction(em -> {
            ScratchCode currentscratchCode = ScratchCodeDAO.find(em, scratchCode.getScopeId(), scratchCode.getId());

            if (currentscratchCode == null) {
                throw new KapuaEntityNotFoundException(ScratchCode.TYPE, scratchCode.getId());
            }

            // Passing attributes??
            return ScratchCodeDAO.update(em, scratchCode);
        });
    }

    @Override
    public ScratchCode find(KapuaId scopeId, KapuaId scratchCodeId) throws KapuaException {
        // Validation of the fields
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(scratchCodeId, "scratchCodeId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(AuthenticationDomains.CREDENTIAL_DOMAIN, Actions.read, scopeId));

        return entityManagerSession.doAction(em -> ScratchCodeDAO.find(em, scopeId, scratchCodeId));
    }

    @Override
    public ScratchCodeListResult query(KapuaQuery query) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(query, "query");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(AuthenticationDomains.CREDENTIAL_DOMAIN, Actions.read, query.getScopeId()));

        return entityManagerSession.doAction(em -> ScratchCodeDAO.query(em, query));
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(query, "query");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(AuthenticationDomains.CREDENTIAL_DOMAIN, Actions.read, query.getScopeId()));

        return entityManagerSession.doAction(em -> ScratchCodeDAO.count(em, query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId scratchCodeId) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(scratchCodeId, "scratchCode.id");
        ArgumentValidator.notNull(scopeId, "scratchCode.scopeId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(AuthenticationDomains.CREDENTIAL_DOMAIN, Actions.delete, scopeId));

        entityManagerSession.doTransactedAction(em -> {
            if (ScratchCodeDAO.find(em, scopeId, scratchCodeId) == null) {
                throw new KapuaEntityNotFoundException(ScratchCode.TYPE, scratchCodeId);
            }
            return ScratchCodeDAO.delete(em, scopeId, scratchCodeId);
        });
    }

    @Override
    public ScratchCodeListResult createAllScratchCodes(ScratchCodeCreator scratchCodeCreator) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(scratchCodeCreator, "scratchCodeCreator");
        ArgumentValidator.notNull(scratchCodeCreator.getScopeId(), "scratchCodeCreator.scopeId");
        ArgumentValidator.notNull(scratchCodeCreator.getMfaOptionId(), "scratchCodeCreator.mfaOptionId");

        List<String> codes = MFA_AUTHENTICATOR.generateCodes();
        ScratchCodeListResult scratchCodeListResult = new ScratchCodeListResultImpl();

        //
        // Check existing ScratchCodes
        ScratchCodeListResult existingScratchCodeListResult = findByMfaOptionId(scratchCodeCreator.getScopeId(), scratchCodeCreator.getMfaOptionId());
        if (!existingScratchCodeListResult.isEmpty()) {
            throw new KapuaExistingScratchCodesException();
        }

        for (String code : codes) {
            scratchCodeCreator.setCode(code);
            ScratchCode scratchCode = create(scratchCodeCreator);
            scratchCodeListResult.addItem(scratchCode);
        }

        return scratchCodeListResult;
    }

    @Override
    public ScratchCodeListResult findByMfaOptionId(KapuaId scopeId, KapuaId mfaOptionId) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(mfaOptionId, ScratchCodeAttributes.MFA_OPTION_ID);

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(AuthenticationDomains.CREDENTIAL_DOMAIN, Actions.read, scopeId));

        //
        // Build query
        ScratchCodeQuery query = new ScratchCodeQueryImpl(scopeId);
        QueryPredicate predicate = query.attributePredicate(ScratchCodeAttributes.MFA_OPTION_ID, mfaOptionId);
        query.setPredicate(predicate);

        //
        // Query and return result
        return query(query);
    }

    private void deleteScratchCodeByMfaOptionId(KapuaId scopeId, KapuaId mfaOptionId) throws KapuaException {
        KapuaLocator locator = KapuaLocator.getInstance();
        ScratchCodeFactory scratchCodeFactory = locator.getFactory(ScratchCodeFactory.class);

        ScratchCodeQuery query = scratchCodeFactory.newQuery(scopeId);
        query.setPredicate(query.attributePredicate(ScratchCodeAttributes.MFA_OPTION_ID, mfaOptionId));

        ScratchCodeListResult scratchCodesToDelete = query(query);

        for (ScratchCode c : scratchCodesToDelete.getItems()) {
            delete(c.getScopeId(), c.getId());
        }
    }

    private void deleteScratchCodeByAccountId(KapuaId scopeId, KapuaId accountId) throws KapuaException {
        KapuaLocator locator = KapuaLocator.getInstance();
        ScratchCodeFactory scratchCodeFactory = locator.getFactory(ScratchCodeFactory.class);

        ScratchCodeQuery query = scratchCodeFactory.newQuery(accountId);

        ScratchCodeListResult scratchCodesToDelete = query(query);

        for (ScratchCode c : scratchCodesToDelete.getItems()) {
            delete(c.getScopeId(), c.getId());
        }
    }

}
