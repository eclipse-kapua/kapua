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
package org.eclipse.kapua.service.authorization.role;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaNamedEntity;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link Role} {@link org.eclipse.kapua.model.KapuaEntity} definition.
 * <p>
 * {@link Role} is a collection of {@link RolePermission}s that can be assigned to one or more {@link org.eclipse.kapua.service.user.User}s (using {@link AccessInfo}).
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "role")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = RoleXmlRegistry.class, factoryMethod = "newRole")
public interface Role extends KapuaNamedEntity {

    String TYPE = "role";

    @Override
    @Schema(example = "role")
    default String getType() {
        return TYPE;
    }
}
