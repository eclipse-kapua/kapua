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
package org.eclipse.kapua.service.device.management.keystore.internal.message.response;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystore;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreCSR;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreItem;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreItems;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystores;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponsePayload;

/**
 * {@link DeviceKeystore} {@link KapuaResponsePayload} implementation.
 *
 * @since 1.5.0
 */
public class KeystoreResponsePayload extends KapuaResponsePayload {

    private static final long serialVersionUID = 4380715272822080425L;

    private final String charEncoding = KapuaLocator.getInstance().getComponent(DeviceManagementSetting.class).getString(DeviceManagementSettingKey.CHAR_ENCODING);
    private final XmlUtil xmlUtil = KapuaLocator.getInstance().getComponent(XmlUtil.class);

    /**
     * Gets the {@link DeviceKeystores} from the {@link #getBody()}.
     *
     * @return The {@link DeviceKeystores} from the {@link #getBody()}.
     * @throws Exception
     *         if reading {@link #getBody()} errors.
     * @since 1.5.0
     */
    public Optional<DeviceKeystores> getKeystores() throws Exception {
        if (!hasBody()) {
            return Optional.empty();
        }

        String bodyString = new String(getBody(), charEncoding);
        return Optional.ofNullable(xmlUtil.unmarshal(bodyString, DeviceKeystores.class));
    }

    /**
     * Sets the {@link DeviceKeystores} in the {@link #getBody()}.
     *
     * @param keystores
     *         The {@link DeviceKeystores} in the {@link #getBody()}.
     * @throws Exception
     *         if writing errors.
     * @since 1.5.0
     */
    public void setKeystores(@NotNull DeviceKeystores keystores) throws Exception {
        String bodyString = xmlUtil.marshal(keystores);
        setBody(bodyString.getBytes(charEncoding));
    }

    /**
     * Gets the {@link DeviceKeystoreItems} from the {@link #getBody()}.
     *
     * @return The {@link DeviceKeystoreItems} from the {@link #getBody()}.
     * @throws Exception
     *         if reading {@link #getBody()} errors.
     * @since 1.5.0
     */
    public Optional<DeviceKeystoreItems> getKeystoreItems() throws Exception {
        if (!hasBody()) {
            return Optional.empty();
        }

        String bodyString = new String(getBody(), charEncoding);
        return Optional.ofNullable(xmlUtil.unmarshal(bodyString, DeviceKeystoreItems.class));
    }

    /**
     * Sets the {@link DeviceKeystoreItems} in the {@link #getBody()}.
     *
     * @param keystoreItems
     *         The {@link DeviceKeystoreItems} in the {@link #getBody()}.
     * @throws Exception
     *         if writing errors.
     * @since 1.5.0
     */
    public void setKeystoreItems(@NotNull DeviceKeystoreItems keystoreItems) throws Exception {
        String bodyString = xmlUtil.marshal(keystoreItems);
        setBody(bodyString.getBytes(charEncoding));
    }

    /**
     * Gets the {@link DeviceKeystoreItem} from the {@link #getBody()}.
     *
     * @return The {@link DeviceKeystoreItem} from the {@link #getBody()}.
     * @throws Exception
     *         if reading {@link #getBody()} errors.
     * @since 1.5.0
     */
    public Optional<DeviceKeystoreItem> getKeystoreItem() throws Exception {
        if (!hasBody()) {
            return Optional.empty();
        }

        String bodyString = new String(getBody(), charEncoding);
        return Optional.ofNullable(xmlUtil.unmarshal(bodyString, DeviceKeystoreItem.class));
    }

    /**
     * Sets the {@link DeviceKeystoreItem} in the {@link #getBody()}.
     *
     * @param keystoreItem
     *         The {@link DeviceKeystoreItem} in the {@link #getBody()}.
     * @throws Exception
     *         if writing errors.
     * @since 1.5.0
     */
    public void setKeystoreItem(@NotNull DeviceKeystoreItem keystoreItem) throws Exception {
        String bodyString = xmlUtil.marshal(keystoreItem);
        setBody(bodyString.getBytes(charEncoding));
    }

    /**
     * Gets the {@link DeviceKeystoreCSR} from the {@link #getBody()}.
     *
     * @return The {@link DeviceKeystoreCSR} from the {@link #getBody()}.
     * @throws Exception
     *         if reading {@link #getBody()} errors.
     * @since 1.5.0
     */
    public Optional<DeviceKeystoreCSR> getCSR() throws Exception {
        if (!hasBody()) {
            return Optional.empty();
        }

        String bodyString = new String(getBody(), charEncoding);
        return Optional.ofNullable(xmlUtil.unmarshal(bodyString, DeviceKeystoreCSR.class));
    }

    /**
     * Sets the {@link DeviceKeystoreCSR} in the {@link #getBody()}.
     *
     * @param deviceCSR
     *         The {@link DeviceKeystoreCSR} in the {@link #getBody()}.
     * @throws Exception
     *         if writing errors.
     * @since 1.5.0
     */
    public void setCSR(@NotNull DeviceKeystoreCSR deviceCSR) throws Exception {
        String bodyString = xmlUtil.marshal(deviceCSR);
        setBody(bodyString.getBytes(charEncoding));
    }
}
