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
package org.eclipse.kapua.service.scheduler.trigger.quartz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.scheduler.exception.TriggerInvalidDatesException;
import org.eclipse.kapua.service.scheduler.exception.TriggerInvalidSchedulingException;
import org.eclipse.kapua.service.scheduler.quartz.driver.QuartzTriggerDriver;
import org.eclipse.kapua.service.scheduler.quartz.driver.exception.TriggerNeverFiresException;
import org.eclipse.kapua.service.scheduler.trigger.Trigger;
import org.eclipse.kapua.service.scheduler.trigger.TriggerCreator;
import org.eclipse.kapua.service.scheduler.trigger.TriggerFactory;
import org.eclipse.kapua.service.scheduler.trigger.TriggerListResult;
import org.eclipse.kapua.service.scheduler.trigger.TriggerRepository;
import org.eclipse.kapua.service.scheduler.trigger.TriggerService;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionFactory;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionRepository;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerProperty;
import org.eclipse.kapua.storage.TxContext;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link TriggerService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class TriggerServiceImpl implements TriggerService {

    private static final Logger LOG = LoggerFactory.getLogger(TriggerServiceImpl.class);

    /**
     * Lazy {@code static} reference to {@link TriggerDefinition} named "Inteval Job".
     *
     * @since 1.2.0
     */
    private TriggerDefinition intervalJobTriggerDefinition;

    /**
     * Lazy {@code static} reference to {@link TriggerDefinition} named "Cron Job".
     *
     * @since 1.2.0
     */
    private TriggerDefinition cronJobTriggerDefinition;

    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final TxManager txManager;
    private final TriggerRepository triggerRepository;
    private final TriggerFactory triggerFactory;
    private final TriggerDefinitionRepository triggerDefinitionRepository;
    private final TriggerDefinitionFactory triggerDefinitionFactory;

    public TriggerServiceImpl(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            TxManager txManager,
            TriggerRepository triggerRepository,
            TriggerFactory triggerFactory,
            TriggerDefinitionRepository triggerDefinitionRepository,
            TriggerDefinitionFactory triggerDefinitionFactory) {
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.txManager = txManager;
        this.triggerRepository = triggerRepository;
        this.triggerFactory = triggerFactory;
        this.triggerDefinitionRepository = triggerDefinitionRepository;
        this.triggerDefinitionFactory = triggerDefinitionFactory;
    }

    @Override
    public Trigger create(TriggerCreator triggerCreator) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(triggerCreator, "triggerCreator");
        ArgumentValidator.notNull(triggerCreator.getScopeId(), "triggerCreator.scopeId");
        ArgumentValidator.validateEntityName(triggerCreator.getName(), "triggerCreator.name");
        ArgumentValidator.notNull(triggerCreator.getStartsOn(), "triggerCreator.startsOn");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.SCHEDULER, Actions.write, triggerCreator.getScopeId()));

        // Convert creator to new model.
        // To be removed after removing of TriggerCreator.cronScheduling and TriggerCreator.retryInterval
        adaptTriggerCreator(triggerCreator);
        //TODO: review, added this back just to pass tests, not sure if it is a legit check
        ArgumentValidator.notNull(triggerCreator.getTriggerDefinitionId(), "triggerCreator.triggerDefinitionId");

        return txManager.execute(tx -> {
            // Check trigger definition
            TriggerDefinition triggerDefinition = triggerDefinitionRepository.find(tx, KapuaId.ANY, triggerCreator.getTriggerDefinitionId())
                    .orElseThrow(() -> new KapuaEntityNotFoundException(TriggerDefinition.TYPE, triggerCreator.getTriggerDefinitionId()));

            final Map<String, TriggerProperty> triggerDefinitionPropertiesByName = triggerDefinition.getTriggerProperties().stream()
                    .collect(Collectors.toMap(jsdp -> jsdp.getName(), Function.identity()));
            for (TriggerProperty jsp : triggerCreator.getTriggerProperties()) {
                final TriggerProperty jsdp = triggerDefinitionPropertiesByName.get(jsp.getName());
                if (jsdp == null) {
                    continue;
                }
                ArgumentValidator.areEqual(jsp.getPropertyType(), jsdp.getPropertyType(), "triggerCreator.triggerProperties{}." + jsp.getName() + ".propertyType");
                ArgumentValidator.notNull(jsp.getPropertyValue(), "triggerCreator.triggerProperties{}." + jsp.getName() + ".propertyValue");
            }

            // Check duplicate name
            if (triggerRepository.countEntitiesWithName(tx, triggerCreator.getName()) > 0) {
                throw new KapuaDuplicateNameException(triggerCreator.getName());
            }

            // Check dates
            if (triggerCreator.getEndsOn() != null) {
                Date startTime = triggerCreator.getStartsOn();
                Date endTime = triggerCreator.getEndsOn();

                if (endTime.before(new Date()) ||
                        startTime.getTime() == endTime.getTime() ||
                        startTime.after(endTime)) {
                    throw new TriggerInvalidDatesException(startTime, endTime, new Date());
                }
            }
            // Do create
            try {
                Trigger toBeCreated = triggerFactory.newEntity(triggerCreator.getScopeId());
                toBeCreated.setName(triggerCreator.getName());
                toBeCreated.setDescription(triggerCreator.getDescription());
                toBeCreated.setStartsOn(triggerCreator.getStartsOn());
                toBeCreated.setEndsOn(triggerCreator.getEndsOn());
                toBeCreated.setTriggerDefinitionId(triggerCreator.getTriggerDefinitionId());
                toBeCreated.setTriggerProperties(triggerCreator.getTriggerProperties());

                Trigger trigger = triggerRepository.create(tx, toBeCreated);

                // Quartz Job definition and creation
                if (getIntervalJobTriggerDefinition().getId().equals(triggerCreator.getTriggerDefinitionId())) {
                    QuartzTriggerDriver.createIntervalJobTrigger(trigger);
                } else if (getCronJobTriggerDefinition().getId().equals(triggerCreator.getTriggerDefinitionId())) {
                    QuartzTriggerDriver.createCronJobTrigger(trigger);
                }
                //else {
                // Is a DeviceConnect trigger
                //}
                return trigger;
            } catch (TriggerNeverFiresException tnfe) {
                TriggerProperty schedulingTriggerProperty = null;
                if (getIntervalJobTriggerDefinition().getId().equals(triggerCreator.getTriggerDefinitionId())) {
                    schedulingTriggerProperty = triggerCreator.getTriggerProperty("interval");
                } else if (getCronJobTriggerDefinition().getId().equals(triggerCreator.getTriggerDefinitionId())) {
                    schedulingTriggerProperty = triggerCreator.getTriggerProperty("cronExpression");
                }

                throw new TriggerInvalidSchedulingException(
                        tnfe,
                        triggerCreator.getStartsOn(),
                        triggerCreator.getEndsOn(),
                        triggerCreator.getTriggerDefinitionId(),
                        schedulingTriggerProperty != null ? schedulingTriggerProperty.getPropertyValue() : null);
            }
        });
    }

    @Override
    public Trigger update(Trigger trigger) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(trigger.getScopeId(), "trigger.scopeId");
        ArgumentValidator.notNull(trigger.getId(), "trigger.id");
        ArgumentValidator.validateEntityName(trigger.getName(), "trigger.name");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.SCHEDULER, Actions.write, trigger.getScopeId()));

        return txManager.execute(tx -> {
            // Check existence
            if (!triggerRepository.find(tx, trigger.getScopeId(), trigger.getId()).isPresent()) {
                throw new KapuaEntityNotFoundException(trigger.getType(), trigger.getId());
            }

            final Trigger adapted = adaptTrigger(tx, trigger);
            // Check trigger definition
            TriggerDefinition triggerDefinition = triggerDefinitionRepository.find(tx, KapuaId.ANY, adapted.getTriggerDefinitionId())
                    .orElseThrow(() -> new KapuaEntityNotFoundException(TriggerDefinition.TYPE, adapted.getTriggerDefinitionId()));

            for (TriggerProperty jsp : adapted.getTriggerProperties()) {
                for (TriggerProperty jsdp : triggerDefinition.getTriggerProperties()) {
                    if (jsp.getName().equals(jsdp.getName())) {
                        ArgumentValidator.areEqual(jsp.getPropertyType(), jsdp.getPropertyType(), "trigger.triggerProperties[]." + jsp.getName());
                        ArgumentValidator.notNull(jsp.getPropertyType(), "trigger.triggerProperties{}." + jsp.getName());
                        break;
                    }
                }
            }

            // Check duplicate name
            if (triggerRepository.countOtherEntitiesWithNameInScope(tx, adapted.getScopeId(), adapted.getId(), adapted.getName()) > 0) {
                throw new KapuaDuplicateNameException(adapted.getName());
            }
            // Check dates
            if (adapted.getEndsOn() != null) {
                Date startTime = new Date(adapted.getStartsOn().getTime());
                Date endTime = new Date(adapted.getEndsOn().getTime());

                if (endTime.before(new Date()) ||
                        startTime.getTime() == (endTime.getTime()) ||
                        startTime.after(endTime)) {
                    throw new TriggerInvalidDatesException(startTime, endTime, new Date());
                }
            }
            // Do update
            try {
                Trigger updatedTrigger = triggerRepository.update(tx, adapted);

                // Quartz Job definition and creation
                if (getIntervalJobTriggerDefinition().getId().equals(updatedTrigger.getTriggerDefinitionId())) {
                    QuartzTriggerDriver.deleteTrigger(updatedTrigger);
                    QuartzTriggerDriver.createIntervalJobTrigger(adapted);
                } else if (getCronJobTriggerDefinition().getId().equals(updatedTrigger.getTriggerDefinitionId())) {
                    QuartzTriggerDriver.deleteTrigger(updatedTrigger);
                    QuartzTriggerDriver.createCronJobTrigger(adapted);
                }
                //else {
                // Is a DeviceConnect trigger
                //}

                return updatedTrigger;
            } catch (TriggerNeverFiresException tnfe) {
                TriggerProperty schedulingTriggerProperty = null;
                if (getIntervalJobTriggerDefinition().getId().equals(adapted.getTriggerDefinitionId())) {
                    schedulingTriggerProperty = adapted.getTriggerProperty("interval");
                } else if (getCronJobTriggerDefinition().getId().equals(adapted.getTriggerDefinitionId())) {
                    schedulingTriggerProperty = adapted.getTriggerProperty("cronExpression");
                }

                throw new TriggerInvalidSchedulingException(
                        tnfe,
                        adapted.getStartsOn(),
                        adapted.getEndsOn(),
                        adapted.getTriggerDefinitionId(),
                        schedulingTriggerProperty != null ? schedulingTriggerProperty.getPropertyValue() : null
                );
            }
        });
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId triggerId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(triggerId, "scopeId");
        ArgumentValidator.notNull(scopeId, "triggerId");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.SCHEDULER, Actions.delete, scopeId));

        // Do delete
        QuartzTriggerDriver.deleteTrigger(txManager.execute(tx -> {
            return triggerRepository.delete(tx, scopeId, triggerId);
        }));
    }

    @Override
    public Trigger find(KapuaId scopeId, KapuaId triggerId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(triggerId, "triggerId");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.SCHEDULER, Actions.read, scopeId));

        // Do find
        return txManager.execute(tx -> {
            final Optional<Trigger> trigger = triggerRepository.find(tx, scopeId, triggerId);
            if (trigger.isPresent()) {
                return adaptTrigger(tx, trigger.get());
            }
            return null;
        });
    }

    @Override
    public TriggerListResult query(KapuaQuery query) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.SCHEDULER, Actions.read, query.getScopeId()));
        return txManager.execute(tx -> {
            // Do query
            TriggerListResult triggers = triggerRepository.query(tx, query);
            final TriggerListResult res = new TriggerListResult();
            for (Trigger trigger : triggers.getItems()) {
                res.addItem(adaptTrigger(tx, trigger));
            }

            return triggers;
        });
    }

    @Override
    public void deleteAllByJobId(KapuaId scopeId, KapuaId jobId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(jobId, "jobId");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.SCHEDULER, Actions.delete, scopeId));
        txManager.execute(tx -> {
            triggerRepository.deleteAllByJobId(tx, scopeId, jobId);
            return null;
        });
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.SCHEDULER, Actions.read, query.getScopeId()));
        // Do count
        return txManager.execute(tx -> triggerRepository.count(tx, query));
    }
    // Private methods

    /**
     * Gets the {@link TriggerDefinition} named 'Interval Job'
     *
     * @return he {@link TriggerDefinition} named 'Interval Job'
     * @throws KapuaException
     *         In case is not found.
     * @since 1.1.0
     */
    private synchronized TriggerDefinition getIntervalJobTriggerDefinition() throws KapuaException {
        if (intervalJobTriggerDefinition == null) {
            intervalJobTriggerDefinition = getTriggerDefinition("Interval Job");
        }

        return intervalJobTriggerDefinition;
    }

    /**
     * Gets the {@link TriggerDefinition} named 'Cron Job'
     *
     * @return he {@link TriggerDefinition} named 'Cron Job'
     * @throws KapuaException
     *         In case is not found.
     * @since 1.1.0
     */
    private synchronized TriggerDefinition getCronJobTriggerDefinition() throws KapuaException {
        if (cronJobTriggerDefinition == null) {
            cronJobTriggerDefinition = getTriggerDefinition("Cron Job");
        }

        return cronJobTriggerDefinition;
    }

    /**
     * Gets the {@link TriggerDefinition} by the given name.
     *
     * @param triggerDefinitionName
     *         The {@link TriggerDefinition#getName()} to look for.
     * @return The {@link TriggerDefinition} by the given name.
     * @throws KapuaException
     *         In case nothing is found.
     * @since 1.1.0
     */
    private synchronized TriggerDefinition getTriggerDefinition(String triggerDefinitionName) throws KapuaException {
        return txManager.execute(
                        tx -> triggerDefinitionRepository.findByName(tx, triggerDefinitionName))
                .orElseThrow(() -> new KapuaEntityNotFoundException(TriggerDefinition.TYPE, triggerDefinitionName));
    }

    /**
     * Adapts {@link TriggerCreator#getRetryInterval()} and {@link TriggerCreator#getCronScheduling()}  to the new model which make use of {@link TriggerDefinition}s
     *
     * @param triggerCreator
     *         The {@link TriggerCreator} to adapt.
     * @throws KapuaException
     *         In case that {@link TriggerDefinition} is not found.
     * @since 1.1.0
     */
    private void adaptTriggerCreator(TriggerCreator triggerCreator) throws KapuaException {
        if (triggerCreator.getRetryInterval() != null) {
            triggerCreator.setTriggerDefinitionId(getIntervalJobTriggerDefinition().getId());
            triggerCreator.getTriggerProperties().add(triggerDefinitionFactory.newTriggerProperty("interval", Integer.class.getName(), triggerCreator.getRetryInterval().toString()));
        } else if (triggerCreator.getCronScheduling() != null) {
            triggerCreator.setTriggerDefinitionId(getCronJobTriggerDefinition().getId());
            triggerCreator.getTriggerProperties().add(triggerDefinitionFactory.newTriggerProperty("cronExpression", String.class.getName(), triggerCreator.getCronScheduling()));
        }
    }

    /**
     * Adapts {@link Trigger#getRetryInterval()} and {@link Trigger#getCronScheduling()} to the new model which make use of {@link TriggerDefinition}s
     *
     * @param trigger
     *         The {@link Trigger} to adapt.
     * @return
     * @throws KapuaException
     *         In case that {@link TriggerDefinition} is not found.
     * @since 1.1.0
     */
    private @NotNull Trigger adaptTrigger(TxContext tx, @NotNull Trigger trigger) throws KapuaException {
        boolean converted = false;
        if (trigger.getRetryInterval() != null) {
            trigger.setTriggerDefinitionId(getIntervalJobTriggerDefinition().getId());

            List<TriggerProperty> triggerProperties = new ArrayList<>(trigger.getTriggerProperties());
            triggerProperties.add(triggerDefinitionFactory.newTriggerProperty("interval", Integer.class.getName(), trigger.getRetryInterval().toString()));
            trigger.setTriggerProperties(triggerProperties);

            trigger.setRetryInterval(null);

            converted = true;

        } else if (trigger.getCronScheduling() != null) {
            trigger.setTriggerDefinitionId(getCronJobTriggerDefinition().getId());

            List<TriggerProperty> triggerProperties = new ArrayList<>(trigger.getTriggerProperties());
            triggerProperties.add(triggerDefinitionFactory.newTriggerProperty("cronExpression", String.class.getName(), trigger.getCronScheduling()));
            trigger.setTriggerProperties(triggerProperties);

            trigger.setCronScheduling(null);

            converted = true;
        }

        if (converted) {
            try {
                return triggerRepository.update(tx, trigger);
            } catch (Exception e) {
                LOG.warn("Cannot convert Trigger to new format!", e);
            }
        }
        return trigger;
    }
}
