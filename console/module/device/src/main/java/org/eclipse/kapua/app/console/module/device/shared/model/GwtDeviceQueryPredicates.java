/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.device.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;
import org.eclipse.kapua.app.console.module.api.shared.model.KapuaBaseModel;

import java.io.Serializable;
import java.util.List;

public class GwtDeviceQueryPredicates extends KapuaBaseModel implements Serializable {

    private static final long serialVersionUID = 2649696808210585054L;

    private static final String CLIENT_ID = "clientId";
    private static final String DISPLAY_NAME = "displayName";
    private static final String SERIAL_NUMBER = "serialNumber";
    private static final String IOT_FRAMEWORK_VERSION = "iotFrameworkVersion";
    private static final String APPLICATION_IDENTIFIERS = "applicationIdentifiers";
    private static final String CUSTOM_ATTRIBUTE_1 = "customAttribute1";
    private static final String CUSTOM_ATTRIBUTE_2 = "customAttribute2";

    public enum GwtDeviceStatus implements IsSerializable {
        ENABLED, DISABLED, ANY;

        GwtDeviceStatus() {
        }
    }

    public enum GwtDeviceCertificateStatus implements IsSerializable {
        ANY, NOT_INSTALLED, INSTALLED_OUT_OF_DATE, INSTALLED_UP_TO_DATE;

        GwtDeviceCertificateStatus() {
        }
    }

    public enum GwtDeviceConnectionStatus implements IsSerializable {
        CONNECTED, DISCONNECTED, MISSING, ANY, UNKNOWN;

        GwtDeviceConnectionStatus() {
        }
    }

    public enum GwtSortOrder implements IsSerializable {
        ASCENDING, DESCENDING;

        GwtSortOrder() {
        }
    }

    public enum GwtSortAttribute implements IsSerializable {
        CLIENT_ID, DISPLAY_NAME, LAST_EVENT_ON;

        GwtSortAttribute() {
        }
    }

    public enum GwtGroupDevice implements IsSerializable {
        NO_GROUP, ANY;

        GwtGroupDevice() {
        }
    }

    public GwtDeviceQueryPredicates() {
        setSortAttribute(GwtSortAttribute.CLIENT_ID.name());
        setSortOrder(GwtSortOrder.ASCENDING.name());
    }

    @Override
    public <X> X get(String property) {
        if ("deviceStatusEnum".equals(property)) {
            return (X) (GwtDeviceStatus.valueOf(getDeviceStatus()));
        } else if ("deviceConnectionStatusEnum".equals(property)) {
            return (X) (GwtDeviceConnectionStatus.valueOf(getDeviceConnectionStatus()));
        } else if ("deviceCertificateStatusEnum".equals(property)) {
            return (X) (GwtDeviceCertificateStatus.valueOf(getDeviceCertificateStatus()));
        } else if ("sortOrderEnum".equals(property)) {
            return (X) (GwtSortOrder.valueOf(getSortOrder()));
        } else if ("sortAttributeEnum".equals(property)) {
            return (X) (GwtSortAttribute.valueOf(getSortAttribute()));
        } else if ("groupDeviceEnum".equals(property)) {
            return (X) (GwtGroupDevice.valueOf(getGroupDevice()));
        } else {
            return super.get(property);
        }
    }

    public String getClientId() {
        return (String) get(CLIENT_ID);
    }

    public String getUnescapedClientId() {
        return (String) getUnescaped(CLIENT_ID);
    }

    public void setClientId(String clientId) {
        set(CLIENT_ID, clientId);
    }

    public String getDisplayName() {
        return (String) get(DISPLAY_NAME);
    }

    public String getUnescapedDisplayName() {
        return (String) getUnescaped(DISPLAY_NAME);
    }

    public void setDisplayName(String displayName) {
        set(DISPLAY_NAME, displayName);
    }

    public String getSerialNumber() {
        return (String) get(SERIAL_NUMBER);
    }

    public String getUnescapedSerialNumber() {
        return (String) getUnescaped(SERIAL_NUMBER);
    }

    public void setSerialNumber(String serialNumber) {
        set(SERIAL_NUMBER, serialNumber);
    }

    public String getDeviceStatus() {
        return (String) get("deviceStatus");
    }

