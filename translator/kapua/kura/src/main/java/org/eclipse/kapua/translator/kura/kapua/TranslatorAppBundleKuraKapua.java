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

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.kapua.service.device.call.kura.model.bundle.BundleMetrics;
import org.eclipse.kapua.service.device.call.kura.model.bundle.KuraBundles;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponsePayload;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundle;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundles;
import org.eclipse.kapua.service.device.management.bundle.message.internal.BundleResponseChannel;
import org.eclipse.kapua.service.device.management.bundle.message.internal.BundleResponseMessage;
import org.eclipse.kapua.service.device.management.bundle.message.internal.BundleResponsePayload;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;

/**
 * {@link org.eclipse.kapua.translator.Translator} implementation from {@link KuraResponseMessage} to {@link BundleResponseMessage}
 *
 * @since 1.0.0
 */
public class TranslatorAppBundleKuraKapua extends AbstractSimpleTranslatorResponseKuraKapua<BundleResponseChannel, BundleResponsePayload, BundleResponseMessage> {

    @Inject
    public TranslatorAppBundleKuraKapua(DeviceManagementSetting deviceManagementSetting) {
        super(deviceManagementSetting, BundleResponseMessage.class, BundleResponsePayload.class);
    }

    @Override
    protected BundleResponseChannel translateChannel(KuraResponseChannel kuraResponseChannel) throws InvalidChannelException {
        try {
            translatorKuraKapuaUtils.validateKuraResponseChannel(kuraResponseChannel, BundleMetrics.APP_ID, BundleMetrics.APP_VERSION);

            return new BundleResponseChannel();
        } catch (Exception e) {
            throw new InvalidChannelException(e, kuraResponseChannel);
        }
    }

    @Override
    protected BundleResponsePayload translatePayload(KuraResponsePayload kuraResponsePayload) throws InvalidPayloadException {
        BundleResponsePayload bundleResponsePayload = super.translatePayload(kuraResponsePayload);

        try {
            if (kuraResponsePayload.hasBody()) {
                KuraBundles kuraBundles = readXmlBodyAs(kuraResponsePayload.getBody(), KuraBundles.class);

                bundleResponsePayload.setDeviceBundles(translate(kuraBundles));
            }

            return bundleResponsePayload;
        } catch (Exception e) {
            throw new InvalidPayloadException(e, kuraResponsePayload);
        }
    }

    /**
     * Translates {@link KuraBundles} to {@link DeviceBundles}
     *
     * @param kuraBundles
     *         The {@link KuraBundles} to translate.
     * @return The translated {@link DeviceBundles}.
     * @since 1.0.0
     */
    private DeviceBundles translate(KuraBundles kuraBundles) {
        DeviceBundles deviceBundles = new DeviceBundles();
        List<DeviceBundle> deviceBundlesList = deviceBundles.getBundles();

        Arrays.stream(kuraBundles.getBundles()).forEach(kuraBundle -> {
            DeviceBundle deviceBundle = new DeviceBundle();
            deviceBundle.setId(kuraBundle.getId());
            deviceBundle.setName(kuraBundle.getName());
            deviceBundle.setVersion(kuraBundle.getVersion());
            deviceBundle.setState(kuraBundle.getState());

            deviceBundlesList.add(deviceBundle);
        });

        return deviceBundles;
    }
}
