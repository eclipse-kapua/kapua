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
package org.eclipse.kapua.app.console.module.api.shared.util;

import org.eclipse.kapua.app.console.module.api.shared.model.GwtEntityModel;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtUpdatableEntityModel;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.KapuaEntityBase;
import org.eclipse.kapua.model.KapuaUpdatableEntity;
import org.eclipse.kapua.model.id.KapuaId;

public class KapuaGwtCommonsModelConverter {

    private KapuaGwtCommonsModelConverter() {
    }

    //    /**
    //     * Converts a {@link Role} into a {@link GwtRole} object for GWT usage.
    //     *
    //     * @param role The {@link Role} to convertKapuaId.
    //     * @return The converted {@link GwtRole}.
    //     * @since 1.0.0
    //     */
    //    public static GwtRole convertKapuaId(Role role) {
    //        GwtRole gwtRole = new GwtRole();
    //
    //        //
    //        // Covert commons attributes
    //        convertUpdatableEntity(role, gwtRole);
    //
    //        //
    //        // Convert other attributes
    //        gwtRole.setName(role.getName());
    //
    //        //
    //        // Return converted entity
    //        return gwtRole;
    //    }
    //
    //    /**
    //     * Merges a {@link Role} and a {@link AccessRole} into a {@link GwtAccessRole} object for GWT usage.
    //     *
    //     * @param role       The {@link Role} to merge.
    //     * @param accessRole The {@link AccessRole} to merge.
    //     * @return The converted {@link GwtAccessRole}.
    //     * @since 1.0.0
    //     */
    //    public static GwtAccessRole convertKapuaId(Role role, AccessRole accessRole) {
    //        GwtAccessRole gwtAccessRole = new GwtAccessRole();
    //
    //        //
    //        // Covert commons attributes
    //        convertUpdatableEntity(accessRole, gwtAccessRole);
    //
    //        //
    //        // Convert other attributes
    //        gwtAccessRole.setRoleName(role.getName());
    //        gwtAccessRole.setRoleId(role.getViewId().toCompactId());
    //        gwtAccessRole.setAccessInfoId(accessRole.getAccessInfoId().toCompactId());
    //        //
    //        // Return converted entity
    //        return gwtAccessRole;
    //    }
    //
    //    /**
    //     * Converts a {@link AccessRole} into a {@link GwtAccessRole} object for GWT usage.
    //     *
    //     * @param accessRole The {@link AccessRole} to convertKapuaId.
    //     * @return The converted {@link GwtAccessRole}.
    //     * @since 1.0.0
    //     */
    //    public static GwtAccessRole convertKapuaId(AccessRole accessRole) {
    //        GwtAccessRole gwtAccessRole = new GwtAccessRole();
    //
    //        //
    //        // Covert commons attributes
    //        convertUpdatableEntity(accessRole, gwtAccessRole);
    //
    //        gwtAccessRole.setRoleId(accessRole.getRoleId().toCompactId());
    //        gwtAccessRole.setAccessInfoId(accessRole.getAccessInfoId().toCompactId());
    //
    //        //
    //        // Return converted entity
    //        return gwtAccessRole;
    //    }
    //
    //    /**
    //     * Converts a {@link AccessPermission} into a {@link GwtAccessPermission} object for GWT usage.
    //     *
    //     * @param accessPermission The {@link AccessPermission} to convertKapuaId.
    //     * @return The converted {@link GwtAccessPermission}.
    //     * @since 1.0.0
    //     */
    //    public static GwtAccessPermission convertKapuaId(AccessPermission accessPermission) {
    //        GwtAccessPermission gwtAccessPermission = new GwtAccessPermission();
    //
    //        //
    //        // Covert commons attributes
    //        convertUpdatableEntity(accessPermission, gwtAccessPermission);
    //
    //        gwtAccessPermission.setAccessInfoId(accessPermission.getAccessInfoId().toCompactId());
    //        gwtAccessPermission.setPermissionDomain(accessPermission.getPermission().getDomain());
    //
    //        if (accessPermission.getPermission().getAction() != null) {
    //            gwtAccessPermission.setPermissionAction(accessPermission.getPermission().getAction().toString());
    //        } else {
    //            gwtAccessPermission.setPermissionAction("ALL");
    //        }
    //
    //        if (accessPermission.getPermission().getTargetScopeId() != null) {
    //            gwtAccessPermission.setPermissionTargetScopeId(accessPermission.getPermission().getTargetScopeId().toCompactId());
    //        } else {
    //            gwtAccessPermission.setPermissionTargetScopeId("ALL");
    //        }
    //
    //        if (accessPermission.getPermission().getGroupId() != null) {
    //            gwtAccessPermission.setPermissionGroupId(accessPermission.getPermission().getGroupId().toCompactId());
    //        } else {
    //            gwtAccessPermission.setPermissionGroupId("ALL");
    //        }
    //
    //        gwtAccessPermission.setPermissionForwardable(accessPermission.getPermission().getForwardable());
    //
    //        //
    //        // Return converted entity
    //        return gwtAccessPermission;
    //    }
    //
    //    /**
    //     * Converts a {@link AccessInfo} into a {@link GwtAccessInfo} object for GWT usage.
    //     *
    //     * @param accessInfo The {@link AccessInfo} to convertKapuaId.
    //     * @return The converted {@link GwtAccessInfo}.
    //     * @since 1.0.0
    //     */
    //    public static GwtAccessInfo convertKapuaId(AccessInfo accessInfo) {
    //        GwtAccessInfo gwtAccessInfo = new GwtAccessInfo();
    //        //
    //        // Covert commons attributes
    //        convertUpdatableEntity(accessInfo, gwtAccessInfo);
    //
    //        gwtAccessInfo.setUserId(accessInfo.getUserId().toCompactId());
    //
    //        //
    //        // Return converted entity
    //        return gwtAccessInfo;
    //    }
    //
    //    /**
    //     * Converts a {@link RolePermission} into a {@link GwtRolePermission} object for GWT usage.
    //     *
    //     * @param rolePermission The {@link RolePermission} to convertKapuaId
    //     * @return The converted {@link GwtRolePermission}
    //     * @since 1.0.0
    //     */
    //    public static GwtRolePermission convertKapuaId(RolePermission rolePermission) {
    //        GwtRolePermission gwtRolePermission = new GwtRolePermission();
    //
    //        //
    //        // Covert commons attributes
    //        convertUpdatableEntity(rolePermission, gwtRolePermission);
    //
    //        //
    //        // Convert other attributes
    //        GwtPermission gwtPermission = convertKapuaId((Permission) rolePermission.getPermission());
    //
    //        gwtRolePermission.setRoleId(convertKapuaId(rolePermission.getRoleId()));
    //        gwtRolePermission.setDomain(gwtPermission.getDomain());
    //        gwtRolePermission.setAction(gwtPermission.getAction());
    //        gwtRolePermission.setGroupId(gwtPermission.getGroupId());
    //        gwtRolePermission.setTargetScopeId(gwtPermission.getTargetScopeId());
    //        gwtRolePermission.setForwardable(gwtPermission.getForwardable());
    //
    //        //
    //        // Return converted entity
    //        return gwtRolePermission;
    //    }
    //
    //    /**
    //     * Converts a {@link Permission} into a {@link GwtPermission} object for GWT usage.
    //     *
    //     * @param permission The {@link Permission} to convertKapuaId.
    //     * @return The converted {@link GwtPermission}.
    //     * @since 1.0.0
    //     */
    //    public static GwtPermission convertKapuaId(Permission permission) {
    //        return new GwtPermission(convertDomain(permission.getDomain()),
    //                convertKapuaId(permission.getAction()),
    //                convertKapuaId(permission.getTargetScopeId()),
    //                convertKapuaId(permission.getGroupId()),
    //                permission.getForwardable());
    //    }
    //
    //    /**
    //     * Converts a {@link Action} into a {@link GwtAction}
    //     *
    //     * @param action The {@link Action} to convertKapuaId
    //     * @return The converted {@link GwtAction}
    //     * @since 1.0.0
    //     */
    //    public static GwtAction convertKapuaId(Actions action) {
    //
    //        GwtAction gwtAction = null;
    //        if (action != null) {
    //            switch (action) {
    //            case connect:
    //                gwtAction = GwtAction.connect;
    //                break;
    //            case delete:
    //                gwtAction = GwtAction.delete;
    //                break;
    //            case execute:
    //                gwtAction = GwtAction.execute;
    //                break;
    //            case read:
    //                gwtAction = GwtAction.read;
    //                break;
    //            case write:
    //                gwtAction = GwtAction.write;
    //                break;
    //            }
    //        }
    //        return gwtAction;
    //    }
    //
    //    /**
    //     * Converts a {@link Group} into a {@link GwtGroup}
    //     *
    //     * @param group The {@link Group} to convertKapuaId
    //     * @return The converted {@link GwtGroup}
    //     * @since 1.0.0
    //     */
    //    public static GwtGroup convertKapuaId(Group group) {
    //
    //        GwtGroup gwtGroup = new GwtGroup();
    //        //
    //        // Covert commons attributes
    //        convertUpdatableEntity(group, gwtGroup);
    //
    //        //
    //        // Convert other attributes
    //        gwtGroup.setGroupName(group.getName());
    //
    //        return gwtGroup;
    //    }
    //
    //    /**
    //     * Converts a {@link String} domain into a {@link GwtDomain}
    //     *
    //     * @param domain The {@link String} domain to convertKapuaId
    //     * @return The converted {@link GwtDomain}
    //     * @since 1.0.0
    //     */
    //    public static GwtDomain convertDomain(String domain) {
    //        GwtDomain gwtDomain = null;
    //
    //        if (new AccessInfoDomain().getName().equals(domain)) {
    //            gwtDomain = GwtDomain.access_info;
    //        } else if (new AccessTokenDomain().getName().equals(domain)) {
    //            gwtDomain = GwtDomain.access_token;
    //        } else if (Domains.ACCOUNT.getName().equals(domain)) {
    //            gwtDomain = GwtDomain.account;
    //        } else if (new BrokerDomain().getName().equals(domain)) {
    //            gwtDomain = GwtDomain.broker;
    //        } else if (new CredentialDomain().getName().equals(domain)) {
    //            gwtDomain = GwtDomain.credential;
    //        } else if (new DatastoreDomain().getName().equals(domain)) {
    //            gwtDomain = GwtDomain.datastore;
    //        } else if (new DeviceDomain().getName().equals(domain)) {
    //            gwtDomain = GwtDomain.device;
    //        } else if (new DeviceConnectionDomain().getName().equals(domain)) {
    //            gwtDomain = GwtDomain.device_connection;
    //        } else if (new DeviceEventDomain().getName().equals(domain)) {
    //            gwtDomain = GwtDomain.device_event;
    //        } else if (new DeviceLifecycleDomain().getName().equals(domain)) {
    //            gwtDomain = GwtDomain.device_lifecycle;
    //        } else if (new DeviceManagementDomain().getName().equals(domain)) {
    //            gwtDomain = GwtDomain.device_management;
    //        } else if (new DomainDomain().getName().equals(domain)) {
    //            gwtDomain = GwtDomain.domain;
    //        } else if (new GroupDomain().getName().equals(domain)) {
    //            gwtDomain = GwtDomain.group;
    //        } else if (new RoleDomain().getName().equals(domain)) {
    //            gwtDomain = GwtDomain.role;
    //        } else if (new UserDomain().getName().equals(domain)) {
    //            gwtDomain = GwtDomain.user;
    //        }
    //
    //        return gwtDomain;
    //    }
    //
    //    /**
    //     * Converts a {@link String} action into a {@link GwtAction}
    //     *
    //     * @param action The {@link String} action to convertKapuaId
    //     * @return The converted {@link GwtAction}
    //     * @since 1.0.0
    //     */
    //    public static GwtAction convertAction(String action) {
    //        return GwtAction.valueOf(action);
    //    }
    //
    //    /**
    //     * Converts a {@link Action} action into a {@link GwtAction}
    //     *
    //     * @param action The {@link Action} action to convertKapuaId
    //     * @return The converted {@link GwtAction}
    //     * @since 1.0.0
    //     */
    //    public static GwtAction convertAction(Action action) {
    //        return GwtAction.valueOf(action.toString());
    //    }

