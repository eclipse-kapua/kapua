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
package org.eclipse.kapua.service.job.internal;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.job.JobCreator;
import org.eclipse.kapua.service.job.JobFactory;

/**
 * {@link JobFactory} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class JobFactoryImpl implements JobFactory {

    @Override
    public Job newEntity(KapuaId scopeId) {
        return new JobImpl(scopeId);
    }

    @Override
    public JobCreator newCreator(KapuaId scopeId) {
        return new JobCreatorImpl(scopeId);
    }

    @Override
    public Job clone(Job job) {
        try {
            return new JobImpl(job);
        } catch (Exception e) {
            throw new KapuaEntityCloneException(e, Job.TYPE, job);
        }
    }
}
