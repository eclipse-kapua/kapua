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
package org.eclipse.kapua.app.console.module.device.client.device.group;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaErrorCode;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityAddEditDialog;
import org.eclipse.kapua.app.console.module.api.client.ui.panel.FormPanel;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;
import org.eclipse.kapua.app.console.module.api.client.util.FailureHandler;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceService;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceServiceAsync;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DeviceGroupAddDialog extends EntityAddEditDialog {

    private static final GwtDeviceServiceAsync GWT_DEVICE_SERVICE = GWT.create(GwtDeviceService.class);

    private final GwtDevice selectedDevice;

    private ComboBox<GwtGroup> groupsCombo;

    public DeviceGroupAddDialog(GwtSession currentSession, GwtDevice selectedDevice) {
        super(currentSession);

        this.selectedDevice = selectedDevice;

        DialogUtils.resizeDialog(this, 400, 150);
    }

    @Override
    public void submit() {

        String groupId = groupsCombo.getValue().getId();

        GWT_DEVICE_SERVICE.addDeviceGroup(xsrfToken, selectedDevice.getScopeId(), selectedDevice.getId(), groupId, new AsyncCallback<Void>() {

            @Override
            public void onSuccess(Void gwtAccessPermission) {
                exitStatus = true;
                exitMessage = "Group successfully assigned";
                hide();
            }

            @Override
            public void onFailure(Throwable cause) {
                unmask();
                submitButton.enable();
                cancelButton.enable();
                status.hide();
                exitStatus = false;
                if (!isPermissionErrorMessage(cause)) {
                    if (cause instanceof GwtKapuaException) {
                        GwtKapuaException gwtCause = (GwtKapuaException) cause;
                        if (gwtCause.getCode().equals(GwtKapuaErrorCode.DUPLICATE_NAME)) {
                            groupsCombo.markInvalid(gwtCause.getMessage());
                        } else if (gwtCause.getCode().equals(GwtKapuaErrorCode.ENTITY_NOT_FOUND)) {
                            groupsCombo.markInvalid(gwtCause.getMessage());
                        }
                    }
                    FailureHandler.handleFormException(formPanel, cause);
                }
            }
        });
    }

    @Override
    public String getHeaderMessage() {
        return "Assign Group";
    }

    @Override
    public String getInfoMessage() {
        return "Assign a Group to the device";
    }

    @Override
    public void createBody() {
        submitButton.disable();

        FormPanel formPanel = new FormPanel(FORM_LABEL_WIDTH);
        Listener<BaseEvent> comboBoxListener = new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                DeviceGroupAddDialog.this.formPanel.fireEvent(Events.OnClick);
            }
        };
        // Groups
        groupsCombo = new ComboBox<GwtGroup>();
        groupsCombo.setStore(new ListStore<GwtGroup>());
        groupsCombo.setEditable(false);
        groupsCombo.setTypeAhead(false);
        groupsCombo.setAllowBlank(false);
        groupsCombo.disable();
        groupsCombo.setEmptyText("Select a Group");
        groupsCombo.setFieldLabel("* Group");
        groupsCombo.setToolTip("Select a Group from the list to be assigned to the Device.");
        groupsCombo.setTriggerAction(TriggerAction.ALL);
        groupsCombo.setDisplayField("groupName");
        groupsCombo.setTemplate("<tpl for=\".\"><div role=\"listitem\" class=\"x-combo-list-item\" title={groupName}>{groupName}</div></tpl>");
        groupsCombo.addListener(Events.Select, comboBoxListener);

        GWT_DEVICE_SERVICE.findAllGroups(selectedDevice.getScopeId(), new AsyncCallback<List<GwtGroup>>() {

            @Override
            public void onFailure(Throwable caught) {
                exitStatus = false;
                if (!isPermissionErrorMessage(caught)) {
                    exitMessage = "Unable to load Group list: " + caught.getLocalizedMessage();
                }
                hide();
            }

            @Override
            public void onSuccess(List<GwtGroup> result) {
                Collections.sort(result, new Comparator<GwtGroup>() {

                    @Override
                    public int compare(GwtGroup group1, GwtGroup group2) {
                        return group1.getGroupName().compareTo(group2.getGroupName());
                    }
                });
                groupsCombo.getStore().add(result);

                groupsCombo.setEmptyText(result.isEmpty() ? "No Groups found..." : "Select a group");

                groupsCombo.enable();
            }
        });

        formPanel.add(groupsCombo);

        bodyPanel.add(formPanel);
    }

    @Override
    protected void onRender(Element parent, int pos) {
        super.onRender(parent, pos);
    }
}
