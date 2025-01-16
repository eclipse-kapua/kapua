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

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.KapuaSerializable;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link DeviceAssets} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "deviceAssets")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = DeviceAssetXmlRegistry.class, factoryMethod = "newAssetListResult")
@Schema(description = "A list of the DeviceAssetDefinitions")
public interface DeviceAssets extends KapuaSerializable {

    /**
     * Get the {@link DeviceAsset} {@link List}
     *
     * @return The {@link DeviceAsset} {@link List}.
     * @since 1.0.0
     */
    @XmlElement(name = "deviceAsset")
    List<DeviceAsset> getAssets();

    /**
     * Sets the {@link DeviceAsset} {@link List}.
     *
     * @param assets The {@link DeviceAsset} {@link List}.
     * @since 1.0.0
     */
    void setAssets(List<DeviceAsset> assets);
}