    /**
     * Converts a {@link KapuaId} into its {@link String} short id representation.
     * <p>
     * Example: 1 =&gt; AQ
     * </p>
     *
     * @param kapuaId
     *         The {@link KapuaId} to convertKapuaId
     * @return The short id representation of the {@link KapuaId}
     * @since 1.0.0
     */
    public static String convertKapuaId(KapuaId kapuaId) {
        if (kapuaId == null) {
            return null;
        }
        // Return converted entity
        return kapuaId.toCompactId();
    }
    //    /**
    //     * Converts a {@link User} into a {@link GwtUser} for GWT usage.
    //     *
    //     * @param user The {@link User} to convertKapuaId.
    //     * @return The converted {@link GwtUser}
    //     * @since 1.0.0
    //     */
    //    public static GwtUser convertKapuaId(User user)
    //            throws KapuaException {
    //
    //        GwtUser gwtUser = new GwtUser();
    //
    //        //
    //        // Convert commons attributes
    //        convertUpdatableEntity(user, gwtUser);
    //
    //        //
    //        // Convert other attributes
    //        gwtUser.setUsername(user.getName());
    //        gwtUser.setDisplayName(user.getDisplayName());
    //        gwtUser.setEmail(user.getEmail());
    //        gwtUser.setPhoneNumber(user.getPhoneNumber());
    //        gwtUser.setStatus(user.getStatus().name());
    //        gwtUser.setExpirationDate(user.getExpirationDate());
    //
    //        //
    //        // Return converted entity
    //        return gwtUser;
    //    }
    //
    //    public static GwtDevice convertKapuaId(Device device)
    //            throws KapuaException {
    //
    //        GwtDevice gwtDevice = new GwtDevice();
    //        gwtDevice.setId(convertKapuaId(device.getViewId()));
    //        gwtDevice.setScopeId(convertKapuaId(device.getScopeId()));
    //        gwtDevice.setGwtDeviceStatus(device.getStatus().toString());
    //        gwtDevice.setClientId(device.getClientId());
    //        gwtDevice.setDisplayName(device.getDisplayName());
    //        gwtDevice.setModelId(device.getModelId());
    //        gwtDevice.setSerialNumber(device.getSerialNumber());
    //        gwtDevice.setGroupId(convertKapuaId(device.getGroupId()));
    //        gwtDevice.setFirmwareVersion(device.getFirmwareVersion());
    //        gwtDevice.setBiosVersion(device.getBiosVersion());
    //        gwtDevice.setOsVersion(device.getOsVersion());
    //        gwtDevice.setJvmVersion(device.getJvmVersion());
    //        gwtDevice.setOsgiVersion(device.getOsgiFrameworkVersion());
    //        gwtDevice.setAcceptEncoding(device.getAcceptEncoding());
    //        gwtDevice.setApplicationIdentifiers(device.getApplicationIdentifiers());
    //        gwtDevice.setIotFrameworkVersion(device.getApplicationFrameworkVersion());
    //        gwtDevice.setIccid(device.getIccid());
    //        gwtDevice.setImei(device.getImei());
    //        gwtDevice.setImsi(device.getImsi());
    //        gwtDevice.setCustomAttribute1(device.getCustomAttribute1());
    //        gwtDevice.setCustomAttribute2(device.getCustomAttribute2());
    //        gwtDevice.setCustomAttribute3(device.getCustomAttribute3());
    //        gwtDevice.setCustomAttribute4(device.getCustomAttribute4());
    //        gwtDevice.setCustomAttribute5(device.getCustomAttribute5());
    //        gwtDevice.setOptlock(device.getOptlock());
    //
    //        // Last device event
    //        if (device.getLastEvent() != null) {
    //            DeviceEvent lastEvent = device.getLastEvent();
    //
    //            gwtDevice.setLastEventType(lastEvent.getType());
    //            gwtDevice.setLastEventOn(lastEvent.getReceivedOn());
    //
    //        }
    //
    //        // Device connection
    //        gwtDevice.setConnectionIp(device.getConnectionIp());
    //        gwtDevice.setConnectionInterface(device.getConnectionInterface());
    //        if (device.getConnection() != null) {
    //            DeviceConnection connection = device.getConnection();
    //            gwtDevice.setClientIp(connection.getClientIp());
    //            gwtDevice.setGwtDeviceConnectionStatus(connection.getStatus().toString());
    //            gwtDevice.setDeviceUserId(connection.getUserId().toCompactId());
    //        }
    //        return gwtDevice;
    //    }
    //
    //    public static GwtDeviceEvent convertKapuaId(DeviceEvent deviceEvent) {
    //        GwtDeviceEvent gwtDeviceEvent = new GwtDeviceEvent();
    //        gwtDeviceEvent.setDeviceId(deviceEvent.getDeviceId().toCompactId());
    //        gwtDeviceEvent.setSentOn(deviceEvent.getSentOn());
    //        gwtDeviceEvent.setReceivedOn(deviceEvent.getReceivedOn());
    //        gwtDeviceEvent.setEventType(deviceEvent.getResource());
    //        gwtDeviceEvent.setGwtActionType(deviceEvent.getAction().toString());
    //        gwtDeviceEvent.setGwtResponseCode(deviceEvent.getResponseCode().toString());
    //        String escapedMessage = KapuaSafeHtmlUtils.htmlEscape(deviceEvent.getEventMessage());
    //        gwtDeviceEvent.setEventMessage(escapedMessage);
    //
    //        return gwtDeviceEvent;
    //    }
    //
    //    public static GwtDeviceConnection convertKapuaId(DeviceConnection deviceConnection) {
    //        GwtDeviceConnection gwtDeviceConnection = new GwtDeviceConnection();
    //
    //        //
    //        // Convert commons attributes
    //        convertUpdatableEntity(deviceConnection, gwtDeviceConnection);
    //
    //        //
    //        // Convert other attributes
    //        gwtDeviceConnection.setClientId(deviceConnection.getClientId());
    //        gwtDeviceConnection.setUserId(convertKapuaId(deviceConnection.getUserId()));
    //        gwtDeviceConnection.setClientIp(deviceConnection.getClientIp());
    //        gwtDeviceConnection.setServerIp(deviceConnection.getServerIp());
    //        gwtDeviceConnection.setProtocol(deviceConnection.getProtocol());
    //        gwtDeviceConnection.setConnectionStatus(convertKapuaId(deviceConnection.getStatus()));
    //        gwtDeviceConnection.setOptlock(deviceConnection.getOptlock());
    //
    //        //
    //        // Return converted entity
    //        return gwtDeviceConnection;
    //    }
    //
    //    private static String convertKapuaId(DeviceConnectionStatus status) {
    //        return status.toString();
    //    }
    //
    //    public static GwtCredential convertKapuaId(Credential credential, User user) {
    //        GwtCredential gwtCredential = new GwtCredential();
    //        convertUpdatableEntity(credential, gwtCredential);
    //        gwtCredential.setUserId(credential.getUserId().toCompactId());
    //        gwtCredential.setCredentialType(credential.getCredentialType().toString());
    //        gwtCredential.setCredentialKey(credential.getCredentialKey());
    //        gwtCredential.setCredentialStatus(credential.getStatus().toString());
    //        gwtCredential.setExpirationDate(credential.getExpirationDate());
    //        if (user != null) {
    //            gwtCredential.setUsername(user.getName());
    //        }
    //        gwtCredential.setSubjectType(GwtSubjectType.USER.toString());
    //        return gwtCredential;
    //    }
    //
    //    public static GwtTopic convertToTopic(ChannelInfo channelInfo) {
    //        return new GwtTopic(channelInfo.getName(), channelInfo.getName(), channelInfo.getName(), channelInfo.getLastMessageOn());
    //    }
    //
    //    public static GwtDatastoreAsset convertToAssets(ChannelInfo channelInfo) {
    //        return new GwtDatastoreAsset(channelInfo.getName().substring(6), channelInfo.getName(), channelInfo.getName(), channelInfo.getLastMessageOn());
    //    }
    //
    //    public static GwtHeader convertToHeader(MetricInfo metric) {
    //        GwtHeader header = new GwtHeader();
    //        header.setName(metric.getName());
    //        header.setType(metric.getMetricType().getSimpleName());
    //        return header;
    //    }

