/*******************************************************************************
 * Copyright (c) 2026, 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.extras.migrator.device.group;

import org.eclipse.kapua.commons.model.AbstractKapuaUpdatableEntity;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceExtendedProperty;
import org.eclipse.kapua.service.device.registry.DeviceStatus;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.event.DeviceEvent;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * {@link Device} implementation.
 *
 * @since 2.1.0
 */
@Entity(name = "Device")
@Table(name = "dvc_device")
public class DeviceMigratorImpl extends AbstractKapuaUpdatableEntity implements Device {

    @Basic
    @Column(name = "client_id", nullable = false, updatable = false)
    private String clientId;

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
    /**
     * Constructor.
     *
     * @since 2.0.0
     */
    public DeviceMigratorImpl() {
    }

    @Override
    public KapuaEid getGroupId() {
        return groupId;
    }

    @Override
    public void setGroupId(KapuaId groupId) {
        this.groupId = KapuaEid.parseKapuaId(groupId);
    }

    @Override
    public Set<KapuaId> getGroupIds() {
        return new HashSet<>(groupIds);
    }

    public void setGroupIds(Set<KapuaId> groupIds) {
        this.groupIds = new HashSet<>();

        for (KapuaId gId : groupIds) {
            this.groupIds.add(KapuaEid.parseKapuaId(gId));
        }
    }

    // Attributes below do not require migration
    @Override
    public Set<KapuaId> getTagIds() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTagIds(Set<KapuaId> tagIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public void setClientId(String clientId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public KapuaId getConnectionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setConnectionId(KapuaId connectionId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeviceConnection getConnection() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeviceStatus getStatus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStatus(DeviceStatus status) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDisplayName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDisplayName(String diplayName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public KapuaId getLastEventId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLastEventId(KapuaId lastEventId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeviceEvent getLastEvent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSerialNumber() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSerialNumber(String serialNumber) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getModelId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setModelId(String modelId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getModelName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setModelName(String modelName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getImei() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setImei(String imei) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getImsi() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setImsi(String imsi) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getIccid() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setIccid(String iccid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getBiosVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBiosVersion(String biosVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFirmwareVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFirmwareVersion(String firmwareVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getOsVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOsVersion(String osVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getJvmVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setJvmVersion(String jvmVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getOsgiFrameworkVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOsgiFrameworkVersion(String osgiFrameworkVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getApplicationFrameworkVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setApplicationFrameworkVersion(String appFrameworkVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getConnectionInterface() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setConnectionInterface(String connectionInterface) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getConnectionIp() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setConnectionIp(String connectionIp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getApplicationIdentifiers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setApplicationIdentifiers(String applicationIdentifiers) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAcceptEncoding() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAcceptEncoding(String acceptEncoding) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCustomAttribute1() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCustomAttribute1(String customAttribute1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCustomAttribute2() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCustomAttribute2(String customAttribute2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCustomAttribute3() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCustomAttribute3(String customAttribute3) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCustomAttribute4() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCustomAttribute4(String customAttribute4) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCustomAttribute5() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCustomAttribute5(String customAttribute5) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<DeviceExtendedProperty> getExtendedProperties() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addExtendedProperty(DeviceExtendedProperty extendedProperty) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setExtendedProperties(List<DeviceExtendedProperty> extendedProperties) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTamperStatus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTamperStatus(String tamperStatus) {
        throw new UnsupportedOperationException();
    }
}
