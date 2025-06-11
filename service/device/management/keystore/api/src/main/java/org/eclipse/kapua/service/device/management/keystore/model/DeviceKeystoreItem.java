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

import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.xml.DateXmlAdapter;
import org.eclipse.kapua.service.device.registry.Device;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link DeviceKeystoreItem} definition.
 * <p>
 * Identifies an item of the {@link DeviceKeystore} for the {@link Device}.
 *
 * @since 1.5.0
 */
@XmlRootElement(name = "deviceKeystoreItem")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = DeviceKeystoreXmlRegistry.class, factoryMethod = "newDeviceKeystoreItem")
public interface DeviceKeystoreItem {

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
     * Gets the item type.
     * <p>
     * Examples:
     * <ul>
     *     <li>PRIVATE_KEY</li>
     *     <li>TRUSTED_CERTIFICATE</li>
     * </ul>
     *
     * @return The keystore item type.
     * @return The item type.
     * @since 1.5.0
     */
    @XmlElement(name = "itemType")
    @Schema(example = "TRUSTED_CERTIFICATE")
    String getItemType();

    /**
     * Sets the item type.
     *
     * @param itemType The item type.
     * @since 1.5.0
     */
    void setItemType(String itemType);

    /**
     * Gets the size in bytes.
     *
     * @return The size in bytes.
     * @since 1.5.0
     */
    @XmlElement(name = "size")
    @Schema(example = "2048")
    Integer getSize();

    /**
     * Sets the size in bytes.
     *
     * @param size The size in bytes.
     * @since 1.5.0
     */
    void setSize(Integer size);

    /**
     * Gets the algorithm.
     *
     * @return The algorithm.
     * @since 1.5.0
     */
    @XmlElement(name = "algorithm")
    @Schema(example = "RSA")
    String getAlgorithm();

    /**
     * Sets the algorithm.
     *
     * @param algorithm The algorithm.
     * @since 1.5.0
     */
    void setAlgorithm(String algorithm);

    /**
     * Gets the subject distinguished name.
     *
     * @return The subject distinguished name.
     * @since 1.5.0
     */
    @XmlElement(name = "subjectDN")
    @Schema(example = "mqtt.eclipse.org")
    String getSubjectDN();

    /**
     * Sets the subject distinguished name.
     *
     * @param subjectDN The subject distinguished name.
     * @since 1.5.0
     */
    void setSubjectDN(String subjectDN);

    /**
     * Gets the {@link List} of {@link DeviceKeystoreSubjectAN}s.
     *
     * @return The {@link List} of {@link DeviceKeystoreSubjectAN}s.
     * @since 1.5.0
     */
    @XmlElement(name = "subjectANs")
    List<DeviceKeystoreSubjectAN> getSubjectAN();

    /**
     * Adds a {@link DeviceKeystoreSubjectAN} to {@link #getSubjectAN()}.
     *
     * @param subjectAN The {@link DeviceKeystoreSubjectAN} to add.
     * @since 1.5.0
     */
    void addSubjectAN(DeviceKeystoreSubjectAN subjectAN);

    /**
     * Sets the {@link List} of {@link DeviceKeystoreSubjectAN}s.
     *
     * @param subjectAN The {@link List} of {@link DeviceKeystoreSubjectAN}s.
     * @since 1.5.0
     */
    void setSubjectAN(List<DeviceKeystoreSubjectAN> subjectAN);

    /**
     * Gets the issuer.
     *
     * @return The issuer.
     * @since 1.5.0
     */
    @XmlElement(name = "issuer")
    @Schema(example = "Let's Encrypt Authority X3,O = Let's Encrypt,C = US")
    String getIssuer();

    /**
     * Sets the issuer.
     *
     * @param issuer The issuer.
     * @since 1.5.0
     */
    void setIssuer(String issuer);

    /**
     * Gets the not before {@link Date}.
     *
     * @return The not before {@link Date}.
     * @since 1.5.0
     */
    @XmlElement(name = "notBefore")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getNotBefore();

    /**
     * Sets the not before {@link Date}.
     *
     * @param notBefore The not before {@link Date}.
     * @since 1.5.0
     */
    void setNotBefore(Date notBefore);

    /**
     * Gets the not after {@link Date}.
     *
     * @return The not after {@link Date}.
     * @since 1.5.0
     */
    @XmlElement(name = "notAfter")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getNotAfter();

    /**
     * Sets the not after {@link Date}.
     *
     * @param notAfter The not after {@link Date}.
     * @since 1.5.0
     */
    void setNotAfter(Date notAfter);

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

    /**
     * Gets the certificate chain.
     *
     * @return The certificate chain.
     * @since 1.5.0
     */
    @XmlElement(name = "certificateChain")
    List<String> getCertificateChain();

    /**
     * Adds a certificate to the {@link #getCertificateChain()}.
     *
     * @param certificate The certificate to add.
     * @since 1.5.0
     */
    void addCertificateChain(String certificate);

    /**
     * Sets the certificate chain.
     *
     * @param certificateChain The certificate chain.
     * @since 1.5.0
     */
    void setCertificateChain(List<String> certificateChain);

}
