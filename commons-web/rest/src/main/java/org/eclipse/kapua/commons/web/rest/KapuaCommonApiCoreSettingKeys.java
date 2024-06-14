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
package org.eclipse.kapua.commons.web.rest;

import org.eclipse.kapua.commons.setting.SettingKey;

/**
 * Common REST API {@link SettingKey}s
 */
public enum KapuaCommonApiCoreSettingKeys implements SettingKey {

    API_EXCEPTION_STACKTRACE_SHOW("api.exception.stacktrace.show"),

    /**
     * @since 1.0.0
     */
    API_PATH_PARAM_SCOPEID_WILDCARD("api.path.param.scopeId.wildcard");

    private final String key;

    /**
     * Constructor
     *
     * @param key
     *         The value for the setting.
     * @since 1.0.0
     */
    KapuaCommonApiCoreSettingKeys(String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }
}
