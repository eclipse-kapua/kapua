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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.service.internal.cache.EntityCache;
import org.eclipse.kapua.commons.storage.KapuaEntityRepositoryCachingWrapper;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.access.AccessPermissionAttributes;
import org.eclipse.kapua.service.authorization.access.AccessPermissionListResult;
import org.eclipse.kapua.service.authorization.access.AccessPermissionRepository;
import org.eclipse.kapua.storage.TxContext;

public class CachingAccessPermissionRepository
        extends KapuaEntityRepositoryCachingWrapper<AccessPermission, AccessPermissionListResult>
        implements AccessPermissionRepository {

    private final AccessPermissionRepository wrapped;

    public CachingAccessPermissionRepository(AccessPermissionRepository wrapped, EntityCache entityCache) {
        super(wrapped, entityCache);
        this.wrapped = wrapped;
    }

    @Override
    public AccessPermission create(TxContext tx, AccessPermission entity) throws KapuaException {
        final AccessPermission created = super.create(tx, entity);
        entityCache.removeList(created.getScopeId(), created.getAccessInfoId());
        return created;
    }

    @Override
    public AccessPermission delete(TxContext tx, KapuaId scopeId, KapuaId entityId) throws KapuaException {

        final AccessPermission deleted = super.delete(tx, scopeId, entityId);
        if (deleted != null) {
            entityCache.removeList(scopeId, deleted.getAccessInfoId());
        }
        return deleted;
    }

    @Override
    public AccessPermissionListResult findByAccessInfoId(TxContext tx, KapuaId scopeId, KapuaId accessInfoId) throws KapuaException {
        AccessPermissionListResult listResult = (AccessPermissionListResult) entityCache.getList(scopeId, accessInfoId);
        if (listResult == null) {
            // Build query
            final KapuaQuery query = new KapuaQuery(scopeId);
            query.setPredicate(query.attributePredicate(AccessPermissionAttributes.ACCESS_INFO_ID, accessInfoId));

            listResult = wrapped.findByAccessInfoId(tx, scopeId, accessInfoId);
            entityCache.putList(scopeId, accessInfoId, listResult);
        }
        return listResult;
    }

    @Override
    public AccessPermissionListResult deleteAllByDomainAndAction(TxContext tx, String domainEntryName, Actions actionToDelete) throws KapuaException {
        final AccessPermissionListResult removed = wrapped.deleteAllByDomainAndAction(tx, domainEntryName, actionToDelete);
        if (!removed.isEmpty()) {
            removed.getItems().forEach(item -> entityCache.removeList(item.getScopeId(), item.getAccessInfoId()));
        }
        return removed;
    }
}
