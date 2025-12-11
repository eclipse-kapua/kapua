/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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

public class ElasticsearchKeywords {

    private ElasticsearchKeywords() {
    }

    static final String ACTION_GET = "GET";
    static final String ACTION_DELETE = "DELETE";
    static final String ACTION_POST = "POST";
    static final String ACTION_PUT = "PUT";
    static final String ACTION_HEAD = "HEAD";

    static final String INDEX_ALL = "ALL";

    static final String KEY_DOC = "doc";
    static final String KEY_DOC_AS_UPSERT = "doc_as_upsert";
    static final String KEY_DOC_ID = "_id";
    static final String KEY_DOC_INDEX = "_index";
    static final String KEY_DOC_TYPE = "_type";

    static final String KEY_ITEMS = "items";
    static final String KEY_COUNT = "count";
    static final String KEY_RESULT = "result";
    static final String KEY_STATUS = "status";
    static final String KEY_UPDATE = "update";
    static final String KEY_HITS = "hits";
    static final String KEY_TOTAL = "total";
    static final String KEY_VALUE = "value";
    static final String KEY_RELATION = "relation";
    static final String KEY_AGGREGATIONS = "aggregations";
    static final String KEY_BUCKETS = "buckets";
    static final String KEY_BUCKETS_KEY = "key";
}
