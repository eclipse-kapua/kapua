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
package org.eclipse.kapua.job.engine.jbatch;

import javax.batch.runtime.BatchRuntime;
import javax.inject.Singleton;

import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.job.engine.JobEngineService;
import org.eclipse.kapua.job.engine.jbatch.driver.JbatchDriver;
import org.eclipse.kapua.job.engine.jbatch.driver.utils.JobDefinitionBuildUtils;
import org.eclipse.kapua.job.engine.jbatch.setting.JobEngineSetting;
import org.eclipse.kapua.service.job.execution.JobExecutionService;
import org.eclipse.kapua.service.job.step.JobStepFactory;
import org.eclipse.kapua.service.job.step.JobStepService;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionService;

import com.google.inject.Provides;

public class JobengineJbatchModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(JobEngineService.class).to(JobEngineServiceJbatch.class).in(Singleton.class);
        bind(JobEngineSetting.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    JbatchDriver jbatchDriver(JobExecutionService jobExecutionService, JobStepService jobStepService, JobStepFactory jobStepFactory, JobStepDefinitionService jobStepDefinitionService,
            JobDefinitionBuildUtils jobDefinitionBuildUtils) {
        return new JbatchDriver(BatchRuntime.getJobOperator(), jobExecutionService, jobStepService, jobStepFactory, jobStepDefinitionService, jobDefinitionBuildUtils);
    }
}
