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

import java.util.ArrayList;
import java.util.List;

/**
 * {@link BulkUpdateResponse} definition.
 * <p>
 * It bundles a {@link List} of {@link UpdateRequest}
 *
 * @since 1.0.0
 */
public class BulkUpdateResponse {

    List<UpdateResponse> responseList;

    /**
     * Gets the {@link List} of {@link UpdateResponse}s.
     *
     * @return The {@link List} of {@link UpdateResponse}s.
     * @since 1.0.0
     */
    public List<UpdateResponse> getResponse() {
        if (responseList == null) {
            responseList = new ArrayList<>();
        }

        return responseList;
    }

    /**
     * Adds an {@link UpdateResponse} to the {@link List}
     *
     * @param response The {@link UpdateResponse} to add.
     * @since 1.0.0
     */
    public void add(UpdateResponse response) {
        getResponse().add(response);
    }

    /**
     * Sets the {@link List} of {@link UpdateResponse}s.
     *
     * @param responseList The {@link List} of {@link UpdateResponse}s.
     * @since 1.0.0
     */
    public void setResponse(List<UpdateResponse> responseList) {
        this.responseList = responseList;
    }

}
