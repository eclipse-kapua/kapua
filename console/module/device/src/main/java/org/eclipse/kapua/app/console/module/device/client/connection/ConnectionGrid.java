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
package org.eclipse.kapua.app.console.module.device.client.connection;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
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
import org.eclipse.kapua.app.console.module.device.client.connection.toolbar.ConnectionGridToolbar;
import org.eclipse.kapua.app.console.module.device.client.messages.ConsoleConnectionMessages;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnection;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnectionQuery;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceConnectionService;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceConnectionServiceAsync;

import java.util.ArrayList;
import java.util.List;

public class ConnectionGrid extends EntityGrid<GwtDeviceConnection> {

    private static final ConsoleConnectionMessages CONNECTION_MSGS = GWT.create(ConsoleConnectionMessages.class);
    private static final ConsoleMessages MSGS = GWT.create(ConsoleMessages.class);

    private final GwtDeviceConnectionServiceAsync gwtDeviceConnectionService = GWT.create(GwtDeviceConnectionService.class);
    private static final String CONNECTION = "connection";

    private GwtDeviceConnectionQuery filterQuery;
    private ConnectionGridToolbar toolbar;

    ConnectionGrid(AbstractEntityView<GwtDeviceConnection> entityView, GwtSession currentSession) {
        super(entityView, currentSession);
        filterQuery = new GwtDeviceConnectionQuery();
        filterQuery.setScopeId(currentSession.getSelectedAccountId());
    }

    @Override
    protected EntityCRUDToolbar<GwtDeviceConnection> getToolbar() {
        if (toolbar == null) {
            toolbar = new ConnectionGridToolbar(currentSession);
        }
        return toolbar;
    }

    @Override
    protected RpcProxy<PagingLoadResult<GwtDeviceConnection>> getDataProxy() {
        return new RpcProxy<PagingLoadResult<GwtDeviceConnection>>() {

            @Override
            protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<GwtDeviceConnection>> callback) {
                gwtDeviceConnectionService.query((PagingLoadConfig) loadConfig, filterQuery, callback);
            }
        };
    }

    @Override
    protected List<ColumnConfig> getColumns() {
        ColumnConfig column = null;
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        column = new ColumnConfig("connectionStatus", CONNECTION_MSGS.connectionTableStatus(), 50);
        column.setAlignment(HorizontalAlignment.CENTER);
        GridCellRenderer<GwtDeviceConnection> setStatusIcon = new GridCellRenderer<GwtDeviceConnection>() {

            @Override
            public String render(GwtDeviceConnection gwtDeviceConnection, String property, ColumnData config, int rowIndex, int colIndex, ListStore<GwtDeviceConnection> deviceList,
                                 Grid<GwtDeviceConnection> grid) {

                KapuaIcon icon;
                if (gwtDeviceConnection.getConnectionStatusEnum() != null) {
                    switch (gwtDeviceConnection.getConnectionStatusEnum()) {
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
        configs.add(column);

        column = new ColumnConfig("clientId", 120);
        column.setHeader(CONNECTION_MSGS.connectionTableClientId());
        column.setWidth(150);
        configs.add(column);

        column = new ColumnConfig("userName", 120);
        column.setHeader(CONNECTION_MSGS.connectionTableUserName());
        column.setSortable(false);
        column.setWidth(150);
        configs.add(column);

        column = new ColumnConfig("protocol", 120);
        column.setHeader(CONNECTION_MSGS.connectionTableProtocol());
        column.setWidth(150);
        configs.add(column);

        column = new ColumnConfig("connectionUserCouplingMode", 120);
        column.setHeader(CONNECTION_MSGS.connectionTableUserCouplingMode());
        column.setWidth(150);
        configs.add(column);

        column = new ColumnConfig("reservedUserName", 120);
        column.setHeader(CONNECTION_MSGS.connectionTableReservedUserName());
        column.setWidth(150);
        column.setSortable(false);
        configs.add(column);

        column = new ColumnConfig("authenticationType", 120);
        column.setHeader("Authentication Type");
        column.setSortable(false);
        configs.add(column);

        column = new ColumnConfig("clientIp", 120);
        column.setHeader(CONNECTION_MSGS.connectionTableClientIp());
        column.setWidth(150);
        configs.add(column);

        column = new ColumnConfig("modifiedOnFormatted", CONNECTION_MSGS.connectionTableModifiedOn(), 130);
        configs.add(column);
        return configs;
    }

    @Override
    public String getEmptyGridText() {
        return MSGS.gridNoResultFound(CONNECTION);
    }

    @Override
    protected KapuaPagingToolbarMessages getKapuaPagingToolbarMessages() {
        return new KapuaPagingToolbarMessages() {

            @Override
            public String pagingToolbarShowingPost() {
                return MSGS.specificPagingToolbarShowingPost(CONNECTION);
            }

            @Override
            public String pagingToolbarNoResult() {
                return MSGS.specificPagingToolbarNoResult(CONNECTION);
            }
        };
    }

    @Override
    public GwtQuery getFilterQuery() {
        return filterQuery;
    }

    @Override
    public void setFilterQuery(GwtQuery filterQuery) {
        this.filterQuery = (GwtDeviceConnectionQuery) filterQuery;
    }
}
