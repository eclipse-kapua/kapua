/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.scheduler.steps;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.kapua.qa.common.TestBase;
import org.eclipse.kapua.qa.common.cucumber.CucTriggerProperty;
import org.eclipse.kapua.service.scheduler.trigger.Trigger;
import org.eclipse.kapua.service.scheduler.trigger.TriggerAttributes;
import org.eclipse.kapua.service.scheduler.trigger.TriggerCreator;
import org.eclipse.kapua.service.scheduler.trigger.TriggerFactory;
import org.eclipse.kapua.service.scheduler.trigger.TriggerListResult;
import org.eclipse.kapua.service.scheduler.trigger.TriggerService;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionAttributes;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionFactory;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionQuery;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionService;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerProperty;
import org.junit.Assert;

import com.google.inject.Singleton;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

@Singleton
public class JobScheduleServiceSteps extends TestBase {

    private TriggerFactory triggerFactory;
    private TriggerService triggerService;
    private TriggerDefinitionFactory triggerDefinitionFactory;
    private TriggerDefinitionService triggerDefinitionService;

    private static final String TRIGGER_DEFINITION_ID = "TriggerDefinitionId";
    private static final String TRIGGER = "Trigger";
    private static final String TRIGGER_CREATOR = "TriggerCreator";
    private static final String CURRENT_TRIGGER_ID = "CurrentTriggerId";
    private static final String UPDATED_TRIGGER = "UpdatedTrigger";
    private static final String TRIGGER_START_DATE = "TriggerStartDate";
    private static final String TRIGGER_END_DATE = "TriggerEndDate";

    // ****************************************************************************************
    // * Implementation of Gherkin steps used in JobService.feature scenarios.                *
    // *                                                                                      *
    // * MockedLocator is used for Location Service. Mockito is used to mock other            *
    // * services that the Account services dependent on. Dependent services are:             *
    // * - Authorization Service                                                              *
    // ****************************************************************************************

    private static final String KAPUA_ID_CLASS_NAME = "org.eclipse.kapua.model.id.KapuaId";

    // Default constructor
    @Inject
    public JobScheduleServiceSteps(StepData stepData) {
        super(stepData);
    }

    @After(value = "@setup")
    public void setServices() {
        KapuaLocator locator = KapuaLocator.getInstance();
        triggerFactory = locator.getFactory(TriggerFactory.class);
        triggerService = locator.getService(TriggerService.class);
        triggerDefinitionFactory = locator.getFactory(TriggerDefinitionFactory.class);
        triggerDefinitionService = locator.getService(TriggerDefinitionService.class);
    }

    // ************************************************************************************
    // ************************************************************************************
    // * Definition of Cucumber scenario steps                                            *
    // ************************************************************************************
    // ************************************************************************************

    // ************************************************************************************
    // * Setup and tear-down steps                                                        *
    // ************************************************************************************

    @Before
    public void beforeScenarioDockerFull(Scenario scenario) {
        updateScenario(scenario);
    }

    // ************************************************************************************
    // * The Cucumber test steps                                                          *
    // ************************************************************************************

