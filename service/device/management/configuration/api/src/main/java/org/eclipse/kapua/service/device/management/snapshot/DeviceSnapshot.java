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
package org.eclipse.kapua.service.device.management.snapshot;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * {@link DeviceSnapshot} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "snapshot")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DeviceSnapshot {

    private String id;
    private Long timestamp;

    /**
     * Gets the identifier.
     *
     * @return The identifier.
     * @since 1.0.0
     */
    @XmlElement(name = "id")
    public String getId() {
        return id;
    }

    /**
     * Sets the identifier.
     *
     * @param id
     *         The identifier.
     * @since 1.0.0
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the timestamp.
     *
     * @return The timestamp.
     * @since 1.0.0
     */
    @XmlElement(name = "timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp.
     *
     * @param timestamp
     *         The timestamp.
     * @since 1.0.0
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

}
