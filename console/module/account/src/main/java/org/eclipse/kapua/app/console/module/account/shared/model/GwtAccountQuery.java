/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.account.shared.model;

import java.util.Date;

import org.eclipse.kapua.app.console.module.api.shared.model.query.GwtQuery;

public class GwtAccountQuery extends GwtQuery {

    /**
     *
     */
    private static final long serialVersionUID = -2313840713047445711L;

    private String name;
    private String organizationName;
    private String organizationEmail;
    private Date expirationDate;
    private String organizationContactName;
    private String organizationPhoneNumber;
    private String organizationAddressLine1;
    private String organizationAddressLine2;
    private String organizationAddressLine3;
    private String organizationZipPostCode;
    private String organizationCity;
    private String organizationStateProvinceCountry;
    private String organizationCountry;
    private String status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationEmail() {
        return organizationEmail;
    }

    public void setOrganizationEmail(String organizationEmail) {
        this.organizationEmail = organizationEmail;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getOrganizationContactName() {
        return organizationContactName;
    }

    public void setOrganizationContactName(String organizationContactName) {
        this.organizationContactName = organizationContactName;
    }

    public String getOrganizationPhoneNumber() {
        return organizationPhoneNumber;
    }

    public void setOrganizationPhoneNumber(String organizationPhoneNumber) {
       this.organizationPhoneNumber = organizationPhoneNumber;
    }

    public String getOrganizationAddressLine1() {
        return organizationAddressLine1;
    }

    public void setOrganizationAddressLine1(String organizationAddressLine1) {
        this.organizationAddressLine1 = organizationAddressLine1;
    }

    public String getOrganizationAddressLine2() {
        return organizationAddressLine2;
    }

    public void setOrganizationAddressLine2(String organizationAddressLine2) {
        this.organizationAddressLine2 = organizationAddressLine2;
    }

    public String getOrganizationAddressLine3() {
        return organizationAddressLine3;
    }

    public void setOrganizationAddressLine3(String organizationAddressLine3) {
        this.organizationAddressLine3 = organizationAddressLine3;
    }

    public String getOrganizationZipPostCode() {
        return organizationZipPostCode;
    }

    public void setOrganizationZipPostCode(String organizationZipPostCode) {
        this.organizationZipPostCode = organizationZipPostCode;
    }

    public String getOrganizationCity() {
        return organizationCity;
    }

    public void setOrganizationCity(String organizationCity) {
        this.organizationCity = organizationCity;
    }

    public String getOrganizationStateProvinceCountry() {
        return organizationStateProvinceCountry;
    }

    public void setOrganizationStateProvinceCountry(String organizationStateProvinceCountry) {
        this.organizationStateProvinceCountry = organizationStateProvinceCountry;
    }

    public String getOrganizationCountry() {
        return organizationCountry;
    }

    public void setOrganizationCountry(String organizationCountry) {
        this.organizationCountry = organizationCountry;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
