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
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtGroupService;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtGroupServiceAsync;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDevice;

import java.util.ArrayList;

public class DeviceGroupGrid extends GroupGrid {

    private static final GwtGroupServiceAsync GWT_GROUP_SERVICE = GWT.create(GwtGroupService.class);

    private DeviceGroupToolbar deviceGroupToolbar;
    private GwtDevice selectedDevice;

    protected DeviceGroupGrid(AbstractEntityView<GwtGroup> entityView, GwtSession currentSession, GwtDevice selectedDevice) {
        super(entityView, currentSession);

        this.selectedDevice = selectedDevice;

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
        if (selectedDevice == null) {
            refresh();
        }
    }

    @Override
    protected RpcProxy<PagingLoadResult<GwtGroup>> getDataProxy() {
        return new RpcProxy<PagingLoadResult<GwtGroup>>() {

            @Override
            protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<GwtGroup>> callback) {
                if (selectedDevice != null) {
                    GWT_GROUP_SERVICE.findByDeviceId((PagingLoadConfig) loadConfig, currentSession.getSelectedAccountId(), selectedDevice.getId(), callback);
                } else {
                    callback.onSuccess(new BasePagingLoadResult<GwtGroup>(new ArrayList<GwtGroup>()));
                }
            }
        };

    }

    @Override
    protected DeviceGroupToolbar getToolbar() {
        if (deviceGroupToolbar == null) {
            deviceGroupToolbar = new DeviceGroupToolbar(currentSession);
        }
        return deviceGroupToolbar;
    }

    public void setSelectedDevice(GwtDevice selectedDevice) {
        this.selectedDevice = selectedDevice;
        deviceGroupToolbar.setSelectedDevice(selectedDevice);
    }

}
