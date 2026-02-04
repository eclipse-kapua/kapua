/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.authorization.shared.util;

import org.eclipse.kapua.app.console.module.api.shared.util.KapuaGwtCommonsModelConverter;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtAccessInfo;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtAccessPermission;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtAccessRole;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtDomain;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtPermission;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtPermission.GwtAction;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtRole;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtRolePermission;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.access.AccessRole;
import org.eclipse.kapua.service.authorization.domain.Domain;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RolePermission;

public class KapuaGwtAuthorizationModelConverter {

    private KapuaGwtAuthorizationModelConverter() {
    }

    /**
     * Converts a {@link Group} into a {@link GwtGroup}
     *
     * @param group The {@link Group} to convertKapuaId
     * @return The converted {@link GwtGroup}
     * @since 1.0.0
     */
    public static GwtGroup convertGroup(Group group) {

        GwtGroup gwtGroup = new GwtGroup();
        // Covert commons attributes
        KapuaGwtCommonsModelConverter.convertUpdatableEntity(group, gwtGroup);
        // Convert other attributes
        gwtGroup.setGroupName(group.getName());
        gwtGroup.setGroupDescription(group.getDescription());
        gwtGroup.setDomain(group.getDomain());

        return gwtGroup;
    }

    /**
     * Converts a {@link Role} into a {@link GwtRole} object for GWT usage.
     *
     * @param role The {@link Role} to convertKapuaId.
     * @return The converted {@link GwtRole}.
     * @since 1.0.0
     */
    public static GwtRole convertRole(Role role) {
        GwtRole gwtRole = new GwtRole();
        // Covert commons attributes
        KapuaGwtCommonsModelConverter.convertUpdatableEntity(role, gwtRole);
        // Convert other attributes
        gwtRole.setName(role.getName());
        gwtRole.setDescription(role.getDescription());
        // Return converted entity
        return gwtRole;
    }

    /**
     * Merges a {@link Role} and a {@link AccessRole} into a {@link GwtAccessRole} object for GWT usage.
     *
     * @param role       The {@link Role} to merge.
     * @param accessRole The {@link AccessRole} to merge.
     * @return The converted {@link GwtAccessRole}.
     * @since 1.0.0
     */
    public static GwtAccessRole mergeRoleAccessRole(Role role, AccessRole accessRole) {
        GwtAccessRole gwtAccessRole = new GwtAccessRole();
        // Covert commons attributes
        KapuaGwtCommonsModelConverter.convertEntity(accessRole, gwtAccessRole);
        // Convert other attributes
        gwtAccessRole.setRoleName(role.getName());
        gwtAccessRole.setRoleDescription(role.getDescription());
        gwtAccessRole.setRoleId(role.getId().toCompactId());
        gwtAccessRole.setAccessInfoId(accessRole.getAccessInfoId().toCompactId());
        // Return converted entity
        return gwtAccessRole;
    }

    /**
     * Converts a {@link AccessRole} into a {@link GwtAccessRole} object for GWT usage.
     *
     * @param accessRole The {@link AccessRole} to convertKapuaId.
     * @return The converted {@link GwtAccessRole}.
     * @since 1.0.0
     */
    public static GwtAccessRole convertAccessRole(AccessRole accessRole) {
        GwtAccessRole gwtAccessRole = new GwtAccessRole();
        // Covert commons attributes
        KapuaGwtCommonsModelConverter.convertEntity(accessRole, gwtAccessRole);

        gwtAccessRole.setRoleId(accessRole.getRoleId().toCompactId());
        gwtAccessRole.setAccessInfoId(accessRole.getAccessInfoId().toCompactId());
        // Return converted entity
        return gwtAccessRole;
    }

    /**
     * Converts a {@link AccessPermission} into a {@link GwtAccessPermission} object for GWT usage.
     *
     * @param accessPermission The {@link AccessPermission} to convertKapuaId.
     * @return The converted {@link GwtAccessPermission}.
     * @since 1.0.0
     */
    public static GwtAccessPermission convertAccessPermission(AccessPermission accessPermission) {
        GwtAccessPermission gwtAccessPermission = new GwtAccessPermission();
        // Covert commons attributes
        KapuaGwtCommonsModelConverter.convertEntity(accessPermission, gwtAccessPermission);

        gwtAccessPermission.setAccessInfoId(accessPermission.getAccessInfoId().toCompactId());
        gwtAccessPermission.setPermissionDomain(accessPermission.getPermission().getDomain());

        if (accessPermission.getPermission().getAction() != null) {
            gwtAccessPermission.setPermissionAction(accessPermission.getPermission().getAction().toString());
        } else {
            gwtAccessPermission.setPermissionAction("ALL");
        }

        if (accessPermission.getPermission().getTargetScopeId() != null) {
            gwtAccessPermission.setPermissionTargetScopeId(accessPermission.getPermission().getTargetScopeId().toCompactId());
        } else {
            gwtAccessPermission.setPermissionTargetScopeId("ALL");
        }

        if (accessPermission.getPermission().getGroupId() != null) {
            gwtAccessPermission.setPermissionGroupId(accessPermission.getPermission().getGroupId().toCompactId());
        } else {
            gwtAccessPermission.setPermissionGroupId("ALL");
        }

        gwtAccessPermission.setPermissionForwardable(accessPermission.getPermission().getForwardable());
        // Return converted entity
        return gwtAccessPermission;
    }

