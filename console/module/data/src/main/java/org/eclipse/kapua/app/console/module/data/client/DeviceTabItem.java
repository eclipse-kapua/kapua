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
package org.eclipse.kapua.app.console.module.data.client;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.IconSet;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.KapuaIcon;
import org.eclipse.kapua.app.console.module.api.client.ui.button.KapuaButton;
import org.eclipse.kapua.app.console.module.api.client.ui.tab.TabItem;
import org.eclipse.kapua.app.console.module.api.client.util.ConsoleInfo;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.data.client.messages.ConsoleDataMessages;
import org.eclipse.kapua.app.console.module.data.shared.model.GwtDatastoreDevice;
import org.eclipse.kapua.app.console.module.data.shared.model.GwtHeader;

import java.util.ArrayList;
import java.util.List;

public class DeviceTabItem extends TabItem {

    private static final ConsoleDataMessages MSGS = GWT.create(ConsoleDataMessages.class);

    private GwtSession currentSession;

    private DeviceTable deviceTable;

    private KapuaButton queryButton;

    private MetricsTable metricsTable;
    private ResultsTable resultsTable;

    public DeviceTabItem(GwtSession currentSession) {
        super(MSGS.deviceTabItemTitle(), null);
        this.currentSession = currentSession;
        this.setBorders(false);
        this.setLayout(new BorderLayout());
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);

        setWidth("100%");

        BorderLayoutData messageLayout = new BorderLayoutData(LayoutRegion.NORTH, 0.02f);
        messageLayout.setMargins(new Margins(5));
        Text welcomeMessage = new Text();
        welcomeMessage.setText(MSGS.deviceTabItemMessage());
        add(welcomeMessage, messageLayout);

        LayoutContainer tables = new LayoutContainer(new BorderLayout());
        BorderLayoutData tablesLayout = new BorderLayoutData(LayoutRegion.CENTER);
        tablesLayout.setMargins(new Margins(0, 5, 0, 5));
        tablesLayout.setMinSize(250);
        add(tables, tablesLayout);

        BorderLayoutData deviceLayout = new BorderLayoutData(LayoutRegion.WEST, 0.5f);
        deviceTable = new DeviceTable(currentSession);
        deviceTable.setBorders(false);
        deviceLayout.setMargins(new Margins(0, 5, 0, 0));
        deviceLayout.setSplit(true);
        deviceLayout.setMinSize(235);
        deviceTable.addSelectionChangedListener(new SelectionChangedListener<GwtDatastoreDevice>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<GwtDatastoreDevice> selectedDevice) {
                metricsTable.refresh(selectedDevice.getSelectedItem());
            }
        });
        tables.add(deviceTable, deviceLayout);

        BorderLayoutData metricLayout = new BorderLayoutData(LayoutRegion.CENTER);
        metricLayout.setMargins(new Margins(0, 0, 0, 5));
        metricsTable = new MetricsTable(currentSession, MetricsTable.Type.DEVICE);
        metricsTable.addSelectionListener(new SelectionChangedListener<GwtHeader>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<GwtHeader> se) {
                resultsTable.setMetrics(metricsTable.getSelectedMetrics());
                if (!se.getSelection().isEmpty()) {
                    queryButton.enable();
                    resultsTable.getDateRangeSelector().enable();
                } else {
                    queryButton.disable();
                    resultsTable.getDateRangeSelector().disable();
                }
            }
        });
        tables.add(metricsTable, metricLayout);

        BorderLayoutData queryButtonLayout = new BorderLayoutData(LayoutRegion.SOUTH, 0.1f);
        queryButtonLayout.setMargins(new Margins(5, 5, 15, 5));
        queryButton = new KapuaButton(MSGS.deviceTabItemQueryButtonText(), new KapuaIcon(IconSet.SEARCH), new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                List<GwtHeader> metrics = metricsTable.getSelectedMetrics();

                int binaryMetricsSelectedCount = 0;
                List<String> binaryMetricsSelected = new ArrayList<String>();
                for (GwtHeader metric : metrics) {
                    if ("binary".equals(metric.getType())) {
                        binaryMetricsSelectedCount++;
                        binaryMetricsSelected.add(metric.getName());
                    }
                }

                if (binaryMetricsSelectedCount > 0) {

                    StringBuilder invalidMetricsMessage = new StringBuilder();

                    for (int i = 0; i < binaryMetricsSelected.size() && i < 10; i ++) {
                        invalidMetricsMessage
                                .append(binaryMetricsSelected.get(i))
                                .append(", ");
                    }

                    // Removing trailing comma
                    invalidMetricsMessage.delete(invalidMetricsMessage.length() - 2, invalidMetricsMessage.length() - 1);

                    // If more than 10 metrics are invalid add "and others
                    if (binaryMetricsSelected.size() > 10) {
                        invalidMetricsMessage.append(" and more");
                    }

                    ConsoleInfo.display("Warning!", "Metrics " + invalidMetricsMessage + " do not support the search criteria. Remove the metrics from the selection and repeat the query");
                    return;
                }

                GwtDatastoreDevice gwtDevice = deviceTable.getSelectedDevice();
                resultsTable.refresh(gwtDevice, metrics);
            }
        });
        queryButton.disable();

        TableLayout queryButtonTL = new TableLayout();
        queryButtonTL.setCellPadding(0);
        LayoutContainer queryButtonContainer = new LayoutContainer(queryButtonTL);
        queryButtonContainer.add(queryButton, new TableData());
        tables.add(queryButtonContainer, queryButtonLayout);

        BorderLayoutData resultsLayout = new BorderLayoutData(LayoutRegion.SOUTH, 0.5f);
        resultsLayout.setSplit(true);
        resultsLayout.setMaxSize(Window.getClientHeight()/2);

        TabPanel resultsTabPanel = new TabPanel();
        resultsTabPanel.setPlain(true);
        resultsTabPanel.setBorders(false);
        resultsTabPanel.setBodyBorder(false);

        resultsTable = new ResultsTable(currentSession, queryButton);
        TabItem resultsTableTabItem = new TabItem(MSGS.resultsTableTabItemTitle(), new KapuaIcon(IconSet.TABLE));
        resultsTableTabItem.setLayout(new FitLayout());
        resultsTableTabItem.add(resultsTable);
        resultsTabPanel.add(resultsTableTabItem);

        ConsoleResizeHandler consoleResizeHandler = new ConsoleResizeHandler();
        consoleResizeHandler.addResizeHandler(messageLayout, 1150);

        add(resultsTabPanel, resultsLayout);
    }

    public void refreshTables() {
        if (deviceTable != null) {
            deviceTable.refresh();
        }
        if (metricsTable != null) {
            metricsTable.clearTable();
        }
        if (resultsTable != null) {
            resultsTable.clearTable();
        }
    }
}
