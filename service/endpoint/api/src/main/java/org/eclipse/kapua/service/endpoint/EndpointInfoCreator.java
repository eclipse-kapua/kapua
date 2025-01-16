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

import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaEntityCreator;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link EndpointInfo} creator definition.<br>
 * It is used to create a new {@link EndpointInfo}.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "endpointInfoCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = EndpointInfoXmlRegistry.class, factoryMethod = "newCreator")
@Schema(description = "An object containing the properties for the new EndpointInfo to be created")
public interface EndpointInfoCreator extends KapuaEntityCreator<EndpointInfo> {

    @Schema(example = "mqtt")
    String getSchema();

    void setSchema(String schema);

    @Schema(example = "10.200.12.148")
    String getDns();

    void setDns(String dns);

    @Schema(description = "1883")
    int getPort();

    void setPort(int port);

    boolean getSecure();

    void setSecure(boolean secure);

    Set<EndpointUsage> getUsages();

    void setUsages(Set<EndpointUsage> usages);

    @Schema(description = "resource")
    String getEndpointType();

    void setEndpointType(String endpointType);

}
