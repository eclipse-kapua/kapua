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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaNamedEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link TriggerDefinition} {@link org.eclipse.kapua.model.KapuaEntityCreator} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "triggerDefinitionCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class TriggerDefinitionCreator extends KapuaNamedEntityCreator {

    private static final long serialVersionUID = 4602067255120049746L;

    private TriggerType triggerType;
    private String processorName;
    private List<TriggerProperty> triggerProperties;

    public TriggerDefinitionCreator() {
    }

    public TriggerDefinitionCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public TriggerDefinitionCreator(KapuaId scopeId, String name) {
        super(scopeId, name);
    }

    /**
     * Gets the {@link TriggerType}.
     *
     * @return The {@link TriggerType}.
     * @since 1.1.0
     */
    public TriggerType getTriggerType() {
        return triggerType;
    }

    /**
     * Sets the {@link TriggerType}.
     *
     * @param triggerType
     *         The {@link TriggerType}.
     * @since 1.1.0
     */
    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    /**
     * Sets the processor name.
     *
     * @return The processor name.
     * @since 1.1.0
     */
    public String getProcessorName() {
        return processorName;
    }

    /**
     * Sets the processor name.
     *
     * @param processorName
     *         The processor name.
     * @since 1.1.0
     */
    public void setProcessorName(String processorName) {
        this.processorName = processorName;
    }

    /**
     * Gets the {@link TriggerProperty}s.
     *
     * @return The {@link TriggerProperty}s.
     * @since 1.1.0
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
     * Sets the {@link TriggerProperty}s.
     *
     * @param triggerProperties
     *         The {@link TriggerProperty}s.
     * @since 1.1.0
     */
    public void setTriggerProperties(List<TriggerProperty> triggerProperties) {
        this.triggerProperties = triggerProperties;
    }
}
