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
package org.eclipse.kapua.service.device.management.registry.operation.notification.internal;

import javax.inject.Singleton;

import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaJpaTxManagerFactory;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.device.management.registry.operation.DeviceManagementOperationRepository;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotificationFactory;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotificationRepository;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotificationService;

import com.google.inject.Provides;

public class DeviceManagementRegistryNotificationModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(ManagementOperationNotificationFactory.class).to(ManagementOperationNotificationFactoryImpl.class);
    }

    @Provides
    @Singleton
    ManagementOperationNotificationService managementOperationNotificationService(
            AuthorizationService authorizationService,
            ManagementOperationNotificationFactory entityFactory,
            ManagementOperationNotificationRepository repository,
            DeviceManagementOperationRepository deviceManagementOperationRepository,
            KapuaJpaTxManagerFactory jpaTxManagerFactory) {
        return new ManagementOperationNotificationServiceImpl(
                authorizationService,
                entityFactory,
                jpaTxManagerFactory.create("kapua-device_management_operation_registry"),
                repository,
                deviceManagementOperationRepository
        );
    }

    @Provides
    @Singleton
    ManagementOperationNotificationRepository managementOperationNotificationRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        return new ManagementOperationNotificationImplJpaRepository(jpaRepoConfig);
    }

}
