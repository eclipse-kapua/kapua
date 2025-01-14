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
package org.eclipse.kapua.app.console.module.device.shared.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceQuery;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceQueryPredicates;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnectionQuery;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnectionStatus;
import org.eclipse.kapua.app.console.module.device.shared.model.management.assets.GwtDeviceAsset;
import org.eclipse.kapua.app.console.module.device.shared.model.management.assets.GwtDeviceAssetChannel;
import org.eclipse.kapua.app.console.module.device.shared.model.management.assets.GwtDeviceAssetChannel.GwtDeviceAssetChannelMode;
import org.eclipse.kapua.app.console.module.device.shared.model.management.assets.GwtDeviceAssets;
import org.eclipse.kapua.app.console.module.device.shared.model.management.registry.GwtDeviceManagementOperationQuery;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.FieldSortCriteria;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate.Operator;
import org.eclipse.kapua.model.type.ObjectTypeConverter;
import org.eclipse.kapua.model.type.ObjectValueConverter;
import org.eclipse.kapua.service.device.management.asset.DeviceAsset;
import org.eclipse.kapua.service.device.management.asset.DeviceAssetChannel;
import org.eclipse.kapua.service.device.management.asset.DeviceAssetChannelMode;
import org.eclipse.kapua.service.device.management.asset.DeviceAssetFactory;
import org.eclipse.kapua.service.device.management.asset.DeviceAssets;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationAttributes;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationFactory;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationQuery;
import org.eclipse.kapua.service.device.registry.DeviceAttributes;
import org.eclipse.kapua.service.device.registry.DeviceFactory;
import org.eclipse.kapua.service.device.registry.DeviceQuery;
import org.eclipse.kapua.service.device.registry.DeviceStatus;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionAttributes;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionFactory;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionQuery;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionStatus;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;

public class GwtKapuaDeviceModelConverter {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final DeviceFactory DEVICE_FACTORY = LOCATOR.getFactory(DeviceFactory.class);
    private static final DeviceConnectionFactory DEVICE_CONNECTION_FACTORY = LOCATOR.getFactory(DeviceConnectionFactory.class);
    private static final DeviceManagementOperationFactory DEVICE_MANAGEMENT_OPERATION_FACTORY = LOCATOR.getFactory(DeviceManagementOperationFactory.class);

    private static final DeviceAssetFactory ASSET_FACTORY = LOCATOR.getFactory(DeviceAssetFactory.class);

    private GwtKapuaDeviceModelConverter() {
    }

