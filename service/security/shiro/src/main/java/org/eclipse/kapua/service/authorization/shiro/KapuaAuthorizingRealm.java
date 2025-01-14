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
import org.eclipse.kapua.model.query.KapuaListResult;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfoAttributes;
import org.eclipse.kapua.service.authorization.access.AccessInfoFactory;
import org.eclipse.kapua.service.authorization.access.AccessInfoService;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.access.AccessPermissionListResult;
import org.eclipse.kapua.service.authorization.access.AccessPermissionService;
import org.eclipse.kapua.service.authorization.access.AccessRole;
import org.eclipse.kapua.service.authorization.access.AccessRoleListResult;
import org.eclipse.kapua.service.authorization.access.AccessRoleService;
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
        // Get user access infos
        KapuaQuery accessInfoQuery = new KapuaQuery(user.getScopeId());
        accessInfoQuery.setPredicate(accessInfoQuery.attributePredicate(AccessInfoAttributes.USER_ID, user.getId()));

        final KapuaListResult<AccessInfo> accessInfos;
        try {
            accessInfos = KapuaSecurityUtils.doPrivileged(() -> accessInfoService.query(accessInfoQuery));
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new ShiroException("Error while find access info!", e);
        }
        // Check existence
        if (accessInfos == null) {
            throw new UnknownAccountException();
        }
        // Create SimpleAuthorizationInfo with principals permissions
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        // Get user roles set and related permissions
        for (AccessInfo accessInfo : accessInfos.getItems()) {

            // Access Permissions
            AccessPermissionService accessPermissionService = locator.getService(AccessPermissionService.class);
            AccessPermissionListResult accessPermissions;
            try {
                accessPermissions = KapuaSecurityUtils.doPrivileged(() -> accessPermissionService.findByAccessInfoId(accessInfo.getScopeId(), accessInfo.getId()));
            } catch (AuthenticationException e) {
                throw e;
            } catch (Exception e) {
                throw new ShiroException("Error while find access permissions!", e);
            }

            for (AccessPermission accessPermission : accessPermissions.getItems()) {
                PermissionImpl p = accessPermission.getPermission();
                logger.trace("User: {} has permission: {}", username, p);
                info.addObjectPermission(permissionMapper.mapPermission(p));
            }

            // Access Role Id
            AccessRoleService accessRoleService = locator.getService(AccessRoleService.class);
            AccessRoleListResult accessRoles;
            try {
                accessRoles = KapuaSecurityUtils.doPrivileged(() -> accessRoleService.findByAccessInfoId(accessInfo.getScopeId(), accessInfo.getId()));
            } catch (AuthenticationException e) {
                throw e;
            } catch (Exception e) {
                throw new ShiroException("Error while find access role ids!", e);
            }

            RoleService roleService = locator.getService(RoleService.class);
            RolePermissionService rolePermissionService = locator.getService(RolePermissionService.class);
            for (AccessRole accessRole : accessRoles.getItems()) {

                KapuaId roleId = accessRole.getRoleId();

                Role role;
                try {
                    role = KapuaSecurityUtils.doPrivileged(() -> roleService.find(accessRole.getScopeId(), roleId));
                } catch (AuthenticationException e) {
                    throw e;
                } catch (Exception e) {
                    throw new ShiroException("Error while find role ids!", e);
                }

                info.addRole(role.getName());
                final RolePermissionListResult rolePermissions;
                try {
                    rolePermissions = KapuaSecurityUtils.doPrivileged(() -> rolePermissionService.findByRoleId(role.getScopeId(), role.getId()));
                } catch (Exception e) {
                    throw new ShiroException("Error while find role permission!", e);
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
