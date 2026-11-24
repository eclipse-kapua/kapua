/*******************************************************************************
 * Copyright (c) 2026, 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.api.client.util.model;

import java.util.Date;

public class MfaTrustMachineCookie implements KapuaCookie {

    public static final String COOKIE_NAME_PREFIX = "kapua-trust-";

    private final String username;

    private final String trustKey;

    private final Date expirationDate;

    private final String sameSite;

    private final boolean secure;

    public MfaTrustMachineCookie(String username, String trustKey, Date expirationDate, String sameSite, boolean secure) {
        this.username = username;
        this.trustKey = trustKey;
        this.expirationDate = expirationDate;
        this.sameSite = sameSite;
        this.secure = secure;
    }

    public String getName() {
        return COOKIE_NAME_PREFIX + getUsername();
    }

    public String getUsername() {
        return username;
    }

    public String getTrustKey() {
        return trustKey;
    }

    @Override
    public String getValue() {
        return getTrustKey();
    }

    @Override
    public Date getExpirationDate() {
        return expirationDate;
    }

    @Override
    public String getSameSite() {
        return sameSite;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }
}
