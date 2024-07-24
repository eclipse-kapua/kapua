/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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

import java.util.Collections;
import java.util.Comparator;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationsFacade;
import org.eclipse.kapua.service.config.ServiceComponentConfiguration;
import org.eclipse.kapua.service.config.ServiceConfiguration;

@Path("{scopeId}/serviceConfigurations")
public class ServiceConfigurations extends AbstractKapuaResource {

    @Inject
    public ServiceConfigurationsFacade serviceConfigurationsFacade;

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public ServiceConfiguration get(@PathParam("scopeId") ScopeId scopeId) throws KapuaException {
        final ServiceConfiguration res = serviceConfigurationsFacade.fetchAllConfigurations(scopeId);
        Collections.sort(res.getComponentConfigurations(), Comparator.comparing(ServiceComponentConfiguration::getName));
        return res;
    }

    @PUT
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response update(
            @PathParam("scopeId") ScopeId scopeId,
            ServiceConfiguration serviceConfiguration
    ) throws KapuaException {
        serviceConfigurationsFacade.update(scopeId, serviceConfiguration);
        return Response.noContent().build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("{serviceId}")
    public ServiceComponentConfiguration getComponent(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("serviceId") String serviceId
    ) throws KapuaException {
        return serviceConfigurationsFacade.fetchConfiguration(scopeId, serviceId);
    }

    @PUT
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("{serviceId}")
    public Response updateComponent(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("serviceId") String serviceId,
            ServiceComponentConfiguration serviceComponentConfiguration
    ) throws KapuaException {
        serviceConfigurationsFacade.update(scopeId, serviceId, serviceComponentConfiguration);
        return Response.noContent().build();
    }

}
