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
 *     Eurotech - initial API and implementation
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.service.account.steps;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalNullArgumentException;
import org.eclipse.kapua.commons.model.id.IdGenerator;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdImpl;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.kapua.qa.common.TestBase;
import org.eclipse.kapua.qa.common.cucumber.CucAccount;
import org.eclipse.kapua.qa.common.cucumber.CucConfig;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountAttributes;
import org.eclipse.kapua.service.account.AccountCreator;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountListResult;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.account.Organization;
import org.junit.Assert;

import com.google.inject.Singleton;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Implementation of Gherkin steps used in AccountService.feature scenarios.
 * <p>
 * MockedLocator is used for Location Service. Mockito is used to mock other services that the Account services dependent on. Dependent services are: - Authorization Service
 */
@Singleton
public class AccountServiceSteps extends TestBase {

    protected KapuaLocator locator;

    private static final String ACCOUNT_CREATOR = "AccountCreator";
    private static final String LAST_ACCOUNT = "LastAccount";
    private static final String LAST_ACCOUNT_ID = "LastAccountId";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TMP_ACC = "tmp_acc_";
    private static final String INT_VALUE = "IntValue";

    // Account creator object used for creating new accounts.
    private AccountService accountService;
    private AccountFactory accountFactory;

    // Default constructor
    @Inject
    public AccountServiceSteps(StepData stepData) {
        super(stepData);
    }

    @After(value = "@setup")
    public void setServices() {
        locator = KapuaLocator.getInstance();
        accountFactory = locator.getFactory(AccountFactory.class);
        accountService = locator.getService(AccountService.class);
    }

    // *************************************
    // Definition of Cucumber scenario steps
    // *************************************

    // Setup and tear-down steps

    @Before(value = "@env_docker or @env_docker_base or @env_none", order = 10)
    public void beforeScenarioNone(Scenario scenario) {
        updateScenario(scenario);
    }

    // The Cucumber test steps

    @Given("^An account creator with the name \"(.*)\"$")
    public void prepareTestAccountCreatorWithName(String name) {

        AccountCreator accountCreator = prepareRegularAccountCreator(SYS_SCOPE_ID, name);
        stepData.put(ACCOUNT_CREATOR, accountCreator);
    }

    @Given("Account")
    public void givenAccount(List<CucAccount> accountList) throws Exception {
        CucAccount cucAccount = accountList.get(0);
        // If accountId is not set in account list, use last created Account for scope id
        if (cucAccount.getScopeId() == null) {
            cucAccount.setScopeId(((Account) stepData.get(LAST_ACCOUNT)).getId().getId());
        }

        Account tmpAccount = createAccount(cucAccount);
        stepData.put(LAST_ACCOUNT, tmpAccount);
        if (tmpAccount != null) {
            stepData.put(LAST_ACCOUNT_ID, tmpAccount.getId());
        }
    }

