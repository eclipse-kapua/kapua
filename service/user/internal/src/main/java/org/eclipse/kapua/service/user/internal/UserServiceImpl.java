/*******************************************************************************
 * Copyright (c) 2016, 2025 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.configuration.KapuaConfigurableServiceBase;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.jpa.EventStorer;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.event.ServiceEvent;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserFactory;
import org.eclipse.kapua.service.user.UserListResult;
import org.eclipse.kapua.service.user.UserQuery;
import org.eclipse.kapua.service.user.UserRepository;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

/**
 * {@link UserService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class UserServiceImpl extends KapuaConfigurableServiceBase implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserServiceValidationUtils userServiceValidationUtils;

    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final EventStorer eventStorer;

    public UserServiceImpl(
            TxManager txManager,
            ServiceConfigurationManager serviceConfigurationManager,
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            UserFactory userFactory,
            UserServiceValidationUtils userServiceValidationUtils,
            UserRepository userRepository,
            EventStorer eventStorer
    ) {
        super(
            txManager,
            serviceConfigurationManager,
            Domains.USER,
            authorizationService,
            permissionFactory
        );
        this.userServiceValidationUtils = userServiceValidationUtils;
        this.userRepository = userRepository;
        this.userFactory = userFactory;
        this.eventStorer = eventStorer;
    }

    @Override
    public User create(UserCreator userCreator) throws KapuaException {

        // Validate UserCreator
        userServiceValidationUtils.validateCreatePreconditions(userCreator);

        // Do create
        return txManager.execute(tx -> {
            // Check entity limit
            serviceConfigurationManager.checkAllowedEntities(tx, userCreator.getScopeId(), "Users");

            // Validate in-transaction conditions
            userServiceValidationUtils.validateCreateInTransaction(tx, userCreator);

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
        //
        // Validate User
        userServiceValidationUtils.validateUpdatePreconditions(user);

        //
        // Do update
        return txManager.execute(
                tx -> {
                    // Validate in-transaction conditions
                    userServiceValidationUtils.validateUpdateInTransaction(tx, user);

                    // Do update
                    return userRepository.update(tx, user);
                },
                eventStorer::accept);
    }

    @Override
    public User find(KapuaId scopeId, KapuaId userId)
            throws KapuaException {
        // Validate preconditions
        userServiceValidationUtils.validateFindPreconditions(scopeId, userId);

        // Do find
        return txManager
                .execute(tx -> userRepository.find(tx, scopeId, userId))
                .orElse(null);
    }

    @Override
    public User find(KapuaId userId)
            throws KapuaException {
        // Validate preconditions
        userServiceValidationUtils.validateFindByIdPreConditions(userId);

        // Do find
        User user = txManager
                .execute(tx -> userRepository.find(tx, userId))
                .orElse(null);

        // Validate post conditions
        userServiceValidationUtils.validateFindByFieldPostConditions(user);

        // Return result
        return user;
    }

    @Override
    public User findByName(String name) throws KapuaException {
        // Validate preconditions
        userServiceValidationUtils.validateFindByNamePreConditions(name);

        // Do find
        User user = txManager
                .execute(tx -> userRepository.findByName(tx, name))
                .orElse(null);

        // Validate post conditions
        userServiceValidationUtils.validateFindByFieldPostConditions(user);

        // Return result
        return user;
    }

    @Override
    public User findByExternalId(String externalId) throws KapuaException {
        // Validate preconditions
        userServiceValidationUtils.validateFindByExternalIdPreConditions(externalId);

        // Do find
        User user = txManager
                .execute(tx -> userRepository.findByExternalId(tx, externalId))
                .orElse(null);

        // Validate post conditions
        userServiceValidationUtils.validateFindByFieldPostConditions(user);

        // Return result
        return user;
    }

    @Override
    public User findByExternalUsername(String externalUsername) throws KapuaException {
        // Validate preconditions
        userServiceValidationUtils.validateFindByExternalUsernamePreConditions(externalUsername);

        // Do find
        User user = txManager
                .execute(tx -> userRepository.findByExternalUsername(tx, externalUsername))
                .orElse(null);

        // Validate post conditions
        userServiceValidationUtils.validateFindByFieldPostConditions(user);

        // Return result
        return user;
    }

    @Override
    public UserListResult query(KapuaQuery query) throws KapuaException {
        // Validate preconditions
        userServiceValidationUtils.validateQueryPreconditions(query);

        // Do query
        return txManager.execute(tx -> userRepository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        // Validate preconditions
        userServiceValidationUtils.validateCountPreconditions(query);

        // Do count
        return txManager.execute(tx -> userRepository.count(tx, query));
    }

    /**
     * @deprecated {@link UserService#delete(User)} has been deprecated.
     */
    @Override
    @Deprecated
    public void delete(User user) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(user, "user");

        // Do delete
        delete(user.getScopeId(), user.getId());
    }

    @Override
    //@RaiseServiceEvent
    public void delete(KapuaId scopeId, KapuaId userId) throws KapuaException {
        // Validate preconditions
        userServiceValidationUtils.validateDeletePreconditions(scopeId, userId);

        // Do delete
        txManager.execute(
                tx -> {
                    // Validate in-transaction conditions
                    userServiceValidationUtils.validateDeleteInTransaction(tx, scopeId, userId);

                    // Do  delete
                    return userRepository.delete(tx, scopeId, userId);
                },
                eventStorer::accept);
    }


    // -----------------------------------------------------------------------------------------
    // Private Methods
    // -----------------------------------------------------------------------------------------

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
        UserQuery query = new UserQueryImpl(accountId);
        UserListResult usersToDelete = query(query);

        for (User u : usersToDelete.getItems()) {
            delete(u.getScopeId(), u.getId());
        }
    }

}
