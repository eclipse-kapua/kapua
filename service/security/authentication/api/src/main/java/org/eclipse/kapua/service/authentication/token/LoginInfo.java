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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import java.util.Set;

import org.eclipse.kapua.KapuaSerializable;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.group.GroupPermission;
import org.eclipse.kapua.service.authorization.role.RolePermission;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = AccessTokenXmlRegistry.class, factoryMethod = "newAccessToken")
public interface LoginInfo extends KapuaSerializable {

    @XmlElement(name = "accessToken")
    AccessToken getAccessToken();

    void setAccessToken(AccessToken accessToken);

    @XmlElement(name = "rolePermission")
    Set<RolePermission> getRolePermission();

    void setRolePermission(Set<RolePermission> rolePermissions);

    @XmlElement(name = "accessPermission")
    Set<AccessPermission> getAccessPermission();

    void setAccessPermission(Set<AccessPermission> accessPermissions);

    @XmlElement(name = "groupRolePermission")
    Set<RolePermission> getGroupRolePermission();

    void setGroupRolePermission(Set<RolePermission> groupRolePermissions);

    @XmlElement(name = "groupPermission")
    Set<GroupPermission> getGroupPermission();

    void setGroupPermission(Set<GroupPermission> groupPermissions);


}
