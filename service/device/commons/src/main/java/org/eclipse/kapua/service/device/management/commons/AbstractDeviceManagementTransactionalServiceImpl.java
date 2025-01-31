/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.commons;

import java.util.Optional;
import java.util.concurrent.Callable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.ThrowingRunnable;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.device.management.DeviceManagementService;
import org.eclipse.kapua.service.device.management.exception.DeviceManagementResponseBadRequestException;
import org.eclipse.kapua.service.device.management.exception.DeviceManagementResponseCodeException;
import org.eclipse.kapua.service.device.management.exception.DeviceManagementResponseContentException;
import org.eclipse.kapua.service.device.management.exception.DeviceManagementResponseInternalErrorException;
import org.eclipse.kapua.service.device.management.exception.DeviceManagementResponseNotFoundException;
import org.eclipse.kapua.service.device.management.exception.DeviceManagementResponseUnknownCodeException;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestMessage;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponseCode;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponseMessage;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponsePayload;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionStatus;
import org.eclipse.kapua.service.device.registry.event.DeviceEventCreator;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;
import org.eclipse.kapua.storage.TxManager;

/**
 * Utility {@code abstract} {@link Class} used to provide utility methods to all implementation of {@link DeviceManagementService}s.
 *
 * @since 1.0.0
 */
public abstract class AbstractDeviceManagementTransactionalServiceImpl {

    protected final TxManager txManager;
    protected final AuthorizationService authorizationService;

    protected final DeviceEventService deviceEventService;
    protected final DeviceEventFactory deviceEventFactory;

    protected final DeviceRegistryService deviceRegistryService;

    public AbstractDeviceManagementTransactionalServiceImpl(
            TxManager txManager,
            AuthorizationService authorizationService,
            DeviceEventService deviceEventService,
            DeviceEventFactory deviceEventFactory,
            DeviceRegistryService deviceRegistryService) {
        this.txManager = txManager;
        this.authorizationService = authorizationService;
        this.deviceEventService = deviceEventService;
        this.deviceEventFactory = deviceEventFactory;
        this.deviceRegistryService = deviceRegistryService;
    }
    // Device Registry

    /**
     * Creates a {@link org.eclipse.kapua.service.device.registry.event.DeviceEvent} extracting data from the given {@link KapuaRequestMessage} and {@link KapuaResponseMessage}.
     * <p>
     * This operation is performed using {@link KapuaSecurityUtils#doPrivileged(ThrowingRunnable)} since {@link org.eclipse.kapua.commons.model.domains.Domains#DEVICE_EVENT} isn't a required
     * permission to use any of the Device Management Services.
     *
     * @param scopeId
     *         The scopeId in which to create the {@link org.eclipse.kapua.service.device.registry.event.DeviceEvent}
     * @param deviceId
     *         The {@link org.eclipse.kapua.service.device.registry.Device} id for which the {@link org.eclipse.kapua.service.device.registry.event.DeviceEvent} is created
     * @param requestMessage
     *         The {@link KapuaRequestMessage} of the DeviceManagementService operation from which to extract data.
     * @param responseMessage
     *         The {@link KapuaResponseMessage} of the DeviceManagementService operation from which to extract data.
     * @throws KapuaException
     *         If the creation of the {@link org.eclipse.kapua.service.device.registry.event.DeviceEvent} fails for some reasons
     * @since 1.0.0
     */
    protected void createDeviceEvent(KapuaId scopeId, KapuaId deviceId, KapuaRequestMessage<?, ?> requestMessage, KapuaResponseMessage<?, ?> responseMessage) throws KapuaException {

        DeviceEventCreator deviceEventCreator =
                new DeviceEventCreator(
                        scopeId,
                        deviceId,
                        responseMessage != null ? responseMessage.getReceivedOn() : requestMessage.getSentOn(),
                        requestMessage.getChannel().getAppName().getValue());

        deviceEventCreator.setPosition(responseMessage != null ? responseMessage.getPosition() : null);
        deviceEventCreator.setSentOn(responseMessage != null ? responseMessage.getSentOn() : requestMessage.getSentOn());
        deviceEventCreator.setAction(requestMessage.getChannel().getMethod());
        deviceEventCreator.setResponseCode(responseMessage != null ? responseMessage.getResponseCode() : KapuaResponseCode.SENT);
        deviceEventCreator.setEventMessage(responseMessage != null ? responseMessage.getPayload().toDisplayString() : requestMessage.getPayload().toDisplayString());

        KapuaSecurityUtils.doPrivileged(() -> deviceEventService.create(deviceEventCreator));
    }

