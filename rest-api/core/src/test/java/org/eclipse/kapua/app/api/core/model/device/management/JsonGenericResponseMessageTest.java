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
package org.eclipse.kapua.app.api.core.model.device.management;

import java.util.Date;
import java.util.UUID;

import org.eclipse.kapua.app.api.core.model.message.JsonKapuaPayload;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.device.management.request.message.response.GenericResponseChannel;
import org.eclipse.kapua.service.device.management.request.message.response.GenericResponseMessage;
import org.eclipse.kapua.service.device.management.request.message.response.GenericResponsePayload;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category(JUnitTests.class)
public class JsonGenericResponseMessageTest {

    GenericResponseMessage genericResponseMessage;
    UUID id;
    StorableId storableId;
    Date timestamp, receivedOn, sentOn, capturedOn;
    Date[] dates;
    KapuaId scopeId, deviceId;
    String clientId;
    KapuaPosition kapuaPosition;
    GenericResponseChannel genericResponseChannel;
    GenericResponsePayload genericResponsePayload;
    JsonGenericResponseMessage jsonGenericResponseMessage1, jsonGenericResponseMessage2;
    KapuaPayload newKapuaPayload;

    @Before
    public void initialize() {
        genericResponseMessage = Mockito.mock(GenericResponseMessage.class);
        id = new UUID(10L, 100L);
        storableId = Mockito.mock(StorableId.class);
        timestamp = new Date();
        scopeId = KapuaId.ONE;
        deviceId = KapuaId.ONE;
        clientId = "clientID";
        receivedOn = new Date();
        sentOn = new Date();
        capturedOn = new Date();
        dates = new Date[]{new Date(), new Date(99L), new Date(99999999999999L)};
        kapuaPosition = Mockito.mock(KapuaPosition.class);
        genericResponseChannel = Mockito.mock(GenericResponseChannel.class);
        genericResponsePayload = Mockito.mock(GenericResponsePayload.class);
        newKapuaPayload = Mockito.mock(KapuaPayload.class);

        Mockito.when(genericResponseMessage.getId()).thenReturn(id);
        Mockito.when(genericResponseMessage.getScopeId()).thenReturn(scopeId);
        Mockito.when(genericResponseMessage.getDeviceId()).thenReturn(deviceId);
        Mockito.when(genericResponseMessage.getClientId()).thenReturn(clientId);
        Mockito.when(genericResponseMessage.getReceivedOn()).thenReturn(receivedOn);
        Mockito.when(genericResponseMessage.getSentOn()).thenReturn(sentOn);
        Mockito.when(genericResponseMessage.getCapturedOn()).thenReturn(capturedOn);
        Mockito.when(genericResponseMessage.getPosition()).thenReturn(kapuaPosition);
        Mockito.when(genericResponseMessage.getChannel()).thenReturn(genericResponseChannel);
        Mockito.when(genericResponseMessage.getPayload()).thenReturn(genericResponsePayload);

        jsonGenericResponseMessage1 = new JsonGenericResponseMessage();
        jsonGenericResponseMessage2 = new JsonGenericResponseMessage(genericResponseMessage);
    }

    @Test
    public void jsonGenericResponseMessageWithoutParameterTest() {
        JsonGenericResponseMessage jsonGenericResponseMessage = new JsonGenericResponseMessage();

        Assert.assertNull("Null expected.", jsonGenericResponseMessage.getId());
        Assert.assertNull("Null expected.", jsonGenericResponseMessage.getScopeId());
        Assert.assertNull("Null expected.", jsonGenericResponseMessage.getDeviceId());
        Assert.assertNull("Null expected.", jsonGenericResponseMessage.getClientId());
        Assert.assertNull("Null expected.", jsonGenericResponseMessage.getReceivedOn());
        Assert.assertNull("Null expected.", jsonGenericResponseMessage.getSentOn());
        Assert.assertNull("Null expected.", jsonGenericResponseMessage.getCapturedOn());
        Assert.assertNull("Null expected.", jsonGenericResponseMessage.getPosition());
        Assert.assertNull("Null expected.", jsonGenericResponseMessage.getChannel());
        Assert.assertNull("Null expected.", jsonGenericResponseMessage.getPayload());
    }

