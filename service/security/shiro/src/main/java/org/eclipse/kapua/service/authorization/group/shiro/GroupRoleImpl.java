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
package org.eclipse.kapua.service.authorization.group.shiro;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.AbstractKapuaEntity;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.group.GroupRole;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * {@link GroupRole} implementation.
 *
 * @since 2.1.0
 */
@Entity(name = "GroupRole")
@Table(name = "athz_group_role")
public class GroupRoleImpl extends AbstractKapuaEntity implements GroupRole {

    private static final long serialVersionUID = 8400951097610833058L;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "eid", column = @Column(name = "group_id"))
    })
    private KapuaEid groupId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "eid", column = @Column(name = "role_id"))
    })
    private KapuaEid roleId;

    /**
     * Empty constructor required by JPA.
     *
     * @since 1.0.0
     */
    protected GroupRoleImpl() {
        super();
    }

    /**
     * Constructor.
     *
     * @param scopeId The scope {@link KapuaId} to set for this {@link GroupRole}.
     * @since 1.0.0
     */
    public GroupRoleImpl(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * Clone constructor.
     *
     * @param groupRole The {@link GroupRole} to clone.
     * @throws KapuaException If the given {@link GroupRole} is incompatible with the implementation-specific type.
     * @since 1.0.0
     */
    public GroupRoleImpl(GroupRole groupRole) throws KapuaException {
        super(groupRole);

        setGroupId(groupRole.getGroupId());
        setRoleId(groupRole.getRoleId());
    }

    @Override
    public void setGroupId(KapuaId groupInfoId) {
        this.groupId = KapuaEid.parseKapuaId(groupInfoId);
    }

    @Override
    public KapuaId getGroupId() {
        return groupId;
    }

    @Override
    public void setRoleId(KapuaId roleId) {
        this.roleId = KapuaEid.parseKapuaId(roleId);
    }

    @Override
    public KapuaId getRoleId() {
        return roleId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (groupId == null ? 0 : groupId.hashCode());
        result = prime * result + (roleId == null ? 0 : roleId.hashCode());
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
        GroupRoleImpl other = (GroupRoleImpl) obj;
        if (groupId == null) {
            if (other.groupId != null) {
                return false;
            }
        } else if (!groupId.equals(other.groupId)) {
            return false;
        }
        if (roleId == null) {
            if (other.roleId != null) {
                return false;
            }
        } else if (!roleId.equals(other.roleId)) {
            return false;
        }
        return true;
    }
}
