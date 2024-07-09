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
 *      Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.device.call.kura.model.configuration;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.service.device.call.kura.model.configuration.xml.KuraXmlConfigPropertiesAdapter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Describes the configuration of an OSGi Component.<br> The Component configuration groups all the information related to the configuration of a Component.<br> It provides access to parsed
 * ObjectClassDefintion associated to this Component.<br> The configuration does not reuse the OSGi ObjectClassDefinition as the latter does not provide access to certain aspects such as the required
 * attribute, the min and max values.<br> Instead it returns the raw ObjectClassDefintion as parsed from the MetaType Information XML resource associated to this Component.
 *
 * @since 1.0
 */
@JsonRootName("componentConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class KuraDeviceComponentConfiguration {

    /**
     * The PID (service's persistent identity) of the OSGi Component associated to this configuration.<br> The service's persistent identity is defined as the name attribute of the Component
     * Descriptor XML file; at runtime, the same value is also available in the component.name and in the service.pid attributes of the Component Configuration.
     */
    @JsonProperty("pid")
    @XmlAttribute(name = "pid")
    private String componentId;

    /**
     * The raw ObjectClassDefinition as parsed from the MetaType Information XML resource associated to this Component.
     */
    @JsonIgnore
    @XmlElementRef(type = KapuaTocd.class)
    private KapuaTocd definition;

    /**
     * The Dictionary of properties currently used by this component.
     */
    @JsonIgnore
    @XmlElement(name = "properties")
    @XmlJavaTypeAdapter(KuraXmlConfigPropertiesAdapter.class)
    private Map<String, Object> properties;

    // Required by JAXB
    public KuraDeviceComponentConfiguration() {
    }

    /**
     * Get the component identifier.<br> The PID (service's persistent identity) of the OSGi Component associated to this configuration.<br> The service's persistent identity is defined as the name
     * attribute of the Component Descriptor XML file; at runtime, the same value is also available in the component.name and in the service.pid attributes of the Component Configuration.
     *
     * @return
     */
    public String getComponentId() {
        return componentId;
    }

    /**
     * Set the component identifier. Please refer to {@link KuraDeviceComponentConfiguration#getComponentId}
     *
     * @param componentId
     */
    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    /**
     * Get the class definition.<br> The raw ObjectClassDefinition as parsed from the MetaType Information XML resource associated to this Component.
     *
     * @return
     */
    public KapuaTocd getDefinition() {
        return definition;
    }

    /**
     * Set the class definition. Please refer to {@link KuraDeviceComponentConfiguration#getDefinition}
     *
     * @param definition
     */
    public void setDefinition(KapuaTocd definition) {
        this.definition = definition;
    }

    /**
     * Get configuration properties.<br> The Dictionary of properties currently used by this component.
     *
     * @return
     */
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Set configuration properties. Please refer to {@link KuraDeviceComponentConfiguration#getProperties}
     *
     * @param properties
     */
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
