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
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.shiro.exception.SubjectUnauthorizedException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class SubjectUnauthorizedExceptionMapperTest extends Assert {

    SubjectUnauthorizedExceptionMapper subjectUnathorizedExceptionMapper;

    @Before
    public void initialize() {
        subjectUnathorizedExceptionMapper = new SubjectUnauthorizedExceptionMapper();
    }

    @Test
    public void toResponseTest() {
        assertEquals("Expected and actual values should be the same.", 403, subjectUnathorizedExceptionMapper.toResponse(new SubjectUnauthorizedException(Mockito.mock(Permission.class))).getStatus());
        assertEquals("Expected and actual values should be the same.", "Forbidden", subjectUnathorizedExceptionMapper.toResponse(new SubjectUnauthorizedException(Mockito.mock(Permission.class))).getStatusInfo().toString());
    }

    @Test
    public void toResponseNullPermissionTest() {
        assertEquals("Expected and actual values should be the same.", 403, subjectUnathorizedExceptionMapper.toResponse(new SubjectUnauthorizedException(null)).getStatus());
        assertEquals("Expected and actual values should be the same.", "Forbidden", subjectUnathorizedExceptionMapper.toResponse(new SubjectUnauthorizedException(null)).getStatusInfo().toString());
    }

    @Test(expected = NullPointerException.class)
    public void toResponseNullTest() {
        subjectUnathorizedExceptionMapper.toResponse(null);
    }
}