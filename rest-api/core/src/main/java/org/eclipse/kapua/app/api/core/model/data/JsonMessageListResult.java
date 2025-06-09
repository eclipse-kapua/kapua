/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.api.core.model.data;

import org.eclipse.kapua.service.elasticsearch.client.model.ResultList;

/**
 * @since 1.0.0
 */
public class JsonMessageListResult extends org.eclipse.kapua.service.storable.model.StorableListResult<JsonDatastoreMessage> {

    /**
     * Construct a message result list
     *
     * @since 1.0.0
     */
    public JsonMessageListResult() {
        super();
    }

    /**
     * Construct the message result list from the provided list
     *
     * @param resultList
     * @since 1.0.0
     */
    public JsonMessageListResult(ResultList<JsonDatastoreMessage> resultList) {
        super(resultList.getResult(), resultList.getTotalCount());
    }

}
