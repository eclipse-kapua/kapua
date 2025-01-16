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
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link Trigger} {@link KapuaNamedEntityCreator} definition.
 *
 * @see org.eclipse.kapua.model.KapuaNamedEntityCreator
 * @since 1.0.0
 */
@XmlRootElement(name = "triggerCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = TriggerXmlRegistry.class, factoryMethod = "newCreator")
@Schema(description = "An object containing the properties for the new Job Trigger to be created")
public interface TriggerCreator extends KapuaNamedEntityCreator<Trigger> {

    /**
     * Gets the start {@link Date} from which this {@link Trigger} will be valid.
     *
     * @return The start {@link Date} from which this {@link Trigger} will be valid.
     * @since 1.0.0
     */
    Date getStartsOn();

    /**
     * Sets the start {@link Date} from which this {@link Trigger} will be valid.
     *
     * @param starstOn The start {@link Date} from which this {@link Trigger} will be valid.
     * @since 1.0.0
     */
    void setStartsOn(Date starstOn);

    /**
     * Gets the end {@link Date} until which this {@link Trigger} will be valid.
     * <p>
     * {@code null} means that never expires.
     *
     * @return The start {@link Date} from which this {@link Trigger} will be valid.
     * @since 1.0.0
     */
    Date getEndsOn();

    /**
     * Gets the end {@link Date} until which this {@link Trigger} will be valid.
     * <p>
     * {@code null} means that never expires.
     *
     * @param endsOn The end {@link Date} until which this {@link Trigger} will be valid.
     * @since 1.0.0
     */
    void setEndsOn(Date endsOn);

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
    String getCronScheduling();

    /**
     * Sets the CRON scheduling.
     * <p>
     * This field is {@code deprecated}. Please make use of {@link org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition} and {@link TriggerProperty}es.
     *
     * @param cronScheduling The CRON scheduling.
     * @since 1.0.0
     * @deprecated since 1.1.0
     */
    @Deprecated
    void setCronScheduling(String cronScheduling);

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
    Long getRetryInterval();

    /**
     * Sets the retry interval.
     * <p>
     * This field is {@code deprecated}. Please make use of {@link org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition} and {@link TriggerProperty}es.
     *
     * @param retryInterval The retry interval.
     * @since 1.0.0
     * @deprecated since 1.1.0
     */
    @Deprecated
    void setRetryInterval(Long retryInterval);

    /**
     * Gets the {@link org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition} {@link KapuaId} which this {@link TriggerCreator} refers to.
     *
     * @return The {@link org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition} {@link KapuaId} which this {@link TriggerCreator} refers to.
     * @since 1.1.0
     */
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    @Schema(example = "AQ")
    KapuaId getTriggerDefinitionId();

    /**
     * Sets the {@link org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition} {@link KapuaId} which this {@link TriggerCreator} refers to.
     *
     * @param triggerDefinitionId The {@link org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition} {@link KapuaId} which this {@link TriggerCreator} refers to.
     * @since 1.1.0
     */
    void setTriggerDefinitionId(KapuaId triggerDefinitionId);

    /**
     * Gets the {@link List} of {@link TriggerProperty}es associated with this {@link TriggerCreator}
     *
     * @return The {@link List} of {@link TriggerProperty}es associated with this {@link TriggerCreator}
     * @since 1.0.0
     */
    List<TriggerProperty> getTriggerProperties();

    /**
     * Gets the {@link TriggerProperty} by the name.
     *
     * @param name The {@link TriggerProperty#getName()} to look for.
     * @return The found {@link TriggerProperty} or {@code null}.
     * @since 1.5.0
     */
    TriggerProperty getTriggerProperty(String name);

    /**
     * Sets the {@link List} of {@link TriggerProperty}es associated with this {@link TriggerCreator}
     *
     * @param triggerProperties The {@link List} of {@link TriggerProperty}es associated with this {@link TriggerCreator}
     * @since 1.0.0
     */
    void setTriggerProperties(List<TriggerProperty> triggerProperties);
}
