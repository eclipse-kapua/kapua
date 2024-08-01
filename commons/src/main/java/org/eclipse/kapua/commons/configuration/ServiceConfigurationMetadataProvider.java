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

import java.util.Optional;

import org.eclipse.kapua.model.config.metatype.KapuaTmetadata;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.service.config.KapuaConfigurableService;
import org.eclipse.kapua.storage.TxContext;

public interface ServiceConfigurationMetadataProvider {

    /**
     * Reads the {@link KapuaTmetadata} for the given {@link KapuaConfigurableService} pid. If no metadata can be found, just return Optional.empty() Same if the metadata is empty (containing no
     * {@link KapuaTocd}s)
     *
     * @param txContext
     *         the transaction context (can be ignored if the data source is not transactional)
     * @param pid
     *         The {@link KapuaConfigurableService} pid
     * @return The {@link KapuaTmetadata} for the given {@link KapuaConfigurableService} pid.
     * @since 1.0.0
     */
    Optional<KapuaTmetadata> fetchMetadata(TxContext txContext, String pid);
}
