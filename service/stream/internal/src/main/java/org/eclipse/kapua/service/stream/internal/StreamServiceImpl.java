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
package org.eclipse.kapua.service.stream.internal;

import com.google.common.base.Strings;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.locator.KapuaProvider;
import org.eclipse.kapua.message.Message;
import org.eclipse.kapua.message.device.data.KapuaDataMessage;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.device.call.kura.exception.KuraDeviceCallErrorCodes;
import org.eclipse.kapua.service.device.call.kura.exception.KuraDeviceCallException;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.kura.data.KuraDataMessage;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponseMessage;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.endpoint.EndpointInfo;
import org.eclipse.kapua.service.endpoint.EndpointInfoAttributes;
import org.eclipse.kapua.service.endpoint.EndpointInfoFactory;
import org.eclipse.kapua.service.endpoint.EndpointInfoQuery;
import org.eclipse.kapua.service.endpoint.EndpointInfoService;
import org.eclipse.kapua.service.stream.StreamDomains;
import org.eclipse.kapua.service.stream.StreamService;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.TranslatorNotFoundException;
import org.eclipse.kapua.transport.TransportClientFactory;
import org.eclipse.kapua.transport.TransportFacade;
import org.eclipse.kapua.transport.exception.TransportClientGetException;
import org.eclipse.kapua.transport.message.TransportMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link StreamService} implementation.
 *
 * @since 1.0.0
 */
@KapuaProvider
public class StreamServiceImpl implements StreamService {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final AuthorizationService AUTHORIZATION_SERVICE = LOCATOR.getService(AuthorizationService.class);
    private static final PermissionFactory PERMISSION_FACTORY = LOCATOR.getFactory(PermissionFactory.class);

    private static final DeviceRegistryService DEVICE_REGISTRY_SERVICE = LOCATOR.getService(DeviceRegistryService.class);

    private static final EndpointInfoService ENDPOINT_INFO_SERVICE = LOCATOR.getService(EndpointInfoService.class);
    private static final EndpointInfoFactory ENDPOINT_INFO_FACTORY = LOCATOR.getFactory(EndpointInfoFactory.class);

    private static final TransportClientFactory TRANSPORT_CLIENT_FACTORY = LOCATOR.getFactory(TransportClientFactory.class);

    @Override
    public KapuaResponseMessage<?, ?> publish(KapuaDataMessage kapuaDataMessage, Long timeout)
            throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(kapuaDataMessage.getScopeId(), "dataMessage.scopeId");
        ArgumentValidator.notNull(kapuaDataMessage.getChannel(), "dataMessage.channel");

