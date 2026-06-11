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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import java.util.Set;

import org.eclipse.kapua.KapuaSerializable;
import org.eclipse.kapua.plugin.sso.openid.SSOData;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.group.GroupPermission;
import org.eclipse.kapua.service.authorization.role.RolePermission;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public interface LoginInfo extends KapuaSerializable {

    @XmlElement(name = "accessToken")
    AccessToken getAccessToken();

    void setAccessToken(AccessToken accessToken);

    //
    // Permissions - New getter and setters for various set of Permissions
    // These have correct @XmlElement and @XmlElementWrapper and (more) correct method name
    // Luckily the serialized XML/Json output does not get crazy because of duplicates @XmlElement mappings.

    @XmlElementWrapper(name = "rolePermissions")
    @XmlElement(name = "rolePermission")
    Set<RolePermission> getRolePermissions();

    void setRolePermissions(Set<RolePermission> rolePermissions);

    @XmlElementWrapper(name = "accessPermissions")
    @XmlElement(name = "accessPermission")
    Set<AccessPermission> getAccessPermissions();

    void setAccessPermissions(Set<AccessPermission> accessPermissions);

    @XmlElementWrapper(name = "groupRolePermissions")
    @XmlElement(name = "groupRolePermission")
    Set<RolePermission> getGroupRolePermissions();

    void setGroupRolePermissions(Set<RolePermission> groupRolePermissions);

    @XmlElementWrapper(name = "groupPermissions")
    @XmlElement(name = "groupPermission")
    Set<GroupPermission> getGroupPermissions();

    void setGroupPermissions(Set<GroupPermission> groupPermissions);

    //
    // Permissions - Deprecated getters and setters for various set of Permissions
    // Deprecated because of a wrong @XmlElement mapping that was missing @XmlElementWrapper
    // and because they were in singular form even if they were a Set

    /**
     * @deprecated Since 2.1.0. Please use {@link #getRolePermissions()}
     */
    @Deprecated
    @XmlElement(name = "rolePermission")
    Set<RolePermission> getRolePermission();

    /**
     * @deprecated Since 2.1.0. Please use {@link #setRolePermissions(Set)} 
     */
    @Deprecated
    void setRolePermission(Set<RolePermission> rolePermissions);

    /**
     * @deprecated Since 2.1.0. Please use {@link #getAccessPermissions()}
     */
    @Deprecated
    @XmlElement(name = "accessPermission")
    Set<AccessPermission> getAccessPermission();

    /**
     * @deprecated Since 2.1.0. Please use {@link #setAccessPermissions(Set)} 
     */
    @Deprecated
    void setAccessPermission(Set<AccessPermission> accessPermissions);

    /**
     * @deprecated Since 2.1.0. Please use {@link #getGroupRolePermissions()}
     */
    @Deprecated
    @XmlElement(name = "groupRolePermission")
    Set<RolePermission> getGroupRolePermission();

    /**
     * @deprecated Since 2.1.0. Please use {@link #setGroupRolePermissions(Set)} 
     */
    @Deprecated
    void setGroupRolePermission(Set<RolePermission> groupRolePermissions);

    /**
     * @deprecated Since 2.1.0. Please use {@link #getGroupPermissions()}
     */
    @Deprecated
    @XmlElement(name = "groupPermission")
    Set<GroupPermission> getGroupPermission();

    /**
     * @deprecated Since 2.1.0. Please use {@link #setGroupPermissions(Set)} 
     */
    @Deprecated
    void setGroupPermission(Set<GroupPermission> groupPermissions);

    @XmlTransient
    SSOData getSsoData();

    @XmlTransient
    void setSsoData(SSOData ssoData);


}
