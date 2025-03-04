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

import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.kapua.service.authentication.shiro.setting.KapuaAuthenticationSetting;
import org.eclipse.kapua.service.authentication.shiro.setting.KapuaAuthenticationSettingKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class SystemPasswordLengthProviderImpl implements SystemPasswordLengthProvider {

    private final KapuaAuthenticationSetting kapuaAuthenticationSetting;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final int SYSTEM_MAXIMUM_PASSWORD_LENGTH = 255;

    @Inject
    public SystemPasswordLengthProviderImpl(KapuaAuthenticationSetting kapuaAuthenticationSetting) {
        this.kapuaAuthenticationSetting = kapuaAuthenticationSetting;
    }

    @Override
    /**
     * The minimum password length specified for the whole system. If not defined, assume 12; if defined and less than 12, assume 12.
     */
    public int getSystemMinimumPasswordLength() {
        int minPasswordLengthConfigValue;
        try {
            minPasswordLengthConfigValue = kapuaAuthenticationSetting.getInt(KapuaAuthenticationSettingKeys.AUTHENTICATION_CREDENTIAL_USERPASS_PASSWORD_MINLENGTH);
        } catch (NoSuchElementException ex) {
            logger.warn("Minimum password length not set, 12 characters minimum will be enforced");
            minPasswordLengthConfigValue = 12;
        }
        if (minPasswordLengthConfigValue < 12) {
            logger.warn("Minimum password length too short, 12 characters minimum will be enforced");
            minPasswordLengthConfigValue = 12;
        }
        return minPasswordLengthConfigValue;
    }

    @Override
    public int getSystemMaximumPasswordLength() {
        return SYSTEM_MAXIMUM_PASSWORD_LENGTH;
    }
}
