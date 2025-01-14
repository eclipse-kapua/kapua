/*******************************************************************************
 * Copyright (c) 2021, 2024 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.job.steps;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.kapua.qa.common.TestBase;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.job.JobAttributes;
import org.eclipse.kapua.service.job.JobFactory;
import org.eclipse.kapua.service.job.JobQuery;
import org.eclipse.kapua.service.job.JobService;

public class JobServiceTestBase extends TestBase {

    // Job
    protected static final String JOB_NAME = "jobName";
    protected static final String TEST_JOB = "Test job";
    protected static final String CURRENT_JOB_ID = "CurrentJobId";
    protected static final String JOB = "Job";
    protected static final String JOB_CREATOR = "JobCreator";

    // Job Execution
    protected static final String JOB_EXECUTION = "JobExecution";
    protected static final String JOB_EXECUTION_LIST = "JobExecutionList";

    // Job Step Definition
    protected static final String CURRENT_JOB_STEP_DEFINITION_ID = "CurrentJobStepDefinitionId";
    protected static final String JOB_STEP_DEFINITION_CREATOR = "JobStepDefinitionCreator";
    protected static final String JOB_STEP_DEFINITION = "JobStepDefinition";
    protected static final String JOB_STEP_DEFINITIONS = "JobStepDefinitions";

    // Job Step
    protected static final String CURRENT_JOB_STEP_ID = "CurrentJobStepId";
    protected static final String JOB_STEP = "JobStep";
    protected static final String JOB_STEPS = "JobSteps";
    protected static final String JOB_STEP_CREATOR = "JobStepCreator";
    protected static final String JOB_STEP_CREATORS = "JobStepCreators";

    // Job Target
    protected static final String JOB_TARGET_CREATOR = "JobTargetCreator";
    protected static final String JOB_TARGET = "JobTarget";
    protected static final String JOB_TARGET_LIST = "JobTargetList";

    private JobService jobService;

    private JobFactory jobFactory;

    protected JobServiceTestBase(StepData stepData) {
        super(stepData);

        KapuaLocator locator = KapuaLocator.getInstance();

        jobService = locator.getService(JobService.class);
        jobFactory = locator.getFactory(JobFactory.class);
    }

    public Job findJob(String jobName) throws Exception {
        JobQuery jobQuery = new JobQuery(getCurrentScopeId());
        jobQuery.setPredicate(
                jobQuery.attributePredicate(JobAttributes.NAME, jobName)
        );

        Job job = jobService.query(jobQuery).getFirstItem();

        if (job == null) {
            throw new KapuaEntityNotFoundException(Job.TYPE, jobName);
        }

        return job;
    }
}
