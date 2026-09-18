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
import org.eclipse.kapua.service.elasticsearch.client.rest.lowlevel.DeviceStoreClientBuilder;

import com.google.inject.multibindings.ProvidesIntoSet;

/**
 * Contributes {@link ElasticsearchDeviceStoreClientBuilder} to the {@link java.util.Set} of {@link DeviceStoreClientBuilder}s that a consuming module
 * (e.g. {@code DatastoreModule}) picks from, by {@link DeviceStoreClientBuilder#getId()}.
 * <p>
 * Deliberately not {@code @Singleton}: {@link DeviceStoreClientBuilder} is stateful (see {@link DeviceStoreClientBuilder#initializeAndSetHosts}), so each
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
    DeviceStoreClientBuilder elasticsearchDeviceStoreClientBuilder() {
        return new ElasticsearchDeviceStoreClientBuilder();
    }
}
