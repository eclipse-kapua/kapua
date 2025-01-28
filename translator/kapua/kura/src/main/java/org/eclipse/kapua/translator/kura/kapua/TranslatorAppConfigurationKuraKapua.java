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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.translator.kura.kapua;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.kapua.model.config.metatype.KapuaTad;
import org.eclipse.kapua.model.config.metatype.KapuaTicon;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.config.metatype.KapuaToption;
import org.eclipse.kapua.model.config.metatype.Password;
import org.eclipse.kapua.service.device.call.kura.model.configuration.ConfigurationMetrics;
import org.eclipse.kapua.service.device.call.kura.model.configuration.KuraDeviceComponentConfiguration;
import org.eclipse.kapua.service.device.call.kura.model.configuration.KuraDeviceConfiguration;
import org.eclipse.kapua.service.device.call.kura.model.configuration.KuraPassword;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponsePayload;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.internal.DeviceComponentConfigurationImpl;
import org.eclipse.kapua.service.device.management.configuration.internal.DeviceConfigurationImpl;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationResponseChannel;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationResponseMessage;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationResponsePayload;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;

/**
 * {@link org.eclipse.kapua.translator.Translator} implementation from {@link KuraResponseMessage} to {@link ConfigurationResponseMessage}
 *
 * @since 1.0.0
 */
public class TranslatorAppConfigurationKuraKapua extends AbstractSimpleTranslatorResponseKuraKapua<ConfigurationResponseChannel, ConfigurationResponsePayload, ConfigurationResponseMessage> {

    @Inject
    public TranslatorAppConfigurationKuraKapua(DeviceManagementSetting deviceManagementSetting) {
        super(deviceManagementSetting, ConfigurationResponseMessage.class, ConfigurationResponsePayload.class);
    }

    @Override
    protected ConfigurationResponseChannel translateChannel(KuraResponseChannel kuraResponseChannel) throws InvalidChannelException {
        try {
            translatorKuraKapuaUtils.validateKuraResponseChannel(kuraResponseChannel, ConfigurationMetrics.APP_ID, ConfigurationMetrics.APP_VERSION);

            return new ConfigurationResponseChannel();
        } catch (Exception e) {
            throw new InvalidChannelException(e, kuraResponseChannel);
        }
    }

    @Override
    protected ConfigurationResponsePayload translatePayload(KuraResponsePayload kuraResponsePayload) throws InvalidPayloadException {
        ConfigurationResponsePayload configurationResponsePayload = super.translatePayload(kuraResponsePayload);

        try {
            if (kuraResponsePayload.hasBody()) {
                KuraDeviceConfiguration kuraDeviceConfiguration = readXmlBodyAs(kuraResponsePayload.getBody(), KuraDeviceConfiguration.class);

                configurationResponsePayload.setDeviceConfigurations(translate(kuraDeviceConfiguration));
            }

            // Return Kapua Payload
            return configurationResponsePayload;
        } catch (Exception e) {
            throw new InvalidPayloadException(e, kuraResponsePayload);
        }
    }

    private DeviceConfiguration translate(KuraDeviceConfiguration kuraDeviceConfiguration) {
        DeviceConfigurationImpl deviceConfiguration = new DeviceConfigurationImpl();

        for (KuraDeviceComponentConfiguration kuraDeviceCompConf : kuraDeviceConfiguration.getConfigurations()) {

            String componentId = kuraDeviceCompConf.getComponentId();
            DeviceComponentConfigurationImpl deviceComponentConfiguration = new DeviceComponentConfigurationImpl(componentId);
            deviceComponentConfiguration.setProperties(translate(kuraDeviceCompConf.getProperties()));

            // Translate also definitions when they are available
            if (kuraDeviceCompConf.getDefinition() != null) {
                deviceComponentConfiguration.setDefinition(translate(kuraDeviceCompConf.getDefinition()));
            }

            // Add to kapua configuration
            deviceConfiguration.getComponentConfigurations().add(deviceComponentConfiguration);
        }

        return deviceConfiguration;
    }

    private KapuaTocd translate(KapuaTocd kuraDefinition) {
        KapuaTocd definition = new KapuaTocd();

        definition.setId(kuraDefinition.getId());
        definition.setName(kuraDefinition.getName());
        definition.setDescription(kuraDefinition.getDescription());

        kuraDefinition.getAD().forEach(kuraAd -> {
            KapuaTad ad = new KapuaTad();
            ad.setCardinality(kuraAd.getCardinality());
            ad.setDefault(ad.getDefault());
            ad.setDescription(kuraAd.getDescription());
            ad.setId(kuraAd.getId());
            ad.setMax(kuraAd.getMax());
            ad.setMin(kuraAd.getMin());
            ad.setName(kuraAd.getName());
            ad.setType(kuraAd.getType());
            ad.setRequired(kuraAd.isRequired());

            kuraAd.getOption().forEach(kuraToption -> {
                KapuaToption kapuaToption = new KapuaToption();

                kapuaToption.setLabel(kuraToption.getLabel());
                kapuaToption.setValue(kuraToption.getValue());
                ad.addOption(kapuaToption);
            });

            kuraAd.getOtherAttributes().forEach(ad::putOtherAttribute);
            definition.addAD(ad);
        });

        kuraDefinition.getIcon().forEach(kuraIcon -> {
            KapuaTicon icon = new KapuaTicon();
            icon.setResource(kuraIcon.getResource());
            icon.setSize(kuraIcon.getSize());
            definition.addIcon(icon);
        });

        kuraDefinition.getAny().forEach(definition::addAny);
        kuraDefinition.getOtherAttributes().forEach(definition::putOtherAttribute);

        return definition;
    }

    private Map<String, Object> translate(Map<String, Object> kuraProperties) {
        Map<String, Object> properties = new HashMap<>();

        kuraProperties.forEach((key, value) -> {
            // Special management of Password type field
            if (value instanceof KuraPassword) {
                value = new Password(((KuraPassword) value).getPassword());
            } else if (value instanceof KuraPassword[]) {
                KuraPassword[] kuraPasswords = (KuraPassword[]) value;
                Password[] passwords = new Password[kuraPasswords.length];

                int i = 0;
                for (KuraPassword p : kuraPasswords) {
                    passwords[i++] = new Password(p.getPassword());
                }

                value = passwords;
            }
            // Set property
            properties.put(key, value);
        });
        return properties;
    }
}
