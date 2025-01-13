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
package org.eclipse.kapua.service.job.step.internal;

import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaNamedEntityJpaRepository;
import org.eclipse.kapua.commons.model.AbstractKapuaNamedEntity_;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.job.step.JobStep;
import org.eclipse.kapua.service.job.step.JobStepAttributes;
import org.eclipse.kapua.service.job.step.JobStepListResult;
import org.eclipse.kapua.service.job.step.JobStepRepository;
import org.eclipse.kapua.storage.TxContext;

public class JobStepImplJpaRepository
        extends KapuaNamedEntityJpaRepository<org.eclipse.kapua.service.job.step.JobStep, JobStepImpl, JobStepListResult>
        implements JobStepRepository {

    public JobStepImplJpaRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        super(JobStepImpl.class, JobStep.TYPE, () -> new JobStepListResult(), jpaRepoConfig);
    }

    @Override
    public long countOtherEntitiesWithNameInScope(TxContext tx, KapuaId scopeId, KapuaId excludedId, String name, KapuaId jobId) {
        return doCount(tx, name,
                (cb, r) -> mapScopeIdToCriteria(scopeId, cb, r),
                (cb, r) -> cb.notEqual(r.get(AbstractKapuaNamedEntity_.ID), KapuaEid.parseKapuaId(excludedId)),
                (cb, r) -> cb.equal(r.get(JobStepAttributes.JOB_ID), KapuaEid.parseKapuaId(jobId))
        );
    }

    @Override
    public long countEntitiesWithNameInScope(TxContext tx, KapuaId scopeId, String name, KapuaId jobId) {
        return doCount(tx, name,
                (cb, r) -> mapScopeIdToCriteria(scopeId, cb, r),
                (cb, r) -> cb.equal(r.get(JobStepAttributes.JOB_ID), KapuaEid.parseKapuaId(jobId))
        );
    }
}
