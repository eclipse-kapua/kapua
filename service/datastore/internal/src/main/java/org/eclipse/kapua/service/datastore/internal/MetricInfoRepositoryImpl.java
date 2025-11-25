/*******************************************************************************
 * Copyright (c) 2020, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.datastore.internal;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.MetricInfoFactory;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreUtils;
import org.eclipse.kapua.service.datastore.internal.schema.MetricInfoSchema;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettings;
import org.eclipse.kapua.service.datastore.model.MetricInfo;
import org.eclipse.kapua.service.datastore.model.MetricInfoListResult;
import org.eclipse.kapua.service.datastore.model.query.MetricInfoQuery;
import org.eclipse.kapua.service.elasticsearch.client.ElasticsearchClientProvider;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicateFactory;

import javax.inject.Inject;

public class MetricInfoRepositoryImpl extends DatastoreElasticSearchRepositoryBase<MetricInfo, MetricInfoListResult, MetricInfoQuery> implements MetricInfoRepository {

    private final DatastoreUtils datastoreUtils;

    @Inject
    protected MetricInfoRepositoryImpl(
            ElasticsearchClientProvider elasticsearchClientProviderInstance,
            MetricInfoFactory metricInfoFactory,
            StorablePredicateFactory storablePredicateFactory,
            DatastoreSettings datastoreSettings,
            DatastoreUtils datastoreUtils,
            DatastoreCacheManager datastoreCacheManager) {
        super(elasticsearchClientProviderInstance,
                MetricInfo.class,
                metricInfoFactory,
                storablePredicateFactory,
                datastoreCacheManager.getIndexCache(),
                datastoreSettings);
        this.datastoreUtils = datastoreUtils;
    }

    @Override
    protected JsonNode getIndexSchema() throws MappingException {
        return MetricInfoSchema.getMetricTypeSchema();
    }

    @Override
    protected String indexResolver(KapuaId scopeId) {
        return datastoreUtils.getMetricIndexName(scopeId);
    }

    @Override
    protected StorableId idExtractor(MetricInfo storable) {
        return storable.getId();
    }
}
