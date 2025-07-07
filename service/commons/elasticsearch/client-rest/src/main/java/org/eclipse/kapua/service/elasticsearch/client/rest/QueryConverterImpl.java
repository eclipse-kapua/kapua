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
 * Query converter implementation
 *
 * @since 1.0
 */
public class QueryConverterImpl implements QueryConverter {

    private boolean sourceEnabled = true;
    private boolean queryEnabled = true;
    private boolean sortEnabled = true;
    private boolean fromEnabled = true;
    private boolean sizeEnabled = true;

    @Override
    public JsonNode convertQuery(Object query) throws QueryMappingException {
        if (!(query instanceof StorableQuery)) {
            throw new QueryMappingException("Given query is not a StorableQuery");
        }

        try {
            StorableQuery storableQuery = (StorableQuery) query;
            ObjectNode rootNode = MappingUtils.newObjectNode();

            if (sourceEnabled) {
                ObjectNode includesFields = MappingUtils.newObjectNode();
                includesFields.set(SchemaKeys.KEY_INCLUDES, MappingUtils.newArrayNode(storableQuery.getIncludes(storableQuery.getFetchStyle())));
                includesFields.set(SchemaKeys.KEY_EXCLUDES, MappingUtils.newArrayNode(storableQuery.getExcludes(storableQuery.getFetchStyle())));
                rootNode.set(SchemaKeys.KEY_SOURCE, includesFields);
            }

            // query
            if (queryEnabled && storableQuery.getPredicate() != null) {
                rootNode.set(SchemaKeys.KEY_QUERY, storableQuery.getPredicate().toSerializedMap());
            }

            // sort
            ArrayNode sortNode = MappingUtils.newArrayNode();
            List<SortField> sortFields = storableQuery.getSortFields();
            if (sortEnabled && sortFields != null && !sortFields.isEmpty()) {
                for (SortField field : sortFields) {
                    sortNode.add(MappingUtils.newObjectNode(field.getField(), field.getSortDirection().name()));
                }
                rootNode.set(SchemaKeys.KEY_SORT, sortNode);
            }

            // offset and limit settings
            Integer offset = storableQuery.getOffset();
            if (fromEnabled && offset != null) {
                rootNode.set(SchemaKeys.KEY_FROM, MappingUtils.newNumericNode(offset));
            }
            Integer limit = storableQuery.getLimit();
            if (sizeEnabled && limit != null) {
                rootNode.set(SchemaKeys.KEY_SIZE, MappingUtils.newNumericNode(limit));
            }
            return rootNode;
        } catch (MappingException me) {
            throw new QueryMappingException(me, "Cannot convert Storable Query");
        }
    }

    @Override
    public JsonNode convertQuery(String actionType, Object query) throws QueryMappingException {
        if (actionType.equals("COUNT") || actionType.equals("DELETE")) {
            queryEnabled = true;
            sourceEnabled = false;
            sortEnabled = false;
            fromEnabled = false;
            sizeEnabled = false;
        }
        if (actionType.equals("SEARCH")) {
            resetEnablers();
        }
        return convertQuery(query);
    }

    @Override
    public Object getFetchStyle(Object query) throws QueryMappingException {
        if (!(query instanceof StorableQuery)) {
            throw new QueryMappingException("Given query is not a StorableQuery");
        }

        return ((StorableQuery) query).getFetchStyle();
    }

    private void resetEnablers() {
        queryEnabled = true;
        fromEnabled = true;
        sourceEnabled = true;
        sortEnabled = true;
        sizeEnabled = true;
    }

}
