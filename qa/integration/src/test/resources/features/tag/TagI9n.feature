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
#
###############################################################################
@deviceRegistry
@env_docker

Feature: Tag tests

  @setup
  Scenario: Start full docker environment
    Given Init Security Context
    And Start full docker environment


  Scenario: Create a single device with associated tags
  Create some tags. Then create a device with that associated tags.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I create a device with name "device" and tags
      | tag-1 |
      | tag-2 |
      | tag-3 |
    When I search for a device with name "device"
    Then Tag "tag-1" is assigned to device "device"
    And Tag "tag-2" is assigned to device "device"
    And Tag "tag-3" is assigned to device "device"

  @teardown
  Scenario: Stop full docker environment
    Given Stop full docker environment
    And Clean Locator Instance

