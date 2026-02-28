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
package org.eclipse.kapua.app.api.resources.v1.resources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jersey.rest.model.EntityId;
import org.eclipse.kapua.commons.jersey.rest.model.ScopeId;
import org.eclipse.kapua.app.api.core.resources.AbstractKapuaResource;
import org.eclipse.kapua.job.engine.IsJobRunningMultipleResponse;
import org.eclipse.kapua.job.engine.IsJobRunningResponse;
import org.eclipse.kapua.job.engine.JobEngineService;
import org.eclipse.kapua.job.engine.JobStartOptions;
import org.eclipse.kapua.job.engine.MultipleJobIdRequest;
import org.eclipse.kapua.job.engine.exception.JobNotRunningException;

@Path("{scopeId}/jobs")
public class JobEngine extends AbstractKapuaResource {

    @Inject
    public JobEngineService jobEngineService;

    @POST
    @Path("{jobId}/_start")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void startJob(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("jobId") EntityId jobId,
            JobStartOptions jobStartOptions) throws KapuaException {
        jobEngineService.startJob(scopeId, jobId, jobStartOptions);
    }

    @POST
    @Path("{jobId}/_stop")
    public Response stopJob(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("jobId") EntityId jobId) throws KapuaException {
        try {
            jobEngineService.stopJob(scopeId, jobId);
            return Response.accepted().build();
        } catch (JobNotRunningException jobNotRunningException) {
            return Response.accepted().build();
        }
    }

    @GET
    @Path("{jobId}/_isRunning")
    public IsJobRunningResponse isRunning(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("jobId") EntityId jobId) throws KapuaException {
        return new IsJobRunningResponse(jobId, jobEngineService.isRunning(scopeId, jobId));
    }

    @POST
    @Path("_isRunning")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public IsJobRunningMultipleResponse isRunning(
            @PathParam("scopeId") ScopeId scopeId,
            MultipleJobIdRequest jobIds) throws KapuaException, JAXBException {
        return new IsJobRunningMultipleResponse(jobEngineService.isRunning(scopeId, jobIds.getJobIds()));
    }

    @POST
    @Path("{jobId}/executions/{executionId}/_resume")
    public void resumeJobExecution(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("jobId") EntityId jobId,
            @PathParam("executionId") EntityId executionId) throws KapuaException {
        jobEngineService.resumeJobExecution(scopeId, jobId, executionId);
    }

    @POST
    @Path("{jobId}/executions/{executionId}/_stop")
    public Response stopJobExecution(
            @PathParam("scopeId") ScopeId scopeId,
            @PathParam("jobId") EntityId jobId,
            @PathParam("executionId") EntityId executionId) throws KapuaException {
        try {
            jobEngineService.stopJobExecution(scopeId, jobId, executionId);
            return Response.accepted().build();
        } catch (JobNotRunningException jobNotRunningException) {
            return Response.accepted().build();
        }
    }

}
