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
package org.eclipse.kapua.app.console.module.device.client.device.inventory.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityDeleteDialog;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.module.device.shared.model.management.inventory.GwtInventoryImage;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceInventoryManagementService;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceInventoryManagementServiceAsync;

public class InventoryImageDeleteDialog extends EntityDeleteDialog {

    private static final GwtDeviceInventoryManagementServiceAsync GWT_DEVICE_INVENTORY_MANAGEMENT_SERVICE = GWT.create(GwtDeviceInventoryManagementService.class);

    private GwtDevice gwtDevice;
    private GwtInventoryImage gwtInventoryImage;

    public InventoryImageDeleteDialog(GwtDevice gwtDevice, GwtInventoryImage gwtInventoryImage) {
        this.gwtDevice = gwtDevice;
        this.gwtInventoryImage = gwtInventoryImage;

        DialogUtils.resizeDialog(this, 300, 120);
    }

    @Override
    public String getHeaderMessage() {
        return "Delete inventory image: " + gwtInventoryImage.getName();
    }

    @Override
    public String getInfoMessage() {
        return "Are you sure to delete image item? If used by a container (even a stopped one) it will not be deleted on the device.";
    }

    @Override
    public void submit() {
        GWT_DEVICE_INVENTORY_MANAGEMENT_SERVICE.deleteDeviceImage(xsrfToken,
                gwtDevice.getScopeId(),
                gwtDevice.getId(),
                gwtInventoryImage,
                new AsyncCallback<Void>() {

                    @Override
                    public void onSuccess(Void arg0) {
                        exitStatus = true;
                        exitMessage = "Image deleted!";
                        hide();
                    }

                    @Override
                    public void onFailure(Throwable cause) {
                        exitStatus = false;
                        exitMessage = "Error while deleting image: " + cause.getMessage();
                        hide();
                    }
                });
    }
}
