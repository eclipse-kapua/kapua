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
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.commons.jersey.rest.model.CountResult;
import org.eclipse.kapua.commons.jersey.rest.model.EntityId;
import org.eclipse.kapua.commons.jersey.rest.model.ScopeId;
import org.eclipse.kapua.commons.jersey.rest.model.SetResult;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.credential.CredentialAttributes;
import org.eclipse.kapua.service.authentication.credential.CredentialCreator;
import org.eclipse.kapua.service.authentication.credential.CredentialFactory;
import org.eclipse.kapua.service.authentication.credential.CredentialListResult;
import org.eclipse.kapua.service.authentication.credential.CredentialQuery;
import org.eclipse.kapua.service.authentication.credential.CredentialService;

import com.google.common.base.Strings;

@Path("{scopeId}/credentials")
public class Credentials extends AbstractKapuaResource {

    @Inject
    public
    CredentialService credentialService;
    @Inject
    public CredentialFactory credentialFactory;

    /**
     * Gets the {@link Credential} list in the scope.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param offset
     *         The result set offset.
     * @param limit
     *         The result set limit.
     * @return The {@link CredentialListResult} of all the credentials associated to the current selected scope.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public CredentialListResult simpleQuery(
            @PathParam("scopeId") ScopeId scopeId,
            @QueryParam("userId") EntityId userId,
            @QueryParam("sortParam") String sortParam,
            @QueryParam("askTotalCount") boolean askTotalCount,
            @QueryParam("sortDir") @DefaultValue("ASCENDING") SortOrder sortDir,
            @QueryParam("offset") @DefaultValue("0") int offset,
            @QueryParam("limit") @DefaultValue("50") int limit) throws KapuaException {
        CredentialQuery query = credentialFactory.newQuery(scopeId);

        AndPredicate andPredicate = query.andPredicate();
        if (userId != null) {
            andPredicate.and(query.attributePredicate(CredentialAttributes.USER_ID, userId));
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
     * Queries the results with the given {@link CredentialQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link CredentialQuery} to use to filter results.
     * @return The {@link CredentialListResult} of all the result matching the given {@link CredentialQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_query")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public CredentialListResult query(
            @PathParam("scopeId") ScopeId scopeId,
            CredentialQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return credentialService.query(query);
    }

    /**
     * Counts the results with the given {@link CredentialQuery} parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to search results.
     * @param query
     *         The {@link CredentialQuery} to use to filter results.
     * @return The count of all the result matching the given {@link CredentialQuery} parameter.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Path("_count")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public CountResult count(
            @PathParam("scopeId") ScopeId scopeId,
            CredentialQuery query) throws KapuaException {
        query.setScopeId(scopeId);

        return new CountResult(credentialService.count(query));
    }

    /**
     * Creates a new Credential based on the information provided in CredentialCreator parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} in which to create the {@link Credential}
     * @param credentialCreator
     *         Provides the information for the new Credential to be created.
     * @return The newly created Credential object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response create(
            @PathParam("scopeId") ScopeId scopeId,
            CredentialCreator credentialCreator) throws KapuaException {
        credentialCreator.setScopeId(scopeId);

        return returnCreated(credentialService.create(credentialCreator));
    }

    /**
     * Returns the Credential specified by the "credentialId" path parameter.
     *
     * @param scopeId
     *         The {@link ScopeId} of the requested {@link Credential}.
     * @param credentialId
     *         The id of the requested Credential.
     * @return The requested Credential object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @GET
    @Path("{credentialId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Credential find(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("credentialId") EntityId credentialId) throws KapuaException {
        Credential credential = credentialService.find(scopeId, credentialId);

        return returnNotNullEntity(credential, Credential.TYPE, credentialId);
    }

    /**
     * Updates the Credential based on the information provided in the Credential parameter.
     *
     * @param credential
     *         The modified Credential whose attributed need to be updated.
     * @return The updated credential.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @PUT
    @Path("{credentialId}")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Credential update(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("credentialId") EntityId credentialId,
            Credential credential) throws KapuaException {
        credential.setScopeId(scopeId);
        credential.setId(credentialId);

        return credentialService.update(credential);
    }

    /**
     * Deletes the Credential specified by the "credentialId" path parameter.
     *
     * @param credentialId
     *         The id of the Credential to be deleted.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @DELETE
    @Path("{credentialId}")
    public Response deleteCredential(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("credentialId") EntityId credentialId) throws KapuaException {
        credentialService.delete(scopeId, credentialId);

        return returnNoContent();
    }

    /**
     * Unlocks a {@link Credential} that has been locked due to a lockout policy.
     *
     * @param scopeId
     *         The {@link ScopeId} of {@link Credential} to unlock.
     * @param credentialId
     *         The id of the Credential to be unlocked.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     * @deprecated Since 2.0.0. Please make use of {@link #unlock(ScopeId, EntityId)}
     */
    @POST
    @Path("{credentialId}/unlock")
    @Deprecated
    public Response unlockCredential(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("credentialId") EntityId credentialId) throws KapuaException {
        credentialService.unlock(scopeId, credentialId);

        return returnNoContent();
    }

    /**
     * Unlocks a {@link Credential} that has been locked due to a lockout policy.
     *
     * @param scopeId
     *         The {@link ScopeId} of {@link Credential} to unlock.
     * @param credentialId
     *         The id of the Credential to be unlocked.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 2.0.0
     */
    @POST
    @Path("{credentialId}/_unlock")
    public Response unlock(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("credentialId") EntityId credentialId) throws KapuaException {
        credentialService.unlock(scopeId, credentialId);

        return returnNoContent();
    }

    @GET
    @Path("_availableCredentials")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SetResult getAvailableAuthAdapter() throws KapuaException {
        return new SetResult(credentialService.getAvailableCredentialTypes());
    }
}
