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
package org.eclipse.kapua.service.storeengine.client.rest.exception;

import org.eclipse.kapua.service.storeengine.client.exception.ClientInternalError;

/**
 * {@link ClientInternalError} to {@code throw} when there is an error on {@link com.fasterxml.jackson.databind.ObjectMapper#writeValueAsString(Object)}
 *
 * @since 1.3.0
 */
public class RequestEntityWriteError extends ClientInternalError {

    /**
     * Constructor.
     *
     * @param cause The original cause of the error.
     * @since 1.3.0
     */
    public RequestEntityWriteError(Throwable cause) {
        super(cause, "Error managing ObjectMapper.write");
    }
}
