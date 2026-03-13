/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authentication;

import org.eclipse.kapua.model.id.KapuaId;

import java.security.Principal;

/**
 * Kapua {@link Principal} implementation.<br>
 * Uniquely identifies a user.
 *
 * @since 1.0
 */
// TODO it's an object used by both authorization and authentication... should leave it in authentication module?
public interface KapuaPrincipal extends Principal, java.io.Serializable {

    /**
     * Return the token identifier
     *
     * @return
     */
    String getTokenId();

    /**
     * Return the user id
     *
     * @return
     */
    KapuaId getUserId();

    /**
     * Return the account it
     *
     * @return
     */
    KapuaId getAccountId();

    /**
     * Return the remote client ip from which the user should be connected
     *
     * @return
     */
    String getClientIp();

    /**
     * Return the client identifiers from which the user should be connected
     *
     * @return
     */
    String getClientId();

    /**
     * Return the client connection Id from which the user should be connected
     *
     * @return
     */
    String getConnectionId();

    /**
     * Returns true if the principal belongs to a user connected throug an internal connector
     *
     * @return
     */
    boolean isInternal();
}
