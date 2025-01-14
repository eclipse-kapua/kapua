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
package org.eclipse.kapua.service.scheduler.trigger.fired;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.KapuaEntityService;

/**
 * {@link FiredTrigger} {@link KapuaEntityService} definition.
 *
 * @see org.eclipse.kapua.service.KapuaEntityService
 * @since 1.5.0
 */
public interface FiredTriggerService extends KapuaEntityService<FiredTrigger, FiredTriggerCreator> {

    /**
     * Returns the {@link FiredTriggerListResult} with elements matching the provided query.
     *
     * @param query
     *         The {@link KapuaQuery} used to filter results.
     * @return The {@link FiredTriggerListResult} with elements matching the query parameter.
     * @throws KapuaException
     * @since 1.5.0
     */
    @Override
    FiredTriggerListResult query(KapuaQuery query) throws KapuaException;

}
