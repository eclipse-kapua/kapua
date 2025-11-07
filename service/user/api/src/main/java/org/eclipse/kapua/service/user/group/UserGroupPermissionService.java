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
package org.eclipse.kapua.service.user.group;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.KapuaEntityService;
import org.eclipse.kapua.service.authorization.group.GroupPermission;
import org.eclipse.kapua.service.authorization.group.GroupPermissionCreator;
import org.eclipse.kapua.service.authorization.group.GroupPermissionListResult;
import org.eclipse.kapua.service.user.User;

/**
 * {@link User} {@link GroupPermission} {@link KapuaEntityService} definition.
 *
 * @since 2.1.0
 */
public interface UserGroupPermissionService extends KapuaEntityService<GroupPermission, GroupPermissionCreator> {

    GroupPermissionListResult findByGroupId(KapuaId scopeId, KapuaId userGroupId) throws KapuaException;

    @Override
    GroupPermissionListResult query(KapuaQuery query) throws KapuaException;
}
