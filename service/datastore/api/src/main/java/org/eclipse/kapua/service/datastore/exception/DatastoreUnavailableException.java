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
package org.eclipse.kapua.service.datastore.exception;

import org.eclipse.kapua.service.elasticsearch.client.DeviceStoreClientProvider;

/**
 * {@link DatastoreServiceException} to {@code throw} when the underling Elasticsearch is not available.
 *
 * @since 1.3.0
 */
public class DatastoreUnavailableException extends DatastoreServiceException {

    private final String elasticSearchProviderName;

    /**
     * Constructor.
     *
     * @param cause @param cause The root {@link Throwable} of this {@link DatastoreUnavailableException}.
     * @since 1.3.0
     */
    public DatastoreUnavailableException(Throwable cause, String elasticSearchProviderName) {
        super(DatastoreServiceErrorCodes.DATASTORE_UNAVAILABLE_EXCEPTION, cause, elasticSearchProviderName);

        this.elasticSearchProviderName = elasticSearchProviderName;
    }

    /**
     * Gets the {@link DeviceStoreClientProvider} name.
     *
     * @return The {@link DeviceStoreClientProvider} name.
     * @since 1.3.0
     */
    public String getElasticSearchProviderName() {
        return elasticSearchProviderName;
    }
}
