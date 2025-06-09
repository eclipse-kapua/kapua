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

import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.scheduler.trigger.TriggerRepository;
import org.eclipse.kapua.service.scheduler.trigger.fired.FiredTriggerFactory;
import org.eclipse.kapua.service.scheduler.trigger.fired.FiredTriggerRepository;
import org.eclipse.kapua.service.scheduler.trigger.fired.FiredTriggerService;
import org.eclipse.kapua.storage.TxManager;

import com.google.inject.Provides;

public class SchedulerTriggerFiredModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(FiredTriggerFactory.class).to(FiredTriggerFactoryImpl.class);
    }

    @Provides
    @Singleton
    FiredTriggerService firedTriggerService(
            AuthorizationService authorizationService,
            @Named("schedulerTxManager") TxManager txManager,
            FiredTriggerRepository firedTriggerRepository,
            FiredTriggerFactory firedTriggerFactory,
            TriggerRepository triggerRepository) {
        return new FiredTriggerServiceImpl(
                authorizationService,
                txManager,
                firedTriggerRepository,
                firedTriggerFactory,
                triggerRepository);
    }

    @Provides
    @Singleton
    FiredTriggerRepository firedTriggerRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        return new FiredTriggerImplJpaRepository(jpaRepoConfig);
    }
}
