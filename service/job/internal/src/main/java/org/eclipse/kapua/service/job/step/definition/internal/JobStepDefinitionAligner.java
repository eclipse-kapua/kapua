/*******************************************************************************
 * Copyright (c) 2024, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.job.step.definition.internal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.JpaAwareTxContext;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.initializers.KapuaInitializingMethod;
import org.eclipse.kapua.model.KapuaNamedEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinition;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionRepository;
import org.eclipse.kapua.service.job.step.definition.JobStepProperty;
import org.eclipse.kapua.storage.TxContext;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This aligner aligns the declared {@link JobStepDefinition}s in each module with the database.
 *
 * @since 2.0.0
 */
public class JobStepDefinitionAligner {

    private static final Logger LOG = LoggerFactory.getLogger(JobStepDefinitionAligner.class);
    private final TxManager txManager;
    private final JobStepDefinitionRepository jobStepDefinitionRepository;
    private final Set<JobStepDefinition> wiredJobStepDefinitions;
    private final Comparator<JobStepDefinition> jobStepDefinitionComparator;
    private final Comparator<JobStepProperty> jobStepPropertyComparator;

    @Inject
    public JobStepDefinitionAligner(@Named("jobTxManager") TxManager txManager,
            JobStepDefinitionRepository domainRepository,
            Set<JobStepDefinition> wiredJobStepDefinitions) {
        this.txManager = txManager;
        this.jobStepDefinitionRepository = domainRepository;
        this.wiredJobStepDefinitions = wiredJobStepDefinitions;

        jobStepDefinitionComparator = Comparator
                .comparing((JobStepDefinition jsp) ->
                                Optional.ofNullable(jsp.getScopeId())
                                        .map(KapuaId::getId)
                                        .orElse(null),
                        Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(JobStepDefinition::getName, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(JobStepDefinition::getDescription, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(JobStepDefinition::getStepType, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(JobStepDefinition::getReaderName, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(JobStepDefinition::getProcessorName, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(JobStepDefinition::getWriterName, Comparator.nullsFirst(Comparator.naturalOrder()));

        jobStepPropertyComparator = Comparator
                .comparing(JobStepProperty::getName, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(JobStepProperty::getDescription, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(JobStepProperty::getPropertyType, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(JobStepProperty::getPropertyValue, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(JobStepProperty::getExampleValue, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(JobStepProperty::getRequired, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(JobStepProperty::getSecret, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(JobStepProperty::getMinLength, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(JobStepProperty::getMaxLength, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(JobStepProperty::getMinValue, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(JobStepProperty::getMaxValue, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(JobStepProperty::getValidationRegex, Comparator.nullsFirst(Comparator.naturalOrder()));
    }

    @KapuaInitializingMethod(priority = 20)
    public void populate() {
        LOG.info("JobStepDefinitions alignment commencing. Found {} JobStepDefinition declarations in wiring", wiredJobStepDefinitions.size());
        Map<String, JobStepDefinition> wiredJobStepDefinitionsByName = wiredJobStepDefinitions
                .stream()
                .collect(Collectors.toMap(KapuaNamedEntity::getName, d -> d));

        List<String> wiredJobStepDefinitionsNotInDb = new ArrayList<>(wiredJobStepDefinitionsByName.keySet());
        try {
            KapuaSecurityUtils.doPrivileged(() -> {
                txManager.execute(tx -> {
                    // Retrieve all JobStepDefinition from the database
                    List<JobStepDefinitionImpl> dbJobStepDefinitions = jobStepDefinitionRepository.query(tx, new KapuaQuery((KapuaId) null)).getItems()
                            .stream()
                            .map(dbJobStepDefinition -> (JobStepDefinitionImpl) dbJobStepDefinition)
                            .collect(Collectors.toList());
                    LOG.info("Found {} JobStepDefinition declarations in database", dbJobStepDefinitions.size());

                    // Check all JobStepDefinition from the database against the wired ones
                    for (JobStepDefinitionImpl dbJobStepDefinition : dbJobStepDefinitions) {
                        if (!wiredJobStepDefinitionsByName.containsKey(dbJobStepDefinition.getName())) {
                            // Leave it be. As we share the database with other components, it might have been created by such components and be hidden from us
                            LOG.warn("JobStepDefinition '{}' is only present in the database but it isn't wired in loaded modules!", dbJobStepDefinition.getName());
                            continue;
                        }

                        // Good news, it's both declared in wiring and present in the db!
                        wiredJobStepDefinitionsNotInDb.remove(dbJobStepDefinition.getName());

                        // Trigger fetch of Actions collection from db, otherwise the toString would not show the details
                        dbJobStepDefinition.getStepProperties();

                        // Check alignment between known and DB JobStepProperty
                        JobStepDefinition wiredJobStepDefinition = wiredJobStepDefinitionsByName.get(dbJobStepDefinition.getName());

                        if (jobStepDefinitionComparator.compare(dbJobStepDefinition, wiredJobStepDefinition) == 0) {
                            LOG.info("JobStepDefinition '{}' basic properties are aligned... proceeding checking JobStepProperties...", dbJobStepDefinition.getName());

                            if (jobStepDefinitionPropertiesAreEqual(dbJobStepDefinition, wiredJobStepDefinition)) {
                                //We are happy!
                                LOG.info("JobStepDefinition '{}' is aligned!", dbJobStepDefinition.getName());
                                continue;
                            }
                        }

                        LOG.info("JobStepDefinition '{}' is not aligned!", dbJobStepDefinition.getName());

                        // Align them!
                        alignJobStepDefinitions(tx, dbJobStepDefinition, wiredJobStepDefinition);
                    }

                    if (wiredJobStepDefinitionsNotInDb.isEmpty()) {
                        LOG.info("All wired JobStepDefinition were already present in the database");
                    } else {
                        LOG.info("There are {} wired JobStepDefinition not present on the database", wiredJobStepDefinitionsNotInDb.size());

                        for (String wiredJobStepDefinitionsNotInDbName : wiredJobStepDefinitionsNotInDb) {
                            LOG.info("Creating new JobStepDefinition '{}'...", wiredJobStepDefinitionsNotInDbName);

                            createNewJobStepDefinition(tx, wiredJobStepDefinitionsByName.get(wiredJobStepDefinitionsNotInDbName));

                            LOG.info("Creating new JobStepDefinition '{}'... DONE!", wiredJobStepDefinitionsNotInDbName);
                        }
                    }

                    LOG.info("JobStepDefinition alignment complete!");
                    return null;
                });
            });
        } catch (KapuaException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the given {@link JobStepDefinition} from the database matches the {@link JobStepDefinition} wired in the module.
     *
     * @param dbJobStepDefinition
     *         The {@link JobStepDefinition} from the database
     * @param wiredJobStepDefinition
     *         The {@link JobStepDefinition} wired in the module
     * @return {@code true} if they match, {@code false} otherwise
     * @since 2.0.0
     */
    private boolean jobStepDefinitionPropertiesAreEqual(JobStepDefinition dbJobStepDefinition, JobStepDefinition wiredJobStepDefinition) {
        for (JobStepProperty wiredJobStepDefinitionProperty : wiredJobStepDefinition.getStepProperties()) {
            JobStepProperty dbJobStepDefinitionProperty = dbJobStepDefinition.getStepProperty(wiredJobStepDefinitionProperty.getName());

            if (dbJobStepDefinitionProperty == null) {
                LOG.warn("Wired JobStepProperty '{}' of JobStepDefinition '{}' is not aligned with the database one",
                        wiredJobStepDefinitionProperty.getName(),
                        wiredJobStepDefinitionProperty.getName());

                return false;
            }

            if (jobStepPropertyComparator.compare(dbJobStepDefinitionProperty, wiredJobStepDefinitionProperty) != 0) {
                LOG.warn("Database JobStepProperty '{}' of JobStepDefinition '{}' is not aligned with the wired one",
                        dbJobStepDefinitionProperty.getName(),
                        dbJobStepDefinition.getName());

                return false;
            }
        }

        return true;
    }

    /**
     * Aligns the {@link JobStepProperty} from the database to match the {@link JobStepDefinition} wired in the modules.
     *
     * @param txContext
     *         The {@link TxContext} of the transaction.
     * @param dbJobStepDefinition
     *         The {@link JobStepDefinition} from the database to align
     * @param wiredJobStepDefinition
     *         The {@link JobStepDefinition} wired in the modules with the correct values.
     * @throws KapuaException
     * @since 2.0.0
     */
    private void alignJobStepDefinitions(TxContext txContext, JobStepDefinitionImpl dbJobStepDefinition, JobStepDefinition wiredJobStepDefinition) throws KapuaException {
        LOG.info("JobStepDefinition '{}' aligning...", dbJobStepDefinition.getName());

        dbJobStepDefinition.setScopeId(wiredJobStepDefinition.getScopeId());
        dbJobStepDefinition.setName(wiredJobStepDefinition.getName());
        dbJobStepDefinition.setDescription(wiredJobStepDefinition.getDescription());
        dbJobStepDefinition.setStepType(wiredJobStepDefinition.getStepType());
        dbJobStepDefinition.setReaderName(wiredJobStepDefinition.getReaderName());
        dbJobStepDefinition.setProcessorName(wiredJobStepDefinition.getProcessorName());
        dbJobStepDefinition.setWriterName(wiredJobStepDefinition.getWriterName());

        EntityManager entityManager = JpaAwareTxContext.extractEntityManager(txContext);

        Map<String, JobStepDefinitionPropertyImpl> dbPropertiesByName = dbJobStepDefinition.getStepPropertiesEntitites()
                .stream()
                .collect(Collectors.toMap(jsp -> jsp.getId().getName(), jsp -> jsp));

        for (JobStepProperty wiredJobStepProperty : wiredJobStepDefinition.getStepProperties()) {
            JobStepDefinitionPropertyImpl dbJobStepPropertyEntity = dbPropertiesByName.get(wiredJobStepProperty.getName());

            if (dbJobStepPropertyEntity == null) {
                JobStepDefinitionPropertyImpl dbMissingJobStepProperty =
                        new JobStepDefinitionPropertyImpl(dbJobStepDefinition, wiredJobStepProperty);
                entityManager.persist(dbMissingJobStepProperty);
            } else {
                dbJobStepPropertyEntity.getJobStepProperty().setDescription(wiredJobStepProperty.getDescription());
                dbJobStepPropertyEntity.getJobStepProperty().setPropertyType(wiredJobStepProperty.getPropertyType());
                dbJobStepPropertyEntity.getJobStepProperty().setPropertyValue(wiredJobStepProperty.getPropertyValue());
                dbJobStepPropertyEntity.getJobStepProperty().setRequired(wiredJobStepProperty.getRequired());
                dbJobStepPropertyEntity.getJobStepProperty().setSecret(wiredJobStepProperty.getSecret());
                dbJobStepPropertyEntity.getJobStepProperty().setExampleValue(wiredJobStepProperty.getExampleValue());
                dbJobStepPropertyEntity.getJobStepProperty().setMinLength(wiredJobStepProperty.getMinLength());
                dbJobStepPropertyEntity.getJobStepProperty().setMaxLength(wiredJobStepProperty.getMaxLength());
                dbJobStepPropertyEntity.getJobStepProperty().setMinValue(wiredJobStepProperty.getMinValue());
                dbJobStepPropertyEntity.getJobStepProperty().setMaxValue(wiredJobStepProperty.getMaxValue());
                dbJobStepPropertyEntity.getJobStepProperty().setValidationRegex(wiredJobStepProperty.getValidationRegex());

                entityManager.merge(dbJobStepPropertyEntity);
            }
        }

        LOG.info("JobStepDefinition '{}' aligning... DONE!", dbJobStepDefinition.getName());

    }

    /**
     * Creates a new {@link JobStepDefinition} into the database from the given wired {@link JobStepDefinition}
     *
     * @param tx
     *         The {@link TxContext} of the transaction
     * @param wiredJobStepDefinitionNotInDb
     *         The wired {@link JobStepDefinition}
     * @throws KapuaException
     * @since 2.0.0
     */
    private void createNewJobStepDefinition(TxContext tx, JobStepDefinition wiredJobStepDefinitionNotInDb) throws KapuaException {

        JobStepDefinitionImpl newJobStepDefinition = new JobStepDefinitionImpl();
        newJobStepDefinition.setScopeId(wiredJobStepDefinitionNotInDb.getScopeId());
        newJobStepDefinition.setName(wiredJobStepDefinitionNotInDb.getName());
        newJobStepDefinition.setDescription(wiredJobStepDefinitionNotInDb.getDescription());
        newJobStepDefinition.setStepType(wiredJobStepDefinitionNotInDb.getStepType());
        newJobStepDefinition.setReaderName(wiredJobStepDefinitionNotInDb.getReaderName());
        newJobStepDefinition.setProcessorName(wiredJobStepDefinitionNotInDb.getProcessorName());
        newJobStepDefinition.setWriterName(wiredJobStepDefinitionNotInDb.getWriterName());
        newJobStepDefinition.setStepProperties(wiredJobStepDefinitionNotInDb.getStepProperties());

        jobStepDefinitionRepository.create(tx, newJobStepDefinition);
    }
}
