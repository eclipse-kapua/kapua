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
@accountExpiration
@env_none

Feature: Account expiration features
  Accounts have an expiration date. From this date onward the accounts are considered disabled
  and cannot be logged into anymore.

  @setup
  Scenario: Initialize test environment
    Given Init Security Context

  Scenario: Account with future expiration date
  Set the expiration date of an account in the future. It must be possible to log into such
  account.

    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 20    |
    Given Account
      | name      | scopeId | expirationDate |
      | account-a | 1       | tomorrow       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 10    |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And User A
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then No exception was thrown
    And I logout

  Scenario: Account with no expiration date
  Do not set the expiration date of an account. Such an account should never expire.

    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 20    |
    Given Account
      | name      | scopeId |
      | account-a | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 10    |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And User A
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then No exception was thrown
    And I logout

  Scenario: Account past its expiration date
  Set the expiration date of the account in the past. Te account should be expired and should
  not be possible to log in with it.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 30    |
    Given Account
      | name      | scopeId | expirationDate |
      | account-a | 1       | yesterday      |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 10    |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And User A
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | false   |
    And I logout
    Given I expect the exception "KapuaAuthenticationException" with the text "The provided LoginCredentials are DISABLED."
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then An exception was thrown
    And I logout

  Scenario: Account exactly on its expiration date
  Set the expiration date of the account to the current date. Te account should be expired and should
  not be possible to log in with it.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 30    |
    Given Account
      | name      | scopeId | expirationDate |
      | account-a | 1       | today          |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 10    |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And User A
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | false   |
    And I logout
    Given I expect the exception "KapuaAuthenticationException" with the text "The provided LoginCredentials are DISABLED."
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then An exception was thrown
    And I logout

  Scenario: Child account expires before parent
  Create a chain of accounts. Each child account expires before its parent.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    Given Account
      | name      | scopeId | expirationDate |
      | account-a | 1       | 20/7/2018      |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-a"
    And Account
      | name      | expirationDate |
      | account-b | 19/7/2018      |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-b"
    And Account
      | name      | expirationDate |
      | account-c | 18/7/2018      |
    And I logout

  Scenario: Both the parent and child accounts have the same expiration date
  Create a chain of accounts. All accounts have the same expiration dates. This should be allowed.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    Given Account
      | name      | scopeId | expirationDate |
      | account-a | 1       | 20/7/2018      |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-a"
    And Account
      | name      | expirationDate |
      | account-b | 20/7/2018      |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-b"
    And Account
      | name      | expirationDate |
      | account-c | 20/7/2018      |
    And I logout

  Scenario: Both the parent and child accounts do not expire
  Create a chain of accounts. All accounts have null expiration dates. This should be allowed.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    Given Account
      | name      | scopeId |
      | account-a | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-a"
    And Account
      | name      |
      | account-b |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-b"
    And Account
      | name      |
      | account-c |
    And I logout

  Scenario: Child account expires after parent
  Create a chain of two accounts. An attempt to create an account with an expiration date past the
  parents should be impossible.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    Given Account
      | name      | scopeId | expirationDate |
      | account-a | 1       | 20/7/2018      |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-a"
    Given I expect the exception "KapuaIllegalArgumentException" with the text "argument expirationDate"
    And Account
      | name      | expirationDate |
      | account-b | 21/7/2018      |
    Then An exception was thrown

  Scenario: Child account has null expiration date
  Create a chain of two accounts. An attempt to create a child account with a null expiration date.
  This should not be possible.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    Given Account
      | name      | scopeId | expirationDate |
      | account-a | 1       | 20/7/2018      |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-a"
    Given I expect the exception "KapuaIllegalArgumentException" with the text "argument expirationDate"
    And Account
      | name      |
      | account-b |
    Then An exception was thrown

  Scenario: Modify parent expiration so that it still expires after child
  Create a chain of accounts. Modify the expiration date of the parent so that it is still later than
  the child expiration date. Regular case. Should be allowed.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    Given Account
      | name      | scopeId | expirationDate |
      | account-a | 1       | 20/7/2018      |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-a"
    And Account
      | name      | expirationDate |
      | account-b | 19/7/2018      |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-b"
    And Account
      | name      | expirationDate |
      | account-c | 18/7/2018      |
    Given I select account "account-a"
    When I change the current account expiration date to "25/7/2018"
    Then No exception was thrown
    And I logout

  Scenario: Delete parent expiration
  Create a chain of accounts. Set the expiration date of the parent to null. Regular case. Should be allowed.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    Given Account
      | name      | scopeId | expirationDate |
      | account-a | 1       | 20/7/2018      |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-a"
    And Account
      | name      | expirationDate |
      | account-b | 19/7/2018      |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-b"
    And Account
      | name      | expirationDate |
      | account-c | 18/7/2018      |
    Given I select account "account-a"
    When I change the current account expiration date to "null"
    Then No exception was thrown
    And I logout

  Scenario: Modify parent expiration to before child expiration
  Create a chain of accounts. Modify the expiration date of the parent so that it expires before childs.
  Should not be allowed.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    Given Account
      | name      | scopeId | expirationDate |
      | account-a | 1       | 20/7/2018      |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-a"
    And Account
      | name      | expirationDate |
      | account-b | 19/7/2018      |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-b"
    And Account
      | name      | expirationDate |
      | account-c | 18/7/2018      |
    Given I select account "account-a"
    Given I expect the exception "KapuaIllegalArgumentException" with the text "argument expirationDate"
    When I change the current account expiration date to "15/7/2018"
    Then An exception was thrown
    And I logout

  Scenario: Modify middle child expiration so that it still expires before parent
  Create a chain of accounts. Modify the expiration date of the middle child so that it is still before the
  parents expiration date. Regular case. Should be allowed.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    Given Account
      | name      | scopeId | expirationDate |
      | account-a | 1       | 20/7/2018      |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-a"
    And Account
      | name      | expirationDate |
      | account-b | 19/7/2018      |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-b"
    And Account
      | name      | expirationDate |
      | account-c | 10/7/2018      |
    Given I select account "account-b"
    When I change the current account expiration date to "15/7/2018"
    Then No exception was thrown
    And I logout

  Scenario: Modify last child expiration so that it still expires before parent
  Create a chain of accounts. Modify the expiration date of the last child so that it is still before the
  parents expiration date. Regular case. Should be allowed.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    Given Account
      | name      | scopeId | expirationDate |
      | account-a | 1       | 20/7/2018      |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-a"
    And Account
      | name      | expirationDate |
      | account-b | 19/7/2018      |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-b"
    And Account
      | name      | expirationDate |
      | account-c | 10/7/2018      |
    Given I select account "account-c"
    When I change the current account expiration date to "15/7/2018"
    Then No exception was thrown
    And I logout

  Scenario: Modify middle child expiration to outlive parent
  Create a chain of accounts. Modify the expiration date of the middle child so that it expires after its parent.
  Should not be allowed.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    Given Account
      | name      | scopeId | expirationDate |
      | account-a | 1       | 20/7/2018      |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-a"
    And Account
      | name      | expirationDate |
      | account-b | 19/7/2018      |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-b"
    And Account
      | name      | expirationDate |
      | account-c | 18/7/2018      |
    Given I select account "account-b"
    Given I expect the exception "KapuaIllegalArgumentException" with the text "argument expirationDate"
    When I change the current account expiration date to "25/7/2018"
    Then An exception was thrown
    And I logout

  Scenario: Delete middle child expiration
  Create a chain of accounts. Set the expiration date of the middle child to null.
  Should not be allowed.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    Given Account
      | name      | scopeId | expirationDate |
      | account-a | 1       | 20/7/2018      |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-a"
    And Account
      | name      | expirationDate |
      | account-b | 19/7/2018      |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 40    |
    Given I select account "account-b"
    And Account
      | name      | expirationDate |
      | account-c | 18/7/2018      |
    Given I select account "account-b"
    Given I expect the exception "KapuaIllegalArgumentException" with the text "argument expirationDate"
    When I change the current account expiration date to "null"
    Then An exception was thrown
    And I logout


  Scenario: Account status is disabled
  Create disabled account, verify login is not possible.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 30    |
    Given Account
      | name      | scopeId | status |
      | account-a | 1       | DISABLED |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 10    |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And User A
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | false   |
    And I logout
    Given I expect the exception "KapuaAuthenticationException" with the text "The provided LoginCredentials are DISABLED."
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then An exception was thrown
    And I logout

  Scenario: Account status is enabled
  Create enabled account, verify login is possible
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 50    |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 30    |
    Given Account
      | name      | scopeId | status |
      | account-a | 1       | ENABLED |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 10    |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And User A
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | false   |
    And I logout
    And No exception was thrown
    And I logout

  Scenario: Cannot disable account with enabled child accounts
  Create a parent account with ENABLED status and one or more ENABLED child accounts.
  Attempting to change the parent status to DISABLED should fail because it has enabled children.

    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5    |
    Given Account
      | name           | scopeId | status  |
      | parent-account | 1       | ENABLED |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5    |
    Given I select account "parent-account"
    And Account
      | name         | status  |
      | child-one    | ENABLED |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5   |
    And Account
      | name         | status  |
      | child-two    | ENABLED |
    Given I select account "parent-account"
    Given I expect the exception "KapuaIllegalArgumentException" with the text "Cannot disable an account with some enabled child accounts"
    When I change the status of account "parent-account" to "DISABLED"
    Then An exception was thrown
    And I logout

  @teardown
  Scenario: Reset Security Context for all scenarios
    Given Clean Locator Instance
    And Reset Security Context