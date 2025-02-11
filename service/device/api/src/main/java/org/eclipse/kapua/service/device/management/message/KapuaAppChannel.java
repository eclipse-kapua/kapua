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
package org.eclipse.kapua.service.device.management.message;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * Device Application {@link KapuaControlChannel} definition.
 * <p>
 * This object defines the common channel behavior for a Kapua request or response message.<br> The request message is used to perform interactive operations with the device (e.g. to send command to
 * the device, to ask configurations...)
 *
 * @since 1.0.0
 */
public abstract class KapuaAppChannel extends KapuaControlChannel {

    private static final long serialVersionUID = 3755325191379812125L;

    private List<String> semanticParts;
    private KapuaAppProperties appName;
    private KapuaAppProperties appVersion;

    /**
     * Gets the device application name.
     *
     * @return The device application name.
     * @since 1.0.0
     */
    @XmlElement(name = "appName")
    public KapuaAppProperties getAppName() {
        return appName;
    }

    /**
     * Sets the device application name.
     *
     * @param appName
     *         The device application name.
     * @since 1.0.0
     */
    public void setAppName(KapuaAppProperties appName) { // do I have to keep it as a KapuaAppProperties?
        this.appName = appName;
    }

    /**
     * Gets the device application version.
     *
     * @return The device application version.
     * @since 1.0.0
     */
    @XmlElement(name = "version")
    public KapuaAppProperties getVersion() {
        return appVersion;
    }

    /**
     * Sets the device application version.
     *
     * @param appVersion
     *         The device application version.
     * @since 1.0.0
     */
    public void setVersion(KapuaAppProperties appVersion) {
        this.appVersion = appVersion;
    }

}
