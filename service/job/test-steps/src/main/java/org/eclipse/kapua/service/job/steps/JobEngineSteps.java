/*******************************************************************************
 * Copyright (c) 2021, 2025 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.job.steps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.job.engine.JobEngineService;
import org.eclipse.kapua.job.engine.JobStartOptions;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.kapua.service.device.registry.DeviceAttributes;
import org.eclipse.kapua.service.device.registry.DeviceFactory;
import org.eclipse.kapua.service.device.registry.DeviceListResult;
import org.eclipse.kapua.service.device.registry.DeviceQuery;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.job.JobFactory;
import org.eclipse.kapua.service.job.JobListResult;
import org.eclipse.kapua.service.job.JobQuery;
import org.eclipse.kapua.service.job.JobService;
import org.eclipse.kapua.service.job.targets.JobTarget;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;

@Singleton
public class JobEngineSteps extends JobServiceTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(JobEngineSteps.class);

    private DeviceFactory deviceFactory;
    private DeviceRegistryService deviceRegistryService;
    private JobEngineService jobEngineService;
    private JobService jobService;
    private JobFactory jobFactory;

    @Inject
    public JobEngineSteps(StepData stepData) {
        super(stepData);
    }

    @Before(value = "@env_docker or @env_docker_base or @env_none", order = 10)
    public void beforeScenarioNone(Scenario scenario) {
        updateScenario(scenario);
    }

    @After(value = "@setup")
    public void setServices() {
        KapuaLocator locator = KapuaLocator.getInstance();

        deviceFactory = locator.getFactory(DeviceFactory.class);
        deviceRegistryService = locator.getService(DeviceRegistryService.class);
        jobEngineService = locator.getService(JobEngineService.class);
        jobService = locator.getService(JobService.class);
        jobFactory = locator.getFactory(JobFactory.class);
    }

    @When("I start a job")
    public void startJob() throws Exception {
        primeException();
        KapuaId currentJobId = (KapuaId) stepData.get(CURRENT_JOB_ID);
        try {
            JobStartOptions jobStartOptions = new JobStartOptions();
            jobStartOptions.setEnqueue(true);
            jobEngineService.startJob(getCurrentScopeId(), currentJobId, jobStartOptions);
        } catch (KapuaException ke) {
            verifyException(ke);
        }
    }

    @When("I start a job for targets")
    public void startJobOnPreciseTargets(List<String> clientIds) throws Exception {
        primeException();
        KapuaId currentJobId = (KapuaId) stepData.get(CURRENT_JOB_ID);
        try {
            DeviceQuery deviceQuery = new DeviceQuery(getCurrentScopeId());
            deviceQuery.setPredicate(
                    deviceQuery.attributePredicate(DeviceAttributes.CLIENT_ID, clientIds)
            );
            DeviceListResult devices = deviceRegistryService.query(deviceQuery);
            List<KapuaId> deviceIds = devices.getItems()
                    .stream()
                    .map(KapuaEntity::getId)
                    .collect(Collectors.toList());

            Set<KapuaId> targetIdsSublist = new HashSet<>();
            List<JobTarget> currentJobTargets = (ArrayList<JobTarget>) stepData.get(JOB_TARGET_LIST);
            for (JobTarget jobT : currentJobTargets) {
                if (deviceIds.contains(jobT.getJobTargetId())) {
                    targetIdsSublist.add(jobT.getId());
                }
            }

            JobStartOptions jobStartOptions = new JobStartOptions();
            jobStartOptions.setTargetIdSublist(targetIdsSublist);
            jobStartOptions.setEnqueue(true);
            jobEngineService.startJob(getCurrentScopeId(), currentJobId, jobStartOptions);
        } catch (KapuaException ke) {
            verifyException(ke);
        }
    }

    // Wait Job Running

    /**
     * Waits the {@link Job} in context to start.
     *
     * @param waitSeconds
     *         The max time to wait
     * @throws Exception
     * @since 2.1.0
     */
    @And("I wait for another job start up to {int}s")
    public void waitJobInContextToStart(int waitSeconds) throws Exception {
        Job job = (Job) stepData.get(JOB);

        long now = System.currentTimeMillis();
        while ((System.currentTimeMillis() - now) < (waitSeconds * 1000L)) {
            try {
                if (jobEngineService.isRunning(job.getScopeId(), job.getId())) {
                    return;
                }
            } catch (Exception e) {
                LOG.warn("Error while checking running status for Job {}. Ignoring... Error: {}", job.getName(), e.getMessage());
            }

            // Check frequently!
            TimeUnit.MILLISECONDS.sleep(25);
        }

        Assert.fail("Job " + job.getName() + " did not start an execution within " + waitSeconds + "s");
    }

    // Wait Job Finish Run

    /**
     * Waits the last {@link Job} in context to finish it execution up the given wait time
     *
     * @param waitSeconds
     *         The max time to wait
     * @throws Exception
     * @since 2.1.0
     */
    @And("I wait job to finish its execution up to {int}s")
    public void waitJobInContextUpTo(int waitSeconds) throws Exception {
        Job job = (Job) stepData.get(JOB);

        waitJobUpTo(job, waitSeconds);
    }

    /**
     * Looks for a {@link Job} by its {@link Job#getName()} and waits to finish it execution up the given wait time
     *
     * @param jobName
     *         The {@link Job#getName()} to look for
     * @param waitSeconds
     *         The max time to wait
     * @throws Exception
     * @since 2.1.0
     */
    @And("I wait job {string} to finish its execution up to {int}s")
    public void waitJobByNameUpTo(String jobName, int waitSeconds) throws Exception {
        Job job = findJob(jobName);

        waitJobUpTo(job, waitSeconds);
    }

    /**
     * Wait the given {@link Job} to finish its execution up the given wait time
     *
     * @param job
     *         The {@link Job} to monitor
     * @param waitSeconds
     *         The max time to wait
     * @throws Exception
     * @since 2.1.0
     */
    private void waitJobUpTo(Job job, int waitSeconds) throws Exception {
        long now = System.currentTimeMillis();
        while ((System.currentTimeMillis() - now) < (waitSeconds * 1000L)) {
            try {
                if (!jobEngineService.isRunning(job.getScopeId(), job.getId())) {
                    return;
                }
            } catch (Exception e) {
                LOG.warn("Error while checking running status for Job {}. Ignoring... Error: {}", job.getName(), e.getMessage());
            }

            TimeUnit.MILLISECONDS.sleep(100);
        }

        Assert.fail("Job " + job.getName() + " did not completed its execution within " + waitSeconds + "s");
    }

    // Check Job Running

    /**
     * Checks that the last {@link Job} in context is running
     *
     * @throws Exception
     * @since 2.1.0
     */
    @And("I confirm job is running")
    public void checkJobInContextIsRunning() throws Exception {
        Job job = (Job) stepData.get(JOB);

        checkJobIsRunning(job, true);
    }

    /**
     * Checks that the given jobs are running
     *
     * @throws Exception
     * @since 2.1.0
     */
    @And("I confirm multiple jobs have these running status")
    public void checkJobsAreRunning(DataTable dataTable) throws Exception {
        Map<String, String> jobNamesToRunningStatus = dataTable.asMap(String.class, String.class);
        checkJobsAreRunning(jobNamesToRunningStatus);
    }

    /**
     * Looks for a {@link Job} by its {@link Job#getName()} and checks that is running
     *
     * @param jobName
     *         The {@link Job#getName()} to look for
     * @throws Exception
     * @since 2.1.0
     */
    @And("I confirm job {string} is running")
    public void checkJobByNameIsRunning(String jobName) throws Exception {
        Job job = findJob(jobName);

        checkJobIsRunning(job, true);
    }

    /**
     * Checks that the last {@link Job} in context is not running
     *
     * @throws Exception
     * @since 2.1.0
     */
    @And("I confirm job is not running")
    public void checkJobInContextIsNotRunning() throws Exception {
        Job job = (Job) stepData.get(JOB);

        checkJobIsRunning(job, false);
    }

    /**
     * Looks for a {@link Job} by its {@link Job#getName()} and checks that is not running
     *
     * @param jobName
     *         The {@link Job#getName()} to look for
     * @throws Exception
     * @since 2.1.0
     */
    @And("I confirm job {string} is not running")
    public void checkJobByNameIsNotRunning(String jobName) throws Exception {
        Job job = findJob(jobName);

        checkJobIsRunning(job, false);
    }

    /**
     * Checks the running status of the given {@link Job}
     *
     * @param job
     *         The {@link Job} to check
     * @param expectedRunning
     *         Whether expecting running or not
     * @throws Exception
     * @since 2.1.0
     */
    private void checkJobIsRunning(Job job, boolean expectedRunning) throws Exception {
        Assert.assertEquals(expectedRunning, jobEngineService.isRunning(job.getScopeId(), job.getId()));
    }

    private void checkJobsAreRunning(Map<String, String> jobNamesToRunningStatus) throws Exception {
        KapuaId currentScopeId = ((Job) stepData.get(JOB)).getScopeId();
        JobQuery query = jobFactory.newQuery(currentScopeId);
        JobListResult allJobs = jobService.query(query); //all jobs

        Map<KapuaId, Boolean> jobIdsToRunningStatus = new HashMap<>();
        for (Job j : allJobs.getItems()) {
            if (jobNamesToRunningStatus.containsKey(j.getName())) { //job actually queried
                jobIdsToRunningStatus.put(j.getId(),
                        Boolean.parseBoolean(jobNamesToRunningStatus.get(j.getName()))); //input expected running status
            }
        }

        Map<KapuaId, Boolean> restultMap = jobEngineService.isRunning(currentScopeId, jobIdsToRunningStatus.keySet());

        Assert.assertEquals(jobIdsToRunningStatus, restultMap);
    }

    @When("I restart a job")
    public void restartJob() throws Exception {
        primeException();
        KapuaId currentJobId = (KapuaId) stepData.get(CURRENT_JOB_ID);
        try {
            JobStartOptions jobStartOptions = jobEngineFactory.newJobStartOptions();
            jobStartOptions.setResetStepIndex(true);
            jobStartOptions.setFromStepIndex(0);
            jobStartOptions.setEnqueue(true);
            jobEngineService.startJob(getCurrentScopeId(), currentJobId, jobStartOptions);
        } catch (KapuaException ke) {
            verifyException(ke);
        }
    }

    @And("I stop the job")
    public void iStopTheJob() throws Exception {
        Job job = (Job) stepData.get(JOB);
        try {
            primeException();
            jobEngineService.stopJob(getCurrentScopeId(), job.getId());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }
}
