/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.scheduler.trigger.fired.quartz;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.scheduler.trigger.fired.FiredTrigger;
import org.eclipse.kapua.service.scheduler.trigger.fired.FiredTriggerCreator;
import org.eclipse.kapua.service.scheduler.trigger.fired.FiredTriggerFactory;

/**
 * {@link FiredTriggerFactory} implementation.
 *
 * @since 1.5.0
 */
@Singleton
public class FiredTriggerFactoryImpl implements FiredTriggerFactory {

    @Override
    public FiredTrigger newEntity(KapuaId scopeId) {
        return new FiredTriggerImpl(scopeId);
    }

    @Override
    public FiredTriggerCreator newCreator(KapuaId scopeId) {
        return new FiredTriggerCreatorImpl(scopeId);
    }

    @Override
    public FiredTrigger clone(FiredTrigger firedTrigger) {
        try {
            return new FiredTriggerImpl(firedTrigger);
        } catch (Exception e) {
            throw new KapuaEntityCloneException(e, FiredTrigger.TYPE, firedTrigger);
        }
    }
}
