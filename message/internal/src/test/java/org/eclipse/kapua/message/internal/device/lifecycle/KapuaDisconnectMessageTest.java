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
package org.eclipse.kapua.message.internal.device.lifecycle;

import org.eclipse.kapua.message.device.lifecycle.KapuaDisconnectChannel;
import org.eclipse.kapua.message.device.lifecycle.KapuaDisconnectMessage;
import org.eclipse.kapua.message.device.lifecycle.KapuaDisconnectPayload;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class KapuaDisconnectMessageTest {

    private static final String PAYLOAD_DISPLAY_STR = "" +
            "displayName=A display name" +
            "~~uptime=12";

    @Test
    public void kapuaDisconnectPayloadInitConstructor() {
        KapuaDisconnectPayload kapuaDisconnectPayload = populateKapuaDisconnectPayload();

        Assert.assertEquals("12", kapuaDisconnectPayload.getUptime());
        Assert.assertEquals("A display name", kapuaDisconnectPayload.getDisplayName());
    }

    @Test
    public void toDisplayString() throws Exception {
        KapuaDisconnectPayload kapuaDisconnectPayload = populateKapuaDisconnectPayload();

        String displayStr = kapuaDisconnectPayload.toDisplayString();
        Assert.assertEquals(PAYLOAD_DISPLAY_STR, displayStr);
    }

    @Test
    public void kapuaDisconnectMessageConstructor() throws Exception {
        KapuaDisconnectMessage kapuaDisconnectMessage = new KapuaDisconnectMessage();

        Assert.assertNotNull(kapuaDisconnectMessage);
    }

    @Test
    public void kapuaDisconnectMessageGetterSetters() throws Exception {
        KapuaDisconnectMessage kapuaDisconnectMessage = new KapuaDisconnectMessage();

        kapuaDisconnectMessage.setClientId("clientId-1");
        Assert.assertEquals("clientId-1", kapuaDisconnectMessage.getClientId());
    }

    @Test
    public void kapuaDisconnectChannelGetterSetters() throws Exception {
        KapuaDisconnectChannel kapuaDisconnectChannel = new KapuaDisconnectChannel();

        kapuaDisconnectChannel.setClientId("clientId-1");
        Assert.assertEquals("clientId-1", kapuaDisconnectChannel.getClientId());
    }

    /**
     * Prepare Kapua Disconnect payload data, data is not necessary semantically correct.
     *
     * @return all KapuaDisconnectPayload fields populated with data.
     */
    private static KapuaDisconnectPayload populateKapuaDisconnectPayload() {
        return new KapuaDisconnectPayload(
                "12",
                "A display name"
        );
    }
}
