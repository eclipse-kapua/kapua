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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.KapuaSerializable;

/**
 * Service configuration entity definition.
 *
 * @since 1.0
 */
@XmlRootElement(name = "serviceConfigurations")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class ServiceConfiguration implements KapuaSerializable {

    private static final long serialVersionUID = -2167999497954676423L;

    private List<ServiceComponentConfiguration> configurations;

    public ServiceConfiguration() {
        configurations = new ArrayList<>();
    }

    /**
     * Get the service component configuration list
     *
     * @return
     */
    @XmlElement(name = "configuration")
    public List<ServiceComponentConfiguration> getComponentConfigurations() {
        return configurations;
    }
}
