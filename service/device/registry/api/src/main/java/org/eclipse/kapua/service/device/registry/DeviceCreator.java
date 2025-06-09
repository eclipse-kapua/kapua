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
package org.eclipse.kapua.service.device.registry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.KapuaUpdatableEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.event.DeviceEvent;

/**
 * {@link DeviceCreator} encapsulates all the information needed to create a new {@link Device} in the system.<br> The data provided will be used to seed the new {@link Device} and its related
 * information.<br> The fields of the {@link DeviceCreator} presents the attributes that are searchable for a given device.<br> The DeviceCreator Properties field can be used to provide additional
 * properties associated to the Device; those properties will not be searchable through Device queries.<br> The clientId field of the Device is used to store the MAC address of the primary network
 * interface of the device.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "deviceCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {
        "groupId",
        "clientId",
        "status",
        "connectionId",
        "lastEventId",
        "displayName",
        "serialNumber",
        "modelId",
        "modelName",
        "imei",
        "imsi",
        "iccid",
        "biosVersion",
        "firmwareVersion",
        "osVersion",
        "jvmVersion",
        "osgiFrameworkVersion",
        "applicationFrameworkVersion",
        "connectionInterface",
        "connectionIp",
        "applicationIdentifiers",
        "acceptEncoding",
        "customAttribute1",
        "customAttribute2",
        "customAttribute3",
        "customAttribute4",
        "customAttribute5",
        "extendedProperties",
        "tagIds",
        "tamperStatus"
})
public class DeviceCreator extends KapuaUpdatableEntityCreator {

    private static final long serialVersionUID = 8497299443773395462L;
    private KapuaId groupId;
    private String clientId;
    private DeviceStatus status = DeviceStatus.ENABLED;
    private KapuaId connectionId;
    private KapuaId lastEventId;
    private String displayName;
    private String serialNumber;
    private String modelId;
    private String modelName;
    private String imei;
    private String imsi;
    private String iccid;
    private String biosVersion;
    private String firmwareVersion;
    private String osVersion;
    private String jvmVersion;
    private String osgiFrameworkVersion;
    private String applicationFrameworkVersion;
    private String connectionInterface;
    private String connectionIp;
    private String applicationIdentifiers;
    private String acceptEncoding;
    private String customAttribute1;
    private String customAttribute2;
    private String customAttribute3;
    private String customAttribute4;
    private String customAttribute5;
    private List<DeviceExtendedProperty> extendedProperties;
    private String tamperStatus;
    private Set<KapuaId> tagIds;

    public DeviceCreator() {
    }

    public DeviceCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public DeviceCreator(KapuaEntityCreator entityCreator) {
        super(entityCreator);
    }

    public DeviceCreator(KapuaId scopeId, String clientId) {
        super(scopeId);
        this.clientId = clientId;
    }

    /**
     * Gets the {@link Group#getId()}.
     *
     * @return The {@link Group#getId()}.
     * @since 1.0.0
     */
    @XmlElement(name = "groupId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getGroupId() {
        return groupId;
    }

    /**
     * Sets the {@link Group#getId()}.
     *
     * @param groupId
     *         The {@link Group#getId()}.
     * @since 1.0.0
     */
    public void setGroupId(KapuaId groupId) {
        this.groupId = groupId;
    }

    /**
     * Gets the client identifier.
     *
     * @return The client identifier.
     * @since 1.0.0
     */
    @XmlElement(name = "clientId")
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets the client identifier.
     *
     * @param clientId
     *         The client identifier.
     * @since 1.0.0
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Gets the {@link DeviceConnection#getId()}.
     *
     * @return The {@link DeviceConnection#getId()}.
     * @since 1.0.0
     */
    @XmlElement(name = "connectionId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getConnectionId() {
        return connectionId;
    }

    /**
     * Sets the {@link DeviceConnection#getId()}.
     *
     * @param connectionId
     *         The {@link DeviceConnection#getId()}.
     * @since 1.0.0
     */
    public void setConnectionId(KapuaId connectionId) {
        this.connectionId = connectionId;
    }

    /**
     * Gets the {@link DeviceStatus}.
     *
     * @return The {@link DeviceStatus}.
     * @since 1.0.0
     */
    @XmlElement(name = "status")
    public DeviceStatus getStatus() {
        return status;
    }

    /**
     * Sets the {@link DeviceStatus}.
     *
     * @param status
     *         The {@link DeviceStatus}.
     * @since 1.0.0
     */
    public void setStatus(DeviceStatus status) {
        this.status = status;
    }

    /**
     * Gets the display name.
     *
     * @return The display name.
     * @since 1.0.0
     */
    @XmlElement(name = "displayName")
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name.
     *
     * @param displayName
     *         The display name.
     * @since 1.0.0
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the last {@link DeviceEvent#getId()}.
     *
     * @return The last {@link DeviceEvent#getId()}.
     * @since 1.0.0
     */
    @XmlElement(name = "lastEventId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getLastEventId() {
        return lastEventId;
    }

    /**
     * Sets the last {@link DeviceEvent#getId()}.
     *
     * @param lastEventId
     *         The last {@link DeviceEvent#getId()}.
     * @since 1.0.0
     */
    public void setLastEventId(KapuaId lastEventId) {
        this.lastEventId = lastEventId;
    }

    /**
     * Gets the serial number.
     *
     * @return The serial number.
     * @since 1.0.0
     */
    @XmlElement(name = "serialNumber")
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number.
     *
     * @param serialNumber
     *         The serial number.
     * @since 1.0.0
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Gets the model identifier.
     *
     * @return The model identifier.
     * @since 1.0.0
     */
    @XmlElement(name = "modelId")
    public String getModelId() {
        return modelId;
    }

    /**
     * Sets the model identifier.
     *
     * @param modelId
     *         The model identifier.
     * @since 1.0.0
     */
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    /**
     * Gets the model name.
     *
     * @return The model name.
     * @since 1.0.0
     */
    @XmlElement(name = "modelName")
    public String getModelName() {
        return modelName;
    }

    /**
     * Sets the model name.
     *
     * @param modelName
     *         The model name.
     * @since 1.0.0
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * Gets the modem imei.
     *
     * @return The modem imei.
     * @since 1.0.0
     */
    @XmlElement(name = "imei")
    public String getImei() {
        return imei;
    }

    /**
     * Sets the modem imei.
     *
     * @param imei
     *         The modem imei.
     * @since 1.0.0
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Gets the modem imsi.
     *
     * @return The modem imsi.
     * @since 1.0.0
     */
    @XmlElement(name = "imsi")
    public String getImsi() {
        return this.imsi;
    }

    /**
     * Sets the modem imsi.
     *
     * @param imsi
     *         The modem imsi.
     * @since 1.0.0
     */
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    /**
     * Gets the modem iccid.
     *
     * @return The modem iccid.
     * @since 1.0.0
     */
    @XmlElement(name = "iccid")
    public String getIccid() {
        return iccid;
    }

    /**
     * Sets the modem iccid.
     *
     * @param iccid
     *         The modem iccid.
     * @since 1.0.0
     */
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    /**
     * Gets the bios version.
     *
     * @return The bios version.
     * @since 1.0.0
     */
    @XmlElement(name = "biosVersion")
    public String getBiosVersion() {
        return biosVersion;
    }

    /**
     * Sets the bios version.
     *
     * @param biosVersion
     *         The bios version.
     * @since 1.0.0
     */
    public void setBiosVersion(String biosVersion) {
        this.biosVersion = biosVersion;
    }

    /**
     * Gets the firmware version.
     *
     * @return The firmware version.
     * @since 1.0.0
     */
    @XmlElement(name = "firmwareVersion")
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    /**
     * Sets the firmware version.
     *
     * @param firmwareVersion
     *         The firmware version.
     * @since 1.0.0
     */
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    /**
     * Gets the OS version.
     *
     * @return The OS version.
     * @since 1.0.0
     */
    @XmlElement(name = "osVersion")
    public String getOsVersion() {
        return osVersion;
    }

    /**
     * Sets the OS version.
     *
     * @param osVersion
     *         The OS version.
     * @since 1.0.0
     */
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    /**
     * Gets the JVM version.
     *
     * @return The JVM version.
     * @since 1.0.0
     */
    @XmlElement(name = "jvmVersion")
    public String getJvmVersion() {
        return jvmVersion;
    }

    /**
     * Sets the JVM version.
     *
     * @param jvmVersion
     *         The JVM version.
     * @since 1.0.0
     */
    public void setJvmVersion(String jvmVersion) {
        this.jvmVersion = jvmVersion;
    }

    /**
     * Gets the OSGi framework version.
     *
     * @return The OSGi framework version.
     * @since 1.0.0
     */
    @XmlElement(name = "osgiFrameworkVersion")
    public String getOsgiFrameworkVersion() {
        return osgiFrameworkVersion;
    }

    /**
     * Sets the OSGi framework version.
     *
     * @param osgiFrameworkVersion
     *         The OSGi framework version.
     * @since 1.0.0
     */
    public void setOsgiFrameworkVersion(String osgiFrameworkVersion) {
        this.osgiFrameworkVersion = osgiFrameworkVersion;
    }

    /**
     * Gets the application framework version.
     *
     * @return The application framework version.
     * @since 1.0.0
     */
    @XmlElement(name = "applicationFrameworkVersion")
    public String getApplicationFrameworkVersion() {
        return applicationFrameworkVersion;
    }

    /**
     * Sets the application framework version.
     *
     * @param appFrameworkVersion
     *         The application framework version.
     * @since 1.0.0
     */
    public void setApplicationFrameworkVersion(String appFrameworkVersion) {
        this.applicationFrameworkVersion = appFrameworkVersion;
    }

    /**
     * Gets the device network interfaces name.
     *
     * @return The device network interfaces name.
     * @since 1.0.0
     */
    @XmlElement(name = "connectionInterface")
    public String getConnectionInterface() {
        return connectionInterface;
    }

    /**
     * Sets the device network interfaces name.
     *
     * @param connectionInterface
     *         The device network interfaces name.
     * @since 1.0.0
     */
    public void setConnectionInterface(String connectionInterface) {
        this.connectionInterface = connectionInterface;
    }

    /**
     * Gets the device network interfaces IP.
     *
     * @return The device network interfaces IP.
     * @since 1.0.0
     */
    @XmlElement(name = "connectionIp")
    public String getConnectionIp() {
        return connectionIp;
    }

    /**
     * Sets the device network interfaces IP.
     *
     * @param connectionIp
     *         The device network interfaces IP.
     * @since 1.0.0
     */
    public void setConnectionIp(String connectionIp) {
        this.connectionIp = connectionIp;
    }

    /**
     * Gets the application identifiers supported.
     *
     * @return The application identifiers supported.
     * @since 1.0.0
     */
    @XmlElement(name = "applicationIdentifiers")
    public String getApplicationIdentifiers() {
        return applicationIdentifiers;
    }

    /**
     * Sets the application identifiers supported.
     *
     * @param applicationIdentifiers
     *         The application identifiers supported.
     * @since 1.0.0
     */
    public void setApplicationIdentifiers(String applicationIdentifiers) {
        this.applicationIdentifiers = applicationIdentifiers;
    }

    /**
     * Gets the accept encodings.
     *
     * @return The accept encodings.
     * @since 1.0.0
     */
    @XmlElement(name = "acceptEncoding")
    public String getAcceptEncoding() {
        return acceptEncoding;
    }

    /**
     * Sets the accept encodings.
     *
     * @param acceptEncoding
     *         The accept encodings.
     * @since 1.0.0
     */
    public void setAcceptEncoding(String acceptEncoding) {
        this.acceptEncoding = acceptEncoding;
    }

    /**
     * Gets the custom attribute 1.
     *
     * @return The custom attribute 1.
     * @since 1.0.0
     */
    @XmlElement(name = "customAttribute1")
    public String getCustomAttribute1() {
        return customAttribute1;
    }

    /**
     * Sets the custom attribute 1.
     *
     * @param customAttribute1
     *         The custom attribute 1.
     * @since 1.0.0
     */
    public void setCustomAttribute1(String customAttribute1) {
        this.customAttribute1 = customAttribute1;
    }

    /**
     * Gets the custom attribute 2.
     *
     * @return The custom attribute 2.
     * @since 1.0.0
     */
    @XmlElement(name = "customAttribute2")
    public String getCustomAttribute2() {
        return customAttribute2;
    }

    /**
     * Sets the custom attribute 2.
     *
     * @param customAttribute2
     *         The custom attribute 2.
     * @since 1.0.0
     */
    public void setCustomAttribute2(String customAttribute2) {
        this.customAttribute2 = customAttribute2;
    }

    /**
     * Gets the custom attribute 3.
     *
     * @return The custom attribute 3.
     * @since 1.0.0
     */
    @XmlElement(name = "customAttribute3")
    public String getCustomAttribute3() {
        return customAttribute3;
    }

    /**
     * Sets the custom attribute 3.
     *
     * @param customAttribute3
     *         The custom attribute 3.
     * @since 1.0.0
     */
    public void setCustomAttribute3(String customAttribute3) {
        this.customAttribute3 = customAttribute3;
    }

    /**
     * Gets the custom attribute 4.
     *
     * @return The custom attribute 4.
     * @since 1.0.0
     */
    @XmlElement(name = "customAttribute4")
    public String getCustomAttribute4() {
        return customAttribute4;
    }

    /**
     * Sets the custom attribute 4.
     *
     * @param customAttribute4
     *         The custom attribute 4.
     * @since 1.0.0
     */
    public void setCustomAttribute4(String customAttribute4) {
        this.customAttribute4 = customAttribute4;
    }

    /**
     * Gets the custom attribute 5.
     *
     * @return The custom attribute 5.
     * @since 1.0.0
     */
    @XmlElement(name = "customAttribute5")
    public String getCustomAttribute5() {
        return customAttribute5;
    }

    /**
     * Sets the custom attribute 5.
     *
     * @param customAttribute5
     *         The custom attribute 5.
     * @since 1.0.0
     */
    public void setCustomAttribute5(String customAttribute5) {
        this.customAttribute5 = customAttribute5;
    }

    /**
     * Gets the {@link DeviceExtendedProperty} {@link List}.
     *
     * @return The {@link DeviceExtendedProperty} {@link List}.
     * @since 1.5.0
     */
    @XmlElement(name = "extendedProperties")
    public List<DeviceExtendedProperty> getExtendedProperties() {
        if (extendedProperties == null) {
            extendedProperties = new ArrayList<>();
        }

        return extendedProperties;
    }

    /**
     * Add a {@link DeviceExtendedProperty} to the {@link List}.
     *
     * @param extendedProperty
     *         The {@link DeviceExtendedProperty} to add.
     * @since 1.5.0
     */
    public void addExtendedProperty(@NotNull DeviceExtendedProperty extendedProperty) {
        getExtendedProperties().add(extendedProperty);
    }

    /**
     * Sets the {@link DeviceExtendedProperty} {@link List}.
     *
     * @param extendedProperties
     *         The {@link DeviceExtendedProperty} {@link List}.
     * @since 1.5.0
     */
    public void setExtendedProperties(List<DeviceExtendedProperty> extendedProperties) {
        this.extendedProperties = extendedProperties;
    }

    /**
     * Gets the tamper status.
     *
     * @return The application tamper status.
     * @since 2.0.0
     */
    @XmlElement(name = "tamperStatus")
    public String getTamperStatus() {
        return tamperStatus;
    }

    /**
     * Sets the tamper status.
     *
     * @param tamperStatus
     *         The tamper status.
     * @since 2.0.0
     */
    public void setTamperStatus(String tamperStatus) {
        this.tamperStatus = tamperStatus;
    }

    /**
     * Gets the list of tags associated with the device.
     *
     * @return The list of tags.
     * @since 2.0.0
     */
    @XmlElement(name = "tagIds")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public Set<KapuaId> getTagIds() {
        return tagIds == null ? new HashSet<>() : tagIds;
    }

    /**
     * Sets the list of tags associated with the device.
     *
     * @param tagIds
     *         The list of tags.
     * @since 2.0.0
     */
    public void setTagIds(Set<KapuaId> tagIds) {
        this.tagIds = tagIds;
    }

}
