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
package org.eclipse.kapua.service.scheduler.trigger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaNamedEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerProperty;

/**
 * {@link Trigger} {@link KapuaNamedEntityCreator} definition.
 *
 * @see org.eclipse.kapua.model.KapuaNamedEntityCreator
 * @since 1.0.0
 */
@XmlRootElement(name = "triggerCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class TriggerCreator extends KapuaNamedEntityCreator<Trigger> {

    private static final long serialVersionUID = -2460883485294616032L;

    private Date startsOn;
    private Date endsOn;
    private String cronScheduling;
    private Long retryInterval;
    private KapuaId triggerDefinitionId;
    private List<TriggerProperty> triggerProperties;

    public TriggerCreator() {
    }

    public TriggerCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public TriggerCreator(KapuaId scopeId, String name) {
        super(scopeId, name);
    }

    /**
     * Gets the start {@link Date} from which this {@link Trigger} will be valid.
     *
     * @return The start {@link Date} from which this {@link Trigger} will be valid.
     * @since 1.0.0
     */
    public Date getStartsOn() {
        return startsOn;
    }

    /**
     * Sets the start {@link Date} from which this {@link Trigger} will be valid.
     *
     * @param startsOn
     *         The start {@link Date} from which this {@link Trigger} will be valid.
     * @since 1.0.0
     */
    public void setStartsOn(Date startsOn) {
        this.startsOn = startsOn;
    }

    /**
     * Gets the end {@link Date} until which this {@link Trigger} will be valid.
     * <p>
     * {@code null} means that never expires.
     *
     * @return The start {@link Date} from which this {@link Trigger} will be valid.
     * @since 1.0.0
     */
    public Date getEndsOn() {
        return endsOn;
    }

    /**
     * Gets the end {@link Date} until which this {@link Trigger} will be valid.
     * <p>
     * {@code null} means that never expires.
     *
     * @param endsOn
     *         The end {@link Date} until which this {@link Trigger} will be valid.
     * @since 1.0.0
     */
    public void setEndsOn(Date endsOn) {
        this.endsOn = endsOn;
    }

    /**
     * Gets the CRON scheduling.
     * <p>
     * This field is {@code deprecated}. Please make use of {@link org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition} and {@link TriggerProperty}es.
     *
     * @return The CRON scheduling.
     * @since 1.0.0
     * @deprecated since 1.1.0
     */
    @Deprecated
    public String getCronScheduling() {
        return cronScheduling;
    }

    /**
     * Sets the CRON scheduling.
     * <p>
     * This field is {@code deprecated}. Please make use of {@link org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition} and {@link TriggerProperty}es.
     *
     * @param cronScheduling
     *         The CRON scheduling.
     * @since 1.0.0
     * @deprecated since 1.1.0
     */
    @Deprecated
    public void setCronScheduling(String cronScheduling) {
        this.cronScheduling = cronScheduling;
    }

    /**
     * Gets the retry interval.
     * <p>
     * This field is {@code deprecated}. Please make use of {@link org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition} and {@link TriggerProperty}es.
     *
     * @return The retry interval.
     * @since 1.0.0
     * @deprecated since 1.1.0
     */
    @Deprecated
    public Long getRetryInterval() {
        return retryInterval;
    }

    /**
     * Sets the retry interval.
     * <p>
     * This field is {@code deprecated}. Please make use of {@link org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition} and {@link TriggerProperty}es.
     *
     * @param retryInterval
     *         The retry interval.
     * @since 1.0.0
     * @deprecated since 1.1.0
     */
    @Deprecated
    public void setRetryInterval(Long retryInterval) {
        this.retryInterval = retryInterval;
    }

    /**
     * Gets the {@link org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition} {@link KapuaId} which this {@link TriggerCreator} refers to.
     *
     * @return The {@link org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition} {@link KapuaId} which this {@link TriggerCreator} refers to.
     * @since 1.1.0
     */
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getTriggerDefinitionId() {
        return triggerDefinitionId;
    }

    /**
     * Sets the {@link org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition} {@link KapuaId} which this {@link TriggerCreator} refers to.
     *
     * @param triggerDefinitionId
     *         The {@link org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition} {@link KapuaId} which this {@link TriggerCreator} refers to.
     * @since 1.1.0
     */
    public void setTriggerDefinitionId(KapuaId triggerDefinitionId) {
        this.triggerDefinitionId = triggerDefinitionId;
    }

    /**
     * Gets the {@link List} of {@link TriggerProperty}es associated with this {@link TriggerCreator}
     *
     * @return The {@link List} of {@link TriggerProperty}es associated with this {@link TriggerCreator}
     * @since 1.0.0
     */
    public List<TriggerProperty> getTriggerProperties() {
        if (triggerProperties == null) {
            triggerProperties = new ArrayList<>();
        }

        return triggerProperties;
    }

    /**
     * Gets the {@link TriggerProperty} by the name.
     *
     * @param name
     *         The {@link TriggerProperty#getName()} to look for.
     * @return The found {@link TriggerProperty} or {@code null}.
     * @since 1.5.0
     */
    public TriggerProperty getTriggerProperty(String name) {
        return getTriggerProperties().stream().filter(tp -> tp.getName().equals(name)).findAny().orElse(null);
    }

    /**
     * Sets the {@link List} of {@link TriggerProperty}es associated with this {@link TriggerCreator}
     *
     * @param triggerProperties
     *         The {@link List} of {@link TriggerProperty}es associated with this {@link TriggerCreator}
     * @since 1.0.0
     */
    public void setTriggerProperties(List<TriggerProperty> triggerProperties) {
        this.triggerProperties = triggerProperties;
    }

}
