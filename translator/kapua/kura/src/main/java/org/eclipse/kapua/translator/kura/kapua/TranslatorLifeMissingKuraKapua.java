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
import org.eclipse.kapua.message.device.lifecycle.KapuaMissingChannel;
import org.eclipse.kapua.message.device.lifecycle.KapuaMissingMessage;
import org.eclipse.kapua.message.device.lifecycle.KapuaMissingPayload;
import org.eclipse.kapua.message.internal.device.lifecycle.KapuaMissingChannelImpl;
import org.eclipse.kapua.message.internal.device.lifecycle.KapuaMissingMessageImpl;
import org.eclipse.kapua.message.internal.device.lifecycle.KapuaMissingPayloadImpl;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraMissingChannel;
import org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraMissingMessage;
import org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraMissingPayload;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.AbstractAccountResolvingTranslator;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.exception.InvalidMessageException;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;
import org.eclipse.kapua.translator.exception.TranslateException;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * {@link Translator} implementation from {@link KuraMissingMessage} to {@link KapuaMissingMessage}
 *
 * @since 1.0.0
 */
public class TranslatorLifeMissingKuraKapua extends AbstractAccountResolvingTranslator<KuraMissingMessage, KapuaMissingMessage> {

    private final DeviceRegistryService deviceRegistryService;
    private final TranslatorKuraKapuaUtils translatorKuraKapuaUtils;

    /**
     * Constructor.
     *
     * @param accountService           The {@link AccountService} used to resolve the {@link Account} ids from the {@link Account} names.
     * @param accountsByNameCache      The {@link LocalCache} of {@link Account}s by {@link Account#getName()}, shared among {@link Translator}s.
     * @param deviceRegistryService    The {@link DeviceRegistryService}.
     * @param translatorKuraKapuaUtils The {@link TranslatorKuraKapuaUtils}.
     * @since 2.1.0
     */
    @Inject
    public TranslatorLifeMissingKuraKapua(
            AccountService accountService,
            @Named("accountsByNameCache") LocalCache<String, Account> accountsByNameCache,
            DeviceRegistryService deviceRegistryService,
            TranslatorKuraKapuaUtils translatorKuraKapuaUtils) {
        super(accountService, accountsByNameCache);
        this.deviceRegistryService = deviceRegistryService;
        this.translatorKuraKapuaUtils = translatorKuraKapuaUtils;
    }

    @Override
    public KapuaMissingMessage translate(KuraMissingMessage kuraMissingMessage) throws TranslateException {
        try {
            KapuaMissingMessage kapuaMissingMessage = new KapuaMissingMessageImpl();
            kapuaMissingMessage.setChannel(translate(kuraMissingMessage.getChannel()));
            kapuaMissingMessage.setPayload(translate(kuraMissingMessage.getPayload()));

            KapuaId accountId = findAccountIdByName(kuraMissingMessage.getChannel().getScope());
            if (accountId == null) {
                throw new KapuaEntityNotFoundException(Account.TYPE, kuraMissingMessage.getChannel().getScope());
            }

            Device device = deviceRegistryService.findByClientId(accountId, kuraMissingMessage.getChannel().getClientId());
            if (device == null) {
                throw new KapuaEntityNotFoundException(Device.class.toString(), kuraMissingMessage.getChannel().getClientId());
            }

            kapuaMissingMessage.setDeviceId(device.getId());
            kapuaMissingMessage.setScopeId(accountId);
            kapuaMissingMessage.setCapturedOn(kuraMissingMessage.getPayload().getTimestamp());
            kapuaMissingMessage.setSentOn(kuraMissingMessage.getPayload().getTimestamp());
            kapuaMissingMessage.setReceivedOn(kuraMissingMessage.getTimestamp());
            kapuaMissingMessage.setPosition(translatorKuraKapuaUtils.translate(kuraMissingMessage.getPayload().getPosition()));

            return kapuaMissingMessage;
        } catch (InvalidChannelException | InvalidPayloadException te) {
            throw te;
        } catch (Exception e) {
            throw new InvalidMessageException(e, kuraMissingMessage);
        }
    }

    private KapuaMissingChannel translate(KuraMissingChannel kuraMissingChannel) {
        KapuaMissingChannel kapuaMissingChannel = new KapuaMissingChannelImpl();
        kapuaMissingChannel.setClientId(kuraMissingChannel.getClientId());
        return kapuaMissingChannel;
    }

    private KapuaMissingPayload translate(KuraMissingPayload kuraMissingPayload) {
        KapuaMissingPayload kapuaMissingPayload = new KapuaMissingPayloadImpl();
        kapuaMissingPayload.setBody(kuraMissingPayload.getBody());
        kapuaMissingPayload.setMetrics(kuraMissingPayload.getMetrics());
        return kapuaMissingPayload;
    }

    @Override
    public Class<KuraMissingMessage> getClassFrom() {
        return KuraMissingMessage.class;
    }

    @Override
    public Class<KapuaMissingMessage> getClassTo() {
        return KapuaMissingMessage.class;
    }
}
