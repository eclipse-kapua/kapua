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
import org.eclipse.kapua.service.elasticsearch.client.model.Request;

/**
 * {@link ClientException} to {@code throw} when a {@link Request} fails.
 *
 * @since 1.3.0
 */
public class ClientActionResponseException extends ClientException {

    private final String action;
    private final String reason;
    private final String responseCode;

    /**
     * Constructor.
     *
     * @param action       The action that generated the {@link ClientActionResponseException}.
     * @param reason       The reason of the {@link ClientActionResponseException}.
     * @param responseCode The non-2xx response code returned from the {@link ElasticsearchClientWrapper}
     * @since 1.3.0
     */
    public ClientActionResponseException(String action, String reason, String responseCode) {
        super(ClientErrorCodes.ACTION_RESPONSE_ERROR, null, action, reason, responseCode);

        this.reason = reason;
        this.action = action;
        this.responseCode = responseCode;
    }

    /**
     * Constructor.
     *
     * @param cause  The root {@link Throwable} of this {@link ClientActionResponseException}.
     * @param action The action that generated the {@link ClientActionResponseException}.
     * @param reason The reason of the {@link ClientActionResponseException}.
     * @since 1.3.0
     */
    public ClientActionResponseException(Throwable cause, String action, String reason, String responseCode) {
        super(ClientErrorCodes.ACTION_RESPONSE_ERROR, cause, action, reason);

        this.reason = reason;
        this.action = action;
        this.responseCode = responseCode;
    }

    /**
     * Gets the action that generated the {@link ClientActionResponseException}.
     *
     * @return The action that generated the {@link ClientActionResponseException}.
     * @since 1.3.0
     */
    public String getAction() {
        return action;
    }

    /**
     * Gets the reason of the {@link ClientActionResponseException}.
     *
     * @return The reason of the {@link ClientActionResponseException}.
     * @since 1.3.0
     */
    public String getReason() {
        return reason;
    }

    /**
     * Gets the HTTP response code.
     *
     * @return The HTTP response code.
     * @since 1.3.0
     */
    public String getResponseCode() {
        return responseCode;
    }
}
