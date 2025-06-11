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
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link DeviceKeystoreCSR} definition.
 * <p>
 * Contains the result of the {@link DeviceKeystoreCSRInfo}.
 *
 * @since 1.5.0
 */
@XmlRootElement(name = "deviceKeystoreCSR")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = DeviceKeystoreXmlRegistry.class, factoryMethod = "newDeviceKeystoreCSR")
public interface DeviceKeystoreCSR extends KapuaSerializable {

    /**
     * Gets the certificate signing request from the {@link Device#getId()}.
     *
     * @return The certificate signing request from the {@link Device#getId()}.
     * @since 1.5.0
     */
    @XmlElement(name = "signingRequest")
    @Schema(
        example = "-----BEGIN CERTIFICATE " +
                      "REQUEST-----\\nMIICgTCCAWkCAQAwPDELMAkGA1UEBhMCVVMxEDAOBgNVBAoTB0VjbGlwc2UxDDAK" +
                      "\\nBgNVBAsTA0lvVDENMAsGA1UEAxMES3VyYTCCASIwDQYJKoZIhvcNAQEBBQADggEP" +
                      "\\nADCCAQoCggEBAKpmnJeOJ7wczIMj3nUe+qxAtfJaXhUJkGy+bQuEfSEKRhA9QXAT" +
                      "\\nbt6N5alSj9mHb0OcOESBdUEr8vt28d5qHyHUUJ3yOJH3qURGO3He8yqLuUmgMgdK\\nDtp5bGFy5ltW/F" +
                      "+ASB8vJlX2jaC/Tybq8KjPTzVeEIilyQ9LDQMLmH7l+WklkpsK\\nLZHF" +
                      "+2fATJK7HISijozZiVfk8EFi5JXbGo9VFlKouwTU3V2NVY9f4cIftPb5pNs2\\nlEL+ZkAuaPksHzkI0z+bPwR4" +
                      "+tlMTxgcQE25r7fPK3FYEuOugSV8zGghI1dBDAHx" +
                      "\\neHYVpduJPhz7RtdVw3x7eM7I1C2IrmfHaP0CAwEAAaAAMA0GCSqGSIb3DQEBCwUA\\nA4IBAQAC8rvMaHZ" +
                      "+7szRm490O0nOj2wC0yngvciyBvCqEiKGmlOjeXxJAVjTG+r6\\ntXe6Jce9weIRdbI0HHVWkNVBX7Z0xjuD" +
                      "/SjrXOKjx1gm1DTbkp97OTBXuPhuiNXq\\nIhvy/j0P/yFRAUP+YRkV6N5OE76fUst" +
                      "/VHUvMWbEEnH9qPGYmSwV4yBgsSRiL4km\\n84uuNDaILuCuYqTMtfoPSrfcILrKMfmPRvNE5DNDbk/BsR33zyBXCjnd" +
                      "+/P61sKo\\nVSn6maFDBHcZP2jkBOBr8QmW8jt3oR9qWX5LXBpEHkmki8cy6FEhUOGZIuPAd8Rj" +
                      "\\nPfZ8kKHpraMQuOeg0ZsZcZzlZsa8\\n-----END CERTIFICATE REQUEST-----\\n"
    )
    String getSigningRequest();

    /**
     * Sets the certificate signing request from the {@link Device#getId()}.
     *
     * @param signingRequest The signing request from the {@link Device#getId()}.
     * @since 1.5.0
     */
    void setSigningRequest(String signingRequest);
}
