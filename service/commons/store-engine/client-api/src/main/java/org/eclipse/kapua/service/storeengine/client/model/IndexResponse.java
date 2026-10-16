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
package org.eclipse.kapua.service.storeengine.client.model;

import java.util.Arrays;

/**
 * {@link IndexResponse} definition.
 *
 * @since 1.0.0
 */
public class IndexResponse {

    private boolean indexExists;
    private String[] indexes;

    /**
     * Constructor.
     *
     * @param indexExists Whether or not the index esists.
     * @since 1.0.0
     */
    public IndexResponse(boolean indexExists) {
        this.indexExists = indexExists;
    }

    /**
     * Constructor.
     *
     * @param indexes The index names.
     * @since 1.0.0
     */
    public IndexResponse(String[] indexes) {
        this.indexes = indexes;
    }

    /**
     * Gets whether or not the index esists.
     *
     * @return {@code true} if index exists, {@code false} otherwise.
     * @since 1.0.0
     */
    public boolean isIndexExists() {
        return indexExists;
    }

    /**
     * Gets the index names.
     *
     * @return The index names.
     * @since 1.0.0
     */
    public String[] getIndexes() {
        return indexes;
    }

    @Override
    public String toString() {
        return "IndexResponse{" +
                "indexExists=" + indexExists +
                ", indexes=" + Arrays.toString(indexes) +
                '}';
    }
}
