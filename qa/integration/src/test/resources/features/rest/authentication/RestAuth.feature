###############################################################################
# Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
#
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0
#
# Contributors:
#     Eurotech - initial API and implementation
###############################################################################
@env_docker_base
@rest_auth

Feature: REST API tests for User
  REST API tests for authentication via login-pw and access token. Refresh access token feature is also covered

  @setup
  Scenario: Initialize Jaxb and security context, then start rest-api container and dependencies
    Given Init Security Context
    #NB: be aware that sub-sequent tests depends on the value of TTL that you set here
    And start rest-API container and dependencies with auth token TTL "3000"ms and refresh token TTL "2000"ms and cors endpoint refresh interval 60s

  Scenario: Simple login with username-pw works and I can call another API endpoint without errors
  First, the authentication via login-pw is tested.
  Then, the access token auth. is tested trough the call to the "get user" api call using the previous generated access token

    Given Server with host "127.0.0.1" on port "8081"
    Given An authenticated user
    When REST "GET" call at "/v1/_/users?offset=0&limit=50" with JSON ""
    Then REST response code is 200
    Then REST response contains list of Users

  Scenario: 'Refresh token' feature is working properly and returns a token that I can use to login

    Given Server with host "127.0.0.1" on port "8081"
    Given An authenticated user
    When I refresh last access token
    Then REST response code is 200
    # now I extract the jwt created by the refresh and I substitute the old token with this one. Doing so, I use that jwt to perform the next api call
    And I extract "tokenId" from the response and I save it in the key "tokenId"
    When REST "GET" call at "/v1/_/users?offset=0&limit=50" with JSON ""
    Then REST response code is 200
    Then REST response contains list of Users

  Scenario: Refresh token, then try to call another api endpoint with the previous "refreshed", now invalidated, token
  The call should fail because you are using an invalidated token for auth. when calling the "get user" api

    Given Server with host "127.0.0.1" on port "8081"
    Given An authenticated user
    When I refresh last access token
    Then REST response code is 200
    When REST "GET" call at "/v1/_/users?offset=0&limit=50" with JSON ""
    Then REST response code is 401
    And REST response containing text "The provided AccessToken has been invalidated in the past"

  Scenario: Auth. with access token fails when I wait the token TTL

    Given Server with host "127.0.0.1" on port "8081"
    Given An authenticated user
    When REST "GET" call at "/v1/_/users?offset=0&limit=50" with JSON ""
    Then REST response code is 200
    Then REST response contains list of Users
    Then I wait 3 seconds
    When REST "GET" call at "/v1/_/users?offset=0&limit=50" with JSON ""
    Then REST response code is 401

  Scenario: Refresh token using wrong parameters - wrong refresh token, previous jwt (right)

    Given Server with host "127.0.0.1" on port "8081"
    Given An authenticated user
    When I refresh access token using refresh token "foo" and jwt ""
    Then REST response code is 401
    And REST response containing text "The provided refresh token doesn't match the one for this AccessToken"

  Scenario: Refresh token using wrong parameters - previous refresh token (right), wrong jwt

    Given Server with host "127.0.0.1" on port "8081"
    Given An authenticated user
    When I refresh access token using refresh token "" and jwt "foo"
    Then REST response code is 401
    And REST response containing text "Error while refreshing the AccessToken"

  Scenario: Refresh a token when I wait the token refresh TTL

    Given Server with host "127.0.0.1" on port "8081"
    Given An authenticated user
    Then I wait 2 seconds
    When I refresh last access token
    Then REST response code is 401
    And REST response containing text "The provided refresh token is expired"

  Scenario: Refresh a token that has been invalidated in the past

    Given Server with host "127.0.0.1" on port "8081"
    Given An authenticated user
    When I refresh last access token
    #now in step data I have the previous token that is invalidated on db
    When I refresh last access token
    Then REST response code is 401
    And REST response containing text "The provided AccessToken has been invalidated"

  @teardown
  Scenario: Stop full docker environment
    Given Stop full docker environment
    And Clean Locator Instance
