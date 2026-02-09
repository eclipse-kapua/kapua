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
package org.eclipse.kapua.app.console.module.user.client.tabs.groups;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.ui.view.AbstractEntityView;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.client.group.GroupGrid;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUser;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserService;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserServiceAsync;

import java.util.ArrayList;

public class UserGroupGrid extends GroupGrid {

    private static final GwtUserServiceAsync GWT_USER_SERVICE = GWT.create(GwtUserService.class);

    private UserGroupToolbar userGroupToolbar;
    private GwtUser selectedUser;

    protected UserGroupGrid(AbstractEntityView<GwtGroup> entityView, GwtSession currentSession, GwtUser selectedUser) {
        super(entityView, currentSession);

        this.selectedUser = selectedUser;

        refreshOnRender = false;
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        /* Despite this grid, being a "slave" grid (i.e. a grid that depends on the value
         * selected in another grid) and so not refreshed on render (see comment in
         * EntityGrid class), it should be refreshed anyway on render if no item is
         * selected on the master grid, otherwise the paging toolbar will still be enabled
         * even if no results are actually available in this grid */
        if (selectedUser == null) {
            refresh();
        }
    }

    @Override
    protected RpcProxy<PagingLoadResult<GwtGroup>> getDataProxy() {
        return new RpcProxy<PagingLoadResult<GwtGroup>>() {

            @Override
            protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<GwtGroup>> callback) {
                if (selectedUser != null) {
                    GWT_USER_SERVICE.findGroupsByUserId((PagingLoadConfig) loadConfig, currentSession.getSelectedAccountId(), selectedUser.getId(), callback);
                } else {
                    callback.onSuccess(new BasePagingLoadResult<GwtGroup>(new ArrayList<GwtGroup>()));
                }
            }
        };

    }

    @Override
    protected UserGroupToolbar getToolbar() {
        if (userGroupToolbar == null) {
            userGroupToolbar = new UserGroupToolbar(currentSession);
        }
        return userGroupToolbar;
    }

    public void setSelectedUser(GwtUser selectedUser) {
        this.selectedUser = selectedUser;

        userGroupToolbar.setSelectedUser(selectedUser);
    }

}
