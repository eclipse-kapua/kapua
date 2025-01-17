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

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.scheduler.trigger.Trigger;
import org.eclipse.kapua.service.scheduler.trigger.TriggerFactory;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerProperty;
import org.eclipse.kapua.service.scheduler.trigger.definition.quartz.TriggerPropertyImpl;

/**
 * {@link TriggerFactory} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class TriggerFactoryImpl implements TriggerFactory {

    @Override
    public Trigger newEntity(KapuaId scopeId) {
        return new TriggerImpl(scopeId);
    }

    @Override
    public TriggerProperty newTriggerProperty(String name, String type, String value) {
        return new TriggerPropertyImpl(name, type, value);
    }

    @Override
    public Trigger clone(Trigger trigger) {
        try {
            return new TriggerImpl(trigger);
        } catch (Exception e) {
            throw new KapuaEntityCloneException(e, Trigger.TYPE, trigger);
        }
    }
}
