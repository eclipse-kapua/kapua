/*******************************************************************************
 * Copyright (c) 2017, 2024 Red Hat Inc and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Red Hat Inc - initial API and implementation
 *     Eurotech
 *******************************************************************************/
package org.eclipse.kapua.qa.common;

import java.time.Duration;
import java.util.Date;
import java.util.Map;
import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.eclipse.kapua.commons.crypto.setting.CryptoSettingKeys;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;
import org.eclipse.kapua.commons.util.KapuaDateUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.qa.common.cucumber.CucAccount;
import org.eclipse.kapua.qa.common.cucumber.CucConfig;
import org.eclipse.kapua.qa.common.cucumber.CucConnection;
import org.eclipse.kapua.qa.common.cucumber.CucCredentials;
import org.eclipse.kapua.qa.common.cucumber.CucDevice;
import org.eclipse.kapua.qa.common.cucumber.CucDeviceExtendedProperty;
import org.eclipse.kapua.qa.common.cucumber.CucDomain;
import org.eclipse.kapua.qa.common.cucumber.CucGroup;
import org.eclipse.kapua.qa.common.cucumber.CucJobStepProperty;
import org.eclipse.kapua.qa.common.cucumber.CucMessageRange;
import org.eclipse.kapua.qa.common.cucumber.CucMetric;
import org.eclipse.kapua.qa.common.cucumber.CucPermission;
import org.eclipse.kapua.qa.common.cucumber.CucRole;
import org.eclipse.kapua.qa.common.cucumber.CucRolePermission;
import org.eclipse.kapua.qa.common.cucumber.CucTopic;
import org.eclipse.kapua.qa.common.cucumber.CucTriggerProperty;
import org.eclipse.kapua.qa.common.cucumber.CucUser;
import org.eclipse.kapua.qa.common.cucumber.CucUserProfile;
import org.eclipse.kapua.qa.common.dbms.DbmsSpecifics;
import org.eclipse.kapua.qa.common.dbms.DbmsSpecificsFactory;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreElasticsearchClientSettings;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreElasticsearchClientSettingsKey;
import org.eclipse.kapua.transport.message.jms.JmsTopic;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Strings;
import com.google.inject.Singleton;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

@Singleton
public class BasicSteps extends TestBase {

    private static final Logger logger = LoggerFactory.getLogger(BasicSteps.class);

    public static final String JOB_ENGINE_CONTAINER_NAME = "job-engine";
    public static final String ES_CONTAINER_NAME = "es";
    public static final String DB_CONTAINER_NAME = "db";
    public static final String EVENTS_BROKER_CONTAINER_NAME = "events-broker";
    public static final String MESSAGE_BROKER_CONTAINER_NAME = "message-broker";
    public static final String TELEMETRY_CONSUMER_CONTAINER_NAME = "telemetry-consumer";
    public static final String LIFECYCLE_CONSUMER_CONTAINER_NAME = "lifecycle-consumer";
    public static final String AUTH_SERVICE_CONTAINER_NAME = "auth-service";
    public static final String API_CONTAINER_NAME = "rest-api";

    public static final String LAST_CREDENTIAL_ID = "LastCredentialId";

    private static final double WAIT_MULTIPLIER = Double.parseDouble(System.getProperty("org.eclipse.kapua.qa.waitMultiplier", "1.0"));

    private static final String KAPUA_CUSTOM_PROPERTIES = "@KapuaProperties(\"";
    private static final int KAPUA_CUSTOM_PROPERTIES_SIZE = KAPUA_CUSTOM_PROPERTIES.length();

    private static final String LAST_ACCOUNT_ID = "LastAccountId";
    private static final String LAST_USER_ID = "LastUserId";
    private static final String EXCEPTION_NAME = "ExceptionName";
    private static final String STATUS_CODE = "StatusCode";
    private static final String EXCEPTION_CAUGHT = "ExceptionCaught";
    private static final String ASSERT_ERROR_NAME = "AssertErrorName";
    private static final String ASSERT_ERROR_CAUGHT = "AssertErrorCaught";

