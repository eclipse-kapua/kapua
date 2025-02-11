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
package org.eclipse.kapua.message.device.lifecycle;

/**
 * {@link KapuaBirthPayload} definition.
 *
 * @since 1.0.0
 */
public class KapuaBirthPayload extends KapuaLifecyclePayload {

    private static final long serialVersionUID = 304433271740125817L;

    /**
     * Constructor.
     *
     * @since 1.1.0
     */
    public KapuaBirthPayload() {
        super();
    }

    /**
     * Constructor.
     * <p>
     * Sets all available properties of the {@link KapuaBirthPayload} at once.
     *
     * @param uptime
     *         The {@link KapuaBirthPayloadAttibutes#UPTIME} of the {@link KapuaBirthMessage}
     * @param displayName
     *         The {@link KapuaBirthPayloadAttibutes#DISPLAY_NAME} of the {@link KapuaBirthMessage}
     * @param modelName
     *         The {@link KapuaBirthPayloadAttibutes#MODEL_NAME} of the {@link KapuaBirthMessage}
     * @param modelId
     *         The {@link KapuaBirthPayloadAttibutes#MODEL_ID} of the {@link KapuaBirthMessage}
     * @param partNumber
     *         The {@link KapuaBirthPayloadAttibutes#PART_NUMBER} of the {@link KapuaBirthMessage}
     * @param serialNumber
     *         The {@link KapuaBirthPayloadAttibutes#SERIAL_NUMBER} of the {@link KapuaBirthMessage}
     * @param firmware
     *         The {@link KapuaBirthPayloadAttibutes#FIRMWARE} of the {@link KapuaBirthMessage}
     * @param firmwareVersion
     *         The {@link KapuaBirthPayloadAttibutes#FIRMWARE_VERSION} of the {@link KapuaBirthMessage}
     * @param bios
     *         The {@link KapuaBirthPayloadAttibutes#BIOS} of the {@link KapuaBirthMessage}
     * @param biosVersion
     *         The {@link KapuaBirthPayloadAttibutes#BIOS_VERSION} of the {@link KapuaBirthMessage}
     * @param os
     *         The {@link KapuaBirthPayloadAttibutes#OS} of the {@link KapuaBirthMessage}
     * @param osVersion
     *         The {@link KapuaBirthPayloadAttibutes#OS_VERSION} of the {@link KapuaBirthMessage}
     * @param jvm
     *         The {@link KapuaBirthPayloadAttibutes#JVM} of the {@link KapuaBirthMessage}
     * @param jvmVersion
     *         The {@link KapuaBirthPayloadAttibutes#JVM_VERSION} of the {@link KapuaBirthMessage}
     * @param jvmProfile
     *         The {@link KapuaBirthPayloadAttibutes#JVM_PROFILE} of the {@link KapuaBirthMessage}
     * @param containerFramework
     *         The {@link KapuaBirthPayloadAttibutes#CONTAINER_FRAMEWORK} of the {@link KapuaBirthMessage}
     * @param containerFrameworkVersion
     *         The {@link KapuaBirthPayloadAttibutes#CONTAINER_FRAMEWORK_VERSION} of the {@link KapuaBirthMessage}
     * @param applicationFramework
     *         The {@link KapuaBirthPayloadAttibutes#APPLICATION_FRAMEWORK} of the {@link KapuaBirthMessage}
     * @param applicationFrameworkVersion
     *         The {@link KapuaBirthPayloadAttibutes#APPLICATION_FRAMEWORK_VERSION} of the {@link KapuaBirthMessage}
     * @param connectionInterface
     *         The {@link KapuaBirthPayloadAttibutes#CONNECTION_INTERFACE} of the {@link KapuaBirthMessage}
     * @param connectionIp
     *         The {@link KapuaBirthPayloadAttibutes#CONNECTION_IP} of the {@link KapuaBirthMessage}
     * @param acceptEncoding
     *         The {@link KapuaBirthPayloadAttibutes#ACCEPT_ENCODING} of the {@link KapuaBirthMessage}
     * @param applicationIdentifiers
     *         The {@link KapuaBirthPayloadAttibutes#APPLICATION_IDENTIFIERS} of the {@link KapuaBirthMessage}
     * @param availableProcessors
     *         The {@link KapuaBirthPayloadAttibutes#AVAILABLE_PROCESSORS} of the {@link KapuaBirthMessage}
     * @param totalMemory
     *         The {@link KapuaBirthPayloadAttibutes#TOTAL_MEMORY} of the {@link KapuaBirthMessage}
     * @param osArch
     *         The {@link KapuaBirthPayloadAttibutes#OS_ARCH} of the {@link KapuaBirthMessage}
     * @param modemImei
     *         The {@link KapuaBirthPayloadAttibutes#MODEM_IMEI} of the {@link KapuaBirthMessage}
     * @param modemImsi
     *         The {@link KapuaBirthPayloadAttibutes#MODEM_IMSI} of the {@link KapuaBirthMessage}
     * @param modemIccid
     *         The {@link KapuaBirthPayloadAttibutes#MODEM_ICCID} of the {@link KapuaBirthMessage}
     * @param extendedProperties
     *         The {@link KapuaBirthPayloadAttibutes#EXTENDED_PROPERTIES} of the {@link KapuaBirthMessage}
     * @param tamperStatus
     *         The {@link KapuaBirthPayloadAttibutes#TAMPER_STATUS} of the {@link KapuaBirthMessage}
     * @since 1.0.0
     */
    public KapuaBirthPayload(String uptime,
            String displayName,
            String modelName,
            String modelId,
            String partNumber,
            String serialNumber,
            String firmware,
            String firmwareVersion,
            String bios,
            String biosVersion,
            String os,
            String osVersion,
            String jvm,
            String jvmVersion,
            String jvmProfile,
            String containerFramework,
            String containerFrameworkVersion,
            String applicationFramework,
            String applicationFrameworkVersion,
            String connectionInterface,
            String connectionIp,
            String acceptEncoding,
            String applicationIdentifiers,
            String availableProcessors,
            String totalMemory,
            String osArch,
            String modemImei,
            String modemImsi,
            String modemIccid,
            String extendedProperties,
            String tamperStatus) {

        setUptime(uptime);
        setDisplayName(displayName);
        setModelName(modelName);
        setModelId(modelId);
        setPartNumber(partNumber);
        setSerialNumber(serialNumber);
        setFirmware(firmware);
        setFirmwareVersion(firmwareVersion);
        setBios(bios);
        setBiosVersion(biosVersion);
        setOs(os);
        setOsVersion(osVersion);
        setJvm(jvm);
        setJvmVersion(jvmVersion);
        setJvmProfile(jvmProfile);
        setContainerFramework(containerFramework);
        setContainerFrameworkVersion(containerFrameworkVersion);
        setApplicationFramework(applicationFramework);
        setApplicationFrameworkVersion(applicationFrameworkVersion);
        setConnectionInterface(connectionInterface);
        setConnectionIp(connectionIp);
        setAcceptEncoding(acceptEncoding);
        setApplicationIdentifiers(applicationIdentifiers);
        setAvailableProcessors(availableProcessors);
        setTotalMemory(totalMemory);
        setOsArch(osArch);
        setModemImei(modemImei);
        setModemImsi(modemImsi);
        setModemIccid(modemIccid);
        setExtendedProperties(extendedProperties);
        setTamperStatus(tamperStatus);
    }

