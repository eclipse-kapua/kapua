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

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOption;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOptionCreator;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOptionService;

@Path("user")
public class UserMfa extends AbstractKapuaResource {

    @Inject
    public MfaOptionService mfaOptionService;

    /**
     * Creates a new {@link MfaOption} for the user specified by the "userId" path parameter.
     *
     * @return The newly created {@link MfaOption} object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 2.0.0
     */
    @POST
    @Path("mfa")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response activateMfa() throws KapuaException {

        MfaOptionCreator mfaOptionCreator = new MfaOptionCreator(KapuaSecurityUtils.getSession().getScopeId());
        mfaOptionCreator.setUserId(KapuaSecurityUtils.getSession().getUserId());

        return returnCreated(KapuaSecurityUtils.doPrivileged(() -> mfaOptionService.create(mfaOptionCreator)));
    }

    /**
     * Returns the {@link MfaOption} of the user specified by the "userId" path parameter.
     *
     * @return The requested {@link MfaOption} object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.4.0
     */
    @GET
    @Path("mfa")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public MfaOption findMfa() throws KapuaException {
        MfaOption mfaOption = KapuaSecurityUtils.doPrivileged(() -> mfaOptionService.findByUserId(KapuaSecurityUtils.getSession().getScopeId(), KapuaSecurityUtils.getSession().getUserId()));
        if (mfaOption == null) {
            throw new KapuaEntityNotFoundException(MfaOption.TYPE, "MfaOption");  // TODO: not sure "MfaOption" it's the best value to return here
        }
        return mfaOption;
    }

    /**
     * Deletes the {@link MfaOption} of the user specified by the "userId" path parameter.
     *
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.4.0
     */
    @DELETE
    @Path("mfa")
    public Response deleteMfa() throws KapuaException {
        KapuaSecurityUtils.doPrivileged(() -> mfaOptionService.deleteByUserId(KapuaSecurityUtils.getSession().getScopeId(), KapuaSecurityUtils.getSession().getUserId()));

        return returnNoContent();
    }

    /**
     * Disable trusted machine for a given {@link MfaOption}.
     *
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.4.0
     */
    @DELETE
    @Path("mfa/disableTrust")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response disableTrust() throws KapuaException {
        KapuaSecurityUtils.doPrivileged(() -> mfaOptionService.disableTrustByUserId(KapuaSecurityUtils.getSession().getScopeId(), KapuaSecurityUtils.getSession().getUserId()));
        return returnNoContent();
    }

}