    private final DBHelper database;
    private DbmsSpecifics dbmsSpecifics;

    @Inject
    public BasicSteps(StepData stepData, DBHelper database) {
        super(stepData);

        this.database = database;
    }

    @Before(value = "@setup and (@env_docker or @env_docker_base or @env_none)", order = 0)
    public void initParametersSetup(Scenario scenario) {
        setProperties(scenario,
                "kapuadb",
                "true",
                "certificates/jwt/test.key",
                "certificates/jwt/test.cert",
                "localhost",
                "http://job-engine:8080/v1",
                "trusted");
    }

    @Before(value = "(@env_docker or @env_docker_base) and not (@setup or @teardown)", order = 0)
    public void beforeScenarioDockerFull(Scenario scenario) {
        beforeCommon(scenario);
    }

    @Before(value = "@env_none and not (@setup or @teardown)", order = 0)
    public void beforeScenarioDockerBase(Scenario scenario) {
        beforeCommon(scenario);

        databaseInit();
    }

    @After(value = "@env_docker_base and @setup", order = 0)
    public void afterScenarioDockerBaseSetup(Scenario scenario) {
        databaseInit();
    }

    @After(value = "(@env_docker or @env_docker_base) and not (@setup or @teardown)", order = 0)
    public void afterScenarioDockerFull(Scenario scenario) {
        // afterScenarioDocker(scenario);
        logger.info("Database cleanup...");
        database.deleteAll();
        logger.info("Database cleanup... DONE");
        SecurityUtils.getSubject().logout();
        KapuaSecurityUtils.clearSession();
    }

    @After(value = "@env_none and not (@setup or @teardown)", order = 0)
    public void afterScenarioNone(Scenario scenario) {
        logger.info("Database drop...");
        try {
            database.dropAll();
            database.close();
        } catch (Exception e) {
            logger.error("Failed execute @After", e);
        }
        logger.info("Database drop... DONE");
        KapuaSecurityUtils.clearSession();
    }

    protected void beforeCommon(Scenario scenario) {
        this.scenario = scenario;
        stepData.clear();
    }

    protected void databaseInit() {
        database.init();
        // Create KapuaSession using KapuaSecurityUtils and kapua-sys user as logged in user.
        // All operations on database are performed using system user.
        // Only for unit tests. Integration tests assume that a real login is performed.
        KapuaSession kapuaSession = new KapuaSession(null, SYS_SCOPE_ID, SYS_USER_ID);
        KapuaSecurityUtils.setSession(kapuaSession);
    }


    //
    // Data Table Types
    //

    @DataTableType
    public CucAccount cucAccount(Map<String, String> entry) {
        return new CucAccount(
                entry.get("name"),
                Util.parseBigInteger(entry.get("scopeId")),
                entry.get("expirationDate"));
    }

    @DataTableType
    public CucConfig cucConfig(Map<String, String> entry) {
        return new CucConfig(
                entry.get("scopeId"),
                entry.get("parentId"),
                entry.get("type"),
                entry.get("name"),
                entry.get("value"));
    }

    @DataTableType
    public CucConnection cucConnection(Map<String, String> entry) {
        return new CucConnection(
                entry.get("scope"),
                Util.parseKapuaId(entry.get("scopeId")),
                entry.get("status"),
                entry.get("clientId"),
                entry.get("user"),
                Util.parseKapuaId(entry.get("userId")),
                entry.get("allowUserChange"),
                entry.get("userCouplingMode"),
                entry.get("reservedUser"),
                Util.parseKapuaId(entry.get("reservedUserId")),
                entry.get("protocol"),
                entry.get("clientIp"),
                entry.get("serverIp")
        );
    }