    public GwtDeviceStatus getDeviceStatusEnum() {
        return get("deviceStatusEnum");
    }

    public void setDeviceStatus(String deviceStatus) {
        set("deviceStatus", deviceStatus);
    }

    public String getDeviceConnectionStatus() {
        return (String) get("deviceConnectionStatus");
    }

    public GwtDeviceConnectionStatus getDeviceConnectionStatusEnum() {
        return get("deviceConnectionStatusEnum");
    }

    public void setDeviceConnectionStatus(String deviceConnectionStatus) {
        set("deviceConnectionStatus", deviceConnectionStatus);
    }

    public String getIotFrameworkVersion() {
        return (String) get(IOT_FRAMEWORK_VERSION);
    }

    public String getUnescapedIotFrameworkVersion() {
        return (String) getUnescaped(IOT_FRAMEWORK_VERSION);
    }

    public void setIotFrameworkVersion(String iotFrameworkVersion) {
        set(IOT_FRAMEWORK_VERSION, iotFrameworkVersion);
    }

    public String getApplicationIdentifiers() {
        return (String) get(APPLICATION_IDENTIFIERS);
    }

    public String getUnescapedApplicationIdentifiers() {
        return (String) getUnescaped(APPLICATION_IDENTIFIERS);
    }

    public void setApplicationIdentifiers(String applicationIdentifiers) {
        set(APPLICATION_IDENTIFIERS, applicationIdentifiers);
    }

    public String getImei() {
        return (String) get("imei");
    }

    public void setImei(String imei) {
        set("imei", imei);
    }

    public String getImsi() {
        return (String) get("imsi");
    }

    public void setImsi(String imsi) {
        set("imsi", imsi);
    }

    public String getIccid() {
        return (String) get("iccid");
    }

    public void setIccid(String iccid) {
        set("iccid", iccid);
    }

    public String getCustomAttribute1() {
        return (String) get(CUSTOM_ATTRIBUTE_1);
    }

    public String getUnescapedCustomAttribute1() {
        return (String) getUnescaped(CUSTOM_ATTRIBUTE_1);
    }

    public void setCustomAttribute1(String customAttribute1) {
        set(CUSTOM_ATTRIBUTE_1, customAttribute1);
    }

    public String getCustomAttribute2() {
        return (String) get(CUSTOM_ATTRIBUTE_2);
    }

    public String getUnescapedCustomAttribute2() {
        return (String) getUnescaped(CUSTOM_ATTRIBUTE_2);
    }

    public void setCustomAttribute2(String customAttribute2) {
        set(CUSTOM_ATTRIBUTE_2, customAttribute2);
    }

    public String getDeviceCertificateStatus() {
        return get("deviceCertificateStatus");
    }

    public GwtDeviceCertificateStatus getDeviceCertificateStatusEnum() {
        return get("deviceCertificateStatusEnum");
    }

    public void setDeviceCertificateStatus(String deviceCertificateStatus) {
        set("deviceCertificateStatus", deviceCertificateStatus);
    }

    public String getGroupDevice() {
        return get("groupDevice");
    }

    public GwtGroupDevice getGroupDeviceEnum() {
        return get("groupDeviceEnum");
    }

    public void setGroupDevice(String groupDevice) {
        set("groupDevice", groupDevice);
    }

    public String getSortOrder() {
        return (String) get("sortOrder");
    }

    public GwtSortOrder getSortOrderEnum() {
        return get("sortOrderEnum");
    }

    public void setSortOrder(String sortOrder) {
        set("sortOrder", sortOrder);
    }

    public String getSortAttribute() {
        return (String) get("sortAttribute");
    }

    public GwtSortAttribute getSortAttributeEnum() {
        return get("sortAttributeEnum");
    }

    public void setSortAttribute(String sortAttribute) {
        set("sortAttribute", sortAttribute);
    }

    public Long getDefaultPki() {
        return get("defaultPki");
    }

    public void setDefaultPki(Long value) {
        set("defaultPki", value);
    }

    public List<String> getGroupIds() {
        return get("groupIds");
    }

    public void setGroupIds(List<String> groupIds) {
        set("groupIds", groupIds);
    }



    public List<String> getTagIds() {
        return (List<String>) get("tagIds");
    }

    public void setTagIds(List<String> tagIds) {
        set("tagIds", tagIds);
    }

}
