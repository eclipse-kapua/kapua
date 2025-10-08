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
package org.eclipse.kapua.service.user.internal;

import com.google.common.base.Strings;
import org.eclipse.kapua.KapuaDuplicateExternalIdException;
import org.eclipse.kapua.KapuaDuplicateExternalUsernameException;
import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.KapuaDuplicateNameInAnotherAccountError;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.CommonsValidationRegex;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.type.DateConverter;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.CheckStrategy;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupAttributes;
import org.eclipse.kapua.service.authorization.group.GroupFactory;
import org.eclipse.kapua.service.authorization.group.GroupListResult;
import org.eclipse.kapua.service.authorization.group.GroupQuery;
import org.eclipse.kapua.service.authorization.group.GroupService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.tag.Tag;
import org.eclipse.kapua.service.tag.TagAttributes;
import org.eclipse.kapua.service.tag.TagFactory;
import org.eclipse.kapua.service.tag.TagListResult;
import org.eclipse.kapua.service.tag.TagQuery;
import org.eclipse.kapua.service.tag.TagService;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserRepository;
import org.eclipse.kapua.service.user.UserStatus;
import org.eclipse.kapua.service.user.UserType;
import org.eclipse.kapua.storage.TxContext;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link UserServiceValidationUtils} implementation.
 *
 * @since 2.1.0
 */
public final class UserServiceValidationUtilsImpl implements UserServiceValidationUtils {

    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;

    private final GroupService groupService;
    private final GroupFactory groupFactory;

    protected final ServiceConfigurationManager serviceConfigurationManager;
    private final TagService tagService;
    private final TagFactory tagFactory;

    private final UserRepository userRepository;

    public UserServiceValidationUtilsImpl(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            GroupService groupService,
            GroupFactory groupFactory,
            ServiceConfigurationManager serviceConfigurationManager,
            TagService tagService,
            TagFactory tagFactory,
            UserRepository userRepository
    ) {
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.groupService = groupService;
        this.groupFactory = groupFactory;
        this.serviceConfigurationManager = serviceConfigurationManager;
        this.tagService = tagService;
        this.tagFactory = tagFactory;
        this.userRepository = userRepository;
    }

    @Override
    public void validateCreatePreconditions(UserCreator userCreator) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(userCreator.getScopeId().getId(), "userCreator.scopeId");
        ArgumentValidator.notEmptyOrNull(userCreator.getName(), "userCreator.name");
        ArgumentValidator.match(userCreator.getName(), CommonsValidationRegex.NAME_REGEXP, "userCreator.name");
        ArgumentValidator.lengthRange(userCreator.getName(), 3, 255, "userCreator.name");
        ArgumentValidator.match(userCreator.getEmail(), CommonsValidationRegex.EMAIL_REGEXP, "userCreator.email");
        ArgumentValidator.notNull(userCreator.getStatus(), "userCreator.status");

        // .groupsIds
        validateGroupIds(userCreator.getScopeId(), userCreator.getGroupIds());

        // .tagIds
        validateTagIds(userCreator.getScopeId(), userCreator.getTagIds());

        // .userType
        ArgumentValidator.notNull(userCreator.getUserType(), "userCreator.userType");
        if (userCreator.getUserType() == UserType.EXTERNAL) {
            if (userCreator.getExternalId() != null) {
                ArgumentValidator.notEmptyOrNull(userCreator.getExternalId(), "userCreator.externalId");
                ArgumentValidator.lengthRange(userCreator.getExternalId(), 3, 255, "userCreator.externalId");
            } else {
                ArgumentValidator.notEmptyOrNull(userCreator.getExternalUsername(), "userCreator.externalUsername");
                ArgumentValidator.lengthRange(userCreator.getExternalUsername(), 3, 255, "userCreator.externalUsername");
            }
        } else if (userCreator.getUserType() == UserType.INTERNAL) {
            ArgumentValidator.isEmptyOrNull(userCreator.getExternalId(), "userCreator.externalId");
            ArgumentValidator.isEmptyOrNull(userCreator.getExternalUsername(), "userCreator.externalUsername");
        }

