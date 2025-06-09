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
package org.eclipse.kapua.service.scheduler.trigger.definition;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.eclipse.kapua.entity.EntityPropertiesReadException;
import org.eclipse.kapua.entity.EntityPropertiesWriteException;
import org.eclipse.kapua.model.id.KapuaId;

public class TriggerDefinitionRecord implements TriggerDefinition {

    private KapuaId scopeId;
    private String name;
    private String description;

    private TriggerType triggerType;

    private String processorName;

    private List<TriggerProperty> triggerProperties;

    public TriggerDefinitionRecord(KapuaId scopeId,
            String name,
            String description,
            TriggerType triggerType,
            String processorName,
            List<TriggerProperty> triggerProperties) {
        this.scopeId = scopeId;
        this.name = name;
        this.description = description;
        this.triggerType = triggerType;
        this.processorName = processorName;
        this.triggerProperties = triggerProperties;
    }

    @Override
    public KapuaId getScopeId() {
        return scopeId;
    }

    @Override
    public void setScopeId(KapuaId scopeId) {
        this.scopeId = scopeId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public TriggerType getTriggerType() {
        return triggerType;
    }

    @Override
    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    @Override
    public String getProcessorName() {
        return processorName;
    }

    @Override
    public void setProcessorName(String processorName) {
        this.processorName = processorName;
    }

    @Override
    public List<TriggerProperty> getTriggerProperties() {
        return triggerProperties;
    }

    @Override
    public void setTriggerProperties(List<TriggerProperty> triggerProperties) {
        this.triggerProperties = triggerProperties;
    }

    @Override
    public KapuaId getId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setId(KapuaId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Date getCreatedOn() {
        throw new UnsupportedOperationException();
    }

    @Override
    public KapuaId getCreatedBy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Date getModifiedOn() {
        throw new UnsupportedOperationException();
    }

    @Override
    public KapuaId getModifiedBy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getOptlock() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOptlock(int optlock) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Properties getEntityAttributes() throws EntityPropertiesReadException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setEntityAttributes(Properties props) throws EntityPropertiesWriteException {

    }

    @Override
    public Properties getEntityProperties() throws EntityPropertiesReadException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setEntityProperties(Properties props) throws EntityPropertiesWriteException {
        throw new UnsupportedOperationException();
    }

    @Override
    public TriggerProperty getTriggerProperty(String name) {
        throw new UnsupportedOperationException();
    }
}
