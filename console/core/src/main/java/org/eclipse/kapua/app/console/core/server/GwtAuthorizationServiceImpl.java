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
package org.eclipse.kapua.app.console.core.server;

import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.console.core.server.util.ConsoleSsoLocator;
import org.eclipse.kapua.app.console.core.shared.model.authentication.GwtJwtCredential;
import org.eclipse.kapua.app.console.core.shared.model.authentication.GwtJwtIdToken;
import org.eclipse.kapua.app.console.core.shared.model.authentication.GwtLoginCredential;
import org.eclipse.kapua.app.console.core.shared.service.GwtAuthorizationService;
import org.eclipse.kapua.app.console.module.account.shared.model.GwtAccount;
import org.eclipse.kapua.app.console.module.account.shared.util.KapuaGwtAccountModelConverter;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSessionPermission;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSessionPermissionAction;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSessionPermissionScope;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUser;
import org.eclipse.kapua.app.console.module.user.shared.util.KapuaGwtUserModelConverter;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.ThrowingRunnable;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.JwtCredentials;
import org.eclipse.kapua.service.authentication.UsernamePasswordCredentials;
import org.eclipse.kapua.service.authentication.exception.KapuaAuthenticationErrorCodes;
import org.eclipse.kapua.service.authentication.exception.KapuaAuthenticationException;
import org.eclipse.kapua.service.authentication.registration.RegistrationService;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfoService;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.access.AccessPermissionListResult;
import org.eclipse.kapua.service.authorization.access.AccessPermissionService;
import org.eclipse.kapua.service.authorization.access.AccessRole;
import org.eclipse.kapua.service.authorization.access.AccessRoleListResult;
import org.eclipse.kapua.service.authorization.access.AccessRoleService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.eclipse.kapua.service.authorization.role.RolePermissionListResult;
import org.eclipse.kapua.service.authorization.role.RolePermissionService;
import org.eclipse.kapua.service.authorization.role.RoleService;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettings;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettingsKey;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GwtAuthorizationServiceImpl extends KapuaRemoteServiceServlet implements GwtAuthorizationService {

    private static final long serialVersionUID = -3919578632016541047L;

    private static final Logger LOG = LoggerFactory.getLogger(GwtAuthorizationServiceImpl.class);

    public static final String SESSION_CURRENT = "console.current.session";

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final AccountService ACCOUNT_SERVICE = LOCATOR.getService(AccountService.class);

    private static final AuthenticationService AUTHENTICATION_SERVICE = LOCATOR.getService(AuthenticationService.class);

    private static final AccessInfoService ACCESS_INFO_SERVICE = LOCATOR.getService(AccessInfoService.class);
    private static final AccessPermissionService ACCESS_PERMISSION_SERVICE = LOCATOR.getService(AccessPermissionService.class);

    private static final AccessRoleService ACCESS_ROLE_SERVICE = LOCATOR.getService(AccessRoleService.class);
    private static final RoleService ROLE_SERVICE = LOCATOR.getService(RoleService.class);
    private static final RolePermissionService ROLE_PERMISSION_SERVICE = LOCATOR.getService(RolePermissionService.class);

    private static final RegistrationService REGISTRATION_SERVICE = LOCATOR.getService(RegistrationService.class);

    private static final UserService USER_SERVICE = LOCATOR.getService(UserService.class);
    private static final DatastoreSettings DATASTORE_SETTINGS = LOCATOR.getComponent(DatastoreSettings.class);

    /**
     * Login call in response to the login dialog.
     */
    @Override
    public GwtSession login(GwtLoginCredential gwtLoginCredentials, boolean trustReq) throws GwtKapuaException {

        try {
            // Check Credentials Values
            ArgumentValidator.notNull(gwtLoginCredentials, "loginCredentials");
            ArgumentValidator.notEmptyOrNull(gwtLoginCredentials.getUsername(), "loginCredentials.username");
            ArgumentValidator.notEmptyOrNull(gwtLoginCredentials.getPassword(), "loginCredentials.password");

            // Parse Credentials
            UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(gwtLoginCredentials.getUsername(), gwtLoginCredentials.getPassword());
            usernamePasswordCredentials.setAuthenticationCode(gwtLoginCredentials.getAuthenticationCode());
            usernamePasswordCredentials.setTrustKey(gwtLoginCredentials.getTrustKey());
            usernamePasswordCredentials.setTrustMe(trustReq);

            // Cleanup any previous session
            cleanupSession();

            // Login
            AUTHENTICATION_SERVICE.login(usernamePasswordCredentials);

            // Populate Session
            return establishSession();
        } catch (Throwable t) {
            internalLogout();

            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }

    @Override
    public GwtSession login(GwtJwtCredential gwtAccessTokenCredentials, GwtJwtIdToken gwtJwtIdToken) throws GwtKapuaException {

        try {
            // Check Credentials Values
            ArgumentValidator.notNull(gwtAccessTokenCredentials, "loginCredentials");
            ArgumentValidator.notEmptyOrNull(gwtAccessTokenCredentials.getAccessToken(), "loginCredentials.accessToken");
            ArgumentValidator.notNull(gwtJwtIdToken, "jwtIdToken");
            ArgumentValidator.notEmptyOrNull(gwtJwtIdToken.getIdToken(), "jwtIdToken.idToken");

            // Parse Credentials
            JwtCredentials jwtCredentials = new JwtCredentials(gwtAccessTokenCredentials.getAccessToken(), gwtJwtIdToken.getIdToken());

            // Cleanup any previous session
            cleanupSession();

            // Login and check account auto-creation
            try {
                AUTHENTICATION_SERVICE.login(jwtCredentials);
            } catch (KapuaAuthenticationException e) {
                handleLoginError(jwtCredentials, e);
            }

            // Populate Session
            return establishSession();
        } catch (Throwable t) {
            internalLogout();

            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }

    /**
     * Invalidates the {@link HttpSession} if it is not new.
     * <p>
     * This prevents Session Fixation vulnerability.
     *
     * @since 1.5.0
     */
    private void cleanupSession() {
        SecurityUtils.getSubject().logout();

        // Invalidate old sessions
        HttpServletRequest request = getThreadLocalRequest();
        HttpSession session = request.getSession();
        if (!session.isNew()) {
            session.invalidate();
        }

        request.getSession(true);
    }

    private void handleLoginError(JwtCredentials credentials, KapuaAuthenticationException e) throws KapuaException {
        LOG.debug("Handling error code: {}", e.getCode());

        if (!isAccountCreationEnabled()) {
            LOG.debug("Account creation is not active");
            throw e;
        }

        if (e.getCode().equals(KapuaAuthenticationErrorCodes.UNKNOWN_LOGIN_CREDENTIAL)) {
            try {
                LOG.info("Trying auto account creation");
                if (REGISTRATION_SERVICE.createAccount(credentials)) {
                    LOG.info("Created new account");
                    AUTHENTICATION_SERVICE.login(credentials);
                } else {
                    LOG.info("New account did not get created");
                    throw e; // throw the original error
                }
            } catch (Exception e1) {
                LOG.warn("Failed to auto-create account", e1);
                throw e; // we throw the original error instead
            }
        } else {
            // it is an exception we can't handle by auto-account-creation
            throw e;
        }
    }

    private boolean isAccountCreationEnabled() {
        return REGISTRATION_SERVICE.isAccountCreationEnabled();
    }

    /**
     * Return the currently authenticated user or null if no session has been established.
     */
    @Override
    public GwtSession getCurrentSession() throws GwtKapuaException {

        GwtSession gwtSession = null;
        try {
            Subject currentUser = SecurityUtils.getSubject();
            if (currentUser != null && currentUser.isAuthenticated()) {

                Session session = currentUser.getSession();
                gwtSession = (GwtSession) session.getAttribute(SESSION_CURRENT);

                // Store the user information in the sessions
                String username = ((User) currentUser.getPrincipal()).getName();

                // get the session
                if (gwtSession == null) {
                    gwtSession = establishSession();
                } else {
                    User user = USER_SERVICE.findByName(username);
                    gwtSession.setUserId(user.getId().toCompactId());
                }
            }
        } catch (Throwable t) {
            LOG.warn("Error in getCurrentSession.", t);
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }

        return gwtSession;
    }

    private GwtSession establishSession() throws KapuaException {
        // Get info from session
        final KapuaSession kapuaSession = KapuaSecurityUtils.getSession();
        LOG.debug("Kapua session: {}", kapuaSession);
        // Get user info
        LOG.debug("Looking up - scopeId: {}, userId: {}", kapuaSession.getScopeId(), kapuaSession.getUserId());
        final User user = KapuaSecurityUtils.doPrivileged(new Callable<User>() {

            @Override
            public User call() throws Exception {
                return USER_SERVICE.find(kapuaSession.getScopeId(), kapuaSession.getUserId());
            }
        });
        // Get account info
        Account account = KapuaSecurityUtils.doPrivileged(new Callable<Account>() {

            @Override
            public Account call() throws Exception {
                return ACCOUNT_SERVICE.find(kapuaSession.getScopeId());
            }
        });
        // Convert entities
        GwtUser gwtUser = KapuaGwtUserModelConverter.convertUser(user);
        GwtAccount gwtAccount = KapuaGwtAccountModelConverter.convertAccount(account);
        // Build the session
        final GwtSession gwtSession = new GwtSession();

        // Console info
        SystemSetting commonsConfig = SystemSetting.getInstance();

        gwtSession.setVersion(commonsConfig.getString(SystemSettingKey.VERSION));
        gwtSession.setBuildVersion(commonsConfig.getString(SystemSettingKey.BUILD_REVISION));
        gwtSession.setBuildNumber(commonsConfig.getString(SystemSettingKey.BUILD_NUMBER));
        gwtSession.setSsoEnabled(ConsoleSsoLocator.getLocator(this).getService().isEnabled());
        gwtSession.setDatastoreDisabled(DATASTORE_SETTINGS.getBoolean(DatastoreSettingsKey.DISABLE_DATASTORE, false));

        // Account Info
        gwtSession.setAccountId(gwtAccount.getId());
        gwtSession.setAccountName(gwtAccount.getName());
        gwtSession.setAccountPath(gwtAccount.getParentAccountPath());
        // Following 2 have been commented after deprecation of GwtSession.getRootAccount* methods
        // gwtSession.setRootAccountId(gwtAccount.getId());
        // gwtSession.setRootAccountName(gwtAccount.getName());

        // Selected Account info
        gwtSession.setSelectedAccountId(gwtAccount.getId());
        gwtSession.setSelectedAccountName(gwtAccount.getName());
        gwtSession.setSelectedAccountPath(gwtAccount.getParentAccountPath());

        // User info
        gwtSession.setUserId(gwtUser.getId());
        gwtSession.setUserName(gwtUser.getUsername());
        gwtSession.setUserDisplayName(gwtUser.getDisplayName());
        gwtSession.setTokenId(kapuaSession.getAccessToken().getTokenId());
        gwtSession.setOpenIDIdToken(kapuaSession.getOpenIDidToken());

        // Setting Mfa trust key
        String trustKey = kapuaSession.getAccessToken().getTrustKey();
        if (trustKey != null && !trustKey.isEmpty()) {
            gwtSession.setTrustKey(kapuaSession.getAccessToken().getTrustKey());
        }
        // Load permissions
        KapuaSecurityUtils.doPrivileged(new ThrowingRunnable() {

            @Override
            public void run() throws Exception {
                AccessInfo userAccessInfo = ACCESS_INFO_SERVICE.findByUserId(user.getScopeId(), user.getId());

                if (userAccessInfo != null) {

                    // Permission info
                    AccessPermissionListResult accessPermissions = ACCESS_PERMISSION_SERVICE.findByAccessInfoId(userAccessInfo.getScopeId(), userAccessInfo.getId());
                    for (AccessPermission ap : accessPermissions.getItems()) {
                        gwtSession.addSessionPermission(convert(ap.getPermission()));
                    }

                    // Role info
                    AccessRoleListResult accessRoles = ACCESS_ROLE_SERVICE.findByAccessInfoId(userAccessInfo.getScopeId(), userAccessInfo.getId());

                    for (AccessRole ar : accessRoles.getItems()) {
                        Role role = ROLE_SERVICE.find(ar.getScopeId(), ar.getRoleId());

                        RolePermissionListResult rolePermissions = ROLE_PERMISSION_SERVICE.findByRoleId(role.getScopeId(), role.getId());
                        for (RolePermission rp : rolePermissions.getItems()) {
                            gwtSession.addSessionPermission(convert(rp.getPermission()));
                        }
                    }
                }
            }
        });
        // Return session
        return gwtSession;
    }

    /**
     * Logout call in response to the logout link/button.
     */
    @Override
    public void logout() throws GwtKapuaException {
        try {
            // Setting user initiated
            Subject shiroSubject = SecurityUtils.getSubject();
            KapuaSession kapuaSession = (KapuaSession) shiroSubject.getSession().getAttribute(KapuaSession.KAPUA_SESSION_KEY);
            kapuaSession.setUserInitiatedLogout(true);

            // Actual logout
            internalLogout();
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }

    /**
     * Internal logout, also used in case of exceptions.
     */
    private void internalLogout() throws GwtKapuaException {
        try {
            // Logout
            AUTHENTICATION_SERVICE.logout();

            // Invalidate http session
            HttpServletRequest request = getThreadLocalRequest();
            request.getSession().invalidate();
        } catch (Throwable throwable) {
            throw KapuaExceptionHandler.buildExceptionFromError(throwable);
        }
    }

    private GwtSessionPermission convert(Permission permission) {
        // Domain parsing
        String domain = permission.getDomain();

        // Action parsing
        GwtSessionPermissionAction action = convert(permission.getAction());

        // Target scope parsing
        GwtSessionPermissionScope gwtSessionPermissionScope;
        if (permission.getTargetScopeId() == null) {
            gwtSessionPermissionScope = GwtSessionPermissionScope.ALL;
        } else if (permission.getForwardable()) {
            gwtSessionPermissionScope = GwtSessionPermissionScope.CHILDREN;
        } else {
            gwtSessionPermissionScope = GwtSessionPermissionScope.SELF;
        }

        // Create converted object
        return new GwtSessionPermission(domain, action, gwtSessionPermissionScope);
    }

    private GwtSessionPermissionAction convert(Actions action) {
        return action != null ? GwtSessionPermissionAction.valueOf(action.name()) : null;
    }

}
