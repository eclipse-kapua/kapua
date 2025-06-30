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

import org.eclipse.kapua.service.elasticsearch.client.model.InsertRequest;

import javax.validation.constraints.NotNull;

/**
 * {@link RestElasticsearchClient} resource paths.
 *
 * @since 1.3.0
 */
public class ElasticsearchResourcePaths {

    /**
     * Constructor.
     *
     * @since 1.3.0
     */
    private ElasticsearchResourcePaths() {
    }

    /**
     * @since 1.0.0
     */
    public static String getBulkPath() {
        return "/_bulk";
    }

    /**
     * @since 1.0.0
     */
    public static String deleteByQuery(@NotNull String index) {
        return String.format("/%s/_delete_by_query", index);
    }

    /**
     * @since 1.0.0
     */
    public static String findIndex(String index) {
        return String.format("/_cat/indices?h=index&index=%s", index);
    }

    /**
     * @since 1.0.0
     */
    public static String id(@NotNull String index, @NotNull String id) {
        return String.format("/%s/_doc/%s", index, id);
    }

    /**
     * @since 1.0.0
     */
    public static String index(String index) {
        return String.format("/%s", index);
    }

    /**
     * @since 1.0.0
     * since types have been removed it simply inserts the default "_doc" value in the request path
     */
    public static String insertType(@NotNull InsertRequest request) {
        if (request.getId() != null) {
            return String.format("%s/%s", defaultPathDocType(request.getIndex()), request.getId());
        } else {
            return defaultPathDocType(request.getIndex());
        }
    }

    /**
     * @since 1.0.0
     */
    public static String mapping(@NotNull String index) {
        return String.format("/%s/_mapping", index);
    }

    /**
     * @since 1.0.0
     */
    public static String search(@NotNull String index) {
        return String.format("/%s/_search", index);
    }

    /**
     * @since 2.1.0
     */
    public static String count(@NotNull String index) {
        return String.format("/%s/_count", index);
    }

    /**
     * @since 1.0.0
     */
    public static String refreshAllIndexes() {
        return "/_all/_refresh";
    }

    /**
     * @since 1.0.0
     */
    public static String defaultPathDocType(@NotNull String index) {
        return String.format("/%s/_doc", index);
    }

    /**
     * @since 1.0.0
     */
    public static String upsert(@NotNull String index, @NotNull String id) {
        return String.format("/%s/_update/%s", index, id);
    }

}