    @DataTableType
    public CucCredentials cucCredentials(Map<String, String> entry) {
        return new CucCredentials(
                entry.get("name"),
                entry.get("password"),
                Util.parseBoolean(entry.get("enabled")),
                entry.get("expirationDate"));
    }

    @DataTableType
    public CucDevice cucDevice(Map<String, String> entry) {
        return new CucDevice(
                Util.parseInteger(entry.get("scopeId")),
                Util.parseInteger(entry.get("groupId")),
                Util.parseInteger(entry.get("connectionId")),
                Util.parseInteger(entry.get("lastEventId")),
                Util.parseInteger(entry.get("preferredUserId")),
                entry.get("clientId"),
                entry.get("displayName"),
                entry.get("status"),
                entry.get("modelId"),
                entry.get("modelName"),
                entry.get("serialNumber"),
                entry.get("imei"),
                entry.get("imsi"),
                entry.get("iccid"),
                entry.get("biosVersion"),
                entry.get("firmwareVersion"),
                entry.get("osVersion"),
                entry.get("jvmVersion"),
                entry.get("osgiFrameworkVersion"),
                entry.get("applicationFrameworkVersion"),
                entry.get("connectionInterface"),
                entry.get("connectionIp"),
                entry.get("applicationIdentifiers"),
                entry.get("acceptEncoding"),
                entry.get("customAttribute1"),
                entry.get("customAttribute2"),
                entry.get("customAttribute3"),
                entry.get("customAttribute4"),
                entry.get("customAttribute5")
        );
    }

    @DataTableType
    public CucDeviceExtendedProperty cucDeviceExtendedProperty(Map<String, String> entry) {
        return new CucDeviceExtendedProperty(
                Strings.emptyToNull(entry.get("groupName")),
                Strings.emptyToNull(entry.get("name")),
                Strings.emptyToNull(entry.get("value"))
        );
    }

    @DataTableType
    public CucDomain cucDomain(Map<String, String> entry) {
        return new CucDomain(
                entry.get("name"),
                entry.get("serviceName"),
                entry.get("actions")
        );
    }

    @DataTableType
    public CucGroup cucGroup(Map<String, String> entry) {
        return new CucGroup(
                entry.get("name"),
                Util.parseInteger(entry.get("scope")),
                Util.parseKapuaId(entry.get("scopeId"))
        );
    }

    @DataTableType
    public CucJobStepProperty cucJobStepProperty(Map<String, String> entry) {
        return new CucJobStepProperty(
                entry.get("name"),
                entry.get("type"),
                entry.get("value"),
                entry.get("exampleValue"));
    }

    @DataTableType
    public CucMessageRange cucMessageRange(Map<String, String> entry) {
        return new CucMessageRange(
                entry.get("topic"),
                entry.get("clientId"),
                entry.get("startDate"),
                entry.get("endDate"),
                Util.parseInteger(entry.get("count"))
        );
    }

    @DataTableType
    public CucMetric cucMetric(Map<String, String> entry) {
        return new CucMetric(
                entry.get("metric"),
                entry.get("type"),
                entry.get("value"),
                Util.parseInt(entry.get("message"))
        );
    }

    @DataTableType
    public CucPermission cucPermission(Map<String, String> entry) {
        return new CucPermission(
                entry.get("domain"),
                Util.parseAction(entry.get("action")),
                Util.parseInteger(entry.get("targetScope")),
                Util.parseKapuaId(entry.get("targetScopeId"))
        );
    }

    @DataTableType
    public CucRole cucRole(Map<String, String> entry) {
        return new CucRole(
                entry.get("name"),
                Util.parseInteger(entry.get("scopeId")),
                entry.get("actions"),
                Util.parseKapuaId(entry.get("id")),
                Util.parseActions(entry.get("actionSet"))
        );
    }

