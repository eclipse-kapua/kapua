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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.kapua.qa.common.cucumber.CucJobStepProperty;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.job.step.JobStep;
import org.eclipse.kapua.service.job.step.JobStepAttributes;
import org.eclipse.kapua.service.job.step.JobStepCreator;
import org.eclipse.kapua.service.job.step.JobStepFactory;
import org.eclipse.kapua.service.job.step.JobStepListResult;
import org.eclipse.kapua.service.job.step.JobStepService;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinition;
import org.eclipse.kapua.service.job.step.definition.JobStepProperty;
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
public class JobStepServiceSteps extends JobServiceTestBase {

    private static final Logger logger = LoggerFactory.getLogger(JobStepServiceSteps.class);

    // Job Step definition service objects
    private JobStepService jobStepService;
    private JobStepFactory jobStepFactory;

    @Inject
    public JobStepServiceSteps(StepData stepData) {
        super(stepData);
    }

    @Before(value = "@env_docker or @env_docker_base or @env_none", order = 10)
    public void beforeScenarioNone(Scenario scenario) {
        updateScenario(scenario);
    }

    @After(value = "@setup")
    public void setServices() {
        KapuaLocator locator = KapuaLocator.getInstance();

        jobStepService = locator.getService(JobStepService.class);
        jobStepFactory = locator.getFactory(JobStepFactory.class);
    }
    // Prepare

    @Given("I prepare a JobStepCreator with the name {string}")
    public void iPrepareAJobStepCreator(String name) {
        iPrepareAJobStepCreatorWithPropertyList(name, Collections.emptyList());
    }

    @Given("I prepare a JobStepCreator with the name {string} and the following properties")
    public void iPrepareAJobStepCreatorWithPropertyList(String name, List<CucJobStepProperty> cucJobStepProperties) {
        KapuaId currentStepDefinitionId = (KapuaId) stepData.get(CURRENT_JOB_STEP_DEFINITION_ID);

        List<JobStepProperty> tmpPropLst = new ArrayList<>();
        for (CucJobStepProperty cucJobStepProperty : cucJobStepProperties) {
            tmpPropLst.add(
                    jobStepFactory.newStepProperty(
                            cucJobStepProperty.getName(),
                            cucJobStepProperty.getType(),
                            cucJobStepProperty.getValue()
                    )
            );
        }

        JobStepCreator stepCreator = prepareDefaultJobStepCreator();
        stepCreator.setName(name);
        stepCreator.setJobStepDefinitionId(currentStepDefinitionId);
        stepCreator.setJobStepProperties(tmpPropLst);

        stepData.put(JOB_STEP_CREATOR, stepCreator);
    }

    /**
     * Adds a {@link JobStep} to the {@link Job} in context with the {@link JobStepDefinition} in context with the given {@link JobStepProperty}es
     *
     * @param jobStepName
     *         The {@link JobStepCreator#getName()}
     * @param cucJobStepProperties
     *         The {@link JobStepCreator#getStepProperties()}
     * @throws Exception
     * @since 2.1.0
     */
    @And("I add job step to job with name {string} and with selected job step definition and properties")
    public void iAddJobStepToJobWithSelectedJobStepDefinitionAndFollowingJobStepProperties(String jobStepName, List<CucJobStepProperty> cucJobStepProperties) throws Exception {
        Job job = (Job) stepData.get(JOB);
        JobStepDefinition jobStepDefinition = (JobStepDefinition) stepData.get(JOB_STEP_DEFINITION);

        JobStepCreator jobStepCreator = jobStepFactory.newCreator(job.getScopeId());
        jobStepCreator.setJobId(job.getId());
        jobStepCreator.setName(jobStepName);
        jobStepCreator.setJobStepDefinitionId(jobStepDefinition.getId());
        jobStepCreator.setStepProperties(
                cucJobStepProperties.stream()
                        .map(
                                (cucJobStepProperty) -> jobStepFactory.newStepProperty(
                                        cucJobStepProperty.getName(),
                                        cucJobStepProperty.getType(),
                                        cucJobStepProperty.getValue()
                                )
                        )
                        .collect(Collectors.toList())
        );

        JobStep jobStep = jobStepService.create(jobStepCreator);
        stepData.put(JOB_STEP, jobStep);
    }

