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
package org.eclipse.kapua.service.device.management.commons.message.notification;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.device.management.message.notification.KapuaNotifyMessage;
import org.eclipse.kapua.service.device.management.message.notification.KapuaNotifyPayload;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class KapuaNotifyMessageTest {

    private static final String NOTIFY_MSG_STR = "Client id 'clientId-1' - semantic topic 'part1/part2/part3'";

    @Test
    public void kapuaNotifyPayloadConstructor() {
        KapuaNotifyPayload kapuaNotifyPayload = new KapuaNotifyPayload();

        Assert.assertNotNull(kapuaNotifyPayload);
    }

    @Test
    public void kapuaNotifyMessageConstructor() throws Exception {
        KapuaNotifyMessage kapuaNotifyMessage = new KapuaNotifyMessage();

        Assert.assertNotNull(kapuaNotifyMessage);
    }
}
