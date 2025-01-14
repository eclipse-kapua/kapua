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
package org.eclipse.kapua.app.console.module.job.server;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaErrorCode;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.job.shared.model.GwtJobStartOptions;
import org.eclipse.kapua.app.console.module.job.shared.service.GwtJobEngineService;
import org.eclipse.kapua.app.console.module.job.shared.util.GwtKapuaJobModelConverter;
import org.eclipse.kapua.job.engine.JobEngineFactory;
import org.eclipse.kapua.job.engine.JobEngineService;
import org.eclipse.kapua.job.engine.JobStartOptions;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.service.job.execution.JobExecutionAttributes;
import org.eclipse.kapua.service.job.execution.JobExecutionFactory;
import org.eclipse.kapua.service.job.execution.JobExecutionListResult;
import org.eclipse.kapua.service.job.execution.JobExecutionQuery;
import org.eclipse.kapua.service.job.execution.JobExecutionService;

public class GwtJobEngineServiceImpl extends KapuaRemoteServiceServlet implements GwtJobEngineService {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final JobEngineService JOB_ENGINE_SERVICE = LOCATOR.getService(JobEngineService.class);
    private static final JobEngineFactory JOB_ENGINE_FACTORY = LOCATOR.getFactory(JobEngineFactory.class);

    private static final JobExecutionService JOB_EXECUTION_SERVICE = LOCATOR.getService(JobExecutionService.class);
    private static final JobExecutionFactory JOB_EXECUTION_FACTORY = LOCATOR.getFactory(JobExecutionFactory.class);

    @Override
    public void start(String gwtScopeId, String gwtJobId) throws GwtKapuaException {
        start(gwtScopeId, gwtJobId, null);
    }

    @Override
    public void start(String gwtScopeId, String gwtJobId, GwtJobStartOptions gwtJobStartOptions) throws GwtKapuaException {

        KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtScopeId);
        KapuaId jobId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobId);

        try {
            JobStartOptions jobStartOptions;
            if (gwtJobStartOptions != null) {
                jobStartOptions = GwtKapuaJobModelConverter.convertJobStartOptions(gwtJobStartOptions);
            } else {
                jobStartOptions = JOB_ENGINE_FACTORY.newJobStartOptions();

            }

            jobStartOptions.setEnqueue(true);

            JOB_ENGINE_SERVICE.startJob(scopeId, jobId, jobStartOptions);
        } catch (KapuaException kaex) {
            KapuaExceptionHandler.handle(kaex);
        }
    }

    @Override
    public void stop(String gwtScopeId, String gwtJobId) throws GwtKapuaException {
        stopExecution(gwtScopeId, gwtJobId, null);
    }

    @Override
    public void stopExecution(String gwtScopeId, String gwtJobId, String gwtJobExecutionId) throws GwtKapuaException {
        KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtScopeId);
        KapuaId jobId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobId);
        KapuaId jobExecutionId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobExecutionId);

        try {
            if (jobExecutionId == null) {
                //TODO: #LAYER_VIOLATION - job execution lookup should not be done here (horribly inefficient)
                JobExecutionQuery query = new JobExecutionQuery(scopeId);

                query.setPredicate(
                        query.andPredicate(
                                query.attributePredicate(JobExecutionAttributes.JOB_ID, jobId),
                                query.attributePredicate(JobExecutionAttributes.ENDED_ON, null)
                        )
                );

                query.setSortCriteria(query.fieldSortCriteria(JobExecutionAttributes.STARTED_ON, SortOrder.DESCENDING));
                query.setLimit(1);

                JobExecutionListResult jobExecutions = JOB_EXECUTION_SERVICE.query(query);

                if (!jobExecutions.isEmpty()) {
                    jobExecutionId = jobExecutions.getFirstItem().getId();
                } else {
                    throw new GwtKapuaException(GwtKapuaErrorCode.JOB_STOPPING_ERROR, null, gwtScopeId, gwtJobId);
                }
            }

            JOB_ENGINE_SERVICE.stopJobExecution(scopeId, jobId, jobExecutionId);
        } catch (KapuaException kaex) {
            KapuaExceptionHandler.handle(kaex);
        }
    }

    @Override
    public void restart(String gwtScopeId, String gwtJobId) throws GwtKapuaException {
        restart(gwtScopeId, gwtJobId, null);
    }

    @Override
    public void restart(String gwtScopeId, String gwtJobId, String gwtJobTargetId) throws GwtKapuaException {
        GwtJobStartOptions gwtJobStartOptions = new GwtJobStartOptions();
        gwtJobStartOptions.setResetStepIndex(true);
        gwtJobStartOptions.setFromStepIndex(0);
        if (gwtJobTargetId == null) {
            gwtJobStartOptions.setTargetIdSublist(new ArrayList<String>());
        } else {
            gwtJobStartOptions.setTargetIdSublist(Collections.singletonList(gwtJobTargetId));
        }
        start(gwtScopeId, gwtJobId, gwtJobStartOptions);
    }
}
