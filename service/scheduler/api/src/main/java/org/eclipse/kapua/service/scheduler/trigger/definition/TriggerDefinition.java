/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.scheduler.trigger.definition;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaNamedEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link TriggerDefinition} {@link KapuaNamedEntity} definition.
 *
 * @since 1.1.0
 */
@XmlRootElement(name = "triggerDefinition")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = TriggerDefinitionXmlRegistry.class, factoryMethod = "newEntity")
public interface TriggerDefinition extends KapuaNamedEntity {

    String TYPE = "triggerDefinition";

    @Override
    @Schema(example = "triggerDefinition")
    default String getType() {
        return TYPE;
    }

    /**
     * Gets the {@link TriggerType}.
     *
     * @return The {@link TriggerType}.
     * @since 1.1.0
     */
    TriggerType getTriggerType();

    /**
     * Sets the {@link TriggerType}.
     *
     * @param triggerType The {@link TriggerType}.
     * @since 1.1.0
     */
    void setTriggerType(TriggerType triggerType);

    /**
     * Sets the processor name.
     *
     * @return The processor name.
     * @since 1.1.0
     */
    @Schema(example = "org.eclipse.kapua.service.scheduler.trigger.quartz.job.KapuaJobLauncer")
    String getProcessorName();

    /**
     * Sets the processor name.
     *
     * @param processorName The processor name.
     * @since 1.1.0
     */
    void setProcessorName(String processorName);

    /**
     * Gets the {@link TriggerProperty}s.
     *
     * @return The {@link TriggerProperty}s.
     * @since 1.1.0
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
     * Sets the {@link TriggerProperty}s.
     *
     * @param triggerProperties The {@link TriggerProperty}s.
     * @since 1.1.0
     */
    void setTriggerProperties(List<TriggerProperty> triggerProperties);

}
