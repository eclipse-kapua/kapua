/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.endpoint.steps;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalNullArgumentException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.kapua.qa.common.TestBase;
import org.eclipse.kapua.service.endpoint.EndpointInfo;
import org.eclipse.kapua.service.endpoint.EndpointInfoAttributes;
import org.eclipse.kapua.service.endpoint.EndpointInfoCreator;
import org.eclipse.kapua.service.endpoint.EndpointInfoFactory;
import org.eclipse.kapua.service.endpoint.EndpointInfoListResult;
import org.eclipse.kapua.service.endpoint.EndpointInfoQuery;
import org.eclipse.kapua.service.endpoint.EndpointInfoService;
import org.junit.Assert;

import com.google.inject.Singleton;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

@Singleton
public class EndpointServiceSteps extends TestBase {

    private EndpointInfoService endpointInfoService;
    private EndpointInfoFactory endpointInfoFactory;

    // ****************************************************************************************
    // * Implementation of Gherkin steps used in JobService.feature scenarios.                *
    // *                                                                                      *
    // * MockedLocator is used for Location Service. Mockito is used to mock other            *
    // * services that the Account services dependent on. Dependent services are:             *
    // * - Authorization Service                                                              *
    // ****************************************************************************************

    private static final String ENDPOINT_INFO = "EndpointInfo";

    // Default constructor
    @Inject
    public EndpointServiceSteps(StepData stepData) {
        super(stepData);
    }

    @After(value = "@setup")
    public void setServices() {
        KapuaLocator locator = KapuaLocator.getInstance();
        endpointInfoService = locator.getService(EndpointInfoService.class);
        endpointInfoFactory = locator.getFactory(EndpointInfoFactory.class);
    }

    // ************************************************************************************
    // ************************************************************************************
    // * Definition of Cucumber scenario steps                                            *
    // ************************************************************************************
    // ************************************************************************************

    // ************************************************************************************
    // * Setup and tear-down steps                                                        *
    // ************************************************************************************

    @Before(value = "@env_docker or @env_docker_base or @env_none", order = 10)
    public void beforeScenarioNone(Scenario scenario) {
        updateScenario(scenario);
    }

    // ************************************************************************************
    // * The Cucumber test steps                                                          *
    // ************************************************************************************

