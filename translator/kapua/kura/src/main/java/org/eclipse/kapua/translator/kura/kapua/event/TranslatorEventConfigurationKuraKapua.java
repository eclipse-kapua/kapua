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
package org.eclipse.kapua.translator.kura.kapua.event;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.kura.model.asset.AssetMetrics;
import org.eclipse.kapua.service.device.call.kura.model.bundle.BundleMetrics;
import org.eclipse.kapua.service.device.call.kura.model.command.CommandMetrics;
import org.eclipse.kapua.service.device.call.kura.model.configuration.ConfigurationMetrics;
import org.eclipse.kapua.service.device.call.kura.model.configuration.KuraDeviceComponentConfiguration;
import org.eclipse.kapua.service.device.call.kura.model.deploy.PackageMetrics;
import org.eclipse.kapua.service.device.call.message.kura.event.configuration.KuraConfigurationEventChannel;
import org.eclipse.kapua.service.device.call.message.kura.event.configuration.KuraConfigurationEventMessage;
import org.eclipse.kapua.service.device.call.message.kura.event.configuration.KuraConfigurationEventPayload;
import org.eclipse.kapua.service.device.management.asset.internal.DeviceAssetAppProperties;
import org.eclipse.kapua.service.device.management.bundle.internal.DeviceBundleAppProperties;
import org.eclipse.kapua.service.device.management.command.internal.CommandAppProperties;
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.internal.DeviceConfigurationAppProperties;
import org.eclipse.kapua.service.device.management.configuration.message.event.DeviceConfigurationEventMessage;
import org.eclipse.kapua.service.device.management.configuration.message.event.internal.DeviceConfigurationEventChannelImpl;
import org.eclipse.kapua.service.device.management.configuration.message.event.internal.DeviceConfigurationEventMessageImpl;
import org.eclipse.kapua.service.device.management.configuration.message.event.internal.DeviceConfigurationEventPayloadImpl;
import org.eclipse.kapua.service.device.management.message.KapuaAppProperties;
import org.eclipse.kapua.service.device.management.packages.message.internal.PackageAppProperties;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.InvalidBodyContentException;
import org.eclipse.kapua.translator.exception.InvalidBodyEncodingException;
import org.eclipse.kapua.translator.exception.InvalidBodyException;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.exception.InvalidMessageException;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;
import org.eclipse.kapua.translator.exception.TranslateException;
import org.eclipse.kapua.translator.kura.kapua.TranslatorKuraKapuaUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * {@link Translator} implementation from {@link KuraConfigurationEventMessage} to {@link DeviceConfigurationEventMessageImpl}
 *
 * @since 2.0.0
 */
public class TranslatorEventConfigurationKuraKapua extends Translator<KuraConfigurationEventMessage, DeviceConfigurationEventMessage> {

    @Inject
    private AccountService accountService;
    @Inject
    private DeviceRegistryService deviceRegistryService;
    @Inject
    private TranslatorKuraKapuaUtils translatorKuraKapuaUtils;

    private final Map<String, KapuaAppProperties> appNameDictionary;
    private final Map<String, KapuaAppProperties> appVersionDictionary;

