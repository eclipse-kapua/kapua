/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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

import javax.inject.Inject;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreUtils;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettings;
import org.eclipse.kapua.service.datastore.model.ClientInfo;
import org.eclipse.kapua.service.datastore.model.ClientInfoListResult;
import org.eclipse.kapua.service.datastore.model.query.ClientInfoQuery;
import org.eclipse.kapua.service.datastore.model.query.ClientInfoSchema;
import org.eclipse.kapua.service.elasticsearch.client.ElasticsearchClientProvider;
import org.eclipse.kapua.service.elasticsearch.client.SchemaKeys;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicateFactory;
import org.eclipse.kapua.service.storable.model.utils.KeyValueEntry;
import org.eclipse.kapua.service.storable.model.utils.MappingUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ClientInfoElasticsearchRepository extends DatastoreElasticSearchRepositoryBase<ClientInfo, ClientInfoListResult, ClientInfoQuery> implements ClientInfoRepository {

    private final DatastoreUtils datastoreUtils;

    @Inject
    protected ClientInfoElasticsearchRepository(
            ElasticsearchClientProvider elasticsearchClientProviderInstance,
            StorablePredicateFactory storablePredicateFactory,
            DatastoreSettings datastoreSettings,
            DatastoreUtils datastoreUtils,
            DatastoreCacheManager datastoreCacheManager) {
        super(elasticsearchClientProviderInstance,
                ClientInfo.class,
                storablePredicateFactory,
                datastoreCacheManager.getClientsCache(),
                datastoreSettings,
                scopeId -> new ClientInfoQuery(scopeId),
                () -> new ClientInfoListResult());
        this.datastoreUtils = datastoreUtils;
    }

    @Override
    protected String indexResolver(KapuaId scopeId) {
        return datastoreUtils.getClientIndexName(scopeId);
    }

    @Override
    protected JsonNode getIndexSchema() throws MappingException {
        return getClientTypeSchema();
    }

    /**
     * Create and return the Json representation of the client info schema
     *
     * @return
     * @throws MappingException
     * @since 1.0.0
     */
    public static JsonNode getClientTypeSchema() throws MappingException {

        ObjectNode clientNode = MappingUtils.newObjectNode();
        {
            ObjectNode sourceClient = MappingUtils.newObjectNode(new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_ENABLED, true) });
            clientNode.set(SchemaKeys.KEY_SOURCE, sourceClient);

            ObjectNode propertiesNode = MappingUtils.newObjectNode();
            {
                ObjectNode clientId = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE) });
                propertiesNode.set(ClientInfoSchema.CLIENT_ID, clientId);

                ObjectNode clientTimestamp = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DATE), new KeyValueEntry(SchemaKeys.KEY_FORMAT, DatastoreUtils.DATASTORE_DATE_FORMAT) });
                propertiesNode.set(ClientInfoSchema.CLIENT_TIMESTAMP, clientTimestamp);

                ObjectNode clientScopeId = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE) });
                propertiesNode.set(ClientInfoSchema.CLIENT_SCOPE_ID, clientScopeId);

                ObjectNode clientMessageId = MappingUtils.newObjectNode(
                        new KeyValueEntry[] { new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE) });
                propertiesNode.set(ClientInfoSchema.CLIENT_MESSAGE_ID, clientMessageId);
            }
            clientNode.set(SchemaKeys.FIELD_NAME_PROPERTIES, propertiesNode);
        }

        return clientNode;
    }

    @Override
    protected StorableId idExtractor(ClientInfo storable) {
        return storable.getId();
    }

    @Override
    public void refreshAllIndexes() {
        super.refreshIndex(datastoreUtils.getClientIndexName(KapuaId.ANY));
    }

    @Override
    public void deleteAllIndexes() {
        super.deleteIndexes(datastoreUtils.getClientIndexName(KapuaId.ANY));
    }
}
