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
 * Query result list definition.
 *
 * @param <T> {@link ResultList} object type.
 * @since 1.0.0
 */
public class ResultList<T> {

    private final List<T> result;
    private final long totalCount;
    private boolean totalHitsExceedsCount; //true iff in ES there are actually more than 10k hits

    /**
     * Constructor.
     *
     * @param totalCount The total count of matched objects.
     * @since 1.0.0
     */
    public ResultList(long totalCount) {
        result = new ArrayList<>();

        this.totalCount = totalCount;
    }

    /**
     * Adds a object to the {@link ResultList}.
     *
     * @param object The object to add to the {@link ResultList}.
     * @since 1.0.0
     */
    public void add(T object) {
        result.add(object);
    }

    public void setTotalHitsExceedsCount(boolean value) {
        this.totalHitsExceedsCount=value;
    }

    public boolean getTotalHitsExceedsCount() {
        return this.totalHitsExceedsCount;
    }

    /**
     * Gets the {@link List} of results.
     *
     * @return The {@link List} of results.
     * @since 1.0.0
     */
    public List<T> getResult() {
        return result;
    }

    /**
     * Gets the total count of matched objects.
     *
     * @return The total count of matched objects.
     * @since 1.0.0
     */
    public long getTotalCount() {
        return totalCount;
    }

}
