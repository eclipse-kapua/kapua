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
package org.eclipse.kapua.service.device.management.asset.store.settings;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.asset.store.DeviceAssetStoreService;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link DeviceAssetStoreEnablementPolicy} definition.
 *
 * @since 2.0.0
 */
@Schema(description = "The policy for which the Device Asset Store Application is enabled or not for this device. 'INHERITED' means that it inherits the setting from the Account-wide Asset Store Service Configuration")
public enum DeviceAssetStoreEnablementPolicy {

    /**
     * The {@link DeviceAssetStoreService} is enabled.
     *
     * @since 2.0.0
     */
    ENABLED,

    /**
     * The {@link DeviceAssetStoreService} is disabled.
     *
     * @since 2.0.0
     */
    DISABLED,

    /**
     * The {@link DeviceAssetStoreService} is resolved to the {@link DeviceAssetStoreService#getConfigValues(KapuaId)} setting.
     *
     * @since 2.0.0
     */
    INHERITED
}
