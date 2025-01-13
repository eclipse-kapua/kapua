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
package org.eclipse.kapua.service.account.xml;

import javax.xml.bind.annotation.XmlRegistry;

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountCreator;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountQuery;
import org.eclipse.kapua.service.account.Organization;

/**
 * {@link Account} xml factory class
 *
 * @since 1.0.0
 */
@XmlRegistry
public class AccountXmlRegistry {

    private final KapuaLocator locator = KapuaLocator.getInstance();
    private final AccountFactory factory = locator.getFactory(AccountFactory.class);

    /**
     * Instantiates a new {@link Account}.
     *
     * @return The newly instantiated {@link Account}.
     * @since 1.0.0
     */
    public Account newAccount() {
        return factory.newEntity(null);
    }

    /**
     * Instantiates a new {@link AccountCreator}.
     *
     * @return The newly instantiated {@link AccountCreator}.
     * @since 1.0.0
     */
    public AccountCreator newAccountCreator() {
        return factory.newCreator(null);
    }

    /**
     * Instantiates a new {@link AccountQuery}.
     *
     * @return The newly instantiated {@link AccountQuery}.
     * @since 1.0.0
     */
    public AccountQuery newQuery() {
        return factory.newQuery(null);
    }

    /**
     * Instantiates a new {@link Organization}.
     *
     * @return The newly instantiated {@link Organization}.
     * @since 1.0.0
     */
    public Organization newOrganization() {
        return factory.newOrganization();
    }
}
