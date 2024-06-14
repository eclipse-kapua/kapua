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
package org.eclipse.kapua.commons.web.rest.model;

import java.math.BigInteger;
import java.util.Base64;

import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link KapuaId} implementation to be used on REST API to parse the entityId.
 *
 * @since 1.0.0
 */
public class EntityId implements KapuaId {

    private static final long serialVersionUID = 6893262093856905182L;

    private BigInteger id;

    /**
     * Builds the {@link KapuaId} from the given {@link String} compact entity id.
     *
     * @param compactEntityId
     *         The compact entityId to parse.
     * @since 1.0.0
     */
    public EntityId(String compactEntityId) {
        byte[] bytes = Base64.getUrlDecoder().decode(compactEntityId);
        setId(new BigInteger(bytes));
    }

    @Override
    public BigInteger getId() {
        return id;
    }

    protected void setId(BigInteger id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return KapuaId.toString(this);
    }

    @Override
    public int hashCode() {
        return KapuaId.hashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return KapuaId.areEquals(this, obj);
    }
}
