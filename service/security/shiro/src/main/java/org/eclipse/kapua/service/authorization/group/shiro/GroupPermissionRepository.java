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
package org.eclipse.kapua.service.authorization.group.shiro;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.group.GroupPermission;
import org.eclipse.kapua.service.authorization.group.GroupPermissionListResult;
import org.eclipse.kapua.storage.KapuaEntityRepository;
import org.eclipse.kapua.storage.TxContext;

public interface GroupPermissionRepository extends KapuaEntityRepository<GroupPermission, GroupPermissionListResult> {

    GroupPermissionListResult findByGroupId(TxContext txContext, KapuaId scopeId, KapuaId groupId) throws KapuaException;

}
