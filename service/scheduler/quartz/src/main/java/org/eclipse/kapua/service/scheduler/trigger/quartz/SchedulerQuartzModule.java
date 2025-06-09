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
package org.eclipse.kapua.service.scheduler.trigger.quartz;

import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaJpaTxManagerFactory;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.model.domain.DomainEntry;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.scheduler.trigger.TriggerFactory;
import org.eclipse.kapua.service.scheduler.trigger.TriggerRepository;
import org.eclipse.kapua.service.scheduler.trigger.TriggerService;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionFactory;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionRepository;
import org.eclipse.kapua.storage.TxManager;

import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;

public class SchedulerQuartzModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(TriggerFactory.class).to(TriggerFactoryImpl.class);
    }

    @ProvidesIntoSet
    public Domain schedulerDomain() {
        return new DomainEntry(Domains.SCHEDULER, "org.eclipse.kapua.service.scheduler.SchedulerService", false, Actions.read, Actions.delete, Actions.write);
    }

    @Provides
    @Singleton
    @Named("schedulerTxManager")
    TxManager jobTxManager(
            KapuaJpaTxManagerFactory jpaTxManagerFactory
    ) {
        return jpaTxManagerFactory.create("kapua-scheduler");
    }

    @Provides
    @Singleton
    TriggerService triggerService(
            AuthorizationService authorizationService,
            @Named("schedulerTxManager") TxManager txManager,
            TriggerRepository triggerRepository,
            TriggerFactory triggerFactory,
            TriggerDefinitionRepository triggerDefinitionRepository,
            TriggerDefinitionFactory triggerDefinitionFactory) {
        return new TriggerServiceImpl(
                authorizationService,
                txManager,
                triggerRepository,
                triggerFactory,
                triggerDefinitionRepository,
                triggerDefinitionFactory
        );
    }

    @Provides
    @Singleton
    TriggerRepository triggerRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        return new TriggerImplJpaRepository(jpaRepoConfig);
    }
}
