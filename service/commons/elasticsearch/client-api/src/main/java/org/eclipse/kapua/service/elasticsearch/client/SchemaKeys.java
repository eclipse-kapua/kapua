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

/**
 * Schema keys and values type definition
 *
 * @since 1.0.0
 */
public class SchemaKeys {

    private SchemaKeys() {
    }

    /**
     * Query key
     *
     * @since 1.0.0
     */
    public static final String KEY_QUERY = "query";
    /**
     * Sort key
     *
     * @since 1.0.0
     */
    public static final String KEY_SORT = "sort";
    /**
     * Include fields key
     *
     * @since 1.0.0
     */
    public static final String KEY_INCLUDES = "includes";
    /**
     * Exclude fields key
     *
     * @since 1.0.0
     */
    public static final String KEY_EXCLUDES = "excludes";
    /**
     * From key (used by queries for paginating the result set)
     *
     * @since 1.0.0
     */
    public static final String KEY_FROM = "from";
    /**
     * Size key (used by queries to limit the result set size)
     *
     * @since 1.0.0
     */
    public static final String KEY_SIZE = "size";

    /**
     * Query ascending sort key
     *
     * @since 1.0.0
     */
    public static final String SORT_ASCENDING_VALUE = "asc";
    /**
     * Query descending sort key
     *
     * @since 1.0.0
     */
    public static final String SORT_DESCENDING_VALUE = "desc";
    /**
     * Format key
     *
     * @since 1.0.0
     */
    public static final String KEY_FORMAT = "format";

    public static final String KEY_AGGREGATION_SIZE = "size";

    public static final String KEY_AGGREGATION_FIELD = "field";

    public static final String KEY_AGGREGATION_TERMS = "terms";

    public static final String KEY_AGGREGATION_NODE = "aggs";
    /**
     * Index key
     *
     * @since 1.0.0
     */
    public static final String KEY_INDEX = "index";
    /**
     * Settings key
     *
     * @since 1.5.0
     */
    public static final String KEY_SETTINGS = "settings";
    /**
     * Source key
     *
     * @since 1.0.0
     */
    public static final String KEY_SOURCE = "_source";
    /**
     * Type key
     *
     * @since 1.0.0
     */
    public static final String KEY_TYPE = "type";
    /**
     * Enabled key
     *
     * @since 1.0.0
     */
    public static final String KEY_ENABLED = "enabled";
    /**
     * Dynamic key
     *
     * @since 1.0.0
     */
    public static final String KEY_DYNAMIC = "dynamic";
    /**
     * Object binary type
     *
     * @since 1.0.0
     */
    public static final String TYPE_BINARY = "binary";
    /**
     * Object date type
     *
     * @since 1.0.0
     */
    public static final String TYPE_DATE = "date";
    /**
     * Object double type
     *
     * @since 1.0.0
     */
    public static final String TYPE_DOUBLE = "double";
    /**
     * Object geo point type
     *
     * @since 1.0.0
     */
    public static final String TYPE_GEO_POINT = "geo_point";
    /**
     * Object integer type
     *
     * @since 1.0.0
     */
    public static final String TYPE_INTEGER = "integer";
    /**
     * Object ip address type
     *
     * @since 1.0.0
     */
    public static final String TYPE_IP = "ip";
    /**
     * Object object type
     *
     * @since 1.0.0
     */
    public static final String TYPE_OBJECT = "object";
    /**
     * Object string type
     *
     * @since 1.0.0
     */
    public static final String TYPE_STRING = "string";
    /**
     * Object keyword type (Structured string that can be indexed with new ES version)<br>
     * <b>Please leave the "index" property for the keyword fields to false (default value) otherwise the value will be analyzed and indexed for the search operation)<br>
     * (see https://www.elastic.co/guide/en/elasticsearch/reference/current/keyword.html)</b>
     *
     * @since 1.0.0
     */
    public static final String TYPE_KEYWORD = "keyword";

    /**
     * "false" field value
     *
     * @since 1.0.0
     */
    public static final String VALUE_FALSE = "false";
    /**
     * "true" field value
     *
     * @since 1.0.0
     */
    public static final String VALUE_TRUE = "true";

    /**
     * Refresh interval (for schema definition)
     *
     * @since 1.0.0
     */
    public static final String KEY_REFRESH_INTERVAL = "refresh_interval";
    /**
     * Shard number (for schema definition)
     *
     * @since 1.0.0
     */
    public static final String KEY_SHARD_NUMBER = "number_of_shards";
    /**
     * Replica number (for schema definition)
     *
     * @since 1.0.0
     */
    public static final String KEY_REPLICA_NUMBER = "number_of_replicas";

    /**
     * Message field.
     *
     * @since 1.0.0
     */
    public static final String FIELD_NAME_MESSAGE = "message";
    /**
     * Metrics field.
     *
     * @since 1.0.0
     */
    public static final String FIELD_NAME_METRICS = "metrics";
    /**
     * Position field.
     *
     * @since 1.0.0
     */
    public static final String FIELD_NAME_POSITION = "position";
    /**
     * Properties field.
     *
     * @since 1.0.0
     */
    public static final String FIELD_NAME_PROPERTIES = "properties";

}
