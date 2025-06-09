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
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtAccessPermission;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtAccessPermissionCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtAccessPermissionService;
import org.eclipse.kapua.app.console.module.authorization.shared.util.GwtKapuaAuthorizationModelConverter;
import org.eclipse.kapua.app.console.module.authorization.shared.util.KapuaGwtAuthorizationModelConverter;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.FieldSortCriteria;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfoService;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.access.AccessPermissionAttributes;
import org.eclipse.kapua.service.authorization.access.AccessPermissionCreator;
import org.eclipse.kapua.service.authorization.access.AccessPermissionListResult;
import org.eclipse.kapua.service.authorization.access.AccessPermissionService;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupService;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class GwtAccessPermissionServiceImpl extends KapuaRemoteServiceServlet implements GwtAccessPermissionService {

    private static final long serialVersionUID = 3606053200278262228L;

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final AccessInfoService ACCESS_INFO_SERVICE = LOCATOR.getService(AccessInfoService.class);

    private static final AccessPermissionService ACCESS_PERMISSION_SERVICE = LOCATOR.getService(AccessPermissionService.class);

    private static final AccountService ACCOUNT_SERVICE = LOCATOR.getService(AccountService.class);
    private static final UserService USER_SERVICE = LOCATOR.getService(UserService.class);

    @Override
    public GwtAccessPermission create(GwtXSRFToken xsrfToken, GwtAccessPermissionCreator gwtAccessPermissionCreator) throws GwtKapuaException {
        // Checking XSRF token
        checkXSRFToken(xsrfToken);
        // Do create
        GwtAccessPermission gwtAccessPermission = null;
        try {
            // Convert from GWT Entity
            AccessPermissionCreator accessPermissionCreator = GwtKapuaAuthorizationModelConverter.convertAccessPermissionCreator(gwtAccessPermissionCreator);

            // Create
            AccessPermission accessPermission = ACCESS_PERMISSION_SERVICE.create(accessPermissionCreator);

            // Convert
            gwtAccessPermission = KapuaGwtAuthorizationModelConverter.convertAccessPermission(accessPermission);

        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
        // Return result
        return gwtAccessPermission;
    }

    @Override
    public void delete(GwtXSRFToken gwtXsrfToken, String scopeShortId, String accessPermissionShortId) throws GwtKapuaException {
        // Checking XSRF token
        checkXSRFToken(gwtXsrfToken);
        // Do delete
        try {
            // Convert from GWT Entity
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(scopeShortId);
            KapuaId accessPermissionId = GwtKapuaCommonsModelConverter.convertKapuaId(accessPermissionShortId);

            // Delete
            ACCESS_PERMISSION_SERVICE.delete(scopeId, accessPermissionId);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
    }

    @Override
    public PagingLoadResult<GwtAccessPermission> findByUserId(PagingLoadConfig loadConfig, String scopeShortId, String userShortId) throws GwtKapuaException {
        // Do get
        int totalLength = 0;
        List<GwtAccessPermission> gwtAccessPermissions = new ArrayList<GwtAccessPermission>();

        if (userShortId != null) {
            try {

                KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(scopeShortId);
                KapuaLocator locator = KapuaLocator.getInstance();
                GroupService groupService = locator.getService(GroupService.class);
                KapuaId userId = GwtKapuaCommonsModelConverter.convertKapuaId(userShortId);

                AccessInfo accessInfo = ACCESS_INFO_SERVICE.findByUserId(scopeId, userId);

                if (accessInfo != null) {
                    final KapuaQuery query = new KapuaQuery(scopeId);
                    query.setPredicate(query.attributePredicate(AccessPermissionAttributes.ACCESS_INFO_ID, accessInfo.getId()));
                    query.setLimit(loadConfig.getLimit());
                    query.setOffset(loadConfig.getOffset());
                    query.setAskTotalCount(true);

                    String sortField = StringUtils.isEmpty(loadConfig.getSortField()) ? KapuaEntityAttributes.CREATED_ON : loadConfig.getSortField();
                    if (sortField.equals("createdOnFormatted")) {
                        sortField = AccessPermissionAttributes.CREATED_ON;
                    }
                    SortOrder sortOrder = loadConfig.getSortDir().equals(SortDir.DESC) ? SortOrder.DESCENDING : SortOrder.ASCENDING;
                    FieldSortCriteria sortCriteria = query.fieldSortCriteria(sortField, sortOrder);
                    query.setAskTotalCount(true);
                    query.setSortCriteria(sortCriteria);

                    AccessPermissionListResult accessPermissionList = ACCESS_PERMISSION_SERVICE.query(query);
                    totalLength = accessPermissionList.getTotalCount().intValue();

                    //FIXME: what is the point of checking for empty list and then just loop? The if does not add anything!
                    if (!accessPermissionList.isEmpty()) {
                        for (final AccessPermission accessPermission : accessPermissionList.getItems()) {
                            User createdByUser = KapuaSecurityUtils.doPrivileged(new Callable<User>() {

                                @Override
                                public User call() throws Exception {
                                    return USER_SERVICE.find(accessPermission.getScopeId(), accessPermission.getCreatedBy());
                                }
                            });

                            Account targetScopeIdAccount = null;
                            if (accessPermission.getPermission().getTargetScopeId() != null) {
                                targetScopeIdAccount = KapuaSecurityUtils.doPrivileged(new Callable<Account>() {

                                    @Override
                                    public Account call() throws Exception {
                                        return ACCOUNT_SERVICE.find(accessPermission.getScopeId(), accessPermission.getPermission().getTargetScopeId());
                                    }
                                });
                            }

                            GwtAccessPermission gwtAccessPermission = KapuaGwtAuthorizationModelConverter.convertAccessPermission(accessPermission);
                            if (accessPermission.getPermission().getGroupId() != null) {
                                Group group = groupService.find(scopeId, accessPermission.getPermission().getGroupId());

                                if (group != null) {
                                    gwtAccessPermission.setGroupName(group.getName());
                                }
                            } else {
                                gwtAccessPermission.setGroupName("ALL");
                            }

                            gwtAccessPermission.setCreatedByName(createdByUser != null ? createdByUser.getName() : null);
                            gwtAccessPermission.setPermissionTargetScopeIdByName(targetScopeIdAccount != null ? targetScopeIdAccount.getName() : null);

                            gwtAccessPermissions.add(gwtAccessPermission);
                        }
                    }
                }
            } catch (Throwable t) {
                KapuaExceptionHandler.handle(t);
            }
        }

        return new BasePagingLoadResult<GwtAccessPermission>(gwtAccessPermissions, loadConfig.getOffset(), totalLength);
    }
}
