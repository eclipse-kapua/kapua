/*******************************************************************************
 * Copyright (c) 2025, 2025 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech
 *******************************************************************************/
package org.eclipse.kapua.service.authorization.group.shiro;

import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupCreator;
import org.eclipse.kapua.service.authorization.group.GroupRepository;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.tag.Tag;
import org.eclipse.kapua.service.tag.TagAttributes;
import org.eclipse.kapua.service.tag.TagFactory;
import org.eclipse.kapua.service.tag.TagListResult;
import org.eclipse.kapua.service.tag.TagQuery;
import org.eclipse.kapua.service.tag.TagService;
import org.eclipse.kapua.storage.TxContext;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link GroupServiceValidationUtils} implementation.
 *
 * @since 2.1.0
 */
public final class GroupServiceValidationUtilsImpl implements GroupServiceValidationUtils {

    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;

    protected final ServiceConfigurationManager serviceConfigurationManager;
    private final TagService tagService;
    private final TagFactory tagFactory;

    private final GroupRepository groupRepository;

    public GroupServiceValidationUtilsImpl(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            ServiceConfigurationManager serviceConfigurationManager,
            TagService tagService,
            TagFactory tagFactory,
            GroupRepository groupRepository
    ) {
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.serviceConfigurationManager = serviceConfigurationManager;
        this.tagService = tagService;
        this.tagFactory = tagFactory;
        this.groupRepository = groupRepository;
    }

    @Override
    public void validateCreatePreconditions(GroupCreator groupCreator) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(groupCreator.getScopeId().getId(), "groupCreator.scopeId");
        ArgumentValidator.notEmptyOrNull(groupCreator.getName(), "groupCreator.name");
        ArgumentValidator.validateEntityName(groupCreator.getName(), "groupCreator.name");

        //
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.write, groupCreator.getScopeId()));
    }

    @Override
    public void validateCreateInTransaction(TxContext txContext, GroupCreator groupCreator) throws KapuaException {
        // Check entity limit
        serviceConfigurationManager.checkAllowedEntities(txContext, groupCreator.getScopeId(), "Groups");

        //
        // Check duplicates

        // .name
        if (groupRepository.countEntitiesWithNameInScope(txContext, groupCreator.getScopeId(), groupCreator.getName()) > 0) {
            throw new KapuaDuplicateNameException(groupCreator.getName());
        }
    }

    @Override
    public void validateUpdatePreconditions(Group group) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(group, "group");
        ArgumentValidator.notNull(group.getId(), "group.id");
        ArgumentValidator.notNull(group.getScopeId(), "group.scopeId");
        ArgumentValidator.validateEntityName(group.getName(), "group.name");

        // .tagIds
        validateTagIds(group.getScopeId(), group.getTagIds());

        //
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.write, group.getScopeId()));
    }

    @Override
    public void validateUpdateInTransaction(TxContext txContext, Group group) throws KapuaException {
        //
        // Check existence
        groupRepository
                .find(txContext, group.getScopeId(), group.getId())
                .orElseThrow(() -> new KapuaEntityNotFoundException(Group.TYPE, group.getId()));

        //
        // Check duplicates

        // .name
        if (groupRepository.countOtherEntitiesWithNameInScope(txContext, group.getScopeId(), group.getId(), group.getName()) > 0) {
            throw new KapuaDuplicateNameException(group.getName());
        }
    }

    @Override
    public void validateFindPreconditions(KapuaId scopeId, KapuaId groupId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(groupId, "groupId");

        // Check access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.read, scopeId));
    }

    @Override
    public void validateQueryPreconditions(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.read, query.getScopeId()));
    }

    @Override
    public void validateCountPreconditions(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.read, query.getScopeId()));
    }

    @Override
    public void validateDeletePreconditions(KapuaId scopeId, KapuaId groupId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(groupId, "id");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.delete, scopeId, Group.ANY));
    }

    @Override
    public void validateDeleteInTransaction(TxContext txContext, KapuaId scopeId, KapuaId groupId) throws KapuaException {
        // Check existence
        Group group = groupRepository
                .find(txContext, scopeId, groupId)
                .orElseThrow(() -> new KapuaEntityNotFoundException(Group.TYPE, groupId));
    }


    //
    // Private methods
    //

    /**
     * Applies validation logics to {@link Group#getTagIds()} attribute.
     * <p>
     * Requirements are:
     * <ul>
     *     <li>The Tags defined must exists</li>
     * </ul>
     *
     * @param tagIds The {@link Group} to check
     * @throws KapuaException
     * @since 2.1.0
     */
    private void validateTagIds(KapuaId scopeId, @NotNull Collection<KapuaId> tagIds) throws KapuaException {
        if (tagIds.isEmpty()) {
            return;
        }

        // Look for Tags
        TagQuery tagQuery = tagFactory.newQuery(scopeId);
        tagQuery.setPredicate(tagQuery.attributePredicate(TagAttributes.ENTITY_ID, tagIds));
        TagListResult dbTags = KapuaSecurityUtils.doPrivileged(() -> tagService.query(tagQuery));

        // Match Tags found with given Tag IDs
        if (tagIds.size() != dbTags.getSize()) {
            // Some tags have not been found
            Set<KapuaId> dbTagsIds =
                    dbTags.getItems()
                            .stream()
                            .map(Tag::getId)
                            .collect(Collectors.toSet());

            for (KapuaId tagId : tagIds) {
                if (!dbTagsIds.contains(tagId)) {
                    throw new KapuaEntityNotFoundException(Tag.TYPE, tagId);
                }
            }
        }
    }
}
