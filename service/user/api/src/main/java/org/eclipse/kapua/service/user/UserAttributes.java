/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.user;

import org.eclipse.kapua.model.KapuaNamedEntityAttributes;

/**
 * {@link UserAttributes} attributes.
 *
 * @see org.eclipse.kapua.model.KapuaEntityAttributes
 * @since 1.0.0
 */
public class UserAttributes extends KapuaNamedEntityAttributes {

    public static final String TAG_IDS = "tagIds";
    public static final String STATUS = "status";
    public static final String EXPIRATION_DATE = "expirationDate";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String EMAIL = "email";
    public static final String DISPLAY_NAME = "displayName";
    public static final String EXTERNAL_ID = "externalId";
    public static final String EXTERNAL_USERNAME = "externalUsername";
    public static final String USER_TYPE = "userType";
}
