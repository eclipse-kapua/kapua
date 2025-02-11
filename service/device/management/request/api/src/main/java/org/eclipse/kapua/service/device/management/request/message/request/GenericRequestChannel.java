/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.request.message.request;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.service.device.management.message.request.KapuaRequestChannel;

/**
 * Generic {@link KapuaRequestChannel} definition.
 *
 * @since 1.0.0
 */
@XmlType
public class GenericRequestChannel extends KapuaRequestChannel {

    private static final long serialVersionUID = -5140230399807797717L;

    private List<String> resources;

    /**
     * Gets the resources.
     * <p>
     * To be used if {@link #getResource()} is not enough.
     *
     * @return The resources.
     * @since 1.0.0
     */
    @XmlElement(name = "resources")
    public List<String> getResources() {
        if (resources == null) {
            resources = new ArrayList<>();
        }

        return resources;
    }

    /**
     * Sets the resources.
     * <p>
     * To be used if {@link #setResource(String)} is not enough.
     *
     * @param resources
     *         The resources.
     * @since 1.0.0
     */
    public void setResources(List<String> resources) {
        this.resources = resources;
    }
}
