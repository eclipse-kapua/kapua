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
package org.eclipse.kapua.message.internal.xml;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.internal.MessageJAXBContextProvider;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class KapuaMetricsMapAdapterTest {

    private static final String NEWLINE = System.lineSeparator();

    private static final String METRICS_XML_STR = //
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
                    "<payload>" + NEWLINE +
                    "   <metrics>" + NEWLINE +
                    "      <metric>" + NEWLINE +
                    "         <valueType>string</valueType>" + NEWLINE +
                    "         <value>value1</value>" + NEWLINE +
                    "         <name>key1</name>" + NEWLINE +
                    "      </metric>" + NEWLINE +
                    "   </metrics>" + NEWLINE +
                    "</payload>" + NEWLINE;

    private XmlUtil xmlUtil;

    @Before
    public void before() throws Exception {
        xmlUtil = new XmlUtil(new MessageJAXBContextProvider());
    }

    @Test
    public void marshalWithAdapter() throws Exception {
        KapuaPayload metricsMap = new KapuaPayload();

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("key1", "value1");
        metricsMap.setMetrics(metrics);

        StringWriter strWriter = new StringWriter();
        xmlUtil.marshal(metricsMap, strWriter);
        Assert.assertEquals(METRICS_XML_STR, strWriter.toString());
    }

    @Test
    public void unmarshalWithAdapter() throws Exception {
        KapuaPayload metricsMap = new KapuaPayload();
        Map<String, Object> metrics = new HashMap<>();
        metrics.put(String.valueOf("key1"), String.valueOf("value1"));
        metricsMap.setMetrics(metrics);

        KapuaPayload metricsMapResp = xmlUtil.unmarshal(METRICS_XML_STR, KapuaPayload.class);
        Assert.assertEquals(metricsMap.getMetrics().get("key1"), metricsMapResp.getMetrics().get("key1"));
    }

}
