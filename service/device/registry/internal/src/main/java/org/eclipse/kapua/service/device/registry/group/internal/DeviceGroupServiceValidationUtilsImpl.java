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

import com.google.common.collect.Sets;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.CheckStrategy;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.device.registry.group.DeviceGroup;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupCreator;

import java.util.Set;

/**
 * {@link DeviceGroupServiceValidationUtils} implementation.
 *
 * @since 2.1.0
 */
public final class DeviceGroupServiceValidationUtilsImpl implements DeviceGroupServiceValidationUtils {

    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final GroupService groupService;

    public DeviceGroupServiceValidationUtilsImpl(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            GroupService groupService
    ) {
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.groupService = groupService;
    }

    @Override
    public void validateCreatePreConditions(DeviceGroupCreator deviceGroupCreator) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(deviceGroupCreator.getScopeId(), "deviceGroupCreator.scopeId");
        ArgumentValidator.notEmptyOrNull(deviceGroupCreator.getName(), "deviceGroupCreator.name");
        ArgumentValidator.validateEntityName(deviceGroupCreator.getName(), "deviceGroupCreator.name");

        //
        // Check Access
        Set<Permission> permissions = Sets.newHashSet(
                permissionFactory.newPermission(Domains.GROUP, Actions.write, deviceGroupCreator.getScopeId()),
                permissionFactory.newPermission(Domains.DEVICE_GROUP, Actions.write, deviceGroupCreator.getScopeId())
        );
        authorizationService.checkPermissions(permissions, CheckStrategy.AT_LEAST_ONE_OF);
    }

    @Override
    public void validateUpdatePreConditions(DeviceGroup deviceGroup) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(deviceGroup, "deviceGroup");
        ArgumentValidator.notNull(deviceGroup.getId(), "deviceGroup.id");
        ArgumentValidator.notNull(deviceGroup.getScopeId(), "deviceGroup.scopeId");
        ArgumentValidator.validateEntityName(deviceGroup.getName(), "deviceGroup.name");

        //
        // Check Access
        Set<Permission> permissions = Sets.newHashSet(
                permissionFactory.newPermission(Domains.GROUP, Actions.write, deviceGroup.getScopeId()),
                permissionFactory.newPermission(Domains.DEVICE_GROUP, Actions.write, deviceGroup.getScopeId())
        );
        authorizationService.checkPermissions(permissions, CheckStrategy.AT_LEAST_ONE_OF);

        // Check correct domain
        checkGroupDomainIsDevice(deviceGroup.getScopeId(), deviceGroup.getId());
    }

    @Override
    public void validateFindPreConditions(KapuaId scopeId, KapuaId deviceGroupId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(deviceGroupId, "deviceGroupId");

        // Check access
        Set<Permission> permissions = Sets.newHashSet(
                permissionFactory.newPermission(Domains.GROUP, Actions.read, scopeId),
                permissionFactory.newPermission(Domains.DEVICE_GROUP, Actions.read, scopeId)
        );
        authorizationService.checkPermissions(permissions, CheckStrategy.AT_LEAST_ONE_OF);
    }

    @Override
    public void validateFindPostConditions(DeviceGroup deviceGroup) {
        if (deviceGroup != null) {

        }
    }

    @Override
    public void validateQueryPreConditions(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");

        // Check Access
        Set<Permission> permissions = Sets.newHashSet(
                permissionFactory.newPermission(Domains.GROUP, Actions.read, query.getScopeId()),
                permissionFactory.newPermission(Domains.DEVICE_GROUP, Actions.read, query.getScopeId())
        );
        authorizationService.checkPermissions(permissions, CheckStrategy.AT_LEAST_ONE_OF);
    }

    @Override
    public void validateCountPreConditions(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");

        // Check Access
        Set<Permission> permissions = Sets.newHashSet(
                permissionFactory.newPermission(Domains.GROUP, Actions.read, query.getScopeId()),
                permissionFactory.newPermission(Domains.DEVICE_GROUP, Actions.read, query.getScopeId())
        );
        authorizationService.checkPermissions(permissions, CheckStrategy.AT_LEAST_ONE_OF);
    }

    @Override
    public void validateDeletePreConditions(KapuaId scopeId, KapuaId deviceGroupId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(deviceGroupId, "id");

        // Check Access
        Set<Permission> permissions = Sets.newHashSet(
                permissionFactory.newPermission(Domains.GROUP, Actions.delete, scopeId),
                permissionFactory.newPermission(Domains.DEVICE_GROUP, Actions.delete, scopeId)
        );
        authorizationService.checkPermissions(permissions, CheckStrategy.AT_LEAST_ONE_OF);

        // Check correct domain
        checkGroupDomainIsDevice(scopeId, deviceGroupId);
    }

    //
    // Private methods
    //

    private void checkGroupDomainIsDevice(KapuaId scopeId, KapuaId deviceGroupId) throws KapuaException {
        // Check Group Domain is `device`
        Group group = KapuaSecurityUtils.doPrivileged(() -> groupService.find(scopeId, deviceGroupId));

        if (group == null || !group.getDomain().equals("device")) {
            throw new KapuaEntityNotFoundException(DeviceGroup.TYPE, deviceGroupId);
        }
    }
}
