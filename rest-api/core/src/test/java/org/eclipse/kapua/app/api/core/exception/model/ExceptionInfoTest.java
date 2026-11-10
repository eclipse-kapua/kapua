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
package org.eclipse.kapua.app.api.core.exception.model;

import org.eclipse.kapua.KapuaErrorCode;
import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.ws.rs.core.Response;

@Category(JUnitTests.class)
public class ExceptionInfoTest extends Assert {

    Response.Status[] statusList;
    KapuaErrorCode[] kapuaErrorCodes;
    int[] expectedStatusCodes;
    String[] expectedErrorCodes;

    @Before
    public void initialize() {
        statusList = new Response.Status[]{Response.Status.OK, Response.Status.CREATED, Response.Status.ACCEPTED, Response.Status.NO_CONTENT,
                Response.Status.RESET_CONTENT, Response.Status.PARTIAL_CONTENT, Response.Status.MOVED_PERMANENTLY, Response.Status.FOUND,
                Response.Status.SEE_OTHER, Response.Status.NOT_MODIFIED, Response.Status.USE_PROXY, Response.Status.TEMPORARY_REDIRECT,
                Response.Status.BAD_REQUEST, Response.Status.UNAUTHORIZED, Response.Status.PAYMENT_REQUIRED, Response.Status.FORBIDDEN,
                Response.Status.NOT_FOUND, Response.Status.METHOD_NOT_ALLOWED, Response.Status.NOT_ACCEPTABLE, Response.Status.PROXY_AUTHENTICATION_REQUIRED,
                Response.Status.REQUEST_TIMEOUT, Response.Status.CONFLICT, Response.Status.GONE, Response.Status.LENGTH_REQUIRED,
                Response.Status.PRECONDITION_FAILED, Response.Status.REQUEST_ENTITY_TOO_LARGE, Response.Status.REQUEST_URI_TOO_LONG, Response.Status.UNSUPPORTED_MEDIA_TYPE,
                Response.Status.REQUESTED_RANGE_NOT_SATISFIABLE, Response.Status.EXPECTATION_FAILED, Response.Status.INTERNAL_SERVER_ERROR, Response.Status.NOT_IMPLEMENTED,
                Response.Status.BAD_GATEWAY, Response.Status.SERVICE_UNAVAILABLE, Response.Status.GATEWAY_TIMEOUT, Response.Status.HTTP_VERSION_NOT_SUPPORTED};
        kapuaErrorCodes = new KapuaErrorCodes[]{KapuaErrorCodes.ADMIN_ROLE_DELETED_ERROR, KapuaErrorCodes.ENTITY_NOT_FOUND, KapuaErrorCodes.ENTITY_ALREADY_EXISTS, KapuaErrorCodes.DUPLICATE_NAME,
                KapuaErrorCodes.DUPLICATE_EXTERNAL_ID, KapuaErrorCodes.ENTITY_UNIQUENESS, KapuaErrorCodes.ILLEGAL_ACCESS, KapuaErrorCodes.ILLEGAL_ARGUMENT,
                KapuaErrorCodes.ILLEGAL_NULL_ARGUMENT, KapuaErrorCodes.ILLEGAL_STATE, KapuaErrorCodes.OPTIMISTIC_LOCKING, KapuaErrorCodes.UNAUTHENTICATED,
                KapuaErrorCodes.OPERATION_NOT_SUPPORTED, KapuaErrorCodes.INTERNAL_ERROR, KapuaErrorCodes.SEVERE_INTERNAL_ERROR, KapuaErrorCodes.PARENT_LIMIT_EXCEEDED_IN_CONFIG,
                KapuaErrorCodes.SUBJECT_UNAUTHORIZED, KapuaErrorCodes.ENTITY_ALREADY_EXIST_IN_ANOTHER_ACCOUNT, KapuaErrorCodes.EXTERNAL_ID_ALREADY_EXIST_IN_ANOTHER_ACCOUNT, KapuaErrorCodes.BUNDLE_START_ERROR,
                KapuaErrorCodes.BUNDLE_STOP_ERROR, KapuaErrorCodes.PACKAGE_URI_SYNTAX_ERROR, KapuaErrorCodes.MAX_NUMBER_OF_ITEMS_REACHED, KapuaErrorCodes.DEVICE_NOT_FOUND, KapuaErrorCodes.DOWNLOAD_PACKAGE_EXCEPTION,
                KapuaErrorCodes.PERMISSION_DELETE_NOT_ALLOWED, KapuaErrorCodes.SERVICE_DISABLED};
        expectedStatusCodes = new int[]{200, 201, 202, 204, 205, 206, 301, 302, 303, 304, 305, 307, 400, 401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417, 500, 501, 502, 503, 504, 505};
        expectedErrorCodes = new String[]{"ADMIN_ROLE_DELETED_ERROR", "ENTITY_NOT_FOUND", "ENTITY_ALREADY_EXISTS", "DUPLICATE_NAME", "DUPLICATE_EXTERNAL_ID", "ENTITY_UNIQUENESS", "ILLEGAL_ACCESS", "ILLEGAL_ARGUMENT",
                "ILLEGAL_NULL_ARGUMENT", "ILLEGAL_STATE", "OPTIMISTIC_LOCKING", "UNAUTHENTICATED", "OPERATION_NOT_SUPPORTED", "INTERNAL_ERROR", "SEVERE_INTERNAL_ERROR", "PARENT_LIMIT_EXCEEDED_IN_CONFIG",
                "SUBJECT_UNAUTHORIZED", "ENTITY_ALREADY_EXIST_IN_ANOTHER_ACCOUNT", "EXTERNAL_ID_ALREADY_EXIST_IN_ANOTHER_ACCOUNT", "BUNDLE_START_ERROR", "BUNDLE_STOP_ERROR", "PACKAGE_URI_SYNTAX_ERROR", "MAX_NUMBER_OF_ITEMS_REACHED",
                "DEVICE_NOT_FOUND", "DOWNLOAD_PACKAGE_EXCEPTION", "PERMISSION_DELETE_NOT_ALLOWED", "SERVICE_DISABLED"};
    }

