/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.user.internal;

import org.eclipse.kapua.commons.model.AbstractKapuaNamedEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserStatus;
import org.eclipse.kapua.service.user.UserType;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * {@link UserCreator} implementation.
 *
 * @since 1.0.0
 */
public class UserCreatorImpl extends AbstractKapuaNamedEntityCreator<User> implements UserCreator {

    private static final long serialVersionUID = 4664940282892151008L;

    private Set<KapuaId> groupIds;
    private Set<KapuaId> tagIds;

    private UserStatus status;
    private String displayName;
    private String email;
    private String phoneNumber;
    private UserType userType = UserType.INTERNAL;
    private String externalId;
    private String externalUsername;
    private Date expirationDate;

    /**
     * Constructor
     *
     * @param accountId
     * @param name
     */
    public UserCreatorImpl(KapuaId accountId, String name) {
        super(accountId, name);

        setStatus(UserStatus.ENABLED);
        setUserType(UserType.INTERNAL);
    }

    @Override
    public Set<KapuaId> getGroupIds() {
        if (groupIds == null) {
            groupIds = Collections.emptySet();
        }

        return groupIds;
    }

    @Override
    public void setGroupIds(Set<KapuaId> groupIds) {
        this.groupIds = groupIds;
    }

    public UserCreatorImpl(KapuaId scopeId) {
        this(scopeId, null);
    }

    @Override
    public Set<KapuaId> getTagIds() {
        if (tagIds == null) {
            tagIds = Collections.emptySet();
        }

        return tagIds;
    }

    @Override
    public void setTagIds(Set<KapuaId> tagIds) {
        this.tagIds = tagIds;
    }

    @Override
    public UserStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(UserStatus status) {
        this.status = status;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public UserType getUserType() {
        return userType;
    }

    @Override
    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    @Override
    public String getExternalId() {
        return externalId;
    }

    @Override
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public String getExternalUsername() {
        return externalUsername;
    }

    @Override
    public void setExternalUsername(String externalUsername) {
        this.externalUsername = externalUsername;
    }

    @Override
    public Date getExpirationDate() {
        return expirationDate;
    }

    @Override
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
