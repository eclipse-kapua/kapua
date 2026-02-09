/*******************************************************************************
 * Copyright (c) 2025, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.user.group.internal;

import org.eclipse.kapua.commons.model.AbstractKapuaNamedEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.user.group.UserGroup;
import org.eclipse.kapua.service.user.group.UserGroupCreator;

/**
 * {@link UserGroupCreator} implementation.
 *
 * @since 2.1.0
 */
public class UserGroupCreatorImpl extends AbstractKapuaNamedEntityCreator<UserGroup> implements UserGroupCreator {

    /**
     * Constructor.
     *
     * @param scopeId The {@link #getScopeId()}
     * @since 2.1.0
     */
    public UserGroupCreatorImpl(KapuaId scopeId) {
        super(scopeId);
    }

}
