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

import org.eclipse.kapua.model.KapuaNamedEntityCreator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * {@link GroupCreator} definition.
 * <p>
 * It is used to create a new {@link Group}.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "groupCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = GroupXmlRegistry.class, factoryMethod = "newGroupCreator")
public interface GroupCreator extends KapuaNamedEntityCreator<Group> {

    /**
     * Gets the target domain
     *
     * @return The target domain
     * @since 2.1.0
     */
    String getDomain();

    /**
     * Sets the target domain
     *
     * @param domain The target domain
     * @since 2.1.0
     */
    void setDomain(String domain);
}
