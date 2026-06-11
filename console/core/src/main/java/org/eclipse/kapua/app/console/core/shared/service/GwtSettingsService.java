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
package org.eclipse.kapua.app.console.core.shared.service;

import org.eclipse.kapua.app.console.core.shared.model.GwtProductInformation;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;

/**
 * This service retrieves a subset of configuration values for front-end usage.
 */
@RemoteServiceRelativePath("settings")
public interface GwtSettingsService extends RemoteService {

    public GwtProductInformation getProductInformation();

    public String getOpenIDLoginUri() throws GwtKapuaException;

    public String getOpenIDLoginOrganizationUri() throws GwtKapuaException;

    public String getOpenIDLogoutUri(String idToken, String accountId) throws GwtKapuaException;

    public String getHomeUri() throws GwtKapuaException;

    public boolean getOpenIDEnabled();
}
