/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.plugin.sso.openid;

import org.eclipse.kapua.service.account.Account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * SSO Data definition.
 * <p>
 * The {@link Account} field is excluded from XML/JSON serialization ({@link XmlTransient})
 * as it is an internal reference not meant to be exposed via the REST API.
 * </p>
 *
 * @since 2.0.0
 */
@XmlRootElement(name = "ssoData")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"accountSupportsBrokering", "accountSupportsDirectLogin", "uriSuffixDirectLogin", "companyDomainNames"})
public interface SSOData {

    @XmlTransient
    void setAccount(Account account);

    @XmlTransient
    Account getAccount();

    void setAccountSupportsBrokering(boolean accountSupportsBrokering);

    @XmlElement(name = "accountSupportsBrokering")
    boolean getAccountSupportsBrokering();

    void setAccountSupportsDirectLogin(boolean supportDirectLogin);

    @XmlElement(name = "accountSupportsDirectLogin")
    boolean getAccountSupportsDirectLogin();

    void setUriSuffixDirectLogin(String suffix);

    @XmlElement(name = "uriSuffixDirectLogin")
    String getUriSuffixDirectLogin();

    @XmlElement(name = "companyDomainNames")
    List<String> getCompanyDomainNames();

    void setCompanyDomainNames(List<String> domains);
}
