/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authentication.credential.mfa;

import org.eclipse.kapua.model.KapuaUpdatableEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.model.xml.DateXmlAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;

/**
 * MfaOption {@link KapuaUpdatableEntity} definition.
 * <p>
 * Used to handle {@link MfaOption} needed by the various authentication algorithms.
 *
 * @since 1.3.0
 */
@XmlRootElement(name = "mfaOption")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = MfaOptionXmlRegistry.class, factoryMethod = "newMfaOption")
public interface MfaOption extends KapuaUpdatableEntity {

    String TYPE = "mfaOption";

    @Override
    default String getType() {
        return TYPE;
    }

    /**
     * Gets the User#getId.
     *
     * @return The User#getId.
     * @since 1.3.0
     */
    @XmlElement(name = "userId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    KapuaId getUserId();

    /**
     * Sets the User#getId.
     *
     * @param userId The User#getId.
     * @since 1.3.0
     */
    void setUserId(KapuaId userId);

    /**
     * Gets the secret key that generates access codes
     *
     * @return The secret key that generates access codes
     * @since 1.3.0
     */
    @XmlElement(name = "mfaSecretKey")
    String getMfaSecretKey();

    /**
     * Sets the secret key that generates access codes
     *
     * @param mfaSecretKey The secret key that generates access codes
     * @since 1.3.0
     */
    void setMfaSecretKey(String mfaSecretKey);

    /**
     * Gets the trust key for trusted machines
     *
     * @return The trust key for trusted machines
     * @since 1.3.0
     */
    @XmlElement(name = "trustKey")
    String getTrustKey();

    /**
     * Sets the trust key for trusted machines
     *
     * @param trustKey The trust key for trusted machines
     * @since 1.3.0
     */
    void setTrustKey(String trustKey);

    /**
     * Gets whether the {@link #getTrustKey()} is present or not
     *
     * @return {@code true} if it is present, {@code false} otherwise
     * @since 2.1.0
     */
    @XmlElement(name = "hasTrustMe")
    boolean getHasTrustMe();

    /**
     * Gets the {@link #getTrustKey()}  expiration date
     *
     * @return The {@link #getTrustKey()}  expiration date
     * @since 1.3.0
     */
    @XmlElement(name = "trustExpirationDate")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getTrustExpirationDate();

    /**
     * Sets the {@link #getTrustKey()}  expiration date
     *
     * @param trustExpirationDate The {@link #getTrustKey()}  expiration date
     * @since 1.3.0
     */
    void setTrustExpirationDate(Date trustExpirationDate);

    /**
     * Gets the {@link #getMfaSecretKey()} in the form of a base64 QR code image
     *
     * @return The {@link #getMfaSecretKey()} in the form of a base64 QR code image
     * @since 1.3.0
     */
    @XmlElement(name = "qrCodeImage")
    String getQRCodeImage();

    /**
     * Sets the {@link #getMfaSecretKey()} in the form of a base64 QR code image
     *
     * @param qrCodeImage The {@link #getMfaSecretKey()} in the form of a base64 QR code image
     * @since 1.3.0
     */
    void setQRCodeImage(String qrCodeImage);

    /**
     * Gets the list of {@link ScratchCode}s
     *
     * @return The list of {@link ScratchCode}s
     * @since 1.3.0
     */
    @XmlElement(name = "scratchCodes")
    List<ScratchCode> getScratchCodes();

    /**
     * Sets the list of {@link ScratchCode}s
     *
     * @param scratchCodes The list of {@link ScratchCode}s
     * @since 1.3.0
     */
    void setScratchCodes(List<ScratchCode> scratchCodes);
}