    @DataTableType
    public CucRolePermission cucRolePermission(Map<String, String> entry) {
        return new CucRolePermission(
                Util.parseKapuaId(entry.get("scope")),
                Util.parseInteger(entry.get("scopeId")),
                Util.parseKapuaId(entry.get("role")),
                Util.parseInteger(entry.get("roleId")),
                Util.parseAction(entry.get("action")),
                entry.get("actionName"),
                Util.parseKapuaId(entry.get("targetScope")),
                Util.parseInteger(entry.get("targetScopeId"))
        );
    }

    @DataTableType
    public CucTopic cucTopic(Map<String, String> entry) {
        return new CucTopic(
                entry.get("topic"),
                entry.get("clientId"),
                entry.get("captured")
        );
    }

    @DataTableType
    public CucTriggerProperty cucTriggerProperty(Map<String, String> entry) {
        return new CucTriggerProperty(
                entry.get("name"),
                entry.get("type"),
                entry.get("value")
        );
    }

    @DataTableType
    public CucUser cucUser(Map<String, String> entry) {
        return new CucUser(
                entry.get("name"),
                entry.get("displayName"),
                entry.get("email"),
                entry.get("phoneNumber"),
                Util.parseUserStatus(entry.get("status")),
                Util.parseUserType(entry.get("userType")),
                Util.parseBigInteger(entry.get("scopeId")),
                entry.get("password"),
                entry.get("expirationDate"));
    }


    @DataTableType
    public CucUserProfile cucUserProfile(Map<String, String> entry) {
        return new CucUserProfile(entry.get("displayName"),
                                  entry.get("phoneNumber"),
                                  entry.get("email"));
    }

    //
    // Parameter Types
    //
    @ParameterType(".*")
    public JmsTopic topic(String topic) {
        return new JmsTopic(topic);
    }

    @ParameterType(value = "true|True|TRUE|false|False|FALSE")
    public boolean booleanValue(String value) {
        return Boolean.parseBoolean(value);
    }

    private void setProperties(Scenario scenario,
           String schema,
           String updateSchema,
           String jwtKey,
           String jwtCertificate,
           String brokerHost,
           String jobEngineUrl,
           String jobEngineAuthMode) {

        setDbmsSpecsForThisScenario(scenario);

        logger.info("=====> Init parameters for the environment...");
        SystemSetting.resetInstance();

        System.setProperty(SystemSettingKey.DB_SCHEMA.key(), schema);
        System.setProperty(SystemSettingKey.DB_SCHEMA_UPDATE.key(), updateSchema);
        System.setProperty(SystemSettingKey.DB_CONNECTION_USE_SSL.key(), "false");
        System.setProperty(CryptoSettingKeys.CRYPTO_SECRET_KEY.key(), "kapuaTestsKey!!!");
        Map<String, String> specificDbmsSettings = this.dbmsSpecifics.getClientJdbcSettings();
        for (Map.Entry<String, String> entry : specificDbmsSettings.entrySet()) {
            System.setProperty(entry.getKey(), entry.getValue());
        }

        System.setProperty("certificate.jwt.private.key", jwtKey);
        System.setProperty("certificate.jwt.certificate", jwtCertificate);
        System.setProperty("broker.host", brokerHost);
        System.setProperty("job.engine.base.url", jobEngineUrl);
        System.setProperty("job.engine.client.auth.mode", jobEngineAuthMode);
        setSpecificProperties(scenario);
        logParameters();
        KapuaLocator.clearInstance();
        logger.info("=====> Init parameters for the environment... DONE");
    }

    private void setDbmsSpecsForThisScenario(Scenario scenario) {
        this.dbmsSpecifics = DbmsSpecificsFactory.createSpecifics(scenario);
        database.setDbmsSpecifics(this.dbmsSpecifics);
    }

