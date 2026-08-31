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
import org.eclipse.kapua.service.authorization.shiro.exception.KapuaAuthorizationErrorCodes;
import org.eclipse.kapua.service.authorization.shiro.exception.KapuaAuthorizationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class KapuaAuthorizationExceptionMapperTest extends Assert {

    KapuaAuthorizationExceptionMapper kapuaAuthorizationExceptionMapper;

    @Before
    public void initialize() {
        kapuaAuthorizationExceptionMapper = new KapuaAuthorizationExceptionMapper();
    }

    @Test
    public void toResponseTest() {
        KapuaAuthorizationErrorCodes[] errorCodes = {KapuaAuthorizationErrorCodes.INVALID_STRING_PERMISSION,
                KapuaAuthorizationErrorCodes.SUBJECT_UNAUTHORIZED, KapuaAuthorizationErrorCodes.ENTITY_SCOPE_MISSMATCH};

        for (KapuaAuthorizationErrorCodes code : errorCodes) {
            KapuaAuthorizationException kapuaAuthorizationException = new KapuaAuthorizationException(code);

            assertEquals("Expected and actual values should be the same.", 403, kapuaAuthorizationExceptionMapper.toResponse(kapuaAuthorizationException).getStatus());
            assertEquals("Expected and actual values should be the same.", "Forbidden", kapuaAuthorizationExceptionMapper.toResponse(kapuaAuthorizationException).getStatusInfo().toString());
        }
    }

    @Test
    public void toResponseNullCodeTest() {
        KapuaAuthorizationException kapuaAuthorizationException = new KapuaAuthorizationException(null);
        assertEquals("Expected and actual values should be the same.", 403, kapuaAuthorizationExceptionMapper.toResponse(kapuaAuthorizationException).getStatus());
        assertEquals("Expected and actual values should be the same.", "Forbidden", kapuaAuthorizationExceptionMapper.toResponse(kapuaAuthorizationException).getStatusInfo().toString());
    }

    @Test
    public void toResponseNullTest() {
        assertEquals("Expected and actual values should be the same.", 403, kapuaAuthorizationExceptionMapper.toResponse(null).getStatus());
        assertEquals("Expected and actual values should be the same.", "Forbidden", kapuaAuthorizationExceptionMapper.toResponse(null).getStatusInfo().toString());
    }
}