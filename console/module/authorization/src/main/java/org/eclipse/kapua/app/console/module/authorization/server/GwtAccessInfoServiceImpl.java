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
package org.eclipse.kapua.app.console.module.authorization.server;

import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtAccessInfo;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtAccessInfoCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtAccessInfoService;
import org.eclipse.kapua.app.console.module.authorization.shared.util.GwtKapuaAuthorizationModelConverter;
import org.eclipse.kapua.app.console.module.authorization.shared.util.KapuaGwtAuthorizationModelConverter;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfoCreator;
import org.eclipse.kapua.service.authorization.access.AccessInfoService;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.access.AccessPermissionListResult;
import org.eclipse.kapua.service.authorization.access.AccessPermissionService;
import org.eclipse.kapua.service.authorization.access.AccessRole;
import org.eclipse.kapua.service.authorization.access.AccessRoleListResult;
import org.eclipse.kapua.service.authorization.access.AccessRoleService;

public class GwtAccessInfoServiceImpl extends KapuaRemoteServiceServlet implements GwtAccessInfoService {

    private static final long serialVersionUID = 3606053200278262228L;

    @Override
    public GwtAccessInfo create(GwtXSRFToken xsrfToken, GwtAccessInfoCreator gwtAccessInfoCreator) throws GwtKapuaException {
        // Checking XSRF token
        checkXSRFToken(xsrfToken);
        // Do create
        GwtAccessInfo gwtAccessInfo = null;
        try {
            // Convert from GWT Entity
            AccessInfoCreator accessInfoCreator = GwtKapuaAuthorizationModelConverter.convertAccessInfoCreator(gwtAccessInfoCreator);

            // Create
            KapuaLocator locator = KapuaLocator.getInstance();
            AccessInfoService accessInfoService = locator.getService(AccessInfoService.class);
            AccessInfo accessInfo = accessInfoService.create(accessInfoCreator);

            // Convert
            gwtAccessInfo = KapuaGwtAuthorizationModelConverter.convertAccessInfo(accessInfo);

        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
        // Return result
        return gwtAccessInfo;
    }

    @Override
    public void delete(GwtXSRFToken gwtXsrfToken, String scopeShortId, String accessInfoShortId) throws GwtKapuaException {
        // Checking XSRF token
        checkXSRFToken(gwtXsrfToken);
        // Do delete
        try {
            // Convert from GWT Entity
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(scopeShortId);
            KapuaId accessInfoId = GwtKapuaCommonsModelConverter.convertKapuaId(accessInfoShortId);

            // Delete
            KapuaLocator locator = KapuaLocator.getInstance();
            AccessInfoService accessInfoService = locator.getService(AccessInfoService.class);
            accessInfoService.delete(scopeId, accessInfoId);

            AccessRoleService accessRoleService = locator.getService(AccessRoleService.class);
            AccessRoleListResult accessRoles = accessRoleService.findByAccessInfoId(scopeId, accessInfoId);
            for (AccessRole accessRole : accessRoles.getItems()) {
                accessRoleService.delete(scopeId, accessRole.getId());
            }

            AccessPermissionService accessPermissionService = locator.getService(AccessPermissionService.class);
            AccessPermissionListResult accessPermissions = accessPermissionService.findByAccessInfoId(scopeId, accessInfoId);
            for (AccessPermission accessPermission : accessPermissions.getItems()) {
                accessPermissionService.delete(scopeId, accessPermission.getId());

            }
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
    }

    @Override
    public GwtAccessInfo findByUserIdOrCreate(String scopeShortId, String userShortId) throws GwtKapuaException {
        GwtAccessInfo gwtAccessInfo = null;

        try {
            // Convert from GWT Entity
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(scopeShortId);
            KapuaId userId = GwtKapuaCommonsModelConverter.convertKapuaId(userShortId);

            // Find
            KapuaLocator locator = KapuaLocator.getInstance();
            AccessInfoService accessInfoService = locator.getService(AccessInfoService.class);
            AccessInfo accessInfo = accessInfoService.findByUserId(scopeId, userId);
            if (accessInfo == null) {
                AccessInfoCreator accessInfoCreator = new AccessInfoCreator(scopeId);
                accessInfoCreator.setUserId(userId);
                accessInfo = accessInfoService.create(accessInfoCreator);
            }
            gwtAccessInfo = KapuaGwtAuthorizationModelConverter.convertAccessInfo(accessInfo);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
        return gwtAccessInfo;
    }

}
