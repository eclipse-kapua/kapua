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
package org.eclipse.kapua.app.console.module.api.client.ui.tab;

import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import org.eclipse.kapua.app.console.module.api.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.IconSet;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.KapuaIcon;
import org.eclipse.kapua.app.console.module.api.client.ui.grid.KapuaGrid;
import org.eclipse.kapua.app.console.module.api.client.ui.panel.ContentPanel;
import org.eclipse.kapua.app.console.module.api.client.util.DateUtils;
import org.eclipse.kapua.app.console.module.api.client.util.KapuaLoadListener;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtEntityModel;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtGroupedNVPair;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class EntityDescriptionTabItem<M extends GwtEntityModel> extends KapuaTabItem<M> {

    protected static final ConsoleMessages MSGS = GWT.create(ConsoleMessages.class);

    private ContentPanel contentPanel;
    private ToolBar toolbar;
    private Grid<GwtGroupedNVPair> descriptionGrid;
    private GroupingStore<GwtGroupedNVPair> descriptionValuesStore;
    private BaseListLoader<ListLoadResult<GwtGroupedNVPair>> descriptionValuesLoader;
    private GroupingView groupingView;

    public EntityDescriptionTabItem(GwtSession currentSession) {
        super(currentSession, MSGS.entityTabDescriptionTitle(), new KapuaIcon(IconSet.INFO));

        groupingView = new GroupingView();
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);

        contentPanel = new ContentPanel();
        contentPanel.setBodyBorder(true);
        contentPanel.setBorders(false);
        contentPanel.setHeaderVisible(false);
        contentPanel.setLayout(new FitLayout());
        // Container borders
        setBorders(false);

        RpcProxy<ListLoadResult<GwtGroupedNVPair>> proxy = getDataProxy();
        descriptionValuesLoader = new BaseListLoader<ListLoadResult<GwtGroupedNVPair>>(proxy);
        descriptionValuesLoader.addLoadListener(new DescriptionLoadListener());

        descriptionValuesStore = new GroupingStore<GwtGroupedNVPair>(descriptionValuesLoader);
        descriptionValuesStore.groupBy("groupLoc");
        // Columns
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

        // Name
        ColumnConfig nameColumn = new ColumnConfig();
        nameColumn.setId("nameLoc");
        nameColumn.setHeader(MSGS.entityTabDescriptionName());
        nameColumn.setWidth(50);
        columns.add(nameColumn);

        // Value
        GridCellRenderer<GwtGroupedNVPair> valueColumnRenderer = new GridCellRenderer<GwtGroupedNVPair>() {

            @Override
            public Object render(GwtGroupedNVPair model, String property, ColumnData config,
                                 int rowIndex, int colIndex, ListStore<GwtGroupedNVPair> store,
                                 Grid<GwtGroupedNVPair> grid) {
                return renderValueCell(model, property, config, rowIndex, colIndex, store, grid);
            }
        };

        ColumnConfig valueColumn = new ColumnConfig();
        valueColumn.setId("value");
        valueColumn.setHeader(MSGS.devicePropValue());
        valueColumn.setWidth(50);
        valueColumn.setRenderer(valueColumnRenderer);
        columns.add(valueColumn);
        // Grid
        groupingView.setShowGroupedColumn(false);
        groupingView.setForceFit(true);
        groupingView.setAutoFill(true);
        groupingView.setSortingEnabled(false);
        groupingView.setShowGroupedColumn(false);
        groupingView.setEnableNoGroups(false);
        groupingView.setEnableGroupingMenu(false);

        ColumnModel cm = new ColumnModel(columns);
        descriptionGrid = new KapuaGrid<GwtGroupedNVPair>(descriptionValuesStore, cm);
        descriptionGrid.setView(groupingView);
        descriptionGrid.setBorders(false);

        this.setStyleAttribute("border-top-width: 0px", "!important;");

        toolbar = getToolbar();
        if (toolbar != null) {
            contentPanel.setTopComponent(toolbar);
        }
        contentPanel.add(descriptionGrid);

        add(contentPanel);

        setGroupViewText(getGroupViewText());
    }

    @Override
    public void setEntity(M t) {
        super.setEntity(t);

    }

    protected void setGroupViewText(String message) {
        groupingView.setEmptyText(message);
    }

    protected String getGroupViewText() {
        return MSGS.entityTabDescriptionNoSelection();
    }

    protected abstract RpcProxy<ListLoadResult<GwtGroupedNVPair>> getDataProxy();

    protected Object renderNameCell(GwtGroupedNVPair model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<GwtGroupedNVPair> store, Grid<GwtGroupedNVPair> grid) {
        return model.getName();
    }

    protected Object renderValueCell(GwtGroupedNVPair model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<GwtGroupedNVPair> store, Grid<GwtGroupedNVPair> grid) {
        Object value = model.getValue();

        if (value instanceof Date) {
            Date dateValue = (Date) value;
            return DateUtils.formatDateTime(dateValue);
        } else if (value instanceof String) {
            return ((String) value)
                    .replace("\n", "<br>")
                    .replace(" ", "&nbsp;");
        }
        return value;
    }

    @Override
    protected void doRefresh() {
        if (selectedEntity != null) {
            descriptionValuesLoader.load();
        } else {
            descriptionValuesStore.removeAll();
        }
    }

    private class DescriptionLoadListener extends KapuaLoadListener {

        @Override
        public void loaderLoadException(LoadEvent le) {
            super.loaderLoadException(le);
            descriptionGrid.unmask();
        }
    }

    protected ToolBar getToolbar() {
        return null;
    }
}
