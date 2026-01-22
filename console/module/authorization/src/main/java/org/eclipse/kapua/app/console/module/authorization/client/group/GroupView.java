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
import org.eclipse.kapua.app.console.module.api.client.ui.grid.EntityGrid;
import org.eclipse.kapua.app.console.module.api.client.ui.panel.EntityFilterPanel;
import org.eclipse.kapua.app.console.module.api.client.ui.view.AbstractEntityView;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.client.messages.ConsoleGroupMessages;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;

public class GroupView extends AbstractEntityView<GwtGroup> {

    private EntityGroupDataProvider entityGroupDataProvider;
    private GroupGrid groupGrid;

    private static final ConsoleGroupMessages MSGS = GWT.create(ConsoleGroupMessages.class);

    public GroupView(GwtSession gwtSession, EntityGroupDataProvider entityGroupDataProvider) {
        super(gwtSession);

        this.entityGroupDataProvider = entityGroupDataProvider;
    }

    public static String getName() {
        return MSGS.groups();
    }

    @Override
    public EntityGrid<GwtGroup> getEntityGrid(AbstractEntityView<GwtGroup> entityView, GwtSession currentSession) {
        if (groupGrid == null) {
            groupGrid = new GroupGrid(entityView, entityGroupDataProvider, currentSession);
        }

        return groupGrid;
    }

    @Override
    public EntityFilterPanel<GwtGroup> getEntityFilterPanel(AbstractEntityView<GwtGroup> entityView, GwtSession currentSession) {
        return new GroupFilterPanel(this, currentSession);
    }
}
