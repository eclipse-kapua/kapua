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
package org.eclipse.kapua.service.authorization.group.shiro;

import org.eclipse.kapua.commons.model.AbstractKapuaNamedEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.domain.DomainCreator;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupCreator;

/**
 * {@link GroupCreator} implementation.
 *
 * @since 1.0.0
 */
public class GroupCreatorImpl extends AbstractKapuaNamedEntityCreator<Group> implements GroupCreator {

    private static final long serialVersionUID = -4676187845961673421L;

    private String domain;

    /**
     * Constructor
     *
     * @param scopeId The scope id to set.
     * @param name    The name to set for this {@link DomainCreator}.
     * @since 1.0.0
     */
    public GroupCreatorImpl(KapuaId scopeId, String name) {
        super(scopeId, name);
    }

    public GroupCreatorImpl(KapuaId scopeId) {
        super(scopeId);
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }
}
