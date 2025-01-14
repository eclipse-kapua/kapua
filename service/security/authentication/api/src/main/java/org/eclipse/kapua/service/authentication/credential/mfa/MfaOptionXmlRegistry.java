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
package org.eclipse.kapua.service.authentication.credential.mfa;

import javax.xml.bind.annotation.XmlRegistry;

import org.eclipse.kapua.locator.KapuaLocator;

@XmlRegistry
public class MfaOptionXmlRegistry {

    private final MfaOptionFactory mfaOptionFactory = KapuaLocator.getInstance().getFactory(MfaOptionFactory.class);

    /**
     * Instantiates a new {@link MfaOptionCreator} instance
     *
     * @return The newly instantiated {@link MfaOptionCreator}
     * @since 1.3.0
     */
    public MfaOptionCreator newMfaOptionCreator() {
        return mfaOptionFactory.newCreator(null, null);
    }

    /**
     * Instantiates a new {@link MfaOption} instance
     *
     * @return The newly instantiated {@link MfaOption}
     * @since 1.3.0
     */
    public MfaOption newMfaOption() {
        return mfaOptionFactory.newEntity(null);
    }
}
