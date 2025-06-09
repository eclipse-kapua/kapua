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

import org.eclipse.kapua.service.device.call.kura.model.keystore.KeystoreMetrics;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestMessage;
import org.eclipse.kapua.service.device.management.keystore.internal.message.request.KeystoreRequestChannel;
import org.eclipse.kapua.service.device.management.keystore.internal.message.request.KeystoreRequestMessage;
import org.eclipse.kapua.service.device.management.keystore.internal.message.request.KeystoreRequestPayload;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.kapua.kura.AbstractTranslatorKapuaKura;
import org.eclipse.kapua.translator.kapua.kura.TranslatorKapuaKuraUtils;

/**
 * {@link Translator} {@code abstract} implementation from {@link KeystoreRequestMessage} to {@link KuraRequestMessage}
 *
 * @since 1.5.0
 */
public abstract class AbstractTranslatorAppKeystoreKapuaKura<M extends KeystoreRequestMessage<M>> extends AbstractTranslatorKapuaKura<KeystoreRequestChannel, KeystoreRequestPayload, M> {

    @Override
    protected KuraRequestChannel translateChannel(KeystoreRequestChannel kapuaChannel) throws InvalidChannelException {
        try {
            KuraRequestChannel kuraRequestChannel = TranslatorKapuaKuraUtils.buildBaseRequestChannel(KeystoreMetrics.APP_ID, KeystoreMetrics.APP_VERSION, kapuaChannel.getMethod());

            if (kapuaChannel.getResource() != null) {
                if ("items".equals(kapuaChannel.getResource())) {
                    kuraRequestChannel.setResources(new String[] { "keystores", "entries" });
                } else if ("item".equals(kapuaChannel.getResource())) {
                    kuraRequestChannel.setResources(new String[] { "keystores", "entries", "entry" });
                } else if ("certificate".equals(kapuaChannel.getResource())) {
                    kuraRequestChannel.setResources(new String[] { "keystores", "entries", "certificate" });
                } else if ("keypair".equals(kapuaChannel.getResource())) {
                    kuraRequestChannel.setResources(new String[] { "keystores", "entries", "keypair" });
                } else if ("csr".equals(kapuaChannel.getResource())) {
                    kuraRequestChannel.setResources(new String[] { "keystores", "entries", "csr" });
                }
            } else {
                kuraRequestChannel.setResources(new String[] { "keystores" });
            }

            // Return Kura Channel
            return kuraRequestChannel;
        } catch (Exception e) {
            throw new InvalidChannelException(e, kapuaChannel);
        }
    }

    @Override
    public Class<KuraRequestMessage> getClassTo() {
        return KuraRequestMessage.class;
    }
}
