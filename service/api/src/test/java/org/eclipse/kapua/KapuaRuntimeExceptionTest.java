/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(JUnitTests.class)
public class KapuaRuntimeExceptionTest {

    String expectedErrorMessage;
    KapuaErrorCode kapuaErrorCode;
    Object argument1, argument2, argument3;
    Throwable[] throwables;

    @Before
    public void initialize() {
        expectedErrorMessage = "kapua-service-error-messages";
        kapuaErrorCode = KapuaErrorCodes.ADMIN_ROLE_DELETED_ERROR;
        argument1 = "user";
        argument2 = 1;
        argument3 = 'c';
        throwables = new Throwable[]{new Throwable(), null};
    }

    @Test
    public void kapuaRuntimeExceptionKapuaErrorCodeParameterTest() {
        KapuaRuntimeException kapuaRuntimeException = new KapuaRuntimeException(kapuaErrorCode);

        Assert.assertEquals("Expected and actual values should be the same.", KapuaErrorCodes.ADMIN_ROLE_DELETED_ERROR, kapuaRuntimeException.getCode());
        Assert.assertNull("Null expected.", kapuaRuntimeException.getCause());
        Assert.assertEquals("Expected and actual values should be the same.", "Operation not allowed on admin role.", kapuaRuntimeException.getMessage());
        Assert.assertEquals("Expected and actual values should be the same.", "Operation not allowed on admin role.", kapuaRuntimeException.getLocalizedMessage());
        Assert.assertEquals("Expected and actual values should be the same.", expectedErrorMessage, kapuaRuntimeException.getKapuaErrorMessagesBundle());
    }

    @Test
    public void kapuaRuntimeExceptionNullKapuaErrorCodeParameterTest() {
        KapuaRuntimeException kapuaRuntimeException = new KapuaRuntimeException(null);

        Assert.assertNull("Null expected.", kapuaRuntimeException.getCode());
        Assert.assertNull("Null expected.", kapuaRuntimeException.getCause());
        Assert.assertEquals("Expected and actual values should be the same.", expectedErrorMessage, kapuaRuntimeException.getKapuaErrorMessagesBundle());
        Assert.assertEquals("Error: ", kapuaRuntimeException.getMessage());
        Assert.assertEquals("Error: ", kapuaRuntimeException.getLocalizedMessage());
    }

    @Test
    public void kapuaRuntimeExceptionKapuaErrorCodeObjectParametersTest() {
        KapuaRuntimeException kapuaRuntimeException = new KapuaRuntimeException(kapuaErrorCode, argument1, argument2, argument3);

        Assert.assertEquals("Expected and actual values should be the same.", KapuaErrorCodes.ADMIN_ROLE_DELETED_ERROR, kapuaRuntimeException.getCode());
        Assert.assertNull("Null expected.", kapuaRuntimeException.getCause());
        Assert.assertEquals("Expected and actual values should be the same.", "Operation not allowed on admin role.", kapuaRuntimeException.getMessage());
        Assert.assertEquals("Expected and actual values should be the same.", "Operation not allowed on admin role.", kapuaRuntimeException.getLocalizedMessage());
        Assert.assertEquals("Expected and actual values should be the same.", expectedErrorMessage, kapuaRuntimeException.getKapuaErrorMessagesBundle());
    }

    @Test
    public void kapuaRuntimeExceptionKapuaErrorCodeNullObjectParametersTest() {
        KapuaRuntimeException kapuaRuntimeException = new KapuaRuntimeException(kapuaErrorCode, null);

        Assert.assertEquals("Expected and actual values should be the same.", KapuaErrorCodes.ADMIN_ROLE_DELETED_ERROR, kapuaRuntimeException.getCode());
        Assert.assertNull("Null expected.", kapuaRuntimeException.getCause());
        Assert.assertEquals("Expected and actual values should be the same.", "Operation not allowed on admin role.", kapuaRuntimeException.getMessage());
        Assert.assertEquals("Expected and actual values should be the same.", "Operation not allowed on admin role.", kapuaRuntimeException.getLocalizedMessage());
        Assert.assertEquals("Expected and actual values should be the same.", expectedErrorMessage, kapuaRuntimeException.getKapuaErrorMessagesBundle());
    }

    @Test
    public void kapuaRuntimeExceptionNullKapuaErrorCodeObjectParametersTest() {
        KapuaRuntimeException kapuaRuntimeException = new KapuaRuntimeException(null, argument1, argument2, argument3);

        Assert.assertNull("Null expected.", kapuaRuntimeException.getCode());
        Assert.assertNull("Null expected.", kapuaRuntimeException.getCause());
        Assert.assertEquals("Expected and actual values should be the same.", expectedErrorMessage, kapuaRuntimeException.getKapuaErrorMessagesBundle());
        Assert.assertEquals("Error: user, 1, c", kapuaRuntimeException.getMessage());
        Assert.assertEquals("Error: user, 1, c", kapuaRuntimeException.getLocalizedMessage());
    }

