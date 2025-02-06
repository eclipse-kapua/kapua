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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.service.device.management.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.KapuaSerializable;

/**
 * {@link DeviceConfiguration} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "deviceConfigurations")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DeviceConfiguration implements KapuaSerializable {

    private static final long serialVersionUID = -2167999497954676423L;

    private List<DeviceComponentConfiguration> configurations;

    /**
     * Gets the {@link List} of {@link DeviceComponentConfiguration}.
     *
     * @return The {@link List} of {@link DeviceComponentConfiguration}.
     * @since 1.0.0
     */
    @XmlElement(name = "configuration")
    public List<DeviceComponentConfiguration> getComponentConfigurations() {
        if (configurations == null) {
            configurations = new ArrayList<>();
        }

        return configurations;
    }

    /**
     * Adds a {@link DeviceComponentConfiguration} to the {@link List} of {@link DeviceComponentConfiguration}.
     *
     * @param componentConfiguration
     *         The {@link DeviceComponentConfiguration} to add.
     * @since 2.0.0
     */
    public void addComponentConfiguration(DeviceComponentConfiguration componentConfiguration) {
        getComponentConfigurations().add(componentConfiguration);
    }

    /**
     * Sets the {@link List} of {@link DeviceComponentConfiguration}.
     *
     * @param componentConfigurations
     *         The {@link List} of {@link DeviceComponentConfiguration}.
     * @since 2.0.0
     */
    public void setComponentConfigurations(List<DeviceComponentConfiguration> componentConfigurations) {
        this.configurations = componentConfigurations;
    }

}
