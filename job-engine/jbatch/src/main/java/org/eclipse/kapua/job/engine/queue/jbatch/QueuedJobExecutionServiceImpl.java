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
package org.eclipse.kapua.job.engine.queue.jbatch;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecution;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecutionCreator;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecutionListResult;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecutionRepository;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecutionService;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.storage.TxManager;

/**
 * {@link QueuedJobExecutionService} implementation
 */
@Singleton
public class QueuedJobExecutionServiceImpl implements QueuedJobExecutionService {

    private final AuthorizationService authorizationService;
    private final TxManager txManager;
    private final QueuedJobExecutionRepository repository;

    public QueuedJobExecutionServiceImpl(
            AuthorizationService authorizationService,
            TxManager txManager,
            QueuedJobExecutionRepository repository) {
        this.authorizationService = authorizationService;
        this.txManager = txManager;
        this.repository = repository;
    }

    @Override
    public QueuedJobExecution create(QueuedJobExecutionCreator creator) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(creator, "queuedJobExecutionCreator");
        ArgumentValidator.notNull(creator.getScopeId(), "queuedJobExecutionCreator.scopeId");
        // Check access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.write, null));

        QueuedJobExecution queuedJobExecutionImpl = new QueuedJobExecutionImpl(creator.getScopeId());
        queuedJobExecutionImpl.setJobId(creator.getJobId());
        queuedJobExecutionImpl.setJobExecutionId(creator.getJobExecutionId());
        queuedJobExecutionImpl.setWaitForJobExecutionId(creator.getWaitForJobExecutionId());
        queuedJobExecutionImpl.setStatus(creator.getStatus());
        // Do create
        return txManager.execute(tx -> repository.create(tx, queuedJobExecutionImpl));
    }

    @Override
    public QueuedJobExecution update(QueuedJobExecution queuedJobExecution) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(queuedJobExecution, "queuedJobExecution");
        ArgumentValidator.notNull(queuedJobExecution.getScopeId(), "queuedJobExecution.scopeId");
        // Check access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.write, null));

        return txManager.execute(tx -> repository.update(tx, queuedJobExecution));
    }

    @Override
    public QueuedJobExecution find(KapuaId scopeId, KapuaId queuedJobExecutionId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(queuedJobExecutionId, "queuedJobExecutionId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.read, scopeId));
        // Do find
        return txManager.execute(tx -> repository.find(tx, scopeId, queuedJobExecutionId))
                .orElse(null);
    }

    @Override
    public QueuedJobExecutionListResult query(KapuaQuery query) throws KapuaException {
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
    public void delete(KapuaId scopeId, KapuaId queuedJobExecutionId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(queuedJobExecutionId, "queuedJobExecutionId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.delete, scopeId));
        // Do delete
        txManager.execute(tx -> repository.delete(tx, scopeId, queuedJobExecutionId));
    }
}