        //
        // Check Access
        // Check that current Subject can manage all the target Groups
        Set<Permission> groupPermissions = buildSetPermissionsFromGroupIds(Domains.USER, Actions.write, userCreator.getScopeId(), userCreator.getGroupIds());
        authorizationService.checkPermissions(groupPermissions);
    }

    @Override
    public void validateCreateInTransaction(TxContext txContext, UserCreator userCreator) throws KapuaException {
        // Check entity limit
        serviceConfigurationManager.checkAllowedEntities(txContext, userCreator.getScopeId(), "Users");

        //
        // Check duplicates

        // .name
        // in scope
        if (userRepository.countEntitiesWithNameInScope(txContext, userCreator.getScopeId(), userCreator.getName()) > 0) {
            throw new KapuaDuplicateNameException(userCreator.getName());
        }

        // in other scopes
        if (userRepository.countEntitiesWithName(txContext, userCreator.getName()) > 0) {
            throw new KapuaDuplicateNameInAnotherAccountError(userCreator.getName());
        }

        // If UserType is EXTERNAL
        if (userCreator.getUserType() == UserType.EXTERNAL) {
            // .externalId
            if (!Strings.isNullOrEmpty(userCreator.getExternalId())) {
                Optional<User> userByExternalId = userRepository.findByExternalId(txContext, userCreator.getExternalId());

                if (userByExternalId.isPresent()) {
                    throw new KapuaDuplicateExternalIdException(userCreator.getExternalId());
                }
            }

            // .externalUsername
            if (!Strings.isNullOrEmpty(userCreator.getExternalUsername())) {
                Optional<User> userByExternalPreferredUserame = userRepository.findByExternalUsername(txContext, userCreator.getExternalUsername());

                if (userByExternalPreferredUserame.isPresent()) {
                    throw new KapuaDuplicateExternalUsernameException(userCreator.getExternalUsername());
                }
            }
        }
    }

    @Override
    public void validateUpdatePreconditions(User user) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(user.getId(), "user.id");
        ArgumentValidator.notNull(user.getScopeId(), "user.scopeId");
        ArgumentValidator.notEmptyOrNull(user.getName(), "user.name");
        ArgumentValidator.match(user.getName(), CommonsValidationRegex.NAME_REGEXP, "user.name");
        ArgumentValidator.lengthRange(user.getName(), 3, 255, "user.name");
        ArgumentValidator.match(user.getEmail(), CommonsValidationRegex.EMAIL_REGEXP, "user.email");
        ArgumentValidator.notNull(user.getStatus(), "user.status");
        ArgumentValidator.notNull(user.getUserType(), "user.userType");

        // .userType
        if (user.getUserType() == UserType.EXTERNAL) {
            if (user.getExternalId() != null) {
                ArgumentValidator.notEmptyOrNull(user.getExternalId(), "user.externalId");
                ArgumentValidator.lengthRange(user.getExternalId(), 3, 255, "user.externalId");
            } else {
                ArgumentValidator.notEmptyOrNull(user.getExternalUsername(), "user.externalUsername");
                ArgumentValidator.lengthRange(user.getExternalUsername(), 3, 255, "user.externalUsername");
            }
        } else if (user.getUserType() == UserType.INTERNAL) {
            ArgumentValidator.isEmptyOrNull(user.getExternalId(), "user.externalId");
            ArgumentValidator.isEmptyOrNull(user.getExternalUsername(), "user.externalUsername");
        }

        // .tagIds
        validateTagIds(user.getScopeId(), user.getTagIds());

        //
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER, Actions.write, user.getScopeId(), Group.ANY));
    }

    @Override
    public void validateUpdateInTransaction(TxContext txContext, User user) throws KapuaException {
        //
        // Check existence
        User currentUser =
                userRepository
                        .find(txContext, user.getScopeId(), user.getId())
                        .orElseThrow(() -> new KapuaEntityNotFoundException(User.TYPE, user.getId()));

        //
        // Check fields
        // .groupIds
        validateGroupIds(user.getScopeId(), user.getGroupIds());

        //
        // Check not updatable fields

        // .name
        if (!Objects.equals(currentUser.getName(), user.getName())) {
            throw new KapuaIllegalArgumentException("user.name", user.getName());
        }

        // .userType
        if (!Objects.equals(currentUser.getUserType(), user.getUserType())) {
            throw new KapuaIllegalArgumentException("user.userType", user.getUserType().toString());
        }

        //
        // Check update on admin User
        validateUpdateSystemUser(user);

        //
        // Check update on logged User
        validateUpdateCurrentSubject(user);

        //
        // Check duplicates

        // .externalId
        if (user.getExternalId() != null) {
            if (userRepository.findByExternalId(txContext, user.getExternalId())
                    .map(User::getId)
                    .map(id -> !(id.equals(user.getId())))
                    .orElse(false)) {
                throw new KapuaDuplicateExternalIdException(user.getExternalId());
            }
        }

        // .externalUsername
        if (user.getExternalUsername() != null) {
            if (userRepository.findByExternalUsername(txContext, user.getExternalUsername())
                    .map(User::getId)
                    .map(id -> !(id.equals(user.getId())))
                    .orElse(false)) {
                throw new KapuaDuplicateExternalUsernameException(user.getExternalUsername());
            }
        }

        //
        // Check Access
        checkAccessGroupIds(txContext, user);
    }

    @Override
    public void validateFindPreconditions(KapuaId scopeId, KapuaId userId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(userId, "userId");

        // Check access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER, Actions.read, scopeId, Group.ANY));
    }

    @Override
    public void validateFindByIdPreConditions(KapuaId userId) throws KapuaException {
        ArgumentValidator.notNull(userId, "userId");
    }

    @Override
    public void validateFindByNamePreConditions(String name) throws KapuaException {
        ArgumentValidator.notEmptyOrNull(name, "name");
    }

    @Override
    public void validateFindByExternalIdPreConditions(String externalId) throws KapuaException {
        ArgumentValidator.notEmptyOrNull(externalId, "externalId");
    }

    @Override
    public void validateFindByExternalUsernamePreConditions(String externalUsername) throws KapuaException {
        ArgumentValidator.notEmptyOrNull(externalUsername, "externalUsername");
    }

    @Override
    public void validateFindByFieldPostConditions(User user) throws KapuaException {
        // Check access
        if (user != null) {
            Set<Permission> groupPermissions = buildSetPermissionsFromGroupIds(Domains.USER, Actions.read, user.getScopeId(), user.getGroupIds());

            authorizationService.checkPermissions(groupPermissions, CheckStrategy.AT_LEAST_ONE_OF);
        }
    }

    @Override
    public void validateQueryPreconditions(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER, Actions.read, query.getScopeId(), Group.ANY));
    }

    @Override
    public void validateCountPreconditions(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER, Actions.read, query.getScopeId(), Group.ANY));
    }

    @Override
    public void validateDeletePreconditions(KapuaId scopeId, KapuaId userId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(userId, "id");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER, Actions.delete, scopeId, Group.ANY));
    }

    @Override
    public void validateDeleteInTransaction(TxContext txContext, KapuaId scopeId, KapuaId userId) throws KapuaException {
        // Check existence
        User user = userRepository
                .find(txContext, scopeId, userId)
                .orElseThrow(() -> new KapuaEntityNotFoundException(User.TYPE, userId));

        //
        // Validate permission on Groups
        Set<KapuaId> groupIds = findCurrentGroupIds(txContext, scopeId, userId);
        Set<Permission> groupPermissions = buildSetPermissionsFromGroupIds(Domains.USER, Actions.delete, scopeId, groupIds);
        authorizationService.checkPermissions(groupPermissions);

        //
        // Check delete on admin User
        validateDeleteSystemUser(user);

        //
        // Check update on logged User
        validateDeleteCurrentSubject(user);
    }


    //
    // Private methods
    //
    private void validateUpdateSystemUser(@NotNull User user) throws KapuaIllegalArgumentException {
        String adminUsername = SystemSetting.getInstance().getString(SystemSettingKey.SYS_ADMIN_USERNAME);

        if (adminUsername.equals(user.getName())) {
            // Admin user cannot have an expiration date
            if (user.getExpirationDate() != null) {
                throw new KapuaIllegalArgumentException("user.expirationDate", DateConverter.toString(user.getExpirationDate()));
            }

            // Admin user cannot be disabled
            if (UserStatus.DISABLED.equals(user.getStatus())) {
                throw new KapuaIllegalArgumentException("user.status", user.getStatus().name());
            }
        }
    }

    private void validateUpdateCurrentSubject(@NotNull User user) throws KapuaIllegalArgumentException {
        KapuaId currentSubjectId = KapuaSecurityUtils.getSession().getUserId();

        if (user.getId().equals(currentSubjectId)) {

            // Current Subject cannot disable itself
            if (UserStatus.DISABLED.equals(user.getStatus())) {
                throw new KapuaIllegalArgumentException("user.status", user.getStatus().name());
            }
        }
    }

    private void validateDeleteSystemUser(@NotNull User user) throws KapuaIllegalArgumentException {
        String adminUsername = SystemSetting.getInstance().getString(SystemSettingKey.SYS_ADMIN_USERNAME);

        // Admin user cannot be deleted
        if (adminUsername.equals(user.getName())) {
            throw new KapuaIllegalArgumentException("userId", user.getId().toString());
        }
    }

    private void validateDeleteCurrentSubject(@NotNull User user) throws KapuaIllegalArgumentException {
        KapuaId currentSubjectId = KapuaSecurityUtils.getSession().getUserId();

        // Current Subject cannot delete itself
        if (user.getId().equals(currentSubjectId)) {
            throw new KapuaIllegalArgumentException("userId", user.getId().toString());
        }
    }

    /**
     * Checks that the given Group exists.
     *
     * @param scopeId The {@link Group#getScopeId()}
     * @param groupId The {@link Group#getId()}
     * @throws KapuaException
     * @since 2.1.0
     */
    private void checkGroupExistence(KapuaId scopeId, KapuaId groupId) throws KapuaException {
        if (groupId != null) {
            Group group = KapuaSecurityUtils.doPrivileged(() -> groupService.find(scopeId, groupId));

            if (group == null) {
                throw new KapuaEntityNotFoundException(Group.TYPE, groupId);
            }
        }
    }

    private void validateGroupIds(KapuaId scopeId, Set<KapuaId> groupIds) throws KapuaException {
        //
        // Check existence of target Groups
        if (!groupIds.isEmpty()) {

            GroupQuery groupQuery = groupFactory.newQuery(scopeId);
            groupQuery.setPredicate(groupQuery.attributePredicate(GroupAttributes.ENTITY_ID, groupIds));

            GroupListResult dbGroups = KapuaSecurityUtils.doPrivileged(() -> groupService.query(groupQuery));
            if (groupIds.size() != dbGroups.getSize()) {
                // Some groups have not been found
                Set<KapuaId> dbGroupIds =
                        dbGroups.getItems()
                                .stream()
                                .map(Group::getId)
                                .collect(Collectors.toSet());

                for (KapuaId groupId : groupIds) {
                    if (!dbGroupIds.contains(groupId)) {
                        throw new KapuaEntityNotFoundException(Group.TYPE, groupId);
                    }
                }
            }
        }
    }

    private void checkAccessGroupIds(TxContext txContext, User user) throws KapuaException {
        //
        // Check that current User can manage at least one of the current Groups of the User
        Set<KapuaId> currentGroupIds = findCurrentGroupIds(txContext, user.getScopeId(), user.getId());

        Set<Permission> currentGroupPermissions =
                currentGroupIds.stream()
                        .map(groupId -> permissionFactory.newPermission(Domains.USER, Actions.write, user.getScopeId(), groupId))
                        .collect(Collectors.toSet());

        authorizationService.checkPermissions(currentGroupPermissions, CheckStrategy.AT_LEAST_ONE_OF);

        //
        // Check access to Groups that have been changed
        Set<KapuaId> updatedGroupIds = new HashSet<>();

        // Added groups - all Groups present in the updated User but not on DB
        updatedGroupIds.addAll(
                user.getGroupIds()
                        .stream()
                        .filter(groupId -> !currentGroupIds.contains(groupId))
                        .collect(Collectors.toSet())
        );

        // Removed groups - all Groups not present in the updated User but exists on DB
        updatedGroupIds.addAll(
                currentGroupIds
                        .stream()
                        .filter(currentGroupId1-> !user.getGroupIds().contains(currentGroupId1))
                        .collect(Collectors.toSet())
        );

        // If any of the Group have been changed, check authorization for those Groups
        if (!updatedGroupIds.isEmpty()) {
            Set<Permission> groupPermissions =
                    updatedGroupIds
                            .stream()
                            .map(groupId -> permissionFactory.newPermission(Domains.USER, Actions.write, user.getScopeId(), groupId))
                            .collect(Collectors.toSet());

            authorizationService.checkPermissions(groupPermissions);
        }
    }

    /**
     * Applies validation logics to {@link User#getTagIds()} attribute.
     * <p>
     * Requirements are:
     * <ul>
     *     <li>The Tags defined must exists</li>
     * </ul>
     *
     * @param tagIds The {@link User} to check
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


    /**
     * Finds the current Set of {@link Group} ids assigned to the given {@link User}.
     *
     * @param scopeId  The {@link User#getScopeId()}
     * @param userId The {@link User#getId()}
     * @return The Set of {@link Group} ids found.
     * @throws KapuaException if any error occurs while looking for the Group.
     * @since 2.1.0
     */
    private Set<KapuaId> findCurrentGroupIds(TxContext tx, KapuaId scopeId, KapuaId userId) throws KapuaException {
        try {
            Optional<User> optionalUser = userRepository.find(tx, scopeId, userId);

            return optionalUser
                    .map(User::getGroupIds)
                    .orElse(Collections.emptySet());
        } catch (Exception e) {
            throw KapuaException.internalError(e, "Error while searching groupId");
        }
    }

    /**
     * Builds a set of {@link Permission} from a collection of Group Ids
     *
     * @param domain The {@link Permission#getDomain()}
     * @param action The {@link Permission#getAction()}
     * @param scopeId The {@link Permission#getTargetScopeId()}
     * @param groupIds The collection of Group Ids
     *
     * @return The set of {@link Permission}
     * @since 2.1.0
     */
    private Set<Permission> buildSetPermissionsFromGroupIds(String domain, Actions action, KapuaId scopeId, Collection<KapuaId> groupIds) {
        return groupIds.stream()
                .map(groupId -> permissionFactory.newPermission(domain, action, scopeId, groupId))
                .collect(Collectors.toSet());

    }

}
