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

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.job.JobAttributes;
import org.eclipse.kapua.service.job.JobFactory;
import org.eclipse.kapua.service.job.JobQuery;
import org.eclipse.kapua.service.job.JobService;
import org.eclipse.kapua.service.job.execution.JobExecution;
import org.eclipse.kapua.service.job.execution.JobExecutionAttributes;
import org.eclipse.kapua.service.job.execution.JobExecutionCreator;
import org.eclipse.kapua.service.job.execution.JobExecutionFactory;
import org.eclipse.kapua.service.job.execution.JobExecutionListResult;
import org.eclipse.kapua.service.job.execution.JobExecutionQuery;
import org.eclipse.kapua.service.job.execution.JobExecutionService;
import org.eclipse.kapua.service.job.execution.JobExecutionStatus;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

@Singleton
public class JobExecutionServiceSteps extends JobServiceTestBase {

    private static final Logger LOG = LoggerFactory.getLogger(JobExecutionServiceSteps.class);

    private JobService jobService;
    private JobFactory jobFactory;

    private JobExecutionService jobExecutionService;
    private JobExecutionFactory jobExecutionFactory;

    @Inject
    public JobExecutionServiceSteps(StepData stepData) {
        super(stepData);
    }

    @Before(value = "@env_docker or @env_docker_base or @env_none", order = 10)
    public void beforeScenarioNone(Scenario scenario) {
        updateScenario(scenario);
    }

    @After(value = "@setup")
    public void setServices() {
        KapuaLocator locator = KapuaLocator.getInstance();

        jobService = locator.getService(JobService.class);
        jobFactory = locator.getFactory(JobFactory.class);

        jobExecutionService = locator.getService(JobExecutionService.class);
        jobExecutionFactory = locator.getFactory(JobExecutionFactory.class);
    }

