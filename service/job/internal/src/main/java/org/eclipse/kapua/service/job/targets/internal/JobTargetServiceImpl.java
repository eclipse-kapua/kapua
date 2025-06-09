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
package org.eclipse.kapua.service.job.targets.internal;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaEntityUniquenessException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.job.JobRepository;
import org.eclipse.kapua.service.job.targets.JobTarget;
import org.eclipse.kapua.service.job.targets.JobTargetAttributes;
import org.eclipse.kapua.service.job.targets.JobTargetCreator;
import org.eclipse.kapua.service.job.targets.JobTargetFactory;
import org.eclipse.kapua.service.job.targets.JobTargetListResult;
import org.eclipse.kapua.service.job.targets.JobTargetRepository;
import org.eclipse.kapua.service.job.targets.JobTargetService;
import org.eclipse.kapua.service.job.targets.JobTargetStatus;
import org.eclipse.kapua.storage.TxManager;

/**
 * {@link JobTargetService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class JobTargetServiceImpl implements JobTargetService {

    private final AuthorizationService authorizationService;
    private final TxManager txManager;
    private final JobTargetRepository jobTargetRepository;
    private final JobTargetFactory jobTargetFactory;
    private final JobRepository jobRepository;

    public JobTargetServiceImpl(
            AuthorizationService authorizationService,
            TxManager txManager,
            JobTargetRepository jobTargetRepository,
            JobTargetFactory jobTargetFactory, JobRepository jobRepository) {
        this.authorizationService = authorizationService;
        this.txManager = txManager;
        this.jobTargetRepository = jobTargetRepository;
        this.jobTargetFactory = jobTargetFactory;
        this.jobRepository = jobRepository;
    }

    @Override
    public JobTarget create(JobTargetCreator jobTargetCreator) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(jobTargetCreator, "jobTargetCreator");
        ArgumentValidator.notNull(jobTargetCreator.getScopeId(), "jobTargetCreator.scopeId");
        ArgumentValidator.notNull(jobTargetCreator.getJobId(), "jobTargetCreator.jobId");
        ArgumentValidator.notNull(jobTargetCreator.getJobTargetId(), "jobTargetCreator.jobTargetId");
        // Check access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.write, jobTargetCreator.getScopeId()));
        return txManager.execute(tx -> {
            // Check Job Existing
            final Job job = jobRepository.find(tx, jobTargetCreator.getScopeId(), jobTargetCreator.getJobId())
                    .orElseThrow(() -> new KapuaEntityNotFoundException(Job.TYPE, jobTargetCreator.getJobId()));
            // Check duplicate
            KapuaQuery jobTargetQuery = new KapuaQuery(jobTargetCreator.getScopeId());
            jobTargetQuery.setPredicate(
                    jobTargetQuery.andPredicate(
                            jobTargetQuery.attributePredicate(JobTargetAttributes.JOB_ID, jobTargetCreator.getJobId()),
                            jobTargetQuery.attributePredicate(JobTargetAttributes.JOB_TARGET_ID, jobTargetCreator.getJobTargetId())
                    )
            );

            if (count(jobTargetQuery) > 0) {
                List<Map.Entry<String, Object>> uniquesFieldValues = new ArrayList<>();
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(JobTargetAttributes.SCOPE_ID, jobTargetCreator.getScopeId()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(JobTargetAttributes.JOB_ID, jobTargetCreator.getJobId()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(JobTargetAttributes.ENTITY_ID, jobTargetCreator.getJobTargetId()));

                throw new KapuaEntityUniquenessException(JobTarget.TYPE, uniquesFieldValues);
            }
            // Create JobTarget

            final JobTarget jobTarget = jobTargetFactory.newEntity(jobTargetCreator.getScopeId());
            jobTarget.setJobId(jobTargetCreator.getJobId());
            jobTarget.setJobTargetId(jobTargetCreator.getJobTargetId());
            jobTarget.setStepIndex(0);
            jobTarget.setStatus(JobTargetStatus.PROCESS_AWAITING);
            // Do create
            return jobTargetRepository.create(tx, jobTarget);
        });
    }

    @Override
    public JobTarget find(KapuaId scopeId, KapuaId jobTargetId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(jobTargetId, "jobTargetId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.write, scopeId));
        // Do find
        return txManager.execute(tx -> jobTargetRepository.find(tx, scopeId, jobTargetId))
                .orElse(null);
    }

    @Override
    public JobTargetListResult query(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.read, query.getScopeId()));
        // Do query
        return txManager.execute(tx -> jobTargetRepository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.read, query.getScopeId()));
        // Do query
        return txManager.execute(tx -> jobTargetRepository.count(tx, query));
    }

    @Override
    public JobTarget update(JobTarget jobTarget) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(jobTarget, "jobTarget");
        ArgumentValidator.notNull(jobTarget.getScopeId(), "jobTarget.scopeId");
        ArgumentValidator.notNull(jobTarget.getId(), "jobTarget.id");
        ArgumentValidator.notNull(jobTarget.getStepIndex(), "jobTarget.stepIndex");
        ArgumentValidator.notNull(jobTarget.getStatus(), "jobTarget.status");
        // Check access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.write, jobTarget.getScopeId()));
        // Check existence
        return txManager.execute(tx -> {
            if (!jobTargetRepository.find(tx, jobTarget.getScopeId(), jobTarget.getId()).isPresent()) {
                throw new KapuaEntityNotFoundException(jobTarget.getType(), jobTarget.getId());
            }
            // Do update
            return jobTargetRepository.update(tx, jobTarget);
        });
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId jobTargetId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(jobTargetId, "jobTargetId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.delete, scopeId));
        // Do delete
        txManager.execute(tx -> jobTargetRepository.delete(tx, scopeId, jobTargetId));
    }
}
