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
package org.eclipse.kapua.service.device.management.bundle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * {@link DeviceBundle} definition.
 * <p>
 * This entity is used to get information about bundles installed in the device.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "bundle")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DeviceBundle {

    public long id;
    public String name;
    public String version;
    public String state;

    /**
     * Gets the bundle identifier.
     *
     * @return The bundle identifier.
     * @since 1.0.0
     */
    @XmlElement(name = "id")
    public long getId() {
        return id;
    }

    /**
     * Sets the bundle identifier.
     *
     * @param id
     *         The bundle identifier.
     * @since 1.0.0
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the name.
     *
     * @return The name.
     * @since 1.0.0
     */
    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name
     *         The name.
     * @since 1.0.0
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the state.
     *
     * @return The state.
     * @since 1.0.0
     */
    @XmlElement(name = "state")
    public String getState() {
        return state;
    }

    /**
     * Sets the state.
     *
     * @param state
     *         The state.
     * @since 1.0.0
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the bundle version.
     *
     * @return The version.
     * @since 1.0.0
     */
    @XmlElement(name = "version")
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version
     *         The version.
     * @since 1.0.0
     */
    public void setVersion(String version) {
        this.version = version;
    }

}
