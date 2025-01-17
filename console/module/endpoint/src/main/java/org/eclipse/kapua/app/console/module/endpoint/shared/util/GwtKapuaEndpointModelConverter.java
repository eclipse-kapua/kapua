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
package org.eclipse.kapua.app.console.module.endpoint.shared.util;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.endpoint.shared.model.GwtEndpointQuery;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.query.FieldSortCriteria;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.service.endpoint.EndpointInfoAttributes;
import org.eclipse.kapua.service.endpoint.EndpointInfoFactory;
import org.eclipse.kapua.service.endpoint.EndpointInfoQuery;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.common.base.Strings;

public class GwtKapuaEndpointModelConverter {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();
    private static final EndpointInfoFactory ENDPOINT_FACTORY = LOCATOR.getFactory(EndpointInfoFactory.class);

    private GwtKapuaEndpointModelConverter() {
    }

    /**
     * Converts a {@link GwtEndpointQuery} into a {@link EndpointInfoQuery} object for backend usage
     *
     * @param loadConfig
     *         the load configuration
     * @param gwtEndpointQuery
     *         the {@link GwtEndpointQuery} to convertKapuaId
     * @return the converted {@link EndpointInfoQuery}
     */
    public static EndpointInfoQuery convertEndpointQuery(PagingLoadConfig loadConfig, GwtEndpointQuery gwtEndpointQuery) {

        EndpointInfoQuery query = new EndpointInfoQuery(GwtKapuaCommonsModelConverter.convertKapuaId(gwtEndpointQuery.getScopeId()));

        // Predicates conversion
        AndPredicate andPredicate = query.andPredicate();

        if (!Strings.isNullOrEmpty(gwtEndpointQuery.getSchema())) {
            andPredicate.and(query.attributePredicate(EndpointInfoAttributes.SCHEMA, gwtEndpointQuery.getSchema(), AttributePredicate.Operator.LIKE));
        }

        if (!Strings.isNullOrEmpty(gwtEndpointQuery.getDns())) {
            andPredicate.and(query.attributePredicate(EndpointInfoAttributes.DNS, gwtEndpointQuery.getDns(), AttributePredicate.Operator.LIKE));
        }

        if (gwtEndpointQuery.getPort() != null) {
            andPredicate.and(query.attributePredicate(EndpointInfoAttributes.PORT, gwtEndpointQuery.getPort().intValue(), AttributePredicate.Operator.LIKE));
        }

        if (gwtEndpointQuery.getCheck()) {
            andPredicate.and(query.attributePredicate(EndpointInfoAttributes.SECURE, gwtEndpointQuery.getSecure(), AttributePredicate.Operator.EQUAL));
        }

        query.setPredicate(andPredicate);

        // Sort order conversion
        String sortField = StringUtils.isEmpty(loadConfig.getSortField()) ? EndpointInfoAttributes.DNS : loadConfig.getSortField();

        if (sortField.equals("schema")) {
            sortField = EndpointInfoAttributes.SCHEMA;
        } else if (sortField.equals("createdOnFormatted")) {
            sortField = EndpointInfoAttributes.CREATED_ON;
        }

        SortOrder sortOrder = loadConfig.getSortDir().equals(SortDir.DESC) ? SortOrder.DESCENDING : SortOrder.ASCENDING;
        FieldSortCriteria sortCriteria = query.fieldSortCriteria(sortField, sortOrder);

        // Query conversion
        query.setSortCriteria(sortCriteria);
        query.setOffset(loadConfig.getOffset());
        query.setLimit(loadConfig.getLimit());
        query.setAskTotalCount(gwtEndpointQuery.getAskTotalCount());
        return query;
    }
}
