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
package org.eclipse.kapua.service.datastore.internal;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.datastore.internal.mediator.ConfigurationException;
import org.eclipse.kapua.service.datastore.internal.mediator.MessageInfo;
import org.eclipse.kapua.service.datastore.internal.mediator.MessageStoreConfiguration;

/**
 * Datastore configuration provider implementation.
 *
 * @since 1.0.0
 */
public class ConfigurationProviderImpl implements ConfigurationProvider {

    private AccountService accountService;
    private ServiceConfigurationManager serviceConfigurationManager;

    /**
     * Construct the configuration provider with the provided parameters
     *
     * @param serviceConfigurationManager
     * @param accountService
     */
    public ConfigurationProviderImpl(ServiceConfigurationManager serviceConfigurationManager,
            AccountService accountService) {
        this.accountService = accountService;
        this.serviceConfigurationManager = serviceConfigurationManager;
    }

    @Override
    public MessageStoreConfiguration getConfiguration(KapuaId scopeId)
            throws ConfigurationException {
        MessageStoreConfiguration messageStoreConfiguration = null;
        try {
            messageStoreConfiguration = new MessageStoreConfiguration(serviceConfigurationManager.getConfigValues(scopeId, true));
        } catch (KapuaException e) {
            throw new ConfigurationException("Cannot load configuration parameters", e);
        }
        return messageStoreConfiguration;
    }

    @Override
    public MessageInfo getInfo(KapuaId scopeId)
            throws ConfigurationException {
        Account account = null;
        if (scopeId != null) {
            try {
                account = KapuaSecurityUtils.doPrivileged(() -> accountService.find(scopeId));
            } catch (Exception exc) {
                throw new ConfigurationException("Cannot get account information", exc);
            }
        }
        return new MessageInfo(account);
    }

}
