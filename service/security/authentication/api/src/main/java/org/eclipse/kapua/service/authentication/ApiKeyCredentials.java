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

/**
 * Api key {@link LoginCredentials} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "apiKeyCredentials")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class ApiKeyCredentials implements LoginCredentials {

    private static final long serialVersionUID = -5920944517814926028L;

    private String apiKey;

    public ApiKeyCredentials() {
    }

    /**
     * Constructor.
     *
     * @param apiKey
     *         The Api Key.
     * @since 1.0.0
     */
    public ApiKeyCredentials(String apiKey) {
        setApiKey(apiKey);
    }

    /**
     * /** Gets the Api Key to authenticate.
     *
     * @return The Api Key to authenticate.
     * @since 1.0.0
     */
    @XmlElement(name = "apiKey")
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Sets the Api Key to authenticate.
     *
     * @param apiKey
     *         The Api Key to authenticate.
     * @since 1.0.0
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

}
