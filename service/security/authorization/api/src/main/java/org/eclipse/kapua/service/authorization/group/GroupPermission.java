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

import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.authorization.permission.Permission;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "groupPermission")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = GroupPermissionXmlRegistry.class, factoryMethod = "newEntity")
public interface GroupPermission extends KapuaEntity {

    String TYPE = "groupPermission";

    @Override
    default String getType() {
        return TYPE;
    }

    @XmlElement(name = "groupId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    KapuaId getGroupId();

    void setGroupId(KapuaId groupId);

    /**
     * Gets the {@link Permission} that this {@link GroupPermission} has.
     *
     * @return The {@link Permission} that this {@link GroupPermission} has.
     */
    @XmlElement(name = "permission")
    <P extends Permission> P getPermission();

    /**
     * Sets the {@link Permission} that this {@link GroupPermission} has.<br>
     * It up to the implementation class to make a clone of the given {@link Permission} or use the given {@link Permission}.
     *
     * @param permission The {@link Permission} to set for this {@link GroupPermission}.
     * @since 1.0.0
     */
    void setPermission(Permission permission);

}
