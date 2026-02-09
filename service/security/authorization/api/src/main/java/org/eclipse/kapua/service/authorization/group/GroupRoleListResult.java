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

import org.eclipse.kapua.model.query.KapuaListResult;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * {@link GroupRole} {@link KapuaListResult} definition.
 *
 * @since 2.1.0
 */
@XmlRootElement(name = "accessPermissions")
@XmlType(factoryClass = GroupRoleXmlRegistry.class, factoryMethod = "newListResult")
public interface GroupRoleListResult extends KapuaListResult<GroupRole> {

}
