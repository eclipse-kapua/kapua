/*******************************************************************************
 * Copyright (c) 2016, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.api.core.model;

import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.model.type.ObjectTypeConverter;

import com.google.common.base.Strings;

import java.util.Objects;

public class MetricType<V extends Comparable<V>> {

    private Class<V> type;

    @SuppressWarnings("unchecked")
    public MetricType(String stringMetricType) throws KapuaIllegalArgumentException {
        if (!Strings.isNullOrEmpty(stringMetricType)) {
            try {
                type = (Class<V>) ObjectTypeConverter.fromString(stringMetricType);
            } catch (ClassNotFoundException e) {
                throw new KapuaIllegalArgumentException("type", stringMetricType);
            }
        }
    }

    public Class<V> getType() {
        return type;
    }

    /**
     * @since 2.1.0
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MetricType<?> that = (MetricType<?>) o;
        return Objects.equals(getType(), that.getType());
    }

    /**
     * @since 2.1.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(getType());
    }
}
