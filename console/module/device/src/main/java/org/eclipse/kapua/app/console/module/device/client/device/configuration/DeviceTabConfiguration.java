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
package org.eclipse.kapua.app.console.module.device.client.device.configuration;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.TabPanel.TabPosition;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.IconSet;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.KapuaIcon;
import org.eclipse.kapua.app.console.module.api.client.ui.tab.KapuaTabItem;
import org.eclipse.kapua.app.console.module.api.client.ui.tab.TabItem;
import org.eclipse.kapua.app.console.module.api.client.util.FailureHandler;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.device.client.messages.ConsoleDeviceMessages;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.module.device.shared.model.permission.DeviceManagementSessionPermission;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceManagementService;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceManagementServiceAsync;

public class DeviceTabConfiguration extends KapuaTabItem<GwtDevice> {

    private static final ConsoleDeviceMessages MSGS = GWT.create(ConsoleDeviceMessages.class);

    private static final GwtDeviceManagementServiceAsync GWT_DEVICE_MANAGEMENT_SERVICE = GWT.create(GwtDeviceManagementService.class);

    private TabPanel tabsPanel;
    private TabItem tabComponents;
    private TabItem tabSnapshots;

    private DeviceConfigComponents configComponents;
    private DeviceConfigSnapshots configSnapshots;

    public DeviceTabConfiguration(GwtSession currentSession) {
        super(currentSession, MSGS.tabConfiguration(), new KapuaIcon(IconSet.WRENCH));

        configComponents = new DeviceConfigComponents(currentSession, this);
        configSnapshots = new DeviceConfigSnapshots(currentSession, this);

        setEnabled(false);
        getHeader().setVisible(false);
    }

    @Override
    public void setEntity(GwtDevice gwtDevice) {
        super.setEntity(gwtDevice);

        if (gwtDevice != null) {
            setEnabled(gwtDevice.isOnline() && currentSession.hasPermission(DeviceManagementSessionPermission.read()));
            getHeader().setVisible(gwtDevice.hasApplication(GwtDevice.GwtDeviceApplication.APP_CONFIGURATION_V1) || gwtDevice.hasApplication(GwtDevice.GwtDeviceApplication.APP_CONFIGURATION_V2));
            setText(MSGS.tabConfiguration());

            if (!gwtDevice.isOnline()) {
                GWT_DEVICE_MANAGEMENT_SERVICE.isStoreServiceEnabled(gwtDevice.getScopeId(), gwtDevice.getId(), new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable t) {
                        FailureHandler.handle(t);
                    }

                    @Override
                    public void onSuccess(Boolean enabled) {
                        if (enabled) {
                            setEnabled(currentSession.hasPermission(DeviceManagementSessionPermission.read()));
                            setText(MSGS.tabConfiguration() + " (Offline)");
                        }
                    }
                });
            }
        } else {
            setEnabled(false);
            getHeader().setVisible(false);
        }

        if (selectedEntity != null) {
            // Configurations
            configComponents.setDevice(selectedEntity);

            // Snapshot
            configSnapshots.setDevice(selectedEntity);
        }

    }

    @Override
    public void doRefresh() {
        if (tabsPanel == null) {
            return;
        }

        if (selectedEntity != null) {
            tabSnapshots.setEnabled(selectedEntity.isOnline());
        }
        if (tabsPanel.getSelectedItem() == tabComponents) {
            configComponents.refresh();
        } else if (tabsPanel.getSelectedItem() == tabSnapshots) {
            configSnapshots.refresh();
        }

    }

    @Override
    protected void onRender(Element parent, int index) {

        super.onRender(parent, index);

        setId("DeviceTabsContainer");
        setLayout(new FitLayout());
        setBorders(false);

        tabsPanel = new TabPanel();
        tabsPanel.setPlain(true);
        tabsPanel.setBorders(false);
        tabsPanel.setTabPosition(TabPosition.BOTTOM);

        tabComponents = new TabItem(MSGS.deviceConfigComponents(), new KapuaIcon(IconSet.PUZZLE_PIECE));
        tabComponents.setBorders(false);
        tabComponents.setLayout(new FitLayout());
        tabComponents.add(configComponents);
        tabComponents.addListener(Events.Select, new Listener<ComponentEvent>() {

            @Override
            public void handleEvent(ComponentEvent be) {
                configComponents.refresh();
            }
        });
        tabsPanel.add(tabComponents);

        tabSnapshots = new TabItem(MSGS.deviceConfigSnapshots(), new KapuaIcon(IconSet.ARCHIVE));
        tabSnapshots.setBorders(true);
        tabSnapshots.setLayout(new FitLayout());
        tabSnapshots.add(configSnapshots);
        tabSnapshots.addListener(Events.Select, new Listener<ComponentEvent>() {

            @Override
            public void handleEvent(ComponentEvent be) {
                configSnapshots.refresh();
            }
        });
        tabsPanel.add(tabSnapshots);

        add(tabsPanel);
        layout(true);
        Node node0 = tabsPanel.getElement();
        Node node1 = node0.getChild(1);
        Node node2 = node1.getChild(0);
        if (node2.getNodeType() == Node.ELEMENT_NODE) {
            Element elem = (Element) node2;
            elem.setAttribute("style", "border-top: 0px; border-bottom: 0px; border-color: #d0d0d0; background: #eaeaea;");
        }
    }
}
