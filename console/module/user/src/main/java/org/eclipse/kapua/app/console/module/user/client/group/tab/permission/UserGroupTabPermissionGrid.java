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
package org.eclipse.kapua.app.console.module.user.client.group.tab.permission;

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
import org.eclipse.kapua.app.console.module.api.client.ui.grid.CreatedByNameCellRenderer;
import org.eclipse.kapua.app.console.module.api.client.ui.grid.EntityGrid;
import org.eclipse.kapua.app.console.module.api.client.ui.view.AbstractEntityView;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.EntityCRUDToolbar;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.KapuaPagingToolbarMessages;
import org.eclipse.kapua.app.console.module.api.shared.model.query.GwtQuery;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.client.messages.ConsolePermissionMessages;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupPermission;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupPermissionQuery;
import org.eclipse.kapua.app.console.module.user.shared.model.permission.UserGroupSessionPermission;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserGroupService;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserGroupServiceAsync;

import java.util.ArrayList;
import java.util.List;

public class UserGroupTabPermissionGrid extends EntityGrid<GwtGroupPermission> {

    private static final GwtUserGroupServiceAsync GWT_USER_GROUP_SERVICE = GWT.create(GwtUserGroupService.class);

    private static final ConsolePermissionMessages PERMISSION_MSGS = GWT.create(ConsolePermissionMessages.class);
    private static final ConsoleMessages COMMONS_MSGS = GWT.create(ConsoleMessages.class);
    private static final String PERMISSION = "permission";

    private GwtGroup gwtUserGroup;

    private UserGroupTabPermissionToolbar toolbar;

    public UserGroupTabPermissionGrid(AbstractEntityView<GwtGroupPermission> entityView, GwtSession currentSession) {
        super(entityView, currentSession);
    }

    @Override
    protected RpcProxy<PagingLoadResult<GwtGroupPermission>> getDataProxy() {
        return new RpcProxy<PagingLoadResult<GwtGroupPermission>>() {

            @Override
            protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<GwtGroupPermission>> callback) {
                GwtGroupPermissionQuery gwtGroupPermissionQuery = new GwtGroupPermissionQuery();
                gwtGroupPermissionQuery.setScopeId(gwtUserGroup.getScopeId());
                gwtGroupPermissionQuery.setGroupId(gwtUserGroup.getId());

                GWT_USER_GROUP_SERVICE.queryPermission((PagingLoadConfig) loadConfig,
                        gwtGroupPermissionQuery,
                        callback);
            }
        };
    }

    @Override
    protected List<ColumnConfig> getColumns() {

        List<ColumnConfig> columnConfigs = new ArrayList<ColumnConfig>();

        ColumnConfig columnConfig = new ColumnConfig("id", PERMISSION_MSGS.gridAccessRoleColumnHeaderId(), 100);
        columnConfig.setSortable(false);
        columnConfig.setHidden(true);
        columnConfigs.add(columnConfig);

        columnConfig = new ColumnConfig("permissionDomain", PERMISSION_MSGS.gridAccessRoleColumnHeaderDomain(), 200);
        columnConfig.setSortable(false);
        columnConfigs.add(columnConfig);

        columnConfig = new ColumnConfig("permissionAction", PERMISSION_MSGS.gridAccessRoleColumnHeaderAction(), 200);
        columnConfig.setSortable(false);
        columnConfigs.add(columnConfig);

        columnConfig = new ColumnConfig("permissionTargetScopeIdByName", PERMISSION_MSGS.gridRolePermissionColumnHeaderTargetScopeId(), 200);
        columnConfig.setSortable(false);
        columnConfigs.add(columnConfig);

        if (currentSession.hasPermission(UserGroupSessionPermission.read())) {
            columnConfig = new ColumnConfig("groupName", PERMISSION_MSGS.gridAccessRoleColumnHeaderGroupName(), 200);
            columnConfig.setSortable(false);
            columnConfigs.add(columnConfig);
        }

        columnConfig = new ColumnConfig("permissionForwardable", PERMISSION_MSGS.gridAccessRoleColumnHeaderForwardable(), 200);
        columnConfig.setRenderer(new GridCellRenderer<GwtGroupPermission>() {

            @Override
            public Object render(GwtGroupPermission model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<GwtGroupPermission> store, Grid<GwtGroupPermission> grid) {
                return model.getPermissionForwardable() ? COMMONS_MSGS.yes() : COMMONS_MSGS.no();
            }
        });
        columnConfig.setSortable(false);
        columnConfigs.add(columnConfig);

        columnConfig = new ColumnConfig("createdOnFormatted", PERMISSION_MSGS.gridAccessRoleColumnHeaderCreatedOn(), 200);
        columnConfigs.add(columnConfig);

        columnConfig = new ColumnConfig("createdByName", PERMISSION_MSGS.gridAccessRoleColumnHeaderCreatedBy(), 200);
        columnConfig.setRenderer(new CreatedByNameCellRenderer<GwtGroupPermission>());
        columnConfig.setSortable(false);
        columnConfigs.add(columnConfig);

        return columnConfigs;
    }

    public GwtGroup getUserGroup() {
        return gwtUserGroup;
    }

    public void setUserGroup(GwtGroup gwtUserGroup) {
        this.gwtUserGroup = gwtUserGroup;

        toolbar.setGwtUserGroup(gwtUserGroup);
    }

    @Override
    public GwtQuery getFilterQuery() {
        return null;
    }

    @Override
    public void setFilterQuery(GwtQuery filterQuery) {
    }

    @Override
    public EntityCRUDToolbar<GwtGroupPermission> getToolbar() {
        if (toolbar == null) {
            toolbar = new UserGroupTabPermissionToolbar(currentSession);
            toolbar.setEditButtonVisible(false);
            toolbar.setBorders(true);
        }
        return toolbar;
    }

//    @Override
//    protected void onRender(Element target, int index) {
//        super.onRender(target, index);
//        /* Despite this grid, being a "slave" grid (i.e. a grid that depends on the value
//         * selected in another grid) and so not refreshed on render (see comment in
//         * EntityGrid class), it should be refreshed anyway on render if no item is
//         * selected on the master grid, otherwise the paging toolbar will still be enabled
//         * even if no results are actually available in this grid */
//        if (gwtUserGroup == null) {
//            refresh();
//        }
//    }

    @Override
    public String getEmptyGridText() {
        return COMMONS_MSGS.gridNoResultAvailable(PERMISSION);
    }

    @Override
    protected KapuaPagingToolbarMessages getKapuaPagingToolbarMessages() {
        return new KapuaPagingToolbarMessages() {

            @Override
            public String pagingToolbarShowingPost() {
                return COMMONS_MSGS.specificPagingToolbarShowingPost(PERMISSION);
            }

            @Override
            public String pagingToolbarNoResult() {
                return COMMONS_MSGS.specificPagingToolbarNoResult(PERMISSION);
            }
        };
    }
}
