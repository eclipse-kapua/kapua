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
package org.eclipse.kapua.service.device.management.job.internal;

import javax.inject.Singleton;

import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaJpaTxManagerFactory;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperationFactory;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperationRepository;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperationService;

import com.google.inject.Provides;

public class DeviceManagementJobModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(JobDeviceManagementOperationFactory.class).to(JobDeviceManagementOperationFactoryImpl.class);
    }

    @Provides
    @Singleton
    public JobDeviceManagementOperationService jobDeviceManagementOperationService(
            JobDeviceManagementOperationFactory entityFactory,
            AuthorizationService authorizationService,
            JobDeviceManagementOperationRepository repository,
            KapuaJpaTxManagerFactory jpaTxManagerFactory) {
        return new JobDeviceManagementOperationServiceImpl(entityFactory,
                authorizationService,
                jpaTxManagerFactory.create("kapua-job-device-management-operation"),
                repository);
    }

    @Provides
    @Singleton
    JobDeviceManagementOperationRepository jobDeviceManagementOperationRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        return new JobDeviceManagementOperationImplJpaRepository(jpaRepoConfig);
    }
}
