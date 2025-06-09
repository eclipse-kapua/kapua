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
package org.eclipse.kapua.translator.kapua.kura.inventory;

import javax.inject.Inject;

import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.service.device.call.kura.model.inventory.InventoryMetrics;
import org.eclipse.kapua.service.device.call.kura.model.inventory.bundles.KuraInventoryBundle;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestPayload;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryBundleExecRequestMessage;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryRequestChannel;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryRequestPayload;
import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundle;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;
import org.eclipse.kapua.translator.kapua.kura.AbstractTranslatorKapuaKura;
import org.eclipse.kapua.translator.kapua.kura.TranslatorKapuaKuraUtils;

/**
 * {@link Translator} implementation from {@link InventoryBundleExecRequestMessage} to {@link KuraRequestMessage}
 *
 * @since 1.5.0
 */
public class TranslatorAppInventoryBundleExecKapuaKura extends AbstractTranslatorKapuaKura<InventoryRequestChannel, InventoryRequestPayload, InventoryBundleExecRequestMessage> {

    private final String charEncoding;

    @Inject
    public TranslatorAppInventoryBundleExecKapuaKura(DeviceManagementSetting deviceManagementSetting) {
        charEncoding = deviceManagementSetting.getString(DeviceManagementSettingKey.CHAR_ENCODING);
    }

    @Override
    protected KuraRequestChannel translateChannel(InventoryRequestChannel inventoryRequestChannel) throws InvalidChannelException {
        try {
            KuraRequestChannel kuraRequestChannel = TranslatorKapuaKuraUtils.buildBaseRequestChannel(InventoryMetrics.APP_ID, InventoryMetrics.APP_VERSION, inventoryRequestChannel.getMethod());

            switch (inventoryRequestChannel.getBundleAction()) {
            case START:
                kuraRequestChannel.setResources(new String[] { inventoryRequestChannel.getResource(), "_start" });
                break;
            case STOP:
                kuraRequestChannel.setResources(new String[] { inventoryRequestChannel.getResource(), "_stop" });
                break;
            default:
                throw new KapuaIllegalArgumentException("inventoryRequestChannel.bundleAction", inventoryRequestChannel.getBundleAction().name());
            }

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
                DeviceInventoryBundle deviceInventoryBundle = inventoryRequestPayload.getDeviceInventoryBundle().orElse(new DeviceInventoryBundle());

                KuraInventoryBundle kuraInventoryBundle = new KuraInventoryBundle();
                kuraInventoryBundle.setId(Integer.valueOf(deviceInventoryBundle.getId()));
                kuraInventoryBundle.setName(deviceInventoryBundle.getName());
                kuraInventoryBundle.setVersion(deviceInventoryBundle.getVersion());
                kuraInventoryBundle.setState(deviceInventoryBundle.getStatus());
                kuraInventoryBundle.setSigned(deviceInventoryBundle.getSigned());

                kuraRequestPayload.setBody(getJsonMapper().writeValueAsString(kuraInventoryBundle).getBytes(charEncoding));
            }

            return kuraRequestPayload;
        } catch (Exception e) {
            throw new InvalidPayloadException(e, inventoryRequestPayload);
        }
    }

    @Override
    public Class<InventoryBundleExecRequestMessage> getClassFrom() {
        return InventoryBundleExecRequestMessage.class;
    }

    @Override
    public Class<KuraRequestMessage> getClassTo() {
        return KuraRequestMessage.class;
    }

}
