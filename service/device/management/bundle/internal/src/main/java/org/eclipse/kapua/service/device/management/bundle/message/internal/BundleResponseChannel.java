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
package org.eclipse.kapua.service.device.management.bundle.message.internal;

import org.eclipse.kapua.service.device.management.bundle.DeviceBundle;
import org.eclipse.kapua.service.device.management.bundle.internal.DeviceBundleAppProperties;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponseChannel;

/**
 * {@link DeviceBundle} {@link KapuaResponseChannel} implementation.
 *
 * @since 1.0.0
 */
public class BundleResponseChannel extends KapuaResponseChannel {

    private static final long serialVersionUID = 7627283688891772616L;

    /**
     * Constructor.
     *
     * @since 1.5.0
     */
    public BundleResponseChannel() {
        setAppName(DeviceBundleAppProperties.APP_NAME);
        setVersion(DeviceBundleAppProperties.APP_VERSION);
    }
}
