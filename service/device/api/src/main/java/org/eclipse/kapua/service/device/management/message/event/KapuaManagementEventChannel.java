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
package org.eclipse.kapua.service.device.management.message.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.eclipse.kapua.service.device.management.message.KapuaAppChannel;
import org.eclipse.kapua.service.device.management.message.KapuaEventChannel;

/**
 * Device event {@link KapuaAppChannel} definition.
 *
 * @since 2.0.0
 */
public class KapuaManagementEventChannel extends KapuaEventChannel {

    private String appName;

    private String appVersion;
    private String[] resources;

    /**
     * Gets the device application name.
     *
     * @return The device application name.
     * @since 2.0.0
     */
    @XmlElement(name = "appName")
    public String getAppName() {
        return appName;
    }

    /**
     * Sets the device application name.
     *
     * @param appName
     *         The device application name.
     * @since 2.0.0
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * Gets the device application version.
     *
     * @return The device application version.
     * @since 2.0.0
     */
    @XmlElement(name = "appVersion")
    public String getAppVersion() {
        return appVersion;
    }

    /**
     * Sets the device application version.
     *
     * @param appVersion
     *         The device application version.
     * @since 2.0.0
     */
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    /**
     * Get the request resources
     *
     * @return
     * @since 2.0.0
     */
    public String[] getResources() {
        return resources;
    }

    /**
     * Set the request resources
     *
     * @param resources
     * @since 2.0.0
     */
    public void setResources(String[] resources) {
        this.resources = resources;
    }

    @Override
    public List<String> getSemanticParts() {
        List<String> semanticParts = new ArrayList<>();
        semanticParts.add(getAppName());
        semanticParts.add(getAppVersion());

        if (getResources() != null) {
            semanticParts.addAll(Arrays.asList(getResources()));
        }

        return semanticParts;
    }

    @Override
    public void setSemanticParts(List<String> semanticParts) {
        throw new UnsupportedOperationException();
    }

}
