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
@user
@lockoutExpiration
@env_none

Feature: User and Credential expiration and lockout features
  User Service has expiration value after which user is disabled.
  There is also expiration and status on user's credentials which are also tested.
  Additionally login failures and lockout and lockout resets are tested.

  @setup
  Scenario: Init Security Context for all scenarios
    Given Init Security Context

#
# Credential state
#
  Scenario: If user credential is in state enabled, user can login
  User is set up with credentials that have state enabled. If credentials state is
  enabled, user can login into system. All other expiration settings are set for
  successful login. Only state is tested.
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

  Scenario: If user credential is in state disabled, user can not login
  User is set up with credentials that have state disabled. If credentials state is
  disabled, user can not login into system. All other expiration settings are set for
  successful login. Only state is tested.
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
      | kapua-a | ToManySecrets123# | false   |
    And I logout
    Given I expect the exception "KapuaAuthenticationException" with the text "The provided LoginCredentials are DISABLED."
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then An exception was thrown
    And I logout
#
# Expiration date
#
  Scenario: If user credential expiration date is before today, user can not login
  Expiration date on credentials is set one day in the past and is in state enabled.
  This prevents user from logging in.
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
      | name      | scopeId |
      | account-a | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 20    |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And User A
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled | expirationDate |
      | kapua-a | ToManySecrets123# | true    | yesterday      |
    And I logout
    Given I expect the exception "KapuaAuthenticationException" with the text "The provided LoginCredentials are expired."
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then An exception was thrown
    And I logout

  Scenario: If user credential expiration date is today, user can not login it is day inclusive
  Expiration date on credentials is set to today and is in state enabled.
  This prevents user from logging in, because expiration is today and is day inclusive.
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
      | name      | scopeId |
      | account-a | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 20    |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And User A
      | name    | displayName  | email             | phoneNumber     | status  | userType |
      | kapua-a | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL |
    And I add credentials
      | name    | password          | enabled | expirationDate |
      | kapua-a | ToManySecrets123# | true    | today          |
    And I logout
    Given I expect the exception "KapuaAuthenticationException" with the text " The provided LoginCredentials are expired."
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then An exception was thrown
    And I logout

  Scenario: If user credential expiration date is tomorrow, user can login
  Expiration date on credentials is set to tomorrow and is in state enabled.
  This allows user to login, because expiration is not yet reached.
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
      | name    | password          | enabled | expirationDate |
      | kapua-a | ToManySecrets123# | true    | tomorrow       |
    And I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then No exception was thrown
    And I logout

  Scenario: If user expiration date is before today, user can not login
  Expiration date on user is set to yesterday and is in state enabled.
  Expiration on Credentials is not set and is in state enabled.
  This doesn't allow user to login, because expiration was reached.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    Given Account
      | name      | scopeId |
      | account-a | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And User A
      | name    | displayName  | email             | phoneNumber     | status  | userType | expirationDate |
      | kapua-a | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL | yesterday      |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And I logout
    Given I expect the exception "KapuaAuthenticationException" with the text "The provided LoginCredentials are expired."
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then An exception was thrown
    And I logout

  Scenario: If user expiration date is today, user can not login because expiration date was reached
  Expiration date on user is set today and is in state enabled.
  Expiration on Credentials is not set and is in state enabled.
  This doesn't allow user to login, because expiration is reached.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    Given Account
      | name      | scopeId |
      | account-a | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And User A
      | name    | displayName  | email             | phoneNumber     | status  | userType | expirationDate |
      | kapua-a | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL | today          |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And I logout
    Given I expect the exception "KapuaAuthenticationException" with the text "The provided LoginCredentials are expired."
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then An exception was thrown
    And I logout

  Scenario: If user expiration date is tomorrow, user can login
  Expiration date on user is set tomorrow and is in state enabled.
  Expiration on Credentials is not set and is in state enabled.
  This allows user to login, because expiration is not yet reached.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    Given Account
      | name      | scopeId |
      | account-a | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And User A
      | name    | displayName  | email             | phoneNumber     | status  | userType | expirationDate |
      | kapua-a | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL | tomorrow       |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then No exception was thrown
    And I logout
