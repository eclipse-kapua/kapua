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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaNamedEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link Group} {@link org.eclipse.kapua.model.KapuaEntity} definition.<br>
 * {@link Group}s serve as tag for entities marked as {@link Groupable}.
 * It is possible to assign a {@link Group} to a {@link org.eclipse.kapua.model.KapuaEntity}.
 * It is possible to assign {@link AccessPermission} and {@link RolePermission} based on the {@link Group#getId()}.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "group")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = GroupXmlRegistry.class, factoryMethod = "newGroup")
public interface Group extends KapuaNamedEntity {

    @XmlTransient
    KapuaId ANY = KapuaId.ANY;

    String TYPE = "group";

    @Override
    @Schema(example = "group")
    default String getType() {
        return TYPE;
    }
}
