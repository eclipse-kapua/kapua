/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
import java.util.List;

import javax.inject.Inject;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.kapua.qa.common.cucumber.CucJobStepProperty;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinition;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionCreator;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionFactory;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionQuery;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionService;
import org.eclipse.kapua.service.job.step.definition.JobStepProperty;
import org.eclipse.kapua.service.job.step.definition.JobStepType;
import org.eclipse.kapua.service.job.steps.model.TestJobStepProcessor;
import org.junit.Assert;

import com.google.inject.Singleton;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

@Singleton
public class JobStepDefinitionServiceSteps extends JobServiceTestBase {

    // Job Step definition service objects
    private JobStepDefinitionService jobStepDefinitionService;
    private JobStepDefinitionFactory jobStepDefinitionFactory;

    @Inject
    public JobStepDefinitionServiceSteps(StepData stepData) {
        super(stepData);
    }

    @Before(value = "@env_docker or @env_docker_base or @env_none", order = 10)
    public void beforeScenarioNone(Scenario scenario) {
        updateScenario(scenario);
    }

    @After(value = "@setup")
    public void setServices() {
        KapuaLocator locator = KapuaLocator.getInstance();

        jobStepDefinitionService = locator.getService(JobStepDefinitionService.class);
        jobStepDefinitionFactory = locator.getFactory(JobStepDefinitionFactory.class);
    }

    @Given("A regular step definition creator with the name {string}")
    public void prepareARegularStepDefinitionCreator(String name) {
        JobStepDefinitionCreator stepDefinitionCreator = prepareDefaultJobStepDefinitionCreator();
        stepDefinitionCreator.setName(name);
        stepData.put(JOB_STEP_DEFINITION_CREATOR, stepDefinitionCreator);
    }

    @Given("A regular definition creator with the name {string} and {int} properties")
    public void prepareARegularStepDefinitionCreatorWithProperties(String name, Integer cnt) {
        JobStepDefinitionCreator stepDefinitionCreator = prepareDefaultJobStepDefinitionCreator();
        stepDefinitionCreator.setName(name);
        List<JobStepProperty> tmpPropLst = new ArrayList<>();
        tmpPropLst.add(jobStepDefinitionFactory.newStepProperty("Property1", "Type1", null));
        tmpPropLst.add(jobStepDefinitionFactory.newStepProperty("Property2", "Type2", null));
        tmpPropLst.add(jobStepDefinitionFactory.newStepProperty("Property3", "Type3", null));
        stepDefinitionCreator.setStepProperties(tmpPropLst);
        stepData.put(JOB_STEP_DEFINITION_CREATOR, stepDefinitionCreator);
    }

    @Given("A regular step definition creator with the name {string} and the following properties")
    public void iPrepareARegularStepDefinitionCreatorWithPropertyList(String name, List<CucJobStepProperty> list) {
        JobStepDefinitionCreator stepDefinitionCreator = prepareDefaultJobStepDefinitionCreator();
        stepDefinitionCreator.setName(name);
        List<JobStepProperty> tmpPropLst = new ArrayList<>();
        for (CucJobStepProperty prop : list) {
            tmpPropLst.add(jobStepDefinitionFactory.newStepProperty(prop.getName(), prop.getType(), null));
        }
        stepDefinitionCreator.setStepProperties(tmpPropLst);
        stepData.put(JOB_STEP_DEFINITION_CREATOR, stepDefinitionCreator);
    }

