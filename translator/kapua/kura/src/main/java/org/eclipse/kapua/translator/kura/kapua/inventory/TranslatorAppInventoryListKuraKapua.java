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
package org.eclipse.kapua.translator.kura.kapua.inventory;

import javax.inject.Inject;

import org.eclipse.kapua.service.device.call.kura.model.inventory.KuraInventoryItems;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponsePayload;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryListResponseMessage;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryResponsePayload;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;

/**
 * {@link Translator} implementation from {@link KuraResponseMessage} to {@link InventoryListResponseMessage}
 *
 * @since 1.5.0
 */
public class TranslatorAppInventoryListKuraKapua extends AbstractTranslatorAppInventoryKuraKapua<InventoryListResponseMessage> {

    /**
     * Constructor.
     *
     * @since 1.5.0
     */
    @Inject
    public TranslatorAppInventoryListKuraKapua(DeviceManagementSetting deviceManagementSetting) {
        super(deviceManagementSetting, InventoryListResponseMessage.class);
    }

    @Override
    protected InventoryResponsePayload translatePayload(KuraResponsePayload kuraResponsePayload) throws InvalidPayloadException {
        try {
            InventoryResponsePayload inventoryResponsePayload = super.translatePayload(kuraResponsePayload);

            if (kuraResponsePayload.hasBody()) {
                KuraInventoryItems inventoryItems = readJsonBodyAs(kuraResponsePayload.getBody(), KuraInventoryItems.class);

                if (!inventoryItems.getInventoryItems().isEmpty()) {
                    inventoryResponsePayload.setDeviceInventory(translate(inventoryItems));
                }
            }

            return inventoryResponsePayload;
        } catch (Exception e) {
            throw new InvalidPayloadException(e, kuraResponsePayload);
        }
    }
}
