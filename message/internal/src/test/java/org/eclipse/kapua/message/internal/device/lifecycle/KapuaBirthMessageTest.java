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
 *******************************************************************************/
package org.eclipse.kapua.message.internal.device.lifecycle;

import org.eclipse.kapua.message.device.lifecycle.KapuaBirthChannel;
import org.eclipse.kapua.message.device.lifecycle.KapuaBirthMessage;
import org.eclipse.kapua.message.device.lifecycle.KapuaBirthPayload;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class KapuaBirthMessageTest {

    private static final String PAYLOAD_DISPLAY_STR = "" +
            "acceptEncoding=UTF-8" +
            "~~applicationFramework=applicationFramework" +
            "~~applicationFrameworkVersion=applicationFrameworkV-1" +
            "~~applicationIdentifiers=applicationIdentifiers" +
            "~~availableProcessors=1" +
            "~~bios=bios" +
            "~~biosVersion=biosV-1" +
            "~~connectionInterface=connectionInterface" +
            "~~connectionIp=192.168.1.1" +
            "~~containerFramework=containerFramework" +
            "~~containerFrameworkVersion=containerFrameworkV-1" +
            "~~displayName=Display Name" +
            "~~extendedProperties=" + "{\n  \"version\": \"1.0\",\n  \"properties\": {\n    \"GroupName\": {\n      \"propertyName\": \"propertyValue\"\n    }\n  }\n}" +
            "~~firmware=firmware-1" +
            "~~firmwareVersion=firmwareV-1" +
            "~~jvm=Oracle HotSpot" +
            "~~jvmProfile=desktop" +
            "~~jvmVersion=8" +
            "~~modelId=Model Id-1" +
            "~~modelName=Model Name" +
            "~~modemIccid=8991101200003204510" +
            "~~modemImei=49-015420-323751" +
            "~~modemImsi=359881234567890" +
            "~~os=Linux~~osArch=Linux x86" +
            "~~osVersion=osV-1" +
            "~~partNumber=part-1" +
            "~~serialNumber=SN-123" +
            "~~tamperStatus=NOT_TAMPERED" +
            "~~totalMemory=4" +
            "~~uptime=12";

    @Test
    public void kapuaBirthPayloadInitConstructor() {
        KapuaBirthPayload kapuaBirthPayload = populateKapuaBirthPayload();

        Assert.assertEquals("12", kapuaBirthPayload.getUptime());
        Assert.assertEquals("Display Name", kapuaBirthPayload.getDisplayName());
        Assert.assertEquals("Model Name", kapuaBirthPayload.getModelName());
        Assert.assertEquals("Model Id-1", kapuaBirthPayload.getModelId());
        Assert.assertEquals("part-1", kapuaBirthPayload.getPartNumber());
        Assert.assertEquals("SN-123", kapuaBirthPayload.getSerialNumber());
        Assert.assertEquals("firmware-1", kapuaBirthPayload.getFirmware());
        Assert.assertEquals("firmwareV-1", kapuaBirthPayload.getFirmwareVersion());
        Assert.assertEquals("bios", kapuaBirthPayload.getBios());
        Assert.assertEquals("biosV-1", kapuaBirthPayload.getBiosVersion());
        Assert.assertEquals("Linux", kapuaBirthPayload.getOs());
        Assert.assertEquals("osV-1", kapuaBirthPayload.getOsVersion());
        Assert.assertEquals("Oracle HotSpot", kapuaBirthPayload.getJvm());
        Assert.assertEquals("8", kapuaBirthPayload.getJvmVersion());
        Assert.assertEquals("desktop", kapuaBirthPayload.getJvmProfile());
        Assert.assertEquals("containerFramework", kapuaBirthPayload.getContainerFramework());
        Assert.assertEquals("containerFrameworkV-1", kapuaBirthPayload.getContainerFrameworkVersion());
        Assert.assertEquals("applicationFramework", kapuaBirthPayload.getApplicationFramework());
        Assert.assertEquals("applicationFrameworkV-1", kapuaBirthPayload.getApplicationFrameworkVersion());
        Assert.assertEquals("connectionInterface", kapuaBirthPayload.getConnectionInterface());
        Assert.assertEquals("192.168.1.1", kapuaBirthPayload.getConnectionIp());
        Assert.assertEquals("UTF-8", kapuaBirthPayload.getAcceptEncoding());
        Assert.assertEquals("applicationIdentifiers", kapuaBirthPayload.getApplicationIdentifiers());
        Assert.assertEquals("1", kapuaBirthPayload.getAvailableProcessors());
        Assert.assertEquals("4", kapuaBirthPayload.getTotalMemory());
        Assert.assertEquals("Linux x86", kapuaBirthPayload.getOsArch());
        Assert.assertEquals("49-015420-323751", kapuaBirthPayload.getModemImei());
        Assert.assertEquals("359881234567890", kapuaBirthPayload.getModemImsi());
        Assert.assertEquals("8991101200003204510", kapuaBirthPayload.getModemIccid());
        Assert.assertEquals("{\n  \"version\": \"1.0\",\n  \"properties\": {\n    \"GroupName\": {\n      \"propertyName\": \"propertyValue\"\n    }\n  }\n}",
                kapuaBirthPayload.getExtendedProperties());
    }

    @Test
    public void toDisplayString() throws Exception {
        KapuaBirthPayload kapuaBirthPayload = populateKapuaBirthPayload();

        String displayStr = kapuaBirthPayload.toDisplayString();
        Assert.assertEquals(PAYLOAD_DISPLAY_STR, displayStr);
    }

    @Test
    public void kapuaBirthMessageConstructor() throws Exception {
        KapuaBirthMessage kapuaBirthMessage = new KapuaBirthMessage();

        Assert.assertNotNull(kapuaBirthMessage);
    }

    @Test
    public void kapuaBirthMessageGetterSetters() throws Exception {
        KapuaBirthMessage kapuaBirthMessage = new KapuaBirthMessage();

        kapuaBirthMessage.setClientId("clientId-1");
        Assert.assertEquals("clientId-1", kapuaBirthMessage.getClientId());
    }

    @Test
    public void kapuaBirthChannelGetterSetters() throws Exception {
        KapuaBirthChannel kapuaBirthChannel = new KapuaBirthChannel();

        kapuaBirthChannel.setClientId("clientId-1");
        Assert.assertEquals("clientId-1", kapuaBirthChannel.getClientId());
    }

    /**
     * Prepare Kapua Birth payload data, data is not necessary semantically correct.
     *
     * @return all KapuaBirthPayload fields populated with data.
     */
    private static KapuaBirthPayload populateKapuaBirthPayload() {
        return new KapuaBirthPayload(
                "12",
                "Display Name",
                "Model Name",
                "Model Id-1",
                "part-1",
                "SN-123",
                "firmware-1",
                "firmwareV-1",
                "bios",
                "biosV-1",
                "Linux",
                "osV-1",
                "Oracle HotSpot",
                "8",
                "desktop",
                "containerFramework",
                "containerFrameworkV-1",
                "applicationFramework",
                "applicationFrameworkV-1",
                "connectionInterface",
                "192.168.1.1",
                "UTF-8",
                "applicationIdentifiers",
                "1",
                "4",
                "Linux x86",
                "49-015420-323751",
                "359881234567890",
                "8991101200003204510",
                "{\n" +
                        "  \"version\": \"1.0\",\n" +
                        "  \"properties\": {\n" +
                        "    \"GroupName\": {\n" +
                        "      \"propertyName\": \"propertyValue\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}",
                "NOT_TAMPERED"
        );
    }
}
