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
@docker
Feature: Testing docker steps
  Test that documents functionality of docker steps.

  Scenario: Execute possible docker steps to show its usage
    For now it only lists docker images

    Given List images by name "kapua/kapua-broker:1.6.13"
    #And Pull image "kapua/kapua-sql:1.6.13"
    And Pull image "elasticsearch:5.4.0"
    Then Create network
    And Start DB container with name "db"
    And Start ES container with name "es"
    And Start EventBroker container with name "events-broker"
    Then I wait 15 seconds
    And Start Message Broker container
      | name     | brokerAddress  | brokerIp | clusterName  | mqttPort | mqttHostPort | mqttsPort | mqttsHostPort | webPort | webHostPort | debugPort | debugHostPort | brokerInternalDebugPort| dockerImage |
      | broker-1 | broker1         | 0.0.0.0  | test-cluster | 1883     | 1883         | 8883      | 8883          | 8161    | 8161        | 9999      | 9999          | 9991                   | kapua/kapua-broker:1.6.13 |
    And Start Message Broker container
      | name     | brokerAddress  | brokerIp | clusterName  | mqttPort | mqttHostPort | mqttsPort | mqttsHostPort | webPort | webHostPort | debugPort | debugHostPort | brokerInternalDebugPort| dockerImage |
      | broker-2 | broker2        | 0.0.0.0  | test-cluster | 1883     | 1884         | 8883      | 8884          | 8161    | 8162        | 9999      | 9998          | 9991                   | kapua/kapua-broker:1.6.13 |
    Then I wait 30 seconds
    And Create mqtt "client-1" client for broker "0.0.0.0" on port 1883 with user "kapua-sys" and pass "kapua-password"
    And Connect to mqtt client "client-1"
    And Subscribe mqtt client "client-1" to topic "#"
    And Create mqtt "client-2" client for broker "0.0.0.0" on port 1884 with user "kapua-sys" and pass "kapua-password"
    And Connect to mqtt client "client-2"
    And Subscribe mqtt client "client-2" to topic "#"
    And Publish string "foo" to topic "$EDC/kapua-sys/kapua-sys/topic/1" as client "client-1"
    And I wait 5 seconds
    Then Client "client-1" has 1 message
    # Uncomment this step to prove broker cluster is working.
    #And Client "client-2" has 1 message
    And Disconnect mqtt client "client-1"
    And I wait 15 seconds
    Then Stop container with name "broker-2"
    And Remove container with name "broker-2"
    And Stop container with name "broker-1"
    And Remove container with name "broker-1"
    And Stop container with name "events-broker"
    And Remove container with name "events-broker"
    And Stop container with name "es"
    And Remove container with name "es"
    And Stop container with name "db"
    And Remove container with name "db"
    And Remove network