    /**
     * Utility method to convertKapuaId commons properties of {@link KapuaUpdatableEntity} object to the GWT matching {@link GwtUpdatableEntityModel} object
     *
     * @param kapuaEntity
     *         The {@link KapuaUpdatableEntity} from which to copy values
     * @param gwtEntity
     *         The {@link GwtUpdatableEntityModel} into which copy values
     * @since 1.0.0
     */
    public static void convertUpdatableEntity(KapuaUpdatableEntity kapuaEntity, GwtUpdatableEntityModel gwtEntity) {
        if (kapuaEntity == null || gwtEntity == null) {
            return;
        }

        convertEntity((KapuaEntity) kapuaEntity, (GwtEntityModel) gwtEntity);

        gwtEntity.setModifiedOn(kapuaEntity.getModifiedOn());
        gwtEntity.setModifiedBy(convertKapuaId(kapuaEntity.getModifiedBy()));
        gwtEntity.setOptlock(kapuaEntity.getOptlock());
    }

    /**
     * Utility method to convertKapuaId commons properties of {@link KapuaEntity} object to the GWT matching {@link GwtEntityModel} object
     *
     * @param kapuaEntity
     *         The {@link KapuaEntity} from which to copy values
     * @param gwtEntity
     *         The {@link GwtEntityModel} into which copy values
     * @since 1.0.0
     */
    public static void convertEntity(KapuaEntity kapuaEntity, GwtEntityModel gwtEntity) {
        if (kapuaEntity == null || gwtEntity == null) {
            return;
        }

        gwtEntity.setScopeId(convertKapuaId(kapuaEntity.getScopeId()));
        gwtEntity.setId(convertKapuaId(kapuaEntity.getId()));
        gwtEntity.setCreatedOn(kapuaEntity.getCreatedOn());
        gwtEntity.setCreatedBy(convertKapuaId(kapuaEntity.getCreatedBy()));
    }

