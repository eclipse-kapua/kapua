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
package org.eclipse.kapua.service.device.registry.event;

import java.util.Date;

import org.eclipse.kapua.model.KapuaEntityFactory;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link DeviceEventFactory} definition.
 *
 * @see org.eclipse.kapua.model.KapuaEntityFactory
 * @since 1.0.0
 */
public interface DeviceEventFactory extends KapuaEntityFactory<DeviceEvent, DeviceEventCreator, DeviceEventQuery> {

    /**
     * Instantiates a new {@link DeviceEventCreator}
     *
     * @param scopeId
     *         The scope {@link KapuaId} to be set in the {@link DeviceEventCreator}
     * @param deviceId
     *         The {@link org.eclipse.kapua.service.device.registry.Device} {@link KapuaId} to be set in the {@link DeviceEventCreator}
     * @param receivedOn
     *         The received on {@link KapuaId} to be set in the {@link DeviceEventCreator}
     * @param resource
     *         The resource {@link KapuaId} to be set in the {@link DeviceEventCreator}
     * @return The newly instantiated {@link DeviceEventCreator}
     * @since 1.0.0
     */
    DeviceEventCreator newCreator(KapuaId scopeId, KapuaId deviceId, Date receivedOn, String resource);

}
