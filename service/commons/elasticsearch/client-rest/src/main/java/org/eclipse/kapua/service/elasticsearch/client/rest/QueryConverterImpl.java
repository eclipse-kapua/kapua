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
package org.eclipse.kapua.service.elasticsearch.client.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.kapua.service.elasticsearch.client.QueryConverter;
import org.eclipse.kapua.service.elasticsearch.client.SchemaKeys;
import org.eclipse.kapua.service.elasticsearch.client.exception.QueryMappingException;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.query.SortField;
import org.eclipse.kapua.service.storable.model.query.StorableQuery;
import org.eclipse.kapua.service.storable.model.utils.MappingUtils;

import java.util.List;

/**
 * Query converter implementation that converts only fields supported by the given ES endpoint/action (Query, Delete, Count etc.)
 * In order to avoid bad requests
 *
 * @since 2.1.0
 */
public class QueryConverterImpl implements QueryConverter {

    private static ConvertOptions convertOptionsCount = new ConvertOptions(false, true, false, false, false);
    private static ConvertOptions convertOptionsDelete = convertOptionsCount;
    private static ConvertOptions convertOptionsSearch = new ConvertOptions(true, true, true, true, true);

    @Override
    public JsonNode convertQuery(Object query) throws QueryMappingException {
        return convertQuery(query, convertOptionsSearch);
    }

    @Override
    public JsonNode convertCountQuery(Object query) throws QueryMappingException {
        return convertQuery(query, convertOptionsCount);
    }

    @Override
    public JsonNode convertDeleteQuery(Object query) throws QueryMappingException {
        return convertQuery(query, convertOptionsDelete);
    }

    private JsonNode convertQuery(Object query, ConvertOptions convertOptions) throws QueryMappingException {
        if (!(query instanceof StorableQuery)) {
            throw new QueryMappingException("Given query is not a StorableQuery");
        }

        try {
            StorableQuery storableQuery = (StorableQuery) query;
            ObjectNode rootNode = MappingUtils.newObjectNode();

            if (convertOptions.sourceEnabled) {
                ObjectNode includesFields = MappingUtils.newObjectNode();
                includesFields.set(SchemaKeys.KEY_INCLUDES, MappingUtils.newArrayNode(storableQuery.getIncludes(storableQuery.getFetchStyle())));
                includesFields.set(SchemaKeys.KEY_EXCLUDES, MappingUtils.newArrayNode(storableQuery.getExcludes(storableQuery.getFetchStyle())));
                rootNode.set(SchemaKeys.KEY_SOURCE, includesFields);
            }

            // query
            if (convertOptions.queryEnabled && storableQuery.getPredicate() != null) {
                rootNode.set(SchemaKeys.KEY_QUERY, storableQuery.getPredicate().toSerializedMap());
            }

            // sort
            ArrayNode sortNode = MappingUtils.newArrayNode();
            List<SortField> sortFields = storableQuery.getSortFields();
            if (convertOptions.sortEnabled && sortFields != null && !sortFields.isEmpty()) {
                for (SortField field : sortFields) {
                    sortNode.add(MappingUtils.newObjectNode(field.getField(), field.getSortDirection().name()));
                }
                rootNode.set(SchemaKeys.KEY_SORT, sortNode);
            }

            // offset and limit settings
            Integer offset = storableQuery.getOffset();
            if (convertOptions.fromEnabled && offset != null) {
                rootNode.set(SchemaKeys.KEY_FROM, MappingUtils.newNumericNode(offset));
            }
            Integer limit = storableQuery.getLimit();
            if (convertOptions.sizeEnabled && limit != null) {
                rootNode.set(SchemaKeys.KEY_SIZE, MappingUtils.newNumericNode(limit));
            }
            return rootNode;
        } catch (MappingException me) {
            throw new QueryMappingException(me, "Cannot convert Storable Query");
        }
    }

    @Override
    public Object getFetchStyle(Object query) throws QueryMappingException {
        if (!(query instanceof StorableQuery)) {
            throw new QueryMappingException("Given query is not a StorableQuery");
        }

        return ((StorableQuery) query).getFetchStyle();
    }

    private static class ConvertOptions {

        protected boolean sourceEnabled;
        protected boolean queryEnabled;
        protected boolean sortEnabled;
        protected boolean fromEnabled;
        protected boolean sizeEnabled;

        ConvertOptions(boolean source,boolean query,boolean sort, boolean from, boolean size) {
            this.sourceEnabled = source;
            this.queryEnabled = query;
            this.sortEnabled = sort;
            this.fromEnabled = from;
            this.sizeEnabled = size;
        }

    }

}
