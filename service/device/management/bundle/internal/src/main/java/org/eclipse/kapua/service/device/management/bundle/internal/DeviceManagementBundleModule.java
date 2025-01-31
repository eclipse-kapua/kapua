/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.bundle.internal;

import javax.inject.Singleton;

import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.jpa.KapuaJpaTxManagerFactory;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.model.domain.DomainEntry;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.device.management.DeviceManagementService;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundleFactory;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundleManagementService;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;

import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;

public class DeviceManagementBundleModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(DeviceBundleFactory.class).to(DeviceBundleFactoryImpl.class);
    }

    @ProvidesIntoSet
    public Domain deviceManagementModule() {
        return new DomainEntry(Domains.DEVICE_MANAGEMENT, DeviceManagementService.class.getName(), false, Actions.execute, Actions.read, Actions.write);
    }

    @Provides
    @Singleton
    DeviceBundleManagementService deviceBundleManagementService(
            AuthorizationService authorizationService,
            DeviceEventService deviceEventService,
            DeviceEventFactory deviceEventFactory,
            DeviceRegistryService deviceRegistryService,
            KapuaJpaTxManagerFactory jpaTxManagerFactory,
            DeviceBundleFactory deviceBundleFactory) {
        return new DeviceBundleManagementServiceImpl(
                jpaTxManagerFactory.create("kapua-device_management_operation_registry"),
                authorizationService,
                deviceEventService,
                deviceEventFactory,
                deviceRegistryService,
                deviceBundleFactory);
    }
}
