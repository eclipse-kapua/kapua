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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaNamedEntityUpdateRequest;
import org.eclipse.kapua.model.xml.DateXmlAdapter;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "account")
@Schema(description = "An object that holds all the information necessary to update an Account, including the details about the Contacts of that account.\n" +
                          "In Kapua an Account is the container of all other resources (Users, Devices, etc.), and is the equivalent of the concept of Tenant.\n" +
                          "Every entity in Kapua will have a `scopeId` property, that holds the ID of the Account who holds that entity.")
public class AccountUpdateRequest extends KapuaNamedEntityUpdateRequest {

    /**
     * The account's expiration date.
     *
     * @since 1.0.0
     */
    @XmlElement(name = "expirationDate")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    @Schema(description = "The Expiration date and time for the Account. If empty, the Account has no expiration")
    public Date expirationDate;

    /**
     * The details of this account's {@link Organization}.
     *
     * @since 1.0.0
     */
    @XmlElement(name = "organization")
    public Organization organization;
}
