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
package org.eclipse.kapua.app.console.module.device.server;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.device.shared.model.management.registry.GwtDeviceManagementOperation;
import org.eclipse.kapua.app.console.module.device.shared.model.management.registry.GwtDeviceManagementOperationQuery;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceManagementOperationService;
import org.eclipse.kapua.app.console.module.device.shared.util.GwtKapuaDeviceModelConverter;
import org.eclipse.kapua.app.console.module.device.shared.util.KapuaGwtDeviceModelConverter;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperation;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationListResult;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationQuery;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationRegistryService;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotification;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotificationAttributes;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotificationFactory;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotificationListResult;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotificationQuery;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotificationService;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.common.base.Strings;

/**
 * The server side implementation of the RPC service.
 */
public class GwtDeviceManagementOperationServiceImpl extends KapuaRemoteServiceServlet implements GwtDeviceManagementOperationService {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final DeviceManagementOperationRegistryService DEVICE_MANAGEMENT_OPERATION_REGISTRY_SERVICE = LOCATOR.getService(DeviceManagementOperationRegistryService.class);

    private static final ManagementOperationNotificationService MANAGEMENT_OPERATION_NOTIFICATION_SERVICE = LOCATOR.getService(ManagementOperationNotificationService.class);
    private static final ManagementOperationNotificationFactory MANAGEMENT_OPERATION_NOTIFICATION_FACTORY = LOCATOR.getFactory(ManagementOperationNotificationFactory.class);
    private static final long serialVersionUID = 958857278895524016L;

    @Override
    public PagingLoadResult<GwtDeviceManagementOperation> query(PagingLoadConfig loadConfig, GwtDeviceManagementOperationQuery gwtQuery) throws GwtKapuaException {

        int totalLength = 0;
        List<GwtDeviceManagementOperation> gwtDeviceManagementOperations = new ArrayList<GwtDeviceManagementOperation>();
        try {
            DeviceManagementOperationQuery query = GwtKapuaDeviceModelConverter.convertDeviceManagementOperationQuery(loadConfig, gwtQuery);

            DeviceManagementOperationListResult managementOperations = DEVICE_MANAGEMENT_OPERATION_REGISTRY_SERVICE.query(query);
            totalLength = managementOperations.getTotalCount().intValue();

            for (DeviceManagementOperation dmo : managementOperations.getItems()) {
                GwtDeviceManagementOperation gwtDmo = KapuaGwtDeviceModelConverter.convertManagementOperation(dmo);

                if (dmo.getEndedOn() == null) {
                    //TODO: #LAYER_VIOLATION - entity lookup should be done elsewhere
                    ManagementOperationNotificationQuery notificationQuery = new ManagementOperationNotificationQuery(dmo.getScopeId());
                    notificationQuery.setPredicate(notificationQuery.attributePredicate(ManagementOperationNotificationAttributes.OPERATION_ID, dmo.getId()));
                    notificationQuery.setSortCriteria(notificationQuery.fieldSortCriteria(ManagementOperationNotificationAttributes.SENT_ON, SortOrder.ASCENDING));

                    ManagementOperationNotificationListResult notifications = MANAGEMENT_OPERATION_NOTIFICATION_SERVICE.query(notificationQuery);

                    StringBuilder logSb = new StringBuilder();
                    for (ManagementOperationNotification mon : notifications.getItems()) {
                        if (!Strings.isNullOrEmpty(mon.getMessage())) {
                            logSb.append(mon.getSentOn()).append(" - ").append(mon.getMessage()).append("\n");
                        }
                    }

                    gwtDmo.setLog(logSb.toString());
                }

                gwtDeviceManagementOperations.add(gwtDmo);
            }

        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return new BasePagingLoadResult<GwtDeviceManagementOperation>(gwtDeviceManagementOperations, loadConfig.getOffset(), totalLength);
    }
}
