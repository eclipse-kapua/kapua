/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.api.core.model.CountResult;
import org.eclipse.kapua.app.api.core.model.EntityId;
import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.commons.rest.model.errors.EntityNotFoundExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.ExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.IllegalArgumentExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.SubjectUnauthorizedExceptionInfo;
import org.eclipse.kapua.model.KapuaNamedEntityAttributes;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.device.registry.DeviceListResult;
import org.eclipse.kapua.service.tag.Tag;
import org.eclipse.kapua.service.tag.TagCreator;
import org.eclipse.kapua.service.tag.TagFactory;
import org.eclipse.kapua.service.tag.TagListResult;
import org.eclipse.kapua.service.tag.TagQuery;
import org.eclipse.kapua.service.tag.TagService;
import com.google.common.base.Strings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Path("{scopeId}/tags")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tag")
public class Tags extends AbstractKapuaResource {

    @Inject
    public TagService tagService;
    @Inject
    public TagFactory tagFactory;

    /**
     * Gets the {@link Tag} list in the scope.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param name    The {@link Tag} name to filter results
     * @param offset  The result set offset.
     * @param limit   The result set limit.
     * @return The {@link TagListResult} of all the tags associated to the current selected scope.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get all the Tag")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The list of the Tag available in the Scope",
            content = @Content(schema = @Schema(implementation = DeviceListResult.class))
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
    public TagListResult simpleQuery(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The tag name to filter results")
            @QueryParam("name") String name,
            @Parameter(description = "The name of the parameter that will be used as a sorting key")
            @QueryParam("askTotalCount") boolean askTotalCount,
            @Parameter(description = "A term to match on different fields. Every entity whose at least one of the specified fields starts with this value will be matched.\n" +
                                         "Matches on the following fields:\n- DESCRIPTION\n- NAME")
            @QueryParam("matchTerm") String matchTerm,
            @Parameter(description = "The name of the parameter that will be used as a sorting key")
            @QueryParam("sortParam") String sortParam,
            @Parameter(description = "The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive (except for \"clientId\" parameter).")
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @Parameter(description = "An Offset on the result size. Used to skip the first `n` items of a result set, with `n` equal to the value of `offset`")
            @QueryParam("offset") @DefaultValue("0") int offset,
            @Parameter(description = "A Limit on the result size. The result set will not contain more items than this number")
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        TagQuery query = tagFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate();
        if (!Strings.isNullOrEmpty(name)) {
            andPredicate.and(query.attributePredicate(KapuaNamedEntityAttributes.NAME, name));
        }
        if (matchTerm != null && !matchTerm.isEmpty()) {
            andPredicate.and(query.matchPredicate(matchTerm));
        }
        if (!Strings.isNullOrEmpty(sortParam)) {
            query.setSortCriteria(query.fieldSortCriteria(sortParam, sortDir));
        }
        query.setPredicate(andPredicate);

        query.setOffset(offset);
        query.setLimit(limit);
        query.setAskTotalCount(askTotalCount);

        return query(scopeId, query);
    }

    /**
     * Queries the results with the given {@link TagQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link TagQuery} to use to filter results.
     * @return The {@link TagListResult} of all the result matching the given {@link TagQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @POST
    @Path("_query")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Query the Tags")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The result of the query",
            content = @Content(schema = @Schema(implementation = TagListResult.class))
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
    public TagListResult query(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            TagQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return tagService.query(query);
    }

    /**
     * Counts the results with the given {@link TagQuery} parameter.
     *
     * @param scopeId The {@link ScopeId} in which to search results.
     * @param query   The {@link TagQuery} to use to filter results.
     * @return The count of all the result matching the given {@link TagQuery} parameter.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @POST
    @Path("_count")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Count the Tags")
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
    public CountResult count(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            TagQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return new CountResult(tagService.count(query));
    }

    /**
     * Creates a new Tag based on the information provided in TagCreator
     * parameter.
     *
     * @param scopeId    The {@link ScopeId} in which to create the {@link Tag}
     * @param tagCreator Provides the information for the new {@link Tag} to be created.
     * @return The newly created {@link Tag} object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Create a new Tag")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The Tag that has just been created",
            content = @Content(schema = @Schema(implementation = Tag.class))
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
    public Response create(
            @PathParam("scopeId") ScopeId scopeId,
            TagCreator tagCreator) throws KapuaException {
        tagCreator.setScopeId(scopeId);

        return returnCreated(tagService.create(tagCreator));
    }

    /**
     * Returns the Tag specified by the "tagId" path parameter.
     *
     * @param scopeId The {@link ScopeId} of the requested {@link Tag}.
     * @param tagId   The id of the requested Tag.
     * @return The requested Tag object.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @GET
    @Path("{tagId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Operation(summary = "Get a single Tag")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The details of the desired Tag",
            content = @Content(schema = @Schema(implementation = Tag.class))
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
            responseCode = "404",
            description = "The desired entity could not be found",
            content = @Content(schema = @Schema(implementation = EntityNotFoundExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public Tag find(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Tag on which to perform the operation")
            @PathParam("tagId") EntityId tagId) throws KapuaException {
        Tag tag = tagService.find(scopeId, tagId);

        return returnNotNullEntity(tag, Tag.TYPE, tagId);
    }

    /**
     * Updates the Tag based on the information provided in the Tag parameter.
     *
     * @param scopeId The ScopeId of the requested {@link Tag}.
     * @param tagId   The id of the requested {@link Tag}
     * @param tag     The modified Tag whose attributed need to be updated.
     * @return The updated tag.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @PUT
    @Path("{tagId}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Operation(summary = "Update a single Tag")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The details of the updated Tag",
            content = @Content(schema = @Schema(implementation = Tag.class))
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
            responseCode = "404",
            description = "The desired entity could not be found",
            content = @Content(schema = @Schema(implementation = EntityNotFoundExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public Tag update(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Tag on which to perform the operation")
            @PathParam("tagId") EntityId tagId,
            Tag tag) throws KapuaException {
        tag.setScopeId(scopeId);
        tag.setId(tagId);

        return tagService.update(tag);
    }

    /**
     * Deletes the Tag specified by the "tagId" path parameter.
     *
     * @param scopeId The ScopeId of the requested {@link Tag}.
     * @param tagId   The id of the Tag to be deleted.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @DELETE
    @Path("{tagId}")
    @Operation(summary = "Delete a single Tag")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "The Tag has been deleted"
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
            responseCode = "404",
            description = "The desired entity could not be found",
            content = @Content(schema = @Schema(implementation = EntityNotFoundExceptionInfo.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public Response deleteTag(
            @Parameter(description = "The ID of the Scope where to perform the operation.")
            @PathParam("scopeId") ScopeId scopeId,
            @Parameter(description = "The ID of the Tag on which to perform the operation")
            @PathParam("tagId") EntityId tagId) throws KapuaException {
        tagService.delete(scopeId, tagId);

        return returnNoContent();
    }
}
