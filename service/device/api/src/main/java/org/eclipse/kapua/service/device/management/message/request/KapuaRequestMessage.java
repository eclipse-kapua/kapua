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
package org.eclipse.kapua.service.device.management.message.request;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponseChannel;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponseMessage;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponsePayload;

/**
 * Request {@link KapuaMessage} definition.
 * <p>
 * This object defines the for a Kapua request message. The request message is used to perform interactive operations with the device (e.g. to send command to the device, to ask configurations...)
 *
 * @since 1.0.0
 */
@XmlType
public abstract class KapuaRequestMessage<REQ_CHN extends KapuaRequestChannel, REQ_PAY extends KapuaRequestPayload> extends KapuaMessage<REQ_CHN, REQ_PAY> {

    /**
     * Gets the {@link KapuaRequestMessage} {@link Class} type.
     *
     * @return The {@link KapuaRequestMessage} {@link Class} type.
     * @since 1.0.0
     */
    @XmlTransient
    public abstract <REQ extends KapuaRequestMessage<REQ_CHN, REQ_PAY>> Class<REQ> getRequestClass();

    /**
     * Gets the {@link KapuaResponseMessage} {@link Class} type.
     *
     * @return The {@link KapuaResponseMessage} {@link Class} type.
     * @since 1.0.0
     */
    @XmlTransient
    public abstract <RSC extends KapuaResponseChannel, RSP extends KapuaResponsePayload, RES extends KapuaResponseMessage<RSC, RSP>> Class<RES> getResponseClass();

}
