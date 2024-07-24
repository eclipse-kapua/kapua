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
package org.eclipse.kapua.commons.configuration;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.cache.LocalCache;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.config.KapuaConfigurableService;
import org.eclipse.kapua.service.config.ServiceComponentConfiguration;
import org.eclipse.kapua.storage.TxContext;

public class ServiceConfigurationManagerCachingWrapper implements ServiceConfigurationManager {

    private final ServiceConfigurationManager wrapped;

    private static final int LOCAL_CACHE_SIZE_MAX = SystemSetting.getInstance().getInt(SystemSettingKey.TMETADATA_LOCAL_CACHE_SIZE_MAXIMUM, 100);

    /**
     * This cache is to hold the {@link KapuaTocd}s that are read from the metatype files.
     * <p>
     * The key is a {@link Triple} composed by:
     * <ul>
     *     <li>The {@link KapuaConfigurableService} PID</li>
     *     <li>The ID of the {@link Account} for the current request</li>
     *     <li>A {@link Boolean} flag indicating whether disabled properties are excluded from the AD or not</li>
     * </ul>
     *
     * @since 1.2.0
     */
    private final LocalCache<Pair<KapuaId, Boolean>, KapuaTocd> kapuaTocdLocalCache = new LocalCache<>(LOCAL_CACHE_SIZE_MAX, null);

    /**
     * This cache only holds the {@link Boolean} value {@literal True} if the {@link KapuaTocd} has been already read from the file at least once, regardless of the value. With this we can know when a
     * read from {@code KAPUA_TOCD_LOCAL_CACHE} returns {@literal null} because of the requested key is not present, and when the key is present but its actual value is {@literal null}.
     *
     * @since 1.2.0
     */
    private final LocalCache<Pair<KapuaId, Boolean>, Boolean> kapuaTocdEmptyLocalCache = new LocalCache<>(LOCAL_CACHE_SIZE_MAX, false);

    public ServiceConfigurationManagerCachingWrapper(ServiceConfigurationManager wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getDomain() {
        return wrapped.getDomain();
    }

    @Override
    public void checkAllowedEntities(TxContext txContext, KapuaId scopeId, String entityType) throws KapuaException {
        wrapped.checkAllowedEntities(txContext, scopeId, entityType);
    }

    @Override
    public void setConfigValues(KapuaId scopeId, Optional<KapuaId> parentId, Map<String, Object> values) throws KapuaException {
        wrapped.setConfigValues(scopeId, parentId, values);

    }

    @Override
    public Map<String, Object> getConfigValues(KapuaId scopeId, boolean excludeDisabled) throws KapuaException {
        return wrapped.getConfigValues(scopeId, excludeDisabled);
    }

    @Override
    public Map<String, Object> getConfigValues(TxContext txContext, KapuaId scopeId, boolean excludeDisabled) throws KapuaException {
        return wrapped.getConfigValues(txContext, scopeId, excludeDisabled);
    }

    @Override
    public KapuaTocd getConfigMetadata(KapuaId scopeId, boolean excludeDisabled) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");

        // Get the Tocd
        // Keep distinct values for service PID, Scope ID and disabled properties included/excluded from AD
        Pair<KapuaId, Boolean> cacheKey = Pair.of(scopeId, excludeDisabled);
        try {
            // Check if the OCD is already in cache, but not in the "empty" cache
            KapuaTocd tocd;
            tocd = kapuaTocdLocalCache.get(cacheKey);
            if (tocd == null && !kapuaTocdEmptyLocalCache.get(cacheKey)) {
                // If not, read metadata and process it
                tocd = wrapped.getConfigMetadata(scopeId, excludeDisabled);
                // If null, put it in the "empty" ocd cache, else put it in the "standard" cache
                if (tocd != null) {
                    // If the value is not null, put it in "standard" cache and remove the entry from the "empty" cache if present
                    kapuaTocdLocalCache.put(cacheKey, tocd);
                    kapuaTocdEmptyLocalCache.remove(cacheKey);
                } else {
                    // If the value is null, just remember we already read it from file at least once
                    kapuaTocdEmptyLocalCache.put(cacheKey, true);
                }
            }
            return tocd;
        } catch (Exception e) {
            throw KapuaException.internalError(e);
        }
    }

    @Override
    public ServiceComponentConfiguration extractServiceComponentConfiguration(KapuaId scopeId) throws KapuaException {
        return wrapped.extractServiceComponentConfiguration(scopeId);
    }
}
