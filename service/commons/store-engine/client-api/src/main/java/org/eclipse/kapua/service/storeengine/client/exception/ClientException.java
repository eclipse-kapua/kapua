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
package org.eclipse.kapua.service.storeengine.client.exception;

import org.eclipse.kapua.KapuaException;

/**
 * {@link ClientException} definition.
 * <p>
 * This is the base {@link KapuaException} for all detailed {@link Exception} of this module.
 *
 * @since 1.0.0
 */
public class ClientException extends KapuaException {

    private static final long serialVersionUID = 2393001020208113850L;

    private static final String ELASTICSEARCH_CLIENT_ERROR_MESSAGES = "elasticsearch-client-error-messages";

    /**
     * Construct the exception with the provided error code
     *
     * @param code The {@link ClientErrorCodes}
     * @since 1.0.0
     */
    public ClientException(ClientErrorCodes code) {
        super(code);
    }

    /**
     * Constructor.
     *
     * @param code      The {@link ClientErrorCodes}
     * @param arguments Additional argument associated with the {@link ClientException}.
     * @since 1.0.0
     */
    public ClientException(ClientErrorCodes code, Object... arguments) {
        super(code, arguments);
    }

    /**
     * Constructor.
     *
     * @param code      The {@link ClientErrorCodes}
     * @param cause     The root {@link Throwable} of this {@link ClientException}.
     * @param arguments Additional argument associated with the {@link ClientException}.
     * @since 1.0.0
     */
    public ClientException(ClientErrorCodes code, Throwable cause, Object... arguments) {
        super(code, cause, arguments);
    }


    /**
     * Construct the exception with the provided code, throwable and message
     *
     * @param code  The {@link ClientErrorCodes}
     * @param cause The root {@link Throwable} of this {@link ClientException}.
     * @since 1.0.0
     */
    public ClientException(ClientErrorCodes code, Throwable cause) {
        super(code, cause);
    }

    @Override
    protected String getKapuaErrorMessagesBundle() {
        return ELASTICSEARCH_CLIENT_ERROR_MESSAGES;
    }
}
