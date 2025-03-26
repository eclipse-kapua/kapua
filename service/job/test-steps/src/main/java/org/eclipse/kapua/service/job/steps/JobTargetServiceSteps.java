/*******************************************************************************
 * Copyright (c) 2021, 2024 Eurotech and/or its affiliates and others
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

import com.google.inject.Singleton;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotification;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceAttributes;
import org.eclipse.kapua.service.device.registry.DeviceFactory;
import org.eclipse.kapua.service.device.registry.DeviceListResult;
import org.eclipse.kapua.service.device.registry.DeviceQuery;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.job.targets.JobTarget;
import org.eclipse.kapua.service.job.targets.JobTargetAttributes;
import org.eclipse.kapua.service.job.targets.JobTargetCreator;
import org.eclipse.kapua.service.job.targets.JobTargetFactory;
import org.eclipse.kapua.service.job.targets.JobTargetListResult;
import org.eclipse.kapua.service.job.targets.JobTargetQuery;
import org.eclipse.kapua.service.job.targets.JobTargetService;
import org.eclipse.kapua.service.job.targets.JobTargetStatus;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Singleton
public class JobTargetServiceSteps extends JobServiceTestBase {

    private final static Logger LOG = LoggerFactory.getLogger(JobTargetServiceSteps.class);

    private static final String DEVICE = "Device";

    private DeviceFactory deviceFactory;
    private DeviceRegistryService deviceRegistryService;

    private JobTargetService jobTargetService;
    private JobTargetFactory jobTargetFactory;

    @Inject
    public JobTargetServiceSteps(StepData stepData) {
        super(stepData);
    }

    @Before(value = "@env_docker or @env_docker_base or @env_none", order = 10)
    public void beforeScenarioNone(Scenario scenario) {
        updateScenario(scenario);
    }

    @After(value = "@setup")
    public void setServices() {
        KapuaLocator locator = KapuaLocator.getInstance();

        deviceFactory= locator.getFactory(DeviceFactory.class);
        deviceRegistryService = locator.getService(DeviceRegistryService.class);

        jobTargetService = locator.getService(JobTargetService.class);
        jobTargetFactory = locator.getFactory(JobTargetFactory.class);
    }

    @Given("A regular job target item")
    public void createARegularTarget() throws Exception {
        JobTargetCreator targetCreator = prepareDefaultJobTargetCreator();
        stepData.put(JOB_TARGET_CREATOR, targetCreator);
        primeException();
        try {
            stepData.remove(JOB_TARGET);
            JobTarget target = jobTargetService.create(targetCreator);
            stepData.put(JOB_TARGET, target);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @Given("A new job target item")
    public void createANewTarget() throws Exception {
        JobTargetCreator targetCreator = prepareJobTargetCreator();
        stepData.put(JOB_TARGET_CREATOR, targetCreator);
        primeException();
        try {
            stepData.remove(JOB_TARGET);
            JobTarget target = jobTargetService.create(targetCreator);
            stepData.put(JOB_TARGET, target);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @And("I create a new job target item")
    public void iCreateANewJobTargetItem() throws Exception {
        JobTargetCreator targetCreator = prepareJobTargetCreator();
        stepData.put(JOB_TARGET_CREATOR, targetCreator);
        primeException();
        try {
            stepData.remove(JOB_TARGET);
            JobTarget target = jobTargetService.create(targetCreator);
            stepData.put(JOB_TARGET, target);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }


    @And("I confirm the step index is different than {int} and status is {string}")
    public void iConfirmTheStepIndexIsDifferentThanAndStatusIs(int stepIndex, String status) {
        JobTarget jobTarget = (JobTarget) stepData.get(JOB_TARGET);
        Assert.assertNotEquals(stepIndex, jobTarget.getStepIndex());
        Assert.assertEquals(status, jobTarget.getStatus().toString());
    }


    @And("I search for the job targets in database")
    public void iSearchForTheJobTargetsInDatabase() {
        ArrayList<JobTarget> jobTargets = (ArrayList<JobTarget>) stepData.get(JOB_TARGET_LIST);
        stepData.updateCount(jobTargets.size());
    }

    /**
     * Adds {@link Device}s with the given {@link Device#getClientId()}s as a {@link JobTarget} of the {@link Job} in context.
     *
     * @param clientIds The {@link Device#getClientId()}s to add
     * @throws Exception
     * @since 2.1.0
     */
    @And("I add device target(s) to job")
    public void addDeviceTargetsToJob(List<String> clientIds) throws Exception {
        DeviceQuery deviceQuery = deviceFactory.newQuery(getCurrentScopeId());
        deviceQuery.setPredicate(
                deviceQuery.attributePredicate(DeviceAttributes.CLIENT_ID, clientIds)
        );

        DeviceListResult devices = deviceRegistryService.query(deviceQuery);

        List<KapuaId> deviceIds = devices.getItems()
                .stream()
                .map(KapuaEntity::getId)
                .collect(Collectors.toList());

        Job job = (Job) stepData.get("Job");

        JobTargetCreator jobTargetCreator = jobTargetFactory.newCreator(getCurrentScopeId());
        jobTargetCreator.setJobId(job.getId());

        List<JobTarget> jobTargets = (ArrayList<JobTarget>) stepData.get(JOB_TARGET_LIST) == null ? new ArrayList<>() : (ArrayList<JobTarget>) stepData.get(JOB_TARGET_LIST);
        for (KapuaId deviceId : deviceIds) {
            jobTargetCreator.setJobTargetId(deviceId);
            JobTarget jobTarget = jobTargetService.create(jobTargetCreator);
            stepData.put(JOB_TARGET, jobTarget);
            jobTargets.add(jobTarget);
        }
        stepData.put(JOB_TARGET_LIST, jobTargets);
    }

    @And("I add target(s) to job")
    public void addTargetsToJob() throws Exception {
        JobTargetCreator jobTargetCreator = jobTargetFactory.newCreator(getCurrentScopeId());
        Job job = (Job) stepData.get("Job");
        ArrayList<Device> devices = (ArrayList<Device>) stepData.get("DeviceList");
        ArrayList<JobTarget> jobTargetList = new ArrayList<>();
        try {
            primeException();
            for (Device dev : devices) {
                jobTargetCreator.setJobTargetId(dev.getId());
                jobTargetCreator.setJobId(job.getId());
                JobTarget jobTarget = jobTargetService.create(jobTargetCreator);
                stepData.put(JOB_TARGET, jobTarget);
                jobTargetList.add(jobTarget);
            }
            stepData.put(JOB_TARGET_LIST, jobTargetList);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I search for the last job target in the database")
    public void findLastJobTarget() throws Exception {
        JobTarget target = (JobTarget) stepData.get(JOB_TARGET);
        primeException();
        try {
            stepData.remove(JOB_TARGET);
            JobTarget targetFound = jobTargetService.find(target.getScopeId(), target.getId());
            stepData.put(JOB_TARGET, targetFound);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I confirm the step index is {int} and status is {string}")
    public void checkStepIndexAndStatus(int stepIndex, String status) throws KapuaException {
        JobTarget jobTarget = (JobTarget) stepData.get(JOB_TARGET);
        JobTarget target = jobTargetService.find(jobTarget.getScopeId(), jobTarget.getId());
        LOG.error("step: {}, status: {}", target.getStepIndex(), target.getStatus().name());
        Assert.assertEquals(stepIndex, target.getStepIndex());
        Assert.assertEquals(status, target.getStatus().toString());
    }

    @When("I delete the last job target in the database")
    public void deleteLastJobTarget() throws Exception {
        JobTarget target = (JobTarget) stepData.get(JOB_TARGET);
        primeException();
        try {
            jobTargetService.delete(target.getScopeId(), target.getId());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I update the job target target id")
    public void updateJobTargetTargetId() throws Exception {
        JobTarget target = (JobTarget) stepData.get(JOB_TARGET);
        JobTargetCreator targetCreator = (JobTargetCreator) stepData.get(JOB_TARGET_CREATOR);
        targetCreator.setJobTargetId(getKapuaId());
        stepData.put(JOB_TARGET_CREATOR, targetCreator);
        target.setJobTargetId(targetCreator.getJobTargetId());
        primeException();
        try {
            target = jobTargetService.update(target);
            stepData.put(JOB_TARGET, target);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I update the job target step number to {int}")
    public void setTargetStepIndex(int i) throws Exception {
        JobTarget target = (JobTarget) stepData.get(JOB_TARGET);
        target.setStepIndex(i);
        primeException();
        try {
            target = jobTargetService.update(target);
            stepData.put(JOB_TARGET, target);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I update the job target step status to {string}")
    public void setTargetStepStatus(String stat) throws Exception {
        JobTarget target = (JobTarget) stepData.get(JOB_TARGET);
        target.setStatus(parseJobTargetStatusFromString(stat));
        primeException();
        try {
            target = jobTargetService.update(target);
            stepData.put(JOB_TARGET, target);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I update the job target step exception message to {string}")
    public void setTargetStepExceptionMessage(String text) throws Exception {
        JobTarget target = (JobTarget) stepData.get(JOB_TARGET);
        Exception kex = new Exception(text);
        target.setException(kex);
        primeException();
        try {
            target = jobTargetService.update(target);
            stepData.put(JOB_TARGET, target);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I count the targets in the current scope")
    public void countTargetsForJob() throws Exception {
        updateCount(() -> (int) jobTargetService.count(jobTargetFactory.newQuery(getCurrentScopeId())));
    }

    @When("I query the targets for the current job")
    public void queryTargetsForJob() throws Exception {
        Job job = (Job) stepData.get("Job");
        JobTargetQuery tmpQuery = jobTargetFactory.newQuery(getCurrentScopeId());
        tmpQuery.setPredicate(tmpQuery.attributePredicate(JobTargetAttributes.JOB_ID, job.getId(), AttributePredicate.Operator.EQUAL));
        primeException();
        try {
            stepData.remove(JOB_TARGET_LIST);
            JobTargetListResult targetList = jobTargetService.query(tmpQuery);
            stepData.put(JOB_TARGET_LIST, targetList);
            stepData.updateCount(targetList.getSize());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @And("I confirm job target has step index {int} and status {string} within {int} second(s)")
    public void iConfirmJobTargetHasStatus(int stepIndex, String jobStatus, int timeout) throws Exception {
        try {
            JobTarget jobTarget = (JobTarget) stepData.get(JOB_TARGET);
            long endWaitTime = System.currentTimeMillis() + timeout * 1000;
            JobTarget targetFound = null;
            do {
                targetFound = jobTargetService.find(jobTarget.getScopeId(), jobTarget.getId());
                if (targetFound.getStepIndex() == stepIndex && jobStatus.equals(targetFound.getStatus().name())) {
                    return;
                }
                Thread.sleep(1000);
            }
            while (System.currentTimeMillis() < endWaitTime);
            //lets the test fail for the right reason
            targetFound = jobTargetService.find(jobTarget.getScopeId(), jobTarget.getId());
            Assert.assertEquals(jobStatus, targetFound.getStatus().toString());
            Assert.assertEquals(stepIndex, targetFound.getStepIndex());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @When("I count the targets in the current scope and I count {int}")
    public void iCountTheTargetsInTheCurrentScopeAndICount(int targetNum) throws Exception {
        updateCountAndCheck(() -> (int) jobTargetService.count(jobTargetFactory.newQuery(getCurrentScopeId())), targetNum);
    }

    @Then("The target step index is indeed {int}")
    public void checkTargetStepIndex(int i) {
        JobTarget target = (JobTarget) stepData.get(JOB_TARGET);
        Assert.assertEquals(String.format("The step index should be %d but is in fact %d.", i, target.getStepIndex()), i, target.getStepIndex());
    }

    @Then("The target step exception message is indeed {string}")
    public void checkTargetStepExceptionMessage(String text) {
        JobTarget target = (JobTarget) stepData.get(JOB_TARGET);
        Assert.assertEquals(text, target.getException().getMessage());
    }

    @Then("The target step status is indeed {string}")
    public void checkTargetStepStatus(String stat) {
        JobTarget target = (JobTarget) stepData.get(JOB_TARGET);
        Assert.assertEquals(parseJobTargetStatusFromString(stat), target.getStatus());
    }

    @Then("The job target matches the creator")
    public void checkJobTargetItemAgainstCreator() {
        JobTarget target = (JobTarget) stepData.get(JOB_TARGET);
        JobTargetCreator targetCreator = (JobTargetCreator) stepData.get(JOB_TARGET_CREATOR);
        Assert.assertEquals(targetCreator.getJobId(), target.getJobId());
        Assert.assertEquals(targetCreator.getJobTargetId(), target.getJobTargetId());
        Assert.assertEquals(targetCreator.getScopeId(), target.getScopeId());
    }

    @Then("There is no such job target item in the database")
    public void checkThatNoTargetWasFound() {
        Assert.assertNull("Unexpected job target item found!", stepData.get(JOB_TARGET));
    }


    @When("I test the sanity of the job target factory")
    public void testTheJobTargetFactory() {
        Assert.assertNotNull(jobTargetFactory.newCreator(SYS_SCOPE_ID));
        Assert.assertNotNull(jobTargetFactory.newEntity(SYS_SCOPE_ID));
        Assert.assertNotNull(jobTargetFactory.newListResult());
        Assert.assertNotNull(jobTargetFactory.newQuery(SYS_SCOPE_ID));
    }


    // Check Job Targets

    /**
     * Checks that the {@link JobTarget} in context for the {@link Job} in context has the expected {@link JobTarget#getStepIndex()} and {@link JobTarget#getStatus()}
     *
     * @param expectedStepIndex The expected {@link JobTarget#getStepIndex()}
     * @param expectedJobTargetStatus The expected {@link JobTarget#getStatus()}
     * @throws Exception
     * @since 2.1.0
     */
    @When("I confirm that job target in job has step index {int} and status {string}")
    public void checkJobTargetForJobInContextHas(int expectedStepIndex, String expectedJobTargetStatus) throws Exception {

        JobTarget jobTarget = (JobTarget) stepData.get(JOB_TARGET);

        checkJobTargetForJobHas(jobTarget, expectedStepIndex, expectedJobTargetStatus);
    }

    /**
     * Checks that the {@link JobTarget} that matches the {@link Device} with the given {@link Device#getClientId()} for the {@link Job} in context has the expected {@link JobTarget#getStepIndex()} and {@link JobTarget#getStatus()}
     *
     * @param clientId The {@link Device#getClientId()} to look for
     * @param expectedStepIndex The expected {@link JobTarget#getStepIndex()}
     * @param expectedJobTargetStatus The expected {@link JobTarget#getStatus()}
     * @throws Exception
     * @since 2.1.0
     */
    @And("I confirm that device job target {string} in job has step index {int} and status {string}")
    public void checkJobTargetByNameForJobHas(String clientId, int expectedStepIndex, String expectedJobTargetStatus) throws Exception {
        Device device = deviceRegistryService.findByClientId(getCurrentScopeId(), clientId);

        if (device == null) {
            throw new KapuaEntityNotFoundException(Device.TYPE, clientId);
        }

        Job job = (Job) stepData.get(JOB);

        JobTargetQuery jobTargetQuery = jobTargetFactory.newQuery(job.getScopeId());
        jobTargetQuery.setPredicate(
            jobTargetQuery.andPredicate(
                jobTargetQuery.attributePredicate(JobTargetAttributes.JOB_ID, job.getId()),
                jobTargetQuery.attributePredicate(JobTargetAttributes.JOB_TARGET_ID, device.getId())
            )
        );

        JobTarget jobTarget = jobTargetService.query(jobTargetQuery).getFirstItem();

        if (jobTarget == null) {
            throw new KapuaEntityNotFoundException(JobTarget.TYPE, device.getId());
        }

        checkJobTargetForJobHas(jobTarget, expectedStepIndex, expectedJobTargetStatus);
    }

    /**
     * Checks that given {@link JobTarget} has the expected {@link JobTarget#getStepIndex()} and {@link JobTarget#getStatus()}
     *
     * @param expectedStepIndex The expected {@link JobTarget#getStepIndex()}
     * @param expectedJobTargetStatus The expected {@link JobTarget#getStatus()}
     * @throws Exception
     * @since 2.1.0
     */
    private void checkJobTargetForJobHas(JobTarget jobTarget, int expectedStepIndex, String expectedJobTargetStatus) throws Exception {
        JobTarget updatedJobTarget = jobTargetService.find(jobTarget.getScopeId(), jobTarget.getId());

        Assert.assertEquals(expectedStepIndex, updatedJobTarget.getStepIndex());
        Assert.assertEquals(JobTargetStatus.valueOf(expectedJobTargetStatus), updatedJobTarget.getStatus());

        stepData.put(JOB_TARGET, updatedJobTarget);
    }

    /**
     * Waits the {@link JobTarget} in context to finish its processing and to have {@link JobTarget#getStatus()} set to {@link JobTargetStatus#NOTIFIED_COMPLETION}
     * <p>
     * It also takes as a valid {@link JobTarget#getStatus()} {@link JobTargetStatus#PROCESS_OK} because the {@link ManagementOperationNotification} can be processed fast and
     * {@link JobTarget} can switch from {@link JobTargetStatus#NOTIFIED_COMPLETION} to {@link JobTargetStatus#PROCESS_OK} while waiting for the next check.
     *
     * @param waitSeconds The max time to wait
     * @throws Exception
     * @since 2.1.0
     */
    @Then("I wait job target to finish processing and notify completion up to {int}s")
    public void waitJobTargetInContextToNotifyCompletion(int waitSeconds) throws Exception {
        JobTarget jobTarget = (JobTarget) stepData.get(JOB_TARGET);

        long now = System.currentTimeMillis();
        while ((System.currentTimeMillis() - now) < (waitSeconds * 1000L)) {
            JobTarget updatedJobTarget = jobTargetService.find(jobTarget.getScopeId(), jobTarget.getId());
            if (JobTargetStatus.NOTIFIED_COMPLETION.equals(updatedJobTarget.getStatus()) ||
                    // Processing of notification is fast so we accept also PROCESS_OK as a valid status
                    JobTargetStatus.PROCESS_OK.equals(updatedJobTarget.getStatus())) {
                return;
            }

            TimeUnit.MILLISECONDS.sleep(100);
        }

        Assert.fail("Job Target" + jobTarget.getId() + " did not notified completion of processing within " + waitSeconds + "s");
    }

    //
    // Private methods
    //
    private JobTargetCreator prepareJobTargetCreator() {
        KapuaId currentJobId = (KapuaId) stepData.get(CURRENT_JOB_ID);
        Device device = (Device) stepData.get(DEVICE);
        JobTargetCreator tmpCr = jobTargetFactory.newCreator(getCurrentScopeId());
        tmpCr.setJobId(currentJobId);
        tmpCr.setJobTargetId(device.getId());
        return tmpCr;
    }

    private JobTargetCreator prepareDefaultJobTargetCreator() {
        KapuaId currentJobId = (KapuaId) stepData.get(CURRENT_JOB_ID);
        JobTargetCreator tmpCr = jobTargetFactory.newCreator(getCurrentScopeId());
        tmpCr.setJobId(currentJobId);
        tmpCr.setJobTargetId(getKapuaId());
        return tmpCr;
    }

    private JobTargetStatus parseJobTargetStatusFromString(String stat) {
        switch (stat.toUpperCase().trim()) {
            case "PROCESS_AWAITING":
                return JobTargetStatus.PROCESS_AWAITING;
            case "PROCESS_OK":
                return JobTargetStatus.PROCESS_OK;
            case "PROCESS_FAILED":
            default:
                return JobTargetStatus.PROCESS_FAILED;
        }
    }
}
