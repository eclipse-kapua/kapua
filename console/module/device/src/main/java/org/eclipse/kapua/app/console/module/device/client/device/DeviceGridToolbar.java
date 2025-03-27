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
package org.eclipse.kapua.app.console.module.device.client.device;

import com.extjs.gxt.ui.client.event.Events;
import org.eclipse.kapua.app.console.module.api.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.IconSet;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.KapuaIcon;
import org.eclipse.kapua.app.console.module.api.client.ui.button.KapuaButton;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.KapuaDialog;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.EntityCRUDToolbar;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceQuery;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceQueryPredicates;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnectionStatus;
import org.eclipse.kapua.app.console.module.device.shared.model.permission.DeviceConnectionSessionPermission;
import org.eclipse.kapua.app.console.module.device.shared.model.permission.DeviceSessionPermission;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;

public class DeviceGridToolbar extends EntityCRUDToolbar<GwtDevice> {

    private static final ConsoleMessages MSGS = GWT.create(ConsoleMessages.class);

    private KapuaButton disconnectButton;
    private KapuaButton export;

    public DeviceGridToolbar(GwtSession currentSession) {
        super(currentSession);
    }

    @Override
    protected KapuaDialog getAddDialog() {
        return new DeviceAddDialog(currentSession);
    }

    @Override
    protected void onRender(Element target, int index) {

        disconnectButton = new KapuaButton("Disconnect", new KapuaIcon(IconSet.CHAIN_BROKEN), new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                DeviceDisconnectDialog dialog = new DeviceDisconnectDialog(gridSelectionModel.getSelectedItem());
                dialog.addListener(Events.Hide, getHideDialogListener());
                dialog.show();
            }
        });
        disconnectButton.disable();

        if (currentSession.hasPermission(DeviceConnectionSessionPermission.write())) {
            addExtraButton(disconnectButton);
        }

        export = new KapuaButton(MSGS.exportToCSV(), new KapuaIcon(IconSet.FILE_TEXT_O), new SelectionListener<ButtonEvent>() {

                    @Override
                    public void componentSelected(ButtonEvent be) {
                        export("csv");
                    }
                });
        addExtraButton(export);

        super.onRender(target, index);

        getAddEntityButton().setEnabled(currentSession.hasPermission(DeviceSessionPermission.write()));
        getEditEntityButton().disable();
    }

    @Override
    protected KapuaDialog getEditDialog() {
        DeviceEditDialog dialog = null;
        if (selectedEntity != null) {
            dialog = new DeviceEditDialog(currentSession, selectedEntity);
        }
        return dialog;
    }

    @Override
    protected KapuaDialog getDeleteDialog() {
        DeviceDeleteDialog dialog = null;
        if (selectedEntity != null) {
            dialog = new DeviceDeleteDialog(selectedEntity);
        }
        return dialog;
    }

    @Override
    protected void updateButtonEnablement() {
        super.updateButtonEnablement();

        getAddEntityButton().setEnabled(currentSession.hasPermission(DeviceSessionPermission.write()));
        getEditEntityButton().setEnabled(selectedEntity != null && currentSession.hasPermission(DeviceSessionPermission.write()) );
        getDeleteEntityButton().setEnabled(selectedEntity != null && currentSession.hasPermission(DeviceSessionPermission.delete()));

        disconnectButton.setEnabled(
                selectedEntity != null &&
                selectedEntity.getDeviceConnectionId() != null &&
                GwtDeviceConnectionStatus.CONNECTED.equals(selectedEntity.getGwtDeviceConnectionStatusEnum()) &&
                currentSession.hasPermission(DeviceConnectionSessionPermission.write()));
    }

    private void export(String format) {
        GwtDeviceQuery query = (GwtDeviceQuery) entityGrid.getFilterQuery();
        StringBuilder sbUrl = new StringBuilder("exporter_device?format=")
                .append(format)
                .append("&scopeId=")
                .append(URL.encodeQueryString(currentSession.getSelectedAccountId()));

        if (query.getPredicates().getClientId() != null && !query.getPredicates().getClientId().isEmpty()) {
            sbUrl.append("&clientId=")
                    .append(query.getPredicates().getClientId());
        }

        if (query.getPredicates().getDisplayName() != null && !query.getPredicates().getDisplayName().isEmpty()) {
            sbUrl.append("&displayName=")
                    .append(query.getPredicates().getDisplayName());
        }

        if (query.getPredicates().getSerialNumber() != null && !query.getPredicates().getSerialNumber().isEmpty()) {
            sbUrl.append("&serialNumber=")
                    .append(query.getPredicates().getSerialNumber());
        }

        if (query.getPredicates().getDeviceStatus() != null && !query.getPredicates().getDeviceStatus().equals(GwtDeviceQueryPredicates.GwtDeviceConnectionStatus.ANY.name())) {
            sbUrl.append("&deviceStatus=")
                    .append(query.getPredicates().getDeviceStatus());
        }

        if (query.getPredicates().getIotFrameworkVersion() != null && !query.getPredicates().getIotFrameworkVersion().isEmpty()) {
            sbUrl.append("&iotFrameworkVersion=")
                    .append(query.getPredicates().getIotFrameworkVersion());
        }

        if (query.getPredicates().getApplicationIdentifiers() != null && !query.getPredicates().getApplicationIdentifiers().isEmpty()) {
            sbUrl.append("&applicationIdentifiers=")
                    .append(query.getPredicates().getApplicationIdentifiers());
        }

        if (query.getPredicates().getCustomAttribute1() != null && !query.getPredicates().getCustomAttribute1().isEmpty()) {
            sbUrl.append("&customAttribute1=")
                    .append(query.getPredicates().getCustomAttribute1());
        }

        if (query.getPredicates().getCustomAttribute2() != null && !query.getPredicates().getCustomAttribute2().isEmpty()) {
            sbUrl.append("&customAttribute2=")
                    .append(query.getPredicates().getCustomAttribute2());
        }

        if (query.getPredicates().getGroupId() != null) {
            sbUrl.append("&accessGroup=")
                    .append(URL.encodeQueryString(query.getPredicates().getGroupId()));
        }

        if (query.getPredicates().getTagIds() != null && !query.getPredicates().getTagIds().isEmpty()) {
            for (String tagId : query.getPredicates().getTagIds()) {
                if (tagId != null && !tagId.isEmpty()) {
                    sbUrl.append("&tag=")
                            .append(URL.encodeQueryString(tagId));
                }
            }
        }

        Window.open(sbUrl.toString(), "_blank", "location=no");
    }

    public void setExportEnabled(boolean enabled) {
        export.setEnabled(enabled);
    }
}
