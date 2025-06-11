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
package org.eclipse.kapua.service.authentication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Api key {@link LoginCredentials} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "apiKeyCredentials")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = AuthenticationXmlRegistry.class, factoryMethod = "newApiKeyCredentials")
public interface ApiKeyCredentials extends LoginCredentials {

    /**
     * Gets the Api Key to authenticate.
     *
     * @return The Api Key to authenticate.
     * @since 1.0.0
     */
    @XmlElement(name = "apiKey")
    @Schema(example = "z8PEVr4XdBS/KKEKbVG9tJzj6DNNpSVCDpW53CWm")
    String getApiKey();

    /**
     * Sets the Api Key to authenticate.
     *
     * @param apiKey The Api Key to authenticate.
     * @since 1.0.0
     */
    void setApiKey(String apiKey);
}
