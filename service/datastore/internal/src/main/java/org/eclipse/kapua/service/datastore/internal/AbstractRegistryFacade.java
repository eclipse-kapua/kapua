/*******************************************************************************
 * Copyright (c) 2020, 2026 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.internal.client.DatastoreClientFactory;
import org.eclipse.kapua.service.datastore.internal.mediator.ConfigurationException;
import org.eclipse.kapua.service.datastore.internal.mediator.MessageStoreConfiguration;
import org.eclipse.kapua.service.elasticsearch.client.ElasticsearchClient;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientUnavailableException;
import org.eclipse.kapua.service.storable.model.Storable;
import org.eclipse.kapua.service.storable.model.StorableListResult;
import org.eclipse.kapua.service.storable.model.query.StorableQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRegistryFacade {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRegistryFacade.class);

    private final ConfigurationProvider configProvider;


    public AbstractRegistryFacade(ConfigurationProvider configProvider) {
        this.configProvider = configProvider;
    }

    public ConfigurationProvider getConfigProvider() {
        return configProvider;
    }

    protected MessageStoreConfiguration getConfigurationFromCache(KapuaId scopeId) throws ConfigurationException {
        return configProvider.getConfigurationFromCache(scopeId);
    }

    protected boolean isDatastoreServiceEnabled(KapuaId scopeId) throws ConfigurationException {
        MessageStoreConfiguration messageStoreConfiguration = getConfigurationFromCache(scopeId);
        long ttl = messageStoreConfiguration.getDataTimeToLiveMilliseconds();

        return messageStoreConfiguration.getDataStorageEnabled() && ttl != MessageStoreConfiguration.DISABLED;
    }

    protected ElasticsearchClient<?> getElasticsearchClient() throws ClientUnavailableException {
        return DatastoreClientFactory.getElasticsearchClient();
    }

    protected <T extends Storable> void setLimitExceed(StorableQuery query, boolean hitsExceedsTotalCount, StorableListResult<T> list) {
        int offset = query.getOffset() != null ? query.getOffset() : 0;
        if (query.getLimit() != null) {
            if (hitsExceedsTotalCount || //pre-condition: there are more than 10k documents in ES && query limit is <= 10k
                    list.getTotalCount() > offset + query.getLimit()) {
                list.setLimitExceeded(true);
            }
        }
    }
}
