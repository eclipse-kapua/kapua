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
package org.eclipse.kapua.service.device.management.packages.message.internal;

import org.eclipse.kapua.service.device.management.message.response.KapuaResponseChannel;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackage;

/**
 * {@link DevicePackage} {@link KapuaResponseChannel} implementation.
 *
 * @since 1.0.0
 */
public class PackageResponseChannel extends KapuaResponseChannel {

    private static final long serialVersionUID = 6807938372603652236L;

    /**
     * Constructor.
     *
     * @since 1.5.0
     */
    public PackageResponseChannel() {
        setAppName(PackageAppProperties.APP_NAME);
        setVersion(PackageAppProperties.APP_VERSION);
    }
}
