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

import org.eclipse.kapua.message.device.lifecycle.KapuaMissingChannel;
import org.eclipse.kapua.message.device.lifecycle.KapuaMissingMessage;
import org.eclipse.kapua.message.device.lifecycle.KapuaMissingPayload;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class KapuaMissingMessageTest {

    @Test
    public void kapuaMissingPayloadConstructor() {
        KapuaMissingPayload kapuaMissingPayload = new KapuaMissingPayload();

        Assert.assertNotNull(kapuaMissingPayload);
    }

    @Test
    public void kapuaMissingMessageConstructor() throws Exception {
        KapuaMissingMessage kapuaMissingMessage = new KapuaMissingMessage();

        Assert.assertNotNull(kapuaMissingMessage);
    }

    @Test
    public void kapuaMissingChannelGetterSetters() throws Exception {
        KapuaMissingChannel kapuaMissingChannel = new KapuaMissingChannel();

        kapuaMissingChannel.setClientId("clientId-1");
        Assert.assertEquals("clientId-1", kapuaMissingChannel.getClientId());
    }

}
