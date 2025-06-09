/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.inventory.internal.message;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundles;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainers;
import org.eclipse.kapua.service.device.management.inventory.model.inventory.DeviceInventory;
import org.eclipse.kapua.service.device.management.inventory.model.packages.DeviceInventoryPackages;
import org.eclipse.kapua.service.device.management.inventory.model.system.DeviceInventorySystemPackages;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponsePayload;

/**
 * {@link DeviceInventory} {@link KapuaResponsePayload} implementation.
 *
 * @since 1.5.0
 */
public class InventoryResponsePayload extends KapuaResponsePayload {

    private static final long serialVersionUID = 4380715272822080425L;

    private final String charEncoding = KapuaLocator.getInstance().getComponent(DeviceManagementSetting.class).getString(DeviceManagementSettingKey.CHAR_ENCODING);
    private final XmlUtil xmlUtil = KapuaLocator.getInstance().getComponent(XmlUtil.class);

    /**
     * Gets the {@link DeviceInventory} from the {@link #getBody()}.
     *
     * @return The {@link DeviceInventory} from the {@link #getBody()}.
     * @throws Exception
     *         if reading {@link #getBody()} errors.
     * @since 1.5.0
     */
    public Optional<DeviceInventory> getDeviceInventory() throws Exception {
        if (!hasBody()) {
            return Optional.empty();
        }

        String bodyString = new String(getBody(), charEncoding);
        return Optional.ofNullable(xmlUtil.unmarshal(bodyString, DeviceInventory.class));
    }

    /**
     * Sets the {@link DeviceInventory} in the {@link #getBody()}.
     *
     * @param deviceInventory
     *         The {@link DeviceInventory} in the {@link #getBody()}.
     * @throws Exception
     *         if writing errors.
     * @since 1.5.0
     */
    public void setDeviceInventory(@NotNull DeviceInventory deviceInventory) throws Exception {
        String bodyString = xmlUtil.marshal(deviceInventory);
        setBody(bodyString.getBytes(charEncoding));
    }

    /**
     * Gets the {@link DeviceInventoryBundles} from the {@link #getBody()}.
     *
     * @return The {@link DeviceInventoryBundles} from the {@link #getBody()}.
     * @throws Exception
     *         if reading {@link #getBody()} errors.
     * @since 1.5.0
     */
    public Optional<DeviceInventoryBundles> getDeviceInventoryBundles() throws Exception {
        if (!hasBody()) {
            return Optional.empty();
        }

        String bodyString = new String(getBody(), charEncoding);
        return Optional.ofNullable(xmlUtil.unmarshal(bodyString, DeviceInventoryBundles.class));
    }

    /**
     * Sets the {@link DeviceInventoryBundles} in the {@link #getBody()}.
     *
     * @param inventoryBundles
     *         The {@link DeviceInventoryBundles} in the {@link #getBody()}.
     * @throws Exception
     *         if writing errors.
     * @since 1.5.0
     */
    public void setDeviceInventoryBundles(@NotNull DeviceInventoryBundles inventoryBundles) throws Exception {
        String bodyString = xmlUtil.marshal(inventoryBundles);
        setBody(bodyString.getBytes(charEncoding));
    }

    /**
     * Gets the {@link DeviceInventoryContainers} from the {@link #getBody()}.
     *
     * @return The {@link DeviceInventoryContainers} from the {@link #getBody()}.
     * @throws Exception
     *         if reading {@link #getBody()} errors.
     * @since 2.0.0
     */
    public Optional<DeviceInventoryContainers> getDeviceInventoryContainers() throws Exception {
        if (!hasBody()) {
            return Optional.empty();
        }

        String bodyString = new String(getBody(), charEncoding);
        return Optional.ofNullable(xmlUtil.unmarshal(bodyString, DeviceInventoryContainers.class));
    }

    /**
     * Sets the {@link DeviceInventoryContainers} in the {@link #getBody()}.
     *
     * @param inventoryContainers
     *         The {@link DeviceInventoryContainers} in the {@link #getBody()}.
     * @throws Exception
     *         if writing errors.
     * @since 2.0.0
     */
    public void setDeviceInventoryContainers(@NotNull DeviceInventoryContainers inventoryContainers) throws Exception {
        String bodyString = xmlUtil.marshal(inventoryContainers);
        setBody(bodyString.getBytes(charEncoding));
    }

    /**
     * Gets the {@link DeviceInventorySystemPackages} from the {@link #getBody()}.
     *
     * @return The {@link DeviceInventorySystemPackages} from the {@link #getBody()}.
     * @throws Exception
     *         if reading {@link #getBody()} errors.
     * @since 1.5.0
     */
    public Optional<DeviceInventorySystemPackages> getDeviceInventorySystemPackages() throws Exception {
        if (!hasBody()) {
            return Optional.empty();
        }

        String bodyString = new String(getBody(), charEncoding);
        return Optional.ofNullable(xmlUtil.unmarshal(bodyString, DeviceInventorySystemPackages.class));
    }

    /**
     * Sets the {@link DeviceInventorySystemPackages} in the {@link #getBody()}.
     *
     * @param systemPackages
     *         The {@link DeviceInventorySystemPackages} in the {@link #getBody()}.
     * @throws Exception
     *         if writing errors.
     * @since 1.5.0
     */
    public void setDeviceInventorySystemPackages(@NotNull DeviceInventorySystemPackages systemPackages) throws Exception {
        String bodyString = xmlUtil.marshal(systemPackages);
        setBody(bodyString.getBytes(charEncoding));
    }

    /**
     * Gets the {@link DeviceInventoryPackages} from the {@link #getBody()}.
     *
     * @return The {@link DeviceInventoryPackages} from the {@link #getBody()}.
     * @throws Exception
     *         if reading {@link #getBody()} errors.
     * @since 1.5.0
     */
    public Optional<DeviceInventoryPackages> getDeviceInventoryPackages() throws Exception {
        if (!hasBody()) {
            return Optional.empty();
        }

        String bodyString = new String(getBody(), charEncoding);
        return Optional.ofNullable(xmlUtil.unmarshal(bodyString, DeviceInventoryPackages.class));
    }

    /**
     * Sets the {@link DeviceInventoryPackages} in the {@link #getBody()}.
     *
     * @param packages
     *         The {@link DeviceInventoryPackages} in the {@link #getBody()}.
     * @throws Exception
     *         if writing errors.
     * @since 1.5.0
     */
    public void setDeviceInventoryPackages(@NotNull DeviceInventoryPackages packages) throws Exception {
        String bodyString = xmlUtil.marshal(packages);
        setBody(bodyString.getBytes(charEncoding));
    }
}
