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
 *******************************************************************************/
package org.eclipse.kapua.translator.kura.kapua;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.commons.cache.LocalCache;
import org.eclipse.kapua.message.device.data.KapuaDataChannel;
import org.eclipse.kapua.message.device.data.KapuaDataMessage;
import org.eclipse.kapua.message.device.data.KapuaDataMessageFactory;
import org.eclipse.kapua.message.device.data.KapuaDataPayload;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.message.kura.data.KuraDataChannel;
import org.eclipse.kapua.service.device.call.message.kura.data.KuraDataMessage;
import org.eclipse.kapua.service.device.call.message.kura.data.KuraDataPayload;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.AbstractAccountResolvingTranslator;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.exception.InvalidMessageException;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;
import org.eclipse.kapua.translator.exception.TranslateException;
import org.eclipse.kapua.translator.setting.TranslatorKapuaKuraSettingKeys;
import org.eclipse.kapua.translator.setting.TranslatorKapuaKuraSettings;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * {@link Translator} implementation from {@link KuraDataMessage} to {@link KapuaDataMessage}
 *
 * @since 1.0.0
 */
public class TranslatorDataKuraKapua extends AbstractAccountResolvingTranslator<KuraDataMessage, KapuaDataMessage> {

    private final DeviceRegistryService deviceRegistryService;
    private final KapuaDataMessageFactory kapuaDataMessageFactory;
    private final TranslatorKuraKapuaUtils translatorKuraKapuaUtils;

    private final boolean resolveDeviceId;

    /**
     * Constructor.
     *
     * @param accountService              The {@link AccountService} used to resolve the {@link KuraDataChannel#getScope()}.
     * @param accountsByNameCache         The {@link LocalCache} of {@link Account}s by {@link Account#getName()} used to resolve the {@link KuraDataChannel#getScope()}.
     * @param deviceRegistryService       The {@link DeviceRegistryService} used to resolve the {@link KuraDataChannel#getClientId()}.
     * @param kapuaDataMessageFactory     The {@link KapuaDataMessageFactory}.
     * @param translatorKuraKapuaUtils    The {@link TranslatorKuraKapuaUtils}.
     * @param translatorKapuaKuraSettings The {@link TranslatorKapuaKuraSettings}.
     * @since 2.1.0
     */
    @Inject
    public TranslatorDataKuraKapua(
            AccountService accountService,
            @Named("accountsByNameCache") LocalCache<String, Account> accountsByNameCache,
            DeviceRegistryService deviceRegistryService,
            KapuaDataMessageFactory kapuaDataMessageFactory,
            TranslatorKuraKapuaUtils translatorKuraKapuaUtils,
            TranslatorKapuaKuraSettings translatorKapuaKuraSettings) {
        super(accountService, accountsByNameCache);
        this.deviceRegistryService = deviceRegistryService;
        this.kapuaDataMessageFactory = kapuaDataMessageFactory;
        this.translatorKuraKapuaUtils = translatorKuraKapuaUtils;
        this.resolveDeviceId = translatorKapuaKuraSettings.getBoolean(TranslatorKapuaKuraSettingKeys.TRANSLATOR_KURA_KAPUA_DATA_DEVICE_ID_RESOLVE);
    }

    @Override
    public KapuaDataMessage translate(KuraDataMessage kuraMessage) throws TranslateException {
        try {
            // Kapua Channel
            KapuaDataChannel kapuaDataChannel = translate(kuraMessage.getChannel());
            // Kapua payload
            KapuaDataPayload kapuaDataPayload = translate(kuraMessage.getPayload());
            // Kapua message
            KapuaId accountId = findAccountIdByName(kuraMessage.getChannel().getScope());

            if (accountId == null) {
                throw new KapuaEntityNotFoundException(Account.TYPE, kuraMessage.getChannel().getScope());
            }

            KapuaDataMessage kapuaDataMessage = kapuaDataMessageFactory.newKapuaDataMessage();
            kapuaDataMessage.setScopeId(accountId);
            kapuaDataMessage.setClientId(kuraMessage.getChannel().getClientId());
            kapuaDataMessage.setChannel(kapuaDataChannel);
            kapuaDataMessage.setPayload(kapuaDataPayload);
            kapuaDataMessage.setCapturedOn(kuraMessage.getPayload().getTimestamp());
            kapuaDataMessage.setSentOn(kuraMessage.getPayload().getTimestamp());
            kapuaDataMessage.setReceivedOn(kuraMessage.getTimestamp());
            kapuaDataMessage.setPosition(translatorKuraKapuaUtils.translate(kuraMessage.getPayload().getPosition()));

            // Optionally resolve the KapuaDataChannel.clientId to improve performances
            if (resolveDeviceId) {
                Device device = deviceRegistryService.findByClientId(accountId, kuraMessage.getChannel().getClientId());
                kapuaDataMessage.setDeviceId(device != null ? device.getId() : null);
            }

            // Return Kapua Message
            return kapuaDataMessage;
        } catch (InvalidChannelException | InvalidPayloadException te) {
            throw te;
        } catch (Exception e) {
            throw new InvalidMessageException(e, kuraMessage);
        }
    }

    private KapuaDataChannel translate(KuraDataChannel kuraChannel) {
        KapuaDataChannel kapuaChannel = kapuaDataMessageFactory.newKapuaDataChannel();
        kapuaChannel.setSemanticParts(kuraChannel.getSemanticParts());

        // Return Kapua Channel
        return kapuaChannel;
    }

    private KapuaDataPayload translate(KuraDataPayload kuraPayload) {
        KapuaDataPayload kapuaPayload = kapuaDataMessageFactory.newKapuaDataPayload();

        if (!kuraPayload.getMetrics().isEmpty()) {
            kapuaPayload.setMetrics(kuraPayload.getMetrics());
        }

        if (kuraPayload.hasBody()) {
            kapuaPayload.setBody(kuraPayload.getBody());
        }

        // Return Kapua payload
        return kapuaPayload;
    }

    @Override
    public Class<KuraDataMessage> getClassFrom() {
        return KuraDataMessage.class;
    }

    @Override
    public Class<KapuaDataMessage> getClassTo() {
        return KapuaDataMessage.class;
    }

}
