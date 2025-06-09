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
package org.eclipse.kapua.extras.migrator.encryption.job;

import java.util.Optional;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaNamedEntityJpaRepository;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.job.step.JobStep;
import org.eclipse.kapua.service.job.step.JobStepListResult;
import org.eclipse.kapua.service.job.step.JobStepRepository;
import org.eclipse.kapua.service.job.step.definition.JobStepProperty;
import org.eclipse.kapua.storage.TxContext;

public class JobStepMigratorJpaRepository
        extends KapuaNamedEntityJpaRepository<JobStep, JobStepMigrator, JobStepListResult>
        implements JobStepRepository {

    public JobStepMigratorJpaRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        super(JobStepMigrator.class, JobStep.TYPE, () -> new JobStepListResult(), jpaRepoConfig);
    }

    @Override
    public JobStep update(TxContext tx, JobStep updatedEntity) throws KapuaException {
        JobStepMigrator jobStepImpl = (JobStepMigrator) updatedEntity;

        for (JobStepProperty jobStepProperty : jobStepImpl.getStepProperties()) {
            jobStepProperty.setPropertyValue(jobStepProperty.getPropertyValue());
        }
        return super.update(tx, updatedEntity);
    }

    @Override
    public Optional<JobStep> find(TxContext tx, KapuaId scopeId, KapuaId entityId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JobStep create(TxContext tx, JobStep entity) throws KapuaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public JobStep delete(TxContext tx, KapuaId scopeId, KapuaId entityId) throws KapuaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<JobStep> findByName(TxContext tx, String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<JobStep> findByName(TxContext tx, KapuaId scopeId, String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long countOtherEntitiesWithNameInScope(TxContext tx, KapuaId scopeId, KapuaId jobStepId, String name, KapuaId jobId) throws KapuaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long countEntitiesWithNameInScope(TxContext tx, KapuaId scopeId, String name, KapuaId jobId) throws KapuaException {
        throw new UnsupportedOperationException();
    }
}
