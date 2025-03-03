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
package org.eclipse.kapua.app.console.module.device.client.connection.toolbar;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.google.gwt.user.client.Element;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.IconSet;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.KapuaIcon;
import org.eclipse.kapua.app.console.module.api.client.ui.button.KapuaButton;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.KapuaDialog;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.EntityCRUDToolbar;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.device.client.connection.ConnectionEditDialog;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnection;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnectionStatus;
import org.eclipse.kapua.app.console.module.device.shared.model.permission.DeviceConnectionSessionPermission;

public class ConnectionGridToolbar extends EntityCRUDToolbar<GwtDeviceConnection> {

    private KapuaButton disconnectButton;

    public ConnectionGridToolbar(GwtSession currentSession) {
        super(currentSession);

        setAddButtonVisible(false);
        setEditButtonVisible(true);
        setDeleteButtonVisible(false);
    }

    @Override
    protected void onRender(Element target, int index) {

        disconnectButton = new KapuaButton("Disconnect", new KapuaIcon(IconSet.CHAIN_BROKEN), new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                DeviceConnectionDisconnectDialog dialog = new DeviceConnectionDisconnectDialog(gridSelectionModel.getSelectedItem());
                dialog.addListener(Events.Hide, getHideDialogListener());
                dialog.show();
            }
        });
        disconnectButton.disable();

        if (currentSession.hasPermission(DeviceConnectionSessionPermission.write())) {
            addExtraButton(disconnectButton);
        }

        super.onRender(target, index);
    }

    @Override
    protected KapuaDialog getAddDialog() {
        return null;
    }

    @Override
    protected KapuaDialog getEditDialog() {
        GwtDeviceConnection selectedConnection = gridSelectionModel.getSelectedItem();
        ConnectionEditDialog dialog = null;
        if (selectedConnection != null) {
            dialog = new ConnectionEditDialog(currentSession, selectedConnection);
        }
        return dialog;
    }

    @Override
    protected KapuaDialog getDeleteDialog() {
        return null;
    }

    @Override
    protected void updateButtonEnablement() {
        super.updateButtonEnablement();

        getEditEntityButton().setEnabled(
            currentSession.hasPermission(DeviceConnectionSessionPermission.write()) &&
            selectedEntity != null
        );

        disconnectButton.setEnabled(
            currentSession.hasPermission(DeviceConnectionSessionPermission.write()) &&
            selectedEntity != null &&
            GwtDeviceConnectionStatus.CONNECTED.equals(selectedEntity.getConnectionStatusEnum())
        );
    }
}
