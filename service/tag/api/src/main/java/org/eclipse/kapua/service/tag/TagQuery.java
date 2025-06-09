/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.tag;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;

/**
 * {@link Tag} {@link KapuaQuery} definition.
 *
 * @see KapuaQuery
 * @since 1.0.0
 */
@XmlRootElement(name = "query")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class TagQuery extends KapuaQuery {

    public TagQuery() {
    }

    /**
     * Constructor.
     *
     * @param scopeId
     *         The {@link #getScopeId()}.
     * @since 1.0.0
     */
    public TagQuery(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * Instantiates a new {@link TagMatchPredicate}.
     *
     * @param matchTerm
     *         The term to use to match.
     * @param <T>
     *         The type of the term
     * @return The newly instantiated {@link TagMatchPredicate}.
     * @since 2.1.0
     */
    public <T> TagMatchPredicate<T> matchPredicate(T matchTerm) {
        return new TagMatchPredicate<>(matchTerm);
    }

}
