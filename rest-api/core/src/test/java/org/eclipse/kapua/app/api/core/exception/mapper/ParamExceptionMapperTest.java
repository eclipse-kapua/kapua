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

import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.glassfish.jersey.server.ParamException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class ParamExceptionMapperTest extends Assert {

    ParamExceptionMapper paramExceptionMapper;

    @Before
    public void initialize() {
        paramExceptionMapper = new ParamExceptionMapper();
    }

    @Test
    public void toResponseTest() {
        Throwable[] throwables = {new Throwable(), new Throwable("message"), new Throwable(new Exception()), new KapuaIllegalArgumentException("name", "value"), new KapuaIllegalArgumentException(null, "value"), new KapuaIllegalArgumentException("name", null), new KapuaIllegalArgumentException(null, null)};
        for (Throwable throwable : throwables) {
            assertEquals("Expected and actual values should be the same.", 400, paramExceptionMapper.toResponse(new ParamException.PathParamException(throwable, "name", "default value")).getStatus());
            assertEquals("Expected and actual values should be the same.", "Bad Request", paramExceptionMapper.toResponse(new ParamException.PathParamException(throwable, "name", "default value")).getStatusInfo().toString());
        }
    }

    @Test(expected = NullPointerException.class)
    public void toResponseNullTest() {
        paramExceptionMapper.toResponse(null);
    }
}