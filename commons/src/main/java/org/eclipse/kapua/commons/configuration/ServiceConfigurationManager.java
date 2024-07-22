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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.config.ServiceComponentConfiguration;
import org.eclipse.kapua.storage.TxContext;

public interface ServiceConfigurationManager {

    /**
     * Whether this {@link KapuaService} is enabled for the given scope {@link KapuaId}.
     *
     * @param scopeId
     *         The scope {@link KapuaId} for which to check.
     * @return {@code true} if the {@link KapuaService} is enabled, {@code false} otherwise.
     * @since 1.2.0
     */
    default boolean isServiceEnabled(TxContext txContext, KapuaId scopeId) {
        return true;
    }

    String getDomain();

    void checkAllowedEntities(TxContext txContext, KapuaId scopeId, String entityType) throws KapuaException;

    void setConfigValues(TxContext txContext, KapuaId scopeId, Optional<KapuaId> parentId, Map<String, Object> values) throws KapuaException;

    Map<String, Object> getConfigValues(TxContext txContext, KapuaId scopeId, boolean excludeDisabled) throws KapuaException;

    KapuaTocd getConfigMetadata(TxContext txContext, KapuaId scopeId, boolean excludeDisabled) throws KapuaException;

    ServiceComponentConfiguration extractServiceComponentConfiguration(TxContext txContext, KapuaId scopeId) throws KapuaException;
}
