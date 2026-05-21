/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.account;

import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.KapuaNamedEntityAttributes;

/**
 * {@link Account} {@link KapuaEntityAttributes}.
 *
 * @see KapuaEntityAttributes
 * @since 1.0.0
 */
public class AccountAttributes extends KapuaNamedEntityAttributes {

    /**
     * @since 1.0.0
     */
    public static final String PARENT_ACCOUNT_PATH = "parentAccountPath";

    /**
     * @since 1.0.0
     */
    public static final String CHILD_ACCOUNTS = "childAccounts";

    /**
     * @since 1.0.0
     */
    public static final String ORGANIZATION = "organization";

    /**
     * @since 1.0.0
     */
    public static final String ORGANIZATION_NAME = ORGANIZATION + ".name";

    /**
     * @since 1.0.0
     */
    public static final String ORGANIZATION_EMAIL = ORGANIZATION + ".email";

    /**
     * @since 1.0.0
     */
    public static final String EXPIRATION_DATE = "expirationDate";

    /**
     * @since 1.0.0
     */
    public static final String CONTACT_NAME = ORGANIZATION + ".personName";

    /**
     * @since 1.0.0
     */
    public static final String PHONE_NUMBER = ORGANIZATION + ".phoneNumber";

    /**
     * @since 1.0.0
     */
    public static final String ADDRESS_1 = ORGANIZATION + ".addressLine1";

    /**
     * @since 1.0.0
     */
    public static final String ADDRESS_2 = ORGANIZATION + ".addressLine2";

    /**
     * @since 1.0.0
     */
    public static final String ADDRESS_3 = ORGANIZATION + ".addressLine3";

    /**
     * @since 1.0.0
     */
    public static final String ZIP_POST_CODE = ORGANIZATION + ".zipPostCode";

    /**
     * @since 1.0.0
     */
    public static final String ORGANIZATION_CITY = ORGANIZATION + ".city";

    /**
     * @since 1.0.0
     */
    public static final String STATE_PROVINCE = ORGANIZATION + ".stateProvinceCounty";

    /**
     * @since 1.0.0
     */
    public static final String ORGANIZATION_COUNTRY = ORGANIZATION + ".country";

    /**
     * @since 2.0.0
     */
    public static final String STATUS = "status";
}
