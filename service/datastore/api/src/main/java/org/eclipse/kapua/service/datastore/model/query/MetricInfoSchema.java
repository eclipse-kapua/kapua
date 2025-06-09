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
package org.eclipse.kapua.service.datastore.model.query;

import org.eclipse.kapua.service.datastore.model.MetricInfo;

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

}
