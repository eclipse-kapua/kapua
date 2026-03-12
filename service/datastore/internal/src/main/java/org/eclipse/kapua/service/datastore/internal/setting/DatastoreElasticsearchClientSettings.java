/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.datastore.internal.setting;

import org.eclipse.kapua.commons.setting.AbstractKapuaSetting;
import org.eclipse.kapua.service.elasticsearch.client.ElasticsearchClientWrapper;

/**
 * Datastore {@link ElasticsearchClientWrapper} {@link org.eclipse.kapua.commons.setting.AbstractKapuaSetting}
 *
 * @since 1.3.0
 */
//TODO: FIXME: singletons should not be handled manually, we have DI for that
public class DatastoreElasticsearchClientSettings extends AbstractKapuaSetting<DatastoreElasticsearchClientSettingsKey> {

    /**
     * Resource file from which source properties.
     *
     * @since 1.3.0
     */
    private static final String DATASTORE_ELASTICSEARCH_CONFIG_RESOURCE = "kapua-datastore-elasticsearch-client-settings.properties";

    /**
     * Singleton instance of this {@link Class}.
     *
     * @since 1.3.0
     */
    private static final DatastoreElasticsearchClientSettings INSTANCE = new DatastoreElasticsearchClientSettings();

    /**
     * Initialize the {@link AbstractKapuaSetting} with the {@link DatastoreElasticsearchClientSettings#DATASTORE_ELASTICSEARCH_CONFIG_RESOURCE} value.
     *
     * @since 1.3.0
     */
    private DatastoreElasticsearchClientSettings() {
        super(DATASTORE_ELASTICSEARCH_CONFIG_RESOURCE);
    }

    /**
     * Gets a singleton instance of {@link DatastoreElasticsearchClientSettings}.
     *
     * @return A singleton instance of {@link DatastoreElasticsearchClientSettings}.
     * @since 1.3.0
     */
    public static DatastoreElasticsearchClientSettings getInstance() {
        return INSTANCE;
    }
}
