/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.qa.integration.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.guice.ScenarioScoped;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.authentication.token.AccessToken;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserListResult;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@ScenarioScoped
public class RestClientSteps {

    private static final Logger logger = LoggerFactory.getLogger(RestClientSteps.class);

    private static final String TOKEN_ID = "tokenId"; //jwt
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String REST_RESPONSE = "restResponse";
    private static final String REST_RESPONSE_CODE = "restResponseCode";
    private static final String REST_RESPONSE_HEADERS = "restResponseHeaders";

    /**
     * Scenario scoped step data.
     */
    private StepData stepData;

    @Inject
    public RestClientSteps(StepData stepData) {
        this.stepData = stepData;
    }

    @When("REST GET call at {string}")
    public void restGetCall(String resource) throws Exception {
        String host = (String) stepData.get("host");
        String port = (String) stepData.get("port");
        String tokenId = (String) stepData.get(TOKEN_ID);
        resource = insertStepData(resource);
        URL url = new URL("http://" + host + ":" + port + resource);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try (AutoCloseable cconn = () -> conn.disconnect()) {
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept-Language", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            if (tokenId != null) {
                conn.setRequestProperty("Authorization", "Bearer " + tokenId);
            }
            int httpRespCode = conn.getResponseCode();
            if (httpRespCode == 200) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())))) {
                    String output;
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                }
                stepData.put(REST_RESPONSE, sb.toString());
            }
            stepData.put(REST_RESPONSE_CODE, httpRespCode);
        } catch (IOException ioe) {
            logger.error("Exception on REST GET call execution: " + resource);
            throw ioe;
        }
    }

    @When("REST POST call at {string} with JSON {string}")
    public void restPostCallWithJson(String resource, String json) throws Exception {
        String host = (String) stepData.get("host");
        String port = (String) stepData.get("port");
        String tokenId = (String) stepData.get(TOKEN_ID);
        resource = insertStepData(resource);
        json = insertStepData(json);
        URL url = new URL("http://" + host + ":" + port + resource);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try (AutoCloseable cconn = () -> conn.disconnect()) {
            conn.setRequestProperty("Accept-Language", "UTF-8");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty("Accept", "application/json");
            if (tokenId != null) {
                conn.setRequestProperty("Authorization", "Bearer " + tokenId);
            }
            conn.setDoOutput(true);
            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream())) {
                outputStreamWriter.write(json);
                outputStreamWriter.flush();
            }
            int httpRespCode = conn.getResponseCode();
            if (httpRespCode == 200) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())))) {
                    String output;
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                }
                stepData.put(REST_RESPONSE, sb.toString());
            }
            stepData.put(REST_RESPONSE_CODE, httpRespCode);
        } catch (IOException ioe) {
            logger.error("Exception on REST POST call execution: " + resource);
            throw ioe;
        }
    }

    @When("REST {string} call at {string} with JSON {string}")
    public void restCall(String method, String resource, String json) throws Exception {
        restCallInternal(method, resource, json, true);
    }

    @When("REST {string} call at {string} with JSON {string} in cross-site mode with origin {string}")
    public void restCallCrossSite(String method, String resource, String json, String origin) throws Exception {
        restCallInternal(method, resource, json, true,
                com.google.common.net.HttpHeaders.ORIGIN, origin,
                com.google.common.net.HttpHeaders.SEC_FETCH_SITE, "cross-site");
    }

    public void restCallInternal(String method, String resource, String json, boolean authenticateCall, String... additionalHeaders) throws Exception {
        // Create an instance of HttpClient
        HttpClient httpClient = HttpClient.newHttpClient();
        String host = (String) stepData.get("host");
        String port = (String) stepData.get("port");

        resource = insertStepData(resource);
        // Define the URL you want to send the GET request to
        String url = "http://" + host + ":" + port + resource;

        HttpRequest.Builder baseBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept-Language", "UTF-8")
                .header("accept", "application/json");

        if (authenticateCall) {
            String tokenId = (String) stepData.get(TOKEN_ID);
            baseBuilder.setHeader("Authorization", "Bearer " + tokenId);
        }

        if (method.equals("POST")) {
            baseBuilder.setHeader("Content-Type", "application/json");
            baseBuilder.POST(HttpRequest.BodyPublishers.ofString(json));
        }
        else if (method.equals("GET")) {
            baseBuilder.GET();
        } else if (method.equals("PUT")) {
            baseBuilder.setHeader("Content-Type", "application/json");
            baseBuilder.PUT(HttpRequest.BodyPublishers.ofString(json));
        }

        if (additionalHeaders != null) {
            if (additionalHeaders.length % 2 != 0) {
                throw new IllegalArgumentException("Number of additional headers must be even");
            }
            for (int i = 0; i < additionalHeaders.length; i += 2) {
                String name = additionalHeaders[i];
                String value = additionalHeaders[i + 1];
                baseBuilder.setHeader(name, value);
            }
        }

        // Create an HttpRequest object
        HttpRequest request = baseBuilder
                .build();

        try {
            // Send the request and retrieve the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Print out the response status code
            System.out.println("Response Code: " + response.statusCode());
            stepData.put(REST_RESPONSE_CODE, response.statusCode());

            // Print out the response body
            System.out.println("Response Body: " + response.body());
            stepData.put(REST_RESPONSE, response.body());

            stepData.put(REST_RESPONSE_HEADERS, response.headers());

        } catch (Exception e) {
            // Handle exceptions
            logger.error("Exception on REST POST call execution: " + resource);
            throw e;
        }
    }

    @When("I try to authenticate with a cross-site call with origin {string} using json {string}")
    public void createCustomCrossSiteCall(String origin, String authJson) throws Exception {
        restCallInternal("POST", "/v1/authentication/user",
                authJson, false,
                com.google.common.net.HttpHeaders.ORIGIN, origin,
                com.google.common.net.HttpHeaders.SEC_FETCH_SITE, "cross-site");
    }

    @When("I try to authenticate with a same-site call")
    public void createSameOriginCall() throws Exception {
        restCallInternal("POST", "/v1/authentication/user",
                "{\"password\": \"kapua-password\", \"username\": \"kapua-sys\"}", false,
                com.google.common.net.HttpHeaders.SEC_FETCH_SITE, "same-site");
    }

    @When("I refresh last access token")
    public void refreshToken() throws Exception {
        String tokenId = (String) stepData.get(TOKEN_ID);
        String refreshToken = (String) stepData.get(REFRESH_TOKEN);
        String resource = "/v1/authentication/refresh";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("refreshToken", refreshToken);
        jsonObject.put("tokenId", tokenId);
        restCallInternal("POST", resource, jsonObject.toString(), false);
    }

    @When("I refresh access token using refresh token {string} and jwt {string}")
    public void refreshPreciseToken(String refreshToken, String jwt) throws Exception {
        if (!jwt.isEmpty()) {
            stepData.put(TOKEN_ID, jwt);
        }
        if (!refreshToken.isEmpty()) {
            stepData.put(REFRESH_TOKEN, refreshToken);
        }
        refreshToken();
    }

    @And("I extract {string} from the response and I save it in the key {string}")
    public void exctractFromResponse(String field, String key) throws JsonProcessingException {
        String response = (String) stepData.get(REST_RESPONSE);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response);
        stepData.put(key,jsonResponse.get(field).toString().replace("\"", ""));
        String test = (String) stepData.get(key);
    }

    @Then("REST response containing text {string}")
    public void restResponseContaining(String checkStr) throws Exception {
        String restResponse = (String) stepData.get(REST_RESPONSE);
        Assert.assertTrue(String.format("Response %s doesn't include %s.", restResponse, checkStr),
                restResponse.contains(checkStr));
    }

    @Then("I expect {string} header in the response with value {string}")
    public void restResponseHeaderContaining(String headerName,String headerValue) throws Exception {
        java.net.http.HttpHeaders headers = (java.net.http.HttpHeaders) stepData.get(REST_RESPONSE_HEADERS);
        String headerValueResponse = headers.firstValue(headerName).orElseThrow(() -> new Exception("The searched header is not present in the response!"));
        Assert.assertEquals(headerValueResponse, headerValue);
    }

    @Then("I expect no {string} header in the response")
    public void restResponseHeaderNotContaining(String headerName) {
        java.net.http.HttpHeaders headers = (java.net.http.HttpHeaders) stepData.get(REST_RESPONSE_HEADERS);
        Assert.assertFalse(headers.firstValue(headerName).isPresent());
    }

    @Then("REST response containing Account")
    public void restResponseContainingAccount() throws Exception {
        String restResponse = (String) stepData.get(REST_RESPONSE);
        Account account = XmlUtil.unmarshalJson(restResponse, Account.class);
        KapuaId accId = account.getId();
        System.out.println("Account Id = " + accId);
        stepData.put("lastAccountId", accId.toStringId());
        stepData.put("lastAccountCompactId", accId.toCompactId());
    }

    @Then("REST response containing {string} with prefix account {string}")
    public void restResponseContainingPrefixVar(String checkStr, String var) {
        String restResponse = (String) stepData.get(REST_RESPONSE);
        Account account = (Account) stepData.get(var);
        Assert.assertTrue(String.format("Response %s doesn't include %s.", restResponse, account.getId() + "-data-message" + checkStr),
                restResponse.contains(account.getId() + "-data-message" + checkStr));
    }

    @Then("REST response containing AccessToken")
    public void restResponseContainingAccessToken() throws Exception {
        String restResponse = (String) stepData.get(REST_RESPONSE);
        AccessToken token = XmlUtil.unmarshalJson(restResponse, AccessToken.class);
        Assert.assertTrue("Token is null.", token.getTokenId() != null);
        stepData.put(TOKEN_ID, token.getTokenId());
        stepData.put(REFRESH_TOKEN, token.getRefreshToken());
    }

    @Then("REST response containing User")
    public void restResponseContainingUser() throws Exception {
        String restResponse = (String) stepData.get(REST_RESPONSE);
        User user = XmlUtil.unmarshalJson(restResponse, User.class);
        stepData.put("lastUserCompactId", user.getId().toCompactId());
    }

    @Then("REST response contains list of Users")
    public void restResponseContainsUsers() throws Exception {
        String restResponse = (String) stepData.get(REST_RESPONSE);
        UserListResult userList = XmlUtil.unmarshalJson(restResponse, UserListResult.class);
        Assert.assertFalse("Retrieved user list should NOT be empty.", userList.isEmpty());
    }

    @Then("REST response doesn't contain User")
    public void restResponseDoesntContainUser() throws Exception {
        String restResponse = (String) stepData.get(REST_RESPONSE);
        User user = XmlUtil.unmarshalJson(restResponse, User.class);
        Assert.assertTrue("There should be NO User retrieved.", user == null);
    }

    @Then("REST response code is {int}")
    public void restResponseDoesntContainUser(int expeted) throws Exception {
        int restResponseCode = (Integer) stepData.get(REST_RESPONSE_CODE);
        Assert.assertEquals("Wrong response code.", expeted, restResponseCode);
    }

    @Then("^REST response contains limitExceed field with value (true|false)$")
    public void restResponseContainsLimitExceedValueWithValue(String value) throws Exception {
        String restResponse = (String) stepData.get(REST_RESPONSE);
        UserListResult userList = XmlUtil.unmarshalJson(restResponse, UserListResult.class);
        Assert.assertEquals(Boolean.parseBoolean(value), userList.isLimitExceeded());
    }

    @Given("^An authenticated user$")
    public void anAuthenticationToken() throws Exception {
        restCallInternal("POST", "/v1/authentication/user",
                "{\"password\": \"kapua-password\", \"username\": \"kapua-sys\"}", false);
        restResponseContainingAccessToken();
    }

    @Given("^(\\d+) new users? created$")
    public void newUsersCreated(int howManyUser) throws Exception {
        for (int i = 0; i < howManyUser; ++i) {
            restPostCallWithJson("/_/users", String.format("{\"name\": \"new-user-%d\"}", i));
        }
    }

    /**
     * Take input parameter and replace its $var$ with value of var that is stored
     * in step data.
     *
     * @param template string that gets parameters replaced with value
     * @return string with inserted parameter values
     */
    private String insertStepData(String template) {
        List<String> keys = stepData.getKeys();
        for (String key : keys) {
            Object oValue = stepData.get(key);
            if (oValue instanceof String) {
                String value = (String) oValue;
                template = template.replace("" + key + "", value);
            }
        }

        return template;
    }

    // TODO move this step in common steps
    @Given("Move step data {string} to {string}")
    public void moveStepData(String keyFrom, String keyTo) {
        Object valueFrom = stepData.get(keyFrom);
        stepData.put(keyTo, valueFrom);
    }

    // TODO move this step in common steps
    @Given("Move Account compact id from step data {string} to {string}")
    public void moveAccountCompactIdStepData(String keyFrom, String keyTo) {
        Account account = (Account) stepData.get(keyFrom);
        stepData.put(keyTo, account.getId().toCompactId());
    }

    // TODO move this step in common steps
    @Given("Clear step data with key {string}")
    public void clearStepData(String key) {
        stepData.remove(key);
    }
}
