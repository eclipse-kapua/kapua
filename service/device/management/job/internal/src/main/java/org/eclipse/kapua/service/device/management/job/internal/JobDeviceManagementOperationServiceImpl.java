/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.job.internal;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityUniquenessException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperation;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperationAttributes;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperationCreator;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperationFactory;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperationListResult;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperationRepository;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperationService;
import org.eclipse.kapua.storage.TxManager;

/**
 * {@link JobDeviceManagementOperationService} implementation
 *
 * @since 1.1.0
 */
@Singleton
public class JobDeviceManagementOperationServiceImpl
        implements JobDeviceManagementOperationService {

    private final JobDeviceManagementOperationFactory entityFactory;
    private final AuthorizationService authorizationService;
    private final TxManager txManager;
    private final JobDeviceManagementOperationRepository repository;

    @Inject
    public JobDeviceManagementOperationServiceImpl(
            JobDeviceManagementOperationFactory entityFactory,
            AuthorizationService authorizationService,
            TxManager txManager,
            JobDeviceManagementOperationRepository repository) {
        this.entityFactory = entityFactory;
        this.authorizationService = authorizationService;
        this.txManager = txManager;
        this.repository = repository;
    }

    @Override
    public JobDeviceManagementOperation create(JobDeviceManagementOperationCreator jobDeviceManagementOperationCreator) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(jobDeviceManagementOperationCreator, "jobDeviceManagementOperationCreator");
        ArgumentValidator.notNull(jobDeviceManagementOperationCreator.getScopeId(), "jobDeviceManagementOperationCreator.scopeId");
        ArgumentValidator.notNull(jobDeviceManagementOperationCreator.getJobId(), "jobDeviceManagementOperationCreator.jobId");
        ArgumentValidator.notNull(jobDeviceManagementOperationCreator.getDeviceManagementOperationId(), "jobDeviceManagementOperationCreator.deviceManagementOperationId");
        // Check access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.write, null));
        // Check duplicate
        KapuaQuery query = new KapuaQuery(jobDeviceManagementOperationCreator.getScopeId());
        query.setPredicate(
                new AndPredicate(
                        new AttributePredicate<>(JobDeviceManagementOperationAttributes.JOB_ID, jobDeviceManagementOperationCreator.getJobId()),
                        new AttributePredicate<>(JobDeviceManagementOperationAttributes.DEVICE_MANAGEMENT_OPERATION_ID, jobDeviceManagementOperationCreator.getDeviceManagementOperationId())
                )
        );

        return txManager.execute(tx -> {
            if (repository.count(tx, query) > 0) {
                List<Map.Entry<String, Object>> uniqueAttributes = new ArrayList<>();

                uniqueAttributes.add(new AbstractMap.SimpleEntry<>(JobDeviceManagementOperationAttributes.JOB_ID, jobDeviceManagementOperationCreator.getJobId()));
                uniqueAttributes.add(
                        new AbstractMap.SimpleEntry<>(JobDeviceManagementOperationAttributes.DEVICE_MANAGEMENT_OPERATION_ID, jobDeviceManagementOperationCreator.getDeviceManagementOperationId()));

                throw new KapuaEntityUniquenessException(JobDeviceManagementOperation.TYPE, uniqueAttributes);
            }
            // Create JobDeviceManagementOperation
            JobDeviceManagementOperation newEntity = entityFactory.newEntity(jobDeviceManagementOperationCreator.getScopeId());
            newEntity.setJobId(jobDeviceManagementOperationCreator.getJobId());
            newEntity.setDeviceManagementOperationId(jobDeviceManagementOperationCreator.getDeviceManagementOperationId());
            // Do create
            return repository.create(tx, newEntity);
        });

    }

    @Override
    public JobDeviceManagementOperation find(KapuaId scopeId, KapuaId jobDeviceManagementOperationId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(jobDeviceManagementOperationId, "jobDeviceManagementOperationId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.write, scopeId));
        // Do find
        return txManager.execute(tx -> repository.find(tx, scopeId, jobDeviceManagementOperationId))
                .orElse(null);
    }

    @Override
    public JobDeviceManagementOperationListResult query(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.read, query.getScopeId()));
        // Do query
        return txManager.execute(tx -> repository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.read, query.getScopeId()));
        // Do query
        return txManager.execute(tx -> repository.count(tx, query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId jobDeviceManagementOperationId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(jobDeviceManagementOperationId, "jobDeviceManagementOperationId");

        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.delete, scopeId));

        // Do delete
        txManager.execute(tx -> repository.delete(tx, scopeId, jobDeviceManagementOperationId));
    }
}
