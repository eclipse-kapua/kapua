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
package org.eclipse.kapua.service.elasticsearch.client.exception;

import org.eclipse.kapua.service.elasticsearch.client.ElasticsearchClientWrapper;

/**
 * {@link ClientException} to throw when {@link ElasticsearchClientWrapper} fails to close properly.
 *
 * @since 1.3.0
 */
public class ClientClosingException extends ClientException {

    /**
     * Constructor.
     *
     * @param cause The root {@link Throwable} of this {@link ClientClosingException}.
     * @since 1.3.0
     */
    public ClientClosingException(Throwable cause) {
        super(ClientErrorCodes.CLIENT_CLOSING_ERROR, cause);
    }
}
