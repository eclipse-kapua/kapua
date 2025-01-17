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
package org.eclipse.kapua.app.console.module.job.shared.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.job.shared.model.GwtJob;
import org.eclipse.kapua.app.console.module.job.shared.model.GwtJobExecutionQuery;
import org.eclipse.kapua.app.console.module.job.shared.model.GwtJobQuery;
import org.eclipse.kapua.app.console.module.job.shared.model.GwtJobStartOptions;
import org.eclipse.kapua.app.console.module.job.shared.model.GwtJobStep;
import org.eclipse.kapua.app.console.module.job.shared.model.GwtJobStepCreator;
import org.eclipse.kapua.app.console.module.job.shared.model.GwtJobStepDefinitionQuery;
import org.eclipse.kapua.app.console.module.job.shared.model.GwtJobStepProperty;
import org.eclipse.kapua.app.console.module.job.shared.model.GwtJobStepQuery;
import org.eclipse.kapua.app.console.module.job.shared.model.GwtJobTargetQuery;
import org.eclipse.kapua.app.console.module.job.shared.model.scheduler.GwtTriggerProperty;
import org.eclipse.kapua.app.console.module.job.shared.model.scheduler.GwtTriggerQuery;
import org.eclipse.kapua.job.engine.JobEngineFactory;
import org.eclipse.kapua.job.engine.JobStartOptions;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.FieldSortCriteria;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate.Operator;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.job.JobAttributes;
import org.eclipse.kapua.service.job.JobFactory;
import org.eclipse.kapua.service.job.JobQuery;
import org.eclipse.kapua.service.job.execution.JobExecutionAttributes;
import org.eclipse.kapua.service.job.execution.JobExecutionQuery;
import org.eclipse.kapua.service.job.step.JobStep;
import org.eclipse.kapua.service.job.step.JobStepAttributes;
import org.eclipse.kapua.service.job.step.JobStepCreator;
import org.eclipse.kapua.service.job.step.JobStepFactory;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionQuery;
import org.eclipse.kapua.service.job.step.definition.JobStepProperty;
import org.eclipse.kapua.service.job.targets.JobTargetAttributes;
import org.eclipse.kapua.service.scheduler.trigger.TriggerAttributes;
import org.eclipse.kapua.service.scheduler.trigger.TriggerFactory;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerProperty;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;

public class GwtKapuaJobModelConverter {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final JobFactory JOB_FACTORY = LOCATOR.getFactory(JobFactory.class);
    private static final JobStepFactory JOB_STEP_FACTORY = LOCATOR.getFactory(JobStepFactory.class);
    private static final JobEngineFactory JOB_ENGINE_FACTORY = LOCATOR.getFactory(JobEngineFactory.class);

    private static final TriggerFactory TRIGGER_FACTORY = LOCATOR.getFactory(TriggerFactory.class);

    private GwtKapuaJobModelConverter() {
    }

    public static Job convertJob(GwtJob gwtJob) {
        Job job = JOB_FACTORY.newEntity(GwtKapuaCommonsModelConverter.convertKapuaId(gwtJob.getScopeId()));

        GwtKapuaCommonsModelConverter.convertUpdatableEntity(gwtJob, job);

        job.setName(gwtJob.getJobName());
        job.setDescription(gwtJob.getDescription());
        job.setJobSteps(convertJobSteps(gwtJob.getJobSteps()));
        job.setJobXmlDefinition(gwtJob.getJobXmlDefinition());

        return job;
    }

