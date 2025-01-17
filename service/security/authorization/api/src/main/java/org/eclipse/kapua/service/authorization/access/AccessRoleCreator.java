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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.authorization.role.Role;

/**
 * {@link AccessRole} creator definition.<br> It is used to create a new {@link AccessRole}.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "accessRoleCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "accessInfoId", "roleId" })
public class AccessRoleCreator extends KapuaEntityCreator<AccessRole> {

    private static final long serialVersionUID = 972154225756734130L;

    private KapuaId accessInfo;
    private KapuaId roleId;

    public AccessRoleCreator() {
    }

    /**
     * Constructor
     *
     * @param scopeId
     */
    public AccessRoleCreator(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * Sets the {@link AccessInfo} id for this {@link AccessRole}.
     *
     * @param accessInfoId
     *         The {@link AccessInfo} id for this {@link AccessRole}.
     * @since 1.0.0
     */
    public void setAccessInfoId(KapuaId accessInfoId) {
        this.accessInfo = accessInfoId;
    }

    /**
     * Gets the {@link AccessInfo} id of this {@link AccessRole}.
     *
     * @return The {@link AccessInfo} id of this {@link AccessRole}.
     * @since 1.0.0
     */
    @XmlElement(name = "accessInfoId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getAccessInfoId() {
        return accessInfo;
    }

    /**
     * Sets the {@link Role} id to assign to the {@link AccessRole} created entity. It up to the implementation class to make a clone of the object or use the given object.
     *
     * @param roleId
     *         The {@link Role} id
     * @since 1.0.0
     */
    public void setRoleId(KapuaId roleId) {
        this.roleId = roleId;
    }

    /**
     * Gets the {@link Role} id added to this {@link AccessRole}.
     *
     * @return The {@link Role} id added to this {@link AccessRole}.
     * @since 1.0.0
     */
    @XmlElement(name = "roleId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getRoleId() {
        return roleId;
    }
}
