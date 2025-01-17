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
package org.eclipse.kapua.service.authentication.token.shiro;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.token.AccessToken;
import org.eclipse.kapua.service.authentication.token.AccessTokenFactory;
import org.eclipse.kapua.service.authentication.token.LoginInfo;

/**
 * {@link AccessTokenFactory} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class AccessTokenFactoryImpl implements AccessTokenFactory {

    @Override
    public AccessToken newEntity(KapuaId scopeId) {
        return new AccessTokenImpl(scopeId);
    }

    @Override
    public AccessToken clone(AccessToken accessToken) {
        try {
            return new AccessTokenImpl(accessToken);
        } catch (Exception e) {
            throw new KapuaEntityCloneException(e, AccessToken.TYPE, accessToken);
        }
    }

    @Override
    public LoginInfo newLoginInfo() {
        return new LoginInfoImpl();
    }

}