    /**
     * Checks whether the {@link Device} exists and if it is connected.
     *
     * @param scopeId
     *         The {@link Device#getScopeId()}.
     * @param deviceId
     *         The {@link Device#getId()}.
     * @return {@code true} if {@link Device} exists, has a {@link Device#getConnection()} and the {@link DeviceConnection#getStatus()} is {@link DeviceConnectionStatus#CONNECTED}, {@code false}
     *         otherwise.
     * @throws KapuaException
     */
    public boolean isDeviceConnected(KapuaId scopeId, KapuaId deviceId) throws KapuaException {
        // Validate arguments
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(deviceId, "deviceId");
        // Check Device existence
        Device device = Optional.ofNullable(deviceRegistryService.find(scopeId, deviceId))
                .orElseThrow(() -> new KapuaEntityNotFoundException(Device.TYPE, deviceId));
        // Check Device Connection status
        return Optional.ofNullable(device.getConnection())
                .map(conn -> DeviceConnectionStatus.CONNECTED.equals(conn.getStatus()))
                .orElse(false);
    }

    // Response handling

    /**
     * Checks the {@link KapuaResponseMessage#getResponseCode()} if it is {@link KapuaResponseCode#ACCEPTED} or throws the proper {@link DeviceManagementResponseCodeException}.
     *
     * @param responseMessage
     *         The {@link KapuaResponseMessage} to check.
     * @throws DeviceManagementResponseCodeException
     *         if {@link KapuaResponseMessage#getResponseCode()} is not {@link KapuaResponseCode#ACCEPTED}.
     * @since 1.5.0
     */
    protected void checkResponseAcceptedOrThrowError(@NotNull KapuaResponseMessage<?, ?> responseMessage) throws DeviceManagementResponseCodeException {
        try {
            checkResponseAcceptedOrThrowError(responseMessage, null);
        } catch (DeviceManagementResponseContentException e) {
            // Ignored since it cannot be thrown when resultGetter is null.
        }
    }

    /**
     * Checks the {@link KapuaResponseMessage#getResponseCode()} and returns the result if it is {@link KapuaResponseCode#ACCEPTED} or throws the proper {@link DeviceManagementResponseCodeException}.
     * <p>
     * If {@link Callable} is not provided the returned result will be {@code null}.
     *
     * @param responseMessage
     *         The {@link KapuaResponseMessage} to check.
     * @param resultGetter
     *         The {@link Callable} that gets the result.
     * @param <R>
     *         The type of the result.
     * @return The result from the {@link KapuaResponseMessage} if {@link KapuaResponseMessage#getResponseCode()} is {@link KapuaResponseCode#ACCEPTED}.
     * @throws DeviceManagementResponseCodeException
     *         if {@link KapuaResponseMessage#getResponseCode()} is not {@link KapuaResponseCode#ACCEPTED}.
     * @throws DeviceManagementResponseContentException
     *         if getting the result causes any {@link Exception}.
     * @since 1.5.0
     */
    protected <R> R checkResponseAcceptedOrThrowError(@NotNull KapuaResponseMessage<?, ?> responseMessage, @Null Callable<R> resultGetter)
            throws DeviceManagementResponseCodeException, DeviceManagementResponseContentException {
        if (responseMessage.getResponseCode().isAccepted()) {
            if (resultGetter != null) {
                try {
                    return resultGetter.call();
                } catch (Exception e) {
                    throw new DeviceManagementResponseContentException(e, responseMessage.getPayload());
                }
            } else {
                return null;
            }
        } else {
            throw buildExceptionFromDeviceResponseNotAccepted(responseMessage);
        }
    }

    /**
     * Builds the {@link DeviceManagementResponseCodeException} from the {@link KapuaResponseMessage}.
     *
     * @param kapuaResponseMessage
     *         The {@link KapuaResponseMessage} to build from.
     * @return The proper {@link DeviceManagementResponseCodeException} for the {@link KapuaResponseMessage#getResponseCode()}
     * @since 1.5.0
     */
    protected DeviceManagementResponseCodeException buildExceptionFromDeviceResponseNotAccepted(KapuaResponseMessage<?, ?> kapuaResponseMessage) {

        KapuaResponsePayload responsePayload = kapuaResponseMessage.getPayload();

        switch (kapuaResponseMessage.getResponseCode()) {
        case BAD_REQUEST:
            return new DeviceManagementResponseBadRequestException(kapuaResponseMessage.getResponseCode(), responsePayload.getExceptionMessage(), responsePayload.getExceptionMessage());
        case NOT_FOUND:
            return new DeviceManagementResponseNotFoundException(kapuaResponseMessage.getResponseCode(), responsePayload.getExceptionMessage(), responsePayload.getExceptionMessage());
        case INTERNAL_ERROR:
            return new DeviceManagementResponseInternalErrorException(kapuaResponseMessage.getResponseCode(), responsePayload.getExceptionMessage(), responsePayload.getExceptionMessage());
        case ACCEPTED:
        default:
            return new DeviceManagementResponseUnknownCodeException(kapuaResponseMessage.getResponseCode(), responsePayload.getExceptionMessage(), responsePayload.getExceptionMessage());
        }
    }
}
