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
package org.eclipse.kapua.service.authorization.group.shiro;

import org.eclipse.kapua.commons.model.query.AbstractKapuaQuery;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.access.AccessRoleQuery;
import org.eclipse.kapua.service.authorization.group.GroupRoleQuery;

/**
 * {@link AccessRoleQuery} implementation.
 *
 * @since 2.1.0
 */
public class GroupRoleQueryImpl extends AbstractKapuaQuery implements GroupRoleQuery {

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    public GroupRoleQueryImpl() {
        super();
    }

    /**
     * Constructor.
     *
     * @param scopeId The {@link #getScopeId()}.
     * @since 1.0.0
     */
    public GroupRoleQueryImpl(KapuaId scopeId) {
        this();
        setScopeId(scopeId);
    }
}
