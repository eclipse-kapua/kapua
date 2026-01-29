/*******************************************************************************
 * Copyright (c) 2021, 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.translator.kura.kapua.inventory;

import org.eclipse.kapua.service.device.call.kura.model.inventory.images.KuraInventoryImages;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponsePayload;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.inventory.DeviceInventoryManagementFactory;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryImagesResponseMessage;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryResponsePayload;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;

import javax.inject.Inject;

public class TranslatorAppInventoryImagesKuraKapua extends AbstractTranslatorAppInventoryKuraKapua<InventoryImagesResponseMessage> {

    /**
     * Constructor.
     *
     * @since 2.0.0
     */
    @Inject
    public TranslatorAppInventoryImagesKuraKapua(DeviceManagementSetting deviceManagementSetting, DeviceInventoryManagementFactory deviceInventoryManagementFactory) {
        super(deviceManagementSetting, deviceInventoryManagementFactory, InventoryImagesResponseMessage.class);
    }

    @Override
    protected InventoryResponsePayload translatePayload(KuraResponsePayload kuraResponsePayload) throws InvalidPayloadException {
        try {
            InventoryResponsePayload inventoryResponsePayload = super.translatePayload(kuraResponsePayload);

            if (kuraResponsePayload.hasBody()) {
                KuraInventoryImages inventoryContainers = readJsonBodyAs(kuraResponsePayload.getBody(), KuraInventoryImages.class);

                if (!inventoryContainers.getInventoryImages().isEmpty()) {
                    inventoryResponsePayload.setDeviceInventoryImages(translate(inventoryContainers));
                }
            }

            return inventoryResponsePayload;
        } catch (Exception e) {
            throw new InvalidPayloadException(e, kuraResponsePayload);
        }
    }
}
