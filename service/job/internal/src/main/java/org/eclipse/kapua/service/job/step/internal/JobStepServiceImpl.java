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

import com.google.common.base.Strings;
import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaEntityUniquenessException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.service.internal.AbstractKapuaService;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.locator.KapuaProvider;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AttributePredicate.Operator;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.job.JobDomains;
import org.eclipse.kapua.service.job.exception.CannotModifyJobStepsException;
import org.eclipse.kapua.service.job.execution.JobExecutionAttributes;
import org.eclipse.kapua.service.job.execution.JobExecutionFactory;
import org.eclipse.kapua.service.job.execution.JobExecutionQuery;
import org.eclipse.kapua.service.job.execution.JobExecutionService;
import org.eclipse.kapua.service.job.internal.JobEntityManagerFactory;
import org.eclipse.kapua.service.job.internal.settings.JobServiceSettingKeys;
import org.eclipse.kapua.service.job.internal.settings.JobServiceSettings;
import org.eclipse.kapua.service.job.step.JobStep;
import org.eclipse.kapua.service.job.step.JobStepAttributes;
import org.eclipse.kapua.service.job.step.JobStepCreator;
import org.eclipse.kapua.service.job.step.JobStepIndex;
import org.eclipse.kapua.service.job.step.JobStepListResult;
import org.eclipse.kapua.service.job.step.JobStepQuery;
import org.eclipse.kapua.service.job.step.JobStepService;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinition;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionService;
import org.eclipse.kapua.service.job.step.definition.JobStepProperty;

import javax.xml.bind.DatatypeConverter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * {@link JobStepService} implementation
 *
 * @since 1.0.0
 */
@KapuaProvider
public class JobStepServiceImpl extends AbstractKapuaService implements JobStepService {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final AuthorizationService AUTHORIZATION_SERVICE = LOCATOR.getService(AuthorizationService.class);
    private static final PermissionFactory PERMISSION_FACTORY = LOCATOR.getFactory(PermissionFactory.class);

    private static final JobExecutionService JOB_EXECUTION_SERVICE = LOCATOR.getService(JobExecutionService.class);
    private static final JobExecutionFactory JOB_EXECUTION_FACTORY = LOCATOR.getFactory(JobExecutionFactory.class);

    private static final JobStepDefinitionService JOB_STEP_DEFINITION_SERVICE = LOCATOR.getService(JobStepDefinitionService.class);


    private final JobServiceSettings jobServiceSettings = JobServiceSettings.getInstance();

    /**
     * The maximum length that a {@link JobStepProperty#getPropertyValue()} is allowed to have
     *
     * @since 2.0.0
     */
    private final int jobStepPropertyValueLengthMax = jobServiceSettings.getInt(JobServiceSettingKeys.JOB_STEP_PROPERTY_VALUE_LENGTH_MAX);

    public JobStepServiceImpl() {
        super(JobEntityManagerFactory.getInstance(), null);
    }

    @Override
    public JobStep create(JobStepCreator jobStepCreator) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(jobStepCreator, "jobStepCreator");
        ArgumentValidator.notNull(jobStepCreator.getScopeId(), "jobStepCreator.scopeId");
        ArgumentValidator.validateEntityName(jobStepCreator.getName(), "jobStepCreator.name");
        ArgumentValidator.notNull(jobStepCreator.getJobStepDefinitionId(), "jobStepCreator.stepDefinitionId");

        for (JobStepProperty jobStepProperty : jobStepCreator.getStepProperties()) {
            if (jobStepProperty.getPropertyValue() != null) {
                Integer stepPropertyMaxLength = jobStepProperty.getMaxLength() != null ? jobStepProperty.getMaxLength() : jobStepPropertyValueLengthMax;
                ArgumentValidator.lengthRange(jobStepProperty.getPropertyValue(), jobStepProperty.getMinLength(), stepPropertyMaxLength, "jobStepCreator.stepProperties[]." + jobStepProperty.getName());
            }
        }

        if (jobStepCreator.getDescription() != null) {
            ArgumentValidator.numRange(jobStepCreator.getDescription().length(), 0, 8192, "jobStepCreator.description");
        }