        //
        // Check Access
        AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(StreamDomains.STREAM_DOMAIN, Actions.write, kapuaDataMessage.getScopeId()));

        //
        // Do publish
        try (TransportFacade transportFacade = borrowClient(kapuaDataMessage)) {

            //
            // Get Kura to transport translator for the request and vice versa
            Translator<KapuaDataMessage, KuraDataMessage> translatorKapuaKura = getTranslator(KapuaDataMessage.class, KuraDataMessage.class);
            Translator<KuraDataMessage, ?> translatorKuraTransport = getTranslator(KuraDataMessage.class, transportFacade.getMessageClass());

            KuraDataMessage kuraDataMessage = translatorKapuaKura.translate(kapuaDataMessage);

            //
            // Do send

            // Set current timestamp
            kuraDataMessage.setTimestamp(new Date());

            // Send
            transportFacade.sendAsync((TransportMessage<?, ?>) translatorKuraTransport.translate(kuraDataMessage));

        } catch (KapuaException ke) {
            throw new KuraDeviceCallException(KuraDeviceCallErrorCodes.CALL_ERROR, ke, (Object[]) null);
        }

        return null;
    }

    //
    // Private methods
    //

    /**
     * Picks a {@link TransportFacade} to send the {@link KuraResponseMessage}.
     *
     * @param kapuaDataMessage The K
     * @return The {@link TransportFacade} to use to send the {@link KuraDataMessage}.
     * @throws KuraDeviceCallException If getting the {@link TransportFacade} causes any {@link Exception}.
     * @since 1.0.0
     */
    protected TransportFacade<?, ?, ?, ?> borrowClient(KapuaDataMessage kapuaDataMessage) throws KuraDeviceCallException {
        String serverURI = null;
        try {
            //
            // Check Device existence
            Device device = checkDeviceInfo(kapuaDataMessage);

            // Resolve URI
            if (device != null) {
                serverURI = device.getConnection().getServerIp();
            } else {
                serverURI = getEndpointInfoDNS(kapuaDataMessage);
            }

            if (Strings.isNullOrEmpty(serverURI)) {
                throw new TransportClientGetException(serverURI);
            }

            Map<String, Object> configParameters = new HashMap<>(1);
            configParameters.put("serverAddress", serverURI);
            return TRANSPORT_CLIENT_FACTORY.getFacade(configParameters);
        } catch (Exception e) {
            throw new KuraDeviceCallException(KuraDeviceCallErrorCodes.CALL_ERROR, e, serverURI);
        }
    }

    /**
     * Checks the {@link KapuaDataMessage#getDeviceId()} and {@link KapuaDataMessage#getClientId()}.
     * <p>
     * At least one of the two must be present if not both:
     * <ul>
     *     <li>Only {@link KapuaDataMessage#getClientId()} is specified: The given clientId will be resolved to the matching {@link Device}. If not found {@code null} will be returned</li>
     *     <li>Only {@link KapuaDataMessage#getDeviceId()} is specified: The gived {@link Device#getId()} will be resolved to the {@link Device} and the {@link KapuaDataMessage#getClientId()} will be populated with its value</li>
     *     <li>Both specified: The {@link Device#getClientId()} and the {@link KapuaDataMessage#getClientId()} must match</li>
     * </ul>
     *
     * @param dataMessage The {@link KapuaDataMessage} to publish
     * @return The {@link Device} matching the {@link KapuaDataMessage#getDeviceId()} or {@link KapuaDataMessage#getClientId()}
     * @throws KapuaEntityNotFoundException  if {@link KapuaDataMessage#getDeviceId()} does not match any existing {@link Device}
     * @throws KapuaIllegalArgumentException if {@link KapuaDataMessage#getClientId()} does not match the {@link Device#getClientId()}
     * @throws KapuaException                If any other error occurs.
     * @since 1.2.0
     */
    private Device checkDeviceInfo(KapuaDataMessage dataMessage) throws KapuaException {
        Device device = null;

        // If `dataMessage.deviceId` check that it exists and that the `dataMessage.clientId` matches if specified.
        if (dataMessage.getDeviceId() != null) {
            device = DEVICE_REGISTRY_SERVICE.find(dataMessage.getScopeId(), dataMessage.getDeviceId());

            if (device == null) {
                throw new KapuaEntityNotFoundException(Device.TYPE, dataMessage.getDeviceId());
            } else {
                if (dataMessage.getClientId() == null) {
                    dataMessage.setClientId(device.getClientId());
                } else if (!device.getClientId().equals(dataMessage.getClientId())) {
                    throw new KapuaIllegalArgumentException("dataMessage.clientId", dataMessage.getClientId());
                }
            }
        }

        // If `dataMessage.deviceId` is not present, try resolve Device from the `dataMessage.clientId`
        if (device == null) {
            device = KapuaSecurityUtils.doPrivileged(() -> DEVICE_REGISTRY_SERVICE.findByClientId(dataMessage.getScopeId(), dataMessage.getClientId()));
        }

        return device;
    }

    /**
     * Looks for the available {@link EndpointInfo} with {@link EndpointInfo#getSchema()} = "mqtt"
     *
     * @param dataMessage The {@link KapuaDataMessage} to publish
     * @return The {@link String} {@link java.net.URI} to connect to.
     * @throws KapuaException If no {@link EndpointInfo} is available
     * @since 1.2.0
     */
    private String getEndpointInfoDNS(KapuaDataMessage dataMessage) throws KapuaException {
        String serverURI;
        EndpointInfoQuery query = ENDPOINT_INFO_FACTORY.newQuery(dataMessage.getScopeId());
        query.setPredicate(
                query.andPredicate(
                        query.attributePredicate(EndpointInfoAttributes.SCHEMA, "mqtt"),
                        query.attributePredicate(EndpointInfoAttributes.SECURE, Boolean.FALSE)
                )
        );

        EndpointInfo endpointInfo = ENDPOINT_INFO_SERVICE.query(query).getFirstItem();
        if (endpointInfo == null) {
            throw KapuaException.internalError("No endpoint defined!");
        }
        serverURI = endpointInfo.getDns();
        return serverURI;
    }


    /**
     * Gets the translator for the given {@link Message} types.
     *
     * @param from The {@link Message} type from which to translate.
     * @param to   The {@link Message} type to which to translate.
     * @param <F>  The {@link Message} {@code class}from which to translate.
     * @param <T>  The {@link Message} {@code class} to which to translate.
     * @return The {@link Translator} found.
     * @throws KuraDeviceCallException If error occurs while loojing for  the {@link Translator}.
     * @since 1.0.0
     */
    protected <F extends Message<?, ?>, T extends Message<?, ?>> Translator<F, T> getTranslator(Class<F> from, Class<T> to) throws KuraDeviceCallException {
        Translator<F, T> translator;
        try {
            translator = Translator.getTranslatorFor(from, to);
        } catch (TranslatorNotFoundException e) {
            throw new KuraDeviceCallException(KuraDeviceCallErrorCodes.CALL_ERROR, e, from, to);
        }
        return translator;
    }
}
