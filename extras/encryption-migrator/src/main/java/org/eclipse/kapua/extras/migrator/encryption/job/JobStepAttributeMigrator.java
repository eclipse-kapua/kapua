/*******************************************************************************
 * Copyright (c) 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.extras.migrator.encryption.job;

import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaJpaTxManagerFactory;
import org.eclipse.kapua.extras.migrator.encryption.api.AbstractEntityAttributeMigrator;
import org.eclipse.kapua.extras.migrator.encryption.api.EntitySecretAttributeMigrator;
import org.eclipse.kapua.service.job.step.JobStep;

public class JobStepAttributeMigrator extends AbstractEntityAttributeMigrator<JobStep> implements EntitySecretAttributeMigrator<JobStep> {

    public JobStepAttributeMigrator(String persistenceUnitName, KapuaJpaTxManagerFactory jpaTxManagerFactory) {
        super(new JobStepMigratorServiceImpl(
                jpaTxManagerFactory.create(persistenceUnitName),
                new JobStepMigratorJpaRepository(new KapuaJpaRepositoryConfiguration())
        ));
    }

    @Override
    public String getEntityName() {
        return JobStep.TYPE;
    }

}
