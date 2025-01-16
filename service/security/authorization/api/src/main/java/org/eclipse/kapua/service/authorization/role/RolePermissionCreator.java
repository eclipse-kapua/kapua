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
package org.eclipse.kapua.service.authorization.role;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.authorization.permission.Permission;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link RolePermission} creator definition.<br>
 * It is used to create a new {@link RolePermission}.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "rolePermissionCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"roleId", "permission"},//
        factoryClass = RolePermissionXmlRegistry.class, factoryMethod = "newCreator")
@Schema(description = "An object containing the properties for the new RolePermission to be created")
public interface RolePermissionCreator extends KapuaEntityCreator<RolePermission> {

    /**
     * Sets the {@link Role} id for this {@link RolePermission}.
     *
     * @param roleId The {@link Role} id for this {@link RolePermission}.
     * @since 1.0.0
     */
    void setRoleId(KapuaId roleId);

    /**
     * Gets the {@link Role} id of this {@link RolePermission}.
     *
     * @return The {@link Role} id of this {@link RolePermission}.
     * @since 1.0.0
     */
    @XmlElement(name = "roleId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    KapuaId getRoleId();

    /**
     * Sets the {@link Permission} to assign to the {@link RolePermission} created entity.
     * It up to the implementation class to make a clone of the object or use the given object.
     *
     * @param permission The {@link Permission}.
     * @since 1.0.0
     */
    void setPermission(Permission permission);

    /**
     * Gets the set of {@link Permission} added to this {@link RolePermission}.
     *
     * @return The set of {@link Permission}.
     * @since 1.0.0
     */
    @XmlElement(name = "permission")
    <P extends Permission> P getPermission();
}
