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
package org.eclipse.kapua.service.device.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.KapuaSortCriteria;
import org.eclipse.kapua.model.query.SortOrder;

/**
 * {@link Device} {@link KapuaQuery} definition.
 *
 * @see KapuaQuery
 * @since 1.0.0
 */
@XmlRootElement(name = "query")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DeviceQuery extends KapuaQuery {

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    private DeviceQuery() {
        super();
    }

    /**
     * Constructor.
     *
     * @param scopeId
     *         The {@link #getScopeId()}.
     * @since 1.0.0
     */
    public DeviceQuery(KapuaId scopeId) {
        this();
        setScopeId(scopeId);
    }

    @Override
    public KapuaSortCriteria getDefaultSortCriteria() {
        return fieldSortCriteria(DeviceAttributes.CLIENT_ID, SortOrder.ASCENDING);
    }

    /**
     * Instantiates a new {@link DeviceMatchPredicate}.
     *
     * @param matchTerm
     *         The term to use to match.
     * @param <T>
     *         The type of the term
     * @return The newly instantiated {@link DeviceMatchPredicate}.
     * @since 1.3.0
     */
    public <T> DeviceMatchPredicate<T> matchPredicate(T matchTerm) {
        return new DeviceMatchPredicate<>(matchTerm);
    }

}
