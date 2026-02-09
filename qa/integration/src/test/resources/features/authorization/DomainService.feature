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
@security
@domainService
@env_none

Feature: Domain Service tests

@setup
Scenario: Initialize test environment
    Given Init Jaxb Context
    And Init Security Context

  Scenario: Count domains in a blank database
  The default domain table must contain 20 preset entries.

    When I login as user with name "kapua-sys" and password "kapua-password"
    When I count the domain entries in the database
    Then I count 23

  Scenario: Regular domain
  Create a regular domain entry. The newly created entry must match the
  creator parameters.

    When I login as user with name "kapua-sys" and password "kapua-password"
    Given I create the domain
      | name        | actions    |
      | test_name_1 | read,write |
    Then A domain was created
    And The domain matches the creator

  Scenario: Domain with null name
  It must not be possible to create a domain entry with a null name. In
  such case the domain service must throw an exception.

    When I login as user with name "kapua-sys" and password "kapua-password"
    Given I expect the exception "KapuaIllegalNullArgumentException" with the text "An illegal null value was provided"
    When I create the domain
      | actions    |
      | read,write |
    Then An exception was thrown

  Scenario: Domain with null actions
  It must not be possible to create a domain entry with a null set of supported actions. In
  such case the domain service must throw an exception.

    When I login as user with name "kapua-sys" and password "kapua-password"
    Given I expect the exception "KapuaIllegalNullArgumentException" with the text "An illegal null value was provided"
    When I create the domain
      | name        |
      | test_name_1 |
    Then An exception was thrown

  Scenario: Domains with duplicate names
  Domain names must be unique in the database. If an already existing name is used for a
  new domain an exception must be thrown.

    When I login as user with name "kapua-sys" and password "kapua-password"
    Given I create the domain
      | name        | actions    |
      | test_name_1 | read,write |
    Then A domain was created
    And The domain matches the creator
    Given I expect the exception "kapuaIntegrityConstraintViolationException" with the text "Entity constraint violation error."
    When I create the domain
      | name        | actions    |
      | test_name_1 | read,write |
    Then An exception was thrown

  Scenario: Find the last created domain entry
  It must be possible to find a dmain entry based on its unique ID.

    When I login as user with name "kapua-sys" and password "kapua-password"
    Given I create the domain
      | name        | actions      |
      | test_name_2 | read,execute |
    When I search for the last created domain
    Then The domain matches the creator

  Scenario: Compare domain entries
  The domain object is comparable.

    When I login as user with name "kapua-sys" and password "kapua-password"
    Then I can compare domain objects

  Scenario: Delete the last created domain entry
  It must be possible to delete an entry from the domain table. The domain is deleted
  based on its ID.

    When I login as user with name "kapua-sys" and password "kapua-password"
    Given I create the domain
      | name        | actions      |
      | test_name_2 | read,execute |
    When I search for the last created domain
    Then The domain matches the creator
    When I delete the last created domain
    And I search for the last created domain
    Then There is no domain

  Scenario: Delete a nonexistent domain
  If the requested ID is not found in the database, the delete function must throw
  an exception.

    When I login as user with name "kapua-sys" and password "kapua-password"
    Given I expect the exception "KapuaEntityNotFoundException" with the text "The entity of type domain"
    When I try to delete domain with a random ID
    Then An exception was thrown

  Scenario: Count domains in the database
  It must be possible to count all the domain entries in the domain table.

    When I login as user with name "kapua-sys" and password "kapua-password"
    When I count the domain entries in the database
    Then This is the initial count
    Given I create the domains
      | name        | actions      |
      | test_name_1 | read,write   |
      | test_name_2 | read,execute |
      | test_name_3 | write,delete |
    When I count the domain entries in the database
    Then 3 more domains were created

  Scenario: Domain entry query
  It must be possible to query domain entries based on the name property.

    When I login as user with name "kapua-sys" and password "kapua-password"
    Given I create the domains
      | name        | actions      |
      | test_name_1 | read,write   |
      | test_name_2 | read,execute |
      | test_name_3 | write,delete |
    When I query for domains with the name "test_name_2"
    Then I count 1

  @teardown
  Scenario: Reset Security Context for all scenarios
    Given Clean Locator Instance
    And Reset Security Context
