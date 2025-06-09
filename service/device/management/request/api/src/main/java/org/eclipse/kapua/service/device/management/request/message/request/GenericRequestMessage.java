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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.service.device.management.message.request.KapuaRequestMessage;
import org.eclipse.kapua.service.device.management.request.message.response.GenericResponseMessage;

/**
 * Generic {@link KapuaRequestMessage} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "genericRequestMessage")
@XmlType
public class GenericRequestMessage extends KapuaRequestMessage<GenericRequestChannel, GenericRequestPayload> {

    private static final long serialVersionUID = -8491427803023664571L;

    @Override
    public Class<GenericRequestMessage> getRequestClass() {
        return GenericRequestMessage.class;
    }

    @Override
    public Class<GenericResponseMessage> getResponseClass() {
        return GenericResponseMessage.class;
    }
}
