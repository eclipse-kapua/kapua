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
package org.eclipse.kapua.service.job.internal;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.configuration.KapuaConfigurableServiceBase;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.job.engine.JobEngineService;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.job.JobCreator;
import org.eclipse.kapua.service.job.JobListResult;
import org.eclipse.kapua.service.job.JobRepository;
import org.eclipse.kapua.service.job.JobService;
import org.eclipse.kapua.service.scheduler.trigger.TriggerService;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link JobService} implementation
 *
 * @since 1.0.0
 */
@Singleton
public class JobServiceImpl extends KapuaConfigurableServiceBase implements JobService {

    private static final Logger LOG = LoggerFactory.getLogger(JobServiceImpl.class);

    private final JobEngineService jobEngineService;
    private final JobRepository jobRepository;
    private final TriggerService triggerService;

    /**
     * Default constructor for injection
     *
     * @param authorizationService
     *         The {@link AuthorizationService} instance
     * @param jobRepository
     * @param triggerService
     *         The {@link TriggerService} instance
     * @since 2.0.0
     */
    public JobServiceImpl(
            ServiceConfigurationManager serviceConfigurationManager,
            JobEngineService jobEngineService,
            AuthorizationService authorizationService,
            TxManager txManager,
            JobRepository jobRepository,
            TriggerService triggerService) {
        super(txManager, serviceConfigurationManager, Domains.JOB, authorizationService);
        this.jobEngineService = jobEngineService;
        this.jobRepository = jobRepository;
        this.triggerService = triggerService;
    }

    @Override
    public Job create(JobCreator creator) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(creator, "jobCreator");
        ArgumentValidator.notNull(creator.getScopeId(), "jobCreator.scopeId");
        ArgumentValidator.validateEntityName(creator.getName(), "jobCreator.name");
        // Check access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.write, creator.getScopeId()));
        return txManager.execute(tx -> {
            // Check entity limit
            serviceConfigurationManager.checkAllowedEntities(tx, creator.getScopeId(), "Jobs");
            // Check duplicate name
            if (jobRepository.countEntitiesWithNameInScope(tx, creator.getScopeId(), creator.getName()) > 0) {
                throw new KapuaDuplicateNameException(creator.getName());
            }

            Job jobImpl = new JobImpl(creator.getScopeId());
            jobImpl.setName(creator.getName());
            jobImpl.setDescription(creator.getDescription());
            // Do create
            return jobRepository.create(tx, jobImpl);
        });
    }

    @Override
    public Job update(Job job) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(job, "job");
        ArgumentValidator.notNull(job.getScopeId(), "job.scopeId");
        ArgumentValidator.validateEntityName(job.getName(), "job.name");
        // Check access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.write, job.getScopeId()));

        return txManager.execute(tx -> {
            // Check existence
            if (!jobRepository.find(tx, job.getScopeId(), job.getId()).isPresent()) {
                throw new KapuaEntityNotFoundException(Job.TYPE, job.getId());
            }
            // Check duplicate name
            if (jobRepository.countOtherEntitiesWithNameInScope(tx, job.getScopeId(), job.getId(), job.getName()) > 0) {
                throw new KapuaDuplicateNameException(job.getName());
            }
            // Do update
            return jobRepository.update(tx, job);
        });
    }

    @Override
    public Job find(KapuaId scopeId, KapuaId jobId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(jobId, KapuaEntityAttributes.ENTITY_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.read, scopeId));
        // Do find
        return txManager.execute(tx -> jobRepository.find(tx, scopeId, jobId))
                .orElse(null);
    }

    @Override
    public JobListResult query(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.read, query.getScopeId()));
        // Do query
        return txManager.execute(tx -> jobRepository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.read, query.getScopeId()));
        // Do query
        return txManager.execute(tx -> jobRepository.count(tx, query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId jobId) throws KapuaException {
        deleteInternal(scopeId, jobId, false);
    }

    @Override
    public void deleteForced(KapuaId scopeId, KapuaId jobId) throws KapuaException {
        deleteInternal(scopeId, jobId, true);
    }
    // Private methods

    /**
     * Deletes the {@link Job} like {@link #delete(KapuaId, KapuaId)}.
     * <p>
     * If {@code forced} is {@code true} {@link org.eclipse.kapua.service.authorization.permission.Permission} checked will be {@code job:delete:null}, and when invoking
     * {@link JobEngineService#cleanJobData(KapuaId, KapuaId)} any exception is logged and ignored.
     *
     * @param scopeId
     *         The {@link KapuaId} scopeId of the {@link Job}.
     * @param jobId
     *         The {@link KapuaId} of the {@link Job}.
     * @param forced
     *         Whether or not the {@link Job} must be forcibly deleted.
     * @throws KapuaException
     *         In case something bad happens.
     * @since 1.1.0
     */
    private void deleteInternal(KapuaId scopeId, KapuaId jobId, boolean forced) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(jobId, KapuaEntityAttributes.ENTITY_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.delete, forced ? null : scopeId));

        txManager.execute(tx -> {
            // Check existence
            if (!jobRepository.find(tx, scopeId, jobId).isPresent()) {
                throw new KapuaEntityNotFoundException(Job.TYPE, jobId);
            }

            try {
                KapuaSecurityUtils.doPrivileged(() -> {
                    // Do delete
                    triggerService.deleteAllByJobId(scopeId, jobId);
                    jobEngineService.cleanJobData(scopeId, jobId);
                });
            } catch (Exception e) {
                if (forced) {
                    LOG.warn("Error while cleaning Job data. Ignoring exception since delete is forced! Error: {}", e.getMessage());
                    LOG.debug("Error while cleaning Job data. Ignoring exception since delete is forced!", e);
                } else {
                    throw e;
                }
            }
            return jobRepository.delete(tx, scopeId, jobId);
        });
    }
}
