/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.device.client.device;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.IconSet;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.KapuaIcon;
import org.eclipse.kapua.app.console.module.api.client.ui.color.Color;
import org.eclipse.kapua.app.console.module.api.client.ui.grid.EntityGrid;
import org.eclipse.kapua.app.console.module.api.client.ui.view.AbstractEntityView;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.EntityCRUDToolbar;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.KapuaPagingToolbarMessages;
import org.eclipse.kapua.app.console.module.api.shared.model.query.GwtQuery;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.device.client.messages.ConsoleConnectionMessages;
import org.eclipse.kapua.app.console.module.device.client.messages.ConsoleDeviceMessages;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceQuery;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceQueryPredicates;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceService;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceServiceAsync;

import java.util.ArrayList;
import java.util.List;

public class DeviceGrid extends EntityGrid<GwtDevice> {

    private GwtDeviceQuery query;
    private DeviceGridToolbar toolbar;

    private static final GwtDeviceServiceAsync GWT_DEVICE_SERVICE = GWT.create(GwtDeviceService.class);

    private static final ConsoleDeviceMessages DEVICE_MSGS = GWT.create(ConsoleDeviceMessages.class);
    private static final ConsoleConnectionMessages CONNECTION_MSGS = GWT.create(ConsoleConnectionMessages.class);
    private static final ConsoleMessages MSGS = GWT.create(ConsoleMessages.class);
    private static final String DEVICE = "device";

    public DeviceGrid(AbstractEntityView<GwtDevice> entityView, GwtSession currentSession) {
        super(entityView, currentSession);
        query = new GwtDeviceQuery();
        query.setScopeId(currentSession.getSelectedAccountId());
        query.setPredicates(new GwtDeviceQueryPredicates());
    }

