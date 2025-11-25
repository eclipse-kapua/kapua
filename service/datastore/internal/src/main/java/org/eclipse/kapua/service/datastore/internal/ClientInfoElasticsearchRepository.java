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
import org.eclipse.kapua.service.datastore.ClientInfoFactory;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreUtils;
import org.eclipse.kapua.service.datastore.internal.schema.ClientInfoSchema;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettings;
import org.eclipse.kapua.service.datastore.model.ClientInfo;
import org.eclipse.kapua.service.datastore.model.ClientInfoListResult;
import org.eclipse.kapua.service.datastore.model.query.ClientInfoQuery;
import org.eclipse.kapua.service.elasticsearch.client.ElasticsearchClientProvider;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicateFactory;

import javax.inject.Inject;

public class ClientInfoElasticsearchRepository extends DatastoreElasticSearchRepositoryBase<ClientInfo, ClientInfoListResult, ClientInfoQuery> implements ClientInfoRepository {

    private final DatastoreUtils datastoreUtils;

    @Inject
    protected ClientInfoElasticsearchRepository(
            ElasticsearchClientProvider elasticsearchClientProviderInstance,
            ClientInfoFactory clientInfoFactory,
            StorablePredicateFactory storablePredicateFactory,
            DatastoreSettings datastoreSettings,
            DatastoreUtils datastoreUtils,
            DatastoreCacheManager datastoreCacheManager) {
        super(elasticsearchClientProviderInstance,
                ClientInfo.class,
                clientInfoFactory,
                storablePredicateFactory,
                datastoreCacheManager.getIndexCache(),
                datastoreSettings);
        this.datastoreUtils = datastoreUtils;
    }

    @Override
    protected String indexResolver(KapuaId scopeId) {
        return datastoreUtils.getClientIndexName(scopeId);
    }

    @Override
    protected JsonNode getIndexSchema() throws MappingException {
        return ClientInfoSchema.getClientTypeSchema();
    }

    @Override
    protected StorableId idExtractor(ClientInfo storable) {
        return storable.getId();
    }

}
