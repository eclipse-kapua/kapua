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
package org.eclipse.kapua.service.authentication.shiro;

import java.util.Map;
import java.util.Optional;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.configuration.RootUserTester;
import org.eclipse.kapua.commons.configuration.ServiceConfigRepository;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManagerImpl;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationMetadataProvider;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.credential.CredentialService;
import org.eclipse.kapua.service.authentication.credential.shiro.AccountPasswordLengthProviderImpl;
import org.eclipse.kapua.service.authentication.shiro.setting.KapuaAuthenticationSetting;
import org.eclipse.kapua.storage.TxContext;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CredentialServiceConfigurationManagerImpl extends ServiceConfigurationManagerImpl implements ServiceConfigurationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialServiceConfigurationManagerImpl.class);
    private final SystemPasswordLengthProvider systemPasswordLengthProvider;

    public CredentialServiceConfigurationManagerImpl(
            TxManager txManager,
            ServiceConfigRepository serviceConfigRepository,
            SystemPasswordLengthProvider systemPasswordLengthProvider,
            RootUserTester rootUserTester,
            KapuaAuthenticationSetting kapuaAuthenticationSetting,
            ServiceConfigurationMetadataProvider serviceConfigurationMetadataProvider) {
        super(CredentialService.class.getName(),
                Domains.CREDENTIAL,
                txManager,
                serviceConfigRepository,
                rootUserTester,
                serviceConfigurationMetadataProvider);
        this.systemPasswordLengthProvider = systemPasswordLengthProvider;
    }

    @Override
    protected boolean validateNewConfigValuesCoherence(TxContext txContext, KapuaTocd ocd, Map<String, Object> updatedProps, KapuaId scopeId, Optional<KapuaId> parentId) throws KapuaException {
        if (updatedProps.get(AccountPasswordLengthProviderImpl.PASSWORD_MIN_LENGTH_ACCOUNT_CONFIG_KEY) != null) {
            // If we're going to set a new limit, check that it's not less than system limit
            int newPasswordLimit = Integer.parseInt(updatedProps.get(AccountPasswordLengthProviderImpl.PASSWORD_MIN_LENGTH_ACCOUNT_CONFIG_KEY).toString());
            if (newPasswordLimit < systemPasswordLengthProvider.getSystemMinimumPasswordLength() || newPasswordLimit > systemPasswordLengthProvider.getSystemMaximumPasswordLength()) {
                throw new KapuaIllegalArgumentException(AccountPasswordLengthProviderImpl.PASSWORD_MIN_LENGTH_ACCOUNT_CONFIG_KEY, String.valueOf(newPasswordLimit));
            }
        }
        return true;
    }

}