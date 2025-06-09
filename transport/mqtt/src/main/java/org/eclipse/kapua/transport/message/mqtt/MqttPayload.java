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
package org.eclipse.kapua.transport.message.mqtt;

import java.util.Arrays;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.kapua.transport.message.TransportPayload;
import org.eclipse.kapua.transport.mqtt.setting.MqttClientSetting;
import org.eclipse.kapua.transport.mqtt.setting.MqttClientSettingKeys;

/**
 * Implementation of {@link TransportPayload} API for {@link org.eclipse.kapua.transport.mqtt.MqttFacade}.
 *
 * @since 1.0.0
 */
public class MqttPayload implements TransportPayload {

    //TODO: FIXME: REMOVE: A collaborator in a data class? Behaviour should not be part of a data class!
    private static final int BODY_TOSTRING_LENGTH = MqttClientSetting.getInstance().getInt(MqttClientSettingKeys.PAYLOAD_TOSTRING_LENGTH, 64);

    /**
     * The raw body of this {@link MqttPayload}.
     *
     * @since 1.0.0
     */
    private byte[] body;

    /**
     * Constructor.
     *
     * @since 1.2.0
     */
    MqttPayload() {
        this(new byte[0]);
    }

    /**
     * Constructor.
     *
     * @param body
     *         The raw body to set for this {@link MqttPayload}.
     * @since 1.0.0
     */
    public MqttPayload(byte[] body) {
        this.body = body;
    }

    /**
     * Gets the raw body set for this {@link MqttPayload}.
     *
     * @return The raw body set for this {@link MqttPayload}.
     * @since 1.0.0
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * Sets the raw body set for this {@link MqttPayload}.
     *
     * @param body
     *         the raw body set for this {@link MqttPayload}.
     * @since 1.0.0
     */
    public void setBody(byte[] body) {
        this.body = body;
    }

    /**
     * Says whether or not the {@link #getBody()} has value.
     * <p>
     * Checks for {@code null} and size equals to 0
     *
     * @return {@code true} if {@link #getBody()} is not {@code null} and {@link #getBody()}{@code length > 0}, {@code false} otherwise.
     * @since 1.2.0
     */
    public boolean hasBody() {
        return getBody() != null && getBody().length > 0;
    }

    /**
     * Gets the {@link Base64} encoded value of {@link #getBody()} as a user-friendly value.
     * <p>
     * If {@link #hasBody()} the {@link #getBody()} will be shortened if longer that a configurable parameter named: {@code transport.mqtt.payload.body.toString.length}. Else {@link StringUtils#EMPTY}
     * will be used.
     *
     * @return The {@link Base64} encoded value of {@link #getBody()}.
     * @since 1.2.0
     */
    @Override
    public String toString() {
        if (hasBody()) {
            byte[] bodyForPrint = getBody();

            if (bodyForPrint.length > BODY_TOSTRING_LENGTH) {
                bodyForPrint = Arrays.copyOf(getBody(), BODY_TOSTRING_LENGTH);
            }

            return Base64.getEncoder().encodeToString(bodyForPrint);
        } else {
            return StringUtils.EMPTY;
        }
    }
}
