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
 *******************************************************************************/
package org.eclipse.kapua.service.device.management.commons.call;

import java.util.Date;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.KapuaIllegalNullArgumentException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.device.call.DeviceCall;
import org.eclipse.kapua.service.device.call.DeviceCallFactory;
import org.eclipse.kapua.service.device.call.exception.DeviceCallTimeoutException;
import org.eclipse.kapua.service.device.call.message.app.request.DeviceRequestMessage;
import org.eclipse.kapua.service.device.call.message.app.response.DeviceResponseMessage;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.exception.DeviceManagementSendException;
import org.eclipse.kapua.service.device.management.exception.DeviceManagementTimeoutException;
import org.eclipse.kapua.service.device.management.exception.DeviceNeverConnectedException;
import org.eclipse.kapua.service.device.management.exception.DeviceNotConnectedException;
import org.eclipse.kapua.service.device.management.message.KapuaMethod;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestChannel;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestMessage;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestPayload;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponseMessage;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionStatus;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.TranslatorHub;
import org.eclipse.kapua.transport.exception.TransportException;

/**
 * {@link DeviceCallBuilder} definition.
 * <p>
 * It replaces {@link DeviceCallExecutor} providing:
 * <ul>
 *     <li>Support for {@link KapuaMethod#SUBMIT}</li>
 *     <li>Support for {@link KapuaMethod#CANCEL}</li>
 *     <li>Sending {@link KapuaRequestMessage} without waiting for {@link KapuaResponseMessage}</li>
 *     <li>Better API layout</li>
 * </ul>
 * Invokes and manages the {@link DeviceCall}.
 *
 * @param <REQ_CHANNEL>
 *         The {@link KapuaRequestChannel} implementation.
 * @param <REQ_PAYLOAD>
 *         The {@link KapuaRequestPayload} implementation.
 * @param <REQ>
 *         The {@link KapuaRequestMessage} implementation.
 * @param <RES>
 *         The {@link KapuaResponseMessage} implementation.
 * @since 1.4.0
 */
