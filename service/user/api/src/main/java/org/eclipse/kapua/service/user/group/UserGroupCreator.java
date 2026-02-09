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
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * {@link UserGroup} {@link KapuaEntityCreator} definition
 *
 * @since 2.1.0
 */
@XmlRootElement(name = "userGroupCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = UserGroupXmlRegistry.class, factoryMethod = "newCreator")
public interface UserGroupCreator extends KapuaNamedEntityCreator<UserGroup> {

    /**
     * Gets the set of Tag id assigned to this entity.
     *
     * @return The set Tag id assigned to this entity.
     * @since 2.1.0
     */
    @XmlElementWrapper(name = "tagIds")
    @XmlElement(name = "tagId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    Set<KapuaId> getTagIds();

    /**
     * Sets the set of Tag id of this entity.
     *
     * @param tagIds The set Tag id to assign.
     * @since 2.1.0
     */
    void setTagIds(Set<KapuaId> tagIds);
}
