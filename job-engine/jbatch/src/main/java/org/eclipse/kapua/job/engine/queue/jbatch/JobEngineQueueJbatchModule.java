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
package org.eclipse.kapua.job.engine.queue.jbatch;

import javax.inject.Singleton;

import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaJpaTxManagerFactory;
import org.eclipse.kapua.job.engine.jbatch.listener.QueuedJobExecutionCheckTaskFactory;
import org.eclipse.kapua.job.engine.jbatch.listener.QueuedJobExecutionCheckTaskFactoryImpl;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecutionFactory;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecutionRepository;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecutionService;
import org.eclipse.kapua.service.authorization.AuthorizationService;

import com.google.inject.Provides;

public class JobEngineQueueJbatchModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(QueuedJobExecutionFactory.class).to(QueuedJobExecutionFactoryImpl.class);
        bind(QueuedJobExecutionCheckTaskFactory.class).to(QueuedJobExecutionCheckTaskFactoryImpl.class);
    }

    @Provides
    @Singleton
    QueuedJobExecutionService queuedJobExecutionService(
            AuthorizationService authorizationService,
            QueuedJobExecutionRepository repository,
            KapuaJpaTxManagerFactory jpaTxManagerFactory) {
        return new QueuedJobExecutionServiceImpl(
                authorizationService,
                jpaTxManagerFactory.create("kapua-job-engine"),
                repository);
    }

    @Provides
    @Singleton
    QueuedJobExecutionRepository queuedJobExecutionRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        return new QueuedJobExecutionImplJpaRepository(jpaRepoConfig);
    }
}
