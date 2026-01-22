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
package org.eclipse.kapua.app.console.module.authorization.client.group;

import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityAddEditDialog;
import org.eclipse.kapua.app.console.module.api.client.ui.dialog.entity.EntityDeleteDialog;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtEntityModel;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtGroupedNVPair;
import org.eclipse.kapua.app.console.module.api.shared.model.query.GwtQuery;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSessionPermission;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupCreator;

public interface EntityGroupDataProvider<M extends GwtEntityModel, Q extends GwtQuery> {

    String getAssignableDomain();

    RpcProxy<PagingLoadResult<M>> getEntityGridDataProxy(Q query);

    void getEntityDescriptionTabResults(M selectedEntity, Object loadConfig, AsyncCallback<ListLoadResult<GwtGroupedNVPair>> callback);

    GwtSessionPermission getEntityGroupAddSessionPermission();

    void handleCreateEntityGroup(EntityAddEditDialog entityAddDialog, GwtGroupCreator gwtGroupCreator) ;

    void handleUpdateEntityGroup(EntityAddEditDialog entityEditDialog, GwtGroup selectedGroup);

    void handleDeleteEntityGroup(EntityDeleteDialog entityDeleteDialog, GwtGroup gwtGroup);
}
