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
package org.eclipse.kapua.service.authorization.role.shiro;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.JpaAwareTxContext;
import org.eclipse.kapua.commons.jpa.KapuaEntityJpaRepository;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.permission.shiro.PermissionImpl_;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.eclipse.kapua.service.authorization.role.RolePermissionListResult;
import org.eclipse.kapua.service.authorization.role.RolePermissionRepository;
import org.eclipse.kapua.storage.TxContext;

public class RolePermissionImplJpaRepository
        extends KapuaEntityJpaRepository<RolePermission, RolePermissionImpl, RolePermissionListResult>
        implements RolePermissionRepository {

    public RolePermissionImplJpaRepository(KapuaJpaRepositoryConfiguration configuration) {
        super(RolePermissionImpl.class, RolePermission.TYPE, () -> new RolePermissionListResult(), configuration);
    }

    @Override
    public RolePermissionListResult findByRoleId(TxContext tx, KapuaId scopeId, KapuaId roleId) throws KapuaException {
        final RolePermissionListResult res = listSupplier.get();
        res.addItems(doFindAllByField(tx, scopeId, RolePermissionImpl_.ROLE_ID, roleId));
        return res;
    }

    @Override
    public RolePermissionListResult deleteAllByDomainAndAction(TxContext tx, String domainName, Actions actionToDelete) throws KapuaException {
        final EntityManager em = JpaAwareTxContext.extractEntityManager(tx);
        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<RolePermissionImpl> listQuery = cb.createQuery(RolePermissionImpl.class);
        final Root<RolePermissionImpl> listRoot = listQuery.from(RolePermissionImpl.class);
        listQuery.where(
                // Find all the triggers that are associated with this job
                cb.and(
                        cb.equal(listRoot.get(RolePermissionImpl_.permission).get(PermissionImpl_.domain), domainName),
                        cb.equal(listRoot.get(RolePermissionImpl_.permission).get(PermissionImpl_.action), actionToDelete)
                )
        );
        final List<RolePermissionImpl> resultList = em.createQuery(listQuery).getResultList();

        if (!resultList.isEmpty()) {
            final CriteriaDelete<RolePermissionImpl> deleteQuery = cb.createCriteriaDelete(RolePermissionImpl.class);
            final Root<RolePermissionImpl> deleteRoot = deleteQuery.from(RolePermissionImpl.class);
            deleteQuery.where(deleteRoot.get(RolePermissionImpl_.id).in(resultList.stream().map(r -> r.getId()).map(KapuaEid::parseKapuaId).collect(Collectors.toList())));
            em.createQuery(deleteQuery).executeUpdate();
        }
        final RolePermissionListResult res = new RolePermissionListResult();
        res.addItems(resultList);
        return res;
    }
}
