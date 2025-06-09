/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.message.internal;

import java.io.StringWriter;
import java.util.Map;

import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class KapuaPayloadTest {

    private static final String PAYLOAD_DISPLAY_STR = "Boolean=true~~Double=42.42~~Float=42.42~~Integer=42~~Long=43~~String=Big brown fox~~byte=Ym9keQA~~unknown=";

    private static final String KAPUA_PAYLOAD_XML_STR = "missing";

    private XmlUtil xmlUtil;

    @Before
    public void before() throws Exception {
        xmlUtil = new XmlUtil(new MessageJAXBContextProvider());
    }

    @Test
    public void neverGetNull() {
        Assert.assertNotNull(new KapuaPayload().getMetrics());
    }

    @Test
    public void payloadGetterSetters() throws Exception {
        KapuaPayload kapuaPayload = new KapuaPayload();
        KapuaMessageUtil.populatePayload(kapuaPayload);

        Map<String, Object> properties = kapuaPayload.getMetrics();
        byte[] body = kapuaPayload.getBody();
        Assert.assertEquals("value1", properties.get("key1"));
        Assert.assertEquals("value2", properties.get("key2"));
        Assert.assertArrayEquals(new byte[] { 'b', 'o', 'd', 'y' }, body);
    }

    @Test
    public void displayStringEmpty() throws Exception {
        final KapuaPayload kapuaPayload = new KapuaPayload();
        Assert.assertEquals("", kapuaPayload.toDisplayString());
    }

    @Test
    public void displayString() throws Exception {
        final KapuaPayload kapuaPayload = new KapuaPayload();

        KapuaMessageUtil.populatePayloadWithAllTypesOfMetrics(kapuaPayload);
        kapuaPayload.getMetrics().put("null", null);

        String displayStr = kapuaPayload.toDisplayString();
        Assert.assertEquals(PAYLOAD_DISPLAY_STR, displayStr);
    }

    @Test
    @Ignore("KapuaPayload marshaling not working")
    public void marshallPayload() throws Exception {
        KapuaPayload kapuaPayload = new KapuaPayload();
        KapuaMessageUtil.populatePayload(kapuaPayload);

        StringWriter strWriter = new StringWriter();
        xmlUtil.marshal(kapuaPayload, strWriter);
        Assert.assertEquals(KAPUA_PAYLOAD_XML_STR, strWriter.toString());
    }
}
