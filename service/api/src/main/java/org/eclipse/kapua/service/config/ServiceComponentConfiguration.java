/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.config;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.config.metatype.KapuaTocd;

/**
 * Service component configuration entity definition.
 *
 * @since 1.0
 */
@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {
        "id",
        "name",
        "definition",
        "properties"
})
public class ServiceComponentConfiguration {

    private String id;
    private String name;
    private KapuaTocd definition;
    private Map<String, Object> properties;

    /**
     * Constructor
     */
    public ServiceComponentConfiguration() {
    }

    /**
     * Constructor
     *
     * @param id
     */
    public ServiceComponentConfiguration(String id) {
        this.id = id;
    }

    /**
     * Get service configuration component identifier
     *
     * @return
     */
    @XmlElement(name = "id")
    public String getId() {
        return id;
    }

    /**
     * Set service configuration component identifier
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get service configuration component name
     *
     * @return
     */
    @XmlAttribute(name = "name")
    public String getName() {
        return name;
    }

    /**
     * Set service configuration component name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get service configuration component definition
     *
     * @return
     */
    @XmlElement(name = "definition")
    public KapuaTocd getDefinition() {
        return definition;
    }

    /**
     * Set service configuration component definition
     *
     * @param definition
     */
    public void setDefinition(KapuaTocd definition) {
        this.definition = definition;
    }

    /**
     * Get service configuration component properties
     *
     * @return
     */
    @XmlElement(name = "properties")
    @XmlJavaTypeAdapter(ServiceXmlConfigPropertiesAdapter.class)
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Set service configuration component properties
     *
     * @param properties
     */
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
