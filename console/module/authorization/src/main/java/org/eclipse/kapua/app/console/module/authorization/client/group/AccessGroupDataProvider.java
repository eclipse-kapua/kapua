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
package org.eclipse.kapua.app.console.module.authorization.client.group;

import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaErrorCode;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityAddEditDialog;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityDeleteDialog;
import org.eclipse.kapua.app.console.module.api.client.util.FailureHandler;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtGroupedNVPair;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSessionPermission;
import org.eclipse.kapua.app.console.module.authorization.client.messages.ConsoleGroupMessages;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupQuery;
import org.eclipse.kapua.app.console.module.authorization.shared.model.permission.GroupSessionPermission;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtGroupService;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtGroupServiceAsync;

public class AccessGroupDataProvider implements EntityGroupDataProvider<GwtGroup, GwtGroupQuery> {

    private static final ConsoleGroupMessages GROUP_MSGS = GWT.create(ConsoleGroupMessages.class);

    private static final GwtGroupServiceAsync GWT_GROUP_SERVICE = GWT.create(GwtGroupService.class);

    public RpcProxy<PagingLoadResult<GwtGroup>> getEntityGridDataProxy(final GwtGroupQuery query) {
        return new RpcProxy<PagingLoadResult<GwtGroup>>() {

            @Override
            protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<GwtGroup>> callback) {
                GWT_GROUP_SERVICE.query((PagingLoadConfig) loadConfig, query, callback);
            }
        };
    }

    @Override
    public String getAssignableDomain() {
        return null;
    }

    @Override
    public void getEntityDescriptionTabResults(GwtGroup selectedEntity, Object loadConfig, AsyncCallback<ListLoadResult<GwtGroupedNVPair>> callback) {
        GWT_GROUP_SERVICE.getGroupDescription(selectedEntity.getScopeId(), selectedEntity.getId(), callback);
    }

    @Override
    public GwtSessionPermission getEntityGroupAddSessionPermission() {
        return GroupSessionPermission.write();
    }

    @Override
    public void handleCreateEntityGroup(EntityAddEditDialog entityAddDialog, GwtGroupCreator gwtGroupCreator) {
        final GroupAddDialog groupAddDialog = (GroupAddDialog) entityAddDialog;

        GWT_GROUP_SERVICE.create(gwtGroupCreator, new AsyncCallback<GwtGroup>() {

            @Override
            public void onSuccess(GwtGroup gwtGroup) {
                groupAddDialog.exitStatus = true;
                groupAddDialog.exitMessage = GROUP_MSGS.dialogAddConfirmation();
                groupAddDialog.hide();
            }

            @Override
            public void onFailure(Throwable cause) {
                groupAddDialog.exitStatus = false;
                groupAddDialog.status.hide();
                groupAddDialog.formPanel.getButtonBar().enable();
                groupAddDialog.unmask();
                groupAddDialog.submitButton.enable();
                groupAddDialog.cancelButton.enable();

                if (!groupAddDialog.isPermissionErrorMessage(cause)) {
                    if (cause instanceof GwtKapuaException) {
                        GwtKapuaException gwtCause = (GwtKapuaException) cause;
                        if (gwtCause.getCode().equals(GwtKapuaErrorCode.DUPLICATE_NAME)) {
                            groupAddDialog.groupNameField.markInvalid(gwtCause.getMessage());
                        }
                    }
                    FailureHandler.handleFormException(groupAddDialog.formPanel, cause);
                }
            }
        });
    }

    public void handleUpdateEntityGroup(EntityAddEditDialog entityEditDialog, GwtGroup selectedGroup) {
        final GroupEditDialog groupEditDialog = (GroupEditDialog) entityEditDialog;

        GWT_GROUP_SERVICE.update(selectedGroup, new AsyncCallback<GwtGroup>() {

            @Override
            public void onFailure(Throwable cause) {
                groupEditDialog.exitStatus = false;
                groupEditDialog.status.hide();
                groupEditDialog.formPanel.getButtonBar().enable();
                groupEditDialog.unmask();
                groupEditDialog.submitButton.enable();
                groupEditDialog.cancelButton.enable();
                if (!groupEditDialog.isPermissionErrorMessage(cause)) {
                    if (cause instanceof GwtKapuaException) {
                        GwtKapuaException gwtCause = (GwtKapuaException) cause;
                        if (gwtCause.getCode().equals(GwtKapuaErrorCode.DUPLICATE_NAME)) {
                            groupEditDialog.groupNameField.markInvalid(gwtCause.getMessage());
                        }
                    }
                    FailureHandler.handleFormException(groupEditDialog.formPanel, cause);
                }
            }

            @Override
            public void onSuccess(GwtGroup arg0) {
                groupEditDialog.exitStatus = true;
                groupEditDialog.exitMessage = GROUP_MSGS.dialogEditConfirmation();
                groupEditDialog.hide();
            }
        });
    }

    public void handleDeleteEntityGroup(EntityDeleteDialog entityDeleteDialog, GwtGroup gwtGroup) {
        final GroupDeleteDialog groupDeleteDialog = (GroupDeleteDialog) entityDeleteDialog;

        GWT_GROUP_SERVICE.delete(gwtGroup.getScopeId(), gwtGroup.getId(), new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable arg0) {
                groupDeleteDialog.exitStatus = false;
                if (!groupDeleteDialog.isPermissionErrorMessage(arg0)) {
                    groupDeleteDialog.exitMessage = GROUP_MSGS.dialogDeleteError(arg0.getLocalizedMessage());
                }
                groupDeleteDialog.hide();

            }

            @Override
            public void onSuccess(Void arg0) {
                groupDeleteDialog.exitStatus = true;
                groupDeleteDialog.exitMessage = GROUP_MSGS.dialogDeleteConfirmation();
                groupDeleteDialog.hide();
            }
        });

    }
}
