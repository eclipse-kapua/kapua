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
package org.eclipse.kapua.service.datastore.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.service.elasticsearch.client.model.ResultList;
import org.eclipse.kapua.service.storable.model.StorableListResult;

/**
 * Metric information query result list definition.<br> This object contains the list of the metric information objects retrieved by the search service.
 *
 * @since 1.0
 */
@XmlRootElement(name = "metricInfos")
@XmlType
public class MetricInfoListResult extends StorableListResult<MetricInfo> {

    private static final long serialVersionUID = 9057086672566426909L;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    public MetricInfoListResult() {
        super();
    }

    /**
     * Constructor.
     *
     * @param resultList
     *         The {@link ResultList} to add.
     * @since 1.0.0
     */
    public MetricInfoListResult(ResultList<MetricInfo> resultList) {
        super(resultList.getResult(), resultList.getTotalCount());
    }
}
