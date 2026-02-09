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
