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
import io.swagger.v3.oas.annotations.media.Schema;

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
}, factoryClass = ServiceConfigurationXmlRegistry.class, factoryMethod = "newComponentConfiguration")
public interface ServiceComponentConfiguration {

    /**
     * Get service configuration component identifier
     *
     * @return
     */
    @XmlElement(name = "id")
    @Schema(example = "org.eclipse.kapua.service.account.AccountService")
    String getId();

    /**
     * Set service configuration component identifier
     *
     * @param id
     */
    void setId(String id);

    /**
     * Get service configuration component name
     *
     * @return
     */
    @XmlAttribute(name = "name")
    @Schema(example = "AccountService")
    String getName();

    /**
     * Set service configuration component name
     *
     * @param unescapedComponentName
     */
    void setName(String unescapedComponentName);

    /**
     * Get service configuration component definition
     *
     * @return
     */
    @XmlElement(name = "definition")
    KapuaTocd getDefinition();

    /**
     * Set service configuration component definition
     *
     * @param definition
     */
    void setDefinition(KapuaTocd definition);

    /**
     * Get service configuration component properties
     *
     * @return
     */
    @XmlElement(name = "properties")
    @XmlJavaTypeAdapter(ServiceXmlConfigPropertiesAdapter.class)
    Map<String, Object> getProperties();

    /**
     * Set service configuration component properties
     *
     * @param properties
     */
    void setProperties(Map<String, Object> properties);
}
