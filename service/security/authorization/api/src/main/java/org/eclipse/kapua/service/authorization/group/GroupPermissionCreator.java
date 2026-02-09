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
package org.eclipse.kapua.service.authorization.group;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.authorization.permission.Permission;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "groupPermissionCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = GroupPermissionXmlRegistry.class, factoryMethod = "newCreator")
public interface GroupPermissionCreator extends KapuaEntityCreator<GroupPermission> {


    @XmlElement(name = "groupId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    KapuaId getGroupId();

    void setGroupId(KapuaId groupId);

    /**
     * Gets the set of {@link Permission} added to this {@link GroupPermission}.
     *
     * @return The set of {@link Permission}.
     * @since 1.0.0
     */
    @XmlElement(name = "permission")
    <P extends Permission> P getPermission();

    /**
     * Sets the {@link Permission} to assign to the {@link GroupPermission} created entity.
     * It up to the implementation class to make a clone of the object or use the given object.
     *
     * @param permission The {@link Permission}.
     * @since 1.0.0
     */
    void setPermission(Permission permission);
}
