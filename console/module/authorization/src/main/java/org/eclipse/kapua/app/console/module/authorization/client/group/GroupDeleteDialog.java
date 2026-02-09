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
package org.eclipse.kapua.app.console.module.authorization.client.group;

import com.google.gwt.core.client.GWT;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityDeleteDialog;
import org.eclipse.kapua.app.console.module.api.client.util.DialogUtils;
import org.eclipse.kapua.app.console.module.authorization.client.messages.ConsoleGroupMessages;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;

public class GroupDeleteDialog extends EntityDeleteDialog {

    private static final ConsoleGroupMessages MSGS = GWT.create(ConsoleGroupMessages.class);
    private EntityGroupDataProvider entityGroupDataProvider;
    private GwtGroup gwtGroup;

    public GroupDeleteDialog(EntityGroupDataProvider entityGroupDataProvider, GwtGroup gwtGroup) {
        this.entityGroupDataProvider = entityGroupDataProvider;
        this.gwtGroup = gwtGroup;

        DialogUtils.resizeDialog(this, 300, 135);
    }

    @Override
    public void submit() {
        entityGroupDataProvider.handleDeleteEntityGroup(this, gwtGroup);
    }

    @Override
    public String getHeaderMessage() {
        return MSGS.dialogDeleteHeader(gwtGroup.getGroupName());
    }

    @Override
    public String getInfoMessage() {
        return MSGS.dialogDeleteInfo();
    }

}
