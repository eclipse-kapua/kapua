/*******************************************************************************
 * Copyright (c) 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.translator.kapua.kura.inventory;

import org.eclipse.kapua.service.device.call.kura.model.inventory.InventoryMetrics;
import org.eclipse.kapua.service.device.call.kura.model.inventory.images.KuraInventoryImage;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestPayload;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.inventory.DeviceInventoryManagementFactory;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryImageDeleteRequestMessage;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryRequestChannel;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryRequestPayload;
import org.eclipse.kapua.service.device.management.inventory.model.image.DeviceInventoryImage;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;
import org.eclipse.kapua.translator.kapua.kura.AbstractTranslatorKapuaKura;
import org.eclipse.kapua.translator.kapua.kura.TranslatorKapuaKuraUtils;

import javax.inject.Inject;

public class TranslatorAppInventoryImageDeleteKapuaKura extends AbstractTranslatorKapuaKura<InventoryRequestChannel, InventoryRequestPayload, InventoryImageDeleteRequestMessage> {

    private final String charEncoding;
    private final DeviceInventoryManagementFactory deviceInventoryManagementFactory;

    @Inject
    public TranslatorAppInventoryImageDeleteKapuaKura(DeviceManagementSetting deviceManagementSetting, DeviceInventoryManagementFactory deviceInventoryManagementFactory) {
        this.deviceInventoryManagementFactory = deviceInventoryManagementFactory;
        this.charEncoding = deviceManagementSetting.getString(DeviceManagementSettingKey.CHAR_ENCODING);
    }

    @Override
    protected KuraRequestChannel translateChannel(InventoryRequestChannel inventoryRequestChannel) throws InvalidChannelException {
        try {
            KuraRequestChannel kuraRequestChannel = TranslatorKapuaKuraUtils.buildBaseRequestChannel(InventoryMetrics.APP_ID, InventoryMetrics.APP_VERSION, inventoryRequestChannel.getMethod());
            kuraRequestChannel.setResources(new String[]{inventoryRequestChannel.getResource()});
            // Return Kura Channel
            return kuraRequestChannel;
        } catch (Exception e) {
            throw new InvalidChannelException(e, inventoryRequestChannel);
        }
    }

    @Override
    protected KuraRequestPayload translatePayload(InventoryRequestPayload inventoryRequestPayload) throws InvalidPayloadException {
        try {
            KuraRequestPayload kuraRequestPayload = new KuraRequestPayload();

            if (inventoryRequestPayload.hasBody()) {
                DeviceInventoryImage deviceInventoryImage = inventoryRequestPayload.getDeviceInventoryImage().orElse(deviceInventoryManagementFactory.newDeviceInventoryImage());

                KuraInventoryImage kuraInventoryImage = new KuraInventoryImage();
                kuraInventoryImage.setName(deviceInventoryImage.getName());
                kuraInventoryImage.setVersion(deviceInventoryImage.getVersion());
                kuraInventoryImage.setType(deviceInventoryImage.getImageType());

                kuraRequestPayload.setBody(getJsonMapper().writeValueAsString(kuraInventoryImage).getBytes(charEncoding));
            }

            return kuraRequestPayload;
        } catch (Exception e) {
            throw new InvalidPayloadException(e, inventoryRequestPayload);
        }
    }

    @Override
    public Class<InventoryImageDeleteRequestMessage> getClassFrom() {
        return InventoryImageDeleteRequestMessage.class;
    }

    @Override
    public Class<KuraRequestMessage> getClassTo() {
        return KuraRequestMessage.class;
    }

}
