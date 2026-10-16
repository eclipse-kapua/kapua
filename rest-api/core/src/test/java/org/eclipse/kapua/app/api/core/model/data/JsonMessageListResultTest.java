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

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.storeengine.client.model.ResultList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.LinkedList;
import java.util.List;


@Category(JUnitTests.class)
public class JsonMessageListResultTest {

    ResultList<JsonDatastoreMessage> resultList;
    List expectedList;
    JsonDatastoreMessage item1, item2;

    @Before
    public void initialize() {
        resultList = new ResultList<>(3);
        item1 = Mockito.mock(JsonDatastoreMessage.class);
        item2 = Mockito.mock(JsonDatastoreMessage.class);

        resultList.add(item1);
        resultList.add(null);
        resultList.add(item2);
        expectedList = new LinkedList<>();

        expectedList.add(item1);
        expectedList.add(null);
        expectedList.add(item2);
    }

    @Test
    public void jsonMessageListResultWithoutParameterTest() {
        JsonMessageListResult jsonMessageListResult = new JsonMessageListResult();

        Assert.assertTrue("True expected.", jsonMessageListResult.getItems().isEmpty());
        Assert.assertNull("Null expected.", jsonMessageListResult.getTotalCount());
    }

    @Test
    public void jsonMessageListResultWithParameterTest() {
        JsonMessageListResult jsonMessageListResult = new JsonMessageListResult(resultList);

        Assert.assertEquals("Expected and actual values should be the same.", expectedList, jsonMessageListResult.getItems());
        Assert.assertEquals("Expected and actual values should be the same.", (Long) 3L, jsonMessageListResult.getTotalCount());
    }

    @Test(expected = NullPointerException.class)
    public void jsonMessageListResultWithNullParameterTest() {
        new JsonMessageListResult(null);
    }
} 