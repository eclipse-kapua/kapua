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
package org.eclipse.kapua.service.job.targets;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.KapuaEntityService;
import org.eclipse.kapua.service.KapuaUpdatableEntityService;

/**
 * {@link JobTargetService} exposes APIs to manage JobTarget objects.<br> It includes APIs to create, update, find, list and delete Jobs.<br> Instances of the JobTargetService can be acquired through
 * the ServiceLocator object.
 *
 * @since 1.0
 */
public interface JobTargetService extends KapuaEntityService<JobTarget, JobTargetCreator>,
        KapuaUpdatableEntityService<JobTarget> {

    /**
     * Returns the {@link JobTargetListResult} with elements matching the provided query.
     *
     * @param query
     *         The {@link KapuaQuery} used to filter results.
     * @return The {@link JobTargetListResult} with elements matching the query parameter.
     * @throws KapuaException
     * @since 1.0.0
     */
    @Override
    JobTargetListResult query(KapuaQuery query) throws KapuaException;
}
