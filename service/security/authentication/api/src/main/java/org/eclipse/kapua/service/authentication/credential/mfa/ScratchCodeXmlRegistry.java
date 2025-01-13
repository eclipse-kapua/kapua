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
public class ScratchCodeXmlRegistry {

    private final ScratchCodeFactory scratchCodeFactory = KapuaLocator.getInstance().getFactory(ScratchCodeFactory.class);

    /**
     * Instantiates a new {@link ScratchCode} instance.
     *
     * @return The newly instantiated {@link ScratchCode}
     * @since 1.3.0
     */
    public ScratchCode newScratchCode() {
        return scratchCodeFactory.newEntity(null);
    }
}
