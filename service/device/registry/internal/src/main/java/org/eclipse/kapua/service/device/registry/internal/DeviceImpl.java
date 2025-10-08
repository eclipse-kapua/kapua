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
package org.eclipse.kapua.service.device.registry.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.AbstractKapuaUpdatableEntity;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceExtendedProperty;
import org.eclipse.kapua.service.device.registry.DeviceStatus;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.internal.DeviceConnectionImpl;
import org.eclipse.kapua.service.device.registry.event.DeviceEvent;
import org.eclipse.kapua.service.device.registry.event.internal.DeviceEventImpl;
import org.eclipse.kapua.service.tag.Taggable;

/**
 * {@link Device} implementation.
 *
 * @since 1.0.0
 */
@Entity(name = "Device")
@Table(name = "dvc_device")
public class DeviceImpl extends AbstractKapuaUpdatableEntity implements Device, Taggable {

    private static final long serialVersionUID = 7688047426522474413L;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "eid", column = @Column(name = "group_id", nullable = true, updatable = true))
    })
    private KapuaEid groupId;

    @ElementCollection
    @CollectionTable(name = "dvc_device_group", joinColumns = @JoinColumn(name = "device_id", referencedColumnName = "id"))
    @AttributeOverrides({
            @AttributeOverride(name = "eid", column = @Column(name = "group_id", nullable = false, updatable = false))
    })
    private Set<KapuaEid> groupIds;

    @ElementCollection
    @CollectionTable(name = "dvc_device_tag", joinColumns = @JoinColumn(name = "device_id", referencedColumnName = "id"))
    @AttributeOverrides({
            @AttributeOverride(name = "eid", column = @Column(name = "tag_id", nullable = false, updatable = false))
    })
    private Set<KapuaEid> tagIds;

    @Basic
    @Column(name = "client_id", nullable = false, updatable = false)
    private String clientId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "eid", column = @Column(name = "connection_id", nullable = true, updatable = true))
    })
    private KapuaEid connectionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connection_id", referencedColumnName = "id", insertable = false, updatable = false)
    private DeviceConnectionImpl connection;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DeviceStatus status;

    @Basic
    @Column(name = "display_name")
    private String displayName;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "eid", column = @Column(name = "last_event_id", nullable = true, updatable = true))
    })
    private KapuaEid lastEventId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_event_id", referencedColumnName = "id", insertable = false, updatable = false)
    private DeviceEventImpl lastEvent;

    @Basic
    @Column(name = "serial_number")
    private String serialNumber;

    @Basic
    @Column(name = "model_id")
    private String modelId;

    @Basic
    @Column(name = "model_name")
    private String modelName;

    @Basic
    @Column(name = "imei")
    private String imei;

    @Basic
    @Column(name = "imsi")
    private String imsi;

    @Basic
    @Column(name = "iccid")
    private String iccid;

    @Basic
    @Column(name = "bios_version")
    private String biosVersion;

    @Basic
    @Column(name = "firmware_version")
    private String firmwareVersion;

    @Basic
    @Column(name = "os_version")
    private String osVersion;

    @Basic
    @Column(name = "jvm_version")
    private String jvmVersion;

    @Basic
    @Column(name = "osgi_framework_version")
    private String osgiFrameworkVersion;

    @Basic
    @Column(name = "app_framework_version")
    private String applicationFrameworkVersion;

    @Basic
    @Column(name = "connection_interface")
    private String connectionInterface;

    @Basic
    @Column(name = "connection_interface_clob")
    private String connectionInterfaceClob;

    @Basic
    @Column(name = "connection_ip")
    private String connectionIp;

    @Basic
    @Column(name = "connection_ip_clob")
    private String connectionIpClob;

    @Basic
    @Column(name = "app_identifiers")
    private String applicationIdentifiers;

    @Basic
    @Column(name = "accept_encoding")
    private String acceptEncoding;

    @Basic
    @Column(name = "custom_attribute_1")
    private String customAttribute1;

    @Basic
    @Column(name = "custom_attribute_2")
    private String customAttribute2;

    @Basic
    @Column(name = "custom_attribute_3")
    private String customAttribute3;

    @Basic
    @Column(name = "custom_attribute_4")
    private String customAttribute4;

    @Basic
    @Column(name = "custom_attribute_5")
    private String customAttribute5;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "dvc_device_extended_properties", joinColumns = @JoinColumn(name = "device_id", referencedColumnName = "id"))
    private List<DeviceExtendedPropertyImpl> extendedProperties;

    @Basic
    @Column(name = "tamper_status")
    private String tamperStatus;

    /**
     * Constructor
     *
     * @since 1.0.0
     */
    protected DeviceImpl() {
        super();
    }

    /**
     * Constructor.
     *
     * @param scopeId The scope {@link KapuaId} to set into the {@link Device}.
     * @since 1.0.0
     */
    public DeviceImpl(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * Clone constructor.
     *
     * @param device The {@link Device} to clone.
     * @throws KapuaException
     * @since 1.1.0
     */
    public DeviceImpl(Device device) throws KapuaException {
        super(device);

        setGroupId(device.getGroupId());
        setGroupIds(device.getGroupIds());
        setTagIds(device.getTagIds());
        setClientId(device.getClientId());
        setConnectionId(device.getConnectionId());
        setConnection(device.getConnection());
        setStatus(device.getStatus());
        setDisplayName(device.getDisplayName());
        setLastEventId(device.getLastEventId());
        setLastEvent(device.getLastEvent());
        setSerialNumber(device.getSerialNumber());
        setModelId(device.getModelId());
        setModelName(device.getModelName());
        setImei(device.getImei());
        setImsi(device.getImsi());
        setIccid(device.getIccid());
        setBiosVersion(device.getBiosVersion());
        setFirmwareVersion(device.getFirmwareVersion());
        setOsVersion(device.getOsVersion());
        setJvmVersion(device.getJvmVersion());
        setOsgiFrameworkVersion(device.getOsgiFrameworkVersion());
        setApplicationFrameworkVersion(device.getApplicationFrameworkVersion());
        setConnectionInterface(device.getConnectionInterface());
        setConnectionIp(device.getConnectionIp());
        setApplicationIdentifiers(device.getApplicationIdentifiers());
        setAcceptEncoding(device.getAcceptEncoding());
        setCustomAttribute1(device.getCustomAttribute1());
        setCustomAttribute2(device.getCustomAttribute2());
        setCustomAttribute3(device.getCustomAttribute3());
        setCustomAttribute4(device.getCustomAttribute4());
        setCustomAttribute5(device.getCustomAttribute5());
        setExtendedProperties(device.getExtendedProperties());
    }

    @Override
    public KapuaId getGroupId() {
        return groupId;
    }

    @Override
    public void setGroupId(KapuaId groupId) {
        this.groupId = KapuaEid.parseKapuaId(groupId);
    }

    @Override
    public void setGroupIds(Set<KapuaId> groupIds) {
        this.groupIds = new HashSet<>();

        for (KapuaId groupId : groupIds) {
            this.groupIds.add(KapuaEid.parseKapuaId(groupId));
        }
    }

    @Override
    public Set<KapuaId> getGroupIds() {
        Set<KapuaId> groupIds = new HashSet<>();

        if (this.groupIds != null) {
            for (KapuaId groupId : this.groupIds) {
                groupIds.add(KapuaEid.parseKapuaId(groupId));
            }
        }

        return groupIds;
    }

    @Override
    public void setTagIds(Set<KapuaId> tagIds) {
        this.tagIds = new HashSet<>();

        for (KapuaId id : tagIds) {
            this.tagIds.add(KapuaEid.parseKapuaId(id));
        }
    }

    @Override
    public Set<KapuaId> getTagIds() {
        Set<KapuaId> tagIds = new HashSet<>();

        if (this.tagIds != null) {
            for (KapuaId deviceTagId : this.tagIds) {
                tagIds.add(new KapuaEid(deviceTagId));
            }
        }

        return tagIds;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public KapuaId getConnectionId() {
        return connectionId;
    }

    @Override
    public void setConnectionId(KapuaId connectionId) {
        this.connectionId = KapuaEid.parseKapuaId(connectionId);
    }

    @Override
    public DeviceConnection getConnection() {
        return connection;
    }

    public void setConnection(DeviceConnection connection) throws KapuaException {
        this.connection = connection != null ? new DeviceConnectionImpl(connection) : null;
    }

    @Override
    public DeviceStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(DeviceStatus status) {
        this.status = status;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public KapuaId getLastEventId() {
        return lastEventId;
    }

    @Override
    public void setLastEventId(KapuaId lastEventId) {
        this.lastEventId = KapuaEid.parseKapuaId(lastEventId);
    }

    @Override
    public DeviceEvent getLastEvent() {
        return lastEvent;
    }

    public void setLastEvent(DeviceEvent lastEvent) throws KapuaException {
        this.lastEvent = lastEvent != null ? new DeviceEventImpl(lastEvent) : null;
    }

    @Override
    public String getSerialNumber() {
        return serialNumber;
    }

    @Override
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Override
    public String getModelId() {
        return modelId;
    }

    @Override
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    @Override
    public String getModelName() {
        return modelName;
    }

    @Override
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public String getImei() {
        return imei;
    }

    @Override
    public void setImei(String imei) {
        this.imei = imei;
    }

    @Override
    public String getImsi() {
        return imsi;
    }

    @Override
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    @Override
    public String getIccid() {
        return iccid;
    }

    @Override
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    @Override
    public String getBiosVersion() {
        return biosVersion;
    }

    @Override
    public void setBiosVersion(String biosVersion) {
        this.biosVersion = biosVersion;
    }

    @Override
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    @Override
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    @Override
    public String getOsVersion() {
        return osVersion;
    }

    @Override
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    @Override
    public String getJvmVersion() {
        return jvmVersion;
    }

    @Override
    public void setJvmVersion(String jvmVersion) {
        this.jvmVersion = jvmVersion;
    }

    @Override
    public String getOsgiFrameworkVersion() {
        return osgiFrameworkVersion;
    }

    @Override
    public void setOsgiFrameworkVersion(String osgiFrameworkVersion) {
        this.osgiFrameworkVersion = osgiFrameworkVersion;
    }

    @Override
    public String getApplicationFrameworkVersion() {
        return applicationFrameworkVersion;
    }

    @Override
    public void setApplicationFrameworkVersion(String applicationFrameworkVersion) {
        this.applicationFrameworkVersion = applicationFrameworkVersion;
    }

    @Override
    public String getConnectionInterface() {
        return getConnectionInterfaceClob() == null ? connectionInterface : getConnectionInterfaceClob();
    }

    @Override
    public void setConnectionInterface(String connectionInterface) {
        if (connectionInterface != null && connectionInterface.length() > 255) {
            setConnectionInterfaceClob(connectionInterface);
            this.connectionInterface = connectionInterface.substring(0, 255);
        } else {
            this.connectionInterface = connectionInterface;
        }
    }

    /**
     * Gets the {@link #getConnectionInterface()} for big values.
     * <p>
     * When setting a value which is longer than 255 chars,
     * the value is stored in {@link #connectionInterfaceClob} while in {@link #connectionInterface}
     * is trimmed at 255 chars for indexing performances.
     *
     * @return The full value if greater than 255, or {@code null}
     * @since 2.0.0
     */
    private String getConnectionInterfaceClob() {
        return connectionInterfaceClob;
    }

    /**
     * Sets the {@link #getConnectionInterface()} for big values.
     *
     * @param connectionInterfaceClob The value greater than 255 chars.
     * @since 2.0.0
     */
    private void setConnectionInterfaceClob(String connectionInterfaceClob) {
        this.connectionInterfaceClob = connectionInterfaceClob;
    }

    @Override
    public String getConnectionIp() {
        return getConnectionIpClob() == null ? connectionIp : getConnectionIpClob();
    }

    @Override
    public void setConnectionIp(String connectionIp) {
        if (connectionIp != null && connectionIp.length() > 64) {
            setConnectionIpClob(connectionIp);
            this.connectionIp = connectionIp.substring(0, 64);
        } else {
            this.connectionIp = connectionIp;
        }
    }

    /**
     * Gets the {@link #getConnectionIp()} for big values.
     * <p>
     * When setting a value which is longer than 64 chars,
     * the value is stored in {@link #connectionIpClob} while in {@link #connectionIp}
     * is trimmed at 64 chars for indexing performances.
     *
     * @return The full value if greater than 64, or {@code null}
     * @since 2.0.0
     */
    private String getConnectionIpClob() {
        return connectionIpClob;
    }

    /**
     * Sets the {@link #getConnectionIp()} for big values.
     *
     * @param connectionIpClob The value greater than 64 chars.
     * @since 2.0.0
     */
    private void setConnectionIpClob(String connectionIpClob) {
        this.connectionIpClob = connectionIpClob;
    }

    @Override
    public String getApplicationIdentifiers() {
        return applicationIdentifiers;
    }

    @Override
    public void setApplicationIdentifiers(String applicationIdentifiers) {
        this.applicationIdentifiers = applicationIdentifiers;
    }

    @Override
    public String getAcceptEncoding() {
        return acceptEncoding;
    }

    @Override
    public void setAcceptEncoding(String acceptEncoding) {
        this.acceptEncoding = acceptEncoding;
    }

    @Override
    public String getCustomAttribute1() {
        return customAttribute1;
    }

    @Override
    public void setCustomAttribute1(String customAttribute1) {
        this.customAttribute1 = customAttribute1;
    }

    @Override
    public String getCustomAttribute2() {
        return customAttribute2;
    }

    @Override
    public void setCustomAttribute2(String customAttribute2) {
        this.customAttribute2 = customAttribute2;
    }

    @Override
    public String getCustomAttribute3() {
        return customAttribute3;
    }

    @Override
    public void setCustomAttribute3(String customAttribute3) {
        this.customAttribute3 = customAttribute3;
    }

    @Override
    public String getCustomAttribute4() {
        return customAttribute4;
    }

    @Override
    public void setCustomAttribute4(String customAttribute4) {
        this.customAttribute4 = customAttribute4;
    }

    @Override
    public String getCustomAttribute5() {
        return customAttribute5;
    }

    @Override
    public void setCustomAttribute5(String customAttribute5) {
        this.customAttribute5 = customAttribute5;
    }

    @Override
    public List<DeviceExtendedProperty> getExtendedProperties() {
        if (extendedProperties == null) {
            extendedProperties = new ArrayList<>();
        }

        return new ArrayList<>(extendedProperties);
    }

    @Override
    public void addExtendedProperty(DeviceExtendedProperty extendedProperty) {
        if (extendedProperties == null) {
            extendedProperties = new ArrayList<>();
        }

        extendedProperties.add(DeviceExtendedPropertyImpl.parse(extendedProperty));
    }

    @Override
    public void setExtendedProperties(List<DeviceExtendedProperty> extendedProperties) {
        this.extendedProperties = new ArrayList<>();

        if (extendedProperties != null) {
            extendedProperties.forEach(dep -> this.extendedProperties.add(DeviceExtendedPropertyImpl.parse(dep)));
        }
    }

    @Override
    public String getTamperStatus() {
        return tamperStatus;
    }

    @Override
    public void setTamperStatus(String tamperStatus) {
        this.tamperStatus = tamperStatus;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
