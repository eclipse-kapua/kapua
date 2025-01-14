/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech
 *******************************************************************************/
package org.eclipse.kapua.service.tag.steps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.kapua.qa.common.TestBase;
import org.eclipse.kapua.qa.common.cucumber.CucConfig;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.tag.Tag;
import org.eclipse.kapua.service.tag.TagAttributes;
import org.eclipse.kapua.service.tag.TagCreator;
import org.eclipse.kapua.service.tag.TagFactory;
import org.eclipse.kapua.service.tag.TagListResult;
import org.eclipse.kapua.service.tag.TagQuery;
import org.eclipse.kapua.service.tag.TagService;
import org.junit.Assert;

import com.google.inject.Singleton;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Implementation of Gherkin steps used in TagService.feature scenarios.
 */
@Singleton
public class TagServiceSteps extends TestBase {

    /**
     * Tag service.
     */
    private TagService tagService;
    private TagFactory tagFactory;

    @Inject
    public TagServiceSteps(StepData stepData) {
        super(stepData);
    }

    @After(value = "@setup")
    public void setServices() {
        KapuaLocator locator = KapuaLocator.getInstance();
        tagService = locator.getService(TagService.class);
        tagFactory = locator.getFactory(TagFactory.class);
    }

    // *************************************
    // Definition of Cucumber scenario steps
    // *************************************

    @Before
    public void beforeScenarioDockerFull(Scenario scenario) {
        updateScenario(scenario);
    }

    @Given("I configure the tag service")
    public void setConfigurationValue(List<CucConfig> testConfigs) throws Exception {
        Account lastAcc = (Account) stepData.get("LastAccount");
        KapuaId scopeId = SYS_SCOPE_ID;
        KapuaId parentId = SYS_SCOPE_ID;
        if (lastAcc != null) {
            scopeId = lastAcc.getId();
            parentId = lastAcc.getScopeId();
        }
        Map<String, Object> valueMap = new HashMap<>();
        for (CucConfig config : testConfigs) {
            config.addConfigToMap(valueMap);
        }
        try {
            primeException();
            tagService.setConfigValues(scopeId, parentId, valueMap);
        } catch (KapuaException ke) {
            verifyException(ke);
        }
    }

    @Given("I create tag with name {string} without description")
    public void tagWithNameWithoutDescriptionIsCreated(String tagName) throws Exception {
        try {
            TagCreator tagCreator = tagCreatorCreatorWithoutDescription(tagName);
            stepData.remove("tag");
            Tag tag = tagService.create(tagCreator);
            stepData.put("tag", tag);
        } catch (Exception e) {
            verifyException(e);
        }
    }

    @And("A tag with name {string} is created")
    public void creatingTagWithName(String tagName) throws Exception {
        try {
            TagCreator tagCreator = tagCreatorCreatorWithoutDescription(tagName);
            stepData.remove("tag");
            Tag tag = tagService.create(tagCreator);
            stepData.put("tag", tag);
        } catch (Exception e) {
            verifyException(e);
        }
    }

    @When("I create tag with name {string} and description {string}")
    public void tagWithNameIsCreatedWithDescription(String tagName, String tagDescription) throws Exception {
        try {
            TagCreator tagCreator = tagCreatorCreatorWithDescription(tagName, tagDescription);
            stepData.remove("tag");
            Tag tag = tagService.create(tagCreator);
            stepData.put("tag", tag);
        } catch (Exception e) {
            verifyException(e);
        }
    }

    @When("I create tag with name {string}")
    public void tagWithNameIsCreatedWithoutDescription(String tagName) throws Exception {
        try {
            TagCreator tagCreator = tagCreatorCreatorWithDescription(tagName, null);
            stepData.remove("tag");
            Tag tag = tagService.create(tagCreator);
            stepData.put("tag", tag);
        } catch (Exception e) {
            verifyException(e);
        }
    }

    @Given("I try to create tags with that include invalid symbols in name")
    public void tagWithInvalidSymbols() throws Exception {
        String invalidSymbols = "!\"#$%&'()=»Ç" +
                ">:;<-.,⁄@‹›€" +
                "*ı–°·‚±Œ„‰" +
                "?“‘”’ÉØ∏{}|Æ" +
                "æÒ\uF8FFÔÓÌÏÎÍÅ«" +
                "◊Ñˆ¯Èˇ¿";
        for (int i = 0; i < invalidSymbols.length(); i++) {
            String tagName = "Tag" + invalidSymbols.charAt(i);
            try {
                TagCreator tagCreator = tagCreatorCreatorWithoutDescription(tagName);
                Tag tag = tagService.create(tagCreator);
            } catch (Exception e) {
                verifyException(e);
            }
        }
    }

    @When("Tag with name {string} is searched")
    public void tagWithNameIfSearched(String tagName) throws Throwable {
        try {
            stepData.remove("tag");
            primeException();
            TagQuery query = new TagQuery(SYS_SCOPE_ID);
            query.setPredicate(query.attributePredicate(TagAttributes.NAME, tagName, AttributePredicate.Operator.EQUAL));
            //TODO: #LAYER_VIOLATION - isn't this a find by name?
            TagListResult queryResult = tagService.query(query);
            Tag foundTag = queryResult.getFirstItem();
            stepData.put("tag", foundTag);
            stepData.put("queryResult", queryResult);
        } catch (KapuaException ke) {
            verifyException(ke);
        }
    }