    /**
     * Gets the uptime.
     *
     * @return The uptime.
     * @since 1.0.0
     */
    public String getUptime() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.UPTIME);
    }

    /**
     * Sets the uptime.
     *
     * @param uptime
     *         The uptime.
     * @since 1.1.0
     */
    public void setUptime(String uptime) {
        getMetrics().put(KapuaBirthPayloadAttibutes.UPTIME, uptime);
    }

    /**
     * Gets the display name.
     *
     * @return The display name.
     * @since 1.0.0
     */
    public String getDisplayName() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.DISPLAY_NAME);
    }

    /**
     * Sets the display name.
     *
     * @param displayName
     *         The display name.
     * @since 1.1.0
     */
    public void setDisplayName(String displayName) {
        getMetrics().put(KapuaBirthPayloadAttibutes.DISPLAY_NAME, displayName);
    }

    /**
     * Gets the model name.
     *
     * @return The model name.
     * @since 1.0.0
     */
    public String getModelName() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.MODEL_NAME);
    }

    /**
     * Sets the model name.
     *
     * @param modelName
     *         The model name.
     * @since 1.1.0
     */
    public void setModelName(String modelName) {
        getMetrics().put(KapuaBirthPayloadAttibutes.MODEL_NAME, modelName);
    }

    /**
     * Gets the model identifier.
     *
     * @return The model identifier.
     * @since 1.0.0
     */
    public String getModelId() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.MODEL_ID);
    }

    /**
     * Sets the model identifier.
     *
     * @param modelId
     *         The model identifier.
     * @since 1.1.0
     */
    public void setModelId(String modelId) {
        getMetrics().put(KapuaBirthPayloadAttibutes.MODEL_ID, modelId);
    }

    /**
     * Gets the part number.
     *
     * @return The part number.
     * @since 1.0.0
     */
    public String getPartNumber() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.PART_NUMBER);
    }

    /**
     * Sets the part number.
     *
     * @param partNumber
     *         The part number.
     * @since 1.1.0
     */
    public void setPartNumber(String partNumber) {
        getMetrics().put(KapuaBirthPayloadAttibutes.PART_NUMBER, partNumber);
    }

    /**
     * Gets the serial number.
     *
     * @return The serial number.
     * @since 1.0.0
     */
    public String getSerialNumber() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.SERIAL_NUMBER);
    }

    /**
     * Sets the serial number.
     *
     * @param serialNumber
     *         The serial number.
     * @since 1.1.0
     */
    public void setSerialNumber(String serialNumber) {
        getMetrics().put(KapuaBirthPayloadAttibutes.SERIAL_NUMBER, serialNumber);
    }

    /**
     * Gets the firmware name.
     *
     * @return The firmware name.
     * @since 1.0.0
     */
    public String getFirmware() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.FIRMWARE);
    }

    /**
     * Sets the firmware name.
     *
     * @param firmware
     *         The firmware name.
     * @since 1.1.0
     */
    public void setFirmware(String firmware) {
        getMetrics().put(KapuaBirthPayloadAttibutes.FIRMWARE, firmware);
    }

    /**
     * Gets the firmware version.
     *
     * @return The firmware version.
     * @since 1.0.0
     */
    public String getFirmwareVersion() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.FIRMWARE_VERSION);
    }

    /**
     * Sets the firmware version.
     *
     * @param firmwareVersion
     *         The firmware version.
     * @since 1.1.0
     */
    public void setFirmwareVersion(String firmwareVersion) {
        getMetrics().put(KapuaBirthPayloadAttibutes.FIRMWARE_VERSION, firmwareVersion);
    }

    /**
     * Gets the bios name
     *
     * @return The bios name.
     * @since 1.0.0
     */
    public String getBios() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.BIOS);
    }

    /**
     * Sets the bios name.
     *
     * @param bios
     *         The bios name.
     * @since 1.1.0
     */
    public void setBios(String bios) {
        getMetrics().put(KapuaBirthPayloadAttibutes.BIOS, bios);
    }

    /**
     * Gets the bios version.
     *
     * @return The bios version.
     * @since 1.0.0
     */
    public String getBiosVersion() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.BIOS_VERSION);
    }

    /**
     * Sets the bios version.
     *
     * @param biosVersion
     *         The bios version.
     * @since 1.1.0
     */
    public void setBiosVersion(String biosVersion) {
        getMetrics().put(KapuaBirthPayloadAttibutes.BIOS_VERSION, biosVersion);
    }

    /**
     * Gets the operating system name.
     *
     * @return The operating system name.
     * @since 1.0.0
     */
    public String getOs() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.OS);
    }

    /**
     * Sets the operating system name.
     *
     * @param os
     *         The operating system name.
     * @since 1.1.0
     */
    public void setOs(String os) {
        getMetrics().put(KapuaBirthPayloadAttibutes.OS, os);
    }

    /**
     * Gets the operating system architecture.
     *
     * @return The operating system architecture.
     * @since 1.0.0
     */
    public String getOsArch() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.OS_ARCH);
    }

    /**
     * Sets the operating system architecture.
     *
     * @param osArch
     *         The operating system architecture.
     * @since 1.1.0
     */
    public void setOsArch(String osArch) {
        getMetrics().put(KapuaBirthPayloadAttibutes.OS_ARCH, osArch);
    }

    /**
     * Gets the operating system version.
     *
     * @return The operating system version.
     * @since 1.0.0
     */
    public String getOsVersion() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.OS_VERSION);
    }

    /**
     * Sets the operating system version.
     *
     * @param osVersion
     *         The operating system version.
     * @since 1.0.0
     */
    public void setOsVersion(String osVersion) {
        getMetrics().put(KapuaBirthPayloadAttibutes.OS_VERSION, osVersion);
    }

    /**
     * Gets the java virtual machine name/vendor.
     *
     * @return The java virtual machine name/vendor.
     * @since 1.0.0
     */
    public String getJvm() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.JVM);
    }

    /**
     * Sets the java virtual machine name/vendor.
     *
     * @param jvm
     *         The java virtual machine name/vendor.
     * @since 1.1.0
     */
    public void setJvm(String jvm) {
        getMetrics().put(KapuaBirthPayloadAttibutes.JVM, jvm);
    }

    /**
     * Gets the java virtual machine version.
     *
     * @return The java virtual machine version.
     * @since 1.0.0
     */
    public String getJvmVersion() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.JVM_VERSION);
    }

    /**
     * Sets the java virtual machine version.
     *
     * @param jvmVersion
     *         The java virtual machine version.
     * @since 1.1.0
     */
    public void setJvmVersion(String jvmVersion) {
        getMetrics().put(KapuaBirthPayloadAttibutes.JVM_VERSION, jvmVersion);
    }

    /**
     * Gets the java virtual machine profile.
     *
     * @return The java virtual machine profile.
     * @since 1.0.0
     */
    public String getJvmProfile() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.JVM_PROFILE);
    }

    /**
     * Sets the java virtual machine profile.
     *
     * @param jvmProfile
     *         The java virtual machine profile.
     * @since 1.1.0
     */
    public void setJvmProfile(String jvmProfile) {
        getMetrics().put(KapuaBirthPayloadAttibutes.JVM_PROFILE, jvmProfile);
    }

    /**
     * Gets the container framework name.
     *
     * @return The container framework name.
     * @since 1.0.0
     */
    public String getContainerFramework() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.CONTAINER_FRAMEWORK);
    }

    /**
     * Sets the container framework name.
     *
     * @param containerFramework
     *         The container framework name.
     * @since 1.1.0
     */
    public void setContainerFramework(String containerFramework) {
        getMetrics().put(KapuaBirthPayloadAttibutes.CONTAINER_FRAMEWORK, containerFramework);
    }

    /**
     * Gets the container framework version.
     *
     * @return The container framework version.
     * @since 1.0.0
     */
    public String getContainerFrameworkVersion() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.CONTAINER_FRAMEWORK_VERSION);
    }

    /**
     * Sets the container framework version.
     *
     * @param containerFrameworkVersion
     *         The container framework version.
     * @since 1.1.0
     */
    public void setContainerFrameworkVersion(String containerFrameworkVersion) {
        getMetrics().put(KapuaBirthPayloadAttibutes.CONTAINER_FRAMEWORK_VERSION, containerFrameworkVersion);
    }

    /**
     * Gets the application framework name.
     *
     * @return The application framework name.
     * @since 1.0.0
     */
    public String getApplicationFramework() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.APPLICATION_FRAMEWORK);
    }

    /**
     * Sets the application framework name.
     *
     * @param applicationFramework
     *         The application framework name.
     * @since 1.1.0
     */
    public void setApplicationFramework(String applicationFramework) {
        getMetrics().put(KapuaBirthPayloadAttibutes.APPLICATION_FRAMEWORK, applicationFramework);
    }

    /**
     * Gets the application framework version
     *
     * @return The application framework version
     * @since 1.0.0
     */
    public String getApplicationFrameworkVersion() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.APPLICATION_FRAMEWORK_VERSION);
    }

    /**
     * Sets the application framework version
     *
     * @param applicationFrameworkVersion
     *         The application framework version
     * @since 1.1.0
     */
    public void setApplicationFrameworkVersion(String applicationFrameworkVersion) {
        getMetrics().put(KapuaBirthPayloadAttibutes.APPLICATION_FRAMEWORK_VERSION, applicationFrameworkVersion);
    }

    /**
     * Gets the connection interface.
     *
     * @return The connection interface.
     * @since 1.0.0
     */
    public String getConnectionInterface() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.CONNECTION_INTERFACE);
    }

    /**
     * Sets the connection interface.
     *
     * @param connectionInterface
     *         The connection interface.
     * @since 1.1.0
     */
    public void setConnectionInterface(String connectionInterface) {
        getMetrics().put(KapuaBirthPayloadAttibutes.CONNECTION_INTERFACE, connectionInterface);
    }

    /**
     * Gets the connection interface ip.
     *
     * @return The connection interface ip.
     * @since 1.0.0
     */
    public String getConnectionIp() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.CONNECTION_IP);
    }

    /**
     * Sets the connection interface ip.
     *
     * @param connectionIp
     *         The connection interface ip.
     * @since 1.1.0
     */
    public void setConnectionIp(String connectionIp) {
        getMetrics().put(KapuaBirthPayloadAttibutes.CONNECTION_IP, connectionIp);
    }

    /**
     * Gets the accepted encoding.
     *
     * @return The accepted encoding.
     * @since 1.0.0
     */
    public String getAcceptEncoding() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.ACCEPT_ENCODING);
    }

    /**
     * Sets the accepted encoding.
     *
     * @param acceptEncoding
     *         The accepted encoding.
     * @since 1.1.0
     */
    public void setAcceptEncoding(String acceptEncoding) {
        getMetrics().put(KapuaBirthPayloadAttibutes.ACCEPT_ENCODING, acceptEncoding);
    }

    /**
     * Gets the application identifiers.
     *
     * @return The application identifiers.
     * @since 1.0.0
     */
    public String getApplicationIdentifiers() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.APPLICATION_IDENTIFIERS);
    }

    /**
     * Sets the application identifiers.
     *
     * @param applicationIdentifiers
     *         The application identifiers.
     * @since 1.1.0
     */
    public void setApplicationIdentifiers(String applicationIdentifiers) {
        getMetrics().put(KapuaBirthPayloadAttibutes.APPLICATION_IDENTIFIERS, applicationIdentifiers);
    }

    /**
     * Gets the available processors.
     *
     * @return The available processors.
     * @since 1.0.0
     */
    public String getAvailableProcessors() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.AVAILABLE_PROCESSORS);
    }

    /**
     * Sets the available processors.
     *
     * @param availableProcessors
     *         The available processors.
     * @since 1.1.0
     */
    public void setAvailableProcessors(String availableProcessors) {
        getMetrics().put(KapuaBirthPayloadAttibutes.AVAILABLE_PROCESSORS, availableProcessors);
    }

    /**
     * Gets the total memory.
     *
     * @return The total memory.
     * @since 1.0.0
     */
    public String getTotalMemory() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.TOTAL_MEMORY);
    }

    /**
     * Sets the total memory.
     *
     * @param totalMemory
     *         The total memory.
     * @since 1.1.0
     */
    public void setTotalMemory(String totalMemory) {
        getMetrics().put(KapuaBirthPayloadAttibutes.TOTAL_MEMORY, totalMemory);
    }

    /**
     * Gets the modem IMEI.
     *
     * @return The modem IMEI.
     * @since 1.0.0
     */
    public String getModemImei() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.MODEM_IMEI);
    }

    /**
     * Sets the modem IMEI.
     *
     * @param modemImei
     *         The modem IMEI.
     * @since 1.1.0
     */
    public void setModemImei(String modemImei) {
        getMetrics().put(KapuaBirthPayloadAttibutes.MODEM_IMEI, modemImei);
    }

    /**
     * Gets the modem IMSI.
     *
     * @return The modem IMSI.
     * @since 1.0.0
     */
    public String getModemImsi() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.MODEM_IMSI);
    }

    /**
     * Sets the modem IMSI.
     *
     * @param modemImsi
     *         The modem IMSI.
     * @since 1.1.0
     */
    public void setModemImsi(String modemImsi) {
        getMetrics().put(KapuaBirthPayloadAttibutes.MODEM_IMSI, modemImsi);
    }

    /**
     * Gets the modem ICCID.
     *
     * @return The modem ICCID.
     * @since 1.0.0
     */
    public String getModemIccid() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.MODEM_ICCID);
    }

    /**
     * Sets the modem ICCID.
     *
     * @param modemIccid
     *         The modem ICCID.
     * @since 1.1.0
     */
    public void setModemIccid(String modemIccid) {
        getMetrics().put(KapuaBirthPayloadAttibutes.MODEM_ICCID, modemIccid);
    }

    /**
     * Gets the extended properties.
     *
     * @return The extended properties.
     * @since 1.5.0
     */
    public String getExtendedProperties() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.EXTENDED_PROPERTIES);
    }

    /**
     * Sets the extended properties.
     *
     * @param extendedProperties
     *         The extended properties.
     * @since 1.5.0
     */
    public void setExtendedProperties(String extendedProperties) {
        getMetrics().put(KapuaBirthPayloadAttibutes.EXTENDED_PROPERTIES, extendedProperties);
    }

    /**
     * Gets the tamper status
     *
     * @return The tamper status.
     * @since 2.0.0
     */
    public String getTamperStatus() {
        return (String) getMetrics().get(KapuaBirthPayloadAttibutes.TAMPER_STATUS);
    }

    /**
     * Sets the tamper status.
     *
     * @param tamperStatus
     *         The tamper status.
     * @since 1.5.0
     */
    public void setTamperStatus(String tamperStatus) {
        getMetrics().put(KapuaBirthPayloadAttibutes.TAMPER_STATUS, tamperStatus);
    }

}
