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

import org.eclipse.kapua.model.KapuaEntityFactory;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link Account} {@link KapuaEntityFactory} definition.
 *
 * @see KapuaEntityFactory
 * @since 1.0.0
 */
public interface AccountFactory extends KapuaEntityFactory<Account, AccountCreator> {

    /**
     * Instantiates a new {@link AccountCreator} with the given name.
     *
     * @param scopeId
     *         The scope {@link KapuaId} to set in the {@link AccountCreator}
     * @param name
     *         The name to set in the {@link AccountCreator}
     * @return The newly instantiated {@link AccountCreator}
     * @since 1.0.0
     * @deprecated Since 1.6.0. Please make use of {@link #newCreator(KapuaId)} and then use {@link AccountCreator#setName(String)}.
     */
    @Deprecated
    AccountCreator newCreator(KapuaId scopeId, String name);

    /**
     * Instantiates a new {@link Organization}.
     *
     * @return The newly instantiated {@link Organization}.
     * @since 1.0.0
     */
    Organization newOrganization();
}
