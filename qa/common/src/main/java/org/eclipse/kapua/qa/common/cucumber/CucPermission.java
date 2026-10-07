/*******************************************************************************
 * Copyright (c) 2017, 2026 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech
 *******************************************************************************/
package org.eclipse.kapua.qa.common.cucumber;

import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;

import java.math.BigInteger;

/**
 * Data object used in Gherkin to transfer Permission data.
 */
public class CucPermission {

    private String domain;

    private Actions action;

    private Integer targetScope;
    private KapuaId targetScopeId;

    private boolean forwardable;

    public CucPermission(String domain, Actions action, Integer targetScope, KapuaId targetScopeId) {
        this(domain, action, targetScope, targetScopeId, false);
    }

    public CucPermission(String domain, Actions action, Integer targetScope, KapuaId targetScopeId, boolean forwardable) {
        this.domain = domain;
        this.action = action;
        this.targetScope = targetScope;
        this.targetScopeId = targetScopeId;
        this.forwardable = forwardable;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Actions getAction() {
        return action;
    }

    public void setAction(Actions action) {
        this.action = action;
    }

    public KapuaId getTargetScopeId() {
        if (targetScope != null) {
            return targetScopeId = new KapuaEid(BigInteger.valueOf(targetScope));
        }
        return targetScopeId;
    }

    public void setTargetScopeId(KapuaId targetScopeId) {
        this.targetScopeId = targetScopeId;
    }

    public void setTargetScopeId(BigInteger targetScopeId) {
        this.targetScopeId = new KapuaEid(targetScopeId);
    }

    public boolean getForwardable() {
        return forwardable;
    }

    public void setForwardable(boolean forwardable) {
        this.forwardable = forwardable;
    }
}
