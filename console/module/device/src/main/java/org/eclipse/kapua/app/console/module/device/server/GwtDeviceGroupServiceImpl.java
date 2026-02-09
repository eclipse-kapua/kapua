/*******************************************************************************
 * Copyright (c) 2017, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.device.server;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.server.util.UserCreatedByModifiedByUtils;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtGroupedNVPair;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupQuery;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceGroupService;
import org.eclipse.kapua.app.console.module.device.shared.util.GwtKapuaDeviceModelConverter;
import org.eclipse.kapua.app.console.module.device.shared.util.KapuaGwtDeviceModelConverter;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.registry.group.DeviceGroup;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupCreator;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupFactory;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupListResult;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupQuery;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GwtDeviceGroupServiceImpl extends KapuaRemoteServiceServlet implements GwtDeviceGroupService {

    private static final String ENTITY_INFO = "entityInfo";

    private static final long serialVersionUID = 7430961652373364113L;

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final DeviceGroupService DEVICE_GROUP_SERVICE = LOCATOR.getService(DeviceGroupService.class);
    private static final DeviceGroupFactory DEVICE_GROUP_FACTORY = LOCATOR.getFactory(DeviceGroupFactory.class);

    @Override
    public GwtGroup create(GwtGroupCreator gwtGroupCreator) throws GwtKapuaException {
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtGroupCreator.getScopeId());

            DeviceGroupCreator deviceGroupCreator = DEVICE_GROUP_FACTORY.newCreator(scopeId);
            deviceGroupCreator.setName(gwtGroupCreator.getName());
            deviceGroupCreator.setDescription(gwtGroupCreator.getDescription());

            DeviceGroup deviceGroup = DEVICE_GROUP_SERVICE.create(deviceGroupCreator);

            return KapuaGwtDeviceModelConverter.convertDeviceGroup(deviceGroup);
        } catch (Exception e) {
            throw KapuaExceptionHandler.buildExceptionFromError(e);
        }
    }

    @Override
    public GwtGroup update(GwtGroup gwtGroup) throws GwtKapuaException {
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtGroup.getScopeId());
            KapuaId groupId = KapuaEid.parseCompactId(gwtGroup.getId());

            DeviceGroup deviceGroup = DEVICE_GROUP_SERVICE.find(scopeId, groupId);

            if (deviceGroup == null) {
                throw new KapuaEntityNotFoundException(DeviceGroup.TYPE, groupId);
            }

            deviceGroup.setName(gwtGroup.getGroupName());
            deviceGroup.setDescription(gwtGroup.getUnescapedDescription());
            deviceGroup.setOptlock(gwtGroup.getOptlock());

            DEVICE_GROUP_SERVICE.update(deviceGroup);

            return KapuaGwtDeviceModelConverter.convertDeviceGroup(DEVICE_GROUP_SERVICE.find(deviceGroup.getScopeId(), deviceGroup.getId()));
        } catch (Exception e) {
            throw KapuaExceptionHandler.buildExceptionFromError(e);
        }
    }

    @Override
    public ListLoadResult<GwtGroupedNVPair> getDeviceGroupDescription(String scopeShortId, String groupShortId) throws GwtKapuaException {
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(scopeShortId);
            KapuaId groupId = KapuaEid.parseCompactId(groupShortId);

            DeviceGroup deviceGroup = DEVICE_GROUP_SERVICE.find(scopeId, groupId);

            List<GwtGroupedNVPair> gwtGroupDescription = new ArrayList<GwtGroupedNVPair>();
            if (deviceGroup != null) {
                gwtGroupDescription.add(new GwtGroupedNVPair("accessGroupInfo", "accessGroupName", deviceGroup.getName()));
                gwtGroupDescription.add(new GwtGroupedNVPair("accessGroupInfo", "accessGroupDescription", deviceGroup.getDescription()));
                gwtGroupDescription.add(new GwtGroupedNVPair("accessGroupInfo", "accessGroupDomain", "Device"));
                gwtGroupDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "accessGroupCreatedOn", deviceGroup.getCreatedOn()));
                gwtGroupDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "accessGroupCreatedBy", UserCreatedByModifiedByUtils.resolveFromId(deviceGroup.getCreatedBy())));
                gwtGroupDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "accessGroupModifiedOn", deviceGroup.getModifiedOn()));
                gwtGroupDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "accessGroupModifiedBy", UserCreatedByModifiedByUtils.resolveFromId(deviceGroup.getModifiedBy())));

            }

            return new BaseListLoadResult<GwtGroupedNVPair>(gwtGroupDescription);
        } catch (Exception e) {
            throw KapuaExceptionHandler.buildExceptionFromError(e);
        }
    }

    @Override
    public PagingLoadResult<GwtGroup> query(PagingLoadConfig loadConfig, GwtGroupQuery gwtGroupQuery) throws GwtKapuaException {
        try {
            DeviceGroupQuery deviceGroupQuery = GwtKapuaDeviceModelConverter.convertDeviceGroupQuery(loadConfig, gwtGroupQuery);

            DeviceGroupListResult deviceGroups = DEVICE_GROUP_SERVICE.query(deviceGroupQuery);

            List<GwtGroup> gwtGroupList = new ArrayList<GwtGroup>();
            if (!deviceGroups.isEmpty()) {

                Map<KapuaId, String> idDevicenameMap = UserCreatedByModifiedByUtils.resolveFromListResult(deviceGroups);

                for (DeviceGroup deviceGroup : deviceGroups.getItems()) {
                    GwtGroup gwtGroup = KapuaGwtDeviceModelConverter.convertDeviceGroup(deviceGroup);

                    gwtGroup.setCreatedByName(idDevicenameMap.get(deviceGroup.getCreatedBy()));
                    gwtGroup.setModifiedByName(idDevicenameMap.get(deviceGroup.getModifiedBy()));

                    gwtGroupList.add(gwtGroup);
                }
            }

            return new BasePagingLoadResult<GwtGroup>(gwtGroupList, loadConfig.getOffset(), deviceGroups.getTotalCount().intValue());
        } catch (Exception e) {
            throw KapuaExceptionHandler.buildExceptionFromError(e);
        }
    }

    @Override
    public void delete(String gwtScopeId, String gwtDeviceGroupId) throws GwtKapuaException {
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtScopeId);
            KapuaId deviceGroupid = GwtKapuaCommonsModelConverter.convertKapuaId(gwtDeviceGroupId);

            DEVICE_GROUP_SERVICE.delete(scopeId, deviceGroupid);
        } catch (Exception e) {
            throw KapuaExceptionHandler.buildExceptionFromError(e);
        }
    }
}
