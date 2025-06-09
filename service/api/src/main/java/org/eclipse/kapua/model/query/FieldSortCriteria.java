/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.model.query;

public class FieldSortCriteria implements KapuaSortCriteria {

    /**
     * Field attribute name
     */
    private String attributeName;

    /**
     * Field sort order
     */
    private SortOrder sortOrder;

    /**
     * Constructor
     *
     * @param attributeName
     * @param sortOrder
     */
    public FieldSortCriteria(String attributeName, SortOrder sortOrder) {
        this.attributeName = attributeName;
        this.sortOrder = sortOrder;
    }

    /**
     * Get the sort attribute name
     *
     * @return
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Get the sort attribute order
     *
     * @return
     */
    public SortOrder getSortOrder() {
        return sortOrder;
    }
}
