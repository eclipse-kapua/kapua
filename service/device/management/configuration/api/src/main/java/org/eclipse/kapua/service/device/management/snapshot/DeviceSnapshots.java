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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.KapuaSerializable;

/**
 * {@link DeviceSnapshots} definition.
 * <p>
 * This entity manages a list of {@link DeviceSnapshot}
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "snapshots")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DeviceSnapshots implements KapuaSerializable {

    private static final long serialVersionUID = -7831418953347834946L;

    private List<DeviceSnapshot> snapshotIds;

    /**
     * Gets the {@link DeviceSnapshot} {@link List}.
     *
     * @return The {@link DeviceSnapshot} {@link List}.
     * @since 1.0.0
     */
    @XmlElement(name = "snapshotId")
    public List<DeviceSnapshot> getSnapshots() {
        if (snapshotIds == null) {
            snapshotIds = new ArrayList<>();
        }

        return snapshotIds;
    }
}
