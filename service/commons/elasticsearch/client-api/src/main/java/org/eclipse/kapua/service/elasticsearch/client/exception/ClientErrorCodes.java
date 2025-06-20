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

import org.eclipse.kapua.KapuaErrorCode;

/**
 * {@link ClientException} {@link KapuaErrorCode}s
 *
 * @since 1.0.0
 */
public enum ClientErrorCodes implements KapuaErrorCode {

    /**
     * See {@link ClientUnavailableException}.
     *
     * @since 1.0.0
     */
    CLIENT_UNAVAILABLE,

    /**
     * See {@link ClientInitializationException}.
     *
     * @since 1.3.0
     */
    CLIENT_INITIALIZATION_ERROR,

    /**
     * See @{@link ClientProviderInitException}
     *
     * @since 1.3.0
     */
    CLIENT_PROVIDER_INIT_ERROR,

    /**
     * See {@link ClientClosingException}.
     *
     * @since 1.3.0
     */
    CLIENT_CLOSING_ERROR,

    /**
     * See {@link DatamodelMappingException}.
     *
     * @since 1.0.0
     */
    DATAMODEL_MAPPING_EXCEPTION,

    /**
     * See {@link QueryMappingException}.
     *
     * @since 1.0.0
     */
    QUERY_MAPPING_EXCEPTION,

    /**
     * Error on performed action.
     *
     * @since 1.0.0
     */
    ACTION_ERROR,

    /**
     * See @{@link ClientCommunicationException}.
     *
     * @since 1.0.0
     */
    CLIENT_COMMUNICATION_ERROR,

    /**
     * See {@link ClientInternalError}.
     *
     * @since 1.3.0
     */
    INTERNAL_ERROR,

    /**
     * See {@link ClientActionResponseException}
     *
     * @since 1.3.0
     */
    ACTION_RESPONSE_ERROR,

}
