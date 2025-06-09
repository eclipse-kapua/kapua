/*******************************************************************************
 * Copyright (c) 2017, 2022 Red Hat Inc and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Red Hat Inc - initial API and implementation
 *     Eurotech
 *******************************************************************************/
package org.eclipse.kapua.translator.kura.kapua;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseMetrics;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponsePayload;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponseChannel;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponseMessage;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponsePayload;
import org.eclipse.kapua.service.device.management.request.GenericRequestFactory;
import org.eclipse.kapua.service.device.management.request.message.response.GenericResponseMessage;
import org.eclipse.kapua.service.device.management.request.message.response.GenericResponsePayload;
import org.eclipse.kapua.translator.exception.InvalidBodyContentException;
import org.eclipse.kapua.translator.exception.InvalidBodyEncodingException;
import org.eclipse.kapua.translator.exception.InvalidBodyException;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * {@link org.eclipse.kapua.translator.Translator} {@code abstract} implementation for {@link KuraResponseMessage} to {@link KapuaResponseMessage}
 *
 * @since 1.0.0
 */
public abstract class AbstractSimpleTranslatorResponseKuraKapua<TO_C extends KapuaResponseChannel, TO_P extends KapuaResponsePayload, TO_M extends KapuaResponseMessage<TO_C, TO_P>>
        extends AbstractTranslatorResponseKuraKapua<TO_C, TO_P, TO_M> {

    private final String charEncoding;
    private final boolean showStacktrace;

    @Inject
    private ObjectMapper jsonMapper;
    @Inject
    private XmlUtil xmlUtil;
    @Inject
    private GenericRequestFactory genericRequestFactory;

    private final Class<TO_M> messageClazz;
    private final Class<TO_P> payloadClazz;

    /**
     * Constructor.
     *
     * @param messageClazz
     *         The {@link Class} of the {@link KapuaResponseMessage}. It must have a 0-arguments constructor.
     * @param payloadClazz
     *         The {@link Class} of the {@link KapuaResponsePayload}. It must have a 0-arguments constructor.
     * @since 1.0.0
     */
    public AbstractSimpleTranslatorResponseKuraKapua(DeviceManagementSetting deviceManagementSetting, Class<TO_M> messageClazz, Class<TO_P> payloadClazz) {
        this.messageClazz = messageClazz;
        this.payloadClazz = payloadClazz;
        charEncoding = deviceManagementSetting.getString(DeviceManagementSettingKey.CHAR_ENCODING);
        showStacktrace = deviceManagementSetting.getBoolean(DeviceManagementSettingKey.SHOW_STACKTRACE, false);
    }

    @Override
    protected TO_M createMessage() throws KapuaException {
        try {
            if (this.messageClazz.equals(GenericResponseMessage.class)) {
                return this.messageClazz.cast(genericRequestFactory.newResponseMessage());
            } else {
                return this.messageClazz.newInstance();
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw KapuaException.internalError(e);
        }
    }

    @Override
    public Class<KuraResponseMessage> getClassFrom() {
        return KuraResponseMessage.class;
    }

    @Override
    public Class<TO_M> getClassTo() {
        return this.messageClazz;
    }

    @Override
    protected abstract TO_C translateChannel(KuraResponseChannel kuraResponseChannel) throws InvalidChannelException;

    @Override
    protected TO_P translatePayload(KuraResponsePayload kuraResponsePayload) throws InvalidPayloadException {
        try {
            TO_P appResponsePayload;
            if (payloadClazz.equals(GenericResponsePayload.class)) {
                appResponsePayload = this.payloadClazz.cast(new GenericResponsePayload());
            } else {
                appResponsePayload = payloadClazz.newInstance();
            }

            appResponsePayload.setExceptionMessage(kuraResponsePayload.getExceptionMessage());

            if (showStacktrace) {
                appResponsePayload.setExceptionStack(kuraResponsePayload.getExceptionStack());
                kuraResponsePayload.getMetrics().remove(KuraResponseMetrics.EXCEPTION_STACK.getName());
            }

            return appResponsePayload;
        } catch (Exception e) {
            throw new InvalidPayloadException(e, kuraResponsePayload);
        }
    }

    /**
     * Reads the given {@code byte[]} as the requested {@code returnAs} parameter.
     * <p>
     * The encoding is fetched from {@link DeviceManagementSettingKey#CHAR_ENCODING}.
     *
     * @param bytesToRead
     *         The {@link KapuaResponsePayload#getBody()}
     * @param returnAs
     *         The {@link Class} to read as.
     * @param <T>
     *         The type of the return.
     * @return Returns the given {@code byte[]} as the given {@link Class}
     * @throws InvalidBodyEncodingException
     *         See {@link #readBodyAsString(byte[], String)}
     * @throws InvalidBodyContentException
     *         If the given {@code byte[]} is not representing the given {@code returnAs} parameter or is not correctly formatted.
     * @since 1.5.0
     */
    protected <T> T readXmlBodyAs(@NotNull byte[] bytesToRead, @NotNull Class<T> returnAs) throws InvalidBodyException {
        String bodyString = readBodyAsString(bytesToRead, charEncoding);

        try {
            return xmlUtil.unmarshal(bodyString, returnAs);
        } catch (Exception e) {
            throw new InvalidBodyContentException(e, returnAs, bytesToRead);
        }
    }

    /**
     * Reads the given {@code byte[]} as the requested {@code returnAs} parameter.
     *
     * @param bytesToRead
     *         The {@link KapuaResponsePayload#getBody()}
     * @param returnAs
     *         The {@link Class} to read as.
     * @param <T>
     *         The type of the return.
     * @return Returns the given {@code byte[]} as the given {@link Class}
     * @throws InvalidBodyEncodingException
     *         See {@link #readBodyAsString(byte[], String)}
     * @throws InvalidBodyContentException
     *         If the given {@code byte[]} is not representing the given {@code returnAs} parameter or is not correctly formatted.
     * @since 1.5.0
     */
    protected <T> T readJsonBodyAs(@NotNull byte[] bytesToRead, @NotNull Class<T> returnAs) throws InvalidBodyException {
        String bodyString = readBodyAsString(bytesToRead, charEncoding);

        try {
            return getJsonMapper().readValue(bodyString, returnAs);
        } catch (Exception e) {
            throw new InvalidBodyContentException(e, returnAs, bytesToRead);
        }
    }

    /**
     * Reads the given {@code byte[]} with the given encoding into a {@link String}.
     *
     * @param body
     *         The {@code byte[]} to read.
     * @param encoding
     *         The target encoding
     * @return The {@link String} representing the {@code byte[]}.
     * @throws InvalidBodyEncodingException
     *         if the given {@code byte[]} cannot be read using the given encoding.
     * @see String#String(byte[], String)
     */
    protected String readBodyAsString(@NotNull byte[] body, @NotNull String encoding) throws InvalidBodyEncodingException {
        try {
            return new String(body, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new InvalidBodyEncodingException(e, encoding, body);
        }
    }

    /**
     * Returns the {@link ObjectMapper} instance.
     * <p>
     * This can be also used to change the {@link ObjectMapper}'s configuration.
     *
     * @return The {@link ObjectMapper} instance.
     * @since 1.5.0
     */
    protected ObjectMapper getJsonMapper() {
        return jsonMapper;
    }
}
