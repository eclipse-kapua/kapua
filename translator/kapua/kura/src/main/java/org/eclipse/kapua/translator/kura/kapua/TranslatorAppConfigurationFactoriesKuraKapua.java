/*******************************************************************************
 * Copyright (c) 2025 Eurotech and/or its affiliates and others
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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import javax.inject.Inject;
import org.eclipse.kapua.service.device.call.kura.model.configuration.ConfigurationMetrics;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponsePayload;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfigurationFactories;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfigurationFactory;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationFactoriesResponseMessage;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationFactoriesResponsePayload;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationResponseChannel;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;

public class TranslatorAppConfigurationFactoriesKuraKapua extends AbstractSimpleTranslatorResponseKuraKapua<ConfigurationResponseChannel, ConfigurationFactoriesResponsePayload, ConfigurationFactoriesResponseMessage> {

    private final ObjectMapper jsonMapper;
    private final DeviceConfigurationFactory deviceConfigurationFactory;

    @Inject
    public TranslatorAppConfigurationFactoriesKuraKapua(DeviceManagementSetting deviceManagementSetting, ObjectMapper jsonMapper, DeviceConfigurationFactory deviceConfigurationFactory) {
        super(deviceManagementSetting, ConfigurationFactoriesResponseMessage.class, ConfigurationFactoriesResponsePayload.class);
        this.jsonMapper = jsonMapper;
        this.deviceConfigurationFactory = deviceConfigurationFactory;
    }

    @Override
    protected ConfigurationResponseChannel translateChannel(KuraResponseChannel kuraResponseChannel) throws InvalidChannelException {
        try {
            translatorKuraKapuaUtils.validateKuraResponseChannel(kuraResponseChannel, ConfigurationMetrics.APP_ID, ConfigurationMetrics.APP_VERSION_2);

            return new ConfigurationResponseChannel();
        } catch (Exception e) {
            throw new InvalidChannelException(e, kuraResponseChannel);
        }
    }

    @Override
    protected ConfigurationFactoriesResponsePayload translatePayload(KuraResponsePayload kuraResponsePayload) throws InvalidPayloadException {
        try {
            Representation representation = jsonMapper.readValue(kuraResponsePayload.getBody(), Representation.class);
            DeviceComponentConfigurationFactories factories = deviceConfigurationFactory.newComponentConfigurationFactories();
            representation.pids.stream().sorted().forEach(factories.getIds()::add);
            return new ConfigurationFactoriesResponsePayload(factories);
        } catch (Exception e) {
            throw new InvalidPayloadException(e, kuraResponsePayload);
        }
    }

    private static class Representation {
        private final Set<String> pids;

        @JsonCreator
        public Representation(@JsonProperty("pids") Set<String> pids) {
            this.pids = pids;
        }
    }
}
