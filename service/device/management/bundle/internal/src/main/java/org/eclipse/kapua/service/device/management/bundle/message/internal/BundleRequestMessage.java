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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.service.device.management.bundle.message.internal;

import org.eclipse.kapua.service.device.management.bundle.DeviceBundle;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestMessage;

/**
 * {@link DeviceBundle} {@link KapuaRequestMessage} implementation.
 *
 * @since 1.0.0
 */
public class BundleRequestMessage extends KapuaRequestMessage<BundleRequestChannel, BundleRequestPayload> {

    private static final long serialVersionUID = -3275899725471413937L;

    @Override
    public Class<BundleRequestMessage> getRequestClass() {
        return BundleRequestMessage.class;
    }

    @Override
    public Class<BundleResponseMessage> getResponseClass() {
        return BundleResponseMessage.class;
    }

}
