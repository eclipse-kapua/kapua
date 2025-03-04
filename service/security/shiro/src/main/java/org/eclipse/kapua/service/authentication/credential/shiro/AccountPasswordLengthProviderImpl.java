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
package org.eclipse.kapua.service.authentication.credential.shiro;

import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.shiro.SystemPasswordLengthProvider;
import org.eclipse.kapua.storage.TxContext;

public class AccountPasswordLengthProviderImpl implements AccountPasswordLengthProvider {

    private final SystemPasswordLengthProvider systemPasswordLengthProvider;
    private final ServiceConfigurationManager credentialServiceConfigurationManager;
    public static final String PASSWORD_MIN_LENGTH_ACCOUNT_CONFIG_KEY = "password.minLength";

    public AccountPasswordLengthProviderImpl(SystemPasswordLengthProvider systemPasswordLengthProvider, ServiceConfigurationManager credentialServiceConfigurationManager) {
        this.systemPasswordLengthProvider = systemPasswordLengthProvider;
        this.credentialServiceConfigurationManager = credentialServiceConfigurationManager;
    }

    @Override
    public int getMinimumPasswordLength(TxContext txContext, KapuaId scopeId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");

        // Check access
        // None

        // Get system minimum password length
        int minPasswordLength = systemPasswordLengthProvider.getSystemMinimumPasswordLength();

        if (!KapuaId.ANY.equals(scopeId)) {
            Object minPasswordLengthAccountConfigValue = getConfigValues(txContext, scopeId).get(PASSWORD_MIN_LENGTH_ACCOUNT_CONFIG_KEY);
            if (minPasswordLengthAccountConfigValue != null) {
                minPasswordLength = Integer.parseInt(minPasswordLengthAccountConfigValue.toString());
            }
        }
        return minPasswordLength;
    }

    @Override
    public int getMaximumPasswordLength(TxContext txContext, KapuaId scopeId) throws KapuaException {
        return systemPasswordLengthProvider.getSystemMaximumPasswordLength();
    }

    private Map<String, Object> getConfigValues(TxContext tx, KapuaId scopeId) throws KapuaException {
        return credentialServiceConfigurationManager.getConfigValues(tx, scopeId, true);
    }
}
