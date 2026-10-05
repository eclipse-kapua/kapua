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
package org.eclipse.kapua.service.storeengine.client.rest.lowlevel;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Picks the {@link StoreEngineClientBuilder} whose {@link StoreEngineClientBuilder#getId()} matches a configured engine id, out of whatever
 * {@link StoreEngineClientBuilder}s are contributed on the classpath.
 * <p>
 * Shared by every consumer that needs to resolve its own {@link StoreEngineClientBuilder} (e.g. kapua's own datastore) so
 * the "read a setting, find the matching builder" logic isn't duplicated in each of them.
 *
 * @since 2.1.0
 */
public class DeviceStoreClientBuilderLocator {

    /**
     * @param engineId
     *         The configured engine id (e.g. {@code "elasticsearch"}, or for example {@code "opensearch"}), matched case-insensitively.
     * @param candidates
     *         Every {@link StoreEngineClientBuilder} contributed on the classpath.
     * @return The matching {@link StoreEngineClientBuilder}.
     * @throws IllegalArgumentException
     *         if no contributed {@link StoreEngineClientBuilder} has a matching {@link StoreEngineClientBuilder#getId()}.
     */
    public StoreEngineClientBuilder locate(String engineId, Set<StoreEngineClientBuilder> candidates) {
        for (StoreEngineClientBuilder candidate : candidates) {
            if (engineId.equalsIgnoreCase(candidate.getId())) {
                return candidate;
            }
        }
        String availableIds = candidates.stream().map(StoreEngineClientBuilder::getId).collect(Collectors.joining(", "));
        throw new IllegalArgumentException(String.format("Unable to find a StoreEngineClientBuilder for engine '%s'. Available: [%s]", engineId, availableIds));
    }
}
