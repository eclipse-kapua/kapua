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
import org.eclipse.kapua.message.device.lifecycle.KapuaDisconnectChannel;
import org.eclipse.kapua.message.device.lifecycle.KapuaDisconnectMessage;
import org.eclipse.kapua.message.device.lifecycle.KapuaDisconnectPayload;
import org.eclipse.kapua.message.internal.device.lifecycle.KapuaDisconnectChannelImpl;
import org.eclipse.kapua.message.internal.device.lifecycle.KapuaDisconnectMessageImpl;
import org.eclipse.kapua.message.internal.device.lifecycle.KapuaDisconnectPayloadImpl;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraDisconnectChannel;
import org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraDisconnectMessage;
import org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraDisconnectPayload;
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
 * {@link Translator} implementation from {@link KuraDisconnectMessage} to {@link KapuaDisconnectMessage}
 *
 * @since 1.0.0
 */
public class TranslatorLifeDisconnectKuraKapua extends AbstractAccountResolvingTranslator<KuraDisconnectMessage, KapuaDisconnectMessage> {

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
    public TranslatorLifeDisconnectKuraKapua(
            AccountService accountService,
            @Named("accountsByNameCache") LocalCache<String, Account> accountsByNameCache,
            DeviceRegistryService deviceRegistryService,
            TranslatorKuraKapuaUtils translatorKuraKapuaUtils) {
        super(accountService, accountsByNameCache);
        this.deviceRegistryService = deviceRegistryService;
        this.translatorKuraKapuaUtils = translatorKuraKapuaUtils;
    }

    @Override
    public KapuaDisconnectMessage translate(KuraDisconnectMessage kuraDisconnectMessage) throws TranslateException {
        try {
            KapuaDisconnectMessage kapuaDisconnectMessage = new KapuaDisconnectMessageImpl();
            kapuaDisconnectMessage.setChannel(translate(kuraDisconnectMessage.getChannel()));
            kapuaDisconnectMessage.setPayload(translate(kuraDisconnectMessage.getPayload()));

            KapuaId accountId = findAccountIdByName(kuraDisconnectMessage.getChannel().getScope());
            if (accountId == null) {
                throw new KapuaEntityNotFoundException(Account.TYPE, kuraDisconnectMessage.getChannel().getScope());
            }

            Device device = deviceRegistryService.findByClientId(accountId, kuraDisconnectMessage.getChannel().getClientId());
            if (device == null) {
                throw new KapuaEntityNotFoundException(Device.class.toString(), kuraDisconnectMessage.getChannel().getClientId());
            }

            kapuaDisconnectMessage.setDeviceId(device.getId());
            kapuaDisconnectMessage.setScopeId(accountId);
            kapuaDisconnectMessage.setCapturedOn(kuraDisconnectMessage.getPayload().getTimestamp());
            kapuaDisconnectMessage.setSentOn(kuraDisconnectMessage.getPayload().getTimestamp());
            kapuaDisconnectMessage.setReceivedOn(kuraDisconnectMessage.getTimestamp());
            kapuaDisconnectMessage.setPosition(translatorKuraKapuaUtils.translate(kuraDisconnectMessage.getPayload().getPosition()));

            return kapuaDisconnectMessage;
        } catch (InvalidChannelException | InvalidPayloadException te) {
            throw te;
        } catch (Exception e) {
            throw new InvalidMessageException(e, kuraDisconnectMessage);
        }
    }

    private KapuaDisconnectChannel translate(KuraDisconnectChannel kuraDisconnectChannel) {
        KapuaDisconnectChannel kapuaDisconnectChannel = new KapuaDisconnectChannelImpl();
        kapuaDisconnectChannel.setClientId(kuraDisconnectChannel.getClientId());
        return kapuaDisconnectChannel;
    }

    private KapuaDisconnectPayload translate(KuraDisconnectPayload kuraDisconnectPayload) {
        return new KapuaDisconnectPayloadImpl(kuraDisconnectPayload.getUptime(), kuraDisconnectPayload.getDisplayName());
    }

    @Override
    public Class<KuraDisconnectMessage> getClassFrom() {
        return KuraDisconnectMessage.class;
    }

    @Override
    public Class<KapuaDisconnectMessage> getClassTo() {
        return KapuaDisconnectMessage.class;
    }

}
