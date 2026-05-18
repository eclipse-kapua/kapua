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
package org.eclipse.kapua.app.api.core.model.data;

import org.eclipse.kapua.app.api.core.model.message.JsonKapuaPayload;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.message.device.data.KapuaDataChannel;
import org.eclipse.kapua.message.device.data.KapuaDataMessage;
import org.eclipse.kapua.message.device.data.KapuaDataPayload;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.Date;
import java.util.UUID;


@Category(JUnitTests.class)
public class JsonKapuaDataMessageTest {

    KapuaDataMessage kapuaDataMessage;
    UUID id;
    StorableId storableId;
    Date timestamp, receivedOn, sentOn, capturedOn;
    Date[] dates;
    KapuaId scopeId, deviceId;
    String clientId;
    KapuaPosition kapuaPosition;
    KapuaDataChannel kapuaDataChannel;
    KapuaDataPayload kapuaPayload;
    JsonKapuaDataMessage jsonKapuaDataMessage1, jsonKapuaDataMessage2;

    @Before
    public void initialize() {
        kapuaDataMessage = Mockito.mock(KapuaDataMessage.class);
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
        kapuaDataChannel = Mockito.mock(KapuaDataChannel.class);
        kapuaPayload = Mockito.mock(KapuaDataPayload.class);

        Mockito.when(kapuaDataMessage.getId()).thenReturn(id);
        Mockito.when(kapuaDataMessage.getScopeId()).thenReturn(scopeId);
        Mockito.when(kapuaDataMessage.getDeviceId()).thenReturn(deviceId);
        Mockito.when(kapuaDataMessage.getClientId()).thenReturn(clientId);
        Mockito.when(kapuaDataMessage.getReceivedOn()).thenReturn(receivedOn);
        Mockito.when(kapuaDataMessage.getSentOn()).thenReturn(sentOn);
        Mockito.when(kapuaDataMessage.getCapturedOn()).thenReturn(capturedOn);
        Mockito.when(kapuaDataMessage.getPosition()).thenReturn(kapuaPosition);
        Mockito.when(kapuaDataMessage.getChannel()).thenReturn(kapuaDataChannel);
        Mockito.when(kapuaDataMessage.getPayload()).thenReturn(kapuaPayload);

        jsonKapuaDataMessage1 = new JsonKapuaDataMessage(kapuaDataMessage);
        jsonKapuaDataMessage2 = new JsonKapuaDataMessage();
    }

    @Test
    public void jsonKapuaDataMessageWithoutParameterTest() {
        JsonKapuaDataMessage jsonKapuaDataMessage = new JsonKapuaDataMessage();

        Assert.assertNull("Null expected.", jsonKapuaDataMessage.getId());
        Assert.assertNull("Null expected.", jsonKapuaDataMessage.getScopeId());
        Assert.assertNull("Null expected.", jsonKapuaDataMessage.getDeviceId());
        Assert.assertNull("Null expected.", jsonKapuaDataMessage.getClientId());
        Assert.assertNull("Null expected.", jsonKapuaDataMessage.getReceivedOn());
        Assert.assertNull("Null expected.", jsonKapuaDataMessage.getSentOn());
        Assert.assertNull("Null expected.", jsonKapuaDataMessage.getCapturedOn());
        Assert.assertNull("Null expected.", jsonKapuaDataMessage.getPosition());
        Assert.assertNull("Null expected.", jsonKapuaDataMessage.getChannel());
        Assert.assertNull("Null expected.", jsonKapuaDataMessage.getPayload());
    }

