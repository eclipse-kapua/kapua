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
 *******************************************************************************/
package org.eclipse.kapua.service.datastore.internal.model.query;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.internal.mediator.MetricInfoField;
import org.eclipse.kapua.service.datastore.internal.schema.MetricInfoSchema;
import org.eclipse.kapua.service.datastore.model.query.MetricInfoQuery;
import org.eclipse.kapua.service.storable.model.query.AbstractStorableQuery;
import org.eclipse.kapua.service.storable.model.query.SortField;
import org.eclipse.kapua.service.storable.model.query.StorableFetchStyle;

import java.util.Collections;

/**
 * {@link MetricInfoQuery} implementation.
 *
 * @since 1.0.0
 */
public class MetricInfoQueryImpl extends AbstractStorableQuery implements MetricInfoQuery {

    /**
     * Constructor.
     *
     * @param scopeId The scope {@link KapuaId}.
     * @since 1.0.0
     */
    public MetricInfoQueryImpl(KapuaId scopeId) {
        super(scopeId);

        setSortFields(Collections.singletonList(SortField.ascending(MetricInfoSchema.METRIC_MTR_NAME_FULL)));
    }

    public MetricInfoQueryImpl(MetricInfoQuery other) {
        super((AbstractStorableQuery) other);
    }

    @Override
    public String[] getIncludes(StorableFetchStyle fetchStyle) {
        return new String[]{"*"};
    }

    @Override
    public String[] getExcludes(StorableFetchStyle fetchStyle) {
        return new String[]{""};
    }

    @Override
    public String[] getFields() {
        return new String[]{MetricInfoField.SCOPE_ID.field(),
                MetricInfoField.CLIENT_ID.field(),
                MetricInfoField.CHANNEL.field(),
                MetricInfoField.NAME_FULL.field(),
                MetricInfoField.TYPE_FULL.field(),
                MetricInfoField.TIMESTAMP_FULL.field(),
                MetricInfoField.MESSAGE_ID_FULL.field()};
    }

}