    public static void convertEntity(KapuaEntityBase kapuaEntity, GwtEntityModel gwtEntity) {
        if (kapuaEntity == null || gwtEntity == null) {
            return;
        }

        gwtEntity.setScopeId(convertKapuaId(kapuaEntity.getScopeId()));
        gwtEntity.setId(convertKapuaId(kapuaEntity.getId()));
        gwtEntity.setCreatedOn(kapuaEntity.getCreatedOn());
        gwtEntity.setCreatedBy(convertKapuaId(kapuaEntity.getCreatedBy()));
    }

    //    /**
    //     * @param client
    //     * @return
    //     */
    //    public static GwtDatastoreDevice convertToDatastoreDevice(ClientInfo client) {
    //        return new GwtDatastoreDevice(client.getClientId(), client.getLastMessageOn());
    //    }
    //
    //    /**
    //     * @param message
    //     * @param headers
    //     * @return
    //     */
    //    public static GwtMessage convertToMessage(DatastoreMessage message, List<GwtHeader> headers) {
    //        GwtMessage gwtMessage = new GwtMessage();
    //        List<String> semanticParts = message.getChannel().getSemanticParts();
    //        StringBuilder semanticTopic = new StringBuilder();
    //        for (int i = 0; i < semanticParts.size() - 1; i++) {
    //            semanticTopic.append(semanticParts.get(i));
    //            semanticTopic.append("/");
    //        }
    //        semanticTopic.append(semanticParts.get(semanticParts.size() - 1));
    //        gwtMessage.setChannel(semanticTopic.toString());
    //        gwtMessage.setClientId(message.getClientId());
    //        gwtMessage.setTimestamp(message.getTimestamp());
    //        for (GwtHeader header : headers) {
    //            gwtMessage.set(header.getName(), message.getPayload().getMetrics().get(header.getName()));
    //        }
    //        return gwtMessage;
    //    }
    //
    //    public static GwtDeviceAssets convertKapuaId(DeviceAssets assets) {
    //        GwtDeviceAssets gwtAssets = new GwtDeviceAssets();
    //        List<GwtDeviceAsset> gwtAssetsList = new ArrayList<GwtDeviceAsset>();
    //        for (DeviceAsset asset : assets.getAssets()) {
    //            gwtAssetsList.add(convertKapuaId(asset));
    //        }
    //        gwtAssets.setAssets(gwtAssetsList);
    //        return gwtAssets;
    //    }
    //
    //    public static GwtDeviceAsset convertKapuaId(DeviceAsset asset) {
    //        GwtDeviceAsset gwtAsset = new GwtDeviceAsset();
    //        List<GwtDeviceAssetChannel> gwtChannelsList = new ArrayList<GwtDeviceAssetChannel>();
    //        gwtAsset.setName(asset.getName());
    //        for (DeviceAssetChannel channel : asset.getChannels()) {
    //            gwtChannelsList.add(convertKapuaId(channel));
    //        }
    //        gwtAsset.setChannels(gwtChannelsList);
    //        return gwtAsset;
    //    }
    //
    //    public static GwtDeviceAssetChannel convertKapuaId(DeviceAssetChannel channel) {
    //        GwtDeviceAssetChannel gwtChannel = new GwtDeviceAssetChannel();
    //        gwtChannel.setName(channel.getName());
    //        gwtChannel.setError(channel.getError());
    //        gwtChannel.setTimestamp(channel.getTimestamp());
    //        gwtChannel.setMode(channel.getMode().toString());
    //        gwtChannel.setType(ObjectTypeConverter.toString(channel.getType()));
    //        gwtChannel.setValue(ObjectValueConverter.toString(channel.getValue()));
    //        return gwtChannel;
    //    }

}
