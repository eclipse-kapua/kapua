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
package org.eclipse.kapua.commons.rest.jersey;

import javax.ws.rs.ext.ContextResolver;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;

public class MoxyJsonConfigContextResolver implements ContextResolver<MoxyJsonConfig> {

    MoxyJsonConfig config;

    public MoxyJsonConfigContextResolver() {
        config = new MoxyJsonConfig().property(JAXBContextProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
    }

    @Override
    public MoxyJsonConfig getContext(Class<?> type) {
        return config;
    }
}
