/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.keystore.internal.message.request;

import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystore;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestMessage;

/**
 * {@link DeviceKeystore} {@code abstract} {@link KapuaRequestMessage} implementation.
 *
 * @since 1.5.0
 */
public abstract class KeystoreRequestMessage<M extends KeystoreRequestMessage> extends KapuaRequestMessage<KeystoreRequestChannel, KeystoreRequestPayload> {

    private static final long serialVersionUID = 3593350285989405174L;

    private final Class<M> requestClass;

    /**
     * Constructor.
     *
     * @param requestClass
     *         The {@link KeystoreRequestMessage} sub-type
     * @since 1.5.0
     */
    protected KeystoreRequestMessage(Class<M> requestClass) {
        this.requestClass = requestClass;
    }

    @Override
    public Class<M> getRequestClass() {
        return requestClass;
    }
}
