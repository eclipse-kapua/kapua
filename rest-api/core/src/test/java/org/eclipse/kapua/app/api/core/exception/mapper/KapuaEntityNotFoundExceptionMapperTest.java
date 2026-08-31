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

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class KapuaEntityNotFoundExceptionMapperTest extends Assert {

    KapuaEntityNotFoundExceptionMapper kapuaEntityNotFoundExceptionMapper;

    @Before
    public void initialize() {
        kapuaEntityNotFoundExceptionMapper = new KapuaEntityNotFoundExceptionMapper();
    }

    @Test
    public void toResponseTest() {
        assertEquals("Expected and actual values should be the same.", 404, kapuaEntityNotFoundExceptionMapper.toResponse(new KapuaEntityNotFoundException("entity type", KapuaId.ANY)).getStatus());
        assertEquals("Expected and actual values should be the same.", "Not Found", kapuaEntityNotFoundExceptionMapper.toResponse(new KapuaEntityNotFoundException("entity type", KapuaId.ANY)).getStatusInfo().toString());
    }

    @Test(expected = NullPointerException.class)
    public void toResponseNullTest() {
        kapuaEntityNotFoundExceptionMapper.toResponse(null);
    }
}