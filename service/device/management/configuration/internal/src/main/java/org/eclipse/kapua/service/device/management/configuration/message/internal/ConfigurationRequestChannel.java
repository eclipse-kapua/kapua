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
package org.eclipse.kapua.service.device.management.configuration.message.internal;

import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestChannel;

/**
 * {@link DeviceConfiguration} {@link KapuaRequestChannel} implementation.
 *
 * @since 1.0.0
 */
public class ConfigurationRequestChannel extends KapuaRequestChannel {

    private static final long serialVersionUID = 4679870909531884966L;

    private String configurationId;
    private String componentId;

    /**
     * Gets the {@link DeviceConfiguration} identifier.
     *
     * @return The {@link DeviceConfiguration} identifier.
     * @since 1.0.0
     */
    public String getConfigurationId() {
        return configurationId;
    }

    /**
     * Sets the {@link DeviceConfiguration} identifier.
     *
     * @param configurationId
     *         The {@link DeviceConfiguration} identifier.
     * @since 1.0.0
     */
    public void setConfigurationId(String configurationId) {
        this.configurationId = configurationId;
    }

    /**
     * Gets the {@link DeviceComponentConfiguration#getId()}.
     *
     * @return The {@link DeviceComponentConfiguration#getId()}.
     * @since 1.0.0
     */
    public String getComponentId() {
        return componentId;
    }

    /**
     * Sets the {@link DeviceComponentConfiguration#getId()}.
     *
     * @param componentId
     *         The {@link DeviceComponentConfiguration#getId()}.
     * @since 1.0.0
     */
    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }
}
