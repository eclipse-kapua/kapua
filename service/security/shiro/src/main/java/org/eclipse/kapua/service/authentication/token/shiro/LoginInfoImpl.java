/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authentication.token.shiro;

import java.util.Set;

import org.eclipse.kapua.service.authentication.token.AccessToken;
import org.eclipse.kapua.service.authentication.token.LoginInfo;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.group.GroupPermission;
import org.eclipse.kapua.service.authorization.role.RolePermission;

public class LoginInfoImpl implements LoginInfo {

    private AccessToken accessToken;
    private Set<RolePermission> rolePermissions;
    private Set<AccessPermission> accessPermissions;

    private Set<RolePermission> groupRolePermissions;
    private Set<GroupPermission> groupPermissions;

    @Override
    public AccessToken getAccessToken() {
        return accessToken;
    }

    @Override
    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public Set<RolePermission> getRolePermission() {
        return rolePermissions;
    }

    @Override
    public void setRolePermission(Set<RolePermission> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    @Override
    public Set<AccessPermission> getAccessPermission() {
        return accessPermissions;
    }

    @Override
    public void setAccessPermission(Set<AccessPermission> accessPermissions) {
        this.accessPermissions = accessPermissions;
    }

    @Override
    public Set<RolePermission> getGroupRolePermission() {
        return groupRolePermissions;
    }

    @Override
    public void setGroupRolePermission(Set<RolePermission> groupRolePermissions) {
        this.groupRolePermissions = groupRolePermissions;
    }

    @Override
    public Set<GroupPermission> getGroupPermission() {
        return groupPermissions;
    }

    @Override
    public void setGroupPermission(Set<GroupPermission> groupPermissions) {
        this.groupPermissions = groupPermissions;
    }
}
