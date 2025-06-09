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
package org.eclipse.kapua.commons.service.event.store.internal;

import javax.inject.Inject;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecord;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecordCreator;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecordListResult;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecordRepository;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreService;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.event.RaiseServiceEvent;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.storage.TxManager;

/**
 * {@link EventStoreService} implementation.
 *
 * @since 1.0.0
 */
public class EventStoreServiceImpl
        implements EventStoreService {

    private final AuthorizationService authorizationService;
    private final TxManager txManager;
    private final EventStoreRecordRepository repository;

    @Inject
    public EventStoreServiceImpl(
            AuthorizationService authorizationService,
            TxManager txManager,
            EventStoreRecordRepository repository) {
        this.authorizationService = authorizationService;
        this.txManager = txManager;
        this.repository = repository;
    }

    @Override
    @RaiseServiceEvent
    public EventStoreRecord create(EventStoreRecordCreator kapuaEventCreator)
            throws KapuaException {

        throw new UnsupportedOperationException();
    }

    @Override
    @RaiseServiceEvent
    public EventStoreRecord update(EventStoreRecord kapuaEvent)
            throws KapuaException {
        // Validation of the fields
        ArgumentValidator.notNull(kapuaEvent.getId(), "kapuaEvent.id");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.EVENT_STORE, Actions.write, kapuaEvent.getScopeId()));
        // Do update
        return txManager.execute(tx -> repository.update(tx, kapuaEvent));
    }

    @Override
    @RaiseServiceEvent
    public void delete(KapuaId scopeId, KapuaId kapuaEventId)
            throws KapuaException {
        // Validation of the fields
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(kapuaEventId, KapuaEntityAttributes.ENTITY_ID);
        // Check Access
        Actions action = Actions.write;
        authorizationService.checkPermission(new Permission(Domains.EVENT_STORE, action, scopeId));
        // Do delete
        txManager.execute(tx -> repository.delete(tx, scopeId, kapuaEventId));
    }

    @Override
    public EventStoreRecord find(KapuaId scopeId, KapuaId kapuaEventId)
            throws KapuaException {
        // Validation of the fields
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(kapuaEventId, KapuaEntityAttributes.ENTITY_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.EVENT_STORE, Actions.read, scopeId));
        // Make sure kapuaEvent exists
        return txManager.execute(tx -> repository.find(tx, scopeId, kapuaEventId))
                .orElse(null);
    }

    @Override
    public EventStoreRecord find(KapuaId kapuaEventId)
            throws KapuaException {
        // Validation of the fields
        ArgumentValidator.notNull(kapuaEventId, KapuaEntityAttributes.ENTITY_ID);
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.EVENT_STORE, Actions.read, kapuaEventId));

        return txManager.execute(tx -> repository.find(tx, KapuaId.ANY, kapuaEventId))
                .orElse(null);
    }

    @Override
    public EventStoreRecordListResult query(KapuaQuery query)
            throws KapuaException {
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.EVENT_STORE, Actions.read, query.getScopeId()));
        return txManager.execute(tx -> repository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query)
            throws KapuaException {
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.EVENT_STORE, Actions.read, query.getScopeId()));
        return txManager.execute(tx -> repository.count(tx, query));
    }
}
