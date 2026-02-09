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
package org.eclipse.kapua.service.device.registry.group.internal;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.device.registry.group.DeviceGroup;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupCreator;

/**
 * {@link DeviceGroupServiceImpl} validation utilities.
 *
 * @since 2.1.0
 */
public interface DeviceGroupServiceValidationUtils {

    /**
     * Validates a {@link DeviceGroupCreator} on {@link DeviceGroupServiceImpl#create(DeviceGroupCreator)}
     *
     * @param deviceGroupCreator The {@link DeviceGroupCreator} to validate
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateCreatePreConditions(DeviceGroupCreator deviceGroupCreator) throws KapuaException;

    /**
     * Validates a {@link DeviceGroup} on {@link DeviceGroupServiceImpl#update(DeviceGroup)}
     *
     * @param deviceGroup The {@link DeviceGroup} to validate
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateUpdatePreConditions(DeviceGroup deviceGroup) throws KapuaException;

    /**
     * Validates inputs for {@link DeviceGroupServiceImpl#find(KapuaId, KapuaId)}
     *
     * @param scopeId The {@link DeviceGroup#getScopeId()}
     * @param deviceGroupId The {@link DeviceGroup#getId()}
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateFindPreConditions(KapuaId scopeId, KapuaId deviceGroupId) throws KapuaException;

    /**
     * Validates output for {@link DeviceGroupServiceImpl#find(KapuaId, KapuaId)}

     * @param deviceGroup The {@link DeviceGroup} to check
     * @since 2.1.0
     */
    void validateFindPostConditions(DeviceGroup deviceGroup);

    /**
     * Validates {@link KapuaQuery} for {@link DeviceGroupServiceImpl#query(KapuaQuery)}
     *
     * @param query The {@link KapuaQuery} to validate
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateQueryPreConditions(KapuaQuery query) throws KapuaException;

    /**
     * Validates {@link KapuaQuery} for {@link DeviceGroupServiceImpl#count(KapuaQuery)}
     *
     * @param query The {@link KapuaQuery} to validate
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateCountPreConditions(KapuaQuery query) throws KapuaException;

    /**
     * Validates a {@link DeviceGroup} on {@link DeviceGroupServiceImpl#delete(KapuaId, KapuaId)}
     *
     * @param scopeId The {@link DeviceGroup#getScopeId()}
     * @param deviceGroupId The {@link DeviceGroup#getId()}
     * @throws KapuaException
     * @since 2.1.0
     */
    void validateDeletePreConditions(KapuaId scopeId, KapuaId deviceGroupId) throws KapuaException;



}