    @And("I prepare a JobStepCreator with the name {string} and properties")
    // TODO Review implementation. These should be tested specifically for each JobStepDefinition
    public void iPrepareAJobStepCreatorWithPropertyList1(String name, List<CucJobStepProperty> tmpProperty) {
        JobStepCreator stepCreator;
        ArrayList<JobStepDefinition> jobStepDefinitions = (ArrayList<JobStepDefinition>) stepData.get(JOB_STEP_DEFINITIONS);
        ArrayList<JobStepCreator> stepCreators = new ArrayList<>();
        String firstValue = tmpProperty.get(0).getValue();
        String[] values = firstValue.split(",");

        ArrayList<String> bundleIds = new ArrayList<>(Arrays.asList(values));
        for (String bundleId : bundleIds) {
            for (JobStepDefinition stepDefinition : jobStepDefinitions) {
                stepCreator = prepareDefaultJobStepCreator();
                stepCreator.setName(name + stepCreators.size());
                stepCreator.setJobStepDefinitionId(stepDefinition.getId());
                List<JobStepProperty> tmpPropLst = new ArrayList<>();
                for (CucJobStepProperty prop : tmpProperty) {
                    if ((stepDefinition.getName().equals("Bundle Start") && prop.getName().equals("bundleId"))) {
                        tmpPropLst.add(jobStepFactory.newStepProperty(prop.getName(), prop.getType(), bundleId));
                    } else if (stepDefinition.getName().equals("Bundle Stop") && prop.getName().equals("bundleId")) {
                        tmpPropLst.add(jobStepFactory.newStepProperty(prop.getName(), prop.getType(), bundleId));
                    } else if (stepDefinition.getName().equals("Command Execution") && prop.getName().equals("commandInput")) {
                        tmpPropLst.add(jobStepFactory.newStepProperty(prop.getName(), prop.getType(), prop.getValue()));
                    } else if (stepDefinition.getName().equals("Configuration Put") && prop.getName().equals("configuration")) {
                        tmpPropLst.add(jobStepFactory.newStepProperty(prop.getName(), prop.getType(), prop.getValue()));
                    } else if (stepDefinition.getName().equals("Asset Write") && prop.getName().equals("assets")) {
                        tmpPropLst.add(jobStepFactory.newStepProperty(prop.getName(), prop.getType(), prop.getValue()));
                    } else if (stepDefinition.getName().equals("Package Download / Install") && prop.getName().equals("packageDownloadRequest")) {
                        tmpPropLst.add(jobStepFactory.newStepProperty(prop.getName(), prop.getType(), prop.getValue()));
                    } else if (stepDefinition.getName().equals("Package Uninstall") && prop.getName().equals("packageUninstallRequest")) {
                        tmpPropLst.add(jobStepFactory.newStepProperty(prop.getName(), prop.getType(), prop.getValue()));
                    } else if (prop.getName().equals("timeout")) {
                        tmpPropLst.add(jobStepFactory.newStepProperty(prop.getName(), prop.getType(), prop.getValue()));
                    }
                }
                stepCreator.setJobStepProperties(tmpPropLst);
                stepCreators.add(stepCreator);
                stepData.put(JOB_STEP_CREATOR, stepCreator);
                stepData.put(JOB_STEP_CREATORS, stepCreators);
            }
        }
    }
    // Create

