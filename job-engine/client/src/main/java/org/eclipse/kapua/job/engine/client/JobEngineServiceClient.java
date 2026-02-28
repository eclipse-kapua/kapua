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
package org.eclipse.kapua.job.engine.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.kapua.EntityNotFoundExceptionInfo;
import org.eclipse.kapua.ExceptionInfo;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.job.engine.IsJobRunningMultipleResponse;
import org.eclipse.kapua.job.engine.IsJobRunningResponse;
import org.eclipse.kapua.job.engine.JobEngineService;
import org.eclipse.kapua.job.engine.JobStartOptions;
import org.eclipse.kapua.job.engine.MultipleJobIdRequest;
import org.eclipse.kapua.job.engine.client.filter.SessionInfoFilter;
import org.eclipse.kapua.job.engine.client.settings.JobEngineClientSetting;
import org.eclipse.kapua.job.engine.client.settings.JobEngineClientSettingKeys;
import org.eclipse.kapua.job.engine.exception.CleanJobDataException;
import org.eclipse.kapua.job.engine.exception.CleanJobDataExceptionInfo;
import org.eclipse.kapua.job.engine.exception.JobAlreadyRunningException;
import org.eclipse.kapua.job.engine.exception.JobAlreadyRunningExceptionInfo;
import org.eclipse.kapua.job.engine.exception.JobEngineException;
import org.eclipse.kapua.job.engine.exception.JobInvalidTargetException;
import org.eclipse.kapua.job.engine.exception.JobInvalidTargetExceptionInfo;
import org.eclipse.kapua.job.engine.exception.JobMissingStepException;
import org.eclipse.kapua.job.engine.exception.JobMissingStepExceptionInfo;
import org.eclipse.kapua.job.engine.exception.JobMissingTargetException;
import org.eclipse.kapua.job.engine.exception.JobMissingTargetExceptionInfo;
import org.eclipse.kapua.job.engine.exception.JobNotRunningException;
import org.eclipse.kapua.job.engine.exception.JobNotRunningExceptionInfo;
import org.eclipse.kapua.job.engine.exception.JobResumingException;
import org.eclipse.kapua.job.engine.exception.JobResumingExceptionInfo;
import org.eclipse.kapua.job.engine.exception.JobRunningException;
import org.eclipse.kapua.job.engine.exception.JobRunningExceptionInfo;
import org.eclipse.kapua.job.engine.exception.JobStartingException;
import org.eclipse.kapua.job.engine.exception.JobStartingExceptionInfo;
import org.eclipse.kapua.job.engine.exception.JobStoppingException;
import org.eclipse.kapua.job.engine.exception.JobStoppingExceptionInfo;
import org.eclipse.kapua.model.id.KapuaId;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * {@link JobEngineService} remote client implementation.
 *
 * @since 1.5.0
 */
@Singleton
public class JobEngineServiceClient implements JobEngineService {

    private static final Logger LOG = LoggerFactory.getLogger(JobEngineServiceClient.class);

    private final WebTarget jobEngineTarget;
    private final XmlUtil xmlUtil;

    /**
     * Constructor.
     *
     * @since 1.5.0
     */
    @Inject
    public JobEngineServiceClient(JobEngineClientSetting jobEngineClientSetting, XmlUtil xmlUtil) {
        this.xmlUtil = xmlUtil;
        Client jobEngineClient =
                ClientBuilder
                        .newClient()
                        .register(SessionInfoFilter.class)
                        .register(MoxyJsonFeature.class);

        jobEngineTarget = jobEngineClient.target(jobEngineClientSetting.getString(JobEngineClientSettingKeys.JOB_ENGINE_BASE_URL));
    }

    @Override
    public void startJob(KapuaId scopeId, KapuaId jobId) throws KapuaException {
        try {
            String path = String.format("start/%s/%s", scopeId.toCompactId(), jobId.toCompactId());
            LOG.debug("POST {}", path);

            Response response = getPreparedRequest(path).post(null);

            checkResponse("POST", path, response);
        } catch (ClientErrorException clientErrorException) {
            throw KapuaException.internalError(clientErrorException);
        }
    }

