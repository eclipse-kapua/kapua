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
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.KapuaNamedEntity;
import org.eclipse.kapua.model.xml.DateXmlAdapter;
import org.eclipse.kapua.service.account.xml.AccountParentPathXmlAdapter;
import org.eclipse.kapua.service.account.xml.AccountXmlRegistry;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link Account} {@link KapuaEntity} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "account")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = AccountXmlRegistry.class, factoryMethod = "newAccount")
@Schema(description = "An object that holds all the information pertaining an Account, including the details about the Contacts of that account." +
                          "In Kapua an Account is the container of all other resources (Users, Devices, etc.), and is the equivalent of the concept of Tenant." +
                          "Every entity in Kapua will have a `scopeId` property, that holds the ID of the Account who holds that entity.")
public interface Account extends KapuaNamedEntity {

    String TYPE = "account";

    @Override
    @Schema(example = "account")
    default String getType() {
        return TYPE;
    }

    /**
     * Gets the {@link Organization}.
     *
     * @return The {@link Organization}.
     * @since 1.0.0
     */
    @XmlElement(name = "organization")
    @Schema(description = "An object with all the information needed to create a new Account")
    Organization getOrganization();

    /**
     * Sets the {@link Organization}.
     *
     * @param organization The account's {@link Organization}.
     * @since 1.0.0
     */
    void setOrganization(Organization organization);

    /**
     * Returns the parent account path.
     * <p>
     * The account path is a '/' separated list of the parents {@link Account} identifiers in reverse order (so it should be read from right to left).
     * <p>
     * e.g. The parent account path 7/14/15 means that the current account has 15 as parent, then 15 has 14 as parent and 14 has 7 as parent.
     *
     * @return The parent account path
     * @since 1.0.0
     */
    @XmlElement(name = "parentAccountPath")
    @XmlJavaTypeAdapter(AccountParentPathXmlAdapter.class)
    @Schema(description = "The full Scope ID Hierarchy for the Account")
    String getParentAccountPath();

    /**
     * Sets the parent account path.
     * <p>
     * The account path is a '/' separated list of the parents {@link Account} identifiers in reverse order (so it should be read from right to left).
     * <p>
     * e.g. The parent account path 7/14/15 means that the current account has 15 as parent, then 15 has 14 as parent and 14 has 7 as parent.
     *
     * @param parentAccountPath The parent account path.
     * @since 1.0.0
     */
    void setParentAccountPath(String parentAccountPath);

    /**
     * Gets the expiration date.
     *
     * @return The expiration date.
     * @since 1.0.0
     */
    @XmlElement(name = "expirationDate")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    @Schema(description = "The Expiration date and time for the Account. If empty, the Account has no expiration")
    Date getExpirationDate();

    /**
     * Sets the expiration date.
     *
     * @param expirationDate The expiration date.
     * @since 1.0.0
     */
    void setExpirationDate(Date expirationDate);

    /**
     * Gets the {@link List} of {@link Account} that are children of this {@link Account}
     *
     * @return the {@link List} of {@link Account} that are children of this {@link Account}
     * @since 1.0.0
     */
    List<Account> getChildAccounts();

}
