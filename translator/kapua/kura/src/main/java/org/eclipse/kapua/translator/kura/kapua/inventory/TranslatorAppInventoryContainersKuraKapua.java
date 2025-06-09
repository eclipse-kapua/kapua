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
package org.eclipse.kapua.translator.kura.kapua.inventory;

import javax.inject.Inject;

import org.eclipse.kapua.service.device.call.kura.model.inventory.containers.KuraInventoryContainers;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponsePayload;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryContainersResponseMessage;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryResponsePayload;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;

/**
 * {@link Translator} implementation from {@link KuraResponseMessage} to {@link InventoryContainersResponseMessage}
 *
 * @since 2.0.0
 */
public class TranslatorAppInventoryContainersKuraKapua extends AbstractTranslatorAppInventoryKuraKapua<InventoryContainersResponseMessage> {

    /**
     * Constructor.
     *
     * @since 2.0.0
     */
    @Inject
    public TranslatorAppInventoryContainersKuraKapua(DeviceManagementSetting deviceManagementSetting) {
        super(deviceManagementSetting, InventoryContainersResponseMessage.class);
    }

    @Override
    protected InventoryResponsePayload translatePayload(KuraResponsePayload kuraResponsePayload) throws InvalidPayloadException {
        try {
            InventoryResponsePayload inventoryResponsePayload = super.translatePayload(kuraResponsePayload);

            if (kuraResponsePayload.hasBody()) {
                KuraInventoryContainers inventoryContainers = readJsonBodyAs(kuraResponsePayload.getBody(), KuraInventoryContainers.class);

                if (!inventoryContainers.getInventoryContainers().isEmpty()) {
                    inventoryResponsePayload.setDeviceInventoryContainers(translate(inventoryContainers));
                }
            }

            return inventoryResponsePayload;
        } catch (Exception e) {
            throw new InvalidPayloadException(e, kuraResponsePayload);
        }
    }
}
