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
package org.eclipse.kapua.service.device.management.packages.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.core.JaxbClassProvider;
import org.eclipse.kapua.commons.core.SimpleJaxbClassProvider;
import org.eclipse.kapua.commons.jpa.KapuaJpaTxManagerFactory;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.device.management.packages.DevicePackageFactory;
import org.eclipse.kapua.service.device.management.packages.DevicePackageManagementService;
import org.eclipse.kapua.service.device.management.packages.internal.setting.PackageManagementServiceSetting;
import org.eclipse.kapua.service.device.management.packages.model.install.DevicePackageInstallRequest;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationFactory;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationRegistryService;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;

import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;

public class DeviceManagementPackagesModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(DevicePackageFactory.class).to(DevicePackageFactoryImpl.class).in(Singleton.class);
        bind(PackageManagementServiceSetting.class).in(Singleton.class);
        final Multibinder<JaxbClassProvider> jaxbClassProviderMultibinder = Multibinder.newSetBinder(binder(), JaxbClassProvider.class);
        jaxbClassProviderMultibinder.addBinding()
                .toInstance(new SimpleJaxbClassProvider(
                                DevicePackageInstallRequest.class
                        )
                );
    }

    @Provides
    @Inject
    DevicePackageManagementService devicePackageManagementService(
            AuthorizationService authorizationService,
            DeviceEventService deviceEventService,
            DeviceEventFactory deviceEventFactory,
            DeviceRegistryService deviceRegistryService,
            DeviceManagementOperationRegistryService deviceManagementOperationRegistryService,
            DeviceManagementOperationFactory deviceManagementOperationFactory,
            DevicePackageFactory devicePackageFactory,
            KapuaJpaTxManagerFactory jpaTxManagerFactory,
            PackageManagementServiceSetting packageManagementServiceSetting
    ) {
        return new DevicePackageManagementServiceImpl(
                jpaTxManagerFactory.create("kapua-device_management_operation_registry"),
                authorizationService,
                deviceEventService,
                deviceEventFactory,
                deviceRegistryService,
                deviceManagementOperationRegistryService,
                deviceManagementOperationFactory,
                devicePackageFactory,
                packageManagementServiceSetting
        );
    }
}
