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
package org.eclipse.kapua.service.authentication.token;

import java.util.Date;

import org.eclipse.kapua.model.KapuaEntityFactory;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link AccessTokenFactory} definition.
 *
 * @see org.eclipse.kapua.model.KapuaEntityFactory
 * @since 1.0.0
 */
public interface AccessTokenFactory extends KapuaEntityFactory<AccessToken, AccessTokenCreator, AccessTokenQuery> {

    /**
     * Instantiates a new {@link AccessTokenCreator}.
     *
     * @param scopeId
     *         The scope {@link KapuaId} to set into the{@link AccessToken}.
     * @param userId
     *         The {@link org.eclipse.kapua.service.user.User} {@link KapuaId} to set into the{@link AccessToken}.
     * @param tokenId
     *         The token id to set into the{@link AccessToken}.
     * @param expiresOn
     *         The expiration date to set into the{@link AccessToken}.
     * @param refreshToken
     *         The refresh token to set into the{@link AccessToken}.
     * @return The newly instantiated {@link AccessTokenCreator}.
     * @since 1.0.0
     */
    AccessTokenCreator newCreator(KapuaId scopeId, KapuaId userId, String tokenId, Date expiresOn, String refreshToken, Date refreshExpiresOn, String tokenIdentifier);

    /**
     * Instantiates a new {@link LoginInfo}
     *
     * @return a new {@link LoginInfo} object
     */
    LoginInfo newLoginInfo();

}
