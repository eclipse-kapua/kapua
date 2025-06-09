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

import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestPayload;

/**
 * {@link DeviceConfiguration} {@link KapuaRequestPayload} implementation.
 *
 * @since 1.0.0
 */
public class ConfigurationRequestPayload extends KapuaRequestPayload {

    private static final long serialVersionUID = 1400605735748313538L;

    private final String charEncoding = KapuaLocator.getInstance().getComponent(DeviceManagementSetting.class).getString(DeviceManagementSettingKey.CHAR_ENCODING);
    private final XmlUtil xmlUtil = KapuaLocator.getInstance().getComponent(XmlUtil.class);

    /**
     * Gets the {@link DeviceConfiguration}from the {@link #getBody()}.
     *
     * @return The {@link DeviceConfiguration}from the {@link #getBody()}.
     * @throws Exception
     *         if reading {@link #getBody()} errors.
     * @since 1.5.0
     */
    public Optional<DeviceConfiguration> getDeviceConfigurations() throws Exception {
        if (!hasBody()) {
            return Optional.empty();
        }

        String bodyString = new String(getBody(), charEncoding);
        return Optional.ofNullable(xmlUtil.unmarshal(bodyString, DeviceConfiguration.class));
    }

    /**
     * Sets the {@link DeviceConfiguration} in the {@link #getBody()}.
     *
     * @param deviceConfiguration
     *         The {@link DeviceConfiguration}.
     * @throws Exception
     *         if writing errors.
     * @since 1.5.0
     */
    public void setDeviceConfigurations(@NotNull DeviceConfiguration deviceConfiguration) throws Exception {
        String bodyString = xmlUtil.marshal(deviceConfiguration);
        setBody(bodyString.getBytes(charEncoding));
    }
}
