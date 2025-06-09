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
import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundle;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainer;
import org.eclipse.kapua.service.device.management.inventory.model.inventory.DeviceInventory;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestPayload;

/**
 * {@link DeviceInventory} {@link KapuaRequestPayload} implementation.
 *
 * @since 1.5.0
 */
public class InventoryRequestPayload extends KapuaRequestPayload {

    private static final long serialVersionUID = 837931637524736407L;

    private final String charEncoding = KapuaLocator.getInstance().getComponent(DeviceManagementSetting.class).getString(DeviceManagementSettingKey.CHAR_ENCODING);
    private final XmlUtil xmlUtil = KapuaLocator.getInstance().getComponent(XmlUtil.class);

    /**
     * Gets the {@link DeviceInventoryBundle} from the {@link #getBody()}.
     *
     * @return The {@link DeviceInventoryBundle} from the {@link #getBody()}.
     * @throws Exception
     *         if reading {@link #getBody()} errors.
     * @since 1.5.0
     */
    public Optional<DeviceInventoryBundle> getDeviceInventoryBundle() throws Exception {
        if (!hasBody()) {
            return Optional.empty();
        }

        String bodyString = new String(getBody(), charEncoding);
        return Optional.ofNullable(xmlUtil.unmarshal(bodyString, DeviceInventoryBundle.class));
    }

    /**
     * Sets the {@link DeviceInventoryBundle} in the {@link #getBody()}.
     *
     * @param deviceInventoryBundle
     *         The {@link DeviceInventoryBundle} in the {@link #getBody()}.
     * @throws Exception
     *         if writing errors.
     * @since 1.5.0
     */
    public void setDeviceInventoryBundle(@NotNull DeviceInventoryBundle deviceInventoryBundle) throws Exception {
        String bodyString = xmlUtil.marshal(deviceInventoryBundle);
        setBody(bodyString.getBytes(charEncoding));
    }

    /**
     * Gets the {@link DeviceInventoryContainer} from the {@link #getBody()}.
     *
     * @return The {@link DeviceInventoryContainer} from the {@link #getBody()}.
     * @throws Exception
     *         if reading {@link #getBody()} errors.
     * @since 2.0.0
     */
    public Optional<DeviceInventoryContainer> getDeviceInventoryContainer() throws Exception {
        if (!hasBody()) {
            return Optional.empty();
        }

        String bodyString = new String(getBody(), charEncoding);
        return Optional.ofNullable(xmlUtil.unmarshal(bodyString, DeviceInventoryContainer.class));
    }

    /**
     * Sets the {@link DeviceInventoryContainer} in the {@link #getBody()}.
     *
     * @param deviceInventoryContainer
     *         The {@link DeviceInventoryContainer} in the {@link #getBody()}.
     * @throws Exception
     *         if writing errors.
     * @since 2.0.0
     */
    public void setDeviceInventoryContainer(@NotNull DeviceInventoryContainer deviceInventoryContainer) throws Exception {
        String bodyString = xmlUtil.marshal(deviceInventoryContainer);
        setBody(bodyString.getBytes(charEncoding));
    }
}
