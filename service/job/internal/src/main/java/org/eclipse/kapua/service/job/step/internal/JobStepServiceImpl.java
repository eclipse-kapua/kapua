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
package org.eclipse.kapua.service.job.step.internal;

import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Singleton;
import javax.xml.bind.DatatypeConverter;

import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AttributePredicate.Operator;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.job.exception.CannotModifyJobStepsException;
import org.eclipse.kapua.service.job.execution.JobExecutionAttributes;
import org.eclipse.kapua.service.job.execution.JobExecutionFactory;
import org.eclipse.kapua.service.job.execution.JobExecutionQuery;
import org.eclipse.kapua.service.job.execution.JobExecutionService;
import org.eclipse.kapua.service.job.internal.settings.JobServiceSettingKeys;
import org.eclipse.kapua.service.job.internal.settings.JobServiceSettings;
import org.eclipse.kapua.service.job.step.JobStep;
import org.eclipse.kapua.service.job.step.JobStepAttributes;
import org.eclipse.kapua.service.job.step.JobStepCreator;
import org.eclipse.kapua.service.job.step.JobStepFactory;
import org.eclipse.kapua.service.job.step.JobStepIndex;
import org.eclipse.kapua.service.job.step.JobStepListResult;
import org.eclipse.kapua.service.job.step.JobStepRepository;
import org.eclipse.kapua.service.job.step.JobStepService;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinition;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionRepository;
import org.eclipse.kapua.service.job.step.definition.JobStepProperty;
import org.eclipse.kapua.storage.TxContext;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * {@link JobStepService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class JobStepServiceImpl implements JobStepService {

    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final TxManager txManager;
    private final JobStepRepository jobStepRepository;
    private final JobStepFactory jobStepFactory;
    private final JobExecutionService jobExecutionService;
    private final JobExecutionFactory jobExecutionFactory;
    private final JobStepDefinitionRepository jobStepDefinitionRepository;
    private final XmlUtil xmlUtil;

    public JobStepServiceImpl(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            TxManager txManager,
            JobStepRepository jobStepRepository,
            JobStepFactory jobStepFactory,
            JobExecutionService jobExecutionService,
            JobExecutionFactory jobExecutionFactory,
            JobStepDefinitionRepository jobStepDefinitionRepository,
            XmlUtil xmlUtil) {
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.txManager = txManager;
        this.jobStepRepository = jobStepRepository;
        this.jobStepFactory = jobStepFactory;
        this.jobExecutionService = jobExecutionService;
        this.jobExecutionFactory = jobExecutionFactory;
        this.jobStepDefinitionRepository = jobStepDefinitionRepository;
        this.xmlUtil = xmlUtil;
    }

    private final JobServiceSettings jobServiceSettings = new JobServiceSettings();
    /**
     * The maximum length that a {@link JobStepProperty#getPropertyValue()} is allowed to have
     *
     * @since 2.0.0
     */
    private final int jobStepPropertyValueLengthMax = jobServiceSettings.getInt(JobServiceSettingKeys.JOB_STEP_PROPERTY_VALUE_LENGTH_MAX);

    @Override
    public JobStep create(JobStepCreator jobStepCreator) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(jobStepCreator, "jobStepCreator");
        ArgumentValidator.notNull(jobStepCreator.getScopeId(), "jobStepCreator.scopeId");
        ArgumentValidator.validateEntityName(jobStepCreator.getName(), "jobStepCreator.name");
        ArgumentValidator.notNull(jobStepCreator.getJobStepDefinitionId(), "jobStepCreator.stepDefinitionId");

        for (JobStepProperty jobStepProperty : jobStepCreator.getStepProperties()) {
            if (jobStepProperty.getPropertyValue() != null) {
                Integer stepPropertyMaxLength = jobStepProperty.getMaxLength() != null ? jobStepProperty.getMaxLength() : jobStepPropertyValueLengthMax;
                ArgumentValidator.lengthRange(jobStepProperty.getPropertyValue(), jobStepProperty.getMinLength(), stepPropertyMaxLength,
                        "jobStepCreator.stepProperties[]." + jobStepProperty.getName());
            }
        }

        if (jobStepCreator.getDescription() != null) {
            ArgumentValidator.numRange(jobStepCreator.getDescription().length(), 0, 8192, "jobStepCreator.description");
        }
        // Check access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.JOB, Actions.write, jobStepCreator.getScopeId()));

        return txManager.execute(tx -> {
            // Check job step definition
            validateJobStepProperties(tx, jobStepCreator);

            // Check duplicate name
            if (jobStepRepository.countEntitiesWithNameInScope(
                    tx,
                    jobStepCreator.getScopeId(),
                    jobStepCreator.getName(),
                    jobStepCreator.getJobId()) > 0) {
                throw new KapuaDuplicateNameException(jobStepCreator.getName());
            }
            // Check Job Executions
            if (KapuaSecurityUtils.doPrivileged(() -> jobExecutionService.countByJobId(jobStepCreator.getScopeId(), jobStepCreator.getJobId())) > 0) {
                throw new CannotModifyJobStepsException(jobStepCreator.getJobId());
            }
            // Populate JobStepCreator.stepIndex if not specified
            final KapuaQuery query = new KapuaQuery(jobStepCreator.getScopeId());
            if (jobStepCreator.getStepIndex() == null) {
                query.setPredicate(query.attributePredicate(JobStepAttributes.JOB_ID, jobStepCreator.getJobId()));
                query.setSortCriteria(query.fieldSortCriteria(JobStepAttributes.STEP_INDEX, SortOrder.DESCENDING));
                query.setLimit(1);

                final JobStepListResult jobStepListResult = jobStepRepository.query(tx, query);
                final JobStep lastJobStep = jobStepListResult.getFirstItem();

                jobStepCreator.setStepIndex(lastJobStep != null ? lastJobStep.getStepIndex() + 1 : JobStepIndex.FIRST);
            }
            // Check if JobStep.stepIndex is duplicate.
            KapuaQuery jobStepQuery = new KapuaQuery(jobStepCreator.getScopeId());
            jobStepQuery.setPredicate(
                    jobStepQuery.andPredicate(
                            jobStepQuery.attributePredicate(JobStepAttributes.JOB_ID, jobStepCreator.getJobId()),
                            jobStepQuery.attributePredicate(JobStepAttributes.STEP_INDEX, jobStepCreator.getStepIndex())
                    )
            );

            JobStep jobStepAtIndex = jobStepRepository.query(tx, jobStepQuery).getFirstItem();

            if (jobStepAtIndex != null) {
                // JobStepCreator inserted between existing JobSteps.
                // Moving existing JobSteps + 1
                KapuaQuery selectorKapuaQuery = new KapuaQuery(jobStepAtIndex.getScopeId());
                selectorKapuaQuery.setPredicate(
                        selectorKapuaQuery.andPredicate(
                                selectorKapuaQuery.attributePredicate(JobStepAttributes.JOB_ID, jobStepAtIndex.getJobId()),
                                selectorKapuaQuery.attributePredicate(JobStepAttributes.STEP_INDEX, jobStepAtIndex.getStepIndex(), Operator.GREATER_THAN_OR_EQUAL)
                        )
                );

                shiftJobStepPosition(tx, selectorKapuaQuery, +1);
            }
            // Create JobStep
            JobStep jobStep = jobStepFactory.newEntity(jobStepCreator.getScopeId());
            jobStep.setName(jobStepCreator.getName());
            jobStep.setDescription(jobStepCreator.getDescription());
            jobStep.setJobId(jobStepCreator.getJobId());
            jobStep.setJobStepDefinitionId(jobStepCreator.getJobStepDefinitionId());
            jobStep.setStepIndex(jobStepCreator.getStepIndex());
            jobStep.setStepProperties(jobStepCreator.getStepProperties());
            // Do create
            return jobStepRepository.create(tx, jobStep);
        });
    }

    @Override
    public JobStep update(JobStep jobStep) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(jobStep, "jobStep");
        ArgumentValidator.notNull(jobStep.getScopeId(), "jobStep.scopeId");
        ArgumentValidator.validateEntityName(jobStep.getName(), "jobStep.name");
        ArgumentValidator.notNull(jobStep.getJobStepDefinitionId(), "jobStep.stepDefinitionId");

        for (JobStepProperty jobStepProperty : jobStep.getStepProperties()) {
            if (jobStepProperty.getPropertyValue() != null) {
                Integer stepPropertyMaxLength = jobStepProperty.getMaxLength() != null ? jobStepProperty.getMaxLength() : jobStepPropertyValueLengthMax;
                ArgumentValidator.lengthRange(jobStepProperty.getPropertyValue(), jobStepProperty.getMinLength(), stepPropertyMaxLength, "jobStep.stepProperties[]." + jobStepProperty.getName());
            }
        }

        if (jobStep.getDescription() != null) {
            ArgumentValidator.numRange(jobStep.getDescription().length(), 0, 8192, "jobStep.description");
        }
        // Check access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.JOB, Actions.write, jobStep.getScopeId()));

        return txManager.execute(tx -> {
            // Check existence
            final JobStep currentJobStep = jobStepRepository.find(tx, jobStep.getScopeId(), jobStep.getId())
                    .orElseThrow(() -> new KapuaEntityNotFoundException(jobStep.getType(), jobStep.getId()));
            // Check job step definition
            validateJobStepProperties(tx, jobStep);
            // Check duplicate name
            if (jobStepRepository.countOtherEntitiesWithNameInScope(
                    tx,
                    jobStep.getScopeId(),
                    jobStep.getId(),
                    jobStep.getName(),
                    jobStep.getJobId()
            ) > 0
            ) {
                throw new KapuaDuplicateNameException(jobStep.getName());
            }
            // Check Job Executions
            if (KapuaSecurityUtils.doPrivileged(() -> jobExecutionService.countByJobId(jobStep.getScopeId(), jobStep.getJobId()) > 0)) {
                throw new CannotModifyJobStepsException(jobStep.getJobId());
            }
            // Do Update
            // Check if JobStep.stepIndex has changed
            if (jobStep.getStepIndex() != currentJobStep.getStepIndex()) {

                if (jobStep.getStepIndex() < currentJobStep.getStepIndex()) {
                    // Moved before current position.
                    // Shift JobSteps +1
                    KapuaQuery selectorKapuaQuery = new KapuaQuery(jobStep.getScopeId());
                    selectorKapuaQuery.setPredicate(
                            selectorKapuaQuery.andPredicate(
                                    selectorKapuaQuery.attributePredicate(JobStepAttributes.JOB_ID, jobStep.getJobId()),
                                    selectorKapuaQuery.attributePredicate(JobStepAttributes.STEP_INDEX, jobStep.getStepIndex(), Operator.GREATER_THAN_OR_EQUAL),
                                    selectorKapuaQuery.attributePredicate(JobStepAttributes.STEP_INDEX, currentJobStep.getStepIndex(), Operator.LESS_THAN)
                            )
                    );

                    shiftJobStepPosition(tx, selectorKapuaQuery, +1);
                } else {
                    // Moved after current position.
                    // Shift JobSteps -1
                    KapuaQuery selectorKapuaQuery = new KapuaQuery(jobStep.getScopeId());
                    selectorKapuaQuery.setPredicate(
                            selectorKapuaQuery.andPredicate(
                                    selectorKapuaQuery.attributePredicate(JobStepAttributes.JOB_ID, jobStep.getJobId()),
                                    selectorKapuaQuery.attributePredicate(JobStepAttributes.STEP_INDEX, currentJobStep.getStepIndex(), Operator.GREATER_THAN),
                                    selectorKapuaQuery.attributePredicate(JobStepAttributes.STEP_INDEX, jobStep.getStepIndex(), Operator.LESS_THAN_OR_EQUAL)
                            )
                    );

                    shiftJobStepPosition(tx, selectorKapuaQuery, -1);
                }
            }

            return jobStepRepository.update(tx, jobStep);

        });
    }

    @Override
    public JobStep find(KapuaId scopeId, KapuaId jobStepId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(jobStepId, "jobStepId");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.JOB, Actions.write, scopeId));
        // Do find
        return txManager.execute(tx -> jobStepRepository.find(tx, scopeId, jobStepId))
                .orElse(null);
    }

    @Override
    public JobStepListResult query(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.JOB, Actions.read, query.getScopeId()));
        // Do query
        return txManager.execute(tx -> jobStepRepository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.JOB, Actions.read, query.getScopeId()));
        // Do query
        return txManager.execute(tx -> jobStepRepository.count(tx, query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId jobStepId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(jobStepId, "jobStepId");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.JOB, Actions.delete, scopeId));

        txManager.execute(tx -> {
            // Check existence
            final JobStep jobStep = jobStepRepository.find(tx, scopeId, jobStepId)
                    .orElseThrow(() -> new KapuaEntityNotFoundException(JobStep.TYPE, jobStepId));
            // Check Job Executions
            final JobExecutionQuery jobExecutionQuery = new JobExecutionQuery(scopeId);
            jobExecutionQuery.setPredicate(
                    jobExecutionQuery.attributePredicate(JobExecutionAttributes.JOB_ID, jobStep.getJobId())
            );

            if (KapuaSecurityUtils.doPrivileged(() -> jobExecutionService.countByJobId(scopeId, jobStep.getJobId())) > 0) {
                throw new CannotModifyJobStepsException(jobStep.getJobId());
            }
            // Do delete
            JobStep deletedJobStep = jobStepRepository.find(tx, scopeId, jobStepId)
                    .orElseThrow(() -> new KapuaEntityNotFoundException(JobStep.TYPE, jobStepId));

            jobStepRepository.delete(tx, scopeId, jobStepId);
            // Shift following steps of one position in the step index
            KapuaQuery query = new KapuaQuery(scopeId);

            query.setPredicate(
                    query.andPredicate(
                            query.attributePredicate(JobStepAttributes.JOB_ID, deletedJobStep.getJobId()),
                            query.attributePredicate(JobStepAttributes.STEP_INDEX, deletedJobStep.getStepIndex(), Operator.GREATER_THAN)
                    )
            );

            JobStepListResult followingJobStep = jobStepRepository.query(tx, query);

            for (JobStep js : followingJobStep.getItems()) {
                js.setStepIndex(js.getStepIndex() - 1);

                jobStepRepository.update(tx, js);
            }
            return jobStep.getJobId();
        });
    }

    @Override
    public int getJobStepPropertyMaxLength() throws KapuaException {
        // Check access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.JOB, Actions.read, KapuaId.ANY));

        // Return the value
        return jobStepPropertyValueLengthMax;
    }

    // Private methods

    /**
     * Shifts {@link JobStep} matched by the given {@link KapuaQuery} according to the given increment.
     *
     * @param tx
     *         The {@link TxContext} which is owning the transaction.
     * @param selectorQuery
     *         The selector {@link KapuaQuery}.
     * @param increment
     *         The increment o apply to the matched {@link JobStep}s
     * @throws KapuaException
     * @since 2.0.0
     */
    private void shiftJobStepPosition(TxContext tx, KapuaQuery selectorQuery, int increment) throws KapuaException {
        selectorQuery.setSortCriteria(selectorQuery.fieldSortCriteria(JobStepAttributes.STEP_INDEX, SortOrder.ASCENDING));

        JobStepListResult followingJobStepListResult = jobStepRepository.query(tx, selectorQuery);

        LoggerFactory.getLogger(JobStepServiceImpl.class).warn("Got {} steps to move", followingJobStepListResult.getSize());

        // Move them +1/-1 position
        for (JobStep followingJobStep : followingJobStepListResult.getItems()) {
            LoggerFactory.getLogger(JobStepServiceImpl.class).warn("Moving step named {} to index {}", followingJobStep.getName(), followingJobStep.getStepIndex() + increment);
            followingJobStep.setStepIndex(followingJobStep.getStepIndex() + increment);
            jobStepRepository.update(tx, followingJobStep);
        }
    }

    private void validateJobStepProperties(TxContext txContext, JobStepCreator jobStepCreator) throws KapuaException {
        JobStepDefinition jobStepDefinition = jobStepDefinitionRepository.find(txContext, KapuaId.ANY, jobStepCreator.getJobStepDefinitionId()).orElse(null);
        ArgumentValidator.notNull(jobStepDefinition, "jobStepCreator.jobStepDefinitionId");

        try {
            validateJobStepProperties(jobStepCreator.getStepProperties(), jobStepDefinition);
        } catch (KapuaIllegalArgumentException kiae) {
            throw new KapuaIllegalArgumentException("jobStepCreator." + kiae.getArgumentName(), kiae.getArgumentValue());
        }
    }

    private void validateJobStepProperties(TxContext txContext, JobStep jobStep) throws KapuaException {
        JobStepDefinition jobStepDefinition = jobStepDefinitionRepository.find(txContext, KapuaId.ANY, jobStep.getJobStepDefinitionId()).orElse(null);
        ArgumentValidator.notNull(jobStepDefinition, "jobStep.jobStepDefinitionId");

        try {
            validateJobStepProperties(jobStep.getStepProperties(), jobStepDefinition);
        } catch (KapuaIllegalArgumentException kiae) {
            throw new KapuaIllegalArgumentException("jobStep." + kiae.getArgumentName(), kiae.getArgumentValue());
        }
    }

    private void validateJobStepProperties(List<JobStepProperty> jobStepProperties, JobStepDefinition jobStepDefinition) throws KapuaIllegalArgumentException {

        for (JobStepProperty jobStepProperty : jobStepProperties) {
            for (JobStepProperty jobStepDefinitionProperty : jobStepDefinition.getStepProperties()) {
                if (jobStepProperty.getName().equals(jobStepDefinitionProperty.getName())) {

                    if (jobStepDefinitionProperty.getRequired()) {
                        ArgumentValidator.notNull(jobStepProperty.getPropertyValue(), "stepProperties[]." + jobStepProperty.getName());
                    }

                    if (jobStepProperty.getPropertyValue() != null) {
                        ArgumentValidator.areEqual(
                                jobStepProperty.getPropertyType(),
                                jobStepDefinitionProperty.getPropertyType(),
                                "stepProperties[]." + jobStepProperty.getName()
                        );
                        ArgumentValidator.lengthRange(
                                jobStepProperty.getPropertyValue(),
                                jobStepDefinitionProperty.getMinLength(),
                                jobStepDefinitionProperty.getMaxLength(),
                                "stepProperties[]." + jobStepProperty.getName()
                        );

                        validateJobStepPropertyValue(jobStepProperty, jobStepDefinitionProperty);
                    }
                }
            }
        }
    }

    private <C extends Comparable<C>, E extends Enum<E>> void validateJobStepPropertyValue(JobStepProperty jobStepProperty, JobStepProperty jobStepDefinitionProperty)
            throws KapuaIllegalArgumentException {
        try {
            Class<?> jobStepDefinitionPropertyClass = Class.forName(jobStepDefinitionProperty.getPropertyType());

            if (Comparable.class.isAssignableFrom(jobStepDefinitionPropertyClass)) {
                Class<C> jobStepDefinitionPropertyClassComparable = (Class<C>) jobStepDefinitionPropertyClass;

                C propertyValue = fromString(jobStepProperty.getPropertyValue(), jobStepDefinitionPropertyClassComparable);
                C propertyMinValue = fromString(jobStepDefinitionProperty.getMinValue(), jobStepDefinitionPropertyClassComparable);
                C propertyMaxValue = fromString(jobStepDefinitionProperty.getMaxValue(), jobStepDefinitionPropertyClassComparable);

                ArgumentValidator.valueRange(propertyValue, propertyMinValue, propertyMaxValue, "stepProperties[]." + jobStepProperty.getName());

                if (String.class.equals(jobStepDefinitionPropertyClass) && !Strings.isNullOrEmpty(jobStepDefinitionProperty.getValidationRegex())) {
                    ArgumentValidator.match((String) propertyValue, () -> Pattern.compile(jobStepDefinitionProperty.getValidationRegex()), "stepProperties[]." + jobStepProperty.getName());
                }
            } else if (KapuaId.class.isAssignableFrom(jobStepDefinitionPropertyClass) ||
                    (jobStepDefinitionPropertyClass == byte[].class || jobStepDefinitionPropertyClass == Byte[].class)) {
                fromString(jobStepProperty.getPropertyValue(), jobStepDefinitionPropertyClass);
            } else if (jobStepDefinitionPropertyClass.isEnum()) {
                Class<E> jobStepDefinitionPropertyClassEnum = (Class<E>) jobStepDefinitionPropertyClass;
                Enum.valueOf(jobStepDefinitionPropertyClassEnum, jobStepProperty.getPropertyValue());
            } else {
                // Try both formats: XML - JSON
                try {
                    xmlUtil.unmarshal(jobStepProperty.getPropertyValue(), jobStepDefinitionPropertyClass);
                } catch (Exception e) {
                    xmlUtil.unmarshalJson(jobStepProperty.getPropertyValue(), jobStepDefinitionPropertyClass);
                }
            }

        } catch (KapuaIllegalArgumentException kiae) {
            throw kiae;
        } catch (Exception e) {
            throw new KapuaIllegalArgumentException("stepProperties[]." + jobStepProperty.getName(), jobStepProperty.getPropertyValue());
        }
    }

    public <T, E extends Enum<E>> T fromString(String jobStepPropertyString, Class<T> type) throws Exception {
        T stepProperty = null;
        if (jobStepPropertyString != null) {
            if (type == String.class) {
                stepProperty = (T) jobStepPropertyString;
            } else if (type == Integer.class) {
                stepProperty = (T) Integer.valueOf(jobStepPropertyString);
            } else if (type == Long.class) {
                stepProperty = (T) Long.valueOf(jobStepPropertyString);
            } else if (type == Float.class) {
                stepProperty = (T) Float.valueOf(jobStepPropertyString);
            } else if (type == Double.class) {
                stepProperty = (T) Double.valueOf(jobStepPropertyString);
            } else if (type == Boolean.class) {
                stepProperty = (T) Boolean.valueOf(jobStepPropertyString);
            } else if (type == byte[].class || type == Byte[].class) {
                stepProperty = (T) DatatypeConverter.parseBase64Binary(jobStepPropertyString);
            } else if (type == KapuaId.class) {
                stepProperty = (T) KapuaEid.parseCompactId(jobStepPropertyString);
            }
        }

        return stepProperty;
    }
}
