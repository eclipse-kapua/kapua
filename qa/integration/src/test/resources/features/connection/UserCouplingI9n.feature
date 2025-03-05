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
@connection
@userCoupling
@env_docker

Feature: User Coupling

  @setup
  Scenario: Start docker environment
    Given Init Security Context
    And Start full docker environment

  Scenario: Test LOOSE user coupling on single connection

    When I login as user with name "kapua-sys" and password "kapua-password"
    Given Account
      | name       | scopeId |
      | test-acc-1 | 1       |
    And A full set of device privileges for account "test-acc-1"
    And The default connection coupling mode for account "test-acc-1" is set to "LOOSE"
    And Such a set of privileged users for account "test-acc-1"
      | name        | password     | displayName | status  |
      | test-user-1 | KeepCalm123. | Test User 1 | ENABLED |
      | test-user-2 | KeepCalm123. | Test User 2 | ENABLED |
    And Devices such as
      | clientId | displayName | modelId         | serialNumber |
      | device-1 | testGateway | ReliaGate 10-20 | 12341234ABC  |
    And The following device connections
      | scope      | clientId | user        | userCouplingMode |
      | test-acc-1 | device-1 | test-user-1 | LOOSE            |

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-1" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-2" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-2" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

  Scenario: New connection with reserved ID

    When I login as user with name "kapua-sys" and password "kapua-password"
    Given Account
      | name       | scopeId |
      | test-acc-1 | 1       |
    And A full set of device privileges for account "test-acc-1"
    And The default connection coupling mode for account "test-acc-1" is set to "LOOSE"
    And Such a set of privileged users for account "test-acc-1"
      | name        | password     | displayName | status  |
      | test-user-1 | KeepCalm123. | Test User 1 | ENABLED |
      | test-user-2 | KeepCalm123. | Test User 2 | ENABLED |
    And Devices such as
      | clientId | displayName | modelId         | serialNumber |
      | device-1 | testGateway | ReliaGate 10-20 | 12341234ABC  |
    And The following device connections
      | scope      | clientId | user        | reservedUser |
      | test-acc-1 | device-1 | test-user-1 | test-user-1  |

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-1" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 0 connections

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-2" and password "KeepCalm123."
    And Device birth message is sent using account name "test-acc-1"
    And I search for a connection from the device "device-2" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-2" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

  Scenario: Test STRICT user coupling on single connection

    When I login as user with name "kapua-sys" and password "kapua-password"
    Given Account
      | name       | scopeId |
      | test-acc-1 | 1       |
    And A full set of device privileges for account "test-acc-1"
    And The default connection coupling mode for account "test-acc-1" is set to "LOOSE"
    And Such a set of privileged users for account "test-acc-1"
      | name        | displayName | status  |
      | test-user-1 | Test User 1 | ENABLED |
      | test-user-2 | Test User 2 | ENABLED |
    And Devices such as
      | clientId | displayName | modelId         | serialNumber |
      | device-1 | testGateway | ReliaGate 10-20 | 12341234ABC  |
    And The following device connections
      | scope      | clientId | user        | userCouplingMode |
      | test-acc-1 | device-1 | test-user-1 | STRICT           |

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-1" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

  Scenario: Test STRICT user coupling with user change allowed on single connection

    When I login as user with name "kapua-sys" and password "kapua-password"
    Given Account
      | name       | scopeId |
      | test-acc-1 | 1       |
    And A full set of device privileges for account "test-acc-1"
    And The default connection coupling mode for account "test-acc-1" is set to "LOOSE"
    And Such a set of privileged users for account "test-acc-1"
      | name        | displayName | status  |
      | test-user-1 | Test User 1 | ENABLED |
      | test-user-2 | Test User 2 | ENABLED |
    And Devices such as
      | clientId | displayName | modelId         | serialNumber |
      | device-1 | testGateway | ReliaGate 10-20 | 12341234ABC  |
    And The following device connections
      | scope      | clientId | user        | userCouplingMode |
      | test-acc-1 | device-1 | test-user-1 | STRICT           |

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-1" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    Then I set the user change flag for the connection from device "device-1" in account "test-acc-1" to "true"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-2" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-2" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

  Scenario: Test LOOSE user coupling with 3 connections

    When I login as user with name "kapua-sys" and password "kapua-password"
    Given Account
      | name       | scopeId |
      | test-acc-1 | 1       |
    And A full set of device privileges for account "test-acc-1"
    And The default connection coupling mode for account "test-acc-1" is set to "LOOSE"
    And Such a set of privileged users for account "test-acc-1"
      | name        | displayName | status  |
      | test-user-1 | Test User 1 | ENABLED |
      | test-user-2 | Test User 2 | ENABLED |
      | test-user-3 | Test User 3 | ENABLED |
    And Devices such as
      | clientId | displayName | modelId         | serialNumber |
      | device-1 | testGateway | ReliaGate 10-20 | 12341234ABC  |
      | device-2 | testGateway | ReliaGate 10-20 | 12341234ABD  |
      | device-3 | testGateway | ReliaGate 10-20 | 12341234ABE  |
    And The following device connections
      | scope      | clientId | user        | userCouplingMode |
      | test-acc-1 | device-1 | test-user-1 | LOOSE            |
      | test-acc-1 | device-2 | test-user-2 | LOOSE            |
      | test-acc-1 | device-3 | test-user-3 | LOOSE            |

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-1" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-2" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-1" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-1" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

  Scenario: Test STRICT user coupling with 3 connections and a reserved user

    When I login as user with name "kapua-sys" and password "kapua-password"
    Given Account
      | name       | scopeId |
      | test-acc-1 | 1       |
    And A full set of device privileges for account "test-acc-1"
    And The default connection coupling mode for account "test-acc-1" is set to "LOOSE"
    And Such a set of privileged users for account "test-acc-1"
      | name        | displayName | status  |
      | test-user-1 | Test User 1 | ENABLED |
      | test-user-2 | Test User 2 | ENABLED |
      | test-user-3 | Test User 3 | ENABLED |
    And Devices such as
      | clientId | displayName | modelId         | serialNumber |
      | device-1 | testGateway | ReliaGate 10-20 | 12341234ABC  |
      | device-2 | testGateway | ReliaGate 10-20 | 12341234ABD  |
      | device-3 | testGateway | ReliaGate 10-20 | 12341234ABE  |
    And The following device connections
      | scope      | clientId | user        | userCouplingMode | reservedUser |
      | test-acc-1 | device-1 | test-user-1 | STRICT           | test-user-1  |
      | test-acc-1 | device-2 | test-user-2 | LOOSE            |              |
      | test-acc-1 | device-3 | test-user-3 | LOOSE            |              |

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-2" and password "KeepCalm123."
    And I search for a connection from the device "device-2" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-3" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

  Scenario: Extra long continuous test with multiple subscenarios with LOOSE default connection mode

    When I login as user with name "kapua-sys" and password "kapua-password"
    Given Account
      | name       | scopeId |
      | test-acc-1 | 1       |
    And A full set of device privileges for account "test-acc-1"
    And The default connection coupling mode for account "test-acc-1" is set to "LOOSE"
    And Such a set of privileged users for account "test-acc-1"
      | name        | displayName | status  |
      | test-user-1 | Test User 1 | ENABLED |
      | test-user-2 | Test User 2 | ENABLED |
      | test-user-3 | Test User 3 | ENABLED |
    And Devices such as
      | clientId | displayName | modelId         | serialNumber |
      | device-1 | testGateway | ReliaGate 10-20 | 12341234ABC  |
      | device-2 | testGateway | ReliaGate 10-20 | 12341234ABD  |
      | device-3 | testGateway | ReliaGate 10-20 | 12341234ABE  |
    And The following device connections
      | scope      | clientId | user        |
      | test-acc-1 | device-1 | test-user-1 |
      | test-acc-1 | device-2 | test-user-2 |
      | test-acc-1 | device-3 | test-user-3 |

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-1" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-2" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-1" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-1" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    Then I set the user coupling mode for the connection from device "device-1" in account "test-acc-1" to "STRICT"
    And I set the reserved user for the connection from device "device-1" in account "test-acc-1" to "test-user-1"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-2" and password "KeepCalm123."
    And I search for a connection from the device "device-2" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-3" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    And I set the reserved user for the connection from device "device-1" in account "test-acc-1" to "null"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-2" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-2" and password "KeepCalm123."
    And I search for a connection from the device "device-2" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-3" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected

    And I set the reserved user for the connection from device "device-1" in account "test-acc-1" to "test-user-1"
    Then I set the user coupling mode for the connection from device "device-2" in account "test-acc-1" to "STRICT"
    # Try to set a duplicate reserved user
    Given I expect the exception "kapuaIntegrityConstraintViolationException" with the text "Entity constraint violation error."
    When I set the reserved user for the connection from device "device-2" in account "test-acc-1" to "test-user-1"
    Then An exception was thrown
    # Reserved users must be unique!
    When I set the reserved user for the connection from device "device-2" in account "test-acc-1" to "test-user-2"
    Then No exception was thrown

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-2" and password "KeepCalm123."
    And I search for a connection from the device "device-2" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-3" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I set the reserved user for the connection from device "device-2" in account "test-acc-1" to "null"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-2" and password "KeepCalm123."
    And I search for a connection from the device "device-2" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-2" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-3" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    Then I set the user change flag for the connection from device "device-2" in account "test-acc-1" to "true"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    Then I set the user change flag for the connection from device "device-2" in account "test-acc-1" to "true"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-2" and password "KeepCalm123."
    And I search for a connection from the device "device-2" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    Then I set the user change flag for the connection from device "device-2" in account "test-acc-1" to "true"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-3" and password "KeepCalm123."
    And I search for a connection from the device "device-2" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "CONNECTED"
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-3" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    Then I set the user coupling mode for the connection from device "device-3" in account "test-acc-1" to "STRICT"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    Then I set the user change flag for the connection from device "device-3" in account "test-acc-1" to "true"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-2" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    Then I set the user change flag for the connection from device "device-3" in account "test-acc-1" to "true"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-3" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

  Scenario: Extra long continuous test with multiple subscenarios with STRICT default connection mode

    When I login as user with name "kapua-sys" and password "kapua-password"
    Given Account
      | name       | scopeId |
      | test-acc-1 | 1       |
    And A full set of device privileges for account "test-acc-1"
    And The default connection coupling mode for account "test-acc-1" is set to "STRICT"
    And Such a set of privileged users for account "test-acc-1"
      | name        | displayName | status  |
      | test-user-1 | Test User 1 | ENABLED |
      | test-user-2 | Test User 2 | ENABLED |
      | test-user-3 | Test User 3 | ENABLED |
    And Devices such as
      | clientId | displayName | modelId         | serialNumber |
      | device-1 | testGateway | ReliaGate 10-20 | 12341234ABC  |
      | device-2 | testGateway | ReliaGate 10-20 | 12341234ABD  |
      | device-3 | testGateway | ReliaGate 10-20 | 12341234ABE  |
    And The following device connections
      | scope      | clientId | user        |
      | test-acc-1 | device-1 | test-user-1 |
      | test-acc-1 | device-2 | test-user-2 |
      | test-acc-1 | device-3 | test-user-3 |

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-1" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    Then I set the user coupling mode for the connection from device "device-1" in account "test-acc-1" to "STRICT"
    And I set the reserved user for the connection from device "device-1" in account "test-acc-1" to "test-user-1"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-2" and password "KeepCalm123."
    And I search for a connection from the device "device-2" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-3" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    And I set the reserved user for the connection from device "device-1" in account "test-acc-1" to "null"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-2" and password "KeepCalm123."
    And I search for a connection from the device "device-2" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-3" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected

    And I set the reserved user for the connection from device "device-1" in account "test-acc-1" to "test-user-1"
    Then I set the user coupling mode for the connection from device "device-2" in account "test-acc-1" to "STRICT"
