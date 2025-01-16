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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.service.account.xml.AccountXmlRegistry;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link Organization} definition.
 *
 * @since 1.0.0
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = AccountXmlRegistry.class, factoryMethod = "newOrganization")
@Schema(description = "An object with all the information needed to create a new Account")
public interface Organization {

    /**
     * Gets the name.
     *
     * @return The name.
     * @since 1.0.0
     */
    @Schema(
        description = "The Name of the Organization",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "ACME Inc."
    )
    String getName();

    /**
     * Sets the name
     *
     * @param name The name.
     * @since 1.0.0
     */
    void setName(String name);

    /**
     * Gets the referent name.
     *
     * @return The referent name.
     * @since 1.0.0
     */
    @Schema(
        description = "The Name of the Person listed as a Contact for the Organization",
        example = "Wile Ethelbert Coyote"
    )
    String getPersonName();

    /**
     * Sets the referent name.
     *
     * @param personName The referent name
     * @since 1.0.0
     */
    void setPersonName(String personName);

    /**
     * Gets the email.
     *
     * @return The email.
     * @since 1.0.0
     */
    @Schema(
        description = "The Email Address of the Person listed as a Contact for the Organization",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "wile.coyote@acme.inc"
    )
    String getEmail();

    /**
     * Sets the email.
     *
     * @param email The email.
     * @since 1.0.0
     */
    void setEmail(String email);

    /**
     * Gets the phone number.
     *
     * @return The phone number.
     * @since 1.0.0
     */
    @Schema(
        description = "The Phone Number of the Person listed as a Contact for the Organization",
        example = "+1 (555) 123 4567"
    )
    String getPhoneNumber();

    /**
     * Sets the phone number.
     *
     * @param phoneNumber The phone number.
     * @since 1.0.0
     */
    void setPhoneNumber(String phoneNumber);

    /**
     * Gets the address first line.
     *
     * @return The address first line.
     * @since 1.0.0
     */
    @Schema(
        description = "First line of the Address for the Organization",
        example = "wile.coyote@acme.inc"
    )
    String getAddressLine1();

    /**
     * Sets the address first line.
     *
     * @param addressLine1 The address first line.
     * @since 1.0.0
     */
    void setAddressLine1(String addressLine1);

    /**
     * Gets the address second line.
     *
     * @return The address second line.
     * @since 1.0.0
     */
    @Schema(
        description = "Second line of the Address for the Organization",
        example = "123 Looney Tunes Drive"
    )
    String getAddressLine2();

    /**
     * Sets the address second line.
     *
     * @param addressLine2 The address second line.
     * @since 1.0.0
     */
    void setAddressLine2(String addressLine2);


    /**
     * Gets the address third line.
     *
     * @return The address third line.
     * @since 1.1.0
     */
    @Schema(
        description = "Third line of the Address for the Organization",
        example = "Block 1"
    )
    String getAddressLine3();

    /**
     * Sets the address third line.
     *
     * @param addressLine3 The address third line.
     * @since 1.1.0
     */
    void setAddressLine3(String addressLine3);

    /**
     * Gets the postal ZIP code.
     *
     * @return The postal ZIP code.
     * @since 1.0.0
     */
    @Schema(
        description = "The Zip / Postcode for the Organization",
        example = "00100"
    )
    String getZipPostCode();

    /**
     * Sets the postal ZIP code.
     *
     * @param zipPostalCode The postal ZIP code.
     * @since 1.0.0
     */
    void setZipPostCode(String zipPostalCode);

    /**
     * Gets the city.
     *
     * @return The city.
     * @since 1.0.0
     */
    @Schema(
        description = "The City of the Organization",
        example = "Apartment 25"
    )
    String getCity();

    /**
     * Sets the city.
     *
     * @param city The city.
     * @since 1.0.0
     */
    void setCity(String city);

    /**
     * Gets the province or state (if it is a federal state) within a country.
     *
     * @return The province or state (if it is a federal state) within a country.
     * @since 1.0.0
     */
    @Schema(
        description = "The State / Province / County of the Organization",
        example = "Green County, GC"
    )
    String getStateProvinceCounty();

    /**
     * Sets the province or state (if it is a federal state) within a country.
     *
     * @param stateProvinceCounty The province or state (if it is a federal state) within a country.
     * @since 1.0.0
     */
    void setStateProvinceCounty(String stateProvinceCounty);

    /**
     * Gets the country.
     *
     * @return The country.
     * @since 1.0.0
     */
    @Schema(
        description = "The Country of the Organization",
        example = "United States"
    )
    String getCountry();

    /**
     * Sets the country.
     *
     * @param country The country.
     * @since 1.0.0
     */
    void setCountry(String country);
}
