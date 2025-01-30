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
 *     Eurotech
 *******************************************************************************/
package org.eclipse.kapua.service.user.steps;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdImpl;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.qa.common.BasicSteps;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.kapua.qa.common.TestBase;
import org.eclipse.kapua.qa.common.cucumber.CucConfig;
import org.eclipse.kapua.qa.common.cucumber.CucCredentials;
import org.eclipse.kapua.qa.common.cucumber.CucPermission;
import org.eclipse.kapua.qa.common.cucumber.CucUser;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.LoginCredentials;
import org.eclipse.kapua.service.authentication.UsernamePasswordCredentials;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.credential.CredentialAttributes;
import org.eclipse.kapua.service.authentication.credential.CredentialCreator;
import org.eclipse.kapua.service.authentication.credential.CredentialListResult;
import org.eclipse.kapua.service.authentication.credential.CredentialService;
import org.eclipse.kapua.service.authentication.credential.CredentialStatus;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOption;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOptionCreator;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOptionFactory;
import org.eclipse.kapua.service.authentication.credential.mfa.MfaOptionService;
import org.eclipse.kapua.service.authentication.credential.mfa.shiro.MfaOptionFactoryImpl;
import org.eclipse.kapua.service.authorization.access.AccessInfoCreator;
import org.eclipse.kapua.service.authorization.access.AccessInfoService;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.access.AccessPermissionAttributes;
import org.eclipse.kapua.service.authorization.access.AccessPermissionService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.authorization.permission.shiro.PermissionImpl;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserAttributes;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserFactory;
import org.eclipse.kapua.service.user.UserListResult;
import org.eclipse.kapua.service.user.UserQuery;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.service.user.UserStatus;
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
 * Implementation of Gherkin steps used in user test scenarios.
 * <p>
 * Mockito is used to mock other services that UserService is dependent on.
 */
@Singleton
public class UserServiceSteps extends TestBase {

    private static final String USER_CREATOR = "UserCreator";
    private static final String USER_LIST = "UserList";
    private static final String USER_NOT_FOUND = "User %1s was not found!";
    private static final String LAST_ACCOUNT = "LastAccount";
    private static final String METADATA = "Metadata";
    private static final String LAST_USER = "LastUser";
    private static final String LAST_FOUND_ACCESS_PERMISSION = "LastFoundAccessPermission";
    private static final String LAST_PERMISSION_ADDED_TO_USER = "LastPermissionAddedToUser";
    private static final String FOUND_USERS = "FoundUsers";

    /**
     * User service by locator.
     */
    private UserService userService;

    /**
     * User factory by locator.
     */
    private UserFactory userFactory;

    /**
     * Security services services by locator.
     */
    private AccessInfoService accessInfoService;
    private AuthenticationService authenticationService;
    private PermissionFactory permissionFactory;
    private CredentialService credentialService;
    private AccessPermissionService accessPermissionService;

    @Inject
    public UserServiceSteps(StepData stepData) {
        super(stepData);
    }

    // *************************************
    // Definition of Cucumber scenario steps
    // *************************************

    @After(value = "@setup")
    public void setServices() {
        KapuaLocator locator = KapuaLocator.getInstance();
        userService = locator.getService(UserService.class);
        userFactory = locator.getFactory(UserFactory.class);
        authenticationService = locator.getService(AuthenticationService.class);
        credentialService = locator.getService(CredentialService.class);
        accessInfoService = locator.getService(AccessInfoService.class);
        permissionFactory = locator.getFactory(PermissionFactory.class);
        accessPermissionService = locator.getService(AccessPermissionService.class);
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        updateScenario(scenario);
    }

    @Given("User with name {string} in scope with id {int}")
    public void crateUserWithName(String userName, int scopeId) {
        long now = (new Date()).getTime();
        String userEmail = MessageFormat.format("testuser_{0,number,#}@organization.com", now);
        String displayName = MessageFormat.format("User Display Name {0}", now);
        KapuaEid scpId = new KapuaEid(BigInteger.valueOf(scopeId));
        UserCreator uc = new UserCreator(scpId, userName);
        uc.setDisplayName(displayName);
        uc.setEmail(userEmail);
        uc.setPhoneNumber("+1 555 123 4567");
        uc.setStatus(UserStatus.ENABLED);
        stepData.put(USER_CREATOR, uc);
        scenario.log("User " + userName + " created.");
    }

    @When("I create user")
    public void createUser() throws Exception {
        stepData.remove("User");
        User user = userService.create((UserCreator) stepData.get(USER_CREATOR));
        stepData.put("User", user);
    }

    @Given("An invalid user")
    public void provideInvalidUserObject() {
        User user = userFactory.newEntity(getKapuaId());
        user.setId(getKapuaId());
        user.setName(getKapuaId().toString());
        stepData.put("User", user);
    }

    @When("I change name to {string}")
    public void changeUserName(String userName) throws Exception {
        User user = (User) stepData.get("User");
        user.setName(userName);
        user = userService.update(user);
        stepData.put("User", user);
    }

    @When("I change user to")
    public void changeUserTo(List<CucUser> userList) throws Exception {
        User user = (User) stepData.get("User");
        try {
            for (CucUser userItem : userList) {
                user.setName(userItem.getName());
                user.setDisplayName(userItem.getDisplayName());
                user.setEmail(userItem.getEmail());
                user.setPhoneNumber(userItem.getPhoneNumber());
                user.setStatus(userItem.getStatus());
                user = userService.update(user);
            }
            stepData.put("User", user);
        } catch (KapuaException kapuaException) {
            verifyException(kapuaException);
        }
    }

    @When("I delete user")
    public void deleteUser() throws Exception {
        try {
            primeException();
            User user = (User) stepData.get("User");
            userService.delete(user.getScopeId(), user.getId());
        } catch (KapuaException ke) {
            verifyException(ke);
        }
    }

