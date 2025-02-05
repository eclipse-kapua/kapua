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

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.service.device.management.packages.model.FileType;

/**
 * {@link DevicePackageDownloadRequest} definition.
 * <p>
 * All the available options to perform a download (and optionally install).
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "downloadRequest")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DevicePackageDownloadRequest {

    private URI uri;
    private String name;
    private String version;

    private String username;
    private String password;

    private String fileHash;
    private FileType fileType;

    private Boolean install;

    private Boolean reboot;
    private Integer rebootDelay;

    private AdvancedPackageDownloadOptions advancedOptions;

    /**
     * Gets the download URI of the file.
     *
     * @return The download URI of the file.
     * @since 1.0.0
     */
    @XmlElement(name = "uri")
    public URI getUri() {
        return uri;
    }

    /**
     * Sets the download URI of the file.
     *
     * @param uri
     *         The download URI of the file.
     * @since 1.0.0
     */
    public void setUri(URI uri) {
        this.uri = uri;
    }

    /**
     * Gets the package name.
     *
     * @return The package name.
     * @since 1.0.0
     */
    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    /**
     * Sets the package name.
     *
     * @param name
     *         The package name.
     * @since 1.0.0
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the package version.
     *
     * @return The package version.
     * @since 1.0.0
     */
    @XmlElement(name = "version")
    public String getVersion() {
        return version;
    }

    /**
     * Set the package version.
     *
     * @param version
     *         The package version.
     * @since 1.0.0
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets the username to provide as a credential when accessing the URI.
     *
     * @return The username to provide as a credential when accessing the URI.
     * @since 1.1.0
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username to provide as a credential when accessing the URI.
     *
     * @param username
     *         The username to provide as a credential when accessing the URI.
     * @since 1.1.0
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password to provide as a credential when accessing the URI.
     *
     * @return The password to provide as a credential when accessing the URI.
     * @since 1.1.0
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password to provide as a credential when accessing the URI.
     *
     * @param password
     *         The password to provide as a credential when accessing the URI.
     * @since 1.1.0
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the file hash to verify the downloaded file.
     * <p>
     * It must be specifies as {@code {HASH_ALGORITHM}:{HASH_VALUE}}
     * <p>
     * Example: MD5:46cbc7f212b94187cb6480fe9429a89c
     *
     * @return The file hash to verify the downloaded file.
     * @since 1.1.0
     */
    public String getFileHash() {
        return fileHash;
    }

    /**
     * Sets the file hash to verify the downloaded file.
     *
     * @param fileHash
     *         The file hash to verify the downloaded file.
     * @see DevicePackageDownloadRequest#getFileHash()
     * @since 1.1.0
     */
    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    /**
     * Gets the {@link FileType} of the target file to download.
     *
     * @return The {@link FileType} of the target file to download.
     * @since 1.1.0
     */
    public FileType getFileType() {
        return fileType;
    }

    /**
     * Sets the {@link FileType} of the target file to download.
     *
     * @param fileType
     *         The {@link FileType} of the target file to download.
     * @since 1.1.0
     */
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    /**
     * Gets whether or not install the file right after it has been downloaded.
     *
     * @return Whether or not install the file right after it has been downloaded.
     * @since 1.0.0
     */
    @XmlElement(name = "install")
    public Boolean getInstall() {
        return install;
    }

    /**
     * Sets whether or not install the file right after it has been downloaded.
     *
     * @param install
     *         Whether or not install the file right after it has been downloaded.
     * @since 1.0.0
     */
    public void setInstall(Boolean install) {
        this.install = install;
    }

    /**
     * Gets whether or not reboot the device after the operation has been completed.
     *
     * @return Whether or not reboot the device after the operation has been completed.
     * @since 1.0.0
     */
    @XmlElement(name = "reboot")
    public Boolean getReboot() {
        return reboot;
    }

    /**
     * Sets whether or not reboot the device after the operation has been completed.
     *
     * @param reboot
     *         Whether or not reboot the device after the operation has been completed.
     * @since 1.0.0
     */
    public void setReboot(Boolean reboot) {
        this.reboot = reboot;
    }

    /**
     * Gets the delay after which the device is rebooted when the operation has been completed.
     *
     * @return The delay after which the device is rebooted when the operation has been completed.
     * @since 1.0.0
     */
    @XmlElement(name = "rebootDelay")
    public Integer getRebootDelay() {
        return rebootDelay;
    }

    /**
     * Sets the delay after which the device is rebooted when the operation has been completed.
     *
     * @param rebootDelay
     *         The delay after which the device is rebooted when the operation has been completed.
     * @since 1.0.0
     */
    public void setRebootDelay(Integer rebootDelay) {
        this.rebootDelay = rebootDelay;
    }

    /**
     * Gets the {@link AdvancedPackageDownloadOptions} to tune the download operation.
     *
     * @return The {@link AdvancedPackageDownloadOptions} to tune the download operation.
     * @since 1.1.0
     */
    @XmlElement(name = "advancedOptions")
    public AdvancedPackageDownloadOptions getAdvancedOptions() {
        if (advancedOptions == null) {
            advancedOptions = new AdvancedPackageDownloadOptions();
        }

        return advancedOptions;
    }

    /**
     * Sets the {@link AdvancedPackageDownloadOptions} to tune the download operation.
     *
     * @param advancedOptions
     *         The {@link AdvancedPackageDownloadOptions} to tune the download operation.
     * @since 1.1.0
     */
    public void setAdvancedOptions(AdvancedPackageDownloadOptions advancedOptions) {
        this.advancedOptions = advancedOptions;
    }
}