    @Given("A regular job execution item")
    public void createARegularExecution() throws Exception {
        JobExecutionCreator executionCreator = prepareDefaultJobExecutionCreator();
        stepData.put("JobExecutionCreator", executionCreator);
        primeException();
        try {
            stepData.remove(JOB_EXECUTION);
            JobExecution execution = jobExecutionService.create(executionCreator);
            stepData.put(JOB_EXECUTION, execution);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I update the job id for the execution item")
    public void updateJobIdForExecution() throws Exception {
        Job job = (Job) stepData.get("Job");
        JobExecution execution = (JobExecution) stepData.get(JOB_EXECUTION);
        execution.setJobId(job.getId());
        primeException();
        try {
            execution = jobExecutionService.update(execution);
            stepData.put(JOB_EXECUTION, execution);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I update the end time of the execution item as if the job finished now")
    public void updateJobExecutionEndTime() throws Exception {
        JobExecution execution = (JobExecution) stepData.get(JOB_EXECUTION);
        primeException();
        try {
            execution.setEndedOn(DateTime.now().toDate());
            execution = jobExecutionService.update(execution);
            stepData.put(JOB_EXECUTION, execution);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I update the end time of the execution item as if the job never finished")
    public void updateJobExecutionEndTimeWithNull() throws Exception {
        JobExecution execution = (JobExecution) stepData.get(JOB_EXECUTION);
        primeException();
        try {
            execution.setEndedOn(null);
            execution = jobExecutionService.update(execution);
            stepData.put(JOB_EXECUTION, execution);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I search for the last job execution in the database")
    public void findLastJobExecution() throws Exception {
        JobExecution execution = (JobExecution) stepData.get(JOB_EXECUTION);
        primeException();
        try {
            stepData.remove("JobExecutionFound");
            JobExecution foundExecution = jobExecutionService.find(execution.getScopeId(), execution.getId());
            stepData.put("JobExecutionFound", foundExecution);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I delete the last job execution in the database")
    public void deleteLastJobExecution() throws Exception {
        JobExecution execution = (JobExecution) stepData.get(JOB_EXECUTION);
        primeException();
        try {
            jobExecutionService.delete(execution.getScopeId(), execution.getId());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    // Check Job Execution

    /**
     * Checks that the last {@link Job} in context has the given number of {@link JobExecution}s
     *
     * @param expectedNumberOfExecution
     *         Expected number of {@link JobExecution}s
     * @throws Exception
     * @since 2.1.0
     */
    @When("I confirm that job has {int} job execution")
    public void checkJobInContextHasExecution(int expectedNumberOfExecution) throws Exception {
        Job job = (Job) stepData.get(JOB);

        checkJobHasExecution(job, expectedNumberOfExecution);
    }

    /**
     * Looks for a {@link Job} by its {@link Job#getName()} and checks that has the given number of {@link JobExecution}s
     *
     * @param jobName
     *         The {@link Job#getName()} to look for
     * @param expectedNumberOfExecution
     *         Expected number of {@link JobExecution}s
     * @throws Exception
     * @since 2.1.0
     */
    @When("I confirm that job {string} has {int} job execution")
    public void checkJobByNameHasExecution(String jobName, int expectedNumberOfExecution) throws Exception {
        Job job = findJob(jobName);

        checkJobHasExecution(job, expectedNumberOfExecution);
    }

    /**
     * Checks that the given {@link Job} has the given number of {@link JobExecution}s
     *
     * @param job
     *         The {@link Job} to check
     * @param expectedNumberOfExecution
     *         Expected number of {@link JobExecution}s
     * @throws Exception
     * @since 2.1.0
     */
    private void checkJobHasExecution(Job job, int expectedNumberOfExecution) throws Exception {
        long actualNumberOfExecution = jobExecutionService.countByJobId(job.getScopeId(), job.getId());

        Assert.assertEquals(expectedNumberOfExecution, actualNumberOfExecution);
    }

    @When("I count the execution items for the current job")
    public void countExecutionsForJob() throws Exception {
        Job job = (Job) stepData.get("Job");
        JobExecutionQuery tmpQuery = new JobExecutionQuery(getCurrentScopeId());
        tmpQuery.setPredicate(tmpQuery.attributePredicate(JobExecutionAttributes.JOB_ID, job.getId(), AttributePredicate.Operator.EQUAL));
        updateCount(() -> (int) jobExecutionService.count(tmpQuery));
    }

    @Then("I query for the execution items for the current job")
    public void queryExecutionsForJobWithPackages() throws Exception {
        Job job = (Job) stepData.get("Job");
        JobExecutionQuery tmpQuery = new JobExecutionQuery(getCurrentScopeId());
        tmpQuery.setPredicate(tmpQuery.attributePredicate(JobExecutionAttributes.JOB_ID, job.getId(), AttributePredicate.Operator.EQUAL));
        primeException();
        try {
            stepData.remove(JOB_EXECUTION_LIST);
            JobExecutionListResult resultList = jobExecutionService.query(tmpQuery);
            stepData.put(JOB_EXECUTION_LIST, resultList);
            stepData.updateCount(resultList.getSize());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @Then("I query for the execution items for the current job starting from date in the future")
    public void queryExecutionsForJobWithStartDateInFuture() throws Exception {
        final Date startDate = createDateInFuture();
        queryExecutionsForJobWithStartDate(startDate);
    }

    @Then("I query for the execution items for the current job starting from date in the past")
    public void queryExecutionsForJobWithStartDateInPast() throws Exception {
        final Date startDate = createDateInPast();
        queryExecutionsForJobWithStartDate(startDate);
    }

    private void queryExecutionsForJobWithStartDate(Date startDate) throws Exception {
        final Job job = (Job) stepData.get("Job");
        final JobExecutionQuery tmpQuery = new JobExecutionQuery(getCurrentScopeId());
        final AndPredicate andPredicate = tmpQuery.andPredicate(tmpQuery.attributePredicate(JobExecutionAttributes.JOB_ID, job.getId()));
        andPredicate.and(tmpQuery.attributePredicate(JobExecutionAttributes.STARTED_ON, startDate, AttributePredicate.Operator.GREATER_THAN_OR_EQUAL));
        tmpQuery.setPredicate(andPredicate);
        primeException();
        try {
            stepData.remove(JOB_EXECUTION_LIST);
            JobExecutionListResult resultList = jobExecutionService.query(tmpQuery);
            stepData.put(JOB_EXECUTION_LIST, resultList);
            stepData.updateCount(resultList.getSize());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    private Date createDateInFuture() {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        return calendar.getTime();
    }

    private Date createDateInPast() {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        return calendar.getTime();
    }

    @Then("I query for the execution items for the current job and I count {int} finished within {int} second(s)")
    public void queryExecutionsForJob(int numberOfExecutions, int timeout) throws Exception {
        iQueryForTheExecutionItemsForTheCurrentJobAndICountOrMoreInternal(numberOfExecutions, timeout, false);
    }

    @And("I query for the execution items for the current job and I count {int} or more finished within {int} second(s)")
    public void iQueryForTheExecutionItemsForTheCurrentJobAndICountOrMore(int numberOfExecutions, int timeout) throws Exception {
        iQueryForTheExecutionItemsForTheCurrentJobAndICountOrMoreInternal(numberOfExecutions, timeout, true);
    }

    private void iQueryForTheExecutionItemsForTheCurrentJobAndICountOrMoreInternal(int numberOfExecutions, int timeout, boolean greater) throws Exception {
        long endWaitTime = System.currentTimeMillis() + timeout * 1000;
        JobExecutionListResult resultList = null;
        do {
            primeException();
            try {
                stepData.remove(JOB_EXECUTION_LIST);
                resultList = getExecutionsForJob((Job) stepData.get("Job"));
                stepData.put(JOB_EXECUTION_LIST, resultList);
                stepData.updateCount(resultList.getSize());
                if (greater) {
                    if (resultList.getSize() >= numberOfExecutions) {
                        return;
                    }
                } else {
                    if (numberOfExecutions == resultList.getSize()) {
                        return;
                    }
                }
            } catch (KapuaException ex) {
                verifyException(ex);
            }
            Thread.sleep(1000);
        }
        while (System.currentTimeMillis() < endWaitTime);
        if (greater) {
            Assert.assertTrue(resultList.getSize() >= numberOfExecutions);
        } else {
            Assert.assertEquals(numberOfExecutions, resultList.getSize());
        }
    }

    private JobExecutionListResult getExecutionsForJob(Job job) throws Exception {
        JobExecutionQuery tmpQuery = new JobExecutionQuery(getCurrentScopeId());
        tmpQuery.setPredicate(tmpQuery.attributePredicate(JobExecutionAttributes.JOB_ID, job.getId(), AttributePredicate.Operator.EQUAL));
        return jobExecutionService.query(tmpQuery);
    }

    @When("I confirm the executed job is finished")
    public void confirmJobIsFinished() {
        JobExecutionListResult resultList = (JobExecutionListResult) stepData.get(JOB_EXECUTION_LIST);
        JobExecution jobExecution = resultList.getFirstItem();
        Assert.assertNotNull("Job execution end date cannot be null!", jobExecution.getEndedOn());
        Assert.assertNotNull("Job execution log cannot be null!", jobExecution.getLog());
    }

    @When("I query for the job with the name {string} and I count {int} execution item(s) or more and I confirm the executed job(s) is/are finished within {int} second(s)")
    public void iQueryForTheJobWithTheNameAndJobExecutionCountGreaterAndExecutionStatus(String jobName, int count, int timeout) throws Exception {
        iQueryForTheJobWithTheNameAndJobExecutionCountInternal(jobName, count, timeout, true);
    }

    @When("I query for the job with the name {string} and I count {int} execution item(s) and I confirm the executed job(s) is/are finished within {int} second(s)")
    public void iQueryForTheJobWithTheNameAndJobExecutionCountAndExecutionStatus(String jobName, int count, int timeout) throws Exception {
        iQueryForTheJobWithTheNameAndJobExecutionCountInternal(jobName, count, timeout, false);
    }

    @When("I query for the job with the name {string} and I count {int} execution item(s) after {int} second(s)")
    public void iQueryForTheJobWithTheNameAndJobExecutionCountAfter(String jobName, int count, int secondsToWait) throws Exception {
        Thread.sleep(secondsToWait * 1000);
        primeException();
        try {
            //update job in stepdata to allow job execution retrieval
            getJobAndUpdateStepData(jobName);
            JobExecutionListResult jobExecutionList = getJobExecutionListAndUpdateStepData();
            Assert.assertEquals("Wrong job execution count", 0, jobExecutionList.getSize());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    private void iQueryForTheJobWithTheNameAndJobExecutionCountInternal(String jobName, int count, int timeout, boolean greaterThan) throws Exception {
        primeException();
        try {
            Job job = null;
            JobExecutionListResult jobExecutionList = null;
            JobExecution jobExecution = null;
            int execution = 0;
            while (execution++ < timeout) {
                //step 1
                job = getJobAndUpdateStepData(jobName);
                //step 2
                jobExecutionList = getJobExecutionListAndUpdateStepData();
                jobExecution = jobExecutionList.getFirstItem();
                if (isJobExecutionCompled(jobExecutionList, jobExecution, job, execution, jobName, count, greaterThan)) {
                    //all fine return
                    LOG.info("Job executions are fine after {} seconds", execution - 1);
                    return;
                }
                Thread.sleep(1000);
            }
            logJobInfo(jobExecutionList, job, execution);
            jobInfoAssertCheck(jobExecutionList, jobExecution, job, jobName, count, greaterThan);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    private Job getJobAndUpdateStepData(String jobName) throws Exception {
        JobQuery tmpQuery = new JobQuery(getCurrentScopeId());
        tmpQuery.setPredicate(tmpQuery.attributePredicate(JobAttributes.NAME, jobName));

        stepData.remove("Job");
        Job job = jobService.query(tmpQuery).getFirstItem();
        stepData.put("Job", job);
        return job;
    }

    private JobExecutionListResult getJobExecutionListAndUpdateStepData() throws Exception {
        stepData.remove(JOB_EXECUTION_LIST);
        JobExecutionListResult jobExecutionList = getExecutionsForJob((Job) stepData.get("Job"));
        stepData.put(JOB_EXECUTION_LIST, jobExecutionList);
        stepData.updateCount(jobExecutionList.getSize());
        return jobExecutionList;
    }

    private boolean isJobExecutionCompled(JobExecutionListResult jobExecutionList, JobExecution jobExecution, Job job, int execution, String jobName, int count, boolean greaterThan) {
        boolean result = jobName.equals(job.getName()) &&
                jobExecution != null &&
                jobExecution.getEndedOn() != null &&
                jobExecution.getLog() != null;
        return result && (greaterThan ? jobExecutionList.getSize() >= count : jobExecutionList.getSize() == count);
    }

    private void jobInfoAssertCheck(JobExecutionListResult jobExecutionList, JobExecution jobExecution, Job job, String jobName, int count, boolean greaterThan) {
        Assert.assertEquals(jobName, job.getName());
        if (!greaterThan) {
            Assert.assertEquals("Wrong Job execution expected count" + jobExecutionList.getSize(), count, jobExecutionList.getSize());
        } else {
            Assert.assertTrue("Job execution expected count is greater than " + count + " - found " + jobExecutionList.getSize(), count <= jobExecutionList.getSize());
        }
        Assert.assertNotNull("Job execution cannot be null!", jobExecution);
        Assert.assertNotNull("Job execution end date cannot be null!", jobExecution.getEndedOn());
        Assert.assertNotNull("Job execution log cannot be null!", jobExecution.getLog());
    }

    private void logJobInfo(JobExecutionListResult jobExecutionList, Job job, int execution) {
        LOG.info("Job executions after {} seconds", execution);
        LOG.info("job id: {} - name: {} - description: {}", job.getId(), job.getName(), job.getDescription());
        if (jobExecutionList.getItems() != null) {
            if (jobExecutionList.getSize() > 0) {
                LOG.info("Job execution empty list");
            } else {
                LOG.info("Job execution list size {}", jobExecutionList.getSize());
                jobExecutionList.getItems().forEach(jobExecutionTmp -> {
                    LOG.info("    s: {} - e: {} - log: {}", jobExecutionTmp.getStartedOn(), jobExecutionTmp.getEndedOn(), jobExecutionTmp.getLog());
                });
            }
        }
    }

    @Then("The job execution matches the creator")
    public void checkJobExecutionItemAgainstCreator() {
        JobExecutionCreator executionCreator = (JobExecutionCreator) stepData.get("JobExecutionCreator");
        JobExecution execution = (JobExecution) stepData.get(JOB_EXECUTION);
        Assert.assertEquals(executionCreator.getScopeId(), execution.getScopeId());
        Assert.assertEquals(executionCreator.getJobId(), execution.getJobId());
        Assert.assertEquals(executionCreator.getStartedOn(), execution.getStartedOn());
    }

    @Then("The job execution items match")
    public void checkJobExecutionItemsRunning() {
        JobExecution execution = (JobExecution) stepData.get(JOB_EXECUTION);
        JobExecution foundExecution = (JobExecution) stepData.get("JobExecutionFound");
        Assert.assertEquals(execution.getScopeId(), foundExecution.getScopeId());
        Assert.assertEquals(execution.getJobId(), foundExecution.getJobId());
        Assert.assertEquals(execution.getStartedOn(), foundExecution.getStartedOn());
        Assert.assertEquals(execution.getEndedOn(), foundExecution.getEndedOn());
    }

    @Then("The job execution status is {string}")
    public void checkJobExecutionItems(String status) {
        JobExecution foundExecution = (JobExecution) stepData.get("JobExecutionFound");
        Assert.assertEquals(foundExecution.getStatus(), JobExecutionStatus.valueOf(status));
    }

    @Then("There is no such job execution item in the database")
    public void checkThatNoExecutionWasFound() {
        Assert.assertNull("Unexpected job execution item found!", stepData.get("JobExecutionFound"));
    }

    @When("I test the sanity of the job execution factory")
    public void testTheJobExecutionFactory() {
        Assert.assertNotNull(jobExecutionFactory.newCreator(SYS_SCOPE_ID));
        Assert.assertNotNull(jobExecutionFactory.newEntity(SYS_SCOPE_ID));
    }
    // Private methods

    private JobExecutionCreator prepareDefaultJobExecutionCreator() {
        KapuaId currentJobId = (KapuaId) stepData.get(CURRENT_JOB_ID);
        JobExecutionCreator tmpCr = jobExecutionFactory.newCreator(getCurrentScopeId());
        tmpCr.setJobId(currentJobId);
        tmpCr.setStartedOn(DateTime.now().toDate());
        return tmpCr;
    }
}
