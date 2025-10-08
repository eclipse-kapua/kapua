/*******************************************************************************
 * Copyright (c) 2016, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authorization.permission;

import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.domain.Domain;
import org.eclipse.kapua.service.authorization.group.Group;

import javax.security.auth.Subject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * {@link Permission} definition.
 * <p>
 * A permission can be associated to a {@link Subject} (using {@link AccessInfo} entity) or a {@link Domain}.<br>
 * {@link Permission}s enable the assignee to do {@link Actions} under specified {@link Domain} and in specified scopes.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "permission")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
            "domain",
            "action",
            "targetScopeId",
            "groupId",
            "forwardable"
        },
        factoryClass = PermissionXmlRegistry.class,
        factoryMethod = "newPermission")
public interface Permission {

    String WILDCARD = "*";
    String SEPARATOR = ":";

    /**
     * Sets the domain on which the {@link Permission} gives access.
     *
     * @param domain The domain of the {@link Permission}.
     * @since 1.0.0
     */
    void setDomain(String domain);

    /**
     * Gets the domain on which the {@link Permission} gives access.
     *
     * @return The domain on which the {@link Permission} gives access.
     * @since 1.0.0
     */
    @XmlElement(name = "domain")
    String getDomain();

    /**
     * Sets the {@link org.eclipse.kapua.model.domain.Actions} that this {@link Permission} allows to do on the domain.
     *
     * @param action The {@link javax.swing.Action} that this {@link Permission} allows
     * @since 1.0.0
     */
    void setAction(Actions action);

    /**
     * Gets the {@link Actions} that this {@link Permission} allows to do on the domain.
     *
     * @return The {@link Actions} that this {@link Permission} allows.
     * @since 1.0.0
     */
    @XmlElement(name = "action")
    Actions getAction();

    /**
     * Sets the target scope id that this {@link Permission} gives access.
     *
     * @param targetScopeId The target scope id that this {@link Permission} gives access.
     * @since 1.0.0
     */
    void setTargetScopeId(KapuaId targetScopeId);

    /**
     * Gets the target scope id that this {@link Permission} gives access.
     *
     * @return The target scope id that this {@link Permission} gives access.
     * @since 1.0.0
     */
    @XmlElement(name = "targetScopeId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    KapuaId getTargetScopeId();

    /**
     * Sets the {@link Group} id that this {@link Permission} gives access.
     *
     * @param groupId The {@link Group} id that this {@link Permission} gives access.
     * @since 1.0.0
     */
    void setGroupId(KapuaId groupId);

    /**
     * Gets the {@link Group} id that this {@link Permission} gives access.
     *
     * @return The {@link Group} id that this {@link Permission} gives access.
     * @since 1.0.0
     */
    @XmlElement(name = "groupId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    KapuaId getGroupId();

    /**
     * Sets whether this {@link Permission} is valid also for children scopeId.
     *
     * @param forwardable {@code true} if this {@link Permission} is forward-able to children scopeIds.
     * @since 1.0.0
     */
    void setForwardable(boolean forwardable);

    /**
     * Gets whether this {@link Permission} is valid also for children scopeIds.
     * If a {@link Permission} is forward-able to children, the {@link Permission} will be valid
     * for all scopeIds of the {@link #getTargetScopeId()} scopeId.
     *
     * @return {@code true} if this {@link Permission} is forward-able to children scopeIds.
     * @since 1.0.0
     */
    @XmlElement(name = "forwardable")
    boolean getForwardable();
}
