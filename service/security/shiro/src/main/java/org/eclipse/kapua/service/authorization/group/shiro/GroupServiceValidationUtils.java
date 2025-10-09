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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupCreator;
import org.eclipse.kapua.service.authorization.group.GroupService;
import org.eclipse.kapua.storage.TxContext;

/**
 * {@link GroupService} validation utilities.
 *
 * @since 2.1.0
 */
public interface GroupServiceValidationUtils {

    /**
     * Validates a {@link GroupCreator} on {@link GroupService#create(GroupCreator)}
     *
     * @param groupCreator The {@link GroupCreator} to validate
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateCreatePreconditions(GroupCreator groupCreator) throws KapuaException;

    /**
     * Validates a {@link Group} on {@link GroupService#create(GroupCreator)} in a {@link TxContext}
     *
     * @param txContext The {@link TxContext} to use
     * @param groupCreator The {@link GroupCreator} to validate
     * @throws KapuaException
     *
     * @since 2.1.0
     */
    void validateCreateInTransaction(TxContext txContext, GroupCreator groupCreator) throws KapuaException;

    /**
     * Validates a {@link Group} on {@link GroupService#update(Group)}
     *
     * @param group The {@link Group} to validate
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateUpdatePreconditions(Group group) throws KapuaException;

    /**
     * Validates a {@link Group} on {@link GroupService#update(Group)} in a {@link TxContext}
     *
     * @param txContext The {@link TxContext} to use
     * @param group The {@link Group} to validate
     * @throws KapuaException
     *
     * @since 2.1.0
     */
    void validateUpdateInTransaction(TxContext txContext, Group group) throws KapuaException;

    /**
     * Validates inputs for {@link GroupService#find(KapuaId, KapuaId)}
     *
     * @param scopeId The {@link Group#getScopeId()}
     * @param groupId The {@link Group#getId()}
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateFindPreconditions(KapuaId scopeId, KapuaId groupId) throws KapuaException;

    /**
     * Validates {@link KapuaQuery} for {@link GroupService#query(KapuaQuery)}
     *
     * @param query The {@link KapuaQuery} to validate
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateQueryPreconditions(KapuaQuery query) throws KapuaException;

    /**
     * Validates {@link KapuaQuery} for {@link GroupService#count(KapuaQuery)}
     *
     * @param query The {@link KapuaQuery} to validate
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateCountPreconditions(KapuaQuery query) throws KapuaException;

    /**
     * Validates a {@link Group} on {@link GroupService#delete(KapuaId, KapuaId)}
     *
     * @param scopeId The {@link Group#getScopeId()}
     * @param groupId The {@link Group#getId()}
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateDeletePreconditions(KapuaId scopeId, KapuaId groupId) throws KapuaException;

    /**
     * Validates a {@link Group} on {@link GroupService#delete(KapuaId, KapuaId)} in a {@link TxContext}
     *
     * @param txContext The {@link TxContext} to use
     * @param scopeId The {@link Group#getScopeId()}
     * @param groupId The {@link Group#getId()}
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateDeleteInTransaction(TxContext txContext, KapuaId scopeId, KapuaId groupId) throws KapuaException;

}
