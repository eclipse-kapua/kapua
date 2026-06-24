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
package org.eclipse.kapua.translator.kapua.kura;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.eclipse.kapua.service.device.call.kura.model.configuration.ConfigurationMetrics;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestPayload;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationCreationRequestMessage;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationCreationRequestPayload;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationRequestChannel;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;

public class TranslatorAppConfigurationCreationKapuaKura extends AbstractTranslatorKapuaKura<ConfigurationRequestChannel, ConfigurationCreationRequestPayload, ConfigurationCreationRequestMessage> {

    private final ObjectMapper jsonMapper;

    @Inject
    public TranslatorAppConfigurationCreationKapuaKura(ObjectMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    protected KuraRequestChannel translateChannel(ConfigurationRequestChannel kapuaChannel) throws InvalidChannelException {
        try {
            KuraRequestChannel channel = TranslatorKapuaKuraUtils.buildBaseRequestChannel(ConfigurationMetrics.APP_ID, ConfigurationMetrics.APP_VERSION_2, kapuaChannel.getMethod());
            channel.setResources(new String[]{"factoryComponents"});
            return channel;
        } catch (Exception e) {
            throw new InvalidChannelException(e, kapuaChannel);
        }
    }

    @Override
    protected KuraRequestPayload translatePayload(ConfigurationCreationRequestPayload kapuaPayload) throws InvalidPayloadException {
        try {
            KuraRequestPayload kuraRequestPayload = new KuraRequestPayload();
            RequestBody requestBody = new RequestBody();
            requestBody.configs.add(new Configs(kapuaPayload.getComponentFactoryId(), kapuaPayload.getComponentId()));

            kuraRequestPayload.setBody(jsonMapper.writeValueAsBytes(requestBody));

            // Return Kura Payload
            return kuraRequestPayload;
        } catch (Exception e) {
            throw new InvalidPayloadException(e, kapuaPayload);
        }
    }

    @Override
    public Class<ConfigurationCreationRequestMessage> getClassFrom() {
        return ConfigurationCreationRequestMessage.class;
    }

    @Override
    public Class<KuraRequestMessage> getClassTo() {
        return KuraRequestMessage.class;
    }

    private static class RequestBody {

        @JsonProperty("configs")
        public final List<Configs> configs = new ArrayList<>();
    }

    private static class Configs {

        @JsonProperty("factoryPid")
        public final String factoryPid;
        @JsonProperty("pid")
        public final String pid;

        Configs(String factoryPid, String pid) {
            this.factoryPid = factoryPid;
            this.pid = pid;
        }
    }
}
