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
package org.eclipse.kapua.service.job.step.definition.internal;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinition;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionFactory;
import org.eclipse.kapua.service.job.step.definition.JobStepProperty;

/**
 * {@link JobStepDefinitionFactory} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class JobStepDefinitionFactoryImpl implements JobStepDefinitionFactory {

    @Override
    public JobStepDefinition newEntity(KapuaId scopeId) {
        return new JobStepDefinitionImpl(scopeId);
    }

    @Override
    public JobStepProperty newStepProperty(String name, String type, String value) {
        return new JobStepPropertyImpl(name, type, value, null);
    }

    @Override
    public JobStepProperty newStepProperty(String name, String type, String value, String exampleValue) {
        return new JobStepPropertyImpl(name, type, value, exampleValue);
    }

    @Override
    public JobStepDefinition clone(JobStepDefinition jobStepDefinition) {
        try {
            return new JobStepDefinitionImpl(jobStepDefinition);
        } catch (Exception e) {
            throw new KapuaEntityCloneException(e, JobStepDefinition.TYPE, jobStepDefinition);
        }
    }
}
