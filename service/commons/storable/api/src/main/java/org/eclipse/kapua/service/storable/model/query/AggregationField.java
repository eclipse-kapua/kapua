/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.service.storable.model.query;

/**
 * {@link AggregationField} definition.
 * <p>
 * It defines an aggregation field for queries.
 *
 * @since 2.0.0
 */

public class AggregationField {

    private String aggregationName;
    private String fieldName;
    private int size;
    private String after;
    private String minFieldName;

    /**
     * Name of the {@code min} sub-aggregation computed on {@link #getMinFieldName()}.
     */
    public static final String MIN_AGGREGATION_NAME = "min_sub_aggregation";

    /**
     * Constructor.
     * <p>
     * Required by JAXB.
     *
     * @since 2.0.0
     */
    public AggregationField() {
    }

    public AggregationField(String aggregationName, String fieldName, int size) {
        this.aggregationName = aggregationName;
        this.fieldName = fieldName;
        this.size = size;
    }

    /**
     * @param after The value of the aggregation field after which the buckets are returned (exclusive), {@code null} to start from the first bucket.
     */
    public AggregationField(String aggregationName, String fieldName, int size, String after) {
        this(aggregationName, fieldName, size);
        this.after = after;
    }

    /**
     * @param minFieldName The name of the (date) field whose minimum value is computed for each bucket, {@code null} to compute nothing.
     */
    public AggregationField(String aggregationName, String fieldName, int size, String after, String minFieldName) {
        this(aggregationName, fieldName, size, after);
        this.minFieldName = minFieldName;
    }

    public String getAggregationName() {
        return aggregationName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public int getSize() {
        return size;
    }

    public String getAfter() {
        return after;
    }

    public String getMinFieldName() {
        return minFieldName;
    }

}
