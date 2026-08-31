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
import org.eclipse.kapua.service.authentication.KapuaAuthenticationErrorCodes;
import org.eclipse.kapua.service.authentication.shiro.KapuaAuthenticationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class KapuaAuthenticationExceptionMapperTest extends Assert {

    KapuaAuthenticationExceptionMapper kapuaAuthenticationExceptionMapper;

    @Before
    public void initialize() {
        kapuaAuthenticationExceptionMapper = new KapuaAuthenticationExceptionMapper();
    }

    @Test
    public void toResponseTest() {
        KapuaAuthenticationErrorCodes[] errorCodes = {KapuaAuthenticationErrorCodes.SUBJECT_ALREADY_LOGGED, KapuaAuthenticationErrorCodes.INVALID_CREDENTIALS_TYPE_PROVIDED,
                KapuaAuthenticationErrorCodes.AUTHENTICATION_ERROR, KapuaAuthenticationErrorCodes.CREDENTIAL_CRYPT_ERROR, KapuaAuthenticationErrorCodes.INVALID_LOGIN_CREDENTIALS,
                KapuaAuthenticationErrorCodes.EXPIRED_LOGIN_CREDENTIALS, KapuaAuthenticationErrorCodes.LOCKED_LOGIN_CREDENTIAL, KapuaAuthenticationErrorCodes.DISABLED_LOGIN_CREDENTIAL,
                KapuaAuthenticationErrorCodes.UNKNOWN_SESSION_CREDENTIAL, KapuaAuthenticationErrorCodes.INVALID_SESSION_CREDENTIALS, KapuaAuthenticationErrorCodes.EXPIRED_SESSION_CREDENTIALS,
                KapuaAuthenticationErrorCodes.LOCKED_SESSION_CREDENTIAL, KapuaAuthenticationErrorCodes.DISABLED_SESSION_CREDENTIAL, KapuaAuthenticationErrorCodes.JWK_FILE_ERROR,
                KapuaAuthenticationErrorCodes.REFRESH_ERROR, KapuaAuthenticationErrorCodes.JWK_GENERATION_ERROR, KapuaAuthenticationErrorCodes.JWT_CERTIFICATE_NOT_FOUND,
                KapuaAuthenticationErrorCodes.PASSWORD_CANNOT_BE_CHANGED};

        for (KapuaAuthenticationErrorCodes code : errorCodes) {
            KapuaAuthenticationException kapuaAuthenticationException = new KapuaAuthenticationException(code);

            assertEquals("Expected and actual values should be the same.", 401, kapuaAuthenticationExceptionMapper.toResponse(kapuaAuthenticationException).getStatus());
            assertEquals("Expected and actual values should be the same.", "Unauthorized", kapuaAuthenticationExceptionMapper.toResponse(kapuaAuthenticationException).getStatusInfo().toString());
        }
    }

    @Test
    public void toResponseRequireMfaCredentialsTest() {
        KapuaAuthenticationException kapuaAuthenticationException = new KapuaAuthenticationException(KapuaAuthenticationErrorCodes.REQUIRE_MFA_CREDENTIALS);

        assertEquals("Expected and actual values should be the same.", 403, kapuaAuthenticationExceptionMapper.toResponse(kapuaAuthenticationException).getStatus());
        assertEquals("Expected and actual values should be the same.", "Forbidden", kapuaAuthenticationExceptionMapper.toResponse(kapuaAuthenticationException).getStatusInfo().toString());
    }

    @Test(expected = NullPointerException.class)
    public void toResponseNullCodeTest() {
        KapuaAuthenticationException kapuaAuthenticationException = new KapuaAuthenticationException(null);
        kapuaAuthenticationExceptionMapper.toResponse(kapuaAuthenticationException);
    }

    @Test(expected = NullPointerException.class)
    public void toResponseNullTest() {
        kapuaAuthenticationExceptionMapper.toResponse(null);
    }
}