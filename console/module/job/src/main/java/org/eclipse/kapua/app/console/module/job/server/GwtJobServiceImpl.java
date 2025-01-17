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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtGroupedNVPair;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.job.shared.model.GwtJob;
import org.eclipse.kapua.app.console.module.job.shared.model.GwtJobCreator;
import org.eclipse.kapua.app.console.module.job.shared.model.GwtJobQuery;
import org.eclipse.kapua.app.console.module.job.shared.service.GwtJobService;
import org.eclipse.kapua.app.console.module.job.shared.util.GwtKapuaJobModelConverter;
import org.eclipse.kapua.app.console.module.job.shared.util.KapuaGwtJobModelConverter;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.job.JobCreator;
import org.eclipse.kapua.service.job.JobFactory;
import org.eclipse.kapua.service.job.JobListResult;
import org.eclipse.kapua.service.job.JobQuery;
import org.eclipse.kapua.service.job.JobService;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserFactory;
import org.eclipse.kapua.service.user.UserListResult;
import org.eclipse.kapua.service.user.UserQuery;
import org.eclipse.kapua.service.user.UserService;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class GwtJobServiceImpl extends KapuaRemoteServiceServlet implements GwtJobService {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final JobService JOB_SERVICE = LOCATOR.getService(JobService.class);
    private static final JobFactory JOB_FACTORY = LOCATOR.getFactory(JobFactory.class);

    private static final UserService USER_SERVICE = LOCATOR.getService(UserService.class);
    private static final UserFactory USER_FACTORY = LOCATOR.getFactory(UserFactory.class);

    private static final String ENTITY_INFO = "entityInfo";

    @Override
    public PagingLoadResult<GwtJob> query(PagingLoadConfig loadConfig, GwtJobQuery gwtJobQuery) throws GwtKapuaException {
        // Do query
        int totalLength = 0;
        List<GwtJob> gwtJobs = new ArrayList<GwtJob>();
        try {

            // Convert from GWT entity
            JobQuery jobQuery = GwtKapuaJobModelConverter.convertJobQuery(gwtJobQuery, loadConfig);

            // query
            JobListResult jobs = JOB_SERVICE.query(jobQuery);
            totalLength = jobs.getTotalCount().intValue();

            // If there are results
            if (!jobs.isEmpty()) {
                //TODO: #LAYER_VIOLATION - user lookup should not be done here (horribly inefficient)
                UserListResult usernames = KapuaSecurityUtils.doPrivileged(new Callable<UserListResult>() {

                    @Override
                    public UserListResult call() throws Exception {
                        return USER_SERVICE.query(new UserQuery(null));
                    }
                });

                Map<String, String> usernameMap = new HashMap<String, String>();
                for (User user : usernames.getItems()) {
                    usernameMap.put(user.getId().toCompactId(), user.getName());
                }

                // Converto to GWT entity
                for (Job j : jobs.getItems()) {
                    GwtJob gwtJob = KapuaGwtJobModelConverter.convertJob(j);
                    gwtJob.setCreatedByName(usernameMap.get(j.getCreatedBy().toCompactId()));
                    gwtJobs.add(gwtJob);
                }
            }

        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return new BasePagingLoadResult<GwtJob>(gwtJobs, loadConfig.getOffset(), totalLength);
    }

    @Override
    public GwtJob create(GwtXSRFToken xsrfToken, GwtJobCreator gwtJobCreator) throws GwtKapuaException {
        checkXSRFToken(xsrfToken);

        GwtJob gwtJob = null;
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtJobCreator.getScopeId());
            JobCreator jobCreator = new JobCreator(scopeId);
            jobCreator.setName(gwtJobCreator.getName());
            jobCreator.setDescription(gwtJobCreator.getDescription());
            // Create the Job
            Job job = JOB_SERVICE.create(jobCreator);

            // convert to GwtJob and return
            gwtJob = KapuaGwtJobModelConverter.convertJob(job);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return gwtJob;
    }

    @Override
    public GwtJob find(String accountId, String jobIdString) throws GwtKapuaException {
        KapuaId scopeId = KapuaEid.parseCompactId(accountId);
        KapuaId jobId = KapuaEid.parseCompactId(jobIdString);

        GwtJob gwtJob = null;
        try {
            Job job = JOB_SERVICE.find(scopeId, jobId);

            if (job != null) {
                gwtJob = KapuaGwtJobModelConverter.convertJob(job);
            }
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return gwtJob;
    }

    @Override
    public GwtJob update(GwtXSRFToken xsrfToken, GwtJob gwtJob) throws GwtKapuaException {
        checkXSRFToken(xsrfToken);

        GwtJob gwtJobUpdated = null;
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtJob.getScopeId());
            KapuaId jobId = KapuaEid.parseCompactId(gwtJob.getId());

            Job job = JOB_SERVICE.find(scopeId, jobId);

            if (job != null) {
                // Update job
                job.setName(gwtJob.getJobName());
                job.setDescription(gwtJob.getUnescapedDescription());

                // optlock
                job.setOptlock(gwtJob.getOptlock());

                // update the job
                gwtJobUpdated = KapuaGwtJobModelConverter.convertJob(JOB_SERVICE.update(job));
            }
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
        return gwtJobUpdated;

    }

    @Override
    public void delete(GwtXSRFToken xsrfToken, String gwtScopeId, String gwtJobId) throws GwtKapuaException {
        checkXSRFToken(xsrfToken);

        try {
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtScopeId);
            KapuaId jobId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobId);

            JOB_SERVICE.delete(scopeId, jobId);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
    }

    @Override
    public void deleteForced(GwtXSRFToken xsrfToken, String gwtScopeId, String gwtJobId) throws GwtKapuaException {
        checkXSRFToken(xsrfToken);

        try {
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtScopeId);
            KapuaId jobId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobId);

            JOB_SERVICE.deleteForced(scopeId, jobId);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
    }

    @Override
    public ListLoadResult<GwtGroupedNVPair> findJobDescription(String gwtScopeId,
            String gwtJobId) throws GwtKapuaException {
        List<GwtGroupedNVPair> gwtJobDescription = new ArrayList<GwtGroupedNVPair>();
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtScopeId);
            KapuaId jobId = KapuaEid.parseCompactId(gwtJobId);

            Job job = JOB_SERVICE.find(scopeId, jobId);

            UserListResult userListResult = KapuaSecurityUtils.doPrivileged(new Callable<UserListResult>() {

                @Override
                public UserListResult call() throws Exception {
                    return USER_SERVICE.query(new UserQuery(null));
                }
            });

            Map<String, String> usernameMap = new HashMap<String, String>();
            for (User user : userListResult.getItems()) {
                usernameMap.put(user.getId().toCompactId(), user.getName());
            }

            if (job != null) {
                gwtJobDescription.add(new GwtGroupedNVPair("jobInfo", "jobName", job.getName()));
                gwtJobDescription.add(new GwtGroupedNVPair("jobInfo", "jobDescription", job.getDescription()));
                gwtJobDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "jobCreatedOn", job.getCreatedOn()));
                gwtJobDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "jobCreatedBy", job.getCreatedBy() != null ? usernameMap.get(job.getCreatedBy().toCompactId()) : null));
                gwtJobDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "jobModifiedOn", job.getModifiedOn()));
                gwtJobDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "jobModifiedBy", job.getModifiedBy() != null ? usernameMap.get(job.getModifiedBy().toCompactId()) : null));
            }
        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }

        return new BaseListLoadResult<GwtGroupedNVPair>(gwtJobDescription);
    }
}
