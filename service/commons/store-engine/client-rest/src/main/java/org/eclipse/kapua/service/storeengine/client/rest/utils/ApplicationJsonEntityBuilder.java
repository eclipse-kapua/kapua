/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.storeengine.client.rest.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.entity.ContentType;

/**
 * Utility class that builds an {@link HttpEntity} with {@link EntityBuilder#setContentType(ContentType)} set to {@link ContentType#APPLICATION_JSON}.
 * <p>
 * Hence the name, what a surprise!
 *
 * @since 1.3.0
 * @deprecated 1.5.0
 */
@Deprecated
public class ApplicationJsonEntityBuilder {

    private ApplicationJsonEntityBuilder() {
    }

    public static HttpEntity buildFrom(String jsonText) {
        return EntityBuilder.create()
                .setText(jsonText)
                .setContentType(ContentType.APPLICATION_JSON)
                .build();
    }
}
