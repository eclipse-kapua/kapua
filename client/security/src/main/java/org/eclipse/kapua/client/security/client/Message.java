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
package org.eclipse.kapua.client.security.client;

import java.util.Map;

public class Message {

    private String destination;
    private Map<String, Object> properties;
    private String body;

    public Message(String destination, String body, Map<String, Object> properties) {
        this.destination = destination;
        this.body = body;
        this.properties = properties;
    }

    public String getDestination() {
        return destination;
    }

    public String getBody() {
        return body;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

}