# Try to set a duplicate reserved user
    Given I expect the exception "kapuaIntegrityConstraintViolationException" with the text "Entity constraint violation error."
    When I set the reserved user for the connection from device "device-2" in account "test-acc-1" to "test-user-1"
    Then An exception was thrown
# Reserved users must be unique!
    When I set the reserved user for the connection from device "device-2" in account "test-acc-1" to "test-user-2"
    Then No exception was thrown

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-2" and password "KeepCalm123."
    And I search for a connection from the device "device-2" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-3" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I set the reserved user for the connection from device "device-2" in account "test-acc-1" to "null"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-2" and password "KeepCalm123."
    And I search for a connection from the device "device-2" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-3" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    Then I set the user change flag for the connection from device "device-2" in account "test-acc-1" to "true"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    Then I set the user change flag for the connection from device "device-2" in account "test-acc-1" to "true"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-2" and password "KeepCalm123."
    And I search for a connection from the device "device-2" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    Then I set the user change flag for the connection from device "device-2" in account "test-acc-1" to "true"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-3" and password "KeepCalm123."
    And I search for a connection from the device "device-2" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-3" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    Then I set the user change flag for the connection from device "device-3" in account "test-acc-1" to "true"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-2" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    Then I set the user change flag for the connection from device "device-3" in account "test-acc-1" to "true"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-3" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

  Scenario: Extra long continuous test with multiple subscenarios with STRICT default connection mode and no previously defined devices

    When I login as user with name "kapua-sys" and password "kapua-password"
    Given Account
      | name       | scopeId |
      | test-acc-1 | 1       |
    And A full set of device privileges for account "test-acc-1"
    And The default connection coupling mode for account "test-acc-1" is set to "STRICT"
    And Such a set of privileged users for account "test-acc-1"
      | name        | displayName | status  |
      | test-user-1 | Test User 1 | ENABLED |
      | test-user-2 | Test User 2 | ENABLED |
      | test-user-3 | Test User 3 | ENABLED |

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And Device birth message is sent using account name "test-acc-1"
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-1" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-2" and password "KeepCalm123."
    And Device birth message is sent using account name "test-acc-1"
    And I search for a connection from the device "device-2" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-2" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-2" and password "KeepCalm123."
    And Device birth message is sent using account name "test-acc-1"
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-2" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I set the reserved user for the connection from device "device-2" in account "test-acc-1" to "test-user-2"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-1" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-2" and password "KeepCalm123."
    And I search for a connection from the device "device-2" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-2" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    Then I set the user change flag for the connection from device "device-3" in account "test-acc-1" to "true"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-3" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-3" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    And The default connection coupling mode for account "test-acc-1" is set to "LOOSE"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-1" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-3" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-3" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-2" and password "KeepCalm123."
    And I search for a connection from the device "device-2" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-2" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-2", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-2" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-3" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-1" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-3", username "test-user-3" and password "KeepCalm123."
    And I search for a connection from the device "device-3" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-3" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    And The default connection coupling mode for account "test-acc-1" is set to "STRICT"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-3" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-3" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    Then I set the user change flag for the connection from device "device-1" in account "test-acc-1" to "true"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-1" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-1" within 10 seconds
    Then KuraMock is disconnected
    And I wait for 2 seconds

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-2" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-3" and password "KeepCalm123."
    And I wait for 2 seconds
    When I search for a connection from the device "device-1" in account "test-acc-1"
    Then I find 1 connection
    And The connection status is "DISCONNECTED"

    Then I set the user change flag for the connection from device "device-1" in account "test-acc-1" to "true"

    When I start the Kura Mock to connect to broker "tcp://localhost:1883", clientId "device-1", username "test-user-3" and password "KeepCalm123."
    And I search for a connection from the device "device-1" in account "test-acc-1" I find 1 connection with status "CONNECTED" and user "test-user-3" within 10 seconds
    Then KuraMock is disconnected

  @teardown
  Scenario: Stop docker environment
    Given Stop full docker environment
    And Clean Locator Instance
