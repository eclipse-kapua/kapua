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
package org.eclipse.kapua.service.scheduler.trigger.definition.quartz;

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
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionRepository;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerProperty;
import org.eclipse.kapua.storage.TxContext;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This aligner aligns the declared {@link TriggerDefinition}s in each module with the database.
 *
 * @since 2.1.0
 */
public class TriggerDefinitionAligner {

    private static final Logger LOG = LoggerFactory.getLogger(TriggerDefinitionAligner.class);
    private final TxManager txManager;
    private final TriggerDefinitionRepository triggerDefinitionRepository;
    private final Set<TriggerDefinition> wiredTriggerDefinitions;
    private final Comparator<TriggerDefinition> triggerDefinitionComparator;
    private final Comparator<TriggerProperty> triggerPropertyComparator;

    @Inject
    public TriggerDefinitionAligner(
            @Named("schedulerTxManager") TxManager txManager,
            TriggerDefinitionRepository domainRepository,
            Set<TriggerDefinition> wiredTriggerDefinitions) {
        this.txManager = txManager;
        this.triggerDefinitionRepository = domainRepository;
        this.wiredTriggerDefinitions = wiredTriggerDefinitions;

        triggerDefinitionComparator = Comparator
                .comparing((TriggerDefinition triggerDefinition) ->
                                Optional.ofNullable(triggerDefinition.getScopeId())
                                        .map(KapuaId::getId)
                                        .orElse(null),
                        Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(TriggerDefinition::getName, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(TriggerDefinition::getDescription, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(TriggerDefinition::getTriggerType, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(TriggerDefinition::getProcessorName, Comparator.nullsFirst(Comparator.naturalOrder()));

        triggerPropertyComparator = Comparator
                .comparing(TriggerProperty::getName, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(TriggerProperty::getDescription, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(TriggerProperty::getPropertyType, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(TriggerProperty::getPropertyValue, Comparator.nullsFirst(Comparator.naturalOrder()));
    }

    @KapuaInitializingMethod(priority = 20)
    public void populate() {
        LOG.info("TriggerDefinition alignment commencing. Found {} TriggerDefinition declarations in wiring", wiredTriggerDefinitions.size());
        Map<String, TriggerDefinition> wiredTriggerDefinitionsByName = wiredTriggerDefinitions
                .stream()
                .collect(Collectors.toMap(KapuaNamedEntity::getName, d -> d));

        List<String> wireTriggerDefinitionsNotInDb = new ArrayList<>(wiredTriggerDefinitionsByName.keySet());
        try {
            KapuaSecurityUtils.doPrivileged(() -> {
                txManager.execute(tx -> {
                    // Retrieve all TriggerDefinition from the database
                    List<TriggerDefinitionImpl> dbTriggerDefinitions = triggerDefinitionRepository.query(tx, new KapuaQuery((KapuaId) null)).getItems()
                            .stream()
                            .map(dbTriggrDefinition -> (TriggerDefinitionImpl) dbTriggrDefinition)
                            .collect(Collectors.toList());
                    LOG.info("Found {} TriggerDefinition declarations in database", dbTriggerDefinitions.size());

                    // Check all TriggerDefinition from the database against the wired ones
                    for (TriggerDefinitionImpl dbTriggerDefinition : dbTriggerDefinitions) {
                        if (!wiredTriggerDefinitionsByName.containsKey(dbTriggerDefinition.getName())) {
                            // Leave it be. As we share the database with other components, it might have been created by such components and be hidden from us
                            LOG.warn("TriggerDefinition '{}' is only present in the database but it isn't wired in loaded modules!", dbTriggerDefinition.getName());
                            continue;
                        }

                        // Good news, it's both declared in wiring and present in the db!
                        wireTriggerDefinitionsNotInDb.remove(dbTriggerDefinition.getName());

                        // Trigger fetch of Actions collection from db, otherwise the toString would not show the details
                        dbTriggerDefinition.getTriggerProperties();

                        // Check alignment between known and database TriggerProperty
                        TriggerDefinition wiredTriggerDefinition = wiredTriggerDefinitionsByName.get(dbTriggerDefinition.getName());

                        if (triggerDefinitionComparator.compare(dbTriggerDefinition, wiredTriggerDefinition) == 0) {
                            LOG.info("TriggerDefinition '{}' basic properties are aligned... proceeding checking TriggerProperties...", dbTriggerDefinition.getName());

                            if (triggerDefinitionPropertiesAreEqual(dbTriggerDefinition, wiredTriggerDefinition)) {
                                //We are happy!
                                LOG.info("TriggerDefinition '{}' is aligned!", dbTriggerDefinition.getName());
                                continue;
                            }
                        }

                        LOG.info("TriggerDefinition '{}' is not aligned!", dbTriggerDefinition.getName());

                        // Align them!
                        alignTriggerDefinitions(tx, dbTriggerDefinition, wiredTriggerDefinition);
                    }

                    if (wireTriggerDefinitionsNotInDb.isEmpty()) {
                        LOG.info("All wired TriggerDefinition were already present in the database");
                    } else {
                        LOG.info("There are {} wired TriggerDefinition not present on the database", wireTriggerDefinitionsNotInDb.size());

                        for (String wiredTriggerDefinitionsNotInDbName : wireTriggerDefinitionsNotInDb) {
                            LOG.info("Creating new TriggerDefinition '{}'...", wiredTriggerDefinitionsNotInDbName);

                            createNewTriggerDefinition(tx, wiredTriggerDefinitionsByName.get(wiredTriggerDefinitionsNotInDbName));

                            LOG.info("Creating new TriggerDefinition '{}'... DONE!", wiredTriggerDefinitionsNotInDbName);
                        }
                    }

                    LOG.info("TriggerDefinition alignment complete!");
                    return null;
                });
            });
        } catch (KapuaException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the given {@link TriggerDefinition} from the database matches the {@link TriggerDefinition} wired in the module.
     *
     * @param dbTriggerDefinition
     *         The {@link TriggerDefinition} from the database
     * @param wiredTriggerDefinition
     *         The {@link TriggerDefinition} wired in the module
     * @return {@code true} if they match, {@code false} otherwise
     * @since 2.1.0
     */
    private boolean triggerDefinitionPropertiesAreEqual(TriggerDefinition dbTriggerDefinition, TriggerDefinition wiredTriggerDefinition) {
        for (TriggerProperty wiredTriggerDefinitionProperty : wiredTriggerDefinition.getTriggerProperties()) {
            TriggerProperty dbTriggerDefinitionProperty = dbTriggerDefinition.getTriggerProperty(wiredTriggerDefinitionProperty.getName());

            if (dbTriggerDefinitionProperty == null) {
                LOG.warn("Wired TriggerProperty '{}' of TriggerDefinition '{}' is not aligned with the database one",
                        wiredTriggerDefinitionProperty.getName(),
                        wiredTriggerDefinition.getName());

                return false;
            }

            if (triggerPropertyComparator.compare(dbTriggerDefinitionProperty, wiredTriggerDefinitionProperty) != 0) {
                LOG.warn("Database TriggerProperty '{}' of TriggerDefinition '{}' is not aligned with the wired one",
                        dbTriggerDefinitionProperty.getName(),
                        dbTriggerDefinition.getName());

                return false;
            }
        }

        return true;
    }

    /**
     * Aligns the {@link TriggerProperty} from the database to match the {@link TriggerDefinition} wired in the modules.
     *
     * @param txContext
     *         The {@link TxContext} of the transaction.
     * @param dbTriggerDefinition
     *         The {@link TriggerDefinition} from the database to align
     * @param wiredTriggerDefinition
     *         The {@link TriggerDefinition} wired in the modules with the correct values.
     * @throws KapuaException
     * @since 2.1.0
     */
    private void alignTriggerDefinitions(TxContext txContext, TriggerDefinitionImpl dbTriggerDefinition, TriggerDefinition wiredTriggerDefinition) throws KapuaException {
        LOG.info("TriggerDefinition '{}' aligning...", dbTriggerDefinition.getName());

        dbTriggerDefinition.setScopeId(wiredTriggerDefinition.getScopeId());
        dbTriggerDefinition.setName(wiredTriggerDefinition.getName());
        dbTriggerDefinition.setDescription(wiredTriggerDefinition.getDescription());
        dbTriggerDefinition.setTriggerType(wiredTriggerDefinition.getTriggerType());
        dbTriggerDefinition.setProcessorName(wiredTriggerDefinition.getProcessorName());

        EntityManager entityManager = JpaAwareTxContext.extractEntityManager(txContext);

        Map<String, TriggerDefinitionPropertyImpl> dbPropertiesByName = dbTriggerDefinition.getTriggerPropertiesEntities()
                .stream()
                .collect(Collectors.toMap(triggerProperty -> triggerProperty.getId().getName(), triggerDefinitionProperty -> triggerDefinitionProperty));

        for (TriggerProperty wiredTriggerProperty : wiredTriggerDefinition.getTriggerProperties()) {
            TriggerDefinitionPropertyImpl dbTriggerPropertyEntity = dbPropertiesByName.get(wiredTriggerProperty.getName());

            if (dbTriggerPropertyEntity == null) {
                LOG.info("Wired TriggerProperty '{}' is not present in the database... Adding to database", wiredTriggerProperty.getName());
                TriggerDefinitionPropertyImpl dbMissingTriggerProperty = new TriggerDefinitionPropertyImpl(dbTriggerDefinition, wiredTriggerProperty);
                entityManager.persist(dbMissingTriggerProperty);
            } else {
                LOG.info("Wired TriggerProperty '{}' is in the database, but is not aligned... Aligning database", wiredTriggerProperty.getName());
                dbTriggerPropertyEntity.getTriggerProperty().setDescription(wiredTriggerProperty.getDescription());
                dbTriggerPropertyEntity.getTriggerProperty().setPropertyType(wiredTriggerProperty.getPropertyType());
                dbTriggerPropertyEntity.getTriggerProperty().setPropertyValue(wiredTriggerProperty.getPropertyValue());

                entityManager.merge(dbTriggerPropertyEntity);
            }
        }

        // Removing from database what is not wired in the application
        Map<String, TriggerProperty> wiredPropertiesByName = wiredTriggerDefinition.getTriggerProperties()
                .stream()
                .collect(Collectors.toMap(TriggerProperty::getName, triggerProperty -> triggerProperty));

        dbTriggerDefinition.getTriggerPropertiesEntities()
                .removeIf(dbTriggerPropertyEntity -> {
                    TriggerProperty triggerProperty = wiredPropertiesByName.get(dbTriggerPropertyEntity.getTriggerProperty().getName());

                    if (triggerProperty == null) {
                        LOG.info("Database TriggerProperty '{}' is not wired... Removing from database", dbTriggerPropertyEntity.getTriggerProperty().getName());
                        return true;
                    } else {
                        return false;
                    }
                });

        LOG.info("TriggerDefinition '{}' aligning... DONE!", dbTriggerDefinition.getName());
    }

    /**
     * Creates a new {@link TriggerDefinition} into the database from the given wired {@link TriggerDefinition}
     *
     * @param tx
     *         The {@link TxContext} of the transaction
     * @param wiredTriggerDefinitionNotInDb
     *         The wired {@link TriggerDefinition}
     * @throws KapuaException
     * @since 2.1.0
     */
    private void createNewTriggerDefinition(TxContext tx, TriggerDefinition wiredTriggerDefinitionNotInDb) throws KapuaException {

        TriggerDefinitionImpl newTriggerDefinition = new TriggerDefinitionImpl();
        newTriggerDefinition.setScopeId(wiredTriggerDefinitionNotInDb.getScopeId());
        newTriggerDefinition.setName(wiredTriggerDefinitionNotInDb.getName());
        newTriggerDefinition.setDescription(wiredTriggerDefinitionNotInDb.getDescription());
        newTriggerDefinition.setTriggerType(wiredTriggerDefinitionNotInDb.getTriggerType());
        newTriggerDefinition.setProcessorName(wiredTriggerDefinitionNotInDb.getProcessorName());
        newTriggerDefinition.setTriggerProperties(wiredTriggerDefinitionNotInDb.getTriggerProperties());

        triggerDefinitionRepository.create(tx, newTriggerDefinition);
    }
}
