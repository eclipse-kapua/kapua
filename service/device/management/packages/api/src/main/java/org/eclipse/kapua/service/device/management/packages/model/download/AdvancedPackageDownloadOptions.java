/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
import javax.xml.bind.annotation.XmlType;

/**
 * Advanced options for the {@link org.eclipse.kapua.service.device.registry.Device} when performing a {@link DevicePackageDownloadOperation}
 *
 * @since 1.1.0
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class AdvancedPackageDownloadOptions {

    private Boolean restart;
    private Integer blockSize;
    private Integer blockDelay;
    private Integer blockTimeout;
    private Integer notifyBlockSize;
    private String installVerifierURI;

    /**
     * Constructor.
     *
     * @since 1.1.0
     */
    public AdvancedPackageDownloadOptions() {
    }

    /**
     * Clone Constructor.
     *
     * @param advancedPackageDownloadOptions
     *         The {@link AdvancedPackageDownloadOptions} to clone.
     * @since 1.1.0
     */
    public AdvancedPackageDownloadOptions(AdvancedPackageDownloadOptions advancedPackageDownloadOptions) {
        super();

        setRestart(advancedPackageDownloadOptions.getRestart());
        setBlockSize(advancedPackageDownloadOptions.getBlockSize());
        setBlockDelay(advancedPackageDownloadOptions.getBlockDelay());
        setBlockTimeout(advancedPackageDownloadOptions.getBlockTimeout());
        setNotifyBlockSize(advancedPackageDownloadOptions.getNotifyBlockSize());
        setInstallVerifierURI(advancedPackageDownloadOptions.getInstallVerifierURI());
    }

    /**
     * Gets whether or not to restart the download from the beginning.
     *
     * @return {@code true} if the download must be restarted from the beginning, {@code false} otherwise.
     * @since 1.2.0
     */
    public Boolean getRestart() {
        return restart;
    }

    /**
     * Sets whether or not to restart the download from the beginning.
     *
     * @param restart
     *         {@code true} if the download must be restarted from the beginning, {@code false} otherwise.
     * @since 1.2.0
     */
    public void setRestart(Boolean restart) {
        this.restart = restart;
    }

    /**
     * Gets the size in {@code Byte}s of the blocks to transfer from the URI.
     *
     * @return The size in {@code Byte}s of the blocks to transfer from the URI.
     * @since 1.1.0
     */
    @XmlElement(name = "blockSize")
    public Integer getBlockSize() {
        return blockSize;
    }

    /**
     * Sets the size in {@code Byte}s of the blocks to transfer from the URI.
     *
     * @param blockSize
     *         The size in {@code Byte}s of the blocks to transfer from the URI.
     * @since 1.1.0
     */
    public void setBlockSize(Integer blockSize) {
        this.blockSize = blockSize;
    }

    /**
     * Gets the delay between each block transfer from the URI.
     *
     * @return The delay between each block transfer from the URI.
     * @since 1.1.0
     */
    @XmlElement(name = "blockDelay")
    public Integer getBlockDelay() {
        return blockDelay;
    }

    /**
     * Sets the delay between each block transfer from the URI.
     *
     * @param blockDelay
     *         The delay between each block transfer from the URI.
     * @since 1.1.0
     */
    public void setBlockDelay(Integer blockDelay) {
        this.blockDelay = blockDelay;
    }

    /**
     * Gets the timeout for transferring a block from the URI.
     *
     * @return The timeout for transferring a block from the URI.
     * @since 1.1.0
     */
    @XmlElement(name = "blockTimeout")
    public Integer getBlockTimeout() {
        return blockTimeout;
    }

    /**
     * Sets the timeout for transferring a block from the URI.
     *
     * @param blockTimeout
     *         The timeout for transferring a block from the URI.
     * @since 1.1.0
     */
    public void setBlockTimeout(Integer blockTimeout) {
        this.blockTimeout = blockTimeout;
    }

    /**
     * Gets the size in {@code Byte}s of the blocks to be transfer to cause a {@link org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotification} to be
     * sent from the {@link org.eclipse.kapua.service.device.registry.Device}.
     *
     * @return The size in {@code Byte}s of the blocks to be transfer to cause a {@link org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotification}.
     * @since 1.1.0
     */
    @XmlElement(name = "notifyBlockSize")
    public Integer getNotifyBlockSize() {
        return notifyBlockSize;
    }

    /**
     * Sets the size in {@code Byte}s of the blocks to be transfer to cause a {@link org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotification} to be
     * sent from the {@link org.eclipse.kapua.service.device.registry.Device}.
     *
     * @param notifyBlockSize
     *         The size in {@code Byte}s of the blocks to be transfer to cause a {@link org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotification}.
     * @since 1.1.0
     */
    public void setNotifyBlockSize(Integer notifyBlockSize) {
        this.notifyBlockSize = notifyBlockSize;
    }

    /**
     * Gets the URI for the executable shell script to verify the installing of the downloaded file.
     *
     * @return The URI for the executable shell script to verify the installing of the downloaded file.
     * @since 1.1.0
     */
    @XmlElement(name = "installVerifierURI")
    public String getInstallVerifierURI() {
        return installVerifierURI;
    }

    /**
     * Sets the URI for the executable shell script to verify the installing of the downloaded file.
     *
     * @param installVerifiesURI
     *         The URI for the executable shell script to verify the installing of the downloaded file.
     * @since 1.1.0
     */
    public void setInstallVerifierURI(String installVerifiesURI) {
        this.installVerifierURI = installVerifiesURI;
    }
}
