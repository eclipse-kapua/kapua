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
 *******************************************************************************/
package org.eclipse.kapua.service.device.management.asset;

import java.util.Date;

/**
 * {@link DeviceAssetChannel} definition.
 * <p>
 * This entity is used to get information about channels installed in the device.
 *
 * @since 1.0.0
 */
public class DeviceAssetChannel {

    private String name;
    private Class<?> clazz;
    private DeviceAssetChannelMode mode;
    private Object value;
    private String error;
    private Date timestamp;

    /**
     * Gets the name.
     *
     * @return The name.
     * @since 1.0.0
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the name.
     *
     * @param name
     *         The name.
     * @since 1.0.0
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the {@link Class} type.
     * <p>
     * This is the {@link Class} of the {@link #getValue()}.
     *
     * @return The channel value type.
     * @since 1.0.0
     */
    public Class<?> getType() {
        return clazz;
    }

    /**
     * Sets the {@link Class} type. This type must be coherent with the value given to {@link #setValue(Object)}. If not, errors will occur during the interaction with the device.
     *
     * @param type
     *         The {@link Class} type.
     * @since 1.0.0
     */
    public void setType(Class<?> type) {
        this.clazz = type;
    }

    /**
     * Gets the {@link DeviceAssetChannelMode}.
     *
     * @return The {@link DeviceAssetChannelMode}.
     * @since 1.0.0
     */
    public DeviceAssetChannelMode getMode() {
        return mode;
    }

    /**
     * Sets the {@link DeviceAssetChannelMode}.
     *
     * @param deviceAssetChannelMode
     *         The {@link DeviceAssetChannelMode}.
     * @since 1.0.0
     */
    public void setMode(DeviceAssetChannelMode deviceAssetChannelMode) {
        this.mode = deviceAssetChannelMode;
    }

    /**
     * Gets the value channel.
     * <p>
     * Depending on the {@link DeviceAssetChannelMode} this can be a value {@link DeviceAssetChannelMode#READ} from the channel or to {@link DeviceAssetChannelMode#WRITE} into the channel. This is
     * mutually exclusive with {@link #getError()}
     *
     * @return The value channel.
     * @since 1.0.0
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the value channel.
     * <p>
     * Depending on the {@link DeviceAssetChannelMode} this can be a value {@link DeviceAssetChannelMode#READ} from the channel or to {@link DeviceAssetChannelMode#WRITE} into the channel.
     *
     * @param value
     *         The value channel.
     * @since 1.0.0
     */
    public void setValue(Object value) {
        this.value = value;

        if (value != null) {
            setType(value.getClass());
        }
    }

    /**
     * Gets the error message.
     * <p>
     * When reading from or writing to a channel, if any error occurs it will be reported here. This is mutually exclusive with {@link #getValue()}
     *
     * @return The error message, if error has occurred.
     * @since 1.0.0
     */
    public String getError() {
        return error;
    }

    /**
     * Sets the error message.
     * <p>
     * This must be set if error has occurred during reading from/wrtiting to
     *
     * @param error
     *         The error message.
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * Gets the {@link Date} of the time when the value was read from the channel.
     *
     * @return The {@link Date}  of the time when the value was read from the channel.
     * @since 1.0.0
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the {@link Date} of the time when the value was read from the channel.
     *
     * @param timestamp
     *         The {@link Date} of the time when the value was read from the channel.
     * @since 1.0.0
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