    @When("I create a new JobStep from the existing creator")
    public void iCreateAJobStepFromTheCreator() throws Exception {
        JobStepCreator stepCreator = (JobStepCreator) stepData.get(JOB_STEP_CREATOR);

        KapuaId currentJobId = (KapuaId) stepData.get(CURRENT_JOB_ID);
        stepCreator.setJobId(currentJobId);
        primeException();
        try {
            stepData.remove(JOB_STEP);
            stepData.remove(CURRENT_JOB_STEP_ID);
            JobStep step = jobStepService.create(stepCreator);
            stepData.put(JOB_STEP, step);
            stepData.put(CURRENT_JOB_STEP_ID, step.getId());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @And("I create multiple new JobSteps from the existing creators")
    public void iCreateANewStepEntitiesFromTheExistingCreator() throws Exception {

        KapuaId currentJobId = (KapuaId) stepData.get(CURRENT_JOB_ID);

        ArrayList<JobStep> jobSteps = new ArrayList<>();
        ArrayList<JobStepCreator> jobStepCreators = (ArrayList<JobStepCreator>) stepData.get(JOB_STEP_CREATORS);
        for (JobStepCreator jobStepCreator : jobStepCreators) {
            jobStepCreator.setJobId(currentJobId);
            primeException();
            try {
                stepData.remove(JOB_STEP);
                stepData.put(JOB_STEPS, Collections.emptyList());
                stepData.remove(CURRENT_JOB_STEP_ID);

                JobStep step = jobStepService.create(jobStepCreator);
                jobSteps.add(step);
                stepData.put(JOB_STEP, step);
                stepData.put(JOB_STEPS, jobSteps);
                stepData.put(CURRENT_JOB_STEP_ID, step.getId());
            } catch (KapuaException ex) {
                verifyException(ex);
            }
        }
    }

    // Update
    @When("I update the JobStep.name to {string}")
    public void iUpdateJobStepName(String name) throws Exception {
        JobStep jobStep = (JobStep) stepData.get(JOB_STEP);
        jobStep.setName(name);

        updateJobStep(jobStep);
    }

    @When("I update the JobStep.stepIndex to {int}")
    public void iUpdateStepIndex(Integer stepIndex) throws Exception {
        JobStep jobStep = (JobStep) stepData.get(JOB_STEP);
        jobStep.setStepIndex(stepIndex);

        updateJobStep(jobStep);
    }

    @When("I update the JobStep.jobStepDefinitionId to the current JobStepDefinition.id")
    public void iUpdateStepDefinition() throws Exception {
        KapuaId currentStepDefId = (KapuaId) stepData.get(CURRENT_JOB_STEP_DEFINITION_ID);

        JobStep jobStep = (JobStep) stepData.get(JOB_STEP);
        jobStep.setJobStepDefinitionId(currentStepDefId);

        updateJobStep(jobStep);
    }
    // Search

    @When("I look for the last JobStep")
    public void iLookForLastJobStep() throws Exception {
        JobStep step = (JobStep) stepData.get(JOB_STEP);
        primeException();
        try {
            JobStep jobStep = jobStepService.find(step.getScopeId(), step.getId());
            stepData.put(JOB_STEP, jobStep);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I look for the JobStep with name {string}")
    public void iLookForJobStepWithName(String name) throws Exception {
        KapuaQuery tmpQuery = new KapuaQuery(getCurrentScopeId());
        tmpQuery.setPredicate(tmpQuery.attributePredicate(JobStepAttributes.NAME, name));

        primeException();
        try {
            JobStepListResult jobSteps = jobStepService.query(tmpQuery);
            stepData.put(JOB_STEP, jobSteps.getFirstItem());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }
    // Query

    @When("I query for a JobStep with the name {string}")
    public void iQueryForJobStepWithName(String name) throws Exception {
        KapuaQuery tmpQuery = new KapuaQuery(getCurrentScopeId());
        tmpQuery.setPredicate(tmpQuery.attributePredicate(JobStepAttributes.NAME, name));

        primeException();
        try {
            stepData.remove(JOB_STEPS);
            JobStepListResult jobSteps = jobStepService.query(tmpQuery);
            stepData.put(JOB_STEPS, jobSteps);
            stepData.updateCount(jobSteps.getSize());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }
    // Count

    @When("I count the JobSteps and I find {int} JobStep within {int} second(s)")
    public void iCountJobStepAndCheck(int count, int timeout) throws Exception {
        KapuaId currentJobId = (KapuaId) stepData.get(CURRENT_JOB_ID);
        primeException();
        long endWaitTime = System.currentTimeMillis() + timeout * 1000;
        try {
            do {
                if (count == getJobStepListResult(currentJobId).getSize()) {
                    return;
                }
                Thread.sleep(1000);
            }
            while (System.currentTimeMillis() < endWaitTime);
            logger.info("============================    Wait 30 seconds again!!!! job id: {}", currentJobId);
            Thread.sleep(30000);
            JobStepListResult list = getJobStepListResult(currentJobId);
            list.getItems().forEach(jobStep -> logger.info("{} - {}", jobStep.getJobId(), jobStep.getJobStepDefinitionId()));
            Assert.assertEquals("Wrong job step size!", count, list.getSize());
        } catch (KapuaException ke) {
            verifyException(ke);
        }
    }

    private JobStepListResult getJobStepListResult(KapuaId currentJobId) throws KapuaException {
        KapuaQuery jobStepQuery = new KapuaQuery(getCurrentScopeId());
        jobStepQuery.setPredicate(jobStepQuery.attributePredicate(JobStepAttributes.JOB_ID, currentJobId, AttributePredicate.Operator.EQUAL));

        return jobStepService.query(jobStepQuery);
    }

    @When("I count the JobSteps in the current scope")
    public void iCountJobStepInCurrentScope() throws Exception {
        updateCount(() -> (int) jobStepService.count(new KapuaQuery(getCurrentScopeId())));
    }
    // Delete

    @When("I delete the last JobStep")
    public void iDeleteLastJobStep() throws Exception {
        JobStep jobStep = (JobStep) stepData.get(JOB_STEP);

        primeException();
        try {
            jobStepService.delete(jobStep.getScopeId(), jobStep.getId());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }
    // Check

    @Then("The JobStep matches the creator")
    public void checkJobStepMatchesCreator() {
        JobStep step = (JobStep) stepData.get(JOB_STEP);
        JobStepCreator stepCreator = (JobStepCreator) stepData.get(JOB_STEP_CREATOR);

        Assert.assertEquals(stepCreator.getJobId(), step.getJobId());
        Assert.assertEquals(stepCreator.getJobStepDefinitionId(), step.getJobStepDefinitionId());
        Assert.assertEquals(stepCreator.getName(), step.getName());
        Assert.assertEquals(stepCreator.getDescription(), step.getDescription());
        Assert.assertEquals(stepCreator.getStepIndex(), (Integer) step.getStepIndex());
        Assert.assertEquals(stepCreator.getStepProperties().size(), step.getStepProperties().size());
    }

    @Then("The JobStep.stepIndex is {int}")
    public void checkJobStepMatchesCreator(int stepIndex) {
        JobStep jobStep = (JobStep) stepData.get(JOB_STEP);

        Assert.assertEquals(stepIndex, jobStep.getStepIndex());
    }

    @Then("The JobStep is not found")
    public void checkJobStepNotFound() {
        Assert.assertNull(stepData.get(JOB_STEP));
    }
    // Others

    @When("I test the JobStepFactory")
    public void testTheStepFactory() {
        Assert.assertNotNull(jobStepFactory.newCreator(SYS_SCOPE_ID));
        Assert.assertNotNull(jobStepFactory.newEntity(SYS_SCOPE_ID));
        Assert.assertNotNull(new KapuaQuery(SYS_SCOPE_ID));
        Assert.assertNotNull(jobStepFactory.newStepProperty("TestName", "TestType", "TestValue"));
    }
    // Private methods

    private JobStepCreator prepareDefaultJobStepCreator() {
        JobStepCreator tmpCr = jobStepFactory.newCreator(getCurrentScopeId());
        tmpCr.setName(String.format("StepName_%d", random.nextInt()));
        tmpCr.setDescription("StepDescription");
        return tmpCr;
    }

    private void updateJobStep(JobStep jobStep) throws Exception {
        primeException();
        try {
            jobStep = jobStepService.update(jobStep);
            stepData.put(JOB_STEP, jobStep);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }
}
