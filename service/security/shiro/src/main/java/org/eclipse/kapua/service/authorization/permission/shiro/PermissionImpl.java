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
package org.eclipse.kapua.service.authorization.permission.shiro;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.permission.Permission;

/**
 * {@link Permission} implementation.
 *
 * @since 1.0.0
 */
@Embeddable
public class PermissionImpl
        //        extends
        //        WildcardPermission
        implements
        //        , org.apache.shiro.authz.Permission
        Serializable {

    private static final long serialVersionUID = 1480557438886065675L;

    //
    //    //TODO: FIXME: REMOVE: A service in a jpa class? Behaviour should not be part of a data class!
    //    @Transient
    //    private final AccountService accountService = KapuaLocator.getInstance().getService(AccountService.class);
    //    //TODO: FIXME: REMOVE: A service in a jpa class? Behaviour should not be part of a data class!
    //    @Transient
    //    private final DomainRegistryService domainService = KapuaLocator.getInstance().getService(DomainRegistryService.class);

    @Basic
    @Column(name = "domain", nullable = true, updatable = false)
    private String domain;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = true, updatable = false)
    private Actions action;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "eid", column = @Column(name = "target_scope_id", nullable = true, updatable = false))
    })
    private KapuaEid targetScopeId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "eid", column = @Column(name = "group_id", nullable = true, updatable = false))
    })
    private KapuaEid groupId;

    @Basic
    @Column(name = "forwardable", nullable = false, updatable = false)
    private boolean forwardable;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    protected PermissionImpl() {
        super();
    }

    /**
     * Constructor.
     *
     * @param permission
     *         The {@link Permission} to parse.
     * @since 1.0.0
     */
    public PermissionImpl(Permission permission) {
        this(permission.getDomain(),
                permission.getAction(),
                permission.getTargetScopeId(),
                permission.getGroupId(),
                permission.getForwardable());
    }

    /**
     * Constructor.
     *
     * @param domain
     *         The {@link Permission#getDomain()}.
     * @param action
     *         The {@link Permission#getAction()}.
     * @param targetScopeId
     *         The {@link Permission#getTargetScopeId()}.
     * @param groupId
     *         The {@link Permission#getGroupId()}.
     * @since 1.0.0
     */
    public PermissionImpl(String domain, Actions action, KapuaId targetScopeId, KapuaId groupId) {
        this(domain, action, targetScopeId, groupId, false);
    }

    /**
     * Constructor.
     *
     * @param domain
     *         The {@link Permission#getDomain()}.
     * @param action
     *         The {@link Permission#getAction()}.
     * @param targetScopeId
     *         The {@link Permission#getTargetScopeId()}.
     * @param groupId
     *         The {@link Permission#getGroupId()}.
     * @param forwardable
     *         Whether the {@link Permission} is {@link Permission#getForwardable()}
     * @since 1.0.0
     */
    public PermissionImpl(String domain, Actions action, KapuaId targetScopeId, KapuaId groupId, boolean forwardable) {

        setDomain(domain);
        setAction(action);
        setTargetScopeId(targetScopeId);
        setGroupId(groupId);
        setForwardable(forwardable);

    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }

    public void setAction(Actions action) {
        this.action = action;
    }

    public Actions getAction() {
        return action;
    }

    public void setTargetScopeId(KapuaId targetScopeId) {
        this.targetScopeId = KapuaEid.parseKapuaId(targetScopeId);
    }

    public KapuaId getTargetScopeId() {
        return targetScopeId;
    }

    public void setGroupId(KapuaId groupId) {
        this.groupId = KapuaEid.parseKapuaId(groupId);
    }

    public KapuaId getGroupId() {
        return groupId;
    }

    public boolean getForwardable() {
        return forwardable;
    }

    public void setForwardable(boolean forwardable) {
        this.forwardable = forwardable;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(domain != null ? domain : Permission.WILDCARD)
                .append(Permission.SEPARATOR)
                .append(action != null ? action.name() : Permission.WILDCARD)
                .append(Permission.SEPARATOR)
                .append(targetScopeId != null ? targetScopeId.getId() : Permission.WILDCARD)
                .append(Permission.SEPARATOR)
                .append(groupId != null ? groupId.getId() : Permission.WILDCARD);

        return sb.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + (action == null ? 0 : action.hashCode());
        result = prime * result + (domain == null ? 0 : domain.hashCode());
        result = prime * result + (targetScopeId == null ? 0 : targetScopeId.hashCode());
        result = prime * result + (groupId == null ? 0 : groupId.hashCode());
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
        PermissionImpl other = (PermissionImpl) obj;
        if (action != other.action) {
            return false;
        }
        if (domain == null) {
            if (other.domain != null) {
                return false;
            }
        } else if (!domain.equals(other.domain)) {
            return false;
        }
        if (targetScopeId == null) {
            if (other.targetScopeId != null) {
                return false;
            }
        } else if (!targetScopeId.equals(other.targetScopeId)) {
            return false;
        }
        if (groupId == null) {
            return other.groupId == null;
        } else {
            return groupId.equals(other.groupId);
        }
    }
}
