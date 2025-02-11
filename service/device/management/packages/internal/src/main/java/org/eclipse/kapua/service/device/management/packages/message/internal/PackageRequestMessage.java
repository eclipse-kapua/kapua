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
package org.eclipse.kapua.service.device.management.packages.message.internal;

import org.eclipse.kapua.service.device.management.message.request.KapuaRequestMessage;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackage;

/**
 * {@link DevicePackage} {@link KapuaRequestMessage} implementation.
 *
 * @since 1.0.0
 */
public class PackageRequestMessage extends KapuaRequestMessage<PackageRequestChannel, PackageRequestPayload> {

    private static final long serialVersionUID = -2210763263102978350L;

    @Override
    public Class<PackageRequestMessage> getRequestClass() {
        return PackageRequestMessage.class;
    }

    @Override
    public Class<PackageResponseMessage> getResponseClass() {
        return PackageResponseMessage.class;
    }

}
