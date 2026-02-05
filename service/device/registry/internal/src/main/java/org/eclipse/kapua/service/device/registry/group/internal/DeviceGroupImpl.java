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

import org.eclipse.kapua.commons.model.AbstractKapuaNamedEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.device.registry.group.DeviceGroup;

import java.util.HashSet;
import java.util.Set;

/**
 * {@link DeviceGroup} implementation.
 *
 * @since 2.1.0
 */
public class DeviceGroupImpl extends AbstractKapuaNamedEntity implements DeviceGroup {

    private static final long serialVersionUID = -3760818776351242930L;

    private Set<KapuaId> tagIds;


    /**
     * Constructor.
     *
     * @param scopeId the scope {@link KapuaId}
     * @since 1.0.0
     */
    public DeviceGroupImpl(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * Clone constructor.
     *
     * @param deviceGroup the {@link Group} to clone.
     * @since 1.0.0
     */
    public DeviceGroupImpl(DeviceGroup deviceGroup) {
        super(deviceGroup);

        setTagIds(deviceGroup.getTagIds());
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

    /**
     * Parse a {@link DeviceGroup} instance into a {@link DeviceGroupImpl}.
     *
     * @param deviceGroup The {@link DeviceGroup} instance to parse
     * @return The parsed {@link DeviceGroupImpl} instance.
     * @since 2.1.0
     */
    public static DeviceGroupImpl parse(DeviceGroup deviceGroup) {
        if (deviceGroup == null) {
            return null;
        }

        return deviceGroup instanceof DeviceGroupImpl ?
                (DeviceGroupImpl) deviceGroup :
                new DeviceGroupImpl(deviceGroup);
    }
}
