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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityDeleteDialog;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceService;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceServiceAsync;

public class DeviceGroupDeleteDialog extends EntityDeleteDialog {

    private static final GwtDeviceServiceAsync GWT_DEVICE_SERVICE = GWT.create(GwtDeviceService.class);

    private final GwtDevice selectedDevice;
    private final GwtGroup selectedGroup;

    public DeviceGroupDeleteDialog(GwtDevice selectedDevice, GwtGroup selectedGroup) {
        this.selectedDevice = selectedDevice;
        this.selectedGroup = selectedGroup;

        DialogUtils.resizeDialog(this, 300, 135);
    }

    @Override
    public String getHeaderMessage() {
        return "Remove Group: " + selectedGroup.getGroupName();
    }

    @Override
    public String getInfoMessage() {
        return "Are you sure you want to remove the selected Group?";
    }

    @Override
    public void submit() {
        GWT_DEVICE_SERVICE.deleteDeviceGroup(xsrfToken, selectedDevice.getScopeId(), selectedDevice.getId(), selectedGroup.getId(), new AsyncCallback<Void>() {

            @Override
            public void onSuccess(Void arg0) {
                exitStatus = true;
                exitMessage = "Group successfully removed";
                hide();
            }

            @Override
            public void onFailure(Throwable cause) {
                exitStatus = false;
                if (!isPermissionErrorMessage(cause)) {
                    exitMessage = "Error while removing group: " + cause.getLocalizedMessage();
                }
                hide();
            }
        });

    }
}
