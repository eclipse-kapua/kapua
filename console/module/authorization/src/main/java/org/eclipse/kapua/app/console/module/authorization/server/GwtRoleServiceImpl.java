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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtGroupedNVPair;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtPermission;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtRole;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtRoleCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtRolePermission;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtRolePermissionCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtRoleQuery;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtRoleService;
import org.eclipse.kapua.app.console.module.authorization.shared.util.GwtKapuaAuthorizationModelConverter;
import org.eclipse.kapua.app.console.module.authorization.shared.util.KapuaGwtAuthorizationModelConverter;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.FieldSortCriteria;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupService;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RoleCreator;
import org.eclipse.kapua.service.authorization.role.RoleFactory;
import org.eclipse.kapua.service.authorization.role.RoleListResult;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.eclipse.kapua.service.authorization.role.RolePermissionAttributes;
import org.eclipse.kapua.service.authorization.role.RolePermissionCreator;
import org.eclipse.kapua.service.authorization.role.RolePermissionFactory;
import org.eclipse.kapua.service.authorization.role.RolePermissionListResult;
import org.eclipse.kapua.service.authorization.role.RolePermissionService;
import org.eclipse.kapua.service.authorization.role.RoleQuery;
import org.eclipse.kapua.service.authorization.role.RoleService;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserFactory;
import org.eclipse.kapua.service.user.UserListResult;
import org.eclipse.kapua.service.user.UserQuery;
import org.eclipse.kapua.service.user.UserService;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class GwtRoleServiceImpl extends KapuaRemoteServiceServlet implements GwtRoleService {

    private static final long serialVersionUID = 3606053200278262228L;

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final AccountService ACCOUNT_SERVICE = LOCATOR.getService(AccountService.class);

    private static final RoleService ROLE_SERVICE = LOCATOR.getService(RoleService.class);
    private static final RoleFactory ROLE_FACTORY = LOCATOR.getFactory(RoleFactory.class);

    private static final RolePermissionService ROLE_PERMISSION_SERVICE = LOCATOR.getService(RolePermissionService.class);
    private static final RolePermissionFactory ROLE_PERMISSION_FACTORY = LOCATOR.getFactory(RolePermissionFactory.class);

    private static final UserService USER_SERVICE = LOCATOR.getService(UserService.class);
    private static final UserFactory USER_FACTORY = LOCATOR.getFactory(UserFactory.class);

    private static final GroupService GROUP_SERVICE = LOCATOR.getService(GroupService.class);

    private static final String ENTITY_INFO = "entityInfo";

    @Override
    public GwtRole create(GwtXSRFToken xsrfToken, GwtRoleCreator gwtRoleCreator) throws GwtKapuaException {
        // Checking XSRF token
        checkXSRFToken(xsrfToken);
        // Do create
        GwtRole gwtRole = null;
        try {
            // Convert from GWT Entity
            RoleCreator roleCreator = GwtKapuaAuthorizationModelConverter.convertRoleCreator(gwtRoleCreator);

            // Create
            Role role = ROLE_SERVICE.create(roleCreator);

            // Convert
            gwtRole = KapuaGwtAuthorizationModelConverter.convertRole(role);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
        // Return result
        return gwtRole;
    }

    @Override
    public GwtRole update(GwtXSRFToken gwtXsrfToken, GwtRole gwtRole) throws GwtKapuaException {
        // Checking XSRF token
        checkXSRFToken(gwtXsrfToken);
        // Do update
        GwtRole gwtRoleUpdated = null;
        try {
            // Convert from GWT Entity
            Role role = GwtKapuaAuthorizationModelConverter.convertRole(gwtRole);

            // Update
            Role roleUpdated = ROLE_SERVICE.update(role);

            // Convert
            gwtRoleUpdated = KapuaGwtAuthorizationModelConverter.convertRole(roleUpdated);

        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
        // Return result
        return gwtRoleUpdated;
    }

    @Override
    public GwtRole find(String scopeShortId, String roleShortId) throws GwtKapuaException {
        // Do find
        GwtRole gwtRole = null;
        try {
            // Convert from GWT Entity
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(scopeShortId);
            KapuaId roleId = GwtKapuaCommonsModelConverter.convertKapuaId(roleShortId);

            // Delete
            Role role = ROLE_SERVICE.find(scopeId, roleId);

            gwtRole = KapuaGwtAuthorizationModelConverter.convertRole(role);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
        // Return result
        return gwtRole;
    }

    @Override
    public PagingLoadResult<GwtRole> query(PagingLoadConfig loadConfig, final GwtRoleQuery gwtRoleQuery) throws GwtKapuaException {
        // Do query
        int totalLength = 0;
        List<GwtRole> gwtRoles = new ArrayList<GwtRole>();

        try {
            // Convert from GWT entity
            RoleQuery roleQuery = GwtKapuaAuthorizationModelConverter.convertRoleQuery(loadConfig, gwtRoleQuery);

            // query
            RoleListResult roles = ROLE_SERVICE.query(roleQuery);
            totalLength = roles.getTotalCount().intValue();

            if (!roles.isEmpty()) {
                //TODO: #LAYER_VIOLATION - user lookup should not be done here (horribly inefficient)
                UserListResult usernames = KapuaSecurityUtils.doPrivileged(new Callable<UserListResult>() {

                    @Override
                    public UserListResult call() throws Exception {
                        return USER_SERVICE.query(new UserQuery(null));
                    }
                });

                Map<String, String> usernameMap = new HashMap<String, String>();
                for (User user : usernames.getItems()) {
                    usernameMap.put(user.getId().toCompactId(), user.getName());
                }

                // Converto to GWT entity
                for (Role r : roles.getItems()) {
                    GwtRole gwtRole = KapuaGwtAuthorizationModelConverter.convertRole(r);
                    gwtRole.setCreatedByName(usernameMap.get(r.getCreatedBy().toCompactId()));
                    gwtRole.setModifiedByName(usernameMap.get(r.getModifiedBy().toCompactId()));
                    gwtRoles.add(gwtRole);
                }
            }

        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return new BasePagingLoadResult<GwtRole>(gwtRoles, loadConfig.getOffset(), totalLength);
    }

    @Override
    public ListLoadResult<GwtGroupedNVPair> getRoleDescription(String scopeShortId, String roleShortId) throws GwtKapuaException {
        // Do get
        List<GwtGroupedNVPair> gwtRoleDescription = new ArrayList<GwtGroupedNVPair>();
        try {
            // Convert from GWT Entity
            final KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(scopeShortId);
            KapuaId roleId = GwtKapuaCommonsModelConverter.convertKapuaId(roleShortId);

            // Find
            final Role role = ROLE_SERVICE.find(scopeId, roleId);
            UserListResult userListResult = KapuaSecurityUtils.doPrivileged(new Callable<UserListResult>() {

                @Override
                public UserListResult call() throws Exception {
                    return USER_SERVICE.query(new UserQuery(null));
                }
            });

            Map<String, String> usernameMap = new HashMap<String, String>();
            for (User user : userListResult.getItems()) {
                usernameMap.put(user.getId().toCompactId(), user.getName());
            }

            // If there are results
            if (role != null) {
                gwtRoleDescription.add(new GwtGroupedNVPair("roleInfo", "roleName", role.getName()));
                gwtRoleDescription.add(new GwtGroupedNVPair("roleInfo", "roleDescription", role.getDescription()));
                gwtRoleDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "roleModifiedOn", role.getModifiedOn()));
                gwtRoleDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "roleModifiedBy", role.getModifiedBy() != null ? usernameMap.get(role.getModifiedBy().toCompactId()) : null));
                gwtRoleDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "roleCreatedOn", role.getCreatedOn()));
                gwtRoleDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "roleCreatedBy", role.getCreatedBy() != null ? usernameMap.get(role.getCreatedBy().toCompactId()) : null));
            }

        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return new BaseListLoadResult<GwtGroupedNVPair>(gwtRoleDescription);
    }

    @Override
    public PagingLoadResult<GwtRolePermission> getRolePermissions(PagingLoadConfig loadConfig, String scopeShortId, String roleShortId) throws GwtKapuaException {
        // Do get
        int totalLength = 0;
        List<GwtRolePermission> gwtRolePermissions = new ArrayList<GwtRolePermission>();
        try {
            // Convert from GWT Entity
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(scopeShortId);
            KapuaId roleId = GwtKapuaCommonsModelConverter.convertKapuaId(roleShortId);

            // Get permissions assigned to the Role
            KapuaQuery query = new KapuaQuery(scopeId);
            query.setPredicate(query.attributePredicate(RolePermissionAttributes.ROLE_ID, roleId));
            query.setLimit(loadConfig.getLimit());
            query.setOffset(loadConfig.getOffset());

            String sortField = StringUtils.isEmpty(loadConfig.getSortField()) ? "createdOnFormatted" : loadConfig.getSortField();
            if (sortField.equals("createdOnFormatted")) {
                sortField = KapuaEntityAttributes.CREATED_ON;
            }

            SortOrder sortOrder = loadConfig.getSortDir().equals(SortDir.DESC) ? SortOrder.DESCENDING : SortOrder.ASCENDING;
            FieldSortCriteria sortCriteria = query.fieldSortCriteria(sortField, sortOrder);
            query.setSortCriteria(sortCriteria);
            query.setAskTotalCount(true);

            RolePermissionListResult list = ROLE_PERMISSION_SERVICE.query(query);
            totalLength = list.getTotalCount().intValue();

            if (list != null) {
                for (final RolePermission rolePermission : list.getItems()) {
                    User createdByUser = KapuaSecurityUtils.doPrivileged(new Callable<User>() {

                        @Override
                        public User call() throws Exception {
                            return USER_SERVICE.find(rolePermission.getScopeId(), rolePermission.getCreatedBy());
                        }
                    });

                    Account targetScopeIdAccount = null;
                    if (rolePermission.getPermission().getTargetScopeId() != null) {
                        targetScopeIdAccount = KapuaSecurityUtils.doPrivileged(new Callable<Account>() {

                            @Override
                            public Account call() throws Exception {
                                return ACCOUNT_SERVICE.find(rolePermission.getScopeId(), rolePermission.getPermission().getTargetScopeId());
                            }
                        });
                    }

                    GwtRolePermission gwtRolePermission = KapuaGwtAuthorizationModelConverter.convertRolePermission(rolePermission);
                    if (rolePermission.getPermission().getGroupId() != null) {
                        Group group = GROUP_SERVICE.find(rolePermission.getScopeId(), rolePermission.getPermission().getGroupId());
                        if (group != null) {
                            gwtRolePermission.setGroupName(group.getName());
                        }
                    } else {
                        gwtRolePermission.setGroupName("ALL");
                    }

                    gwtRolePermission.setCreatedByName(createdByUser != null ? createdByUser.getName() : null);
                    gwtRolePermission.setTargetScopeIdByName(targetScopeIdAccount != null ? targetScopeIdAccount.getName() : null);

                    gwtRolePermissions.add(gwtRolePermission);
                }
            }
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return new BasePagingLoadResult<GwtRolePermission>(gwtRolePermissions, loadConfig.getOffset(), totalLength);
    }

    @Override
    public void delete(GwtXSRFToken gwtXsrfToken, String scopeShortId, String roleShortId) throws GwtKapuaException {
        // Checking XSRF token
        checkXSRFToken(gwtXsrfToken);
        // Do delete
        try {
            // Convert from GWT Entity
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(scopeShortId);
            KapuaId roleId = GwtKapuaCommonsModelConverter.convertKapuaId(roleShortId);

            // Delete
            ROLE_SERVICE.delete(scopeId, roleId);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
    }

    @Override
    public GwtRolePermission addRolePermission(GwtXSRFToken gwtXsrfToken, GwtRolePermissionCreator gwtRolePermissionCreator, GwtPermission gwtPermission) throws GwtKapuaException {
        checkXSRFToken(gwtXsrfToken);

        GwtRolePermission newGwtRolePermission = null;
        try {
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtRolePermissionCreator.getScopeId());

            RolePermissionCreator rolePermissionCreator = new RolePermissionCreator(scopeId);
            rolePermissionCreator.setRoleId(GwtKapuaCommonsModelConverter.convertKapuaId(gwtRolePermissionCreator.getRoleId()));
            rolePermissionCreator.setScopeId(scopeId);
            rolePermissionCreator.setPermission(GwtKapuaAuthorizationModelConverter.convertPermission(gwtPermission));

            RolePermission newRolePermission = null;

            newRolePermission = ROLE_PERMISSION_SERVICE.create(rolePermissionCreator);
            newGwtRolePermission = KapuaGwtAuthorizationModelConverter.convertRolePermission(newRolePermission);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
        return newGwtRolePermission;
    }

    @Override
    public void deleteRolePermission(GwtXSRFToken gwtXsrfToken, String scopeShortId, String roleShortId) throws GwtKapuaException {
        checkXSRFToken(gwtXsrfToken);
        try {
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(scopeShortId);
            KapuaId roleId = GwtKapuaCommonsModelConverter.convertKapuaId(roleShortId);

            ROLE_PERMISSION_SERVICE.delete(scopeId, roleId);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
    }

    @Override
    public List<GwtRole> findAll(String scopeIdString) throws GwtKapuaException {
        KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
        List<GwtRole> gwtRoleList = new ArrayList<GwtRole>();
        try {
            RoleQuery query = new RoleQuery(scopeId);
            RoleListResult list = ROLE_SERVICE.query(query);

            for (Role role : list.getItems()) {
                gwtRoleList.add(KapuaGwtAuthorizationModelConverter.convertRole(role));
            }
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return gwtRoleList;
    }
}
