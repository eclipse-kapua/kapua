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

import javax.validation.constraints.NotNull;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.device.call.DeviceCall;
import org.eclipse.kapua.service.device.call.DeviceCallFactory;
import org.eclipse.kapua.service.device.call.exception.DeviceCallTimeoutException;
import org.eclipse.kapua.service.device.call.message.app.request.DeviceRequestMessage;
import org.eclipse.kapua.service.device.call.message.app.response.DeviceResponseMessage;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.exception.DeviceManagementRequestBadMethodException;
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
 * {@link DeviceCallExecutor} definition.
 * <p>
 * Invokes and manages the {@link DeviceCall}.
 *
 * @param <C>
 *         The {@link KapuaRequestChannel} implementation.
 * @param <P>
 *         The {@link KapuaRequestPayload} implementation.
 * @param <RQ>
 *         The {@link KapuaRequestMessage} implementation.
 * @param <RS>
 *         The {@link KapuaResponseMessage} implementation.
 * @since 1.0.0
 * @deprecated Since 1.5.0. Please use {@link DeviceCallBuilder} which supports {@link KapuaMethod#SUBMIT}, {@link KapuaMethod#CANCEL} and send request without waiting for a response
 */
@Deprecated
public class DeviceCallExecutor<C extends KapuaRequestChannel, P extends KapuaRequestPayload, RQ extends KapuaRequestMessage<C, P>, RS extends KapuaResponseMessage> {

    private final DeviceCallFactory deviceCallFactory = KapuaLocator.getInstance().getFactory(DeviceCallFactory.class);

    private final DeviceRegistryService deviceRegistryService = KapuaLocator.getInstance().getService(DeviceRegistryService.class);

    private final TranslatorHub translatorHub = KapuaLocator.getInstance().getComponent(TranslatorHub.class);

    private final Long defaultTimeout = KapuaLocator.getInstance().getComponent(DeviceManagementSetting.class).getLong(DeviceManagementSettingKey.REQUEST_TIMEOUT);

    private final RQ requestMessage;
    private final Long timeout;

    /**
     * Constructor.
     *
     * @param requestMessage
     *         The {@link KapuaRequestMessage}.
     * @since 1.0.0
     */
    public DeviceCallExecutor(@NotNull RQ requestMessage) {
        this(requestMessage, null);
    }

    /**
     * Constructor.
     * <p>
     * If the {@code timeout} parameter is not given, the default one will be used.
     *
     * @param requestMessage
     *         The {@link KapuaRequestMessage} to send.
     * @param timeout
     *         The timeout of the request.
     * @since 1.0.0
     */
    public DeviceCallExecutor(@NotNull RQ requestMessage, @Nullable Long timeout) {
        this.requestMessage = requestMessage;
        this.timeout = timeout != null ? timeout : defaultTimeout;
    }

    /**
     * Performs the {@link DeviceCall}.
     *
     * @return The {@link KapuaResponseMessage}.
     * @throws KapuaEntityNotFoundException
     *         If the {@link Device} is not found.
     * @throws DeviceNotConnectedException
     *         If the {@link Device} is not {@link DeviceConnectionStatus#CONNECTED}.
     * @throws DeviceManagementTimeoutException
     *         If waiting of the {@link KapuaResponseMessage} goes on timeout.
     * @throws DeviceManagementSendException
     *         If sending the {@link KapuaRequestMessage} goes on error.
     * @since 1.0.0
     */
    public RS send() throws KapuaEntityNotFoundException, DeviceNotConnectedException, DeviceManagementTimeoutException, DeviceManagementSendException, TransportException {
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
        // Translate the request from Kapua to Device
        try {
            requestMessage.setSentOn(new Date());

            DeviceCall<DeviceRequestMessage<?, ?>, DeviceResponseMessage<?, ?>> deviceCall = deviceCallFactory.newDeviceCall();
            Translator<RQ, DeviceRequestMessage<?, ?>> tKapuaToClient = translatorHub.getTranslatorFor(requestMessage.getRequestClass(), deviceCall.getBaseMessageClass());
            DeviceRequestMessage<?, ?> deviceRequestMessage = tKapuaToClient.translate(requestMessage);
            // Send the request
            DeviceResponseMessage<?, ?> responseMessage = null;
            switch (requestMessage.getChannel().getMethod()) {
            case CREATE:
            case POST:
                responseMessage = deviceCall.create(deviceRequestMessage, timeout);
                break;
            case READ:
            case GET:
                responseMessage = deviceCall.read(deviceRequestMessage, timeout);
                break;
            case OPTIONS:
                responseMessage = deviceCall.options(deviceRequestMessage, timeout);
                break;
            case DELETE:
            case DEL:
                responseMessage = deviceCall.delete(deviceRequestMessage, timeout);
                break;
            case EXECUTE:
            case EXEC:
                responseMessage = deviceCall.execute(deviceRequestMessage, timeout);
                break;
            case WRITE:
            case PUT:
                responseMessage = deviceCall.write(deviceRequestMessage, timeout);
                break;
            default:
                throw new DeviceManagementRequestBadMethodException(requestMessage.getChannel().getMethod());
            }
            // Translate the response from Device to Kapua
            Translator<DeviceResponseMessage<?, ?>, KapuaResponseMessage> tClientToKapua = translatorHub.getTranslatorFor(deviceCall.getBaseMessageClass(), requestMessage.getResponseClass());
            return (RS) tClientToKapua.translate(responseMessage);
        } catch (DeviceCallTimeoutException dcte) {
            throw new DeviceManagementTimeoutException(dcte, timeout);
        } catch (TransportException te) {
            throw te;
        } catch (Exception e) {
            throw new DeviceManagementSendException(e, requestMessage);
        }
    }
}
