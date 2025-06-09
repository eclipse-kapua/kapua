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
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.internal.settings.DeviceConfigurationManagementSettings;
import org.eclipse.kapua.service.device.management.configuration.internal.settings.DeviceConfigurationManagementSettingsKeys;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponsePayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DeviceConfiguration} {@link KapuaResponsePayload} implementation.
 *
 * @since 1.0.0
 */
public class ConfigurationResponsePayload extends KapuaResponsePayload {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationResponsePayload.class);

    //TODO: FIXME: REMOVE: A collaborator in a data class? Behaviour should not be part of a data class!
    private final String payloadToDisplayStringMode = KapuaLocator.getInstance().getComponent(DeviceConfigurationManagementSettings.class)
            .getString(DeviceConfigurationManagementSettingsKeys.PAYLOAD_TO_DISPLAY_STRING_MODE, "NONE");
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

    @Override
    public String toDisplayString() {
        try {
            PayloadToDisplayStringMode toDisplayStringMode;
            try {
                toDisplayStringMode = PayloadToDisplayStringMode.valueOf(payloadToDisplayStringMode);
            } catch (IllegalArgumentException iae) {
                LOG.warn(
                        "Invalid device.management.configuration.payload.toDisplayString.mode setting value {}. Please fix the configuration value. Allowed values are: NONE, DEFAULT, NUMBER_OF_COMPONENTS, LIST_OF_COMPONENTS. Defaulting to DEFAULT",
                        payloadToDisplayStringMode);
                toDisplayStringMode = PayloadToDisplayStringMode.DEFAULT;
            }

            switch (toDisplayStringMode) {
            case NONE:
                return "";
            case DEFAULT:
                return super.toDisplayString();
            case NUMBER_OF_COMPONENTS:
                return "Read " + getDeviceConfigurations().map(p -> p.getComponentConfigurations().size()).orElse(0) + " configuration components: " + getDeviceConfigurations()
                        .map(dc -> dc.getComponentConfigurations().stream().map(DeviceComponentConfiguration::getId).sorted(String::compareTo).collect(Collectors.joining(", "))).orElse("");
            case LIST_OF_COMPONENTS:
                return "Read configuration components: " + getDeviceConfigurations().map(
                        dc -> dc.getComponentConfigurations().stream().map(DeviceComponentConfiguration::getId).sorted(String::compareTo).collect(Collectors.joining(", "))).orElse("");
            }
        } catch (Exception e) {
            LOG.warn("Error while invoking ConfigurationResponsePayload.toDisplayString(). Defaulting to KapuaResponsePayload.toDisplayString(). Error: {}", e.getMessage());
        }

        return super.toDisplayString();
    }

    /**
     * Defines the way the {@link #toDisplayString()} will behave.
     *
     * @since 2.0.0
     */
    private enum PayloadToDisplayStringMode {

        /**
         * Displays nothing.
         *
         * @since 2.0.0
         */
        NONE,

        /**
         * Displays what {@link KapuaPayload#toDisplayString()} displays (only metrics currently).
         *
         * @since 2.0.0
         */
        DEFAULT,

        /**
         * Displays the number of {@link DeviceComponentConfiguration} in the {@link DeviceConfiguration}.
         *
         * @since 2.0.0
         */
        NUMBER_OF_COMPONENTS,

        /**
         * Displays the list of {@link DeviceComponentConfiguration} in the {@link DeviceConfiguration}.
         *
         * @since 2.0.0
         */
        LIST_OF_COMPONENTS,
    }
}


