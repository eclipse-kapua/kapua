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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.service.user.internal;

import java.util.Objects;
import java.util.Optional;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaDuplicateExternalIdException;
import org.eclipse.kapua.KapuaDuplicateExternalUsernameException;
import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.KapuaDuplicateNameInAnotherAccountError;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.configuration.KapuaConfigurableServiceBase;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.jpa.EventStorer;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.CommonsValidationRegex;
import org.eclipse.kapua.event.ServiceEvent;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserFactory;
import org.eclipse.kapua.service.user.UserListResult;
import org.eclipse.kapua.service.user.UserQuery;
import org.eclipse.kapua.service.user.UserRepository;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.service.user.UserStatus;
import org.eclipse.kapua.service.user.UserType;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link UserService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class UserServiceImpl extends KapuaConfigurableServiceBase implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final EventStorer eventStorer;

    public UserServiceImpl(
            ServiceConfigurationManager serviceConfigurationManager,
            AuthorizationService authorizationService,
            TxManager txManager,
            UserRepository userRepository, UserFactory userFactory,
            EventStorer eventStorer) {
        super(txManager, serviceConfigurationManager, Domains.USER, authorizationService);
        this.userRepository = userRepository;
        this.userFactory = userFactory;
        this.eventStorer = eventStorer;
    }

    @Override
    public User create(UserCreator userCreator) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(userCreator.getScopeId().getId(), "userCreator.scopeId");
        ArgumentValidator.notEmptyOrNull(userCreator.getName(), "userCreator.name");
        ArgumentValidator.match(userCreator.getName(), CommonsValidationRegex.NAME_REGEXP, "userCreator.name");
        ArgumentValidator.lengthRange(userCreator.getName(), 3, 255, "userCreator.name");
        ArgumentValidator.match(userCreator.getEmail(), CommonsValidationRegex.EMAIL_REGEXP, "userCreator.email");
        ArgumentValidator.notNull(userCreator.getStatus(), "userCreator.status");

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
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.USER, Actions.write, userCreator.getScopeId()));

        return txManager.execute(tx -> {
            // Check entity limit
            serviceConfigurationManager.checkAllowedEntities(tx, userCreator.getScopeId(), "Users");
            // Check duplicate name
            if (userRepository.countEntitiesWithNameInScope(tx, userCreator.getScopeId(), userCreator.getName()) > 0) {
                throw new KapuaDuplicateNameException(userCreator.getName());
            }
            if (userRepository.countEntitiesWithName(tx, userCreator.getName()) > 0) {
                throw new KapuaDuplicateNameInAnotherAccountError(userCreator.getName());
            }
            // Check User.userType
            if (userCreator.getUserType() == UserType.EXTERNAL) {
                // Check duplicate externalId
                if (userCreator.getExternalId() != null) {
                    final Optional<User> userByExternalId = userRepository.findByExternalId(tx, userCreator.getExternalId());
                    if (userByExternalId.isPresent()) {
                        throw new KapuaDuplicateExternalIdException(userCreator.getExternalId());
                    }
                }

                // Check duplicate externalUsername
                if (userCreator.getExternalUsername() != null) {
                    Optional<User> userByExternalPreferredUserame = userRepository.findByExternalUsername(tx, userCreator.getExternalUsername());
                    if (userByExternalPreferredUserame.isPresent()) {
                        throw new KapuaDuplicateExternalUsernameException(userCreator.getExternalUsername());
                    }
                }
            }
            // Do create
            // Create User
            User user = userFactory.newEntity(userCreator.getScopeId());
            user.setName(userCreator.getName());
            user.setDisplayName(userCreator.getDisplayName());
            user.setEmail(userCreator.getEmail());
            user.setPhoneNumber(userCreator.getPhoneNumber());
            user.setUserType(userCreator.getUserType());
            user.setExternalId(userCreator.getExternalId());
            user.setExternalUsername(userCreator.getExternalUsername());
            user.setStatus(userCreator.getStatus());
            user.setExpirationDate(userCreator.getExpirationDate());
            return userRepository.create(tx, user);
        });
    }

    @Override
    //@RaiseServiceEvent
    public User update(User user) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(user.getId(), "user.id");
        ArgumentValidator.notNull(user.getScopeId(), "user.scopeId");
        ArgumentValidator.notEmptyOrNull(user.getName(), "user.name");
        ArgumentValidator.match(user.getName(), CommonsValidationRegex.NAME_REGEXP, "user.name");
        ArgumentValidator.lengthRange(user.getName(), 3, 255, "user.name");
        ArgumentValidator.match(user.getEmail(), CommonsValidationRegex.EMAIL_REGEXP, "user.email");
        ArgumentValidator.notNull(user.getStatus(), "user.status");
        ArgumentValidator.notNull(user.getUserType(), "user.userType");

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
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.USER, Actions.write, user.getScopeId()));

        return txManager.execute(
                tx -> {
                    // Check existence
                    User currentUser = userRepository.find(tx, user.getScopeId(), user.getId())
                            .orElseThrow(() -> new KapuaEntityNotFoundException(User.TYPE, user.getId()));
                    // Check action on Sys admin user
                    if (user.getExpirationDate() != null || !currentUser.getName().equals(user.getName())) {
                        // Check not deleting environment admin
                        validateSystemUser(user.getName());
                    }
                    // Check disabling on logged user
                    if (user.getId().equals(KapuaSecurityUtils.getSession().getUserId())) {
                        if (user.getStatus().equals(UserStatus.DISABLED)) {
                            throw new KapuaIllegalArgumentException("user.status", user.getStatus().name());
                        }
                    }
                    // Check not updatable fields

                    // User.userType
                    if (!Objects.equals(currentUser.getUserType(), user.getUserType())) {
                        throw new KapuaIllegalArgumentException("user.userType", user.getUserType().toString());
                    }

                    // User.name
                    if (!Objects.equals(currentUser.getName(), user.getName())) {
                        throw new KapuaIllegalArgumentException("user.name", user.getName());
                    }
                    // Check duplicates

                    // User.externalId
                    if (user.getExternalId() != null) {
                        if (userRepository.findByExternalId(tx, user.getExternalId())
                                .map(u -> u.getId())
                                .map(id -> !(id.equals(user.getId())))
                                .orElse(false)) {
                            throw new KapuaDuplicateExternalIdException(user.getExternalId());
                        }
                    }

                    // User.externalUsername
                    if (user.getExternalUsername() != null) {
                        if (userRepository.findByExternalUsername(tx, user.getExternalUsername())
                                .map(u -> u.getId())
                                .map(id -> !(id.equals(user.getId())))
                                .orElse(false)) {
                            throw new KapuaDuplicateExternalUsernameException(user.getExternalUsername());
                        }
                    }
                    // Do update
                    return userRepository.update(tx, user);
                },
                eventStorer::accept);
    }

    @Override
    public void delete(User user) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(user, "user");
        // Do delete
        txManager.execute(tx -> userRepository.delete(tx, user));
    }

    @Override
    //@RaiseServiceEvent
    public void delete(KapuaId scopeId, KapuaId userId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(userId.getId(), "user.id");
        ArgumentValidator.notNull(scopeId.getId(), "user.scopeId");

        // Check Access
        authorizationService.checkPermission(new Permission(Domains.USER, Actions.delete, scopeId));

        txManager.execute(
                tx -> {
                    // Check existence
                    User user = userRepository.find(tx, scopeId, userId)
                            .orElseThrow(() -> new KapuaEntityNotFoundException(User.TYPE, userId));

                    // Check not deleting environment admin
                    validateSystemUser(user.getName());

                    // Check not deleting self
                    validateSelf(user);

                    // Do  delete
                    return userRepository.delete(tx, user);
                },
                eventStorer::accept);
    }

    @Override
    public User find(KapuaId scopeId, KapuaId userId)
            throws KapuaException {
        // Validation of the fields
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(userId, "userId");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.USER, Actions.read, scopeId));

        // Do the find
        return txManager.execute(tx -> userRepository.find(tx, scopeId, userId))
                .orElse(null);
    }

    @Override
    public User findByName(String name) throws KapuaException {
        // Validation of the fields
        ArgumentValidator.notEmptyOrNull(name, "name");

        return checkReadAccess(txManager.execute(tx -> userRepository.findByName(tx, name)))
                .orElse(null);
    }

    @Override
    public User findByExternalId(String externalId) throws KapuaException {
        // Validation of the fields
        ArgumentValidator.notEmptyOrNull(externalId, "externalId");
        // Do the find
        return checkReadAccess(txManager.execute(tx -> userRepository.findByExternalId(tx, externalId)))
                .orElse(null);
    }

    @Override
    public User findByExternalUsername(String externalUsername) throws KapuaException {
        // Validation of the fields
        ArgumentValidator.notEmptyOrNull(externalUsername, "externalUsername");
        // Do the find
        return checkReadAccess(txManager.execute(tx -> userRepository.findByExternalUsername(tx, externalUsername)))
                .orElse(null);
    }

    @Override
    public UserListResult query(KapuaQuery query)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.USER, Actions.read, query.getScopeId()));
        // Do query
        return txManager.execute(tx -> userRepository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query)
            throws KapuaException {
        // Argument Validator
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(new Permission(Domains.USER, Actions.read, query.getScopeId()));
        // Do count
        return txManager.execute(tx -> userRepository.count(tx, query));
    }

    // -----------------------------------------------------------------------------------------
    // Private Methods
    // -----------------------------------------------------------------------------------------

    private Optional<User> checkReadAccess(Optional<User> user) throws KapuaException {
        if (user.isPresent()) {
            authorizationService.checkPermission(new Permission(Domains.USER, Actions.read, user.get().getScopeId()));
        }
        return user;
    }

    private void validateSystemUser(String name) throws KapuaException {
        String adminUsername = SystemSetting.getInstance().getString(SystemSettingKey.SYS_ADMIN_USERNAME);

        if (adminUsername.equals(name)) {
            throw new KapuaIllegalArgumentException("name", adminUsername);
        }
    }

    private void validateSelf(User user) throws KapuaException {
        if (user.getId().equals(KapuaSecurityUtils.getSession().getUserId())) {
            throw new KapuaIllegalArgumentException("name", user.getName());
        }
    }

    //@ListenServiceEvent(fromAddress = "account")
    public void onKapuaEvent(ServiceEvent kapuaEvent) throws KapuaException {
        if (kapuaEvent == null) {
            // service bus error. Throw some exception?
        }
        LOGGER.info("UserService: received kapua event from {}, operation {}", kapuaEvent.getService(), kapuaEvent.getOperation());
        if ("account".equals(kapuaEvent.getService()) && "delete".equals(kapuaEvent.getOperation())) {
            deleteUserByAccountId(kapuaEvent.getScopeId(), kapuaEvent.getEntityId());
        }
    }

    private void deleteUserByAccountId(KapuaId scopeId, KapuaId accountId) throws KapuaException {
        UserQuery query = new UserQuery(accountId);
        UserListResult usersToDelete = query(query);

        for (User u : usersToDelete.getItems()) {
            delete(u.getScopeId(), u.getId());
        }
    }

}