    @Test
    public void jsonGenericResponseMessageWithParameterTest() {
        JsonGenericResponseMessage jsonGenericResponseMessage = new JsonGenericResponseMessage(genericResponseMessage);

        Assert.assertEquals("Expected and actual values should be the same.", id, jsonGenericResponseMessage.getId());
        Assert.assertEquals("Expected and actual values should be the same.", scopeId, jsonGenericResponseMessage.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", deviceId, jsonGenericResponseMessage.getDeviceId());
        Assert.assertEquals("Expected and actual values should be the same.", clientId, jsonGenericResponseMessage.getClientId());
        Assert.assertEquals("Expected and actual values should be the same.", receivedOn, jsonGenericResponseMessage.getReceivedOn());
        Assert.assertEquals("Expected and actual values should be the same.", sentOn, jsonGenericResponseMessage.getSentOn());
        Assert.assertEquals("Expected and actual values should be the same.", capturedOn, jsonGenericResponseMessage.getCapturedOn());
        Assert.assertEquals("Expected and actual values should be the same.", kapuaPosition, jsonGenericResponseMessage.getPosition());
        Assert.assertEquals("Expected and actual values should be the same.", genericResponseChannel, jsonGenericResponseMessage.getChannel());
        Assert.assertNotNull("NotNull expected.", jsonGenericResponseMessage.getPayload());
    }

    @Test(expected = NullPointerException.class)
    public void jsonGenericResponseMessageWithNullParameterTest() {
        new JsonGenericResponseMessage(null);
    }

    @Test
    public void setAndGetIdTest() {
        UUID newId = new UUID(1L, 99L);

        jsonGenericResponseMessage1.setId(newId);
        jsonGenericResponseMessage2.setId(newId);

        Assert.assertEquals("Expected and actual values should be the same.", newId, jsonGenericResponseMessage1.getId());
        Assert.assertEquals("Expected and actual values should be the same.", newId, jsonGenericResponseMessage2.getId());

        jsonGenericResponseMessage1.setId(null);
        jsonGenericResponseMessage2.setId(null);

        Assert.assertNull("Null expected.", jsonGenericResponseMessage1.getId());
        Assert.assertNull("Null expected.", jsonGenericResponseMessage2.getId());
    }

    @Test
    public void setAndGetScopeIdTest() {
        KapuaId newScopeId = KapuaId.ANY;

        jsonGenericResponseMessage1.setScopeId(newScopeId);
        jsonGenericResponseMessage2.setScopeId(newScopeId);

        Assert.assertEquals("Expected and actual values should be the same.", newScopeId, jsonGenericResponseMessage1.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", newScopeId, jsonGenericResponseMessage2.getScopeId());

        jsonGenericResponseMessage1.setScopeId(null);
        jsonGenericResponseMessage2.setScopeId(null);

        Assert.assertNull("Null expected.", jsonGenericResponseMessage1.getScopeId());
        Assert.assertNull("Null expected.", jsonGenericResponseMessage2.getScopeId());
    }

    @Test
    public void setAndGetDeviceIdTest() {
        KapuaId newDeviceId = KapuaId.ANY;

        jsonGenericResponseMessage1.setDeviceId(newDeviceId);
        jsonGenericResponseMessage2.setDeviceId(newDeviceId);

        Assert.assertEquals("Expected and actual values should be the same.", newDeviceId, jsonGenericResponseMessage1.getDeviceId());
        Assert.assertEquals("Expected and actual values should be the same.", newDeviceId, jsonGenericResponseMessage2.getDeviceId());

        jsonGenericResponseMessage1.setDeviceId(null);
        jsonGenericResponseMessage2.setDeviceId(null);

        Assert.assertNull("Null expected.", jsonGenericResponseMessage1.getDeviceId());
        Assert.assertNull("Null expected.", jsonGenericResponseMessage2.getDeviceId());
    }

    @Test
    public void setAndGetClientIdTest() {
        String[] newClientIDs = {"ID", ",.  id *&64930 new ID ,,,", "  NEW12 ,./)(*&%^% IDnew", "newID  98*90__=88id ", ",,,.id new ID 847&^3#@!  "};

        for (String newClientID : newClientIDs) {
            jsonGenericResponseMessage1.setClientId(newClientID);
            jsonGenericResponseMessage2.setClientId(newClientID);

            Assert.assertEquals("Expected and actual values should be the same.", newClientID, jsonGenericResponseMessage1.getClientId());
            Assert.assertEquals("Expected and actual values should be the same.", newClientID, jsonGenericResponseMessage2.getClientId());
        }

        jsonGenericResponseMessage1.setClientId(null);
        jsonGenericResponseMessage2.setClientId(null);

        Assert.assertNull("Null expected.", jsonGenericResponseMessage1.getClientId());
        Assert.assertNull("Null expected.", jsonGenericResponseMessage2.getClientId());
    }

    @Test
    public void setAndGetReceivedOnTest() {
        for (Date newReceivedOn : dates) {
            jsonGenericResponseMessage1.setReceivedOn(newReceivedOn);
            jsonGenericResponseMessage2.setReceivedOn(newReceivedOn);

            Assert.assertEquals("Expected and actual values should be the same.", newReceivedOn, jsonGenericResponseMessage1.getReceivedOn());
            Assert.assertEquals("Expected and actual values should be the same.", newReceivedOn, jsonGenericResponseMessage2.getReceivedOn());
        }

        jsonGenericResponseMessage1.setReceivedOn(null);
        jsonGenericResponseMessage2.setReceivedOn(null);

        Assert.assertNull("Null expected.", jsonGenericResponseMessage1.getReceivedOn());
        Assert.assertNull("Null expected.", jsonGenericResponseMessage2.getReceivedOn());
    }

    @Test
    public void setAndGetSentOnTest() {
        for (Date newSentOn : dates) {
            jsonGenericResponseMessage1.setSentOn(newSentOn);
            jsonGenericResponseMessage2.setSentOn(newSentOn);

            Assert.assertEquals("Expected and actual values should be the same.", newSentOn, jsonGenericResponseMessage1.getSentOn());
            Assert.assertEquals("Expected and actual values should be the same.", newSentOn, jsonGenericResponseMessage2.getSentOn());
        }

        jsonGenericResponseMessage1.setSentOn(null);
        jsonGenericResponseMessage2.setSentOn(null);

        Assert.assertNull("Null expected.", jsonGenericResponseMessage1.getSentOn());
        Assert.assertNull("Null expected.", jsonGenericResponseMessage2.getSentOn());
    }

    @Test
    public void setAndGetCapturedOnTest() {
        for (Date newCapturedOn : dates) {
            jsonGenericResponseMessage1.setCapturedOn(newCapturedOn);
            jsonGenericResponseMessage2.setCapturedOn(newCapturedOn);

            Assert.assertEquals("Expected and actual values should be the same.", newCapturedOn, jsonGenericResponseMessage1.getCapturedOn());
            Assert.assertEquals("Expected and actual values should be the same.", newCapturedOn, jsonGenericResponseMessage2.getCapturedOn());
        }

        jsonGenericResponseMessage1.setCapturedOn(null);
        jsonGenericResponseMessage2.setCapturedOn(null);

        Assert.assertNull("Null expected.", jsonGenericResponseMessage1.getCapturedOn());
        Assert.assertNull("Null expected.", jsonGenericResponseMessage2.getCapturedOn());
    }

    @Test
    public void setAndGetPositionTest() {
        KapuaPosition newPosition = Mockito.mock(KapuaPosition.class);

        jsonGenericResponseMessage1.setPosition(newPosition);
        jsonGenericResponseMessage2.setPosition(newPosition);

        Assert.assertEquals("Expected and actual values should be the same.", newPosition, jsonGenericResponseMessage1.getPosition());
        Assert.assertEquals("Expected and actual values should be the same.", newPosition, jsonGenericResponseMessage2.getPosition());

        jsonGenericResponseMessage1.setPosition(null);
        jsonGenericResponseMessage2.setPosition(null);

        Assert.assertNull("Null expected.", jsonGenericResponseMessage1.getPosition());
        Assert.assertNull("Null expected.", jsonGenericResponseMessage2.getPosition());
    }

    @Test
    public void setAndGetChannelTest() {
        GenericResponseChannel newGenericResponseChannel = Mockito.mock(GenericResponseChannel.class);

        jsonGenericResponseMessage1.setChannel(newGenericResponseChannel);
        jsonGenericResponseMessage2.setChannel(newGenericResponseChannel);

        Assert.assertEquals("Expected and actual values should be the same.", newGenericResponseChannel, jsonGenericResponseMessage1.getChannel());
        Assert.assertEquals("Expected and actual values should be the same.", newGenericResponseChannel, jsonGenericResponseMessage2.getChannel());

        jsonGenericResponseMessage1.setChannel(null);
        jsonGenericResponseMessage2.setChannel(null);

        Assert.assertNull("Null expected.", jsonGenericResponseMessage1.getChannel());
        Assert.assertNull("Null expected.", jsonGenericResponseMessage2.getChannel());
    }

    @Test
    public void setAndGetJsonKapuaPayloadTest() {
        JsonKapuaPayload newJsonKapuaPayload = Mockito.mock(JsonKapuaPayload.class);

        jsonGenericResponseMessage1.setPayload(newJsonKapuaPayload);
        jsonGenericResponseMessage2.setPayload(newJsonKapuaPayload);

        Assert.assertEquals("Expected and actual values should be the same.", newJsonKapuaPayload, jsonGenericResponseMessage1.getPayload());
        Assert.assertEquals("Expected and actual values should be the same.", newJsonKapuaPayload, jsonGenericResponseMessage2.getPayload());

        jsonGenericResponseMessage1.setPayload((JsonKapuaPayload) null);
        jsonGenericResponseMessage2.setPayload((JsonKapuaPayload) null);

        Assert.assertNull("Null expected.", jsonGenericResponseMessage1.getPayload());
        Assert.assertNull("Null expected.", jsonGenericResponseMessage2.getPayload());
    }

    @Test(expected = NullPointerException.class)
    public void setAndGetKapuaPayloadMessageWithoutParametersTest() {
        jsonGenericResponseMessage1.setPayload(newKapuaPayload);

        Assert.assertNotNull("NotNull expected.", jsonGenericResponseMessage1.getPayload());

        jsonGenericResponseMessage1.setPayload((KapuaPayload) null);
    }

    @Test(expected = NullPointerException.class)
    public void setAndGetKapuaPayloadMessageWithParameterTest() {
        jsonGenericResponseMessage2.setPayload(newKapuaPayload);

        Assert.assertNotNull("NotNull expected.", jsonGenericResponseMessage2.getPayload());

        jsonGenericResponseMessage2.setPayload((KapuaPayload) null);
    }
}