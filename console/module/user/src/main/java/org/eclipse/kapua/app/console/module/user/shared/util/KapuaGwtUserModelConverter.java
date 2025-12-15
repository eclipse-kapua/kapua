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
package org.eclipse.kapua.app.console.module.user.shared.util;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.console.module.api.shared.util.KapuaGwtCommonsModelConverter;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUser;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.group.UserGroup;

public class KapuaGwtUserModelConverter {

    private KapuaGwtUserModelConverter() {
    }

    /**
     * Converts a {@link User} into a {@link GwtUser} for GWT usage.
     *
     * @param user The {@link User} to convertAccessPermissionCreator.
     * @return The converted {@link GwtUser}
     * @since 1.0.0
     */
    public static GwtUser convertUser(User user) throws KapuaException {

        GwtUser gwtUser = new GwtUser();
        // Convert commons attributes
        KapuaGwtCommonsModelConverter.convertUpdatableEntity(user, gwtUser);
        // Convert other attributes
        gwtUser.setUsername(user.getName());
        gwtUser.setDisplayName(user.getDisplayName());
        gwtUser.setEmail(user.getEmail());
        gwtUser.setPhoneNumber(user.getPhoneNumber());
        gwtUser.setStatus(user.getStatus().toString());
        gwtUser.setExpirationDate(user.getExpirationDate());
        gwtUser.setUserType(user.getUserType().toString());
        gwtUser.setExternalId(user.getExternalId());
        gwtUser.setExternalUsername(user.getExternalUsername());
        // Return converted entity
        return gwtUser;
    }

    public static GwtGroup convertUserGroup(UserGroup group) {

        GwtGroup gwtGroup = new GwtGroup();
        // Covert commons attributes
        KapuaGwtCommonsModelConverter.convertUpdatableEntity(group, gwtGroup);
        // Convert other attributes
        gwtGroup.setGroupName(group.getName());
        gwtGroup.setGroupDescription(group.getDescription());
        gwtGroup.setDomain("user");

        return gwtGroup;
    }
}
