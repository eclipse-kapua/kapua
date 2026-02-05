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
package org.eclipse.kapua.service.authentication.credential.mfa;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaEntityService;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.KapuaUpdatableEntityService;

/**
 * {@link MfaOption} {@link KapuaService} definition.
 *
 * @since 1.3.0
 */
public interface MfaOptionService extends KapuaEntityService<MfaOption, MfaOptionCreator>,
        KapuaUpdatableEntityService<MfaOption> {

    /**
     * Return the {@link MfaOption} result looking by user identifier (and also scope identifier)
     *
     * @param scopeId The User#getScopeId()}.
     * @param userId  The User#getId()}
     * @return The {@link MfaOption} of the User, if found.
     * @throws KapuaException
     * @since 1.3.0
     */
    MfaOption findByUserId(KapuaId scopeId, KapuaId userId) throws KapuaException;

    /**
     * Enables the trust machine for a User
     *
     * @param scopeId The User#getScopeId
     * @param userId  The User#getId
     * @return the value of the {@link MfaOption#getTrustKey()} in plain text
     * @since 1.3.0
     */
    String enableTrust(KapuaId scopeId, KapuaId userId) throws KapuaException;

    /**
     * Disables the trust machine for the given {@link MfaOption}.
     *
     * @param scopeId     The {@link MfaOption#getScopeId()}
     * @param mfaOptionId The {@link MfaOption#getId()}
     * @since 1.3.0
     */
    void disableTrust(KapuaId scopeId, KapuaId mfaOptionId) throws KapuaException;

    /**
     * Disables the trust machine of a User
     *
     * @param scopeId The User#getScopeId
     * @param userId  The User#getId
     * @since 1.3.0
     */
    void disableTrustByUserId(KapuaId scopeId, KapuaId userId) throws KapuaException;

    void deleteByUserId(KapuaId scopeId, KapuaId userId) throws KapuaException;

    boolean validateMfaCredentials(KapuaId scopeId, KapuaId userId, String tokenAuthenticationCode, String tokenTrustKey) throws KapuaException;
}
