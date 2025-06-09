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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.service.device.call.message.DevicePosition;
import org.eclipse.kapua.service.device.call.message.app.DeviceAppMetrics;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseCode;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponseCode;
import org.eclipse.kapua.translator.exception.TranslatorErrorCodes;
import org.eclipse.kapua.translator.exception.TranslatorException;

/**
 * {@link org.eclipse.kapua.translator.Translator} utilities.<br> It provides helpful methods for translate {@link DevicePosition} and {@link KuraResponseCode}.
 *
 * @since 1.0.0
 */
public final class TranslatorKuraKapuaUtilsImpl implements TranslatorKuraKapuaUtils {

    private final String controlMessageClassifier;

    public TranslatorKuraKapuaUtilsImpl(String controlMessageClassifier) {
        this.controlMessageClassifier = controlMessageClassifier;
    }

    /**
     * Validates the given {@link KuraResponseChannel}.
     * <p>
     * Checks that:
     * <ul>
     *     <li>the {@link KuraResponseChannel#getMessageClassification()} matches the configured {@link SystemSetting#getMessageClassifier()}</li>
     *     <li>the {@link KuraResponseChannel#getAppId()} is formatted as {@code appId-appVersion}</li>
     *     <li>the {@link KuraResponseChannel#getAppId()} first token matches the given application name</li>
     *     <li>the {@link KuraResponseChannel#getAppId()} second token matches the given application version</li>
     * </ul>
     *
     * @param kuraResponseChannel
     *         the {@link KuraResponseChannel} to check.
     * @param appName
     *         the application name.
     * @param appVersion
     *         the application version.
     * @throws TranslatorException
     *         The any of the constraints fails.
     * @since 1.2.0
     */
    @Override
    public void validateKuraResponseChannel(KuraResponseChannel kuraResponseChannel, DeviceAppMetrics appName, DeviceAppMetrics appVersion) throws TranslatorException {
        if (!controlMessageClassifier.equals(kuraResponseChannel.getMessageClassification())) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_CLASSIFIER, null, kuraResponseChannel.getMessageClassification());
        }

        String[] appIdTokens = kuraResponseChannel.getAppId().split("-");

        if (appIdTokens.length < 2) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_APP_NAME, null, (Object) appIdTokens);
        }

        if (!appName.getName().equals(appIdTokens[0])) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_APP_NAME, null, appIdTokens[0]);
        }

        if (!appVersion.getName().equals(appIdTokens[1])) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_APP_VERSION, null, appIdTokens[1]);
        }
    }

    /**
     * Translates {@link DevicePosition} to {@link KapuaPosition}
     *
     * @param devicePosition
     *         The {@link DevicePosition} to translate.
     * @return The translated {@link KapuaPosition}.
     * @since 1.0.0
     */
    @Override
    public KapuaPosition translate(DevicePosition devicePosition) {
        KapuaPosition kapuaPosition = null;

        if (devicePosition != null) {
            kapuaPosition = new KapuaPosition();

            kapuaPosition.setAltitude(devicePosition.getAltitude());
            kapuaPosition.setHeading(devicePosition.getHeading());
            kapuaPosition.setLatitude(devicePosition.getLatitude());
            kapuaPosition.setLongitude(devicePosition.getLongitude());
            kapuaPosition.setPrecision(devicePosition.getPrecision());
            kapuaPosition.setSatellites(devicePosition.getSatellites());
            kapuaPosition.setSpeed(devicePosition.getSpeed());
            kapuaPosition.setStatus(devicePosition.getStatus());
            kapuaPosition.setTimestamp(devicePosition.getTimestamp());
        }

        return kapuaPosition;
    }

    /**
     * Translate {@link KuraResponseCode} to {@link KapuaResponseCode}
     *
     * @param kuraResponseCode
     *         The {@link KuraResponseCode} to translate.
     * @return The translated {@link KapuaResponseCode}
     * @since 1.0.0
     */
    @Override
    public KapuaResponseCode translate(KuraResponseCode kuraResponseCode) throws KapuaException {
        if (kuraResponseCode == null) {
            return null;
        }

        KapuaResponseCode responseCode;
        switch (kuraResponseCode) {
        case ACCEPTED:
            responseCode = KapuaResponseCode.ACCEPTED;
            break;
        case BAD_REQUEST:
            responseCode = KapuaResponseCode.BAD_REQUEST;
            break;
        case NOT_FOUND:
            responseCode = KapuaResponseCode.NOT_FOUND;
            break;
        case INTERNAL_ERROR:
            responseCode = KapuaResponseCode.INTERNAL_ERROR;
            break;
        default:
            throw KapuaException.internalError("Kura Response code not mapped");
        }

        return responseCode;
    }
}
