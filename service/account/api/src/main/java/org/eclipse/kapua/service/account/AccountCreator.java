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

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.KapuaNamedEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.xml.DateXmlAdapter;

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
                "expirationDate"
        })
public class AccountCreator extends KapuaNamedEntityCreator {

    private static final long serialVersionUID = -2460883485294616032L;

    private String organizationName;
    private String organizationPersonName;
    private String organizationEmail;
    private String organizationPhoneNumber;
    private String organizationAddressLine1;
    private String organizationAddressLine2;
    private String organizationAddressLine3;
    private String organizationCity;
    private String organizationZipPostCode;
    private String organizationStateProvinceCounty;
    private String organizationCountry;

    private Date expirationDate;

    public AccountCreator() {
    }

    public AccountCreator(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * Constructor.
     *
     * @param scopeId
     *         The {@link AccountCreator#getScopeId()}.
     * @param name
     *         The {@link AccountCreator#getName()}.
     * @since 1.0.0
     */
    public AccountCreator(KapuaId scopeId, String name) {
        super(scopeId, name);
    }

    /**
     * Gets the {@link Organization#getName()}.
     *
     * @return The {@link Organization#getName()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationName")
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * Sets the {@link Organization#getName()}.
     *
     * @param organizationName
     *         The {@link Organization#getName()}.
     * @since 1.0.0
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * Gets the {@link Organization#getPersonName()}.
     *
     * @return The {@link Organization#getPersonName()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationPersonName")
    public String getOrganizationPersonName() {
        return organizationPersonName;
    }

    /**
     * Sets the {@link Organization#getPersonName()}.
     *
     * @param organizationPersonName
     *         The {@link Organization#getPersonName()}.
     * @since 1.0.0
     */
    public void setOrganizationPersonName(String organizationPersonName) {
        this.organizationPersonName = organizationPersonName;
    }

    /**
     * Gets the {@link Organization#getEmail()}.
     *
     * @return The {@link Organization#getEmail()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationEmail")
    public String getOrganizationEmail() {
        return organizationEmail;
    }

    /**
     * Sets the {@link Organization#getEmail()}.
     *
     * @param organizationEmail
     *         The {@link Organization#getEmail()}.
     * @since 1.0.0
     */
    public void setOrganizationEmail(String organizationEmail) {
        this.organizationEmail = organizationEmail;
    }

    /**
     * Gets the {@link Organization#getPhoneNumber()}.
     *
     * @return The {@link Organization#getPhoneNumber()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationPhoneNumber")
    public String getOrganizationPhoneNumber() {
        return organizationPhoneNumber;
    }

    /**
     * Sets the {@link Organization#getPhoneNumber()}.
     *
     * @param organizationPhoneNumber
     *         The {@link Organization#getPhoneNumber()}.
     * @since 1.0.0
     */
    public void setOrganizationPhoneNumber(String organizationPhoneNumber) {
        this.organizationPhoneNumber = organizationPhoneNumber;
    }

    /**
     * Gets the {@link Organization#getAddressLine1()}.
     *
     * @return The {@link Organization#getAddressLine1()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationAddressLine1")
    public String getOrganizationAddressLine1() {
        return organizationAddressLine1;
    }

    /**
     * Sets the {@link Organization#getAddressLine1()}.
     *
     * @param organizationAddressLine1
     *         The {@link Organization#getAddressLine1()}.
     * @since 1.0.0
     */
    public void setOrganizationAddressLine1(String organizationAddressLine1) {
        this.organizationAddressLine1 = organizationAddressLine1;
    }

    /**
     * Gets the {@link Organization#getAddressLine2()}.
     *
     * @return The {@link Organization#getAddressLine2()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationAddressLine2")
    public String getOrganizationAddressLine2() {
        return organizationAddressLine2;
    }

    /**
     * Sets the {@link Organization#getAddressLine2()}.
     *
     * @param organizationAddressLine2
     *         The {@link Organization#getAddressLine2()}.
     * @since 1.0.0
     */
    public void setOrganizationAddressLine2(String organizationAddressLine2) {
        this.organizationAddressLine2 = organizationAddressLine2;
    }

    /**
     * Gets the {@link Organization#getAddressLine3()}.
     *
     * @return The {@link Organization#getAddressLine3()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationAddressLine3")
    public String getOrganizationAddressLine3() {
        return organizationAddressLine3;
    }

    /**
     * Sets the {@link Organization#getAddressLine3()}.
     *
     * @param organizationAddressLine3
     *         The {@link Organization#getAddressLine3()}.
     * @since 1.0.0
     */
    public void setOrganizationAddressLine3(String organizationAddressLine3) {
        this.organizationAddressLine3 = organizationAddressLine3;
    }

    /**
     * Gets the {@link Organization#getCity()}.
     *
     * @return The {@link Organization#getCity()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationCity")
    public String getOrganizationCity() {
        return organizationCity;
    }

    /**
     * Sets the {@link Organization#getCity()}.
     *
     * @param organizationCity
     *         The {@link Organization#getCity()}.
     * @since 1.0.0
     */
    public void setOrganizationCity(String organizationCity) {
        this.organizationCity = organizationCity;
    }

    /**
     * Gets the {@link Organization#getZipPostCode()}.
     *
     * @return The {@link Organization#getZipPostCode()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationZipPostCode")
    public String getOrganizationZipPostCode() {
        return organizationZipPostCode;
    }

    /**
     * Sets the {@link Organization#getZipPostCode()}.
     *
     * @param organizationZipPostCode
     *         The {@link Organization#getZipPostCode()}.
     * @since 1.0.0
     */
    public void setOrganizationZipPostCode(String organizationZipPostCode) {
        this.organizationZipPostCode = organizationZipPostCode;
    }

    /**
     * Gets the {@link Organization#getStateProvinceCounty()}.
     *
     * @return The {@link Organization#getStateProvinceCounty()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationStateProvinceCounty")
    public String getOrganizationStateProvinceCounty() {
        return this.organizationStateProvinceCounty;
    }

    /**
     * Sets the {@link Organization#getStateProvinceCounty()}.
     *
     * @param organizationStateProvinceCounty
     *         The {@link Organization#getStateProvinceCounty()}.
     * @since 1.0.0
     */
    public void setOrganizationStateProvinceCounty(String organizationStateProvinceCounty) {
        this.organizationStateProvinceCounty = organizationStateProvinceCounty;
    }

    /**
     * Gets the {@link Organization#getCountry()}.
     *
     * @return The {@link Organization#getCountry()}.
     * @since 1.0.0
     */
    @XmlElement(name = "organizationCountry")
    public String getOrganizationCountry() {
        return organizationCountry;
    }

    /**
     * Sets the {@link Organization#getCountry()}.
     *
     * @param organizationCountry
     *         The {@link Organization#getCountry()}.
     * @since 1.0.0
     */
    public void setOrganizationCountry(String organizationCountry) {
        this.organizationCountry = organizationCountry;
    }

    /**
     * Gets the expiration date.
     *
     * @return The expiration date.
     * @since 1.0.0
     */
    @XmlElement(name = "expirationDate")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the expiration date.
     *
     * @param expirationDate
     *         The expiration date.
     * @since 1.0.0
     */
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