    public static DeviceConnectionQuery convertConnectionQuery(PagingLoadConfig loadConfig, GwtDeviceConnectionQuery gwtDeviceConnectionQuery) {
        DeviceConnectionQuery query = new DeviceConnectionQuery(GwtKapuaCommonsModelConverter.convertKapuaId(gwtDeviceConnectionQuery.getScopeId()));
        AndPredicate predicate = query.andPredicate();

        if (gwtDeviceConnectionQuery.getClientId() != null && !gwtDeviceConnectionQuery.getClientId().trim().isEmpty()) {
            predicate.and(query.attributePredicate(DeviceConnectionAttributes.CLIENT_ID, gwtDeviceConnectionQuery.getClientId(), Operator.LIKE));
        }
        if (gwtDeviceConnectionQuery.getConnectionStatus() != null && !gwtDeviceConnectionQuery.getConnectionStatus().equals(GwtDeviceConnectionStatus.ANY.toString())) {
            predicate.and(query.attributePredicate(DeviceConnectionAttributes.STATUS, convertConnectionStatus(gwtDeviceConnectionQuery.getConnectionStatus()), Operator.EQUAL));
        }
        if (gwtDeviceConnectionQuery.getClientIP() != null && !gwtDeviceConnectionQuery.getClientIP().trim().isEmpty()) {
            predicate.and(query.attributePredicate(DeviceConnectionAttributes.CLIENT_IP, gwtDeviceConnectionQuery.getClientIP(), Operator.LIKE));
        }
        if (gwtDeviceConnectionQuery.getUserName() != null && !gwtDeviceConnectionQuery.getUserName().trim().isEmpty()) {
            predicate.and(query.attributePredicate(DeviceConnectionAttributes.USER_ID, gwtDeviceConnectionQuery.getUserName(), Operator.LIKE));
        }
        if (gwtDeviceConnectionQuery.getGwtDeviceConnectionUser() != null) {
            predicate = predicate.and(query.attributePredicate(DeviceConnectionAttributes.USER_ID, KapuaEid.parseCompactId(gwtDeviceConnectionQuery.getUserId())));
        }
        if (gwtDeviceConnectionQuery.getGwtDeviceConnectionReservedUser() != null) {
            switch (gwtDeviceConnectionQuery.getGwtDeviceConnectionReservedUser()) {
            case NONE:
                predicate = predicate.and(query.attributePredicate(DeviceConnectionAttributes.RESERVED_USER_ID, null, Operator.IS_NULL));
                break;
            default:
                predicate = predicate.and(query.attributePredicate(DeviceConnectionAttributes.RESERVED_USER_ID, KapuaEid.parseCompactId(gwtDeviceConnectionQuery.getReservedUserId())));
            }
        }

        if (gwtDeviceConnectionQuery.getProtocol() != null && !gwtDeviceConnectionQuery.getProtocol().trim().isEmpty()) {
            predicate.and(query.attributePredicate(DeviceConnectionAttributes.PROTOCOL, gwtDeviceConnectionQuery.getProtocol(), Operator.LIKE));
        }

        String sortField = StringUtils.isEmpty(loadConfig.getSortField()) ? DeviceConnectionAttributes.CLIENT_ID : loadConfig.getSortField();
        if (sortField.equals("connectionUserCouplingMode")) {
            sortField = DeviceConnectionAttributes.USER_COUPLING_MODE;
        } else if (sortField.equals("modifiedOnFormatted")) {
            sortField = DeviceConnectionAttributes.MODIFIED_ON;
        }
        SortOrder sortOrder = loadConfig.getSortDir().equals(SortDir.DESC) ? SortOrder.DESCENDING : SortOrder.ASCENDING;
        FieldSortCriteria sortCriteria = query.fieldSortCriteria(sortField, sortOrder);
        query.setSortCriteria(sortCriteria);
        query.setOffset(loadConfig.getOffset());
        query.setLimit(loadConfig.getLimit());
        query.setPredicate(predicate);
        query.setAskTotalCount(gwtDeviceConnectionQuery.getAskTotalCount());
        return query;
    }

    public static DeviceConnectionStatus convertConnectionStatus(String connectionStatus) {
        return DeviceConnectionStatus.valueOf(connectionStatus);
    }

    public static DeviceAssets convertDeviceAssets(GwtDeviceAssets deviceAssets) {
        DeviceAssets assets = new DeviceAssets();
        List<DeviceAsset> assetList = new ArrayList<DeviceAsset>();
        for (GwtDeviceAsset gwtDeviceAsset : deviceAssets.getAssets()) {
            assetList.add(convertDeviceAsset(gwtDeviceAsset));
        }
        assets.setAssets(assetList);
        return assets;
    }

    public static DeviceAsset convertDeviceAsset(GwtDeviceAsset gwtDeviceAsset) {

        DeviceAsset deviceAsset = ASSET_FACTORY.newDeviceAsset();
        deviceAsset.setName(gwtDeviceAsset.getName());
        for (GwtDeviceAssetChannel gwtDeviceAssetChannel : gwtDeviceAsset.getChannels()) {
            deviceAsset.getChannels().add(convertDeviceAssetChannel(gwtDeviceAssetChannel));
        }
        return deviceAsset;
    }