    @And("I try to create scheduler with name {string}")
    public void iTryToCreateSchedulerWithName(String schedulerName) throws Exception {
        TriggerCreator triggerCreator = new TriggerCreator(getCurrentScopeId());
        KapuaId triggerDefinitionId = (KapuaId) stepData.get(TRIGGER_DEFINITION_ID);
        triggerCreator.setName(schedulerName);
        triggerCreator.setDescription("A trigger description");
        triggerCreator.setStartsOn(new Date());
        triggerCreator.setTriggerDefinitionId(triggerDefinitionId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            primeException();
            stepData.remove(TRIGGER);
            Trigger trigger = triggerService.create(triggerCreator);
            stepData.put(TRIGGER, trigger);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @And("I find scheduler properties with name {string}")
    public void iFindTriggerPropertiesWithName(String triggerDefinitionName) throws Exception {
        primeException();
        try {
            TriggerDefinitionQuery triggerDefinitionQuery = new TriggerDefinitionQuery(getCurrentScopeId());
            triggerDefinitionQuery.setPredicate(triggerDefinitionQuery.attributePredicate(TriggerDefinitionAttributes.NAME, triggerDefinitionName, AttributePredicate.Operator.EQUAL));
            TriggerDefinition triggerDefinition = triggerDefinitionService.query(triggerDefinitionQuery).getFirstItem();
            stepData.put("TriggerDefinition", triggerDefinition);
            stepData.put(TRIGGER_DEFINITION_ID, triggerDefinition.getId());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @And("A regular trigger creator with the name {string} is created")
    public void aRegularTriggerCreatorWithTheName(String triggerName) {
        TriggerCreator triggerCreator = new TriggerCreator(getCurrentScopeId());
        KapuaId currentTriggerDefId = (KapuaId) stepData.get(TRIGGER_DEFINITION_ID);
        KapuaId jobId = (KapuaId) stepData.get("CurrentJobId");
        triggerCreator.setName(triggerName);
        triggerCreator.setDescription("A trigger description");
        triggerCreator.setTriggerDefinitionId(currentTriggerDefId);
        triggerCreator.getTriggerProperties().add(triggerDefinitionFactory.newTriggerProperty("jobId", KAPUA_ID_CLASS_NAME, jobId.toCompactId()));
        triggerCreator.getTriggerProperties().add(triggerDefinitionFactory.newTriggerProperty("scopeId", KAPUA_ID_CLASS_NAME, getCurrentScopeId().toCompactId()));
        stepData.remove(TRIGGER_CREATOR);
        stepData.put(TRIGGER_CREATOR, triggerCreator);
    }

    @And("A trigger creator without a name")
    public void aTriggerCreatorWithoutAName() {
        TriggerCreator triggerCreator = new TriggerCreator(getCurrentScopeId());
        KapuaId currentTriggerDefId = (KapuaId) stepData.get(TRIGGER_DEFINITION_ID);
        triggerCreator.setTriggerDefinitionId(currentTriggerDefId);
        triggerCreator.setName(null);
        stepData.put(TRIGGER_CREATOR, triggerCreator);
    }

    @And("A regular trigger creator with the name {string} and following properties")
    public void aRegularTriggerCreatorWithTheNameAndFollowingProperties(String triggerName, List<CucTriggerProperty> list) {
        TriggerCreator triggerCreator = new TriggerCreator(getCurrentScopeId());
        KapuaId currentTriggerDefId = (KapuaId) stepData.get(TRIGGER_DEFINITION_ID);
        triggerCreator.setName(triggerName);
        triggerCreator.setDescription("A trigger description");
        triggerCreator.setTriggerDefinitionId(currentTriggerDefId);
        List<TriggerProperty> tmpPropList = new ArrayList<>();
        for (CucTriggerProperty prop : list) {
            tmpPropList.add(triggerFactory.newTriggerProperty(prop.getName(), prop.getType(), prop.getValue()));
        }
        triggerCreator.setTriggerProperties(tmpPropList);
        stepData.put(TRIGGER_CREATOR, triggerCreator);
    }

    @And("I try to create a new trigger entity from the existing creator")
    public void iCreateANewTriggerEntityFromTheExistingCreator() throws Exception {
        TriggerCreator triggerCreator = (TriggerCreator) stepData.get(TRIGGER_CREATOR);
        triggerCreator.setScopeId(getCurrentScopeId());
        triggerCreator.setStartsOn(new Date());
        primeException();
        try {
            stepData.remove(TRIGGER);
            stepData.remove(CURRENT_TRIGGER_ID);
            Trigger trigger = triggerService.create(triggerCreator);
            stepData.put(TRIGGER, trigger);
            stepData.put(CURRENT_TRIGGER_ID, trigger.getId());
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @And("I try to edit trigger name {string}")
    public void iTryToEditTriggerName(String newTriggerName) throws Exception {
        try {
            Trigger trigger = (Trigger) stepData.get(TRIGGER);
            trigger.setName(newTriggerName);
            trigger.setDescription("A trigger updated description");
            primeException();
            Trigger updatedTrigger = triggerService.update(trigger);
            stepData.put(UPDATED_TRIGGER, updatedTrigger);
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @And("I try to delete last created trigger")
    public void iTryToDeleteTrigger() throws Exception {
        try {
            Trigger trigger = (Trigger) stepData.get(TRIGGER);
            primeException();
            triggerService.delete(getCurrentScopeId(), trigger.getId());
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @And("I create a new trigger from the existing creator with previously defined date properties")
    public void createTriggerWithDateProperties() throws Exception {
        TriggerCreator triggerCreator = (TriggerCreator) stepData.get(TRIGGER_CREATOR);
        Date startDate = (Date) stepData.get(TRIGGER_START_DATE);
        Date endDate = (Date) stepData.get(TRIGGER_END_DATE);
        triggerCreator.setScopeId(getCurrentScopeId());
        triggerCreator.setStartsOn(startDate);
        triggerCreator.setEndsOn(endDate);
        primeException();
        try {
            stepData.remove(TRIGGER);
            stepData.remove(CURRENT_TRIGGER_ID);
            Trigger trigger = triggerService.create(triggerCreator);
            trigger.getTriggerProperties();
            stepData.put(TRIGGER, trigger);
            stepData.put(CURRENT_TRIGGER_ID, trigger.getId());
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @And("The trigger is set to start on {string} at {string}")
    public void setTriggerStartDate(String startDateStr, String startTimeStr) throws Exception {
        try {
            primeException();
            Date startDate = setDateAndTimeValue(startDateStr, startTimeStr);
            stepData.put(TRIGGER_START_DATE, startDate);
        } catch (ParseException ex) {
            verifyException(ex);
        }
    }

    @And("The trigger is set to start today at {string}")
    public void setTodayAsTriggerStartDate(String startTimeStr) throws Exception {
        try {
            primeException();
            Date startDate = setTodayAsDateValue(startTimeStr);
            stepData.put(TRIGGER_START_DATE, startDate);
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @And("The trigger is set to start tomorrow at {string}")
    public void setTomorrowAsTriggerStartDate(String startTimeStr) throws Exception {
        try {
            primeException();
            Date startDate = setTomorrowAsDateValue(startTimeStr);
            stepData.put(TRIGGER_START_DATE, startDate);
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @And("The trigger is set to end on {string} at {string}")
    public void setTriggerEndDate(String endDateStr, String endTimeStr) throws Exception {
        try {
            primeException();
            Date endDate = setDateAndTimeValue(endDateStr, endTimeStr);
            stepData.put(TRIGGER_END_DATE, endDate);
        } catch (ParseException ex) {
            verifyException(ex);
        }
    }

    @And("The trigger is set to end in {int} second(s)")
    public void setWithinSecondsAsTriggerEndDate(int seconds) throws Exception {
        try {
            primeException();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, seconds);
            Date endDate = calendar.getTime();
            stepData.put(TRIGGER_END_DATE, endDate);
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @And("The trigger is set to end tomorrow at {string}")
    public void setTomorrowAsTriggerEndDate(String startTimeStr) throws Exception {
        try {
            primeException();
            Date endDate = setTomorrowAsDateValue(startTimeStr);
            stepData.put(TRIGGER_END_DATE, endDate);
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    //why this method? can't we use a DateFormat?
    private Date setDateAndTimeValue(String dateStr, String timeStr) throws ParseException {
        String[] dateComponents = dateStr.split("-");
        String[] timeComponents = timeStr.split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateComponents[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(dateComponents[1]) - 1);
        calendar.set(Calendar.YEAR, Integer.parseInt(dateComponents[2]));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeComponents[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeComponents[1]));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    //why this method? can't we use a DateFormat?
    private Date setTodayAsDateValue(String timeString) {
        String[] timeComponents = timeString.split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeComponents[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeComponents[1]));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    //why this method? can't we use a DateFormat?
    private Date setTomorrowAsDateValue(String timeStr) {
        String[] timeComponents = timeStr.split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeComponents[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeComponents[1]));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    @And("I set retry interval to {int}")
    public void iSetRetryIntervalTo(long retryInterval) {
        TriggerCreator triggerCreator = (TriggerCreator) stepData.get(TRIGGER_CREATOR);
        triggerCreator.setRetryInterval(retryInterval);
        stepData.remove(TRIGGER_CREATOR);
        stepData.put(TRIGGER_CREATOR, triggerCreator);
    }

    @Then("I set retry interval to null")
    public void iSetRetryIntervalToNull() {
        TriggerCreator triggerCreator = (TriggerCreator) stepData.get(TRIGGER_CREATOR);
        triggerCreator.setRetryInterval(null);
        stepData.remove(TRIGGER_CREATOR);
        stepData.put(TRIGGER_CREATOR, triggerCreator);
    }

    @Then("I set cron expression to {string}")
    public void iSetCronExpressionTo(String cron) throws Throwable {
        TriggerCreator triggerCreator = (TriggerCreator) stepData.get(TRIGGER_CREATOR);
        triggerCreator.setCronScheduling(cron);
        stepData.remove(TRIGGER_CREATOR);
        stepData.put(TRIGGER_CREATOR, triggerCreator);
    }

    @Then("I delete the previously created trigger")
    public void iDeleteThePreviouslyCreatedTrigger() throws Exception {
        Trigger trigger = (Trigger) stepData.get(TRIGGER);
        primeException();
        try {
            triggerService.delete(trigger.getScopeId(), trigger.getId());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @And("I search for the trigger in the database")
    public void iSearchForTheTriggerInTheDatabase() throws Exception {
        KapuaId currentTriggerID = (KapuaId) stepData.get(CURRENT_TRIGGER_ID);
        primeException();
        try {
            stepData.remove(TRIGGER);
            Trigger trigger = triggerService.find(getCurrentScopeId(), currentTriggerID);
            stepData.put(TRIGGER, trigger);
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @Then("I delete trigger with name {string}")
    public void iDeleteTriggerWithName(String arg0) throws Throwable {
        Trigger trigger = (Trigger) stepData.get(TRIGGER);
        primeException();
        try {
            triggerService.delete(trigger.getScopeId(), trigger.getId());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @And("I search for the trigger with name {string} in the database")
    public void iSearchForTheTriggerWithNameInTheDatabase(String triggerName) throws Throwable {
        KapuaQuery triggerQuery = new KapuaQuery(getCurrentScopeId());
        triggerQuery.setPredicate(triggerQuery.attributePredicate(TriggerAttributes.NAME, triggerName, AttributePredicate.Operator.EQUAL));
        primeException();
        try {
            stepData.remove(TRIGGER);
            TriggerListResult triggerListResult = triggerService.query(triggerQuery);
            Trigger trigger = triggerListResult.getFirstItem();
            stepData.put(TRIGGER, trigger);
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @And("There is no trigger with the name {string} in the database")
    public void thereIsNoTriggerWithTheNameInTheDatabase(String triggerName) throws Throwable {
        Assert.assertNull(stepData.get(TRIGGER));
    }

    @And("I try to edit trigger definition to {string}")
    public void iTryToEditSchedulerPropertyTo(String trigerDefinition) throws Exception {
        Trigger trigger = (Trigger) stepData.get(TRIGGER);
        primeException();
        try {
            TriggerDefinitionQuery triggerDefinitionQuery = new TriggerDefinitionQuery(getCurrentScopeId());
            triggerDefinitionQuery.setPredicate(triggerDefinitionQuery.attributePredicate(TriggerDefinitionAttributes.NAME, trigerDefinition, AttributePredicate.Operator.EQUAL));
            TriggerDefinition triggerDefinition = triggerDefinitionService.query(triggerDefinitionQuery).getFirstItem();
            trigger.setTriggerDefinitionId(triggerDefinition.getId());
            Trigger updateTrigger = triggerService.update(trigger);
            stepData.put(UPDATED_TRIGGER, updateTrigger);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @And("I try to edit start date to {string} at {string}")
    public void iTryToEditStartDateTo(String startDate, String startTime) throws Exception {
        Trigger trigger = (Trigger) stepData.get(TRIGGER);
        Date newTriggerStartOnDate = setDateAndTimeValue(startDate, startTime);
        trigger.setStartsOn(newTriggerStartOnDate);
        try {
            primeException();
            Trigger updatedTrigger = triggerService.update(trigger);
            stepData.put(UPDATED_TRIGGER, updatedTrigger);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @And("I try to edit end date to {string} at {string}")
    public void iTryToEditEndDateTo(String endDate, String endTime) throws Exception {
        Trigger trigger = (Trigger) stepData.get(TRIGGER);
        Date newTriggerEndsOnDate = setDateAndTimeValue(endDate, endTime);
        trigger.setEndsOn(newTriggerEndsOnDate);
        try {
            primeException();
            Trigger updatedTrigger = triggerService.update(trigger);
            stepData.put(UPDATED_TRIGGER, updatedTrigger);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @And("I try to edit end date to tomorrow at {string}")
    public void iTryToEditEndDateToTomorrowAt(String endTime) throws Exception {
        Trigger trigger = (Trigger) stepData.get(TRIGGER);
        Date newTriggerEndsOnDate = setTomorrowAsDateValue(endTime);
        trigger.setEndsOn(newTriggerEndsOnDate);
        try {
            primeException();
            Trigger updatedTrigger = triggerService.update(trigger);
            stepData.put(UPDATED_TRIGGER, updatedTrigger);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

}
