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
package org.eclipse.kapua.service.job.step.internal;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.job.step.JobStepFactory;
import org.eclipse.kapua.service.job.step.definition.JobStepProperty;
import org.eclipse.kapua.service.job.step.definition.internal.JobStepPropertyImpl;

/**
 * {@link JobStepFactory} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class JobStepFactoryImpl implements JobStepFactory {

    @Override
    public org.eclipse.kapua.service.job.step.JobStep newEntity(KapuaId scopeId) {
        return new JobStepImpl(scopeId);
    }

    @Override
    public JobStepProperty newStepProperty(String name, String propertyType, String propertyValue) {
        return new JobStepPropertyImpl(name, propertyType, propertyValue, null);
    }

    @Override
    public org.eclipse.kapua.service.job.step.JobStep clone(org.eclipse.kapua.service.job.step.JobStep jobStep) {
        try {
            return new JobStepImpl(jobStep);
        } catch (Exception e) {
            throw new KapuaEntityCloneException(e, org.eclipse.kapua.service.job.step.JobStep.TYPE, jobStep);
        }
    }
}
