/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.jersey.rest;

import org.eclipse.kapua.KapuaRuntimeException;

public class RestApiRuntimeException extends KapuaRuntimeException {

    /**
     * Builds a new {@link RestApiRuntimeException} instance based on the supplied {@link RestApiErrorCodes}.
     *
     * @param code
     */
    public RestApiRuntimeException(RestApiErrorCodes code) {
        this(code, (Object[]) null);
    }

    /**
     * Builds a new {@link RestApiRuntimeException} instance based on the supplied {@link RestApiErrorCodes} and optional arguments for the associated exception message.
     *
     * @param code
     * @param arguments
     */
    public RestApiRuntimeException(RestApiErrorCodes code, Object... arguments) {
        this(code, null, arguments);
    }

    /**
     * Builds a new {@link KapuaRuntimeException} instance based on the supplied {@link RestApiErrorCodes}, an Throwable cause, and optional arguments for the associated exception message.
     *
     * @param code
     * @param cause
     * @param arguments
     */
    public RestApiRuntimeException(RestApiErrorCodes code, Throwable cause, Object... arguments) {
        super(code, cause, arguments);
    }
}
