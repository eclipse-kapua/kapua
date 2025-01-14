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
package org.eclipse.kapua.app.console.module.user.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtGroupedNVPair;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtAccessRoleQuery;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUser;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUserCreator;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUserQuery;
import org.eclipse.kapua.app.console.module.user.shared.service.GwtUserService;
import org.eclipse.kapua.app.console.module.user.shared.util.GwtKapuaUserModelConverter;
import org.eclipse.kapua.app.console.module.user.shared.util.KapuaGwtUserModelConverter;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.credential.CredentialCreator;
import org.eclipse.kapua.service.authentication.credential.CredentialFactory;
import org.eclipse.kapua.service.authentication.credential.CredentialService;
import org.eclipse.kapua.service.authentication.credential.CredentialStatus;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.authorization.role.RoleService;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceFactory;
import org.eclipse.kapua.service.device.registry.DeviceListResult;
import org.eclipse.kapua.service.device.registry.DeviceQuery;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionService;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserFactory;
import org.eclipse.kapua.service.user.UserListResult;
import org.eclipse.kapua.service.user.UserQuery;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.service.user.UserType;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

/**
 * The server side implementation of the RPC service.
 */
public class GwtUserServiceImpl extends KapuaRemoteServiceServlet implements GwtUserService {

    private static final long serialVersionUID = 7430961652373364113L;

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();
    private static final RoleService ROLE_SERVICE = LOCATOR.getService(RoleService.class);

    private static final CredentialService CREDENTIAL_SERVICE = LOCATOR.getService(CredentialService.class);
    private static final CredentialFactory CREDENTIAL_FACTORY = LOCATOR.getFactory(CredentialFactory.class);

    private static final UserService USER_SERVICE = LOCATOR.getService(UserService.class);

    private static final UserFactory USER_FACTORY = LOCATOR.getFactory(UserFactory.class);

    private static final DeviceConnectionService DEVICE_CONNECTION_SERVICE = LOCATOR.getService(DeviceConnectionService.class);
    private static final AuthorizationService AUTHORIZATION_SERVICE = LOCATOR.getService(AuthorizationService.class);

    private static final PermissionFactory PERMISSION_FACTORY = LOCATOR.getFactory(PermissionFactory.class);
    private static final DeviceRegistryService DEVICE_SERVICE = LOCATOR.getService(DeviceRegistryService.class);
    private static final DeviceFactory DEVICE_FACTORY = LOCATOR.getFactory(DeviceFactory.class);

    private static final String USER_INFO = "userInfo";
    private static final String ENTITY_INFO = "entityInfo";

    @Override
    public GwtUser create(GwtXSRFToken xsrfToken, GwtUserCreator gwtUserCreator) throws GwtKapuaException {
        checkXSRFToken(xsrfToken);

        GwtUser gwtUser = null;
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtUserCreator.getScopeId());

            UserCreator userCreator = USER_FACTORY.newCreator(scopeId, gwtUserCreator.getUsername());
            userCreator.setUserType(GwtKapuaUserModelConverter.convertUserType(gwtUserCreator.getUserType()));
            userCreator.setDisplayName(gwtUserCreator.getDisplayName());
            userCreator.setEmail(gwtUserCreator.getEmail());
            userCreator.setPhoneNumber(gwtUserCreator.getPhoneNumber());
            userCreator.setExpirationDate(gwtUserCreator.getExpirationDate());
            userCreator.setStatus(GwtKapuaUserModelConverter.convertUserStatus(gwtUserCreator.getUserStatus()));
            userCreator.setExternalId(gwtUserCreator.getExternalId());
            userCreator.setExternalUsername(gwtUserCreator.getExternalUsername());
            // Create the User
            User user = USER_SERVICE.create(userCreator);
            // Create credentials
            if (UserType.INTERNAL.equals(user.getUserType()) &&
                    gwtUserCreator.getPassword() != null) {

                CredentialCreator credentialCreator = CREDENTIAL_FACTORY.newCreator(scopeId,
                        user.getId(),
                        "PASSWORD",
                        gwtUserCreator.getPassword(),
                        CredentialStatus.ENABLED,
                        null);

                CREDENTIAL_SERVICE.create(credentialCreator);
            }

