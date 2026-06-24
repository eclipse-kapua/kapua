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

import org.eclipse.kapua.service.device.call.kura.model.configuration.ConfigurationMetrics;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestPayload;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationFactoriesRequestMessage;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationFactoriesRequestPayload;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationRequestChannel;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;

public class TranslatorAppConfigurationFactoriesKapuaKura extends AbstractTranslatorKapuaKura<ConfigurationRequestChannel, ConfigurationFactoriesRequestPayload, ConfigurationFactoriesRequestMessage> {

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
    protected KuraRequestPayload translatePayload(ConfigurationFactoriesRequestPayload kapuaPayload) throws InvalidPayloadException {
        try {
            // Return Kura Payload
            return new KuraRequestPayload();
        } catch (Exception e) {
            throw new InvalidPayloadException(e, kapuaPayload);
        }
    }

    @Override
    public Class<ConfigurationFactoriesRequestMessage> getClassFrom() {
        return ConfigurationFactoriesRequestMessage.class;
    }

    @Override
    public Class<KuraRequestMessage> getClassTo() {
        return KuraRequestMessage.class;
    }
}
