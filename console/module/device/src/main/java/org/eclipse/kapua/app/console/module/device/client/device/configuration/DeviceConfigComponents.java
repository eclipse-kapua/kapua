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

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelSelectionModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaErrorCode;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.IconSet;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.KapuaIcon;
import org.eclipse.kapua.app.console.module.api.client.ui.button.AddButton;
import org.eclipse.kapua.app.console.module.api.client.ui.button.DiscardButton;
import org.eclipse.kapua.app.console.module.api.client.ui.button.KapuaButton;
import org.eclipse.kapua.app.console.module.api.client.ui.button.RefreshButton;
import org.eclipse.kapua.app.console.module.api.client.ui.button.SaveButton;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.ActionDialog;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.InfoDialog;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.InfoDialog.InfoDialogType;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.KapuaMessageBox;
import org.eclipse.kapua.app.console.module.api.client.ui.label.Label;
import org.eclipse.kapua.app.console.module.api.client.util.ConsoleInfo;
import org.eclipse.kapua.app.console.module.api.client.util.CssLiterals;
import org.eclipse.kapua.app.console.module.api.client.util.FailureHandler;
import org.eclipse.kapua.app.console.module.api.client.util.KapuaLoadListener;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtConfigComponent;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.api.shared.service.GwtSecurityTokenService;
import org.eclipse.kapua.app.console.module.api.shared.service.GwtSecurityTokenServiceAsync;
import org.eclipse.kapua.app.console.module.device.client.device.configuration.settings.DeviceConfigurationsStoreSettingsDialog;
import org.eclipse.kapua.app.console.module.device.client.messages.ConsoleDeviceMessages;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceManagementService;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceManagementServiceAsync;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceService;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceServiceAsync;

public class DeviceConfigComponents extends LayoutContainer {

    private static final ConsoleMessages MSGS = GWT.create(ConsoleMessages.class);
    private static final ConsoleDeviceMessages DEVICE_MSGS = GWT.create(ConsoleDeviceMessages.class);

    private final GwtDeviceServiceAsync gwtDeviceService = GWT.create(GwtDeviceService.class);
    private final GwtDeviceManagementServiceAsync gwtDeviceManagementService = GWT.create(GwtDeviceManagementService.class);
    private final GwtSecurityTokenServiceAsync gwtXSRFService = GWT.create(GwtSecurityTokenService.class);

    private boolean dirty;
    private boolean initialized;
    private GwtDevice selectedDevice;
    private DeviceTabConfiguration tabConfig;

    private ToolBar toolBar;

    private Button refreshButton;
    private boolean refreshProcess;

    private Button addButton;
    private boolean addProcess;

    private Button apply;
    private Button reset;

    private Button settings;

    private ContentPanel configPanel;
    private DeviceConfigPanel devConfPanel;
    private BorderLayoutData centerData;

    private BaseTreeLoader loader;
    private TreeStore<ModelData> treeStore;
    private TreePanel<ModelData> tree;

    protected boolean resetProcess;

    protected boolean applyProcess;

    private GwtSession gwtSession;

    private final AsyncCallback<Void> applyConfigCallback = new AsyncCallback<Void>() {

        @Override
        public void onFailure(Throwable caught) {
            if ((caught instanceof GwtKapuaException) &&
                    GwtKapuaErrorCode.SUBJECT_UNAUTHORIZED.equals(((GwtKapuaException) caught).getCode())) {
                ConsoleInfo.display(MSGS.popupError(), caught.getLocalizedMessage());
            } else if (!selectedDevice.isOnline()) {
                ConsoleInfo.display(MSGS.popupError(), DEVICE_MSGS.deviceConnectionError());
            }

            devConfPanel.unmask();
            tree.unmask();

            apply.setEnabled(true);
            reset.setEnabled(true);
            refreshButton.setEnabled(true);
        }

        @Override
        public void onSuccess(Void arg0) {
            dirty = true;
            String componentName = devConfPanel.getConfiguration().getComponentName();
            final boolean isCloudUpdate = "CloudService".equals(componentName);
            if (isCloudUpdate) {
                refreshWhenOnline();
            } else {
                refresh();
            }
        }
    };

    public DeviceConfigComponents(GwtSession currentSession, DeviceTabConfiguration tabConfig) {
        this.tabConfig = tabConfig;

        dirty = false;
        initialized = false;
        gwtSession = currentSession;
    }

