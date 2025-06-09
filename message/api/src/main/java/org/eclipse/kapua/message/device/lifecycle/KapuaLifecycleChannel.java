/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.message.device.lifecycle;

import org.eclipse.kapua.message.KapuaChannel;

/**
 * {@link KapuaLifecycleChannel} definition
 *
 * @since 1.1.0
 */
public abstract class KapuaLifecycleChannel extends KapuaChannel {

    private String clientId;

    /**
     * Gets client id
     *
     * @return The client id
     * @since 1.1.0
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets client id
     *
     * @param clientId
     *         The client id
     * @since 1.1.0
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

}
