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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.storage.TxContext;

/**
 * {@link UserService} validation utilities.
 *
 * @since 2.1.0
 */
public interface UserServiceValidationUtils {

    /**
     * Validates a {@link UserCreator} on {@link UserService#create(UserCreator)}
     *
     * @param userCreator The {@link UserCreator} to validate
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateCreatePreconditions(UserCreator userCreator) throws KapuaException;

    /**
     * Validates a {@link User} on {@link UserService#create(UserCreator)} in a {@link TxContext}
     *
     * @param txContext The {@link TxContext} to use
     * @param userCreator The {@link UserCreator} to validate
     * @throws KapuaException
     *
     * @since 2.1.0
     */
    void validateCreateInTransaction(TxContext txContext, UserCreator userCreator) throws KapuaException;

    /**
     * Validates a {@link User} on {@link UserService#update(User)}
     *
     * @param user The {@link User} to validate
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateUpdatePreconditions(User user) throws KapuaException;

    /**
     * Validates a {@link User} on {@link UserService#update(User)} in a {@link TxContext}
     *
     * @param txContext The {@link TxContext} to use
     * @param user The {@link User} to validate
     * @throws KapuaException
     *
     * @since 2.1.0
     */
    void validateUpdateInTransaction(TxContext txContext, User user) throws KapuaException;

    /**
     * Validates inputs for {@link UserService#find(KapuaId, KapuaId)}
     *
     * @param scopeId The {@link User#getScopeId()}
     * @param userId The {@link User#getId()}
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateFindPreconditions(KapuaId scopeId, KapuaId userId) throws KapuaException;

    /**
     * Validates inputs for {@link UserService#find(KapuaId)}
     *
     * @param userId The {@link User#getId()}
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateFindByIdPreConditions(KapuaId userId) throws KapuaException;

    /**
     * Validates inputs for {@link UserService#findByName(String)}
     *
     * @param name The {@link User#getName()}
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateFindByNamePreConditions(String name) throws KapuaException;

    /**
     * Validates inputs for {@link UserService#findByExternalId(String)}
     *
     * @param externalId The {@link User#getExternalId()}
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateFindByExternalIdPreConditions(String externalId) throws KapuaException;

    /**
     * Validates inputs for {@link UserService#findByExternalUsername(String)}
     *
     * @param externalUsername The {@link User#getExternalUsername()}
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateFindByExternalUsernamePreConditions(String externalUsername) throws KapuaException;

    /**
     * Validates {@link User} found for:
     * <ul>
     *     <li>{@link UserService#find(KapuaId)}</li>
     *     <li>{@link UserService#findByName(String)}</li>
     *     <li>{@link UserService#findByExternalId(String)}</li>
     *     <li>{@link UserService#findByExternalUsername(String)}</li>
     * </ul>
     * @param user The {@link User} found, can be {@code null}
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateFindByFieldPostConditions(User user) throws KapuaException;

    /**
     * Validates {@link KapuaQuery} for {@link UserService#query(KapuaQuery)}
     *
     * @param query The {@link KapuaQuery} to validate
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateQueryPreconditions(KapuaQuery query) throws KapuaException;

    /**
     * Validates {@link KapuaQuery} for {@link UserService#count(KapuaQuery)}
     *
     * @param query The {@link KapuaQuery} to validate
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateCountPreconditions(KapuaQuery query) throws KapuaException;

    /**
     * Validates a {@link User} on {@link UserService#delete(KapuaId, KapuaId)}
     *
     * @param scopeId The {@link User#getScopeId()}
     * @param userId The {@link User#getId()}
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateDeletePreconditions(KapuaId scopeId, KapuaId userId) throws KapuaException;

    /**
     * Validates a {@link User} on {@link UserService#delete(KapuaId, KapuaId)} in a {@link TxContext}
     *
     * @param txContext The {@link TxContext} to use
     * @param scopeId The {@link User#getScopeId()}
     * @param userId The {@link User#getId()}
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateDeleteInTransaction(TxContext txContext, KapuaId scopeId, KapuaId userId) throws KapuaException;

}
