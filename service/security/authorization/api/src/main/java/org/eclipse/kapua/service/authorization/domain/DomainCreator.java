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
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.permission.Permission;

/**
 * {@link DomainCreator} definition.<br>
 * <p>
 * It is used to create a new {@link Domain} with {@link Actions} associated
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "domainCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class DomainCreator extends KapuaEntityCreator { // org.eclipse.kapua.model.domain.Domain {

    private static final long serialVersionUID = -4676187845961673421L;

    private String name;

    private String serviceName;
    private Set<Actions> actions;
    private boolean groupable;

    public DomainCreator() {
    }

    public DomainCreator(KapuaId scopeId, String name) {
        super(scopeId);
        this.name = name;
    }

    public DomainCreator(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * Constructor
     *
     * @param name
     *         The name to set for this {@link DomainCreator}.
     * @since 1.0.0
     */
    public DomainCreator(String name) {
        this.name = name;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Set<Actions> getActions() {
        return actions;
    }

    public boolean getGroupable() {
        return groupable;
    }

    /**
     * Sets the {@link Domain} name.
     *
     * @param name
     *         The {@link Domain} name.
     * @since 1.0.0
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Sets the {@link Domain} service name.
     *
     * @param serviceName
     *         The {@link Domain} name.
     * @since 1.0.0
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Sets the set of {@link Actions} available in the {@link Domain}.<br> It up to the implementation class to make a clone of the set or use the given set.
     *
     * @param actions
     *         The set of {@link Actions}.
     * @since 1.0.0
     */
    public void setActions(Set<Actions> actions) {
        this.actions = actions;
    }

    /**
     * Sets whether or not this {@link Domain} is group-able or not. This determines if the {@link org.eclipse.kapua.service.authorization.permission.Permission} in this {@link Domain} can have a
     * {@link org.eclipse.kapua.service.authorization.group.Group} or not. This is related to the {@link org.eclipse.kapua.service.authorization.group.Groupable} property of a
     * {@link KapuaEntityCreator}.
     *
     * @param groupable
     *         {@code true} if the {@link org.eclipse.kapua.service.authorization.permission.Permission} on this {@link Domain} can have the {@link Permission#getGroupId()} property set, {@code false}
     *         otherwise.
     * @since 0.3.1
     */
    public void setGroupable(boolean groupable) {
        this.groupable = groupable;
    }

}
