/*******************************************************************************
 * Copyright (c) 2016, 2022  Eurotech and/or its affiliates and others
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
 * {@link KapuaAppsPayload} definition.
 *
 * @since 1.0.0
 */
public class KapuaAppsPayload extends KapuaBirthPayload {

    private static final long serialVersionUID = -918081814625264739L;

    /**
     * Constructor.
     *
     * @since 1.1.0
     */
    public KapuaAppsPayload() {
        super();
    }

    /**
     * Constructor.
     *
     * @see KapuaBirthPayload
     */
    public KapuaAppsPayload(String uptime,
            String displayName,
            String modelName,
            String modelId,
            String partNumber,
            String serialNumber,
            String firmware,
            String firmwareVersion,
            String bios,
            String biosVersion,
            String os,
            String osVersion,
            String jvm,
            String jvmVersion,
            String jvmProfile,
            String containerFramework,
            String containerFrameworkVersion,
            String applicationFramework,
            String applicationFrameworkVersion,
            String connectionInterface,
            String connectionIp,
            String acceptEncoding,
            String applicationIdentifiers,
            String availableProcessors,
            String totalMemory,
            String osArch,
            String modemImei,
            String modemImsi,
            String modemIccid,
            String extendedProperties,
            String tamperStatus) {
        super(uptime,
                displayName,
                modelName,
                modelId,
                partNumber,
                serialNumber,
                firmware,
                firmwareVersion,
                bios,
                biosVersion,
                os,
                osVersion,
                jvm,
                jvmVersion,
                jvmProfile,
                containerFramework,
                containerFrameworkVersion,
                applicationFramework,
                applicationFrameworkVersion,
                connectionInterface,
                connectionIp,
                acceptEncoding,
                applicationIdentifiers,
                availableProcessors,
                totalMemory,
                osArch,
                modemImei,
                modemImsi,
                modemIccid,
                extendedProperties,
                tamperStatus);
    }
}
