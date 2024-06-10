/*******************************************************************************
 * Copyright (c) 2023, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.api.resources.v1.resources;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.rest.model.EntityId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.user.PasswordChangeRequest;
import org.eclipse.kapua.service.authentication.user.PasswordResetRequest;
import org.eclipse.kapua.service.authentication.user.UserCredentialsService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/*
 @deprecated
 accidentally exposed under:
 /{scopeId}/user/....
 Where the scopeId has no meaning of the current user (the one from the session will always be used)
 Remove the match with /{scopeId}/... in the next release
 */
@Path("{scopeId: ([\\w-]+)?}{path:|/}user/credentials")
public class UserCredentials extends AbstractKapuaResource {

    @Inject
    public UserCredentialsService userCredentialsService;

    /**
     * Change the user password
     *
     * @param passwordChangeRequest
     *         The {@link PasswordChangeRequest} represents the changing
     * @return The updated {@link Credential}
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     */
    @Path("password")
    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Credential newPassword(
            PasswordChangeRequest passwordChangeRequest) throws KapuaException {
        return userCredentialsService.changePassword(KapuaSecurityUtils.getSession().getScopeId(), KapuaSecurityUtils.getSession().getUserId(), passwordChangeRequest);
    }

    /**
     * Reset the password of a {@link Credential}.
     *
     * @param credentialId
     *         The id of the Credential to reset the password.
     * @param passwordResetRequest
     *         Request for resetting credential password
     * @return The updated credential.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 2.0.0
     * @deprecated since 2.0.0 - use POST /{scopeId}/users/{userId}/password/_reset instead (see {@link UsersCredentials}) It has been considered that a user might want to reset a password credential
     *         using another type of credential (e.g.: apiKey), but for security reasons (e.g.: avoid a leaked apiKey to be used to steal the whole account) only the admin's controlled password reset
     *         is left
     */
    @POST
    @Path("{credentialId}/_reset")
    @Deprecated
    public Credential unlockCredential(
            @PathParam("credentialId") EntityId credentialId,
            PasswordResetRequest passwordResetRequest) throws KapuaException {
        return userCredentialsService.resetPassword(KapuaSecurityUtils.getSession().getScopeId(), credentialId, passwordResetRequest);
    }
}
