/*******************************************************************************
 * Copyright (c) 2025, 2025 Eurotech and/or its affiliates and others
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.IconSet;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.KapuaIcon;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityDeleteDialog;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;
import org.eclipse.kapua.app.console.module.device.shared.model.connection.GwtDeviceConnection;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceConnectionService;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceConnectionServiceAsync;

public class DeviceConnectionDisconnectDialog extends EntityDeleteDialog {

    private final GwtDeviceConnection deviceConnection;

    private static final GwtDeviceConnectionServiceAsync GWT_DEVICE_CONNECTION_SERVICE = GWT.create(GwtDeviceConnectionService.class);

    public DeviceConnectionDisconnectDialog(GwtDeviceConnection deviceConnection) {
        this.deviceConnection = deviceConnection;

        DialogUtils.resizeDialog(this, 300, 135);
        setDisabledFormPanelEvents(true);
    }

    @Override
    public void submit() {
        GWT_DEVICE_CONNECTION_SERVICE.disconnect(xsrfToken, deviceConnection.getScopeId(), deviceConnection.getId(), new AsyncCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
                exitStatus = true;
                exitMessage = "Device Connection disconnected";
                hide();
            }

            @Override
            public void onFailure(Throwable cause) {
                exitStatus = false;
                if (!isPermissionErrorMessage(cause)) {
                    exitMessage = "Failed to issue disconnect command";
                }
                hide();
            }
        });
    }

    @Override
    public String getHeaderMessage() {
        return "Disconnect: " + deviceConnection.getClientId();
    }

    @Override
    public String getInfoMessage() {
        return "Disconnect the selected Device Connection?";
    }

    @Override
    public KapuaIcon getInfoIcon() {
        return new KapuaIcon(IconSet.CHAIN_BROKEN);
    }
}
