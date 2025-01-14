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
package org.eclipse.kapua.service.authorization.group;

import javax.xml.bind.annotation.XmlRegistry;

import org.eclipse.kapua.locator.KapuaLocator;

@XmlRegistry
public class GroupXmlRegistry {

    private final GroupFactory groupFactory = KapuaLocator.getInstance().getFactory(GroupFactory.class);

    /**
     * Creates a new {@link Group} instance
     *
     * @return The newly created {@link Group} instance.
     * @since 1.0.0
     */
    public Group newGroup() {
        return groupFactory.newEntity(null);
    }

    /**
     * Creates a new {@link GroupCreator} instance.
     *
     * @return The newly created {@link GroupCreator} instance.
     * @since 1.0.0
     */
    public GroupCreator newGroupCreator() {
        return groupFactory.newCreator(null, null);
    }
}
