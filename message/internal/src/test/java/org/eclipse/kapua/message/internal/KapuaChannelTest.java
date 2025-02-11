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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.kapua.message.device.data.KapuaDataChannel;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class KapuaChannelTest {

    KapuaDataChannel kapuaDataChannel;

    @Before
    public void before() throws Exception {
        kapuaDataChannel = new KapuaDataChannel();
    }

    @Test
    public void semanticParts() throws Exception {
        List<String> semanticParts = new ArrayList<>();
        semanticParts.add("part1");
        semanticParts.add("part2");
        semanticParts.add("part3");
        kapuaDataChannel.setSemanticParts(semanticParts);
        List<String> getSemanticParts = kapuaDataChannel.getSemanticParts();

        Assert.assertEquals(semanticParts, getSemanticParts);
    }

    @Test
    public void semanticPartsToString() throws Exception {
        List<String> semanticParts = new ArrayList<>();
        semanticParts.add("part1");
        semanticParts.add("part2");
        semanticParts.add("part3");
        kapuaDataChannel.setSemanticParts(semanticParts);
        String toString = kapuaDataChannel.toString();

        Assert.assertEquals("part1/part2/part3", toString);
    }

    @Test
    public void semanticPartsToStringEmpty() throws Exception {
        List<String> semanticParts = new ArrayList<>();

        kapuaDataChannel.setSemanticParts(semanticParts);
        String toString = kapuaDataChannel.toString();

        Assert.assertEquals("", toString);
    }

    @Test
    public void semanticPartsToStringNull() throws Exception {

        kapuaDataChannel.setSemanticParts(null);
        String toString = kapuaDataChannel.toString();

        Assert.assertEquals("", toString);
    }

}
