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
package org.eclipse.kapua.service.user.group.internal;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.user.group.UserGroup;
import org.eclipse.kapua.service.user.group.UserGroupCreator;

/**
 * {@link UserGroupServiceImpl} validation utilities.
 *
 * @since 2.1.0
 */
public interface UserGroupServiceValidationUtils {

    /**
     * Validates a {@link UserGroupCreator} on {@link UserGroupServiceImpl#create(UserGroupCreator)}
     *
     * @param userGroupCreator The {@link UserGroupCreator} to validate
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateCreatePreConditions(UserGroupCreator userGroupCreator) throws KapuaException;

    /**
     * Validates a {@link UserGroup} on {@link UserGroupServiceImpl#update(UserGroup)}
     *
     * @param userGroup The {@link UserGroup} to validate
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateUpdatePreConditions(UserGroup userGroup) throws KapuaException;

    /**
     * Validates inputs for {@link UserGroupServiceImpl#find(KapuaId, KapuaId)}
     *
     * @param scopeId The {@link UserGroup#getScopeId()}
     * @param userGroupId The {@link UserGroup#getId()}
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateFindPreConditions(KapuaId scopeId, KapuaId userGroupId) throws KapuaException;

    /**
     * Validates output for {@link UserGroupServiceImpl#find(KapuaId, KapuaId)}

     * @param userGroup The {@link UserGroup} to check
     * @since 2.1.0
     */
    void validateFindPostConditions(UserGroup userGroup);

    /**
     * Validates inputs for {@link UserGroupServiceImpl#fetchPermissions(KapuaId, KapuaId)}
     *
     * @param scopeId The {@link UserGroup#getScopeId()}
     * @param userGroupId The {@link UserGroup#getId()}
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateFetchPermissionPreConditions(KapuaId scopeId, KapuaId userGroupId) throws KapuaException;

    /**
     * Validates {@link KapuaQuery} for {@link UserGroupServiceImpl#query(KapuaQuery)}
     *
     * @param query The {@link KapuaQuery} to validate
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateQueryPreConditions(KapuaQuery query) throws KapuaException;

    /**
     * Validates {@link KapuaQuery} for {@link UserGroupServiceImpl#count(KapuaQuery)}
     *
     * @param query The {@link KapuaQuery} to validate
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateCountPreConditions(KapuaQuery query) throws KapuaException;

    /**
     * Validates a {@link UserGroup} on {@link UserGroupServiceImpl#delete(KapuaId, KapuaId)}
     *
     * @param scopeId The {@link UserGroup#getScopeId()}
     * @param userGroupId The {@link UserGroup#getId()}
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateDeletePreConditions(KapuaId scopeId, KapuaId userGroupId) throws KapuaException;



}