    @Test
    public void kapuaRuntimeExceptionKapuaErrorCodeThrowableObjectParametesTest() {
        for (Throwable throwable : throwables) {
            KapuaRuntimeException kapuaRuntimeException = new KapuaRuntimeException(KapuaErrorCodes.ENTITY_NOT_FOUND, throwable, argument1, argument2, argument3);
            Assert.assertEquals("Expected and actual values should be the same.", KapuaErrorCodes.ENTITY_NOT_FOUND, kapuaRuntimeException.getCode());
            Assert.assertEquals("Expected and actual values should be the same.", "The entity of type " + argument1 + " with id/name " + argument2 + " was not found.", kapuaRuntimeException.getMessage());
            Assert.assertEquals("Expected and actual values should be the same.", "The entity of type " + argument1 + " with id/name " + argument2 + " was not found.", kapuaRuntimeException.getLocalizedMessage());
            Assert.assertEquals("Expected and actual values should be the same.", expectedErrorMessage, kapuaRuntimeException.getKapuaErrorMessagesBundle());
            Assert.assertEquals("Expected and actual values should be the same.", throwable, kapuaRuntimeException.getCause());
        }
    }

    @Test
    public void kapuaRuntimeExceptionNullKapuaErrorCodeThrowableObjectParametesTest() {
        for (Throwable throwable : throwables) {
            KapuaException kapuaRuntimeException = new KapuaException(null, throwable, argument1, argument2, argument3);
            Assert.assertNull("Null expected.", kapuaRuntimeException.getCode());
            Assert.assertEquals("Expected and actual values should be the same.", throwable, kapuaRuntimeException.getCause());
            Assert.assertEquals("Expected and actual values should be the same.", expectedErrorMessage, kapuaRuntimeException.getKapuaErrorMessagesBundle());
            Assert.assertEquals("Error: user, 1, c", kapuaRuntimeException.getMessage());
            Assert.assertEquals("Error: user, 1, c", kapuaRuntimeException.getLocalizedMessage());
        }
    }

    @Test
    public void kapuaRuntimeExceptionKapuaErrorCodeThrowableNullObjectParametesTest() {
        for (Throwable throwable : throwables) {
            KapuaException kapuaRuntimeException = new KapuaException(KapuaErrorCodes.ENTITY_NOT_FOUND, throwable, null);
            Assert.assertEquals("Expected and actual values should be the same.", KapuaErrorCodes.ENTITY_NOT_FOUND, kapuaRuntimeException.getCode());
            Assert.assertEquals("Expected and actual values should be the same.", "The entity of type {0} with id/name {1} was not found.", kapuaRuntimeException.getMessage());
            Assert.assertEquals("Expected and actual values should be the same.", "The entity of type {0} with id/name {1} was not found.", kapuaRuntimeException.getLocalizedMessage());
            Assert.assertEquals("Expected and actual values should be the same.", expectedErrorMessage, kapuaRuntimeException.getKapuaErrorMessagesBundle());
            Assert.assertEquals("Expected and actual values should be the same.", throwable, kapuaRuntimeException.getCause());
        }
    }

    @Test
    public void internalErrorCauseMessageTest() {
        String[] messages = {"Message", null};

        for (Throwable throwable : throwables) {
            for (String msg : messages) {
                Assert.assertThat("Instance of KapuaRuntimeException expected.", KapuaRuntimeException.internalError(throwable, msg), IsInstanceOf.instanceOf(KapuaRuntimeException.class));
                Assert.assertEquals("Expected and actual values should be the same.", new KapuaRuntimeException(KapuaErrorCodes.INTERNAL_ERROR, throwable, msg).toString(), KapuaRuntimeException.internalError(throwable, msg).toString());
                Assert.assertEquals("Expected and actual values should be the same.", "An internal error occurred: " + msg + ".", KapuaRuntimeException.internalError(throwable, msg).getMessage());
                Assert.assertEquals("Expected and actual values should be the same.", throwable, KapuaRuntimeException.internalError(throwable, msg).getCause());
                Assert.assertEquals("Expected and actual values should be the same.", KapuaErrorCodes.INTERNAL_ERROR, KapuaRuntimeException.internalError(throwable, msg).getCode());
                Assert.assertEquals("Expected and actual values should be the same.", expectedErrorMessage, KapuaRuntimeException.internalError(throwable, msg).getKapuaErrorMessagesBundle());
            }
        }
    }

