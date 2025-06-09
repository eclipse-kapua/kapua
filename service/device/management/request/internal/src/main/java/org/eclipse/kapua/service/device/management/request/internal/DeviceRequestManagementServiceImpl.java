/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.request.internal;

import java.util.Date;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.device.management.commons.AbstractDeviceManagementTransactionalServiceImpl;
import org.eclipse.kapua.service.device.management.commons.call.DeviceCallBuilder;
import org.eclipse.kapua.service.device.management.exception.DeviceManagementRequestBadMethodException;
import org.eclipse.kapua.service.device.management.request.DeviceRequestManagementService;
import org.eclipse.kapua.service.device.management.request.GenericRequestFactory;
import org.eclipse.kapua.service.device.management.request.message.request.GenericRequestChannel;
import org.eclipse.kapua.service.device.management.request.message.request.GenericRequestMessage;
import org.eclipse.kapua.service.device.management.request.message.request.GenericRequestPayload;
import org.eclipse.kapua.service.device.management.request.message.response.GenericResponseMessage;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DeviceRequestManagementService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class DeviceRequestManagementServiceImpl extends AbstractDeviceManagementTransactionalServiceImpl implements DeviceRequestManagementService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceRequestManagementServiceImpl.class);

    private final GenericRequestFactory genericRequestFactory;

    public DeviceRequestManagementServiceImpl(
            TxManager txManager,
            AuthorizationService authorizationService,
            DeviceEventService deviceEventService,
            DeviceEventFactory deviceEventFactory,
            DeviceRegistryService deviceRegistryService,
            GenericRequestFactory genericRequestFactory) {
        super(txManager,
                authorizationService,
                deviceEventService,
                deviceEventFactory,
                deviceRegistryService);
        this.genericRequestFactory = genericRequestFactory;
    }

    @Override
    public GenericResponseMessage exec(GenericRequestMessage requestInput, Long timeout) throws KapuaException {
        return exec(requestInput.getScopeId(), requestInput.getDeviceId(), requestInput, timeout);
    }

    @Override
    public GenericResponseMessage exec(KapuaId scopeId, KapuaId deviceId, GenericRequestMessage requestInput, Long timeout) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(requestInput, "requestInput");
        // Check Access
        Actions action;
        switch (requestInput.getChannel().getMethod()) {
        case EXECUTE:
            action = Actions.execute;
            break;
        case READ:
        case OPTIONS:
            action = Actions.read;
            break;
        case CREATE:
        case WRITE:
            action = Actions.write;
            break;
        case DELETE:
            action = Actions.delete;
            break;
        default:
            throw new DeviceManagementRequestBadMethodException(requestInput.getChannel().getMethod());
        }
        authorizationService.checkPermission(new Permission(Domains.DEVICE_MANAGEMENT, action, requestInput.getScopeId()));
        // Prepare the request
        GenericRequestChannel genericRequestChannel = new GenericRequestChannel();
        genericRequestChannel.setAppName(requestInput.getChannel().getAppName());
        genericRequestChannel.setVersion(requestInput.getChannel().getVersion());
        genericRequestChannel.setMethod(requestInput.getChannel().getMethod());
        genericRequestChannel.setResources(requestInput.getChannel().getResources());

        GenericRequestPayload genericRequestPayload = new GenericRequestPayload();
        genericRequestPayload.setMetrics(requestInput.getPayload().getMetrics());
        genericRequestPayload.setBody(requestInput.getPayload().getBody());

        GenericRequestMessage genericRequestMessage = genericRequestFactory.newRequestMessage();
        genericRequestMessage.setScopeId(requestInput.getScopeId());
        genericRequestMessage.setDeviceId(requestInput.getDeviceId());
        genericRequestMessage.setCapturedOn(new Date());
        genericRequestMessage.setChannel(genericRequestChannel);
        genericRequestMessage.setPayload(genericRequestPayload);
        genericRequestMessage.setPosition(requestInput.getPosition());

        // Build request
        DeviceCallBuilder<GenericRequestChannel, GenericRequestPayload, GenericRequestMessage, GenericResponseMessage> genericDeviceCallBuilder =
                DeviceCallBuilder
                        .newBuilder()
                        .withRequestMessage(genericRequestMessage)
                        .withTimeoutOrDefault(timeout);

        // Do it
        GenericResponseMessage responseMessage;
        try {
            responseMessage = genericDeviceCallBuilder.send();
        } catch (Exception e) {
            LOG.error("Error while sending GenericRequestMessage {} for Device {}. Error: {}", genericRequestMessage, deviceId, e.getMessage(), e);
            throw e;
        }

        // Create event
        createDeviceEvent(scopeId, deviceId, genericRequestMessage, responseMessage);

        return responseMessage;
    }
}
