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
package org.eclipse.kapua.service.job.step.definition.internal;

import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaNamedEntityJpaRepository;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinition;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionListResult;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionRepository;

public class JobStepDefinitionImplJpaRepository
        extends KapuaNamedEntityJpaRepository<JobStepDefinition, JobStepDefinitionImpl, JobStepDefinitionListResult>
        implements JobStepDefinitionRepository {

    public JobStepDefinitionImplJpaRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        super(JobStepDefinitionImpl.class, JobStepDefinition.TYPE, () -> new JobStepDefinitionListResult(), jpaRepoConfig);
    }

}
