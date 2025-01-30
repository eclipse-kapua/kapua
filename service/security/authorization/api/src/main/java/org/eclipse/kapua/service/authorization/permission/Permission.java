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
package org.eclipse.kapua.service.authorization.permission;

import javax.security.auth.Subject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.domain.Domain;
import org.eclipse.kapua.service.authorization.group.Group;

/**
 * {@link Permission} definition.<br> A permission can be associated to a {@link Subject} (using {@link AccessInfo} entity) or a {@link Domain}.<br> {@link Permission}s enable the assignee to do
 * {@link Actions} under specified {@link Domain} and in specified scopes.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "permission")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { //
        "domain", //
        "action", //
        "targetScopeId", //
        "groupId", //
        "forwardable" //
}, factoryMethod = "newPermission")
public class Permission {

    public static final String WILDCARD = "*";
    public static final String SEPARATOR = ":";
    @XmlElement(name = "domain")
    private final String domain;
    @XmlElement(name = "action")
    private final Actions action;
    @XmlElement(name = "targetScopeId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    private final KapuaId targetScopeId;
    @XmlElement(name = "groupId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    private final KapuaId groupId;
    @XmlElement(name = "forwardable")
    private final boolean forwardable;

    // For you, JAXB
    private static Permission newPermission() {
        return new Permission(null, null, null);
    }

    public Permission(String domain, Actions action, KapuaId targetScopeId) {
        this(domain, action, targetScopeId, null, false);
    }

    public Permission(String domain, Actions action, KapuaId targetScopeId, KapuaId groupId, boolean forwardable) {
        this.domain = domain;
        this.action = action;
        this.targetScopeId = targetScopeId;
        this.groupId = groupId;
        this.forwardable = forwardable;
    }

    /**
     * Gets the domain on which the {@link Permission} gives access.
     *
     * @return The domain on which the {@link Permission} gives access.
     * @since 1.0.0
     */
    public String getDomain() {
        return this.domain;
    }

    /**
     * Gets the {@link Actions} that this {@link Permission} allows to do on the domain.
     *
     * @return The {@link Actions} that this {@link Permission} allows.
     * @since 1.0.0
     */
    public Actions getAction() {
        return this.action;
    }

    /**
     * Gets the target scope id that this {@link Permission} gives access.
     *
     * @return The target scope id that this {@link Permission} gives access.
     * @since 1.0.0
     */

    public KapuaId getTargetScopeId() {
        return this.targetScopeId;
    }

    /**
     * Gets the {@link Group} id that this {@link Permission} gives access.
     *
     * @return The {@link Group} id that this {@link Permission} gives access.
     * @since 1.0.0
     */

    public KapuaId getGroupId() {
        return this.groupId;
    }

    /**
     * Gets whether or not this {@link Permission} is valid also for children scopeIds. If a {@link Permission} is forward-able to children, the {@link Permission} will be valid for all scopeIds of
     * the {@link #getTargetScopeId()} scopeId.
     *
     * @return {@code true} if this {@link Permission} is forward-able to children scopeIds.
     * @since 1.0.0
     */
    public boolean getForwardable() {
        return this.forwardable;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(domain != null ? domain : Permission.WILDCARD)
                .append(Permission.SEPARATOR)
                .append(action != null ? action.name() : Permission.WILDCARD)
                .append(Permission.SEPARATOR)
                .append(targetScopeId != null ? targetScopeId.getId() : Permission.WILDCARD)
                .append(Permission.SEPARATOR)
                .append(groupId != null ? groupId.getId() : Permission.WILDCARD);

        return sb.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + (action == null ? 0 : action.hashCode());
        result = prime * result + (domain == null ? 0 : domain.hashCode());
        result = prime * result + (targetScopeId == null ? 0 : targetScopeId.hashCode());
        result = prime * result + (groupId == null ? 0 : groupId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Permission other = (Permission) obj;
        if (action != other.action) {
            return false;
        }
        if (domain == null) {
            if (other.domain != null) {
                return false;
            }
        } else if (!domain.equals(other.domain)) {
            return false;
        }
        if (targetScopeId == null) {
            if (other.targetScopeId != null) {
                return false;
            }
        } else if (!targetScopeId.equals(other.targetScopeId)) {
            return false;
        }
        if (groupId == null) {
            return other.groupId == null;
        } else {
            return groupId.equals(other.groupId);
        }
    }
}
