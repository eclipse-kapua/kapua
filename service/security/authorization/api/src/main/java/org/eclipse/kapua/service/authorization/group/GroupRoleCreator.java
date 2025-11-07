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
import org.eclipse.kapua.service.authorization.role.Role;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * {@link GroupRole} {@link KapuaEntityCreator} definition
 *
 * @since 2.1.0
 */
@XmlRootElement(name = "accessRoleCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = GroupRoleXmlRegistry.class, factoryMethod = "newCreator")
public interface GroupRoleCreator extends KapuaEntityCreator<GroupRole> {

    @XmlElement(name = "groupId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    KapuaId getGroupId();

    void setGroupId(KapuaId groupId);

    /**
     * Gets the {@link Role} id added to this {@link GroupRole}.
     *
     * @return The {@link Role} id added to this {@link GroupRole}.
     * @since 2.1.0
     */
    @XmlElement(name = "roleId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    KapuaId getRoleId();

    /**
     * Sets the {@link Role} id to assign to the {@link GroupRole} created entity.
     * It up to the implementation class to make a clone of the object or use the given object.
     *
     * @param roleId The {@link Role} id
     * @since 2.1.0
     */
    void setRoleId(KapuaId roleId);
}
