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
import org.eclipse.kapua.app.console.module.api.client.util.KapuaSafeHtmlUtils;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.client.messages.ConsoleGroupMessages;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;

public class GroupEditDialog extends GroupAddDialog {

    private static final ConsoleGroupMessages MSGS = GWT.create(ConsoleGroupMessages.class);

    private EntityGroupDataProvider entityGroupDataProvider;

    private GwtGroup selectedGroup;

    public GroupEditDialog(GwtSession currentSession, EntityGroupDataProvider entityGroupDataProvider, GwtGroup selectedGroup) {
        super(currentSession, entityGroupDataProvider);

        this.entityGroupDataProvider = entityGroupDataProvider;
        this.selectedGroup = selectedGroup;
    }

    @Override
    public void createBody() {
        super.createBody();

        if (domainsCombo != null) {
            domainsCombo.removeFromParent();
        }
        domainsLabel.show();

        populateEditDialog(selectedGroup);
    }

    @Override
    public void submit() {
        selectedGroup.setGroupName(groupNameField.getValue());
        selectedGroup.setGroupDescription(KapuaSafeHtmlUtils.htmlUnescape(groupDescriptionField.getValue()));

        entityGroupDataProvider.handleUpdateEntityGroup(this, selectedGroup);
    }

    @Override
    public String getHeaderMessage() {
        return MSGS.dialogEditHeader(selectedGroup.getGroupName());
    }

    @Override
    public String getInfoMessage() {
        return MSGS.dialogEditInfo();
    }

    private void populateEditDialog(GwtGroup gwtGroup) {
        groupNameField.setValue(gwtGroup.getGroupName());
        groupDescriptionField.setValue(gwtGroup.getUnescapedDescription());
        domainsLabel.setText(gwtGroup.getDomain());

        formPanel.clearDirtyFields();
    }
}
