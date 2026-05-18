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
package org.eclipse.kapua.service.authentication.shiro.exceptions;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authentication.exception.KapuaAuthenticationErrorCodes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(JUnitTests.class)
public class AuthenticationRuntimeExceptionTest {

    KapuaAuthenticationErrorCodes[] kapuaAuthenticationErrorCodes;
    Object stringArgument, intArgument, booleanArgument;
    Throwable[] throwables;
    String errorMessageWithoutArguments, errorMessageWithArguments;

    @Before
    public void initialize() {
        kapuaAuthenticationErrorCodes = new KapuaAuthenticationErrorCodes[]{KapuaAuthenticationErrorCodes.SUBJECT_ALREADY_LOGGED, KapuaAuthenticationErrorCodes.INVALID_CREDENTIALS_TYPE_PROVIDED,
                KapuaAuthenticationErrorCodes.AUTHENTICATION_ERROR, KapuaAuthenticationErrorCodes.CREDENTIAL_CRYPT_ERROR, KapuaAuthenticationErrorCodes.UNKNOWN_LOGIN_CREDENTIAL, KapuaAuthenticationErrorCodes.INVALID_LOGIN_CREDENTIALS,
                KapuaAuthenticationErrorCodes.EXPIRED_LOGIN_CREDENTIALS, KapuaAuthenticationErrorCodes.LOCKED_LOGIN_CREDENTIAL, KapuaAuthenticationErrorCodes.DISABLED_LOGIN_CREDENTIAL, KapuaAuthenticationErrorCodes.UNKNOWN_SESSION_CREDENTIAL,
                KapuaAuthenticationErrorCodes.INVALID_SESSION_CREDENTIALS, KapuaAuthenticationErrorCodes.EXPIRED_SESSION_CREDENTIALS, KapuaAuthenticationErrorCodes.LOCKED_SESSION_CREDENTIAL, KapuaAuthenticationErrorCodes.DISABLED_SESSION_CREDENTIAL,
                KapuaAuthenticationErrorCodes.JWK_FILE_ERROR, KapuaAuthenticationErrorCodes.REFRESH_ERROR, KapuaAuthenticationErrorCodes.JWK_GENERATION_ERROR, KapuaAuthenticationErrorCodes.JWT_CERTIFICATE_NOT_FOUND,
                KapuaAuthenticationErrorCodes.PASSWORD_CANNOT_BE_CHANGED};
        stringArgument = "String argument";
        intArgument = 20;
        booleanArgument = true;
        throwables = new Throwable[]{null, new Throwable(), new Throwable(new Exception()), new Throwable("message")};
        errorMessageWithoutArguments = "Error: ";
        errorMessageWithArguments = "Error: " + stringArgument + ", " + intArgument + ", " + booleanArgument;
    }

    @Test
    public void authenticationRuntimeExceptionCodeParameterTest() {
        for (KapuaAuthenticationErrorCodes kapuaAuthenticationErrorCode : kapuaAuthenticationErrorCodes) {
            AuthenticationRuntimeException authenticationRuntimeException = new AuthenticationRuntimeException(kapuaAuthenticationErrorCode);
            Assert.assertEquals("Expected and actual values should be the same.", kapuaAuthenticationErrorCode, authenticationRuntimeException.getCode());
            Assert.assertEquals("Expected and actual values should be the same.", errorMessageWithoutArguments, authenticationRuntimeException.getMessage());
            Assert.assertNull("Null expected.", authenticationRuntimeException.getCause());
        }
    }

    @Test
    public void authenticationRuntimeExceptionNullCodeParameterTest() {
        AuthenticationRuntimeException authenticationRuntimeException = new AuthenticationRuntimeException(null);
        Assert.assertNull("Null expected.", authenticationRuntimeException.getCode());
        Assert.assertNull("Null expected.", authenticationRuntimeException.getCause());
        try {
            authenticationRuntimeException.getMessage();
        } catch (Exception e) {
            Assert.assertTrue("NullPointerException expected.", e instanceof NullPointerException);
        }
    }

