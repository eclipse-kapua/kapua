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

import org.eclipse.kapua.message.Position;

/**
 * {@link DevicePosition} definition.
 * <p>
 * This represent GPS data bound to a {@link DeviceMessage}.
 *
 * @since 1.0.0
 */
public interface DevicePosition extends Position {

    /**
     * Gets the position longitude.
     *
     * @return The position longitude. The position longitude..
     * @since 1.0.0
     */
    Double getLongitude();

    /**
     * Sets the position longitude.
     *
     * @param longitude
     *         The position longitude.
     * @since 1.0.0
     */
    void setLongitude(Double longitude);

    /**
     * Gets the position latitude.
     *
     * @return The position latitude.
     * @since 1.0.0
     */
    Double getLatitude();

    /**
     * Sets the position latitude.
     *
     * @param latitude
     *         The position latitude.
     * @since 1.0.0
     */
    void setLatitude(Double latitude);

    /**
     * Gets the position altitude.
     *
     * @return The position altitude.
     * @since 1.0.0
     */
    Double getAltitude();

    /**
     * Sets the position altitude.
     *
     * @param altitude
     *         The position altitude.
     * @since 1.0.0
     */
    void setAltitude(Double altitude);

    /**
     * Gets the precision.
     *
     * @return The precision.
     * @since 1.0.0
     */
    Double getPrecision();

    /**
     * Sets the precision.
     *
     * @param precision
     *         The precision.
     * @since 1.0.0
     */
    void setPrecision(Double precision);

    /**
     * Gets the heading.
     *
     * @return The heading.
     * @since 1.0.0
     */
    Double getHeading();

    /**
     * Sets the heading.
     *
     * @param heading
     *         The heading.
     * @since 1.0.0
     */
    void setHeading(Double heading);

    /**
     * Gets the speed.
     *
     * @return The speed.
     * @since 1.0.0
     */
    Double getSpeed();

    /**
     * Sets the speed.
     *
     * @param speed
     *         The speed.
     * @since 1.0.0
     */
    void setSpeed(Double speed);

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
     *         The timestamp.
     * @since 1.0.0
     */
    void setTimestamp(Date timestamp);

    /**
     * Gets the satellites count.
     *
     * @return The satellites count.
     * @since 1.0.0
     */
    Integer getSatellites();

    /**
     * Sets the satellites count.
     *
     * @param satellites
     *         The satellites count.
     * @since 1.0.0
     */
    void setSatellites(Integer satellites);

    /**
     * Gets the status.
     *
     * @return The status.
     * @since 1.0.0
     */
    Integer getStatus();

    /**
     * Sets the status.
     *
     * @param status
     *         The status.
     * @since 1.0.0
     */
    void setStatus(Integer status);

}
