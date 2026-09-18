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
package org.eclipse.kapua.service.elasticsearch.client.rest.lowlevel;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Picks the {@link DeviceStoreClientBuilder} whose {@link DeviceStoreClientBuilder#getId()} matches a configured engine id, out of whatever
 * {@link DeviceStoreClientBuilder}s are contributed on the classpath.
 * <p>
 * Shared by every consumer that needs to resolve its own {@link DeviceStoreClientBuilder} (e.g. kapua's own datastore) so
 * the "read a setting, find the matching builder" logic isn't duplicated in each of them.
 *
 * @since 2.1.0
 */
public class DeviceStoreClientBuilderLocator {

    /**
     * @param engineId
     *         The configured engine id (e.g. {@code "elasticsearch"}, or for example {@code "opensearch"}), matched case-insensitively.
     * @param candidates
     *         Every {@link DeviceStoreClientBuilder} contributed on the classpath.
     * @return The matching {@link DeviceStoreClientBuilder}.
     * @throws IllegalArgumentException
     *         if no contributed {@link DeviceStoreClientBuilder} has a matching {@link DeviceStoreClientBuilder#getId()}.
     */
    public DeviceStoreClientBuilder locate(String engineId, Set<DeviceStoreClientBuilder> candidates) {
        for (DeviceStoreClientBuilder candidate : candidates) {
            if (engineId.equalsIgnoreCase(candidate.getId())) {
                return candidate;
            }
        }
        String availableIds = candidates.stream().map(DeviceStoreClientBuilder::getId).collect(Collectors.joining(", "));
        throw new IllegalArgumentException(String.format("Unable to find a DeviceStoreClientBuilder for engine '%s'. Available: [%s]", engineId, availableIds));
    }
}
