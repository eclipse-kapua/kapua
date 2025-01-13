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

import org.eclipse.kapua.model.KapuaEntityFactory;
import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link ScratchCode} {@link KapuaEntityFactory} definition.
 *
 * @see KapuaEntityFactory
 * @since 1.3.0
 */
public interface ScratchCodeFactory extends KapuaObjectFactory {

    /**
     * Instantiates a new {@link ScratchCode}.
     *
     * @param scopeId
     *         The {@link ScratchCode#getScopeId()}
     * @return The newly instantiated {@link ScratchCode}
     * @since 1.3.0
     */
    ScratchCode newEntity(KapuaId scopeId);

    /**
     * Instantiates a new {@link ScratchCode}.
     *
     * @param scopeId
     *         The {@link ScratchCode#getScopeId()}
     * @param mfaOptionId
     *         The {@link MfaOption#getId()}
     * @param code
     *         The {@link ScratchCode#getCode()}.
     * @return The newly instantiated {@link ScratchCode}
     * @since 1.3.0
     */
    ScratchCode newScratchCode(KapuaId scopeId, KapuaId mfaOptionId, String code);
}