    @Given("I create a JobStepDefinition with the name {string} and the following properties")
    public void iCreateAJobStepDefinitionWithProperties(String name, List<CucJobStepProperty> list) throws Exception {
        iPrepareARegularStepDefinitionCreatorWithPropertyList(name, list);

        primeException();
        try {
            stepData.remove(JOB_STEP_DEFINITION);
            stepData.remove(CURRENT_JOB_STEP_DEFINITION_ID);

            JobStepDefinitionCreator stepDefinitionCreator = (JobStepDefinitionCreator) stepData.get(JOB_STEP_DEFINITION_CREATOR);
            JobStepDefinition stepDefinition = jobStepDefinitionService.create(stepDefinitionCreator);

            stepData.put(JOB_STEP_DEFINITION, stepDefinition);
            stepData.put(CURRENT_JOB_STEP_DEFINITION_ID, stepDefinition.getId());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @Given("Search for step definition with the name {string}")
    public void searchARegularStepDefinitionWithProperties(String name) throws Exception {
        primeException();
        try {
            stepData.remove(JOB_STEP_DEFINITION);
            stepData.remove(CURRENT_JOB_STEP_DEFINITION_ID);
            JobStepDefinition stepDefinition = jobStepDefinitionService.findByName(name);
            stepData.put(JOB_STEP_DEFINITION, stepDefinition);
            stepData.put(CURRENT_JOB_STEP_DEFINITION_ID, stepDefinition.getId());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @Given("I set the step definition creator name to null")
    public void setDefinitionCreatorNameToNull() {
        JobStepDefinitionCreator stepDefinitionCreator = (JobStepDefinitionCreator) stepData.get(JOB_STEP_DEFINITION_CREATOR);
        stepDefinitionCreator.setName(null);
        stepData.put(JOB_STEP_DEFINITION_CREATOR, stepDefinitionCreator);
    }

    @Given("I set the step definition creator processor name to {string}")
    public void setDefinitionCreatorProcessorNameTo(String name) {
        JobStepDefinitionCreator stepDefinitionCreator = (JobStepDefinitionCreator) stepData.get(JOB_STEP_DEFINITION_CREATOR);
        stepDefinitionCreator.setProcessorName(name);
        stepData.put(JOB_STEP_DEFINITION_CREATOR, stepDefinitionCreator);
    }

    @Given("I set the step definition creator processor name to null")
    public void setDefinitionCreatorProcessorNameToNull() {
        JobStepDefinitionCreator stepDefinitionCreator = (JobStepDefinitionCreator) stepData.get(JOB_STEP_DEFINITION_CREATOR);
        stepDefinitionCreator.setProcessorName(null);
        stepData.put(JOB_STEP_DEFINITION_CREATOR, stepDefinitionCreator);
    }

    @Given("I create {int} step definition items")
    public void createANumberOfStepDefinitions(Integer num) throws Exception {
        JobStepDefinitionCreator tmpCreator;
        primeException();
        try {
            for (int i = 0; i < num; i++) {
                tmpCreator = new JobStepDefinitionCreator(getCurrentScopeId());
                tmpCreator.setName(String.format("TestStepDefinitionNum%d", random.nextLong()));
                tmpCreator.setProcessorName("TestStepProcessor");
                tmpCreator.setStepType(JobStepType.TARGET);
                jobStepDefinitionService.create(tmpCreator);
            }
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I create a new step definition entity from the existing creator")
    public void createAStepDefinitionFromTheCreator() throws Exception {
        JobStepDefinitionCreator stepDefinitionCreator = (JobStepDefinitionCreator) stepData.get(JOB_STEP_DEFINITION_CREATOR);
        primeException();
        try {
            stepData.remove(JOB_STEP_DEFINITION);
            stepData.remove(CURRENT_JOB_STEP_DEFINITION_ID);
            JobStepDefinition stepDefinition = jobStepDefinitionService.create(stepDefinitionCreator);
            stepData.put(JOB_STEP_DEFINITION, stepDefinition);
            stepData.put(CURRENT_JOB_STEP_DEFINITION_ID, stepDefinition.getId());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I search for the step definition in the database")
    public void findTheExistingStepDefinitionInTheDatabase() throws Exception {
        KapuaId currentStepDefId = (KapuaId) stepData.get(CURRENT_JOB_STEP_DEFINITION_ID);
        primeException();
        try {
            stepData.remove(JOB_STEP_DEFINITION);
            JobStepDefinition stepDefinition = jobStepDefinitionService.find(KapuaId.ANY, currentStepDefId);
            stepData.put(JOB_STEP_DEFINITION, stepDefinition);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @And("I search for step definition(s) with the name")
    public void searchForStepDefinitionWithTheName(List<String> list) throws Exception {
        ArrayList<JobStepDefinition> jobStepDefinitions = new ArrayList<>();
        primeException();
        try {
            stepData.remove(JOB_STEP_DEFINITIONS);
            stepData.remove(JOB_STEP_DEFINITION);
            stepData.remove(CURRENT_JOB_STEP_DEFINITION_ID);
            for (String name : list) {
                JobStepDefinition stepDefinition = jobStepDefinitionService.findByName(name);
                jobStepDefinitions.add(stepDefinition);
                stepData.put(JOB_STEP_DEFINITIONS, jobStepDefinitions);
                stepData.put(JOB_STEP_DEFINITION, stepDefinition);
                stepData.put(CURRENT_JOB_STEP_DEFINITION_ID, stepDefinition.getId());
            }
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I count the step definition in the database")
    public void countStepDefinitionInDatabase() throws Exception {
        updateCount(() -> (int) jobStepDefinitionService.count(new JobStepDefinitionQuery(KapuaId.ANY)));
    }

    @When("I query for step definitions in scope {int}")
    public void countStepDefinitijonsInScope(Integer id) throws Exception {
        updateCount(() -> jobStepDefinitionService.query(new JobStepDefinitionQuery(KapuaId.ANY)).getSize());
    }

    @When("I delete the step definition")
    public void deleteExistingStepDefinition() throws Exception {
        KapuaId currentStepDefId = (KapuaId) stepData.get(CURRENT_JOB_STEP_DEFINITION_ID);
        primeException();
        try {
            jobStepDefinitionService.delete(KapuaId.ANY, currentStepDefId);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I change the step definition name to {string}")
    public void changeExistingStepDefinitionName(String name) throws Exception {
        JobStepDefinition stepDefinition = (JobStepDefinition) stepData.get(JOB_STEP_DEFINITION);
        primeException();
        try {
            stepDefinition.setName(name);
            stepDefinition = jobStepDefinitionService.update(stepDefinition);
            stepData.put(JOB_STEP_DEFINITION, stepDefinition);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I change the step definition processor name to {string}")
    public void changeExistingStepDefinitionProcessor(String name) throws Exception {
        JobStepDefinition stepDefinition = (JobStepDefinition) stepData.get(JOB_STEP_DEFINITION);
        primeException();
        try {
            stepDefinition.setProcessorName(name);
            stepDefinition = jobStepDefinitionService.update(stepDefinition);
            stepData.put(JOB_STEP_DEFINITION, stepDefinition);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I change the step definition type to {string}")
    public void changeExistingStepDefinitionType(String type) throws Exception {
        JobStepDefinition stepDefinition = (JobStepDefinition) stepData.get(JOB_STEP_DEFINITION);
        primeException();
        try {
            stepDefinition.setStepType(getTypeFromString(type));
            stepDefinition = jobStepDefinitionService.update(stepDefinition);
            stepData.put(JOB_STEP_DEFINITION, stepDefinition);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @Then("The step definition entity matches the creator")
    public void checkTheStepDefinitionAgainstTheCreator() {
        JobStepDefinitionCreator stepDefinitionCreator = (JobStepDefinitionCreator) stepData.get(JOB_STEP_DEFINITION_CREATOR);
        JobStepDefinition stepDefinition = (JobStepDefinition) stepData.get(JOB_STEP_DEFINITION);
        Assert.assertEquals("The step definition has the wrong name!", stepDefinitionCreator.getName(), stepDefinition.getName());
        Assert.assertEquals("The step definition has the wrong description!", stepDefinitionCreator.getDescription(), stepDefinition.getDescription());
        Assert.assertEquals("The step definition has the wrong reader name!", stepDefinitionCreator.getReaderName(), stepDefinition.getReaderName());
        Assert.assertEquals("The step definition has the wrong processor name!", stepDefinitionCreator.getProcessorName(), stepDefinition.getProcessorName());
        Assert.assertEquals("The step definition has the wrong writer name!", stepDefinitionCreator.getWriterName(), stepDefinition.getWriterName());
        Assert.assertEquals("The step definition has a wrong step type!", stepDefinitionCreator.getStepType(), stepDefinition.getStepType());
        Assert.assertNotNull("The step definition has no properties!", stepDefinition.getStepProperties());
        Assert.assertEquals("The step definition has a wrong number of properties!", stepDefinitionCreator.getStepProperties().size(), stepDefinition.getStepProperties().size());

        for (JobStepProperty jobStepPropertyCreator : stepDefinitionCreator.getStepProperties()) {
            JobStepProperty jobStepPropertyCreated = stepDefinition.getStepProperty(jobStepPropertyCreator.getName());

            Assert.assertNotNull("JobStepProperty not present: " + jobStepPropertyCreator.getName(), jobStepPropertyCreated);
            Assert.assertEquals(jobStepPropertyCreator.getName(), jobStepPropertyCreated.getName());
            Assert.assertEquals(jobStepPropertyCreator.getPropertyType(), jobStepPropertyCreated.getPropertyType());
            Assert.assertEquals(jobStepPropertyCreator.getPropertyValue(), jobStepPropertyCreated.getPropertyValue());
        }
    }

    @Then("There is no such step definition item in the database")
    public void checkThatNoStepDefinitionWasFound() {
        JobStepDefinition stepDefinition = (JobStepDefinition) stepData.get(JOB_STEP_DEFINITION);
        Assert.assertNull("Unexpected step definition item was found!", stepDefinition);
    }

    @Then("The step definition name is {string}")
    public void checkStepDefinitionName(String name) {
        JobStepDefinition stepDefinition = (JobStepDefinition) stepData.get(JOB_STEP_DEFINITION);
        Assert.assertEquals("The step definition name does not match!", name, stepDefinition.getName());
    }

    @Then("The step definition type is {string}")
    public void checkStepDefinitionType(String type) {
        JobStepDefinition stepDefinition = (JobStepDefinition) stepData.get(JOB_STEP_DEFINITION);
        Assert.assertEquals("The step definition type does not match!", getTypeFromString(type), stepDefinition.getStepType());
    }

    @Then("The step definition processor name is {string}")
    public void checkStepDefinitionProcessorName(String name) {
        JobStepDefinition stepDefinition = (JobStepDefinition) stepData.get(JOB_STEP_DEFINITION);
        Assert.assertEquals("The step definition processor name does not match!", name, stepDefinition.getProcessorName());
    }

    @When("I test the sanity of the step definition factory")
    public void testTheStepDefinitionFactory() {
        Assert.assertNotNull(new JobStepDefinitionCreator(SYS_SCOPE_ID));
        Assert.assertNotNull(jobStepDefinitionFactory.newEntity(SYS_SCOPE_ID));
        Assert.assertNotNull(jobStepDefinitionFactory.newStepProperty("TestName", "TestType", "TestValue", "TestExampleValue"));
    }

    private JobStepType getTypeFromString(String type) {
        if (type.trim().toUpperCase().equals("TARGET")) {
            return JobStepType.TARGET;
        } else {
            return JobStepType.GENERIC;
        }
    }

    private JobStepDefinitionCreator prepareDefaultJobStepDefinitionCreator() {
        JobStepDefinitionCreator tmpCr = new JobStepDefinitionCreator(getCurrentScopeId());
        tmpCr.setName(String.format("DefinitionName_%d", random.nextInt()));
        tmpCr.setDescription("DefinitionDescription");
        tmpCr.setReaderName(null);
        tmpCr.setProcessorName(TestJobStepProcessor.class.getName());
        tmpCr.setWriterName(null);
        tmpCr.setStepType(JobStepType.TARGET);
        return tmpCr;
    }
}
