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

import javax.xml.bind.annotation.XmlRegistry;

import org.eclipse.kapua.locator.KapuaLocator;

@XmlRegistry
public class EndpointInfoXmlRegistry {

    private final EndpointInfoFactory endpointInfoFactory = KapuaLocator.getInstance().getFactory(EndpointInfoFactory.class);

    /**
     * Creates a new {@link EndpointInfo} instance
     *
     * @return
     */
    public EndpointInfo newEntity() {
        return endpointInfoFactory.newEntity(null);
    }

    public EndpointUsage newEndpointUsage() {
        return endpointInfoFactory.newEndpointUsage(null);
    }
}
