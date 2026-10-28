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
package org.eclipse.kapua.service.account.internal.setting;

import org.eclipse.kapua.commons.setting.SettingKey;

/**
 * Available settings key for account service
 *
 * @since 1.0
 *
 */
public enum KapuaAccountSettingKeys implements SettingKey {
    /**
     * The key value in the configuration resources.
     */
    ACCOUNT_KEY("account.key"),
    ACCOUNT_EVENT_ADDRESS("account.eventAddress"),

    /**
     * Maximum size of the local cache of {@link org.eclipse.kapua.service.account.Account}s by name (@Named("accountsByNameCache")), shared by Translators.
     *
     * @since 2.1.0
     */
    ACCOUNT_CACHE_LOCAL_SIZE_MAXIMUM("account.cache.local.size.maximum"),

    /**
     * Expiration time (in seconds) of the local cache of {@link org.eclipse.kapua.service.account.Account}s by name (@Named("accountsByNameCache")), shared by Translators.
     *
     * @since 2.1.0
     */
    ACCOUNT_CACHE_LOCAL_EXPIRE_AFTER("account.cache.local.expire.after"),

    /**
     * Expiration strategy of the local cache of {@link org.eclipse.kapua.service.account.Account}s by name (@Named("accountsByNameCache")), shared by Translators.
     *
     * @since 2.1.0
     */
    ACCOUNT_CACHE_LOCAL_EXPIRE_STRATEGY("account.cache.local.expire.strategy");

    private String key;

    /**
     * Set up the {@code enum} with the key value provided
     *
     * @param key
     *            The value mapped by this {@link Enum} value
     */
    private KapuaAccountSettingKeys(String key) {
        this.key = key;
    }

    /**
     * Gets the key for this {@link KapuaAccountSettingKeys}
     *
     */
    public String key() {
        return key;
    }
}
