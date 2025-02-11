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
package org.eclipse.kapua.service.device.call.message;

import java.util.Date;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.Payloads;
import org.eclipse.kapua.message.Payload;

/**
 * {@link DevicePayload} definition.
 *
 * @since 1.0.0
 */
public interface DevicePayload extends Payload {

    /**
     * Gets the timestamp.
     *
     * @return The timestamp.
     * @since 1.0.0
     */
    Date getTimestamp();

    /**
     * Sets the timestamp.
     *
     * @param timestamp
     *         The timestamp
     * @since 1.0.0
     */
    void setTimestamp(Date timestamp);

    /**
     * Get the {@link DevicePosition}
     *
     * @return A {@link DevicePosition} if present, or {@code null} otherwise
     * @since 1.0.0
     */
    DevicePosition getPosition();

    /**
     * Sets the {@link DevicePosition}
     *
     * @param position
     *         The {@link DevicePosition}
     * @since 1.0.0
     */
    void setPosition(DevicePosition position);

    /**
     * Gets the metrics.
     *
     * @return The metrics
     * @since 1.0.0
     */
    Map<String, Object> getMetrics();

    /**
     * Sets the metrics.
     *
     * @param metrics
     *         The metrics.
     * @since 1.0.0
     */
    void setMetrics(Map<String, Object> metrics);

    /**
     * Adds a new metric to {@link #getMetrics()}.
     *
     * @param name
     *         The name of the metric.
     * @param value
     *         The value of the metric.
     * @since 1.2.0
     */
    void addMetric(String name, Object value);

    /**
     * Removes a metric from {@link #getMetrics()}.
     * <p>
     * If not present it will do nothing.
     *
     * @param name
     *         The name of the metric to remove.
     * @since 1.2.0
     */
    void removeMetric(String name);

    /**
     * Gets the raw body.
     *
     * @return The raw body.
     * @since 1.0.0
     */
    byte[] getBody();

    /**
     * Sets the raw body.
     *
     * @param body
     *         The raw body.
     * @since 1.0.0
     */
    void setBody(byte[] body);

    /**
     * Says whether or not the {@link #getBody()} has value.
     * <p>
     * Checks for {@code null} and size equals to 0
     *
     * @return {@code true} if {@link #getBody()} is not {@code null} and {@link #getBody()}{@code length > 0}, {@code false} otherwise.
     * @since 1.2.0
     */
    default boolean hasBody() {
        return getBody() != null && getBody().length > 0;
    }
    // Encode/Decode stuff

    /**
     * Converts the {@link DevicePayload} to a protobuf encoded {@code byte[]}
     *
     * @return The protobuf encoding of {@code this} {@link DevicePayload}
     * @since 1.0.0
     */
    byte[] toByteArray();

    /**
     * Converts the given {@code byte[]} to a {@link DevicePayload}
     *
     * @param rawPayload
     *         The {@code byte[]} to convert
     * @throws KapuaException
     *         If the given {@code byte[]} is not properly formatted.
     * @since 1.0.0
     */
    void readFromByteArray(byte[] rawPayload)
            throws KapuaException;
    // Display methods

    /**
     * Returns a {@link String} for displaying the {@link DevicePayload} content.
     *
     * @return A {@link String} used for displaying the content of the {@link DevicePayload}, never returns {@code null}
     * @since 1.0.0
     */
    default String toDisplayString() {
        return Payloads.toDisplayString(getMetrics());
    }
}
