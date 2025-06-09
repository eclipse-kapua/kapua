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
package org.eclipse.kapua.app.console.module.authorization.shared.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtAccessInfoCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtAccessPermissionCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtAccessRoleCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtDomain;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupQuery;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtPermission;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtPermission.GwtAction;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtRole;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtRoleCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtRolePermission;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtRoleQuery;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.FieldSortCriteria;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate.Operator;
import org.eclipse.kapua.service.authorization.access.AccessInfoCreator;
import org.eclipse.kapua.service.authorization.access.AccessPermissionCreator;
import org.eclipse.kapua.service.authorization.access.AccessRoleCreator;
import org.eclipse.kapua.service.authorization.domain.DomainListResult;
import org.eclipse.kapua.service.authorization.domain.DomainRegistryService;
import org.eclipse.kapua.service.authorization.group.GroupAttributes;
import org.eclipse.kapua.service.authorization.group.GroupQuery;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RoleAttributes;
import org.eclipse.kapua.service.authorization.role.RoleCreator;
import org.eclipse.kapua.service.authorization.role.RoleFactory;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.eclipse.kapua.service.authorization.role.RolePermissionFactory;
import org.eclipse.kapua.service.authorization.role.RoleQuery;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;

/**
 * Utility class for convertKapuaId {@link BaseModel}s to {@link KapuaEntity}ies and other Kapua models
 */
public class GwtKapuaAuthorizationModelConverter {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final DomainRegistryService DOMAIN_REGISTRY_SERVICE = LOCATOR.getService(DomainRegistryService.class);

    private GwtKapuaAuthorizationModelConverter() {
    }

    public static GroupQuery convertGroupQuery(PagingLoadConfig loadConfig,
            GwtGroupQuery gwtGroupQuery) {
        GroupQuery query = new GroupQuery(GwtKapuaCommonsModelConverter.convertKapuaId(gwtGroupQuery.getScopeId()));

        AndPredicate predicate = query.andPredicate();
        if (gwtGroupQuery.getName() != null && !gwtGroupQuery.getName().isEmpty()) {
            predicate.and(query.attributePredicate(GroupAttributes.NAME, gwtGroupQuery.getName(), Operator.LIKE));
        }
        if (gwtGroupQuery.getDescription() != null && !gwtGroupQuery.getDescription().isEmpty()) {
            predicate.and(query.attributePredicate(GroupAttributes.DESCRIPTION, gwtGroupQuery.getDescription(), Operator.LIKE));
        }
        String sortField = StringUtils.isEmpty(loadConfig.getSortField()) ? GroupAttributes.NAME : loadConfig.getSortField();
        if (sortField.equals("groupName")) {
            sortField = GroupAttributes.NAME;
        } else if (sortField.equals("createdOnFormatted")) {
            sortField = GroupAttributes.CREATED_ON;
        }
        SortOrder sortOrder = loadConfig.getSortDir().equals(SortDir.DESC) ? SortOrder.DESCENDING : SortOrder.ASCENDING;
        FieldSortCriteria sortCriteria = query.fieldSortCriteria(sortField, sortOrder);
        query.setSortCriteria(sortCriteria);
        query.setOffset(loadConfig.getOffset());
        query.setLimit(loadConfig.getLimit());
        query.setPredicate(predicate);
        query.setAskTotalCount(true);

        return query;
    }

    /**
     * Converts a {@link GwtRoleQuery} into a {@link Role} object for backend usage
     *
     * @param loadConfig
     *         the load configuration
     * @param gwtRoleQuery
     *         the {@link GwtRoleQuery} to convertKapuaId
     * @return the converted {@link RoleQuery}
     */
    public static RoleQuery convertRoleQuery(PagingLoadConfig loadConfig, GwtRoleQuery gwtRoleQuery) {

        // Convert query
        RoleQuery query = new RoleQuery(GwtKapuaCommonsModelConverter.convertKapuaId(gwtRoleQuery.getScopeId()));

        AndPredicate predicate = query.andPredicate();

        if (gwtRoleQuery.getName() != null && !gwtRoleQuery.getName().trim().isEmpty()) {
            predicate.and(query.attributePredicate(RoleAttributes.NAME, gwtRoleQuery.getName(), Operator.LIKE));
        }
        if (gwtRoleQuery.getDescription() != null && !gwtRoleQuery.getDescription().trim().isEmpty()) {
            predicate.and(query.attributePredicate(RoleAttributes.DESCRIPTION, gwtRoleQuery.getDescription(), Operator.LIKE));
        }
        String sortField = StringUtils.isEmpty(loadConfig.getSortField()) ? RoleAttributes.NAME : loadConfig.getSortField();
        if (sortField.equals("modifiedOnFormatted")) {
            sortField = RoleAttributes.MODIFIED_ON;
        } else if (sortField.equals("modifiedByName")) {
            sortField = RoleAttributes.MODIFIED_BY;
        }
        SortOrder sortOrder = loadConfig.getSortDir().equals(SortDir.DESC) ? SortOrder.DESCENDING : SortOrder.ASCENDING;
        FieldSortCriteria sortCriteria = query.fieldSortCriteria(sortField, sortOrder);
        query.setSortCriteria(sortCriteria);
        query.setOffset(loadConfig.getOffset());
        query.setLimit(loadConfig.getLimit());
        query.setPredicate(predicate);
        query.setAskTotalCount(gwtRoleQuery.getAskTotalCount());
        // Return converted
        return query;
    }

