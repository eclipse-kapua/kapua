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
package org.eclipse.kapua.service.device.management.bundle;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.KapuaSerializable;

/**
 * {@link DeviceBundles} definition.
 *
 * @since 1.0.0
 */
@XmlType
@XmlRootElement(name = "bundles")
public class DeviceBundles implements KapuaSerializable {

    private static final long serialVersionUID = 734716753080998855L;

    private List<DeviceBundle> bundles;

    /**
     * Gets the {@link List} of {@link DeviceBundle}
     *
     * @return The {@link List} of {@link DeviceBundle}
     * @since 1.0.0
     */
    @XmlElement(name = "bundle")
    public List<DeviceBundle> getBundles() {
        if (bundles == null) {
            bundles = new ArrayList<>();
        }

        return bundles;
    }
}
