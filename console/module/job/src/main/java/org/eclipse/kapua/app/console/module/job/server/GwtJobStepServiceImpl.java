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
import java.util.List;

import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.job.shared.model.GwtJobStep;
import org.eclipse.kapua.app.console.module.job.shared.model.GwtJobStepCreator;
import org.eclipse.kapua.app.console.module.job.shared.model.GwtJobStepProperty;
import org.eclipse.kapua.app.console.module.job.shared.model.GwtJobStepQuery;
import org.eclipse.kapua.app.console.module.job.shared.service.GwtJobStepService;
import org.eclipse.kapua.app.console.module.job.shared.util.GwtKapuaJobModelConverter;
import org.eclipse.kapua.app.console.module.job.shared.util.KapuaGwtJobModelConverter;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.job.step.JobStep;
import org.eclipse.kapua.service.job.step.JobStepAttributes;
import org.eclipse.kapua.service.job.step.JobStepCreator;
import org.eclipse.kapua.service.job.step.JobStepFactory;
import org.eclipse.kapua.service.job.step.JobStepListResult;
import org.eclipse.kapua.service.job.step.JobStepService;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinition;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionService;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class GwtJobStepServiceImpl extends KapuaRemoteServiceServlet implements GwtJobStepService {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final JobStepService JOB_STEP_SERVICE = LOCATOR.getService(JobStepService.class);
    private static final JobStepFactory JOB_STEP_FACTORY = LOCATOR.getFactory(JobStepFactory.class);

    private static final JobStepDefinitionService JOB_STEP_DEFINITION_SERVICE = LOCATOR.getService(JobStepDefinitionService.class);
    private static final long serialVersionUID = 6248597422415080827L;

    @Override
    public PagingLoadResult<GwtJobStep> query(PagingLoadConfig loadConfig, GwtJobStepQuery gwtJobStepQuery) throws GwtKapuaException {
        // Do query
        int totalLength = 0;
        List<GwtJobStep> gwtJobStepList = new ArrayList<GwtJobStep>();
        try {
            // Convert from GWT entity
            KapuaQuery jobStepQuery = GwtKapuaJobModelConverter.convertJobStepQuery(gwtJobStepQuery, loadConfig);

            // query
            JobStepListResult jobStepList = JOB_STEP_SERVICE.query(jobStepQuery);
            totalLength = jobStepList.getTotalCount().intValue();

            // Converto to GWT entity
            for (JobStep js : jobStepList.getItems()) {
                GwtJobStep gwtJobStep = KapuaGwtJobModelConverter.convertJobStep(js);

                //TODO: #LAYER_VIOLATION - I should not have to worry about id conversion here, it is a persistence aspect!

                JobStepDefinition jobStepDefinition = JOB_STEP_DEFINITION_SERVICE.find(KapuaId.ANY, GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobStep.getJobStepDefinitionId()));
                gwtJobStep.setJobStepDefinitionName(jobStepDefinition.getName());

                setEnumOnJobStepProperty(gwtJobStep.getStepProperties());

                gwtJobStepList.add(gwtJobStep);
            }

            return new BasePagingLoadResult<GwtJobStep>(gwtJobStepList, loadConfig.getOffset(), totalLength);
        } catch (Exception e) {
            throw KapuaExceptionHandler.buildExceptionFromError(e);
        }
    }

    @Override
    public Integer count(String scopeIdString, String jobIdString) throws GwtKapuaException {
        try {
            // Convert from GWT entity
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(scopeIdString);
            KapuaId jobId = GwtKapuaCommonsModelConverter.convertKapuaId(jobIdString);

            // query
            KapuaQuery jobStepQuery = new KapuaQuery(scopeId);
            jobStepQuery.setPredicate(jobStepQuery.attributePredicate(JobStepAttributes.JOB_ID, jobId));

            return new Long(JOB_STEP_SERVICE.count(jobStepQuery)).intValue();
        } catch (Exception e) {
            throw KapuaExceptionHandler.buildExceptionFromError(e);
        }
    }

    @Override
    public PagingLoadResult<GwtJobStep> findByJobId(PagingLoadConfig loadConfig, String scopeId, String jobId) throws GwtKapuaException {
        if (jobId != null) {
            GwtJobStepQuery gwtJobStepQuery = new GwtJobStepQuery();
            gwtJobStepQuery.setScopeId(scopeId);
            gwtJobStepQuery.setJobId(jobId);

            return query(loadConfig, gwtJobStepQuery);
        } else {
            return new BasePagingLoadResult<GwtJobStep>(new ArrayList<GwtJobStep>(), 0, 0);
        }
    }

    @Override
    public GwtJobStep create(GwtXSRFToken xsrfToken, GwtJobStepCreator gwtJobStepCreator) throws GwtKapuaException {
        // Checking XSRF token
        checkXSRFToken(xsrfToken);
        // Do create
        GwtJobStep gwtJobStep = null;
        try {
            // Convert from GWT Entity
            JobStepCreator jobStepCreator = GwtKapuaJobModelConverter.convertJobStepCreator(gwtJobStepCreator);

            // Create
            JobStep jobStep = JOB_STEP_SERVICE.create(jobStepCreator);

            // Convert
            gwtJobStep = KapuaGwtJobModelConverter.convertJobStep(jobStep);

            setEnumOnJobStepProperty(gwtJobStep.getStepProperties());
            // Return result
            return gwtJobStep;
        } catch (Exception e) {
            throw KapuaExceptionHandler.buildExceptionFromError(e);
        }
    }

    @Override
    public void delete(GwtXSRFToken xsrfToken, String gwtScopeId, String gwtJobStepId) throws GwtKapuaException {
        checkXSRFToken(xsrfToken);

        KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtScopeId);
        KapuaId jobTargetId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobStepId);

        try {
            JOB_STEP_SERVICE.delete(scopeId, jobTargetId);
        } catch (Exception e) {
            throw KapuaExceptionHandler.buildExceptionFromError(e);
        }
    }

    @Override
    public GwtJobStep find(String gwtScopeId, String gwtJobStepId) throws GwtKapuaException {

        GwtJobStep gwtJobStep = null;
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtScopeId);
            KapuaId jobStepId = KapuaEid.parseCompactId(gwtJobStepId);

            JobStep jobStep = JOB_STEP_SERVICE.find(scopeId, jobStepId);

            if (jobStep != null) {
                gwtJobStep = KapuaGwtJobModelConverter.convertJobStep(jobStep);

                setEnumOnJobStepProperty(gwtJobStep.getStepProperties());
            }
        } catch (Exception e) {
            throw KapuaExceptionHandler.buildExceptionFromError(e);
        }

        return gwtJobStep;
    }

    @Override
    public GwtJobStep update(GwtXSRFToken xsrfToken, GwtJobStep gwtJobStep) throws GwtKapuaException {
        checkXSRFToken(xsrfToken);

        GwtJobStep gwtJobStepUpdated = null;
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtJobStep.getScopeId());
            KapuaId userId = KapuaEid.parseCompactId(gwtJobStep.getId());

            JobStep jobStep = JOB_STEP_SERVICE.find(scopeId, userId);

            if (jobStep != null) {
                // Update job step
                jobStep.setName(gwtJobStep.getUnescapedJobStepName());
                jobStep.setDescription(gwtJobStep.getUnescapedDescription());
                jobStep.setStepIndex(gwtJobStep.getStepIndex());
                jobStep.setJobStepDefinitionId(GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobStep.getJobStepDefinitionId()));
                jobStep.setStepProperties(GwtKapuaJobModelConverter.convertJobStepProperties(gwtJobStep.getStepProperties()));

                // optlock
                jobStep.setOptlock(gwtJobStep.getOptlock());

                // update the user
                JobStep jobStepUpdated = JOB_STEP_SERVICE.update(jobStep);
                // convert to GwtAccount and return
                // reload the user as we want to load all its permissions
                gwtJobStepUpdated = KapuaGwtJobModelConverter.convertJobStep(jobStepUpdated);

                setEnumOnJobStepProperty(gwtJobStep.getStepProperties());
            }

            return gwtJobStepUpdated;
        } catch (Exception e) {
            throw KapuaExceptionHandler.buildExceptionFromError(e);
        }
    }

    @Override
    public int getJobStepPropertyLengthMax() throws GwtKapuaException {
        try {
            return JOB_STEP_SERVICE.getJobStepPropertyMaxLength();
        } catch (Exception e) {
            throw KapuaExceptionHandler.buildExceptionFromError(e);
        }
    }

    /**
     * Set the {@link GwtJobStepProperty#isEnum()} property. This cannot be performed in *.shared.* packages (entity converters are in that package), since `Class.forName` is not present in the JRE
     * Emulation library.
     *
     * @param jobStepProperties
     * @throws ClassNotFoundException
     */
    private void setEnumOnJobStepProperty(List<GwtJobStepProperty> jobStepProperties) throws ClassNotFoundException {
        for (GwtJobStepProperty gjsp : jobStepProperties) {
            gjsp.setEnum(Class.forName(gjsp.getPropertyType()).isEnum());
        }
    }

    @Override
    public GwtJobStepProperty trickGwt() {
        return null;
    }
}
