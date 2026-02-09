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
package org.eclipse.kapua.app.console.module.device.client.device.group.tabs;

import com.google.gwt.user.client.Element;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.KapuaDialog;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.client.group.AccessGroupDataProvider;
import org.eclipse.kapua.app.console.module.authorization.client.group.GroupToolbarGrid;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.module.device.shared.model.permission.DeviceSessionPermission;

public class DeviceGroupToolbar extends GroupToolbarGrid {

    private GwtDevice selectedDevice;

    public DeviceGroupToolbar(GwtSession currentSession) {
        super(currentSession, new AccessGroupDataProvider(), true);

        setEditButtonVisible(false);
    }

    public void setSelectedDevice(GwtDevice selectedDevice) {
        this.selectedDevice = selectedDevice;

        updateButtonEnablement();
    }

    @Override
    protected KapuaDialog getAddDialog() {
        DeviceGroupAddDialog dialog = null;
        if (selectedDevice != null) {
            dialog = new DeviceGroupAddDialog(currentSession, selectedDevice);
        }
        return dialog;
    }

    @Override
    protected KapuaDialog getDeleteDialog() {
        DeviceGroupDeleteDialog dialog = null;
        if (selectedEntity != null) {
            dialog = new DeviceGroupDeleteDialog(selectedDevice, selectedEntity);
        }
        return dialog;
    }

    @Override
    protected void updateButtonEnablement() {
        if (addEntityButton != null) {
            addEntityButton.setEnabled(selectedDevice != null
                    && currentSession.hasPermission(DeviceSessionPermission.write()));
        }
        if (deleteEntityButton != null) {
            deleteEntityButton.setEnabled(selectedDevice != null && selectedEntity != null
                    && currentSession.hasPermission(DeviceSessionPermission.write()));
        }
        if (refreshEntityButton != null) {
            refreshEntityButton.setEnabled(selectedDevice != null);
        }
    }

    @Override
    protected void onRender(Element target, int index) {
        super.onRender(target, index);

        setBorders(true);

        addEntityButton.setText("Assign");
        deleteEntityButton.setText("Remove");

        addEntityButton.setEnabled(
             selectedDevice != null &&
             currentSession.hasPermission(DeviceSessionPermission.write())
        );

        deleteEntityButton.setEnabled(
            selectedDevice != null &&
            selectedEntity != null &&
             currentSession.hasPermission(DeviceSessionPermission.delete())
        );
    }
}