#
# Lockout
#
  Scenario: User locking itself out by using out login attempts
  User service is configured for user to have 3 failed attempts before it is locked
  out. Lockout policy on user service has to be enabled.
  User tries to login three times with wrong password and is locked out. Then
  it logins with correct password but is locked out and denied access.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    Given Account
      | name      | scopeId |
      | account-a | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And User A
      | name    | displayName  | email             | phoneNumber     | status  | userType | expirationDate |
      | kapua-a | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL | tomorrow       |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And I logout
    Given I expect the exception "KapuaAuthenticationException" with the text "The provided LoginCredentials dont match."
    When I login as user with name "kapua-a" and password "WrongPassword123#"
    Then An exception was thrown
    When I login as user with name "kapua-a" and password "WrongPassword123#"
    Then An exception was thrown
    Given I expect the exception "KapuaAuthenticationException" with the text "The provided LoginCredentials are locked."
    When I login as user with name "kapua-a" and password "WrongPassword123#"
    Then An exception was thrown
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then An exception was thrown
    And I logout

  Scenario: User not locking itself out by using less than max failed login attempts
  User service is configured for user to have 3 failed attempts before it is locked
  out. Lockout policy on user service has to be enabled.
  User tries to login two times with wrong password and is not yet locked out. Then
  it logins with correct password and is logged in.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    Given Account
      | name      | scopeId |
      | account-a | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And User A
      | name    | displayName  | email             | phoneNumber     | status  | userType | expirationDate |
      | kapua-a | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL | tomorrow       |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And I logout
    Given I expect the exception "KapuaAuthenticationException" with the text "The provided LoginCredentials dont match."
    When I login as user with name "kapua-a" and password "WrongPassword123#"
    Then An exception was thrown
    When I login as user with name "kapua-a" and password "WrongPassword123#"
    Then An exception was thrown
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then No exception was thrown
    And I logout

  Scenario: User locking itself out with failed attempts and waiting to unlock
  User service is configured for user to have 1 failed attempt before it is locked
  out. Lockout policy on user service has to be enabled.
  User tries to login with wrong password and is locked out. Then it waits for lockout
  time of 1 second to pass and then it logins with correct password and is logged in.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    Given Account
      | name      | scopeId |
      | account-a | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And User A
      | name    | displayName  | email             | phoneNumber     | status  | userType | expirationDate |
      | kapua-a | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL | tomorrow       |
    And I configure credential service
      | type    | name                       | value |
      | boolean | lockoutPolicy.enabled      | true  |
      | integer | lockoutPolicy.maxFailures  | 1     |
      | integer | lockoutPolicy.resetAfter   | 300   |
      | integer | lockoutPolicy.lockDuration | 1     |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And I logout
    Given I expect the exception "KapuaAuthenticationException" with the text "The provided LoginCredentials are locked."
    When I login as user with name "kapua-a" and password "WrongPassword123#"
    Then An exception was thrown
    And I wait 1 second
    And I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then No exception was thrown
    And I logout

  Scenario: User locking itself out with failed attempts and not waiting to unlock
  User service is configured for user to have 1 failed attempt before it is locked
  out. Lockout policy on user service has to be enabled.
  User tries to login with wrong password and is locked out. Then it waits for lockout
  time of 1 second to pass. This wait is not enough, it should wait 5 seconds.
  After wait it logins with correct password and but it is not logged in.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    Given Account
      | name      | scopeId |
      | account-a | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And User A
      | name    | displayName  | email             | phoneNumber     | status  | userType | expirationDate |
      | kapua-a | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL | tomorrow       |
    And I configure credential service
      | type    | name                       | value |
      | boolean | lockoutPolicy.enabled      | true  |
      | integer | lockoutPolicy.maxFailures  | 1     |
      | integer | lockoutPolicy.resetAfter   | 300   |
      | integer | lockoutPolicy.lockDuration | 5     |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And I logout
    Given I expect the exception "KapuaAuthenticationException" with the text "The provided LoginCredentials are locked."
    When I login as user with name "kapua-a" and password "WrongPassword123#"
    Then An exception was thrown
    Then I wait 1 second
    And I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then An exception was thrown
    And I logout

  Scenario: User login with wrong pass, but with enough time between login failures
  User service is configured for user to have 2 failed attempts before it is locked
  out. Lockout policy on user service has to be enabled.
  User tries to login with wrong password but it waits one second between logins. Failed
  logins are reset every second, so user does not get locked out.
  After two failed attempts it logins with correct password and is logged in.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    Given Account
      | name      | scopeId |
      | account-a | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And User A
      | name    | displayName  | email             | phoneNumber     | status  | userType | expirationDate |
      | kapua-a | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL | tomorrow       |
    And I configure credential service
      | type    | name                       | value |
      | boolean | lockoutPolicy.enabled      | true  |
      | integer | lockoutPolicy.maxFailures  | 2     |
      | integer | lockoutPolicy.resetAfter   | 1     |
      | integer | lockoutPolicy.lockDuration | 300   |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And I logout
    Given I expect the exception "KapuaAuthenticationException" with the text "The provided LoginCredentials dont match."
    When I login as user with name "kapua-a" and password "WrongPassword123#"
    Then An exception was thrown
    Then I wait 2 seconds
    When I login as user with name "kapua-a" and password "WrongPassword123#"
    Then An exception was thrown
    And I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then No exception was thrown
    And I logout

  Scenario: User is just created and its credentials are not used to login
  Its lockout error counter is equals to 0.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    Given Account
      | name      | scopeId |
      | account-a | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And User A
      | name    | displayName  | email             | phoneNumber     | status  | userType | expirationDate |
      | kapua-a | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL | tomorrow       |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And I logout
    Then The lockout error counter for user "kapua-a" is 0

  Scenario: User tying to login one time with wrong password
  The user cannot login in, and the lockout error counter is equals to 1.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    Given Account
      | name      | scopeId |
      | account-a | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And User A
      | name    | displayName  | email             | phoneNumber     | status  | userType | expirationDate |
      | kapua-a | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL | tomorrow       |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And I logout
    Given I expect the exception "KapuaAuthenticationException" with the text "The provided LoginCredentials dont match."
    When I login as user with name "kapua-a" and password "WrongPassword123#"
    Then An exception was thrown
    Then The lockout error counter for user "kapua-a" is 1

  Scenario: User login with wrong password and it doesn't provide any otp code
  even if the mfa is activated.
  The user cannot login in, and the lockout error counter is equals to 1.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    Given Account
      | name      | scopeId |
      | account-a | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And User A
      | name    | displayName  | email             | phoneNumber     | status  | userType | expirationDate |
      | kapua-a | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL | tomorrow       |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And I enable mfa
    Then I have mfa enable
    And I logout
    Given I expect the exception "KapuaAuthenticationException" with the text "The provided LoginCredentials dont match."
    When I login as user with name "kapua-a" and password "WrongPassword123#"
    Then An exception was thrown
    Then The lockout error counter for user "kapua-a" is 1

  Scenario: User login with correct username and password, but it doesn't provide any otp code
  even if the mfa is activated.
  The user cannot login in, but the lockout error counter is not increased.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    Given Account
      | name      | scopeId |
      | account-a | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And User A
      | name    | displayName  | email             | phoneNumber     | status  | userType | expirationDate |
      | kapua-a | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL | tomorrow       |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And I enable mfa
    Then I have mfa enable
    And I logout
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then No exception was thrown
    Then The lockout error counter for user "kapua-a" is 0

  Scenario: User login with wrong username and password, after that it login with correct username and password
  but it doesn't provide any otp code even if the mfa is activated.
  The user cannot login in. The lockout error counter is first set to 1, then set to 0.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    Given Account
      | name      | scopeId |
      | account-a | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    And User A
      | name    | displayName  | email             | phoneNumber     | status  | userType | expirationDate |
      | kapua-a | Kapua User A | kapua_a@kapua.com | +386 31 323 444 | ENABLED | INTERNAL | tomorrow       |
    And I add credentials
      | name    | password          | enabled |
      | kapua-a | ToManySecrets123# | true    |
    And I enable mfa
    Then I have mfa enable
    And I logout
    Given I expect the exception "KapuaAuthenticationException" with the text "The provided LoginCredentials dont match."
    When I login as user with name "kapua-a" and password "WrongPassword123#"
    Then An exception was thrown
    Then The lockout error counter for user "kapua-a" is 1
    When I login as user with name "kapua-a" and password "ToManySecrets123#"
    Then No exception was thrown
    Then The lockout error counter for user "kapua-a" is 0

  Scenario: User login concurrently with correct username and password after a login failure
  Every concurrent login resets the lockout error counter set by the login failure, and none of them fails.
    When I login as user with name "kapua-sys" and password "kapua-password"
    And I configure user service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
    Given Account
      | name      | scopeId |
      | account-a | 1       |
    And I configure account service
      | type    | name                   | value |
      | boolean | infiniteChildEntities  | true  |
      | integer | maxNumberChildEntities | 5     |
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
    Given I expect the exception "KapuaAuthenticationException" with the text "The provided LoginCredentials dont match."
    When I login as user with name "kapua-a" and password "WrongPassword123#"
    Then An exception was thrown
    Then The lockout error counter for user "kapua-a" is 1
    When I login concurrently 10 times as user with name "kapua-a" and password "ToManySecrets123#"
    Then No exception was thrown
    Then The lockout error counter for user "kapua-a" is 0

  @teardown
  Scenario: Reset Security Context for all scenarios
    Given Clean Locator Instance
    And Reset Security Context
