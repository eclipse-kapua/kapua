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
package org.eclipse.kapua.app.console.module.user.shared.service;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupPermission;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupPermissionCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupPermissionQuery;
import org.eclipse.kapua.app.console.module.authorization.shared.model.as.GwtGroupRole;
import org.eclipse.kapua.app.console.module.authorization.shared.model.as.GwtGroupRoleCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.model.as.GwtGroupRoleQuery;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("userGroup")
public interface GwtUserGroupService extends RemoteService {


    //
    // Group Permission

    GwtGroupPermission createPermission(GwtXSRFToken xsfrToken, GwtGroupPermissionCreator gwtGroupPermissionCreator) throws GwtKapuaException;

    void deletePermission(GwtXSRFToken xsfrToken, String accountId, String gwtGroupPermissionId) throws GwtKapuaException;

    PagingLoadResult<GwtGroupPermission> queryPermission(PagingLoadConfig loadConfig, GwtGroupPermissionQuery gwtUserGroupPermissionQuery) throws GwtKapuaException;

    //
    // Group Roles
    GwtGroupRole createRole(GwtXSRFToken xsfrToken, GwtGroupRoleCreator gwtGroupRoleCreator) throws GwtKapuaException;

    void deleteRole(GwtXSRFToken xsfrToken, String accountId, String gwtGroupRoleId) throws GwtKapuaException;

    PagingLoadResult<GwtGroupRole> queryRole(PagingLoadConfig loadConfig, GwtGroupRoleQuery gwtGroupRoleQuery) throws GwtKapuaException;

}
