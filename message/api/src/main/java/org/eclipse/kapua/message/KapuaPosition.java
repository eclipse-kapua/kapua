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
package org.eclipse.kapua.message;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.commons.util.Payloads;
import org.eclipse.kapua.model.xml.DateXmlAdapter;

/**
 * {@link KapuaPosition} definition.
 * <p>
 * {@link KapuaPosition} is a data structure to capture a geo location.
 * <p>
 * It can be associated to an {@link org.eclipse.kapua.message.KapuaPayload} to geotag an {@link org.eclipse.kapua.message.KapuaMessage} before sending to Kapua.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "position")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class KapuaPosition implements Position {

    private static final long serialVersionUID = 1L;

    private Double longitude;
    private Double latitude;
    private Double altitude;
    private Double precision;
    private Double heading;
    private Double speed;
    private Date timestamp;
    private Integer satellites;
    private Integer status;

    /**
     * Gets the GPS position longitude
     *
     * @return The GPS position longitude
     * @since 1.0.0
     */
    @XmlElement(name = "longitude")
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Sets the GPS position longitude
     *
     * @param longitude
     *         The GPS position longitude
     * @since 1.0.0
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the GPS position latitude
     *
     * @return The GPS position latitude
     * @since 1.0.0
     */
    @XmlElement(name = "latitude")
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Sets the GPS position latitude
     *
     * @param latitude
     *         The GPS position latitude
     * @since 1.0.0
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the GPS position altitude
     *
     * @return The GPS position altitude
     * @since 1.0.0
     */
    @XmlElement(name = "altitude")
    public Double getAltitude() {
        return altitude;
    }

    /**
     * Sets the GPS position altitude
     *
     * @param altitude
     *         The GPS position altitude
     * @since 1.0.0
     */
    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    /**
     * Gets the GPS precision
     *
     * @return The GPS precision
     * @since 1.0.0
     */
    @XmlElement(name = "precision")
    public Double getPrecision() {
        return precision;
    }

    /**
     * Sets the GPS precision
     *
     * @param precision
     *         The GPS precision
     * @since 1.0.0
     */
    public void setPrecision(Double precision) {
        this.precision = precision;
    }

    /**
     * Gets the GPS heading
     *
     * @return The GPS heading
     * @since 1.0.0
     */
    @XmlElement(name = "heading")
    public Double getHeading() {
        return heading;
    }

    /**
     * Sets the GPS heading
     *
     * @param heading
     *         The GPS heading
     * @since 1.0.0
     */
    public void setHeading(Double heading) {
        this.heading = heading;
    }

    /**
     * Gets the GPS speed
     *
     * @return The GPS speed.
     * @since 1.0.0
     */
    @XmlElement(name = "speed")
    public Double getSpeed() {
        return speed;
    }

    /**
     * Sets the GPS speed
     *
     * @param speed
     *         The GPS speed
     * @since 1.0.0
     */
    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    /**
     * Gets the timestamp
     *
     * @return The timestamp
     * @since 1.0.0
     */
    @XmlElement(name = "timestamp")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp
     *
     * @param timestamp
     *         The timestamp
     * @since 1.0.0
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the satellites count
     *
     * @return The satellites count
     * @since 1.0.0
     */
    @XmlElement(name = "satellites")
    public Integer getSatellites() {
        return satellites;
    }

    /**
     * Sets the satellites count
     *
     * @param satellites
     *         The satellites count.
     * @since 1.0.0
     */
    public void setSatellites(Integer satellites) {
        this.satellites = satellites;
    }

    /**
     * Gets the GPS status
     *
     * @return The GPS status
     * @since 1.0.0
     */
    @XmlElement(name = "status")
    public Integer getStatus() {
        return status;
    }

    /**
     * Sets the GPS status
     *
     * @param status
     *         The GPS status
     * @since 1.0.0
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * Converts the {@link KapuaPosition} attributes to a displayable {@link String}
     *
     * @return The displayable {@link String}
     * @since 1.0.0
     */
    public String toDisplayString() {

        Map<String, Object> properties = new HashMap<>();

        properties.put("latitude", getLatitude());
        properties.put("longitude", getLongitude());
        properties.put("altitude", getAltitude());
        properties.put("precision", getPrecision());
        properties.put("heading", getHeading());
        properties.put("speed", getSpeed());
        properties.put("timestamp", getTimestamp());
        properties.put("satellites", getSatellites());
        properties.put("status", getStatus());

        String displayString = Payloads.toDisplayString(properties);

        return displayString.isEmpty() ? null : displayString;
    }
}
