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
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupPermission;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupPermissionCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupPermissionQuery;
import org.eclipse.kapua.app.console.module.authorization.shared.util.GwtKapuaAuthorizationModelConverter;
import org.eclipse.kapua.app.console.module.authorization.shared.util.KapuaGwtAuthorizationModelConverter;
import org.eclipse.kapua.app.console.module.user.server.user.AccountNameFromIdUtils;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserGroupService;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.group.GroupPermission;
import org.eclipse.kapua.service.authorization.group.GroupPermissionCreator;
import org.eclipse.kapua.service.authorization.group.GroupPermissionFactory;
import org.eclipse.kapua.service.authorization.group.GroupPermissionListResult;
import org.eclipse.kapua.service.authorization.group.GroupPermissionQuery;
import org.eclipse.kapua.service.authorization.group.GroupPermissionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GwtUserGroupServiceImpl extends KapuaRemoteServiceServlet implements GwtUserGroupService {

    private static final long serialVersionUID = 7430961652373364113L;

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final GroupPermissionService GROUP_PERMISSION_SERVICE = LOCATOR.getService(GroupPermissionService.class);
    private static final GroupPermissionFactory GROUP_PERMISSION_FACTORY = LOCATOR.getFactory(GroupPermissionFactory.class);

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
    public GwtGroupPermission findPermission(String gwtScopeId, String gwtGroupPermissionId)
            throws GwtKapuaException {

        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtScopeId);
            KapuaId groupPermissionId = KapuaEid.parseCompactId(gwtGroupPermissionId);

            GroupPermission groupPermission = GROUP_PERMISSION_SERVICE.find(scopeId, groupPermissionId);

            return KapuaGwtAuthorizationModelConverter.convertGroupPermission(groupPermission);
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
}
