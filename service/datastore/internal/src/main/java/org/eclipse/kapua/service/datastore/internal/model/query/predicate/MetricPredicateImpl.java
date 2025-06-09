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
import org.eclipse.kapua.service.datastore.model.query.predicate.MetricPredicate;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.query.predicate.PredicateConstants;
import org.eclipse.kapua.service.storable.model.query.predicate.RangePredicateImpl;
import org.eclipse.kapua.service.storable.model.utils.MappingUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * {@link MetricPredicate} implementation
 *
 * @since 1.0.0
 */
public class MetricPredicateImpl extends RangePredicateImpl implements MetricPredicate {

    private final DatastoreUtils datastoreUtils;
    private String name;
    private Class<?> type;

    public <V extends Comparable<V>> MetricPredicateImpl(String metricName, Class<V> type, V minValue, V maxValue, DatastoreUtils datastoreUtils) {
        super(MessageField.METRICS, minValue, maxValue);
        this.datastoreUtils = datastoreUtils;

        setName(metricName);
        setType(type);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public void setType(Class<?> type) {
        this.type = type;
    }

    /**
     * <pre>
     *  {
     *      "query": {
     *          "range" : {
     *              "metrics.temperature.int" : {
     *                  "gte" : 10,
     *                  "lte" : 20,
     *              }
     *          }
     *      }
     *  }
     * </pre>
     */
    @Override
    public ObjectNode toSerializedMap() throws MappingException {
        ObjectNode valuesNode = MappingUtils.newObjectNode();
        if (getMaxValue() != null) {
            MappingUtils.appendField(valuesNode, PredicateConstants.LTE_KEY, getMaxValue());
        }
        if (getMinValue() != null) {
            MappingUtils.appendField(valuesNode, PredicateConstants.GTE_KEY, getMinValue());
        }

        ObjectNode termNode = MappingUtils.newObjectNode();
        termNode.set(
                getField().field()
                        .concat(".")
                        .concat(datastoreUtils.normalizeMetricName(getName()))
                        .concat(".")
                        .concat(datastoreUtils.getClientMetricFromAcronym(getType().getSimpleName().toLowerCase())),
                valuesNode);

        ObjectNode rootNode = MappingUtils.newObjectNode();
        rootNode.set(PredicateConstants.RANGE_KEY, termNode);
        return rootNode;
    }

}