    public static DeviceAssetChannel convertDeviceAssetChannel(GwtDeviceAssetChannel gwtDeviceAssetChannel) {

        DeviceAssetChannel channel = ASSET_FACTORY.newDeviceAssetChannel();
        channel.setName(gwtDeviceAssetChannel.getName());
        try {
            channel.setType(ObjectTypeConverter.fromString(gwtDeviceAssetChannel.getType()));
            channel.setValue(ObjectValueConverter.fromString(gwtDeviceAssetChannel.getValue(), channel.getType()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        channel.setTimestamp(gwtDeviceAssetChannel.getTimestamp());
        channel.setMode(convertDeviceAssetChannel(gwtDeviceAssetChannel.getModeEnum()));
        channel.setError(gwtDeviceAssetChannel.getError());
        return channel;

    }

    public static DeviceAssetChannelMode convertDeviceAssetChannel(GwtDeviceAssetChannelMode gwtMode) {
        return DeviceAssetChannelMode.valueOf(gwtMode.toString());
    }

    public static DeviceQuery convertDeviceQuery(PagingLoadConfig loadConfig, GwtDeviceQuery gwtDeviceQuery) {
        DeviceQuery query = new DeviceQuery(KapuaEid.parseCompactId(gwtDeviceQuery.getScopeId()));

        if (loadConfig != null) {
            query.setLimit(loadConfig.getLimit());
            query.setOffset(loadConfig.getOffset());
        }

        GwtDeviceQueryPredicates predicates = gwtDeviceQuery.getPredicates();
        AndPredicate andPred = query.andPredicate();

        if (predicates.getClientId() != null) {
            andPred = andPred.and(query.attributePredicate(DeviceAttributes.CLIENT_ID, predicates.getUnescapedClientId(), Operator.LIKE));
        }
        if (predicates.getDisplayName() != null) {
            andPred = andPred.and(query.attributePredicate(DeviceAttributes.DISPLAY_NAME, predicates.getUnescapedDisplayName(), Operator.LIKE));
        }
        if (predicates.getSerialNumber() != null) {
            andPred = andPred.and(query.attributePredicate(DeviceAttributes.SERIAL_NUMBER, predicates.getUnescapedSerialNumber(), Operator.LIKE));
        }
        if (predicates.getDeviceStatus() != null) {
            andPred = andPred.and(query.attributePredicate(DeviceAttributes.STATUS, DeviceStatus.valueOf(predicates.getDeviceStatus())));
        }
        if (predicates.getIotFrameworkVersion() != null) {
            andPred = andPred.and(query.attributePredicate(DeviceAttributes.APPLICATION_FRAMEWORK_VERSION, predicates.getIotFrameworkVersion(), Operator.LIKE));
        }
        if (predicates.getApplicationIdentifiers() != null) {
            andPred = andPred.and(query.attributePredicate(DeviceAttributes.APPLICATION_IDENTIFIERS, predicates.getApplicationIdentifiers(), Operator.LIKE));
        }
        if (predicates.getCustomAttribute1() != null) {
            andPred = andPred.and(query.attributePredicate(DeviceAttributes.CUSTOM_ATTRIBUTE_1, predicates.getCustomAttribute1(), Operator.LIKE));
        }
        if (predicates.getCustomAttribute2() != null) {
            andPred = andPred.and(query.attributePredicate(DeviceAttributes.CUSTOM_ATTRIBUTE_2, predicates.getCustomAttribute2(), Operator.LIKE));
        }
        if (predicates.getDeviceConnectionStatus() != null) {
            switch (predicates.getDeviceConnectionStatusEnum()) {
            case UNKNOWN:
                andPred = andPred.and(query.attributePredicate(DeviceAttributes.CONNECTION_ID, DeviceConnectionStatus.NULL, Operator.IS_NULL));
                break;
            default:
                andPred = andPred.and(query.attributePredicate(DeviceAttributes.CONNECTION_STATUS, DeviceConnectionStatus.valueOf(predicates.getDeviceConnectionStatus())));
            }
        }
        if (predicates.getGroupDevice() != null) {
            switch (predicates.getGroupDeviceEnum()) {
            case NO_GROUP:
                andPred = andPred.and(query.attributePredicate(DeviceAttributes.GROUP_ID, null, Operator.IS_NULL));
                break;
            default:
                if (predicates.getGroupId() != null) {
                    andPred = andPred.and(query.attributePredicate(DeviceAttributes.GROUP_ID, KapuaEid.parseCompactId(predicates.getGroupId())));
                }
            }
        }
        if (predicates.getTagIds() != null) {
            List<KapuaId> tagIds = new ArrayList<KapuaId>();
            for (String gwtTagId : predicates.getTagIds()) {
                tagIds.add(GwtKapuaCommonsModelConverter.convertKapuaId(gwtTagId));
            }
            andPred = andPred.and(query.attributePredicate(DeviceAttributes.TAG_IDS, tagIds));
        }

        if (predicates.getSortAttribute() != null && loadConfig != null) {
            String sortField = StringUtils.isEmpty(loadConfig.getSortField()) ? DeviceAttributes.CLIENT_ID : loadConfig.getSortField();
            SortOrder sortOrder = loadConfig.getSortDir().equals(SortDir.DESC) ? SortOrder.DESCENDING : SortOrder.ASCENDING;
            if (sortField.equals("lastEventOnFormatted")) {
                sortField = DeviceAttributes.LAST_EVENT_ON;
            }
            query.setSortCriteria(query.fieldSortCriteria(sortField, sortOrder));
        } else {
            query.setSortCriteria(query.fieldSortCriteria(DeviceAttributes.CLIENT_ID, SortOrder.ASCENDING));
        }

        query.setPredicate(andPred);
        query.setAskTotalCount(gwtDeviceQuery.getAskTotalCount());
        return query;
    }

    public static DeviceManagementOperationQuery convertDeviceManagementOperationQuery(PagingLoadConfig loadConfig, GwtDeviceManagementOperationQuery gwtQuery) {

        DeviceManagementOperationQuery query = new DeviceManagementOperationQuery(KapuaEid.parseCompactId(gwtQuery.getScopeId()));

        String deviceId = gwtQuery.getDeviceId();
        AndPredicate andPredicate = query.andPredicate();
        if (deviceId != null) {
            andPredicate.and(query.attributePredicate(DeviceManagementOperationAttributes.DEVICE_ID, KapuaEid.parseCompactId(gwtQuery.getDeviceId())));
        }

        String appId = gwtQuery.getAppId();
        if (appId != null) {
            andPredicate.and(query.attributePredicate(DeviceManagementOperationAttributes.APP_ID, appId));
        }

        query.setPredicate(andPredicate);

        if (loadConfig != null && loadConfig.getSortField() != null) {
            String sortField = loadConfig.getSortField();
            SortOrder sortOrder = loadConfig.getSortDir().equals(SortDir.DESC) ? SortOrder.DESCENDING : SortOrder.ASCENDING;

            if (sortField.equals("startedOnFormatted")) {
                sortField = DeviceManagementOperationAttributes.STARTED_ON;
            }
            if (sortField.equals("endedOnFormatted")) {
                sortField = DeviceManagementOperationAttributes.ENDED_ON;
            }
            query.setSortCriteria(query.fieldSortCriteria(sortField, sortOrder));
        } else {
            query.setSortCriteria(query.fieldSortCriteria(DeviceManagementOperationAttributes.STARTED_ON, SortOrder.DESCENDING));
        }

        if (loadConfig != null) {
            query.setLimit(loadConfig.getLimit());
            query.setOffset(loadConfig.getOffset());
        }
        query.setAskTotalCount(gwtQuery.getAskTotalCount());
        return query;
    }
}
