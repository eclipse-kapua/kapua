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
package org.eclipse.kapua.service.device.management.configuration.store.dummy;

import java.util.Map;

import javax.inject.Singleton;

import org.eclipse.kapua.commons.service.internal.KapuaServiceDisabledException;
import org.eclipse.kapua.model.config.metatype.EmptyTocd;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.store.DeviceConfigurationStoreService;
import org.eclipse.kapua.service.device.management.configuration.store.settings.DeviceConfigurationStoreSettings;

/**
 * {@link DeviceConfigurationStoreService} dummy implementation.
 *
 * @since 2.0.0
 */
@Singleton
public class DeviceConfigurationStoreServiceDummy implements DeviceConfigurationStoreService {

    @Override
    public DeviceComponentConfiguration getConfigurations(KapuaId scopeId, KapuaId deviceId, String configurationComponentPid) throws KapuaServiceDisabledException {
        throw new KapuaServiceDisabledException(this.getClass().getName());
    }

    @Override
    public DeviceConfiguration getConfigurations(KapuaId scopeId, KapuaId deviceId) throws KapuaServiceDisabledException {
        throw new KapuaServiceDisabledException(this.getClass().getName());
    }

    @Override
    public void storeConfigurations(KapuaId scopeId, KapuaId deviceId, DeviceComponentConfiguration deviceComponentConfiguration) throws KapuaServiceDisabledException {
        throw new KapuaServiceDisabledException(this.getClass().getName());
    }

    @Override
    public void storeConfigurations(KapuaId scopeId, KapuaId deviceId, DeviceConfiguration deviceConfiguration) throws KapuaServiceDisabledException {
        throw new KapuaServiceDisabledException(this.getClass().getName());
    }

    @Override
    public KapuaTocd getConfigMetadata(KapuaId scopeId) {
        return new EmptyTocd();
    }

    @Override
    public Map<String, Object> getConfigValues(KapuaId scopeId) {
        return null;
    }

    @Override
    public void setConfigValues(KapuaId scopeId, KapuaId parentId, Map<String, Object> values) {
    }

    @Override
    public DeviceConfigurationStoreSettings getApplicationSettings(KapuaId scopeId, KapuaId deviceId) throws KapuaServiceDisabledException {
        throw new KapuaServiceDisabledException(this.getClass().getName());
    }

    @Override
    public void setApplicationSettings(KapuaId scopeId, KapuaId deviceId, DeviceConfigurationStoreSettings deviceApplicationSettings) throws KapuaServiceDisabledException {
        throw new KapuaServiceDisabledException(this.getClass().getName());
    }

    @Override
    public boolean isServiceEnabled(KapuaId scopeId) {
        return false;
    }

    @Override
    public boolean isApplicationEnabled(KapuaId scopeId, KapuaId deviceId) {
        return false;
    }
}