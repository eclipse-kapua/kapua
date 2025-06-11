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
package org.eclipse.kapua.service.device.management.packages.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link DevicePackageBundleInfo} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "bundleInfo")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = DevicePackageXmlRegistry.class, factoryMethod = "newDevicePackageBundleInfo")
public interface DevicePackageBundleInfo {

    /**
     * Gets the name.
     *
     * @return The name.
     * @since 1.0.0
     */
    @XmlElement(name = "name")
    @Schema(
        description = "The bundle name",
        example = "org.eclipse.kura.demo.heater"
    )
    String getName();

    /**
     * Sets the name.
     *
     * @param name The name.
     * @since 1.0.0
     */
    void setName(String name);

    /**
     * Gets the version.
     *
     * @return The version.
     * @since 1.0.0
     */
    @XmlElement(name = "version")
    @Schema(
        description = "The bundle version",
        example = "1.0.300"
    )
    String getVersion();

    /**
     * Sets the version.
     *
     * @param version The version.
     * @since 1.0.0
     */
    void setVersion(String version);
}
