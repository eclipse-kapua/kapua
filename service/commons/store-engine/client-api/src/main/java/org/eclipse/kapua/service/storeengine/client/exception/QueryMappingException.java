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
 * {@link ClientException} to {@code throw} when there is a quuery mapping error.
 *
 * @since 1.0.0
 */
public class QueryMappingException extends ClientException {

    private static final long serialVersionUID = 5211237236391747299L;

    /**
     * Constructor.
     *
     * @param reason The reason of this {@link QueryMappingException}.
     * @since 1.3.0
     */
    public QueryMappingException(String reason) {
        this(null, reason);
    }

    /**
     * Constructor.
     *
     * @param cause  The root cause of the {@link QueryMappingException}.
     * @param reason The reason of this {@link QueryMappingException}.
     * @since 1.3.0
     */
    public QueryMappingException(Throwable cause, String reason) {
        super(ClientErrorCodes.QUERY_MAPPING_EXCEPTION, cause, reason);
    }
}
