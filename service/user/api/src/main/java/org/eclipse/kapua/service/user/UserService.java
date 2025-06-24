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
 *******************************************************************************/
package org.eclipse.kapua.service.user;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.KapuaEntityService;
import org.eclipse.kapua.service.KapuaNamedEntityService;
import org.eclipse.kapua.service.KapuaUpdatableEntityService;
import org.eclipse.kapua.service.config.KapuaConfigurableService;

/**
 * UserService exposes APIs to manage User object under an Account.
 * <p>
 * It includes APIs to create, update, find, list and delete Users.<br>
 * Instances of the UserService can be acquired through the ServiceLocator.
 *
 * @since 1.0.0
 */
public interface UserService extends KapuaEntityService<User, UserCreator>,
        KapuaUpdatableEntityService<User>,
        KapuaNamedEntityService<User>,
        KapuaConfigurableService {

    /**
     * Creates a new user under the account specified in the UserCreator.<br>
     * The returned User object does not have its access information, roles
     * and permissions, loaded.
     *
     * @param userCreator
     * @return created User
     * @throws KapuaException
     */
    @Override
    User create(UserCreator userCreator) throws KapuaException;

    /**
     * Updates an User in the database and returns the refreshed/reloaded entity instance.<br>
     * The returned User object does not have its access information, roles
     * and permissions, loaded.
     *
     * @param user to be update
     * @return
     * @throws KapuaException
     */
    @Override
    User update(User user) throws KapuaException;

    /**
     * Deletes the given {@link User}.
     *
     * @param user The {@link User} to delete
     * @throws KapuaException
     * @deprecated Since 2.0.0. Please make use of {@link #delete(KapuaId, KapuaId)}
     */
    @Deprecated
    void delete(User user) throws KapuaException;

    /**
     * Returns the User with the specified Id; returns null if the user is not found.<br>
     * <b>The API does not perform any access control check and it is meant for internal use.</b>
     *
     * @param userId
     * @return
     * @throws KapuaException
     */
    @Override
    User find(KapuaId accountId, KapuaId userId) throws KapuaException;

    /**
     * Finds a {@link User} by its {@link User#getId()}
     *
     * @param userId The {@link User#getId()} to look for
     * @return The {@link User} found or {@code null}
     * @throws KapuaException
     * @since 2.1.0
     */
    User find(KapuaId userId) throws KapuaException;

    /**
     * Returns the User with the specified username; returns null if the user is not found.
     *
     * @return
     * @throws KapuaException
     */
    @Override
    User findByName(String name) throws KapuaException;

    /**
     * Find user by external id
     *
     * @param externalId the external ID to look for
     * @return the user or {@code null} if the user could not be found
     * @throws KapuaException in case anything goes wrong
     */
    User findByExternalId(String externalId) throws KapuaException;

    /**
     * Finds the {@link User} by it {@link User#getExternalUsername()}
     *
     * @param externalUsername The {@link User#getExternalUsername()}.
     * @return The matching {@link User} or {@code null}.
     * @throws KapuaException
     * @since 2.0.0
     */
    User findByExternalUsername(String externalUsername) throws KapuaException;

    /**
     * Queries for all users
     */
    @Override
    UserListResult query(KapuaQuery query) throws KapuaException;

}
