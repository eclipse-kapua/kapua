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
package org.eclipse.kapua.app.console.module.account.shared.util;

import org.eclipse.kapua.app.console.module.account.shared.model.GwtAccount;
import org.eclipse.kapua.app.console.module.account.shared.model.GwtOrganization;
import org.eclipse.kapua.app.console.module.api.shared.util.KapuaGwtCommonsModelConverter;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountStatus;
import org.eclipse.kapua.service.account.Organization;

import java.util.ArrayList;
import java.util.List;

public class KapuaGwtAccountModelConverter {

    private KapuaGwtAccountModelConverter() {
    }

    /**
     * Converts a {@link Account} into a {@link GwtAccount} for GWT usage.
     *
     * @param account The {@link Account} to convertKapuaId.
     * @return The converted {@link GwtAccount}
     * @since 1.0.0
     */
    public static GwtAccount convertAccount(Account account) {
        GwtAccount gwtAccount = new GwtAccount();
        // Convert commons attributes
        KapuaGwtCommonsModelConverter.convertUpdatableEntity(account, gwtAccount);
        // Convert other attributes
        gwtAccount.setName(account.getName());
        gwtAccount.setGwtOrganization(convertOrganization(account.getOrganization()));
        gwtAccount.setParentAccountId(account.getScopeId() != null ? account.getScopeId().toCompactId() : null);
        gwtAccount.setParentAccountPath(account.getParentAccountPath());
        gwtAccount.setOptlock(account.getOptlock());
        gwtAccount.set("orgName", account.getOrganization().getName());
        gwtAccount.set("orgEmail", account.getOrganization().getEmail());
        gwtAccount.setChildAccounts(convertChildAccounts(account.getChildAccounts()));
        gwtAccount.setExpirationDate(account.getExpirationDate());
        gwtAccount.setContactName(account.getOrganization().getPersonName());
        gwtAccount.setPhoneNumber(account.getOrganization().getPhoneNumber());
        gwtAccount.setAddress1(account.getOrganization().getAddressLine1());
        gwtAccount.setAddress2(account.getOrganization().getAddressLine2());
        gwtAccount.setAddress3(account.getOrganization().getAddressLine3());
        gwtAccount.setZipPostCode(account.getOrganization().getZipPostCode());
        gwtAccount.setCity(account.getOrganization().getCity());
        gwtAccount.setStateProvince(account.getOrganization().getStateProvinceCounty());
        gwtAccount.setCountry(account.getOrganization().getCountry());
        gwtAccount.setAccountStatus(account.getStatus() == null ? AccountStatus.ENABLED.name() : account.getStatus().name()); //TODO: you should perform recursive call
        // Return converted entity
        return gwtAccount;
    }

    private static List<GwtAccount> convertChildAccounts(List<Account> childAccounts) {
        List<GwtAccount> accountList = new ArrayList<GwtAccount>();
        for (Account account : childAccounts) {
            accountList.add(KapuaGwtAccountModelConverter.convertAccount(account));
        }
        return accountList;
    }

    /**
     * Converts a {@link Organization} into a {@link GwtOrganization} for GWT usage.
     *
     * @param organization The {@link Organization} to convertKapuaId.
     * @return The converted {@link GwtOrganization}.
     * @since 1.0.0
     */
    public static GwtOrganization convertOrganization(Organization organization) {
        GwtOrganization gwtOrganization = new GwtOrganization();

        gwtOrganization.setName(organization.getName());
        gwtOrganization.setPersonName(organization.getPersonName());
        gwtOrganization.setEmailAddress(organization.getEmail());
        gwtOrganization.setPhoneNumber(organization.getPhoneNumber());
        gwtOrganization.setAddressLine1(organization.getAddressLine1());
        gwtOrganization.setAddressLine2(organization.getAddressLine2());
        gwtOrganization.setAddressLine3(organization.getAddressLine3());
        gwtOrganization.setZipPostCode(organization.getZipPostCode());
        gwtOrganization.setCity(organization.getCity());
        gwtOrganization.setStateProvinceCounty(organization.getStateProvinceCounty());
        gwtOrganization.setCountry(organization.getCountry());
        // Return converted entity
        return gwtOrganization;
    }
}
