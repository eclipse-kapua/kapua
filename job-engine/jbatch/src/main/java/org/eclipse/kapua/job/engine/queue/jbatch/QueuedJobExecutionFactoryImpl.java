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
package org.eclipse.kapua.job.engine.queue.jbatch;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecution;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecutionCreator;
import org.eclipse.kapua.job.engine.queue.QueuedJobExecutionFactory;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link QueuedJobExecutionFactory} implementation.
 *
 * @since 1.1.0
 */
@Singleton
public class QueuedJobExecutionFactoryImpl implements QueuedJobExecutionFactory {

    @Override
    public QueuedJobExecution newEntity(KapuaId scopeId) {
        return new QueuedJobExecutionImpl(scopeId);
    }

    @Override
    public QueuedJobExecutionCreator newCreator(KapuaId scopeId) {
        return new QueuedJobExecutionCreatorImpl(scopeId);
    }

    @Override
    public QueuedJobExecution clone(QueuedJobExecution queuedJobExecution) {
        try {
            return new QueuedJobExecutionImpl(queuedJobExecution);
        } catch (Exception e) {
            throw new KapuaEntityCloneException(e, QueuedJobExecution.TYPE, queuedJobExecution);
        }
    }
}
