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
package org.eclipse.kapua.app.api.core.resources;

import java.math.BigInteger;

import javax.ws.rs.NotFoundException;

import org.eclipse.kapua.commons.web.rest.model.ScopeId;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class AbstractKapuaResourceTest {

    private class TestKapuaResource extends AbstractKapuaResource {

    }

    AbstractKapuaResource abstractKapuaResource;
    Object[] objects;

    @Before
    public void initialize() {
        abstractKapuaResource = new TestKapuaResource();
        objects = new Object[] { new Object(), "", "string", 10, 'c', KapuaId.ONE, new Throwable(), new ScopeId(BigInteger.valueOf(111)) };
    }

    @Test
    public void returnNotNullEntityTest() {
        for (Object object : objects) {
            Assert.assertEquals("Expected and actual values should be the same.", object, abstractKapuaResource.returnNotNullEntity(object));
        }
    }

    @Test(expected = NotFoundException.class)
    public void returnNotNullEntityNullTest() {
        abstractKapuaResource.returnNotNullEntity(null);
    }

    @Test
    public void returnOkTest() {
        Assert.assertEquals("Expected and actual values should be the same.", "OutboundJaxrsResponse{status=200, reason=OK, hasEntity=false, closed=false, buffered=false}",
                abstractKapuaResource.returnOk().toString());
    }

    @Test
    public void returnNoContentTest() {
        Assert.assertEquals("Expected and actual values should be the same.", "OutboundJaxrsResponse{status=204, reason=No Content, hasEntity=false, closed=false, buffered=false}",
                abstractKapuaResource.returnNoContent().toString());
    }

    @Test
    public void returnCreatedTest() {
        for (Object object : objects) {
            Assert.assertEquals("Expected and actual values should be the same.", "OutboundJaxrsResponse{status=201, reason=Created, hasEntity=true, closed=false, buffered=false}",
                    abstractKapuaResource.returnCreated(object).toString());
            Assert.assertEquals("Expected and actual values should be the same.", object, abstractKapuaResource.returnCreated(object).getEntity());
        }
    }
}
