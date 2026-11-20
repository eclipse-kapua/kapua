/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.api.client.util;

import com.google.gwt.user.client.Cookies;
import org.eclipse.kapua.app.console.module.api.client.util.model.KapuaCookie;

/**
 * {@link CookieUtils} implementation.
 *
 * @since 1.3.0
 */
public class CookieUtils {

    /**
     * Constructor
     */
    private CookieUtils() {
    }

    public static String readCookie(String cookieName) {
        return Cookies.getCookie(cookieName);
    }

    /**
     * Adds a {@link KapuaCookie} into the session
     *
     * @param kapuaCookie the {@link KapuaCookie} to add
     * @since 2.1.0
     */
    static public void addCookie(KapuaCookie kapuaCookie) {
        removeCookie(kapuaCookie.getName());

        Cookies.setCookie(
                kapuaCookie.getName(),
                kapuaCookie.getValue(),
                kapuaCookie.getExpirationDate()
        );
    }

    /**
     * Removes the cookie by name
     *
     * @since 1.3.0
     */
    public static void removeCookie(String cookieName) {
        Cookies.removeCookie(cookieName, "/");
    }
}
