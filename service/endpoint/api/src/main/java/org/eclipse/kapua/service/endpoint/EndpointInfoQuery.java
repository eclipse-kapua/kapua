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
package org.eclipse.kapua.service.endpoint;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;

/**
 * {@link EndpointInfo} {@link KapuaQuery} definition.
 *
 * @see KapuaQuery
 * @since 1.0.0
 */
@XmlRootElement(name = "query")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class EndpointInfoQuery extends KapuaQuery {

    public EndpointInfoQuery() {
    }

    /**
     * Constructor.
     *
     * @param scopeId
     *         The {@link #getScopeId()}.
     * @since 1.0.0
     */
    public EndpointInfoQuery(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * Instantiates a new {@link EndpointInfoMatchPredicate}.
     *
     * @param matchTerm
     *         The term to use to match.
     * @param <T>
     *         The type of the term
     * @return The newly instantiated {@link EndpointInfoMatchPredicate}.
     * @since 2.1.0
     */
    public <T> EndpointInfoMatchPredicate<T> matchPredicate(T matchTerm) {
        return new EndpointInfoMatchPredicate<>(matchTerm);
    }
}
