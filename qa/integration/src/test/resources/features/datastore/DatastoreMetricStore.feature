###############################################################################
# Copyright (c) 2025, 2025 Eurotech and/or its affiliates and others
#
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0
#
# Contributors:
#     Eurotech - initial API and implementation
#
###############################################################################
@datastore
@env_none

Feature: Datastore tests
  Test storage and retrieval of supported metric types.
  This should be converted as a UnitTest

  @setup
  Scenario: Setup test resources (job-engine is required to init DB)
    Given Init Security Context
    And Start Docker environment with resources
      | db                  |
      | es                  |
      | job-engine          |

  Scenario: Store string metric
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And I store a message with clientId "testStringMetric" on topic "topic/string" with timestamp now and metrics
      | metric       | type    | value          |
      | stringMetric | string  | stringMetric   |
    And I refresh all indices
    When searching for the last DatastoreMessage stored
    Then verify last DatastoreMessage stored exist
    And verify the last stored message matches the last DatastoreMessageCreator

  Scenario: Store integer metric
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And I store a message with clientId "testIntegerMetric" on topic "topic/integer" with timestamp now and metrics
      | metric        | type    | value |
      | integerMetric | integer | 123   |
    And I refresh all indices
    When searching for the last DatastoreMessage stored
    Then verify last DatastoreMessage stored exist
    And verify the last stored message matches the last DatastoreMessageCreator

  Scenario: Store long metric
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And I store a message with clientId "testLongMetric" on topic "topic/long" with timestamp now and metrics
      | metric        | type    | value        |
      | longMetric    | long    | 2147489999   |
    And I refresh all indices
    When searching for the last DatastoreMessage stored
    Then verify last DatastoreMessage stored exist
    And verify the last stored message matches the last DatastoreMessageCreator

  Scenario: Store float metric
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And I store a message with clientId "testFloatMetric" on topic "topic/float" with timestamp now and metrics
      | metric        | type    | value |
      | floatMetric   | float   | 12.3  |
    And I refresh all indices
    When searching for the last DatastoreMessage stored
    Then verify last DatastoreMessage stored exist
    And verify the last stored message matches the last DatastoreMessageCreator

  Scenario: Store double metric
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And I store a message with clientId "testDoubleMetric" on topic "topic/double" with timestamp now and metrics
      | metric        | type    | value |
      | doubleMetric  | double  | 1.23  |
    And I refresh all indices
    When searching for the last DatastoreMessage stored
    Then verify last DatastoreMessage stored exist
    And verify the last stored message matches the last DatastoreMessageCreator

  Scenario: Store boolean metric
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And I store a message with clientId "testBooleanMetric" on topic "topic/boolean" with timestamp now and metrics
      | metric        | type    | value |
      | booleanMetric | boolean | true  |
    And I refresh all indices
    When searching for the last DatastoreMessage stored
    Then verify last DatastoreMessage stored exist
    And verify the last stored message matches the last DatastoreMessageCreator

  Scenario: Store binary metric
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And I store a message with clientId "testBinartMetric" on topic "topic/binary" with timestamp now and metrics
      | metric        | type      | value              |
      | binaryMetric  | binary    | YmluYXJ5TWV0cmlj   |
    And I refresh all indices
    When searching for the last DatastoreMessage stored
    Then verify last DatastoreMessage stored exist
    And verify the last stored message matches the last DatastoreMessageCreator

  Scenario: Store date metric
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And I store a message with clientId "testDateMetric" on topic "topic/date" with timestamp now and metrics
      | metric        | type    | value                     |
      | dateMetric    | date    | 2025-01-01T00:00:00.000Z  |
    And I refresh all indices
    When searching for the last DatastoreMessage stored
    Then verify last DatastoreMessage stored exist
    And verify the last stored message matches the last DatastoreMessageCreator

  @teardown
  Scenario: Stop full docker environment
    Given I logout
    And Stop Docker environment
    And Clean Locator Instance

