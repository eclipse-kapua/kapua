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

import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.authorization.permission.Permission;

/**
 * Access permission entity.<br> Describes a {@link Permission} associated to the access info.<br> Wrapping of the {@link Permission} into this class is intended to adds auditing informations like
 * {@link AccessPermission#getCreatedBy()} and{@link AccessPermission#getCreatedOn()}.<br>
 * <br>
 * This is a not editable entity so it can be only removed or created and therefore any change to {@link AccessPermission#getAccessInfoId()} and {@link AccessPermission#getPermission()} property is
 * forbidden.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "accessPermission")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "accessInfoId", "permission" }, //
        factoryClass = AccessPermissionXmlRegistry.class, //
        factoryMethod = "newAccessPermission")
public interface AccessPermission extends KapuaEntity {

    String TYPE = "accessPermission";

    @Override
    default String getType() {
        return TYPE;
    }

    /**
     * Sets the {@link AccessInfo} id of which this {@link AccessPermission} belongs.
     *
     * @param accessId
     *         The {@link AccessInfo} id.
     * @since 1.0.0
     */
    void setAccessInfoId(KapuaId accessId);

    /**
     * Gets the {@link AccessInfo} id of which this {@link AccessPermission} belongs.
     *
     * @return The {@link AccessInfo} id.
     * @since 1.0.0
     */
    @XmlElement(name = "accessInfoId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    KapuaId getAccessInfoId();

    /**
     * Sets the {@link Permission} that this {@link AccessPermission} has.<br> It up to the implementation class to make a clone of the given {@link Permission} or use the given {@link Permission}.
     *
     * @param permission
     *         The {@link Permission} to set for this {@link AccessPermission}.
     * @since 1.0.0
     */
    void setPermission(Permission permission);

    /**
     * Gets the {@link Permission} that this {@link AccessPermission} has.
     *
     * @return The {@link Permission} that this {@link AccessPermission} has.
     */
    @XmlElement(name = "permission")
    Permission getPermission();

}