    private static List<JobStep> convertJobSteps(List<GwtJobStep> gwtJobSteps) {
        List<JobStep> jobStepList = new ArrayList<JobStep>();

        for (GwtJobStep gwtJobStep : gwtJobSteps) {
            JobStep jobStep = JOB_STEP_FACTORY.newEntity(GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobStep.getScopeId()));

            GwtKapuaCommonsModelConverter.convertUpdatableEntity(gwtJobStep, jobStep);

            jobStep.setName(gwtJobStep.getJobStepName());
            jobStep.setDescription(gwtJobStep.getDescription());
            jobStep.setJobId(GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobStep.getJobId()));
            jobStep.setJobStepDefinitionId(GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobStep.getJobStepDefinitionId()));
            jobStep.setStepIndex(gwtJobStep.getStepIndex());
            jobStep.setStepProperties(convertJobStepProperties(gwtJobStep.getStepProperties()));
            jobStepList.add(jobStep);
        }

        return jobStepList;
    }

    public static List<JobStepProperty> convertJobStepProperties(List<GwtJobStepProperty> gwtJobStepProperties) {
        List<JobStepProperty> jobStepPropertyList = new ArrayList<JobStepProperty>();

        for (GwtJobStepProperty gwtProperty : gwtJobStepProperties) {
            JobStepProperty property = JOB_STEP_FACTORY.newStepProperty(gwtProperty.getPropertyName(), gwtProperty.getPropertyType(), gwtProperty.getPropertyValue());

            jobStepPropertyList.add(property);
        }
        return jobStepPropertyList;
    }

    public static JobQuery convertJobQuery(GwtJobQuery gwtJobQuery, PagingLoadConfig loadConfig) {

        JobQuery query = new JobQuery(GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobQuery.getScopeId()));

        AndPredicate predicate = query.andPredicate();
        if (gwtJobQuery.getName() != null && !gwtJobQuery.getName().isEmpty()) {
            predicate.and(query.attributePredicate(JobAttributes.NAME, gwtJobQuery.getName(), Operator.LIKE));
        }
        if (gwtJobQuery.getDescription() != null && !gwtJobQuery.getDescription().isEmpty()) {
            predicate.and(query.attributePredicate(JobAttributes.DESCRIPTION, gwtJobQuery.getDescription(), Operator.LIKE));
        }
        query.setLimit(loadConfig.getLimit());
        query.setOffset(loadConfig.getOffset());

        String sortField = StringUtils.isEmpty(loadConfig.getSortField()) ? JobAttributes.NAME : loadConfig.getSortField();
        if (sortField.equals("jobName")) {
            sortField = JobAttributes.NAME;
        } else if (sortField.equals("createdOnFormatted")) {
            sortField = JobAttributes.CREATED_ON;
        }

        SortOrder sortOrder = loadConfig.getSortDir().equals(SortDir.DESC) ? SortOrder.DESCENDING : SortOrder.ASCENDING;
        FieldSortCriteria sortCriteria = query.fieldSortCriteria(sortField, sortOrder);
        query.setSortCriteria(sortCriteria);
        query.setPredicate(predicate);
        query.setAskTotalCount(gwtJobQuery.getAskTotalCount());
        return query;
    }

    public static KapuaQuery convertJobTargetQuery(GwtJobTargetQuery gwtJobTargetQuery, PagingLoadConfig loadConfig) {
        KapuaQuery query = new KapuaQuery(GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobTargetQuery.getScopeId()));

        AndPredicate andPredicate = query.andPredicate();

        if (gwtJobTargetQuery.getJobId() != null && !gwtJobTargetQuery.getJobId().trim().isEmpty()) {
            andPredicate.and(query.attributePredicate(JobTargetAttributes.JOB_ID, GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobTargetQuery.getJobId())));
        }
        query.setPredicate(andPredicate);

        if (loadConfig != null) {
            query.setLimit(loadConfig.getLimit());
            query.setOffset(loadConfig.getOffset());

            String sortField = StringUtils.isEmpty(loadConfig.getSortField()) ? JobTargetAttributes.ENTITY_ID : loadConfig.getSortField();
            SortOrder sortOrder = loadConfig.getSortDir().equals(SortDir.DESC) ? SortOrder.DESCENDING : SortOrder.ASCENDING;
            FieldSortCriteria sortCriteria = query.fieldSortCriteria(sortField, sortOrder);
            query.setSortCriteria(sortCriteria);
        }
        query.setAskTotalCount(gwtJobTargetQuery.getAskTotalCount());
        return query;
    }

    public static KapuaQuery convertJobStepQuery(GwtJobStepQuery gwtJobStepQuery, PagingLoadConfig loadConfig) {
        KapuaQuery query = new KapuaQuery(GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobStepQuery.getScopeId()));

        AndPredicate andPredicate = query.andPredicate();
        if (gwtJobStepQuery.getJobId() != null && !gwtJobStepQuery.getJobId().trim().isEmpty()) {
            andPredicate.and(query.attributePredicate(JobStepAttributes.JOB_ID, GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobStepQuery.getJobId())));
        }
        query.setPredicate(andPredicate);

        FieldSortCriteria sortCriteria = getFieldSortCriteria(gwtJobStepQuery, loadConfig);

        query.setSortCriteria(sortCriteria);

        query.setLimit(loadConfig.getLimit());
        query.setOffset(loadConfig.getOffset());
        query.setAskTotalCount(gwtJobStepQuery.getAskTotalCount());
        return query;
    }

    private static FieldSortCriteria getFieldSortCriteria(GwtJobStepQuery gwtJobStepQuery, PagingLoadConfig loadConfig) {
        if (gwtJobStepQuery.getSortAttribute() != null) {
            String sortField = null;
            if (gwtJobStepQuery.getSortAttribute().equals(GwtJobStepQuery.GwtSortAttribute.STEP_INDEX)) {
                sortField = JobStepAttributes.STEP_INDEX;
            }
            SortOrder sortOrder = null;
            if (gwtJobStepQuery.getSortOrder().equals(GwtJobStepQuery.GwtSortOrder.DESCENDING)) {
                sortOrder = SortOrder.DESCENDING;
            } else {
                sortOrder = SortOrder.ASCENDING;
            }
            return new FieldSortCriteria(sortField, sortOrder);
        } else {
            String sortField = StringUtils.isEmpty(loadConfig.getSortField()) ? JobStepAttributes.STEP_INDEX : loadConfig.getSortField();
            if (sortField.equals("jobStepName")) {
                sortField = JobStepAttributes.NAME;
            } else if (sortField.equals("jobStepDefinitionId")) {
                sortField = JobStepAttributes.JOB_STEP_DEFINITION_ID;
            } else if (sortField.equals("jobStepDefinitionName")) {
                sortField = JobStepAttributes.JOB_STEP_DEFINITION_ID;
            }
            SortOrder sortOrder = loadConfig.getSortDir().equals(SortDir.DESC) ? SortOrder.DESCENDING : SortOrder.ASCENDING;
            return new FieldSortCriteria(sortField, sortOrder);
        }
    }

    public static JobStepDefinitionQuery convertJobStepDefinitionQuery(PagingLoadConfig loadConfig, GwtJobStepDefinitionQuery gwtJobStepDefinitionQuery) {
        JobStepDefinitionQuery jobStepDefinitionQuery = new JobStepDefinitionQuery(GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobStepDefinitionQuery.getScopeId()));

        AndPredicate andPredicate = jobStepDefinitionQuery.andPredicate();
        jobStepDefinitionQuery.setPredicate(andPredicate);

        if (loadConfig != null) {
            jobStepDefinitionQuery.setLimit(loadConfig.getLimit());
            jobStepDefinitionQuery.setOffset(loadConfig.getOffset());
        }
        return jobStepDefinitionQuery;
    }

    public static JobStepCreator convertJobStepCreator(GwtJobStepCreator gwtJobStepCreator) {

        // Convert scopeId
        KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobStepCreator.getScopeId());
        JobStepCreator jobStepCreator = new JobStepCreator(scopeId);

        jobStepCreator.setName(gwtJobStepCreator.getJobName());
        jobStepCreator.setDescription(gwtJobStepCreator.getJobDescription());
        jobStepCreator.setJobId(GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobStepCreator.getJobId()));
        jobStepCreator.setJobStepDefinitionId(GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobStepCreator.getJobStepDefinitionId()));
        jobStepCreator.setStepIndex(gwtJobStepCreator.getStepIndex());
        jobStepCreator.setJobStepProperties(convertJobStepProperties(gwtJobStepCreator.getProperties()));
        // Return converted
        return jobStepCreator;
    }

    public static KapuaQuery convertTriggerQuery(GwtTriggerQuery gwtTriggerQuery, PagingLoadConfig loadConfig) {

        KapuaQuery query = new KapuaQuery(GwtKapuaCommonsModelConverter.convertKapuaId(gwtTriggerQuery.getScopeId()));

        AttributePredicate<String> kapuaPropertyNameAttributePredicate = query.attributePredicate(TriggerAttributes.TRIGGER_PROPERTIES_NAME, "jobId");
        AttributePredicate<String> kapuaPropertyValueAttributePredicate = query.attributePredicate(TriggerAttributes.TRIGGER_PROPERTIES_VALUE, gwtTriggerQuery.getJobId());
        AttributePredicate<String> kapuaPropertyTypeAttributePredicate = query.attributePredicate(TriggerAttributes.TRIGGER_PROPERTIES_TYPE, KapuaId.class.getName());

        AndPredicate andPredicate = query.andPredicate(
                kapuaPropertyNameAttributePredicate,
                kapuaPropertyValueAttributePredicate,
                kapuaPropertyTypeAttributePredicate
        );

        String sortField = StringUtils.isEmpty(loadConfig.getSortField()) ? TriggerAttributes.ENTITY_ID : loadConfig.getSortField();
        if (sortField.equals("triggerName")) {
            sortField = TriggerAttributes.NAME;
        } else if (sortField.equals("startsOnFormatted")) {
            sortField = TriggerAttributes.STARTS_ON;
        } else if (sortField.equals("endsOnFormatted")) {
            sortField = TriggerAttributes.ENDS_ON;
        }

        SortOrder sortOrder = loadConfig.getSortDir().equals(SortDir.DESC) ? SortOrder.DESCENDING : SortOrder.ASCENDING;
        FieldSortCriteria sortCriteria = query.fieldSortCriteria(sortField, sortOrder);
        query.setSortCriteria(sortCriteria);
        query.setPredicate(andPredicate);
        query.setLimit(loadConfig.getLimit());
        query.setOffset(loadConfig.getOffset());
        query.setAskTotalCount(gwtTriggerQuery.getAskTotalCount());
        return query;
    }

    public static List<TriggerProperty> convertTriggerProperties(List<GwtTriggerProperty> gwtTriggerProperties) {
        List<TriggerProperty> triggerPropertyList = new ArrayList<TriggerProperty>();

        for (GwtTriggerProperty gwtProperty : gwtTriggerProperties) {
            TriggerProperty property = TRIGGER_FACTORY.newTriggerProperty(gwtProperty.getPropertyName(), gwtProperty.getPropertyType(), gwtProperty.getPropertyValue());
            triggerPropertyList.add(property);
        }
        return triggerPropertyList;
    }

    public static JobExecutionQuery convertJobExecutionQuery(PagingLoadConfig pagingLoadConfig, GwtJobExecutionQuery gwtJobExecutionQuery) {
        JobExecutionQuery query = new JobExecutionQuery(GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobExecutionQuery.getScopeId()));
        query.setPredicate(query.attributePredicate(JobExecutionAttributes.JOB_ID, GwtKapuaCommonsModelConverter.convertKapuaId(gwtJobExecutionQuery.getJobId())));
        String sortField = StringUtils.isEmpty(pagingLoadConfig.getSortField()) ? JobAttributes.NAME : pagingLoadConfig.getSortField();
        if (sortField.equals("startedOnFormatted")) {
            sortField = JobExecutionAttributes.STARTED_ON;
        } else if (sortField.equals("endedOnFormatted")) {
            sortField = JobExecutionAttributes.ENDED_ON;
        }
        SortOrder sortOrder = pagingLoadConfig.getSortDir().equals(SortDir.DESC) ? SortOrder.DESCENDING : SortOrder.ASCENDING;
        query.setSortCriteria(query.fieldSortCriteria(sortField, sortOrder));
        query.setLimit(pagingLoadConfig.getLimit());
        query.setOffset(pagingLoadConfig.getOffset());
        query.setAskTotalCount(gwtJobExecutionQuery.getAskTotalCount());
        return query;
    }

    public static JobStartOptions convertJobStartOptions(GwtJobStartOptions gwtJobStartOptions) {
        JobStartOptions jobStartOptions = JOB_ENGINE_FACTORY.newJobStartOptions();
        jobStartOptions.setTargetIdSublist(convertTargetIdSublist(gwtJobStartOptions.getTargetIdSublist()));
        jobStartOptions.setResetStepIndex(gwtJobStartOptions.getResetStepIndex());
        jobStartOptions.setFromStepIndex(gwtJobStartOptions.getFromStepIndex());
        return jobStartOptions;
    }

    private static Set<KapuaId> convertTargetIdSublist(List<String> gwtTargetIdSublist) {
        Set<KapuaId> targetIdSublist = new HashSet<KapuaId>();
        for (String gwtKapuaId : gwtTargetIdSublist) {
            targetIdSublist.add(GwtKapuaCommonsModelConverter.convertKapuaId(gwtKapuaId));
        }
        return targetIdSublist;
    }
}
