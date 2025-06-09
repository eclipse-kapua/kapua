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
package org.eclipse.kapua.message.internal.device.data;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.kapua.message.device.data.KapuaDataChannel;
import org.eclipse.kapua.message.device.data.KapuaDataMessage;
import org.eclipse.kapua.message.device.data.KapuaDataPayload;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class KapuaDeviceDataTest {

    @Test
    public void kapuaDataChanneltoString() throws Exception {
        KapuaDataChannel kapuaDataChannel = new KapuaDataChannel();
        List<String> semanticParts = new ArrayList<>();
        semanticParts.add("part1");
        semanticParts.add("part2");
        semanticParts.add("part3");

        kapuaDataChannel.setSemanticParts(semanticParts);
        Assert.assertEquals("part1/part2/part3", kapuaDataChannel.toString());
    }

    @Test
    public void kapuaDataMesssageGetterSetters() {
        KapuaDataMessage kapuaDataMessage = new KapuaDataMessage();

        kapuaDataMessage.setClientId("clientId-1");
        Assert.assertEquals("clientId-1", kapuaDataMessage.getClientId());
    }

    @Test
    public void kapuaDataPayloadDefaultConstructor() {
        KapuaDataPayload kapuaDataPayload = new KapuaDataPayload();

        Assert.assertNotNull(kapuaDataPayload);
    }
}
