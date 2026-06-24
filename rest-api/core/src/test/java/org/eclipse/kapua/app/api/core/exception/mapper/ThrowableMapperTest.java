/*******************************************************************************
 * Copyright (c) 2021 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.api.core.exception.mapper;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class ThrowableMapperTest extends Assert {

    ThrowableMapper throwableMapper;
    Throwable[] throwables;

    @Before
    public void initialize() {
        throwableMapper = new ThrowableMapper();
        throwables = new Throwable[]{new Throwable(), new Throwable("message"), new Throwable(new Exception())};
    }

    @Test
    public void toResponseTest() {
        for (Throwable throwable : throwables) {
            assertEquals("Expected and actual values should be the same.", 500, throwableMapper.toResponse(throwable).getStatus());
            assertEquals("Expected and actual values should be the same.", "Internal Server Error", throwableMapper.toResponse(throwable).getStatusInfo().toString());
        }
    }

    @Test
    public void toResponseNullTest() {
        assertEquals("Expected and actual values should be the same.", 500, throwableMapper.toResponse(null).getStatus());
        assertEquals("Expected and actual values should be the same.", "Internal Server Error", throwableMapper.toResponse(null).getStatusInfo().toString());
    }
}