    @When("I create a generic account with name {string}")
    public void createGenericAccount(String name) throws Exception {
        AccountCreator accountCreator = prepareRegularAccountCreator(SYS_SCOPE_ID, name);
        stepData.put(ACCOUNT_CREATOR, accountCreator);
        stepData.remove(LAST_ACCOUNT);
        stepData.remove(LAST_ACCOUNT_ID);
        try {
            primeException();
            Account account = accountService.create(accountCreator);
            stepData.put(LAST_ACCOUNT, account);
            stepData.put(LAST_ACCOUNT_ID, account.getId());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I create a generic account with name {string} in current scopeId")
    public void createGenericAccountInCurrentScopeId(String name) throws Exception {
        AccountCreator accountCreator = prepareRegularAccountCreator(getCurrentScopeId(), name);
        stepData.put("AccountCreator", accountCreator);
        stepData.remove(LAST_ACCOUNT);
        stepData.remove(LAST_ACCOUNT_ID);
        try {
            primeException();
            Account account = accountService.create(accountCreator);
            stepData.put(LAST_ACCOUNT, account);
            stepData.put(LAST_ACCOUNT_ID, account.getId());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I create {int} accounts in current scopeId")
    public void createAccountsInCurrentScopeId(int numberOfAccounts) throws Exception {
        for (int i = 0; i < numberOfAccounts; i++) {
            AccountCreator accountCreator = prepareRegularAccountCreator(getCurrentScopeId(), "account" + i);
            try {
                primeException();
                Account account = accountService.create(accountCreator);
            } catch (KapuaException ex) {
                verifyException(ex);
            }
        }
    }

    @Given("An existing account that expires on {string} with the name {string}")
    public void createTestAccountWithName(String expirationDateStr, String name) throws Exception {
        Date expirationDate = new SimpleDateFormat(DATE_FORMAT).parse(expirationDateStr);
        AccountCreator accountCreator = prepareRegularAccountCreator(SYS_SCOPE_ID, name);
        accountCreator.setExpirationDate(expirationDate);
        stepData.put(ACCOUNT_CREATOR, accountCreator);
        stepData.remove(LAST_ACCOUNT);
        stepData.remove(LAST_ACCOUNT_ID);
        try {
            primeException();
            Account account = accountService.create(accountCreator);
            stepData.put(LAST_ACCOUNT, account);
            stepData.put(LAST_ACCOUNT_ID, account.getId());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @Given("I create {int} childs for account with Id {int}")
    public void createANumberOfAccounts(int num, int parentId) throws Exception {
        for (int i = 0; i < num; i++) {
            AccountCreator accountCreator = prepareRegularAccountCreator(new KapuaEid(BigInteger.valueOf(parentId)), TMP_ACC + String.format("%d", i));
            try {
                primeException();
                accountService.create(accountCreator);
            } catch (KapuaException ex) {
                verifyException(ex);
                break;
            }
        }
    }

    @Given("I create {int} childs for account with name {string}")
    public void createANumberOfChildrenForAccountWithName(int num, String name) throws Exception {
        Account tmpAcc = accountService.findByName(name);
        for (int i = 0; i < num; i++) {
            AccountCreator accountCreator = prepareRegularAccountCreator(tmpAcc.getId(), TMP_ACC + String.format("%d", i));
            try {
                primeException();
                accountService.create(accountCreator);
            } catch (KapuaException ex) {
                verifyException(ex);
                break;
            }
        }
    }

    @Given("I create {int} childs for account with expiration date {string} and name {string}")
    public void createANumberOfChildrenForAccountWithName(int num, String expirationDateStr, String name) throws Exception {
        Account tmpAcc = accountService.findByName(name);
        for (int i = 0; i < num; i++) {
            Date expirationDate = new SimpleDateFormat(DATE_FORMAT).parse(expirationDateStr);
            AccountCreator accountCreator = prepareRegularAccountCreator(tmpAcc.getId(), TMP_ACC + String.format("%d", i));
            accountCreator.setExpirationDate(expirationDate);
            try {
                primeException();
                accountService.create(accountCreator);
            } catch (KapuaException ex) {
                verifyException(ex);
                break;
            }
        }
    }

    @Given("I create {int} accounts with organization name {string}")
    public void createANumberOfChildrenForAccountWithOrganizationName(int num, String name) throws Exception {
        for (int i = 0; i < num; i++) {
            AccountCreator accountCreator = prepareRegularAccountCreator(SYS_SCOPE_ID, TMP_ACC + String.format("%d", i));
            accountCreator.setOrganizationName(name);
            try {
                primeException();
                accountService.create(accountCreator);
            } catch (KapuaException ex) {
                verifyException(ex);
                break;
            }
        }
    }

    @When("I create an account with a null name")
    public void createAccountWithNullName() throws Exception {
        AccountCreator accountCreator = prepareRegularAccountCreator(SYS_SCOPE_ID, null);
        try {
            primeException();
            accountService.create(accountCreator);
        } catch (KapuaIllegalNullArgumentException ex) {
            verifyException(ex);
        }
    }

    @When("I modify the account {string}")
    public void changeAccountDetails(String name) throws Exception {
        try {
            primeException();

            Account account = accountService.findByName(name);
            Organization tmpOrg = account.getOrganization();

            // Change an organization detail
            tmpOrg.setName(tmpOrg.getName() + "_xx");
            tmpOrg.setCity(tmpOrg.getCity() + "_xx");
            account.setOrganization(tmpOrg);
            stepData.put(LAST_ACCOUNT, account);

            accountService.update(account);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I modify the current account")
    public void updateAccount() throws Exception {
        Account account = (Account) stepData.get(LAST_ACCOUNT);

        try {
            primeException();
            accountService.update(account);
        } catch (KapuaEntityNotFoundException ex) {
            verifyException(ex);
        }
    }

    @When("I change the expiration date of the account {string} to {string}")
    public void changeAccountExpirationDate(String accName, String expirationDateStr) throws Exception {
        Account tmpAcc = accountService.findByName(accName);
        Date expirationDate;
        if (expirationDateStr.equals("never")) {
            expirationDate = null;
        } else {
            expirationDate = new SimpleDateFormat("yyyy-DD-mm").parse(expirationDateStr);
        }
        tmpAcc.setExpirationDate(expirationDate);
        try {
            primeException();
            accountService.update(tmpAcc);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I change the scope Id for account {string} to {int}")
    public void changeParentPathForAccount(String name, int scopeId) throws Exception {
        Account tmpAcc = accountService.findByName(name);
        tmpAcc.setScopeId(new KapuaIdImpl(BigInteger.valueOf(scopeId)));

        try {
            primeException();
            accountService.update(tmpAcc);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I change the parent path for account {string}")
    public void changeParentPathForAccount(String name) throws Exception {
        Account tmpAcc = accountService.findByName(name);
        String modParentPath = tmpAcc.getParentAccountPath() + "/mod";
        tmpAcc.setParentAccountPath(modParentPath);

        try {
            primeException();
            accountService.update(tmpAcc);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I select account {string}")
    public void selectAccount(String accountName) throws Exception {
        try {
            Account tmpAccount;
            tmpAccount = accountService.findByName(accountName);
            if (tmpAccount != null) {
                stepData.put(LAST_ACCOUNT, tmpAccount);
                stepData.put(LAST_ACCOUNT_ID, tmpAccount.getId());
            } else {
                stepData.remove(LAST_ACCOUNT);
            }
        } catch (KapuaException e) {
            verifyException(e);
        }
    }

    @When("I change the current account expiration date to {string}")
    public void changeCurrentAccountExpirationDate(String newExpiration) throws Exception {
        Account currAcc = (Account) stepData.get(LAST_ACCOUNT);
        Date newDate = parseDateString(newExpiration);

        try {
            primeException();
            currAcc.setExpirationDate(newDate);
            Account tmpAcc = accountService.update(currAcc);
            stepData.put(LAST_ACCOUNT, tmpAcc);
        } catch (KapuaException e) {
            verifyException(e);
        }
    }

    @When("I delete account {string}")
    public void deleteAccountWithName(String name) throws Exception {
        try {
            primeException();
            Account tmpAcc = accountService.findByName(name);
            accountService.delete(tmpAcc.getScopeId(), tmpAcc.getId());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I try to delete the system account")
    public void deleteSystemAccount() throws Exception {
        String adminAccountName = SystemSetting.getInstance().getString(SystemSettingKey.SYS_ADMIN_ACCOUNT);
        Account adminAccount = accountService.findByName(adminAccountName);

        Assert.assertNotNull(adminAccount);
        Assert.assertNotNull(adminAccount.getId());

        try {
            primeException();
            accountService.delete(KapuaId.ANY, adminAccount.getId());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I delete a random account")
    public void deleteRandomAccount() throws Exception {
        try {
            primeException();
            accountService.delete(SYS_SCOPE_ID, new KapuaEid(IdGenerator.generate()));
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I search for the account with name {string}")
    public void findAccountByName(String name) throws Exception {
        try {
            primeException();
            stepData.remove(LAST_ACCOUNT);
            Account account = accountService.findByName(name);
            stepData.put(LAST_ACCOUNT, account);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I search for the account with the remembered account Id")
    public void findAccountByStoredId() throws Exception {
        try {
            primeException();
            stepData.remove(LAST_ACCOUNT);
            KapuaId accountId = (KapuaId) stepData.get(LAST_ACCOUNT_ID);
            Account account = accountService.find(accountId);
            stepData.put(LAST_ACCOUNT, account);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I search for the account with the remembered parent and account Ids")
    public void findAccountByStoredScopeAndAccountIds() throws Exception {
        try {
            primeException();
            stepData.remove(LAST_ACCOUNT);
            KapuaId accountId = (KapuaId) stepData.get(LAST_ACCOUNT_ID);
            Account account = accountService.find(SYS_SCOPE_ID, accountId);
            stepData.put(LAST_ACCOUNT, account);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I search for a random account Id")
    public void findRandomAccountId() throws Exception {
        try {
            primeException();
            stepData.remove(LAST_ACCOUNT);
            Account account = accountService.find(SYS_SCOPE_ID, new KapuaEid(IdGenerator.generate()));
            stepData.put(LAST_ACCOUNT, account);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I set the following parameters")
    public void setAccountParameters(DataTable dataTable) throws Exception {
        Assert.assertEquals("Wrong test setup. Bad parameters size!", 2, dataTable.width());
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        Properties accProps = account.getEntityProperties();

        for (List<String> row : dataTable.asLists()) {
            accProps.setProperty(row.get(0), row.get(1));
        }
        account.setEntityProperties(accProps);

        try {
            primeException();
            account = accountService.update(account);
            stepData.put(LAST_ACCOUNT, account);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I configure {string} item {string} to {string}")
    public void setConfigurationValue(String type, String name, String value) throws Exception {
        Map<String, Object> valueMap = new HashMap<>();

        switch (type) {
        case "integer":
            valueMap.put(name, Integer.valueOf(value));
            break;
        case "string":
            valueMap.put(name, value);
            break;
        default:
            break;
        }
        valueMap.put("infiniteChildEntities", true);

        Account account = (Account) stepData.get(LAST_ACCOUNT);

        try {
            primeException();
            accountService.setConfigValues(account.getId(), account.getScopeId(), valueMap);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I add the unknown config item {string} with value {int}")
    public void addUnknownIntegerConfigurationValue(String name, int value) throws Exception {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        try {
            primeException();
            Map<String, Object> valuesRead = accountService.getConfigValues(account.getId());
            valuesRead.put(name, value);
            accountService.setConfigValues(account.getId(), account.getScopeId(), valuesRead);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I query for all accounts that have the system account as parent")
    public void queryForNumberOfTopLevelAccounts() throws Exception {
        KapuaQuery query = new KapuaQuery(SYS_SCOPE_ID);
        stepData.remove(INT_VALUE);
        try {
            primeException();
            AccountListResult accList = accountService.query(query);
            stepData.put(INT_VALUE, accList.getSize());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I set the expiration date to {string}")
    public void setExpirationDate(String expirationDateStr) throws Exception {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        stepData.remove(LAST_ACCOUNT);
        try {
            primeException();
            Date expirationDate = new SimpleDateFormat(DATE_FORMAT).parse(expirationDateStr);
            account.setExpirationDate(expirationDate);
            account = accountService.update(account);
            stepData.put(LAST_ACCOUNT, account);
        } catch (KapuaException | ParseException ex) {
            verifyException(ex);
        }
    }

    @Then("The account matches the creator settings")
    public void checkCreatedAccountDefaults() {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        AccountCreator accountCreator = (AccountCreator) stepData.get(ACCOUNT_CREATOR);

        Assert.assertNotNull(account);
        Assert.assertNotNull(account.getId());
        Assert.assertNotNull(account.getId().getId());
        Assert.assertTrue(account.getOptlock() >= 0);
        Assert.assertEquals(SYS_SCOPE_ID, account.getScopeId());
        Assert.assertNotNull(account.getCreatedOn());
        Assert.assertNotNull(account.getCreatedBy());
        Assert.assertNotNull(account.getModifiedOn());
        Assert.assertNotNull(account.getModifiedBy());
        Assert.assertNotNull(account.getOrganization());
        Assert.assertEquals(accountCreator.getOrganizationName(), account.getOrganization().getName());
        Assert.assertEquals(accountCreator.getOrganizationPersonName(), account.getOrganization().getPersonName());
        Assert.assertEquals(accountCreator.getOrganizationCountry(), account.getOrganization().getCountry());
        Assert.assertEquals(accountCreator.getOrganizationStateProvinceCounty(), account.getOrganization().getStateProvinceCounty());
        Assert.assertEquals(accountCreator.getOrganizationCity(), account.getOrganization().getCity());
        Assert.assertEquals(accountCreator.getOrganizationAddressLine1(), account.getOrganization().getAddressLine1());
        Assert.assertEquals(accountCreator.getOrganizationAddressLine2(), account.getOrganization().getAddressLine2());
        Assert.assertEquals(accountCreator.getOrganizationEmail(), account.getOrganization().getEmail());
        Assert.assertEquals(accountCreator.getOrganizationZipPostCode(), account.getOrganization().getZipPostCode());
        Assert.assertEquals(accountCreator.getOrganizationPhoneNumber(), account.getOrganization().getPhoneNumber());
    }

    @Then("Account {string} exists")
    public void checkWhetherAccountExists(String name) throws KapuaException {
        Account tmpAcc = accountService.findByName(name);
        Assert.assertNotNull(tmpAcc);
    }

    @Then("Account {string} is correctly modified")
    public void checkForAccountModifications(String name) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        Account tmpAcc = accountService.findByName(name);

        Assert.assertEquals(account.getOrganization().getName(), tmpAcc.getOrganization().getName());
        Assert.assertEquals(account.getOrganization().getCity(), tmpAcc.getOrganization().getCity());
    }

    @Then("The account with name {string} has {int} subaccount(s)")
    public void checkNumberOfAccounts(String accountName, int num) throws KapuaException {
        final KapuaQuery query = new KapuaQuery(getCurrentScopeId());
        Account account = accountService.find(getCurrentScopeId());
        Assert.assertEquals(accountName, account.getName());

        long accountCnt = accountService.count(query);
        Assert.assertEquals(num, accountCnt);
    }

    @Then("Account {string} has {int} children")
    public void checkNumberOfChildrenForNamedAccount(String name, int num) throws Exception {
        try {
            primeException();
            Account tmpAcc = accountService.findByName(name);
            final KapuaQuery query = new KapuaQuery(tmpAcc.getId());
            long accountCnt = accountService.count(query);

            Assert.assertEquals(num, accountCnt);
        } catch (KapuaException ke) {
            verifyException(ke);
        }
    }

    @Then("The account does not exist")
    public void tryToFindInexistentAccount() {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        Assert.assertNull(account);
    }

    @Then("The System account exists")
    public void findSystemAccount() throws KapuaException {
        String adminUserName = SystemSetting.getInstance().getString(SystemSettingKey.SYS_ADMIN_USERNAME);
        Account tmpAcc = accountService.findByName(adminUserName);
        Assert.assertNotNull(tmpAcc);
    }

    @Then("The account has the following parameters")
    public void checkAccountParameters(DataTable dataTable) throws KapuaException {
        Assert.assertEquals("Wrong test setup. Bad parameters size!", 2, dataTable.width());
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        Properties accProps = account.getEntityProperties();

        for (List<String> row : dataTable.asLists()) {
            Assert.assertEquals(row.get(1), accProps.getProperty(row.get(0)));
        }
    }

    @Then("The account has metadata")
    public void checkMetadataExistence() throws KapuaException {
        KapuaId accountId = (KapuaId) stepData.get(LAST_ACCOUNT_ID);
        KapuaTocd metaData = accountService.getConfigMetadata(accountId);
        Assert.assertNotNull(metaData);
    }

    @Then("The default configuration for the account is set")
    public void checkDefaultAccountConfiguration() throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        Map<String, Object> valuesRead = accountService.getConfigValues(account.getId());
        Assert.assertTrue(valuesRead.containsKey("maxNumberChildEntities"));
        Assert.assertEquals(0, valuesRead.get("maxNumberChildEntities"));
    }

    @Then("The config item {string} is set to {string}")
    public void checkConfigValue(String name, String value) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        Map<String, Object> valuesRead = accountService.getConfigValues(account.getId());
        Assert.assertTrue(valuesRead.containsKey(name));
        Assert.assertEquals(value, valuesRead.get(name).toString());
    }

    @Then("The config item {string} is missing")
    public void checkMissingConfigItem(String name) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        Map<String, Object> valuesRead = accountService.getConfigValues(account.getId());
        Assert.assertFalse(valuesRead.containsKey(name));
    }

    @Then("The returned value is {int}")
    public void checkIntegerReturnValue(int val) {
        int intVal = (int) stepData.get(INT_VALUE);
        Assert.assertEquals(val, intVal);
    }

    @When("I configure account service")
    public void setAccountServiceConfig(List<CucConfig> cucConfigs) throws Exception {
        Map<String, Object> valueMap = new HashMap<>();
        KapuaId accId;
        KapuaId scopeId;

        for (CucConfig config : cucConfigs) {
            config.addConfigToMap(valueMap);
        }

        primeException();
        try {
            Account tmpAccount = (Account) stepData.get(LAST_ACCOUNT);
            if (tmpAccount != null) {
                accId = tmpAccount.getId();
                scopeId = SYS_SCOPE_ID;
            } else {
                accId = SYS_SCOPE_ID;
                scopeId = SYS_SCOPE_ID;
            }
            accountService.setConfigValues(accId, scopeId, valueMap);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    // *******************
    // * Private Helpers *
    // *******************

    /**
     * Create a user creator object. The creator is pre-filled with default data.
     *
     * @param parentId
     *         Id of the parent account
     * @param name
     *         The name of the account
     * @return The newly created account creator object.
     */
    private AccountCreator prepareRegularAccountCreator(KapuaId parentId, String name) {
        AccountCreator tmpAccCreator = accountFactory.newCreator(parentId);

        tmpAccCreator.setName(name);
        tmpAccCreator.setOrganizationName("org_" + name);
        tmpAccCreator.setOrganizationPersonName(String.format("person_%s", name));
        tmpAccCreator.setOrganizationCountry("home_country");
        tmpAccCreator.setOrganizationStateProvinceCounty("home_province");
        tmpAccCreator.setOrganizationCity("home_city");
        tmpAccCreator.setOrganizationAddressLine1("address_line_1");
        tmpAccCreator.setOrganizationAddressLine2("address_line_2");
        tmpAccCreator.setOrganizationEmail("org_" + name + "@org.com");
        tmpAccCreator.setOrganizationZipPostCode("1234");
        tmpAccCreator.setOrganizationPhoneNumber("012/123-456-789");

        return tmpAccCreator;
    }

    /**
     * Create account in privileged mode as kapua-sys user. Account is created in scope specified by scopeId in cucAccount parameter. This is not accountId, but account under which it is created.
     * AccountId itself is created automatically.
     *
     * @param cucAccount
     *         basic data about account
     * @return Kapua Account object
     */
    private Account createAccount(CucAccount cucAccount) throws Exception {
        List<Account> accountList = new ArrayList<>();
        KapuaSecurityUtils.doPrivileged(() -> {
            primeException();
            try {
                Account account = accountService.create(accountCreatorCreator(cucAccount.getName(),
                        cucAccount.getScopeId(), cucAccount.getExpirationDate()));
                accountList.add(account);
            } catch (KapuaException ke) {
                verifyException(ke);
            }

            return null;
        });

        return accountList.size() == 1 ? accountList.get(0) : null;
    }

    /**
     * Create account creator.
     *
     * @param name
     *         account name
     * @param scopeId
     *         acount scope id
     * @return
     */
    private AccountCreator accountCreatorCreator(String name, BigInteger scopeId, Date expiration) {

        AccountCreator accountCreator = accountFactory.newCreator(new KapuaEid(scopeId));
        accountCreator.setName(name);
        accountCreator.setOrganizationName("ACME Inc.");
        accountCreator.setOrganizationEmail("some@one.com");

        if (expiration != null) {
            accountCreator.setExpirationDate(expiration);
        }

        return accountCreator;
    }

    @And("I find account with name {string}")
    public void iFindAccountWithName(String accountName) throws Exception {
        final KapuaQuery accountQuery = new KapuaQuery(getCurrentScopeId());
        accountQuery.setPredicate(accountQuery.attributePredicate(AccountAttributes.NAME, accountName));
        AccountListResult accountListResult = accountService.query(accountQuery);
        Assert.assertTrue(accountListResult.getSize() > 0);
    }

    @And("I try to edit description to {string}")
    public void iTryToEditAccountWithName(String description) throws Exception {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        account.setDescription(description);

        try {
            primeException();
            accountService.update(account);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @And("I create an account with name {string}, organization name {string} and email address {string}")
    public void iCreateAAccountWithNameOrganizationNameAndEmailaddress(String accountName, String organizationName, String email) throws Exception {
        AccountCreator accountCreator = accountFactory.newCreator(getCurrentScopeId());
        accountCreator.setName(accountName);
        accountCreator.setOrganizationName(organizationName);
        accountCreator.setOrganizationEmail(email);

        try {
            primeException();
            stepData.remove(LAST_ACCOUNT);
            stepData.remove(LAST_ACCOUNT);
            Account account = accountService.create(accountCreator);
            stepData.put(LAST_ACCOUNT, account);
            stepData.put(LAST_ACCOUNT, account);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I look for my account by id")
    public void findMyAccountById() throws Exception {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        Account selfAccount = accountService.find(account.getId());
        stepData.put(LAST_ACCOUNT, selfAccount);
    }

    @When("I look for my account by id and scope id")
    public void findMyAccountByIdAndScopeId() throws Exception {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        Account selfAccount = accountService.find(account.getId(), account.getId());
        stepData.put(LAST_ACCOUNT, selfAccount);
    }

    @When("I look for my account by name")
    public void findMyAccountByName() throws Exception {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        Account selfAccount = accountService.findByName(account.getName());
        stepData.put(LAST_ACCOUNT, selfAccount);
    }

    @Then("I am able to read my account info")
    public void verifySelfAccount() throws Exception {
        Assert.assertNotNull(stepData.get(LAST_ACCOUNT));
    }

    @And("I create an account with name {string}, organization name {string} and email address {string} and child account")
    public void iCreateAccountWithNameOrganizationNameAndEmailaddressAndChildAccount(String accountName, String organizationName, String email) throws Exception {
        Account lastAccount = (Account) stepData.get(LAST_ACCOUNT);

        AccountCreator accountCreator = accountFactory.newCreator(lastAccount.getId());
        accountCreator.setName(accountName);
        accountCreator.setOrganizationName(organizationName);
        accountCreator.setOrganizationEmail(email);

        try {
            primeException();
            stepData.remove(LAST_ACCOUNT);
            Account account = accountService.create(accountCreator);
            stepData.put(LAST_ACCOUNT, account);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I query for all sub-accounts in {string}")
    public void queryForAllAccountsInCurrentScopeId(String accountName) throws Exception {
        Account tmpAccount = accountService.findByName(accountName);
        final KapuaQuery query = new KapuaQuery(tmpAccount.getId());
        try {
            primeException();
            AccountListResult accList = accountService.query(query);
            stepData.put("NumberOfFoundAccounts", accList.getSize());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I find {int} account(s)")
    public void iFindAccounts(int numberOfAccounts) {
        int foundAccounts = (int) stepData.get("NumberOfFoundAccounts");
        Assert.assertEquals(foundAccounts, numberOfAccounts);
    }

}
