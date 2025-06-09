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
package org.eclipse.kapua.service.device.management.asset.store.dummy;

import java.util.Map;

import javax.inject.Singleton;

import org.eclipse.kapua.commons.service.internal.KapuaServiceDisabledException;
import org.eclipse.kapua.message.device.data.KapuaDataMessage;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.asset.DeviceAssets;
import org.eclipse.kapua.service.device.management.asset.store.DeviceAssetStoreService;
import org.eclipse.kapua.service.device.management.asset.store.settings.DeviceAssetStoreSettings;

/**
 * {@link DeviceAssetStoreService} implementation.
 *
 * @since 2.0.0
 */
@Singleton
public class DeviceAssetStoreServiceDummy implements DeviceAssetStoreService {

    @Override
    public DeviceAssets getAssets(KapuaId scopeId, KapuaId deviceId, DeviceAssets deviceAssets) throws KapuaServiceDisabledException {
        throw new KapuaServiceDisabledException(this.getClass().getName());
    }

    @Override
    public DeviceAssets getAssetsValues(KapuaId scopeId, KapuaId deviceId, DeviceAssets deviceAssets) throws KapuaServiceDisabledException {
        throw new KapuaServiceDisabledException(this.getClass().getName());
    }

    @Override
    public void storeAssets(KapuaId scopeId, KapuaId deviceId, DeviceAssets deviceAssets) throws KapuaServiceDisabledException {
        throw new KapuaServiceDisabledException(this.getClass().getName());
    }

    @Override
    public void storeAssetsValues(KapuaId scopeId, KapuaId deviceId, DeviceAssets deviceAssets) throws KapuaServiceDisabledException {
        throw new KapuaServiceDisabledException(this.getClass().getName());
    }

    @Override
    public void storeAssetValues(KapuaDataMessage message) throws KapuaServiceDisabledException {
        throw new KapuaServiceDisabledException(this.getClass().getName());
    }

    @Override
    public KapuaTocd getConfigMetadata(KapuaId scopeId) {
        return KapuaTocd.empty();
    }

    @Override
    public Map<String, Object> getConfigValues(KapuaId scopeId) {
        return null;
    }

    @Override
    public void setConfigValues(KapuaId scopeId, KapuaId parentId, Map<String, Object> values) {
    }

    @Override
    public DeviceAssetStoreSettings getApplicationSettings(KapuaId scopeId, KapuaId deviceId) throws KapuaServiceDisabledException {
        throw new KapuaServiceDisabledException(this.getClass().getName());
    }

    @Override
    public void setApplicationSettings(KapuaId scopeId, KapuaId deviceId, DeviceAssetStoreSettings deviceApplicationSettings) throws KapuaServiceDisabledException {
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