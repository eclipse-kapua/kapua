/*******************************************************************************
 * Copyright (c) 2016, 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.device.client.connection;

import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.ui.grid.EntityGrid;
import org.eclipse.kapua.app.console.module.api.client.ui.panel.EntityFilterPanel;
import org.eclipse.kapua.app.console.module.api.client.ui.view.AbstractEntityView;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.KapuaTextField;
import org.eclipse.kapua.app.console.module.api.client.util.ConsoleInfo;
import org.eclipse.kapua.app.console.module.api.client.util.CssLiterals;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.device.client.messages.ConsoleConnectionMessages;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceQueryPredicates;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnection;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnectionQuery;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnectionQueryPredicates.GwtDeviceConnectionReservedUser;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnectionQueryPredicates.GwtDeviceConnectionUser;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUser;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUserQuery;
import org.eclipse.kapua.app.console.module.user.shared.model.permission.UserSessionPermission;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserService;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserServiceAsync;

public class ConnectionFilterPanel extends EntityFilterPanel<GwtDeviceConnection> {

    private static final int WIDTH = 193;
    private static final int MAX_LEN = 255;
    private static final ConsoleConnectionMessages MSGS = GWT.create(ConsoleConnectionMessages.class);
    private final GwtUserServiceAsync userService = GWT.create(GwtUserService.class);
    private EntityGrid<GwtDeviceConnection> entityGrid;
    private final GwtSession currentSession;

    private final KapuaTextField<String> clientIdField;
    private final KapuaTextField<String> clientIPFilter;
    private final SimpleComboBox<GwtDeviceQueryPredicates.GwtDeviceConnectionStatus> connectionStatusCombo;
    private final ComboBox<GwtUser> userCombo;
    private GwtUser noUser;
    private GwtUser anyUser;
    private KapuaTextField<String> protocolField;
    private final ComboBox<GwtUser> reservedUserCombo;
    Label userLabel;
    Label reservedUserLabel;

    public ConnectionFilterPanel(AbstractEntityView<GwtDeviceConnection> entityView, GwtSession currentSession) {
        super(entityView, currentSession);
        entityGrid = entityView.getEntityGrid(entityView, currentSession);
        this.currentSession = currentSession;

        VerticalPanel fieldsPanel = getFieldsPanel();
        setHeading(MSGS.connectionFilterHeader());
        Label clientIdLabel = new Label(MSGS.connectionFilterClientIdLabel());
        clientIdLabel.setWidth(WIDTH);
        clientIdLabel.setStyleAttribute(CssLiterals.MARGIN, "5px");
        fieldsPanel.add(clientIdLabel);

        clientIdField = new KapuaTextField<String>();
        clientIdField.setName("name");
        clientIdField.setWidth(WIDTH);
        clientIdField.setMaxLength(MAX_LEN);
        clientIdField.setStyleAttribute(CssLiterals.MARGIN_TOP, "0px");
        clientIdField.setStyleAttribute(CssLiterals.MARGIN_LEFT, "5px");
        clientIdField.setStyleAttribute(CssLiterals.MARGIN_RIGHT, "5px");
        clientIdField.setStyleAttribute(CssLiterals.MARGIN_BOTTOM, "10px");
        fieldsPanel.add(clientIdField);

        Label connectionStatusLabel = new Label(MSGS.connectionFilterConnectionStatus());
        connectionStatusLabel.setWidth(WIDTH);
        connectionStatusLabel.setStyleAttribute(CssLiterals.MARGIN, "5px");
        fieldsPanel.add(connectionStatusLabel);

        connectionStatusCombo = new SimpleComboBox<GwtDeviceQueryPredicates.GwtDeviceConnectionStatus>();
        connectionStatusCombo.setName("connectionStatus");
        connectionStatusCombo.setWidth(WIDTH);
        connectionStatusCombo.setStyleAttribute(CssLiterals.MARGIN_TOP, "0px");
        connectionStatusCombo.setStyleAttribute(CssLiterals.MARGIN_LEFT, "5px");
        connectionStatusCombo.setStyleAttribute(CssLiterals.MARGIN_RIGHT, "5px");
        connectionStatusCombo.setStyleAttribute(CssLiterals.MARGIN_BOTTOM, "10px");

        connectionStatusCombo.add(GwtDeviceQueryPredicates.GwtDeviceConnectionStatus.ANY);
        connectionStatusCombo.add(GwtDeviceQueryPredicates.GwtDeviceConnectionStatus.CONNECTED);
        connectionStatusCombo.add(GwtDeviceQueryPredicates.GwtDeviceConnectionStatus.MISSING);
        connectionStatusCombo.add(GwtDeviceQueryPredicates.GwtDeviceConnectionStatus.DISCONNECTED);

        connectionStatusCombo.setEditable(false);
        connectionStatusCombo.setTriggerAction(TriggerAction.ALL);
        connectionStatusCombo.setSimpleValue(GwtDeviceQueryPredicates.GwtDeviceConnectionStatus.ANY);

        fieldsPanel.add(connectionStatusCombo);

        Label clientIPFilterLabel = new Label(MSGS.connectionFilterCLientIPLabel());
        clientIPFilterLabel.setWidth(WIDTH);
        clientIPFilterLabel.setStyleAttribute(CssLiterals.MARGIN, "5px");
        fieldsPanel.add(clientIPFilterLabel);

        clientIPFilter = new KapuaTextField<String>();
        clientIPFilter.setName("Client IP");
        clientIPFilter.setWidth(WIDTH);
        clientIPFilter.setMaxLength(MAX_LEN);
        clientIPFilter.setStyleAttribute(CssLiterals.MARGIN_TOP, "0px");
        clientIPFilter.setStyleAttribute(CssLiterals.MARGIN_LEFT, "5px");
        clientIPFilter.setStyleAttribute(CssLiterals.MARGIN_RIGHT, "5px");
        clientIPFilter.setStyleAttribute(CssLiterals.MARGIN_BOTTOM, "10px");
        fieldsPanel.add(clientIPFilter);

        if (currentSession.hasPermission(UserSessionPermission.read())) {
            userLabel = new Label(MSGS.connectionFilterUser());
            userLabel.setWidth(WIDTH);
            userLabel.setStyleAttribute(CssLiterals.MARGIN, "5px");
            fieldsPanel.add(userLabel);
        }

        noUser = new GwtUser();
        noUser.setUsername("NO USER");
        noUser.setId(null);

        anyUser = new GwtUser();
        anyUser.setUsername("ANY USER");
        anyUser.setId(null);

        userCombo = new ComboBox<GwtUser>();
        userCombo.setStore(new ListStore<GwtUser>());
        userCombo.disable();
        userCombo.setEditable(false);
        userCombo.setEmptyText(MSGS.connectionFiltreUserEmptyText());
        userCombo.setDisplayField("username");
        userCombo.setValueField("id");
        userCombo.setName("userId");
        userCombo.setWidth(WIDTH);
        userCombo.setStyleAttribute(CssLiterals.MARGIN_TOP, "0px");
        userCombo.setStyleAttribute(CssLiterals.MARGIN_LEFT, "5px");
        userCombo.setStyleAttribute(CssLiterals.MARGIN_RIGHT, "5px");
        userCombo.setStyleAttribute(CssLiterals.MARGIN_BOTTOM, "10px");
        userCombo.setTriggerAction(TriggerAction.ALL);
        userCombo.setValue(anyUser);

        if (currentSession.hasPermission(UserSessionPermission.read())) {
            loadUserEntries(userCombo);

            fieldsPanel.add(userCombo);
        }

        Label protocolLabel = new Label(MSGS.connectionFilterProtocolLabel());
        protocolLabel.setWidth(WIDTH);
        protocolLabel.setStyleAttribute(CssLiterals.MARGIN, "5px");
        fieldsPanel.add(protocolLabel);

        protocolField = new KapuaTextField<String>();
        protocolField.setName("protocol");
        protocolField.setWidth(WIDTH);
        protocolField.setMaxLength(MAX_LEN);
        protocolField.setStyleAttribute(CssLiterals.MARGIN_TOP, "0px");
        protocolField.setStyleAttribute(CssLiterals.MARGIN_LEFT, "5px");
        protocolField.setStyleAttribute(CssLiterals.MARGIN_RIGHT, "5px");
        protocolField.setStyleAttribute(CssLiterals.MARGIN_BOTTOM, "10px");
        fieldsPanel.add(protocolField);

        if (currentSession.hasPermission(UserSessionPermission.read())) {
            reservedUserLabel = new Label(MSGS.connectionFilterReservedUserLabel());
            reservedUserLabel.setWidth(WIDTH);
            reservedUserLabel.setStyleAttribute(CssLiterals.MARGIN, "5px");
            fieldsPanel.add(reservedUserLabel);
        }

        reservedUserCombo = new ComboBox<GwtUser>();
        reservedUserCombo.setStore(new ListStore<GwtUser>());
        reservedUserCombo.disable();
        reservedUserCombo.setEditable(false);
        reservedUserCombo.setEmptyText(MSGS.connectionFilterReservedUserLabel());
        reservedUserCombo.setDisplayField("username");
        reservedUserCombo.setValueField("id");
        reservedUserCombo.setName("userId");
        reservedUserCombo.setWidth(WIDTH);
        reservedUserCombo.setStyleAttribute(CssLiterals.MARGIN_TOP, "0px");
        reservedUserCombo.setStyleAttribute(CssLiterals.MARGIN_LEFT, "5px");
        reservedUserCombo.setStyleAttribute(CssLiterals.MARGIN_RIGHT, "5px");
        reservedUserCombo.setStyleAttribute(CssLiterals.MARGIN_BOTTOM, "10px");
        reservedUserCombo.setTriggerAction(TriggerAction.ALL);
        reservedUserCombo.setValue(anyUser);
        if (currentSession.hasPermission(UserSessionPermission.read())) {
            loadUserEntries(reservedUserCombo);

            fieldsPanel.add(reservedUserCombo);
        }
    }

    @Override
    public void resetFields() {
        clientIdField.setValue(null);
        connectionStatusCombo.setSimpleValue(GwtDeviceQueryPredicates.GwtDeviceConnectionStatus.ANY);
        clientIPFilter.setValue(null);
        userCombo.setValue(anyUser);
        protocolField.setValue(null);
        reservedUserCombo.setValue(anyUser);
        GwtDeviceConnectionQuery query = new GwtDeviceConnectionQuery();
        query.setScopeId(currentSession.getSelectedAccountId());
        entityGrid.refresh(query);
    }

    @Override
    public void doFilter() {
        GwtDeviceConnectionQuery query = new GwtDeviceConnectionQuery();
        query.setScopeId(currentSession.getSelectedAccountId());
        query.setClientId(clientIdField.getValue());
        query.setConnectionStatus(connectionStatusCombo.getSimpleValue().toString());
        query.setClientIP(clientIPFilter.getValue());
        if (userCombo != null && !userCombo.getValue().equals(noUser) && !userCombo.getValue().equals(anyUser)) {
            query.setUserId(userCombo.getValue().getId());
            query.setGwtDeviceConnectionUser(GwtDeviceConnectionUser.ANY);
        }
        else if (userCombo != null && userCombo.getValue().equals(noUser)) {
            query.setGwtDeviceConnectionUser(GwtDeviceConnectionUser.NONE);
        }
        query.setProtocol(protocolField.getValue());
        if (reservedUserCombo != null && !reservedUserCombo.getValue().equals(noUser) && !reservedUserCombo.getValue().equals(anyUser)) {
            query.setReservedUserId(reservedUserCombo.getValue().getId());
            query.setGwtDeviceConnectionReservedUser(GwtDeviceConnectionReservedUser.ANY);
        } else if (reservedUserCombo != null && reservedUserCombo.getValue().equals(noUser)) {
            query.setGwtDeviceConnectionReservedUser(GwtDeviceConnectionReservedUser.NONE);
        }
        entityGrid.refresh(query);
    }

    private void loadUserEntries(final ComboBox<GwtUser> userComboBox) {
        GwtUserQuery userComboFilterQuery = new GwtUserQuery();
        userComboFilterQuery.setScopeId(currentSession.getAccountId());

        PagingLoadConfig loadConfig = new BasePagingLoadConfig(0, 250);

        userService.query(loadConfig, userComboFilterQuery, new AsyncCallback<PagingLoadResult<GwtUser>>() {

            @Override
            public void onSuccess(PagingLoadResult<GwtUser> arg0) {
                userComboBox.getStore().removeAll();
                userComboBox.getStore().add(noUser);
                userComboBox.getStore().add(anyUser);
                userComboBox.getStore().add(arg0.getData());
                userComboBox.setValue(anyUser);
                userComboBox.enable();

            }

            @Override
            public void onFailure(Throwable arg0) {
                userLabel.setVisible(false);
                userComboBox.setVisible(false);
                if (userComboBox.isVisible()) {
                    ConsoleInfo.display(MSGS.connectionFilteringPopUpError(), MSGS.connectionFilteringUsersError());
                }

            }
        });
    }


}
