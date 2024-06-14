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
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.web.rest.model.EntityId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.account.AccountUpdateRequest;

/*
@deprecated
accidentally exposed under:
        /{scopeId}/accounts/....
Where the scopeId has no meaning when dealing with a specific account
Remove the match with /{scopeId}/... in the next release
 */
@Path("{scopeId: ([\\w-]+)?}{path:|/}accounts/{accountId}")
public class Account extends AbstractKapuaResource {

    @Inject
    public AccountService accountService;

    /**
     * Returns the Account specified by the "accountId" path parameter.
     *
     * @param accountId
     *         The id of the requested Account.
     * @return The requested Account object.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public org.eclipse.kapua.service.account.Account find(
            @PathParam("accountId") EntityId accountId) throws KapuaException {
        org.eclipse.kapua.service.account.Account account = accountService.find(accountId);

        return returnNotNullEntity(account, org.eclipse.kapua.service.account.Account.TYPE, accountId);
    }

    /**
     * Updates the Account based on the information provided in the Account parameter.
     *
     * @param accountId
     *         The id of the requested {@link org.eclipse.kapua.service.account.Account}
     * @param request
     *         The modified Account whose attributed need to be updated.
     * @return The updated account.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @PUT
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public org.eclipse.kapua.service.account.Account update(
            @PathParam("accountId") EntityId accountId, //
            AccountUpdateRequest request) throws KapuaException {
        return accountService.updateChildAccount(accountId, request);
    }

    /**
     * Deletes the Account specified by the "accountId" path parameter.
     *
     * @param accountId
     *         The id of the Account to be deleted.
     * @return HTTP 200 if operation has completed successfully.
     * @throws KapuaException
     *         Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */

    @DELETE
    public Response deleteAccount(
            @PathParam("accountId") EntityId accountId) throws KapuaException {
        accountService.delete(KapuaId.ANY, accountId);

        return returnNoContent();
    }

}
