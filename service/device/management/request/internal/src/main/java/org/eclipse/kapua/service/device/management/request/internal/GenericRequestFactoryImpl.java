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
package org.eclipse.kapua.service.device.management.request.internal;

import javax.inject.Singleton;

import org.eclipse.kapua.service.device.management.request.GenericRequestFactory;
import org.eclipse.kapua.service.device.management.request.message.request.GenericRequestMessage;
import org.eclipse.kapua.service.device.management.request.message.response.GenericResponseMessage;

@Singleton
public class GenericRequestFactoryImpl implements GenericRequestFactory {

    @Override
    public GenericRequestMessage newRequestMessage() {
        return new GenericRequestMessage();
    }

    @Override
    public GenericResponseMessage newResponseMessage() {
        return new GenericResponseMessage();
    }
}
