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
package org.eclipse.kapua.service.scheduler.trigger;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.KapuaEntityService;
import org.eclipse.kapua.service.KapuaUpdatableEntityService;

/**
 * {@link Trigger} {@link KapuaEntityService} definition.
 *
 * @see KapuaEntityService
 * @see KapuaUpdatableEntityService
 * @since 1.0.0
 */
public interface TriggerService extends KapuaEntityService<Trigger, TriggerCreator>,
        KapuaUpdatableEntityService<Trigger> {

    /**
     * Returns the {@link TriggerListResult} with elements matching the provided query.
     *
     * @param query
     *         The {@link KapuaQuery} used to filter results.
     * @return The {@link TriggerListResult} with elements matching the query parameter.
     * @throws KapuaException
     * @since 1.0.0
     */
    @Override
    TriggerListResult query(KapuaQuery query) throws KapuaException;

    void deleteAllByJobId(KapuaId scopeId, KapuaId jobId) throws KapuaException;
}
