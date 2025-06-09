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
package org.eclipse.kapua.message.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Embeddable;

import org.eclipse.kapua.commons.util.Payloads;
import org.eclipse.kapua.message.KapuaPosition;

/**
 * {@link KapuaPosition} implementation.
 *
 * @since 1.0.0
 */
@Embeddable
public class KapuaPositionImpl {

    private Double longitude;
    private Double latitude;
    private Double altitude;
    private Double precision;
    private Double heading;
    private Double speed;
    private Date timestamp;
    private Integer satellites;
    private Integer status;

    public KapuaPositionImpl() {
    }

    public KapuaPositionImpl(KapuaPosition kapuaPosition) {
        this.longitude = kapuaPosition.getLongitude();
        this.latitude = kapuaPosition.getLatitude();
        this.altitude = kapuaPosition.getAltitude();
        this.precision = kapuaPosition.getPrecision();
        this.heading = kapuaPosition.getHeading();
        this.speed = kapuaPosition.getSpeed();
        this.timestamp = kapuaPosition.getTimestamp();
        this.satellites = kapuaPosition.getSatellites();
        this.status = kapuaPosition.getStatus();
    }

    public Double getLongitude() {
        return longitude;
    }

    public KapuaPositionImpl setLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public Double getLatitude() {
        return latitude;
    }

    public KapuaPositionImpl setLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public Double getAltitude() {
        return altitude;
    }

    public KapuaPositionImpl setAltitude(Double altitude) {
        this.altitude = altitude;
        return this;
    }

    public Double getPrecision() {
        return precision;
    }

    public KapuaPositionImpl setPrecision(Double precision) {
        this.precision = precision;
        return this;
    }

    public Double getHeading() {
        return heading;
    }

    public KapuaPositionImpl setHeading(Double heading) {
        this.heading = heading;
        return this;
    }

    public Double getSpeed() {
        return speed;
    }

    public KapuaPositionImpl setSpeed(Double speed) {
        this.speed = speed;
        return this;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public KapuaPositionImpl setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Integer getSatellites() {
        return satellites;
    }

    public KapuaPositionImpl setSatellites(Integer satellites) {
        this.satellites = satellites;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public KapuaPositionImpl setStatus(Integer status) {
        this.status = status;
        return this;
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

    public KapuaPosition toPosition() {
        final KapuaPosition kapuaPosition = new KapuaPosition();
        kapuaPosition.setLatitude(getLatitude());
        kapuaPosition.setLongitude(getLongitude());
        kapuaPosition.setAltitude(getAltitude());
        kapuaPosition.setPrecision(getPrecision());
        kapuaPosition.setHeading(getHeading());
        kapuaPosition.setSpeed(getSpeed());
        kapuaPosition.setTimestamp(getTimestamp());
        kapuaPosition.setSatellites(getSatellites());
        kapuaPosition.setStatus(getStatus());
        return kapuaPosition;
    }
}
