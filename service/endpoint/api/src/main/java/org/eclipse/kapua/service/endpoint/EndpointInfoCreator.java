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
package org.eclipse.kapua.service.endpoint;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link EndpointInfo} creator definition.<br> It is used to create a new {@link EndpointInfo}.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "endpointInfoCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class EndpointInfoCreator extends KapuaEntityCreator {

    private String schema;
    private String dns;
    private int port;
    private boolean secure;
    private Set<EndpointUsage> usages;
    private String endpointType;

    public EndpointInfoCreator() {
    }

    public EndpointInfoCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public EndpointInfoCreator(KapuaEntityCreator entityCreator) {
        super(entityCreator);
    }

    public String getSchema() {
        return schema;
    }

    public EndpointInfoCreator setSchema(String schema) {
        this.schema = schema;
        return this;
    }

    public String getDns() {
        return dns;
    }

    public EndpointInfoCreator setDns(String dns) {
        this.dns = dns;
        return this;
    }

    public int getPort() {
        return port;
    }

    public EndpointInfoCreator setPort(int port) {
        this.port = port;
        return this;
    }

    public boolean getSecure() {
        return secure;
    }

    public EndpointInfoCreator setSecure(boolean secure) {
        this.secure = secure;
        return this;
    }

    public Set<EndpointUsage> getUsages() {
        if (usages == null) {
            usages = new HashSet<>();
        }

        return usages;
    }

    public EndpointInfoCreator setUsages(Set<EndpointUsage> usages) {
        this.usages = usages;
        return this;
    }

    public String getEndpointType() {
        return endpointType;
    }

    public EndpointInfoCreator setEndpointType(String endpointType) {
        this.endpointType = endpointType;
        return this;
    }
}
