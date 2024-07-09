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
package org.eclipse.kapua.model.config.metatype;

/**
 * Contains password used by devices connecting to Kapua.
 *
 * @since 1.0
 */
public class Password {

    private final String password;

    /**
     * Constructor
     *
     * @param password
     */
    public Password(String password) {
        this.password = password;
    }

    /**
     * Get password
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return password;
    }

}
