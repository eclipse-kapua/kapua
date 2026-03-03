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

import org.eclipse.kapua.app.console.module.api.shared.model.GwtEntityCreator;

public class GwtAccountCreator extends GwtEntityCreator {

    private static final long serialVersionUID = 3644046497789586781L;

    private String accountName;
    private String accountPassword;
    private String parentAccountId;
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
    private String accountStatus;

    public GwtAccountCreator() {
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountPassword() {
        return accountPassword;
    }

    public void setAccountPassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationPersonName() {
        return organizationPersonName;
    }

    public void setOrganizationPersonName(String organizationPersonName) {
        this.organizationPersonName = organizationPersonName;
    }

    public String getOrganizationEmail() {
        return organizationEmail;
    }

    public void setOrganizationEmail(String organizationEmail) {
        this.organizationEmail = organizationEmail;
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

    public String getOrganizationCity() {
        return organizationCity;
    }

    public void setOrganizationCity(String organizationCity) {
        this.organizationCity = organizationCity;
    }

    public String getOrganizationZipPostCode() {
        return organizationZipPostCode;
    }

    public void setOrganizationZipPostCode(String organizationZipPostCode) {
        this.organizationZipPostCode = organizationZipPostCode;
    }

    public String getOrganizationStateProvinceCounty() {
        return organizationStateProvinceCounty;
    }

    public void setOrganizationStateProvinceCounty(String organizationStateProvinceCounty) {
        this.organizationStateProvinceCounty = organizationStateProvinceCounty;
    }

    public String getOrganizationCountry() {
        return organizationCountry;
    }

    public void setOrganizationCountry(String organizationCountry) {
        this.organizationCountry = organizationCountry;
    }

    public String getParentAccountId() {
        return parentAccountId;
    }

    public void setParentAccountId(String parentAccountId) {
        this.parentAccountId = parentAccountId;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}
