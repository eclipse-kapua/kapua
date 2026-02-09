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

import org.eclipse.kapua.app.console.module.api.client.ui.dialog.KapuaDialog;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.EntityCRUDToolbar;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;

import com.google.gwt.user.client.Element;

public class GroupToolbarGrid extends EntityCRUDToolbar<GwtGroup> {

    private EntityGroupDataProvider entityGroupDataProvider;

    public GroupToolbarGrid(GwtSession currentSession, EntityGroupDataProvider entityGroupDataProvider) {
        super(currentSession);

        this.entityGroupDataProvider = entityGroupDataProvider;
    }

    public GroupToolbarGrid(GwtSession currentSession, EntityGroupDataProvider entityGroupDataProvider, boolean slaveEntity) {
        super(currentSession, slaveEntity);

        this.entityGroupDataProvider = entityGroupDataProvider;
    }

    @Override
    protected KapuaDialog getAddDialog() {
        return new GroupAddDialog(currentSession, entityGroupDataProvider);
    }

    @Override
    protected void onRender(Element target, int index) {
        super.onRender(target, index);

        getAddEntityButton().setEnabled(currentSession.hasPermission(entityGroupDataProvider.getEntityGroupAddSessionPermission()));
    }

    @Override
    protected KapuaDialog getEditDialog() {
        GwtGroup selectedGroup = gridSelectionModel.getSelectedItem();
        GroupEditDialog dialog = null;
        if (selectedGroup != null) {
            dialog = new GroupEditDialog(currentSession, entityGroupDataProvider, selectedGroup);
        }
        return dialog;
    }

    @Override
    protected KapuaDialog getDeleteDialog() {
        GwtGroup selectedGroup = gridSelectionModel.getSelectedItem();
        GroupDeleteDialog dialog = null;
        if (selectedGroup != null) {
            dialog = new GroupDeleteDialog(entityGroupDataProvider, selectedGroup);
        }
        return dialog;
    }

    protected void setEntityGroupDataProvider(EntityGroupDataProvider entityGroupDataProvider) {
        this.entityGroupDataProvider = entityGroupDataProvider;
    }
}
