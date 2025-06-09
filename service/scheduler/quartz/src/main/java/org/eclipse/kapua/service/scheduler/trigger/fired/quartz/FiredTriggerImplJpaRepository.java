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
package org.eclipse.kapua.service.scheduler.trigger.fired.quartz;

import org.eclipse.kapua.commons.jpa.KapuaEntityJpaRepository;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.service.scheduler.trigger.fired.FiredTrigger;
import org.eclipse.kapua.service.scheduler.trigger.fired.FiredTriggerListResult;
import org.eclipse.kapua.service.scheduler.trigger.fired.FiredTriggerRepository;

public class FiredTriggerImplJpaRepository
        extends KapuaEntityJpaRepository<FiredTrigger, FiredTriggerImpl, FiredTriggerListResult>
        implements FiredTriggerRepository {

    public FiredTriggerImplJpaRepository(KapuaJpaRepositoryConfiguration configuration) {
        super(FiredTriggerImpl.class, FiredTrigger.TYPE, () -> new FiredTriggerListResult(), configuration);
    }

}
