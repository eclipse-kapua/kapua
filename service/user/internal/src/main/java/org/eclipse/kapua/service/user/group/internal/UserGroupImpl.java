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
package org.eclipse.kapua.service.user.group.internal;

import org.eclipse.kapua.commons.model.AbstractKapuaNamedEntity;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.user.group.UserGroup;

import java.util.HashSet;
import java.util.Set;

/**
 * {@link UserGroup} implementation.
 *
 * @since 2.1.0
 */
public class UserGroupImpl extends AbstractKapuaNamedEntity implements UserGroup {

    private static final long serialVersionUID = -3760818776351242930L;

    private Set<KapuaId> tagIds;


    /**
     * Constructor.
     *
     * @param scopeId the scope {@link KapuaId}
     * @since 1.0.0
     */
    public UserGroupImpl(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * Clone constructor.
     *
     * @param userGroup the {@link Group} to clone.
     * @since 1.0.0
     */
    public UserGroupImpl(UserGroup userGroup) {
        super(userGroup);

        setTagIds(userGroup.getTagIds());
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

    /**
     * Parse a {@link UserGroup} instance into a {@link UserGroupImpl}.
     *
     * @param userGroup The {@link UserGroup} instance to parse
     * @return The parsed {@link UserGroupImpl} instance.
     * @since 2.1.0
     */
    public static UserGroupImpl parse(UserGroup userGroup) {
        if (userGroup == null) {
            return null;
        }

        return userGroup instanceof UserGroupImpl ?
                (UserGroupImpl) userGroup :
                new UserGroupImpl(userGroup);
    }
}
