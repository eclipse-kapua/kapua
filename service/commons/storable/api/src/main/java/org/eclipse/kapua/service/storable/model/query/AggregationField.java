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

    private final String aggregationName;
    private final String fieldName;
    private final int size;


    public AggregationField(String aggregationName, String fieldName, int size) {
        this.aggregationName = aggregationName;
        this.fieldName = fieldName;
        this.size = size;
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

}
