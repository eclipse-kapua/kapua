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
import org.eclipse.kapua.app.console.module.api.client.util.DateUtils;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtUpdatableEntityModel;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceQueryPredicates.GwtGroupDevice;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnectionStatus;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class GwtDevice extends GwtUpdatableEntityModel implements Serializable {

    private static final String CLIENT_ID = "clientId";
    private static final String GROUP_ID = "groupId";
    private static final String DISPLAY_NAME = "displayName";
    private static final String CUSTOM_ATTRIBUTE_1 = "customAttribute1";
    private static final String CUSTOM_ATTRIBUTE_2 = "customAttribute2";
    private static final String CUSTOM_ATTRIBUTE_3 = "customAttribute3";
    private static final String CUSTOM_ATTRIBUTE_4 = "customAttribute4";
    private static final String CUSTOM_ATTRIBUTE_5 = "customAttribute5";
    private static final long serialVersionUID = -7294466782978445365L;

    public enum GwtDeviceApplication implements IsSerializable {

        APP_CONFIGURATION("CONF-V1"), //
        APP_COMMAND("CMD-V1"), //
        APP_DEPLOY_V1("DEPLOY-V1"), //
        APP_DEPLOY_V2("DEPLOY-V2"), //
        APP_INVENTORY_V1("INVENTORY-V1"), //
        APP_KEYS_V1("KEYS-V1"), //
        APP_PROV_V1("PROV-V1"), //
        APP_PROV_V2("PROV-V2"), //
        APP_PROV_V3("PROV-V3"), //
        APP_VPN_CLIENT("VPNCLIENT-V1"), //
        APP_CERTIFICATES("CERT-V1"), //
        APP_ASSET_V1("ASSET-V1");

        private final String appId;

        GwtDeviceApplication(String appId) {
            this.appId = appId;
        }

        public String getAppId() {
            return appId;
        }
    }

    @Override
    public <X> X get(String property) {
        if ("lastEventOnFormatted".equals(property)) {
            Date lastEventOn = getLastEventOn();
            if (lastEventOn != null) {
                return (X) DateUtils.formatDateTime(lastEventOn);
            } else {
                return (X) "N/A";
            }
        } else if ("uptimeFormatted".equals(property)) {
            if (getUptime() != null) {
                return (X) String.valueOf(getUptime());
            } else {
                return null;
            }
        } else if ("deviceConnectionStatusEnum".equals(property)) {
            return (X) GwtDeviceConnectionStatus.valueOf(getGwtDeviceConnectionStatus());
        } else {
            return super.get(property);
        }
    }

    public String getClientId() {
        return get(CLIENT_ID);
    }

    public String getUnescapedClientId() {
        return getUnescaped(CLIENT_ID);
    }

    public void setClientId(String clientId) {
        set(CLIENT_ID, clientId);
    }

    public String getGroupId() {
        return get(GROUP_ID);
    }

    public String getUnescapedGroupId() {
        return getUnescaped(GROUP_ID);
    }

    public void setGroupId(String groupId) {
        set(GROUP_ID, groupId);
    }

    public List<String> getTagIds() {
        return get("tagIds");
    }

    public void setTagIds(List<String> tagIds) {
        set("tagIds", tagIds);
    }

    public Long getUptime() {
        return get("uptime");
    }

    public String getUptimeFormatted() {
        return get("uptimeFormatted");
    }

    public void setUptime(Long uptime) {
        set("uptime", uptime);
    }

    public String getGwtDeviceStatus() {
        return get("gwtDeviceStatus");
    }

    public void setGwtDeviceStatus(String gwtDeviceStatus) {
        set("gwtDeviceStatus", gwtDeviceStatus);
    }

    public String getGwtDeviceConnectionStatus() {
        return get("deviceConnectionStatus");
    }

    public String getDeviceConnectionId() {
        return get("deviceConnectionId");
    }

    public void setDeviceConnectionId(String deviceConnectionId) {
        set("deviceConnectionId", deviceConnectionId);
    }

    public GwtDeviceConnectionStatus getGwtDeviceConnectionStatusEnum() {
        return get("deviceConnectionStatusEnum");
    }

    public void setGwtDeviceConnectionStatus(String deviceConnectionStatus) {
        set("deviceConnectionStatus", deviceConnectionStatus);
    }

    public GwtGroupDevice getGroupDeviceEnum() {
        return get("groupDeviceEnum");
    }

    public String getGroupDevice() {
        return get("groupDevice");
    }

    public void setGroupDevice(String groupDevice) {
        set("groupDevice", groupDevice);
    }

    public String getDisplayName() {
        return get(DISPLAY_NAME);
    }

    public String getUnescapedDisplayName() {
        return getUnescaped(DISPLAY_NAME);
    }

    public void setDisplayName(String displayName) {
        set(DISPLAY_NAME, displayName);
    }

    public String getModelName() {
        return get("modelName");
    }

    public void setModelName(String modelName) {
        set("modelName", modelName);
    }

    public String getModelId() {
        return get("modelId");
    }

    public void setModelId(String modelId) {
        set("modelId", modelId);
    }

    public String getPartNumber() {
        return get("partNumber");
    }

    public void setPartNumber(String partNumber) {
        set("partNumber", partNumber);
    }

    public String getSerialNumber() {
        return get("serialNumber");
    }

    public void setSerialNumber(String serialNumber) {
        set("serialNumber", serialNumber);
    }

    public String getAvailableProcessors() {
        return get("availableProcessors");
    }

    public void setAvailableProcessors(String availableProcessors) {
        set("availableProcessors", availableProcessors);
    }

    public String getTotalMemory() {
        return get("totalMemory");
    }

    public void setTotalMemory(String totalMemory) {
        set("totalMemory", totalMemory);
    }

    public String getFirmwareVersion() {
        return get("firmwareVersion");
    }

    public void setFirmwareVersion(String firmwareVersion) {
        set("firmwareVersion", firmwareVersion);
    }

    public String getBiosVersion() {
        return get("biosVersion");
    }

    public void setBiosVersion(String biosVersion) {
        set("biosVersion", biosVersion);
    }

    public String getOs() {
        return get("os");
    }

    public void setOs(String os) {
        set("os", os);
    }

    public String getOsVersion() {
        return get("osVersion");
    }

    public void setOsVersion(String osVersion) {
        set("osVersion", osVersion);
    }

    public String getOsArch() {
        return get("osArch");
    }

    public void setOsArch(String osArch) {
        set("osArch", osArch);
    }

    public String getJvmName() {
        return get("jvmName");
    }

    public void setJvmName(String jvmName) {
        set("jvmName", jvmName);
    }

    public String getJvmVersion() {
        return get("jvmVersion");
    }

    public void setJvmVersion(String jvmVersion) {
        set("jvmVersion", jvmVersion);
    }

    public String getJvmProfile() {
        return get("jvmProfile");
    }

    public void setJvmProfile(String jvmProfile) {
        set("jvmProfile", jvmProfile);
    }

    public String getIotFrameworkVersion() {
        return get("iotFrameworkVersion");
    }

    public void setIotFrameworkVersion(String iotFrameworkVersion) {
        set("iotFrameworkVersion", iotFrameworkVersion);
    }

    public String getOsgiFramework() {
        return get("osgiFramework");
    }

    public void setOsgiFramework(String osgiFramework) {
        set("osgiFramework", osgiFramework);
    }

    public String getOsgiVersion() {
        return get("osgiVersion");
    }

    public void setOsgiVersion(String osgiFrameworkVersion) {
        set("osgiVersion", osgiFrameworkVersion);
    }

    public String getClientIp() {
        return get("clientIp");
    }

    public void setClientIp(String clientIp) {
        set("clientIp", clientIp);
    }

    public String getConnectionInterface() {
        return get("connectionInterface");
    }

    public void setConnectionInterface(String connectionInterface) {
        set("connectionInterface", connectionInterface);
    }

    public String getConnectionIp() {
        return get("connectionIp");
    }

    public void setConnectionIp(String connectionIp) {
        set("connectionIp", connectionIp);
    }

    public String getAcceptEncoding() {
        return get("acceptEncoding");
    }

    public void setAcceptEncoding(String acceptEncoding) {
        set("acceptEncoding", acceptEncoding);
    }

    public String getApplicationIdentifiers() {
        return get("applicationIdentifiers");
    }

    public void setApplicationIdentifiers(String applicationIdentifiers) {
        set("applicationIdentifiers", applicationIdentifiers);
    }

    public Double getGpsLatitude() {
        return get("gpsLatitude");
    }

    public void setGpsLatitude(Double gpsLatitude) {
        set("gpsLatitude", gpsLatitude);
    }

    public Double getGpsLongitude() {
        return get("gpsLongitude");
    }

    public void setGpsLongitude(Double gpsLongitude) {
        set("gpsLongitude", gpsLongitude);
    }

    public Double getGpsAltitude() {
        return get("gpsAltitude");
    }

    public void setGpsAltitude(Double gpsAltitude) {
        set("gpsAltitude", gpsAltitude);
    }

    public String getGpsAddress() {
        return get("gpsAddress");
    }

    public void setGpsAddress(String gpsAddress) {
        set("gpsAddress", gpsAddress);
    }

    public Date getLastEventOn() {
        return get("lastEventOn");
    }

    public String getLastEventOnFormatted() {
        return get("lastEventOnFormatted");
    }

    public void setLastEventOn(Date lastEventDate) {
        set("lastEventOn", lastEventDate);
    }

    public String getLastEventType() {
        return get("lastEventType");
    }

    public void setLastEventType(String lastEventType) {
        set("lastEventType", lastEventType);
    }

    public String getDeviceUserId() {
        return get("deviceUserId");
    }

    public void setDeviceUserId(String deviceUserId) {
        set("deviceUserId", deviceUserId);
    }

    public String getIccid() {
        return get("iccid");
    }

    public void setIccid(String iccid) {
        set("iccid", iccid);
    }

    public String getImei() {
        return get("imei");
    }

    public void setImei(String imei) {
        set("imei", imei);
    }

    public String getImsi() {
        return get("imsi");
    }

    public void setCertificateCommonName(String certificateCommonName) {
        set("certificateCommonName", certificateCommonName);
    }

    public String getCertificateCommonName() {
        return get("certificateCommonName");
    }

    public void setImsi(String imsi) {
        set("imsi", imsi);
    }

    public Long getBrokerClusterId() {
        return get("brokerClusterId");
    }

    public void setBrokerClusterId(long brokerClusterId) {
        set("brokerClusterId", brokerClusterId);
    }

    public String getBrokerNodeId() {
        return get("brokerNodeId");
    }

    public void setBrokerNodeId(String brokerNodeId) {
        set("brokerNodeId", brokerNodeId);
    }

    public String getCustomAttribute1() {
        return get(CUSTOM_ATTRIBUTE_1);
    }

    public String getUnescapedCustomAttribute1() {
        return getUnescaped(CUSTOM_ATTRIBUTE_1);
    }

    public void setCustomAttribute1(String customAttribute1) {
        set(CUSTOM_ATTRIBUTE_1, customAttribute1);
    }

    public String getCustomAttribute2() {
        return get(CUSTOM_ATTRIBUTE_2);
    }

    public String getUnescapedCustomAttribute2() {
        return getUnescaped(CUSTOM_ATTRIBUTE_2);
    }

    public void setCustomAttribute2(String customAttribute2) {
        set(CUSTOM_ATTRIBUTE_2, customAttribute2);
    }

    public String getCustomAttribute3() {
        return get(CUSTOM_ATTRIBUTE_3);
    }

    public String getUnescapedCustomAttribute3() {
        return getUnescaped(CUSTOM_ATTRIBUTE_3);
    }

    public void setCustomAttribute3(String customAttribute3) {
        set(CUSTOM_ATTRIBUTE_3, customAttribute3);
    }

    public String getCustomAttribute4() {
        return get(CUSTOM_ATTRIBUTE_4);
    }

    public String getUnescapedCustomAttribute4() {
        return getUnescaped(CUSTOM_ATTRIBUTE_4);
    }

    public void setCustomAttribute4(String customAttribute4) {
        set(CUSTOM_ATTRIBUTE_4, customAttribute4);
    }

    public String getCustomAttribute5() {
        return get(CUSTOM_ATTRIBUTE_5);
    }

    public String getUnescapedCustomAttribute5() {
        return getUnescaped(CUSTOM_ATTRIBUTE_5);
    }

    public void setCustomAttribute5(String customAttribute5) {
        set(CUSTOM_ATTRIBUTE_5, customAttribute5);
    }

    public boolean isOnline() {
        return "CONNECTED".equals(getGwtDeviceConnectionStatus());
    }
    // Utility methods

    public boolean hasApplication(GwtDeviceApplication application) {
        String applicationIdentifiers = getApplicationIdentifiers();

        if (applicationIdentifiers == null || applicationIdentifiers.isEmpty()) {
            return false;
        }

        for (String deviceApplication : applicationIdentifiers.split(",")) {
            if (deviceApplication.equals(application.getAppId())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GwtDevice) {
            return ((GwtDevice) obj).getId().equals(getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
