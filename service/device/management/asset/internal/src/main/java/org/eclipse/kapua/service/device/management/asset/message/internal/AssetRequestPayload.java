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
package org.eclipse.kapua.service.device.management.asset.message.internal;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.device.management.asset.DeviceAssets;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestPayload;

/**
 * {@link DeviceAssets} {@link KapuaRequestPayload} implementation.
 *
 * @since 1.0.0
 */
public class AssetRequestPayload extends KapuaRequestPayload {

    private static final long serialVersionUID = -4372614820336612199L;
    private final String charEncoding = KapuaLocator.getInstance().getComponent(DeviceManagementSetting.class).getString(DeviceManagementSettingKey.CHAR_ENCODING);
    private final XmlUtil xmlUtil = KapuaLocator.getInstance().getComponent(XmlUtil.class);

    /**
     * Gets the {@link DeviceAssets} from the {@link #getBody()}.
     *
     * @return The {@link DeviceAssets} from the {@link #getBody()}.
     * @throws Exception
     *         if reading {@link #getBody()} errors.
     * @since 1.0.0
     */
    public Optional<DeviceAssets> getDeviceAssets() throws Exception {
        if (!hasBody()) {
            return Optional.empty();
        }

        String bodyString = new String(getBody(), charEncoding);
        return Optional.ofNullable(xmlUtil.unmarshal(bodyString, DeviceAssets.class));
    }

    /**
     * Sets the {@link DeviceAssets} in the {@link #getBody()}.
     *
     * @param deviceAssets
     *         The {@link DeviceAssets} in the {@link #getBody()}.
     * @throws Exception
     *         if writing errors.
     * @since 1.0.0
     */
    public void setDeviceAssets(@NotNull DeviceAssets deviceAssets) throws Exception {
        String bodyString = xmlUtil.marshal(deviceAssets);
        setBody(bodyString.getBytes(charEncoding));
    }

}
