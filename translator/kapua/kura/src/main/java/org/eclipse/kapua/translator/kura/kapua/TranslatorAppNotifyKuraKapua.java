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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.model.id.KapuaIdFactory;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.kura.model.asset.AssetMetrics;
import org.eclipse.kapua.service.device.call.kura.model.bundle.BundleMetrics;
import org.eclipse.kapua.service.device.call.kura.model.command.CommandMetrics;
import org.eclipse.kapua.service.device.call.kura.model.configuration.ConfigurationMetrics;
import org.eclipse.kapua.service.device.call.kura.model.deploy.PackageMetrics;
import org.eclipse.kapua.service.device.call.message.kura.app.notification.KuraNotifyChannel;
import org.eclipse.kapua.service.device.call.message.kura.app.notification.KuraNotifyMessage;
import org.eclipse.kapua.service.device.call.message.kura.app.notification.KuraNotifyPayload;
import org.eclipse.kapua.service.device.management.asset.internal.DeviceAssetAppProperties;
import org.eclipse.kapua.service.device.management.bundle.internal.DeviceBundleAppProperties;
import org.eclipse.kapua.service.device.management.command.internal.CommandAppProperties;
import org.eclipse.kapua.service.device.management.configuration.internal.DeviceConfigurationAppProperties;
import org.eclipse.kapua.service.device.management.message.KapuaAppProperties;
import org.eclipse.kapua.service.device.management.message.notification.KapuaNotifyChannel;
import org.eclipse.kapua.service.device.management.message.notification.KapuaNotifyMessage;
import org.eclipse.kapua.service.device.management.message.notification.KapuaNotifyPayload;
import org.eclipse.kapua.service.device.management.message.notification.NotifyStatus;
import org.eclipse.kapua.service.device.management.packages.message.internal.PackageAppProperties;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.exception.InvalidMessageException;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;
import org.eclipse.kapua.translator.exception.TranslateException;

/**
 * {@link Translator} implementation from {@link KuraNotifyMessage} to {@link KapuaNotifyMessage}
 *
 * @since 1.0.0
 */
public class TranslatorAppNotifyKuraKapua extends Translator<KuraNotifyMessage, KapuaNotifyMessage> {

    @Inject
    private AccountService accountService;
    @Inject
    private DeviceRegistryService deviceRegistryService;
    @Inject
    private KapuaIdFactory kapuaIdFactory;
    @Inject
    private TranslatorKuraKapuaUtils translatorKuraKapuaUtils;

    private final Map<String, KapuaAppProperties> appNameDictionary;
    private final Map<String, KapuaAppProperties> appVersionDictionary;

    public TranslatorAppNotifyKuraKapua() {
        appNameDictionary = new HashMap<>();

        appNameDictionary.put(AssetMetrics.APP_ID.getName(), DeviceAssetAppProperties.APP_NAME);
        appNameDictionary.put(BundleMetrics.APP_ID.getName(), DeviceBundleAppProperties.APP_NAME);
        appNameDictionary.put(CommandMetrics.APP_ID.getName(), CommandAppProperties.APP_NAME);
        appNameDictionary.put(ConfigurationMetrics.APP_ID.getName(), DeviceConfigurationAppProperties.APP_NAME);
        appNameDictionary.put(PackageMetrics.APP_ID.getName(), PackageAppProperties.APP_NAME);

        appVersionDictionary = new HashMap<>();

        appVersionDictionary.put(AssetMetrics.APP_ID.getName(), DeviceAssetAppProperties.APP_VERSION);
        appVersionDictionary.put(BundleMetrics.APP_ID.getName(), DeviceBundleAppProperties.APP_VERSION);
        appVersionDictionary.put(CommandMetrics.APP_ID.getName(), CommandAppProperties.APP_VERSION);
        appVersionDictionary.put(ConfigurationMetrics.APP_ID.getName(), DeviceConfigurationAppProperties.APP_VERSION);
        appVersionDictionary.put(PackageMetrics.APP_ID.getName(), PackageAppProperties.APP_VERSION);
    }

