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
package org.eclipse.kapua.service.elasticsearch.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.kapua.service.elasticsearch.client.exception.QueryMappingException;

/**
 * {@link QueryConverter} definition.
 * <p>
 * This object is responsible for translating datastore query model to client query.
 *
 * @since 1.0.0
 */
public interface QueryConverter {

    /**
     * Query fetch style key
     *
     * @since 1.0.0
     */
    String QUERY_FETCH_STYLE_KEY = "query_fetch_style";

    /**
     * Converts the Elasticsearch query to the client query
     *
     * @param query The query to convert.
     * @return The converted query.
     * @throws QueryMappingException if query mappings are not correst.
     * @since 1.0.0
     */
    JsonNode convertQuery(Object query) throws QueryMappingException;

    /**
     * Converts the Elasticsearch query to the client query for the count operation
     *
     * @param query The query to convert.
     * @return The converted query.
     * @throws QueryMappingException if query mappings are not correst.
     * @since 2.1.0
     */
    JsonNode convertCountQuery(Object query) throws QueryMappingException;

    /**
     * Converts the Elasticsearch query to the client query for the delete operation
     *
     * @param query The query to convert.
     * @return The converted query.
     * @throws QueryMappingException if query mappings are not correst.
     * @since 2.1.0
     */
    JsonNode convertDeleteQuery(Object query) throws QueryMappingException;


    /**
     * Gets the query fetch style
     *
     * @param query The query fetch style
     * @return The query fetch style
     * @throws QueryMappingException if query mappings are not correct.
     * @since 1.0.0
     */
    Object getFetchStyle(Object query) throws QueryMappingException;

}
