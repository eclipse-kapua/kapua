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
package org.eclipse.kapua.app.console.module.api.shared.model.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.kapua.app.console.module.api.shared.model.KapuaBaseModel;

public class GwtSession extends KapuaBaseModel implements Serializable {

    private static final long serialVersionUID = -4511854889803351914L;

    // Console info
    private String version;
    private String buildVersion;
    private String buildNumber;
    private boolean ssoEnabled;
    private boolean datastoreDisabled;

    // Account info
    private String accountId;
    private String accountName;
    private String accountPath;

    /**
     * @deprecated Since 2.0.0. This is very misleading since it seems that is a reference to the kapua-sys account (aka root account) but is not.
     */
    @Deprecated
    private String rootAccountId;
    /**
     * @deprecated Since 2.0.0. This is very misleading since it seems that is a reference to the kapua-sys account (aka root account) but is not.
     */
    @Deprecated
    private String rootAccountName;

    // Selected Account info
    private String selectedAccountId;
    private String selectedAccountName;
    private String selectedAccountPath;

    // User info
    private String userId;
    private String userName;
    private String userDisplayName;
    private String tokenId;
    private String trustKey;
    private String openIDIdToken;

    // Permission info
    private List<GwtSessionPermission> sessionPermissions = new ArrayList<GwtSessionPermission>();
    private Map<GwtSessionPermission, Boolean> checkedPermissionsCache = new HashMap<GwtSessionPermission, Boolean>();

    // UI
    /**
     * Is UI form dirty and needs save.
     * <p>
     * This field is set by UI to offer "Do you want to discard unsaved changes?" feature.
     *
     * @since 1.0.0
     */
    private boolean formDirty;

    public GwtSession() {
    }

    // Console info
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public boolean isSsoEnabled() {
        return ssoEnabled;
    }

    public void setSsoEnabled(boolean ssoEnabled) {
        this.ssoEnabled = ssoEnabled;
    }

    public boolean isDatastoreDisabled() {
        return datastoreDisabled;
    }

    public void setDatastoreDisabled(boolean datastoreDisabled) {
        this.datastoreDisabled = datastoreDisabled;
    }

