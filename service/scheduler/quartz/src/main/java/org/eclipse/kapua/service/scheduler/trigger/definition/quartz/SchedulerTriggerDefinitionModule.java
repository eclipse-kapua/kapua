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
package org.eclipse.kapua.service.scheduler.trigger.definition.quartz;

import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionFactory;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionRepository;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionService;
import org.eclipse.kapua.storage.TxManager;

import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;

public class SchedulerTriggerDefinitionModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(TriggerDefinitionFactory.class).to(TriggerDefinitionFactoryImpl.class);
    }

    @Provides
    @Singleton
    TriggerDefinitionService triggerDefinitionService(
            AuthorizationService authorizationService,
            @Named("schedulerTxManager") TxManager txManager,
            TriggerDefinitionRepository triggerDefinitionRepository,
            TriggerDefinitionFactory triggerDefinitionFactory) {
        return new TriggerDefinitionServiceImpl(
                authorizationService,
                txManager,
                triggerDefinitionRepository,
                triggerDefinitionFactory);
    }

    @Provides
    @Singleton
    TriggerDefinitionRepository triggerDefinitionRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        return new TriggerDefinitionImplJpaRepository(jpaRepoConfig);
    }

    @ProvidesIntoSet
    public TriggerDefinition cronJobTriggerDefinition() {
        return new CronJobTriggerDefinition();
    }

    @ProvidesIntoSet
    public TriggerDefinition deviceConnectTriggerDefinition() {
        return new DeviceConnectTriggerDefinition();
    }

    @ProvidesIntoSet
    public TriggerDefinition intervalJobTriggerDefinition() {
        return new IntervalJobTriggerDefinition();
    }

}
