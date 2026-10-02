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
package org.eclipse.kapua.service.datastore.internal.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreUtils;
import org.eclipse.kapua.service.datastore.model.MetricInfo;
import org.eclipse.kapua.service.storeengine.client.SchemaKeys;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.utils.KeyValueEntry;
import org.eclipse.kapua.service.storable.model.utils.MappingUtils;

/**
 * {@link MetricInfo} schema definition.
 *
 * @since 1.0.0
 */
public class MetricInfoSchema {

    /**
     * @since 1.0.0
     */
    private MetricInfoSchema() {
    }

    /**
     * Metric information schema name
     *
     * @since 1.0.0
     */
    public static final String METRIC_TYPE_NAME = "metric";

    /**
     * Metric information - channel
     *
     * @since 1.0.0
     */
    public static final String METRIC_CHANNEL = "channel";

    /**
     * Metric information - client identifier
     *
     * @since 1.0.0
     */
    public static final String METRIC_CLIENT_ID = "client_id";

    /**
     * Metric information - scope id
     *
     * @since 1.0.0
     */
    public static final String METRIC_SCOPE_ID = "scope_id";

    /**
     * Metric information - metric map prefix
     *
     * @since 1.0.0
     */
    public static final String METRIC_MTR = "metric";

    /**
     * Metric information - name
     *
     * @since 1.0.0
     */
    public static final String METRIC_MTR_NAME = "name";

    /**
     * Metric information - full name (so with the metric type suffix)
     *
     * @since 1.0.0
     */
    public static final String METRIC_MTR_NAME_FULL = "metric.name";

    /**
     * Metric information - type
     *
     * @since 1.0.0
     */
    public static final String METRIC_MTR_TYPE = "type";

    /**
     * Metric information - full type (so with the metric type suffix)
     *
     * @since 1.0.0
     */
    public static final String METRIC_MTR_TYPE_FULL = "metric.type";

    /**
     * Metric information - value
     *
     * @since 1.0.0
     */
    public static final String METRIC_MTR_VALUE = "value";

    /**
     * Metric information - full value (so with the metric type suffix)
     *
     * @since 1.0.0
     */
    public static final String METRIC_MTR_VALUE_FULL = "metric.value";

    /**
     * Metric information - message timestamp (of the first message published in this channel)
     *
     * @since 1.0.0
     */
    public static final String METRIC_MTR_TIMESTAMP = "timestamp";

    /**
     * Metric information - message timestamp (of the first message published in this channel, with the metric type suffix)
     *
     * @since 1.0.0
     */
    public static final String METRIC_MTR_TIMESTAMP_FULL = "metric.timestamp";

    /**
     * Metric information - message identifier (of the first message published in this channel)
     *
     * @since 1.0.0
     */
    public static final String METRIC_MTR_MSG_ID = "message_id";

    /**
     * Metric information - full message identifier (of the first message published in this channel, with the metric type suffix)
     *
     * @since 1.0.0
     */
    public static final String METRIC_MTR_MSG_ID_FULL = "metric.message_id";

    /**
     * Create and return the Json representation of the metric info schema
     *
     * @return
     * @throws MappingException
     * @since 1.0.0
     */
    public static JsonNode getMetricTypeSchema() throws MappingException {

        ObjectNode metricNode = MappingUtils.newObjectNode();

        ObjectNode sourceMetric = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_ENABLED, true)});
        metricNode.set(SchemaKeys.KEY_SOURCE, sourceMetric);

        ObjectNode propertiesNode = MappingUtils.newObjectNode();
        {
            ObjectNode metricAccount = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE)});
            propertiesNode.set(METRIC_SCOPE_ID, metricAccount);

            ObjectNode metricClientId = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE)});
            propertiesNode.set(METRIC_CLIENT_ID, metricClientId);

            ObjectNode metricChannel = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE)});
            propertiesNode.set(METRIC_CHANNEL, metricChannel);

            ObjectNode metricMtrNode = MappingUtils.newObjectNode(
                    new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_OBJECT), new KeyValueEntry(SchemaKeys.KEY_ENABLED, true),
                            new KeyValueEntry(SchemaKeys.KEY_DYNAMIC, false)});
            ObjectNode metricMtrPropertiesNode = MappingUtils.newObjectNode();
            {
                ObjectNode metricMtrNameNode = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE)});
                metricMtrPropertiesNode.set(METRIC_MTR_NAME, metricMtrNameNode);

                ObjectNode metricMtrTypeNode = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE)});
                metricMtrPropertiesNode.set(METRIC_MTR_TYPE, metricMtrTypeNode);

                ObjectNode metricMtrValueNode = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE)});
                metricMtrPropertiesNode.set(METRIC_MTR_VALUE, metricMtrValueNode);

                ObjectNode metricMtrTimestampNode = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DATE), new KeyValueEntry(SchemaKeys.KEY_FORMAT, DatastoreUtils.DATASTORE_DATE_FORMAT)});
                metricMtrPropertiesNode.set(METRIC_MTR_TIMESTAMP, metricMtrTimestampNode);

                ObjectNode metricMtrMsgIdNode = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE)});
                metricMtrPropertiesNode.set(METRIC_MTR_MSG_ID, metricMtrMsgIdNode);
            }
            metricMtrNode.set(SchemaKeys.FIELD_NAME_PROPERTIES, metricMtrPropertiesNode);

            propertiesNode.set(METRIC_MTR, metricMtrNode);
        }
        metricNode.set(SchemaKeys.FIELD_NAME_PROPERTIES, propertiesNode);

        return metricNode;
    }

}
