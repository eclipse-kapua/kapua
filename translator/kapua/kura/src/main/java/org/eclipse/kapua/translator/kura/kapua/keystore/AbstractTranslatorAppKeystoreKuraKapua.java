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

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.kapua.service.device.call.kura.model.keystore.KeystoreMetrics;
import org.eclipse.kapua.service.device.call.kura.model.keystore.KuraKeystore;
import org.eclipse.kapua.service.device.call.kura.model.keystore.KuraKeystoreCSR;
import org.eclipse.kapua.service.device.call.kura.model.keystore.KuraKeystoreItem;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseMessage;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.keystore.internal.message.response.KeystoreResponseChannel;
import org.eclipse.kapua.service.device.management.keystore.internal.message.response.KeystoreResponseMessage;
import org.eclipse.kapua.service.device.management.keystore.internal.message.response.KeystoreResponsePayload;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystore;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreCSR;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreItem;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreItems;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreSubjectAN;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystores;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.kura.kapua.AbstractSimpleTranslatorResponseKuraKapua;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Translator} {@code abstract} implementation from {@link KuraResponseMessage} to {@link KeystoreResponseMessage}
 *
 * @since 1.5.0
 */
public abstract class AbstractTranslatorAppKeystoreKuraKapua<M extends KeystoreResponseMessage> extends AbstractSimpleTranslatorResponseKuraKapua<KeystoreResponseChannel, KeystoreResponsePayload, M> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTranslatorAppKeystoreKuraKapua.class);

    /**
     * Constructor.
     *
     * @param responseMessageClass
     *         The type of the {@link KeystoreResponseMessage}.
     * @since 1.5.0
     */
    @Inject
    public AbstractTranslatorAppKeystoreKuraKapua(DeviceManagementSetting deviceManagementSetting, Class<M> responseMessageClass) {
        super(deviceManagementSetting, responseMessageClass, KeystoreResponsePayload.class);
    }

    @Override
    protected KeystoreResponseChannel translateChannel(KuraResponseChannel kuraResponseChannel) throws InvalidChannelException {
        try {
            translatorKuraKapuaUtils.validateKuraResponseChannel(kuraResponseChannel, KeystoreMetrics.APP_ID, KeystoreMetrics.APP_VERSION);

            return new KeystoreResponseChannel();
        } catch (Exception e) {
            throw new InvalidChannelException(e, kuraResponseChannel);
        }
    }

    /**
     * Translates a {@link KuraKeystore}[] to {@link DeviceKeystores}.
     *
     * @param kuraKeystoreArray
     *         The {@link KuraKeystore}[] to translate.
     * @return The translated {@link DeviceKeystores}.
     * @since 1.5.0
     */
    protected DeviceKeystores translate(KuraKeystore[] kuraKeystoreArray) {

        DeviceKeystores deviceKeystores = new DeviceKeystores();

        deviceKeystores.setKeystores(
                Arrays.stream(kuraKeystoreArray).map(kuraKeystore -> {
                    DeviceKeystore deviceKeystore = new DeviceKeystore();

                    deviceKeystore.setId(kuraKeystore.getKeystoreServicePid());
                    deviceKeystore.setKeystoreType(kuraKeystore.getType());
                    deviceKeystore.setSize(kuraKeystore.getSize());

                    return deviceKeystore;
                }).collect(Collectors.toList())
        );

        return deviceKeystores;
    }

    /**
     * Translates a {@link KuraKeystoreItem}[] to {@link DeviceKeystoreItems}.
     *
     * @param kuraKeystoreItemArray
     *         The {@link KuraKeystoreItem}[] to translate.
     * @return The translated {@link DeviceKeystoreItems}.
     * @since 1.5.0
     */
    protected DeviceKeystoreItems translate(KuraKeystoreItem[] kuraKeystoreItemArray) {

        DeviceKeystoreItems deviceKeystoreItems = new DeviceKeystoreItems();

        deviceKeystoreItems.setKeystoreItems(
                Arrays.stream(kuraKeystoreItemArray)
                        .map(this::translate)
                        .collect(Collectors.toList())
        );

        return deviceKeystoreItems;
    }

    /**
     * Translates a {@link KuraKeystoreItem}to {@link DeviceKeystoreItems}.
     *
     * @param kuraKeystoreItem
     *         The {@link KuraKeystoreItem} to translate.
     * @return The translated {@link DeviceKeystoreItems}.
     * @since 1.5.0
     */
    protected DeviceKeystoreItem translate(KuraKeystoreItem kuraKeystoreItem) {

        DeviceKeystoreItem deviceKeystore = new DeviceKeystoreItem();

        deviceKeystore.setKeystoreId(kuraKeystoreItem.getKeystoreServicePid());
        deviceKeystore.setItemType(kuraKeystoreItem.getType());
        deviceKeystore.setSize(kuraKeystoreItem.getSize());
        deviceKeystore.setAlgorithm(kuraKeystoreItem.getAlgorithm());
        deviceKeystore.setAlias(kuraKeystoreItem.getAlias());
        deviceKeystore.setSubjectDN(kuraKeystoreItem.getSubjectDN());
        deviceKeystore.setIssuer(kuraKeystoreItem.getIssuer());
        deviceKeystore.setCertificate(kuraKeystoreItem.getCertificate());
        deviceKeystore.setCertificateChain(kuraKeystoreItem.getCertificateChain());

        for (String[] kuraSubjectAN : kuraKeystoreItem.getSubjectAN()) {
            DeviceKeystoreSubjectAN deviceSubjectAN = new DeviceKeystoreSubjectAN();

            if (kuraSubjectAN == null || kuraSubjectAN.length != 2) {
                LOG.warn("Invalid Subject Alternative Names provided from the device: {}", (Object) kuraSubjectAN);
                continue;
            }

            deviceSubjectAN.setANType(kuraSubjectAN[0]);
            deviceSubjectAN.setValue(kuraSubjectAN[1]);

            deviceKeystore.addSubjectAN(deviceSubjectAN);
        }

        if (kuraKeystoreItem.getStartDate() != null) {
            deviceKeystore.setNotBefore(new Date(kuraKeystoreItem.getStartDate()));
        }

        if (kuraKeystoreItem.getExpirationDate() != null) {
            deviceKeystore.setNotAfter(new Date(kuraKeystoreItem.getExpirationDate()));
        }

        return deviceKeystore;
    }

    /**
     * Translates a {@link KuraKeystoreCSR} to {@link DeviceKeystoreCSR}.
     *
     * @param kuraKeystoreCSR
     *         The {@link KuraKeystoreCSR} to translate.
     * @return The translated {@link DeviceKeystoreCSR}.
     * @since 1.5.0
     */
    protected DeviceKeystoreCSR translate(KuraKeystoreCSR kuraKeystoreCSR) {
        DeviceKeystoreCSR deviceKeystoreCSR = new DeviceKeystoreCSR();

        deviceKeystoreCSR.setSigningRequest(kuraKeystoreCSR.getSigningRequest());

        return deviceKeystoreCSR;
    }
}
