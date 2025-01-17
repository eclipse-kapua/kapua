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

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.access.AccessPermissionFactory;

/**
 * {@link AccessPermissionFactory} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class AccessPermissionFactoryImpl implements AccessPermissionFactory {

    @Override
    public AccessPermission newEntity(KapuaId scopeId) {
        return new AccessPermissionImpl(scopeId);
    }

    @Override
    public AccessPermission clone(AccessPermission accessPermission) {
        return new AccessPermissionImpl(accessPermission);
    }
}
