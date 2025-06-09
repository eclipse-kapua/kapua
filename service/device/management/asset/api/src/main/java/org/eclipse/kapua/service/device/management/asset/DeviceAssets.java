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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.KapuaSerializable;

/**
 * {@link DeviceAssets} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "deviceAssets")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DeviceAssets implements KapuaSerializable {

    private static final long serialVersionUID = -6657213220333406876L;

    private List<DeviceAsset> assets;

    /**
     * Get the {@link DeviceAsset} {@link List}
     *
     * @return The {@link DeviceAsset} {@link List}.
     * @since 1.0.0
     */
    @XmlElement(name = "deviceAsset")
    public List<DeviceAsset> getAssets() {
        if (assets == null) {
            assets = new ArrayList<>();
        }

        return assets;
    }

    /**
     * Sets the {@link DeviceAsset} {@link List}.
     *
     * @param assets
     *         The {@link DeviceAsset} {@link List}.
     * @since 1.0.0
     */
    public void setAssets(List<DeviceAsset> assets) {
        this.assets = assets;
    }
}
