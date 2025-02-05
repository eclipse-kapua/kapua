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

import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link DevicePackageUninstallOperation} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "devicePackageUninstallOperation")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DevicePackageUninstallOperation {

    private KapuaId id;
    private String name;
    private String version;
    private DevicePackageUninstallStatus status;

    /**
     * Get the package identifier
     *
     * @return
     */
    @XmlElement(name = "id")
    public KapuaId getId() {
        return id;
    }

    /**
     * Set the package identifier
     *
     * @param id
     */
    public void setId(KapuaId id) {
        this.id = id;
    }

    /**
     * Get the package name
     *
     * @return
     */
    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    /**
     * Set the package name
     *
     * @param packageName
     */
    public void setName(String packageName) {
        this.name = packageName;
    }

    /**
     * Get the package version
     *
     * @return
     */
    @XmlElement(name = "version")
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
     * Get the package uninstall status
     *
     * @return
     */
    @XmlElement(name = "status")
    public DevicePackageUninstallStatus getStatus() {
        return status;
    }

    /**
     * Set the package uninstall status
     *
     * @param status
     */
    public void setStatus(DevicePackageUninstallStatus status) {
        this.status = status;
    }

}
