###############################################################################
# Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
@account
@env_none

Feature: User Account Service
  The User Account Service is responsible for CRUD operations for user accounts in the Kapua
  database.

  @setup
  @KapuaProperties("locator.class.impl=org.eclipse.kapua.qa.common.MockedLocator")
  Scenario: Initialize test environment
    Given Init Security Context

  Scenario: Handle account creation
  Create a test account and check whether it was created correctly.

    Given I create a generic account with name "test_acc"
    Then The account matches the creator settings

  Scenario: Find all child accounts
  Create a number of subaccounts. Check that the account has the correct number of subaccounts.

    When I create 15 childs for account with Id 1
    Then The account with name "kapua-sys" has 15 subaccounts

  Scenario: Handle duplicate account names
  Accounts with duplicate names must not be created. When a duplicate account name is
  given, the account service should throw an exception.

    Given I create a generic account with name "test_acc_1"
    And I expect the exception "KapuaDuplicateNameException" with the text "An entity with the same name test_acc_1 already exists."
    When I create a generic account with name "test_acc_1"
    Then An exception was thrown

  Scenario: Handle null account name
  If an account name is null, the account service should throw an exception.

    Given I expect the exception "KapuaIllegalNullArgumentException" with the text "*"
    When I create an account with a null name
    Then An exception was thrown

  Scenario: Find account by Id
  The account service must be able to find the requested account entity by its Id.

    Given I create a generic account with name "test_acc_42"
    When I search for the account with the remembered account Id
    Then The account matches the creator settings

  Scenario: Find account by Ids
  The account service must be able to find the requested account entity by its
  parent and account Ids.

    Given I create a generic account with name "test_acc_42"
    When I search for the account with the remembered parent and account Ids
    Then The account matches the creator settings

  Scenario: Find account by random Id
  Search for an account that does not exist.

    When I search for a random account Id
    Then The account does not exist

  Scenario: Find account by name
  The account service must be able to find the requested account entity by its name.

    Given I create a generic account with name "test_acc_42"
    When I search for the account with name "test_acc_42"
    Then The account matches the creator settings

  Scenario: Find by name nonexisting account
  Search for an account name that does not exist.

    When I search for the account with name "test_acc_unlikely_to_exist"
    Then The account does not exist

  Scenario: Modify an existing account
  Test that the account service correctly modifies an existing account. The modified
  account details must be correctly stored in the account database.

    Given I create a generic account with name "test_acc_42"
    When I modify the account "test_acc_42"
    Then Account "test_acc_42" is correctly modified

  Scenario: Modify nonexisting account
  Try to update an account that does not exist anymore. An exception should be thrown.

    Given I create a generic account with name "test_acc_42"
    Then I delete account "test_acc_42"
    Given I expect the exception "KapuaEntityNotFoundException" with the text "The entity of type account with id/name"
    And I modify the current account
    Then An exception was thrown

  Scenario: Delete an existing account
  Delete a previously created account. The account should be deleted from the database.

    Given I create a generic account with name "test_acc_123"
    When I delete account "test_acc_123"
    And I search for the account with name "test_acc_123"
    Then The account does not exist

  Scenario: Delete the Kapua system account
  It must not be possible to delete the system account.

    Given I expect the exception "KapuaIllegalAccessException" with the text "The current subject is not authorized for delete"
    When I try to delete the system account
    Then An exception was thrown
    And The System account exists

  Scenario: Delete nonexisting account
  Try to delete an account with a a random Id. The operation should fail and an exception
  should be thrown.

    Given I expect the exception "KapuaEntityNotFoundException" with the text "The entity of type account with id/name"
    When I delete a random account
    Then An exception was thrown

  Scenario: Every account must have the default configuration items
  Create a new account and check whether it has the default configuration items set.

    Given I create a generic account with name "test_acc_11"
    Then The default configuration for the account is set

  Scenario: A newly created account must have some metadata
  Create a new account. Check whether the account has some associated metadata.

    Given I create a generic account with name "test_acc_11"
    Then The account has metadata

#    Which one? Scenario title says one thing, description states the opposite - which is correct?
  Scenario: It should not be possible to change the configuration items
  Values of the supported configuration items must be modifiable.

    Given I create a generic account with name "test_acc_11"
#    When I expect the exception "org.eclipse.kapua.KapuaException"
    When I configure "integer" item "maxNumberChildEntities" to "5"
#    Then An exception was thrown
    Then The config item "maxNumberChildEntities" is set to "5"

  Scenario: Setting configuration without mandatory items must raise an error
  Mandatory configuration items must always be set. Trying to set configuration items without
  specifying the mandatory items must raise an error.

    Given I create a generic account with name "test_acc_11"
    When I expect the exception "org.eclipse.kapua.KapuaIllegalNullArgumentException"
    When I configure "integer" item "ArbitraryUnknownItem" to "5"
    Then An exception was thrown

  Scenario: Test account query
  Perform a database query and find all the accounts that have the 'scopeId'
  property set to '1' (children of the system account). This is the default query and
  no additional parameter is needed to create it.

    Given I create 9 accounts with organization name "test_org"
    When I query for all accounts that have the system account as parent
    Then The returned value is 9

  @teardown
  Scenario: Reset Security Context for all scenarios
    Given Clean Locator Instance
    And Reset Security Context
