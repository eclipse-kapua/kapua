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
package org.eclipse.kapua.service.device.management.packages.model.uninstall;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Device package uninstall request definition.
 *
 * @since 1.0
 */
@XmlRootElement(name = "uninstallRequest")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {
        "name",
        "version",
        "reboot",
        "rebootDelay" })
public class DevicePackageUninstallRequest {

    public String name;
    public String version;

    public Boolean reboot;
    public Integer rebootDelay;

    /**
     * Get package name
     *
     * @return
     */
    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    /**
     * Set package name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get package version
     *
     * @return
     */
    @XmlElement(name = "version")
    public String getVersion() {
        return version;
    }

    /**
     * Set package version
     *
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Get the device reboot flag
     *
     * @return
     */
    @XmlElement(name = "reboot")
    public Boolean isReboot() {
        return reboot;
    }

    /**
     * Set the device reboot flag
     *
     * @param reboot
     */
    public void setReboot(Boolean reboot) {
        this.reboot = reboot;
    }

    /**
     * Get the reboot delay
     *
     * @return
     */
    @XmlElement(name = "rebootDelay")
    public Integer getRebootDelay() {
        return rebootDelay;
    }

    /**
     * Set the reboot delay
     *
     * @param rebootDelay
     */
    public void setRebootDelay(Integer rebootDelay) {
        this.rebootDelay = rebootDelay;
    }

}
