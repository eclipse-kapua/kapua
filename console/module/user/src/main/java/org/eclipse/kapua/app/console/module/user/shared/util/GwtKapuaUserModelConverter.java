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
package org.eclipse.kapua.app.console.module.user.shared.util;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUser.GwtUserStatus;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUser.GwtUserType;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUserQuery;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.query.FieldSortCriteria;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate.Operator;
import org.eclipse.kapua.service.user.UserAttributes;
import org.eclipse.kapua.service.user.UserQuery;
import org.eclipse.kapua.service.user.UserStatus;
import org.eclipse.kapua.service.user.UserType;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;

/**
 * Utility class for convertKapuaId {@link BaseModel}s to {@link KapuaEntity}ies and other Kapua models
 */
public class GwtKapuaUserModelConverter {

    private GwtKapuaUserModelConverter() {
    }

    /**
     * Converts a {@link GwtUserQuery} into a {@link UserQuery} object for backend usage
     *
     * @param loadConfig
     *         the load configuration
     * @param gwtUserQuery
     *         the {@link GwtUserQuery} to convertKapuaId
     * @return the converted {@link UserQuery}
     */
    public static UserQuery convertUserQuery(PagingLoadConfig loadConfig, GwtUserQuery gwtUserQuery) {

        UserQuery query = new UserQuery(GwtKapuaCommonsModelConverter.convertKapuaId(gwtUserQuery.getScopeId()));

        AndPredicate predicate = query.andPredicate();
        if (gwtUserQuery.getName() != null && !gwtUserQuery.getName().isEmpty()) {
            predicate.and(query.attributePredicate(UserAttributes.NAME, gwtUserQuery.getName(), Operator.LIKE));
        }
        if (gwtUserQuery.getUserStatus() != null && !gwtUserQuery.getUserStatus().equals(GwtUserStatus.ANY.toString())) {
            predicate.and(query.attributePredicate(UserAttributes.STATUS, convertUserStatus(gwtUserQuery.getUserStatus()), Operator.EQUAL));
        }
        if (gwtUserQuery.getPhoneNumber() != null && !gwtUserQuery.getPhoneNumber().isEmpty()) {
            predicate.and(query.attributePredicate(UserAttributes.PHONE_NUMBER, gwtUserQuery.getPhoneNumber(), Operator.LIKE));
        }
        if (gwtUserQuery.getExpirationDate() != null) {
            predicate.and(query.attributePredicate(UserAttributes.EXPIRATION_DATE, gwtUserQuery.getExpirationDate(), Operator.EQUAL));
        }
        if (gwtUserQuery.getEmail() != null && !gwtUserQuery.getEmail().isEmpty()) {
            predicate.and(query.attributePredicate(UserAttributes.EMAIL, gwtUserQuery.getEmail(), Operator.LIKE));
        }
        if (gwtUserQuery.getDisplayName() != null && !gwtUserQuery.getDisplayName().isEmpty()) {
            predicate.and(query.attributePredicate(UserAttributes.DISPLAY_NAME, gwtUserQuery.getDisplayName(), Operator.LIKE));
        }
        if (gwtUserQuery.getUserType() != null && !gwtUserQuery.getUserType().equals(GwtUserType.ANY.toString())) {
            predicate.and(query.attributePredicate(UserAttributes.USER_TYPE, convertUserType(gwtUserQuery.getUserType()), Operator.EQUAL));
        }
        query.setOffset(loadConfig.getOffset());
        query.setLimit(loadConfig.getLimit());
        String sortField = StringUtils.isEmpty(loadConfig.getSortField()) ? UserAttributes.NAME : loadConfig.getSortField();
        if (sortField.equals("username")) {
            sortField = UserAttributes.NAME;
        } else if (sortField.equals("modifiedByName")) {
            sortField = UserAttributes.MODIFIED_BY;
        } else if (sortField.equals("expirationDateFormatted")) {
            sortField = UserAttributes.EXPIRATION_DATE;
        } else if (sortField.equals("modifiedOnFormatted")) {
            sortField = UserAttributes.MODIFIED_ON;
        } else if (sortField.equals("createdOnFormatted")) {
            sortField = UserAttributes.CREATED_ON;
        } else if (sortField.equals("createdByName")) {
            sortField = UserAttributes.CREATED_BY;
        } else if (sortField.equals("userType")) {
            sortField = UserAttributes.USER_TYPE;
        }
        SortOrder sortOrder = loadConfig.getSortDir().equals(SortDir.DESC) ? SortOrder.DESCENDING : SortOrder.ASCENDING;
        FieldSortCriteria sortCriteria = query.fieldSortCriteria(sortField, sortOrder);
        query.setSortCriteria(sortCriteria);
        query.setPredicate(predicate);
        query.setAskTotalCount(gwtUserQuery.getAskTotalCount());
        // Return converted
        return query;
    }

    private static UserStatus convertUserStatus(String userStatus) {
        return UserStatus.valueOf(userStatus);
    }

    public static UserStatus convertUserStatus(GwtUserStatus gwtUserStatus) {
        return UserStatus.valueOf(gwtUserStatus.toString());
    }

    private static UserType convertUserType(String userType) {
        return UserType.valueOf(userType);
    }

    public static UserType convertUserType(GwtUserType gwtUserType) {
        return UserType.valueOf(gwtUserType.toString());
    }

}
