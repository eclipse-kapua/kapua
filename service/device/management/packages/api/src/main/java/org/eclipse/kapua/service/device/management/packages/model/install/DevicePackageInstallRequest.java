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
package org.eclipse.kapua.service.device.management.packages.model.install;

/**
 * Device package install request definition.
 *
 * @since 1.0
 */
public class DevicePackageInstallRequest {

    private String name;
    private String version;
    private Boolean reboot;
    private Integer rebootDelay;

    /**
     * Get the package name
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set the package name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the package version
     *
     * @return
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set the package version
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
