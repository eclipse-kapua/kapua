/*******************************************************************************
 * Copyright (c) 2017, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.user.server;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.server.util.UserCreatedByModifiedByUtils;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.authorization.server.util.GroupNameFromIdUtils;
import org.eclipse.kapua.app.console.module.authorization.server.util.RoleNameFromIdUtils;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupPermission;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupPermissionCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupPermissionQuery;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupRole;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupRoleCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupRoleQuery;
import org.eclipse.kapua.app.console.module.authorization.shared.util.GwtKapuaAuthorizationModelConverter;
import org.eclipse.kapua.app.console.module.authorization.shared.util.KapuaGwtAuthorizationModelConverter;
import org.eclipse.kapua.app.console.module.user.server.user.AccountNameFromIdUtils;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserGroupService;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.group.GroupPermission;
import org.eclipse.kapua.service.authorization.group.GroupPermissionCreator;
import org.eclipse.kapua.service.authorization.group.GroupPermissionFactory;
import org.eclipse.kapua.service.authorization.group.GroupPermissionListResult;
import org.eclipse.kapua.service.authorization.group.GroupPermissionQuery;
import org.eclipse.kapua.service.authorization.group.GroupPermissionService;
import org.eclipse.kapua.service.authorization.group.GroupRole;
import org.eclipse.kapua.service.authorization.group.GroupRoleCreator;
import org.eclipse.kapua.service.authorization.group.GroupRoleFactory;
import org.eclipse.kapua.service.authorization.group.GroupRoleListResult;
import org.eclipse.kapua.service.authorization.group.GroupRoleQuery;
import org.eclipse.kapua.service.authorization.group.GroupRoleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GwtUserGroupServiceImpl extends KapuaRemoteServiceServlet implements GwtUserGroupService {

    private static final long serialVersionUID = 7430961652373364113L;

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final GroupPermissionService GROUP_PERMISSION_SERVICE = LOCATOR.getService(GroupPermissionService.class);
    private static final GroupPermissionFactory GROUP_PERMISSION_FACTORY = LOCATOR.getFactory(GroupPermissionFactory.class);

    private static final GroupRoleService GROUP_ROLE_SERVICE = LOCATOR.getService(GroupRoleService.class);
    private static final GroupRoleFactory GROUP_ROLE_FACTORY = LOCATOR.getFactory(GroupRoleFactory.class);

    @Override
    public GwtGroupPermission createPermission(GwtXSRFToken xsrfToken, GwtGroupPermissionCreator gwtGroupPermissionCreator) throws GwtKapuaException {
        checkXSRFToken(xsrfToken);

        try {
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtGroupPermissionCreator.getScopeId());
            KapuaId groupId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtGroupPermissionCreator.getGroupId());

            GroupPermissionCreator groupPermissionCreator = GROUP_PERMISSION_FACTORY.newCreator(scopeId);
            groupPermissionCreator.setGroupId(groupId);
            groupPermissionCreator.setPermission(GwtKapuaAuthorizationModelConverter.convertPermission(gwtGroupPermissionCreator.getPermission()));

            GroupPermission groupPermission = GROUP_PERMISSION_SERVICE.create(groupPermissionCreator);

            return KapuaGwtAuthorizationModelConverter.convertGroupPermission(groupPermission);
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }

    }

    @Override
    public void deletePermission(GwtXSRFToken xsrfToken, String gwtScopeId, String gwtGroupPermissionId)
            throws GwtKapuaException {
        checkXSRFToken(xsrfToken);

        try {
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtScopeId);
            KapuaId groupPermissionId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtGroupPermissionId);

            GROUP_PERMISSION_SERVICE.delete(scopeId, groupPermissionId);
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }

    @Override
    public PagingLoadResult<GwtGroupPermission> queryPermission(PagingLoadConfig loadConfig, GwtGroupPermissionQuery gwtGroupPermissionQuery) throws GwtKapuaException {
        try {
            // Convert from GWT entity
            GroupPermissionQuery groupPermissionQuery = GwtKapuaAuthorizationModelConverter.convertGroupPermissionQuery(loadConfig, gwtGroupPermissionQuery);

            // Query
            GroupPermissionListResult groupPermissions = GROUP_PERMISSION_SERVICE.query(groupPermissionQuery);

            // If there are results
            List<GwtGroupPermission> gwtGroupPermissions = new ArrayList<GwtGroupPermission>();
            if (!groupPermissions.isEmpty()) {

                Map<KapuaId, String> idAccountNameMap = AccountNameFromIdUtils.resolveAccountNamesFrom(groupPermissions);
                Map<KapuaId, String> idUsernameMap = UserCreatedByModifiedByUtils.resolveFromListResult(groupPermissions);
                Map<KapuaId, String> idGroupNameMap = GroupNameFromIdUtils.resolveGroupNamesFrom(groupPermissions);

                // Convert to GWT entity
                for (GroupPermission groupPermission : groupPermissions.getItems()) {
                    GwtGroupPermission gwtGroupPermission = KapuaGwtAuthorizationModelConverter.convertGroupPermission(groupPermission);

                    gwtGroupPermission.setCreatedByName(idUsernameMap.get(groupPermission.getCreatedBy()));
                    gwtGroupPermission.setPermissionTargetScopeIdByName(idAccountNameMap.get(groupPermission.getPermission().getTargetScopeId()));
                    gwtGroupPermission.setGroupName(idGroupNameMap.get(groupPermission.getPermission().getGroupId()));

                    gwtGroupPermissions.add(gwtGroupPermission);
                }
            }

            return new BasePagingLoadResult<GwtGroupPermission>(gwtGroupPermissions, loadConfig.getOffset(), groupPermissions.getTotalCount().intValue());
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }

    @Override
    public GwtGroupRole createRole(GwtXSRFToken xsrfToken, GwtGroupRoleCreator gwtGroupRoleCreator) throws GwtKapuaException {
        checkXSRFToken(xsrfToken);

        try {
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtGroupRoleCreator.getScopeId());
            KapuaId groupId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtGroupRoleCreator.getUserGroupId());
            KapuaId roleId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtGroupRoleCreator.getRoleId());

            GroupRoleCreator groupRoleCreator = GROUP_ROLE_FACTORY.newCreator(scopeId);
            groupRoleCreator.setGroupId(groupId);
            groupRoleCreator.setRoleId(roleId);

            GroupRole groupRole = GROUP_ROLE_SERVICE.create(groupRoleCreator);

            return KapuaGwtAuthorizationModelConverter.convertGroupRole(groupRole);
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }

    }

    @Override
    public void deleteRole(GwtXSRFToken xsrfToken, String gwtScopeId, String gwtGroupRoleId)
            throws GwtKapuaException {
        checkXSRFToken(xsrfToken);

        try {
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtScopeId);
            KapuaId groupRoleId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtGroupRoleId);

            GROUP_ROLE_SERVICE.delete(scopeId, groupRoleId);
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }

    @Override
    public PagingLoadResult<GwtGroupRole> queryRole(PagingLoadConfig loadConfig, GwtGroupRoleQuery gwtGroupRoleQuery) throws GwtKapuaException {
        try {
            // Convert from GWT entity
            GroupRoleQuery groupRoleQuery = GwtKapuaAuthorizationModelConverter.convertGroupRoleQuery(loadConfig, gwtGroupRoleQuery);

            // Query
            GroupRoleListResult groupRoles = GROUP_ROLE_SERVICE.query(groupRoleQuery);

            // If there are results
            List<GwtGroupRole> gwtGroupRoles = new ArrayList<GwtGroupRole>();
            if (!groupRoles.isEmpty()) {

                Map<KapuaId, String> idUsernameMap = UserCreatedByModifiedByUtils.resolveFromListResult(groupRoles);
                Map<KapuaId, String> idRoleNameMap = RoleNameFromIdUtils.resolveRoleNamesFrom(groupRoles);

                // Convert to GWT entity
                for (GroupRole groupRole : groupRoles.getItems()) {
                    GwtGroupRole gwtGroupRole = KapuaGwtAuthorizationModelConverter.convertGroupRole(groupRole);

                    gwtGroupRole.setCreatedByName(idUsernameMap.get(groupRole.getCreatedBy()));
                    gwtGroupRole.setRoleName(idRoleNameMap.get(groupRole.getRoleId()));

                    gwtGroupRoles.add(gwtGroupRole);
                }
            }

            return new BasePagingLoadResult<GwtGroupRole>(gwtGroupRoles, loadConfig.getOffset(), groupRoles.getTotalCount().intValue());
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }
}
