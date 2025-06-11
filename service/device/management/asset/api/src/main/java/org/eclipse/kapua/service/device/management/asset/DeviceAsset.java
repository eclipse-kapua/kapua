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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.service.device.management.asset.xml.DeviceAssetChannelXmlAdapter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link DeviceAsset} definition.
 * <p>
 * This entity is used to get information about assets installed in the device.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "deviceAsset")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = DeviceAssetXmlRegistry.class, factoryMethod = "newDeviceAsset")
public interface DeviceAsset {

    /**
     * Gets the name.
     *
     * @return The name.
     * @since 1.0.0
     */
    @XmlElement(name = "name")
    @Schema(description = "The name of the DeviceAsset")
    String getName();

    /**
     * Sets the name.
     *
     * @param name The name.
     * @since 1.0.0
     */
    void setName(String name);

    /**
     * Gets the {@link DeviceAssetChannel} available.
     *
     * @return The {@link DeviceAssetChannel} available.
     * @since 1.0.0
     */
    @XmlElementWrapper(name = "channels")
    @XmlElement(name = "channel")
    @XmlJavaTypeAdapter(DeviceAssetChannelXmlAdapter.class)
    List<DeviceAssetChannel> getChannels();

    /**
     * Sets the {@link DeviceAssetChannel} available.
     *
     * @param channels The {@link DeviceAssetChannel} available.
     * @since 1.0.0
     */
    void setChannels(List<DeviceAssetChannel> channels);
}
