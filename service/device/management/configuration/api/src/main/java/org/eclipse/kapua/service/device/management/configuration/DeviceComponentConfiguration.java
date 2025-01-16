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
package org.eclipse.kapua.service.device.management.configuration;

import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Device component configuration entity definition.
 *
 * @since 1.0
 */
@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = DeviceConfigurationXmlRegistry.class, factoryMethod = "newComponentConfiguration")
public interface DeviceComponentConfiguration {

    /**
     * Get device configuration component identifier
     *
     * @return
     */
    @XmlElement(name = "id")
    @Schema(example = "org.eclipse.kura.wire.graph.WireGraphService")
    String getId();

    /**
     * Set device configuration component identifier
     *
     * @param id
     */
    void setId(String id);

    /**
     * Get device configuration component name
     *
     * @return
     */
    @XmlAttribute(name = "name")
    String getName();

    /**
     * Set device configuration component name
     *
     * @param name
     */
    @Schema(example = "kura.service.pid")
    void setName(String name);

    /**
     * Get device configuration component definition
     *
     * @return
     */
    @XmlElement(name = "definition")
    KapuaTocd getDefinition();

    /**
     * Set device configuration component definition
     *
     * @param definition
     */
    void setDefinition(KapuaTocd definition);

    /**
     * Get device configuration component properties
     *
     * @return
     */
    @XmlElement(name = "properties")
    @XmlJavaTypeAdapter(DeviceXmlConfigPropertiesAdapter.class)
    Map<String, Object> getProperties();

    /**
     * Set device configuration component properties
     *
     * @param properties
     */
    void setProperties(Map<String, Object> properties);
}
