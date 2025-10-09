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
package org.eclipse.kapua.app.console.module.authorization.client.tabs.groups;

import com.google.gwt.user.client.Element;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.IconSet;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.KapuaIcon;
import org.eclipse.kapua.app.console.module.api.client.ui.tab.KapuaTabItem;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;

public class GroupTabTags extends KapuaTabItem<GwtGroup> {

    private GroupTagGrid groupTagGrid;

    public GroupTabTags(GwtSession currentSession) {
        super(currentSession, "Tags", new KapuaIcon(IconSet.TAGS));
        groupTagGrid = new GroupTagGrid(null, currentSession, selectedEntity);
        setEnabled(false);
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        add(groupTagGrid);
        setBorders(false);
    }

    @Override
    public void setEntity(GwtGroup selectedGroup) {
        super.setEntity(selectedGroup);
        groupTagGrid.setSelectedGroup(selectedGroup);
        setEnabled(selectedGroup != null);
    }

    @Override
    protected void doRefresh() {
        groupTagGrid.refresh();
    }
}
