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
package org.eclipse.kapua.service.scheduler.trigger.definition.quartz;

import java.util.Optional;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionCreator;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionFactory;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionListResult;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionRepository;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionService;
import org.eclipse.kapua.storage.TxManager;

/**
 * {@link TriggerDefinitionService} implementation.
 *
 * @since 1.1.0
 */
@Singleton
public class TriggerDefinitionServiceImpl implements TriggerDefinitionService {

    private final AuthorizationService authorizationService;
    private final TxManager txManager;
    private final TriggerDefinitionRepository triggerDefinitionRepository;
    private final TriggerDefinitionFactory triggerDefinitionFactory;

    public TriggerDefinitionServiceImpl(
            AuthorizationService authorizationService,
            TxManager txManager,
            TriggerDefinitionRepository triggerDefinitionRepository,
            TriggerDefinitionFactory triggerDefinitionFactory) {
        this.authorizationService = authorizationService;
        this.txManager = txManager;
        this.triggerDefinitionRepository = triggerDefinitionRepository;
        this.triggerDefinitionFactory = triggerDefinitionFactory;
    }

    @Override
    public TriggerDefinition create(TriggerDefinitionCreator triggerDefinitionCreator) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(triggerDefinitionCreator, "triggerDefinitionCreator");
        ArgumentValidator.notNull(triggerDefinitionCreator.getScopeId(), "triggerDefinitionCreator.scopeId");
        ArgumentValidator.notNull(triggerDefinitionCreator.getTriggerType(), "triggerDefinitionCreator.stepType");
        ArgumentValidator.validateEntityName(triggerDefinitionCreator.getName(), "triggerDefinitionCreator.name");

        // Check access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.write, null));

        // Do create
        TriggerDefinition toBeCreated = triggerDefinitionFactory.newEntity(triggerDefinitionCreator.getScopeId());
        toBeCreated.setName(triggerDefinitionCreator.getName());
        toBeCreated.setDescription(triggerDefinitionCreator.getDescription());
        toBeCreated.setProcessorName(triggerDefinitionCreator.getProcessorName());
        toBeCreated.setTriggerProperties(triggerDefinitionCreator.getTriggerProperties());
        return txManager.execute(tx -> triggerDefinitionRepository.create(tx, toBeCreated));
    }

    @Override
    public TriggerDefinition update(TriggerDefinition triggerDefinition) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(triggerDefinition, "stepDefinition");
        ArgumentValidator.notNull(triggerDefinition.getScopeId(), "stepDefinition.scopeId");
        ArgumentValidator.notNull(triggerDefinition.getTriggerType(), "triggerDefinition.stepType");
        ArgumentValidator.validateEntityName(triggerDefinition.getName(), "triggerDefinition.name");

        // Check access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.write, null));

        return txManager.execute(tx -> triggerDefinitionRepository.update(tx, triggerDefinition));
    }

    @Override
    public TriggerDefinition find(KapuaId stepDefinitionId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(stepDefinitionId, KapuaEntityAttributes.ENTITY_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.read, KapuaId.ANY));
        // Do find
        return txManager.execute(tx -> triggerDefinitionRepository.find(tx, KapuaId.ANY, stepDefinitionId))
                .orElse(null);
    }

    @Override
    public TriggerDefinition find(KapuaId scopeId, KapuaId stepDefinitionId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(stepDefinitionId, KapuaEntityAttributes.ENTITY_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.read, KapuaId.ANY));
        // Do find
        return txManager.execute(tx -> triggerDefinitionRepository.find(tx, scopeId, stepDefinitionId))
                .orElse(null);
    }

    @Override
    public TriggerDefinition findByName(String name) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(name, "name");
        // Do find
        return txManager.execute(tx -> {
                    final Optional<TriggerDefinition> triggerDefinition = triggerDefinitionRepository.findByName(tx, name);
                    if (triggerDefinition.isPresent()) {
                        // Check Access
                        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.read, KapuaId.ANY));
                    }
                    return triggerDefinition;
                })
                .orElse(null);
    }

    @Override
    public TriggerDefinitionListResult query(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.read, KapuaId.ANY));
        // Do query
        return txManager.execute(tx -> triggerDefinitionRepository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.read, KapuaId.ANY));
        // Do query
        return txManager.execute(tx -> triggerDefinitionRepository.count(tx, query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId stepDefinitionId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(stepDefinitionId, KapuaEntityAttributes.ENTITY_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.JOB, Actions.delete, null));
        // Do delete
        txManager.execute(tx -> {
            final Optional<TriggerDefinition> toBeDeleted = triggerDefinitionRepository.find(tx, scopeId, stepDefinitionId);
            if (!toBeDeleted.isPresent()) {
                throw new KapuaEntityNotFoundException(TriggerDefinition.TYPE, stepDefinitionId);
            }

            return triggerDefinitionRepository.delete(tx, toBeDeleted.get());
        });

    }
}