    private void logParameters() {
        logger.info("===============================================");
        logger.info("{}: {}", SystemSettingKey.DB_SCHEMA.key(), SystemSetting.getInstance().getString(SystemSettingKey.DB_SCHEMA));
        logger.info("{}: {}", SystemSettingKey.DB_SCHEMA_UPDATE.key(), SystemSetting.getInstance().getString(SystemSettingKey.DB_SCHEMA_UPDATE));
        logger.info("{}: {}", SystemSettingKey.DB_CONNECTION_HOST.key(), SystemSetting.getInstance().getString(SystemSettingKey.DB_CONNECTION_HOST));
        logger.info("{}: {}", SystemSettingKey.DB_CONNECTION_PORT.key(), SystemSetting.getInstance().getString(SystemSettingKey.DB_CONNECTION_PORT));
        logger.info("{}: {}", SystemSettingKey.DB_JDBC_CONNECTION_URL_RESOLVER.key(), SystemSetting.getInstance().getString(SystemSettingKey.DB_JDBC_CONNECTION_URL_RESOLVER));
        logger.info("{}: {}", SystemSettingKey.DB_JDBC_DRIVER.key(), SystemSetting.getInstance().getString(SystemSettingKey.DB_JDBC_DRIVER));
        logger.info("{}: {}", SystemSettingKey.DB_CONNECTION_SCHEME.key(), SystemSetting.getInstance().getString(SystemSettingKey.DB_CONNECTION_SCHEME));
        logger.info("Rest: datastore.elasticsearch.node: {}", DatastoreElasticsearchClientSettings.getInstance().getString(DatastoreElasticsearchClientSettingsKey.NODES));
        logger.info("===============================================");
    }

    private void setSpecificProperties(Scenario scenario) {
        String[] scenarioTags = scenario.getSourceTagNames().toArray(new String[]{});
        for (String str : scenarioTags) {
            if (str.startsWith(KAPUA_CUSTOM_PROPERTIES)) {
                String properties = str.substring(KAPUA_CUSTOM_PROPERTIES_SIZE, str.lastIndexOf("\""));
                String[] splittedProperties = properties.split("\\,");
                for (String property : splittedProperties) {
                    String[] splittedProperty = property.split("=");
                    System.setProperty(splittedProperty[0], splittedProperty[1]);
                }
            }
        }
    }

    @Given("Scope with ID {int}")
    public void setSpecificScopeId(Integer id) {
        stepData.put(LAST_ACCOUNT_ID, getKapuaId(id));
    }

    @Given("The KAPUA-SYS scope")
    public void setRootScope() {
        stepData.put(LAST_ACCOUNT_ID, SYS_SCOPE_ID);
    }

    @Given("A null scope")
    public void setNullScope() {
        stepData.put(LAST_ACCOUNT_ID, null);
    }

    @Given("The User ID {int}")
    public void setSpecificUserId(Integer id) {
        stepData.put(LAST_USER_ID, getKapuaId(id));
    }

    @Given("The KAPUA-SYS user")
    public void setRootUserId() {
        stepData.put(LAST_USER_ID, SYS_USER_ID);
    }

    @Given("A null user")
    public void setNullUser() {
        stepData.put(LAST_USER_ID, null);
    }

    @Given("Server with host {string} on port {string}")
    public void setHostPort(String host, String port) {
        stepData.put("host", host);
        stepData.put("port", port);
    }

    @Given("I expect the exception {string} with the text {string}")
    public void setExpectedExceptionDetails(String name, String text) {
        stepData.put("ExceptionExpected", true);
        stepData.put(EXCEPTION_NAME, name);
        stepData.put("ExceptionMessage", text);
    }

    @When("I wait {int} second(s)")
    public void waitSeconds(int seconds) throws InterruptedException {
        double effectiveSeconds = ((double) seconds) * WAIT_MULTIPLIER;
        Thread.sleep(Duration.ofSeconds((long) Math.ceil(effectiveSeconds)).toMillis());
    }

    @When("{int} second(s) passed(.*)")
    public void secondsPassed(int seconds) throws InterruptedException {
        waitSeconds(seconds);
    }

