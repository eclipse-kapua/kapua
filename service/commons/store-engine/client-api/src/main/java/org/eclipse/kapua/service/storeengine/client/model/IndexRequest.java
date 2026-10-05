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

/**
 * {@link IndexRequest} definition.
 *
 * @since 1.0.0
 */
public class IndexRequest {

    private String index;

    /**
     * Constructor.
     *
     * @param index The index name.
     * @since 1.0.0
     */
    public IndexRequest(String index) {
        setIndex(index);
    }

    /**
     * Gets the index name.
     *
     * @return The index name.
     * @since 1.0.0
     */
    public String getIndex() {
        return index;
    }

    /**
     * Sets the index name.
     *
     * @param index The index name.
     * @since 1.3.0
     */
    public void setIndex(String index) {
        this.index = index;
    }
}
