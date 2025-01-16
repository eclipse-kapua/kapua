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

import org.eclipse.kapua.model.KapuaUpdatableEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Access info entity definition.<br>
 * It contains all authorization accesses for a {@link org.eclipse.kapua.service.user.User}.<br>
 * It refers to the {@link org.eclipse.kapua.service.user.User} entity by the {@link AccessInfo#getUserId()} property.<br>
 * <br>
 * {@link AccessInfo} is unique by the {@link AccessInfo#getUserId()} property.
 *
 * @since 1.0.0
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "userId" }, //
        factoryClass = AccessInfoXmlRegistry.class, //
        factoryMethod = "newAccessInfo")
@Schema(description = "An AccessInfo represents all the security objects (Roles and Permissions) that can be attached to a User. There will be exactly one AccessInfo object for every User")
public interface AccessInfo extends KapuaUpdatableEntity {

    String TYPE = "accessInfo";

    @Override
    @Schema(example = "accessInfo")
    default String getType() {
        return TYPE;
    }

    /**
     * Sets the user id.
     *
     * @param userId The user id.
     * @since 1.0.0
     */
    void setUserId(KapuaId userId);

    /**
     * Gets the user id.
     *
     * @return The user id.
     * @since 1.0.0
     */
    @XmlElement(name = "userId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    KapuaId getUserId();
}
