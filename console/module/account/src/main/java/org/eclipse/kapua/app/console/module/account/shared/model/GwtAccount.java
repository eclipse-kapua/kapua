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

import com.google.gwt.user.client.rpc.IsSerializable;
import org.eclipse.kapua.app.console.module.api.client.util.DateUtils;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtUpdatableEntityModel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class GwtAccount extends GwtUpdatableEntityModel implements Serializable {

    private static final long serialVersionUID = -5999185569672904770L;

    public enum GwtAccountStatus implements IsSerializable {
        ENABLED, DISABLED;

        GwtAccountStatus() {
        }
    }

    private GwtOrganization gwtOrganization;

    public GwtAccount() {
        super();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <X> X get(String property) {
        if ("expirationDateFormatted".equals(property)) {
            if (getExpirationDate() != null) {
                return (X) ((DateUtils.formatDateTime(getExpirationDate())));
            } else {
                return (X) null;
            }
        } else {
            return super.get(property);
        }
    }

    public String getName() {
        return (String) get("name");
    }

    public String getUnescapedName() {
        return (String) getUnescaped("name");
    }

    public void setName(String name) {
        set("name", name);
    }

    public String getPassword() {
        return (String) get("password");
    }

    public void setPassword(String password) {
        set("password", password);
    }

    public GwtOrganization getGwtOrganization() {
        return gwtOrganization;
    }

    public void setGwtOrganization(GwtOrganization gwtOrganization) {
        this.gwtOrganization = gwtOrganization;
    }

    public void setParentAccountId(String parentAccountId) {
        set("parentAccountId", parentAccountId);
    }

    public String getParentAccountId() {
        return (String) get("parentAccountId");
    }

    public String getParentAccountPath() {
        return get("parentAccountPath");
    }

    public void setParentAccountPath(String parentAccountPath) {
        set("parentAccountPath", parentAccountPath);
    }

    public List<GwtAccount> getChildAccounts() {
        return get("childAccounts");
    }

    public void setChildAccounts(List<GwtAccount> childAccounts) {
        set("childAccounts", childAccounts);
    }

    public Date getExpirationDate() {
        return get("expirationDate");
    }

    public String getExpirationDateFormatted() {
        return get("expirationDateFormatted");
    }

    public void setExpirationDate(Date expirationDate) {
        set("expirationDate", expirationDate);
    }

    public String getContactName() {
        return (String) getUnescaped("contactName");
    }

    public void setContactName(String contactName) {
        set("contactName", contactName);
    }

    public String getPhoneNumber() {
        return (String) getUnescaped("phoneNumber");
    }

    public void setPhoneNumber(String phoneNumber) {
        set("phoneNumber", phoneNumber);
    }

    public String getAddress1() {
        return (String) getUnescaped("address1");
    }

    public void setAddress1(String address1) {
        set("address1", address1);
    }

    public String getAddress2() {
        return (String) getUnescaped("address2");
    }

    public void setAddress2(String address2) {
        set("address2", address2);
    }

    public String getAddress3() {
        return (String) getUnescaped("address3");
    }

    public void setAddress3(String address3) {
        set("address3", address3);
    }

    public String getZipPostCode() {
        return (String) getUnescaped("zipPostCode");
    }

    public void setZipPostCode(String zipPostCode) {
        set("zipPostCode", zipPostCode);
    }

    public String getCity() {
        return (String) getUnescaped("city");
    }

    public void setCity(String city) {
        set("city", city);
    }

    public String getStateProvince() {
        return (String) getUnescaped("stateProvince");
    }

    public void setStateProvince(String stateProvince) {
        set("stateProvince", stateProvince);
    }

    public String getCountry() {
        return (String) getUnescaped("country");
    }

    public void setCountry(String country) {
        set("country", country);
    }

    public String getAccountStatus() {
        return get("gwtAccountStatus");
    }

    public void setAccountStatus(String gwtAccountStatus) {
        set("gwtAccountStatus", gwtAccountStatus);
    }

}
