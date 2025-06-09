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
package org.eclipse.kapua.service.authorization.permission.shiro;

import java.util.Set;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.domain.Domain;
import org.eclipse.kapua.service.authorization.domain.DomainFactory;
import org.eclipse.kapua.service.authorization.domain.DomainListResult;
import org.eclipse.kapua.service.authorization.domain.DomainRegistryService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionAttributes;

import com.google.common.collect.Sets;

public class PermissionValidator {

    private final DomainRegistryService domainService;
    private final DomainFactory domainFactory;

    @Inject
    public PermissionValidator(DomainRegistryService domainService, DomainFactory domainFactory) {
        this.domainService = domainService;
        this.domainFactory = domainFactory;
    }

    public void validatePermission(@NotNull Permission permission) throws KapuaException {
        validatePermissions(Sets.newHashSet(permission));
    }

    public void validatePermissions(@NotNull Set<Permission> permissions) throws KapuaException {
        if (!permissions.isEmpty()) {
            DomainListResult domains = domainService.query(new KapuaQuery((KapuaId) null));

            for (Permission p : permissions) {
                if (p.getDomain() != null) {
                    boolean matched = false;
                    for (Domain domain : domains.getItems()) {
                        if (domain.getName().equals(p.getDomain())) {
                            matched = true;
                            if (!domain.getGroupable() && p.getGroupId() != null) {
                                throw new KapuaIllegalArgumentException(PermissionAttributes.GROUP_ID, p.getGroupId().toStringId());
                            }
                            break;
                        }
                    }

                    if (!matched) {
                        throw new KapuaIllegalArgumentException(PermissionAttributes.DOMAIN, p.getDomain());
                    }
                }
            }
        }
    }
}
