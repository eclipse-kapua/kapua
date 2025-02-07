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

import org.eclipse.kapua.service.device.call.kura.model.keystore.KuraKeystoreCertificate;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestPayload;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.keystore.internal.message.request.KeystoreCertificateRequestMessage;
import org.eclipse.kapua.service.device.management.keystore.internal.message.request.KeystoreRequestPayload;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreCertificate;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;

/**
 * {@link Translator} implementation from {@link KeystoreCertificateRequestMessage} to {@link KuraRequestMessage}
 *
 * @since 1.5.0
 */
public class TranslatorAppKeystoreCertificateKapuaKura extends AbstractTranslatorAppKeystoreKapuaKura<KeystoreCertificateRequestMessage> {

    private final String charEncoding;

    @Inject
    public TranslatorAppKeystoreCertificateKapuaKura(DeviceManagementSetting deviceManagementSetting) {
        charEncoding = deviceManagementSetting.getString(DeviceManagementSettingKey.CHAR_ENCODING);
    }

    @Override
    protected KuraRequestPayload translatePayload(KeystoreRequestPayload keystoreRequestPayload) throws InvalidPayloadException {
        try {
            KuraRequestPayload kuraRequestPayload = new KuraRequestPayload();

            if (keystoreRequestPayload.hasBody()) {
                DeviceKeystoreCertificate keystoreCertificate = keystoreRequestPayload.getCertificate().orElse(new DeviceKeystoreCertificate());

                KuraKeystoreCertificate kuraKeystoreCertificate = new KuraKeystoreCertificate();
                kuraKeystoreCertificate.setKeystoreServicePid(keystoreCertificate.getKeystoreId());
                kuraKeystoreCertificate.setAlias(keystoreCertificate.getAlias());
                kuraKeystoreCertificate.setCertificate(keystoreCertificate.getCertificate());

                kuraRequestPayload.setBody(getJsonMapper().writeValueAsString(kuraKeystoreCertificate).getBytes(charEncoding));
            }

            return kuraRequestPayload;
        } catch (Exception e) {
            throw new InvalidPayloadException(e, keystoreRequestPayload);
        }
    }

    @Override
    public Class<KeystoreCertificateRequestMessage> getClassFrom() {
        return KeystoreCertificateRequestMessage.class;
    }
}
