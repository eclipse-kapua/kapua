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
package org.eclipse.kapua.app.console.module.authorization.shared.service;

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

import java.util.List;

@RemoteServiceRelativePath("group")
public interface GwtGroupService extends RemoteService {

    GwtGroup create(GwtGroupCreator gwtGroupCreator) throws GwtKapuaException;

    GwtGroup update(GwtGroup gwtGroup) throws GwtKapuaException;

    GwtGroup find(String scopeShortId, String roleShortId) throws GwtKapuaException;

    PagingLoadResult<GwtGroup> query(PagingLoadConfig loadConfig, GwtGroupQuery gwtGroupQuery) throws GwtKapuaException;

    void delete(String scopeId, String groupId) throws GwtKapuaException;

    ListLoadResult<GwtGroupedNVPair> getGroupDescription(String scopeShortId, String groupShortId) throws GwtKapuaException;

    List<GwtGroup> findAll(String scopeId) throws GwtKapuaException;

    PagingLoadResult<GwtGroup> findByDeviceId(PagingLoadConfig loadConfig, String gwtScopeId, String gwtDeviceId) throws GwtKapuaException;

    PagingLoadResult<GwtGroup> findByUserId(PagingLoadConfig loadConfig, String scopeIdString, String userIdString) throws GwtKapuaException;

}
