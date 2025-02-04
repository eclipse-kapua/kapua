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
import org.eclipse.kapua.KapuaEntityNotFoundException;
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
import org.eclipse.kapua.service.tag.TagListResult;
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
    private final TxManager txManager;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

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
     * @since 2.0.0
     */
    @Inject
    public TagServiceImpl(
            AuthorizationService authorizationService,
            ServiceConfigurationManager serviceConfigurationManager,
            TxManager txManager,
            TagRepository tagRepository, TagMapper tagMapper) {
        super(txManager, serviceConfigurationManager, Domains.TAG, authorizationService);
        this.authorizationService = authorizationService;
        this.tagRepository = tagRepository;
        this.txManager = txManager;
        this.tagMapper = tagMapper;
    }

    @Override
    public Tag create(TagCreator tagCreator) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(tagCreator, "tagCreator");
        ArgumentValidator.notNull(tagCreator.getScopeId(), "tagCreator.scopeId");
        ArgumentValidator.validateEntityName(tagCreator.getName(), "tagCreator.name");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.TAG, Actions.write, tagCreator.getScopeId()));
        final TagImpl newTag = txManager.execute(tx -> {
            // Check entity limit
            serviceConfigurationManager.checkAllowedEntities(tx, tagCreator.getScopeId(), "Tags");
            // Check duplicate name
            final long otherEntitiesWithSameName = tagRepository.countEntitiesWithNameInScope(tx, tagCreator.getScopeId(), tagCreator.getName());
            if (otherEntitiesWithSameName > 0) {
                throw new KapuaDuplicateNameException(tagCreator.getName());
            }

            final TagImpl toBeCreated = new TagImpl(tagCreator.getScopeId());
            toBeCreated.setName(tagCreator.getName());
            toBeCreated.setDescription(tagCreator.getDescription());
            // Do create
            final TagImpl created = tagRepository.create(tx, toBeCreated);
            return created;
        });
        return tagMapper.map(newTag);
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
        final TagImpl updatedTag = txManager.execute(tx -> {
            // Check duplicate name
            final long otherEntitiesWithSameName = tagRepository.countOtherEntitiesWithNameInScope(
                    tx, tag.getScopeId(), tag.getId(), tag.getName());
            if (otherEntitiesWithSameName > 0) {
                throw new KapuaDuplicateNameException(tag.getName());
            }
            final TagImpl existingTag = tagRepository.find(tx, tag.getScopeId(), tag.getId())
                    .orElseThrow(() -> new KapuaEntityNotFoundException("tag", tag.getId()));
            //merge tag info
            tagMapper.merge(existingTag, tag);

            // Do Update
            return tagRepository.update(tx, existingTag);
        });
        return tagMapper.map(updatedTag);
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
                .map(tagMapper::map)
                .orElse(null);
    }

    @Override
    public TagListResult query(KapuaQuery query) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.TAG, Actions.read, query.getScopeId()));
        // Do query
        return tagMapper.map(txManager.<TagImplListResult>execute(tx -> tagRepository.query(tx, query)));
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
