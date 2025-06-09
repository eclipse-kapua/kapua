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
package org.eclipse.kapua.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link KapuaNamedEntityCreator} definition.
 * <p>
 * The {@link KapuaNamedEntityCreator} adds on top of the {@link KapuaUpdatableEntityCreator} the following properties:
 * <ul>
 * <li>name</li>
 * <li>description</li>
 * </ul>
 *
 * <div>
 *
 * <p>
 * <b>Name</b>
 * </p>
 * <p>
 * The <i>Name</i> property is the unique name of the {@link KapuaEntity} in the scope.
 * </p>
 *
 * <p>
 * <b>Description</b>
 * </p>
 * <p>
 * The <i>Description</i> property is the optional description of the {@link KapuaEntity}.
 * </p>
 * </div>
 *
 * @since 1.0.0
 */
@XmlType(propOrder = { "name", "description" })
public abstract class KapuaNamedEntityCreator extends KapuaUpdatableEntityCreator {

    protected String name;
    protected String description;

    public KapuaNamedEntityCreator() {
    }

    public KapuaNamedEntityCreator(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * Constructor
     *
     * @param scopeId
     *         the scope {@link KapuaId}
     * @param name
     *         the name
     * @since 1.0.0
     */
    public KapuaNamedEntityCreator(KapuaId scopeId, String name) {
        super(scopeId);
        this.name = name;
    }

    /**
     * Gets the name
     *
     * @return the name
     * @since 1.0.0
     */
    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    /**
     * Sets the name
     *
     * @param name
     *         the name
     * @since 1.0.0
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description
     *
     * @return the description
     * @since 1.0.0
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description
     *
     * @param description
     *         the description
     * @since 1.0.0
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
