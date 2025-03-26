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
package org.eclipse.kapua.job.engine.app.resources;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.rest.model.IsJobRunningMultipleResponse;
import org.eclipse.kapua.commons.rest.model.IsJobRunningResponse;
import org.eclipse.kapua.commons.rest.model.MultipleJobIdRequest;
import org.eclipse.kapua.job.engine.JobEngineService;
import org.eclipse.kapua.job.engine.JobStartOptions;
import org.eclipse.kapua.model.id.KapuaId;
import org.glassfish.jersey.process.internal.RequestScoped;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

@SuppressWarnings("RestParamTypeInspection")
@Path("/")
@RequestScoped
public class JobEngineResource {

    @Inject
    public JobEngineService jobEngineService;

    @POST
    @Path("clean-data/{scopeId}/{jobId}")
    public void cleanJobData(@PathParam("scopeId") KapuaId scopeId, @PathParam("jobId") KapuaId jobId) throws KapuaException {
        jobEngineService.cleanJobData(scopeId, jobId);
    }

    @GET
    @Path("is-running/{scopeId}/{jobId}")
    public IsJobRunningResponse isRunning(@PathParam("scopeId") KapuaId scopeId, @PathParam("jobId") KapuaId jobId) throws KapuaException {
        return new IsJobRunningResponse(jobId, jobEngineService.isRunning(scopeId, jobId));
    }

    @POST
    @Path("is-running/{scopeId}")
    public IsJobRunningMultipleResponse isRunning(@PathParam("scopeId") KapuaId scopeId, MultipleJobIdRequest jobIds) throws KapuaException {
        return new IsJobRunningMultipleResponse(jobEngineService.isRunning(scopeId, jobIds.getJobIds()));
    }

    @POST
    @Path("resume-execution/{scopeId}/{jobId}/{executionId}")
    public void resumeJobExecution(@PathParam("scopeId") KapuaId scopeId, @PathParam("jobId") KapuaId jobId, @PathParam("executionId") KapuaId executionId) throws KapuaException {
        jobEngineService.resumeJobExecution(scopeId, jobId, executionId);
    }

    @POST
    @Path("start/{scopeId}/{jobId}")
    public void startJob(@PathParam("scopeId") KapuaId scopeId, @PathParam("jobId") KapuaId jobId) throws KapuaException {
        jobEngineService.startJob(scopeId, jobId);
    }

    @POST
    @Path("start-with-options/{scopeId}/{jobId}")
    @Consumes({MediaType.APPLICATION_XML})
    public void startJob(@PathParam("scopeId") KapuaId scopeId, @PathParam("jobId") KapuaId jobId, JobStartOptions jobStartOptions) throws KapuaException {
        jobEngineService.startJob(scopeId, jobId, jobStartOptions);
    }

    @POST
    @Path("stop/{scopeId}/{jobId}")
    public void stopJob(@PathParam("scopeId") KapuaId scopeId, @PathParam("jobId") KapuaId jobId) throws KapuaException {
        jobEngineService.stopJob(scopeId, jobId);
    }

    @POST
    @Path("stop-execution/{scopeId}/{jobId}/{executionId}")
    public void stopJobExecution(@PathParam("scopeId") KapuaId scopeId, @PathParam("jobId") KapuaId jobId, @PathParam("executionId") KapuaId executionId) throws KapuaException {
        jobEngineService.stopJobExecution(scopeId, jobId, executionId);
    }

}
