/*******************************************************************************
 * Copyright (c) 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.app.settings;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.device.registry.Device;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link ByDeviceAppManagementSettings} definition.
 *
 * @since 2.0.0
 */
@Schema(description = "Base class for Device Management Applications that have per-device settings")
public interface ByDeviceAppManagementSettings {

    /**
     * Gets the {@link Device#getScopeId()}
     *
     * @return The {@link Device#getScopeId()}
     * @since 2.0.0
     */
    @XmlElement(name = "scopeId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    @Schema(example = "AQ")
    KapuaId getScopeId();

    /**
     * Sets the {@link Device#getScopeId()}
     *
     * @param kapuaId The {@link Device#getScopeId()}
     * @since 2.0.0
     */
    void setScopeId(KapuaId kapuaId);

    /**
     * Gets the {@link Device#getId()}
     *
     * @return The {@link Device#getId()}
     * @since 2.0.0
     */
    @XmlElement(name = "deviceId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    @Schema(example = "dIVxI5QpFUI")
    KapuaId getDeviceId();

    /**
     * Sets the {@link Device#getId()}
     *
     * @param deviceId The {@link Device#getId()}
     * @since 2.0.0
     */
    void setDeviceId(KapuaId deviceId);
}
