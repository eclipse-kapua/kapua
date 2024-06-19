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
package org.eclipse.kapua.app.api.resources.v1.resources.model.device.management.keystore;

import org.eclipse.kapua.commons.jersey.rest.model.EntityId;
import org.eclipse.kapua.commons.jersey.rest.model.ScopeId;
import org.eclipse.kapua.app.api.resources.v1.resources.DeviceManagementKeystores;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.certificate.info.CertificateInfo;
import org.eclipse.kapua.service.device.management.keystore.DeviceKeystoreManagementService;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystore;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreCertificate;

import javax.ws.rs.HttpMethod;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * {@link DeviceKeystoreCertificateInfo} definition.
 * <p>
 * Collects all parameters available on {@link DeviceKeystoreManagementService#createKeystoreCertificate(KapuaId, KapuaId, String, String, KapuaId, Long)} to allow to be {@link HttpMethod#POST}ed in
 * {@link DeviceManagementKeystores#createDeviceKeystoreCertificate(ScopeId, EntityId, Long, DeviceKeystoreCertificateInfo)}.
 *
 * @since 1.5.0
 */
@XmlRootElement(name = "deviceKeystoreCertificateInfo")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class DeviceKeystoreCertificateInfo {

    private String keystoreId;
    private String alias;
    private KapuaId certificateInfoId;

    /**
     * Constructor.
     *
     * @since 1.5.0
     */
    public DeviceKeystoreCertificateInfo() {
    }

    /**
     * Gets the target {@link DeviceKeystore#getId()}.
     *
     * @return The target {@link DeviceKeystore#getId()}.
     * @since 1.5.0
     */
    public String getKeystoreId() {
        return keystoreId;
    }

    /**
     * Sets the target {@link DeviceKeystore#getId()}.
     *
     * @param keystoreId
     *         The target {@link DeviceKeystore#getId()}.
     * @since 1.5.0
     */
    public void setKeystoreId(String keystoreId) {
        this.keystoreId = keystoreId;
    }

    /**
     * Gets the target {@link DeviceKeystoreCertificate#getAlias()}
     *
     * @return The target {@link DeviceKeystoreCertificate#getAlias()}
     * @since 1.5.0
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the target {@link DeviceKeystoreCertificate#getAlias()}
     *
     * @param alias
     *         The target {@link DeviceKeystoreCertificate#getAlias()}
     * @since 1.5.0
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Gets the {@link CertificateInfo#getId()}.
     *
     * @return The {@link CertificateInfo#getId()}.
     * @since 1.5.0
     */
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getCertificateInfoId() {
        return certificateInfoId;
    }

    /**
     * Sets the {@link CertificateInfo#getId()}.
     *
     * @param certificateInfoId
     *         The {@link CertificateInfo#getId()}.
     * @since 1.5.0
     */
    public void setCertificateInfoId(KapuaId certificateInfoId) {
        this.certificateInfoId = certificateInfoId;
    }
}
