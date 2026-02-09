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
package org.eclipse.kapua.service.device.registry.group.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.kapua.commons.model.AbstractKapuaNamedEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.registry.group.DeviceGroup;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupCreator;

/**
 * {@link DeviceGroupCreator} implementation.
 *
 * @since 2.1.0
 */
public class DeviceGroupCreatorImpl extends AbstractKapuaNamedEntityCreator<DeviceGroup> implements DeviceGroupCreator {

    private static final long serialVersionUID = 2736033455537233881L;

    private Set<KapuaId> tagIds;

    /**
     * Constructor.
     *
     * @param scopeId The {@link #getScopeId()}
     * @since 2.1.0
     */
    public DeviceGroupCreatorImpl(KapuaId scopeId) {
        super(scopeId);
    }


    @Override
    public Set<KapuaId> getTagIds() {
        if (tagIds == null) {
            tagIds = new HashSet<>();
        }

        return tagIds;
    }

    @Override
    public void setTagIds(Set<KapuaId> tagIds) {
        this.tagIds = tagIds;
    }
}
