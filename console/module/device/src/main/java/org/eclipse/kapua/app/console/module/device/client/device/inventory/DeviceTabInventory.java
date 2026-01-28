/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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


import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.TabPanel.TabPosition;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import org.eclipse.kapua.app.console.module.api.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.IconSet;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.KapuaIcon;
import org.eclipse.kapua.app.console.module.api.client.ui.tab.KapuaTabItem;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.device.client.device.DeviceView;
import org.eclipse.kapua.app.console.module.device.client.messages.ConsoleDeviceMessages;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.module.device.shared.model.permission.DeviceManagementSessionPermission;

public class DeviceTabInventory extends KapuaTabItem<GwtDevice> {

    private static final ConsoleDeviceMessages MSGS = GWT.create(ConsoleDeviceMessages.class);
    private static final ConsoleMessages CMSGS = GWT.create(ConsoleMessages.class);

    private DeviceView deviceTabs;

    private boolean initialized;

    private TabPanel tabsPanel;
    private DeviceTabInventoryTabInventory inventoryTab;
    private DeviceTabInventoryTabBundles inventoryBundlesTab;
    private DeviceTabInventoryTabContainer inventoryContainerTab;
    private DeviceTabInventoryTabSystemPackages inventorySystemPackagesTab;
    private DeviceTabInventoryTabImage inventoryTabImage;
    private DeviceTabInventoryTabDeploymentPackages inventoryDeploymentPackageTab;


    public DeviceTabInventory(GwtSession currentSession, DeviceView deviceTabs) {
        super(currentSession, "Inventory", new KapuaIcon(IconSet.ARCHIVE));

        this.deviceTabs = deviceTabs;
        setEnabled(false);
        getHeader().setVisible(false);
    }

    @Override
    public void setEntity(GwtDevice gwtDevice) {
        super.setEntity(gwtDevice);

        setDirty();
        if (gwtDevice != null) {
            setEnabled(gwtDevice.isOnline() && currentSession.hasPermission(DeviceManagementSessionPermission.read()));
            getHeader().setVisible(gwtDevice.hasApplication(GwtDevice.GwtDeviceApplication.APP_INVENTORY_V1));
        } else {
            setEnabled(false);
            getHeader().setVisible(false);
        }

        if (initialized && tabsPanel.getSelectedItem() == null) {
            tabsPanel.setSelection(inventoryDeploymentPackageTab);
        }

    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);

        setLayout(new FitLayout());
        setBorders(false);

        initToolBar();
        initPackagesTabs();

        ContentPanel devicesInventoryPanel = new ContentPanel();
        devicesInventoryPanel.setBorders(false);
        devicesInventoryPanel.setBodyBorder(false);
        devicesInventoryPanel.setHeaderVisible(false);
        devicesInventoryPanel.setLayout(new FitLayout());
        devicesInventoryPanel.add(tabsPanel);

        add(devicesInventoryPanel);

        initialized = true;
    }
    // INITIALIZERS

    private void initToolBar() {

    }

    private void initPackagesTabs() {
        tabsPanel = new TabPanel();
        tabsPanel.setPlain(true);
        tabsPanel.setBorders(false);
        tabsPanel.setTabPosition(TabPosition.BOTTOM);
        // Inventory Sub-tab
        inventoryTab = new DeviceTabInventoryTabInventory(this);
        inventoryTab.setBorders(false);
        inventoryTab.setLayout(new FitLayout());

        inventoryTab.addListener(Events.Select, new Listener<ComponentEvent>() {

            @Override
            public void handleEvent(ComponentEvent be) {
                refresh();
            }
        });
        tabsPanel.add(inventoryTab);
        // Bundle Sub-tab
        inventoryBundlesTab = new DeviceTabInventoryTabBundles(this);
        inventoryBundlesTab.setBorders(false);
        inventoryBundlesTab.setLayout(new FitLayout());

        inventoryBundlesTab.addListener(Events.Select, new Listener<ComponentEvent>() {

            @Override
            public void handleEvent(ComponentEvent be) {
                setEntity(selectedEntity);
                refresh();
            }
        });
        tabsPanel.add(inventoryBundlesTab);
        // Container Sub-tab
        inventoryContainerTab = new DeviceTabInventoryTabContainer(this);
        inventoryContainerTab.setBorders(false);
        inventoryContainerTab.setLayout(new FitLayout());

        inventoryContainerTab.addListener(Events.Select, new Listener<ComponentEvent>() {

            @Override
            public void handleEvent(ComponentEvent be) {
                setEntity(selectedEntity);
                refresh();
            }
        });
        tabsPanel.add(inventoryContainerTab);
        // Images Sub-tab
        inventoryTabImage = new DeviceTabInventoryTabImage(this);
        inventoryTabImage.setBorders(false);
        inventoryTabImage.setLayout(new FitLayout());
        inventoryTabImage.addListener(Events.Select, new Listener<ComponentEvent>() {

            @Override
            public void handleEvent(ComponentEvent be) {
                setEntity(selectedEntity);
                refresh();
            }
        });
        tabsPanel.add(inventoryTabImage);
        // System Packages Sub-tab
        inventorySystemPackagesTab = new DeviceTabInventoryTabSystemPackages(this);
        inventorySystemPackagesTab.setBorders(false);
        inventorySystemPackagesTab.setLayout(new FitLayout());

        inventorySystemPackagesTab.addListener(Events.Select, new Listener<ComponentEvent>() {

            @Override
            public void handleEvent(ComponentEvent be) {
                setEntity(selectedEntity);
                refresh();
            }
        });
        tabsPanel.add(inventorySystemPackagesTab);
        // Deployment Packages Sub-tab
        inventoryDeploymentPackageTab = new DeviceTabInventoryTabDeploymentPackages(this);
        inventoryDeploymentPackageTab.setBorders(false);
        inventoryDeploymentPackageTab.setLayout(new FitLayout());

        inventoryDeploymentPackageTab.addListener(Events.Select, new Listener<ComponentEvent>() {

            @Override
            public void handleEvent(ComponentEvent be) {
                refresh();
            }
        });
        tabsPanel.add(inventoryDeploymentPackageTab);
    }

    // REFRESHER
    @Override
    public void doRefresh() {
        if (initialized) {
            // Refresh the installed tab if selected
            if (tabsPanel.getSelectedItem().equals(inventoryTab)) {
                inventoryTab.refresh();
            }

            if (tabsPanel.getSelectedItem().equals(inventoryDeploymentPackageTab)) {
                inventoryDeploymentPackageTab.refresh();
            }

            if (tabsPanel.getSelectedItem().equals(inventoryBundlesTab)) {
                inventoryBundlesTab.refresh();
            }

            if (tabsPanel.getSelectedItem().equals(inventoryContainerTab)) {
                inventoryContainerTab.refresh();
            }

            if (tabsPanel.getSelectedItem().equals(inventoryTabImage)) {
                inventoryTabImage.refresh();
            }

            if (tabsPanel.getSelectedItem().equals(inventorySystemPackagesTab)) {
                inventorySystemPackagesTab.refresh();
            }
        }
    }
    // ACCESSORS

    public GwtDevice getSelectedDevice() {
        return selectedEntity;
    }

    public void setDirty() {
        if (initialized) {
            setDirty(true);

            inventoryTab.setDirty(true);
            inventoryDeploymentPackageTab.setDirty(true);
            inventoryBundlesTab.setDirty(true);
            inventoryContainerTab.setDirty(true);
            inventorySystemPackagesTab.setDirty(true);
        }
    }

}