public class DeviceCallBuilder<
        REQ_CHANNEL extends KapuaRequestChannel,
        REQ_PAYLOAD extends KapuaRequestPayload,
        REQ extends KapuaRequestMessage<REQ_CHANNEL, REQ_PAYLOAD>,
        RES extends KapuaResponseMessage> {

    private final DeviceCallFactory deviceCallFactory = KapuaLocator.getInstance().getFactory(DeviceCallFactory.class);

    private final DeviceRegistryService deviceRegistryService = KapuaLocator.getInstance().getService(DeviceRegistryService.class);

    private final TranslatorHub translatorHub = KapuaLocator.getInstance().getComponent(TranslatorHub.class);

    private final Long defaultTimeout = KapuaLocator.getInstance().getComponent(DeviceManagementSetting.class).getLong(DeviceManagementSettingKey.REQUEST_TIMEOUT);

    private REQ requestMessage;
    private Long timeout;

    /**
     * Constructor.
     *
     * @since 1.4.0
     */
    private DeviceCallBuilder() {
    }

    /**
     * Instantiates a new {@link DeviceCallBuilder}.
     *
     * @return The newly instantiated {@link DeviceCallBuilder}.
     * @since 1.4.0
     */
    public static DeviceCallBuilder newBuilder() {
        return new DeviceCallBuilder();
    }

    /**
     * Configures the {@link KapuaRequestMessage} to send.
     *
     * @return The {@link DeviceCallBuilder} itself.
     * @since 1.4.0
     */
    public DeviceCallBuilder withRequestMessage(REQ requestMessage) {
        this.requestMessage = requestMessage;
        return this;
    }

    /**
     * Configures the timeout of the MQTT request-reply.
     *
     * @return The {@link DeviceCallBuilder} itself.
     * @since 1.4.0
     */
    public DeviceCallBuilder withTimeout(Long timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * Configures the timeout of the MQTT request-reply or sets the default timeout (see {@link DeviceManagementSettingKey#REQUEST_TIMEOUT}) in case the provided timeout is {@code null}.
     *
     * @return The {@link DeviceCallBuilder} itself.
     * @since 1.4.0
     */
    public DeviceCallBuilder withTimeoutOrDefault(Long timeout) {
        this.timeout = timeout != null ? timeout : defaultTimeout;
        return this;
    }

    /**
     * Performs the {@link DeviceCall}.
     *
     * @return The {@link KapuaResponseMessage}.
     * @throws KapuaEntityNotFoundException
     *         If the {@link Device} is not found.
     * @throws KapuaIllegalArgumentException
     *         If {@link KapuaRequestMessage} has not been set.
     * @throws DeviceNotConnectedException
     *         If the {@link Device} is not {@link DeviceConnectionStatus#CONNECTED}.
     * @throws DeviceManagementTimeoutException
     *         If waiting of the {@link KapuaResponseMessage} goes on timeout.
     * @throws DeviceManagementSendException
     *         If sending the {@link KapuaRequestMessage} goes on error.
     * @since 1.4.0
     */
    public RES send()
            throws KapuaEntityNotFoundException, KapuaIllegalArgumentException, DeviceNotConnectedException, DeviceManagementTimeoutException, DeviceManagementSendException, TransportException {

        deviceCallPreChecks();
        // Translate the request from Kapua to Device
        try {
            requestMessage.setSentOn(new Date());

            DeviceCall<DeviceRequestMessage<?, ?>, DeviceResponseMessage<?, ?>> deviceCall = deviceCallFactory.newDeviceCall();
            Translator<REQ, DeviceRequestMessage<?, ?>> tKapuaToClient = translatorHub.getTranslatorFor(requestMessage.getRequestClass(), deviceCall.getBaseMessageClass());
            DeviceRequestMessage<?, ?> deviceRequestMessage = tKapuaToClient.translate(requestMessage);

            // Send the request
            DeviceResponseMessage<?, ?> responseMessage = deviceCall.send(deviceRequestMessage, timeout);

            // Translate the response from Device to Kapua
            Translator<DeviceResponseMessage<?, ?>, KapuaResponseMessage> tClientToKapua = translatorHub.getTranslatorFor(deviceCall.getBaseMessageClass(),
                    requestMessage.getResponseClass());
            return (RES) tClientToKapua.translate(responseMessage);
        } catch (DeviceCallTimeoutException dcte) {
            throw new DeviceManagementTimeoutException(dcte, timeout);
        } catch (TransportException te) {
            throw te;
        } catch (Exception e) {
            throw new DeviceManagementSendException(e, requestMessage);
        }
    }

    /**
     * Performs the {@link DeviceCall}.
     *
     * @throws KapuaEntityNotFoundException
     *         If the {@link Device} is not found.
     * @throws KapuaIllegalArgumentException
     *         If {@link KapuaRequestMessage} has not been set.
     * @throws DeviceNotConnectedException
     *         If the {@link Device} is not {@link DeviceConnectionStatus#CONNECTED}.
     * @throws DeviceManagementSendException
     *         If sending the {@link KapuaRequestMessage} goes on error.
     * @since 1.4.0
     */
    public void sendAndForget() throws KapuaEntityNotFoundException, KapuaIllegalArgumentException, DeviceNotConnectedException, DeviceManagementSendException, TransportException {

        deviceCallPreChecks();
        // Translate the request from Kapua to Device
        try {
            requestMessage.setSentOn(new Date());

            DeviceCall<DeviceRequestMessage<?, ?>, DeviceResponseMessage<?, ?>> deviceCall = deviceCallFactory.newDeviceCall();
            Translator<REQ, DeviceRequestMessage<?, ?>> tKapuaToClient = translatorHub.getTranslatorFor(requestMessage.getRequestClass(), deviceCall.getBaseMessageClass());
            DeviceRequestMessage<?, ?> deviceRequestMessage = tKapuaToClient.translate(requestMessage);

            // Send the request
            deviceCall.send(deviceRequestMessage, null);

        } catch (TransportException te) {
            throw te;
        } catch (Exception e) {
            throw new DeviceManagementSendException(e, requestMessage);
        }
    }

    //
    // Private methods
    //

    private void deviceCallPreChecks() throws DeviceManagementSendException, KapuaEntityNotFoundException, DeviceNotConnectedException, KapuaIllegalNullArgumentException {
        // Validate arguments
        ArgumentValidator.notNull(requestMessage, "requestMessage");
        // Check Device existence
        Device device;
        try {
            device = deviceRegistryService.find(requestMessage.getScopeId(), requestMessage.getDeviceId());
        } catch (KapuaException e) {
            throw new DeviceManagementSendException(e, requestMessage);
        }
        if (device == null) {
            throw new KapuaEntityNotFoundException(Device.TYPE, requestMessage.getDeviceId());
        }
        // Check Device Connection
        if (device.getConnection() == null) {
            throw new DeviceNeverConnectedException(device.getId());
        }
        // Check Device Connection status
        if (!DeviceConnectionStatus.CONNECTED.equals(device.getConnection().getStatus())) {
            throw new DeviceNotConnectedException(device.getId(), device.getConnection().getStatus());
        }
    }
}
