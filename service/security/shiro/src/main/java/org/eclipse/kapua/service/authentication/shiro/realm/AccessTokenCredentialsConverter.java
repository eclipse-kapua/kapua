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
package org.eclipse.kapua.service.authentication.shiro.realm;

import org.eclipse.kapua.service.authentication.AuthenticationCredentials;
import org.eclipse.kapua.service.authentication.exception.KapuaAuthenticationErrorCodes;
import org.eclipse.kapua.service.authentication.exception.KapuaAuthenticationException;
import org.eclipse.kapua.service.authentication.shiro.AccessTokenCredentialsImpl;

import com.google.common.base.Strings;

/**
 * {@link AccessTokenCredentialsImpl} {@link CredentialsConverter} implementation.
 *
 * @since 2.0.0
 */
public class AccessTokenCredentialsConverter implements CredentialsConverter {

    @Override
    public boolean canProcess(AuthenticationCredentials authenticationCredentials) {
        return authenticationCredentials instanceof AccessTokenCredentialsImpl;
    }

    @Override
    public KapuaAuthenticationToken convertToShiro(AuthenticationCredentials authenticationCredentials) throws KapuaAuthenticationException {

        AccessTokenCredentialsImpl accessTokenCredentials = authenticationCredentials instanceof AccessTokenCredentialsImpl ?
                (AccessTokenCredentialsImpl) authenticationCredentials :
                new AccessTokenCredentialsImpl((AccessTokenCredentialsImpl) authenticationCredentials);

        if (Strings.isNullOrEmpty(accessTokenCredentials.getTokenId())) {
            throw new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.INVALID_SESSION_CREDENTIALS);
        }

        return accessTokenCredentials;
    }
}