    @Override
    public KapuaNotifyMessage translate(KuraNotifyMessage kuraNotifyMessage) throws TranslateException {

        try {
            KapuaNotifyMessage kapuaNotifyMessage = new KapuaNotifyMessage();
            kapuaNotifyMessage.setChannel(translate(kuraNotifyMessage.getChannel()));
            kapuaNotifyMessage.setPayload(translate(kuraNotifyMessage.getPayload()));

            Account account = accountService.findByName(kuraNotifyMessage.getChannel().getScope());
            if (account == null) {
                throw new KapuaEntityNotFoundException(Account.TYPE, kuraNotifyMessage.getChannel().getScope());
            }

            Device device = deviceRegistryService.findByClientId(account.getId(), kuraNotifyMessage.getChannel().getClientId());
            if (device == null) {
                throw new KapuaEntityNotFoundException(Device.class.toString(), kuraNotifyMessage.getChannel().getClientId());
            }

            kapuaNotifyMessage.setDeviceId(device.getId());
            kapuaNotifyMessage.setScopeId(account.getId());
            kapuaNotifyMessage.setCapturedOn(kuraNotifyMessage.getPayload().getTimestamp());
            kapuaNotifyMessage.setSentOn(kuraNotifyMessage.getPayload().getTimestamp());
            kapuaNotifyMessage.setReceivedOn(kuraNotifyMessage.getTimestamp());
            kapuaNotifyMessage.setPosition(translatorKuraKapuaUtils.translate(kuraNotifyMessage.getPayload().getPosition()));

            return kapuaNotifyMessage;
        } catch (InvalidChannelException | InvalidPayloadException te) {
            throw te;
        } catch (Exception e) {
            throw new InvalidMessageException(e, kuraNotifyMessage);
        }
    }

    private KapuaNotifyChannel translate(KuraNotifyChannel kuraNotifyChannel) throws InvalidChannelException {
        try {
            String kuraAppIdName = kuraNotifyChannel.getAppId().split("-")[0];
            String kuraAppIdVersion = kuraNotifyChannel.getAppId().split("-")[1];

            KapuaNotifyChannel kapuaNotifyChannel = new KapuaNotifyChannel();
            kapuaNotifyChannel.setAppName(appNameDictionary.get(kuraAppIdName));
            kapuaNotifyChannel.setVersion(appVersionDictionary.get(kuraAppIdVersion));
            kapuaNotifyChannel.setResources(kuraNotifyChannel.getResources());

            return kapuaNotifyChannel;
        } catch (Exception e) {
            throw new InvalidChannelException(e, kuraNotifyChannel);
        }
    }

    private KapuaNotifyPayload translate(KuraNotifyPayload kuraNotifyPayload) throws InvalidPayloadException {
        try {
            KapuaNotifyPayload kapuaNotifyPayload = new KapuaNotifyPayload();

            kapuaNotifyPayload.setOperationId(kapuaIdFactory.newKapuaId(new BigInteger(kuraNotifyPayload.getOperationId().toString())));
            kapuaNotifyPayload.setResource(kuraNotifyPayload.getResource());
            kapuaNotifyPayload.setProgress(kuraNotifyPayload.getProgress());

            switch (kuraNotifyPayload.getStatus()) {
            case "IN_PROGRESS":
                kapuaNotifyPayload.setStatus(NotifyStatus.RUNNING);
                break;
            case "COMPLETED":
                kapuaNotifyPayload.setStatus(NotifyStatus.COMPLETED);
                break;
            case "FAILED":
                kapuaNotifyPayload.setStatus(NotifyStatus.FAILED);
                break;
            }

            kapuaNotifyPayload.setMessage(kuraNotifyPayload.getMessage());

            return kapuaNotifyPayload;
        } catch (Exception e) {
            throw new InvalidPayloadException(e, kuraNotifyPayload);
        }
    }

    @Override
    public Class<KuraNotifyMessage> getClassFrom() {
        return KuraNotifyMessage.class;
    }

    @Override
    public Class<KapuaNotifyMessage> getClassTo() {
        return KapuaNotifyMessage.class;
    }
}
