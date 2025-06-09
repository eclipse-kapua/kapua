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

import org.eclipse.kapua.app.console.core.shared.model.authentication.GwtJwtCredential;
import org.eclipse.kapua.app.console.core.shared.model.authentication.GwtJwtIdToken;
import org.eclipse.kapua.app.console.core.shared.model.authentication.GwtLoginCredential;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("auth")
public interface GwtAuthorizationService extends RemoteService {

    /**
     * Logins a session based on the given credentials. If credentials are correct a session is established and returned
     *
     * @param gwtLoginCredentials
     *         The credentials to authenticate
     * @return The session info established.
     * @throws GwtKapuaException
     *         If credentials are not valid.
     * @since 1.0.0
     */
    public GwtSession login(GwtLoginCredential gwtLoginCredentials, boolean trustReq) throws GwtKapuaException;

    /**
     * Logins a session based on the given access token. If the access token is correct a session is established and returned. An id token is also passed for identity information about the user.
     *
     * @param gwtAccessTokenCredentials
     *         The access token to authenticate.
     * @param gwtJwtIdToken
     *         The id token which identifies the user.
     * @return The session info established.
     * @throws GwtKapuaException
     *         If the access token is not valid.
     * @since 1.0.0
     */
    public GwtSession login(GwtJwtCredential gwtAccessTokenCredentials, GwtJwtIdToken gwtJwtIdToken) throws GwtKapuaException;

    /**
     * Return the currently authenticated user or null if no session has been established.
     *
     * @return The current active session or null if no session is active.
     * @throws GwtKapuaException
     *         FIXME: document this
     * @since 1.0.0
     */
    public GwtSession getCurrentSession()
            throws GwtKapuaException;

    /**
     * Destroy the current active session.
     *
     * @throws GwtKapuaException
     *         FIXME: document this
     * @since 1.0.0
     */
    public void logout()
            throws GwtKapuaException;

}
