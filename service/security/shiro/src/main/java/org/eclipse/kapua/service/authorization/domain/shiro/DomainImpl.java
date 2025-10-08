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
package org.eclipse.kapua.service.authorization.domain.shiro;

import org.eclipse.kapua.commons.model.AbstractKapuaEntity;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.domain.Domain;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.util.Objects;
import java.util.Set;

/**
 * {@link Domain} implementation.
 *
 * @since 1.0.0
 */
@Entity(name = "Domain")
@Table(name = "athz_domain")
public class DomainImpl extends AbstractKapuaEntity implements Domain {

    private static final long serialVersionUID = 3878607249074632729L;

    @Basic
    @Column(name = "name", nullable = false, updatable = false)
    private String name;

    /**
     * @deprecated since 2.0.0 - This will be removed in future versions as it is not used
     */
    @Deprecated
    @Basic
    @Column(name = "serviceName", nullable = false, updatable = false)
    private String serviceName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "athz_domain_actions", joinColumns = @JoinColumn(name = "domain_id", referencedColumnName = "id"))
    @Column(name = "action", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Actions> actions;

    @Basic
    @Column(name = "groupable", nullable = false, updatable = true)
    private boolean groupable;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    public DomainImpl() {
        super();
    }

    /**
     * Constructor
     *
     * @param scopeId the scope KapuaId
     * @since 1.0.0
     */
    public DomainImpl(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * Clone constructor.
     *
     * @param domain
     * @since 1.1.0
     */
    public DomainImpl(Domain domain) {
        super(domain);

        setName(domain.getName());
        setServiceName(domain.getServiceName());
        setActions(domain.getActions());
        setGroupable(domain.getGroupable());
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @deprecated since 2.0.0 - This will be removed in future versions as it is not used
     */
    @Deprecated
    @Override
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * @deprecated since 2.0.0 - This will be removed in future versions as it is not used
     */
    @Deprecated
    @Override
    public String getServiceName() {
        return this.serviceName;
    }

    @Override
    public Set<Actions> getActions() {
        return actions;
    }

    @Override
    public void setGroupable(boolean groupable) {
        this.groupable = groupable;
    }

    @Override
    public boolean getGroupable() {
        return groupable;
    }

    @Override
    public void setActions(Set<Actions> actions) {
        this.actions = actions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DomainImpl domain = (DomainImpl) o;
        return groupable == domain.groupable &&
                Objects.equals(name, domain.name) &&
                Objects.equals(actions, domain.actions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, actions, groupable);
    }

    @Override
    public String toString() {
        return "DomainImpl{" +
                "scopeId=" + scopeId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", createdOn=" + createdOn +
                ", createdBy=" + createdBy +
                ", actions=" + actions +
                ", groupable=" + groupable +
                ", serviceName='" + serviceName + '\'' +
                '}';
    }
}
