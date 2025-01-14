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
package org.eclipse.kapua.service.job.step.internal;

import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.job.execution.JobExecutionFactory;
import org.eclipse.kapua.service.job.execution.JobExecutionService;
import org.eclipse.kapua.service.job.step.JobStepFactory;
import org.eclipse.kapua.service.job.step.JobStepRepository;
import org.eclipse.kapua.service.job.step.JobStepService;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionRepository;
import org.eclipse.kapua.storage.TxManager;

import com.google.inject.Provides;

public class JobStepModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(JobStepFactory.class).to(JobStepFactoryImpl.class);
    }

    @Provides
    @Singleton
    JobStepRepository jobStepRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        return new JobStepImplJpaRepository(jpaRepoConfig);
    }

    @Provides
    @Singleton
    JobStepService jobStepService(AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            @Named("jobTxManager") TxManager txManager,
            JobStepRepository jobStepRepository,
            JobStepFactory jobStepFactory,
            JobExecutionService jobExecutionService,
            JobExecutionFactory jobExecutionFactory,
            JobStepDefinitionRepository jobStepDefinitionRepository,
            XmlUtil xmlUtil) {
        return new JobStepServiceImpl(authorizationService,
                permissionFactory,
                txManager,
                jobStepRepository,
                jobStepFactory,
                jobExecutionService,
                jobExecutionFactory,
                jobStepDefinitionRepository,
                xmlUtil
        );
    }
}
