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
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserRepository;
import org.eclipse.kapua.service.user.UserStatus;
import org.eclipse.kapua.service.user.UserType;
import org.eclipse.kapua.storage.TxContext;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Optional;

/**
 * {@link UserServiceValidationUtils} implementation.
 *
 * @since 2.1.0
 */
public final class UserServiceValidationUtilsImpl implements UserServiceValidationUtils {

    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final UserRepository userRepository;

    public UserServiceValidationUtilsImpl(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            UserRepository userRepository
    ) {
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
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
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER, Actions.write, userCreator.getScopeId()));
    }

    @Override
    public void validateCreateInTransaction(TxContext txContext, UserCreator userCreator) throws KapuaException {

        // Check duplicate name in scope
        if (userRepository.countEntitiesWithNameInScope(txContext, userCreator.getScopeId(), userCreator.getName()) > 0) {
            throw new KapuaDuplicateNameException(userCreator.getName());
        }

        // Check duplicate name in other scopes
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

        //
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER, Actions.write, user.getScopeId()));
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
    }

    @Override
    public void validateFindPreconditions(KapuaId scopeId, KapuaId userId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(userId, "userId");

        // Check access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER, Actions.read, scopeId));
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
            authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER, Actions.read, user.getScopeId()));
        }
    }

    @Override
    public void validateQueryPreconditions(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER, Actions.read, query.getScopeId()));
    }

    @Override
    public void validateCountPreconditions(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER, Actions.read, query.getScopeId()));
    }

    @Override
    public void validateDeletePreconditions(KapuaId scopeId, KapuaId userId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(userId, "id");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.USER, Actions.delete, scopeId));
    }

    @Override
    public void validateDeleteInTransaction(TxContext txContext, KapuaId scopeId, KapuaId userId) throws KapuaException {
        // Check existence
        User user = userRepository
                .find(txContext, scopeId, userId)
                .orElseThrow(() -> new KapuaEntityNotFoundException(User.TYPE, userId));

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
}
