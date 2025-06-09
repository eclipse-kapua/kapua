/*******************************************************************************
 * Copyright (c) 2023, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.authentication;

import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.service.device.authentication.api.DeviceConnectionCredentialAdapter;

import com.google.inject.multibindings.ProvidesIntoMap;
import com.google.inject.multibindings.StringMapKey;

/**
 * kapua-device-authentication {@link AbstractKapuaModule}.
 *
 * @since 2.0.0
 */
public class DeviceAuthenticationModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
    }

    @ProvidesIntoMap
    @StringMapKey("USER_PASS")
    public DeviceConnectionCredentialAdapter userPassDeviceConnectionAuthHandler() {
        return new UserPassDeviceConnectionCredentialAdapter();
    }
}
