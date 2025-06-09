/*******************************************************************************
 * Copyright (c) 2024, 2022 Eurotech and/or its affiliates and others
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

import javax.xml.bind.annotation.XmlElement;

import org.eclipse.kapua.model.id.KapuaId;

public class KapuaForwardableEntityQuery extends KapuaQuery {

    protected Boolean includeInherited = Boolean.FALSE;

    /**
     * Constructor.
     */
    public KapuaForwardableEntityQuery() {
        super();
    }

    /**
     * Constructor.
     *
     * @param scopeId
     *         The {@link #getScopeId()}.
     */
    public KapuaForwardableEntityQuery(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * Clone constructor.
     *
     * @param query
     *         The {@link KapuaForwardableEntityQuery} to clone.
     */
    public KapuaForwardableEntityQuery(KapuaQuery query) {
        super(query);
        if (query instanceof KapuaForwardableEntityQuery) {
            this.includeInherited = ((KapuaForwardableEntityQuery) query).getIncludeInherited();
        }
    }

    /**
     * Gets whether to query for inherited entities
     *
     * @return {@code true} if set to query for inherited entities, {@code false} otherwise.
     * @since 2.0.0
     */
    @XmlElement(name = "includeInherited")
    public Boolean getIncludeInherited() {
        return includeInherited;
    }

    /**
     * Sets whether to query for inherited entities
     *
     * @param includeInherited
     *         {@code true} to query for inherited entities, {@code false} otherwise.
     * @since 2.0.0
     */
    public void setIncludeInherited(Boolean includeInherited) {
        this.includeInherited = includeInherited;
    }
}
