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
package org.eclipse.kapua.message.device.lifecycle;

/**
 * {@link KapuaDisconnectPayload} definition.
 *
 * @since 1.0.0
 */
public class KapuaDisconnectPayload extends KapuaLifecyclePayload {

    /**
     * Constructor.
     *
     * @since 1.1.0
     */
    public KapuaDisconnectPayload() {
        super();
    }

    /**
     * Constructor.
     * <p>
     * Sets all available properties of the {@link KapuaDisconnectPayload} at once.
     *
     * @param uptime
     *         The {@link KapuaDisconnectPayloadAttibutes#UPTIME}of the {@link KapuaDisconnectPayload}
     * @param displayName
     *         The {@link KapuaDisconnectPayloadAttibutes#DISPLAY_NAME} of the {@link KapuaDisconnectPayload}
     * @since 1.0.0
     */
    public KapuaDisconnectPayload(String uptime, String displayName) {
        setUptime(uptime);
        setDisplayName(displayName);
    }

    /**
     * Gets the device uptime.
     *
     * @return The device uptime.
     * @since 1.0.0
     */
    public String getUptime() {
        return (String) getMetrics().get(KapuaDisconnectPayloadAttibutes.UPTIME);
    }

    /**
     * Sets the device uptime.
     *
     * @param uptime
     *         The device uptime.
     * @since 1.1.0
     */
    public void setUptime(String uptime) {
        getMetrics().put(KapuaDisconnectPayloadAttibutes.UPTIME, uptime);
    }

    /**
     * Gets the device display name.
     *
     * @return The device display name.
     * @since 1.0.0
     */
    public String getDisplayName() {
        return (String) getMetrics().get(KapuaDisconnectPayloadAttibutes.DISPLAY_NAME);
    }

    /**
     * Sets the device display name.
     *
     * @param displayName
     *         The device display name
     * @since 1.1.0
     */
    public void setDisplayName(String displayName) {
        getMetrics().put(KapuaDisconnectPayloadAttibutes.DISPLAY_NAME, displayName);
    }

}
