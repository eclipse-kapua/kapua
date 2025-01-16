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
package org.eclipse.kapua.service.authorization.domain;

import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.domain.AbstractDomain;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link Domain} {@link KapuaEntity} definition
 * <p>
 * {@link Domain} contains the information about the available {@link Actions} for a {@link KapuaEntity} {@link Domain}<br>
 * Services needs to register their own specific {@link Domain}.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "domain")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"name",
        "actions",
        "groupable"})
public interface Domain extends KapuaEntity {//, org.eclipse.kapua.model.domain.Domain {

    String TYPE = "domain";

    @Override
    @Schema(example = "domain")
    default String getType() {
        return TYPE;
    }

    /**
     * Sets the {@link Domain} name.<br>
     * It must be unique within the scope.
     *
     * @param name The name of the {@link Domain}
     * @since 1.0.0
     */
    void setName(String name);

    /**
     * Sets the {@link Domain} service Name.<br>
     *
     * @param serviceName The service name of the {@link Domain}
     * @deprecated since 2.0.0 - This will be removed in future versions as it is not used
     */
    @Deprecated
    void setServiceName(String serviceName);

    /**
     * Gets the {@link Domain} name.
     *
     * @return The {@link Domain} name.
     * @since 1.0.0
     */
    @XmlElement(name = "name")
    @Schema(example = "account")
    String getName();

    /**
     * Gets the {@link Domain} service name.
     *
     * @return The {@link Domain} service name.
     * @deprecated since 2.0.0 - This will be removed in future versions as it is not used
     */
    @Deprecated
    @XmlTransient
    String getServiceName();

    /**
     * Sets the set of {@link Actions} available in this {@link Domain}.<br>
     * It up to the implementation class to make a clone of the set or use the given set.
     *
     * @param actions The set of {@link Actions}.
     * @since 1.0.0
     */
    void setActions(Set<Actions> actions);

    /**
     * Gets the set of {@link Actions} available in this {@link Domain}.<br>
     * The implementation must return the reference of the set and not make a clone.
     *
     * @return The set of {@link Actions}.
     * @since 1.0.0
     */
    @XmlElementWrapper(name = "actions")
    @XmlElement(name = "action")
    @Schema(example = "{\"delete\", \"write\", \"read\"}")
    Set<Actions> getActions();

    /**
     * Sets whether or not this {@link Domain} is group-able or not.
     * This determines if the {@link org.eclipse.kapua.service.authorization.permission.Permission} in this {@link Domain} can have a {@link org.eclipse.kapua.service.authorization.group.Group} or not.
     * This is related to the {@link org.eclipse.kapua.service.authorization.group.Groupable} property of a {@link KapuaEntity}.
     *
     * @param groupable {@code true} if the {@link org.eclipse.kapua.service.authorization.permission.Permission} on this {@link Domain} can have the {@link Permission#getGroupId()} property set, {@code false} otherwise.
     * @since 0.3.1
     */
    void setGroupable(boolean groupable);

    /**
     * Gets whether or not this {@link Domain} is group-able or not.
     *
     * @return {@code true} if the {@link Permission#getGroupId()} can be set, {@code false} otherwise.
     * @since 0.3.1
     */
    @XmlElement(name = "groupable")
    boolean getGroupable();

    /**
     * Returns the {@link KapuaService} {@link org.eclipse.kapua.model.domain.Domain} represented from {@code this} {@link Domain} registry entry.
     *
     * @return The {@link KapuaService} {@link org.eclipse.kapua.model.domain.Domain} of {@code this} {@link Domain}
     * @since 1.0.0
     */
    @XmlTransient
    default org.eclipse.kapua.model.domain.Domain getDomain() {
        return new AbstractDomain() {

            @Override
            public String getName() {
                return Domain.this.getName();
            }

            @Override
            public Set<Actions> getActions() {
                return Domain.this.getActions();
            }

            @Override
            public boolean getGroupable() {
                return Domain.this.getGroupable();
            }

            @Override
            public String getServiceName() {
                return Domain.this.getServiceName();
            }

        };
    }
}
