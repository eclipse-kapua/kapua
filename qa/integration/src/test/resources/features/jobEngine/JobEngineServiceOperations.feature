###############################################################################
# Copyright (c) 2024, 2024 Eurotech and/or its affiliates and others
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
@env_docker
@it
@jobEngine
@jobEngineOperations

Feature: Job Engine Service - Operations
  Job Engine Service tests for basic operations

  @setup
  Scenario: Setup test resources
    Given Init Security Context
    And Start Docker environment with resources
      | db                  |
      | events-broker       |
      | job-engine          |
      | message-broker      |
      | broker-auth-service |
      | consumer-lifecycle  |

  Scenario: Start - Without JobTarget

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I create a job with the name "Test Job - Empty Targets"
    Then I expect the exception "JobMissingTargetException" with the text "does not have targets configured"
    And I start a job
    And An exception was thrown

  Scenario: Start - Without JobStep

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I create a device with name "Test Target"
    And I create a job with the name "TestJob - Empty Steps"
    And I add device targets to job
      | Test Target |
    Then I expect the exception "JobMissingStepException" with the text "does not have steps configured"
    And I start a job
    And An exception was thrown

  Scenario: Start - JobTarget ok

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    And Device birth message is sent
    And Device "rpione3" is connected within 10s
    And I create a job with the name "TestJob"
    And I add device targets to job
      | rpione3 |
    And I search for step definition with the name
      | Bundle Start |
    And I add job step to job with name "Test Step - Bundle Start" and with selected job step definition and properties
      | name     | type              | value |
      | bundleId | java.lang.String  | 34    |
      | timeout  | java.lang.Long    | 5000  |
    When I start a job
    And I wait job to finish its execution up to 10s
    Then I confirm that job has 1 job execution
    And I confirm that job target in job has step index 0 and status "PROCESS_OK"

  Scenario: Start - JobTarget failed

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I create a device with name "Test Target"
    And I create a job with the name "TestJob"
    And I add device targets to job
      | Test Target |
    And I search for step definition with the name
      | Bundle Start |
    And I add job step to job with name "Test Step - Bundle Start" and with selected job step definition and properties
      | name     | type              | value |
      | bundleId | java.lang.String  | 34    |
      | timeout  | java.lang.Long    | 5000  |
    When I start a job
    And I wait job to finish its execution up to 10s
    Then I confirm that job has 1 job execution
    And I confirm that job target in job has step index 0 and status "PROCESS_FAILED"

  Scenario: Start - JobTarget ok then ok

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    And Device birth message is sent
    And Device "rpione3" is connected within 10s
    And I create a job with the name "TestJob"
    And I add device targets to job
      | rpione3 |
    And I search for step definition with the name
      | Bundle Start |
    And I add job step to job with name "Test Step - Bundle Start" and with selected job step definition and properties
      | name     | type              | value |
      | bundleId | java.lang.String  | 34    |
      | timeout  | java.lang.Long    | 5000  |
    When I start a job
    And I wait job to finish its execution up to 10s
    Then I confirm that job has 1 job execution
    And I confirm that job target in job has step index 0 and status "PROCESS_OK"
    When I start a job
    And I wait job to finish its execution up to 10s
    Then I confirm that job has 2 job execution
    And I confirm that job target in job has step index 0 and status "PROCESS_OK"

  Scenario: Start - JobTarget failed then ok

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I create a device with name "rpione3"
    And I create a job with the name "TestJob"
    And I add device targets to job
      | rpione3 |
    And I search for step definition with the name
      | Bundle Start |
    And I add job step to job with name "Test Step - Bundle Start" and with selected job step definition and properties
      | name     | type              | value |
      | bundleId | java.lang.String  | 34    |
      | timeout  | java.lang.Long    | 5000  |
    When I start a job
    And I wait job to finish its execution up to 10s
    Then I confirm that job has 1 job execution
    And I confirm that job target in job has step index 0 and status "PROCESS_FAILED"
    Then I start the Kura Mock
    And Device birth message is sent
    And Device "rpione3" is connected within 10s
    When I start a job
    And I wait job to finish its execution up to 10s
    Then I confirm that job has 2 job execution
    And I confirm that job target in job has step index 0 and status "PROCESS_OK"

  Scenario: Start - JobTarget failed, then "loading status" (process_awaiting), then ok

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I create a device with name "rpione3"
    And I create a job with the name "TestJob"
    And I add device targets to job
      | rpione3 |
    And I search for step definition with the name
      | Command Execution |
    And I add job step to job with name "Test Step - Command Exec" and with selected job step definition and properties
      | name         | type                                                                   | value                                                                                                                                                                                                                                           |
      | commandInput | org.eclipse.kapua.service.device.management.command.DeviceCommandInput | <?xml version="1.0" encoding="UTF-8"?><commandInput><command>ping</command><arguments><argument>-c</argument><argument>10</argument><argument>8.8.8.8</argument></arguments><timeout>30000</timeout><runAsynch>false</runAsynch></commandInput> |
      | timeout      | java.lang.Long                                                         | 30000                                                                                                                                                                                                                                           |
    When I start a job
    #Now I await some time in order to synch job-engine and the service used to read job executions...
    And I wait job to finish its execution up to 15s
    Then I confirm that job has 1 job execution
    And I confirm that job target in job has step index 0 and status "PROCESS_FAILED"
    Then I start the Kura Mock
    And Device birth message is sent
    And Device "rpione3" is connected within 10s
    When I start a job
    And I wait for another job start up to 2s
    Then I confirm that job has 2 job execution
    And I confirm that job target in job has step index 0 and status "PROCESS_AWAITING"
    And I wait job to finish its execution up to 15s
    And I confirm that job target in job has step index 0 and status "PROCESS_OK"

  Scenario: Start - Two JobSteps

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    And Device birth message is sent
    And Device "rpione3" is connected within 10s
    And I create a job with the name "TestJob"
    And I add device targets to job
      | rpione3 |
    And I search for step definition with the name
      | Bundle Start |
    And I add job step to job with name "Test Step - Bundle Start" and with selected job step definition and properties
      | name     | type              | value |
      | bundleId | java.lang.String  | 34    |
      | timeout  | java.lang.Long    | 5000  |
    And I search for step definition with the name
      | Bundle Stop |
    And I add job step to job with name "Test Step - Bundle Stop" and with selected job step definition and properties
      | name     | type              | value |
      | bundleId | java.lang.String  | 34    |
      | timeout  | java.lang.Long    | 5000  |
    When I start a job
    And I wait job to finish its execution up to 10s
    Then I confirm that job has 1 job execution
    And I confirm that job target in job has step index 1 and status "PROCESS_OK"

  Scenario: Is Running - Check running status

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    And Device birth message is sent
    And Device "rpione3" is connected within 10s
    And I create a job with the name "TestJob"
    And I add device targets to job
      | rpione3 |
    And I search for step definition with the name
      | Command Execution |
    And I add job step to job with name "Test Step - Command Exec" and with selected job step definition and properties
      | name         | type                                                                   | value                                                                                                                                                                                                                                           |
      | commandInput | org.eclipse.kapua.service.device.management.command.DeviceCommandInput | <?xml version="1.0" encoding="UTF-8"?><commandInput><command>ping</command><arguments><argument>-c</argument><argument>10</argument><argument>8.8.8.8</argument></arguments><timeout>30000</timeout><runAsynch>false</runAsynch></commandInput> |
      | timeout      | java.lang.Long                                                         | 15000                                                                                                                                                                                                                                           |
    Then I confirm job is not running
    When I start a job
    And I wait for 5 seconds
    Then I confirm job is running
    When I wait job to finish its execution up to 30s
    Then I confirm job is not running
    Then I confirm that job has 1 job execution
    And I confirm that job target in job has step index 0 and status "PROCESS_OK"

  Scenario: Stop - Not running, never run
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I create a device with name "Test Target"
    And I create a job with the name "TestJob"
    And I add device targets to job
      | Test Target |
    And I search for step definition with the name
      | Bundle Start |
    And I add job step to job with name "Test Step - Bundle Start" and with selected job step definition and properties
      | name     | type              | value |
      | bundleId | java.lang.String  | 34    |
      | timeout  | java.lang.Long    | 5000  |
    Then I expect the exception "JobNotRunningException" with the text "cannot be stopped because it is not running"
    And I stop the job
    And An exception was thrown

  Scenario: Stop - Not running, but was

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    And Device birth message is sent
    And Device "rpione3" is connected within 10s
    And I create a job with the name "TestJob"
    And I add device targets to job
      | rpione3 |
    And I search for step definition with the name
      | Bundle Start |
    And I add job step to job with name "Test Step - Bundle Start" and with selected job step definition and properties
      | name     | type              | value |
      | bundleId | java.lang.String  | 34    |
      | timeout  | java.lang.Long    | 5000  |
    When I start a job
    And I wait job to finish its execution up to 10s
    Then I expect the exception "JobNotRunningException" with the text "cannot be stopped because it is not running"
    And I stop the job
    And An exception was thrown

  Scenario: Stop - Running
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    And Device birth message is sent
    And Device "rpione3" is connected within 10s
    And I create a job with the name "TestJob"
    And I add device targets to job
      | rpione3 |
    And I search for step definition with the name
      | Command Execution |
    And I add job step to job with name "Test Step - Command Exec" and with selected job step definition and properties
      | name         | type                                                                   | value                                                                                                                                                                                                                                           |
      | commandInput | org.eclipse.kapua.service.device.management.command.DeviceCommandInput | <?xml version="1.0" encoding="UTF-8"?><commandInput><command>ping</command><arguments><argument>-c</argument><argument>10</argument><argument>8.8.8.8</argument></arguments><timeout>30000</timeout><runAsynch>false</runAsynch></commandInput> |
      | timeout      | java.lang.Long                                                         | 15000                                                                                                                                                                                                                                           |
    And I search for step definition with the name
      | Bundle Start |
    And I add job step to job with name "Test Step - Bundle Start" and with selected job step definition and properties
      | name     | type              | value |
      | bundleId | java.lang.String  | 34    |
      | timeout  | java.lang.Long    | 5000  |
    And I start a job
    And I wait for 5 seconds
    When I stop the job
    Then I wait job to finish its execution up to 10s
    And I confirm that job has 1 job execution
    And I confirm that job target in job has step index 1 and status "PROCESS_AWAITING"
    When I start a job
    Then I wait job to finish its execution up to 10s
    And I confirm that job has 2 job execution
    And I confirm that job target in job has step index 1 and status "PROCESS_OK"

  @teardown
  Scenario: Tear down test resources
    Given I logout
    And KuraMock is disconnected
    And Stop Docker environment
    And Clean Locator Instance