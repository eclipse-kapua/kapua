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
package org.eclipse.kapua.app.console.module.tag.shared.util;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.tag.shared.model.GwtTagQuery;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.FieldSortCriteria;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate.Operator;
import org.eclipse.kapua.service.tag.TagAttributes;
import org.eclipse.kapua.service.tag.TagFactory;
import org.eclipse.kapua.service.tag.TagQuery;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;

public class GwtKapuaTagModelConverter {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();
    private static final TagFactory TAG_FACTORY = LOCATOR.getFactory(TagFactory.class);

    private GwtKapuaTagModelConverter() {
    }

    /**
     * Converts a {@link GwtTagQuery} into a {@link TagQuery} object for backend usage
     *
     * @param loadConfig
     *         the load configuration
     * @param gwtTagQuery
     *         the {@link GwtTagQuery} to convertKapuaId
     * @return the converted {@link TagQuery}
     */
    public static TagQuery convertTagQuery(PagingLoadConfig loadConfig, GwtTagQuery gwtTagQuery) {

        TagQuery query = new TagQuery(GwtKapuaCommonsModelConverter.convertKapuaId(gwtTagQuery.getScopeId()));

        // Predicates conversion
        AndPredicate andPredicate = query.andPredicate();
        if (gwtTagQuery.getName() != null && !gwtTagQuery.getName().isEmpty()) {
            andPredicate.and(query.attributePredicate(TagAttributes.NAME, gwtTagQuery.getName(), Operator.LIKE));
        }
        if (gwtTagQuery.getDescription() != null && !gwtTagQuery.getDescription().isEmpty()) {
            andPredicate.and(query.attributePredicate(TagAttributes.DESCRIPTION, gwtTagQuery.getDescription(), Operator.LIKE));
        }
        if (!gwtTagQuery.getIds().isEmpty()) {
            int i = 0;
            KapuaId[] tagIds = new KapuaId[gwtTagQuery.getIds().size()];
            for (String gwtTagId : gwtTagQuery.getIds()) {
                tagIds[i++] = GwtKapuaCommonsModelConverter.convertKapuaId(gwtTagId);
            }

            andPredicate.and(query.attributePredicate(TagAttributes.ENTITY_ID, tagIds));
        }
        query.setPredicate(andPredicate);

        // Sort order conversion
        String sortField = StringUtils.isEmpty(loadConfig.getSortField()) ? TagAttributes.NAME : loadConfig.getSortField();
        if (sortField.equals("tagName")) {
            sortField = TagAttributes.NAME;
        } else if (sortField.equals("createdOnFormatted")) {
            sortField = TagAttributes.CREATED_ON;
        }
        SortOrder sortOrder = loadConfig.getSortDir().equals(SortDir.DESC) ? SortOrder.DESCENDING : SortOrder.ASCENDING;
        FieldSortCriteria sortCriteria = query.fieldSortCriteria(sortField, sortOrder);

        // Query conversion
        query.setSortCriteria(sortCriteria);
        query.setOffset(loadConfig.getOffset());
        query.setLimit(loadConfig.getLimit());
        query.setAskTotalCount(gwtTagQuery.getAskTotalCount());
        return query;
    }
}
