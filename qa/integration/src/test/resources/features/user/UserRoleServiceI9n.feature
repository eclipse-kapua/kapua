###############################################################################
# Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
@userIntegrationBase
@userRole
@env_docker_base

Feature: User role service integration tests

  @setup
  Scenario: Initialize test environment
    Given Init Jaxb Context
    And Init Security Context
    And Start base docker environment

  Scenario: Adding existing roles to user
  Adding several different roles to one user

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I configure user service
      | type    | name                       | value |
      | boolean | infiniteChildEntities      | true  |
      | integer | maxNumberChildEntities     | 5     |
      | boolean | lockoutPolicy.enabled      | false |
      | integer | lockoutPolicy.maxFailures  | 3     |
      | integer | lockoutPolicy.resetAfter   | 300   |
      | integer | lockoutPolicy.lockDuration | 3     |
    And I create user with name "user1"
    And I add credentials
      | name  | password      | enabled |
      | user1 | User@10031995 | true    |
    And I configure the role service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I create the domain
      | name | actions             |
      | test | read,write, execute |
    And The permissions "read, write, execute"
    And I create the access info entity
    And I create the roles
      | name  |
      | role1 |
      | role2 |
      | role3 |
    And I add permissions to the role
    And I add access roles to user "user1"
    Then Access role with name "role3" is found
    And I logout

  Scenario: Add user permissions to the role
  Creating user "user1" and role "test_role", adding permissions with user domain and read, write and delete actions to the "test_role",
  adding "test_role" to "user1" and after that trying to find, delete or create users as user "user1"

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I configure user service
      | type    | name                       | value |
      | boolean | infiniteChildEntities      | true  |
      | integer | maxNumberChildEntities     | 5     |
      | boolean | lockoutPolicy.enabled      | false |
      | integer | lockoutPolicy.maxFailures  | 3     |
      | integer | lockoutPolicy.resetAfter   | 300   |
      | integer | lockoutPolicy.lockDuration | 3     |
    And I create user with name "user1"
    And I add credentials
      | name  | password      | enabled |
      | user1 | User@10031995 | true    |
    And I select the domain "user"
    And I create the access info entity
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I add access role "test_role" to user "user1"
    And I logout
    Then I login as user with name "user1" and password "User@10031995"
    And I create user with name "new-user"
    And I find user with name "new-user"
    And I try to edit user to name "new-user"
    And I try to delete user "new-user"
    Then No exception was thrown
    And I logout

  Scenario: Add device permissions to the role
  Creating user "user1" and role "test_role", adding permissions with device domain and read, write and delete actions to the "test_role",
  adding "test_role" to "user1" and after that trying to find, delete or create devices as user "user1"

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I configure user service
      | type    | name                       | value |
      | boolean | infiniteChildEntities      | true  |
      | integer | maxNumberChildEntities     | 5     |
      | boolean | lockoutPolicy.enabled      | false |
      | integer | lockoutPolicy.maxFailures  | 3     |
      | integer | lockoutPolicy.resetAfter   | 300   |
      | integer | lockoutPolicy.lockDuration | 3     |
    And I create user with name "user1"
    And I add credentials
      | name  | password      | enabled |
      | user1 | User@10031995 | true    |
    And I select the domain "device"
    And I create the access info entity
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I add access role "test_role" to user "user1"
    And I logout
    Then I login as user with name "user1" and password "User@10031995"
    And I create a device with name "device1"
    And I find device with clientId "device1"
    And I try to edit device to clientId "device2"
    And I delete the device with the clientId "device1"
    Then No exception was thrown
    And I logout

  Scenario: Add device event permissions to the role
  Creating user "user1" and role "test_role", adding permissions with device and device_event domain and read, write and delete actions to the "test_role",
  adding "test_role" to "user1" and after that trying to find device events"

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And Scope with ID 1
    And I configure user service
      | type    | name                       | value |
      | boolean | infiniteChildEntities      | true  |
      | integer | maxNumberChildEntities     | 5     |
      | boolean | lockoutPolicy.enabled      | false |
      | integer | lockoutPolicy.maxFailures  | 3     |
      | integer | lockoutPolicy.resetAfter   | 300   |
      | integer | lockoutPolicy.lockDuration | 3     |
    Given User A
      | name  | displayName  | email             | phoneNumber     | status  | userType |
      | user1 | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL |
    And I add credentials
      | name  | password      | enabled |
      | user1 | User@10031995 | true    |
    And I configure the device registry service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 10    |
    And I create the access info entity
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    And I select the domain "device"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I select the domain "account"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I add access role "test_role" to user "user1"
    And I logout
    Then I login as user with name "user1" and password "User@10031995"
    And A birth message from device "device_1"
    And I expect the exception "SubjectUnauthorizedException" with the text "Required permission: device_event:read:"
    When I search for events from device "device_1" in account "kapua-sys"
    Then An exception was thrown
    And I logout
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select the domain "device_event"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I logout
    Given I login as user with name "user1" and password "User@10031995"
    When I search for events from device "device_1" in account "kapua-sys" I find 1 event within 30 seconds
    And The type of the last event is "BIRTH"
    And No exception was thrown
    And I logout

  Scenario: Add group permissions to the role
  Creating user "user1" and role "test_role", adding permissions with group domain and read, write and delete actions to the "test_role",
  adding "test_role" to "user1" and after that trying to find, delete or create groups as user "user1"

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I configure user service
      | type    | name                       | value |
      | boolean | infiniteChildEntities      | true  |
      | integer | maxNumberChildEntities     | 5     |
      | boolean | lockoutPolicy.enabled      | false |
      | integer | lockoutPolicy.maxFailures  | 3     |
      | integer | lockoutPolicy.resetAfter   | 300   |
      | integer | lockoutPolicy.lockDuration | 3     |
    And I create user with name "user1"
    And I add credentials
      | name  | password      | enabled |
      | user1 | User@10031995 | true    |
    And I select the domain "group"
    And I create the access info entity
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I add access role "test_role" to user "user1"
    And I logout
    Then I login as user with name "user1" and password "User@10031995"
    And I create a group with name "group1"
    And I change the group name from "group1" to "group2"
    And I delete the group with name "group2"
    Then No exception was thrown
    And I logout

  Scenario: Add role permissions to the role
  Creating user "user1" and role "test_role", adding permissions with role domain and read, write and delete actions to the "test_role",
  adding "test_role" to "user1" and after that trying to find, delete or create roles as user "user1"

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I configure user service
      | type    | name                       | value |
      | boolean | infiniteChildEntities      | true  |
      | integer | maxNumberChildEntities     | 5     |
      | boolean | lockoutPolicy.enabled      | false |
      | integer | lockoutPolicy.maxFailures  | 3     |
      | integer | lockoutPolicy.resetAfter   | 300   |
      | integer | lockoutPolicy.lockDuration | 3     |
    And I create user with name "user1"
    And I add credentials
      | name  | password      | enabled |
      | user1 | User@10031995 | true    |
    And I select the domain "role"
    And I create the access info entity
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I add access role "test_role" to user "user1"
    And I logout
    Then I login as user with name "user1" and password "User@10031995"
    And I create the following role
      | scopeId | name       |
      | 1       | test_role1 |
    And I find a role with name "test_role"
    And I update the last created role name to "role-ana"
    And I delete the role with name "test_role"
    Then No exception was thrown
    And I logout

  Scenario: Add tag permissions to the role
  Creating user "user1" and role "test_role", adding permissions with tag domain and read, write and delete actions to the "test_role",
  adding "test_role" to "user1" and after that trying to find, delete or create tags as user "user1"

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I configure user service
      | type    | name                       | value |
      | boolean | infiniteChildEntities      | true  |
      | integer | maxNumberChildEntities     | 5     |
      | boolean | lockoutPolicy.enabled      | false |
      | integer | lockoutPolicy.maxFailures  | 3     |
      | integer | lockoutPolicy.resetAfter   | 300   |
      | integer | lockoutPolicy.lockDuration | 3     |
    And I create user with name "user1"
    And I add credentials
      | name  | password      | enabled |
      | user1 | User@10031995 | true    |
    And I select the domain "tag"
    And I create the access info entity
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I add access role "test_role" to user "user1"
    And I logout
    Then I login as user with name "user1" and password "User@10031995"
    And I create tag with name "tag1" without description
    And Name of tag "tag1" is changed into "tag2"
    And I delete the tag with name "tag2"
    Then No exception was thrown
    And I logout

  Scenario: Add account permissions to the role
  Creating user "user1" and role "test_role", adding permissions with account domain and read, write and delete actions to the "test_role",
  adding "test_role" to "user1" and after that trying to find, delete or create accounts as user "user1"

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I configure user service
      | type    | name                       | value |
      | boolean | infiniteChildEntities      | true  |
      | integer | maxNumberChildEntities     | 5     |
      | boolean | lockoutPolicy.enabled      | false |
      | integer | lockoutPolicy.maxFailures  | 3     |
      | integer | lockoutPolicy.resetAfter   | 300   |
      | integer | lockoutPolicy.lockDuration | 3     |
    And I create user with name "user1"
    And I add credentials
      | name  | password      | enabled |
      | user1 | User@10031995 | true    |
    And I select the domain "account"
    And I create the access info entity
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I add access role "test_role" to user "user1"
    And I logout
    Then I login as user with name "user1" and password "User@10031995"
    And I create an account with name "account1", organization name "organization" and email address "organization@gmail.com"
    And I find account with name "account1"
    And I try to edit description to "account in child account"
    And I delete account "account1"
    Then No exception was thrown
    And I logout

  Scenario: Add job permissions to the role
  Creating user "user1" and role "test_role", adding permissions with job domain and read, write and delete actions to the "test_role",
  adding "test_role" to "user1" and after that trying to find, delete or create jobs as user "user1"

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I configure user service
      | type    | name                       | value |
      | boolean | infiniteChildEntities      | true  |
      | integer | maxNumberChildEntities     | 5     |
      | boolean | lockoutPolicy.enabled      | false |
      | integer | lockoutPolicy.maxFailures  | 3     |
      | integer | lockoutPolicy.resetAfter   | 300   |
      | integer | lockoutPolicy.lockDuration | 3     |
    And I create user with name "user1"
    And I add credentials
      | name  | password      | enabled |
      | user1 | User@10031995 | true    |
    And I select the domain "job"
    And I create the access info entity
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I add access role "test_role" to user "user1"
    And I logout
    Then I login as user with name "user1" and password "User@10031995"
    And I create a job with the name "job1"
    And I find a job with name "job1"
    And I try to edit job to name "job2"
    And I try to delete the job with name "job2"
    Then No exception was thrown
    And I logout

  Scenario: Add domain, user and access_info permissions to the role
  Creating user "user1" and role "test_role", adding permissions with domain, access_info and user domain and read, write and delete actions to the "test_role",
  adding "test_role" to "user1" and after that trying to add permission as user "user1"

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And Scope with ID 1
    And I configure user service
      | type    | name                       | value |
      | boolean | infiniteChildEntities      | true  |
      | integer | maxNumberChildEntities     | 5     |
      | boolean | lockoutPolicy.enabled      | false |
      | integer | lockoutPolicy.maxFailures  | 3     |
      | integer | lockoutPolicy.resetAfter   | 300   |
      | integer | lockoutPolicy.lockDuration | 3     |
    And User A
      | name  | displayName  | email             | phoneNumber     | status  | userType |
      | user1 | Kapua User B | kapua_b@kapua.com | +386 31 323 555 | ENABLED | INTERNAL |
    And I add credentials
      | name  | password      | enabled |
      | user1 | User@10031995 | true    |
    And I create the access info entity
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    And I select the domain "domain"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I select the domain "access_info"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I select the domain "user"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I add access role "test_role" to user "user1"
    And I logout
    Then I login as user with name "user1" and password "User@10031995"
    And I create the domain
      | name | actions             |
      | test | read,write, execute |
    And The permissions "read, write, execute"
    And I create the permissions
    Then No exception was thrown
    And I logout

  Scenario: Test fetching claims - simple permissions
  Test fetching claims with simple permissions

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And Scope with ID 1
    And User A
      | name  | displayName  | email             | phoneNumber     | status  | userType |
      | user1 | Kapua User B | kapua_b@kapua.com | +386 31 323 555 | ENABLED | INTERNAL |
    And I add credentials
      | name  | password      | enabled |
      | user1 | User@10031995 | true    |
    And I create the access info entity
    Given I select the domain "endpoint_info"
    And The permission "read"
    When I create the permission
    Given I select the domain "device_event"
    And The permissions "read, write"
    When I create the permissions
    Given I select the domain "device_management"
    And The permissions "execute, write"
    When I create the permissions
    Then exception is not thrown
    And I logout
    Then I login as user with name "user1" and password "User@10031995"
    When I fetch user claims for the last account
    Then The user claims contains exactly
      | endpoint_info:read        |
      | device_event:read         |
      | device_event:write        |
      | device_management:execute |
      | device_management:write   |
    And I logout

  Scenario: Test fetching claims - roles and permissions
  Test fetching claims with roles and permissions

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And Scope with ID 1
    And User A
      | name  | displayName  | email             | phoneNumber     | status  | userType |
      | user1 | Kapua User B | kapua_b@kapua.com | +386 31 323 555 | ENABLED | INTERNAL |
    And I add credentials
      | name  | password      | enabled |
      | user1 | User@10031995 | true    |
    And I create the access info entity
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    And I select the domain "domain"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I select the domain "access_info"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I select the domain "user"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
    And I add access role "test_role" to user "user1"
    And I logout
    Then I login as user with name "user1" and password "User@10031995"
    When I fetch user claims for the last account
    Then The user claims contains exactly
      | domain:read        |
      | domain:write       |
      | domain:delete      |
      | access_info:read   |
      | access_info:write  |
      | access_info:delete |
      | user:read          |
      | user:write         |
    Given I select the domain "stream"
    And The permission "write"
    When I create the permission
    Given I select the domain "job"
    And The permissions "write, delete"
    When I create the permissions
    When I fetch user claims for the last account
    Then The user claims contains exactly
      | domain:read        |
      | domain:write       |
      | domain:delete      |
      | access_info:read   |
      | access_info:write  |
      | access_info:delete |
      | user:read          |
      | user:write         |
      | stream:write       |
      | job:write          |
      | job:delete         |
    And I logout

  Scenario: Test fetching claims - simple permissions with groups
  Test fetching claims with simple permissions restricted to a group

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And Scope with ID 1
    And User A
      | name  | displayName  | email             | phoneNumber     | status  | userType |
      | user1 | Kapua User B | kapua_b@kapua.com | +386 31 323 555 | ENABLED | INTERNAL |
    And I add credentials
      | name  | password      | enabled |
      | user1 | User@10031995 | true    |
    And I create the access info entity
    Given I select the domain "device"
    And The permission "read"
    And I create a group with name "group1"
    When I restrict permission to last created group
    When I create the permissions
    Given I select the domain "stream"
    And The permissions "write"
    When I create the permissions
    Then exception is not thrown
    And I logout
    Then I login as user with name "user1" and password "User@10031995"
    When I fetch user claims for the last account
    Then The user claims contains exactly
      | device:read        |
      | stream:write       |
    And I logout

  Scenario: Add datastore permissions to the role
  Creating user "user1" and role "test_role", adding permissions with tag datastore and read, write and delete actions to the "test_role",
  adding "test_role" to "user1" and after that trying to work with data as "user1"

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I select account "kapua-sys"
    And I configure user service
      | type    | name                       | value |
      | boolean | infiniteChildEntities      | true  |
      | integer | maxNumberChildEntities     | 5     |
      | boolean | lockoutPolicy.enabled      | false |
      | integer | lockoutPolicy.maxFailures  | 3     |
      | integer | lockoutPolicy.resetAfter   | 300   |
      | integer | lockoutPolicy.lockDuration | 3     |
    And I create user with name "user1"
    And I add credentials
      | name  | password      | enabled |
      | user1 | User@10031995 | true    |
    And I create the access info entity
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    And I select the domain "device"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I add access role "test_role" to user "user1"
    And I logout
    Then I login as user with name "user1" and password "User@10031995"
    And The device "test-device-1"
    And I expect the exception "SubjectUnauthorizedException" with the text "Required permission: datastore:read:"
    And I search for data message with id "fake-id"
    Then An exception was thrown
    And I logout
    And I login as user with name "kapua-sys" and password "kapua-password"
    And I select the domain "datastore"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I logout
    When I login as user with name "user1" and password "User@10031995"
    And I search for data message with id "fake-id"
    Then I don't find message
    And No exception was thrown
    And I logout

  Scenario: Delete access role from user
  Login as kapua-sys user, adding role and create access role.
  If user deletes existing role, access role has to be null.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create user with name "user1"
    And I add credentials
      | name  | password      | enabled |
      | user1 | User@10031995 | true    |
    And I create the access info entity
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    And I add access role "test_role" to user "user1"
    Then Access role with name "test_role" is found
    When I delete the role with name "test_role"
    Then Access role is not found
    And I logout

  Scenario: Add deleted role again
  First add deleted role, and after that create access role

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create user with name "user1"
    And I add credentials
      | name  | password      | enabled |
      | user1 | User@10031995 | true    |
    And I create the access info entity
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    When I add access role "test_role" to user "user1"
    Then Access role with name "test_role" is found
    And I logout

  Scenario: Delete permissions from role
  After access role permissions are removed, user also should not have that permissions

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I configure user service
      | type    | name                       | value |
      | boolean | infiniteChildEntities      | true  |
      | integer | maxNumberChildEntities     | 5     |
      | boolean | lockoutPolicy.enabled      | false |
      | integer | lockoutPolicy.maxFailures  | 3     |
      | integer | lockoutPolicy.resetAfter   | 300   |
      | integer | lockoutPolicy.lockDuration | 3     |
    And I create user with name "user1"
    And I add credentials
      | name  | password      | enabled |
      | user1 | User@10031995 | true    |
    And I select the domain "user"
    And I create the access info entity
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
    And I add access role "test_role" to user "user1"
    And I delete role permissions
    And I logout
    And I login as user with name "user1" and password "User@10031995"
    And I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission to perform this action. Required permission: user:write:1:*."
    And I create user with name "user-test"
    Then An exception was thrown
    And I logout


  Scenario: Add same role to user twice
  If user tries to add same role two times, he gets exception

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I configure user service
      | type    | name                       | value |
      | boolean | infiniteChildEntities      | true  |
      | integer | maxNumberChildEntities     | 5     |
      | boolean | lockoutPolicy.enabled      | false |
      | integer | lockoutPolicy.maxFailures  | 3     |
      | integer | lockoutPolicy.resetAfter   | 300   |
      | integer | lockoutPolicy.lockDuration | 3     |
    And I create user with name "user1"
    And I create the access info entity
    And I add credentials
      | name  | password      | enabled |
      | user1 | User@10031995 | true    |
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    And I add access role "test_role" to user "user1"
    And I expect the exception "KapuaDuplicateNameException" with the text "An entity with the same name test_role already exists."
    And I add access role "test_role" to user "user1"
    And An exception was thrown
    Then I logout

  Scenario: Add same permission twice to the same role
  If user wants to add user:read permission two times to the same role,
  than exception was thrown

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I configure user service
      | type    | name                       | value |
      | boolean | infiniteChildEntities      | true  |
      | integer | maxNumberChildEntities     | 5     |
      | boolean | lockoutPolicy.enabled      | false |
      | integer | lockoutPolicy.maxFailures  | 3     |
      | integer | lockoutPolicy.resetAfter   | 300   |
      | integer | lockoutPolicy.lockDuration | 3     |
    And I create user with name "user1"
    And I select the domain "user"
    And I create the access info entity
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
    Then No exception was thrown
    And I expect the exception "KapuaEntityUniquenessException"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
    Then An exception was thrown
    And I logout

  Scenario: Add scheduler permissions to the role
  Creating user "user1" and role "test_role", adding permissions with scheduler domain and read, write and delete actions to the "test_role",
  adding "test_role" to "user1" and after that trying to work with schedules as "user1"

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I select account "kapua-sys"
    And I configure user service
      | type    | name                       | value |
      | boolean | infiniteChildEntities      | true  |
      | integer | maxNumberChildEntities     | 5     |
      | boolean | lockoutPolicy.enabled      | false |
      | integer | lockoutPolicy.maxFailures  | 3     |
      | integer | lockoutPolicy.resetAfter   | 300   |
      | integer | lockoutPolicy.lockDuration | 3     |
    And I create user with name "user1"
    And I add credentials
      | name  | password      | enabled |
      | user1 | User@10031995 | true    |
    And I create the access info entity
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    And I select the domain "job"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I select the domain "scheduler"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I add access role "test_role" to user "user1"
    And I logout
    Then I login as user with name "user1" and password "User@10031995"
    And I find scheduler properties with name "Interval Job"
    And A regular trigger creator with the name "TestSchedule" and following properties
      | name     | type              | value |
      | interval | java.lang.Integer | 1     |
    And I try to create a new trigger entity from the existing creator
    And I try to edit trigger name "TestSchedule1"
    And I try to delete last created trigger
    Then No exception was thrown
    And I logout
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I delete the last created role permissions
    And I logout
    Given I login as user with name "user1" and password "User@10031995"
    And I expect the exception "SubjectUnauthorizedException" with the text "Required permission: scheduler:write:"
    And I try to create a new trigger entity from the existing creator
    Then An exception was thrown
    And I logout

  Scenario: Add endpoint_info permissions to the role
  Creating user "user1" and role "test_role", adding permissions with endpoint_info domain and read, write and delete actions to the "test_role",
  adding "test_role" to "user1" and after that trying to work with endpoints as "user1"

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I configure user service
      | type    | name                       | value |
      | boolean | infiniteChildEntities      | true  |
      | integer | maxNumberChildEntities     | 5     |
      | boolean | lockoutPolicy.enabled      | false |
      | integer | lockoutPolicy.maxFailures  | 3     |
      | integer | lockoutPolicy.resetAfter   | 300   |
      | integer | lockoutPolicy.lockDuration | 3     |
    And I create user with name "user1"
    And I add credentials
      | name  | password      | enabled |
      | user1 | User@10031995 | true    |
    And I create endpoint with schema "endpoint1", domain "com" and port 8000
    And I create the access info entity
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    And I select the domain "endpoint_info"
    And I create the following role permissions
      | scopeId | actionName | targetScopeId |
      | 1       | read       | 1             |
      | 1       | write      | 1             |
      | 1       | delete     | 1             |
    And I add access role "test_role" to user "user1"
    And I logout
    Then I login as user with name "user1" and password "User@10031995"
    And I try to find endpoint with schema "endpoint1", domain "com" and port 8000
    Then No exception was thrown
    When I expect the exception "SubjectUnauthorizedException" with the text "Required permission: endpoint_info:write:"
    And I create endpoint with schema "endpoint2", domain "dns" and port 20000
    Then An exception was thrown
    When I expect the exception "SubjectUnauthorizedException" with the text "Required permission: endpoint_info:delete:"
    And I delete the endpoint with schema "endpoint1", domain "com" and port 8000
    Then An exception was thrown
    And I logout
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I delete the last created role permissions
    And I logout
    Given I login as user with name "user1" and password "User@10031995"
    And I expect the exception "SubjectUnauthorizedException" with the text "Required permission: endpoint_info:read:"
    And I try to find endpoint with schema "endpoint1", domain "com" and port 8000
    Then An exception was thrown
    And I logout

  Scenario: Try to add two different roles with same permissions
  Adding the same permissions from user domain with read, write and delete actions to both roles.
  Afterwards add both roles to the user and try to work with Users as user "TestUser".

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create user with name "TestUser"
    And I add credentials
      | name     | password      | enabled |
      | TestUser | User@10031995 | true    |
    And I create the access info entity
    And I create the roles
      | scopeId | name       |
      | 1       | test_role  |
      | 1       | test_role1 |
    And I select the domain "user"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I add access roles to user "TestUser"
    And I count the access roles from user "TestUser"
    And I count 2
    And No exception was thrown
    And I logout
    And I login as user with name "TestUser" and password "User@10031995"
    And I create user with name "TestUser1"
    And I find user with name "TestUser1"
    And I change user to
      | name      | displayName       | email                  | phoneNumber     | status   | userType |
      | TestUser1 | Kapua Test User 1 | kapua_test_1@kapua.com | +386 31 321 456 | DISABLED | INTERNAL |
    And I try to delete user "TestUser1"
    Then No exception was thrown
    And I logout

  Scenario: Add admin role to the user
  Creating user "TestUser" and adding "admin" role to him. Login as user "TestUser" and
  try to find, add, edit or delete users, devices, tags, jobs, groups, roles, endpoints.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create user with name "TestUser"
    And I add credentials
      | name     | password      | enabled |
      | TestUser | User@10031995 | true    |
    And I search for user with name "TestUser"
    And I find user with name "TestUser"
    And I find a role with name "admin"
    And I search for the permissions of role "admin"
    And I count 1
    And I create the access info entity
    And I add access role "admin" to user "TestUser"
    And I logout
    And I login as user with name "TestUser" and password "User@10031995"
    And I create user with name "user"
    And I find user with name "user"
    And I change user to
      | name | displayName       | email                  | phoneNumber     | status   | userType |
      | user | Kapua Test User 1 | kapua_test_1@kapua.com | +386 31 321 456 | DISABLED | INTERNAL |
    And I try to delete user "user"
    And I create a device with name "TestDevice"
    And I find device with clientId "TestDevice"
    And I try to edit device to clientId "TestDevice1"
    And I delete the device with the clientId "TestDevice"
    And I create a job with the name "TestJob"
    And I find a job with name "TestJob"
    And I try to edit job to name "TestJob1"
    And I try to delete the job with name "TestJob1"
    And I create tag with name "Tag" without description
    And I find a tag with name "Tag"
    And Name of tag "Tag" is changed into "Tag1"
    And I delete the tag with name "Tag1"
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    And I find a role with name "test_role"
    And I update the last created role name to "role1"
    And I delete the role with name "test_role"
    And I create a group with name "TestGroup"
    And I find the group with name "TestGroup"
    And I update the group name to "TestGroup1"
    And I delete the group with name "TestGroup1"
    And I create endpoint with schema "TestEndpoint", domain "com" and port 8000
    And I try to find endpoint with schema "TestEndpoint", domain "com" and port 8000
    And I try to edit endpoint schema to "TestEndpoint1"
    And I delete the endpoint with schema "TestEndpoint1", domain "com" and port 8000
    Then No exception was thrown
    And I logout

  Scenario: Deleting admin role
  Login as kapua-sys user and try to delete "admin" role.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I find a role with name "admin"
    And I expect the exception "KapuaException" with the text "Operation not allowed on admin role"
    When I delete the role with name "admin"
    Then An exception was thrown
    And I logout

  Scenario: Deleting default permissions from admin role
  Login as kapua-sys user and try to find and delete default permission
  from "admin" role. An exception with proper text should be raised.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And I find a role with name "admin"
    And I search for the permissions of role "admin"
    And I count 1
    And I expect the exception "KapuaException" with the text "Operation not allowed on this specific permission"
    And I delete the default admin role permission
    Then An exception was thrown
    And I search for the permissions of role "admin"
    And I count 1
    And I logout

  Scenario: Add and delete User permissions from the "admin" role
  Login as user kapua-sys, go to Roles and add User permissions with Read, Write and Delete actions to the "admin" role.
  Try to delete added permissions. After deleting, no exception should be thrown.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And I find a role with name "admin"
    And I search for the permissions of role "admin"
    Then I count 1
    And I select the domain "user"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I search for the permissions of role "admin"
    And I count 4
    When I delete all admin role permissions except default permission
    Then No exception was thrown
    And I count the role permissions in scope 1
    And I count 1
    And I logout

  Scenario: Add and delete Device permissions from the "admin" role
  Login as user kapua-sys, go to roles and add Device permissions with Read, Write and Delete actions to the "admin" role.
  Try to delete added permissions. After deleting, no exception should be thrown.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I find a role with name "admin"
    And I search for the permissions of role "admin"
    Then I count 1
    And I select the domain "device"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I search for the permissions of role "admin"
    And I count 4
    When I delete all admin role permissions except default permission
    Then No exception was thrown
    And I search for the permissions of role "admin"
    And I count 1
    And I logout

  Scenario: Add and delete Job permissions from the "admin" role
  Login as user kapua-sys, go to Roles and add Job permissions with Read, Write, Delete and Execute actions to the "admin" role.
  Try to delete added permissions. After deleting, no exceptions should be thrown.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I find a role with name "admin"
    And I search for the permissions of role "admin"
    Then I count 1
    And I select the domain "job"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
      | 1       | execute    |
    And I search for the permissions of role "admin"
    And I count 5
    When I delete all admin role permissions except default permission
    Then No exception was thrown
    And I search for the permissions of role "admin"
    And I count 1
    And I logout

  Scenario: Add and delete Account permissions from the "admin" role
  Login as user kapua-sys, go to Roles and add Account permissions with Read, Write and Delete actions to the "admin" role.
  Try to delete added permissions. After deleting, no exception should be thrown.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I find a role with name "admin"
    And I search for the permissions of role "admin"
    Then I count 1
    And I select the domain "account"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I search for the permissions of role "admin"
    And I count 4
    When I delete all admin role permissions except default permission
    Then No exception was thrown
    And I search for the permissions of role "admin"
    And I count 1
    And I logout

  Scenario: Add and delete Group permissions from the "admin" role
  Login as user kapua-sys, go to Roles and add Group permissions with Read, Write and Delete actions to the "admin" role.
  Try to delete added permissions. After deleting, no exception should be thrown.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I find a role with name "admin"
    And I search for the permissions of role "admin"
    Then I count 1
    And I select the domain "group"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I search for the permissions of role "admin"
    And I count 4
    When I delete all admin role permissions except default permission
    Then No exception was thrown
    And I search for the permissions of role "admin"
    And I count 1
    And I logout

  Scenario: Add and delete Role permissions from the "admin" role
  Login as user kapua-sys, go to Roles and add role permissions with Read, Write and Delete actions to the "admin" role.
  Try to delete added permissions. After deleting, no exception should be thrown.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I find a role with name "admin"
    And I search for the permissions of role "admin"
    Then I count 1
    And I select the domain "role"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I search for the permissions of role "admin"
    And I count 4
    When I delete all admin role permissions except default permission
    Then No exception was thrown
    And I search for the permissions of role "admin"
    And I count 1
    And I logout

  Scenario: Add and delete Endpoint_info permissions from the "admin" role
  Login as user kapua-sys, go to Roles and add Endpoint_info permissions with Read, Write and Delete actions to the "admin" role.
  Try to delete added permissions. After deleting, no exception should be thrown.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I find a role with name "admin"
    And I search for the permissions of role "admin"
    Then I count 1
    And I select the domain "endpoint_info"
    And I create the following role permissions
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I search for the permissions of role "admin"
    And I count 4
    When I delete all admin role permissions except default permission
    Then No exception was thrown
    And I search for the permissions of role "admin"
    And I count 1
    And I logout

  Scenario: Try to find users granted to "admin" role
  Searching for granted users, counting them and confirming their names.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create users with following names
      | name      |
      | TestUser  |
      | TestUser1 |
    And I create the access info entities
    And I find a role with name "admin"
    And I add access role "admin" to created users
    When I search for granted user
    Then I count 2
    And I find granted users with name
      | name      |
      | TestUser  |
      | TestUser1 |
    And No exception was thrown
    And I logout

  Scenario: Try to find users that have assigned specific role
  Creating two users as kapua-sys user, creating role "test_role" and adding this role to newly
  created users. Searching for granted users, counting them and confirming their names.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create users with following names
      | name      |
      | TestUser1 |
      | TestUser2 |
    And I create the access info entities
    And I create the following role
      | scopeId | name       |
      | 1       | test_role1 |
    And I add access role "test_role1" to created users
    When I search for granted user
    Then I count 2
    And I find granted users with name
      | name      |
      | TestUser1 |
      | TestUser2 |
    And No exception was thrown
    And I logout

  Scenario: Adding same role twice to user in child account
  Creating new child account as user kapua-sys and new role "TestRole" and new user "TestUser" within this child account.
  Adding "TestRole" to the user twice. An exception should be thrown.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create an account with name "SubAccount", organization name "Organization" and email address "test@test.com"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I create user with name "TestUser" in account "SubAccount"
    And I create role "TestRole" in account "SubAccount"
    And I create the access info entity in account "SubAccount"
    And I add access role "TestRole" to user "TestUser" in account "SubAccount"
    And I count the access roles from user in account "SubAccount"
    And I count 1
    And I expect the exception "KapuaDuplicateNameException" with the text "An entity with the same name TestRole already exists."
    And I add access role "TestRole" to user "TestUser" in account "SubAccount"
    Then An exception was thrown
    And I logout

  Scenario: Add user permissions to the role in child account
  Creating child account as user kapua-sys, adding user and role to this child account.
  Adding permissions from User domain with Read, Write and Delete actions to the role,
  and after that adding role with permissions to the user. Logout as kapua-sys and login
  as created user, and try to add, edit, find or delete users.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create an account with name "SubAccount", organization name "organization" and email address "organization@gmail.com"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And The account with name "kapua-sys" has 1 subaccount
    And I find account with name "SubAccount"
    And I create role "Role1" in account "SubAccount"
    And I try to find role with name "Role1" in account "SubAccount"
    Then Role with name "Role1" in account "SubAccount" is found
    And I select the domain "user"
    And I create the following role permissions in account "SubAccount"
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I search for the permissions of found role "Role1" in account "SubAccount"
    And I count 3
    And I create user with name "SubUser" in account "SubAccount"
    And I add credentials
      | name    | password      | enabled |
      | SubUser | User@10031995 | true    |
    And I create the access info entity in account "SubAccount"
    And I add access role "Role1" to user "SubUser" in account "SubAccount"
    And I logout
    And I login as user with name "SubUser" and password "User@10031995"
    And I create user with name "User"
    And I find user with name "User"
    And I try to edit user to name "User"
    And I try to delete user "User"
    And No exception was thrown
    And I logout

  Scenario: Add device permissions to the role in child account
  Creating child account as user kapua-sys, adding user and role to this child account.
  Adding permissions from Device domain with Read, Write and Delete actions to the role,
  and after that adding role with permissions to the user. Logout as kapua-sys and login
  as created user, and try to add, edit, find or delete devices.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create an account with name "SubAccount", organization name "organization" and email address "organization@gmail.com"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And The account with name "kapua-sys" has 1 subaccount
    And I find account with name "SubAccount"
    And I create role "Role1" in account "SubAccount"
    And I try to find role with name "Role1" in account "SubAccount"
    Then Role with name "Role1" in account "SubAccount" is found
    And I select the domain "device"
    And I create the following role permissions in account "SubAccount"
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I search for the permissions of found role "Role1" in account "SubAccount"
    And I count 3
    And I create user with name "SubUser" in account "SubAccount"
    And I add credentials
      | name    | password      | enabled |
      | SubUser | User@10031995 | true    |
    And I create the access info entity in account "SubAccount"
    And I add access role "Role1" to user "SubUser" in account "SubAccount"
    And I logout
    And I login as user with name "SubUser" and password "User@10031995"
    And I create a device with name "TestDevice"
    And I find device with clientId "TestDevice"
    And I try to edit device to clientId "TestDevice1"
    And I delete the device with the clientId "TestDevice"
    And No exception was thrown
    And I logout

  Scenario: Add job permissions to the role in child account
  Creating child account as user kapua-sys, adding user and role to this child account.
  Adding permissions from Job domain with Read, Write, Delete and Execute actions to the role,
  and after that adding role with permissions to the user. Logout as kapua-sys and login as created user,
  and try to add, edit, find or delete jobs (Execution is covered in other tests).

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create an account with name "SubAccount", organization name "organization" and email address "organization@gmail.com"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And The account with name "kapua-sys" has 1 subaccount
    And I find account with name "SubAccount"
    And I create role "Role1" in account "SubAccount"
    And I try to find role with name "Role1" in account "SubAccount"
    Then Role with name "Role1" in account "SubAccount" is found
    And I select the domain "job"
    And I create the following role permissions in account "SubAccount"
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
      | 1       | execute    |
    And I search for the permissions of found role "Role1" in account "SubAccount"
    And I count 4
    And I create user with name "SubUser" in account "SubAccount"
    And I add credentials
      | name    | password      | enabled |
      | SubUser | User@10031995 | true    |
    And I create the access info entity in account "SubAccount"
    And I add access role "Role1" to user "SubUser" in account "SubAccount"
    And I logout
    And I login as user with name "SubUser" and password "User@10031995"
    And I create a job with the name "TestJob"
    And I find a job with name "TestJob"
    And I try to edit job to name "TestJob1"
    And I try to delete the job with name "TestJob1"
    And No exception was thrown
    And I logout

  Scenario: Add Account permissions to the role in child account
  Creating child account as user kapua-sys, adding user and role to this child account.
  Adding permissions from Account domain with Read, Write and Delete actions to the role,
  and after that adding role with permissions to the user. Logout as kapua-sys and login
  as created user, and try to add, edit, find or delete accounts.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create an account with name "SubAccount", organization name "organization" and email address "organization@gmail.com"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And The account with name "kapua-sys" has 1 subaccount
    And I find account with name "SubAccount"
    And I create role "Role1" in account "SubAccount"
    And I try to find role with name "Role1" in account "SubAccount"
    Then Role with name "Role1" in account "SubAccount" is found
    And I select the domain "account"
    And I create the following role permissions in account "SubAccount"
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I search for the permissions of found role "Role1" in account "SubAccount"
    And I count 3
    And I create user with name "SubUser" in account "SubAccount"
    And I add credentials
      | name    | password      | enabled |
      | SubUser | User@10031995 | true    |
    And I create the access info entity in account "SubAccount"
    And I add access role "Role1" to user "SubUser" in account "SubAccount"
    And I logout
    And I login as user with name "SubUser" and password "User@10031995"
    And I create an account with name "TestAccount", organization name "Organization" and email address "organization@org.com"
    And I find account with name "TestAccount"
    When I delete account "TestAccount"
    And No exception was thrown
    And I logout

  Scenario: Add Endpoint_info permissions to the role in child account
  Creating child account as user kapua-sys, adding user and role to this child account.
  Adding permissions from Endpoint_info domain with Read, Write and Delete actions to the
  role and after that adding role with permissions to the user. Logout as kapua-sys and
  login as created user and try to add, edit, find or delete endpoints.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create endpoint with schema "TestEndpoint", domain "com" and port 8000
    And I create an account with name "SubAccount", organization name "organization" and email address "organization@gmail.com"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And The account with name "kapua-sys" has 1 subaccount
    And I find account with name "SubAccount"
    And I create role "Role1" in account "SubAccount"
    And I try to find role with name "Role1" in account "SubAccount"
    Then Role with name "Role1" in account "SubAccount" is found
    And I select the domain "endpoint_info"
    And I create the following role permissions in account "SubAccount"
      | scopeId | actionName | targetScopeId |
      | 1       | read       | 1             |
      | 1       | write      | 1             |
      | 1       | delete     | 1             |
    And I search for the permissions of found role "Role1" in account "SubAccount"
    And I count 3
    And I create user with name "SubUser" in account "SubAccount"
    And I add credentials
      | name    | password      | enabled |
      | SubUser | User@10031995 | true    |
    And I create the access info entity in account "SubAccount"
    And I add access role "Role1" to user "SubUser" in account "SubAccount"
    And I logout
    And I login as user with name "SubUser" and password "User@10031995"
    And I try to find endpoint with schema "TestEndpoint", domain "com" and port 8000
    Then No exception was thrown
    When I expect the exception "SubjectUnauthorizedException" with the text "Required permission: endpoint_info:write:"
    And I create endpoint with schema "endpoint2", domain "dns" and port 20000
    Then An exception was thrown
    When I expect the exception "SubjectUnauthorizedException" with the text "Required permission: endpoint_info:delete:"
    And I delete the endpoint with schema "TestEndpoint", domain "com" and port 8000
    Then An exception was thrown
    And I logout

  Scenario: Add Group permissions to the role in child account
  Creating child account as user kapua-sys, adding user and role to this child account.
  Adding permissions from Group domain with Read, Write and Delete actions to the role
  and after that adding role with permissions to the user. Logout as kapua-sys and login
  as created user and try to add, edit, find or delete groups.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create an account with name "SubAccount", organization name "organization" and email address "organization@gmail.com"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And The account with name "kapua-sys" has 1 subaccount
    And I find account with name "SubAccount"
    And I create role "Role1" in account "SubAccount"
    And I try to find role with name "Role1" in account "SubAccount"
    Then Role with name "Role1" in account "SubAccount" is found
    And I select the domain "group"
    And I create the following role permissions in account "SubAccount"
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I search for the permissions of found role "Role1" in account "SubAccount"
    And I count 3
    And I create user with name "SubUser" in account "SubAccount"
    And I add credentials
      | name    | password      | enabled |
      | SubUser | User@10031995 | true    |
    And I create the access info entity in account "SubAccount"
    And I add access role "Role1" to user "SubUser" in account "SubAccount"
    And I logout
    And I login as user with name "SubUser" and password "User@10031995"
    And I create a group with name "TestGroup"
    And I find the group with name "TestGroup"
    And I update the group name to "TestGroup1"
    When I delete the group with name "TestGroup1"
    And No exception was thrown
    And I logout

  Scenario: Add Role permissions to the role in child account
  Creating child account as user kapua-sys, adding user and role to this child account.
  Adding permissions from Role domain with Read, Write and Delete actions to the role and
  after that adding role with permissions to the user. Logout as kapua-sys and login as
  created user and try to add, edit, find or delete roles.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create an account with name "SubAccount", organization name "organization" and email address "organization@gmail.com"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And The account with name "kapua-sys" has 1 subaccount
    And I find account with name "SubAccount"
    And I create role "Role1" in account "SubAccount"
    And I try to find role with name "Role1" in account "SubAccount"
    Then Role with name "Role1" in account "SubAccount" is found
    And I select the domain "role"
    And I create the following role permissions in account "SubAccount"
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I search for the permissions of found role "Role1" in account "SubAccount"
    And I count 3
    And I create user with name "SubUser" in account "SubAccount"
    And I add credentials
      | name    | password      | enabled |
      | SubUser | User@10031995 | true    |
    And I create the access info entity in account "SubAccount"
    And I add access role "Role1" to user "SubUser" in account "SubAccount"
    And I logout
    And I login as user with name "SubUser" and password "User@10031995"
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    And I find a role with name "test_role"
    And I update the last created role name to "test_role1"
    When I delete the role with name "test_role"
    And No exception was thrown
    And I logout

  Scenario: Add Scheduler permissions to the role in child account
  Creating child account as user kapua-sys, adding user and role to this child account.
  Adding permissions from Schduler domain with Read, Write and Delete actions to the role
  and after that adding role with permissions to the user. Logout as kapua-sys and login as
  created user and try to add, edit, find or delete Schedules. After that add also Job permissions
  and repeat the process (user cannot operate with schedules without Job permissions).

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create an account with name "SubAccount", organization name "organization" and email address "organization@gmail.com"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And The account with name "kapua-sys" has 1 subaccount
    And I find account with name "SubAccount"
    And I create role "Role1" in account "SubAccount"
    And I try to find role with name "Role1" in account "SubAccount"
    Then Role with name "Role1" in account "SubAccount" is found
    And I select the domain "scheduler"
    And I create the following role permissions in account "SubAccount"
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I search for the permissions of found role "Role1" in account "SubAccount"
    And I count 3
    And I create user with name "SubUser" in account "SubAccount"
    And I add credentials
      | name    | password      | enabled |
      | SubUser | User@10031995 | true    |
    And I create the access info entity in account "SubAccount"
    And I add access role "Role1" to user "SubUser" in account "SubAccount"
    And I logout
    And I login as user with name "SubUser" and password "User@10031995"
    And I expect the exception "SubjectUnauthorizedException" with the text "Required permission: job:read:-1:*"
    When I find scheduler properties with name "Interval Job"
    Then An exception was thrown
    And I logout
    And I login as user with name "kapua-sys" and password "kapua-password"
    And I select the domain "job"
    And I create the following role permissions in account "SubAccount"
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I logout
    And I login as user with name "SubUser" and password "User@10031995"
    And I find scheduler properties with name "Interval Job"
    And A regular trigger creator with the name "TestSchedule" and following properties
      | name     | type              | value |
      | interval | java.lang.Integer | 1     |
    And I try to create a new trigger entity from the existing creator
    And I try to edit trigger name "TestSchedule1"
    And I try to delete last created trigger
    And No exception was thrown
    And I logout

  Scenario: Add Tag permissions to the role in child account
  Creating child account as user kapua-sys, adding user and role to this child account.
  Adding permissions from Tag domain with Read, Write and Delete actions to the role and
  after that adding role with permissions to the user. Logout as kapua-sys and login as
  created user and try to add, edit, find or delete Tags.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create an account with name "SubAccount", organization name "organization" and email address "organization@gmail.com"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And The account with name "kapua-sys" has 1 subaccount
    And I find account with name "SubAccount"
    And I create role "Role1" in account "SubAccount"
    And I try to find role with name "Role1" in account "SubAccount"
    Then Role with name "Role1" in account "SubAccount" is found
    And I select the domain "tag"
    And I create the following role permissions in account "SubAccount"
      | scopeId | actionName |
      | 1       | read       |
      | 1       | write      |
      | 1       | delete     |
    And I search for the permissions of found role "Role1" in account "SubAccount"
    And I count 3
    And I create user with name "SubUser" in account "SubAccount"
    And I add credentials
      | name    | password      | enabled |
      | SubUser | User@10031995 | true    |
    And I create the access info entity in account "SubAccount"
    And I add access role "Role1" to user "SubUser" in account "SubAccount"
    And I logout
    And I login as user with name "SubUser" and password "User@10031995"
    And I create tag with name "TestTag" without description
    And I find a tag with name "TestTag"
    And Name of tag "TestTag" is changed into "TestTag1"
    And I delete the tag with name "TestTag1"
    And No exception was thrown
    And I logout

  Scenario: Adding "admin" role twice
  Creating user "TestUser" as user kapua-sys and try to add "admin" role twice. Expected exception should be thrown.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create user with name "TestUser"
    And I find a role with name "admin"
    And I create the access info entity
    And I add access role "admin" to user "TestUser"
    When I count the access roles from user "TestUser"
    Then I count 1
    And I expect the exception "KapuaDuplicateNameException" with the text "An entity with the same name admin already exists."
    And I add access role "admin" to user "TestUser"
    Then An exception was thrown
    And I logout

  Scenario: Adding "admin" role to a user in a child account
  Creating child account as user kapua-sys, creating user "TestUser" in child account.
  Trying to add "admin" role to a user in child account. Expected exception should be thrown.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I find a role with name "admin"
    And I count the roles in scope 1
    And I count 1
    And I create an account with name "SubAccount", organization name "Organization" and email address "test@test.com"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    And I create user with name "TestUser" in account "SubAccount"
    And I create the access info entity in account "SubAccount"
    And I expect the exception "KapuaEntityNotFoundException" with the text "The entity of type role with id/name 1 was not found"
    When I add access role "admin" to user "TestUser" in account "SubAccount"
    Then An exception was thrown
    And I logout

  Scenario: Adding "admin" role to multiple users
  Creating two users as kapua-sys user and adding "admin" role to these two users.
  Both users should have 1 access role, no exceptions shouldn't be thrown.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I find a role with name "admin"
    When I count the roles in scope 1
    Then I count 1
    And I create user with name "TestUser"
    And I create the access info entity
    And I add access role "admin" to user "TestUser"
    When I search for access roles in user "TestUser"
    Then I count 1
    And I create user with name "TestUser2"
    And I create the access info entity
    And I add access role "admin" to user "TestUser2"
    When I search for access roles in user "TestUser2"
    Then I count 1
    And No exception was thrown
    And I logout

  Scenario: testing "userIdsByRoleId" method on a selected role
  I verify that I can fetch users assigned to a given role using the appropriate method

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I find a role with name "admin"
    And I create user with name "TestUser"
    And I create the access info entity
    And I add access role "admin" to user "TestUser"
    And I create user with name "TestUser2"
    And I create the access info entity
    And I add access role "admin" to user "TestUser2"
    And I create user with name "TestUser3"
    And I create user with name "TestUser4"
    And I create user with name "TestUser5"
    When I search for users assigned to the last role I found users
      | name      |
      | TestUser  |
      | TestUser2 |
      | kapua-sys |
    And No exception was thrown
    And I create the access info entity
    And I add access role "admin" to user "TestUser5"
    When I search for users assigned to the last role I found users
      | name      |
      | TestUser  |
      | TestUser2 |
      | TestUser5 |
      | kapua-sys |
    And No exception was thrown
    And I logout

  Scenario: Deleting role after adding it to user
  Creating role "test_role" as kapua-sys user, creating user "TestUser".
  Adding role to created user and then deleting it.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    And I create user with name "TestUser"
    And I create the access info entity
    And I add access role "test_role" to user "TestUser"
    And I search for access roles in user "TestUser"
    And I count 1
    And I delete the role with name "test_role"
    When I search for access roles in user "TestUser"
    Then I count 0
    And No exception was thrown
    And I logout

  Scenario: Adding role to multiple users in child account
  Creating child account and two users in that child account, then creating role in this child account.
  Adding new roles to these two users - both users should have 1 access role, and exception shouldn't be thrown.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create an account with name "SubAccount", organization name "Organization" and email address "test@test.com"
    And I create role "SubRole" in account "SubAccount"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    And I create user with name "SubUser" in account "SubAccount"
    And I create the access info entity in account "SubAccount"
    And I add access role "SubRole" to user "SubUser" in account "SubAccount"
    When I count the access roles from user in account "SubAccount"
    Then I count 1
    And I create user with name "SubUser1" in account "SubAccount"
    And I create the access info entity in account "SubAccount"
    And I add access role "SubRole" to user "SubUser1" in account "SubAccount"
    When I count the access roles from user in account "SubAccount"
    Then I count 1
    And No exception was thrown
    And I logout

  Scenario: Adding role from child account to user in new child account
  Creating child account SubAccount in kapua-sys account. Creating role TestRole in SubAccount.
  Creating child account SubSubAccount in SubAccount account. Creating user in SubSubAccount.
  Trying to add created role from SubAccount to user in SubSubAccount. An exception should be thrown.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create an account with name "SubAccount", organization name "Organization" and email address "test@test.com"
    And I create role "TestRole" in account "SubAccount"
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    And I create an account with name "SubSubAccount", organization name "Organization" and email address "test1@test.com" and child account
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    And I create user with name "TestUser" in account "SubSubAccount"
    And I create the access info entity in account "SubSubAccount"
    And I expect the exception "KapuaEntityNotFoundException" with the text "*"
    When I add access role "TestRole" to user "TestUser" in account "SubSubAccount"
    Then An exception was thrown
    And I logout

  Scenario: Adding the same role to user twice in child account
  Creating child account as kapua-sys user, creating role and user in new child account.
  Trying to add created role twice to the same user. An expected exception should be thrown.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create an account with name "SubAccount", organization name "Organization" and email address "test@test.com"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    And I create user with name "TestUser" in account "SubAccount"
    And I create role "TestRole" in account "SubAccount"
    And I create the access info entity in account "SubAccount"
    And I add access role "TestRole" to user "TestUser" in account "SubAccount"
    And I expect the exception "KapuaDuplicateNameException" with the text "An entity with the same name TestRole already exists."
    When I add access role "TestRole" to user "TestUser" in account "SubAccount"
    Then An exception was thrown
    And I logout

  Scenario: Deleting role after it has been added to user in child account
  Creating child account as user kapua-sys, creating role and user in child account.
  Adding role to the user and then deleting it. Checking if user still has created role.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I create an account with name "SubAccount", organization name "Organization" and email address "test@test.com"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    And I create role "TestRole" in account "SubAccount"
    And I create user with name "TestUser" in account "SubAccount"
    And I create the access info entity in account "SubAccount"
    And I add access role "TestRole" to user "TestUser" in account "SubAccount"
    When I count the access roles from user in account "SubAccount"
    And I count 1
    And I delete the role with name "TestRole"
    When I count the access roles from user in account "SubAccount"
    Then I count 0
    And No exception was thrown
    And I logout

  @teardown
  Scenario: Stop test environment
    Given Stop full docker environment
    And Clean Locator Instance
    And Reset Security Context