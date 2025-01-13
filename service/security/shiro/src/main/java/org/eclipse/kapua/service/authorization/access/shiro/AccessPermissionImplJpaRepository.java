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
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.access.AccessPermissionAttributes;
import org.eclipse.kapua.service.authorization.access.AccessPermissionListResult;
import org.eclipse.kapua.service.authorization.access.AccessPermissionQuery;
import org.eclipse.kapua.service.authorization.access.AccessPermissionRepository;
import org.eclipse.kapua.service.authorization.permission.shiro.PermissionImpl_;
import org.eclipse.kapua.storage.TxContext;

public class AccessPermissionImplJpaRepository
        extends KapuaEntityJpaRepository<AccessPermission, AccessPermissionImpl, AccessPermissionListResult>
        implements AccessPermissionRepository {

    public AccessPermissionImplJpaRepository(KapuaJpaRepositoryConfiguration configuration) {
        super(AccessPermissionImpl.class, AccessPermission.TYPE, () -> new AccessPermissionListResult(), configuration);
    }

    @Override
    public AccessPermissionListResult findByAccessInfoId(TxContext tx, KapuaId scopeId, KapuaId accessInfoId) throws KapuaException {
        AccessPermissionQuery query = new AccessPermissionQueryImpl(scopeId);
        query.setPredicate(query.attributePredicate(AccessPermissionAttributes.ACCESS_INFO_ID, accessInfoId));
        return this.query(tx, query);
    }

    @Override
    public AccessPermissionListResult deleteAllByDomainAndAction(TxContext tx, String domainName, Actions actionToDelete) throws KapuaException {
        final EntityManager em = JpaAwareTxContext.extractEntityManager(tx);
        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<AccessPermissionImpl> listQuery = cb.createQuery(AccessPermissionImpl.class);
        final Root<AccessPermissionImpl> listRoot = listQuery.from(AccessPermissionImpl.class);
        listQuery.where(
                // Find all the triggers that are associated with this job
                cb.and(
                        cb.equal(listRoot.get(AccessPermissionImpl_.permission).get(PermissionImpl_.domain), domainName),
                        cb.equal(listRoot.get(AccessPermissionImpl_.permission).get(PermissionImpl_.action), actionToDelete)
                )
        );
        final List<AccessPermissionImpl> resultList = em.createQuery(listQuery).getResultList();

        if (!resultList.isEmpty()) {
            final CriteriaDelete<AccessPermissionImpl> deleteQuery = cb.createCriteriaDelete(AccessPermissionImpl.class);
            final Root<AccessPermissionImpl> deleteRoot = deleteQuery.from(AccessPermissionImpl.class);
            deleteQuery.where(deleteRoot.get(AccessPermissionImpl_.id).in(resultList.stream().map(r -> r.getId()).map(KapuaEid::parseKapuaId).collect(Collectors.toList())));
            em.createQuery(deleteQuery).executeUpdate();
        }
        final AccessPermissionListResult res = new AccessPermissionListResult();
        res.addItems(resultList);
        return res;
    }
}
