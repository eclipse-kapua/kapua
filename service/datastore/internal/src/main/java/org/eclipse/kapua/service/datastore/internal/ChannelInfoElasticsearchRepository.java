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
import org.eclipse.kapua.service.datastore.ChannelInfoFactory;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreUtils;
import org.eclipse.kapua.service.datastore.internal.schema.ChannelInfoSchema;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettings;
import org.eclipse.kapua.service.datastore.model.ChannelInfo;
import org.eclipse.kapua.service.datastore.model.ChannelInfoListResult;
import org.eclipse.kapua.service.datastore.model.query.ChannelInfoQuery;
import org.eclipse.kapua.service.elasticsearch.client.ElasticsearchClientProvider;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicateFactory;

import javax.inject.Inject;

public class ChannelInfoElasticsearchRepository extends DatastoreElasticSearchRepositoryBase<ChannelInfo, ChannelInfoListResult, ChannelInfoQuery> implements ChannelInfoRepository {

    private final DatastoreUtils datastoreUtils;

    @Inject
    protected ChannelInfoElasticsearchRepository(
            ElasticsearchClientProvider elasticsearchClientProviderInstance,
            ChannelInfoFactory channelInfoFactory,
            StorablePredicateFactory storablePredicateFactory,
            DatastoreSettings datastoreSettings,
            DatastoreUtils datastoreUtils,
            DatastoreCacheManager datastoreCacheManager) {
        super(elasticsearchClientProviderInstance,
                ChannelInfo.class,
                channelInfoFactory,
                storablePredicateFactory,
                datastoreCacheManager.getIndexCache(),
                datastoreSettings);
        this.datastoreUtils = datastoreUtils;
    }

    @Override
    protected StorableId idExtractor(ChannelInfo storable) {
        return storable.getId();
    }

    @Override
    protected String indexResolver(KapuaId scopeId) {
        return datastoreUtils.getChannelIndexName(scopeId);
    }

    @Override
    protected JsonNode getIndexSchema() throws MappingException {
        return ChannelInfoSchema.getChannelTypeSchema();
    }
}
