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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.authorization.role.Role;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlRootElement(name = "groupRole")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = GroupRoleXmlRegistry.class, factoryMethod = "newEntity")
public interface GroupRole extends KapuaEntity {

    String TYPE = "groupRole";

    @Override
    default String getType() {
        return TYPE;
    }

    @XmlElement(name = "groupId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    KapuaId getGroupId();

    void setGroupId(KapuaId groupId);

    /**
     * Gets the {@link Role} id that this {@link GroupRole} has.
     *
     * @return The {@link Role} id that this {@link GroupRole} has.
     */
    @XmlElement(name = "roleId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    KapuaId getRoleId();

    /**
     * Sets the {@link Role} id that this {@link GroupRole} has.<br>
     * It up to the implementation class to make a clone of the given {@link Role} or use the given {@link Role}.
     *
     * @param roleId The {@link Role} id to set for this {@link GroupRole}.
     * @since 1.0.0
     */
    void setRoleId(KapuaId roleId) throws KapuaException;
}
