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
package org.eclipse.kapua.service.authorization.group.shiro;

import org.eclipse.kapua.commons.model.AbstractKapuaEntity;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.group.GroupPermission;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.shiro.PermissionImpl;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * {@link GroupPermission} implementation.
 *
 * @since 2.1.0
 */
@Entity(name = "GroupPermission")
@Table(name = "athz_group_permission")
public class GroupPermissionImpl extends AbstractKapuaEntity implements GroupPermission {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "eid", column = @Column(name = "owner_group_id", nullable = false, updatable = false))
    })
    private KapuaEid groupId;

    @Embedded
    private PermissionImpl permission;

    /**
     * Constructor
     *
     * @since 2.1.0
     */
    protected GroupPermissionImpl() {
        super();
    }

    /**
     * Constructor.
     *
     * @param scopeId The scope {@link KapuaId} to set into the {@link AccessPermission}
     * @since 2.1.0
     */
    public GroupPermissionImpl(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * Clone constructor
     *
     * @param groupPermission
     * @since 2.1.0
     */
    public GroupPermissionImpl(GroupPermission groupPermission) {
        super(groupPermission);

        setGroupId(groupPermission.getGroupId());
        setPermission(groupPermission.getPermission());
    }

    @Override
    public void setGroupId(KapuaId groupId) {
        this.groupId = KapuaEid.parseKapuaId(groupId);
    }

    @Override
    public KapuaId getGroupId() {
        return groupId;
    }

    @Override
    public void setPermission(Permission permission) {
        PermissionImpl permissionImpl = null;
        if (permission != null) {
            permissionImpl = permission instanceof PermissionImpl ? (PermissionImpl) permission : new PermissionImpl(permission);
        }
        this.permission = permissionImpl;
    }

    @Override
    public Permission getPermission() {
        return permission != null ? permission : new PermissionImpl(null, null, null, null);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (groupId == null ? 0 : groupId.hashCode());
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
        GroupPermissionImpl other = (GroupPermissionImpl) obj;
        if (groupId == null) {
            if (other.groupId != null) {
                return false;
            }
        } else if (!groupId.equals(other.groupId)) {
            return false;
        }

        return getPermission().equals(other.getPermission());
    }
}
