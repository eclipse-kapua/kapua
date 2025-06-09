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
package org.eclipse.kapua.service.device.management.configuration.message.event;

import java.util.Collections;
import java.util.List;

import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.message.event.KapuaManagementEventPayload;

/**
 * {@link DeviceConfiguration}  {@link KapuaManagementEventPayload} definition.
 *
 * @since 2.0.0
 */
public class DeviceConfigurationEventPayload extends KapuaManagementEventPayload {

    private static final long serialVersionUID = 1400605735748313538L;

    private final String charEncoding = KapuaLocator.getInstance().getComponent(DeviceManagementSetting.class).getString(DeviceManagementSettingKey.CHAR_ENCODING);
    private final XmlUtil xmlUtil = KapuaLocator.getInstance().getComponent(XmlUtil.class);

    /**
     * Gets the {@link List} of changed {@link DeviceComponentConfiguration}s
     *
     * @return The {@link List} of changed {@link DeviceComponentConfiguration}s
     * @since 2.0.0
     */
    public List<DeviceComponentConfiguration> getDeviceComponentConfigurations() throws Exception {
        if (!hasBody()) {
            return Collections.emptyList();
        }

        String bodyString = new String(getBody(), charEncoding);
        return xmlUtil.unmarshal(bodyString, DeviceConfiguration.class).getComponentConfigurations();
    }

    /**
     * Sets the {@link List} of changed {@link DeviceComponentConfiguration}s
     *
     * @param deviceConfiguration
     *         The device Configuration containing the {@link List} of changed {@link DeviceComponentConfiguration}s
     * @since 2.0.0
     */
    public void setDeviceComponentConfigurations(DeviceConfiguration deviceConfiguration) throws Exception {
        String bodyString = xmlUtil.marshal(deviceConfiguration);
        setBody(bodyString.getBytes(charEncoding));
    }
}
