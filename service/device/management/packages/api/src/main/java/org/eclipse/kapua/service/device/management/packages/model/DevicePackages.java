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
package org.eclipse.kapua.service.device.management.packages.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.KapuaSerializable;

/**
 * {@link DevicePackages} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "devicePackages")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DevicePackages implements KapuaSerializable {

    private static final long serialVersionUID = 2450088980495469562L;

    public List<DevicePackage> deploymentPackages;

    /**
     * Gets the {@link DevicePackage} {@link List}
     *
     * @return The {@link DevicePackage} {@link List}
     * @since 1.0.0
     */
    @XmlElement(name = "devicePackage")
    public List<DevicePackage> getPackages() {
        if (deploymentPackages == null) {
            deploymentPackages = new ArrayList<>();
        }

        return deploymentPackages;
    }
}
