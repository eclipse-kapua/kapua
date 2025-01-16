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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.commons.rest.model.errors.ExceptionInfo;
import org.eclipse.kapua.commons.rest.model.errors.IllegalArgumentExceptionInfo;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.authentication.ApiKeyCredentials;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.JwtCredentials;
import org.eclipse.kapua.service.authentication.RefreshTokenCredentials;
import org.eclipse.kapua.service.authentication.UsernamePasswordCredentials;
import org.eclipse.kapua.service.authentication.token.AccessToken;
import org.eclipse.kapua.service.authentication.token.LoginInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/authentication")
@Tag(name = "Authentication")
@SecurityRequirements()
public class Authentication extends AbstractKapuaResource {

    @Inject
    private AuthenticationService authenticationService;

    /**
     * Authenticates a {@link UsernamePasswordCredentials}.
     *
     * @param authenticationCredentials The {@link UsernamePasswordCredentials} to validate.
     * @return The {@link AccessToken} created during login.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("user")
    @Operation(summary = "Login - User and Password")
    @RequestBody(
        description = "Request body for user login",
        required = true,
        content = @Content(
            schema = @Schema(implementation = UsernamePasswordCredentials.class),
            examples = {
                @ExampleObject(
                    name = "Admin",
                    value = "{ \"username\": \"kapua-sys\", \"password\": \"kapua-password\" }"
                ),
                @ExampleObject(
                    name = "MFA With Authentication Code",
                    value = "{\"username\": \"ec-sys\", \"password\": \"ec-password\", \"authenticationCode\": 123456, \"trustMe\": true }"
                ),
                @ExampleObject(
                    name = "MFA With TrustKey",
                    value = "{\"username\": \"ec-sys\", \"password\": \"ec-password\", \"trustKey\": \"1c34b3d4-ca23-11ec-9d64-0242ac120002\" }"
                )
            }
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The new AccessToken",
            content = @Content(schema = @Schema(implementation = AccessToken.class))
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
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public AccessToken loginUsernamePassword(UsernamePasswordCredentials authenticationCredentials) throws KapuaException {
        return authenticationService.login(authenticationCredentials);
    }

    /**
     * Authenticates a user with username, password and mfa authentication code (or trust key, alternatively)
     * and returns the authentication token to be used in subsequent REST API calls.
     * It also enables the trusted machine key if the {@code enableTrust} parameter is 'true'.
     *
     * @param authenticationCredentials The username, password and code authentication credential of a user.
     * @param enableTrust               If true the machine trust key is enabled.
     * @return The authentication token.
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.4.0
     * @deprecated Since 2.0.0. Please make use of {@link UsernamePasswordCredentials#getTrustMe()}.
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("mfa")
    @Deprecated
    @Operation(summary = "MFA Login - User, Password and authenticationCode/trustKey")
    @RequestBody(
        required = true,
        content = @Content(
            schema = @Schema(implementation = UsernamePasswordCredentials.class),
            examples = {
                @ExampleObject(
                    name = "kapua-sys",
                    description = "Default kapua-sys login credentials (with dummy MFA)",
                    value = "{\"username\": \"kapua-sys\", \"password\": \"kapua-password\", \"authenticationCode\": \"123456\" }"
                )
            }
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The new AccessToken",
            content = @Content(schema = @Schema(implementation = AccessToken.class))
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
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public AccessToken loginUsernamePasswordCode(
        @Parameter(description = "If true, the machine trust key is enabled for the MfaOption")
        @QueryParam("enableTrust")
        boolean enableTrust,
            UsernamePasswordCredentials authenticationCredentials) throws KapuaException {
        authenticationCredentials.setTrustMe(enableTrust);

        return loginUsernamePassword(authenticationCredentials);
    }

    /**
     * Authenticates a user with an api key and returns
     * the authentication token to be used in subsequent REST API calls.
     *
     * @param authenticationCredentials The API KEY authentication credential of a user.
     * @return The authentication token
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("apikey")
    @Operation(summary = "Login - API Key")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The new AccessToken",
            content = @Content(schema = @Schema(implementation = AccessToken.class))
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
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public AccessToken loginApiKey(ApiKeyCredentials authenticationCredentials) throws KapuaException {
        return authenticationService.login(authenticationCredentials);
    }

    /**
     * Authenticates a user with JWT and returns
     * the authentication token to be used in subsequent REST API calls.
     *
     * @param authenticationCredentials The JWT authentication credential of a user.
     * @return The authentication token
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("jwt")
    @Operation(summary = "Login - JWT")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The new AccessToken",
            content = @Content(schema = @Schema(implementation = AccessToken.class))
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
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public AccessToken loginJwt(JwtCredentials authenticationCredentials) throws KapuaException {
        return authenticationService.login(authenticationCredentials);
    }

    /**
     * Invalidates the AccessToken related to this session.
     * All subsequent calls will end up with a HTTP 401.
     * A new login is required after this call to make other requests.
     *
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("logout")
    @Operation(summary = "Logout")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Logout Successful"
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
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public Response logout() throws KapuaException {
        authenticationService.logout();

        return returnNoContent();
    }

    /**
     * Refreshes an expired {@link AccessToken}. Both the current AccessToken and the Refresh token will be invalidated.
     * If also the Refresh token is expired, the user will have to restart with a new login.
     *
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.
     * @since 1.0.0
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("refresh")
    @Operation(
        summary = "Refresh an AccessToken",
        description = "Creates a new AccessToken from an existing (even if expired) AccessToken and a Refresh Token that must be still valid. Regardless of the expiration date, the AccessToken provided to the refresh operation WILL be invalidated"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The new AccessToken",
            content = @Content(schema = @Schema(implementation = AccessToken.class))
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
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public AccessToken refresh(RefreshTokenCredentials refreshTokenCredentials) throws KapuaException {
        return authenticationService.refreshAccessToken(refreshTokenCredentials.getTokenId(), refreshTokenCredentials.getRefreshToken());
    }

    /**
     * Gets a {@link LoginInfo}.
     *
     * @return A {@link LoginInfo} containing all the permissions and the {@link AccessToken} for the current session
     * @throws KapuaException Whenever something bad happens. See specific {@link KapuaService} exceptions.s
     * @since 1.1.0
     */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("info")
    @Operation(
        summary = "Login Info",
        description = "Returns all the Authentication and Authorization information about the current session"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "An object conatining all the Authentication and Authorization information about the current session",
            content = @Content(schema = @Schema(implementation = LoginInfo.class))
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
            responseCode = "500",
            description = "An internal error occurred while performing the request",
            content = @Content(schema = @Schema(implementation = ExceptionInfo.class))
        )
    })
    public LoginInfo loginInfo() throws KapuaException {
        return authenticationService.getLoginInfo();
    }
}