    /**
     * Converts a {@link AccessInfo} into a {@link GwtAccessInfo} object for GWT usage.
     *
     * @param accessInfo The {@link AccessInfo} to convertKapuaId.
     * @return The converted {@link GwtAccessInfo}.
     * @since 1.0.0
     */
    public static GwtAccessInfo convertAccessInfo(AccessInfo accessInfo) {
        GwtAccessInfo gwtAccessInfo = new GwtAccessInfo();
        // Covert commons attributes
        KapuaGwtCommonsModelConverter.convertEntity(accessInfo, gwtAccessInfo);

        gwtAccessInfo.setUserId(accessInfo.getUserId().toCompactId());
        // Return converted entity
        return gwtAccessInfo;
    }

    /**
     * Converts a {@link RolePermission} into a {@link GwtRolePermission} object for GWT usage.
     *
     * @param rolePermission The {@link RolePermission} to convertKapuaId
     * @return The converted {@link GwtRolePermission}
     * @since 1.0.0
     */
    public static GwtRolePermission convertRolePermission(RolePermission rolePermission) {
        GwtRolePermission gwtRolePermission = new GwtRolePermission();
        // Covert commons attributes
        KapuaGwtCommonsModelConverter.convertEntity(rolePermission, gwtRolePermission);
        // Convert other attributes
        GwtPermission gwtPermission = convertPermission((Permission) rolePermission.getPermission());

        gwtRolePermission.setRoleId(KapuaGwtCommonsModelConverter.convertKapuaId(rolePermission.getRoleId()));
        gwtRolePermission.setDomain(gwtPermission.getDomain());
        gwtRolePermission.setAction(gwtPermission.getAction());
        gwtRolePermission.setGroupId(gwtPermission.getGroupId());
        gwtRolePermission.setTargetScopeId(gwtPermission.getTargetScopeId());
        gwtRolePermission.setForwardable(gwtPermission.getForwardable());
        // Return converted entity
        return gwtRolePermission;
    }

    /**
     * Converts a {@link Permission} into a {@link GwtPermission} object for GWT usage.
     *
     * @param permission The {@link Permission} to convertKapuaId.
     * @return The converted {@link GwtPermission}.
     * @since 1.0.0
     */
    public static GwtPermission convertPermission(Permission permission) {
        return new GwtPermission(permission.getDomain(),
                convertAction(permission.getAction()),
                KapuaGwtCommonsModelConverter.convertKapuaId(permission.getTargetScopeId()),
                KapuaGwtCommonsModelConverter.convertKapuaId(permission.getGroupId()),
                permission.getForwardable());
    }

    /**
     * Converts a {@link Actions} into a {@link GwtAction}
     *
     * @param action The {@link Actions} to convertKapuaId
     * @return The converted {@link GwtAction}
     * @since 1.0.0
     */
    public static GwtAction convertAction(Actions action) {

        GwtAction gwtAction = null;
        if (action != null) {
            switch (action) {
                case connect:
                    gwtAction = GwtAction.connect;
                    break;
                case delete:
                    gwtAction = GwtAction.delete;
                    break;
                case execute:
                    gwtAction = GwtAction.execute;
                    break;
                case read:
                    gwtAction = GwtAction.read;
                    break;
                case write:
                    gwtAction = GwtAction.write;
                    break;
            }
        }
        return gwtAction;
    }

    /**
     * Converts a {@link String} domain into a {@link GwtDomain}
     *
     * @param domain the {@link Domain} to convert
     * @return The converted {@link GwtDomain}
     * @since 1.0.0
     */
    public static GwtDomain convertDomain(Domain domain) {
        GwtDomain gwtDomain = new GwtDomain();
        gwtDomain.setDomainId(domain.getName());
        gwtDomain.setDomainName(domain.getName());
        gwtDomain.setGroupable(domain.getGroupable());

        return gwtDomain;
    }

    /**
     * Converts a {@link String} action into a {@link GwtAction}
     *
     * @param action The {@link String} action to convertKapuaId
     * @return The converted {@link GwtAction}
     * @since 1.0.0
     */
    public static GwtAction convertAction(String action) {
        return GwtAction.valueOf(action);
    }

}
