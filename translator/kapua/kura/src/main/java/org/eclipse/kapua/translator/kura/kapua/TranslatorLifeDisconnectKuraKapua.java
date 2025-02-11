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

import javax.inject.Inject;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.message.device.lifecycle.KapuaDisconnectChannel;
import org.eclipse.kapua.message.device.lifecycle.KapuaDisconnectMessage;
import org.eclipse.kapua.message.device.lifecycle.KapuaDisconnectPayload;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraDisconnectChannel;
import org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraDisconnectMessage;
import org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraDisconnectPayload;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.exception.InvalidMessageException;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;
import org.eclipse.kapua.translator.exception.TranslateException;

/**
 * {@link Translator} implementation from {@link KuraDisconnectMessage} to {@link KapuaDisconnectMessage}
 *
 * @since 1.0.0
 */
public class TranslatorLifeDisconnectKuraKapua extends Translator<KuraDisconnectMessage, KapuaDisconnectMessage> {

    @Inject
    private AccountService accountService;
    @Inject
    private DeviceRegistryService deviceRegistryService;
    @Inject
    private TranslatorKuraKapuaUtils translatorKuraKapuaUtils;

    @Override
    public KapuaDisconnectMessage translate(KuraDisconnectMessage kuraDisconnectMessage) throws TranslateException {
        try {
            KapuaDisconnectMessage kapuaDisconnectMessage = new KapuaDisconnectMessage();
            kapuaDisconnectMessage.setChannel(translate(kuraDisconnectMessage.getChannel()));
            kapuaDisconnectMessage.setPayload(translate(kuraDisconnectMessage.getPayload()));

            Account account = accountService.findByName(kuraDisconnectMessage.getChannel().getScope());
            if (account == null) {
                throw new KapuaEntityNotFoundException(Account.TYPE, kuraDisconnectMessage.getChannel().getScope());
            }

            Device device = deviceRegistryService.findByClientId(account.getId(), kuraDisconnectMessage.getChannel().getClientId());
            if (device == null) {
                throw new KapuaEntityNotFoundException(Device.class.toString(), kuraDisconnectMessage.getChannel().getClientId());
            }

            kapuaDisconnectMessage.setDeviceId(device.getId());
            kapuaDisconnectMessage.setScopeId(account.getId());
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
        KapuaDisconnectChannel kapuaDisconnectChannel = new KapuaDisconnectChannel();
        kapuaDisconnectChannel.setClientId(kuraDisconnectChannel.getClientId());
        return kapuaDisconnectChannel;
    }

    private KapuaDisconnectPayload translate(KuraDisconnectPayload kuraDisconnectPayload) {
        return new KapuaDisconnectPayload(kuraDisconnectPayload.getUptime(), kuraDisconnectPayload.getDisplayName());
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