    @When("I delete tag with name {string}")
    public void deleteTagWithName(String tagName) throws Throwable {
        try {
            primeException();
            TagQuery query = new TagQuery(getCurrentScopeId());
            query.setPredicate(query.attributePredicate(TagAttributes.NAME, tagName, AttributePredicate.Operator.EQUAL));
            //TODO: #LAYER_VIOLATION - isn't this a find by name?
            TagListResult queryResult = tagService.query(query);
            Tag foundTag = queryResult.getFirstItem();
            //TODO: #LAYER_VIOLATION - or better, delete by name?
            tagService.delete(getCurrentScopeId(), foundTag.getId());
        } catch (Exception e) {
            verifyException(e);
        }
    }

    @Then("I find a tag with name {string}")
    public void tagWithNameIsFound(String tagName) {
        Tag foundTag = (Tag) stepData.get("tag");
        Assert.assertEquals(tagName, foundTag.getName());
    }

    @Then("No tag was found")
    public void checkNoTagWasFound() {
        Assert.assertNull(stepData.get("tag"));
    }

    @Then("Tag with name {string} is not found")
    public void tagWithNameNotFound(String tagName) {
        Tag foundTag = (Tag) stepData.get("tag");
        Assert.assertNull(foundTag);
    }

    @Then("I find and delete tag with name {string}")
    public void tagWithNameIsDeleted(String tagName) throws Throwable {
        try {
            Tag foundTag = (Tag) stepData.get("tag");
            TagListResult queryResult = (TagListResult) stepData.get("queryResult");
            tagService.delete(foundTag.getScopeId(), foundTag.getId());
            queryResult.clearItems();
            foundTag = queryResult.getFirstItem();
            Assert.assertEquals(null, foundTag);
        } catch (Exception e) {
            verifyException(e);
        }
    }

    /**
     * Create TagCreator for creating tag with specified name.
     *
     * @param tagName
     *         name of tag
     * @return tag creator for tag with specified name
     */
    private TagCreator tagCreatorCreatorWithoutDescription(String tagName) {
        TagCreator tagCreator = tagFactory.newCreator(getCurrentScopeId());
        tagCreator.setName(tagName);
        return tagCreator;
    }

    public TagCreator tagCreatorCreatorWithDescription(String tagName, String tagDescription) {
        TagCreator tagCreator = tagFactory.newCreator(SYS_SCOPE_ID);
        tagCreator.setName(tagName);
        tagCreator.setDescription(tagDescription);
        return tagCreator;
    }

    @And("Tag name is changed into name {string}")
    public void tagNameIsChangedIntoName(String tagName) throws Exception {
        Tag tag = (Tag) stepData.get("tag");
        tag.setName(tagName);
        try {
            primeException();
            stepData.remove("tag");
            Tag newtag = tagService.update(tag);
            stepData.put("tag", newtag);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @And("Name of tag {string} is changed into {string}")
    public void nameOfTagIsChangedInto(String tagName, String newTagName) throws Exception {
        try {
            primeException();
            TagQuery query = new TagQuery(getCurrentScopeId());
            query.setPredicate(query.attributePredicate(TagAttributes.NAME, tagName, AttributePredicate.Operator.EQUAL));
            TagListResult queryResult = tagService.query(query);
            Tag foundTag = queryResult.getFirstItem();
            foundTag.setName(newTagName);
            //TODO: #LAYER_VIOLATION - this is updateByName
            Tag newTag = tagService.update(foundTag);
            stepData.put("tag", newTag);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @And("Tag description is changed into {string}")
    public void tagDescriptionIsChangedInto(String description) throws Exception {
        Tag tag = (Tag) stepData.get("tag");
        try {
            tag.setDescription(description);
            primeException();
            stepData.remove("tag");
            Tag newtag = tagService.update(tag);
            stepData.put("tag", newtag);
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @And("Description of tag {string} is changed into {string}")
    public void descriptionOfTagIsChangedInto(String tagName, String newDescription) throws Exception {
        try {
            TagQuery query = new TagQuery(getCurrentScopeId());
            query.setPredicate(query.attributePredicate(TagAttributes.NAME, tagName, AttributePredicate.Operator.EQUAL));
            TagListResult queryResult = tagService.query(query);
            Tag foundTag = queryResult.getFirstItem();
            foundTag.setDescription(newDescription);
            primeException();
            stepData.remove("tag");
            //TODO: #LAYER_VIOLATION - this is just update by name
            Tag newtag = tagService.update(foundTag);
            stepData.put("tag", newtag);
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @And("I delete the tag with name {string}")
    public void tagIsDeleted(String tagName) throws Exception {
        Tag tag = (Tag) stepData.get("tag");
        try {
            tagService.delete(getCurrentScopeId(), tag.getId());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @Given("Tag is assigned to device")
    public void tagIsAssignedToDevice() throws Throwable {
        Tag tag = (Tag) stepData.get("tag");
        Device device = (Device) stepData.get("Device");
        try {
            Set<KapuaId> tagIds = device.getTagIds();
            Assert.assertTrue(tagIds.contains(tag.getId()));
        } catch (Exception e) {
            verifyException(e);
        }
    }

    @Given("Tag is not assigned to device")
    public void tagIsNotAssignedToDevice() throws Throwable {
        Tag tag = (Tag) stepData.get("tag");
        Device device = (Device) stepData.get("Device");
        try {
            Set<KapuaId> tagIds = device.getTagIds();
            Assert.assertFalse(tagIds.contains(tag.getId()));
        } catch (Exception e) {
            verifyException(e);
        }
    }
}
