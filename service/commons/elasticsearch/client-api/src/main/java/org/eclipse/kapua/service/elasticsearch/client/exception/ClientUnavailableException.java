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
package org.eclipse.kapua.service.elasticsearch.client.exception;

import org.eclipse.kapua.service.elasticsearch.client.ElasticsearchClientWrapper;

/**
 * {@link ClientException} to throw when che {@link ElasticsearchClientWrapper} is not available.
 *
 * @since 1.0.0
 */
public class ClientUnavailableException extends ClientException {

    private static final long serialVersionUID = 2211521053876589804L;

    private final String reason;

    /**
     * Constructor.
     *
     * @param reason The reason of the {@link ClientUnavailableException}.
     * @since 1.3.0
     */
    public ClientUnavailableException(String reason) {
        super(ClientErrorCodes.CLIENT_UNAVAILABLE, null, reason);

        this.reason = reason;
    }

    /**
     * Constructor.
     *
     * @param cause  The root {@link Throwable} of this {@link ClientUnavailableException}.
     * @param reason The reason of the {@link ClientUnavailableException}.
     * @since 1.3.0
     */
    public ClientUnavailableException(Throwable cause, String reason) {
        super(ClientErrorCodes.CLIENT_UNAVAILABLE, cause, reason);

        this.reason = reason;
    }

    /**
     * Gets the reason of the {@link ClientUnavailableException}.
     *
     * @return The reason of the {@link ClientUnavailableException}.
     * @since 1.3.0
     */
    public String getReason() {
        return reason;
    }
}
