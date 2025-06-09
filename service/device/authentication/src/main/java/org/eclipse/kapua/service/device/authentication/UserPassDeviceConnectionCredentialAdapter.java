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

import org.eclipse.kapua.client.security.bean.AuthRequest;
import org.eclipse.kapua.service.authentication.LoginCredentials;
import org.eclipse.kapua.service.authentication.UsernamePasswordCredentials;
import org.eclipse.kapua.service.device.authentication.api.DeviceConnectionCredentialAdapter;

/**
 * {@link UsernamePasswordCredentials} {@link DeviceConnectionCredentialAdapter}
 *
 * @since 2.0.0
 */
public class UserPassDeviceConnectionCredentialAdapter implements DeviceConnectionCredentialAdapter {

    @Override
    public LoginCredentials mapToCredential(AuthRequest authRequest) {
        return new UsernamePasswordCredentials(
                authRequest.getUsername(),
                authRequest.getPassword());
    }
}
