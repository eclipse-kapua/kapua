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

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * {@link DevicePackage} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "devicePackage")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DevicePackage {

    private String name;
    private String version;
    private DevicePackageBundleInfos bundleInfos;
    private Date installDate;

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
     * Gets the version.
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

    /**
     * Gets the {@link DevicePackageBundleInfos}.
     *
     * @return The {@link DevicePackageBundleInfos}.
     * @since 1.0.0
     */
    @XmlElement(name = "bundleInfos")
    public DevicePackageBundleInfos getBundleInfos() {
        if (bundleInfos == null) {
            bundleInfos = new DevicePackageBundleInfos();
        }

        return bundleInfos;
    }

    /**
     * Sets the {@link DevicePackageBundleInfos}.
     *
     * @param bundleInfos
     *         The {@link DevicePackageBundleInfos}.
     * @since 1.0.0
     */
    public void setBundleInfos(DevicePackageBundleInfos bundleInfos) {
        this.bundleInfos = bundleInfos;
    }

    /**
     * Gets the installation date.
     *
     * @return The installation date.
     * @since 1.0.0
     */
    @XmlElement(name = "installDate")
    public Date getInstallDate() {
        return installDate;
    }

    /**
     * Sets the installation date.
     *
     * @param installDate
     *         The installation date.
     * @since 1.0.0
     */
    public void setInstallDate(Date installDate) {
        this.installDate = installDate;
    }
}
