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

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * {@link MfaOption} {@link KapuaEntityCreator}
 *
 * @since 1.3.0
 */
@XmlRootElement(name = "mfaOptionCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = MfaOptionXmlRegistry.class, factoryMethod = "newMfaOptionCreator")
public interface MfaOptionCreator extends KapuaEntityCreator<MfaOption> {

    /**
     * Gets the User#getId
     *
     * @return The User#getId
     * @since 1.3.0
     */
    @XmlElement(name = "userId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    KapuaId getUserId();

    /**
     * Sets the User#getId
     *
     * @param userId The User#getId
     * @since 1.3.0
     */
    void setUserId(KapuaId userId);
}
