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
package org.eclipse.kapua.app.console.module.authorization.client.tabs.groups;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.module.api.client.ui.view.AbstractEntityView;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.tag.client.TagGrid;
import org.eclipse.kapua.app.console.module.tag.shared.model.GwtTag;
import org.eclipse.kapua.app.console.module.tag.shared.service.GwtTagService;
import org.eclipse.kapua.app.console.module.tag.shared.service.GwtTagServiceAsync;

import java.util.ArrayList;

public class GroupTagGrid extends TagGrid {

    private static final ConsoleMessages MSGS = GWT.create(ConsoleMessages.class);

    private static final GwtTagServiceAsync GWT_TAG_SERVICE = GWT.create(GwtTagService.class);

    private GroupTagToolbar groupTagToolbar;
    private GwtGroup selectedGroup;

    protected GroupTagGrid(AbstractEntityView<GwtTag> entityView, GwtSession currentSession, GwtGroup selectedGroup) {
        super(entityView, currentSession);
        refreshOnRender = false;
        this.selectedGroup = selectedGroup;
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        /* Despite this grid, being a "slave" grid (i.e. a grid that depends on the value
         * selected in another grid) and so not refreshed on render (see comment in
         * EntityGrid class), it should be refreshed anyway on render if no item is
         * selected on the master grid, otherwise the paging toolbar will still be enabled
         * even if no results are actually available in this grid */
        if (selectedGroup == null) {
            refresh();
        }
    }

    @Override
    protected RpcProxy<PagingLoadResult<GwtTag>> getDataProxy() {
        return new RpcProxy<PagingLoadResult<GwtTag>>() {

            @Override
            protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<GwtTag>> callback) {
                if (selectedGroup != null) {
                    GWT_TAG_SERVICE.findByGroupId((PagingLoadConfig) loadConfig, currentSession.getSelectedAccountId(), selectedGroup.getId(), callback);
                } else {
                    callback.onSuccess(new BasePagingLoadResult<GwtTag>(new ArrayList<GwtTag>()));
                }
            }
        };

    }

    @Override
    protected GroupTagToolbar getToolbar() {
        if (groupTagToolbar == null) {
            groupTagToolbar = new GroupTagToolbar(currentSession);
        }
        return groupTagToolbar;
    }

    public void setSelectedGroup(GwtGroup selectedGroup) {
        this.selectedGroup = selectedGroup;
        groupTagToolbar.setSelectedGroup(selectedGroup);
    }

}
