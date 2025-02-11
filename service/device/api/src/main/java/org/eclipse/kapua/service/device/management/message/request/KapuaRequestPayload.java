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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.message.KapuaPayload;

/**
 * {@link KapuaRequestMessage} {@link KapuaPayload} definition.
 * <p>
 * This object defines the payload for a Kapua request message. The request message is used to perform interactive operations with the device (e.g. to send command to the device, to ask
 * configurations...)
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "payload")
@XmlType
public class KapuaRequestPayload extends KapuaPayload {

    private static final long serialVersionUID = -7231317572182056724L;

}
