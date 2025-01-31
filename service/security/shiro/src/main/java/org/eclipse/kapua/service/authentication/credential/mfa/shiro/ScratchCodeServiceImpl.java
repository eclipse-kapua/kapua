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

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.credential.mfa.ScratchCodeListResult;
import org.eclipse.kapua.service.authentication.credential.mfa.ScratchCodeRepository;
import org.eclipse.kapua.service.authentication.credential.mfa.ScratchCodeService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ScratchCodeService} implementation.
 */
@Singleton
public class ScratchCodeServiceImpl implements ScratchCodeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScratchCodeServiceImpl.class);
    private final TxManager txManager;
    private final ScratchCodeRepository scratchCodeRepository;
    private final AuthorizationService authorizationService;

    public ScratchCodeServiceImpl(
            AuthorizationService authorizationService,
            TxManager txManager,
            ScratchCodeRepository scratchCodeRepository) {
        this.txManager = txManager;
        this.scratchCodeRepository = scratchCodeRepository;
        this.authorizationService = authorizationService;
    }

    @Override
    public ScratchCodeListResult findByMfaOptionId(KapuaId scopeId, KapuaId mfaOptionId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(mfaOptionId, ScratchCodeImpl_.MFA_OPTION_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.CREDENTIAL, Actions.read, scopeId));

        return txManager.execute(tx -> scratchCodeRepository.findByMfaOptionId(tx, scopeId, mfaOptionId));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId scratchCodeId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scratchCode.scopeId");
        ArgumentValidator.notNull(scratchCodeId, "scratchCode.id");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.CREDENTIAL, Actions.delete, scopeId));

        txManager.execute(tx -> scratchCodeRepository.delete(tx, scopeId, scratchCodeId));
    }
}
