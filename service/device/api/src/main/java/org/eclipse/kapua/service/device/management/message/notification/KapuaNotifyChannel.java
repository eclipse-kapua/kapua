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
package org.eclipse.kapua.service.device.management.message.notification;

import org.eclipse.kapua.service.device.management.message.KapuaAppChannel;

/**
 * Notify {@link KapuaAppChannel} definition.
 *
 * @since 1.0.0
 */
public class KapuaNotifyChannel extends KapuaAppChannel {

    private static final long serialVersionUID = 8630706854304183497L;

    private String[] resources;

    /**
     * Get the request resources
     *
     * @return
     * @since 1.2.0
     */
    public String[] getResources() {
        return resources;
    }

    /**
     * Set the request resources
     *
     * @param resources
     * @since 1.2.0
     */
    public void setResources(String[] resources) {
        this.resources = resources;
    }

}
