/*******************************************************************************
 * Copyright (c) 2018, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.device.client.device.group;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.eclipse.kapua.app.console.module.api.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.module.api.client.ui.grid.EntityGrid;
import org.eclipse.kapua.app.console.module.api.client.ui.view.AbstractEntityView;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.EntityCRUDToolbar;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.KapuaPagingToolbarMessages;
import org.eclipse.kapua.app.console.module.api.shared.model.query.GwtQuery;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.client.messages.ConsoleGroupMessages;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceQuery;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceQueryPredicates;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceService;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceServiceAsync;

import java.util.ArrayList;
import java.util.List;

public class GroupSubjectGrid extends EntityGrid<GwtDevice> {

    private GwtGroup selectedGroup;
    private static final GwtDeviceServiceAsync DEVICE_SERVICE = GWT.create(GwtDeviceService.class);
    private static final ConsoleGroupMessages MSGS = GWT.create(ConsoleGroupMessages.class);
    private static final ConsoleMessages C_MSGS = GWT.create(ConsoleMessages.class);
    private static final String ASSIGNED_DEVICE = "assigned device";
    private GwtDeviceQuery query;

    GroupSubjectGrid(AbstractEntityView<GwtDevice> entityView, GwtSession currentSession) {
        super(entityView, currentSession);
        query = new GwtDeviceQuery();
        query.setScopeId(currentSession.getSelectedAccountId());

    }

    @Override
    public String getEmptyGridText() {
        return C_MSGS.gridNoResultAvailable(ASSIGNED_DEVICE);
    }

    @Override
    protected KapuaPagingToolbarMessages getKapuaPagingToolbarMessages() {
        return new KapuaPagingToolbarMessages() {

            @Override
            public String pagingToolbarShowingPost() {
                return C_MSGS.specificPagingToolbarShowingPost(ASSIGNED_DEVICE);
            }

            @Override
            public String pagingToolbarNoResult() {
                return C_MSGS.specificPagingToolbarNoResult(ASSIGNED_DEVICE);
            }
        };
    }

    @Override
    protected RpcProxy<PagingLoadResult<GwtDevice>> getDataProxy() {
        return new RpcProxy<PagingLoadResult<GwtDevice>>() {

            @Override
            protected void load(Object loadConfig,
                    AsyncCallback<PagingLoadResult<GwtDevice>> callback) {
                if (selectedGroup != null) {
                    DEVICE_SERVICE.query((PagingLoadConfig) loadConfig, query, callback);
                } else {
                    callback.onSuccess(new BasePagingLoadResult<GwtDevice>(new ArrayList<GwtDevice>(), 0, 0));
                }
            }

        };
    }

    @Override
    protected List<ColumnConfig> getColumns() {
        List<ColumnConfig> columnConfigs = new ArrayList<ColumnConfig>();

        ColumnConfig columnConfig = new ColumnConfig("id", MSGS.gridGroupSubjectColumnHeaderId(), 100);
        columnConfig.setHidden(true);
        columnConfig.setSortable(false);
        columnConfigs.add(columnConfig);

        columnConfig = new ColumnConfig("clientId", MSGS.gridGroupSubjectColumnHeaderClientId(), 50);
        columnConfig.setSortable(true);
        columnConfigs.add(columnConfig);

        columnConfig = new ColumnConfig("displayName", MSGS.gridGroupSubjectColumnHeaderDisplayName(), 50);
        columnConfig.setSortable(true);
        columnConfigs.add(columnConfig);

        return columnConfigs;
    }

    @Override
    public GwtQuery getFilterQuery() {
        return query;
    }

    @Override
    public void setFilterQuery(GwtQuery filterQuery) {
        this.query = (GwtDeviceQuery) filterQuery;

    }

    @Override
    protected EntityCRUDToolbar<GwtDevice> getToolbar() {
        EntityCRUDToolbar<GwtDevice> toolbar = super.getToolbar();
        toolbar.setRefreshButtonVisible(true);
        toolbar.setAddButtonVisible(false);
        toolbar.setEditButtonVisible(false);
        toolbar.setDeleteButtonVisible(false);
        toolbar.setFilterButtonVisible(false);
        toolbar.setBorders(true);

        return toolbar;
    }

    public void setEntity(GwtGroup gwtGroup) {
        if (gwtGroup != null) {
            selectedGroup = gwtGroup;

            List<String> groupIdsString = new ArrayList<String>(1);
            groupIdsString.add(selectedGroup.getId());

            GwtDeviceQueryPredicates predicates = new GwtDeviceQueryPredicates();
            predicates.setGroupIds(groupIdsString);
            predicates.setGroupDevice("ANY");
            query.setPredicates(predicates);
        }
        refresh();
        entityPagingToolbar.enable();
    }

    @Override
    public void refresh() {
        if (super.rendered) {
            super.refresh();
        }
    }

}
