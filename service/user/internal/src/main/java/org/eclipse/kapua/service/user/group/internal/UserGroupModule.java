/*******************************************************************************
 * Copyright (c) 2025, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.user.group.internal;

import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;
import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.jpa.KapuaJpaTxManagerFactory;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.model.domain.DomainEntry;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.access.AccessPermissionFactory;
import org.eclipse.kapua.service.authorization.access.AccessPermissionService;
import org.eclipse.kapua.service.authorization.access.GroupQueryHelper;
import org.eclipse.kapua.service.authorization.group.GroupFactory;
import org.eclipse.kapua.service.authorization.group.GroupPermissionFactory;
import org.eclipse.kapua.service.authorization.group.GroupPermissionService;
import org.eclipse.kapua.service.authorization.group.GroupRoleService;
import org.eclipse.kapua.service.authorization.group.GroupService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.authorization.role.RolePermissionFactory;
import org.eclipse.kapua.service.authorization.role.RolePermissionService;
import org.eclipse.kapua.service.user.UserRepository;
import org.eclipse.kapua.service.user.group.UserGroup;
import org.eclipse.kapua.service.user.group.UserGroupFactory;
import org.eclipse.kapua.service.user.group.UserGroupPermissionService;
import org.eclipse.kapua.service.user.group.UserGroupRoleService;
import org.eclipse.kapua.service.user.group.UserGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

/**
 * @since 2.1.0
 */
public class UserGroupModule extends AbstractKapuaModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserGroupModule.class);

    @Override
    protected void configureModule() {
        bind(UserGroupFactory.class).to(UserGroupFactoryImpl.class).in(Singleton.class);
    }

    @ProvidesIntoSet
    public Domain userDomain() {
        return new DomainEntry(Domains.USER_GROUP, UserGroup.class.getName(), true, Actions.read, Actions.delete, Actions.write);
    }


    @Provides
    @Singleton
    UserGroupServiceValidationUtils userGroupServiceValidationUtils(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            GroupService groupService,
            AccessPermissionService accessPermissionService,
            AccessPermissionFactory accessPermissionFactory,
            RolePermissionService rolePermissionService,
            RolePermissionFactory rolePermissionFactory,
            GroupPermissionService groupPermissionService,
            GroupPermissionFactory groupPermissionFactory,
            UserRepository userRepository,
            KapuaJpaTxManagerFactory jpaTxManagerFactory
    ) {
        return new UserGroupServiceValidationUtilsImpl(
            authorizationService,
            permissionFactory,
            groupService,
            accessPermissionService,
            accessPermissionFactory,
            rolePermissionService,
            rolePermissionFactory,
            groupPermissionService,
            groupPermissionFactory,
            userRepository,
            jpaTxManagerFactory.create("kapua-user")
        );
    }

    @Provides
    @Singleton
    UserGroupService userGroupService(
            KapuaJpaTxManagerFactory jpaTxManagerFactory,
            GroupService groupService,
            GroupFactory groupFactory,
            GroupQueryHelper groupQueryHelper,
            UserGroupServiceValidationUtils userGroupServiceValidationUtils
    ) {
        return new UserGroupServiceImpl(
            jpaTxManagerFactory.create("kapua-user"),
            groupService,
            groupFactory,
            groupQueryHelper,
            userGroupServiceValidationUtils
        );
    }


    @Provides
    @Singleton
    UserGroupPermissionService userGroupPermissionService(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            UserGroupService userGroupService,
            GroupPermissionService groupPermissionService
    ) {
        return new UserGroupPermissionServiceImpl(
                authorizationService,
                permissionFactory,
                userGroupService,
                groupPermissionService
        );
    }

    @Provides
    @Singleton
    public UserGroupRoleService userGroupRoleService(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            GroupRoleService userGroupRoleService,
            UserGroupService userGroupService
    ) {
        return new UserGroupRoleServiceImpl(
            authorizationService,
            permissionFactory,
            userGroupRoleService,
            userGroupService
        );
    }

}