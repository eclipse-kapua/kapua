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
package org.eclipse.kapua.app.console.module.api.client.ui.grid;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import org.eclipse.kapua.app.console.module.api.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.module.api.client.ui.panel.ContentPanel;
import org.eclipse.kapua.app.console.module.api.client.ui.panel.EntityFilterPanel;
import org.eclipse.kapua.app.console.module.api.client.ui.view.AbstractEntityView;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.EntityCRUDToolbar;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.KapuaPagingToolBar;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.KapuaPagingToolbarMessages;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtEntityModel;
import org.eclipse.kapua.app.console.module.api.shared.model.query.GwtQuery;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;

import java.util.List;

public abstract class EntityGrid<M extends GwtEntityModel> extends ContentPanel {

    private static final ConsoleMessages MSGS = GWT.create(ConsoleMessages.class);

    private static final int ENTITY_PAGE_SIZE = 100;

    protected GwtSession currentSession;
    private AbstractEntityView<M> parentEntityView;

    protected EntityCRUDToolbar<M> entityCRUDToolbar;
    protected KapuaGrid<M> entityGrid;
    protected PagingToolBar entityPagingToolbar;
    protected EntityFilterPanel<M> filterPanel;

    protected BasePagingLoader<PagingLoadResult<M>> entityLoader;
    protected ListStore<M> entityStore;

    /**
     * Some grids (most notably "slave" grids, i.e. the ones that depends from the entity
     * selected in another grid) should not be refreshed on render, otherwise they would be
     * refreshed twice and the paging toolbar may be disabled because of this
     */
    protected boolean refreshOnRender = true;

    protected SelectionMode selectionMode = SelectionMode.SINGLE;
    protected boolean keepSelectedItemsAfterLoad = true;
    protected boolean entityGridConfigured;
    protected boolean selectedAgain;
    protected M selectedItem;
    private boolean deselectable;

    public int getEntityPageSize() {
        return ENTITY_PAGE_SIZE;
    }

    protected EntityGrid(AbstractEntityView<M> entityView, GwtSession currentSession) {
        super(new FitLayout());

        // Set other properties
        this.parentEntityView = entityView;
        this.currentSession = currentSession;

        // Container borders
        setBorders(false);
        setBodyBorder(true);
        setHeaderVisible(false);

        // CRUD toolbar
        entityCRUDToolbar = getToolbar();
        if (entityCRUDToolbar != null) {
            setTopComponent(entityCRUDToolbar);
        }

        // Paging toolbar
        entityPagingToolbar = getPagingToolbar();
        if (entityPagingToolbar != null) {
            setBottomComponent(entityPagingToolbar);
        }
    }

    @Override
    protected void onRender(Element target, int index) {
        super.onRender(target, index);

        // Configure Entity Grid
        if (!entityGridConfigured) {
            configureEntityGrid();
        }

        // Force layout so the entityGrid gets rendered and its listeners initialized
        layout();

        // Bind the grid to CRUD toolbar
        entityCRUDToolbar.setEntityGrid(this);

        // Grid selection mode
        final GridSelectionModel<M> selectionModel = entityGrid.getSelectionModel();
        selectionModel.setSelectionMode(selectionMode);
        selectionModel.addSelectionChangedListener(new SelectionChangedListener<M>() {

            @SuppressWarnings("unchecked")
            @Override
            public void selectionChanged(SelectionChangedEvent<M> se) {
                if (selectionModel.getSelectedItem() != null && selectedAgain == false && !deselectable) {
                    selectionChangedEvent(se.getSelectedItem());
                    selectedItem = selectionModel.getSelectedItem();
                }
                if (selectionModel.getSelectedItem() == null && !deselectable) {
                    selectedAgain = true;
                    selectionModel.select(true, selectedItem);
                }
                if (selectedItem != selectionModel.getSelectedItem() && !deselectable) {
                    selectedAgain = false;
                    selectionChangedEvent(se.getSelectedItem());
                    selectedItem = selectionModel.getSelectedItem();
                }
                if (deselectable) {
                    selectionChangedEvent(se.getSelectedItem());
                }
            }
        });

        // Do first load
        if (refreshOnRender) {
            refresh();
        }
    }

