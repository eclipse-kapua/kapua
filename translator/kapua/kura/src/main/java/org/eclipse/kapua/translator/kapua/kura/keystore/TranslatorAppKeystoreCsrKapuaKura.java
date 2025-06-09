/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.translator.kapua.kura.keystore;

import javax.inject.Inject;

import org.eclipse.kapua.service.device.call.kura.model.keystore.KuraKeystoreCSRInfo;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestPayload;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.keystore.internal.message.request.KeystoreCsrRequestMessage;
import org.eclipse.kapua.service.device.management.keystore.internal.message.request.KeystoreRequestPayload;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreCSRInfo;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;

/**
 * {@link Translator} implementation from {@link KeystoreCsrRequestMessage} to {@link KuraRequestMessage}
 *
 * @since 1.5.0
 */
public class TranslatorAppKeystoreCsrKapuaKura extends AbstractTranslatorAppKeystoreKapuaKura<KeystoreCsrRequestMessage> {

    private final String charEncoding;

    @Inject
    public TranslatorAppKeystoreCsrKapuaKura(DeviceManagementSetting deviceManagementSetting) {
        charEncoding = deviceManagementSetting.getString(DeviceManagementSettingKey.CHAR_ENCODING);
    }

    @Override
    protected KuraRequestPayload translatePayload(KeystoreRequestPayload keystoreRequestPayload) throws InvalidPayloadException {
        try {
            KuraRequestPayload kuraRequestPayload = new KuraRequestPayload();

            if (keystoreRequestPayload.hasBody()) {
                DeviceKeystoreCSRInfo deviceKeystoreCSRInfo = keystoreRequestPayload.getCSRInfo().orElse(new DeviceKeystoreCSRInfo());

                KuraKeystoreCSRInfo kuraKeystoreCSRInfo = new KuraKeystoreCSRInfo();
                kuraKeystoreCSRInfo.setKeystoreServicePid(deviceKeystoreCSRInfo.getKeystoreId());
                kuraKeystoreCSRInfo.setAlias(deviceKeystoreCSRInfo.getAlias());
                kuraKeystoreCSRInfo.setSignatureAlgorithm(deviceKeystoreCSRInfo.getSignatureAlgorithm());
                kuraKeystoreCSRInfo.setAttributes(deviceKeystoreCSRInfo.getAttributes());

                kuraRequestPayload.setBody(getJsonMapper().writeValueAsString(kuraKeystoreCSRInfo).getBytes(charEncoding));
            }

            return kuraRequestPayload;
        } catch (Exception e) {
            throw new InvalidPayloadException(e, keystoreRequestPayload);
        }
    }

    @Override
    public Class<KeystoreCsrRequestMessage> getClassFrom() {
        return KeystoreCsrRequestMessage.class;
    }
}
