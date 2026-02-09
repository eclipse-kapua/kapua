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
package org.eclipse.kapua.app.console.module.device.shared.service;

import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtGroupedNVPair;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupQuery;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("deviceGroup")
public interface GwtDeviceGroupService extends RemoteService {

    GwtGroup create(GwtGroupCreator gwtGroupCreator) throws GwtKapuaException;

    GwtGroup update(GwtGroup gwtGroup) throws GwtKapuaException;

    ListLoadResult<GwtGroupedNVPair> getDeviceGroupDescription(String scopeShortId, String groupShortId) throws GwtKapuaException;

    PagingLoadResult<GwtGroup> query(PagingLoadConfig loadConfig, GwtGroupQuery gwtGroupQuery) throws GwtKapuaException;

    void delete(String scopeId, String groupId) throws GwtKapuaException;
}
