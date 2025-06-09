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
package org.eclipse.kapua.service.authentication.steps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.common.BasicSteps;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.kapua.qa.common.TestBase;
import org.eclipse.kapua.qa.common.cucumber.CucConfig;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.credential.CredentialCreator;
import org.eclipse.kapua.service.authentication.credential.CredentialService;
import org.eclipse.kapua.service.authentication.credential.CredentialStatus;
import org.eclipse.kapua.service.authentication.user.PasswordChangeRequest;
import org.eclipse.kapua.service.authentication.user.PasswordResetRequest;
import org.eclipse.kapua.service.authentication.user.UserCredentialsService;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserService;
import org.junit.Assert;

import com.google.inject.Singleton;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

// Implementation of Gherkin steps used to test miscellaneous Shiro
// authorization functionality.

@Singleton
public class AuthenticationServiceSteps extends TestBase {

    protected KapuaLocator locator;

    private static final String LAST_ACCOUNT = "LastAccount";

    private CredentialService credentialService;
    private UserService userService;

    private UserCredentialsService userCredentialsService;

    @Inject
    public AuthenticationServiceSteps(StepData stepData) {
        super(stepData);
    }

    @Before(value = "@env_docker or @env_docker_base or @env_none", order = 10)
    public void beforeScenarioNone(Scenario scenario) throws KapuaException {
        updateScenario(scenario);
    }

    @After(value = "@setup")
    public void setServices() {
        locator = KapuaLocator.getInstance();
        credentialService = locator.getService(CredentialService.class);
        userService = locator.getService(UserService.class);
        userCredentialsService = locator.getService(UserCredentialsService.class);
    }

    @When("I create default test-user")
    public void createDefaultUser() throws KapuaException {
        UserCreator userCreator = new UserCreator(KapuaId.ONE, "test-user");
        User user = userService.create(userCreator);
        stepData.put("User", user);
    }

    @When("I configure the credential service")
    public void setConfigurationValue(List<CucConfig> cucConfigs) throws Exception {
        Map<String, Object> valueMap = new HashMap<>();
        KapuaId scopeId;
        KapuaId parentScopeId;
        Account tmpAccount = (Account) stepData.get(LAST_ACCOUNT);

        if (tmpAccount != null) {
            scopeId = tmpAccount.getId();
            parentScopeId = tmpAccount.getScopeId();
        } else {
            scopeId = SYS_SCOPE_ID;
            parentScopeId = SYS_SCOPE_ID;
        }

        for (CucConfig config : cucConfigs) {
            config.addConfigToMap(valueMap);
            if (config.getScopeId() != null) {
                scopeId = getKapuaId(config.getScopeId());
            }
            if (config.getParentId() != null) {
                parentScopeId = getKapuaId(config.getParentId());
            }
        }
        try {
            primeException();
            credentialService.setConfigValues(scopeId, parentScopeId, valueMap);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I read credential service configuration")
    public void getConfigurationValue() throws Exception {
        KapuaId scopeId;
        Account tmpAccount = (Account) stepData.get(LAST_ACCOUNT);

        if (tmpAccount != null) {
            scopeId = tmpAccount.getId();
        } else {
            scopeId = SYS_SCOPE_ID;
        }

        try {
            primeException();
            Map<String, Object> configValues = credentialService.getConfigValues(scopeId);
            stepData.put("configValues", configValues);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I check that service configuration {string} matches value {string}")
    public void checkConfigValue(String key, String value) {
        Map<String, Object> configValues = (Map<String, Object>) stepData.get("configValues");
        Object configValue = configValues.get(key);
        Assert.assertEquals(value, configValue);
    }

    @When("I check that service configuration {string} has no value")
    public void checkNullConfigValue(String key) {
        Map<String, Object> configValues = (Map<String, Object>) stepData.get("configValues");
        Object configValue = configValues.get(key);
        Assert.assertEquals(configValue, null);
    }

    @When("I configure the credential service for the account with the id {int}")
    public void setCredentialServiceConfig(int accountId, List<CucConfig> cucConfigs) throws Exception {
        Map<String, Object> valueMap = new HashMap<>();
        KapuaId accId = getKapuaId(accountId);

        for (CucConfig config : cucConfigs) {
            config.addConfigToMap(valueMap);
        }

        primeException();
        try {
            credentialService.setConfigValues(accId, SYS_SCOPE_ID, valueMap);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @Given("I create a new PASSWORD credential for the default user with password {string}")
    public void createPasswordCredential(String password) throws Exception {
        primeException();

        User user = (User) stepData.get("User");
        CredentialCreator credentialCreator = new CredentialCreator(user.getScopeId(), user.getId(), "PASSWORD", password, CredentialStatus.ENABLED, null);
        try {
            Credential credential = credentialService.create(credentialCreator);
            stepData.put(BasicSteps.LAST_CREDENTIAL_ID, credential.getId());
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @When("I change the user credential password with old password {string} and new password {string}")
    public void changeUserPasswordCredential(String oldPassword, String newPassword) throws Exception {
        primeException();

        PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest();
        passwordChangeRequest.setCurrentPassword(oldPassword);
        passwordChangeRequest.setNewPassword(newPassword);

        try {
            userCredentialsService.changePassword(KapuaSecurityUtils.getSession().getScopeId(), KapuaSecurityUtils.getSession().getUserId(), passwordChangeRequest);
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @When("I reset the last created credential password, with the new password {string}")
    public void resetUserPasswordCredentialPassword(String newPassword) throws Exception {
        primeException();

        PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setNewPassword(newPassword);

        KapuaId credentialId = (KapuaId) stepData.get(BasicSteps.LAST_CREDENTIAL_ID);

        try {
            userCredentialsService.resetPassword(getCurrentScopeId(), credentialId, passwordResetRequest);
        } catch (Exception ex) {
            verifyException(ex);
        }
    }
}
