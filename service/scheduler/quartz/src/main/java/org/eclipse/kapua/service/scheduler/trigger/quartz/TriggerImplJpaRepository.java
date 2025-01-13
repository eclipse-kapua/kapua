/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.scheduler.trigger.quartz;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Root;

import org.eclipse.kapua.commons.jpa.JpaAwareTxContext;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaNamedEntityJpaRepository;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.scheduler.trigger.Trigger;
import org.eclipse.kapua.service.scheduler.trigger.TriggerListResult;
import org.eclipse.kapua.service.scheduler.trigger.TriggerRepository;
import org.eclipse.kapua.service.scheduler.trigger.definition.quartz.TriggerPropertyImpl;
import org.eclipse.kapua.service.scheduler.trigger.definition.quartz.TriggerPropertyImpl_;
import org.eclipse.kapua.storage.TxContext;

public class TriggerImplJpaRepository
        extends KapuaNamedEntityJpaRepository<Trigger, TriggerImpl, TriggerListResult>
        implements TriggerRepository {

    public TriggerImplJpaRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        super(TriggerImpl.class, Trigger.TYPE, () -> new TriggerListResult(), jpaRepoConfig);
    }

    @Override
    public void deleteAllByJobId(TxContext txContext, KapuaId scopeId, KapuaId jobId) {
        final javax.persistence.EntityManager em = JpaAwareTxContext.extractEntityManager(txContext);
        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaDelete<TriggerImpl> triggerQuery = cb.createCriteriaDelete(TriggerImpl.class);
        final Root<TriggerImpl> triggerRoot = triggerQuery.from(TriggerImpl.class);
        final ListJoin<TriggerImpl, TriggerPropertyImpl> join = triggerRoot.join(TriggerImpl_.triggerProperties, JoinType.LEFT);
        triggerQuery.where(
                // Find all the triggers that are associated with this job
                cb.and(
                        cb.equal(triggerRoot.get(TriggerImpl_.scopeId), KapuaEid.parseKapuaId(scopeId)),
                        cb.and(
                                cb.equal(join.get(TriggerPropertyImpl_.name), "jobId"),
                                cb.equal(join.get(TriggerPropertyImpl_.propertyValue), jobId.toCompactId()),
                                cb.equal(join.get(TriggerPropertyImpl_.propertyType), KapuaId.class.getName())
                        )
                )
        );
        em.createQuery(triggerQuery).executeUpdate();
    }

}
