/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.job.targets.internal;

import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaUpdatableEntityJpaRepository;
import org.eclipse.kapua.service.job.targets.JobTarget;
import org.eclipse.kapua.service.job.targets.JobTargetListResult;
import org.eclipse.kapua.service.job.targets.JobTargetRepository;

public class JobTargetImplJpaRepository
        extends KapuaUpdatableEntityJpaRepository<JobTarget, JobTargetImpl, JobTargetListResult>
        implements JobTargetRepository {

    public JobTargetImplJpaRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        super(JobTargetImpl.class, JobTarget.TYPE, () -> new JobTargetListResult(), jpaRepoConfig);
    }

}
