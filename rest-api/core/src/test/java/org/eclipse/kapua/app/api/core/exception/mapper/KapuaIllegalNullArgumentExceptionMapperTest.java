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

import org.eclipse.kapua.KapuaIllegalNullArgumentException;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class KapuaIllegalNullArgumentExceptionMapperTest extends Assert {

    KapuaIllegalNullArgumentExceptionMapper kapuaIllegalNullArgumentExceptionMapper;
    KapuaIllegalNullArgumentException[] kapuaIllegalNullArgumentException;

    @Before
    public void initialize() {
        kapuaIllegalNullArgumentExceptionMapper = new KapuaIllegalNullArgumentExceptionMapper();
        kapuaIllegalNullArgumentException = new KapuaIllegalNullArgumentException[]{new KapuaIllegalNullArgumentException("name"), new KapuaIllegalNullArgumentException(""), new KapuaIllegalNullArgumentException(null)};
    }

    @Test
    public void toResponseTest() {
        for (KapuaIllegalNullArgumentException kapuaException : kapuaIllegalNullArgumentException) {
            assertEquals("Expected and actual values should be the same.", 400, kapuaIllegalNullArgumentExceptionMapper.toResponse(kapuaException).getStatus());
            assertEquals("Expected and actual values should be the same.", "Bad Request", kapuaIllegalNullArgumentExceptionMapper.toResponse(kapuaException).getStatusInfo().toString());
        }
    }

    @Test(expected = NullPointerException.class)
    public void toResponseNullTest() {
        kapuaIllegalNullArgumentExceptionMapper.toResponse(null);
    }
}