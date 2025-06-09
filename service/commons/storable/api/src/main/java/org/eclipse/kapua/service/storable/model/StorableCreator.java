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
package org.eclipse.kapua.service.storable.model;

import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link StorableCreator} definition.
 * <p>
 * It is the base {@code interface} for all {@link Object} creators that are {@link Storable}.
 *
 * @param <E>
 *         The {@link Storable} for which this is a {@link StorableCreator} for.
 * @since 1.0.0
 */
public class StorableCreator<E extends Storable> {

    private KapuaId scopeId;

    public StorableCreator() {
    }

    public StorableCreator(KapuaId scopeId) {
        this.scopeId = scopeId;
    }

    /**
     * Gets the scope {@link KapuaId}.
     *
     * @return The scope {@link KapuaId}.
     * @since 1.0.0
     */
    public KapuaId getScopeId() {
        return scopeId;
    }

    /**
     * Sets the scope {@link KapuaId}.
     *
     * @param scopeId
     *         The scope {@link KapuaId}.
     * @since 1.3.0
     */
    public void setScopeId(KapuaId scopeId) {
        this.scopeId = scopeId;
    }
}
