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
@userIntegration
@userPermission
@env_docker

Feature: User Permission tests

  @setup
  Scenario: Start full docker environment
    Given Init Security Context
    And Start full docker environment

  Scenario: Adding One Permission To User
  Create a new user kapua-a, with only one permission - user:read.
  After login kapua-a user should be able to search and find himself.

    When I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain | action |
      | user   | read   |
    Then I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    And I search for user with name "kapua-a"
    Then I find user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I logout

  Scenario: Adding Multiple Permissions To User
  Create a new kapua_a user with all permissions in the User domain.
  After login kapua_a should be able to read, add, edit and delete users.

    When I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain | action |
      | user   | read   |
      | user   | write  |
      | user   | delete |
    Then I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-b | Kapua User b | kapua_b@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    Then I search for user with name "kapua-b"
    And I find user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-b | Kapua User b | kapua_b@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I change user to
      | name    | displayName          | email                 | phoneNumber     | status   | userType |
      | kapua-b | Kapua User b updated | kapua_b_new@kapua.com | +386 31 321 456 | DISABLED | INTERNAL |
    Then I search for user with name "kapua-b"
    And I find user
      | name    | displayName          | email                 | phoneNumber     | status   | userType |
      | kapua-b | Kapua User b updated | kapua_b_new@kapua.com | +386 31 321 456 | DISABLED | INTERNAL |
    And I try to delete user "kapua-b"
    Then I search for user with name "kapua-b"
    And I find no user
    And I logout

  Scenario: Deleting a Permission
  Create a new user kapua-a, with only one permission - user:read.
  Login as kapua-a user, and verify that the permission is added correctly.
  As kapua-sys user delete the only added permission to the kapua_a user.
  After login, kapua_a user should get SubjectUnauthorizedException when doing a user search.

    When I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain | action |
      | user   | read   |
    And I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    And I search for user with name "kapua-a"
    Then I find user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I logout
    When I login as user with name "kapua-sys" and password "kapua-password"
    Then I query for the last permission added to the new User
    And I find the last permission added to the new user
    And I delete the last permission added to the new User
    And I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I search for user with name "kapua-a"
    And An exception was thrown
    And I logout

  Scenario: Adding Previously Deleted Permission
  Create a new user kapua-a, with only one permission - user:read.
  Login as kapua_a and verify that the permission is added correctly.
  As the kapua-sys user remove the only permission added to the kapua_a user.
  After login, kapua_a user should get SubjectUnauthorizedException when doing a user search.
  As kapua-sys user add the previously removed permission to the kapua-a user.
  Login as kapua-a user and verify that the permission is correctly added again.

    When I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain | action |
      | user   | read   |
    And I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    And I search for user with name "kapua-a"
    Then I find user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I logout
    When I login as user with name "kapua-sys" and password "kapua-password"
    Then I query for the last permission added to the new User
    And I find the last permission added to the new user
    And I delete the last permission added to the new User
    And I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I search for user with name "kapua-a"
    Then An exception was thrown
    And I logout
    When I login as user with name "kapua-sys" and password "kapua-password"
    And Add permissions to the last created user
      | domain | action |
      | user   | read   |
    Then I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then I search for user with name "kapua-a"
    And I find user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I logout

  Scenario: Adding Permissions To Parallel User
  Create two users, kapua-a and kapua-b in the same kapua-sys account.
  Add the needed user, account and access_info permissions to the kapua_a user.
  After login, kapua_a user should be able to add user:read permission to the kapua_b user.
  Login as kapua-b user and verify that the permission is correctly.

    When I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain      | action |
      | user        | read   |
      | user        | write  |
      | account     | read   |
      | access_info | read   |
      | access_info | write  |
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-b | Kapua User b | kapua_b@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-b | ToManySecrets123# | true    |
    Then I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    And I search for user with name "kapua-b"
    Then I find user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-b | Kapua User b | kapua_b@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And Add permissions to the last created user
      | domain | action |
      | user   | read   |
    And I logout
    When I login as user with name "kapua-b" and password "ToManySecrets123#"
    Then I search for user with name "kapua-a"
    And I find user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I logout

  Scenario:  Adding Permissions To Child User
  Create a new kapua-a user with user, account and access_info domain permissions in the kapua-sys account.
  Add a new account account-b, with a new kapua-b user with no permissions.
  After login, kapua_a user should be able to add user:read permission to the kapua_b user.
  Login as kapua-b user and verify that the permission is correctly.

    When I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain      | action |
      | user        | read   |
      | user        | write  |
      | account     | read   |
      | access_info | read   |
      | access_info | write  |
    And Account
      | name      |
      | account-b |
    And I configure user service
      | type    | name                       | value |
      | boolean | infiniteChildEntities      | true  |
      | integer | maxNumberChildEntities     | 5     |
      | boolean | lockoutPolicy.enabled      | false |
      | integer | lockoutPolicy.maxFailures  | 3     |
      | integer | lockoutPolicy.resetAfter   | 300   |
      | integer | lockoutPolicy.lockDuration | 3     |
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-b | Kapua User b | kapua_b@kapua.com | +386 31 323 555 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-b | ToManySecrets123# | true    |
    And I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then Add permissions to the last created user
      | domain | action |
      | user   | read   |
    And I logout
    When I login as user with name "kapua-b" and password "ToManySecrets123#"
    Then I search for user with name "kapua-b"
    And I find user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-b | Kapua User b | kapua_b@kapua.com | +386 31 323 555 | ENABLED | INTERNAL |
    And I logout

  Scenario: Add User domain permissions to new user
  Create a new kapua-a user in the kapua-sys account.
  Add all permissions from the User domain to the kapua-a user.
  Login as kapua-a user and verify that all the permissions are added correctly.

    When I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain | action |
      | user   | read   |
      | user   | write  |
      | user   | delete |
    Then I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-b | Kapua User b | kapua_b@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    Then I search for user with name "kapua-b"
    And I find user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-b | Kapua User b | kapua_b@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I change user to
      | name    | displayName          | email                 | phoneNumber     | status   | userType |
      | kapua-b | Kapua User b updated | kapua_b_new@kapua.com | +386 31 321 456 | DISABLED | INTERNAL |
    Then I search for user with name "kapua-b"
    And I find user
      | name    | displayName          | email                 | phoneNumber     | status   | userType |
      | kapua-b | Kapua User b updated | kapua_b_new@kapua.com | +386 31 321 456 | DISABLED | INTERNAL |
    And I try to delete user "kapua-ab"
    Then I search for user with name "kapua-ab"
    And I find no user
    And I logout

  Scenario: Add Device domain permissions to new user
  Create a new kapua-a user in the kapua-sys account.
  Add all permissions from the Device domain to the kapua-a user.
  Login as kapua-a user and verify that all the permissions are added correctly.

    When I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain | action |
      | device | read   |
      | device | write  |
      | device | delete |
    Then I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then A device named "device-client-1"
    And I search for a device with the remembered ID
    Then I find the device
    When I update some device parameters
    Then The device was correctly updated
    And I delete the device with the remembered ID
    And I search for a device with the remembered ID
    Then There is no such device
    And I logout

  Scenario: Add Group domain permissions to new user
  Create a new kapua-a user in the kapua-sys account.
  Add all permissions from the Group domain to the kapua-a user.
  Login as kapua-a user and verify that all the permissions are added correctly.

    When I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain | action |
      | group  | read   |
      | group  | write  |
      | group  | delete |
    Then I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Given I create the group
      | scope | name         |
      | 1     | test_group_1 |
    Then I search for the group with name "test_group_1"
    And The group was found
    Then I update the group name to "updated_test_group_1"
    Then The group was correctly updated
    When I delete the group with name "updated_test_group_1"
    Then I search for the group with name "updated_test_group_1"
    And No group was found
    And I logout

  Scenario: Add Tag domain permissions to new user
  Create a new kapua-a user in the kapua-sys account.
  Add all permissions from the Tag domain to the kapua-a user.
  Login as kapua-a user and verify that all the permissions are added correctly.

    When I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain | action |
      | tag    | read   |
      | tag    | write  |
      | tag    | delete |
    Then I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Given I create tag with name "tag_a" without description
    When Tag with name "tag_a" is searched
    Then I find a tag with name "tag_a"
    Then I find and delete tag with name "tag_a"
    And Tag with name "tag_a" is searched
    And No tag was found
    And I logout

  Scenario: Add Job domain permissions to new user
  Create a new kapua-a user in the kapua-sys account.
  Add permissions from the Job domain to the kapua-a user.
  Login as kapua-a user and verify that all the permissions are added correctly.

    When I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain | action |
      | job    | read   |
      | job    | write  |
      | job    | delete |
    Then I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Given A regular job creator with the name "job-a"
    When I create a new job entity from the existing creator
    When I search for the job in the database
    And The job entity matches the creator
    And I change the job name to "updated-job-a"
    When I search for the job in the database
    Then The job name is "updated-job-a"
    When I delete the job
    And I search for the job in the database
    Then There is no such job item in the database
    And I logout

  Scenario: Add Access Info domain permissions to new user
  Create a new kapua-a and kapua-b users in the kapua-sys account.
  Add permissions from the access_info domain to the kapua-a user.
  Login as kapua-a user and verify that all the permissions are added correctly
  by performing permission add, search and delete on the kapua-b user.

    When I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain      | action |
      | access_info | read   |
      | access_info | write  |
      | access_info | delete |
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-b | Kapua User b | kapua_b@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-b | ToManySecrets123# | true    |
    And I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    And Add permissions to the last created user
      | domain | action |
      | user   | read   |
    And I logout
    When I login as user with name "kapua-b" and password "ToManySecrets123#"
    Then I search for user with name "kapua-b"
    And I find user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-b | Kapua User b | kapua_b@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then I query for the last permission added to the new User
    And I find the last permission added to the new user
    And I delete the last permission added to the new User
    And I logout
    When I login as user with name "kapua-b" and password "ToManySecrets123#"
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I search for user with name "kapua-b"
    And An exception was thrown
    And I logout

  Scenario: Add Role domain permissions to new user
  Create a new kapua-a user in the kapua-sys account.
  Add permissions from the Role domain to the kapua-a user.
  Login as kapua-a user and verify that all the permissions are added correctly.

    When I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain | action |
      | role   | read   |
      | role   | write  |
      | role   | delete |
    And I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Given I create the following role
      | scopeId | name      |
      | 1       | test_role |
    Then I search for the role with name "test_role"
    And The correct role entry was found
    When I update the role name to "updated_test_role"
    And I search for the role with name "updated_test_role"
    When I delete the role with name "updated_test_role"
    And I search for the role with name "updated_test_role"
    Then I find no roles
    And I logout

  Scenario: Add Datastore domain permissions to new user
  Create a new kapua-a user in the kapua-sys account.
  Add permissions from the Datastore domain to the kapua-a user.
  Login as kapua-a user and verify that all the permissions are added correctly.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain    | action |
      | datastore | read   |
      | datastore | write  |
      | datastore | delete |
    And I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    And I prepare a random message and save it as "RandomDataMessage"
    And I store the message "RandomDataMessage" and remember its ID as "RandomDataMessageId"
    And I refresh all indices
    When I search for a data message with ID "RandomDataMessageId" and remember it as "DataStoreMessage"
    Then The datastore message "DataStoreMessage" matches the prepared message "RandomDataMessage"
    When I delete the datastore message with ID "RandomDataMessageId"
    And I refresh all indices
    When I search for a data message with ID "RandomDataMessageId" and remember it as "ShouldBeNull"
    Then Message "ShouldBeNull" is null
    And I logout

  Scenario: Add Domain domain permissions to kapua-sys user
  Login as the kapua-sys user and select the kapua-sys account.
  Verify that the kapua-sys user has all of the permissions from the Credential domain.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    When I count the domain entries in the database
    Then I count 23
    And I create the domain
      | name        | actions             |
      | test_domain | read, write, delete |
    When I search for the last created domain
    Then The domain matches the creator
    When I count the domain entries in the database
    Then I count 24
    When I delete the last created domain
    And I search for the last created domain
    Then There is no domain
    When I count the domain entries in the database
    Then I count 23
    And I logout

  Scenario: Add Domain domain permissions to new user
  Create a new kapua-a user in the kapua-sys account.
  Add all permissions from the Domain domain to the kapua-a user.
  After login the kapua-a user should get the SubjectUnauthorizedException when trying to create a new domain.
  Kapua-a user should be able to perform queries for the already created domain with no exceptions.
  After trying to perform a domain delete kapua-a user should get the SubjectUnauthorizedException.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain | action |
      | domain | read   |
      | domain | write  |
      | domain | delete |
    And I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I create the domain
      | name         | actions             |
      | test_domain1 | read, write, delete |
    Then An exception was thrown
    Given I expect the exception "KapuaIllegalNullArgumentException" with the text "An illegal null value was provided"
    When I search for the last created domain
    And An exception was thrown
    When I count the domain entries in the database
    Then I count 23
    And I logout
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And I create the domain
      | name         | actions             |
      | test_domain2 | read, write, delete |
    When I count the domain entries in the database
    Then I count 24
    Then I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then I search for the last created domain
    And The domain matches the creator
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I delete the last created domain
    Then An exception was thrown
    When I count the domain entries in the database
    Then I count 24
    And I logout

  Scenario: Add Credential domain permissions to new user
  Create a new kapua-a user in the kapua-sys account.
  Add all permissions from the Credential domain to the kapua-a user.
  Login as kapua-a user and verify that all the permissions are added correctly.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain     | action |
      | credential | read   |
      | credential | write  |
      | credential | delete |
      | user       | read   |
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-b | Kapua User b | kapua_b@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    When I search for last created user's credentials
    Then I find 0 credentials
    And I add credentials
      | name    | password           | enabled |
      | kapua-b | ToManySecrets123#2 | true    |
    Then I search for last created user's credentials
    And I find 1 credentials
    And I delete the last created user's credential
    When I search for last created user's credentials
    Then I find 0 credentials
    And I logout

  Scenario: Add Device Event domain permissions to new user
  Create a new kapua-a user in the kapua-sys account.
  Add all permissions from the Broker domain to the kapua-a user, as well as the needed
  permissions from the device domain.
  Login as kapua-a user and verify that all device_event permissions are added correctly.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A device named "test_client1"
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain       | action |
      | device_event | write  |
      | device_event | read   |
      | device_event | delete |
      | device       | read   |
      | device       | write  |
    And I logout
    Then I login as user with name "kapua-a" and password "ToManySecrets123#"
    And A "CREATE" event from device "test_client1"
    When I search for an event with the remembered ID
    Then The event matches the creator parameters
    And I delete the event with the remembered ID
    When I search for an event with the remembered ID
    Then There is no such event
    And I logout

  Scenario: Add Device Connection domain permissions to kapua-sys user
  Login as the kapua-sys user and select the kapua-sys account.
  Verify that the kapua-sys user has all of the permissions from the device_connection domain.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And I have the following connection
      | clientId    | clientIp    | serverIp   | protocol | allowUserChange |
      | testClient1 | 127.0.0.101 | 127.0.0.10 | tcp      | true            |
    When I search for a connection with the client ID "testClient1"
    Then The connection details match
      | clientId    | clientIp    | serverIp   | protocol | allowUserChange |
      | testClient1 | 127.0.0.101 | 127.0.0.10 | tcp      | true            |
    When I modify the connection details to
      | clientIp    | serverIp   | protocol | allowUserChange |
      | 127.0.0.109 | 127.0.0.25 | udp      | true            |
    And I delete the existing connection
    When I search for a connection with the client ID "testClient1"
    Then No connection was found
    And I logout

  Scenario: Add Device Connection domain permissions to new user
  Create a new kapua-a user in the kapua-sys account.
  Add all permissions from the device_connection domain to the kapua-a user.
  Login as kapua-a user and verify that the permissions are added correctly.
  Kapua-a user should be able to perform queries for the already created connections with no exceptions.
  After trying to perform a connection edit or delete kapua-a user should get the SubjectUnauthorizedException.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User a | kapua_a@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain            | action |
      | device_connection | read   |
      | device_connection | write  |
      | device_connection | delete |
    And I have the following connection
      | clientId    | clientIp    | serverIp   | protocol | allowUserChange |
      | testClient1 | 127.0.0.101 | 127.0.0.10 | tcp      | true            |
    And I logout
    Then I login as user with name "kapua-a" and password "ToManySecrets123#"
    When I search for a connection with the client ID "testClient1"
    Then The connection details match
      | clientId    | clientIp    | serverIp   | protocol | allowUserChange |
      | testClient1 | 127.0.0.101 | 127.0.0.10 | tcp      | true            |
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I delete the existing connection
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I modify the connection details to
      | clientIp    | serverIp   | protocol | allowUserChange |
      | 127.0.0.109 | 127.0.0.25 | udp      | true            |
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I have the following connection
      | clientId    | clientIp    | serverIp   | protocol | allowUserChange |
      | testClient2 | 127.0.0.101 | 127.0.0.10 | tcp      | true            |
    Then An exception was thrown
    When I search for a connection with the client ID "testClient2"
    Then No connection was found
    And I logout

  Scenario: Add Scheduler Permissions Without Job Permissions
  Creating "user1", adding permissions with scheduler domain without adding job permissions.
  "user1" should not have permissions to do anything as he is not authorized to do anything with jobs.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name  | displayName  | email           | phoneNumber     | status  | userType |
      | user1 | Kapua User 1 | user1@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name  | password          | enabled |
      | user1 | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain    | action |
      | scheduler | write  |
      | scheduler | read   |
      | scheduler | delete |
    And I logout
    Then I login as user with name "user1" and password "ToManySecrets123#"
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I find scheduler properties with name "Interval Job"
    And A regular trigger creator with the name "TestSchedule" and following properties
      | name     | type              | value |
      | interval | java.lang.Integer | 1     |
    Given I expect the exception "KapuaIllegalNullArgumentException" with the text "*"
    When I try to create a new trigger entity from the existing creator
    Then An exception was thrown
    Given I expect the exception "NullPointerException" with the text "*"
    When I try to edit trigger name "TestSchedule1"
    Then An exception was thrown
    Given I expect the exception "NullPointerException" with the text "*"
    When I try to delete last created trigger
    Then An exception was thrown
    Then I logout

  Scenario: Add Scheduler Permissions With Job Permissions
  Creating "user1", adding permissions with scheduler domain with adding job permissions.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name  | displayName  | email           | phoneNumber     | status  | userType |
      | user1 | Kapua User 1 | user1@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name  | password          | enabled |
      | user1 | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain | action |
      | job    | write  |
      | job    | read   |
      | job    | delete |
    And I logout
    Then I login as user with name "user1" and password "ToManySecrets123#"
    And I find scheduler properties with name "Interval Job"
    And A regular trigger creator with the name "TestSchedule" and following properties
      | name     | type              | value |
      | interval | java.lang.Integer | 1     |
    Given I expect the exception "SubjectUnauthorizedException" with the text "Required permission: scheduler:write:1:*"
    When I try to create a new trigger entity from the existing creator
    Then An exception was thrown
    Given I expect the exception "NullPointerException" with the text "*"
    When I try to edit trigger name "TestSchedule1"
    Then An exception was thrown
    Given I expect the exception "NullPointerException" with the text "*"
    When I try to delete last created trigger
    Then An exception was thrown
    Then I logout
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Add permissions to the last created user
      | domain    | action |
      | scheduler | write  |
      | scheduler | read   |
      | scheduler | delete |
    And I logout
    Then I login as user with name "user1" and password "ToManySecrets123#"
    And I find scheduler properties with name "Interval Job"
    And A regular trigger creator with the name "TestSchedule" and following properties
      | name     | type              | value |
      | interval | java.lang.Integer | 1     |
    When I try to create a new trigger entity from the existing creator
    And I try to edit trigger name "TestSchedule1"
    And I try to delete last created trigger
    Then No exception was thrown
    And I logout

  Scenario: Add Endpoint Permission To The User
  Creating "user1", adding endpoint permissions to the user and test if user can work with endpoints.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Scope with ID 1
    And I select account "kapua-sys"
    And A generic user
      | name  | displayName  | email           | phoneNumber     | status  | userType |
      | user1 | Kapua User 1 | user1@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name  | password          | enabled |
      | user1 | ToManySecrets123# | true    |
    And I create endpoint with schema "endpoint1", domain "com" and port 20000
    And I logout
    Then I login as user with name "user1" and password "ToManySecrets123#"
    Given I expect the exception "SubjectUnauthorizedException" with the text "Required permission: endpoint_info:read:"
    When I try to find endpoint with schema "endpoint1"
    Then An exception was thrown
    Then I logout
    And I login as user with name "kapua-sys" and password "kapua-password"
    And Add permissions to the last created user
      | domain        | action | targetScope |
      | endpoint_info | write  | 1           |
      | endpoint_info | read   | 1           |
      | endpoint_info | delete | 1           |
    Then I logout
    And I login as user with name "user1" and password "ToManySecrets123#"
    When I try to find endpoint with schema "endpoint1"
    Then No exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "Required permission: endpoint_info:write:*:*"
    When I create endpoint with schema "end2", domain "com" and port 20000
    Then An exception was thrown
    When I expect the exception "SubjectUnauthorizedException" with the text "Required permission: endpoint_info:delete:*:*"
    And I delete endpoint with schema "end2", domain "com" and port 20000
    Then An exception was thrown
    Then I logout
    And I login as user with name "kapua-sys" and password "kapua-password"
    When I delete endpoint with schema "end2", domain "com" and port 20000
    Then No exception was thrown
    And I logout

  Scenario: Adding Account:Read permission to user in same scope
  Login as kapua-sys user on the kapua-sys account and add a new user0 user with
  the Account:Read permission to the kapua-sys account. Create a child account subAccount0.
  Login as user0 and perform actions on the subAccount0. Verify that only allowed actions
  are performed and the exception is thrown where needed.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name  | displayName  | email           | phoneNumber     | status  | userType |
      | user0 | Kapua User 0 | user0@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name  | password          | enabled |
      | user0 | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain  | action |
      | account | read   |
    And Account
      | name        | scopeId |
      | subAccount0 | 1       |
    Then I logout
    When I login as user with name "user0" and password "ToManySecrets123#"
    Then I search for the account with the remembered account Id
    And I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I modify the account "subAccount0"
    Then An exception was thrown
    And I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I delete account "subAccount0"
    Then An exception was thrown
    And I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I modify the account "kapua-sys"
    Then An exception was thrown
    And I expect the exception "KapuaIllegalNullArgumentException" with the text "An illegal null value was provided"
    When I delete account "kapua-sys"
    Then An exception was thrown
    And I logout

  Scenario: Adding Account:Write permission to user in same scope
  Login as kapua-sys user on the kapua-sys account and add a new user0 user with
  the Account:Write permission to the kapua-sys account. Create a child account subAccount0.
  Login as user0 and perform actions on the subAccount0. Verify that only allowed actions are
  performed and the exception is thrown where needed.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name  | displayName  | email           | phoneNumber     | status  | userType |
      | user0 | Kapua User 0 | user0@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name  | password          | enabled |
      | user0 | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain  | action |
      | account | write  |
    And Account
      | name        | scopeId |
      | subAccount0 | 1       |
    And I logout
    When I login as user with name "user0" and password "ToManySecrets123#"
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I search for the account with the remembered account Id
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I modify the account "subAccount0"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I delete account "subAccount0"
    And An exception was thrown
    And I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I modify the account "kapua-sys"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I delete account "kapua-sys"
    And An exception was thrown
    And I logout

  Scenario: Adding Account:Delete permission to user in same scope
  Login as kapua-sys user on the kapua-sys account and add a new user0 user with
  the Account:Delete permission to the kapua-sys account. Create a child account subAccount0.
  Login as user0 and perform actions on the subAccount0. Verify that only allowed actions are
  performed and the exception is thrown where needed.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name  | displayName  | email           | phoneNumber     | status  | userType |
      | user0 | Kapua User 0 | user0@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name  | password          | enabled |
      | user0 | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain  | action |
      | account | delete |
    And Account
      | name        | scopeId |
      | subAccount0 | 1       |
    And I logout
    When I login as user with name "user0" and password "ToManySecrets123#"
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I search for the account with the remembered account Id
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I modify the account "subAccount0"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I delete account "subAccount0"
    And An exception was thrown
    And I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I modify the account "kapua-sys"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I delete account "kapua-sys"
    And An exception was thrown
    And I logout

  Scenario: Adding Account:Read and Account:Write permissions to user in same scope
  Login as kapua-sys user on the kapua-sys account and add a new user0 user with the Account:Read
  and Account:Write permissions to the kapua-sys account. Create a child account subAccount0.
  Login as user0 and perform actions on the subAccount0. Verify that only allowed actions are
  performed and the exception is thrown where needed.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name  | displayName  | email           | phoneNumber     | status  | userType |
      | user0 | Kapua User 0 | user0@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name  | password          | enabled |
      | user0 | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain  | action |
      | account | read   |
      | account | write  |
    And Account
      | name        | scopeId |
      | subAccount0 | 1       |
    Then I logout
    When I login as user with name "user0" and password "ToManySecrets123#"
    Then I search for the account with the remembered account Id
    When I modify the account "subAccount0"
    And I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I delete account "subAccount0"
    Then An exception was thrown
    When I modify the account "kapua-sys"
    And I expect the exception "KapuaIllegalNullArgumentException" with the text "An illegal null value was provided"
    When I delete account "kapua-sys"
    Then An exception was thrown
    And I logout

  Scenario: Adding Account:Read and Account:Delete permissions to user in same scope
  Login as kapua-sys user on the kapua-sys account and add a new user0 user with the Account:Read
  and Account:Delete permissions to the kapua-sys account. Create a child account subAccount0.
  Login as user0 and perform actions on the subAccount0. Verify that only allowed actions
  are performed and the exception is thrown where needed.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name  | displayName  | email           | phoneNumber     | status  | userType |
      | user0 | Kapua User 0 | user0@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name  | password          | enabled |
      | user0 | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain  | action |
      | account | read   |
      | account | delete |
    And Account
      | name        | scopeId |
      | subAccount0 | 1       |
    Then I logout
    When I login as user with name "user0" and password "ToManySecrets123#"
    Then I search for the account with the remembered account Id
    And I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I modify the account "subAccount0"
    Then An exception was thrown
    When I delete account "subAccount0"
    And I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I modify the account "kapua-sys"
    Then An exception was thrown
    And I expect the exception "KapuaIllegalNullArgumentException" with the text "An illegal null value was provided"
    When I delete account "kapua-sys"
    Then An exception was thrown
    And I logout

  Scenario: Adding Account:Write and Account:Delete permission to user in same scope
  Login as kapua-sys user on the kapua-sys account and add a new user0 user with the Account:Write
  and Account:Delete permission to the kapua-sys account. Create a child account subAccount0.
  Login as user0 and perform actions on the subAccount0. Verify that only allowed actions are
  performed and the exception is thrown where needed.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name  | displayName  | email           | phoneNumber     | status  | userType |
      | user0 | Kapua User 0 | user0@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name  | password          | enabled |
      | user0 | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain  | action |
      | account | write  |
      | account | delete |
    And Account
      | name        | scopeId |
      | subAccount0 | 1       |
    And I logout
    When I login as user with name "user0" and password "ToManySecrets123#"
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I search for the account with the remembered account Id
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I modify the account "subAccount0"
    And An exception was thrown
    When I delete account "subAccount0"
    And I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I modify the account "kapua-sys"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I delete account "kapua-sys"
    And An exception was thrown
    And I logout

  Scenario: Adding all Account permissions to user in same scope
  Login as kapua-sys user on the kapua-sys account and add a new user0 user with all account
  domain permissions to the kapua-sys account. Create a child account subAccount0.
  Login as user0 and perform actions on the subAccount0. Verify that only allowed actions are
  performed and the exception is thrown where needed.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And A generic user
      | name  | displayName  | email           | phoneNumber     | status  | userType |
      | user0 | Kapua User 0 | user0@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name  | password          | enabled |
      | user0 | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain  | action |
      | account | read   |
      | account | write  |
      | account | delete |
    And Account
      | name        | scopeId |
      | subAccount0 | 1       |
    And I logout
    When I login as user with name "user0" and password "ToManySecrets123#"
    Then I search for the account with the remembered account Id
    And I modify the account "subAccount0"
    And I delete account "subAccount0"
    And I modify the account "kapua-sys"
    Given I expect the exception "KapuaIllegalNullArgumentException" with the text "An illegal null value was provided"
    When I delete account "kapua-sys"
    And An exception was thrown
    And I logout

  Scenario: Adding Account:Read permission to user in sub-account scope
  Login as kapua-sys user on the kapua-sys account and create a new subaccount0 account.
  Create another subAccount1 and add a user1 user with the Account:Read permission to it.
  Login as user1 and perform actions on the subAccount0, subAccount1 and the kapua-sys accounts.
  Verify that only allowed actions are performed and the exception is thrown where needed.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Account
      | name        | scopeId |
      | subAccount0 | 1       |
    And I select account "kapua-sys"
    And Account
      | name        | scopeId |
      | subAccount1 | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And A generic user
      | name  | displayName  | email           | phoneNumber     | status  | userType |
      | user1 | Kapua User 1 | user1@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name  | password          | enabled |
      | user1 | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain  | action |
      | account | read   |
    And I logout
    When I login as user with name "user1" and password "ToManySecrets123#"
    Then I search for the account with the remembered account Id
    And Account "subAccount1" has 0 children
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I modify the account "subAccount0"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I delete account "subAccount0"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I modify the account "kapua-sys"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    When I delete account "kapua-sys"
    And An exception was thrown
    Then Account
      | name         |
      | subAccount01 |
    And Account "subAccount1" has 1 children
    And I logout

  Scenario: Adding Account:Write permission to user in sub-account scope
  Login as kapua-sys user on the kapua-sys account and create a new subaccount0 account.
  Create another subAccount1 and add a user1 user with the Account:Write permission to it.
  Login as user0 and perform actions on the subAccount0 and subAccount1 accounts.
  Verify that only allowed actions are performed and the exception is thrown where needed.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Account
      | name        | scopeId |
      | subAccount0 | 1       |
    And I select account "kapua-sys"
    And Account
      | name        | scopeId |
      | subAccount1 | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And A generic user
      | name  | displayName  | email           | phoneNumber     | status  | userType |
      | user1 | Kapua User 1 | user1@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name  | password          | enabled |
      | user1 | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain  | action |
      | account | write  |
    And I logout
    When I login as user with name "user1" and password "ToManySecrets123#"
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And Account "subAccount1" has 0 children
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I modify the account "subAccount0"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I delete account "subAccount0"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I modify the account "subAccount1"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I delete account "subAccount1"
    And An exception was thrown
    Then Account
      | name         |
      | subAccount01 |
    And Account "subAccount1" has 1 children
    And I set the expiration date to "2030-05-05"
    And I logout

  Scenario: Adding Account:Delete permission to user in sub-account scope
  Login as kapua-sys user on the kapua-sys account and create a new subaccount0 account.
  Create another subAccount1 and add a user1 user with the Account:Delete permission to it.
  Login as user1 and perform actions on the subAccount0 and subAccount1 accounts.
  Verify that only allowed actions are performed and the exception is thrown where needed.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Account
      | name        | scopeId |
      | subAccount0 | 1       |
    And I select account "kapua-sys"
    And Account
      | name        | scopeId |
      | subAccount1 | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And A generic user
      | name  | displayName  | email           | phoneNumber     | status  | userType |
      | user1 | Kapua User 1 | user1@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name  | password          | enabled |
      | user1 | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain  | action |
      | account | delete |
    Then Account
      | name         |
      | subAccount10 |
    And I logout
    When I login as user with name "user1" and password "ToManySecrets123#"
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And Account "subAccount1" has 1 children
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I modify the account "subAccount0"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I delete account "subAccount0"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I modify the account "subAccount1"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I delete account "subAccount1"
    And An exception was thrown
    And I delete account "subAccount10"
    And I logout

  Scenario: Adding all Account permissions to user in sub-account scope
  Login as kapua-sys user on the kapua-sys account and create a new subaccount1 account.
  Create another subAccount01 and add a user1 user with all account domain permissions to it.
  Login as user1 and perform actions on the subAccount1 and subAccount01 accounts.
  Verify that only allowed actions are performed and the exception is thrown where needed.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And Account
      | name        | scopeId |
      | subAccount1 | 1       |
    And Account
      | name         | scopeId |
      | subAccount01 | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And A generic user
      | name  | displayName  | email           | phoneNumber     | status  | userType |
      | user1 | Kapua User 1 | user1@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name  | password          | enabled |
      | user1 | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain  | action |
      | account | read   |
      | account | write  |
      | account | delete |
    And I logout
    When I login as user with name "user1" and password "ToManySecrets123#"
    And Account "subAccount01" has 0 children
    And I modify the account "subAccount01"
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I delete account "subAccount01"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I modify the account "subAccount1"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I delete account "subAccount1"
    And An exception was thrown
    And I logout

  Scenario: Modifying Sub-account of a different parent account
  Login as kapua-sys user on the kapua-sys account and create a new subaccount0 account
  and add user0 to it. Create another subAccount1 and a subAccount01, with user1 on it. User1 has only
  the Account:Read permision. Login as user1 and perform actions on the user0 and subAccount0. Verify
  that only allowed actions are performed and the exception is thrown where needed.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And Account
      | name        | scopeId |
      | subAccount0 | 1       |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And A generic user
      | name  | displayName  | email           | phoneNumber     | status  | userType |
      | user0 | Kapua User 0 | user0@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I select account "kapua-sys"
    And Account
      | name        | scopeId |
      | subAccount1 | 1       |
    And Account
      | name         | scopeId |
      | subAccount01 | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And A generic user
      | name  | displayName  | email           | phoneNumber     | status  | userType |
      | user1 | Kapua User 1 | user1@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name  | password          | enabled |
      | user1 | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain  | action |
      | account | read   |
    And I logout
    When I login as user with name "user1" and password "ToManySecrets123#"
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I search for user with name "user0"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I modify the account "subAccount0"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I delete account "subAccount0"
    And An exception was thrown
    And I logout

  Scenario: Querying Other Items With All Account Permissions
  Login as kapua-sys user on the kapua-sys account and create a new account1 account and add user1 to it.
  User1 should have all the permissions from the account domain. Create a new device, job, tag, group and role.
  Login as user1 and perform actions on the added entities. Verify that only allowed actions are performed and
  the exception is thrown where needed.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And Account
      | name        | scopeId |
      | subAccount1 | 1       |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure the device registry service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 10    |
    And I configure the tag service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure the job service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure the group service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure the role service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And A generic user
      | name  | displayName  | email           | phoneNumber     | status  | userType |
      | user1 | Kapua User 1 | user1@kapua.com | +386 31 321 123 | ENABLED | INTERNAL |
    And I add credentials
      | name  | password          | enabled |
      | user1 | ToManySecrets123# | true    |
    And Add permissions to the last created user
      | domain  | action |
      | account | read   |
      | account | write  |
      | account | delete |
    And A device named "test_device"
    And I create a job with the name "test_job"
    And I create tag with name "test_tag" without description
    And I create a group with name "test_group"
    And I create the following role
      | scopeId | name      |
      | 1       | test_role |
    Then I logout
    When I login as user with name "user1" and password "ToManySecrets123#"
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I search for user with name "user1"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I query for devices with Client Id "test_device"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I find a job with name "test_job"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And Tag with name "test_tag" is searched
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I search for the group with name "test_group"
    And An exception was thrown
    Given I expect the exception "SubjectUnauthorizedException" with the text "User does not have permission"
    And I find a role with name "test_role"
    And An exception was thrown
    And I logout

  @teardown
  Scenario: Stop full docker environment
    Given Stop full docker environment
    And Clean Locator Instance