    @Override
    protected RpcProxy<PagingLoadResult<GwtDevice>> getDataProxy() {
        return new RpcProxy<PagingLoadResult<GwtDevice>>() {

            @Override
            protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<GwtDevice>> callback) {
                GWT_DEVICE_SERVICE.query((PagingLoadConfig) loadConfig,
                        query,
                        callback);
            }
        };
    }

    @Override
    public String getEmptyGridText() {
        return MSGS.gridNoResultFound(DEVICE);
    }

    @Override
    protected KapuaPagingToolbarMessages getKapuaPagingToolbarMessages() {
        return new KapuaPagingToolbarMessages() {

            @Override
            public String pagingToolbarShowingPost() {
                return MSGS.specificPagingToolbarShowingPost(DEVICE);
            }

            @Override
            public String pagingToolbarNoResult() {
                return MSGS.specificPagingToolbarNoResult(DEVICE);
            }
        };
    }

    @Override
    protected List<ColumnConfig> getColumns() {
        // Column Configuration
        List<ColumnConfig> columnConfigs = new ArrayList<ColumnConfig>();

        ColumnConfig column = new ColumnConfig("status", DEVICE_MSGS.deviceTableStatus(), 50);
        column.setAlignment(HorizontalAlignment.CENTER);
        GridCellRenderer<GwtDevice> setStatusIcon = new GridCellRenderer<GwtDevice>() {

            @Override
            public String render(GwtDevice gwtDevice, String property, ColumnData config, int rowIndex, int colIndex, ListStore<GwtDevice> deviceList, Grid<GwtDevice> grid) {

                KapuaIcon icon;
                if (gwtDevice.getGwtDeviceConnectionStatusEnum() != null) {
                    switch (gwtDevice.getGwtDeviceConnectionStatusEnum()) {
                        case CONNECTED:
                            icon = new KapuaIcon(IconSet.PLUG);
                            icon.setColor(Color.GREEN);
                            icon.setTitle(CONNECTION_MSGS.connected());
                            break;
                        case DISCONNECTED:
                            icon = new KapuaIcon(IconSet.PLUG);
                            icon.setColor(Color.YELLOW);
                            icon.setTitle(CONNECTION_MSGS.disconnected());
                            break;
                        case MISSING:
                            icon = new KapuaIcon(IconSet.PLUG);
                            icon.setColor(Color.RED);
                            icon.setTitle(CONNECTION_MSGS.missing());
                            break;
                        case UNKNOWN:
                        case ANY:
                        default:
                            icon = new KapuaIcon(IconSet.PLUG);
                            icon.setColor(Color.GREY);
                            icon.setTitle(CONNECTION_MSGS.unknown());
                            break;
                    }
                } else {
                    icon = new KapuaIcon(IconSet.PLUG);
                    icon.setColor(Color.GREY);
                    icon.setTitle(CONNECTION_MSGS.unknown());
                }

                return icon.getInlineHTML();
            }
        };
        column.setRenderer(setStatusIcon);
        column.setAlignment(HorizontalAlignment.CENTER);
        column.setSortable(false);
        columnConfigs.add(column);

        column = new ColumnConfig("clientId", DEVICE_MSGS.deviceTableClientID(), 175);
        columnConfigs.add(column);

        column = new ColumnConfig("displayName", DEVICE_MSGS.deviceTableDisplayName(), 150);
        column.setSortable(true);
        columnConfigs.add(column);

        column = new ColumnConfig("lastEventOnFormatted", DEVICE_MSGS.deviceTableLastReportedDate(), 130);
        column.setSortable(true);
        column.setAlignment(HorizontalAlignment.CENTER);
        columnConfigs.add(column);

        column = new ColumnConfig("lastEventType", DEVICE_MSGS.deviceTableLastEventType(), 130);
        column.setSortable(false);
        column.setAlignment(HorizontalAlignment.CENTER);
        columnConfigs.add(column);

        column = new ColumnConfig("serialNumber", DEVICE_MSGS.deviceTableSerialNumber(), 100);
        column.setSortable(false);
        column.setHidden(true);
        columnConfigs.add(column);

        column = new ColumnConfig("applicationIdentifiers", DEVICE_MSGS.deviceTableApplications(), 100);
        column.setSortable(false);
        column.setHidden(false);
        columnConfigs.add(column);

        column = new ColumnConfig("iotFrameworkVersion", DEVICE_MSGS.deviceTableEsfKuraVersion(), 80);
        column.setSortable(false);
        column.setAlignment(HorizontalAlignment.CENTER);
        columnConfigs.add(column);

        column = new ColumnConfig("customAttribute1", DEVICE_MSGS.deviceTableCustomAttribute1(), 100);
        column.setSortable(false);
        column.setHidden(true);
        columnConfigs.add(column);

        column = new ColumnConfig("customAttribute2", DEVICE_MSGS.deviceTableCustomAttribute2(), 100);
        column.setSortable(false);
        column.setHidden(true);
        columnConfigs.add(column);

        column = new ColumnConfig("clientIp", DEVICE_MSGS.deviceTableRemoteIpAddress(), 100);
        column.setSortable(false);
        column.setHidden(true);
        columnConfigs.add(column);

        column = new ColumnConfig("modelId", DEVICE_MSGS.deviceTableModelId(), 100);
        column.setSortable(false);
        column.setHidden(true);
        columnConfigs.add(column);

        column = new ColumnConfig("firmwareVersion", DEVICE_MSGS.deviceTableFirmwareVersion(), 100);
        column.setSortable(false);
        column.setHidden(true);
        columnConfigs.add(column);

        column = new ColumnConfig("biosVersion", DEVICE_MSGS.deviceTableBiosVersion(), 100);
        column.setSortable(false);
        column.setHidden(true);
        columnConfigs.add(column);

        column = new ColumnConfig("osVersion", DEVICE_MSGS.deviceTableOsVersion(), 100);
        column.setSortable(false);
        column.setHidden(true);
        columnConfigs.add(column);

        column = new ColumnConfig("jvmVersion", DEVICE_MSGS.deviceTableJvmVersion(), 100);
        column.setSortable(false);
        column.setHidden(true);
        columnConfigs.add(column);

        column = new ColumnConfig("osgiFrameworkVersion", DEVICE_MSGS.deviceTableOsgiVersion(), 100);
        column.setSortable(false);
        column.setHidden(true);
        columnConfigs.add(column);

        column = new ColumnConfig("imei", DEVICE_MSGS.deviceTableImei(), 100);
        column.setSortable(false);
        column.setHidden(true);
        columnConfigs.add(column);

        column = new ColumnConfig("imsi", DEVICE_MSGS.deviceTableImsi(), 100);
        column.setSortable(false);
        column.setHidden(true);
        columnConfigs.add(column);

        column = new ColumnConfig("iccid", DEVICE_MSGS.deviceTableIccid(), 100);
        column.setSortable(false);
        column.setHidden(true);
        columnConfigs.add(column);

        columnConfigs.add(column);

        return columnConfigs;
    }

    @Override
    public GwtQuery getFilterQuery() {
        return query;
    }

    @Override
    public void setFilterQuery(GwtQuery filterQuery) {
        query = (GwtDeviceQuery) filterQuery;
    }

    @Override
    protected EntityCRUDToolbar<GwtDevice> getToolbar() {
        if (toolbar == null) {
            toolbar = new DeviceGridToolbar(currentSession);
        }
        return toolbar;
    }

    private class DeviceLoadListener extends LoadListener {
        @Override
        public void loaderLoad(LoadEvent le) {
            super.loaderLoad(le);
            toolbar.setExportEnabled(((BasePagingLoadResult) le.getData()).getTotalLength() != 0);
        }
    }
}
