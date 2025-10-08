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
package org.eclipse.kapua.app.console.module.user.shared.model;

import org.eclipse.kapua.app.console.module.user.shared.model.GwtUser.GwtUserStatus;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUser.GwtUserType;

import java.io.Serializable;
import java.util.Date;

public class GwtUserCreator implements Serializable {

    private static final long serialVersionUID = -7786687388389046845L;

    private String scopeId;
    private String groupId;
    private String username;
    private String password;
    private String displayName;
    private String email;
    private String phoneNumber;
    private String permissions;
    private Date expirationDate;
    private GwtUserStatus userStatus;
    private String externalId;
    private String externalUsername;
    private GwtUserType userType;

    private boolean isAdministrator;

    public GwtUserCreator() {
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String accountId) {
        this.scopeId = accountId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public boolean isAdministrator() {
        return isAdministrator;
    }

    public void setAdministrator(boolean isAdministrator) {
        this.isAdministrator = isAdministrator;
    }

    public Date getExpirationDate() {
        return this.expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public GwtUserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(GwtUserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public void setUserStatus(String userStatus) {
        setUserStatus(GwtUserStatus.valueOf(userStatus));
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getExternalUsername() {
        return externalUsername;
    }

    public void setExternalUsername(String externalUsername) {
        this.externalUsername = externalUsername;
    }

    public GwtUserType getUserType() {
        return userType;
    }

    public void setGwtUserType(GwtUserType userType) {
        this.userType = userType;
    }
}
