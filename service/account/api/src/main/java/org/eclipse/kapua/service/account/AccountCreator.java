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
package org.eclipse.kapua.service.account;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.KapuaNamedEntityCreator;
import org.eclipse.kapua.model.xml.DateXmlAdapter;
import org.eclipse.kapua.service.account.xml.AccountXmlRegistry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

/**
 * {@link Account} {@link KapuaEntityCreator} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "accountCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder =
        {
                "organizationName",
                "organizationPersonName",
                "organizationEmail",
                "organizationPhoneNumber",
                "organizationAddressLine1",
                "organizationAddressLine2",
                "organizationAddressLine3",
                "organizationCity",
                "organizationZipPostCode",
                "organizationStateProvinceCounty",
                "organizationCountry",
                "expirationDate",
                "status"
        },
        factoryClass = AccountXmlRegistry.class, factoryMethod = "newAccountCreator")
public interface AccountCreator extends KapuaNamedEntityCreator<Account> {

    /**
     * Gets the {@link Organization#getName()}.
     *
     * @return The {@link Organization#getName()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationName")
    String getOrganizationName();

    /**
     * Sets the {@link Organization#getName()}.
     *
     * @param organizationName The {@link Organization#getName()}.
     * @since 1.0.0
     */
    void setOrganizationName(String organizationName);

    /**
     * Gets the {@link Organization#getPersonName()}.
     *
     * @return The {@link Organization#getPersonName()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationPersonName")
    String getOrganizationPersonName();

    /**
     * Sets the {@link Organization#getPersonName()}.
     *
     * @param organizationPersonName The {@link Organization#getPersonName()}.
     * @since 1.0.0
     */
    void setOrganizationPersonName(String organizationPersonName);

    /**
     * Gets the {@link Organization#getEmail()}.
     *
     * @return The {@link Organization#getEmail()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationEmail")
    String getOrganizationEmail();

    /**
     * Sets the {@link Organization#getEmail()}.
     *
     * @param organizationEmail The {@link Organization#getEmail()}.
     * @since 1.0.0
     */
    void setOrganizationEmail(String organizationEmail);

    /**
     * Gets the {@link Organization#getPhoneNumber()}.
     *
     * @return The {@link Organization#getPhoneNumber()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationPhoneNumber")
    String getOrganizationPhoneNumber();

    /**
     * Sets the {@link Organization#getPhoneNumber()}.
     *
     * @param organizationPhoneNumber The {@link Organization#getPhoneNumber()}.
     * @since 1.0.0
     */
    void setOrganizationPhoneNumber(String organizationPhoneNumber);

    /**
     * Gets the {@link Organization#getAddressLine1()}.
     *
     * @return The {@link Organization#getAddressLine1()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationAddressLine1")
    String getOrganizationAddressLine1();

    /**
     * Sets the {@link Organization#getAddressLine1()}.
     *
     * @param organizationAddressLine1 The {@link Organization#getAddressLine1()}.
     * @since 1.0.0
     */
    void setOrganizationAddressLine1(String organizationAddressLine1);

    /**
     * Gets the {@link Organization#getAddressLine2()}.
     *
     * @return The {@link Organization#getAddressLine2()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationAddressLine2")
    String getOrganizationAddressLine2();

    /**
     * Sets the {@link Organization#getAddressLine2()}.
     *
     * @param organizationAddressLine2 The {@link Organization#getAddressLine2()}.
     * @since 1.0.0
     */
    void setOrganizationAddressLine2(String organizationAddressLine2);

    /*3
     * Gets the {@link Organization#getAddressLine3()}.
     *
     * @return The {@link Organization#getAddressLine3()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationAddressLine3")
    String getOrganizationAddressLine3();

    /**
     * Sets the {@link Organization#getAddressLine3()}.
     *
     * @param organizationAddressLine3 The {@link Organization#getAddressLine3()}.
     * @since 1.0.0
     */
    void setOrganizationAddressLine3(String organizationAddressLine3);

    /**
     * Gets the {@link Organization#getCity()}.
     *
     * @return The {@link Organization#getCity()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationCity")
    String getOrganizationCity();

    /**
     * Sets the {@link Organization#getCity()}.
     *
     * @param organizationCity The {@link Organization#getCity()}.
     * @since 1.0.0
     */
    void setOrganizationCity(String organizationCity);

    /**
     * Gets the {@link Organization#getZipPostCode()}.
     *
     * @return The {@link Organization#getZipPostCode()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationZipPostCode")
    String getOrganizationZipPostCode();

    /**
     * Sets the {@link Organization#getZipPostCode()}.
     *
     * @param organizationZipPostCode The {@link Organization#getZipPostCode()}.
     * @since 1.0.0
     */
    void setOrganizationZipPostCode(String organizationZipPostCode);

    /**
     * Gets the {@link Organization#getStateProvinceCounty()}.
     *
     * @return The {@link Organization#getStateProvinceCounty()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationStateProvinceCounty")
    String getOrganizationStateProvinceCounty();

    /**
     * Sets the {@link Organization#getStateProvinceCounty()}.
     *
     * @param organizationStateProvinceCounty The {@link Organization#getStateProvinceCounty()}.
     * @since 1.0.0
     */
    void setOrganizationStateProvinceCounty(String organizationStateProvinceCounty);

    /**
     * Gets the {@link Organization#getCountry()}.
     *
     * @return The {@link Organization#getCountry()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationCountry")
    String getOrganizationCountry();

    /**
     * Sets the {@link Organization#getCountry()}.
     *
     * @param organizationCountry The {@link Organization#getCountry()}.
     * @since 1.0.0
     */
    void setOrganizationCountry(String organizationCountry);

    /**
     * Gets the expiration date.
     *
     * @return The expiration date.
     * @since 1.0.0
     */
    @XmlElement(name = "expirationDate")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getExpirationDate();

    /**
     * Sets the expiration date.
     *
     * @param expirationDate The expiration date.
     * @since 1.0.0
     */
    void setExpirationDate(Date expirationDate);

    /**
     * Gets the {@link AccountStatus}.
     *
     * @return The {@link AccountStatus}.
     * @since 2.0.0
     */
    @XmlElement(name = "status")
    AccountStatus getStatus();

    /**
     * Sets the {@link AccountStatus}.
     *
     * @param status The {@link AccountStatus}.
     * @since 2.0.0
     */
    void setStatus(AccountStatus status);
}
