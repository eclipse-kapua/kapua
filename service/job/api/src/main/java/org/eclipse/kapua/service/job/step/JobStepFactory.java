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
package org.eclipse.kapua.service.job.step;

import org.eclipse.kapua.model.KapuaEntityFactory;
import org.eclipse.kapua.service.job.step.definition.JobStepProperty;

/**
 * {@link JobStepFactory} definition.
 *
 * @see org.eclipse.kapua.model.KapuaEntityFactory
 * @since 1.0.0
 */
public interface JobStepFactory extends KapuaEntityFactory<JobStep, JobStepCreator> {

    /**
     * Instantiates a new {@link JobStepProperty}.
     *
     * @param name
     *         The name to set into the {@link JobStepProperty}.
     * @param propertyType
     *         The type to set into the {@link JobStepProperty}.
     * @param propertyValue
     *         The value to set into the {@link JobStepProperty}.
     * @return The newly instantiated {@link JobStepProperty}
     * @since 1.0.0
     */
    JobStepProperty newStepProperty(String name, String propertyType, String propertyValue);

}
