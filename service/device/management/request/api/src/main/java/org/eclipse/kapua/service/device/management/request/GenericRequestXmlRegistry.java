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
package org.eclipse.kapua.service.device.management.request;

import javax.xml.bind.annotation.XmlRegistry;

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.device.management.request.message.request.GenericRequestMessage;
import org.eclipse.kapua.service.device.management.request.message.response.GenericResponseMessage;

@XmlRegistry
public class GenericRequestXmlRegistry {

    private final GenericRequestFactory genericRequestFactory = KapuaLocator.getInstance().getFactory(GenericRequestFactory.class);

    public GenericRequestMessage newRequestMessage() {
        return genericRequestFactory.newRequestMessage();
    }

    public GenericResponseMessage newResponseMessage() {
        return genericRequestFactory.newResponseMessage();
    }
}
