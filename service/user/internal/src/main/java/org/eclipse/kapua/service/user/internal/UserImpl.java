/*******************************************************************************
 * Copyright (c) 2016, 2025 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.commons.model.AbstractKapuaNamedEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserStatus;
import org.eclipse.kapua.service.user.UserType;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * {@link User} implementation.
 *
 * @since 1.0.0
 */
@Entity(name = "User")
@Table(name = "usr_user")
public class UserImpl extends AbstractKapuaNamedEntity implements User {

    private static final long serialVersionUID = 4029650117581681503L;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @Basic
    @Column(name = "display_name")
    private String displayName;

    @Basic
    @Column(name = "email")
    private String email;

    @Basic
    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", updatable = false, nullable = false)
    private UserType userType;

    @Basic
    @Column(name = "external_id")
    private String externalId;

    @Basic
    @Column(name = "external_username")
    private String externalUsername;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiration_date")
    private Date expirationDate;

    /**
     * Constructor.
     * <p>
     * Required by JPA.
     *
     * @since 1.0.0
     */
    public UserImpl() {
        this(null, null);
    }

    /**
     * Constructor.
     *
     * @param scopeId The scope {@link KapuaId} to set into the {@link User}
     * @since 1.0.0
     */
    public UserImpl(KapuaId scopeId) {
        this(scopeId, null);
    }

    /**
     * Constructor.
     *
     * @param scopeId The scope {@link KapuaId} to set into the {@link User}
     * @param name    The name to set into the {@link User}
     * @since 1.0.0
     */
    public UserImpl(KapuaId scopeId, String name) {
        super(scopeId, name);

        this.status = UserStatus.ENABLED;
        this.userType = UserType.INTERNAL;
    }

    /**
     * Clone constructor.
     *
     * @param user The {@link User} to clone.
     * @since 1.1.0
     */
    public UserImpl(User user) {
        super(user);

        setStatus(user.getStatus());
        setDisplayName(user.getDisplayName());
        setEmail(user.getEmail());
        setPhoneNumber(user.getPhoneNumber());
        setUserType(user.getUserType());
        setExternalId(user.getExternalId());
        setExternalUsername(user.getExternalUsername());
        setExpirationDate(user.getExpirationDate());
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
