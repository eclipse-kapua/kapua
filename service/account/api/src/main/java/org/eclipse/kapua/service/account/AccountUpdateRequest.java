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

@XmlRootElement(name = "account")
public class AccountUpdateRequest extends KapuaNamedEntityUpdateRequest {

    /**
     * The account's expiration date.
     *
     * @since 1.0.0
     */
    @XmlElement(name = "expirationDate")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    public Date expirationDate;

    /**
     * The account's status.
     *
     * @since 2.0.0
     */
    @XmlElement(name = "status")
    public AccountStatus status;

    /**
     * The details of this account's {@link Organization}.
     *
     * @since 1.0.0
     */
    @XmlElement(name = "organization")
    public Organization organization;
}
