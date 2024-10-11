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
package org.eclipse.kapua.service.camel.converter;

import java.util.Base64;
import java.util.Date;

import javax.inject.Inject;
import javax.jms.JMSException;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.camel.application.MetricsCamel;
import org.eclipse.kapua.service.camel.message.CamelKapuaMessage;
import org.eclipse.kapua.service.camel.message.JmsUtil;
import org.eclipse.kapua.service.client.message.MessageConstants;
import org.eclipse.kapua.service.client.message.MessageType;
import org.eclipse.kapua.service.client.protocol.ProtocolDescriptor;
import org.eclipse.kapua.service.client.protocol.ProtocolDescriptorProvider;
import org.eclipse.kapua.service.device.call.message.DeviceMessage;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.TranslatorHub;
import org.eclipse.kapua.transport.message.jms.JmsPayload;
import org.eclipse.kapua.transport.message.jms.JmsTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kapua message converter reference implementation used to convert from Camel incoming messages ({@link JmsMessage}) to a platform specific message type.
 *
 * @since 1.0
 */
public abstract class AbstractKapuaConverter {

    public static final Logger logger = LoggerFactory.getLogger(AbstractKapuaConverter.class);

    protected final TranslatorHub translatorHub;
    protected final MetricsCamel metricsCamel;
    protected final ProtocolDescriptorProvider protocolDescriptorProvider;

    @Inject
    protected AbstractKapuaConverter(TranslatorHub translatorHub, MetricsCamel metricsCamel, ProtocolDescriptorProvider protocolDescriptorProvider) {
        this.translatorHub = translatorHub;
        this.metricsCamel = metricsCamel;
        this.protocolDescriptorProvider = protocolDescriptorProvider;
    }

    /**
     * Convert incoming message to a Kapua message (depending on messageType parameter)
     *
     * @param exchange
     * @param value
     * @param messageType
     *         expected incoming message type
     * @return Message container that contains message of asked type
     * @throws KapuaException
     *         if incoming message does not contain a javax.jms.BytesMessage or an error during conversion occurred
     */
    protected CamelKapuaMessage<?> convertTo(Exchange exchange, Object value, MessageType messageType) throws KapuaException {
        if (exchange.getIn() instanceof Message) {
            Message message = exchange.getIn();
            try {
                byte[] messageContent = (byte[]) value;
                // FIX #164
                Date queuedOn = new Date();//new Date(message.getHeader(MessageConstants.HEADER_KAPUA_RECEIVED_TIMESTAMP, Long.class));
                KapuaId connectionId = SerializationUtils.deserialize(Base64.getDecoder().decode(message.getHeader(MessageConstants.HEADER_KAPUA_CONNECTION_ID, String.class)));
                String clientId = message.getHeader(MessageConstants.HEADER_KAPUA_CLIENT_ID, String.class);
                String connectorName = message.getHeader(MessageConstants.HEADER_KAPUA_CONNECTOR_NAME, String.class);
                ProtocolDescriptor connectorDescriptor = protocolDescriptorProvider.getDescriptor(connectorName);
                if (connectorDescriptor == null) {
                    throw new IllegalStateException(String.format("Unable to find connector descriptor for connector '%s'", connectorName));
                }
                return convertToCamelKapuaMessage(connectorDescriptor, messageType, messageContent, JmsUtil.getTopic(message), queuedOn, connectionId, clientId);
            } catch (JMSException e) {
                metricsCamel.getConverterErrorMessage().inc();
                logger.error("Exception converting message {}", e.getMessage(), e);
                throw KapuaException.internalError(e, "Cannot convert the message type " + message.getClass());
            }
        }
        metricsCamel.getConverterErrorMessage().inc();
        throw KapuaException.internalError("Cannot convert the message - Wrong instance type: " + exchange.getIn().getClass());
    }

    /**
     * Convert raw byte[] message to {@link CamelKapuaMessage}
     *
     * @param connectorDescriptor
     * @param messageType
     * @param messageBody
     * @param jmsTopic
     * @param queuedOn
     * @param connectionId
     * @return
     * @throws KapuaException
     */
    private CamelKapuaMessage<?> convertToCamelKapuaMessage(ProtocolDescriptor connectorDescriptor, MessageType messageType, byte[] messageBody, String jmsTopic, Date queuedOn,
            KapuaId connectionId, String clientId)
            throws KapuaException {
        final Class<? extends DeviceMessage<?, ?>> deviceMessageType = connectorDescriptor.getDeviceClass(messageType);
        final Class<? extends KapuaMessage<?, ?>> kapuaMessageType = connectorDescriptor.getKapuaClass(messageType);

        // first step... from jms to device dependent protocol level (unknown)
        Translator<org.eclipse.kapua.transport.message.jms.JmsMessage, DeviceMessage<?, ?>> translatorFromJms = translatorHub.getTranslatorFor(org.eclipse.kapua.transport.message.jms.JmsMessage.class,
                deviceMessageType);// birth ...

        DeviceMessage<?, ?> deviceMessage = translatorFromJms.translate(new org.eclipse.kapua.transport.message.jms.JmsMessage(new JmsTopic(jmsTopic), queuedOn, new JmsPayload(messageBody)));

        // second step.... from device dependent protocol (unknown) to Kapua
        Translator<DeviceMessage<?, ?>, KapuaMessage<?, ?>> translatorToKapua = translatorHub.getTranslatorFor(deviceMessageType, kapuaMessageType);
        KapuaMessage<?, ?> kapuaMessage = translatorToKapua.translate(deviceMessage);
        if (StringUtils.isEmpty(kapuaMessage.getClientId())) {
            logger.debug("Updating client id since the received value is null (new value {})", clientId);
            kapuaMessage.setClientId(clientId);
        }
        return new CamelKapuaMessage<>(kapuaMessage, connectionId, connectorDescriptor);
    }

}
