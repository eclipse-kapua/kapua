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

import javax.management.relation.RoleInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.authorization.permission.Permission;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Role permission entity.<br>
 * Describes a {@link Permission} associated to the role.<br>
 * Wrapping of the {@link Permission} into this class is intended to adds auditing
 * informations like {@link RolePermission#getCreatedBy()} and{@link RolePermission#getCreatedOn()}.<br>
 * <br>
 * This is a not editable entity so it can be only removed or created and therefore any change to
 * {@link RolePermission#getRoleId()} and {@link RolePermission#getPermission()} property is forbidden.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "rolePermission")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"roleId",
        "permission"}, //
        factoryClass = RolePermissionXmlRegistry.class, //
        factoryMethod = "newRolePermission")
public interface RolePermission extends KapuaEntity {

    String TYPE = "rolePermission";

    @Override
    @Schema(example = "rolePermission")
    default String getType() {
        return TYPE;
    }

    /**
     * Sets the {@link Role} id of which this {@link RolePermission} belongs.
     *
     * @param roleId The {@link RoleInfo} id.
     * @since 1.0.0
     */
    void setRoleId(KapuaId roleId);

    /**
     * Gets the {@link Role} id of which this {@link RolePermission} belongs.
     *
     * @return The {@link Role} id.
     * @since 1.0.0
     */
    @XmlElement(name = "roleId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    @Schema(example = "OYLRT30Z9sA")
    KapuaId getRoleId();

    /**
     * Sets the {@link Permission} that this {@link RolePermission} has.<br>
     * It up to the implementation class to make a clone of the given {@link Permission} or use the given {@link Permission}.
     *
     * @param permission The {@link Permission} to set for this {@link RolePermission}.
     * @since 1.0.0
     */
    void setPermission(Permission permission);

    /**
     * Gets the {@link Permission} that this {@link RolePermission} has.
     *
     * @param <P> The {@link Permission} class implementation.
     * @return The {@link Permission} that this {@link RolePermission} has.
     * @since 1.0.0
     */
    @XmlElement(name = "permission")
    <P extends Permission> P getPermission();
}
