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
package org.eclipse.kapua.service.device.management.keystore.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.KapuaSerializable;
import org.eclipse.kapua.service.device.registry.Device;

/**
 * {@link DeviceKeystoreCSR} definition.
 * <p>
 * Contains the result of the {@link DeviceKeystoreCSRInfo}.
 *
 * @since 1.5.0
 */
@XmlRootElement(name = "deviceKeystoreCSR")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DeviceKeystoreCSR implements KapuaSerializable {

    private String signingRequest;

    /**
     * Gets the certificate signing request from the {@link Device#getId()}.
     *
     * @return The certificate signing request from the {@link Device#getId()}.
     * @since 1.5.0
     */
    @XmlElement(name = "signingRequest")
    public String getSigningRequest() {
        return signingRequest;
    }

    /**
     * Sets the certificate signing request from the {@link Device#getId()}.
     *
     * @param signingRequest
     *         The signing request from the {@link Device#getId()}.
     * @since 1.5.0
     */
    public void setSigningRequest(String signingRequest) {
        this.signingRequest = signingRequest;
    }

}