    public String getEmptyGridText() {
        return MSGS.gridEmptyResult();
    }

    protected void setEmptyGridText(String message) {
        GridView gridView = entityGrid.getView();
        gridView.setEmptyText(message);
    }

    protected EntityCRUDToolbar<M> getToolbar() {
        return new EntityCRUDToolbar<M>(currentSession);
    }

    protected abstract RpcProxy<PagingLoadResult<M>> getDataProxy();

    protected PagingToolBar getPagingToolbar() {
        KapuaPagingToolBar kapuaPagingToolBar = new KapuaPagingToolBar(ENTITY_PAGE_SIZE);
        kapuaPagingToolBar.setKapuaPagingToolbarMessages(getKapuaPagingToolbarMessages());
        return kapuaPagingToolBar;
    }

    protected KapuaPagingToolbarMessages getKapuaPagingToolbarMessages() {
        return new KapuaPagingToolbarMessages() {

            @Override
            public String pagingToolbarShowingPost() {
                return MSGS.pagingToolbarShowingPost();
            }

            @Override
            public String pagingToolbarNoResult() {
                return MSGS.pagingToolbarNoResult();
            }
        };
    }

    /**
     * Configuring entity grid, because it needs to be configured before onRender method is called.
     */

    protected void configureEntityGrid() {
        // Data Proxy
        RpcProxy<PagingLoadResult<M>> dataProxy = getDataProxy();

        // Data Loader
        entityLoader = new BasePagingLoader<PagingLoadResult<M>>(dataProxy);

        // Data Store
        entityStore = new ListStore<M>(entityLoader);

        // Grid Data Load Listener
        EntityGridLoadListener<M> entityGridLoadListener = new EntityGridLoadListener<M>(this, entityStore, EntityFilterPanel.getSearchButton(), EntityFilterPanel.getResetButton());
        entityGridLoadListener.setKeepSelectedOnLoad(keepSelectedItemsAfterLoad);

        entityLoader.addLoadListener(entityGridLoadListener);

        // Bind Entity Paging Toolbar
        if (entityPagingToolbar != null) {
            entityPagingToolbar.bind(entityLoader);
        }

        // Configure columns
        ColumnModel columnModel = new ColumnModel(getColumns());

        // Set grid
        entityGrid = new KapuaGrid<M>(entityStore, columnModel);
        add(entityGrid);

        // Misc
        setEmptyGridText(getEmptyGridText());

        entityGridConfigured = true;
    }

    protected abstract List<ColumnConfig> getColumns();

    public void clearGridElement() {
        entityStore.removeAll();
    }

    public void refresh() {
        entityCRUDToolbar.getRefreshEntityButton().setEnabled(false);
        entityLoader.setReuseLoadConfig(true);
        entityLoader.load();
    }

    public void refresh(GwtQuery query) {
        setFilterQuery(query);
        entityLoader.load(0, entityLoader.getLimit());
    }

    public void setFilterPanel(EntityFilterPanel<M> filterPanel) {
        this.filterPanel = filterPanel;

        if (entityCRUDToolbar != null) {
            entityCRUDToolbar.setFilterPanel(filterPanel);
        }
    }

    protected void selectionChangedEvent(M selectedItem) {
        if (parentEntityView != null) {
            parentEntityView.setSelectedEntity(selectedItem);
        }

        if (entityCRUDToolbar != null) {
            entityCRUDToolbar.setSelectedEntity(selectedItem);
        }
    }

    public void setPagingToolbar(PagingToolBar entityPagingToolbar) {
        this.entityPagingToolbar = entityPagingToolbar;
    }

    public GridSelectionModel<M> getSelectionModel() {
        return entityGrid.getSelectionModel();
    }

    public boolean isRefreshOnRender() {
        return refreshOnRender;
    }

    public void setRefreshOnRender(boolean refreshOnRender) {
        this.refreshOnRender = refreshOnRender;
    }

    public abstract GwtQuery getFilterQuery();

    public abstract void setFilterQuery(GwtQuery filterQuery);

    /**
     * Method is called when grid is loaded.
     */
    public void loaded() {
        entityCRUDToolbar.getRefreshEntityButton().setEnabled(true);
    }

    public void setDeselectable() {
        deselectable = true;
    }

}
