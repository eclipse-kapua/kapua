/*******************************************************************************
 * Copyright (c) 2016, 2026 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.internal.mediator.ConfigurationException;
import org.eclipse.kapua.service.datastore.internal.mediator.MessageInfo;
import org.eclipse.kapua.service.datastore.internal.mediator.MessageStoreConfiguration;

/**
 * Define a datastore configuration provider.<br>
 * These service is responsible to get the configuration parameters (profiled by account) such as the data ttl.
 *
 * @since 1.0
 */
public interface ConfigurationProvider {

    /**
     * Get the configuration for the given scope
     *
     * @param scopeId
     * @return
     * @throws ConfigurationException
     */
    MessageStoreConfiguration getConfiguration(KapuaId scopeId) throws ConfigurationException;


    /**
     * Get the configuration for the given {@code scopeId}. If a cache is configured and an entry
     * is found for the {@code scopeId}, the cached value is returned, otherwise the configuration
     * is read and the value is cached and returned. If no cache is defined works same as
     * {@link getConfiguration(KapuaId scopeId)}. Cache entries expire after a configured time to
     * allow cached configurations to be refreshed.
     *
     * @param scopeId
     * @return
     * @throws ConfigurationException
     */
    MessageStoreConfiguration getConfigurationFromCache(KapuaId scopeId) throws ConfigurationException;

    /**
     * Get the message information for the given scope
     *
     * @param scopeId
     * @return
     * @throws ConfigurationException
     */
    MessageInfo getInfo(KapuaId scopeId) throws ConfigurationException;
}
