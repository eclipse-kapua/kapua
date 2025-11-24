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

import com.google.common.collect.Sets;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.service.authorization.domain.Domain;
import org.eclipse.kapua.service.authorization.domain.DomainFactory;
import org.eclipse.kapua.service.authorization.domain.DomainListResult;
import org.eclipse.kapua.service.authorization.domain.DomainRegistryService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionAttributes;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Set;

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
            DomainListResult domains = domainService.query(domainFactory.newQuery(null));

            for (Permission p : permissions) {
                if (p.getGroupId() != null && p.getForwardable()) {
                    throw new KapuaIllegalArgumentException(PermissionAttributes.FORWARDABLE, "true");
                }
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
