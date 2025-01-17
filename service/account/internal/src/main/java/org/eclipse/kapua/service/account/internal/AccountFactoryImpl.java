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
package org.eclipse.kapua.service.account.internal;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.Organization;

/**
 * {@link AccountFactory} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class AccountFactoryImpl implements AccountFactory {

    @Override
    public Account newEntity(KapuaId scopeId) {
        return new AccountImpl(scopeId);
    }

    @Override
    public Organization newOrganization() {
        return new OrganizationImpl();
    }

    @Override
    public Account clone(Account account) {
        try {
            return new AccountImpl(account);
        } catch (Exception e) {
            throw new KapuaEntityCloneException(e, Account.TYPE, account);
        }
    }
}
