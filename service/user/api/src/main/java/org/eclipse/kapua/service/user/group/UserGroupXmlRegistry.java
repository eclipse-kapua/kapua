/*******************************************************************************
 * Copyright (c) 2025, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.user.group;

import org.eclipse.kapua.locator.KapuaLocator;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * {@link UserGroup} {@link XmlRegistry} definition.
 *
 * @since 2.1.0
 */
@XmlRegistry
public class UserGroupXmlRegistry {

    private final UserGroupFactory userGroupFactory = KapuaLocator.getInstance().getFactory(UserGroupFactory.class);

    /**
     * Instantiates a new {@link UserGroup} instance
     *
     * @return The newly instantiated {@link UserGroup} instance.
     * @since 2.1.0
     */
    public UserGroup newEntity() {
        return userGroupFactory.newEntity(null);
    }

    /**
     * Instantiates a new {@link UserGroupCreator} instance.
     *
     * @return The newly instantiated {@link UserGroupCreator} instance.
     * @since 2.1.0
     */
    public UserGroupCreator newCreator() {
        return userGroupFactory.newCreator(null);
    }

    /**
     * Instantiates a new {@link UserGroupListResult} instance.
     *
     * @return The newly instantiated {@link UserGroupListResult} instance.
     * @since 2.1.0
     */
    public UserGroupListResult newListResult() {
        return userGroupFactory.newListResult();
    }

    /**
     * Instantiates a new {@link UserGroupQuery} instance.
     *
     * @return The newly instantiated {@link UserGroupQuery} instance.
     * @since 2.1.0
     */
    public UserGroupQuery newQuery() {
        return userGroupFactory.newQuery(null);
    }
}
