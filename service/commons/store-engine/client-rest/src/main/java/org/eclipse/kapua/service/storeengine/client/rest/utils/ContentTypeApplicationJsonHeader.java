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

import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;

/**
 * Utility class that initializes a {@link BasicHeader} with parameters:
 * <ul>
 *     <li>{@code name}: {@link HttpHeaders#CONTENT_TYPE}</li>
 *     <li>{@code value}: {@link ContentType#APPLICATION_JSON}</li>
 * </ul>
 * <p>
 * Hence the name, what a surprise!
 *
 * @since 1.3.0
 */
public class ContentTypeApplicationJsonHeader extends BasicHeader {

    /**
     * Constructor.
     *
     * @since 1.3.0
     */
    public ContentTypeApplicationJsonHeader() {
        super(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
    }
}
