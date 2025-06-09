/*******************************************************************************
 * Copyright (c) 2023, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.user.steps;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.kapua.qa.common.TestBase;
import org.eclipse.kapua.qa.common.cucumber.CucUserProfile;
import org.eclipse.kapua.service.user.profile.UserProfile;
import org.eclipse.kapua.service.user.profile.UserProfileService;
import org.junit.Assert;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Then;

@Singleton
public class UserProfileServiceTest extends TestBase {

    private UserProfileService userProfileService;

    @Inject
    public UserProfileServiceTest(StepData stepData) {
        super(stepData);
    }

    @After(value = "@setup")
    public void setServices() {
        KapuaLocator locator = KapuaLocator.getInstance();
        userProfileService = locator.getService(UserProfileService.class);
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        updateScenario(scenario);
    }

    @Then("I change the profile to the following")
    public void iChangeTheUserProfileToTheFollowing(CucUserProfile cucUserProfile) throws Exception {
        UserProfile userProfile = new UserProfile();
        userProfile.setDisplayName(cucUserProfile.getDisplayName());
        userProfile.setEmail(cucUserProfile.getEmail());
        userProfile.setPhoneNumber(cucUserProfile.getPhoneNumber());
        try {
            userProfileService.changeUserProfile(userProfile);
        } catch (Exception e) {
            verifyException(e);
        }
    }

    @Then("I read the following user profile")
    public void iReadTheFollowingUserProfile(CucUserProfile expectedUserProfile) throws KapuaException {
        UserProfile actualUserProfile = userProfileService.getUserProfile();
        Assert.assertEquals(expectedUserProfile.getDisplayName(), actualUserProfile.getDisplayName());
        Assert.assertEquals(expectedUserProfile.getEmail(), actualUserProfile.getEmail());
        Assert.assertEquals(expectedUserProfile.getPhoneNumber(), actualUserProfile.getPhoneNumber());
    }
}