    @Test
    public void authenticationRuntimeExceptionCodeArgumentParametersTest() {
        for (KapuaAuthenticationErrorCodes kapuaAuthenticationErrorCode : kapuaAuthenticationErrorCodes) {
            AuthenticationRuntimeException authenticationRuntimeException = new AuthenticationRuntimeException(kapuaAuthenticationErrorCode, stringArgument, intArgument, booleanArgument);
            Assert.assertEquals("Expected and actual values should be the same.", kapuaAuthenticationErrorCode, authenticationRuntimeException.getCode());
            Assert.assertEquals("Expected and actual values should be the same.", errorMessageWithArguments, authenticationRuntimeException.getMessage());
            Assert.assertNull("Null expected.", authenticationRuntimeException.getCause());
        }
    }

    @Test
    public void authenticationRuntimeExceptionNullCodeArgumentParametersTest() {
        AuthenticationRuntimeException authenticationRuntimeException = new AuthenticationRuntimeException(null, stringArgument, intArgument, booleanArgument);
        Assert.assertNull("Null expected.", authenticationRuntimeException.getCode());
        Assert.assertNull("Null expected.", authenticationRuntimeException.getCause());
        try {
            authenticationRuntimeException.getMessage();
        } catch (Exception e) {
            Assert.assertTrue("NullPointerException expected.", e instanceof NullPointerException);
        }
    }

    @Test
    public void authenticationRuntimeExceptionCodeNullArgumentParametersTest() {
        for (KapuaAuthenticationErrorCodes kapuaAuthenticationErrorCode : kapuaAuthenticationErrorCodes) {
            AuthenticationRuntimeException authenticationRuntimeException = new AuthenticationRuntimeException(kapuaAuthenticationErrorCode, null);
            Assert.assertEquals("Expected and actual values should be the same.", kapuaAuthenticationErrorCode, authenticationRuntimeException.getCode());
            Assert.assertEquals("Expected and actual values should be the same.", errorMessageWithoutArguments, authenticationRuntimeException.getMessage());
            Assert.assertNull("Null expected.", authenticationRuntimeException.getCause());
        }
    }

    @Test
    public void authenticationRuntimeExceptionCodeCauseArgumentsParametersTest() {
        for (KapuaAuthenticationErrorCodes kapuaAuthenticationErrorCode : kapuaAuthenticationErrorCodes) {
            for (Throwable throwable : throwables) {
                AuthenticationRuntimeException authenticationRuntimeException = new AuthenticationRuntimeException(kapuaAuthenticationErrorCode, throwable, stringArgument, intArgument, booleanArgument);
                Assert.assertEquals("Expected and actual values should be the same.", kapuaAuthenticationErrorCode, authenticationRuntimeException.getCode());
                Assert.assertEquals("Expected and actual values should be the same.", errorMessageWithArguments, authenticationRuntimeException.getMessage());
                Assert.assertEquals("Expected and actual values should be the same.", throwable, authenticationRuntimeException.getCause());
            }
        }
    }

    @Test
    public void authenticationRuntimeExceptionNullCodeCauseArgumentsParametersTest() {
        for (Throwable throwable : throwables) {
            AuthenticationRuntimeException authenticationRuntimeException = new AuthenticationRuntimeException(null, throwable, stringArgument, intArgument, booleanArgument);
            Assert.assertNull("Null expected.", authenticationRuntimeException.getCode());
            Assert.assertEquals("Expected and actual values should be the same.", throwable, authenticationRuntimeException.getCause());
            try {
                authenticationRuntimeException.getMessage();
            } catch (Exception e) {
                Assert.assertTrue("NullPointerException expected.", e instanceof NullPointerException);
            }
        }
    }

    @Test
    public void authenticationRuntimeExceptionCodeCauseNullArgumentsParametersTest() {
        for (KapuaAuthenticationErrorCodes kapuaAuthenticationErrorCode : kapuaAuthenticationErrorCodes) {
            for (Throwable throwable : throwables) {
                AuthenticationRuntimeException authenticationRuntimeException = new AuthenticationRuntimeException(kapuaAuthenticationErrorCode, throwable, null);
                Assert.assertEquals("Expected and actual values should be the same.", kapuaAuthenticationErrorCode, authenticationRuntimeException.getCode());
                Assert.assertEquals("Expected and actual values should be the same.", errorMessageWithoutArguments, authenticationRuntimeException.getMessage());
                Assert.assertEquals("Expected and actual values should be the same.", throwable, authenticationRuntimeException.getCause());
            }
        }
    }
}