    @And("I create endpoint with schema {string}, domain {string} and port {int}")
    public void iCreateEndpointWithSchemaDnsAndPort(String schema, String dns, int port) throws Exception {
        EndpointInfoCreator endpointInfoCreator = new EndpointInfoCreator(getCurrentScopeId());
        endpointInfoCreator.setSchema(schema);
        endpointInfoCreator.setDns(dns);
        endpointInfoCreator.setPort(port);
        endpointInfoCreator.setEndpointType(EndpointInfo.ENDPOINT_TYPE_RESOURCE);
        try {
            stepData.remove(ENDPOINT_INFO);
            EndpointInfo endpointInfo = endpointInfoService.create(endpointInfoCreator);
            stepData.put(ENDPOINT_INFO, endpointInfo);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @And("I try to create endpoint with invalid symbols in schema")
    public void iTryToCreateInvalidEndpoint() throws Exception {
        String invalidSymbols = "!\"#$%&'()=»Ç" +
                ">:;<-.,⁄@‹›€" +
                "*ı–°·‚_±Œ„‰" +
                "?“‘”’ÉØ∏{}|Æ" +
                "æÒ\uF8FFÔÓÌÏÎÍÅ«" +
                "◊Ñˆ¯Èˇ¿";
        for (int i = 0; i < invalidSymbols.length(); i++) {
            EndpointInfoCreator endpointInfoCreator = new EndpointInfoCreator(getCurrentScopeId());
            String schema = invalidSymbols + invalidSymbols.charAt(i);
            endpointInfoCreator.setSchema(schema);
            endpointInfoCreator.setDns("dns.com");
            endpointInfoCreator.setPort(2222);
            endpointInfoCreator.setEndpointType(EndpointInfo.ENDPOINT_TYPE_RESOURCE);
            try {
                stepData.remove("EndpointInfo");
                EndpointInfo endpointInfo = endpointInfoService.create(endpointInfoCreator);
                stepData.put("EndpointInfo", endpointInfo);
            } catch (KapuaException ex) {
                verifyException(ex);
            }
        }
    }

    @And("I create endpoint with domain name {string} and port {int} without schema")
    public void iCreateEndpointWithDnsAndPort(String dns, int port) throws Exception {
        EndpointInfoCreator endpointInfoCreator = new EndpointInfoCreator(getCurrentScopeId());
        endpointInfoCreator.setDns(dns);
        endpointInfoCreator.setPort(port);
        endpointInfoCreator.setEndpointType(EndpointInfo.ENDPOINT_TYPE_RESOURCE);
        try {
            EndpointInfo endpointInfo = endpointInfoService.create(endpointInfoCreator);
            stepData.put("EndpointInfo", endpointInfo);
        } catch (KapuaIllegalNullArgumentException ex) {
            verifyException(ex);
        }
    }

    @And("I create endpoint with schema {string} without domain name and port")
    public void iCreateEndpointWithSchemaOnly(String schema) throws Exception {
        EndpointInfoCreator endpointInfoCreator = new EndpointInfoCreator(getCurrentScopeId());
        endpointInfoCreator.setSchema(schema);
        endpointInfoCreator.setEndpointType(EndpointInfo.ENDPOINT_TYPE_RESOURCE);
        try {
            EndpointInfo endpointInfo = endpointInfoService.create(endpointInfoCreator);
            stepData.put("EndpointInfo", endpointInfo);
        } catch (KapuaIllegalNullArgumentException ex) {
            verifyException(ex);
        }
    }

    @And("I create endpoint with NULL parameters")
    public void iCreateEndpointWithNullParameters() throws Exception {
        EndpointInfoCreator endpointInfoCreator = new EndpointInfoCreator(getCurrentScopeId());
        endpointInfoCreator.setSchema(null);
        endpointInfoCreator.setDns(null);
        endpointInfoCreator.setPort(0);
        endpointInfoCreator.setEndpointType(EndpointInfo.ENDPOINT_TYPE_RESOURCE);
        try {
            EndpointInfo endpointInfo = endpointInfoService.create(endpointInfoCreator);
            stepData.put("EndpointInfo", endpointInfo);
        } catch (KapuaIllegalNullArgumentException ex) {
            verifyException(ex);
        }
    }

    @When("I delete the endpoint with schema {string}, domain {string} and port {int}")
    public void iDeleteEndpointWithSchema(String schema, String domain, int port) throws Throwable {
        primeException();
        try {
            EndpointInfoQuery endpointInfoQuery = new EndpointInfoQuery(getCurrentScopeId());
            endpointInfoQuery.setPredicate(endpointInfoQuery.attributePredicate(EndpointInfoAttributes.SCHEMA, schema, AttributePredicate.Operator.EQUAL));
            EndpointInfo endpointInfo = endpointInfoService.query(endpointInfoQuery).getFirstItem();
            Assert.assertEquals(schema, endpointInfo.getSchema());
            Assert.assertEquals(domain, endpointInfo.getDns());
            Assert.assertEquals(port, endpointInfo.getPort());
            endpointInfoService.delete(SYS_SCOPE_ID, endpointInfo.getId());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @And("I try to find endpoint with schema {string}, domain {string} and port {int}")
    public void foundEndpointBySchemaDomainPort(String schema, String domain, int port) throws Exception {
        foundEndpointBySchemaDomainPortSection(schema, domain, port, EndpointInfo.ENDPOINT_TYPE_RESOURCE);
    }

    @And("I try to find endpoint with schema {string}")
    public void foundEndpointBySchema(String schema) throws Exception {
        foundEndpointBySchemaDomainPortSection(schema, null, -1, EndpointInfo.ENDPOINT_TYPE_RESOURCE);
    }

    @And("I try to find endpoint with schema {string}, domain {string} and port {int} and section {string}")
    public void foundEndpointBySchemaDomainPortSection(String schema, String domain, int port, String section) throws Exception {
        primeException();
        try {
            EndpointInfoQuery endpointInfoQuery = new EndpointInfoQuery(getCurrentScopeId());
            AndPredicate andPredicate = endpointInfoQuery.andPredicate();

            if (section.equals("cors")) {
                section = EndpointInfo.ENDPOINT_TYPE_CORS;
            } //to simplify argument passing

            if (schema != null) {
                andPredicate.and(endpointInfoQuery.attributePredicate(EndpointInfoAttributes.SCHEMA, schema, AttributePredicate.Operator.EQUAL));
            }
            if (section != null) {
                andPredicate.and(endpointInfoQuery.attributePredicate(EndpointInfoAttributes.ENDPOINT_TYPE, section));
            }
            if (domain != null) {
                andPredicate.and(endpointInfoQuery.attributePredicate(EndpointInfoAttributes.DNS, domain, AttributePredicate.Operator.EQUAL));
            }
            if (port != -1) {
                andPredicate.and(endpointInfoQuery.attributePredicate(EndpointInfoAttributes.PORT, port, AttributePredicate.Operator.EQUAL));
            }

            endpointInfoQuery.setPredicate(andPredicate);
            EndpointInfo endpointInfo = endpointInfoService.query(endpointInfoQuery).getFirstItem();

            if (schema != null) {
                Assert.assertEquals(schema, endpointInfo.getSchema());
            }
            if (section != null) {
                Assert.assertEquals(section, endpointInfo.getEndpointType());
            }
            if (domain != null) {
                Assert.assertEquals(domain, endpointInfo.getDns());
            }
            if (port != -1) {
                Assert.assertEquals(port, endpointInfo.getPort());
            }

            stepData.put(ENDPOINT_INFO, endpointInfo);
            stepData.put("EndpointInfoId", endpointInfo.getId());
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @And("I try to edit endpoint schema to {string}")
    public void editEndpointSchema(String schema) throws Exception {
        EndpointInfo endpointInfo = (EndpointInfo) stepData.get(ENDPOINT_INFO);
        endpointInfo.setSchema(schema);
        primeException();
        try {
            EndpointInfo newEndpoint = endpointInfoService.update(endpointInfo);
            stepData.put(ENDPOINT_INFO, newEndpoint);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @And("I delete endpoint with schema {string}")
    public void iDeleteEndpointWithSchema(String schema) throws Exception {
        primeException();
        try {
            EndpointInfoQuery endpointInfoQuery = new EndpointInfoQuery(getCurrentScopeId());
            endpointInfoQuery.setPredicate(endpointInfoQuery.attributePredicate(EndpointInfoAttributes.SCHEMA, schema, AttributePredicate.Operator.EQUAL));
            EndpointInfo endpointInfo = endpointInfoService.query(endpointInfoQuery).getFirstItem();
            endpointInfoService.delete(SYS_SCOPE_ID, endpointInfo.getId());
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @And("I delete the last created endpoint")
    public void iDeleteTheLastCreatedEndpoint() throws Exception {
        try {
            EndpointInfo endpointInfo = (EndpointInfo) stepData.get(ENDPOINT_INFO);
            endpointInfoService.delete(SYS_SCOPE_ID, endpointInfo.getId());
        } catch (Exception e) {
            verifyException(e);
        }
    }

    @And("I delete endpoint with schema {string}, domain {string} and port {int}")
    public void iDeleteEndpointWithSchemaDomainAndPort(String schema, String domainName, int port) throws Exception {
        primeException();
        try {
            EndpointInfoQuery endpointInfoQuery = new EndpointInfoQuery(getCurrentScopeId());
            endpointInfoQuery.setPredicate(endpointInfoQuery.attributePredicate(EndpointInfoAttributes.SCHEMA, schema, AttributePredicate.Operator.EQUAL));
            endpointInfoQuery.setPredicate(endpointInfoQuery.attributePredicate(EndpointInfoAttributes.DNS, domainName, AttributePredicate.Operator.EQUAL));
            endpointInfoQuery.setPredicate(endpointInfoQuery.attributePredicate(EndpointInfoAttributes.PORT, port, AttributePredicate.Operator.EQUAL));
            EndpointInfo endpointInfo = endpointInfoService.query(endpointInfoQuery).getFirstItem();
            endpointInfoService.delete(getCurrentScopeId(), endpointInfo.getId());
            stepData.put("DeletedEndpointInfo", endpointInfo);
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @And("I search for all endpoints in current scopeId")
    public void iSearchForAllEndpoints() throws Exception {
        primeException();
        try {
            EndpointInfoQuery endpointInfoQuery = new EndpointInfoQuery(getCurrentScopeId());
            EndpointInfoListResult endpointInfo = endpointInfoService.query(endpointInfoQuery);
            stepData.put("NumberOfEndpoints", endpointInfo.getSize());
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @Then("I find {int} endpoints")
    public void iFindEndpoint(int numberOfEndpoints) {
        int foundEndpoints = (int) stepData.get("NumberOfEndpoints");
        Assert.assertEquals(foundEndpoints, numberOfEndpoints);
    }

    @Then("I found endpoint with schema {string}")
    public void iFoundEndpoint(String endpointSchema) {
        EndpointInfo endpointInfo = (EndpointInfo) stepData.get("EndpointInfo");
        Assert.assertEquals(endpointSchema, endpointInfo.getSchema());
    }

    @Then("I found endpoint with schema {string}, domain {string} and port {int}")
    public void iFoundEndpointWithSchemaDomainAndPort(String endpointSchema, String domainName, int port) {
        EndpointInfo endpointInfo = (EndpointInfo) stepData.get("EndpointInfo");
        Assert.assertEquals(endpointSchema, endpointInfo.getSchema());
        Assert.assertEquals(domainName, endpointInfo.getDns());
        Assert.assertEquals(port, endpointInfo.getPort());
    }

    @Then("I did not find endpoint with schema {string}")
    public void notFoundEndpointWithSchema(String endpointSchema) {
        EndpointInfo endpointInfo = (EndpointInfo) stepData.get("EndpointInfo");
        Assert.assertNotEquals(endpointSchema, endpointInfo.getSchema());
    }

    @When("I delete all endpoints with schema {string}")
    public void iDeleteEndpointsWithSchema(String schema) throws Throwable {
        primeException();
        try {
            EndpointInfoQuery endpointInfoQuery = new EndpointInfoQuery(getCurrentScopeId());
            endpointInfoQuery.setPredicate(endpointInfoQuery.attributePredicate(EndpointInfoAttributes.SCHEMA, schema, AttributePredicate.Operator.EQUAL));
            EndpointInfoListResult endpointsToDelete = endpointInfoService.query(endpointInfoQuery);
            for (int i = 0; i < endpointsToDelete.getSize(); i++) {
                endpointInfoService.delete(getCurrentScopeId(), endpointsToDelete.getItem(i).getId());
            }
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @Then("I edit last created endpoint Schema to {string}")
    public void iEditEndpointSchema(String schema) throws Throwable {
        EndpointInfo endpointInfo = (EndpointInfo) stepData.get("EndpointInfo");
        if (schema.equals("NULL")) {
            schema = null;
        }
        try {
            endpointInfo.setSchema(schema);
            endpointInfoService.update(endpointInfo);
        } catch (Exception e) {
            verifyException(e);
        }
    }

    @Then("I edit last created endpoint Domain Name to {string}")
    public void iEditLastCreatedEndpointDomainNameTo(String domainName) throws Throwable {
        EndpointInfo endpointInfo = (EndpointInfo) stepData.get("EndpointInfo");
        if (domainName.equals("NULL")) {
            domainName = null;
        }
        try {
            endpointInfo.setDns(domainName);
            endpointInfoService.update(endpointInfo);
        } catch (Exception e) {
            verifyException(e);
        }
    }

    @Then("I edit last created endpoint Port to {int}")
    public void iEditLastCreatedEndpointPort(int port) throws Throwable {
        EndpointInfo endpointInfo = (EndpointInfo) stepData.get("EndpointInfo");
        try {
            endpointInfo.setPort(port);
            endpointInfoService.update(endpointInfo);
        } catch (Exception e) {
            verifyException(e);
        }
    }

    @Then("I edit port number to {int} in endpoint with schema {string}, domain {string} and port {int}")
    public void iEditLastCreatedEndpointPortInEndpoint(int newPort, String schema, String domainName, int port) throws Throwable {
        try {
            EndpointInfoQuery endpointInfoQuery = new EndpointInfoQuery(getCurrentScopeId());
            endpointInfoQuery.setPredicate(endpointInfoQuery.attributePredicate(EndpointInfoAttributes.SCHEMA, schema, AttributePredicate.Operator.EQUAL));
            endpointInfoQuery.setPredicate(endpointInfoQuery.attributePredicate(EndpointInfoAttributes.DNS, domainName, AttributePredicate.Operator.EQUAL));
            endpointInfoQuery.setPredicate(endpointInfoQuery.attributePredicate(EndpointInfoAttributes.PORT, port, AttributePredicate.Operator.EQUAL));
            EndpointInfo endpointInfo = endpointInfoService.query(endpointInfoQuery).getFirstItem();
            endpointInfo.setPort(newPort);
            endpointInfoService.update(endpointInfo);
        } catch (Exception e) {
            verifyException(e);
        }
    }

    @And("I create endpoint with schema {string} and port {int} without domain name")
    public void iCreateEndpointWithSchemaAndPortWithoutDomain(String schema, int port) throws Throwable {
        EndpointInfoCreator endpointInfoCreator = new EndpointInfoCreator(getCurrentScopeId());
        endpointInfoCreator.setSchema(schema);
        endpointInfoCreator.setPort(port);
        endpointInfoCreator.setDns(null);
        endpointInfoCreator.setEndpointType(EndpointInfo.ENDPOINT_TYPE_RESOURCE);
        try {
            EndpointInfo endpointInfo = endpointInfoService.create(endpointInfoCreator);
            stepData.put("EndpointInfo", endpointInfo);
        } catch (KapuaIllegalNullArgumentException ex) {
            verifyException(ex);
        }
    }

    @And("I create endpoint with domain name {string} without schema and port")
    public void iCreateEndpointWithDomainWithoutSchemaAndPort(String domainName) throws Throwable {
        EndpointInfoCreator endpointInfoCreator = new EndpointInfoCreator(getCurrentScopeId());
        endpointInfoCreator.setDns(domainName);
        endpointInfoCreator.setEndpointType(EndpointInfo.ENDPOINT_TYPE_RESOURCE);
        try {
            EndpointInfo endpointInfo = endpointInfoService.create(endpointInfoCreator);
            stepData.put("EndpointInfo", endpointInfo);
        } catch (KapuaIllegalNullArgumentException ex) {
            verifyException(ex);
        }
    }

    @And("I create endpoint with schema {string} and domain {string} without port")
    public void iCreateEndpointWithSchemaAndDomainWithoutPort(String schema, String domainName) throws Throwable {
        EndpointInfoCreator endpointInfoCreator = new EndpointInfoCreator(getCurrentScopeId());
        endpointInfoCreator.setDns(domainName);
        endpointInfoCreator.setSchema(schema);
        endpointInfoCreator.setEndpointType(EndpointInfo.ENDPOINT_TYPE_RESOURCE);
        try {
            EndpointInfo endpointInfo = endpointInfoService.create(endpointInfoCreator);
            stepData.put("EndpointInfo", endpointInfo);
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @And("I create endpoint with port {int} without schema and domain name")
    public void iCreateEndpointWithPortWithoutSchemaAndDomain(int port) throws Throwable {
        EndpointInfoCreator endpointInfoCreator = new EndpointInfoCreator(getCurrentScopeId());
        endpointInfoCreator.setPort(port);
        endpointInfoCreator.setEndpointType(EndpointInfo.ENDPOINT_TYPE_RESOURCE);
        try {
            EndpointInfo endpointInfo = endpointInfoService.create(endpointInfoCreator);
            stepData.put("EndpointInfo", endpointInfo);
        } catch (KapuaIllegalNullArgumentException ex) {
            verifyException(ex);
        }
    }

    @And("I create endpoint with schema {string}, domain {string}, port {int} and {string} secure field")
    public void iCreateEndpointWithSchemaDomainPortAndSecureField(String schema, String domainName, int port, String secureField) throws Throwable {
        EndpointInfoCreator endpointInfoCreator = new EndpointInfoCreator(getCurrentScopeId());
        endpointInfoCreator.setSchema(schema);
        endpointInfoCreator.setDns(domainName);
        endpointInfoCreator.setPort(port);
        endpointInfoCreator.setEndpointType(EndpointInfo.ENDPOINT_TYPE_RESOURCE);
        if (secureField.equals("ENABLED")) {
            endpointInfoCreator.setSecure(true);
        } else {
            endpointInfoCreator.setSecure(false);
        }
        try {
            EndpointInfo endpointInfo = endpointInfoService.create(endpointInfoCreator);
            stepData.put("EndpointInfo", endpointInfo);
        } catch (KapuaIllegalNullArgumentException ex) {
            verifyException(ex);
        }
    }

    @Then("I edit last created endpoint Secure field to {string}")
    public void iEditLastCreatedEndpointSecureField(String secureField) throws Throwable {
        EndpointInfo endpointInfo = (EndpointInfo) stepData.get("EndpointInfo");
        try {
            if (secureField.equals("ENABLED")) {
                endpointInfo.setSecure(true);
            } else {
                endpointInfo.setSecure(false);
            }
            endpointInfoService.update(endpointInfo);
        } catch (Exception e) {
            verifyException(e);
        }
    }

    @When("^I create a CORS filter with schema \"([^\"]*)\", domain \"([^\"]*)\" and port (\\d+)$")
    public void iCreateCORSFilter(String schema, String domain, int port) throws Exception {
        EndpointInfoCreator endpointInfoCreator = new EndpointInfoCreator(getCurrentScopeId());
        endpointInfoCreator.setSchema(schema);
        endpointInfoCreator.setDns(domain);
        endpointInfoCreator.setPort(port);
        endpointInfoCreator.setEndpointType(EndpointInfo.ENDPOINT_TYPE_CORS);

        try {
            EndpointInfo endpointInfo = endpointInfoService.create(endpointInfoCreator);
        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }

    @And("^I create the following CORS filters$")
    public void iCreateMultipleCORSFilter(DataTable corsFilters) throws Throwable {
        for (List<String> config : corsFilters.asLists()) {
            iCreateCORSFilter(config.get(0), config.get(1), Integer.parseInt(config.get(2)));
        }
    }

    @Then("I have (\\d+) CORS filters?$")
    public void iHaveCORSFilter(int expectedNum) throws KapuaException {
        int corsFilter = KapuaSecurityUtils.doPrivileged(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                EndpointInfoQuery endpointInfoQuery = new EndpointInfoQuery(getCurrentScopeId());
                EndpointInfoListResult corsFilters = endpointInfoService.query(endpointInfoQuery, EndpointInfo.ENDPOINT_TYPE_CORS);
                return corsFilters.getSize();
            }
        });
        Assert.assertEquals(expectedNum, corsFilter);
    }

    @And("^I delete all CORS filters")
    public void iDeleteAllCORSFilters() throws Exception {
        primeException();

        try {
            EndpointInfoQuery endpointInfoQuery = new EndpointInfoQuery(getCurrentScopeId());
            EndpointInfoListResult endpointsToDelete = endpointInfoService.query(endpointInfoQuery, EndpointInfo.ENDPOINT_TYPE_CORS);

            for (int i = 0; i < endpointsToDelete.getSize(); i++) {
                endpointInfoService.delete(getCurrentScopeId(), endpointsToDelete.getItem(i).getId());
            }

        } catch (KapuaException ex) {
            verifyException(ex);
        }
    }
}
