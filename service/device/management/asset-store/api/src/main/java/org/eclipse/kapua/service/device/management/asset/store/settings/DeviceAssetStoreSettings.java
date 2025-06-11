/*******************************************************************************
 * Copyright (c) 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.asset.store.settings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.service.device.management.app.settings.ByDeviceAppManagementSettings;
import org.eclipse.kapua.service.device.management.asset.store.DeviceAssetStoreService;
import org.eclipse.kapua.service.device.management.asset.store.DeviceAssetStoreXmlFactory;
import org.eclipse.kapua.service.device.registry.Device;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link DeviceAssetStoreSettings} definition.
 * <p>
 * It represents settings of {@link DeviceAssetStoreService} by {@link Device}
 *
 * @since 2.0.0
 */
@XmlRootElement(name = "deviceAssetStoreSettings")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = DeviceAssetStoreXmlFactory.class, factoryMethod = "newDeviceAssetStoreSettings")
@Schema(description = "Base class for Device Management Applications that have per-device settings")
public interface DeviceAssetStoreSettings extends ByDeviceAppManagementSettings {

    /**
     * Gets the {@link DeviceAssetStoreEnablementPolicy}
     *
     * @return The {@link DeviceAssetStoreEnablementPolicy}
     * @since 2.0.0
     */
    @XmlElement(name = "enablementPolicy")
    DeviceAssetStoreEnablementPolicy getEnablementPolicy();

    /**
     * Sets the {@link DeviceAssetStoreEnablementPolicy}
     *
     * @param enablementPolicy The {@link DeviceAssetStoreEnablementPolicy}
     * @since 2.0.0
     */
    void setEnablementPolicy(DeviceAssetStoreEnablementPolicy enablementPolicy);
}
