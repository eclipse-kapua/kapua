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
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link AccessInfo} creator definition.<br>
 * It is used to assign a set of {@link Domain}s and {@link Permission}s to the referenced {@link User}.<br>
 *
 * @since 1.0.0
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"userId",
        "roleIds",
        "permissions"},
        factoryClass = AccessInfoXmlRegistry.class,
        factoryMethod = "newAccessInfoCreator")
@Schema(description = "An object that contains the information needed to create an AccessInfo")
public interface AccessInfoCreator extends KapuaEntityCreator<AccessInfo> {

    /**
     * Sets the user identifier.
     *
     * @param userId The user id to set.
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
    @Schema(description = "The ID of the User to which the AccessInfo will be assigned")
    KapuaId getUserId();

    /**
     * Sets the set of {@link Domain} ids to assign to the {@link AccessInfo} created entity.
     * It up to the implementation class to make a clone of the set or use the given set.
     *
     * @param roleIds The set of {@link Domain} ids.
     * @since 1.0.0
     */
    void setRoleIds(Set<KapuaId> roleIds);

    /**
     * Gets the set of {@link Domain} ids added to this {@link AccessInfoCreator}.
     * The implementation must return the reference of the set and not make a clone.
     *
     * @return The set of {@link Domain} ids.
     * @since 1.0.0
     */
    @XmlElementWrapper(name = "roleIds")
    @XmlElement(name = "roleId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    @Schema(description = "An Array of IDs whose Roles will be attached to the new AccessInfo")
    Set<KapuaId> getRoleIds();

    /**
     * Sets the set of {@link Permissions} to assign to the {@link AccessInfo} created entity.
     * It up to the implementation class to make a clone of the set or use the given set.
     *
     * @param permissions The set of {@link Permissions}.
     * @since 1.0.0
     */
    void setPermissions(Set<Permission> permissions);

    /**
     * Gets the set of {@link Permission} added to this {@link AccessInfoCreator}.
     * The implementation must return the reference of the set and not make a clone.
     *
     * @return The set of {@link Permission}.
     * @since 1.0.0
     */
    @XmlElementWrapper(name = "permissions")
    @XmlElement(name = "permission")
    @Schema(description = "An Array of Permission objects to attach to the new AccessInfo")
    <P extends Permission> Set<P> getPermissions();

}
