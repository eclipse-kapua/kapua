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
package org.eclipse.kapua.model.query;

import org.eclipse.kapua.model.KapuaNamedEntity;
import org.eclipse.kapua.model.KapuaNamedEntityAttributes;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link KapuaQuery} {@code abstract} implementation for {@link KapuaNamedEntity}.
 * <p>
 * It defaults the {@link #getSortCriteria()} on the {@link KapuaNamedEntityAttributes#NAME}
 *
 * @since 1.5.0
 */
public abstract class AbstractKapuaNamedQuery extends KapuaQuery {

    /**
     * Constructor.
     *
     * @since 1.5.0
     */
    public AbstractKapuaNamedQuery() {
        super();
    }

    /**
     * Constructor.
     *
     * @param scopeId
     *         The {@link #getScopeId()}.
     * @since 1.5.0
     */
    public AbstractKapuaNamedQuery(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * Clone constructor.
     *
     * @param query
     *         The {@link KapuaQuery} to clone.
     * @since 1.5.0
     */
    public AbstractKapuaNamedQuery(KapuaQuery query) {
        super(query);
    }

    @Override
    public KapuaSortCriteria getDefaultSortCriteria() {
        return fieldSortCriteria(KapuaNamedEntityAttributes.NAME, SortOrder.ASCENDING);
    }
}
