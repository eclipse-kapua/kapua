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
package org.eclipse.kapua.service.device.registry.group;

import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.KapuaNamedEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Set;

/**
 * {@link DeviceGroup} {@link KapuaEntity} definition
 *
 * @since 2.1.0
 */
@XmlRootElement(name = "deviceGroup")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = DeviceGroupXmlRegistry.class, factoryMethod = "newEntity")
public interface DeviceGroup extends KapuaNamedEntity {

    String TYPE = "deviceGroup";

    @Override
    default String getType() {
        return TYPE;
    }

    /**
     * Gets the set Tag assigned
     *
     * @return The set Tag assigned
     * @since 2.1.0
     */
    @XmlElementWrapper(name = "tagIds")
    @XmlElement(name = "tagId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    Set<KapuaId> getTagIds();

    /**
     * Sets the set Tag to assign
     *
     * @param tagIds The set Tag to assign
     * @since 2.1.0
     */
    void setTagIds(Set<KapuaId> tagIds);
}