    @When("I search for user with name {string}")
    public void searchUserWithName(String userName) throws Exception {
        stepData.remove("User");
        primeException();
        try {
            User user = userService.findByName(userName);
            stepData.put("User", user);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I search for users")
    public void searchForUsers() throws Exception {
        KapuaId scpId = DEFAULT_ID;
        Set<ComparableUser> iFoundUsers;
        stepData.remove(USER_LIST);
        KapuaQuery query = new UserQuery(scpId);
        UserListResult queryResult = userService.query(query);
        iFoundUsers = new HashSet<>();
        List<User> users = queryResult.getItems();
        for (User userItem : users) {
            iFoundUsers.add(new ComparableUser(userItem));
        }
        stepData.put(USER_LIST, iFoundUsers);
    }

    @Then("I find user with name {string}")
    public void findUserWithName(String userName) throws Exception {
        UserCreator userCreator = (UserCreator) stepData.get(USER_CREATOR);
        User user = (User) stepData.get("User");
        Assert.assertNotNull(user.getId());
        Assert.assertNotNull(user.getId().getId());
        Assert.assertTrue(user.getOptlock() >= 0);
        Assert.assertNotNull(user.getScopeId());
        Assert.assertEquals(userName, user.getName());
        Assert.assertNotNull(user.getCreatedOn());
        Assert.assertNotNull(user.getCreatedBy());
        Assert.assertNotNull(user.getModifiedOn());
        Assert.assertNotNull(user.getModifiedBy());
        Assert.assertEquals(userCreator.getDisplayName(), user.getDisplayName());
        Assert.assertEquals(userCreator.getEmail(), user.getEmail());
        Assert.assertEquals(userCreator.getPhoneNumber(), user.getPhoneNumber());
        Assert.assertEquals(UserStatus.ENABLED, user.getStatus());
    }

    @Then("I don't find user with name {string}")
    public void dontFindUserWithName(String userName) throws Exception {
        User user = userService.findByName(userName);
        Assert.assertNull(user);
    }

    @Then("I find user")
    public void findUserFull(List<CucUser> userList) {
        User user = (User) stepData.get("User");
        for (CucUser userItem : userList) {
            matchUserData(user, userItem);
        }
    }

    @Then("I find users")
    public void findUsersFull(List<CucUser> userList) {
        Set<ComparableUser> iFoundUsers = (Set<ComparableUser>) stepData.get(USER_LIST);
        boolean userChecks;
        for (CucUser userItem : userList) {
            userChecks = false;
            for (ComparableUser foundUserItem : iFoundUsers) {
                if (foundUserItem.getUser().getName().equals(userItem.getName())) {
                    matchUserData(foundUserItem.getUser(), userItem);
                    userChecks = true;
                    break;
                }
            }
            if (!userChecks) {
                Assert.fail(String.format(USER_NOT_FOUND, userItem.getName()));
            }
        }
    }

    @Then("I find no user")
    public void noUserFound() {
        Assert.assertNull(stepData.get("User"));
    }

    @When("I search for user with id {int} in scope with id {int}")
    public void searchUserWithIdAndScopeId(int userId, int scopeId) throws Exception {
        KapuaEid scpId = new KapuaEid(BigInteger.valueOf(scopeId));
        KapuaEid usrId = new KapuaEid(BigInteger.valueOf(userId));
        stepData.remove("User");
        User user = userService.find(scpId, usrId);
        stepData.put("User", user);
    }

    @When("I search for created user by id")
    public void searchUserById() throws Exception {
        User user = (User) stepData.get("User");
        user = userService.find(user.getId(), user.getScopeId());
        stepData.put("User", user);
    }

    @When("I search for created user by name")
    public void searchUserByName() throws Exception {
        User user = (User) stepData.get("User");
        user = userService.findByName(user.getName());
        stepData.put("User", user);
    }

    @When("I query for users in scope with id {int}")
    public void queryForUsers(int scopeId) throws Exception {
        stepData.remove(USER_LIST);
        UserQuery query = new UserQuery(new KapuaEid(BigInteger.valueOf(scopeId)));
        UserListResult queryResult = userService.query(query);
        Set<ComparableUser> iFoundUsers = new HashSet<>();
        for (User userItem : queryResult.getItems()) {
            iFoundUsers.add(new ComparableUser(userItem));
        }
        stepData.put(USER_LIST, iFoundUsers);
    }

    @When("I count users in scope {int}")
    public void countUsersInScope(int scopeId) throws Exception {
        UserQuery query = new UserQuery(new KapuaEid(BigInteger.valueOf(scopeId)));
        stepData.updateCount((int) userService.count(query));
    }

    @Then("I count {int} (user/users)")
    public void countUserCount(int cnt) {
        Assert.assertEquals(cnt, stepData.getCount());
    }

    @Then("I count {int} (user/users) as query result list")
    public void countUserQuery(int cnt) {
        Set<ComparableUser> userLst = (Set<ComparableUser>) stepData.get(USER_LIST);
        Assert.assertEquals(cnt, userLst.size());
    }

    @Then("I create same user")
    public void createSameUser() throws Exception {
        try {
            primeException();
            userService.create((UserCreator) stepData.get(USER_CREATOR));
        } catch (KapuaException ke) {
            verifyException(ke);
        }
    }

    @Given("User that doesn't exist")
    public void createNonexistentUser() {
        stepData.remove("User");
        User user = createUserInstance(3234123, 1354133);
        stepData.put("User", user);
    }

    @When("I update nonexistent user")
    public void updateNonexistenUser() throws Exception {
        User user = (User) stepData.get("User");
        try {
            primeException();
            userService.update(user);
        } catch (KapuaException ke) {
            verifyException(ke);
        }
    }

    @When("I delete nonexistent user")
    public void deleteNonexistenUser() throws Exception {
        User user = (User) stepData.get("User");
        try {
            primeException();
            userService.delete(user.getScopeId(), user.getId());
        } catch (KapuaException ke) {
            verifyException(ke);
        }
    }

    @Given("I have the following (user/users)")
    public void haveUsers(List<CucUser> userList) throws Exception {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        KapuaId accountId = (KapuaId) stepData.get("LastAccountId");
        KapuaId currentAccount;
        Set<ComparableUser> iHaveUsers = new HashSet<>();
        User lastUser = null;
        stepData.remove(USER_LIST);
        if (account != null) {
            currentAccount = account.getId();
        } else if (accountId != null) {
            currentAccount = accountId;
        } else {
            currentAccount = DEFAULT_ID;
        }
        primeException();
        try {
            for (CucUser userItem : userList) {
                String name = userItem.getName();
                String displayName = userItem.getDisplayName();
                String email = userItem.getEmail();
                String phone = userItem.getPhoneNumber();
                Date expirationDate = userItem.getExpirationDate();
                UserCreator userCreator;
                if (expirationDate == null) {
                    userCreator = userCreatorCreator(name, displayName, email, phone, currentAccount);
                } else {
                    userCreator = userCreatorCreator(name, displayName, email, phone, currentAccount, expirationDate);
                }
                User user = userService.create(userCreator);
                iHaveUsers.add(new ComparableUser(user));
                lastUser = user;
            }
        } catch (KapuaException ke) {
            verifyException(ke);
        }
        stepData.put(USER_LIST, iHaveUsers);
        stepData.put("User", lastUser);
    }

    @When("I retrieve metadata in scope {int}")
    public void getMetadata(int scopeId) throws KapuaException {
        stepData.remove(METADATA);
        KapuaTocd metadata = userService.getConfigMetadata(getKapuaId(scopeId));
        stepData.put(METADATA, metadata);
    }

    @Then("I have metadata")
    public void haveMetadata() {
        KapuaTocd metadata = (KapuaTocd) stepData.get(METADATA);
        Assert.assertNotNull("Metadata should be retrieved.", metadata);
    }

    @Given("I add credentials")
    public void givenCredentials(List<CucCredentials> credentialsList) throws Exception {
        CucCredentials cucCredentials = credentialsList.get(0);
        createCredentials(cucCredentials);
    }

    @Given("I search for last created user's credentials")
    public void searchForLastCreatedCredentials() throws Exception {
        ComparableUser comparableUser = (ComparableUser) stepData.get(LAST_USER);
        primeException();
        try {
            KapuaQuery credentialQuery = new KapuaQuery(getCurrentScopeId());
            credentialQuery.setPredicate(credentialQuery.attributePredicate(CredentialAttributes.USER_ID, comparableUser.getUser().getId()));
            CredentialListResult credentials = credentialService.query(credentialQuery);
            stepData.put("CredentialsListFound", credentials);
            stepData.put("CredentialsCount", credentials.getSize());
            stepData.put("CredentialFound", credentials.getFirstItem());
        } catch (KapuaException ke) {
            verifyException(ke);
        }
    }

    @Given("I find {int} credentials")
    public void checkCredentialsNumber(int expectedCount) throws Exception {
        int count = (int) stepData.get("CredentialsCount");
        Assert.assertEquals(expectedCount, count);
    }

    @Given("I delete the last created user's credential")
    public void deleteLastCreatedUsersCredentials() throws Exception {
        Credential credential = (Credential) stepData.get("CredentialFound");
        primeException();
        try {
            credentialService.delete(getCurrentScopeId(), credential.getId());
        } catch (KapuaException ke) {
            verifyException(ke);
        }
    }

    @Given("Add permissions to the last created user")
    public void givenPermissions(List<CucPermission> permissionList) throws Exception {
        createPermissions(permissionList, (ComparableUser) stepData.get(LAST_USER), (Account) stepData.get(LAST_ACCOUNT));
    }

    @Given("Full permissions")
    public void givenFullPermissions() throws Exception {
        createPermissions(null, (ComparableUser) stepData.get(LAST_USER), (Account) stepData.get(LAST_ACCOUNT));
    }

    @Given("I delete the last permission added to the new User")
    public void deletePermissionForUser() throws Exception {
        AccessPermission accessPermission = (AccessPermission) stepData.get(LAST_FOUND_ACCESS_PERMISSION);
        primeException();
        try {
            accessPermissionService.delete(accessPermission.getScopeId(), accessPermission.getId());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @Given("I query for the last permission added to the new User")
    public void queryForLastAddedPermission() throws Exception {
        Permission permission = (Permission) stepData.get(LAST_PERMISSION_ADDED_TO_USER);
        primeException();
        try {
            KapuaQuery query = new KapuaQuery(getCurrentScopeId());
            query.setPredicate(query.attributePredicate(AccessPermissionAttributes.PERMISSION, new PermissionImpl(permission)));
            AccessPermission accessPermission = accessPermissionService.query(query).getFirstItem();
            stepData.put(LAST_FOUND_ACCESS_PERMISSION, accessPermission);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @Given("I find the last permission added to the new user")
    public void checkForNullLastAddedPermission() {
        AccessPermission accessPermission = (AccessPermission) stepData.get(LAST_FOUND_ACCESS_PERMISSION);
        Assert.assertNotNull(accessPermission);
    }

    @Given("User A")
    public void givenUserA(List<CucUser> userList) throws Exception {
        // User is created within account that was last created in steps
        ComparableUser tmpUser = null;
        HashSet<ComparableUser> createdList = createUsersInList(userList, (Account) stepData.get(LAST_ACCOUNT));
        Iterator<ComparableUser> userIterator = createdList.iterator();
        while (userIterator.hasNext()) {
            tmpUser = userIterator.next();
        }
        stepData.put("UserA", tmpUser);
        stepData.put(LAST_USER, tmpUser);
        stepData.put("User", tmpUser.getUser());
    }

    @Given("User B")
    public void givenUserB(List<CucUser> userList) throws Exception {
        // User is created within account that was last created in steps
        ComparableUser tmpUser = null;
        HashSet<ComparableUser> createdList = createUsersInList(userList, (Account) stepData.get(LAST_ACCOUNT));
        Iterator<ComparableUser> userIterator = createdList.iterator();
        while (userIterator.hasNext()) {
            tmpUser = userIterator.next();
        }
        stepData.put("UserB", tmpUser);
        stepData.put(LAST_USER, tmpUser);
        stepData.put("User", tmpUser.getUser());
    }

    @Given("A generic user")
    public void givenGenericUser(List<CucUser> userList) throws Exception {
        // User is created within account that was last created in steps
        ComparableUser tmpUser = null;
        HashSet<ComparableUser> createdList = createUsersInList(userList, (Account) stepData.get(LAST_ACCOUNT));
        Iterator<ComparableUser> userIterator = createdList.iterator();
        while (userIterator.hasNext()) {
            tmpUser = userIterator.next();
        }
        stepData.put(LAST_USER, tmpUser);
        stepData.put("LastUserId", tmpUser.getUser().getId());
        stepData.put("User", tmpUser.getUser());
    }

    @When("I login as user with name {string} and password {string}")
    public void loginUser(String userName, String password) throws Exception {
        LoginCredentials credentials = new UsernamePasswordCredentials(userName, password);
        authenticationService.logout();
        primeException();
        try {
            authenticationService.login(credentials);
        } catch (KapuaException e) {
            verifyException(e);
        }
    }

    @When("I try to login as user with name {string} with wrong password {int} times")
    public void loginUserNTimes(String userName, int n) throws Exception {
        String password = "wrongPassword";
        LoginCredentials credentials = new UsernamePasswordCredentials(userName, password);
        authenticationService.logout();
        for (int i = 0; i < n; i++) {
            primeException();
            try {
                authenticationService.login(credentials);
            } catch (KapuaException e) {
                verifyException(e);
            }
        }
    }

    @Then("I try to delete user {string}")
    public void thenDeleteUser(String userName) throws Exception {
        primeException();
        try {
            User userToDelete = userService.findByName(userName);
            if (userToDelete != null) {
                userService.delete(userToDelete.getScopeId(), userToDelete.getId());
            }
        } catch (KapuaException e) {
            verifyException(e);
        }
    }

    @Then("I find user {string}")
    public void thenIFindUser(String userName) throws Exception {
        primeException();
        try {
            User user = userService.findByName(userName);
            Assert.assertNotNull("User doesn't exist.", user);
        } catch (KapuaException e) {
            verifyException(e);
        }
    }

    @Then("I don't find user {string}")
    public void thenIdontFindUser(String userName) throws Exception {
        primeException();
        try {
            User user = userService.findByName(userName);
            Assert.assertNull("User still exists.", user);
        } catch (KapuaException e) {
            verifyException(e);
        }
    }

    @Given("Move User compact id from step data {string} to {string}")
    public void moveUserCompactIdStepData(String keyFrom, String keyTo) {
        ComparableUser comparableUser = (ComparableUser) stepData.get(keyFrom);
        stepData.put(keyTo, comparableUser.getUser().getId().toCompactId());
    }

    @When("I configure user service")
    public void setUserServiceConfig(List<CucConfig> cucConfigs) throws Exception {
        Map<String, Object> valueMap = new HashMap<>();
        KapuaId accId;
        KapuaId scopeId;
        Account tmpAccount = (Account) stepData.get(LAST_ACCOUNT);
        if (tmpAccount != null) {
            accId = tmpAccount.getId();
            scopeId = tmpAccount.getScopeId();
        } else {
            accId = SYS_SCOPE_ID;
            scopeId = SYS_SCOPE_ID;
        }
        for (CucConfig config : cucConfigs) {
            config.addConfigToMap(valueMap);
        }
        primeException();
        try {
            userService.setConfigValues(accId, scopeId, valueMap);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I configure the user service for the account with the id {int}")
    public void setUserServiceConfig(int accountId, List<CucConfig> cucConfigs) throws Exception {
        Map<String, Object> valueMap = new HashMap<>();
        KapuaId accId = new KapuaEid(BigInteger.valueOf(accountId));
        KapuaId scopeId = SYS_SCOPE_ID;
        for (CucConfig config : cucConfigs) {
            config.addConfigToMap(valueMap);
        }
        primeException();
        try {
            userService.setConfigValues(accId, scopeId, valueMap);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @When("I configure credential service")
    public void setCredentialServiceConfig(List<CucConfig> cucConfigs) throws Exception {
        Map<String, Object> valueMap = new HashMap<>();
        KapuaId accId;
        KapuaId scopeId;
        Account tmpAccount = (Account) stepData.get(LAST_ACCOUNT);
        if (tmpAccount != null) {
            accId = tmpAccount.getId();
            scopeId = tmpAccount.getScopeId();
        } else {
            accId = SYS_SCOPE_ID;
            scopeId = SYS_SCOPE_ID;
        }
        for (CucConfig config : cucConfigs) {
            config.addConfigToMap(valueMap);
        }
        primeException();
        try {
            credentialService.setConfigValues(accId, scopeId, valueMap);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @Then("I logout")
    public void logout() throws KapuaException {
        authenticationService.logout();
    }

    // *******************
    // * Private Helpers *
    // *******************

    /**
     * Extract list of users form step parameter table and create those users in kapua. Operation is performed in privileged mode, without access and authorization checks.
     *
     * @param userList
     *         list of users in step
     * @param account
     *         account in which users are created
     * @return Set of created users as ComparableUser Set
     * @throws Exception
     */
    private HashSet<ComparableUser> createUsersInList(List<CucUser> userList, Account account) throws Exception {
        HashSet<ComparableUser> users = new HashSet<>();
        KapuaSecurityUtils.doPrivileged(() -> {
            primeException();
            try {
                for (CucUser userItem : userList) {
                    String name = userItem.getName();
                    String displayName = userItem.getDisplayName();
                    String email = userItem.getEmail();
                    String phone = userItem.getPhoneNumber();
                    KapuaEid scopeId = (KapuaEid) account.getId();
                    Date expirationDate = userItem.getExpirationDate();
                    UserCreator userCreator = userCreatorCreator(name, displayName, email, phone, scopeId, expirationDate);
                    User user = userService.create(userCreator);
                    users.add(new ComparableUser(user));
                }
            } catch (KapuaException ke) {
                verifyException(ke);
            }
            return null;
        });
        return users;
    }

    /**
     * Create User object with user data filed with quasi random data for user name, email, display name. Scope id and user id is set to test wide id.
     *
     * @param userId
     *         unique user id
     * @param scopeId
     *         user scope id
     * @return User instance
     */
    private User createUserInstance(int userId, int scopeId) {
        long now = (new Date()).getTime();
        String username = MessageFormat.format("aaa_test_username_{0,number,#}", now);
        String userEmail = MessageFormat.format("testuser_{0,number,#}@organization.com", now);
        String displayName = MessageFormat.format("User Display Name {0}", now);
        KapuaEid usrId = new KapuaEid(BigInteger.valueOf(userId));
        KapuaEid scpId = new KapuaEid(BigInteger.valueOf(scopeId));
        User user = userFactory.newEntity(scpId);
        user.setId(usrId);
        user.setName(username);
        user.setDisplayName(displayName);
        user.setEmail(userEmail);
        return user;
    }

    /**
     * Create userCreator instance with full data about user.
     *
     * @return UserCreator instance for creating user
     */
    private UserCreator userCreatorCreator(String name, String displayName, String email, String phone, KapuaId scopeId) {
        UserCreator userCreator = new UserCreator(scopeId, name);
        userCreator.setName(name);
        userCreator.setDisplayName(displayName);
        userCreator.setEmail(email);
        userCreator.setPhoneNumber(phone);
        return userCreator;
    }

    private UserCreator userCreatorCreator(String name, String displayName, String email, String phone, KapuaId scopeId, Date expirationDate) {
        UserCreator userCreator = userCreatorCreator(name, displayName, email, phone, scopeId);
        userCreator.setExpirationDate(expirationDate);
        return userCreator;
    }

    /**
     * Create credentials for specific user, set users password. It finds user by name and sets its password.
     *
     * @param cucCredentials
     *         username and open password
     * @return created credential
     */
    private Credential createCredentials(CucCredentials cucCredentials) throws Exception {
        List<Credential> credentialList = new ArrayList<>();
        primeException();
        try {
            User user = userService.findByName(cucCredentials.getName());
            Credential credential = credentialService.create(credentialCreatorCreator(user.getScopeId(),
                    user.getId(), cucCredentials.getPassword(),
                    cucCredentials.getStatus(), cucCredentials.getExpirationDate()));
            credentialList.add(credential);
            stepData.put(BasicSteps.LAST_CREDENTIAL_ID, credentialList.get(0).getId());
        } catch (KapuaException ke) {
            verifyException(ke);
        }
        return credentialList.size() == 1 ? credentialList.get(0) : null;
    }

    /**
     * Create credential creator for user with password.
     *
     * @param scopeId
     *         scopeId in which user is
     * @param userId
     *         userId for which credetntials are set
     * @param password
     *         open password as credetntials
     * @param status
     *         status of credentials enabled or disabled
     * @param expirationDate
     *         credential expiration date
     * @return credential creator used for creating credentials
     */
    private CredentialCreator credentialCreatorCreator(KapuaId scopeId, KapuaId userId, String password, CredentialStatus status, Date expirationDate) {
        CredentialCreator credentialCreator;
        credentialCreator = new CredentialCreator(scopeId, userId, "PASSWORD", password, status, expirationDate);
        return credentialCreator;
    }

    /**
     * Creates permissions for user with specified account. Permissions are created in priveledged mode.
     *
     * @param permissionList
     *         list of permissions for user, if targetScopeId is not set user scope that is specifed as account
     * @param user
     *         user for whom permissions are set
     * @param account
     *         account in which user is defined
     * @throws Exception
     */
    private void createPermissions(List<CucPermission> permissionList, ComparableUser user, Account account) throws Exception {
        KapuaSecurityUtils.doPrivileged(() -> {
            primeException();
            try {
                accessInfoService.create(accessInfoCreatorCreator(permissionList, user, account));
            } catch (KapuaException ke) {
                verifyException(ke);
            }
            return null;
        });
        return;
    }

    /**
     * Create accessInfoCreator instance with data about user permissions. If target scope is not defined in permission list use account scope.
     *
     * @param permissionList
     *         list of all permissions
     * @param user
     *         user for which permissions are set
     * @param account
     *         that user belongs to
     * @return AccessInfoCreator instance for creating user permissions
     */
    private AccessInfoCreator accessInfoCreatorCreator(List<CucPermission> permissionList, ComparableUser user, Account account) throws KapuaException {
        AccessInfoCreator accessInfoCreator = new AccessInfoCreator(account.getId());
        accessInfoCreator.setUserId(user.getUser().getId());
        accessInfoCreator.setScopeId(user.getUser().getScopeId());
        Set<Permission> permissions = new HashSet<>();
        if (permissionList != null) {
            for (CucPermission cucPermission : permissionList) {
                stepData.remove(LAST_PERMISSION_ADDED_TO_USER);
                Actions action = cucPermission.getAction();
                KapuaId targetScopeId = cucPermission.getTargetScopeId();
                if (targetScopeId == null) {
                    targetScopeId = (KapuaEid) account.getId();
                }
                Permission permission = permissionFactory.newPermission(cucPermission.getDomain(),
                        action, targetScopeId);
                permissions.add(permission);
                stepData.put(LAST_PERMISSION_ADDED_TO_USER, permission);
            }
        } else {
            Permission permission = permissionFactory.newPermission((String) null, null, null);
            permissions.add(permission);
        }
        accessInfoCreator.setPermissions(permissions);
        return accessInfoCreator;
    }

    private boolean matchUserData(User user, CucUser cucUser) {
        Assert.assertNotNull(user.getId());
        Assert.assertNotNull(user.getScopeId());
        if ((cucUser.getName() != null) && (cucUser.getName().length() > 0)) {
            Assert.assertEquals(cucUser.getName(), user.getName());
        }
        Assert.assertNotNull(user.getCreatedOn());
        Assert.assertNotNull(user.getCreatedBy());
        Assert.assertNotNull(user.getModifiedOn());
        Assert.assertNotNull(user.getModifiedBy());
        if ((cucUser.getDisplayName() != null) && (cucUser.getDisplayName().length() > 0)) {
            Assert.assertEquals(cucUser.getDisplayName(), user.getDisplayName());
        }
        if ((cucUser.getEmail() != null) && (cucUser.getEmail().length() > 0)) {
            Assert.assertEquals(cucUser.getEmail(), user.getEmail());
        }
        if ((cucUser.getPhoneNumber() != null) && (cucUser.getPhoneNumber().length() > 0)) {
            Assert.assertEquals(cucUser.getPhoneNumber(), user.getPhoneNumber());
        }
        if (cucUser.getStatus() != null) {
            Assert.assertEquals(cucUser.getStatus(), user.getStatus());
        }
        return true;
    }

    @And("I create user with name {string}")
    public void iCreateUserWithName(String userName) throws Exception {
        stepData.remove(USER_CREATOR);
        UserCreator userCreator = new UserCreator(getCurrentScopeId());
        userCreator.setName(userName);
        stepData.put(USER_CREATOR, userCreator);
        try {
            primeException();
            stepData.remove("User");
            User user = userService.create(userCreator);
            stepData.put("User", user);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @Then("I try to edit user to name {string}")
    public void iTryToEditUserWithName(String newUserName) throws Exception {
        User user = (User) stepData.get("User");
        user.setName(newUserName);
        try {
            primeException();
            stepData.remove("User");
            User newUser = userService.update(user);
            stepData.put("User", newUser);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @And("I create user with name {string} in account {string}")
    public void iCreateUserWithNameInSubaccount(String name, String accountName) throws Exception {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        Assert.assertEquals(accountName, account.getName());
        UserCreator userCreator = new UserCreator(account.getId());
        userCreator.setName(name);
        primeException();
        try {
            stepData.remove(USER_CREATOR);
            User childAccountUser = userService.create(userCreator);
            stepData.put(USER_CREATOR, userCreator);
            stepData.put("ChildAccountUser", childAccountUser);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @And("I create users with following names")
    public void iCreateUsersWithFollowingNames(List<CucUser> tmpUsers) throws Exception {
        UserCreator userCreator = new UserCreator(getCurrentScopeId());
        ArrayList<User> userList = new ArrayList<>();
        for (CucUser tmpUser : tmpUsers) {
            userCreator.setName(tmpUser.getName());
            User user = userService.create(userCreator);
            userList.add(user);
        }
        stepData.put(USER_LIST, userList);
    }

    @Given("I have the following user with expiration date in the future")
    public void iHaveTheFollowingUserWithExpirationDateInTheFuture(List<CucUser> userList) throws Exception {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        KapuaId accountId = (KapuaId) stepData.get("LastAccountId");
        KapuaId currentAccount;
        Set<ComparableUser> iHaveUsers = new HashSet<>();
        User lastUser = null;
        stepData.remove(USER_LIST);
        if (account != null) {
            currentAccount = account.getId();
        } else if (accountId != null) {
            currentAccount = accountId;
        } else {
            currentAccount = DEFAULT_ID;
        }
        primeException();
        try {
            for (CucUser userItem : userList) {
                String name = userItem.getName();
                String displayName = userItem.getDisplayName();
                String email = userItem.getEmail();
                String phone = userItem.getPhoneNumber();
                Date expirationDate = userItem.getExpirationDate();
                UserCreator userCreator = userCreatorCreator(name, displayName, email, phone, currentAccount, expirationDate);
                User user = userService.create(userCreator);
                iHaveUsers.add(new ComparableUser(user));
                lastUser = user;
            }
        } catch (KapuaException ke) {
            verifyException(ke);
        }
        stepData.put(USER_LIST, iHaveUsers);
        stepData.put("User", lastUser);
    }

    @And("I find user with expiration date in the future")
    public void iFindUserWithExpirationDateInTheFuture(List<CucUser> userList) {
        Set<ComparableUser> iFoundUsers = (Set<ComparableUser>) stepData.get(USER_LIST);
        boolean userChecks;
        for (CucUser userItem : userList) {
            userChecks = false;
            for (ComparableUser foundUserItem : iFoundUsers) {
                Date t1 = foundUserItem.getUser().getExpirationDate();
                Date t2 = userItem.getExpirationDate();
                LocalDate localDate1 = t1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate localDate2 = t2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (localDate1.getYear() == localDate2.getYear() && localDate1.getMonthValue() == localDate2.getMonthValue()
                        && localDate1.getDayOfMonth() == localDate2.getDayOfMonth()) {
                    matchUserData(foundUserItem.getUser(), userItem);
                    userChecks = true;
                    break;
                }
            }
            if (!userChecks) {
                Assert.fail(String.format(USER_NOT_FOUND, userItem.getExpirationDate()));
            }
        }
    }

    @Then("I search for the user with expiration date in the present")
    public void iSearchForTheUserWithExpirationDateInThePresent() throws KapuaException {
        KapuaId scpId = DEFAULT_ID;
        Set<ComparableUser> iFoundUsers;
        stepData.remove(USER_LIST);
        KapuaQuery query = new UserQuery(scpId);
        UserListResult queryResult = userService.query(query);
        iFoundUsers = new HashSet<>();
        List<User> users = queryResult.getItems();
        for (User userItems : users) {
            iFoundUsers.add(new ComparableUser(userItems));
        }
        stepData.put(USER_LIST, iFoundUsers);
    }

    @And("I find user with expiration date in the present")
    public void iFindUserWithExpirationDateInThePresent(List<CucUser> userList) {
        Set<ComparableUser> iFoundUsers = (Set<ComparableUser>) stepData.get(USER_LIST);
        boolean userChecks;
        for (CucUser userItem : userList) {
            userChecks = false;
            for (ComparableUser foundUserItem : iFoundUsers) {
                Date t1 = foundUserItem.getUser().getExpirationDate();
                Date t2 = userItem.getExpirationDate();
                LocalDate localDate1 = t1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate localDate2 = t2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (localDate1.getYear() == localDate2.getYear() && localDate1.getMonthValue() == localDate2.getMonthValue()
                        && localDate1.getDayOfMonth() == localDate2.getDayOfMonth()) {
                    matchUserData(foundUserItem.getUser(), userItem);
                    userChecks = true;
                    break;
                }
            }
            if (!userChecks) {
                Assert.fail(String.format(USER_NOT_FOUND, userItem.getExpirationDate()));
            }
        }
    }

    @Then("I search for the user with expiration date in the past")
    public void iSearchForTheUserWithExpirationDateInThePast() throws KapuaException {
        KapuaId scpId = DEFAULT_ID;
        Set<ComparableUser> iFoundUsers;
        stepData.remove(USER_LIST);
        KapuaQuery query = new UserQuery(scpId);
        UserListResult queryResult = userService.query(query);
        iFoundUsers = new HashSet<>();
        List<User> users = queryResult.getItems();
        for (User userItems : users) {
            iFoundUsers.add(new ComparableUser(userItems));
        }
        stepData.put(USER_LIST, iFoundUsers);
    }

    @And("I find user with expiration date in the past")
    public void iFindUserWithExpirationDateInThePast(List<CucUser> userList) {
        Set<ComparableUser> iFoundUsers = (Set<ComparableUser>) stepData.get(USER_LIST);
        boolean userChecks;
        for (CucUser userItem : userList) {
            userChecks = false;
            for (ComparableUser foundUserItem : iFoundUsers) {
                Date t1 = foundUserItem.getUser().getExpirationDate();
                Date t2 = userItem.getExpirationDate();

                LocalDate localDate1 = t1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate localDate2 = t2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (localDate1.getYear() == localDate2.getYear() && localDate1.getMonthValue() == localDate2.getMonthValue()
                        && localDate1.getDayOfMonth() == localDate2.getDayOfMonth()) {
                    matchUserData(foundUserItem.getUser(), userItem);
                    userChecks = true;
                    break;
                }
            }
            if (!userChecks) {
                Assert.fail(String.format(USER_NOT_FOUND, userItem.getExpirationDate()));
            }
        }
    }

    @And("I find users with phone number {string}")
    public void iFindUsersWithPhoneNumber(String phoneNumber) {
        UserListResult users = (UserListResult) stepData.get(FOUND_USERS);
        for (User user : users.getItems()) {
            Assert.assertEquals(user.getPhoneNumber(), (phoneNumber));
        }
        stepData.put(USER_LIST, users);
    }

    @Then("I find users with emails")
    public void iFindUsersWithEmails(List<CucUser> userList) {
        Set<ComparableUser> iFoundUsers = (Set<ComparableUser>) stepData.get(USER_LIST);
        boolean userChecks;
        for (CucUser userItem : userList) {
            userChecks = false;
            for (ComparableUser foundUserItem : iFoundUsers) {
                if (foundUserItem.getUser().getEmail().equals(userItem.getEmail())) {
                    matchUserData(foundUserItem.getUser(), userItem);
                    userChecks = true;
                    break;
                }
            }
            if (!userChecks) {
                Assert.fail(String.format(USER_NOT_FOUND, userItem.getEmail()));
            }
        }
    }

    @Then("I find users with email")
    public void iFindUsersWithTheSameEmailAddress(List<CucUser> userList) {
        Set<ComparableUser> iFoundUsers = (Set<ComparableUser>) stepData.get(USER_LIST);
        boolean userChecks;
        for (CucUser userItem : userList) {
            userChecks = false;
            for (ComparableUser foundUserItem : iFoundUsers) {
                if (foundUserItem.getUser().getEmail().equals(userItem.getEmail())) {
                    matchUserData(foundUserItem.getUser(), userItem);
                    userChecks = true;
                    break;
                }
            }
            if (!userChecks) {
                Assert.fail(String.format(USER_NOT_FOUND, userItem.getName()));
            }
        }
    }

    @Then("I find users with phone number")
    public void iFindUsersWithPhoneNumber(List<CucUser> userList) {
        Set<ComparableUser> iFoundUsers = (Set<ComparableUser>) stepData.get(USER_LIST);
        boolean userChecks;
        for (CucUser userItem : userList) {
            userChecks = false;
            for (ComparableUser foundUserItem : iFoundUsers) {
                if (foundUserItem.getUser().getPhoneNumber().equals(userItem.getPhoneNumber())) {
                    matchUserData(foundUserItem.getUser(), userItem);
                    userChecks = true;
                    break;
                }
            }
            if (!userChecks) {
                Assert.fail(String.format(USER_NOT_FOUND, userItem.getName()));
            }
        }
    }

    @And("I search users with phone number {string}")
    public void iSearchUsersWithPhoneNumber(String phoneNum) throws Throwable {
        UserQuery userQuery = new UserQuery(DEFAULT_ID);
        userQuery.setPredicate(userQuery.attributePredicate(UserAttributes.PHONE_NUMBER, phoneNum, AttributePredicate.Operator.EQUAL));
        UserListResult userListResult = userService.query(userQuery);
        stepData.put(FOUND_USERS, userListResult);
    }

    @And("I search users with email {string}")
    public void iSearchForUsersWithEmail(String email) throws Throwable {
        UserQuery userQuery = new UserQuery(DEFAULT_ID);
        userQuery.setPredicate(userQuery.attributePredicate(UserAttributes.EMAIL, email, AttributePredicate.Operator.EQUAL));
        UserListResult userListResult = userService.query(userQuery);
        stepData.put(FOUND_USERS, userListResult);
    }

    @Then("I find users with email {string}")
    public void iFindUsersWithEmail(String email) {
        UserListResult users = (UserListResult) stepData.get(FOUND_USERS);
        for (User user : users.getItems()) {
            Assert.assertEquals(user.getEmail(), (email));
        }
        stepData.put(USER_LIST, users);
    }

    @And("^I enable mfa$")
    public void iEnableMfa() {
        KapuaId userId = KapuaSecurityUtils.getSession().getUserId();
        KapuaId scopeId = KapuaSecurityUtils.getSession().getScopeId();

        MfaOptionFactory mfaFactory = KapuaLocator.getInstance().getFactory(MfaOptionFactoryImpl.class);
        MfaOptionCreator mfaCreator = new MfaOptionCreator(scopeId, userId);
        MfaOptionService mfaOptionService = KapuaLocator.getInstance().getService(MfaOptionService.class);
        try {
            mfaOptionService.create(mfaCreator);
        } catch (KapuaException e) {
            e.printStackTrace();
        }
    }

    @Then("^I have mfa enable$")
    public void iHaveMfaIsEnable() {
        KapuaId userId = KapuaSecurityUtils.getSession().getUserId();
        KapuaId scopeId = KapuaSecurityUtils.getSession().getScopeId();

        MfaOptionService mfaOptionService = KapuaLocator.getInstance().getService(MfaOptionService.class);
        MfaOption mfaOption = null;
        try {
            mfaOption = mfaOptionService.findByUserId(scopeId, userId);
        } catch (KapuaException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(mfaOption);
    }

    @Then("mfa repository can get mfa for user \"([^\"]*)\"$")
    public void mfaRepositoryCanGetMfaForUser(String username) throws KapuaException {
        BigInteger userId = null;
        BigInteger scopeId = null;
        try {
            userId = KapuaSecurityUtils.doPrivileged(() -> userService.findByName(username).getId().getId());
            scopeId = KapuaSecurityUtils.doPrivileged(() -> userService.findByName(username).getScopeId().getId());
        } catch (KapuaException e) {
            e.printStackTrace();
        }
        final MfaOptionService mfaOptionService = KapuaLocator.getInstance().getService(MfaOptionService.class);
        final MfaOption mfaOption = mfaOptionService.findByUserId(new KapuaIdImpl(scopeId), new KapuaIdImpl(userId));
        Assert.assertNotNull(mfaOption);
    }

    @Then("^The lockout error counter for user \"([^\"]*)\" is (\\d+)$")
    public void theLockoutErrorCounterForUserIs(String username, int expectedCounter) {
        KapuaId userIdTmp = null;
        KapuaId scopeIdTmp = null;
        try {
            userIdTmp = KapuaSecurityUtils.doPrivileged(() -> userService.findByName(username).getId());
            scopeIdTmp = KapuaSecurityUtils.doPrivileged(() -> userService.findByName(username).getScopeId());
        } catch (KapuaException e) {
            e.printStackTrace();
        }

        Credential credential = null;
        final KapuaId userId = userIdTmp;
        final KapuaId scopeId = scopeIdTmp;
        try {
            credential = KapuaSecurityUtils.doPrivileged(() -> credentialService.findByUserId(scopeId, userId).getFirstItem());
        } catch (KapuaException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(expectedCounter, credential.getLoginFailures());
    }

    // *****************
    // * Inner Classes *
    // *****************
    private class ComparableUser {

        private User user;

        ComparableUser(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ComparableUser) {
                ComparableUser other = (ComparableUser) obj;
                return compareAllUserAtt(user, other.getUser());
            } else {
                return false;
            }
        }

        private boolean compareAllUserAtt(User thisUser, User otherUser) {
            return thisUser.getName().equals(otherUser.getName()) &&
                    thisUser.getDisplayName().equals(otherUser.getDisplayName()) &&
                    thisUser.getEmail().equals(otherUser.getEmail()) &&
                    thisUser.getPhoneNumber().equals(otherUser.getPhoneNumber()) &&
                    thisUser.getStatus().equals(otherUser.getStatus());
        }
    }
}
