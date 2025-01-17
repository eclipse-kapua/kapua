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
package org.eclipse.kapua.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;

/**
 * {@link KapuaEntityCreator} definition
 * <p>
 * All the {@link KapuaEntityCreator} will be an extension of this.
 *
 * @param <E>
 *         entity type
 * @since 1.0.0
 */
public abstract class KapuaEntityCreator<E extends KapuaEntity> {

    protected KapuaId scopeId;

    public KapuaEntityCreator() {
    }

    public KapuaEntityCreator(KapuaId scopeId) {
        this.scopeId = scopeId;
    }

    protected KapuaEntityCreator(KapuaEntityCreator<E> entityCreator) {
        this(entityCreator.getScopeId());
    }

    /**
     * Gets the scope {@link KapuaId}
     *
     * @return the scope {@link KapuaId}
     * @since 1.0.0
     */
    @XmlElement(name = "scopeId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getScopeId() {
        return scopeId;
    }

    /**
     * Sets the scope {@link KapuaId}
     *
     * @param scopeId
     *         the scope {@link KapuaId}
     * @since 1.0.0
     */
    public void setScopeId(KapuaId scopeId) {

        this.scopeId = scopeId;
    }
}
