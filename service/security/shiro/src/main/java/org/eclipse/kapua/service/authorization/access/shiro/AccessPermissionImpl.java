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
package org.eclipse.kapua.service.authorization.access.shiro;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.kapua.commons.model.AbstractKapuaEntity;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.shiro.PermissionImpl;

/**
 * {@link AccessPermission} implementation.
 *
 * @since 1.0.0
 */
@Entity(name = "AccessPermission")
@Table(name = "athz_access_permission")
public class AccessPermissionImpl extends AbstractKapuaEntity implements AccessPermission {

    private static final long serialVersionUID = -3760818776351242930L;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "eid", column = @Column(name = "access_info_id", nullable = false, updatable = false))
    })
    private KapuaEid accessInfoId;

    @Embedded
    private PermissionImpl permission;

    /**
     * Constructor
     *
     * @since 1.0.0
     */
    protected AccessPermissionImpl() {
        super();
    }

    /**
     * Constructor.
     *
     * @param scopeId
     *         The scope {@link KapuaId} to set into the {@link AccessPermission}
     * @since 1.0.0
     */
    public AccessPermissionImpl(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * Clone constructor
     *
     * @param accessPermission
     * @since 1.0.0
     */
    public AccessPermissionImpl(AccessPermission accessPermission) {
        super(accessPermission);

        setAccessInfoId(accessPermission.getAccessInfoId());
        setPermission(accessPermission.getPermission());
    }

    @Override
    public void setAccessInfoId(KapuaId accessInfoId) {
        this.accessInfoId = KapuaEid.parseKapuaId(accessInfoId);
    }

    @Override
    public KapuaId getAccessInfoId() {
        return accessInfoId;
    }

    @Override
    public void setPermission(Permission permission) {
        PermissionImpl permissionImpl = null;
        if (permission != null) {
            permissionImpl = new PermissionImpl(permission);
        }
        this.permission = permissionImpl;
    }

    @Override
    public Permission getPermission() {
        return permission != null
                ? new Permission(permission.getDomain(), permission.getAction(), permission.getTargetScopeId(), permission.getGroupId(), permission.getForwardable())
                : new Permission(null, null, null);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (accessInfoId == null ? 0 : accessInfoId.hashCode());
        result = prime * result + (permission == null ? 0 : permission.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AccessPermissionImpl other = (AccessPermissionImpl) obj;
        if (accessInfoId == null) {
            if (other.accessInfoId != null) {
                return false;
            }
        } else if (!accessInfoId.equals(other.accessInfoId)) {
            return false;
        }

        return getPermission().equals(other.getPermission());
    }
}
