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
package org.eclipse.kapua.app.console.module.authorization.client.group;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.google.gwt.core.client.GWT;

import org.eclipse.kapua.app.console.module.api.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.module.api.client.ui.grid.CreatedByNameCellRenderer;
import org.eclipse.kapua.app.console.module.api.client.ui.grid.EntityGrid;
import org.eclipse.kapua.app.console.module.api.client.ui.view.AbstractEntityView;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.KapuaPagingToolbarMessages;
import org.eclipse.kapua.app.console.module.api.shared.model.query.GwtQuery;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.client.messages.ConsoleGroupMessages;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupQuery;

import java.util.ArrayList;
import java.util.List;

public class GroupGrid extends EntityGrid<GwtGroup> {

    private static final String ACCESS_GROUP = "access group";

    private static final ConsoleGroupMessages MSGS = GWT.create(ConsoleGroupMessages.class);
    private static final ConsoleMessages C_MSGS = GWT.create(ConsoleMessages.class);
    private EntityGroupDataProvider entityGroupDataProvider;
    private GwtGroupQuery query;
    private GroupToolbarGrid toolbar;

    public GroupGrid(AbstractEntityView<GwtGroup> entityView, EntityGroupDataProvider entityGroupDataProvider, GwtSession currentSession) {
        super(entityView, currentSession);

        setEntityGroupDataProvider(entityGroupDataProvider);

        query = new GwtGroupQuery();
        query.setScopeId(currentSession.getSelectedAccountId());
    }

    @Override
    protected RpcProxy<PagingLoadResult<GwtGroup>> getDataProxy() {
        return entityGroupDataProvider.getEntityGridDataProxy(query);
    }

    @Override
    protected List<ColumnConfig> getColumns() {
        List<ColumnConfig> columnConfigs = new ArrayList<ColumnConfig>();

        ColumnConfig columnConfig = new ColumnConfig("id", MSGS.gridGroupColumnHeaderId(), 100);
        columnConfig.setHidden(true);
        columnConfig.setSortable(false);
        columnConfigs.add(columnConfig);

        columnConfig = new ColumnConfig("groupName", MSGS.gridGroupColumnHeaderGroupName(), 200);
        columnConfigs.add(columnConfig);

        columnConfig = new ColumnConfig("description", MSGS.gridGroupColumnHeaderDescription(), 200);
        columnConfigs.add(columnConfig);

        columnConfig = new ColumnConfig("domain", "Domain", 200);
        columnConfigs.add(columnConfig);

        columnConfig = new ColumnConfig("createdOnFormatted", MSGS.gridGroupColumnHeaderCreatedOn(), 200);
        columnConfigs.add(columnConfig);

        columnConfig = new ColumnConfig("createdByName", MSGS.gridGroupColumnHeaderCreatedBy(), 200);
        columnConfig.setRenderer(new CreatedByNameCellRenderer<GwtGroup>());
        columnConfig.setSortable(false);
        columnConfigs.add(columnConfig);

        return columnConfigs;
    }

    @Override
    public GwtQuery getFilterQuery() {
        return query;
    }

    @Override
    public void setFilterQuery(GwtQuery filterQuery) {
        this.query = (GwtGroupQuery) filterQuery;
    }

    @Override
    protected GroupToolbarGrid getToolbar() {
        if (toolbar == null) {
            toolbar = new GroupToolbarGrid(currentSession, entityGroupDataProvider);
        }

        return toolbar;
    }

    @Override
    public String getEmptyGridText() {
        return C_MSGS.gridNoResultFound(ACCESS_GROUP);
    }

    @Override
    protected KapuaPagingToolbarMessages getKapuaPagingToolbarMessages() {
        return new KapuaPagingToolbarMessages() {

            @Override
            public String pagingToolbarShowingPost() {
                return C_MSGS.specificPagingToolbarShowingPost(ACCESS_GROUP);
            }

            @Override
            public String pagingToolbarNoResult() {
                return C_MSGS.specificPagingToolbarNoResult(ACCESS_GROUP);
            }
        };
    }

    protected void setEntityGroupDataProvider(EntityGroupDataProvider entityGroupDataProvider) {
        this.entityGroupDataProvider = entityGroupDataProvider;

        getToolbar().setEntityGroupDataProvider(entityGroupDataProvider);
    }
}
