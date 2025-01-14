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
package org.eclipse.kapua.app.console.module.authorization.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtAccessRole;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtAccessRoleCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtAccessRoleService;
import org.eclipse.kapua.app.console.module.authorization.shared.util.GwtKapuaAuthorizationModelConverter;
import org.eclipse.kapua.app.console.module.authorization.shared.util.KapuaGwtAuthorizationModelConverter;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.FieldSortCriteria;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfoService;
import org.eclipse.kapua.service.authorization.access.AccessPermissionAttributes;
import org.eclipse.kapua.service.authorization.access.AccessRole;
import org.eclipse.kapua.service.authorization.access.AccessRoleAttributes;
import org.eclipse.kapua.service.authorization.access.AccessRoleCreator;
import org.eclipse.kapua.service.authorization.access.AccessRoleFactory;
import org.eclipse.kapua.service.authorization.access.AccessRoleListResult;
import org.eclipse.kapua.service.authorization.access.AccessRoleService;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RoleService;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class GwtAccessRoleServiceImpl extends KapuaRemoteServiceServlet implements GwtAccessRoleService {

    private static final long serialVersionUID = 3606053200278262228L;

    @Override
    public GwtAccessRole create(GwtXSRFToken xsrfToken, GwtAccessRoleCreator gwtAccessRoleCreator) throws GwtKapuaException {
        // Checking XSRF token
        checkXSRFToken(xsrfToken);
        // Do create
        GwtAccessRole gwtAccessRole = null;
        try {
            // Convert from GWT Entity
            AccessRoleCreator accessRoleCreator = GwtKapuaAuthorizationModelConverter.convertAccessRoleCreator(gwtAccessRoleCreator);

            // Create
            KapuaLocator locator = KapuaLocator.getInstance();
            AccessRoleService accessRoleService = locator.getService(AccessRoleService.class);
            AccessRole accessRole = accessRoleService.create(accessRoleCreator);

            // Convert
            gwtAccessRole = KapuaGwtAuthorizationModelConverter.convertAccessRole(accessRole);

        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
        // Return result
        return gwtAccessRole;
    }

    @Override
    public void delete(GwtXSRFToken gwtXsrfToken, String scopeShortId, String accessRoleShortId) throws GwtKapuaException {
        // Checking XSRF token
        checkXSRFToken(gwtXsrfToken);
        // Do delete
        try {
            // Convert from GWT Entity
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(scopeShortId);
            KapuaId accessRoleId = GwtKapuaCommonsModelConverter.convertKapuaId(accessRoleShortId);

            // Delete
            KapuaLocator locator = KapuaLocator.getInstance();
            AccessRoleService accessRoleService = locator.getService(AccessRoleService.class);
            accessRoleService.delete(scopeId, accessRoleId);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
    }

    @Override
    public PagingLoadResult<GwtAccessRole> findByUserId(PagingLoadConfig loadConfig, String scopeShortId, String userShortId) throws GwtKapuaException {
        // Do get
        int totalLegnth = 0;
        List<GwtAccessRole> gwtAccessRoles = new ArrayList<GwtAccessRole>();
        if (userShortId != null) {

            try {
                KapuaLocator locator = KapuaLocator.getInstance();
                RoleService roleService = locator.getService(RoleService.class);
                AccessInfoService accessInfoService = locator.getService(AccessInfoService.class);
                AccessRoleService accessRoleService = locator.getService(AccessRoleService.class);
                AccessRoleFactory accessRoleFactory = locator.getFactory(AccessRoleFactory.class);
                final UserService userService = locator.getService(UserService.class);

                final KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(scopeShortId);
                KapuaId userId = GwtKapuaCommonsModelConverter.convertKapuaId(userShortId);

                AccessInfo accessInfo = accessInfoService.findByUserId(scopeId, userId);

                if (accessInfo != null) {
                    KapuaQuery query = new KapuaQuery(scopeId);
                    query.setPredicate(query.attributePredicate(AccessPermissionAttributes.ACCESS_INFO_ID, accessInfo.getId()));
                    query.setLimit(loadConfig.getLimit());
                    query.setOffset(loadConfig.getOffset());
                    query.setAskTotalCount(true);
                    String sortField = StringUtils.isEmpty(loadConfig.getSortField()) ? "createdOn" : loadConfig.getSortField();
                    if (sortField.equals("createdOnFormatted")) {
                        sortField = AccessRoleAttributes.CREATED_ON;
                    }

                    SortOrder sortOrder = loadConfig.getSortDir().equals(SortDir.DESC) ? SortOrder.DESCENDING : SortOrder.ASCENDING;
                    FieldSortCriteria sortCriteria = query.fieldSortCriteria(sortField, sortOrder);
                    query.setSortCriteria(sortCriteria);
                    query.setAskTotalCount(true);
                    AccessRoleListResult accessRoleList = accessRoleService.query(query);

                    totalLegnth = accessRoleList.getTotalCount().intValue();
                    if (!accessRoleList.isEmpty()) {
                        for (final AccessRole accessRole : accessRoleList.getItems()) {
                            User createdByUser = KapuaSecurityUtils.doPrivileged(new Callable<User>() {

                                @Override
                                public User call() throws Exception {
                                    return userService.find(accessRole.getScopeId(), accessRole.getCreatedBy());
                                }
                            });
                            Role role = roleService.find(scopeId, accessRole.getRoleId());

                            GwtAccessRole gwtAccessRole = KapuaGwtAuthorizationModelConverter.mergeRoleAccessRole(role, accessRole);

                            if (createdByUser != null) {
                                gwtAccessRole.setCreatedByName(createdByUser.getName());
                            }

                            gwtAccessRoles.add(gwtAccessRole);
                        }
                    }
                }
            } catch (Throwable t) {
                KapuaExceptionHandler.handle(t);
            }
        }
        return new BasePagingLoadResult<GwtAccessRole>(gwtAccessRoles, loadConfig.getOffset(), totalLegnth);
    }
}