    @Test
    public void exceptionInfoWithoutParametersTest() {
        ExceptionInfo exceptionInfo = new ExceptionInfo();

        assertNull("Null expected.", exceptionInfo.getKapuaErrorCode());
        assertEquals("Expected and actual values should be the same.", 0, exceptionInfo.getHttpErrorCode());
    }

    @Test
    public void exceptionInfoWithParametersTest() {
        for (int i = 0; i < statusList.length; i++) {
            for (int j = 0; j < kapuaErrorCodes.length; j++) {
                KapuaException kapuaException = new KapuaException(kapuaErrorCodes[j]);
                ExceptionInfo exceptionInfo = new ExceptionInfo(statusList[i], kapuaErrorCodes[j], kapuaException);
                assertEquals("Expected and actual values should be the same.", expectedErrorCodes[j], exceptionInfo.getKapuaErrorCode());
                assertEquals("Expected and actual values should be the same.", expectedStatusCodes[i], exceptionInfo.getHttpErrorCode());
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void exceptionInfoNullHttpStatusTest() {
        KapuaException kapuaException = new KapuaException(KapuaErrorCodes.ADMIN_ROLE_DELETED_ERROR);
        new ExceptionInfo(null, KapuaErrorCodes.ADMIN_ROLE_DELETED_ERROR, kapuaException);
    }

    @Test(expected = NullPointerException.class)
    public void exceptionInfoNullErrorCodeTest() {
        KapuaException kapuaException = new KapuaException(KapuaErrorCodes.BUNDLE_START_ERROR);
        new ExceptionInfo(Response.Status.OK, null, kapuaException);
    }

    @Test
    public void exceptionInfoNullExceptionTest() {
        for (int i = 0; i < statusList.length; i++) {
            for (int j = 0; j < kapuaErrorCodes.length; j++) {
                ExceptionInfo exceptionInfo = new ExceptionInfo(statusList[i], kapuaErrorCodes[j], null);
                assertEquals("Expected and actual values should be the same.", expectedErrorCodes[j], exceptionInfo.getKapuaErrorCode());
                assertEquals("Expected and actual values should be the same.", expectedStatusCodes[i], exceptionInfo.getHttpErrorCode());
            }
        }
    }
}