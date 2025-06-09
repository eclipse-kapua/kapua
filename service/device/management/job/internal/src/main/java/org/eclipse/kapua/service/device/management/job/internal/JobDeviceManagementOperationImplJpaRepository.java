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

import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaUpdatableEntityJpaRepository;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperation;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperationListResult;
import org.eclipse.kapua.service.device.management.job.JobDeviceManagementOperationRepository;

public class JobDeviceManagementOperationImplJpaRepository
        extends KapuaUpdatableEntityJpaRepository<JobDeviceManagementOperation, JobDeviceManagementOperationImpl, JobDeviceManagementOperationListResult>
        implements JobDeviceManagementOperationRepository {

    public JobDeviceManagementOperationImplJpaRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        super(JobDeviceManagementOperationImpl.class, JobDeviceManagementOperation.TYPE, () -> new JobDeviceManagementOperationListResult(), jpaRepoConfig);
    }
}
