###############################################################################
# Copyright (c) 2022 Eurotech and/or its affiliates and others
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
@deviceManagement
@deviceManagementWires
@env_docker

Feature: Device Management Wire Graph Service Tests

  #
  # Setup
  #

  @setup
  Scenario: Start full docker environment
    Given Init Security Context
    And Start full docker environment

  #
  # Tests
  #

  Scenario: Wiregraph operations - get, upload, delete

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    And Device is connected within 10 seconds
    And Device status is "CONNECTED" within 10 seconds
    And I select account "kapua-sys"
    And I get the KuraMock device after 5 seconds
    And Wire Graph is requested
    And Wire component configurations are received
    And Component configurations are 10
    And There is a component configurations with id "customComponent" and a property named "factoryPid" with value "com.eurotech.framework.internal.example.wire.component.ExampleWireComponent"
    And There is a component configurations with id "customComponent" and a password property named "passwordProperty" with value "passwordValue"
    And There is a component configurations with id "customComponent" and a array property named "stringArrayProperty" which contains the value "test"
    Then Last received Wire graph is uploaded and no exception is thrown
    Then Wire graph is deleted and no exception is thrown
    Then KuraMock is disconnected
    And I logout

  @teardown
  Scenario: Stop full docker environment
    Given Stop full docker environment
    And Clean Locator Instance