        //
        // Check access
        AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(JobDomains.JOB_DOMAIN, Actions.write, jobStepCreator.getScopeId()));

        //
        // Check job step definition
        validateJobStepProperties(jobStepCreator);

        //
        // Check duplicate name
        JobStepQuery query = new JobStepQueryImpl(jobStepCreator.getScopeId());
        query.setPredicate(
                query.andPredicate(
                        query.attributePredicate(JobStepAttributes.JOB_ID, jobStepCreator.getJobId()),
                        query.attributePredicate(JobStepAttributes.NAME, jobStepCreator.getName())
                )
        );

        if (count(query) > 0) {
            throw new KapuaDuplicateNameException(jobStepCreator.getName());
        }

        //
        // Check Job Executions
        JobExecutionQuery jobExecutionQuery = JOB_EXECUTION_FACTORY.newQuery(jobStepCreator.getScopeId());
        jobExecutionQuery.setPredicate(
                jobExecutionQuery.attributePredicate(JobExecutionAttributes.JOB_ID, jobStepCreator.getJobId())
        );

        if (JOB_EXECUTION_SERVICE.count(jobExecutionQuery) > 0) {
            throw new CannotModifyJobStepsException(jobStepCreator.getJobId());
        }

        //
        // Check step index
        if (jobStepCreator.getStepIndex() == null) {
            query.setPredicate(query.attributePredicate(JobStepAttributes.JOB_ID, jobStepCreator.getJobId()));
            query.setSortCriteria(query.fieldSortCriteria(JobStepAttributes.STEP_INDEX, SortOrder.DESCENDING));
            query.setLimit(1);

            JobStepListResult jobStepListResult = query(query);
            JobStep lastJobStep = jobStepListResult.getFirstItem();

            jobStepCreator.setStepIndex(lastJobStep != null ? lastJobStep.getStepIndex() + 1 : JobStepIndex.FIRST);
        } else {
            query.setPredicate(
                    query.andPredicate(
                            query.attributePredicate(JobStepAttributes.JOB_ID, jobStepCreator.getJobId()),
                            query.attributePredicate(JobStepAttributes.STEP_INDEX, jobStepCreator.getStepIndex())
                    )
            );

            if (count(query) > 0) {
                List<Map.Entry<String, Object>> uniquesFieldValues = new ArrayList<>();

                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(JobStepAttributes.SCOPE_ID, jobStepCreator.getScopeId()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(JobStepAttributes.JOB_ID, jobStepCreator.getJobId()));
                uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(JobStepAttributes.STEP_INDEX, jobStepCreator.getStepIndex()));

                throw new KapuaEntityUniquenessException(JobStep.TYPE, uniquesFieldValues);
            }
        }

        //
        // Do create
        return entityManagerSession.doTransactedAction(em -> JobStepDAO.create(em, jobStepCreator));
    }

    @Override
    public JobStep update(JobStep jobStep) throws KapuaException {
        //
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

        //
        // Check access
        AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(JobDomains.JOB_DOMAIN, Actions.write, jobStep.getScopeId()));

        //
        // Check existence
        if (find(jobStep.getScopeId(), jobStep.getId()) == null) {
            throw new KapuaEntityNotFoundException(jobStep.getType(), jobStep.getId());
        }

        //
        // Check job step definition
        validateJobStepProperties(jobStep);

        //
        // Check duplicate name
        JobStepQuery query = new JobStepQueryImpl(jobStep.getScopeId());
        query.setPredicate(
                query.andPredicate(
                        query.attributePredicate(JobStepAttributes.JOB_ID, jobStep.getJobId()),
                        query.attributePredicate(JobStepAttributes.NAME, jobStep.getName()),
                        query.attributePredicate(JobStepAttributes.ENTITY_ID, jobStep.getId(), Operator.NOT_EQUAL)
                )
        );

        if (count(query) > 0) {
            throw new KapuaDuplicateNameException(jobStep.getName());
        }

        //
        // Check Job Executions
        JobExecutionQuery jobExecutionQuery = JOB_EXECUTION_FACTORY.newQuery(jobStep.getScopeId());
        jobExecutionQuery.setPredicate(
                jobExecutionQuery.attributePredicate(JobExecutionAttributes.JOB_ID, jobStep.getJobId())
        );

        if (JOB_EXECUTION_SERVICE.count(jobExecutionQuery) > 0) {
            throw new CannotModifyJobStepsException(jobStep.getJobId());
        }

        // Do Update
        return entityManagerSession.doTransactedAction(em -> JobStepDAO.update(em, jobStep));
    }

    @Override
    public JobStep find(KapuaId scopeId, KapuaId jobStepId) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(jobStepId, "jobStepId");

        //
        // Check Access
        if (!AUTHORIZATION_SERVICE.isPermitted(PERMISSION_FACTORY.newPermission(JobDomains.JOB_DOMAIN, Actions.read, scopeId))) {
            AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(JobDomains.JOB_DOMAIN, Actions.write, scopeId)); //backward compatibility check
        }

        //
        // Do find
        return entityManagerSession.doAction(em -> JobStepDAO.find(em, scopeId, jobStepId));
    }

    @Override
    public JobStepListResult query(KapuaQuery query) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(query, "query");

        //
        // Check Access
        AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(JobDomains.JOB_DOMAIN, Actions.read, query.getScopeId()));

        //
        // Do query
        return entityManagerSession.doAction(em -> JobStepDAO.query(em, query));
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(query, "query");

        //
        // Check Access
        AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(JobDomains.JOB_DOMAIN, Actions.read, query.getScopeId()));

        //
        // Do query
        return entityManagerSession.doAction(em -> JobStepDAO.count(em, query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId jobStepId) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(jobStepId, "jobStepId");

        //
        // Check Access
        AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(JobDomains.JOB_DOMAIN, Actions.delete, scopeId));

        //
        // Check existence
        JobStep jobStep = find(scopeId, jobStepId);
        if (jobStep == null) {
            throw new KapuaEntityNotFoundException(JobStep.TYPE, jobStepId);
        }

        //
        // Check Job Executions
        JobExecutionQuery jobExecutionQuery = JOB_EXECUTION_FACTORY.newQuery(scopeId);
        jobExecutionQuery.setPredicate(
                jobExecutionQuery.attributePredicate(JobExecutionAttributes.JOB_ID, jobStep.getJobId())
        );

        if (JOB_EXECUTION_SERVICE.count(jobExecutionQuery) > 0) {
            throw new CannotModifyJobStepsException(jobStep.getJobId());
        }

        //
        // Do delete
        entityManagerSession.doTransactedAction(em -> {
            JobStep deletedJobStep = JobStepDAO.find(em, scopeId, jobStepId);

            JobStepDAO.delete(em, scopeId, jobStepId);

            //
            // Shift following steps of one position in the step index
            JobStepQuery query = new JobStepQueryImpl(scopeId);

            query.setPredicate(
                    query.andPredicate(
                            query.attributePredicate(JobStepAttributes.JOB_ID, deletedJobStep.getJobId()),
                            query.attributePredicate(JobStepAttributes.STEP_INDEX, deletedJobStep.getStepIndex(), Operator.GREATER_THAN)
                    )
            );

            JobStepListResult followingJobStep = JobStepDAO.query(em, query);

            for (JobStep js : followingJobStep.getItems()) {
                js.setStepIndex(js.getStepIndex() - 1);

                JobStepDAO.update(em, js);
            }
            return deletedJobStep;
        });
    }

    @Override
    public int getJobStepPropertyMaxLength() throws KapuaException {
        //
        // Check access
        AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(JobDomains.JOB_DOMAIN, Actions.read, KapuaId.ANY));

        //
        // Return the value
        return jobStepPropertyValueLengthMax;
    }

    //
    // Private methods
    private void validateJobStepProperties(JobStepCreator jobStepCreator) throws KapuaException {
        JobStepDefinition jobStepDefinition = JOB_STEP_DEFINITION_SERVICE.find(KapuaId.ANY, jobStepCreator.getJobStepDefinitionId());
        ArgumentValidator.notNull(jobStepDefinition, "jobStepCreator.jobStepDefinitionId");

        try {
            validateJobStepProperties(jobStepCreator.getStepProperties(), jobStepDefinition);
        } catch (KapuaIllegalArgumentException kiae) {
            throw new KapuaIllegalArgumentException("jobStepCreator." + kiae.getArgumentName(), kiae.getArgumentValue());
        }
    }

    private void validateJobStepProperties(JobStep jobStep) throws KapuaException {
        JobStepDefinition jobStepDefinition = JOB_STEP_DEFINITION_SERVICE.find(KapuaId.ANY, jobStep.getJobStepDefinitionId());
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
                        ArgumentValidator.areEqual(jobStepProperty.getPropertyType(), jobStepDefinitionProperty.getPropertyType(), "stepProperties[]." + jobStepProperty.getName());
                        ArgumentValidator.lengthRange(jobStepProperty.getPropertyValue(), jobStepDefinitionProperty.getMinLength(), jobStepDefinitionProperty.getMaxLength(),
                                                      "stepProperties[]." + jobStepProperty.getName());

                        validateJobStepPropertyValue(jobStepProperty, jobStepDefinitionProperty);
                    }
                }
            }
        }
    }

    private <C extends Comparable<C>, E extends Enum<E>> void validateJobStepPropertyValue(JobStepProperty jobStepProperty, JobStepProperty jobStepDefinitionProperty) throws KapuaIllegalArgumentException {
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
                XmlUtil.unmarshal(jobStepProperty.getPropertyValue(), jobStepDefinitionPropertyClass);
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
