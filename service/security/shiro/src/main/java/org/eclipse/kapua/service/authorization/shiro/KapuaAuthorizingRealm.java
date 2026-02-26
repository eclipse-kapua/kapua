/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authorization.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfoFactory;
import org.eclipse.kapua.service.authorization.access.AccessInfoService;
import org.eclipse.kapua.service.authorization.access.AccessPermissionService;
import org.eclipse.kapua.service.authorization.access.AccessRoleService;
import org.eclipse.kapua.service.authorization.group.GroupPermission;
import org.eclipse.kapua.service.authorization.group.GroupPermissionListResult;
import org.eclipse.kapua.service.authorization.group.GroupPermissionService;
import org.eclipse.kapua.service.authorization.group.GroupRole;
import org.eclipse.kapua.service.authorization.group.GroupRoleListResult;
import org.eclipse.kapua.service.authorization.group.GroupRoleService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.shiro.PermissionImpl;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.eclipse.kapua.service.authorization.role.RolePermissionListResult;
import org.eclipse.kapua.service.authorization.role.RolePermissionService;
import org.eclipse.kapua.service.authorization.role.RoleService;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * The JPA-based application's one and only configured Apache Shiro Realm.
 */
public class KapuaAuthorizingRealm extends AuthorizingRealm {

    private static final Logger logger = LoggerFactory.getLogger(KapuaAuthorizingRealm.class);

    public static final String REALM_NAME = "kapuaAuthorizingRealm";

    private final PermissionMapper permissionMapper;

    public KapuaAuthorizingRealm() throws KapuaException {
        setName(REALM_NAME);

        permissionMapper = KapuaLocator.getInstance().getComponent(PermissionMapperImpl.class);
    }

    /**
     * Authorization.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals)
            throws AuthenticationException {
        // Extract principal
        String username = ((User) principals.getPrimaryPrincipal()).getName();
        logger.debug("Getting authorization info for: {}", username);

        // Get Services
        KapuaLocator locator = KapuaLocator.getInstance();
        UserService userService = locator.getService(UserService.class);
        AccessInfoService accessInfoService = locator.getService(AccessInfoService.class);
        AccessInfoFactory accessInfoFactory = locator.getFactory(AccessInfoFactory.class);
        AccessRoleService accessRoleService = locator.getService(AccessRoleService.class);
        AccessPermissionService accessPermissionService = locator.getService(AccessPermissionService.class);
        RoleService roleService = locator.getService(RoleService.class);
        RolePermissionService rolePermissionService = locator.getService(RolePermissionService.class);
        GroupPermissionService groupPermissionService = locator.getService(GroupPermissionService.class);
        GroupRoleService groupRoleService = locator.getService(GroupRoleService.class);

        // Get the associated user by name
        final User user;
        try {
            user = KapuaSecurityUtils.doPrivileged(() -> userService.findByName(username));
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new ShiroException("Error while find user!", e);
        }

        // Check existence
        if (user == null) {
            SecurityUtils.getSubject().logout();

            throw new AuthenticationException();
        }

        // Get User AccessInfo
        AccessInfo accessInfo;
        try {
            accessInfo = KapuaSecurityUtils.doPrivileged(() -> accessInfoService.findByUserId(user.getScopeId(), user.getId()));
        }
        catch (Exception e) {
            throw new ShiroException("Error while find access info!", e);
        }

        // Check existence
        if (accessInfo == null) {
            throw new UnknownAccountException();
        }

        // Create SimpleAuthorizationInfo with principal's permissions
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        try {
            Set<Permission> accessInfoPermission = KapuaSecurityUtils.doPrivileged(() -> accessInfoService.fetchPermissions(accessInfo.getScopeId(), accessInfo.getId()));

            info.addObjectPermissions(
                accessInfoPermission
                        .stream()
                        .map(permissionMapper::mapPermission)
                        .collect(Collectors.toSet())
            );
        } catch (Exception e) {
            throw new ShiroException("Error while fetching Permission for AccessInfo!", e);
        }

        // Group Permissions and Roles
        // For each User Group
        for (KapuaId groupId : user.getGroupIds()) {

            // Read from Permission granted to the User Group
            GroupPermissionListResult userGroupPermissions = null;
            try {
                userGroupPermissions = KapuaSecurityUtils.doPrivileged(() -> groupPermissionService.findByGroupId(user.getScopeId(), groupId));
            } catch (KapuaException e) {
                throw new ShiroException("Error while find Group Permissions!", e);
            }

            for (GroupPermission userGroupPermission : userGroupPermissions.getItems()) {
                info.addObjectPermission(permissionMapper.mapPermission(userGroupPermission.getPermission()));
            }

            // Read from Roles granted to the User Group
            GroupRoleListResult userGroupRoles = null;
            try {
                userGroupRoles = KapuaSecurityUtils.doPrivileged(() -> groupRoleService.findByGroupId(user.getScopeId(), groupId));
            } catch (KapuaException e) {
                throw new ShiroException("Error while find Group Roles!", e);
            }

            for (GroupRole userGroupRole : userGroupRoles.getItems()) {
                KapuaId userGroupRoleId = userGroupRole.getRoleId();

                Role role;
                try {
                    role = KapuaSecurityUtils.doPrivileged(() -> roleService.find(userGroupRole.getScopeId(), userGroupRoleId));
                } catch (AuthenticationException e) {
                    throw e;
                } catch (Exception e) {
                    throw new ShiroException("Error while find role!", e);
                }

                info.addRole(role.getName());

                RolePermissionListResult rolePermissions = null;
                try {
                    rolePermissions = KapuaSecurityUtils.doPrivileged(() -> rolePermissionService.findByRoleId(role.getScopeId(), userGroupRoleId));
                } catch (KapuaException e) {
                    throw new ShiroException("Error while find role ids!", e);
                }

                for (RolePermission rolePermission : rolePermissions.getItems()) {

                    PermissionImpl p = rolePermission.getPermission();
                    logger.trace("Role: {} has permission: {}", role, p);
                    info.addObjectPermission(permissionMapper.mapPermission(p));
                }
            }
        }

        // Return authorization info
        return info;
    }

    /**
     * This method always returns false as it works only as AuthorizingReam.
     */
    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return false;
    }

    /**
     * This method can always return null as it does not support any authentication token.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        return null;
    }

}
