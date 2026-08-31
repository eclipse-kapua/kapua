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

import javax.inject.Inject;
import org.eclipse.kapua.service.device.call.kura.model.configuration.ConfigurationMetrics;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseChannel;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationCreationResponseMessage;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationCreationResponsePayload;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationResponseChannel;
import org.eclipse.kapua.translator.exception.InvalidChannelException;

public class TranslatorAppConfigurationCreationKuraKapua extends AbstractSimpleTranslatorResponseKuraKapua<ConfigurationResponseChannel, ConfigurationCreationResponsePayload, ConfigurationCreationResponseMessage> {

    @Inject
    public TranslatorAppConfigurationCreationKuraKapua(DeviceManagementSetting deviceManagementSetting) {
        super(deviceManagementSetting, ConfigurationCreationResponseMessage.class, ConfigurationCreationResponsePayload.class);
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
}