    public void setDevice(GwtDevice selectedDevice) {
        dirty = true;
        this.selectedDevice = selectedDevice;
        if (selectedDevice != null && settings != null) {
            refreshVisibilityStoreSettingButton();
        }
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        setLayout(new FitLayout());
        setBorders(false);

        // init components
        initToolBar();
        initConfigPanel();

        ContentPanel devicesConfigurationPanel = new ContentPanel();
        devicesConfigurationPanel.setBorders(false);
        devicesConfigurationPanel.setBodyBorder(false);
        devicesConfigurationPanel.setHeaderVisible(false);
        devicesConfigurationPanel.setLayout(new FitLayout());
        devicesConfigurationPanel.setScrollMode(Scroll.AUTO);
        devicesConfigurationPanel.setTopComponent(toolBar);
        devicesConfigurationPanel.add(configPanel);

        add(devicesConfigurationPanel);
        initialized = true;
    }

    private void initToolBar() {
        toolBar = new ToolBar();
        toolBar.setBorders(true);
        toolBar.setStyleAttribute("border-left", CssLiterals.border1PxSolidRgb(208, 208, 208));
        toolBar.setStyleAttribute("border-right", CssLiterals.border1PxSolidRgb(208, 208, 208));
        toolBar.setStyleAttribute("border-top", CssLiterals.border1PxSolidRgb(208, 208, 208));
        toolBar.setStyleAttribute("border-bottom", CssLiterals.BORDER_0PX_NONE);
        // Refresh Button
        refreshButton = new RefreshButton(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                if (!refreshProcess) {
                    refreshButton.setEnabled(false);
                    refreshProcess = true;

                    dirty = true;
                    refresh();
                    refreshProcess = false;
                    gwtSession.setFormDirty(false);
                }
            }
        });

        addButton = new AddButton(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                add();
            }
        });

        apply = new SaveButton(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                if (!applyProcess) {
                    applyProcess = true;
                    apply.setEnabled(false);

                    apply();

                    apply.setEnabled(true);
                    applyProcess = false;
                    gwtSession.setFormDirty(false);
                }
            }
        });

        reset = new DiscardButton(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                if (!resetProcess) {
                    resetProcess = true;
                    reset.setEnabled(false);

                    reset();

                    reset.setEnabled(true);
                    resetProcess = false;
                    gwtSession.setFormDirty(false);
                }
            }
        });

        settings = new KapuaButton("Store Settings", new KapuaIcon(IconSet.COG), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                DeviceConfigurationsStoreSettingsDialog deviceAssetStoreSettingsDialog = new DeviceConfigurationsStoreSettingsDialog(selectedDevice);
                deviceAssetStoreSettingsDialog.show();
            }
        });

        apply.setEnabled(false);
        reset.setEnabled(false);
        gwtSession.setFormDirty(false);

        toolBar.add(refreshButton);
        toolBar.add(new SeparatorToolItem());
        toolBar.add(addButton);
        toolBar.add(new SeparatorToolItem());
        toolBar.add(apply);
        toolBar.add(new SeparatorToolItem());
        toolBar.add(reset);
        toolBar.add(new SeparatorToolItem());
        toolBar.add(settings);
    }

    private void initConfigPanel() {
        configPanel = new ContentPanel();
        configPanel.setBorders(false);
        configPanel.setBodyBorder(false);
        configPanel.setHeaderVisible(false);
        configPanel.setStyleAttribute("background-color", "white");
        configPanel.setScrollMode(Scroll.AUTO);

        BorderLayout borderLayout = new BorderLayout();
        configPanel.setLayout(borderLayout);

        // center
        centerData = new BorderLayoutData(LayoutRegion.CENTER);
        centerData.setMargins(new Margins(0));

        // west
        BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 200);
        westData.setSplit(true);
        westData.setCollapsible(true);
        westData.setMargins(new Margins(0, 5, 0, 0));

        // loader and store
        RpcProxy<List<GwtConfigComponent>> proxy = new RpcProxy<List<GwtConfigComponent>>() {

            @Override
            protected void load(Object loadConfig, AsyncCallback<List<GwtConfigComponent>> callback) {
                if (selectedDevice != null && dirty && initialized) {
                    configPanel.mask(MSGS.loading());
                    gwtDeviceManagementService.findDeviceConfigurations(selectedDevice, callback);
                } else {
                    List<GwtConfigComponent> comps = new ArrayList<GwtConfigComponent>();
                    GwtConfigComponent comp = new GwtConfigComponent();
                    comp.setId(DEVICE_MSGS.deviceNoDeviceSelected());
                    comp.setName(DEVICE_MSGS.deviceNoDeviceSelected());
                    comp.setDescription(DEVICE_MSGS.deviceNoDeviceSelected());
                    comps.add(comp);
                    callback.onSuccess(comps);
                }
                dirty = false;
            }
        };

        loader = new BaseTreeLoader<GwtConfigComponent>(proxy);
        loader.addLoadListener(new DataLoadListener());

        treeStore = new TreeStore<ModelData>(loader);

        tree = new TreePanel<ModelData>(treeStore);
        tree.setWidth(200);
        tree.setDisplayProperty("componentName");
        tree.setBorders(true);
        tree.setLabelProvider(modelStringProvider);
        tree.setAutoSelect(true);
        tree.setStyleAttribute("background-color", "white");

        configPanel.add(tree, westData);
        // Selection Listener for the component
        // make sure the form is not dirty before switching.
        tree.getSelectionModel().addListener(Events.BeforeSelect, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent theEvent) {

                SelectionEvent<ModelData> se = (SelectionEvent<ModelData>) theEvent;

                final GwtConfigComponent componentToSwitchTo = (GwtConfigComponent) se.getModel();
                if (devConfPanel != null && devConfPanel.isDirty()) {

                    // cancel the event first
                    theEvent.setCancelled(true);

                    // need to reselect the current entry
                    // as the BeforeSelect event cleared it
                    // we need to do this without raising events
                    TreePanelSelectionModel selectionModel = tree.getSelectionModel();
                    selectionModel.setFiresEvents(false);
                    selectionModel.select(false, devConfPanel.getConfiguration());
                    selectionModel.setFiresEvents(true);

                    // ask for confirmation before switching
                    MessageBox.confirm(MSGS.confirm(),
                            DEVICE_MSGS.deviceConfigDirty(),
                            new Listener<MessageBoxEvent>() {

                                @Override
                                public void handleEvent(MessageBoxEvent ce) {
                                    // if confirmed, delete
                                    Dialog dialog = ce.getDialog();
                                    if (dialog.yesText.equals(ce.getButtonClicked().getText())) {
                                        devConfPanel.removeFromParent();
                                        devConfPanel = null;
                                        tree.getSelectionModel().select(false, componentToSwitchTo);
                                    }
                                }
                            });
                } else {
                    refreshConfigPanel(componentToSwitchTo);

                    // this is needed to select the item in the Tree
                    // Temporarly disable the firing of the selection events
                    // to avoid an infinite loop as BeforeSelect would be invoked again.
                    TreePanelSelectionModel selectionModel = tree.getSelectionModel();
                    selectionModel.setFiresEvents(false);
                    selectionModel.select(false, componentToSwitchTo);

                    // renable firing of the events
                    selectionModel.setFiresEvents(true);
                }
            }
        });

        tree.getSelectionModel().addListener(Events.SelectionChange, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                String customWidth = tree.getElement().getScrollWidth() + "px";
                StyleInjector.inject(".x-tree3-node { width: " + customWidth + "}");
            }
        });

    }

    // --------------------------------------------------------------------------------------
    // Device Configuration Management
    // --------------------------------------------------------------------------------------

    private static final int PERIOD_MILLIS = 1000;

    public void refreshWhenOnline() {

        Timer timer = new Timer() {

            private static final int TIMEOUT_MILLIS = 30000;
            private int countdownMillis = TIMEOUT_MILLIS;

            @Override
            public void run() {
                if (selectedDevice != null) {
                    countdownMillis -= PERIOD_MILLIS;
                    // Poll the current status of the device until is online again or timeout.
                    gwtDeviceService.findDevice(selectedDevice.getScopeId(),
                            selectedDevice.getUnescapedClientId(),
                            new AsyncCallback<GwtDevice>() {

                                @Override
                                public void onFailure(Throwable t) {
                                    done();
                                }

                                @Override
                                public void onSuccess(GwtDevice gwtDevice) {
                                    if (countdownMillis <= 0 ||
                                            // Allow the device to disconnect before checking if it's online again.
                                            ((TIMEOUT_MILLIS - countdownMillis) > 5000 && gwtDevice.isOnline())) {
                                        done();
                                    }
                                }

                                private void done() {
                                    cancel();
                                    refresh();
                                    if (devConfPanel != null) {
                                        devConfPanel.unmask();
                                    }
                                }
                            });
                }
            }
        };
        devConfPanel.mask(MSGS.waiting());
        timer.scheduleRepeating(PERIOD_MILLIS);
    }

    public void refresh() {
        if (dirty && initialized) {
            // clear the tree and disable the toolbar
            apply.setEnabled(false);
            reset.setEnabled(false);
            gwtSession.setFormDirty(false);

            treeStore.removeAll();

            // clear the panel
            if (devConfPanel != null) {
                devConfPanel.removeAll();
                devConfPanel.removeFromParent();

                devConfPanel = null;
                configPanel.layout();
            }

            loader.load();
        }
    }

    private void refreshVisibilityStoreSettingButton() {
        gwtDeviceManagementService.isStoreServiceEnabled(selectedDevice.getScopeId(), selectedDevice.getId(), new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable t) {
                FailureHandler.handle(t);
            }

            @Override
            public void onSuccess(Boolean enabled) {
                settings.setVisible(enabled);
            }
        });
    }

    public void refreshConfigPanel(GwtConfigComponent configComponent) {
        apply.setEnabled(false);
        reset.setEnabled(false);
        gwtSession.setFormDirty(false);

        if (devConfPanel != null) {
            devConfPanel.removeFromParent();
        }
        if (configComponent != null) {

            devConfPanel = new DeviceConfigPanel(configComponent, gwtSession, !selectedDevice.isOnline());

            devConfPanel.addListener(Events.Change, new Listener<BaseEvent>() {

                @Override
                public void handleEvent(BaseEvent be) {
                    apply.setEnabled(selectedDevice.isOnline());
                    reset.setEnabled(selectedDevice.isOnline());
                    gwtSession.setFormDirty(true);
                }
            });
            configPanel.add(devConfPanel, centerData);
            configPanel.layout();
        }
    }

    public void add() {
        if (addProcess) {
            return;
        }
        addButton.setEnabled(false);
        addProcess = true;

        DeviceConfigAddDialog dialog = new DeviceConfigAddDialog(gwtSession, selectedDevice);
        dialog.addListener(Events.Hide, new Listener<ComponentEvent>() {
            @Override
            public void handleEvent(ComponentEvent be) {
                addProcess = false;
                addButton.setEnabled(true);

                // Show exit popup
                ActionDialog dialog = be.getComponent();
                Boolean exitStatus = dialog.getExitStatus();
                if (exitStatus == null) {
                    return;
                }
                if (exitStatus) {
                    ConsoleInfo.display(MSGS.popupInfo(), dialog.getExitMessage());
                } else {
                    ConsoleInfo.display(MSGS.popupError(), dialog.getExitMessage());
                }
            }
        });
        dialog.show();
    }

    public void apply() {
        if (!devConfPanel.isValid()) {
            InfoDialog exitDialog = new InfoDialog(InfoDialogType.ERROR, DEVICE_MSGS.deviceConfigError());
            exitDialog.show();

            return;
        }

        // ask for confirmation
        String componentName = devConfPanel.getConfiguration().getComponentName();
        String message = DEVICE_MSGS.deviceConfigConfirmation();
        final boolean isCloudUpdate = "CloudService".equals(componentName);
        if (isCloudUpdate) {
            message = DEVICE_MSGS.deviceCloudConfigConfirmation(componentName);
        }

        final DeviceConfigPanel finalDevConfPanel = devConfPanel;

        KapuaMessageBox.confirm(MSGS.confirm(),
                message,
                new Listener<MessageBoxEvent>() {

                    @Override
                    public void handleEvent(MessageBoxEvent ce) {

                        // if confirmed, push the update
                        // if confirmed, delete
                        Dialog dialog = ce.getDialog();
                        if (dialog.yesText.equals(ce.getButtonClicked().getText())) {

                            configPanel.mask(MSGS.loading());
                            apply.setEnabled(false);
                            reset.setEnabled(false);
                            refreshButton.setEnabled(false);
                            gwtSession.setFormDirty(false);
                            // Getting XSRF token
                            gwtXSRFService.generateSecurityToken(new AsyncCallback<GwtXSRFToken>() {

                                @Override
                                public void onFailure(Throwable ex) {
                                    FailureHandler.handle(ex);
                                }

                                @Override
                                public void onSuccess(GwtXSRFToken token) {
                                    GwtConfigComponent configComponent = finalDevConfPanel.getUpdatedConfiguration();
                                    gwtDeviceManagementService.updateComponentConfiguration(token, selectedDevice, configComponent, applyConfigCallback);
                                }
                            });

                            // start the configuration update
                        }
                    }
                });
    }

    public void reset() {
        final GwtConfigComponent comp = (GwtConfigComponent) tree.getSelectionModel().getSelectedItem();
        if (devConfPanel != null && comp != null) {
            KapuaMessageBox.confirm(MSGS.confirm(),
                    DEVICE_MSGS.deviceConfigDirty(),
                    new Listener<MessageBoxEvent>() {

                        @Override
                        public void handleEvent(MessageBoxEvent ce) {
                            // if confirmed, delete
                            Dialog dialog = ce.getDialog();
                            if (dialog.yesText.equals(ce.getButtonClicked().getText())) {
                                refreshConfigPanel(comp);
                            }
                        }
                    });
        }
    }

    private ModelStringProvider<ModelData> modelStringProvider = new ModelStringProvider<ModelData>() {

        @Override
        public String getStringValue(ModelData model, String property) {

            KapuaIcon kapuaIcon = null;
            if (model instanceof GwtConfigComponent) {
                String iconName = ((GwtConfigComponent) model).getComponentIcon();

                if (iconName != null) {
                    if (iconName.startsWith("BluetoothService")) {
                        kapuaIcon = new KapuaIcon(IconSet.BTC);
                    } else if (iconName.startsWith("CloudService")) {
                        kapuaIcon = new KapuaIcon(IconSet.CLOUD);
                    } else if (iconName.startsWith("DiagnosticsService")) {
                        kapuaIcon = new KapuaIcon(IconSet.AMBULANCE);
                    } else if (iconName.startsWith("ClockService")) {
                        kapuaIcon = new KapuaIcon(IconSet.CLOCK_O);
                    } else if (iconName.startsWith("DataService")) {
                        kapuaIcon = new KapuaIcon(IconSet.DATABASE);
                    } else if (iconName.startsWith("MqttDataTransport")) {
                        kapuaIcon = new KapuaIcon(IconSet.FORUMBEE);
                    } else if (iconName.startsWith("PositionService")) {
                        kapuaIcon = new KapuaIcon(IconSet.LOCATION_ARROW);
                    } else if (iconName.startsWith("WatchdogService")) {
                        kapuaIcon = new KapuaIcon(IconSet.HEARTBEAT);
                    } else if (iconName.startsWith("SslManagerService")) {
                        kapuaIcon = new KapuaIcon(IconSet.LOCK);
                    } else if (iconName.startsWith("VpnService")) {
                        kapuaIcon = new KapuaIcon(IconSet.CONNECTDEVELOP);
                    } else if (iconName.startsWith("ProvisioningService")) {
                        kapuaIcon = new KapuaIcon(IconSet.EXCLAMATION_CIRCLE);
                    } else if (iconName.startsWith("CommandPasswordService")) {
                        kapuaIcon = new KapuaIcon(IconSet.CHAIN);
                    } else if (iconName.startsWith("WebConsole")) {
                        kapuaIcon = new KapuaIcon(IconSet.LAPTOP);
                    } else if (iconName.startsWith("CommandService")) {
                        kapuaIcon = new KapuaIcon(IconSet.TERMINAL);
                    } else if (iconName.startsWith("DenaliService")) {
                        kapuaIcon = new KapuaIcon(IconSet.SPINNER);
                    } else {
                        kapuaIcon = new KapuaIcon(IconSet.PUZZLE_PIECE);
                    }
                }
            }

            Label label = new Label(((GwtConfigComponent) model).getComponentName(), kapuaIcon);

            return label.getText();
        }
    };

    // --------------------------------------------------------------------------------------
    // Data Load Listener
    // --------------------------------------------------------------------------------------

    private class DataLoadListener extends KapuaLoadListener {

        @Override
        public void loaderBeforeLoad(LoadEvent le) {
            super.loaderBeforeLoad(le);
            refreshButton.disable();
        }

        @Override
        public void loaderLoad(LoadEvent le) {
            if (le.exception != null) {
                FailureHandler.handle(le.exception);
            }
            configPanel.unmask();
            refreshButton.enable();
        }

        @Override
        public void loaderLoadException(LoadEvent le) {
            if (le.exception instanceof GwtKapuaException) {
                FailureHandler.handle(le.exception);
            }

            treeStore.removeAll();

            GwtConfigComponent comp = new GwtConfigComponent();
            comp.setId(DEVICE_MSGS.deviceNoDeviceSelected());
            comp.setName(DEVICE_MSGS.deviceNoComponents());
            comp.setDescription(DEVICE_MSGS.deviceNoConfigSupported());

            List<ModelData> comps = new ArrayList<ModelData>();
            comps.add(comp);
            treeStore.add(comps, false);

            configPanel.unmask();
            refreshButton.enable();
        }
    }
}