    // Account info
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String name) {
        this.accountName = name;
    }

    public String getAccountPath() {
        return accountPath;
    }

    public void setAccountPath(String accountPath) {
        this.accountPath = accountPath;
    }

    /**
     * @deprecated Since 2.0.0. This is very misleading since it seems that is a reference to the kapua-sys account (aka root account) but is not.
     */
    @Deprecated
    public String getRootAccountId() {
        return rootAccountId;
    }

    /**
     * @deprecated Since 2.0.0. This is very misleading since it seems that is a reference to the kapua-sys account (aka root account) but is not.
     */
    @Deprecated
    public void setRootAccountId(String rootAccountId) {
        this.rootAccountId = rootAccountId;
    }

    /**
     * @deprecated Since 2.0.0. This is very misleading since it seems that is a reference to the kapua-sys account (aka root account) but is not.
     */
    @Deprecated
    public String getRootAccountName() {
        return rootAccountName;
    }

    /**
     * @deprecated Since 2.0.0. This is very misleading since it seems that is a reference to the kapua-sys account (aka root account) but is not.
     */
    @Deprecated
    public void setRootAccountName(String rootAccountName) {
        this.rootAccountName = rootAccountName;
    }

    // Selected Account info
    public String getSelectedAccountId() {
        return selectedAccountId;
    }

    public void setSelectedAccountId(String selectedAccountId) {
        this.selectedAccountId = selectedAccountId;
    }

    public String getSelectedAccountName() {
        return selectedAccountName;
    }

    public void setSelectedAccountName(String selectedAccountName) {
        this.selectedAccountName = selectedAccountName;
    }

    public String getSelectedAccountPath() {
        return selectedAccountPath;
    }

    public void setSelectedAccountPath(String selectedAccountPath) {
        this.selectedAccountPath = selectedAccountPath;
    }

    /**
     * Checks if the selected Account is at root level (it means it is kapua-sys)
     *
     * @return {@code true} if it is, {@code false} otherwise
     * @since 2.0.0
     */
    public boolean isSelectedAccountRootLevel() {
        return isSelectedAccountAtLevel(0);
    }

    /**
     * Checks if the selected Account is a first level (it means it is a direct son of kapua-sys)
     *
     * @return {@code true} if it is, {@code false} otherwise
     * @since 2.0.0
     */
    public boolean isSelectedAccountFirstLevel() {
        return isSelectedAccountAtLevel(1);
    }

    /**
     * Checks if the selected Account is the given level.
     *
     * <ul>
     *     <li>/1 = level 0</li>
     *     <li>/1/1234 = level 1</li>
     * </ul>
     *
     * @param level
     *         The level to check against
     * @return {@code true} if it is, {@code false} otherwise
     * @since 2.0.0
     */
    public boolean isSelectedAccountAtLevel(int level) {
        return getSelectedAccountPath().split("/").length == (level + 2);
    }

    // User info
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getTrustKey() {
        return trustKey;
    }

    public void setTrustKey(String trustKey) {
        this.trustKey = trustKey;
    }

    public String getOpenIDIdToken() {
        return openIDIdToken;
    }

    public void setOpenIDIdToken(String openIDIdToken) {
        this.openIDIdToken = openIDIdToken;
    }

    // Permission info
    public List<GwtSessionPermission> getSessionPermissions() {
        if (sessionPermissions == null) {
            sessionPermissions = new ArrayList<GwtSessionPermission>();
        }

        return sessionPermissions;
    }

    public void setSessionPermissions(List<GwtSessionPermission> sessionPermissions) {
        this.sessionPermissions = sessionPermissions;
    }

    public void addSessionPermission(GwtSessionPermission permission) {
        getSessionPermissions().add(permission);
    }

    /**
     * Checks that the current {@link GwtSession} has the given session. This methods uses {@link #hasPermission(GwtSessionPermission)} instantiating the actual {@link GwtSessionPermission}.
     *
     * @param domain
     *         The domain to check
     * @param action
     *         The {@link GwtSessionPermissionAction} to check
     * @param targetScope
     *         The {@link GwtSessionPermissionScope} to check
     * @return {@code true} if the current {@link GwtSession} has the permission, {@code false} otherwise
     * @since 1.0.0
     */
    public boolean hasPermission(String domain, GwtSessionPermissionAction action, GwtSessionPermissionScope targetScope) {
        return hasPermission(new GwtSessionPermission(domain, action, targetScope));
    }

    /**
     * Checks that the current {@link GwtSession} has the given {@link GwtSessionPermission}.
     * <p>
     * This check is done simulating the permission check performed by the {@link org.eclipse.kapua.service.authorization.AuthorizationService#isPermitted(Permission)}. This does not introduces any
     * security risk, since it will only allow to see/have access to certain elements of the UI while service access check is still performed on each call.
     * <p>
     * After the check, the result is cached to allow faster check for subsequent check for the same permission.
     * </p>
     *
     * @param permissionToCheck
     *         The {@link GwtSessionPermission} to check
     * @return {@code true} if the current {@link GwtSession} has the permission, {@code false} otherwise
     * @since 1.0.0
     */
    public boolean hasPermission(GwtSessionPermission permissionToCheck) {
        // Check cache
        Boolean cachedResult = checkedPermissionsCache.get(permissionToCheck);
        if (cachedResult != null) {
            return cachedResult;
        }

        // Check permission
        boolean permitted = isPermitted(permissionToCheck);

        // Set cached value
        checkedPermissionsCache.put(permissionToCheck, permitted);

        // Return it
        return permitted;
    }

    /**
     * This methods simulates the check that is performed by the {@link org.eclipse.kapua.service.authorization.AuthorizationService#isPermitted(Permission)}. {@link Permission#getForwardable()}
     * property is supported in a different way, but produces the same results.
     *
     * @param permissionToCheck
     *         The {@link GwtSessionPermission} to check
     * @return {@code true} if the current {@link GwtSession} has the permission, {@code false} otherwise
     * @since 1.0.0
     */
    private boolean isPermitted(GwtSessionPermission permissionToCheck) {

        for (GwtSessionPermission gsp : getSessionPermissions()) {
            if (gsp.getDomain() == null || gsp.getDomain().equals(permissionToCheck.getDomain())) {
                if (gsp.getAction() == null || gsp.getAction().equals(permissionToCheck.getAction())) {

                    GwtSessionPermissionScope permissionToCheckScope = permissionToCheck.getPermissionScope();

                    boolean check = false;
                    switch (gsp.getPermissionScope()) {
                    case ALL:
                        check = true;
                        break;
                    case CHILDREN:
                        check = (GwtSessionPermissionScope.CHILDREN.equals(permissionToCheckScope) ||
                                GwtSessionPermissionScope.SELF.equals(permissionToCheckScope));
                        break;
                    case SELF:
                        check = GwtSessionPermissionScope.SELF.equals(permissionToCheckScope);
                        break;
                    }

                    if (check) {
                        return check;
                    }
                }
            }
        }

        return false;
    }

    // UI

    /**
     * Is UI form dirty and needs confirmation to switch menu.
     *
     * @return true if user needs to confirm menu change.
     */
    public boolean isFormDirty() {
        return formDirty;
    }

    /**
     * Set user interface into dirty state.
     *
     * @param formDirty
     *         true if user will need to confirm menu change.
     */
    public void setFormDirty(boolean formDirty) {
        this.formDirty = formDirty;
    }

}
