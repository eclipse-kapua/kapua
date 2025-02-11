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
package org.eclipse.kapua.service.device.management.message.request;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.service.device.management.message.KapuaAppChannel;
import org.eclipse.kapua.service.device.management.message.KapuaMethod;

/**
 * {@link KapuaRequestMessage} {@link KapuaAppChannel} definition.
 * <p>
 * This object defines the channel for a Kapua request message. The request message is used to perform interactive operations with the device (e.g. to send command to the device, to ask
 * configurations...)
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "channel")
@XmlType
public class KapuaRequestChannel extends KapuaAppChannel {

    private static final long serialVersionUID = -7140990471048488667L;

    protected KapuaMethod method;
    protected String resource;

    /**
     * Gets the {@link KapuaMethod}
     *
     * @return The {@link KapuaMethod}
     * @since 1.0.0
     */
    @XmlElement(name = "method")
    public KapuaMethod getMethod() {
        return method;
    }

    /**
     * Sets the {@link KapuaMethod}
     *
     * @param method
     *         The {@link KapuaMethod}
     * @since 1.0.0
     */
    public void setMethod(KapuaMethod method) {
        this.method = method;
    }

    /**
     * Gets the requested resource.
     *
     * @return The requested resource.
     * @since 1.5.0
     */
    @XmlElement(name = "resource")
    public String getResource() {
        return resource;
    }

    /**
     * Sets the requested resource.
     *
     * @param resource
     *         The requested resource.
     * @since 1.5.0
     */
    public void setResource(String resource) {
        this.resource = resource;
    }

}
