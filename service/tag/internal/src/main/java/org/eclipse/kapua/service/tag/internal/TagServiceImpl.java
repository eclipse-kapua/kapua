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
package org.eclipse.kapua.service.tag.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.configuration.KapuaConfigurableServiceBase;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.tag.Tag;
import org.eclipse.kapua.service.tag.TagCreator;
import org.eclipse.kapua.service.tag.TagFactory;
import org.eclipse.kapua.service.tag.TagListResult;
import org.eclipse.kapua.service.tag.TagRepository;
import org.eclipse.kapua.service.tag.TagService;
import org.eclipse.kapua.storage.TxManager;

/**
 * {@link TagService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class TagServiceImpl extends KapuaConfigurableServiceBase implements TagService {

    private final AuthorizationService authorizationService;
    private final TagFactory tagFactory;
    private final TxManager txManager;
    private final TagRepository tagRepository;

    /**
     * Injectable Constructor
     *
     * @param authorizationService
     *         The {@link AuthorizationService} instance
     * @param serviceConfigurationManager
     *         The {@link ServiceConfigurationManager} instance
     * @param txManager
     * @param tagRepository
     *         The {@link TagRepository} instance
     * @param tagFactory
     * @since 2.0.0
     */
    @Inject
    public TagServiceImpl(
            AuthorizationService authorizationService,
            ServiceConfigurationManager serviceConfigurationManager,
            TxManager txManager,
            TagRepository tagRepository,
            TagFactory tagFactory) {
        super(txManager, serviceConfigurationManager, Domains.TAG, authorizationService);
        this.authorizationService = authorizationService;
        this.tagRepository = tagRepository;
        this.tagFactory = tagFactory;
        this.txManager = txManager;
    }

    @Override
    public Tag create(TagCreator tagCreator) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(tagCreator, "tagCreator");
        ArgumentValidator.notNull(tagCreator.getScopeId(), "tagCreator.scopeId");
        ArgumentValidator.validateEntityName(tagCreator.getName(), "tagCreator.name");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.TAG, Actions.write, tagCreator.getScopeId()));
        return txManager.execute(tx -> {
            // Check entity limit
            serviceConfigurationManager.checkAllowedEntities(tx, tagCreator.getScopeId(), "Tags");
            // Check duplicate name
            final long otherEntitiesWithSameName = tagRepository.countEntitiesWithNameInScope(tx, tagCreator.getScopeId(), tagCreator.getName());
            if (otherEntitiesWithSameName > 0) {
                throw new KapuaDuplicateNameException(tagCreator.getName());
            }

            final Tag toBeCreated = tagFactory.newEntity(tagCreator.getScopeId());
            toBeCreated.setName(tagCreator.getName());
            toBeCreated.setDescription(tagCreator.getDescription());
            // Do create
            return tagRepository.create(tx, toBeCreated);
        });
    }

    @Override
    public Tag update(Tag tag) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(tag, "tag");
        ArgumentValidator.notNull(tag.getId(), "tag.id");
        ArgumentValidator.notNull(tag.getScopeId(), "tag.scopeId");
        ArgumentValidator.validateEntityName(tag.getName(), "tag.name");

        // Check Access
        authorizationService.checkPermission(
                new Permission(Domains.TAG, Actions.write, tag.getScopeId()));

        // Check duplicate name
        return txManager.execute(tx -> {
            // Check duplicate name
            final long otherEntitiesWithSameName = tagRepository.countOtherEntitiesWithNameInScope(
                    tx, tag.getScopeId(), tag.getId(), tag.getName());
            if (otherEntitiesWithSameName > 0) {
                throw new KapuaDuplicateNameException(tag.getName());
            }
            // Do Update
            return tagRepository.update(tx, tag);
        });
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId tagId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(tagId, "tagId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.TAG, Actions.delete, scopeId));
        // Check existence
        txManager.execute(tx -> tagRepository.delete(tx, scopeId, tagId));
    }

    @Override
    public Tag find(KapuaId scopeId, KapuaId tagId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(tagId, "tagId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.TAG, Actions.read, scopeId));
        // Do find
        return txManager.execute(tx -> tagRepository.find(tx, scopeId, tagId))
                .orElse(null);
    }

    @Override
    public TagListResult query(KapuaQuery query) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.TAG, Actions.read, query.getScopeId()));
        // Do query
        return txManager.execute(tx -> tagRepository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.TAG, Actions.read, query.getScopeId()));
        // Do count
        return txManager.execute(tx -> tagRepository.count(tx, query));
    }
}
