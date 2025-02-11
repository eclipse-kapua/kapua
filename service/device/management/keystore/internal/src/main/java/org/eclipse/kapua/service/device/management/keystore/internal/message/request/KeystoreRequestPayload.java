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
package org.eclipse.kapua.service.device.management.keystore.internal.message.request;

import java.util.Optional;

import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystore;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreCSRInfo;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreCertificate;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreItemQuery;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreKeypair;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestPayload;

/**
 * {@link DeviceKeystore} {@link KapuaRequestPayload} implementation.
 *
 * @since 1.5.0
 */
public class KeystoreRequestPayload extends KapuaRequestPayload {

    private static final long serialVersionUID = 837931637524736407L;

    private final String charEncoding = KapuaLocator.getInstance().getComponent(DeviceManagementSetting.class).getString(DeviceManagementSettingKey.CHAR_ENCODING);
    private final XmlUtil xmlUtil = KapuaLocator.getInstance().getComponent(XmlUtil.class);

    /**
     * Gets the {@link DeviceKeystoreItemQuery} from the {@link KapuaRequestPayload#getBody()}
     *
     * @return The {@link DeviceKeystoreItemQuery}.
     * @throws Exception
     *         if {@link KapuaRequestPayload#getBody()} is not a {@link DeviceKeystoreItemQuery}.
     * @since 1.5.0
     */
    public Optional<DeviceKeystoreItemQuery> getItemQuery() throws Exception {
        if (!hasBody()) {
            return Optional.empty();
        }

        String bodyString = new String(getBody(), charEncoding);
        return Optional.ofNullable(xmlUtil.unmarshal(bodyString, DeviceKeystoreItemQuery.class));
    }

    /**
     * Sets the {@link DeviceKeystoreItemQuery} into the {@link KapuaRequestPayload#getBody()}
     *
     * @param itemQuery
     *         The {@link DeviceKeystoreItemQuery}
     * @throws Exception
     *         if the given {@link DeviceKeystoreItemQuery} is not serializable into the {@link KapuaRequestPayload#getBody()}
     * @since 1.5.0
     */
    public void setItemQuery(DeviceKeystoreItemQuery itemQuery) throws Exception {
        String bodyString = xmlUtil.marshal(itemQuery);
        setBody(bodyString.getBytes(charEncoding));
    }

    /**
     * Gets the {@link DeviceKeystoreCertificate} from the {@link KapuaRequestPayload#getBody()}
     *
     * @return The {@link DeviceKeystoreCertificate}.
     * @throws Exception
     *         if {@link KapuaRequestPayload#getBody()} is not a {@link DeviceKeystoreCertificate}.
     * @since 1.5.0
     */
    public Optional<DeviceKeystoreCertificate> getCertificate() throws Exception {
        if (!hasBody()) {
            Optional.empty();
        }

        String bodyString = new String(getBody(), charEncoding);
        return Optional.ofNullable(xmlUtil.unmarshal(bodyString, DeviceKeystoreCertificate.class));
    }

    /**
     * Sets the {@link DeviceKeystoreCertificate} into the {@link KapuaRequestPayload#getBody()}
     *
     * @param certificate
     *         The {@link DeviceKeystoreCertificate}
     * @throws Exception
     *         if the given {@link DeviceKeystoreCertificate} is not serializable into the {@link KapuaRequestPayload#getBody()}
     * @since 1.5.0
     */
    public void setCertificate(DeviceKeystoreCertificate certificate) throws Exception {
        String bodyString = xmlUtil.marshal(certificate);
        setBody(bodyString.getBytes(charEncoding));
    }

    /**
     * Gets the {@link DeviceKeystoreKeypair} from the {@link KapuaRequestPayload#getBody()}
     *
     * @return The {@link DeviceKeystoreKeypair}.
     * @throws Exception
     *         if {@link KapuaRequestPayload#getBody()} is not a {@link DeviceKeystoreKeypair}.
     * @since 1.5.0
     */
    public Optional<DeviceKeystoreKeypair> getKeypair() throws Exception {
        if (!hasBody()) {
            return Optional.empty();
        }

        String bodyString = new String(getBody(), charEncoding);
        return Optional.of(xmlUtil.unmarshal(bodyString, DeviceKeystoreKeypair.class));
    }

    /**
     * Sets the {@link DeviceKeystoreKeypair} into the {@link KapuaRequestPayload#getBody()}
     *
     * @param keypair
     *         The {@link DeviceKeystoreKeypair}
     * @throws Exception
     *         if the given {@link DeviceKeystoreKeypair} is not serializable into the {@link KapuaRequestPayload#getBody()}
     * @since 1.5.0
     */
    public void setKeypair(DeviceKeystoreKeypair keypair) throws Exception {
        String bodyString = xmlUtil.marshal(keypair);
        setBody(bodyString.getBytes(charEncoding));
    }

    /**
     * Gets the {@link DeviceKeystoreCSRInfo} from the {@link KapuaRequestPayload#getBody()}
     *
     * @return The {@link DeviceKeystoreCSRInfo}.
     * @throws Exception
     *         if {@link KapuaRequestPayload#getBody()} is not a {@link DeviceKeystoreCSRInfo}.
     * @since 1.5.0
     */
    public Optional<DeviceKeystoreCSRInfo> getCSRInfo() throws Exception {
        if (!hasBody()) {
            return Optional.empty();
        }

        String bodyString = new String(getBody(), charEncoding);
        return Optional.ofNullable(xmlUtil.unmarshal(bodyString, DeviceKeystoreCSRInfo.class));
    }

    /**
     * Sets the {@link DeviceKeystoreCSRInfo} into the {@link KapuaRequestPayload#getBody()}
     *
     * @param csrInfo
     *         The {@link DeviceKeystoreCSRInfo}
     * @throws Exception
     *         if the given {@link DeviceKeystoreCSRInfo} is not serializable into the {@link KapuaRequestPayload#getBody()}
     * @since 1.5.0
     */
    public void setCsrInfo(DeviceKeystoreCSRInfo csrInfo) throws Exception {
        String bodyString = xmlUtil.marshal(csrInfo);
        setBody(bodyString.getBytes(charEncoding));
    }
}
