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
package org.eclipse.kapua.service.device.management.packages.model.download;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;

/**
 * Device download package operation entity definition.
 *
 * @since 1.0
 */
@XmlRootElement(name = "packageDownloadOperation")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DevicePackageDownloadOperation {

    private KapuaId id;
    private Integer size;
    private Integer progress;
    private DevicePackageDownloadStatus status;

    /**
     * Get the download package identifier
     *
     * @return
     */
    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getId() {
        return id;
    }

    /**
     * Set the download package identifier
     *
     * @param id
     */
    public void setId(KapuaId id) {
        this.id = id;
    }

    /**
     * Get the package size
     *
     * @return
     */
    @XmlElement(name = "size")
    public Integer getSize() {
        return size;
    }

    /**
     * Set the package size
     *
     * @param downloadSize
     */
    public void setSize(Integer downloadSize) {
        this.size = downloadSize;
    }

    /**
     * Get the download progress
     *
     * @return
     */
    @XmlElement(name = "progress")
    public Integer getProgress() {
        return progress;
    }

    /**
     * Set the download progress
     *
     * @param downloadProgress
     */
    public void setProgress(Integer downloadProgress) {
        this.progress = downloadProgress;
    }

    /**
     * Get the download status
     *
     * @return
     */
    @XmlElement(name = "status")
    public DevicePackageDownloadStatus getStatus() {
        return status;
    }

    /**
     * Set the download status
     *
     * @param status
     */
    public void setStatus(DevicePackageDownloadStatus status) {
        this.status = status;
    }

}
