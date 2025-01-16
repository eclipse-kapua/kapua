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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.api.core.model.CountResult;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.model.StorableEntityId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.commons.rest.model.errors.ExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.IllegalArgumentExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.SubjectUnauthorizedExceptionInfo;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.datastore.ClientInfoFactory;
import org.eclipse.kapua.service.datastore.ClientInfoRegistryService;
import org.eclipse.kapua.service.datastore.internal.mediator.ClientInfoField;
import org.eclipse.kapua.service.datastore.model.ClientInfo;
import org.eclipse.kapua.service.datastore.model.ClientInfoListResult;
import org.eclipse.kapua.service.datastore.model.query.ClientInfoQuery;
import org.eclipse.kapua.service.datastore.model.query.predicate.DatastorePredicateFactory;
import org.eclipse.kapua.service.storable.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.OrPredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicate;
import com.google.common.base.Strings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("{scopeId}/data/clients")
@Tag(name = "Data Client")
public class DataClients extends AbstractKapuaResource {

    @Inject
    public ClientInfoRegistryService clientInfoRegistryService;
    @Inject
    public ClientInfoFactory clientInfoFactory;
    @Inject
    public DatastorePredicateFactory datastorePredicateFactory;

    /**
     * Gets the {@link ClientInfo} list in the scope.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param clientIds
     *         The client id(s) to filter results.
     * @param offset
     *         The result set offset.
     * @param limit
     *         The result set limit.
     * @return The {@link ClientInfoListResult} of all the clientInfos associated to the current selected scope.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Operation(summary = "Query the Data Client")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The result of the query",
            content = @Content(schema = @Schema(implementation = ClientInfoListResult.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "An illegal argument has been passed to the operation",
            content = @Content(schema = @Schema(implementation = IllegalArgumentExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "The authentication failed for some reason. If this was done via an Access Token, it could be expired or invalidated"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "The user performing the operation does not have the required permissions",
            content = @Content(schema = @Schema(implementation = SubjectUnauthorizedExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public ClientInfoListResult simpleQuery(@PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ClientID to filter results")
            @QueryParam("clientId") List<String> clientIds,
            @Parameter(description = "An Offset on the result size. Used to skip the first `n` items of a result set, with `n` equal to the value of `offset`")
            @QueryParam("offset") @DefaultValue("0") int offset,
            @Parameter(description = "A Limit on the result size. The result set will not contain more items than this number")
            @QueryParam("limit") @DefaultValue("50") int limit)
            throws KapuaException {
        AndPredicate andPredicate = datastorePredicateFactory.newAndPredicate();
        final List<StorablePredicate> clientPredicates = Optional.ofNullable(clientIds)
                .orElse(new ArrayList<>())
                .stream()
                .filter(v -> !Strings.isNullOrEmpty(v))
                .map(clientId -> datastorePredicateFactory.newTermPredicate(ClientInfoField.CLIENT_ID, clientId))
                .collect(Collectors.toList());

        if (!clientPredicates.isEmpty()) {
            final OrPredicate orPredicate = datastorePredicateFactory.newOrPredicate();
            orPredicate.setPredicates(clientPredicates);
            andPredicate.addPredicate(orPredicate);
        }

        ClientInfoQuery query = clientInfoFactory.newQuery(scopeId);

        query.setPredicate(andPredicate);

        query.setOffset(offset);
        query.setLimit(limit);

        return query(scopeId, query);
    }

    /**
     * Queries the results with the given {@link ClientInfoQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link ClientInfoQuery} to used to filter results.
     * @return The {@link ClientInfoListResult} of all the result matching the given {@link ClientInfoQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public ClientInfoListResult query(@PathParam("scopeId") ScopeId scopeId,
            ClientInfoQuery query)
            throws KapuaException {
        query.setScopeId(scopeId);
        query.addFetchAttributes(ClientInfoField.TIMESTAMP.field());
        return clientInfoRegistryService.query(query);
    }

    /**
     * Counts the results with the given {@link ClientInfoQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link ClientInfoQuery} to used to filter results.
     * @return The count of all the result matching the given {@link ClientInfoQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_count")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Operation(summary = "Count the Data Clients")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The count of the available Entities",
            content = @Content(schema = @Schema(implementation = CountResult.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "An illegal argument has been passed to the operation",
            content = @Content(schema = @Schema(implementation = IllegalArgumentExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "The authentication failed for some reason. If this was done via an Access Token, it could be expired or invalidated"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "The user performing the operation does not have the required permissions",
            content = @Content(schema = @Schema(implementation = SubjectUnauthorizedExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public CountResult count(@PathParam("scopeId") ScopeId scopeId,
            ClientInfoQuery query)
            throws KapuaException {
        query.setScopeId(scopeId);

        return new CountResult(clientInfoRegistryService.count(query));
    }

    /**
     * Returns the ClientInfo specified by the "clientInfoId" path parameter.
     *
     * @param clientInfoId
     *         The id of the requested ClientInfo.
     * @return The requested ClientInfo object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{clientInfoId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Operation(summary = "Get a single ClientInfo")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The details of the desired Client Info",
            content = @Content(schema = @Schema(implementation = ClientInfo.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "An illegal argument has been passed to the operation",
            content = @Content(schema = @Schema(implementation = IllegalArgumentExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "The authentication failed for some reason. If this was done via an Access Token, it could be expired or invalidated"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "The user performing the operation does not have the required permissions",
            content = @Content(schema = @Schema(implementation = SubjectUnauthorizedExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public ClientInfo find(@PathParam("scopeId") ScopeId scopeId,
            @PathParam("clientInfoId") StorableEntityId clientInfoId)
            throws KapuaException {
        ClientInfo clientInfo = clientInfoRegistryService.find(scopeId, clientInfoId);

        return returnNotNullEntity(clientInfo);
    }
}
