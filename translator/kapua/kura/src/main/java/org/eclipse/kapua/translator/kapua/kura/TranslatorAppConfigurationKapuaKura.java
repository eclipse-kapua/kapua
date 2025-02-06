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
package org.eclipse.kapua.translator.kapua.kura;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.model.config.metatype.KapuaTad;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.config.metatype.Password;
import org.eclipse.kapua.service.device.call.kura.model.configuration.ConfigurationMetrics;
import org.eclipse.kapua.service.device.call.kura.model.configuration.KuraDeviceComponentConfiguration;
import org.eclipse.kapua.service.device.call.kura.model.configuration.KuraDeviceConfiguration;
import org.eclipse.kapua.service.device.call.kura.model.configuration.KuraPassword;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestPayload;
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationRequestChannel;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationRequestMessage;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationRequestPayload;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;

/**
 * {@link org.eclipse.kapua.translator.Translator} implementation from {@link ConfigurationRequestMessage} to {@link KuraRequestMessage}
 *
 * @since 1.0.0
 */
public class TranslatorAppConfigurationKapuaKura extends AbstractTranslatorKapuaKura<ConfigurationRequestChannel, ConfigurationRequestPayload, ConfigurationRequestMessage> {

    @Inject
    private XmlUtil xmlUtil;

    @Override
    protected KuraRequestChannel translateChannel(ConfigurationRequestChannel kapuaChannel) throws InvalidChannelException {
        try {
            KuraRequestChannel kuraRequestChannel = TranslatorKapuaKuraUtils.buildBaseRequestChannel(ConfigurationMetrics.APP_ID, ConfigurationMetrics.APP_VERSION, kapuaChannel.getMethod());

            // Build resources
            List<String> resources = new ArrayList<>();
            if (kapuaChannel.getConfigurationId() == null) {
                resources.add("configurations");
                String componentId = kapuaChannel.getComponentId();
                if (componentId != null) {
                    resources.add(componentId);
                }
            } else if (kapuaChannel.getConfigurationId() != null) {
                resources.add("snapshots");

                String configurationId = kapuaChannel.getConfigurationId();
                if (configurationId != null) {
                    resources.add(configurationId);
                }
            }
            kuraRequestChannel.setResources(resources.toArray(new String[0]));

            // Return Kura Channel
            return kuraRequestChannel;
        } catch (Exception e) {
            throw new InvalidChannelException(e, kapuaChannel);
        }
    }

    @Override
    protected KuraRequestPayload translatePayload(ConfigurationRequestPayload kapuaPayload) throws InvalidPayloadException {
        try {
            KuraRequestPayload kuraRequestPayload = new KuraRequestPayload();

            if (kapuaPayload.hasBody()) {
                DeviceConfiguration kapuaDeviceConfiguration = kapuaPayload.getDeviceConfigurations().orElse(new DeviceConfiguration());

                KuraDeviceConfiguration kuraDeviceConfiguration = translate(kapuaDeviceConfiguration);
                byte[] body;
                try {
                    body = xmlUtil.marshal(kuraDeviceConfiguration).getBytes();
                } catch (Exception e) {
                    throw new InvalidPayloadException(e, kapuaPayload);
                }

                kuraRequestPayload.setBody(body);
            }

            // Return Kura Payload
            return kuraRequestPayload;
        } catch (InvalidPayloadException ipe) {
            throw ipe;
        } catch (Exception e) {
            throw new InvalidPayloadException(e, kapuaPayload);
        }
    }

    protected KuraDeviceConfiguration translate(DeviceConfiguration kapuaDeviceConfiguration) {
        KuraDeviceConfiguration kuraDeviceConfiguration = new KuraDeviceConfiguration();

        for (DeviceComponentConfiguration kapuaDeviceCompConf : kapuaDeviceConfiguration.getComponentConfigurations()) {

            KuraDeviceComponentConfiguration kuraComponentConfiguration = new KuraDeviceComponentConfiguration();
            kuraComponentConfiguration.setComponentId(kapuaDeviceCompConf.getId());
            kuraComponentConfiguration.setProperties(translate(kapuaDeviceCompConf.getProperties()));

            // Translate also definitions when they are available
            if (kapuaDeviceCompConf.getDefinition() != null) {
                kuraComponentConfiguration.setDefinition(translate(kapuaDeviceCompConf.getDefinition()));
            }

            // Add to kapua configuration
            kuraDeviceConfiguration.getConfigurations().add(kuraComponentConfiguration);
        }

        return kuraDeviceConfiguration;
    }

    protected KapuaTocd translate(KapuaTocd kapuaDefinition) {
        KapuaTocd definition = new KapuaTocd();

        definition.setId(kapuaDefinition.getId());
        definition.setName(kapuaDefinition.getName());
        definition.setDescription(kapuaDefinition.getDescription());

        kapuaDefinition.getAD().forEach(kapuaAd -> {
            KapuaTad ad = new KapuaTad();
            ad.setCardinality(kapuaAd.getCardinality());
            ad.setDefault(ad.getDefault());
            ad.setDescription(kapuaAd.getDescription());
            ad.setId(kapuaAd.getId());
            ad.setMax(kapuaAd.getMax());
            ad.setMin(kapuaAd.getMin());
            ad.setName(kapuaAd.getName());
            ad.setType(kapuaAd.getType());
            ad.setRequired(kapuaAd.isRequired());

            kapuaAd.getOption().forEach(kuraToption -> {
                org.eclipse.kapua.model.config.metatype.KapuaToption kapuaToption = new org.eclipse.kapua.model.config.metatype.KapuaToption();
                kapuaToption.setLabel(kuraToption.getLabel());
                kapuaToption.setValue(kuraToption.getValue());
                ad.addOption(kapuaToption);
            });

            kapuaAd.getOtherAttributes().forEach(ad::putOtherAttribute); // Such magic!

            definition.addAD(ad);
        });

        kapuaDefinition.getIcon().forEach(kapuaIcon -> {
            org.eclipse.kapua.model.config.metatype.KapuaTicon icon = new org.eclipse.kapua.model.config.metatype.KapuaTicon();
            icon.setResource(kapuaIcon.getResource());
            icon.setSize(kapuaIcon.getSize());

            definition.addIcon(icon);
        });

        kapuaDefinition.getAny().forEach(definition::addAny); // Such magic!
        kapuaDefinition.getOtherAttributes().forEach(definition::putOtherAttribute); // Such magic!

        return definition;
    }

    protected Map<String, Object> translate(Map<String, Object> kapuaProperties) {
        Map<String, Object> properties = new HashMap<>();

        kapuaProperties.forEach((key, value) -> {
            // Special management of Password type field
            if (value instanceof Password) {
                value = new KuraPassword(((Password) value).getPassword());
            } else if (value instanceof Password[]) {
                Password[] passwords = (Password[]) value;
                KuraPassword[] kuraPasswords = new KuraPassword[passwords.length];

                int i = 0;
                for (Password p : passwords) {
                    kuraPasswords[i++] = new KuraPassword(p.getPassword());
                }

                value = kuraPasswords;
            }

            // Set property
            properties.put(key, value);
        });

        return properties;
    }

    @Override
    public Class<ConfigurationRequestMessage> getClassFrom() {
        return ConfigurationRequestMessage.class;
    }

    @Override
    public Class<KuraRequestMessage> getClassTo() {
        return KuraRequestMessage.class;
    }
}
