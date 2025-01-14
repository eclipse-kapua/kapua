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
package org.eclipse.kapua.service.scheduler.trigger.definition.quartz;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionCreator;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionFactory;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerProperty;

/**
 * {@link TriggerDefinitionFactory} implementation.
 *
 * @since 1.1.0
 */
@Singleton
public class TriggerDefinitionFactoryImpl implements TriggerDefinitionFactory {

    @Override
    public TriggerDefinition newEntity(KapuaId scopeId) {
        return new TriggerDefinitionImpl(scopeId);
    }

    @Override
    public TriggerDefinitionCreator newCreator(KapuaId scopeId) {
        return new TriggerDefinitionCreatorImpl(scopeId);
    }

    @Override
    public TriggerProperty newTriggerProperty(String name, String type, String value) {
        return new TriggerPropertyImpl(name, type, value);
    }

    @Override
    public TriggerDefinition clone(TriggerDefinition triggerDefinition) {
        try {
            return new TriggerDefinitionImpl(triggerDefinition);
        } catch (Exception e) {
            throw new KapuaEntityCloneException(e, TriggerDefinition.TYPE, triggerDefinition);
        }
    }
}