    @Override
    public void startJob(KapuaId scopeId, KapuaId jobId, JobStartOptions jobStartOptions) throws KapuaException {
        try {
            String path = String.format("start-with-options/%s/%s", scopeId.toCompactId(), jobId.toCompactId());
            String jobStartOptionsXml = xmlUtil.marshal(jobStartOptions);
            LOG.debug("POST {} - Content: {}", path, jobStartOptionsXml);

            Response response = getPreparedRequest(path).post(Entity.xml(jobStartOptionsXml));

            checkResponse("POST", path, response);
        } catch (ClientErrorException | JAXBException e) {
            throw KapuaException.internalError(e);
        }
    }

    @Override
    public boolean isRunning(KapuaId scopeId, KapuaId jobId) throws KapuaException {
        try {
            String path = String.format("is-running/%s/%s", scopeId.toCompactId(), jobId.toCompactId());
            LOG.debug("GET {}", path);

            Response response = getPreparedRequest(path).get();

            String responseText = checkResponse("GET", path, response);

            IsJobRunningResponse isRunningJobResponse = xmlUtil.unmarshalJson(responseText, IsJobRunningResponse.class);
            return isRunningJobResponse.isRunning();
        } catch (ClientErrorException | JAXBException | SAXException e) {
            throw KapuaException.internalError(e);
        }
    }

    @Override
    public Map<KapuaId, Boolean> isRunning(KapuaId scopeId, Set<KapuaId> jobIds) throws KapuaException {
        try {
            MultipleJobIdRequest multipleJobIdRequest = new MultipleJobIdRequest();
            multipleJobIdRequest.setJobIds(jobIds);
            String multipleJobIdRequestXml = xmlUtil.marshal(multipleJobIdRequest);

            String path = String.format("is-running/%s", scopeId.toCompactId());
            LOG.debug("POST {} - Content {}", path, multipleJobIdRequestXml);

            Response response = getPreparedRequest(path).post(Entity.xml(multipleJobIdRequestXml));

            String responseText = checkResponse("POST", path, response);

            IsJobRunningMultipleResponse isJobRunningMultipleResponse = xmlUtil.unmarshalJson(responseText, IsJobRunningMultipleResponse.class);
            Map<KapuaId, Boolean> res = new HashMap<>();
            Optional.ofNullable(isJobRunningMultipleResponse.getList())
                    .orElse(new ArrayList<>())
                    .forEach(r -> res.put(r.getJobId(), r.isRunning()));
            return res;
        } catch (ClientErrorException | JAXBException | SAXException e) {
            throw KapuaException.internalError(e);
        }
    }

    @Override
    public void stopJob(KapuaId scopeId, KapuaId jobId) throws KapuaException {
        try {
            String path = String.format("stop/%s/%s", scopeId.toCompactId(), jobId.toCompactId());
            LOG.debug("POST {}", path);

            Response response = getPreparedRequest(path).post(null);

            checkResponse("POST", path, response);
        } catch (ClientErrorException clientErrorException) {
            throw KapuaException.internalError(clientErrorException);
        }
    }

    @Override
    public void stopJobExecution(KapuaId scopeId, KapuaId jobId, KapuaId jobExecutionId) throws KapuaException {
        try {
            String path = String.format("stop-execution/%s/%s/%s", scopeId.toCompactId(), jobId.toCompactId(), jobExecutionId.toCompactId());
            LOG.debug("POST {}", path);

            Response response = getPreparedRequest(path).post(null);

            checkResponse("POST", path, response);
        } catch (ClientErrorException clientErrorException) {
            throw KapuaException.internalError(clientErrorException);
        }
    }

