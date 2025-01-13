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
package org.eclipse.kapua.service.authorization.access.shiro;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfoCreator;
import org.eclipse.kapua.service.authorization.access.AccessInfoFactory;
import org.eclipse.kapua.service.authorization.access.AccessInfoQuery;

/**
 * {@link AccessInfoFactory} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class AccessInfoFactoryImpl implements AccessInfoFactory {

    @Override
    public AccessInfo newEntity(KapuaId scopeId) {
        return new AccessInfoImpl();
    }

    @Override
    public AccessInfoCreator newCreator(KapuaId scopeId) {
        return new AccessInfoCreatorImpl(scopeId);
    }

    @Override
    public AccessInfoQuery newQuery(KapuaId scopeId) {
        return new AccessInfoQueryImpl(scopeId);
    }

    @Override
    public AccessInfo clone(AccessInfo accessInfo) {
        try {
            return new AccessInfoImpl(accessInfo);
        } catch (Exception e) {
            throw new KapuaEntityCloneException(e, AccessInfo.TYPE, accessInfo);
        }
    }
}
