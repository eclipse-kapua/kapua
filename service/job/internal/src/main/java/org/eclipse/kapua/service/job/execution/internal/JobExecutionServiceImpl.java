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
package org.eclipse.kapua.service.job.execution.internal;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.job.execution.JobExecution;
import org.eclipse.kapua.service.job.execution.JobExecutionCreator;
import org.eclipse.kapua.service.job.execution.JobExecutionListResult;
import org.eclipse.kapua.service.job.execution.JobExecutionRepository;
import org.eclipse.kapua.service.job.execution.JobExecutionService;
import org.eclipse.kapua.storage.TxManager;

/**
 * {@link JobExecutionService} implementation
 *
 * @since 1.0.0
 */
@Singleton
public class JobExecutionServiceImpl implements JobExecutionService {

    private final AuthorizationService authorizationService;
    private final TxManager txManager;
    private final JobExecutionRepository jobExecutionRepository;

    public JobExecutionServiceImpl(
            AuthorizationService authorizationService,
            TxManager txManager,
            JobExecutionRepository jobExecutionRepository) {
        this.authorizationService = authorizationService;
        this.txManager = txManager;
        this.jobExecutionRepository = jobExecutionRepository;
    }

    @Override
    public JobExecution create(JobExecutionCreator jobExecutionCreator) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(jobExecutionCreator, "jobExecutionCreator");
        ArgumentValidator.notNull(jobExecutionCreator.getScopeId(), "jobExecutionCreator.scopeId");
        // Check access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.write, jobExecutionCreator.getScopeId()));
        JobExecution jobExecution = new JobExecutionImpl(jobExecutionCreator.getScopeId());
        jobExecution.setJobId(jobExecutionCreator.getJobId());
        jobExecution.setStartedOn(jobExecutionCreator.getStartedOn());
        jobExecution.setTargetIds(jobExecutionCreator.getTargetIds());
        // Do create
        return txManager.execute(tx -> jobExecutionRepository.create(tx, jobExecution));
    }

    @Override
    public JobExecution update(JobExecution jobExecution) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(jobExecution, "jobExecution");
        ArgumentValidator.notNull(jobExecution.getScopeId(), "jobExecution.scopeId");
        // Check access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.write, jobExecution.getScopeId()));

        return txManager.execute(tx -> jobExecutionRepository.update(tx, jobExecution));
    }

    @Override
    public JobExecution find(KapuaId scopeId, KapuaId jobExecutionId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(jobExecutionId, "jobExecutionId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.read, scopeId));
        // Do find
        return txManager.execute(tx -> jobExecutionRepository.find(tx, scopeId, jobExecutionId))
                .orElse(null);
    }

    @Override
    public JobExecutionListResult query(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.read, query.getScopeId()));
        // Do query
        return txManager.execute(tx -> jobExecutionRepository.query(tx, query));
    }

    @Override
    public long countByJobId(KapuaId scopeId, KapuaId jobId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(jobId, "jobId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.read, scopeId));
        // Do find
        return txManager.execute(tx -> jobExecutionRepository.countByJobId(tx, scopeId, jobId));
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.read, query.getScopeId()));
        // Do query
        return txManager.execute(tx -> jobExecutionRepository.count(tx, query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId jobExecutionId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(jobExecutionId, "jobExecutionId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.delete, scopeId));
        // Do delete
        txManager.execute(tx -> jobExecutionRepository.delete(tx, scopeId, jobExecutionId));
    }
}
