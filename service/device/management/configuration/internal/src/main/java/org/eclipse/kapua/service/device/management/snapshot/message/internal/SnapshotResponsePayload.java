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
package org.eclipse.kapua.service.device.management.snapshot.message.internal;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponsePayload;
import org.eclipse.kapua.service.device.management.snapshot.DeviceSnapshots;

/**
 * {@link DeviceSnapshots} {@link KapuaResponsePayload} implementation.
 *
 * @since 1.0.0
 */
public class SnapshotResponsePayload extends KapuaResponsePayload {

    private static final long serialVersionUID = -5650474443429208877L;

    private final String charEncoding = KapuaLocator.getInstance().getComponent(DeviceManagementSetting.class).getString(DeviceManagementSettingKey.CHAR_ENCODING);
    private final XmlUtil xmlUtil = KapuaLocator.getInstance().getComponent(XmlUtil.class);

    /**
     * Gets the {@link DeviceSnapshots} from the {@link #getBody()}.
     *
     * @return The {@link DeviceSnapshots} from the {@link #getBody()}.
     * @throws Exception
     *         if reading {@link #getBody()} errors.
     * @since 1.5.0
     */
    public Optional<DeviceSnapshots> getDeviceSnapshots() throws Exception {
        if (!hasBody()) {
            return Optional.empty();
        }

        String bodyString = new String(getBody(), charEncoding);
        return Optional.ofNullable(xmlUtil.unmarshal(bodyString, DeviceSnapshots.class));
    }

    /**
     * Sets the {@link DeviceSnapshots} in the {@link #getBody()}.
     *
     * @param devicePackages
     *         The {@link DeviceSnapshots} in the {@link #getBody()}.
     * @throws Exception
     *         if writing errors.
     * @since 1.5.0
     */
    public void setDeviceSnapshots(@NotNull DeviceSnapshots devicePackages) throws Exception {
        String bodyString = xmlUtil.marshal(devicePackages);
        setBody(bodyString.getBytes(charEncoding));
    }

}
