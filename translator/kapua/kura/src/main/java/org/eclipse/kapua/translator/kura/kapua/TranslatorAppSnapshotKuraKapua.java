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

import java.util.List;

import javax.inject.Inject;

import org.eclipse.kapua.service.device.call.kura.model.snapshot.KuraSnapshotIds;
import org.eclipse.kapua.service.device.call.kura.model.snapshot.SnapshotMetrics;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponsePayload;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.snapshot.DeviceSnapshot;
import org.eclipse.kapua.service.device.management.snapshot.DeviceSnapshots;
import org.eclipse.kapua.service.device.management.snapshot.message.internal.SnapshotResponseChannel;
import org.eclipse.kapua.service.device.management.snapshot.message.internal.SnapshotResponseMessage;
import org.eclipse.kapua.service.device.management.snapshot.message.internal.SnapshotResponsePayload;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;

/**
 * {@link Translator} implementation from {@link KuraResponseMessage} to {@link SnapshotResponseMessage}
 *
 * @since 1.0.0
 */
public class TranslatorAppSnapshotKuraKapua extends AbstractSimpleTranslatorResponseKuraKapua<SnapshotResponseChannel, SnapshotResponsePayload, SnapshotResponseMessage> {

    @Inject
    public TranslatorAppSnapshotKuraKapua(DeviceManagementSetting deviceManagementSetting) {
        super(deviceManagementSetting, SnapshotResponseMessage.class, SnapshotResponsePayload.class);
    }

    @Override
    protected SnapshotResponseChannel translateChannel(KuraResponseChannel kuraResponseChannel) throws InvalidChannelException {
        try {
            translatorKuraKapuaUtils.validateKuraResponseChannel(kuraResponseChannel, SnapshotMetrics.APP_ID, SnapshotMetrics.APP_VERSION);

            return new SnapshotResponseChannel();
        } catch (Exception e) {
            throw new InvalidChannelException(e, kuraResponseChannel);
        }
    }

    @Override
    protected SnapshotResponsePayload translatePayload(KuraResponsePayload kuraResponsePayload) throws InvalidPayloadException {
        SnapshotResponsePayload snapshotResponsePayload = super.translatePayload(kuraResponsePayload);

        try {

            if (kuraResponsePayload.hasBody()) {
                KuraSnapshotIds kuraSnapshotIds = readXmlBodyAs(kuraResponsePayload.getBody(), KuraSnapshotIds.class);
                snapshotResponsePayload.setDeviceSnapshots(translate(kuraSnapshotIds));
            }

            return snapshotResponsePayload;
        } catch (InvalidPayloadException ipe) {
            throw ipe;
        } catch (Exception e) {
            throw new InvalidPayloadException(e, kuraResponsePayload);
        }
    }

    /**
     * Translates {@link KuraSnapshotIds} in {@link DeviceSnapshots}
     *
     * @param kuraSnapshotIdResult
     *         The {@link KuraSnapshotIds} to translate.
     * @return The translated {@link DeviceSnapshots}
     * @since 1.0.0
     */
    private DeviceSnapshots translate(KuraSnapshotIds kuraSnapshotIdResult) {
        DeviceSnapshots deviceSnapshots = new DeviceSnapshots();

        List<Long> snapshotIds = kuraSnapshotIdResult.getSnapshotIds();
        for (Long snapshotId : snapshotIds) {
            DeviceSnapshot snapshot = new DeviceSnapshot();
            snapshot.setId(Long.toString(snapshotId));
            snapshot.setTimestamp(snapshotId);

            deviceSnapshots.getSnapshots().add(snapshot);
        }

        return deviceSnapshots;
    }
}
