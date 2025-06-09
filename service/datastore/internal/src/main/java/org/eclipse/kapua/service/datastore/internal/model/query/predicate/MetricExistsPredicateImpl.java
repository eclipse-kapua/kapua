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
package org.eclipse.kapua.service.datastore.internal.model.query.predicate;

import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreUtils;
import org.eclipse.kapua.service.datastore.model.query.MessageField;
import org.eclipse.kapua.service.datastore.model.query.predicate.MetricExistsPredicate;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.query.predicate.ExistsPredicateImpl;
import org.eclipse.kapua.service.storable.model.query.predicate.PredicateConstants;
import org.eclipse.kapua.service.storable.model.utils.KeyValueEntry;
import org.eclipse.kapua.service.storable.model.utils.MappingUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * {@link MetricExistsPredicate} implementation.
 *
 * @since 1.2.0
 */
public class MetricExistsPredicateImpl extends ExistsPredicateImpl implements MetricExistsPredicate {

    private final DatastoreUtils datastoreUtils;
    private Class<?> type;

    public <V extends Comparable<V>> MetricExistsPredicateImpl(String fieldName, Class<V> type, DatastoreUtils datastoreUtils) {
        super(fieldName);
        this.datastoreUtils = datastoreUtils;

        setType(type);
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public void setType(Class<?> type) {
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    /**
     * <pre>
     *  {
     *      "query": {
     *          "exists" : { "field" : "metrics.metric.type" }
     *      }
     *   }
     * </pre>
     */
    public ObjectNode toSerializedMap() throws MappingException {
        StringBuilder fieldNameSb = new StringBuilder();

        fieldNameSb.append(MessageField.METRICS.field())
                .append(".")
                .append(datastoreUtils.normalizeMetricName(getName()));

        if (getType() != null) {
            fieldNameSb.append(".")
                    .append(datastoreUtils.getClientMetricFromAcronym(type.getSimpleName().toLowerCase()));
        }

        ObjectNode termNode = MappingUtils.newObjectNode(new KeyValueEntry[] { new KeyValueEntry(PredicateConstants.FIELD_KEY, fieldNameSb.toString()) });

        ObjectNode rootNode = MappingUtils.newObjectNode();
        rootNode.set(PredicateConstants.EXISTS_KEY, termNode);
        return rootNode;
    }

}
