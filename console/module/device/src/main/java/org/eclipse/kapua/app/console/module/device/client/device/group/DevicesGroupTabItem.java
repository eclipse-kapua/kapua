/*******************************************************************************
 * Copyright (c) 2018, 2025 Eurotech and/or its affiliates and others
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.IconSet;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.KapuaIcon;
import org.eclipse.kapua.app.console.module.api.client.ui.tab.KapuaTabItem;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.client.messages.ConsoleGroupMessages;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;

public class DevicesGroupTabItem extends KapuaTabItem<GwtGroup> {

    private static final ConsoleGroupMessages MSGS = GWT.create(ConsoleGroupMessages.class);
    GroupSubjectGrid groupDeviceGrid;

    public DevicesGroupTabItem(GwtSession currentSession) {
        super(currentSession, MSGS.groupTabSubjectGridTitle(), new KapuaIcon(IconSet.SUPPORT));
        groupDeviceGrid = new GroupSubjectGrid(null, currentSession);
        groupDeviceGrid.setRefreshOnRender(false);
        setEnabled(false);
    }

    @Override
    protected void doRefresh() {
        if (selectedEntity != null) {
            groupDeviceGrid.refresh();
        }
    }

    @Override
    public void setEntity(GwtGroup gwtGroup) {
        super.setEntity(gwtGroup);

        setEnabled(gwtGroup != null);

        groupDeviceGrid.setEntity(gwtGroup);

    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        setBorders(false);
        add(groupDeviceGrid);
    }

}
