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
package org.eclipse.kapua.translator.kura.kapua.keystore;

import javax.inject.Inject;

import org.eclipse.kapua.service.device.call.kura.model.keystore.KuraKeystoreCSR;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponsePayload;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.keystore.internal.message.response.KeystoreCsrResponseMessage;
import org.eclipse.kapua.service.device.management.keystore.internal.message.response.KeystoreResponsePayload;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Translator} implementation from {@link KeystoreCsrResponseMessage} to {@link KeystoreCsrResponseMessage}
 *
 * @since 1.5.0
 */
public class TranslatorAppKeystoreCsrKuraKapua extends AbstractTranslatorAppKeystoreKuraKapua<KeystoreCsrResponseMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(TranslatorAppKeystoreCsrKuraKapua.class);

    /**
     * Constructor.
     *
     * @since 1.5.0
     */
    @Inject
    public TranslatorAppKeystoreCsrKuraKapua(DeviceManagementSetting deviceManagementSetting) {
        super(deviceManagementSetting, KeystoreCsrResponseMessage.class);
    }

    @Override
    protected KeystoreResponsePayload translatePayload(KuraResponsePayload kuraResponsePayload) throws InvalidPayloadException {
        try {
            KeystoreResponsePayload keystoreResponsePayload = super.translatePayload(kuraResponsePayload);

            if (kuraResponsePayload.hasBody()) {
                KuraKeystoreCSR kuraKeystoreCSR;
                try {
                    kuraKeystoreCSR = readJsonBodyAs(kuraResponsePayload.getBody(), KuraKeystoreCSR.class);
                } catch (Exception e) {
                    kuraKeystoreCSR = patchForKuraKeystoreCSR(kuraResponsePayload);
                }

                keystoreResponsePayload.setCSR(translate(kuraKeystoreCSR));
            }

            return keystoreResponsePayload;
        } catch (Exception e) {
            throw new InvalidPayloadException(e, kuraResponsePayload);
        }
    }

    /**
     * This method fixes the issue with the {@link KuraKeystoreCSR}.
     * <p>
     * See https://github.com/eclipse/kura/issues/3387 for more info.
     *
     * @param kuraResponsePayload
     *         The {@link KuraKeystoreCSR} that cannot be {@link #readJsonBodyAs(byte[], Class)} {@link KuraKeystoreCSR}.
     * @return The {@link KuraKeystoreCSR} parsed with the alternative format.
     * @since 1.5.0
     */
    private KuraKeystoreCSR patchForKuraKeystoreCSR(KuraResponsePayload kuraResponsePayload) {
        LOG.warn("KEYS-V1/POST/keystore/entries/csr returned not a JSON body... Trying reading body as a String...");

        String kuraKeystoreCsrString = new String(kuraResponsePayload.getBody());
        kuraKeystoreCsrString = kuraKeystoreCsrString.replace("\"", "");
        kuraKeystoreCsrString = kuraKeystoreCsrString.replace("\\n", "\n");

        KuraKeystoreCSR kuraKeystoreCSR = new KuraKeystoreCSR();
        kuraKeystoreCSR.setSigningRequest(kuraKeystoreCsrString);

        LOG.warn("KEYS-V1/POST/keystore/entries/csr returned not a JSON body... Trying reading body as a String... DONE!");
        return kuraKeystoreCSR;
    }
}
