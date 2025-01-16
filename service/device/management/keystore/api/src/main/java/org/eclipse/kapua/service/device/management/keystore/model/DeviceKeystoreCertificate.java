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
 * {@link DeviceKeystoreCertificate} definition.
 * <p>
 * Represent a certificate to be created on the {@link Device}
 *
 * @since 1.5.0
 */
@XmlRootElement(name = "deviceKeystoreCertificate")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = DeviceKeystoreXmlRegistry.class, factoryMethod = "newDeviceKeystoreCertificate")
public interface DeviceKeystoreCertificate extends KapuaSerializable {

    /**
     * Gets the keystore id.
     *
     * @return The keystore id.
     * @since 1.5.0
     */
    @XmlElement(name = "keystoreId")
    @Schema(example = "SSLKeystore")
    String getKeystoreId();

    /**
     * Sets the keystore id.
     *
     * @param keystoreId The keystore id.
     * @since 1.5.0
     */
    void setKeystoreId(String keystoreId);

    /**
     * Gets the alias.
     *
     * @return The alias.
     * @since 1.5.0
     */
    @XmlElement(name = "alias")
    @Schema(example = "ssl-eclipse")
    String getAlias();

    /**
     * Sets the alias.
     *
     * @param alias The alias.
     * @since 1.5.0
     */
    void setAlias(String alias);

    /**
     * Gets the certificate.
     *
     * @return The certificate.
     * @since 1.5.0
     */
    @XmlElement(name = "certificate")
    @Schema(
        example = "-----BEGIN CERTIFICATE-----\\nMIIFVzCCBD+gAwIBAgISA38CzQctm3+HkSyZPnDL8TFsMA0GCSqGSIb3DQEBCwUA" +
                      "\\nMEoxCzAJBgNVBAYTAlVTMRYwFAYDVQQKEw1MZXQncyBFbmNyeXB0MSMwIQYDVQQD" +
                      "\\nExpMZXQncyBFbmNyeXB0IEF1dGhvcml0eSBYMzAeFw0xOTA3MTkxMDIxMTdaFw0x" +
                      "\\nOTEwMTcxMDIxMTdaMBsxGTAXBgNVBAMTEG1xdHQuZWNsaXBzZS5vcmcwggEiMA0G" +
                      "\\nCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDQnt6ZBEZ/vDG0JLqVB45lO6xlLazt" +
                      "\\nYpEqZlGBket6PtjUGLdE2XivTpjtUkERS1cvPBqT1DH/yEZ1CU7iT/gfZtZotR0c\\nqEMogSGkmrN1sAV6Eb" +
                      "+xGT3sPm1WFeKZqKdzAScdULoweUgwbNXa9kAB1uaSYBTe\\ncq2ynfxBKWL/7bVtoeXUOyyaiIxVPTYz5XgpjSUB+9ML" +
                      "/v/+084XhIKA/avGPOSi\\nRHOB+BsqTGyGhDgAHF+CDrRt8U1preS9AKXUvZ0aQL+djV8Y5nXPQPR8c2wplMwL\\n5W" +
                      "/YMrM/dBm64vclKQLVPyEPqMOLMqcf+LkfQi6WOH+JByJfywAlme6jAgMBAAGj" +
                      "\\nggJkMIICYDAOBgNVHQ8BAf8EBAMCBaAwHQYDVR0lBBYwFAYIKwYBBQUHAwEGCCsG\\nAQUFBwMCMAwGA1UdEwEB" +
                      "/wQCMAAwHQYDVR0OBBYEFHc+PmokFlx8Fh/0Lob125ef\\nfLNyMB8GA1UdIwQYMBaAFKhKamMEfd265tE5t6ZFZe" +
                      "/zqOyhMG8GCCsGAQUFBwEB\\nBGMwYTAuBggrBgEFBQcwAYYiaHR0cDovL29jc3AuaW50LXgzLmxldHNlbmNyeXB0" +
                      "\\nLm9yZzAvBggrBgEFBQcwAoYjaHR0cDovL2NlcnQuaW50LXgzLmxldHNlbmNyeXB0" +
                      "\\nLm9yZy8wGwYDVR0RBBQwEoIQbXF0dC5lY2xpcHNlLm9yZzBMBgNVHSAERTBDMAgG" +
                      "\\nBmeBDAECATA3BgsrBgEEAYLfEwEBATAoMCYGCCsGAQUFBwIBFhpodHRwOi8vY3Bz" +
                      "\\nLmxldHNlbmNyeXB0Lm9yZzCCAQMGCisGAQQB1nkCBAIEgfQEgfEA7wB2AHR+2oMx\\nrTMQkSGcziVPQnDCv" +
                      "/1eQiAIxjc1eeYQe8xWAAABbAn2/p8AAAQDAEcwRQIhAIBl" +
                      "\\nIZC2ZCMDs7bkBQN79xNO84VFpe7bQcMeaqHsQH9jAiAYV5kdZBgl17M5RB44NQ+y\\nY" +
                      "/WOF1PWOrNrP3XdeEo7HAB1ACk8UZZUyDlluqpQ/FgH1Ldvv1h6KXLcpMMM9OVF\\nR/R4AAABbAn2" +
                      "/o4AAAQDAEYwRAIgNYxfY0bjRfjhXjjAgyPRSLKq4O5tWTd2W4mn" +
                      "\\nCpE3aCYCIGeKPyuuo9tvHbyVKF4bsoN76FmnOkdsYE0MCKeKkUOkMA0GCSqGSIb3" +
                      "\\nDQEBCwUAA4IBAQCB0ykl1N2U2BMhzFo6dwrECBSFO+ePV2UYGrb+nFunWE4MMKBb" +
                      "\\ndyu7dj3cYRAFCM9A3y0H967IcY+h0u9FgZibmNs+y/959wcbr8F1kvgpVKDb1FGs\\ncuEArADQd3X+4TMM" +
                      "+IeIlqbGVXv3mYPrsP78LmUXkS7ufhMXsD5GSbSc2Zp4/v0o" +
                      "\\n3bsJz6qwzixhqg30tf6siOs9yrpHpPnDnbRrahbwnYTpm6JP0lK53GeFec4ckNi3\\nzT5" +
                      "+hEVOZ4JYPb3xVXkzIjSWmnDVbwC9MFtRaER9MhugKmiAp8SRLbylD0GKOhSB" +
                      "\\n2BDf6JrzhIddKxQ75KgMZE6FQaC3Bz1DFyrj\\n-----END CERTIFICATE-----\\n"
    )
    String getCertificate();

    /**
     * Sets the certificate.
     *
     * @param certificate The certificate.
     * @since 1.5.0
     */
    void setCertificate(String certificate);
}
