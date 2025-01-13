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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.translator.kura.kapua;

import java.util.Date;

import javax.inject.Inject;

import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.service.device.call.kura.model.asset.AssetMetrics;
import org.eclipse.kapua.service.device.call.kura.model.asset.KuraAssetChannelMode;
import org.eclipse.kapua.service.device.call.kura.model.asset.KuraAssets;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponsePayload;
import org.eclipse.kapua.service.device.management.asset.DeviceAsset;
import org.eclipse.kapua.service.device.management.asset.DeviceAssetChannel;
import org.eclipse.kapua.service.device.management.asset.DeviceAssetChannelMode;
import org.eclipse.kapua.service.device.management.asset.DeviceAssetFactory;
import org.eclipse.kapua.service.device.management.asset.DeviceAssets;
import org.eclipse.kapua.service.device.management.asset.message.internal.AssetResponseChannel;
import org.eclipse.kapua.service.device.management.asset.message.internal.AssetResponseMessage;
import org.eclipse.kapua.service.device.management.asset.message.internal.AssetResponsePayload;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * {@link org.eclipse.kapua.translator.Translator} implementation from {@link KuraResponseMessage} to {@link AssetResponseMessage}
 *
 * @since 1.0.0
 */
public class TranslatorAppAssetKuraKapua extends AbstractSimpleTranslatorResponseKuraKapua<AssetResponseChannel, AssetResponsePayload, AssetResponseMessage> {

    private final DeviceAssetFactory deviceAssetFactory;
    private final XmlUtil xmlUtil;

    @Inject
    public TranslatorAppAssetKuraKapua(DeviceManagementSetting deviceManagementSetting, DeviceAssetFactory deviceAssetFactory, XmlUtil xmlUtil) {
        super(deviceManagementSetting, AssetResponseMessage.class, AssetResponsePayload.class);
        this.deviceAssetFactory = deviceAssetFactory;
        this.xmlUtil = xmlUtil;
    }

    @Override
    protected AssetResponseChannel translateChannel(KuraResponseChannel kuraResponseChannel) throws InvalidChannelException {
        try {
            translatorKuraKapuaUtils.validateKuraResponseChannel(kuraResponseChannel, AssetMetrics.APP_ID, AssetMetrics.APP_VERSION);

            return new AssetResponseChannel();
        } catch (Exception e) {
            throw new InvalidChannelException(e, kuraResponseChannel);
        }
    }

    @Override
    protected AssetResponsePayload translatePayload(KuraResponsePayload kuraResponsePayload) throws InvalidPayloadException {
        AssetResponsePayload assetResponsePayload = super.translatePayload(kuraResponsePayload);

        try {
            if (kuraResponsePayload.hasBody()) {

                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(kuraResponsePayload.getBody());
                DeviceAssets deviceAssets = new DeviceAssets();
                KuraAssets kuraAssets = KuraAssets.readJsonNode(jsonNode);

                kuraAssets.getAssets().forEach(kuraAsset -> {
                    DeviceAsset deviceAsset = deviceAssetFactory.newDeviceAsset();
                    deviceAsset.setName(kuraAsset.getName());

                    kuraAsset.getChannels().forEach(kuraAssetChannel -> {
                        DeviceAssetChannel deviceAssetChannel = deviceAssetFactory.newDeviceAssetChannel();
                        deviceAssetChannel.setName(kuraAssetChannel.getName());
                        KuraAssetChannelMode kuraChannelMode = kuraAssetChannel.getMode();
                        if (kuraChannelMode != null) {
                            deviceAssetChannel.setMode(DeviceAssetChannelMode.valueOf(kuraChannelMode.name()));
                        }
                        deviceAssetChannel.setType(kuraAssetChannel.getType());
                        deviceAssetChannel.setValue(kuraAssetChannel.getValue());
                        Long kuraTimestamp = kuraAssetChannel.getTimestamp();
                        if (kuraTimestamp != null && kuraTimestamp > 0) {
                            deviceAssetChannel.setTimestamp(new Date(kuraAssetChannel.getTimestamp()));
                        }
                        deviceAssetChannel.setError(kuraAssetChannel.getError());
                        deviceAsset.getChannels().add(deviceAssetChannel);
                    });

                    deviceAssets.getAssets().add(deviceAsset);
                });

                assetResponsePayload.setBody(xmlUtil.marshal(deviceAssets).getBytes());
            }

            // Return Kapua Payload
            return assetResponsePayload;
        } catch (InvalidPayloadException ipe) {
            throw ipe;
        } catch (Exception e) {
            throw new InvalidPayloadException(e, kuraResponsePayload);
        }
    }
}
