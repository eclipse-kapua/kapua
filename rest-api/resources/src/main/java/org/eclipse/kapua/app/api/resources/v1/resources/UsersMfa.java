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
package org.eclipse.kapua.app.api.resources.v1.resources;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.rest.model.EntityId;
import org.eclipse.kapua.commons.rest.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOption;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOptionService;
import org.eclipse.kapua.service.authentication.credential.mfa.shiro.MfaOptionCreatorImpl;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("{scopeId}/users")
public class UsersMfa extends AbstractKapuaResource {

    @Inject
    public MfaOptionService mfaOptionService;

    /**
     * Creates a new {@link MfaOption} for the user specified by the "userId" path parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to create the {@link MfaOption}
     * @param userId
     *         The {@link EntityId} of the User to which the {@link MfaOption} belongs
     * @return The newly created {@link MfaOption} object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.4.0
     * @deprecated since 2.0.0 - use POST {scopeId}/user/mfa instead (see {@link UserMfa})
     */
    @POST
    @Path("{userId}/mfa")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Deprecated
    public Response createMfa(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("userId") EntityId userId) throws KapuaException {
        return returnCreated(mfaOptionService.create(new MfaOptionCreatorImpl(scopeId, userId)));
    }

    /**
     * Returns the {@link MfaOption} of the user specified by the "userId" path parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} of the requested {@link MfaOption}
     * @param userId
     *         The {@link EntityId} of the User to which the {@link MfaOption} belongs
     * @return The requested {@link MfaOption} object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.4.0
     */
    @GET
    @Path("{userId}/mfa")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public MfaOption findMfa(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("userId") EntityId userId) throws KapuaException {
        MfaOption mfaOption = mfaOptionService.findByUserId(scopeId, userId);
        if (mfaOption == null) {
            throw new KapuaEntityNotFoundException(MfaOption.TYPE, "MfaOption");  // TODO: not sure "MfaOption" it's the best value to return here
        }

        return mfaOption;
    }

    /**
     * Deletes the {@link MfaOption} of the user specified by the "userId" path parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} of the requested {@link MfaOption}
     * @param userId
     *         The {@link EntityId} of the User to which the {@link MfaOption} belongs
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.4.0
     */
    @DELETE
    @Path("{userId}/mfa")
    public Response deleteMfa(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("userId") EntityId userId) throws KapuaException {
        mfaOptionService.deleteByUserId(scopeId, userId);

        return returnNoContent();
    }

    /**
     * Disable trusted machine for a given {@link MfaOption}.
     *
     * @param scopeId
     *         The ScopeId of the requested {@link MfaOption}.
     * @param userId
     *         The {@link EntityId} of the User to which the {@link MfaOption} belongs
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.4.0
     */
    @DELETE
    @Path("{userId}/mfa/disableTrust")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response disableTrust(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("userId") EntityId userId) throws KapuaException {
        mfaOptionService.disableTrustByUserId(scopeId, userId);

        return returnNoContent();
    }
}
