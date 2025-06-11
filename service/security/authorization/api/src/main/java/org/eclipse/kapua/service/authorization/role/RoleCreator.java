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

import java.security.Permissions;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaNamedEntityCreator;
import org.eclipse.kapua.service.authorization.permission.Permission;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link RoleCreator} definition.
 * <p>
 * It is used to create a new {@link Role} with {@link Permission}s associated
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "roleCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = RoleXmlRegistry.class, factoryMethod = "newRoleCreator")
@Schema(description = "An object containing the properties for the new Role to be created")
public interface RoleCreator extends KapuaNamedEntityCreator<Role> {

    /**
     * Sets the set of {@link Permissions} to assign to the {@link Role} created entity.
     * It up to the implementation class to make a clone of the set or use the given set.
     *
     * @param permissions The set of {@link Permissions}.
     * @since 1.0.0
     */
    void setPermissions(Set<Permission> permissions);

    /**
     * Gets the set of {@link Permission} added to this {@link Role}.
     * The implementation must return the reference of the set and not make a clone.
     *
     * @param <P> The {@link Permission} class implementation.
     * @return The set of {@link Permission}.
     * @since 1.0.0
     */
    @XmlElementWrapper(name = "permissions")
    @XmlElement(name = "permission")
    <P extends Permission> Set<P> getPermissions();
}
