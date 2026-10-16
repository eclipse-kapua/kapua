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
 * {@link ClientException} to {@code throw} when there is a datamodel mapping error.
 *
 * @since 1.0.0
 */
public class DatamodelMappingException extends ClientException {

    private static final long serialVersionUID = 5211237236391747299L;

    private final String reason;

    /**
     * Construct the exception with the provided message
     *
     * @param reason The reason of the {@link DatamodelMappingException}.
     * @since 1.3.0
     */
    public DatamodelMappingException(String reason) {
        super(ClientErrorCodes.DATAMODEL_MAPPING_EXCEPTION, reason);

        this.reason = reason;
    }

    /**
     * Constructor.
     *
     * @param cause  The root {@link Throwable} of this {@link DatamodelMappingException}.
     * @param reason The reason of the {@link DatamodelMappingException}.
     * @since 1.3.0
     */
    public DatamodelMappingException(Throwable cause, String reason) {
        super(ClientErrorCodes.DATAMODEL_MAPPING_EXCEPTION, cause, reason);

        this.reason = reason;
    }

    /**
     * Gets the reason of the {@link DatamodelMappingException}.
     *
     * @return The reason of the {@link DatamodelMappingException}.
     * @since 1.3.0
     */
    public String getReason() {
        return reason;
    }
}
