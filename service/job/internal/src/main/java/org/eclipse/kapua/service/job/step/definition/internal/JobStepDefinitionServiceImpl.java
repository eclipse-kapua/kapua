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
package org.eclipse.kapua.service.job.step.definition.internal;

import java.util.Optional;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaDuplicateNameInAnotherAccountError;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinition;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionAttributes;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionCreator;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionListResult;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionQuery;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionRepository;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionService;
import org.eclipse.kapua.storage.TxManager;

/**
 * {@link JobStepDefinitionService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class JobStepDefinitionServiceImpl implements JobStepDefinitionService {

    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final TxManager txManager;
    private final JobStepDefinitionRepository repository;

    public JobStepDefinitionServiceImpl(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            TxManager txManager,
            JobStepDefinitionRepository repository) {
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.txManager = txManager;
        this.repository = repository;
    }

    @Override
    public JobStepDefinition create(JobStepDefinitionCreator stepDefinitionCreator) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(stepDefinitionCreator, "stepDefinitionCreator");
        ArgumentValidator.notNull(stepDefinitionCreator.getScopeId(), "stepDefinitionCreator.scopeId");
        ArgumentValidator.notNull(stepDefinitionCreator.getStepType(), "stepDefinitionCreator.stepType");
        ArgumentValidator.validateEntityName(stepDefinitionCreator.getName(), "stepDefinitionCreator.name");
        ArgumentValidator.notEmptyOrNull(stepDefinitionCreator.getProcessorName(), "stepDefinitionCreator.processorName");
        // Check access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.JOB, Actions.write, null));

        return txManager.execute(tx -> {
            // Check duplicate name
            if (repository.countEntitiesWithName(tx, stepDefinitionCreator.getName()) > 0) {
                throw new KapuaDuplicateNameInAnotherAccountError(stepDefinitionCreator.getName());
            }
            // Create JobStepDefinition

            JobStepDefinitionImpl jobStepDefinitionImpl = new JobStepDefinitionImpl(stepDefinitionCreator.getScopeId());
            jobStepDefinitionImpl.setName(stepDefinitionCreator.getName());
            jobStepDefinitionImpl.setDescription(stepDefinitionCreator.getDescription());
            jobStepDefinitionImpl.setStepType(stepDefinitionCreator.getStepType());
            jobStepDefinitionImpl.setReaderName(stepDefinitionCreator.getReaderName());
            jobStepDefinitionImpl.setProcessorName(stepDefinitionCreator.getProcessorName());
            jobStepDefinitionImpl.setWriterName(stepDefinitionCreator.getWriterName());
            jobStepDefinitionImpl.setStepProperties(stepDefinitionCreator.getStepProperties());
            // Do create
            return repository.create(tx, jobStepDefinitionImpl);
        });
    }

    @Override
    public JobStepDefinition update(JobStepDefinition jobStepDefinition) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(jobStepDefinition, "stepDefinition");
        ArgumentValidator.notNull(jobStepDefinition.getScopeId(), "stepDefinition.scopeId");
        ArgumentValidator.notNull(jobStepDefinition.getStepType(), "jobStepDefinition.stepType");
        ArgumentValidator.validateEntityName(jobStepDefinition.getName(), "jobStepDefinition.name");
        ArgumentValidator.notEmptyOrNull(jobStepDefinition.getProcessorName(), "jobStepDefinition.processorName");

        // Check access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.JOB, Actions.write, null));

        return txManager.execute(tx -> {
            // Check duplicate name
            if (repository.countOtherEntitiesWithNameInScope(
                    tx, jobStepDefinition.getScopeId(), jobStepDefinition.getId(), jobStepDefinition.getName()) > 0) {
                throw new KapuaDuplicateNameInAnotherAccountError(jobStepDefinition.getName());
            }

            // Do Update
            return repository.update(tx, jobStepDefinition);
        });
    }

    @Override
    public JobStepDefinition find(KapuaId scopeId, KapuaId stepDefinitionId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(stepDefinitionId, "stepDefinitionId");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.JOB, Actions.read, scopeId));

        final JobStepDefinitionQuery query = new JobStepDefinitionQuery(scopeId);
        query.setPredicate(query.attributePredicate(JobStepDefinitionAttributes.ENTITY_ID, stepDefinitionId));

        // Do find
        return txManager.execute(tx -> repository.query(tx, query)).getFirstItem();
    }

    @Override
    public JobStepDefinition findByName(String name) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(name, "name");
        // Do find
        return txManager.execute(tx -> {

            Optional<JobStepDefinition> jobStepDefinition = repository.findByName(tx, name);
            if (jobStepDefinition.isPresent()) {
                // Check Access
                authorizationService.checkPermission(permissionFactory.newPermission(Domains.JOB, Actions.read, jobStepDefinition.get().getScopeId()));
            }
            return jobStepDefinition;
        }).orElse(null);
    }

    @Override
    public JobStepDefinitionListResult query(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.JOB, Actions.read, query.getScopeId()));
        // Do query
        return txManager.execute(tx -> repository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.JOB, Actions.read, query.getScopeId()));
        // Do query
        return txManager.execute(tx -> repository.count(tx, query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId stepDefinitionId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(stepDefinitionId, "stepDefinitionId");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.JOB, Actions.delete, null));
        // Do delete
        txManager.execute(tx -> repository.delete(tx, scopeId, stepDefinitionId));
    }
}