    @Then("An exception was thrown")
    public void exceptionCaught() {
        String exName = stepData.contains(EXCEPTION_NAME) ? (String) stepData.get(EXCEPTION_NAME) : "Unknown";
        boolean exCaught = stepData.contains(EXCEPTION_CAUGHT) ? (boolean) stepData.get(EXCEPTION_CAUGHT) : false;
        Assert.assertTrue(String.format("Exception %s was expected but was not raised.", exName), exCaught);
    }

    @Then("No exception was thrown")
    public void noExceptionCaught() {
        boolean exCaught = stepData.contains(EXCEPTION_CAUGHT) ? (boolean) stepData.get(EXCEPTION_CAUGHT) : false;
        Assert.assertFalse("An unexpected exception was raised!", exCaught);
    }

    @Then("Response status code match")
    public void statusCodeMatch() {
        final int statusCode = stepData.contains(STATUS_CODE) ? (int) stepData.get(STATUS_CODE) : -1;
        Assert.assertEquals(statusCode, stepData.get("StatusCode"));
    }

    @Then("I count {int}")
    public void checkCountResult(int num) {
        Assert.assertEquals(num, stepData.getCount());
    }

    @Then("I count {int} or more")
    public void checkAsyncCountResult(int num) {
        Assert.assertTrue(stepData.getCount() >= num);
    }

    @Then("I get the integer {int}")
    public void checkIntResult(int num) {
        Assert.assertEquals(num, (int) stepData.get("IntValue"));
    }

    @Then("I get the boolean {string}")
    public void checkBoolResult(String val) {
        Assert.assertEquals(Boolean.valueOf(val), stepData.get("BoolValue"));
    }

    @Given("The text {string}")
    public void setCustomText(String text) {
        stepData.put("Text", text);
    }

    @Then("I get the text {string}")
    public void checkStringResult(String text) {
        Assert.assertEquals(text, stepData.get("Text"));
    }

    @Given("The date {string}")
    public void setCustomDate(String dateString) throws Exception {
        primeException();
        try {
            Date date = KapuaDateUtils.parseDate(dateString);
            stepData.put("Date", date);
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @Given("System property {string} with value {string}")
    public void setSystemProperty(String key, String value) {
        if ("null".equalsIgnoreCase(value)) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, value);
        }
    }

    @And("I expect the exception {string}")
    public void iExpectTheException(String name) {
        stepData.put("ExceptionExpected", true);
        stepData.put(EXCEPTION_NAME, name);
    }

    @And("I expect the response status code {int}")
    public void iExpectTheStatusCode(int statusCode) {
        stepData.put("StatusCodeExpected", true);
        stepData.put(STATUS_CODE, statusCode);
    }

    @Then("An assertion error was thrown")
    public void anAssertionErrorWasThrown() {
        String assertErrorName = stepData.contains(ASSERT_ERROR_NAME) ? (String) stepData.get(ASSERT_ERROR_NAME) : "Unknown";
        boolean assertErrorCaught = stepData.contains(ASSERT_ERROR_CAUGHT) ? (boolean) stepData.get(ASSERT_ERROR_CAUGHT) : false;
        Assert.assertTrue(String.format("Assert error was expected but was not raised.", assertErrorName), assertErrorCaught);
    }

    @And("I expect the assertion error {string} with the text {string}")
    public void iExpectTheAssertErrorWithTheText(String name, String text) {
        stepData.put("AssertErrorExpected", true);
        stepData.put(ASSERT_ERROR_NAME, name);
        stepData.put("AssertErrorMessage", text);
    }

    @And("No assertion error was thrown")
    public void noAssertionErrorWasThrown() {
        boolean assertErrorCaught = stepData.contains(ASSERT_ERROR_CAUGHT) ? (boolean) stepData.get(ASSERT_ERROR_CAUGHT) : false;
        Assert.assertFalse("An unexpected assert error was raised!", assertErrorCaught);
    }

    @And("I wait for {int} millisecond(s) for processes to settle down")
    public void waitingMilliseconds(int milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);
    }
}
