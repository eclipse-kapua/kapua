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
package org.eclipse.kapua.service.datastore.model.query;

import java.util.Collections;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.storable.model.query.SortField;
import org.eclipse.kapua.service.storable.model.query.StorableFetchStyle;
import org.eclipse.kapua.service.storable.model.query.StorableQuery;

/**
 * Metric information schema query definition
 *
 * @since 1.0
 */
@XmlRootElement(name = "query")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class MetricInfoQuery extends StorableQuery {

    public MetricInfoQuery() {
        setSortFields(Collections.singletonList(SortField.ascending(MetricInfoSchema.METRIC_MTR_NAME_FULL)));
    }

    /**
     * Constructor.
     *
     * @param scopeId
     *         The scope {@link KapuaId}.
     * @since 1.0.0
     */
    public MetricInfoQuery(KapuaId scopeId) {
        super(scopeId);
        setSortFields(Collections.singletonList(SortField.ascending(MetricInfoSchema.METRIC_MTR_NAME_FULL)));

    }

    @Override
    public String[] getIncludes(StorableFetchStyle fetchStyle) {
        return new String[] { "*" };
    }

    @Override
    public String[] getExcludes(StorableFetchStyle fetchStyle) {
        return new String[] { "" };
    }

    @Override
    public String[] getFields() {
        return new String[] { MetricInfoField.SCOPE_ID.field(),
                MetricInfoField.CLIENT_ID.field(),
                MetricInfoField.CHANNEL.field(),
                MetricInfoField.NAME_FULL.field(),
                MetricInfoField.TYPE_FULL.field(),
                MetricInfoField.TIMESTAMP_FULL.field(),
                MetricInfoField.MESSAGE_ID_FULL.field() };
    }

}
