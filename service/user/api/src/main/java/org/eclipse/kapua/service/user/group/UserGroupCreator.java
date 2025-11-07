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

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.KapuaNamedEntityCreator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * {@link UserGroup} {@link KapuaEntityCreator} definition
 *
 * @since 2.1.0
 */
@XmlRootElement(name = "userGroupCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = UserGroupXmlRegistry.class, factoryMethod = "newCreator")
public interface UserGroupCreator extends KapuaNamedEntityCreator<UserGroup> {
}