    /**
     * Converts a {@link GwtRole} into a {@link Role} object for backend usage
     *
     * @param gwtRole
     *         the {@link GwtRole} to convertKapuaId
     * @return the converted {@link Role}
     */
    public static Role convertRole(GwtRole gwtRole) throws KapuaException {

        // Get Services
        KapuaLocator locator = KapuaLocator.getInstance();
        RoleFactory roleFactory = locator.getFactory(RoleFactory.class);
        RolePermissionFactory rolePermissionFactory = locator.getFactory(RolePermissionFactory.class);

        // Convert scopeId
        KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtRole.getScopeId());
        Role role = roleFactory.newEntity(scopeId);
        GwtKapuaCommonsModelConverter.convertUpdatableEntity(gwtRole, role);

        // Convert name
        role.setName(gwtRole.getName());
        role.setDescription(gwtRole.getUnescapedDescription());

        if (gwtRole.getPermissions() != null) {
            // Convert permission associated with role
            Set<RolePermission> rolePermissions = new HashSet<RolePermission>();
            for (GwtRolePermission gwtRolePermission : gwtRole.getPermissions()) {

                Permission p = convertPermission(new GwtPermission(
                        gwtRolePermission.getDomain(),
                        gwtRolePermission.getActionEnum(),
                        gwtRolePermission.getTargetScopeId(),
                        gwtRolePermission.getGroupId(),
                        gwtRolePermission.getForwardable()));

                RolePermission rp = rolePermissionFactory.newEntity(scopeId);
                rp.setPermission(p);
                rp.setId(GwtKapuaCommonsModelConverter.convertKapuaId(gwtRolePermission.getId()));
                rp.setRoleId(role.getId());

                rolePermissions.add(rp);
            }
        }
        // Return converted
        return role;
    }

    /**
     * Converts a {@link GwtRoleCreator} into a {@link RoleCreator} object for backend usage
     *
     * @param gwtRoleCreator
     *         the {@link GwtRoleCreator} to convertKapuaId
     * @return the converted {@link RoleCreator}
     */
    public static RoleCreator convertRoleCreator(GwtRoleCreator gwtRoleCreator) throws KapuaException {

        // Convert scopeId
        KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtRoleCreator.getScopeId());
        RoleCreator roleCreator = new RoleCreator(scopeId);

        // Convert name
        roleCreator.setName(gwtRoleCreator.getName());
        roleCreator.setDescription(gwtRoleCreator.getDescription());

        // Convert permission associated with role
        Set<Permission> permissions = new HashSet<Permission>();
        if (gwtRoleCreator.getPermissions() != null) {
            for (GwtPermission gwtPermission : gwtRoleCreator.getPermissions()) {
                permissions.add(convertPermission(gwtPermission));
            }
        }

        roleCreator.setPermissions(permissions);
        // Return converted
        return roleCreator;
    }

    /**
     * Converts a {@link GwtAccessRoleCreator} into a {@link AccessRoleCreator} object for backend usage
     *
     * @param gwtAccessRoleCreator
     *         the {@link GwtAccessRoleCreator} to convertKapuaId
     * @return the converted {@link AccessRoleCreator}
     * @since 1.0.0
     */
    public static AccessRoleCreator convertAccessRoleCreator(GwtAccessRoleCreator gwtAccessRoleCreator) {

        // Convert scopeId
        KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtAccessRoleCreator.getScopeId());
        AccessRoleCreator accessRoleCreator = new AccessRoleCreator(scopeId);

        // Convert accessInfoId
        accessRoleCreator.setAccessInfoId(GwtKapuaCommonsModelConverter.convertKapuaId(gwtAccessRoleCreator.getAccessInfoId()));

        // Convert roleId
        accessRoleCreator.setRoleId(GwtKapuaCommonsModelConverter.convertKapuaId(gwtAccessRoleCreator.getRoleId()));
        // Return converted
        return accessRoleCreator;
    }

    /**
     * Converts a {@link GwtAccessPermissionCreator} into a {@link AccessPermissionCreator} object for backend usage
     *
     * @param gwtAccessPermissionCreator
     *         the {@link GwtAccessPermissionCreator} to convertKapuaId
     * @return the converted {@link AccessPermissionCreator}
     * @since 1.0.0
     */
    public static AccessPermissionCreator convertAccessPermissionCreator(GwtAccessPermissionCreator gwtAccessPermissionCreator) throws KapuaException {

        // Convert scopeId
        KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtAccessPermissionCreator.getScopeId());
        AccessPermissionCreator accessPermissionCreator = new AccessPermissionCreator(scopeId);

        // Convert accessInfoId
        accessPermissionCreator.setAccessInfoId(GwtKapuaCommonsModelConverter.convertKapuaId(gwtAccessPermissionCreator.getAccessInfoId()));

        // Convert Permission
        accessPermissionCreator.setPermission(convertPermission(gwtAccessPermissionCreator.getPermission()));
        // Return converted
        return accessPermissionCreator;
    }

    public static AccessInfoCreator convertAccessInfoCreator(GwtAccessInfoCreator gwtAccessInfoCreator) {
        // Convert scopeId
        KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtAccessInfoCreator.getScopeId());
        AccessInfoCreator accessInfoCreator = new AccessInfoCreator(scopeId);

        // Convert userId
        accessInfoCreator.setUserId(GwtKapuaCommonsModelConverter.convertKapuaId(gwtAccessInfoCreator.getUserId()));
        // Return converted
        return accessInfoCreator;
    }

    /**
     * Converts a {@link GwtPermission} into a {@link Permission} object for backend usage.
     *
     * @param gwtPermission
     *         The {@link GwtPermission} to convertKapuaId.
     * @return The converted {@link Permission}.
     * @since 1.0.0
     */
    public static Permission convertPermission(GwtPermission gwtPermission) throws KapuaException {
        // Return converted
        return new Permission(gwtPermission.getDomain(),
                convertAction(gwtPermission.getActionEnum()),
                GwtKapuaCommonsModelConverter.convertKapuaId(gwtPermission.getTargetScopeId()),
                GwtKapuaCommonsModelConverter.convertKapuaId(gwtPermission.getGroupId()),
                gwtPermission.getForwardable());
    }

    /**
     * Converts a {@link GwtAction} into the related {@link Actions}
     *
     * @param gwtAction
     *         the {@link GwtAction} to convertKapuaId
     * @return the converted {@link Actions}
     * @since 1.0.0
     */
    public static Actions convertAction(GwtAction gwtAction) {

        Actions action = null;
        if (gwtAction != null) {
            switch (gwtAction) {
            case connect:
                action = Actions.connect;
                break;
            case delete:
                action = Actions.delete;
                break;
            case execute:
                action = Actions.execute;
                break;
            case read:
                action = Actions.read;
                break;
            case write:
                action = Actions.write;
                break;
            case ALL:
                action = null;
                break;
            }
        }
        return action;
    }

    /**
     * Converts a {@link GwtDomain} into the related equivalent domain string
     *
     * @param gwtDomain
     *         the {@link GwtDomain} to convertKapuaId
     * @return the converted domain {@link String}
     * @since 1.0.0
     */
    public static Domain convertDomain(GwtDomain gwtDomain) throws KapuaException {

        String gwtDomainName = gwtDomain.getDomainName();

        if (gwtDomainName == null || gwtDomainName.isEmpty()) {
            return null;
        }

        DomainListResult list = DOMAIN_REGISTRY_SERVICE.query(new KapuaQuery((KapuaId) null));

        for (org.eclipse.kapua.service.authorization.domain.Domain domain : list.getItems()) {
            if (gwtDomainName.equals(domain.getName())) {
                return domain.getDomain();
            }
        }

        throw new KapuaIllegalArgumentException("gwtDomain.name", gwtDomainName);
    }

}
