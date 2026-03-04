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
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.authorization.permission.shiro;

import com.google.common.collect.Sets;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.domain.Domain;
import org.eclipse.kapua.service.authorization.domain.DomainFactory;
import org.eclipse.kapua.service.authorization.domain.DomainListResult;
import org.eclipse.kapua.service.authorization.domain.DomainRegistryService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionAttributes;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

public class PermissionValidator {

    private final AuthorizationService authorizationService;
    private final DomainRegistryService domainService;
    private final DomainFactory domainFactory;

    @Inject
    public PermissionValidator(
            AuthorizationService authorizationService,
            DomainRegistryService domainService,
            DomainFactory domainFactory
    ) {
        this.authorizationService = authorizationService;
        this.domainService = domainService;
        this.domainFactory = domainFactory;
    }

    public void validatePermission(@NotNull KapuaId scopeId, @NotNull Permission permission) throws KapuaException {
        validatePermissions(scopeId, Sets.newHashSet(permission));
    }

    public void validatePermissions(@NotNull KapuaId scopeId, @NotNull Set<Permission> permissions) throws KapuaException {

        // FIXME: This might be moved in a util that also caches the available Domains
        DomainListResult domains = KapuaSecurityUtils.doPrivileged(() -> domainService.query(domainFactory.newQuery(KapuaId.ANY)));
        Map<String, Domain> domainMap = domains.getItemsAsMap(Domain::getName);

        // Do validate
        for (Permission permission : permissions) {
            // If Permission.targetScopeId points to another scope than the one where the Permission are created, check that the current User has the permission on the other scopeId.
            if (permission.getTargetScopeId() == null || !permission.getTargetScopeId().equals(scopeId)) {
                authorizationService.checkPermission(permission);
            }

            // If Permission.groupId is set, cannot be forwardable. The Group exist in only one scope
            if (permission.getGroupId() != null && permission.getForwardable()) {
                throw new KapuaIllegalArgumentException(PermissionAttributes.FORWARDABLE, "true");
            }

            // If Domain is set...
            if (permission.getDomain() != null) {
                // A Permission.domain must exist.
                Domain domain = domainMap.get(permission.getDomain());
                if (domain == null) {
                    throw new KapuaIllegalArgumentException(PermissionAttributes.DOMAIN, permission.getDomain());
                }

                // If Permission.groupId is set the Domain must be groupable.
                if (permission.getGroupId() != null && !domain.getGroupable()) {
                    throw new KapuaIllegalArgumentException(PermissionAttributes.GROUP_ID, permission.getGroupId().toStringId());
                }
            }
        }
    }
}
