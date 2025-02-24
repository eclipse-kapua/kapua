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

import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authorization.AuthorizationService;

@Path("{scopeId}/aaaaaaaaaa/claims")
public class Aaaaaaaaaa extends AbstractKapuaResource {

    @Inject
    private AuthorizationService authorizationService;

    /**
     * Gets the list of user's claims in the scope.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @return The list of all the available claims associated to the current selected scope (and user).
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Set<String> fetchClaims(
            @PathParam("scopeId") ScopeId scopeId
    ) throws KapuaException {
        return authorizationService.fetchUserClaims(scopeId);
    }

}
