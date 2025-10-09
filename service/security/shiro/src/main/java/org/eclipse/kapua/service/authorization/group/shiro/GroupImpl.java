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

import org.eclipse.kapua.commons.model.AbstractKapuaNamedEntity;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.group.Group;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * {@link Group} implementation.
 *
 * @since 1.0.0
 */
@Entity(name = "Group")
@Table(name = "athz_group")
public class GroupImpl extends AbstractKapuaNamedEntity implements Group {

    private static final long serialVersionUID = -3760818776351242930L;

    @ElementCollection
    @CollectionTable(name = "athz_group_tag", joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"))
    @AttributeOverrides({
            @AttributeOverride(name = "eid", column = @Column(name = "tag_id", nullable = false, updatable = false))
    })
    private Set<KapuaEid> tagIds;


    /**
     * Constructor.
     * <p>
     * Required by JPA.
     *
     * @since 1.1.0
     */
    public GroupImpl() {
        super();
    }

    /**
     * Constructor.
     *
     * @param scopeId the scope {@link KapuaId}
     * @since 1.0.0
     */
    public GroupImpl(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * Clone constructor.
     *
     * @param group the {@link Group} to clone.
     * @since 1.0.0
     */
    public GroupImpl(Group group) {
        super(group);

        setTagIds(group.getTagIds());
    }

    @Override
    public void setTagIds(Set<KapuaId> tagIds) {
        this.tagIds = new HashSet<>();

        for (KapuaId id : tagIds) {
            this.tagIds.add(KapuaEid.parseKapuaId(id));
        }
    }

    @Override
    public Set<KapuaId> getTagIds() {
        Set<KapuaId> tagIds = new HashSet<>();

        if (this.tagIds != null) {
            for (KapuaId deviceTagId : this.tagIds) {
                tagIds.add(new KapuaEid(deviceTagId));
            }
        }

        return tagIds;
    }
}
