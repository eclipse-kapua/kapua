/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.endpoint;

import org.eclipse.kapua.model.KapuaEntityFactory;

/**
 * {@link EndpointInfoFactory} definition.
 *
 * @since 1.0.0
 */
public interface EndpointInfoFactory extends KapuaEntityFactory<EndpointInfo> {

    /**
     * Instantiates a new {@link EndpointUsage}.
     *
     * @param name
     *         The name to set into the {@link EndpointUsage}.
     * @return The newly instantiated {@link EndpointUsage}.
     * @since 1.0.0
     */
    EndpointUsage newEndpointUsage(String name);

}
