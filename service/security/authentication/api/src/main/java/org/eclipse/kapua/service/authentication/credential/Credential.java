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
package org.eclipse.kapua.service.authentication.credential;

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

/**
 * Credential {@link KapuaUpdatableEntity} definition
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "credential")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = CredentialXmlRegistry.class, factoryMethod = "newCredential")
public interface Credential extends KapuaUpdatableEntity {

    String TYPE = "credential";

    @Override
    default String getType() {
        return TYPE;
    }

    /**
     * Gets the User#getId owner of the {@link Credential}
     *
     * @return The User#getId
     * @since 1.0.0
     */
    @XmlElement(name = "userId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    KapuaId getUserId();

    /**
     * Sets the User#getId owner of the {@link Credential}
     *
     * @param userId The User#getId
     * @since 1.0.0
     */
    void setUserId(KapuaId userId);

    /**
     * Gets the type.
     *
     * @return The type.
     * @since 1.0.0
     */
    @XmlElement(name = "credentialType")
    String getCredentialType();

    /**
     * Sets the type.
     *
     * @param credentialType The type.
     * @since 1.0.0
     */
    void setCredentialType(String credentialType);

    /**
     * Gets the secret key
     *
     * @return The secret key
     * @since 1.0.0
     */
    @XmlElement(name = "credentialKey")
    String getCredentialKey();

    /**
     * Sets the secret key
     *
     * @param credentialKey The secret key
     * @since 1.0.0
     */
    void setCredentialKey(String credentialKey);

    /**
     * Gets the {@link CredentialStatus}
     *
     * @return The {@link CredentialStatus}
     * @since 1.0.0
     */
    @XmlElement(name = "status")
    CredentialStatus getStatus();

    /**
     * Sets the {@link CredentialStatus}
     *
     * @param status The {@link CredentialStatus}
     * @since 1.0.0
     */
    void setStatus(CredentialStatus status);

    /**
     * Gets the expiration date
     *
     * @return The expiration date
     * @since 1.0.0
     */
    @XmlElement(name = "expirationDate")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getExpirationDate();

    /**
     * Sets the expiration date
     *
     * @param expirationDate The expiration date
     * @since 1.0.0
     */
    void setExpirationDate(Date expirationDate);

    /**
     * Gets how many times this credential failed a login
     *
     * @return How many times this credential failed a login
     * @since 1.0.0
     */
    int getLoginFailures();

    /**
     * Sets how many times this credential failed a login
     *
     * @param loginFailures How many times this credential failed a login
     * @since 1.0.0
     */
    void setLoginFailures(int loginFailures);

    /**
     * Gets when the first login attempt failed
     *
     * @return When first login attempt failed
     * @since 1.0.0
     */
    @XmlElement(name = "firstLoginFailure")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getFirstLoginFailure();

    /**
     * Sets when first login attempt failed
     *
     * @param firstLoginFailure When first login attempt failed.
     * @since 1.0.0
     */
    void setFirstLoginFailure(Date firstLoginFailure);

    /**
     * Gets when the failed login attempts will reset
     *
     * @return When the failed login attempts will reset
     * @since 1.0.0
     */
    @XmlElement(name = "loginFailuresReset")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getLoginFailuresReset();

    /**
     * Sets when the failed login attempts will reset
     *
     * @param loginFailuresReset When the failed login attempts will reset
     * @since 1.0.0
     */
    void setLoginFailuresReset(Date loginFailuresReset);

    /**
     * Gets when the lockout will be reset
     *
     * @return When the lockout will be reset
     * @since 1.0.0
     */
    @XmlElement(name = "lockoutReset")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getLockoutReset();

    /**
     * Sets when the lockout will be reset
     *
     * @param lockoutReset When the lockout will be reset
     * @since 1.0.0
     */
    void setLockoutReset(Date lockoutReset);
}
