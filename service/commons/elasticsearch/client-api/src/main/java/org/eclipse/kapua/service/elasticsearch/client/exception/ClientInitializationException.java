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
 * {@link ClientException} to throw when {@link ElasticsearchClientWrapper} fails to initialize.
 *
 * @since 1.3.0
 */
public class ClientInitializationException extends ClientException {

    private static final long serialVersionUID = 2211521053876589804L;

    private final String reason;

    /**
     * Constructor.
     *
     * @param reason The reason of the exception.
     * @since 1.3.0
     */
    public ClientInitializationException(String reason) {
        this(null, reason);

    }


    /**
     * Constructor.
     *
     * @param cause  The root {@link Throwable} of this {@link ClientInitializationException}.
     * @param reason The reason of the exception.
     * @since 1.3.0
     */
    public ClientInitializationException(Throwable cause, String reason) {
        super(ClientErrorCodes.CLIENT_INITIALIZATION_ERROR, cause);

        this.reason = reason;
    }

    /**
     * Gets the reason of the initialization error.
     *
     * @return The reason of the initialization error.
     * @since 1.3.0
     */
    public String getReason() {
        return reason;
    }
}
