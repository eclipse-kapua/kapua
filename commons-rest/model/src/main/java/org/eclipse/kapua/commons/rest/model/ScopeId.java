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
package org.eclipse.kapua.commons.rest.model;

import java.math.BigInteger;

import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link KapuaId} implementation to be used on REST API to parse the scopeId expressed as string.
 * <p>
 * If the string is equals to "_" the scopeId used will be set to {@link KapuaSession#getScopeId()} of {@link KapuaSecurityUtils#getSession()}, which means that the scope of the current request will
 * be the same of the current session scope.
 *
 * @since 1.0.0
 */
public class ScopeId implements KapuaId {

    private static final long serialVersionUID = 6893262093856905182L;

    private BigInteger id;

    public ScopeId(BigInteger id) {
        this.id = id;
    }

    @Override
    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
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
