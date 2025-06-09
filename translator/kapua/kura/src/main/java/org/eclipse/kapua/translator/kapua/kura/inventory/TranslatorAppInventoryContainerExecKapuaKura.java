/*******************************************************************************
 * Copyright (c) 2022 Eurotech and/or its affiliates and others
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
import org.eclipse.kapua.service.device.call.kura.model.inventory.containers.KuraInventoryContainer;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestPayload;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryContainerExecRequestMessage;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryRequestChannel;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryRequestPayload;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainer;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;
import org.eclipse.kapua.translator.kapua.kura.AbstractTranslatorKapuaKura;
import org.eclipse.kapua.translator.kapua.kura.TranslatorKapuaKuraUtils;

/**
 * {@link Translator} implementation from {@link InventoryContainerExecRequestMessage} to {@link KuraRequestMessage}
 *
 * @since 2.0.0
 */
public class TranslatorAppInventoryContainerExecKapuaKura extends AbstractTranslatorKapuaKura<InventoryRequestChannel, InventoryRequestPayload, InventoryContainerExecRequestMessage> {

    private final String charEncoding;

    @Inject
    public TranslatorAppInventoryContainerExecKapuaKura(DeviceManagementSetting deviceManagementSetting) {
        this.charEncoding = deviceManagementSetting.getString(DeviceManagementSettingKey.CHAR_ENCODING);
    }

    @Override
    protected KuraRequestChannel translateChannel(InventoryRequestChannel inventoryRequestChannel) throws InvalidChannelException {
        try {
            KuraRequestChannel kuraRequestChannel = TranslatorKapuaKuraUtils.buildBaseRequestChannel(InventoryMetrics.APP_ID, InventoryMetrics.APP_VERSION, inventoryRequestChannel.getMethod());

            switch (inventoryRequestChannel.getContainerAction()) {
            case START:
                kuraRequestChannel.setResources(new String[] { inventoryRequestChannel.getResource(), "_start" });
                break;
            case STOP:
                kuraRequestChannel.setResources(new String[] { inventoryRequestChannel.getResource(), "_stop" });
                break;
            default:
                throw new KapuaIllegalArgumentException("inventoryRequestChannel.containerAction", inventoryRequestChannel.getContainerAction().name());
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
                DeviceInventoryContainer deviceInventoryContainer = inventoryRequestPayload.getDeviceInventoryContainer().orElse(new DeviceInventoryContainer());

                KuraInventoryContainer kuraInventoryContainer = new KuraInventoryContainer();
                kuraInventoryContainer.setName(deviceInventoryContainer.getName());
                kuraInventoryContainer.setVersion(deviceInventoryContainer.getVersion());
                kuraInventoryContainer.setType(deviceInventoryContainer.getContainerType());
                if (deviceInventoryContainer.getState() != null) {
                    kuraInventoryContainer.setState(deviceInventoryContainer.getState().name());
                }

                kuraRequestPayload.setBody(getJsonMapper().writeValueAsString(kuraInventoryContainer).getBytes(charEncoding));
            }

            return kuraRequestPayload;
        } catch (Exception e) {
            throw new InvalidPayloadException(e, inventoryRequestPayload);
        }
    }

    @Override
    public Class<InventoryContainerExecRequestMessage> getClassFrom() {
        return InventoryContainerExecRequestMessage.class;
    }

    @Override
    public Class<KuraRequestMessage> getClassTo() {
        return KuraRequestMessage.class;
    }

}