    @Override
    public void resumeJobExecution(KapuaId scopeId, KapuaId jobId, KapuaId jobExecutionId) throws KapuaException {
        try {
            String path = String.format("resume-execution/%s/%s/%s", scopeId.toCompactId(), jobId.toCompactId(), jobExecutionId.toCompactId());
            LOG.debug("POST {}", path);

            Response response = getPreparedRequest(path).post(null);

            checkResponse("POST", path, response);
        } catch (ClientErrorException clientErrorException) {
            throw KapuaException.internalError(clientErrorException);
        }
    }

    @Override
    public void cleanJobData(KapuaId scopeId, KapuaId jobId) throws KapuaException {
        try {
            String path = String.format("clean-data/%s/%s", scopeId.toCompactId(), jobId.toCompactId());
            LOG.debug("POST {}", path);

            Response response = getPreparedRequest(path).post(null);

            checkResponse("POST", path, response);
        } catch (ClientErrorException clientErrorException) {
            throw KapuaException.internalError(clientErrorException);
        }
    }
    // Private methods

    /**
     * Prepares the request from the {@link WebTarget} with {@link WebTarget#request(MediaType...)} set to {@link MediaType#APPLICATION_JSON_TYPE} and {@link Invocation.Builder#accept(MediaType...)}
     * set to {@link MediaType#APPLICATION_JSON_TYPE}.
     *
     * @param path
     *         The path of the request.
     * @return The {@link Invocation.Builder}.
     * @since 2.0.0
     */
    private Invocation.Builder getPreparedRequest(String path) {
        return jobEngineTarget.path(path)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Checks the {@link Response} for errors.
     *
     * @param method
     *         The request method. Used for logging purposes.
     * @param path
     *         The request path. Used for logging purposes.
     * @param response
     *         The {@link Response} to check.
     * @return The body of the request in {@link String} format.
     * @throws KapuaException
     *         The proper {@link KapuaException} if needed.
     * @since 2.0.0
     */
    private String checkResponse(String method, String path, Response response) throws KapuaException {
        String responseText = response.readEntity(String.class);
        LOG.debug("{} {} - Response Code: {} - Content: {}", method, path, response.getStatus(), responseText);
        Family family = response.getStatusInfo().getFamily();
        if (family == Family.CLIENT_ERROR || family == Family.SERVER_ERROR) {
            LOG.error("{} {} - Response Code: {} - Content: {}", method, path, response.getStatus(), responseText);

            throw buildJobEngineExceptionFromResponse(responseText);
        }

        return responseText;
    }

    /**
     * Parses the {@link Response} content to rebuld the original {@link JobEngineException}
     *
     * @param responseText
     *         The {@link Response} content.
     * @return The correct KapuaException.
     * @since 1.5.0
     */
    private KapuaException buildJobEngineExceptionFromResponse(String responseText) {
        try {
            if (StringUtils.isBlank(responseText)) {
                throw new KapuaRuntimeException(KapuaErrorCodes.INTERNAL_ERROR, "JobEngine returned an error but no message was given");
            }

            ExceptionInfo exceptionInfo = xmlUtil.unmarshalJson(responseText, ExceptionInfo.class);

            if (exceptionInfo == null) {
                throw new KapuaRuntimeException(KapuaErrorCodes.INTERNAL_ERROR,
                        "Job Engine returned an not-empty response but it was not deserializable as an ExceptionInfo. Content returned: " + responseText);
            }

            if (exceptionInfo.getKapuaErrorCode() == null) {
                throw new KapuaRuntimeException(KapuaErrorCodes.INTERNAL_ERROR, "Job Engine returned an ExceptionInfo without a KapuaErrorCode. Content returned: " + responseText);
            }

            switch (exceptionInfo.getKapuaErrorCode()) {
            case "ENTITY_NOT_FOUND":
                EntityNotFoundExceptionInfo entityNotFoundExceptionInfo = xmlUtil.unmarshalJson(responseText, EntityNotFoundExceptionInfo.class);
                return new KapuaEntityNotFoundException(entityNotFoundExceptionInfo.getEntityType(), entityNotFoundExceptionInfo.getEntityId());
            case "CANNOT_CLEANUP_JOB_DATA":
            case "CANNOT_CLEANUP_JOB_DATA_WITH_CAUSE":
                CleanJobDataExceptionInfo cleanJobDataExceptionInfo = xmlUtil.unmarshalJson(responseText, CleanJobDataExceptionInfo.class);
                return new CleanJobDataException(cleanJobDataExceptionInfo.getScopeId(), cleanJobDataExceptionInfo.getJobId());
            case "JOB_ALREADY_RUNNING":
                JobAlreadyRunningExceptionInfo jobAlreadyRunningExceptionInfo = xmlUtil.unmarshalJson(responseText, JobAlreadyRunningExceptionInfo.class);
                return new JobAlreadyRunningException(jobAlreadyRunningExceptionInfo.getScopeId(),
                        jobAlreadyRunningExceptionInfo.getJobId(),
                        jobAlreadyRunningExceptionInfo.getExecutionId(),
                        jobAlreadyRunningExceptionInfo.getJobTargetIdSubset());
            case "JOB_TARGET_INVALID":
                JobInvalidTargetExceptionInfo jobInvalidTargetExceptionInfo = xmlUtil.unmarshalJson(responseText, JobInvalidTargetExceptionInfo.class);
                return new JobInvalidTargetException(jobInvalidTargetExceptionInfo.getScopeId(), jobInvalidTargetExceptionInfo.getJobId(), jobInvalidTargetExceptionInfo.getJobTargetIdSubset());
            case "JOB_STEP_MISSING":
                JobMissingStepExceptionInfo jobMissingStepExceptionInfo = xmlUtil.unmarshalJson(responseText, JobMissingStepExceptionInfo.class);
                return new JobMissingStepException(jobMissingStepExceptionInfo.getScopeId(), jobMissingStepExceptionInfo.getJobId());
            case "JOB_TARGET_MISSING":
                JobMissingTargetExceptionInfo jobMissingTargetExceptionInfo = xmlUtil.unmarshalJson(responseText, JobMissingTargetExceptionInfo.class);
                return new JobMissingTargetException(jobMissingTargetExceptionInfo.getScopeId(), jobMissingTargetExceptionInfo.getJobId());
            case "JOB_NOT_RUNNING":
                JobNotRunningExceptionInfo jobNotRunningExceptionInfo = xmlUtil.unmarshalJson(responseText, JobNotRunningExceptionInfo.class);
                return new JobNotRunningException(jobNotRunningExceptionInfo.getScopeId(), jobNotRunningExceptionInfo.getJobId());
            case "JOB_RESUMING":
                JobResumingExceptionInfo jobResumingExceptionInfo = xmlUtil.unmarshalJson(responseText, JobResumingExceptionInfo.class);
                return new JobResumingException(jobResumingExceptionInfo.getScopeId(), jobResumingExceptionInfo.getJobId(), jobResumingExceptionInfo.getExecutionId());
            case "JOB_RUNNING":
                JobRunningExceptionInfo jobRunningExceptionInfo = xmlUtil.unmarshalJson(responseText, JobRunningExceptionInfo.class);
                return new JobRunningException(jobRunningExceptionInfo.getScopeId(), jobRunningExceptionInfo.getJobId());
            case "JOB_STARTING":
                JobStartingExceptionInfo jobStartingExceptionInfo = xmlUtil.unmarshalJson(responseText, JobStartingExceptionInfo.class);
                return new JobStartingException(jobStartingExceptionInfo.getScopeId(), jobStartingExceptionInfo.getJobId());
            case "JOB_STOPPING":
                JobStoppingExceptionInfo jobStoppingExceptionInfo = xmlUtil.unmarshalJson(responseText, JobStoppingExceptionInfo.class);
                return new JobStoppingException(jobStoppingExceptionInfo.getScopeId(), jobStoppingExceptionInfo.getJobId(), jobStoppingExceptionInfo.getExecutionId());
            default:
                return KapuaException.internalError(exceptionInfo.getMessage());
            }
        } catch (JAXBException | SAXException e) {
            return KapuaException.internalError(e);
        }
    }

}
