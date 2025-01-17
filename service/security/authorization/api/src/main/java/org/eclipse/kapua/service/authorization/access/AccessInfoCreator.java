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
package org.eclipse.kapua.service.authorization.access;

import java.security.Permissions;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.authorization.domain.Domain;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.user.User;

/**
 * {@link AccessInfo} creator definition.<br> It is used to assign a set of {@link Domain}s and {@link Permission}s to the referenced {@link User}.<br>
 *
 * @since 1.0.0
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "userId",
        "roleIds",
        "permissions" })
public class AccessInfoCreator extends KapuaEntityCreator<AccessInfo> {

    private static final long serialVersionUID = 972154225756734130L;

    private KapuaId userId;
    private Set<KapuaId> roleIds;
    private Set<Permission> permissions;

    public AccessInfoCreator() {
    }

    /**
     * Constructor
     *
     * @param accessInfo
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public AccessInfoCreator(AccessInfoCreator accessInfo) {
        super((KapuaEntityCreator) accessInfo);

        setUserId(accessInfo.getUserId());
        setRoleIds(accessInfo.getRoleIds());
        setPermissions(accessInfo.getPermissions());
    }

    public AccessInfoCreator(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * Sets the user identifier.
     *
     * @param userId
     *         The user id to set.
     * @since 1.0.0
     */
    public void setUserId(KapuaId userId) {
        this.userId = userId;
    }

    /**
     * Gets the user id.
     *
     * @return The user id.
     * @since 1.0.0
     */
    @XmlElement(name = "userId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getUserId() {
        return userId;
    }

    /**
     * Sets the set of {@link Domain} ids to assign to the {@link AccessInfo} created entity. It up to the implementation class to make a clone of the set or use the given set.
     *
     * @param roleIds
     *         The set of {@link Domain} ids.
     * @since 1.0.0
     */
    public void setRoleIds(Set<KapuaId> roleIds) {
        this.roleIds = roleIds;
    }

    /**
     * Gets the set of {@link Domain} ids added to this {@link AccessInfoCreator}. The implementation must return the reference of the set and not make a clone.
     *
     * @return The set of {@link Domain} ids.
     * @since 1.0.0
     */
    @XmlElementWrapper(name = "roleIds")
    @XmlElement(name = "roleId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public Set<KapuaId> getRoleIds() {
        if (roleIds == null) {
            roleIds = new HashSet<>();
        }
        return roleIds;
    }

    /**
     * Sets the set of {@link Permissions} to assign to the {@link AccessInfo} created entity. It up to the implementation class to make a clone of the set or use the given set.
     *
     * @param permissions
     *         The set of {@link Permissions}.
     * @since 1.0.0
     */
    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    /**
     * Gets the set of {@link Permission} added to this {@link AccessInfoCreator}. The implementation must return the reference of the set and not make a clone.
     *
     * @return The set of {@link Permission}.
     * @since 1.0.0
     */
    @XmlElementWrapper(name = "permissions")
    @XmlElement(name = "permission")
    public Set<Permission> getPermissions() {
        if (permissions == null) {
            permissions = new HashSet<>();
        }
        return permissions;
    }
}