    public TranslatorEventConfigurationKuraKapua() {
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
    public DeviceConfigurationEventMessage translate(KuraConfigurationEventMessage kuraNotifyMessage) throws TranslateException {
        try {
            DeviceConfigurationEventMessage deviceConfigurationEventMessage = new DeviceConfigurationEventMessageImpl();
            deviceConfigurationEventMessage.setChannel(translate(kuraNotifyMessage.getChannel()));
            deviceConfigurationEventMessage.setPayload(translate(kuraNotifyMessage.getPayload()));

            Account account = accountService.findByName(kuraNotifyMessage.getChannel().getScope());
            if (account == null) {
                throw new KapuaEntityNotFoundException(Account.TYPE, kuraNotifyMessage.getChannel().getScope());
            }

            Device device = deviceRegistryService.findByClientId(account.getId(), kuraNotifyMessage.getChannel().getClientId());
            if (device == null) {
                throw new KapuaEntityNotFoundException(Device.class.toString(), kuraNotifyMessage.getChannel().getClientId());
            }

            deviceConfigurationEventMessage.setDeviceId(device.getId());
            deviceConfigurationEventMessage.setScopeId(account.getId());
            deviceConfigurationEventMessage.setCapturedOn(kuraNotifyMessage.getPayload().getTimestamp());
            deviceConfigurationEventMessage.setSentOn(kuraNotifyMessage.getPayload().getTimestamp());
            deviceConfigurationEventMessage.setReceivedOn(kuraNotifyMessage.getTimestamp());
            deviceConfigurationEventMessage.setPosition(translatorKuraKapuaUtils.translate(kuraNotifyMessage.getPayload().getPosition()));

            return deviceConfigurationEventMessage;
        } catch (InvalidChannelException | InvalidPayloadException te) {
            throw te;
        } catch (Exception e) {
            throw new InvalidMessageException(e, kuraNotifyMessage);
        }
    }

    private DeviceConfigurationEventChannelImpl translate(KuraConfigurationEventChannel kuraConfigurationEventChannel) throws InvalidChannelException {
        try {
            String kuraAppIdName = kuraConfigurationEventChannel.getAppName();
            String kuraAppIdVersion = kuraConfigurationEventChannel.getAppVersion();

            DeviceConfigurationEventChannelImpl configurationEventChannel = new DeviceConfigurationEventChannelImpl();
            configurationEventChannel.setAppName(kuraAppIdName);
            configurationEventChannel.setAppVersion(kuraAppIdVersion);
            configurationEventChannel.setResources(kuraConfigurationEventChannel.getResources());

            return configurationEventChannel;
        } catch (Exception e) {
            throw new InvalidChannelException(e, kuraConfigurationEventChannel);
        }
    }

    private DeviceConfigurationEventPayloadImpl translate(KuraConfigurationEventPayload kuraConfigurationEventPayload) throws InvalidPayloadException {
        try {
            DeviceConfigurationEventPayloadImpl configurationEventPayload = new DeviceConfigurationEventPayloadImpl();

            if (kuraConfigurationEventPayload.hasBody()) {
                KuraDeviceComponentConfiguration[] kuraDeviceComponentConfigurations = readJsonBodyAs(kuraConfigurationEventPayload.getBody(), KuraDeviceComponentConfiguration[].class);

                List<DeviceComponentConfiguration> deviceComponentConfigurations = new ArrayList<>();
                for (KuraDeviceComponentConfiguration kuraDeviceComponentConfiguration : kuraDeviceComponentConfigurations) {

                    deviceComponentConfigurations.add(translate(kuraDeviceComponentConfiguration));
                }
                final DeviceConfiguration deviceConfiguration = new DeviceConfiguration();
                deviceConfiguration.setComponentConfigurations(deviceComponentConfigurations);
                configurationEventPayload.setDeviceComponentConfigurations(deviceConfiguration);
            }

            return configurationEventPayload;
        } catch (Exception e) {
            throw new InvalidPayloadException(e, kuraConfigurationEventPayload);
        }
    }

    /**
     * Translates a {@link KuraDeviceComponentConfiguration} to a {@link DeviceComponentConfiguration}.
     * <p>
     * It currently translates only {@link DeviceComponentConfiguration#getId()}
     *
     * @param kuraDeviceComponentConfiguration
     *         The {@link KuraDeviceComponentConfiguration} to translate.
     * @return The translated {@link DeviceComponentConfiguration}.
     * @since 2.0.0
     */
    private DeviceComponentConfiguration translate(KuraDeviceComponentConfiguration kuraDeviceComponentConfiguration) {
        return new DeviceComponentConfiguration(kuraDeviceComponentConfiguration.getComponentId());
    }

    @Override
    public Class<KuraConfigurationEventMessage> getClassFrom() {
        return KuraConfigurationEventMessage.class;
    }

    @Override
    public Class<DeviceConfigurationEventMessage> getClassTo() {
        return DeviceConfigurationEventMessage.class;
    }
    // Things copied from AbstractSimpleTranslatorResponseKuraKapua than need to be refactored

    //TODO: FIXME: promote following methods as an external, injectable collaborator
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

    protected <T> T readJsonBodyAs(@NotNull byte[] bytesToRead, @NotNull Class<T> returnAs) throws InvalidBodyException {
        String bodyString = readBodyAsString(bytesToRead, "UTF-8");

        try {
            return getJsonMapper().readValue(bodyString, returnAs);
        } catch (Exception e) {
            throw new InvalidBodyContentException(e, returnAs, bytesToRead);
        }
    }

    protected String readBodyAsString(@NotNull byte[] body, @NotNull String encoding) throws InvalidBodyEncodingException {
        try {
            return new String(body, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new InvalidBodyEncodingException(e, encoding, body);
        }
    }

    protected ObjectMapper getJsonMapper() {
        return JSON_MAPPER;
    }
}