    @Test
    public void internalErrorCauseTest() {
        String message = "Message";
        Throwable[] throwables = {new Throwable(message), new Throwable()};
        Throwable nullThrowable = null;
        String[] expectedMessage = {"An internal error occurred: " + message + ".", "An internal error occurred: " + throwables[1] + "."};
        String[] arguments = {"Message", "java.lang.Throwable"};

        for (int i = 0; i < throwables.length; i++) {
            Assert.assertThat("Instance of KapuaRuntimeException expected.", KapuaRuntimeException.internalError(throwables[i]), IsInstanceOf.instanceOf(KapuaRuntimeException.class));
            Assert.assertEquals("Expected and actual values should be the same.", new KapuaRuntimeException(KapuaErrorCodes.INTERNAL_ERROR, throwables[i], arguments[i]).toString(), KapuaRuntimeException.internalError(throwables[i]).toString());
            Assert.assertEquals("Expected and actual values should be the same.", expectedMessage[i], KapuaRuntimeException.internalError(throwables[i]).getMessage());
            Assert.assertEquals("Expected and actual values should be the same.", throwables[i], KapuaRuntimeException.internalError(throwables[i]).getCause());
            Assert.assertEquals("Expected and actual values should be the same.", KapuaErrorCodes.INTERNAL_ERROR, KapuaRuntimeException.internalError(throwables[i]).getCode());
            Assert.assertEquals("Expected and actual values should be the same.", expectedErrorMessage, KapuaRuntimeException.internalError(throwables[i]).getKapuaErrorMessagesBundle());
        }
        try {
            KapuaRuntimeException.internalError(nullThrowable);
            Assert.fail("NullPointerException expected.");
        } catch (Exception e) {
            Assert.assertTrue("NullPointerException expected.", e instanceof NullPointerException);
        }
    }

    @Test
    public void internalErrorMessageTest() {
        String[] messages = {"Message", null};

        for (String msg : messages) {
            Assert.assertThat("Instance of KapuaRuntimeException expected.", KapuaRuntimeException.internalError(msg), IsInstanceOf.instanceOf(KapuaRuntimeException.class));
            Assert.assertEquals("Expected and actual values should be the same.", new KapuaRuntimeException(KapuaErrorCodes.INTERNAL_ERROR, null, msg).toString(), KapuaRuntimeException.internalError(msg).toString());
            Assert.assertEquals("Expected and actual values should be the same.", "An internal error occurred: " + msg + ".", KapuaRuntimeException.internalError(msg).getMessage());
            Assert.assertNull("Null expected.", KapuaRuntimeException.internalError(msg).getCause());
            Assert.assertEquals("Expected and actual values should be the same.", KapuaErrorCodes.INTERNAL_ERROR, KapuaRuntimeException.internalError(msg).getCode());
            Assert.assertEquals("Expected and actual values should be the same.", expectedErrorMessage, KapuaRuntimeException.internalError(msg).getKapuaErrorMessagesBundle());
        }
    }

    @Test(expected = KapuaRuntimeException.class)
    public void throwingExceptionKapuaErrorCodeParameterTest() {
        throw new KapuaRuntimeException(kapuaErrorCode);
    }

    @Test(expected = KapuaRuntimeException.class)
    public void throwingExceptionNullKapuaErrorCodeParameterTest() {
        throw new KapuaRuntimeException(null);
    }

    @Test(expected = KapuaRuntimeException.class)
    public void throwingExceptionKapuaErrorCodeObjectParameterTest() {
        throw new KapuaRuntimeException(kapuaErrorCode, argument1, argument2, argument3);
    }

    @Test(expected = KapuaRuntimeException.class)
    public void throwingExceptionNullKapuaErrorCodeObjectParameterTest() {
        throw new KapuaRuntimeException(null, argument1, argument2, argument3);
    }

    @Test(expected = KapuaRuntimeException.class)
    public void throwingExceptionKapuaErrorCodeNullObjectParameterTest() {
        throw new KapuaRuntimeException(kapuaErrorCode, null);
    }

    @Test(expected = KapuaRuntimeException.class)
    public void throwingExceptionNullKapuaErrorCodeNullObjectParameterTest() {
        throw new KapuaRuntimeException(null, null);
    }

    @Test(expected = KapuaRuntimeException.class)
    public void throwingExceptionKapuaErrorCodeThrowableObjectParameterTest() {
        for (Throwable throwable : throwables) {
            throw new KapuaRuntimeException(kapuaErrorCode, throwable, argument1, argument2, argument3);
        }
    }

    @Test(expected = KapuaRuntimeException.class)
    public void throwingExceptionNullKapuaErrorCodeThrowableObjectParameterTest() {
        for (Throwable throwable : throwables) {
            throw new KapuaRuntimeException(null, throwable, argument1, argument2, argument3);
        }
    }

    @Test(expected = KapuaRuntimeException.class)
    public void throwingExceptionKapuaErrorCodeThrowableNullObjectParameterTest() {
        for (Throwable throwable : throwables) {
            throw new KapuaRuntimeException(kapuaErrorCode, throwable, null);
        }
    }

    @Test(expected = KapuaRuntimeException.class)
    public void throwingExceptionNullKapuaErrorCodeThrowableNullObjectParameterTest() {
        for (Throwable throwable : throwables) {
            throw new KapuaRuntimeException(null, throwable, null);
        }
    }
}
