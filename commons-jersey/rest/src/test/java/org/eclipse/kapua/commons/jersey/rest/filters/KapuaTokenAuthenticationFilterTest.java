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
package org.eclipse.kapua.commons.jersey.rest.filters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class KapuaTokenAuthenticationFilterTest {

    HttpServletRequest request;
    HttpServletResponse response;
    KapuaTokenAuthenticationFilter kapuaTokenAuthenticationFilter;
    Object[] mappedValues;

    @Before
    public void initialize() {
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        kapuaTokenAuthenticationFilter = new KapuaTokenAuthenticationFilter();
        mappedValues = new Object[] { new Object(), 0, 10, 100000, "String", 'c', -10, -1000000000, -100000000000L, 10L, 10.0f, null, 10.10d, true, false };
    }

    @Test
    public void isAccessAllowedTrueTest() {
        Mockito.when(request.getMethod()).thenReturn("OPTIONS");
        for (Object mappedValue : mappedValues) {
            Assert.assertTrue("True expected.", kapuaTokenAuthenticationFilter.isAccessAllowed(request, response, mappedValue));
        }
    }

    @Test
    public void onAccessDeniedTest() throws Exception {
        Assert.assertTrue("True expected.", kapuaTokenAuthenticationFilter.onAccessDenied(request, response));
    }

    @Test
    public void onAccessDeniedNullRequestTest() throws Exception {
        Assert.assertTrue("True expected.", kapuaTokenAuthenticationFilter.onAccessDenied(null, response));
    }
}
