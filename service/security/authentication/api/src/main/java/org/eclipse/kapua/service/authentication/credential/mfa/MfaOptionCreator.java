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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.user.User;

/**
 * {@link MfaOption} {@link KapuaEntityCreator}
 *
 * @since 1.3.0
 */
@XmlRootElement(name = "mfaOptionCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class MfaOptionCreator extends KapuaEntityCreator {

    private static final long serialVersionUID = -4619585500941519330L;

    private KapuaId userId;

    public MfaOptionCreator() {
    }

    public MfaOptionCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public MfaOptionCreator(KapuaEntityCreator entityCreator) {
        super(entityCreator);
    }

    /**
     * Constructor
     *
     * @param scopeId
     *         scope identifier
     * @param userId
     *         user identifier
     */
    public MfaOptionCreator(KapuaId scopeId, KapuaId userId) {
        super(scopeId);
        this.userId = userId;
    }

    /**
     * Gets the {@link User#getId()}
     *
     * @return The {@link User#getId()}
     * @since 1.3.0
     */
    @XmlElement(name = "userId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getUserId() {
        return userId;
    }

    /**
     * Sets the {@link User#getId()}
     *
     * @param userId
     *         The {@link User#getId()}
     * @since 1.3.0
     */
    public void setUserId(KapuaId userId) {
        this.userId = userId;
    }
}