            gwtUser = KapuaGwtUserModelConverter.convertUser(USER_SERVICE.find(user.getScopeId(), user.getId()));
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return gwtUser;
    }

    @Override
    public GwtUser update(GwtXSRFToken xsrfToken, GwtUser gwtUser)
            throws GwtKapuaException {
        checkXSRFToken(xsrfToken);

        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtUser.getScopeId());
            KapuaId userId = KapuaEid.parseCompactId(gwtUser.getId());

            User user = USER_SERVICE.find(scopeId, userId);

            if (user != null) {
                // Update user
                user.setName(gwtUser.getUnescapedUsername());
                user.setDisplayName(gwtUser.getUnescapedDisplayName());
                user.setEmail(gwtUser.getUnescapedEmail());
                user.setPhoneNumber(gwtUser.getUnescapedPhoneNumber());
                user.setExpirationDate(gwtUser.getExpirationDate());
                user.setExternalId(gwtUser.getExternalId());
                user.setExternalUsername(gwtUser.getExternalUsername());
                user.setStatus(GwtKapuaUserModelConverter.convertUserStatus(gwtUser.getStatusEnum()));

                // optlock
                user.setOptlock(gwtUser.getOptlock());

                // update the user
                user = USER_SERVICE.update(user);
            }

            return KapuaGwtUserModelConverter.convertUser(user);
        } catch (Exception exception) {
            throw KapuaExceptionHandler.buildExceptionFromError(exception);
        }
    }

    @Override
    public void delete(GwtXSRFToken xsrfToken, String gwtScopeId, String gwtUserId)
            throws GwtKapuaException {
        checkXSRFToken(xsrfToken);

        try {
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtScopeId);
            KapuaId userId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtUserId);

            USER_SERVICE.delete(scopeId, userId);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
    }

    @Override
    public GwtUser find(String accountId, String userIdString)
            throws GwtKapuaException {

        GwtUser gwtUser = null;
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(accountId);
            KapuaId userId = KapuaEid.parseCompactId(userIdString);

            User user = USER_SERVICE.find(scopeId, userId);

            if (user != null) {
                gwtUser = KapuaGwtUserModelConverter.convertUser(user);
            }
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return gwtUser;
    }

    @Override
    public ListLoadResult<GwtUser> findAll(String scopeIdString)
            throws GwtKapuaException {
        KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
        List<GwtUser> gwtUserList = new ArrayList<GwtUser>();
        try {
            UserQuery query = new UserQuery(scopeId);
            UserListResult list = USER_SERVICE.query(query);

            for (User user : list.getItems()) {
                gwtUserList.add(KapuaGwtUserModelConverter.convertUser(user));
            }
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return new BaseListLoadResult<GwtUser>(gwtUserList);
    }

    @Override
    public PagingLoadResult<GwtUser> query(PagingLoadConfig loadConfig, GwtUserQuery gwtUserQuery) throws GwtKapuaException {
        // Do query
        int totalLength = 0;
        List<GwtUser> gwtUsers = new ArrayList<GwtUser>();
        try {
            // Convert from GWT entity
            UserQuery userQuery = GwtKapuaUserModelConverter.convertUserQuery(loadConfig, gwtUserQuery);

            // query
            UserListResult users = USER_SERVICE.query(userQuery);
            totalLength = users.getTotalCount().intValue();

            // If there are results
            if (!users.isEmpty()) {
                //TODO: #LAYER_VIOLATION - missed opportunity to perform paging at the database layer

                UserListResult allUsers = KapuaSecurityUtils.doPrivileged(new Callable<UserListResult>() {

                    @Override
                    public UserListResult call() throws Exception {
                        return USER_SERVICE.query(new UserQuery(null));
                    }
                });

                HashMap<String, String> usernameMap = new HashMap<String, String>();
                for (User user : allUsers.getItems()) {
                    usernameMap.put(user.getId().toCompactId(), user.getName());
                }

                // Convert to GWT entity
                for (User u : users.getItems()) {
                    GwtUser gwtUser = KapuaGwtUserModelConverter.convertUser(u);
                    gwtUser.setCreatedByName(usernameMap.get(u.getCreatedBy().toCompactId()));
                    gwtUser.setModifiedByName(usernameMap.get(u.getModifiedBy().toCompactId()));
                    gwtUsers.add(gwtUser);
                }
            }

        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return new BasePagingLoadResult<GwtUser>(gwtUsers, loadConfig.getOffset(), totalLength);
    }

    @Override
    public ListLoadResult<GwtGroupedNVPair> getUserDescription(boolean isSsoEnabled, String shortScopeId,
            String shortUserId) throws GwtKapuaException {
        List<GwtGroupedNVPair> gwtUserDescription = new ArrayList<GwtGroupedNVPair>();
        try {
            final KapuaId scopeId = KapuaEid.parseCompactId(shortScopeId);
            final KapuaId userId = KapuaEid.parseCompactId(shortUserId);

            final User user = USER_SERVICE.find(scopeId, userId);

            if (user != null) {
                //TODO: #LAYER_VIOLATION - user lookup should not be done here (horribly inefficient)

                UserListResult userListResult = KapuaSecurityUtils.doPrivileged(new Callable<UserListResult>() {

                    @Override
                    public UserListResult call() throws Exception {
                        return USER_SERVICE.query(new UserQuery(null));
                    }
                });

                Map<String, String> usernameMap = new HashMap<String, String>();
                for (User userItem : userListResult.getItems()) {
                    usernameMap.put(userItem.getId().toCompactId(), userItem.getName());
                }

                DeviceConnection deviceConnection = null;
                if (deviceListQuery(scopeId) != null && AUTHORIZATION_SERVICE.isPermitted(PERMISSION_FACTORY.newPermission(Domains.DEVICE_CONNECTION, Actions.read, scopeId))) {
                    for (Device device : deviceListQuery(scopeId).getItems()) {
                        if (device.getConnectionId() != null) {
                            deviceConnection = DEVICE_CONNECTION_SERVICE.find(scopeId, device.getConnectionId());
                            break;
                        }
                    }
                }
                gwtUserDescription.add(new GwtGroupedNVPair(USER_INFO, "userStatus", user.getStatus().toString()));
                gwtUserDescription.add(new GwtGroupedNVPair(USER_INFO, "userName", user.getName()));
                gwtUserDescription.add(new GwtGroupedNVPair(USER_INFO, "userDisplayName", user.getDisplayName()));
                gwtUserDescription.add(new GwtGroupedNVPair(USER_INFO, "userEmail", user.getEmail()));
                gwtUserDescription.add(new GwtGroupedNVPair(USER_INFO, "userPhoneNumber", user.getPhoneNumber()));
                gwtUserDescription.add(new GwtGroupedNVPair(USER_INFO, "expirationDate", user.getExpirationDate()));

                if (isSsoEnabled) {
                    gwtUserDescription.add(new GwtGroupedNVPair(USER_INFO, "userType", user.getUserType().toString()));
                    if (user.getUserType() == UserType.EXTERNAL) {
                        gwtUserDescription.add(new GwtGroupedNVPair(USER_INFO, "userExternalId", user.getExternalId()));
                        gwtUserDescription.add(new GwtGroupedNVPair(USER_INFO, "userExternalUsername", user.getExternalUsername()));
                    }
                }

                gwtUserDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "userCreatedOn", user.getCreatedOn()));
                gwtUserDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "userCreatedBy", user.getCreatedBy() != null ? usernameMap.get(user.getCreatedBy().toCompactId()) : null));
                gwtUserDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "userModifiedOn", user.getModifiedOn()));
                gwtUserDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "userModifiedBy", user.getModifiedBy() != null ? usernameMap.get(user.getModifiedBy().toCompactId()) : null));

                if (deviceConnection != null && deviceConnection.getReservedUserId() != null && deviceConnection.getReservedUserId().equals(user.getId())) {
                    gwtUserDescription.add(new GwtGroupedNVPair(USER_INFO, "userReservedConnection", "Yes"));
                }
            }
        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }

        return new BaseListLoadResult<GwtGroupedNVPair>(gwtUserDescription);
    }

    @Override
    public PagingLoadResult<GwtUser> getUsersForRole(PagingLoadConfig pagingLoadConfig,
            GwtAccessRoleQuery query) throws GwtKapuaException {
        int totalLength = 0;
        List<GwtUser> list = new ArrayList<GwtUser>();
        try {
            KapuaId scopeIdQuery = KapuaEid.parseCompactId(query.getScopeId());
            List<KapuaId> userIds = ROLE_SERVICE.userIdsByRoleId(scopeIdQuery, KapuaEid.parseCompactId(query.getRoleId()));

            for (KapuaId id : userIds) {
                User user = USER_SERVICE.find(scopeIdQuery, id);

                GwtUser gwtUser = KapuaGwtUserModelConverter.convertUser(user);
                gwtUser.set("type", "USER");
                list.add(gwtUser);
            }
            totalLength = list.size();

        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }
        return new BasePagingLoadResult<GwtUser>(list, pagingLoadConfig.getOffset(), totalLength);
    }

    @Override
    public PagingLoadResult<GwtUser> getUsersForAccount(PagingLoadConfig loadConfig, GwtUserQuery gwtUserQuery,
            String accountId) throws GwtKapuaException {

        int totalLength = 0;
        List<GwtUser> gwtUsers = new ArrayList<GwtUser>();
        try {
            UserQuery userQuery = GwtKapuaUserModelConverter.convertUserQuery(loadConfig, gwtUserQuery);
            UserListResult users = USER_SERVICE.query(userQuery);
            totalLength = users.getTotalCount().intValue();

            if (!users.isEmpty()) {
                //TODO: #LAYER_VIOLATION - user lookup should not be done here (horribly inefficient)

                final UserQuery allUsersQuery = new UserQuery(GwtKapuaCommonsModelConverter.convertKapuaId(accountId));
                UserListResult allUsers = KapuaSecurityUtils.doPrivileged(new Callable<UserListResult>() {

                    @Override
                    public UserListResult call() throws Exception {
                        return USER_SERVICE.query(allUsersQuery);
                    }
                });

                HashMap<String, String> usernameMap = new HashMap<String, String>();
                for (User user : allUsers.getItems()) {
                    usernameMap.put(user.getId().toCompactId(), user.getName());
                }
                for (User u : users.getItems()) {
                    GwtUser gwtUser = KapuaGwtUserModelConverter.convertUser(u);
                    gwtUser.setCreatedByName(usernameMap.get(u.getCreatedBy().toCompactId()));
                    gwtUser.setModifiedByName(usernameMap.get(u.getModifiedBy().toCompactId()));
                    gwtUsers.add(gwtUser);
                }
            }

        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return new BasePagingLoadResult<GwtUser>(gwtUsers, loadConfig.getOffset(), totalLength);
    }

    /**
     * Method which returns list of all available devices in current scope(if User have Device:read permission), otherwise null is returned.
     */
    private DeviceListResult deviceListQuery(KapuaId scopeId) throws KapuaException {
        DeviceListResult devicesList = null;
        if (AUTHORIZATION_SERVICE.isPermitted(PERMISSION_FACTORY.newPermission(Domains.DEVICE, Actions.read, scopeId))) {
            DeviceQuery deviceQuery = new DeviceQuery(scopeId);
            devicesList = DEVICE_SERVICE.query(deviceQuery);
        }
        return devicesList;
    }
}
