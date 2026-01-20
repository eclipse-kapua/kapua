/*******************************************************************************
 * Copyright (c) 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.device.client.device.inventory;

import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.extjs.gxt.ui.client.widget.button.Button;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaErrorCode;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.IconSet;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.KapuaIcon;
import org.eclipse.kapua.app.console.module.api.client.ui.button.RefreshButton;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.InfoDialog;
import org.eclipse.kapua.app.console.module.api.client.ui.tab.TabItem;
import org.eclipse.kapua.app.console.module.api.client.util.ConsoleInfo;
import org.eclipse.kapua.app.console.module.api.client.util.FailureHandler;
import org.eclipse.kapua.app.console.module.api.client.util.KapuaLoadListener;
import org.eclipse.kapua.app.console.module.device.client.device.inventory.buttons.ImageDeleteButton;
import org.eclipse.kapua.app.console.module.device.client.device.inventory.dialog.InventoryImageDeleteDialog;
import org.eclipse.kapua.app.console.module.device.client.messages.ConsoleDeviceMessages;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.module.device.shared.model.management.inventory.GwtInventoryImage;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceInventoryManagementService;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceInventoryManagementServiceAsync;

import java.util.ArrayList;
import java.util.List;

public class DeviceTabInventoryTabImage extends TabItem {

    private static final ConsoleMessages MSGS = GWT.create(ConsoleMessages.class);
    private static final ConsoleDeviceMessages DEVICE_MSGS = GWT.create(ConsoleDeviceMessages.class);

    private static final GwtDeviceInventoryManagementServiceAsync GWT_DEVICE_INVENTORY_MANAGEMENT_SERVICE = GWT.create(GwtDeviceInventoryManagementService.class);

    private boolean componentInitialized;
    private boolean dirty = true;

    private DeviceTabInventory parentTabPanel;
    private Grid<GwtInventoryImage> grid;
    private ListLoader<ListLoadResult<GwtInventoryImage>> storeLoader;

    Button deleteButton;
    Button refreshButton;

    public DeviceTabInventoryTabImage(DeviceTabInventory parentTabPanel) {
        super("Images", new KapuaIcon(IconSet.TH));

        this.parentTabPanel = parentTabPanel;
    }

    private GwtDevice getSelectedDevice() {
        return parentTabPanel.getSelectedDevice();
    }

    public void setDirty(boolean isDirty) {
        dirty = isDirty;
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        // Column Configuration
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig column = new ColumnConfig();
        column.setId("name");
        column.setHeader("Name");
        column.setWidth(100);
        configs.add(column);

        column = new ColumnConfig();
        column.setId("version");
        column.setHeader("Version");
        column.setWidth(80);
        configs.add(column);

        column = new ColumnConfig();
        column.setId("type");
        column.setHeader("Type");
        column.setWidth(80);
        configs.add(column);

        ColumnModel columnModel = new ColumnModel(configs);

        RpcProxy<ListLoadResult<GwtInventoryImage>> proxy = new RpcProxy<ListLoadResult<GwtInventoryImage>>() {

            @Override
            protected void load(Object loadConfig, AsyncCallback<ListLoadResult<GwtInventoryImage>> callback) {
                GWT_DEVICE_INVENTORY_MANAGEMENT_SERVICE.findDeviceImages(getSelectedDevice().getScopeId(),
                        getSelectedDevice().getId(),
                        callback);
            }
        };

        storeLoader = new BaseListLoader<ListLoadResult<GwtInventoryImage>>(proxy);
        storeLoader.addLoadListener(new DeviceTabInventoryTabImage.InventoryImagesLoadListener());

        ListStore<GwtInventoryImage> store = new ListStore<GwtInventoryImage>(storeLoader);

        grid = new Grid<GwtInventoryImage>(store, columnModel);
        grid.setBorders(false);
        grid.setStateful(false);
        grid.setLoadMask(true);
        grid.setStripeRows(true);
        grid.setTrackMouseOver(false);
        grid.disableTextSelection(false);
        grid.setAutoExpandColumn("name");
        grid.getView().setAutoFill(true);
        grid.getView().setEmptyText("No images found...");

        grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<GwtInventoryImage>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<GwtInventoryImage> selectionChangedEvent) {
                checkButtonEnablement();
            }
        });

        // Delete Button
        deleteButton = new ImageDeleteButton(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                final InventoryImageDeleteDialog imageDeleteDialog = new InventoryImageDeleteDialog(getSelectedDevice(), grid.getSelectionModel().getSelectedItem());

                imageDeleteDialog.addListener(Events.Hide, new Listener<BaseEvent>() {
                    @Override
                    public void handleEvent(BaseEvent baseEvent) {
                        if (imageDeleteDialog.getExitStatus() != null) {
                            String exitMessage = imageDeleteDialog.getExitMessage();
                            ConsoleInfo.display(imageDeleteDialog.getExitStatus() ? MSGS.information() : MSGS.error(), exitMessage);
                        }
                        setDirty(true);
                        refresh();
                    }
                });

                imageDeleteDialog.show();
            }
        });

        // Refresh Button
        refreshButton = new RefreshButton(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                if (getSelectedDevice() != null && getSelectedDevice().isOnline()) {
                    setDirty(true);
                    refresh();
                } else {
                    openDeviceOfflineAlertDialog();
                }
            }
        });

        ToolBar toolBar = new ToolBar();
        toolBar.setBorders(true);
        toolBar.add(deleteButton);
        toolBar.add(refreshButton);

        checkButtonEnablement();

        ContentPanel rootContentPanel = new ContentPanel();
        rootContentPanel.setLayout(new FitLayout());
        rootContentPanel.setBorders(false);
        rootContentPanel.setBodyBorder(false);
        rootContentPanel.setHeaderVisible(false);
        rootContentPanel.add(grid);
        rootContentPanel.setTopComponent(toolBar);

        add(rootContentPanel);

        componentInitialized = true;
    }

    private void checkButtonEnablement() {
        GwtInventoryImage selectedImage = grid.getSelectionModel().getSelectedItem();

        deleteButton.setEnabled(getSelectedDevice() != null && getSelectedDevice().isOnline() && selectedImage != null);
        refreshButton.setEnabled(getSelectedDevice() != null && getSelectedDevice().isOnline());
    }

    public void refresh() {
        if (dirty && componentInitialized) {

            if (getSelectedDevice() == null) {
                grid.getView().setEmptyText(DEVICE_MSGS.deviceNoDeviceSelectedOrOffline());
            } else {
                storeLoader.load();
            }

            dirty = false;
        }
    }

    public void openDeviceOfflineAlertDialog() {
        InfoDialog errorDialog = new InfoDialog(InfoDialog.InfoDialogType.INFO, DEVICE_MSGS.deviceOffline());
        errorDialog.show();
    }

    private class InventoryImagesLoadListener extends KapuaLoadListener {

        @Override
        public void loaderLoadException(LoadEvent le) {
            grid.unmask();

            Throwable loaderException = le.exception;

            if (loaderException != null) {

                if (loaderException instanceof GwtKapuaException &&
                        ((GwtKapuaException) loaderException).getCode().equals(GwtKapuaErrorCode.DEVICE_MANAGEMENT_RESPONSE_NOT_FOUND)) {
                    ConsoleInfo.display(MSGS.error(), "The 'images' resource is not supported by this device!");
                } else {
                    FailureHandler.handle(le.exception);
                }
            }
        }
    }
}
