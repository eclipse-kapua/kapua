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
package org.eclipse.kapua.service.authentication.credential;

import javax.xml.bind.annotation.XmlRegistry;

import org.eclipse.kapua.locator.KapuaLocator;

/**
 * The {@link Credential} {@link XmlRegistry}
 *
 * @since 1.0.0
 */
@XmlRegistry
public class CredentialXmlRegistry {

    private final CredentialFactory credentialFactory = KapuaLocator.getInstance().getFactory(CredentialFactory.class);

    /**
     * Instantiates a new {@link Credential}.
     *
     * @return The newly instantiated {@link Credential}
     * @since 1.0.0
     */
    public Credential newCredential() {
        return credentialFactory.newEntity(null);
    }
}
