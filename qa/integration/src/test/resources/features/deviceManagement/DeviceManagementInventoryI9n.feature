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
@deviceManagementInventory
@env_docker

Feature: Device Management Inventory Service Tests

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

  Scenario: Request Inventory Items to a Device

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    And Device is connected within 10 seconds
    And Device status is "CONNECTED" within 10 seconds
    And I select account "kapua-sys"
    And I get the KuraMock device after 5 seconds
    And Inventory Items are requested
    And Inventory Items are received
    And Inventory Items are 306
    And Inventory Items has Item named "org.eclipse.kura.hook.file.move.provider" is present
    And Inventory Items has Item named "org.eclipse.kura.hook.file.move.provider" has type "BUNDLE"
    And Inventory Items has Item named "org.eclipse.kura.hook.file.move.provider" has version "1.1.0.202202171533"
    And Inventory Items has Item named "xmlstarlet" is present
    And Inventory Items has Item named "xmlstarlet" has type "APK"
    And Inventory Items has Item named "xmlstarlet" has version "1.6.1-r0"
    Then KuraMock is disconnected
    And I logout

  Scenario: Request Inventory Bundles to a Device

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    And Device is connected within 10 seconds
    And Device status is "CONNECTED" within 10 seconds
    And I select account "kapua-sys"
    And I get the KuraMock device after 5 seconds
    And Inventory Bundles are requested
    And Inventory Bundles are received
    And Inventory Bundles are 180
    And Inventory Bundles has Bundle named "io.netty.transport-native-unix-common" is present
    And Inventory Bundles has Bundle named "io.netty.transport-native-unix-common" has id "140"
    And Inventory Bundles has Bundle named "io.netty.transport-native-unix-common" has version "4.1.68.Final"
    And Inventory Bundles has Bundle named "io.netty.transport-native-unix-common" has status "RESOLVED"
    And Inventory Bundles has Bundle named "io.netty.transport-native-unix-common" is signed "true"
    And Inventory Bundles has Bundle named "org.apache.felix.fileinstall" is present
    And Inventory Bundles has Bundle named "org.apache.felix.fileinstall" has id "178"
    And Inventory Bundles has Bundle named "org.apache.felix.fileinstall" has version "3.6.4"
    And Inventory Bundles has Bundle named "org.apache.felix.fileinstall" has status "ACTIVE"
    And Inventory Bundles has Bundle named "org.apache.felix.fileinstall" is signed "false"
    Then KuraMock is disconnected
    And I logout

  Scenario: Start an Inventory Bundles

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    And Device is connected within 10 seconds
    And Device status is "CONNECTED" within 10 seconds
    And I select account "kapua-sys"
    And I get the KuraMock device after 5 seconds
    And Inventory Bundles are requested
    And Inventory Bundles are received
    And Inventory Bundles are 180
    And Inventory Bundles has Bundle named "io.netty.transport-native-unix-common" is present
    And Inventory Bundles has Bundle named "io.netty.transport-native-unix-common" has id "140"
    And Inventory Bundles has Bundle named "io.netty.transport-native-unix-common" has version "4.1.68.Final"
    And Inventory Bundles has Bundle named "io.netty.transport-native-unix-common" has status "RESOLVED"
    And Inventory Bundles has Bundle named "io.netty.transport-native-unix-common" is signed "true"
    And I start Inventory Bundle named "io.netty.transport-native-unix-common"
    And Inventory Bundles are requested
    And Inventory Bundles are received
    And Inventory Bundles are 180
    And Inventory Bundles has Bundle named "io.netty.transport-native-unix-common" is present
    And Inventory Bundles has Bundle named "io.netty.transport-native-unix-common" has id "140"
    And Inventory Bundles has Bundle named "io.netty.transport-native-unix-common" has version "4.1.68.Final"
    And Inventory Bundles has Bundle named "io.netty.transport-native-unix-common" has status "ACTIVE"
    And Inventory Bundles has Bundle named "io.netty.transport-native-unix-common" is signed "true"
    Then KuraMock is disconnected
    And I logout

  Scenario: Stop an Inventory Bundles

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    And Device is connected within 10 seconds
    And Device status is "CONNECTED" within 10 seconds
    And I select account "kapua-sys"
    And I get the KuraMock device after 5 seconds
    And Inventory Bundles are requested
    And Inventory Bundles are received
    And Inventory Bundles are 180
    And Inventory Bundles has Bundle named "org.apache.felix.fileinstall" is present
    And Inventory Bundles has Bundle named "org.apache.felix.fileinstall" has id "178"
    And Inventory Bundles has Bundle named "org.apache.felix.fileinstall" has version "3.6.4"
    And Inventory Bundles has Bundle named "org.apache.felix.fileinstall" has status "ACTIVE"
    And Inventory Bundles has Bundle named "org.apache.felix.fileinstall" is signed "false"
    And I stop Inventory Bundle named "org.apache.felix.fileinstall"
    And Inventory Bundles are requested
    And Inventory Bundles are received
    And Inventory Bundles are 180
    And Inventory Bundles has Bundle named "org.apache.felix.fileinstall" is present
    And Inventory Bundles has Bundle named "org.apache.felix.fileinstall" has id "178"
    And Inventory Bundles has Bundle named "org.apache.felix.fileinstall" has version "3.6.4"
    And Inventory Bundles has Bundle named "org.apache.felix.fileinstall" has status "RESOLVED"
    And Inventory Bundles has Bundle named "org.apache.felix.fileinstall" is signed "false"
    Then KuraMock is disconnected
    And I logout

  Scenario: Request Inventory Container to a Device

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    And Device is connected within 10 seconds
    And Device status is "CONNECTED" within 10 seconds
    And I select account "kapua-sys"
    And I get the KuraMock device after 5 seconds
    And Inventory Containers are requested
    And Inventory Containers are received
    And Inventory Containers are 11
    And Inventory Containers has Container named "db" is present
    And Inventory Containers has Container named "db" has version "kapua/kapua-sql:latest"
    And Inventory Containers has Container named "db" has type "DOCKER"
    And Inventory Containers has Container named "es" is present
    And Inventory Containers has Container named "es" has version "docker.elastic.co/elasticsearch/elasticsearch:7.8.1"
    And Inventory Containers has Container named "es" has type "DOCKER"
    Then KuraMock is disconnected
    And I logout

  Scenario: Start an Inventory Container

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    And Device is connected within 10 seconds
    And Device status is "CONNECTED" within 10 seconds
    And I select account "kapua-sys"
    And I get the KuraMock device after 5 seconds
    And Inventory Containers are requested
    And Inventory Containers are received
    And Inventory Containers are 11
    And Inventory Containers has Container named "db" is present
    And Inventory Containers has Container named "db" has version "kapua/kapua-sql:latest"
    And Inventory Containers has Container named "db" has type "DOCKER"
    And I start Inventory Container named "db"
    And Inventory Containers are requested
    And Inventory Containers are received
    And Inventory Containers are 11
    And Inventory Containers has Container named "db" is present
    And Inventory Containers has Container named "db" has version "kapua/kapua-sql:latest"
    And Inventory Containers has Container named "db" has type "DOCKER"
    Then KuraMock is disconnected
    And I logout

  Scenario: Stop an Inventory Container

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    And Device is connected within 10 seconds
    And Device status is "CONNECTED" within 10 seconds
    And I select account "kapua-sys"
    And I get the KuraMock device after 5 seconds
    And Inventory Containers are requested
    And Inventory Containers are received
    And Inventory Containers are 11
    And Inventory Containers has Container named "db" is present
    And Inventory Containers has Container named "db" has version "kapua/kapua-sql:latest"
    And Inventory Containers has Container named "db" has type "DOCKER"
    And I stop Inventory Container named "db"
    And Inventory Containers are requested
    And Inventory Containers are received
    And Inventory Containers are 11
    And Inventory Containers has Container named "db" is present
    And Inventory Containers has Container named "db" has version "kapua/kapua-sql:latest"
    And Inventory Containers has Container named "db" has type "DOCKER"
    Then KuraMock is disconnected
    And I logout

  Scenario: Request Inventory Images to a Device

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    And Device is connected within 10 seconds
    And Device status is "CONNECTED" within 10 seconds
    And I select account "kapua-sys"
    And I get the KuraMock device after 5 seconds
    And Inventory Images are requested
    And Inventory Images are received
    And Inventory Images are 3
    And Inventory Images has Image named "nginx" is present
    And Inventory Images has Image named "nginx" has version "latest"
    And Inventory Images has Image named "nginx" has type "CONTAINER_IMAGE"
    And Inventory Images has Image named "alpine" is present
    And Inventory Images has Image named "alpine" has version "latest"
    And Inventory Images has Image named "alpine" has type "CONTAINER_IMAGE"
    Then I delete Inventory Image named "nginx"
    And No exception was thrown
    Then KuraMock is disconnected
    And I logout

  Scenario: Request Inventory System Packages to a Device

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    And Device is connected within 10 seconds
    And Device status is "CONNECTED" within 10 seconds
    And I select account "kapua-sys"
    And I get the KuraMock device after 5 seconds
    And Inventory System Packages are requested
    And Inventory System Packages are received
    And Inventory System Packages are 113
    And Inventory System Packages has System Package named "bash" is present
    And Inventory System Packages has System Package named "bash" has version "5.1.8-r0"
    And Inventory System Packages has System Package named "bash" has type "APK"
    And Inventory System Packages has System Package named "libxcb" is present
    And Inventory System Packages has System Package named "libxcb" has version "1.14-r2"
    And Inventory System Packages has System Package named "libxcb" has type "APK"
    Then KuraMock is disconnected
    And I logout

  Scenario: Request Inventory Deployment Packages to a Device

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    And Device is connected within 10 seconds
    And Device status is "CONNECTED" within 10 seconds
    And I select account "kapua-sys"
    And I get the KuraMock device after 5 seconds
    And Inventory Deployment Packages are requested
    And Inventory Deployment Packages are received
    And Inventory Deployment Packages are 2
    And Inventory Deployment Packages has Deployment Package named "org.eclipse.kura.demo.heater" is present
    And Inventory Deployment Packages has Deployment Package named "org.eclipse.kura.demo.heater" has version "1.0.300"
    And Inventory Deployment Packages has Deployment Package named "org.eclipse.kura.demo.heater" has 1 bundles
    And Inventory Deployment Packages has Deployment Package named "org.eclipse.kura.demo.heater" has bundle named "org.eclipse.kura.demo.heater"
    And Inventory Deployment Packages has Deployment Package named "org.eclipse.kura.demo.heater" has bundle named "org.eclipse.kura.demo.heater" with id "180"
    And Inventory Deployment Packages has Deployment Package named "org.eclipse.kura.demo.heater" has bundle named "org.eclipse.kura.demo.heater" with version "1.0.300"
    And Inventory Deployment Packages has Deployment Package named "org.eclipse.kura.demo.heater" has bundle named "org.eclipse.kura.demo.heater" with state "RESOLVED"
    And Inventory Deployment Packages has Deployment Package named "org.eclipse.kura.demo.heater" has bundle named "org.eclipse.kura.demo.heater" with signed "false"
    And Inventory Deployment Packages has Deployment Package named "de.dentrassi.kura.addons.utils.fileinstall" is present
    And Inventory Deployment Packages has Deployment Package named "de.dentrassi.kura.addons.utils.fileinstall" has version "0.6.0"
    And Inventory Deployment Packages has Deployment Package named "de.dentrassi.kura.addons.utils.fileinstall" has 1 bundles
    And Inventory Deployment Packages has Deployment Package named "de.dentrassi.kura.addons.utils.fileinstall" has bundle named "org.apache.felix.fileinstall"
    And Inventory Deployment Packages has Deployment Package named "de.dentrassi.kura.addons.utils.fileinstall" has bundle named "org.apache.felix.fileinstall" with id "178"
    And Inventory Deployment Packages has Deployment Package named "de.dentrassi.kura.addons.utils.fileinstall" has bundle named "org.apache.felix.fileinstall" with version "3.6.4"
    And Inventory Deployment Packages has Deployment Package named "de.dentrassi.kura.addons.utils.fileinstall" has bundle named "org.apache.felix.fileinstall" with state "ACTIVE"
    And Inventory Deployment Packages has Deployment Package named "de.dentrassi.kura.addons.utils.fileinstall" has bundle named "org.apache.felix.fileinstall" with signed "true"
    Then KuraMock is disconnected
    And I logout

  @teardown
  Scenario: Stop full docker environment
    Given Stop full docker environment
    And Clean Locator Instance
