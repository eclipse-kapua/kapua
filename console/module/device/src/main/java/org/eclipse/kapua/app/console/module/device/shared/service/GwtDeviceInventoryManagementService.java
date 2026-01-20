/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.device.shared.model.management.inventory.GwtInventoryBundle;
import org.eclipse.kapua.app.console.module.device.shared.model.management.inventory.GwtInventoryContainer;
import org.eclipse.kapua.app.console.module.device.shared.model.management.inventory.GwtInventoryDeploymentPackage;
import org.eclipse.kapua.app.console.module.device.shared.model.management.inventory.GwtInventoryImage;
import org.eclipse.kapua.app.console.module.device.shared.model.management.inventory.GwtInventoryItem;
import org.eclipse.kapua.app.console.module.device.shared.model.management.inventory.GwtInventorySystemPackage;

import java.util.List;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("deviceManagementInventory")
public interface GwtDeviceInventoryManagementService extends RemoteService {

    ListLoadResult<GwtInventoryItem> findDeviceInventory(String scopeIdString, String deviceIdString)
            throws GwtKapuaException;

    ListLoadResult<GwtInventoryBundle> findDeviceBundles(String scopeIdString, String deviceIdString)
            throws GwtKapuaException;

    void execDeviceBundle(GwtXSRFToken xsrfToken, String scopeIdString, String deviceIdString, GwtInventoryBundle gwtInventoryBundle, boolean startOrStop)
            throws GwtKapuaException;

    ListLoadResult<GwtInventoryContainer> findDeviceContainers(String scopeIdString, String deviceIdString)
            throws GwtKapuaException;

    ListLoadResult<GwtInventoryImage> findDeviceImages(String scopeIdString, String deviceIdString)
            throws GwtKapuaException;

    void deleteDeviceImage(GwtXSRFToken xsrfToken, String scopeIdString, String deviceIdString, GwtInventoryImage gwtInventoryImage)
            throws GwtKapuaException;

    void execDeviceContainer(GwtXSRFToken xsrfToken, String scopeIdString, String deviceIdString, GwtInventoryContainer gwtInventoryContainer, boolean startOrStop)
            throws GwtKapuaException;

    ListLoadResult<GwtInventorySystemPackage> findDeviceSystemPackages(String scopeIdString, String deviceIdString)
            throws GwtKapuaException;

    List<GwtInventoryDeploymentPackage> findDeviceDeploymentPackages(String scopeIdString, String deviceIdString)
            throws GwtKapuaException;
}
