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

/**
 * {@link ClientException} to {@code throw} when there is a client communication exception (timeout, no node available ...)
 *
 * @since 1.0.0
 */
public class ClientCommunicationException extends ClientException {

    private static final long serialVersionUID = 1599649533697887868L;

    /**
     * Constructor.
     *
     * @since 1.3.0
     */
    public ClientCommunicationException() {
        super(ClientErrorCodes.CLIENT_COMMUNICATION_ERROR);
    }
}
