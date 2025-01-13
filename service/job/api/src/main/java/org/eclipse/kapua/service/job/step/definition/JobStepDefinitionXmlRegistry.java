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
package org.eclipse.kapua.service.job.step.definition;

import javax.xml.bind.annotation.XmlRegistry;

import org.eclipse.kapua.locator.KapuaLocator;

/**
 * {@link JobStepDefinition} xml factory class
 *
 * @since 1.0
 */
@XmlRegistry
public class JobStepDefinitionXmlRegistry {

    private final JobStepDefinitionFactory jobStepDefinitionFactory = KapuaLocator.getInstance().getFactory(JobStepDefinitionFactory.class);

    /**
     * Creates a new job instance
     *
     * @return
     */
    public JobStepDefinition newJobStepDefinition() {
        return jobStepDefinitionFactory.newEntity(null);
    }

    /**
     * Creates a new job creator instance
     *
     * @return
     */
    public JobStepDefinitionCreator newJobStepDefinitionCreator() {
        return jobStepDefinitionFactory.newCreator(null);
    }

    public JobStepDefinitionQuery newQuery() {
        return jobStepDefinitionFactory.newQuery(null);
    }
}
