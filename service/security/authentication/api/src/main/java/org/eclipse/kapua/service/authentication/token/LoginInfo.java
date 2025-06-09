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
package org.eclipse.kapua.service.authentication.token;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.KapuaSerializable;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.role.RolePermission;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class LoginInfo implements KapuaSerializable {

    private AccessToken accessToken;
    private Set<RolePermission> rolePermissions;
    private Set<AccessPermission> accessPermissions;

    @XmlElement(name = "accessToken")
    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    @XmlElement(name = "rolePermission")
    public Set<RolePermission> getRolePermission() {
        return rolePermissions;
    }

    public void setRolePermission(Set<RolePermission> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    @XmlElement(name = "accessPermission")
    public Set<AccessPermission> getAccessPermission() {
        return accessPermissions;
    }

    public void setAccessPermission(Set<AccessPermission> accessPermissions) {
        this.accessPermissions = accessPermissions;
    }

}
