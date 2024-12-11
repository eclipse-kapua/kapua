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

import org.eclipse.kapua.KapuaIllegalArgumentException;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Sort orderdefinition.
 * <p>
 * It define which sorting direction to use when processing {@link KapuaQuery}.
 *
 * @since 1.0.0
 */
@Schema(description = "The sort direction. Can be ASCENDING (default), DESCENDING. Case-insensitive (except for \"clientId\" parameter).")
public enum SortOrder {

    /**
     * Ascending.
     *
     * @since 1.0.0
     */
    ASCENDING,

    /**
     * Descending.
     *
     * @since 1.0.0
     */
    DESCENDING;

    /**
     * It returns a {@link SortOrder} from the given {@link String}
     * <p>
     * It is case insensitive.
     *
     * @param value The {@link String} to convert.
     * @return The converted {@link SortOrder}.
     * @throws KapuaIllegalArgumentException If given {@link String} cannot be converted.
     * @since 1.0.0
     */
    public static SortOrder fromString(String value) throws KapuaIllegalArgumentException {
        String ucValue = value.toUpperCase();
        try {
            return valueOf(ucValue);
        } catch (Exception e) {
            throw new KapuaIllegalArgumentException("sortOrder", value);
        }
    }
}
