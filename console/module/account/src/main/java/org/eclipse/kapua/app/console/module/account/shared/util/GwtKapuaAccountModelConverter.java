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
package org.eclipse.kapua.app.console.module.account.shared.util;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.kapua.app.console.module.account.shared.model.GwtAccountQuery;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.model.query.FieldSortCriteria;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate.Operator;
import org.eclipse.kapua.service.account.AccountAttributes;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;

public class GwtKapuaAccountModelConverter {

    private GwtKapuaAccountModelConverter() {
    }

    public static KapuaQuery convertAccountQuery(PagingLoadConfig loadConfig, GwtAccountQuery gwtAccountQuery) {
        KapuaQuery query = new KapuaQuery(GwtKapuaCommonsModelConverter.convertKapuaId(gwtAccountQuery.getScopeId()));

        AndPredicate predicate = query.andPredicate();

        if (gwtAccountQuery.getName() != null && !gwtAccountQuery.getName().trim().isEmpty()) {
            predicate.and(query.attributePredicate(AccountAttributes.NAME, gwtAccountQuery.getName(), Operator.LIKE));
        }

        if (gwtAccountQuery.getOrganizationName() != null && !gwtAccountQuery.getOrganizationName().isEmpty()) {
            predicate.and(query.attributePredicate(AccountAttributes.ORGANIZATION_NAME, gwtAccountQuery.getOrganizationName(), Operator.LIKE));
        }

        if (gwtAccountQuery.getOrganizationEmail() != null && !gwtAccountQuery.getOrganizationEmail().isEmpty()) {
            predicate.and(query.attributePredicate(AccountAttributes.ORGANIZATION_EMAIL, gwtAccountQuery.getOrganizationEmail(), Operator.LIKE));
        }

        if (gwtAccountQuery.getExpirationDate() != null) {
            predicate.and(query.attributePredicate(AccountAttributes.EXPIRATION_DATE, gwtAccountQuery.getExpirationDate(), Operator.EQUAL));
        }

        if (gwtAccountQuery.getOrganizationContactName() != null && !gwtAccountQuery.getOrganizationContactName().isEmpty()) {
            predicate.and(query.attributePredicate(AccountAttributes.CONTACT_NAME, gwtAccountQuery.getOrganizationContactName(), Operator.LIKE));
        }

        if (gwtAccountQuery.getOrganizationPhoneNumber() != null && !gwtAccountQuery.getOrganizationPhoneNumber().isEmpty()) {
            predicate.and(query.attributePredicate(AccountAttributes.PHONE_NUMBER, gwtAccountQuery.getOrganizationPhoneNumber(), Operator.LIKE));
        }

        if (gwtAccountQuery.getOrganizationAddressLine1() != null && !gwtAccountQuery.getOrganizationAddressLine1().isEmpty()) {
            predicate.and(query.attributePredicate(AccountAttributes.ADDRESS_1, gwtAccountQuery.getOrganizationAddressLine1(), Operator.LIKE));
        }

        if (gwtAccountQuery.getOrganizationAddressLine2() != null && !gwtAccountQuery.getOrganizationAddressLine2().isEmpty()) {
            predicate.and(query.attributePredicate(AccountAttributes.ADDRESS_2, gwtAccountQuery.getOrganizationAddressLine2(), Operator.LIKE));
        }

        if (gwtAccountQuery.getOrganizationZipPostCode() != null && !gwtAccountQuery.getOrganizationZipPostCode().isEmpty()) {
            predicate.and(query.attributePredicate(AccountAttributes.ZIP_POST_CODE, gwtAccountQuery.getOrganizationZipPostCode(), Operator.LIKE));
        }

        if (gwtAccountQuery.getOrganizationCity() != null && !gwtAccountQuery.getOrganizationCity().isEmpty()) {
            predicate.and(query.attributePredicate(AccountAttributes.ORGANIZATION_CITY, gwtAccountQuery.getOrganizationCity(), Operator.LIKE));
        }

        if (gwtAccountQuery.getOrganizationStateProvinceCountry() != null && !gwtAccountQuery.getOrganizationStateProvinceCountry().isEmpty()) {
            predicate.and(query.attributePredicate(AccountAttributes.STATE_PROVINCE, gwtAccountQuery.getOrganizationStateProvinceCountry(), Operator.LIKE));
        }

        if (gwtAccountQuery.getOrganizationCountry() != null && !gwtAccountQuery.getOrganizationCountry().isEmpty()) {
            predicate.and(query.attributePredicate(AccountAttributes.ORGANIZATION_COUNTRY, gwtAccountQuery.getOrganizationCountry(), Operator.LIKE));
        }

        String sortField = StringUtils.isEmpty(loadConfig.getSortField()) ? AccountAttributes.NAME : loadConfig.getSortField();
        if (sortField.equals("modifiedOnFormatted")) {
            sortField = AccountAttributes.MODIFIED_ON;
        } else if (sortField.equals("modifiedByName")) {
            sortField = AccountAttributes.MODIFIED_BY;
        }
        SortOrder sortOrder = loadConfig.getSortDir().equals(SortDir.DESC) ? SortOrder.DESCENDING : SortOrder.ASCENDING;
        FieldSortCriteria sortCriteria = query.fieldSortCriteria(sortField, sortOrder);
        query.setSortCriteria(sortCriteria);
        query.setOffset(loadConfig.getOffset());
        query.setLimit(loadConfig.getLimit());
        query.setPredicate(predicate);
        query.setAskTotalCount(gwtAccountQuery.getAskTotalCount());
        return query;
    }
}
