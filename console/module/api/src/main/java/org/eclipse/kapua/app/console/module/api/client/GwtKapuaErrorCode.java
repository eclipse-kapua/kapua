/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.api.client;

public enum GwtKapuaErrorCode {
    CANNOT_REMOVE_LAST_ADMIN,
    CURRENT_ADMIN_PASSWORD_DOES_NOT_MATCH,
    DUPLICATE_NAME,
    DEVICE_NOT_CONNECTED,
    ENTITY_UNIQUENESS,
    ENTITY_NOT_FOUND,
    ILLEGAL_ACCESS,
    ILLEGAL_ARGUMENT,
    ILLEGAL_NULL_ARGUMENT,
    INTERNAL_ERROR,
    INVALID_RULE_QUERY,
    INVALID_USERNAME_PASSWORD,
    INVALID_XSRF_TOKEN,
    LOCKED_USER,
    OVER_RULE_LIMIT,
    UNAUTHENTICATED,
    WARNING,
    XSRF_INVALID_TOKEN,
    PARENT_LIMIT_EXCEEDED_IN_CONFIG,
    SUBJECT_UNAUTHORIZED,
    ENTITY_ALREADY_EXISTS,
    UNABLE_TO_PARSE_CRON_EXPRESSION,
    ENTITY_ALREADY_EXIST_IN_ANOTHER_ACCOUNT,
    EXTERNAL_ID_ALREADY_EXIST,
    EXTERNAL_USERNAME_ALREADY_EXIST,
    SELF_LIMIT_EXCEEDED_IN_CONFIG,
    MAX_NUMBER_OF_ITEMS_REACHED,
    OPERATION_NOT_ALLOWED_ON_ADMIN_USER,
    RETRY_AND_CRON_BOTH_SELECTED,
    END_BEFORE_START_TIME_ERROR,
    SAME_START_AND_DATE,
    SCHEDULE_DUPLICATE_NAME,
    TRIGGER_NEVER_FIRE,
    JOB_STARTING_ERROR,
    ADMIN_ROLE_DELETED_ERROR,
    PERMISSION_DELETE_NOT_ALLOWED,
    JOB_STOPPING_ERROR,
    REQUIRE_MFA_CODE,

    // Device Management Packages
    PACKAGE_URI_SYNTAX_ERROR,

    // Device Management Inventory
    INVENTORY_GET_ERROR,

    // Device management Generic
    DEVICE_MANAGEMENT_RESPONSE_BAD_REQUEST,
    DEVICE_MANAGEMENT_RESPONSE_NOT_FOUND,
    DEVICE_MANAGEMENT_RESPONSE_INTERNAL_ERROR,
    DEVICE_MANAGEMENT_SEND_ERROR,
    DEVICE_MANAGEMENT_TIMEOUT,
    DEVICE_MANAGEMENT_ERROR,
    OPENID_ERROR
}