    @Test
    public void jsonKapuaDataMessageWithParameterTest() {
        JsonKapuaDataMessage jsonKapuaDataMessage = new JsonKapuaDataMessage(kapuaDataMessage);

        Assert.assertEquals("Expected and actual values should be the same.", id, jsonKapuaDataMessage.getId());
        Assert.assertEquals("Expected and actual values should be the same.", scopeId, jsonKapuaDataMessage.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", deviceId, jsonKapuaDataMessage.getDeviceId());
        Assert.assertEquals("Expected and actual values should be the same.", clientId, jsonKapuaDataMessage.getClientId());
        Assert.assertEquals("Expected and actual values should be the same.", receivedOn, jsonKapuaDataMessage.getReceivedOn());
        Assert.assertEquals("Expected and actual values should be the same.", sentOn, jsonKapuaDataMessage.getSentOn());
        Assert.assertEquals("Expected and actual values should be the same.", capturedOn, jsonKapuaDataMessage.getCapturedOn());
        Assert.assertEquals("Expected and actual values should be the same.", kapuaPosition, jsonKapuaDataMessage.getPosition());
        Assert.assertEquals("Expected and actual values should be the same.", kapuaDataChannel, jsonKapuaDataMessage.getChannel());
        Assert.assertNotNull("NotNull expected.", jsonKapuaDataMessage.getPayload());
    }

    @Test(expected = NullPointerException.class)
    public void jsonKapuaDataMessageWithNullParameterTest() {
        new JsonKapuaDataMessage(null);
    }

    @Test
    public void setAndGetIdTest() {
        UUID newId = new UUID(1L, 99L);

        jsonKapuaDataMessage1.setId(newId);
        jsonKapuaDataMessage2.setId(newId);

        Assert.assertEquals("Expected and actual values should be the same.", newId, jsonKapuaDataMessage1.getId());
        Assert.assertEquals("Expected and actual values should be the same.", newId, jsonKapuaDataMessage2.getId());

        jsonKapuaDataMessage1.setId(null);
        jsonKapuaDataMessage2.setId(null);

        Assert.assertNull("Null expected.", jsonKapuaDataMessage1.getId());
        Assert.assertNull("Null expected.", jsonKapuaDataMessage2.getId());
    }

    @Test
    public void setAndGetScopeIdTest() {
        KapuaId newScopeId = KapuaId.ANY;

        jsonKapuaDataMessage1.setScopeId(newScopeId);
        jsonKapuaDataMessage2.setScopeId(newScopeId);

        Assert.assertEquals("Expected and actual values should be the same.", newScopeId, jsonKapuaDataMessage1.getScopeId());
        Assert.assertEquals("Expected and actual values should be the same.", newScopeId, jsonKapuaDataMessage2.getScopeId());

        jsonKapuaDataMessage1.setScopeId(null);
        jsonKapuaDataMessage2.setScopeId(null);

        Assert.assertNull("Null expected.", jsonKapuaDataMessage1.getScopeId());
        Assert.assertNull("Null expected.", jsonKapuaDataMessage2.getScopeId());
    }

    @Test
    public void setAndGetDeviceIdTest() {
        KapuaId newDeviceId = KapuaId.ANY;

        jsonKapuaDataMessage1.setDeviceId(newDeviceId);
        jsonKapuaDataMessage2.setDeviceId(newDeviceId);

        Assert.assertEquals("Expected and actual values should be the same.", newDeviceId, jsonKapuaDataMessage1.getDeviceId());
        Assert.assertEquals("Expected and actual values should be the same.", newDeviceId, jsonKapuaDataMessage2.getDeviceId());

        jsonKapuaDataMessage1.setDeviceId(null);
        jsonKapuaDataMessage2.setDeviceId(null);

        Assert.assertNull("Null expected.", jsonKapuaDataMessage1.getDeviceId());
        Assert.assertNull("Null expected.", jsonKapuaDataMessage2.getDeviceId());
    }

    @Test
    public void setAndGetClientIdTest() {
        String[] newIds = {"idNEW12<>$*%7464", "", "id123-NEW 1  ^54IDnew 32^$%$", "idNEW    !@#$%^  123&*<>|  ", "  id 12NEW;':...,, id ()   ", "!@#id '>? id ID ,,12NEW1$#  ", "idNew*(-0123 idID 0123!@@,.,,"};

        for (String newClientID : newIds) {
            jsonKapuaDataMessage1.setClientId(newClientID);
            jsonKapuaDataMessage2.setClientId(newClientID);

            Assert.assertEquals("Expected and actual values should be the same.", newClientID, jsonKapuaDataMessage1.getClientId());
            Assert.assertEquals("Expected and actual values should be the same.", newClientID, jsonKapuaDataMessage2.getClientId());
        }

        jsonKapuaDataMessage1.setClientId(null);
        jsonKapuaDataMessage2.setClientId(null);

        Assert.assertNull("Null expected.", jsonKapuaDataMessage1.getClientId());
        Assert.assertNull("Null expected.", jsonKapuaDataMessage2.getClientId());
    }

    @Test
    public void setAndGetReceivedOnTest() {
        for (Date newReceivedOn : dates) {
            jsonKapuaDataMessage1.setReceivedOn(newReceivedOn);
            jsonKapuaDataMessage2.setReceivedOn(newReceivedOn);

            Assert.assertEquals("Expected and actual values should be the same.", newReceivedOn, jsonKapuaDataMessage1.getReceivedOn());
            Assert.assertEquals("Expected and actual values should be the same.", newReceivedOn, jsonKapuaDataMessage2.getReceivedOn());
        }

        jsonKapuaDataMessage1.setReceivedOn(null);
        jsonKapuaDataMessage2.setReceivedOn(null);

        Assert.assertNull("Null expected.", jsonKapuaDataMessage1.getReceivedOn());
        Assert.assertNull("Null expected.", jsonKapuaDataMessage2.getReceivedOn());
    }

    @Test
    public void setAndGetSentOnTest() {
        for (Date newSentOn : dates) {
            jsonKapuaDataMessage1.setSentOn(newSentOn);
            jsonKapuaDataMessage2.setSentOn(newSentOn);

            Assert.assertEquals("Expected and actual values should be the same.", newSentOn, jsonKapuaDataMessage1.getSentOn());
            Assert.assertEquals("Expected and actual values should be the same.", newSentOn, jsonKapuaDataMessage2.getSentOn());
        }

        jsonKapuaDataMessage1.setSentOn(null);
        jsonKapuaDataMessage2.setSentOn(null);

        Assert.assertNull("Null expected.", jsonKapuaDataMessage1.getSentOn());
        Assert.assertNull("Null expected.", jsonKapuaDataMessage2.getSentOn());
    }

    @Test
    public void setAndGetCapturedOnTest() {
        for (Date newCapturedOn : dates) {
            jsonKapuaDataMessage1.setCapturedOn(newCapturedOn);
            jsonKapuaDataMessage2.setCapturedOn(newCapturedOn);

            Assert.assertEquals("Expected and actual values should be the same.", newCapturedOn, jsonKapuaDataMessage1.getCapturedOn());
            Assert.assertEquals("Expected and actual values should be the same.", newCapturedOn, jsonKapuaDataMessage2.getCapturedOn());
        }

        jsonKapuaDataMessage1.setCapturedOn(null);
        jsonKapuaDataMessage2.setCapturedOn(null);

        Assert.assertNull("Null expected.", jsonKapuaDataMessage1.getCapturedOn());
        Assert.assertNull("Null expected.", jsonKapuaDataMessage2.getCapturedOn());
    }

    @Test
    public void setAndGetPositionTest() {
        KapuaPosition newPosition = Mockito.mock(KapuaPosition.class);

        jsonKapuaDataMessage1.setPosition(newPosition);
        jsonKapuaDataMessage2.setPosition(newPosition);

        Assert.assertEquals("Expected and actual values should be the same.", newPosition, jsonKapuaDataMessage1.getPosition());
        Assert.assertEquals("Expected and actual values should be the same.", newPosition, jsonKapuaDataMessage2.getPosition());

        jsonKapuaDataMessage1.setPosition(null);
        jsonKapuaDataMessage2.setPosition(null);

        Assert.assertNull("Null expected.", jsonKapuaDataMessage1.getPosition());
        Assert.assertNull("Null expected.", jsonKapuaDataMessage2.getPosition());
    }

    @Test
    public void setAndGetChannelTest() {
        KapuaDataChannel newChannel = Mockito.mock(KapuaDataChannel.class);

        jsonKapuaDataMessage1.setChannel(newChannel);
        jsonKapuaDataMessage2.setChannel(newChannel);

        Assert.assertEquals("Expected and actual values should be the same.", newChannel, jsonKapuaDataMessage1.getChannel());
        Assert.assertEquals("Expected and actual values should be the same.", newChannel, jsonKapuaDataMessage2.getChannel());

        jsonKapuaDataMessage1.setChannel(null);
        jsonKapuaDataMessage2.setChannel(null);

        Assert.assertNull("Null expected.", jsonKapuaDataMessage1.getChannel());
        Assert.assertNull("Null expected.", jsonKapuaDataMessage2.getChannel());
    }

    @Test
    public void setAndGetJsonKapuaPayloadTest() {
        JsonKapuaPayload newJsonKapuaPayload = Mockito.mock(JsonKapuaPayload.class);

        jsonKapuaDataMessage1.setPayload(newJsonKapuaPayload);
        jsonKapuaDataMessage2.setPayload(newJsonKapuaPayload);

        Assert.assertEquals("Expected and actual values should be the same.", newJsonKapuaPayload, jsonKapuaDataMessage1.getPayload());
        Assert.assertEquals("Expected and actual values should be the same.", newJsonKapuaPayload, jsonKapuaDataMessage2.getPayload());

        jsonKapuaDataMessage1.setPayload((JsonKapuaPayload) null);
        jsonKapuaDataMessage2.setPayload((JsonKapuaPayload) null);

        Assert.assertNull("Null expected.", jsonKapuaDataMessage1.getPayload());
        Assert.assertNull("Null expected.", jsonKapuaDataMessage2.getPayload());
    }

    @Test
    public void setAndGetKapuaPayloadTest() {
        KapuaPayload newKapuaPayload = Mockito.mock(KapuaPayload.class);

        jsonKapuaDataMessage1.setPayload(newKapuaPayload);
        jsonKapuaDataMessage2.setPayload(newKapuaPayload);

        Assert.assertNotNull("NotNull expected.", jsonKapuaDataMessage1.getPayload());
        Assert.assertNotNull("NotNull expected.", jsonKapuaDataMessage2.getPayload());

        try {
            jsonKapuaDataMessage1.setPayload((KapuaPayload) null);
            Assert.fail("NullPointerException expected.");
        } catch (Exception e) {
            Assert.assertTrue("NullPointerException expected.", e instanceof NullPointerException);
        }

        try {
            jsonKapuaDataMessage2.setPayload((KapuaPayload) null);
            Assert.fail("NullPointerException expected.");
        } catch (Exception e) {
            Assert.assertTrue("NullPointerException expected.", e instanceof NullPointerException);
        }
    }
} 