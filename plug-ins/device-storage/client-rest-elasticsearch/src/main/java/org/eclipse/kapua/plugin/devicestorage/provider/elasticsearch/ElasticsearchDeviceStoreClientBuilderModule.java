/*******************************************************************************
 * Copyright (c) 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.plugin.devicestorage.provider.elasticsearch;

import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.service.storeengine.client.rest.lowlevel.StoreEngineClientBuilder;

import com.google.inject.multibindings.ProvidesIntoSet;

/**
 * Contributes {@link ElasticsearchStoreEngineClientBuilder} to the {@link java.util.Set} of {@link StoreEngineClientBuilder}s that a consuming module
 * (e.g. {@code DatastoreModule}) picks from, by {@link StoreEngineClientBuilder#getId()}.
 * <p>
 * Deliberately not {@code @Singleton}: {@link StoreEngineClientBuilder} is stateful (see {@link StoreEngineClientBuilder#initializeAndSetHosts}), so each
 * consumer that resolves a builder out of the {@link java.util.Set} must get its own instance.
 *
 * @since 2.1.0
 */
public class ElasticsearchDeviceStoreClientBuilderModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        // Nothing to bind here - the builder is contributed via @ProvidesIntoSet below.
    }

    @ProvidesIntoSet
    StoreEngineClientBuilder elasticsearchDeviceStoreClientBuilder() {
        return new ElasticsearchStoreEngineClientBuilder();
    }
}
