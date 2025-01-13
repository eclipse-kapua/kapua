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

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.credential.mfa.ScratchCode;
import org.eclipse.kapua.service.authentication.credential.mfa.ScratchCodeFactory;

/**
 * {@link ScratchCodeFactory} implementation.
 */
@Singleton
public class ScratchCodeFactoryImpl implements ScratchCodeFactory {

    @Override
    public ScratchCode newEntity(KapuaId scopeId) {
        return new ScratchCodeImpl(scopeId);
    }

    @Override
    public ScratchCode newScratchCode(KapuaId scopeId, KapuaId mfaOptionId, String code) {
        return new ScratchCodeImpl(scopeId, mfaOptionId, code);
    }
}
